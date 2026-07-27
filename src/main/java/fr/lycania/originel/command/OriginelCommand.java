package fr.lycania.originel.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.GeneralConfig;
import fr.lycania.originel.config.HybrideConfig;
import fr.lycania.originel.config.OriginelConfig;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.skill.HybrideSkillCommand;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
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
                .then(HybrideSkillCommand.build());
    }

    static boolean isStaff(CommandSourceStack source) {
        return CommandUtil.isStaff(source);
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        OriginelConfig.reloadAll();
        context.getSource().sendSuccess(() -> OriginelText.prefixed("Configuration rechargee."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        HybrideFaction.AssignResult result = HybrideFaction.assign(target);
        if (result == HybrideFaction.AssignResult.NOT_WHITELISTED) {
            context.getSource().sendFailure(OriginelText.prefixed(
                    "echec : " + target.getName().getString() + " n'est pas le joueur whitelist pour la faction Hybride."));
            return 0;
        }
        target.sendSystemMessage(OriginelText.lore(HybrideConfig.get().assignMessage()));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(
                target.getName().getString() + " est desormais l'Hybride."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        HybrideFaction.remove(target);
        target.sendSystemMessage(OriginelText.lore(HybrideConfig.get().removeMessage()));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(
                target.getName().getString() + " n'est plus l'Hybride."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeLevelSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        int level = IntegerArgumentType.getInteger(context, "niveau");
        if (!HybrideFaction.isHybride(target)) {
            context.getSource().sendFailure(OriginelText.prefixed(
                    target.getName().getString() + " n'est pas l'Hybride."));
            return 0;
        }
        int max = HybrideFaction.get().getHighestReachableLevel();
        int clamped = Math.min(Math.max(level, 1), max);
        IFactionPlayerHandler handler = VampirismAPI.factionPlayerHandler(target);
        handler.setFactionLevel(HybrideFaction.get(), clamped);
        context.getSource().sendSuccess(() -> OriginelText.prefixed(
                target.getName().getString() + " est maintenant niveau " + clamped + "."), true);
        return Command.SINGLE_SUCCESS;
    }
}
