package fr.lycania.originel.skill;

import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.faction.HybridePlayer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Shared validation/activation logic for a skill's active use, so /originel
 * skill use (staff, any target) and the client skill wheel's network packet
 * (a player activating their own skill) apply exactly the same rules.
 */
public final class SkillActivation {

    public enum Result {
        SUCCESS, NOT_HYBRIDE, NOT_ACTIVE_SKILL, NOT_UNLOCKED, ON_COOLDOWN
    }

    public record Outcome(Result result, String message) {
        public boolean success() {
            return result == Result.SUCCESS;
        }
    }

    private SkillActivation() {
    }

    public static Outcome tryActivate(ServerPlayer target, String skillId) {
        if (!HybrideFaction.isHybride(target)) {
            return new Outcome(Result.NOT_HYBRIDE, target.getName().getString() + " n'est pas l'Hybride.");
        }
        var skillOpt = SkillRegistry.byId(skillId);
        if (skillOpt.isEmpty() || !(skillOpt.get() instanceof ActiveSkill activeSkill)) {
            return new Outcome(Result.NOT_ACTIVE_SKILL, skillId + " n'est pas une competence activable.");
        }
        HybridePlayer data = target.getData(HybrideAttachments.HYBRIDE_PLAYER);
        if (!data.hasSkill(skillId)) {
            return new Outcome(Result.NOT_UNLOCKED, target.getName().getString() + " n'a pas debloque " + skillId + ".");
        }
        long now = target.level().getGameTime();
        long readyAt = data.getCooldownExpiry(skillId);
        if (now < readyAt) {
            long remainingTicks = readyAt - now;
            return new Outcome(Result.ON_COOLDOWN, skillId + " est en recharge encore " + (remainingTicks / 20) + "s.");
        }
        activeSkill.activate(target, data);
        data.setCooldownExpiry(skillId, now + activeSkill.cooldownTicks());
        target.syncData(HybrideAttachments.HYBRIDE_PLAYER);
        return new Outcome(Result.SUCCESS, null);
    }
}
