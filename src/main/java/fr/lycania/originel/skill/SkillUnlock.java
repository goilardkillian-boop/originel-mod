package fr.lycania.originel.skill;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.faction.HybridePlayer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Shared validation/unlock logic for spending skill points, so /originel
 * skill give (staff, any target) and the client skill tree's network packet
 * (a player unlocking their own skill) apply exactly the same rules.
 */
public final class SkillUnlock {

    public enum Result {
        SUCCESS, NOT_HYBRIDE, UNKNOWN_SKILL, ALREADY_UNLOCKED, REQUIRES_MAX_LEVEL, NOT_ENOUGH_POINTS
    }

    public record Outcome(Result result, String message) {
        public boolean success() {
            return result == Result.SUCCESS;
        }
    }

    private SkillUnlock() {
    }

    public static Outcome tryUnlock(ServerPlayer target, String skillId) {
        if (!HybrideFaction.isHybride(target)) {
            return new Outcome(Result.NOT_HYBRIDE, target.getName().getString() + " n'est pas l'Hybride.");
        }
        var skillOpt = SkillRegistry.byId(skillId);
        if (skillOpt.isEmpty()) {
            return new Outcome(Result.UNKNOWN_SKILL, "Competence inconnue : " + skillId);
        }
        Skill skill = skillOpt.get();
        HybridePlayer data = target.getData(HybrideAttachments.HYBRIDE_PLAYER);

        if (data.hasSkill(skillId)) {
            return new Outcome(Result.ALREADY_UNLOCKED, target.getName().getString() + " a deja debloque " + skillId + ".");
        }
        if (skill.requiresMaxLevel()) {
            IFactionPlayerHandler handler = VampirismAPI.factionPlayerHandler(target);
            int max = HybrideFaction.get().getHighestReachableLevel();
            if (handler.getCurrentLevel(HybrideFaction.get()) < max) {
                return new Outcome(Result.REQUIRES_MAX_LEVEL, skillId + " necessite le niveau maximum (" + max + ").");
            }
        }
        if (data.getSkillPoints() < skill.cost()) {
            return new Outcome(Result.NOT_ENOUGH_POINTS,
                    "Pas assez de points de competence (" + data.getSkillPoints() + "/" + skill.cost() + ").");
        }

        data.addSkillPoints(-skill.cost());
        data.unlockSkill(skillId);
        skill.onUnlock(target, data);
        target.syncData(HybrideAttachments.HYBRIDE_PLAYER);
        return new Outcome(Result.SUCCESS, null);
    }
}
