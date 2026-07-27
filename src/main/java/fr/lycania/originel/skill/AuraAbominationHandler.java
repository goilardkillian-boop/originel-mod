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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Etape 10 (+ etape 12) - the Aura d'Abomination skill (originel/SkillRegistry)
 * unlocks as a bare marker; its actual effect lives here. It only fires while
 * the Hybride has dropped its human mask (Metamorphose on) - see FaiblesseHandler
 * for the matching tradeoff, since exposure trades the mask's damage immunity
 * for detectability:
 * <ul>
 *   <li>nearby creature-faction <b>players</b> get a discreet signal
 *   (message/sound/malaise) once when they enter the aura's radius - not
 *   repeated on a timer, which used to spam their chat and re-nauseate them
 *   every few seconds for as long as they stood nearby. WARNED tracks who's
 *   already been notified per Hybride so the same player gets a fresh
 *   warning only after actually leaving and re-entering range;</li>
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

    private static final Map<UUID, Set<UUID>> WARNED = new HashMap<>();

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
        HybridePlayer data = player.getData(HybrideAttachments.HYBRIDE_PLAYER);
        return data.hasSkill("aura_abomination") && data.isTransformed();
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
        if (!data.hasSkill("aura_abomination") || !data.isTransformed()) {
            WARNED.remove(hybride.getUUID());
            return;
        }

        SkillsConfig cfg = SkillsConfig.get();
        int interval = Math.max(1, cfg.auraIntervalTicks());
        if (hybride.tickCount % interval != 0) {
            return;
        }

        ResourceLocation soundId = ResourceLocation.tryParse(cfg.auraSound());
        SoundEvent sound = soundId != null ? BuiltInRegistries.SOUND_EVENT.get(soundId) : null;

        Set<UUID> warned = WARNED.computeIfAbsent(hybride.getUUID(), id -> new HashSet<>());
        Set<UUID> stillNear = new HashSet<>();
        for (ServerPlayer nearby : hybride.level().getEntitiesOfClass(ServerPlayer.class,
                hybride.getBoundingBox().inflate(cfg.auraRadius()),
                p -> p != hybride && isCreatureFaction(p))) {
            stillNear.add(nearby.getUUID());
            if (!warned.add(nearby.getUUID())) {
                continue;
            }
            nearby.sendSystemMessage(OriginelText.lore(cfg.auraMessage()));
            if (sound != null) {
                nearby.playNotifySound(sound, SoundSource.AMBIENT, 1.0f, 1.0f);
            }
            if (cfg.auraMalaiseDurationTicks() > 0) {
                nearby.addEffect(new MobEffectInstance(MobEffects.CONFUSION,
                        cfg.auraMalaiseDurationTicks(), cfg.auraMalaiseAmplifier()));
            }
        }
        warned.retainAll(stillNear);
    }

    private static boolean isCreatureFaction(LivingEntity entity) {
        var faction = VampirismAPI.factionRegistry().getFaction(entity);
        return faction == VReference.VAMPIRE_FACTION || faction == WReference.WEREWOLF_FACTION;
    }
}
