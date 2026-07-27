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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Etape 10 - the Aura d'Abomination skill (originel/SkillRegistry) unlocks
 * as a bare marker; its actual effect (discreet signal to nearby
 * creature-faction players) lives here, following the same pattern used for
 * peau_de_bete/morsure_vampirique in HybrideSkillEventHandler.
 */
@EventBusSubscriber(modid = OriginelMod.MODID)
public final class AuraAbominationHandler {

    private AuraAbominationHandler() {
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

    private static boolean isCreatureFaction(ServerPlayer player) {
        var faction = VampirismAPI.factionRegistry().getFaction(player);
        return faction == VReference.VAMPIRE_FACTION || faction == WReference.WEREWOLF_FACTION;
    }
}
