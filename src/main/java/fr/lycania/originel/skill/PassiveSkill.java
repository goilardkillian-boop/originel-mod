package fr.lycania.originel.skill;

import fr.lycania.originel.faction.HybridePlayer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.IntSupplier;

/**
 * A skill applied continuously (re-asserted every tick) for as long as it's unlocked
 * and the owning player is the Hybride, and removed as soon as either stops being true.
 */
public class PassiveSkill extends Skill {

    public interface Effect {
        void apply(ServerPlayer player, HybridePlayer data);
    }

    private final Effect effect;
    private final Effect onRemove;

    public PassiveSkill(String id, Branch branch, String displayName, IntSupplier costSupplier, Effect effect, Effect onRemove) {
        super(id, branch, SkillType.PASSIVE, displayName, costSupplier, false);
        this.effect = effect;
        this.onRemove = onRemove;
    }

    public void tick(ServerPlayer player, HybridePlayer data) {
        effect.apply(player, data);
    }

    @Override
    public void onUnlock(ServerPlayer player, HybridePlayer data) {
        effect.apply(player, data);
    }

    @Override
    public void onLock(ServerPlayer player, HybridePlayer data) {
        if (onRemove != null) {
            onRemove.apply(player, data);
        }
    }
}
