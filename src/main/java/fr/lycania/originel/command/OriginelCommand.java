package fr.lycania.originel.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.cendre.CendreCommand;
import fr.lycania.originel.config.FaiblesseConfig;
import fr.lycania.originel.config.GeneralConfig;
import fr.lycania.originel.config.HybrideConfig;
import fr.lycania.originel.config.OriginelConfig;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.config.RituelConfig;
import fr.lycania.originel.item.OriginelGiveCommand;
import fr.lycania.originel.redmoon.RedMoonManager;
import fr.lycania.originel.redmoon.RedMoonState;
import fr.lycania.originel.ritual.RitualManager;
import fr.lycania.originel.scellement.ScellementRitualManager;
import fr.lycania.originel.skill.HybrideSkillCommand;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = OriginelMod.MODID)
public final class OriginelCommand {

    private OriginelCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(root());
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> root() {
        return Commands.literal("originel")
                .then(Commands.literal("reload")
                        .requires(OriginelCommand::isStaff)
                        .executes(OriginelCommand::executeReload))
                .then(Commands.literal("set")
                        .requires(OriginelCommand::isStaff)
                        .then(Commands.argument("joueur", EntityArgument.player())
                                .executes(OriginelCommand::executeSet)))
                .then(Commands.literal("remove")
                        .requires(OriginelCommand::isStaff)
                        .then(Commands.argument("joueur", EntityArgument.player())
                                .executes(OriginelCommand::executeRemove)))
                .then(Commands.literal("level")
                        .requires(OriginelCommand::isStaff)
                        .then(Commands.literal("set")
                                .then(Commands.argument("joueur", EntityArgument.player())
                                        .then(Commands.argument("niveau", IntegerArgumentType.integer(1))
                                                .executes(OriginelCommand::executeLevelSet)))))
                .then(HybrideSkillCommand.build())
                .then(OriginelGiveCommand.build())
                .then(CendreCommand.build())
                .then(Commands.literal("scellement")
                        .requires(OriginelCommand::isStaff)
                        .then(Commands.argument("joueur", EntityArgument.player())
                                .executes(OriginelCommand::executeScellement))
                        .then(Commands.literal("stop").executes(OriginelCommand::executeScellementStop)))
                .then(Commands.literal("lunerouge")
                        .requires(OriginelCommand::isStaff)
                        .then(Commands.literal("start").executes(OriginelCommand::executeLuneRougeStart))
                        .then(Commands.literal("stop").executes(OriginelCommand::executeLuneRougeStop)))
                .then(Commands.literal("rituel")
                        .requires(OriginelCommand::isStaff)
                        .then(Commands.literal("start").executes(OriginelCommand::executeRituelStart)));
    }

    static boolean isStaff(CommandSourceStack source) {
        return CommandUtil.isStaff(source);
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        OriginelConfig.reloadAll();
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable("originel.msg.reload_done")), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        HybrideFaction.AssignResult result = HybrideFaction.assign(target);
        if (result == HybrideFaction.AssignResult.NOT_WHITELISTED) {
            context.getSource().sendFailure(OriginelText.prefixed(Component.translatable(
                    "originel.msg.not_whitelisted", target.getName().getString())));
            return 0;
        }
        target.sendSystemMessage(OriginelText.lore(HybrideConfig.get().assignMessage()));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable(
                "originel.msg.set_success", target.getName().getString())), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        if (!HybrideFaction.remove(target)) {
            context.getSource().sendFailure(OriginelText.prefixed(Component.translatable(
                    "originel.msg.not_hybride", target.getName().getString())));
            return 0;
        }
        target.sendSystemMessage(OriginelText.lore(HybrideConfig.get().removeMessage()));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable(
                "originel.msg.remove_success", target.getName().getString())), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeLevelSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        int level = IntegerArgumentType.getInteger(context, "niveau");
        if (!HybrideFaction.isHybride(target)) {
            context.getSource().sendFailure(OriginelText.prefixed(Component.translatable(
                    "originel.msg.not_hybride", target.getName().getString())));
            return 0;
        }
        int max = HybrideFaction.get().getHighestReachableLevel();
        int clamped = Math.min(Math.max(level, 1), max);
        IFactionPlayerHandler handler = VampirismAPI.factionPlayerHandler(target);
        handler.setFactionLevel(HybrideFaction.get(), clamped);
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable(
                "originel.msg.level_set", target.getName().getString(), clamped)), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeScellement(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        if (!HybrideFaction.isHybride(target)) {
            context.getSource().sendFailure(OriginelText.prefixed(Component.translatable(
                    "originel.msg.not_hybride", target.getName().getString())));
            return 0;
        }
        HybridePlayer data = target.getData(HybrideAttachments.HYBRIDE_PLAYER);
        long expiry = target.level().getGameTime() + FaiblesseConfig.get().scellementDurationTicks();
        data.setScellementExpiry(expiry);
        target.sendSystemMessage(OriginelText.lore(Component.translatable("originel.msg.scellement_lore")));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable(
                "originel.msg.scellement_success", target.getName().getString(), FaiblesseConfig.get().scellementDurationTicks() / 20)), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeScellementStop(CommandContext<CommandSourceStack> context) {
        ScellementRitualManager.stop(context.getSource().getServer());
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable("originel.msg.scellement_stop")), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeLuneRougeStart(CommandContext<CommandSourceStack> context) {
        if (RedMoonState.isActive()) {
            context.getSource().sendFailure(OriginelText.prefixed(Component.translatable("originel.msg.lunerouge_already_active")));
            return 0;
        }
        RedMoonManager.start(context.getSource().getServer());
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable("originel.msg.lunerouge_start")), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeLuneRougeStop(CommandContext<CommandSourceStack> context) {
        if (!RedMoonState.isActive()) {
            context.getSource().sendFailure(OriginelText.prefixed(Component.translatable("originel.msg.lunerouge_not_active")));
            return 0;
        }
        RedMoonManager.stop(context.getSource().getServer());
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable("originel.msg.lunerouge_stop")), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRituelStart(CommandContext<CommandSourceStack> context) {
        RitualManager.StartResult result = RitualManager.start(context.getSource().getServer());
        String message = switch (result) {
            case SUCCESS -> null;
            case NOT_WHITELISTED -> RituelConfig.get().messageFailNotWhitelisted();
            case PLAYER_OFFLINE -> RituelConfig.get().messageFailPlayerOffline();
            case ALREADY_HYBRIDE -> RituelConfig.get().messageFailAlreadyHybride();
            case NO_ALTAR -> RituelConfig.get().messageFailNoAltar();
        };
        if (message != null) {
            context.getSource().sendFailure(OriginelText.prefixed(message));
            return 0;
        }
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable("originel.msg.rituel_start_success")), true);
        return Command.SINGLE_SUCCESS;
    }
}
