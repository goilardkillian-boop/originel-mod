package fr.lycania.originel.skill;

import fr.lycania.originel.faction.HybridePlayer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.IntSupplier;

/** A skill triggered on demand via /originel skill use, gated by a configurable cooldown. */
public class ActiveSkill extends Skill {

    public interface Activation {
        void activate(ServerPlayer player, HybridePlayer data);
    }

    private final Activation activation;
    private final IntSupplier cooldownTicksSupplier;

    public ActiveSkill(String id, Branch branch, String displayName, IntSupplier costSupplier,
                        IntSupplier cooldownTicksSupplier, Activation activation, boolean requiresMaxLevel) {
        super(id, branch, SkillType.ACTIVE, displayName, costSupplier, requiresMaxLevel);
        this.cooldownTicksSupplier = cooldownTicksSupplier;
        this.activation = activation;
    }

    public int cooldownTicks() {
        return cooldownTicksSupplier.getAsInt();
    }

    public void activate(ServerPlayer player, HybridePlayer data) {
        activation.activate(player, data);
    }
}
