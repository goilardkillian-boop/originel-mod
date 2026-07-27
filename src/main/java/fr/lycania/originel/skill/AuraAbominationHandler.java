package fr.lycania.originel.skill;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.werewolves.api.WReference;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.SkillsConfig;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Etape 10 (+ etape 12) - the Aura d'Abomination skill (originel/SkillRegistry)
 * unlocks as a bare marker; its actual effect lives here:
 * <ul>
 *   <li>nearby creature-faction <b>players</b> get a discreet per-pulse
 *   signal (message/sound/malaise), following the same tick pattern used
 *   for peau_de_bete/morsure_vampirique in HybrideSkillEventHandler;</li>
 *   <li>every vampire/werewolf <b>mob</b> gets a real, persistent flee
 *   AI goal (vanilla's own AvoidEntityGoal, the same class hostile mobs use
 *   to run from players holding a mace/other fear-inducing effects) that
 *   activates whenever it's near a Hybride with this skill unlocked - not a
 *   one-off knockback, so it reliably applies to every single mob in range,
 *   for as long as the mod is loaded.</li>
 * </ul>
 */
@EventBusSubscriber(modid = OriginelMod.MODID)
public final class AuraAbominationHandler {

    private AuraAbominationHandler() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof PathfinderMob mob)) {
            return;
        }
        if (!isCreatureFaction(mob)) {
            return;
        }
        boolean alreadyInstalled = mob.goalSelector.getAvailableGoals().stream()
                .anyMatch(wrapped -> wrapped.getGoal() instanceof AvoidEntityGoal);
        if (alreadyInstalled) {
            return;
        }
        SkillsConfig cfg = SkillsConfig.get();
        mob.goalSelector.addGoal(0, new AvoidEntityGoal<>(mob, ServerPlayer.class,
                AuraAbominationHandler::isFearedHybride, cfg.auraRadius(),
                cfg.auraFleeWalkSpeedModifier(), cfg.auraFleeSprintSpeedModifier(),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
    }

    private static boolean isFearedHybride(LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player) || !HybrideFaction.isHybride(player)) {
            return false;
        }
        return player.getData(HybrideAttachments.HYBRIDE_PLAYER).hasSkill("aura_abomination");
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer hybride)) {
            return;
        }
        if (!HybrideFaction.isHybride(hybride)) {
            return;
        }
        HybridePlayer data = hybride.getData(HybrideAttachments.HYBRIDE_PLAYER);
        if (!data.hasSkill("aura_abomination")) {
            return;
        }

        SkillsConfig cfg = SkillsConfig.get();
        int interval = Math.max(1, cfg.auraIntervalTicks());
        if (hybride.tickCount % interval != 0) {
            return;
        }

        ResourceLocation soundId = ResourceLocation.tryParse(cfg.auraSound());
        SoundEvent sound = soundId != null ? BuiltInRegistries.SOUND_EVENT.get(soundId) : null;

        if (hybride.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.SOUL, hybride.getX(),
                    hybride.getY() + hybride.getBbHeight() * 0.5, hybride.getZ(), 18, 0.5, 0.6, 0.5, 0.02);
        }

        for (ServerPlayer nearby : hybride.level().getEntitiesOfClass(ServerPlayer.class,
                hybride.getBoundingBox().inflate(cfg.auraRadius()),
                p -> p != hybride && isCreatureFaction(p))) {
            nearby.sendSystemMessage(OriginelText.lore(cfg.auraMessage()));
            if (sound != null) {
                nearby.playNotifySound(sound, SoundSource.AMBIENT, 1.0f, 1.0f);
            }
            if (cfg.auraMalaiseDurationTicks() > 0) {
                nearby.addEffect(new MobEffectInstance(MobEffects.CONFUSION,
                        cfg.auraMalaiseDurationTicks(), cfg.auraMalaiseAmplifier()));
            }
        }
    }

    private static boolean isCreatureFaction(LivingEntity entity) {
        var faction = VampirismAPI.factionRegistry().getFaction(entity);
        return faction == VReference.VAMPIRE_FACTION || faction == WReference.WEREWOLF_FACTION;
    }
}
