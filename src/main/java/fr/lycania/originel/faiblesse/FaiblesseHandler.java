package fr.lycania.originel.faiblesse;

import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.FaiblesseConfig;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.item.OriginelDataComponents;
import fr.lycania.originel.item.OriginelItems;
import fr.lycania.originel.redmoon.RedMoonState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Once the Hybride has dropped its human mask (Metamorphose on - its true,
 * exposed form), it is immune to all damage. The single exception: the
 * attacker wields the Dague de l'Originel, and every enabled condition in
 * faiblesse.toml holds - then damage goes through, multiplied.
 * <p>
 * Keeping the mask on (the default state) trades that protection - and
 * Regeneration impie - away: masked, the Hybride passes for human and takes
 * damage like anyone else, with no ricochet and no special dagger handling.
 */
@EventBusSubscriber(modid = OriginelMod.MODID)
public final class FaiblesseHandler {

    private FaiblesseHandler() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer victim)) {
            return;
        }
        if (!HybrideFaction.isHybride(victim)) {
            return;
        }
        HybridePlayer data = victim.getData(HybrideAttachments.HYBRIDE_PLAYER);
        if (!data.isTransformed()) {
            return;
        }

        if (canPierceInvincibility(victim, event.getSource())) {
            event.setAmount((float) (event.getAmount() * FaiblesseConfig.get().damageMultiplier()));
            return;
        }
        event.setCanceled(true);
        playRicochet(victim);
    }

    private static boolean canPierceInvincibility(ServerPlayer victim, DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker)) {
            return false;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (!weapon.is(OriginelItems.DAGUE_ORIGINEL.get())) {
            return false;
        }

        FaiblesseConfig cfg = FaiblesseConfig.get();

        if (cfg.conditionSangGardienEnabled() && !Boolean.TRUE.equals(weapon.get(OriginelDataComponents.SANG_GARDIEN.get()))) {
            return false;
        }

        if (cfg.conditionScellementEnabled()) {
            HybridePlayer data = victim.getData(HybrideAttachments.HYBRIDE_PLAYER);
            if (!data.isScellee(victim.level().getGameTime())) {
                return false;
            }
        }

        boolean moonRequirementActive = cfg.conditionLunePleineEnabled() || cfg.conditionLuneRougeEnabled();
        if (moonRequirementActive) {
            boolean fullMoon = cfg.conditionLunePleineEnabled() && isFullMoon(victim.level());
            boolean redMoon = cfg.conditionLuneRougeEnabled() && RedMoonState.isActive();
            if (!fullMoon && !redMoon) {
                return false;
            }
        }

        return true;
    }

    private static boolean isFullMoon(Level level) {
        return level.dimensionType().moonPhase(level.getDayTime()) == 0;
    }

    private static void playRicochet(ServerPlayer victim) {
        if (!(victim.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        FaiblesseConfig cfg = FaiblesseConfig.get();

        ResourceLocation soundId = ResourceLocation.tryParse(cfg.ricochetSound());
        SoundEvent sound = soundId != null ? BuiltInRegistries.SOUND_EVENT.get(soundId) : null;
        if (sound != null) {
            serverLevel.playSound(null, victim.blockPosition(), sound, SoundSource.PLAYERS, 1.0f, 1.2f);
        }

        ResourceLocation particleId = ResourceLocation.tryParse(cfg.ricochetParticle());
        if (particleId != null) {
            var particleType = BuiltInRegistries.PARTICLE_TYPE.get(particleId);
            if (particleType instanceof ParticleOptions particleOptions) {
                serverLevel.sendParticles(particleOptions, victim.getX(), victim.getEyeY(), victim.getZ(), 12, 0.3, 0.3, 0.3, 0.02);
            }
        }
    }
}
