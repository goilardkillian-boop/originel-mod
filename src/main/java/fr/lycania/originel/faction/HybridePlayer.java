package fr.lycania.originel.faction;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.entity.player.FactionBasePlayer;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class HybridePlayer extends FactionBasePlayer<IHybridePlayer> implements IHybridePlayer {

    private ISkillHandler<IHybridePlayer> skillHandler;
    private IActionHandler<IHybridePlayer> actionHandler;
    private int skillPoints;
    private int killProgress;
    private final Set<String> unlockedSkills = new HashSet<>();
    private final Map<String, Long> skillCooldowns = new HashMap<>();
    private boolean transformed;

    public HybridePlayer(Player player) {
        super(player);
    }

    public Set<String> getUnlockedSkills() {
        return unlockedSkills;
    }

    public boolean hasSkill(String skillId) {
        return unlockedSkills.contains(skillId);
    }

    public boolean unlockSkill(String skillId) {
        return unlockedSkills.add(skillId);
    }

    public boolean lockSkill(String skillId) {
        return unlockedSkills.remove(skillId);
    }

    /** Game time (in ticks, {@link Level#getGameTime()}) after which the given skill can be used again. */
    public long getCooldownExpiry(String skillId) {
        return skillCooldowns.getOrDefault(skillId, 0L);
    }

    public void setCooldownExpiry(String skillId, long gameTime) {
        skillCooldowns.put(skillId, gameTime);
    }

    public boolean isTransformed() {
        return transformed;
    }

    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void addSkillPoints(int amount) {
        skillPoints = Math.max(0, skillPoints + amount);
    }

    public boolean spendSkillPoint() {
        if (skillPoints <= 0) {
            return false;
        }
        skillPoints--;
        return true;
    }

    public int getKillProgress() {
        return killProgress;
    }

    public void incrementKillProgress() {
        killProgress++;
    }

    public void resetKillProgress() {
        killProgress = 0;
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putInt("skill_points", skillPoints);
        tag.putInt("kill_progress", killProgress);
        tag.putBoolean("transformed", transformed);
        ListTag skillList = new ListTag();
        unlockedSkills.forEach(id -> skillList.add(StringTag.valueOf(id)));
        tag.put("unlocked_skills", skillList);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        skillPoints = nbt.getInt("skill_points");
        killProgress = nbt.getInt("kill_progress");
        transformed = nbt.getBoolean("transformed");
        unlockedSkills.clear();
        for (var element : nbt.getList("unlocked_skills", StringTag.TAG_STRING)) {
            unlockedSkills.add(element.getAsString());
        }
    }

    @Override
    public @NotNull IPlayableFaction<IHybridePlayer> getFaction() {
        return HybrideFaction.get();
    }

    @Override
    public boolean canLeaveFaction() {
        return false;
    }

    @Override
    public @Nullable IFaction<?> getDisguisedAs() {
        return null;
    }

    @Override
    public boolean isDisguised() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return HybrideFaction.get().getHighestReachableLevel();
    }

    @Override
    public Predicate<LivingEntity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        return livingEntity -> false;
    }

    @Override
    public @NotNull ISkillHandler<IHybridePlayer> getSkillHandler() {
        if (skillHandler == null) {
            skillHandler = new SkillHandler<>(this, getFaction());
        }
        return skillHandler;
    }

    @Override
    public IActionHandler<IHybridePlayer> getActionHandler() {
        if (actionHandler == null) {
            actionHandler = new ActionHandler<>(this);
        }
        return actionHandler;
    }

    @Override
    public ResourceLocation getAttachedKey() {
        return HybrideAttachments.Keys.HYBRIDE_PLAYER;
    }

    @Override
    public String nbtKey() {
        return getAttachedKey().getPath();
    }

    // IPlayerEventListener hooks Vampirism's own systems may call. Originel's own
    // gameplay logic (invincibility, aura, etc.) hooks NeoForge events directly
    // instead of relying on these being invoked, so they're intentionally no-ops.

    @Override
    public void onChangedDimension(ResourceKey<Level> from, ResourceKey<Level> to) {
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amount) {
        return false;
    }

    @Override
    public void onJoinWorld() {
    }

    @Override
    public void onPlayerLoggedIn() {
    }

    @Override
    public void onPlayerLoggedOut() {
    }

    @Override
    public void onUpdatePlayer(PlayerTickEvent event) {
    }

    public static class Factory implements Function<IAttachmentHolder, HybridePlayer> {
        @Override
        public HybridePlayer apply(IAttachmentHolder holder) {
            if (holder instanceof Player player) {
                return new HybridePlayer(player);
            }
            throw new IllegalArgumentException("Cannot create Hybride player attachment for holder " + holder.getClass() + ". Expected Player");
        }
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, HybridePlayer> {
        @Override
        public @NotNull HybridePlayer read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.Provider provider) {
            if (holder instanceof Player player) {
                HybridePlayer hybridePlayer = new HybridePlayer(player);
                hybridePlayer.deserializeNBT(provider, tag);
                return hybridePlayer;
            }
            throw new IllegalArgumentException("Expected Player, got " + holder.getClass().getSimpleName());
        }

        @Override
        public CompoundTag write(HybridePlayer attachment, HolderLookup.Provider provider) {
            return attachment.serializeNBT(provider);
        }
    }
}
