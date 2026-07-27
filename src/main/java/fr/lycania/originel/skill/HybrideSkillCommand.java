package fr.lycania.originel.skill;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import fr.lycania.originel.command.CommandUtil;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.commands.CommandSourceStack;
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

        if (!HybrideFaction.isHybride(target)) {
            context.getSource().sendFailure(OriginelText.prefixed(target.getName().getString() + " n'est pas l'Hybride."));
            return 0;
        }
        var skillOpt = SkillRegistry.byId(skillId);
        if (skillOpt.isEmpty()) {
            context.getSource().sendFailure(OriginelText.prefixed("Competence inconnue : " + skillId));
            return 0;
        }
        Skill skill = skillOpt.get();
        HybridePlayer data = target.getData(HybrideAttachments.HYBRIDE_PLAYER);

        if (data.hasSkill(skillId)) {
            context.getSource().sendFailure(OriginelText.prefixed(target.getName().getString() + " a deja debloque " + skillId + "."));
            return 0;
        }
        if (skill.requiresMaxLevel()) {
            IFactionPlayerHandler handler = VampirismAPI.factionPlayerHandler(target);
            int max = HybrideFaction.get().getHighestReachableLevel();
            if (handler.getCurrentLevel(HybrideFaction.get()) < max) {
                context.getSource().sendFailure(OriginelText.prefixed(skillId + " necessite le niveau maximum (" + max + ")."));
                return 0;
            }
        }
        if (data.getSkillPoints() < skill.cost()) {
            context.getSource().sendFailure(OriginelText.prefixed(
                    "Pas assez de points de competence (" + data.getSkillPoints() + "/" + skill.cost() + ")."));
            return 0;
        }
        data.addSkillPoints(-skill.cost());
        data.unlockSkill(skillId);
        skill.onUnlock(target, data);
        target.syncData(HybrideAttachments.HYBRIDE_PLAYER);
        target.sendSystemMessage(OriginelText.prefixed("Competence debloquee : " + skillId + "."));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(
                target.getName().getString() + " a debloque " + skillId + " (points restants : " + data.getSkillPoints() + ")."), true);
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
