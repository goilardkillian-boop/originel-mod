package fr.lycania.originel.skill;

import fr.lycania.originel.faction.HybridePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.IntSupplier;

public abstract class Skill {

    private final String id;
    private final Branch branch;
    private final SkillType type;
    private final String displayName;
    private final IntSupplier costSupplier;
    private final boolean requiresMaxLevel;

    protected Skill(String id, Branch branch, SkillType type, String displayName, IntSupplier costSupplier, boolean requiresMaxLevel) {
        this.id = id;
        this.branch = branch;
        this.type = type;
        this.displayName = displayName;
        this.costSupplier = costSupplier;
        this.requiresMaxLevel = requiresMaxLevel;
    }

    public String id() {
        return id;
    }

    public Branch branch() {
        return branch;
    }

    public SkillType type() {
        return type;
    }

    public Component displayName() {
        return Component.literal(displayName);
    }

    public int cost() {
        return costSupplier.getAsInt();
    }

    public boolean requiresMaxLevel() {
        return requiresMaxLevel;
    }

    /** Called once when the skill is unlocked. */
    public void onUnlock(ServerPlayer player, HybridePlayer data) {
    }

    /** Called once when the skill is removed/reset. */
    public void onLock(ServerPlayer player, HybridePlayer data) {
    }
}
