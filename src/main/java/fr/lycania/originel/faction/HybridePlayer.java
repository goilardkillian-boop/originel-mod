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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public class HybridePlayer extends FactionBasePlayer<IHybridePlayer> implements IHybridePlayer {

    private ISkillHandler<IHybridePlayer> skillHandler;
    private IActionHandler<IHybridePlayer> actionHandler;

    public HybridePlayer(Player player) {
        super(player);
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
