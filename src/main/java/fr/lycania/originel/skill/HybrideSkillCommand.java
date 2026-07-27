package fr.lycania.originel.skill;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fr.lycania.originel.command.CommandUtil;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public final class HybrideSkillCommand {

    private HybrideSkillCommand() {
    }

    private static final SuggestionProvider<CommandSourceStack> SKILL_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(SkillRegistry.all().keySet(), builder);

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("skill")
                .requires(CommandUtil::isStaff)
                .then(Commands.literal("give")
                        .then(Commands.argument("joueur", EntityArgument.player())
                                .then(Commands.argument("competence", StringArgumentType.word())
                                        .suggests(SKILL_SUGGESTIONS)
                                        .executes(HybrideSkillCommand::executeGive))))
                .then(Commands.literal("use")
                        .then(Commands.argument("joueur", EntityArgument.player())
                                .then(Commands.argument("competence", StringArgumentType.word())
                                        .suggests(SKILL_SUGGESTIONS)
                                        .executes(HybrideSkillCommand::executeUse))));
    }

    private static int executeGive(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        String skillId = StringArgumentType.getString(context, "competence");

        SkillUnlock.Outcome outcome = SkillUnlock.tryUnlock(target, skillId);
        if (!outcome.success()) {
            context.getSource().sendFailure(OriginelText.prefixed(outcome.message()));
            return 0;
        }
        HybridePlayer data = target.getData(HybrideAttachments.HYBRIDE_PLAYER);
        target.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.skill_unlocked_target", skillId)));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(Component.translatable(
                "originel.msg.skill_unlocked_staff_feedback", target.getName().getString(), skillId, data.getSkillPoints())), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeUse(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        String skillId = StringArgumentType.getString(context, "competence");
        SkillActivation.Outcome outcome = SkillActivation.tryActivate(target, skillId);
        if (!outcome.success()) {
            context.getSource().sendFailure(OriginelText.prefixed(outcome.message()));
            return 0;
        }
        return Command.SINGLE_SUCCESS;
    }
}
