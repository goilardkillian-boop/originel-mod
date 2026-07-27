package fr.lycania.originel.scellement;

import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.FaiblesseConfig;
import fr.lycania.originel.config.HybrideConfig;
import fr.lycania.originel.config.ScellementRitualConfig;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.UUID;

/**
 * Survival alternative to the staff-only /originel scellement: light a
 * complete Calice with the Briquet special to seal whoever is currently the
 * Hybride, for the same duration (faiblesse.toml#scellement_duration_ticks).
 * A boss bar visible to every online player counts the seal down; /originel
 * scellement stop cancels it early (mainly for testing).
 */
@EventBusSubscriber(modid = OriginelMod.MODID)
public final class ScellementRitualManager {

    private static ServerBossEvent bossEvent;
    private static UUID sealedPlayerUuid;

    private ScellementRitualManager() {
    }

    public static void light(ServerLevel level, BlockPos calicePos, ServerPlayer performer) {
        MinecraftServer server = level.getServer();
        UUID whitelisted = HybrideConfig.get().whitelistedUuid();
        ServerPlayer hybride = whitelisted != null ? server.getPlayerList().getPlayer(whitelisted) : null;
        if (hybride == null || !HybrideFaction.isHybride(hybride)) {
            performer.sendSystemMessage(OriginelText.prefixed(ScellementRitualConfig.get().messageFailNoHybride()));
            return;
        }

        HybridePlayer data = hybride.getData(HybrideAttachments.HYBRIDE_PLAYER);
        long duration = Math.max(1, FaiblesseConfig.get().scellementDurationTicks());
        data.setScellementExpiry(level.getGameTime() + duration);
        sealedPlayerUuid = hybride.getUUID();

        playLightEffect(level, calicePos);
        server.getPlayerList().broadcastSystemMessage(OriginelText.lore(ScellementRitualConfig.get().messageLight()), false);

        if (bossEvent == null) {
            bossEvent = new ServerBossEvent(Component.literal(ScellementRitualConfig.get().bossBarTitle()),
                    BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
            for (ServerPlayer online : server.getPlayerList().getPlayers()) {
                bossEvent.addPlayer(online);
            }
        }
        bossEvent.setProgress(1.0f);
        OriginelMod.LOGGER.info("Rituel de Scellement allume par {} : {} est scelle.",
                performer.getGameProfile().getName(), hybride.getGameProfile().getName());
    }

    /** Cancels the current scellement immediately - /originel scellement stop, for testing. */
    public static void stop(MinecraftServer server) {
        if (sealedPlayerUuid != null) {
            ServerPlayer hybride = server.getPlayerList().getPlayer(sealedPlayerUuid);
            if (hybride != null) {
                hybride.getData(HybrideAttachments.HYBRIDE_PLAYER).setScellementExpiry(0);
            }
        }
        sealedPlayerUuid = null;
        clearBossBar();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (sealedPlayerUuid == null || bossEvent == null) {
            return;
        }
        MinecraftServer server = event.getServer();
        ServerPlayer hybride = server.getPlayerList().getPlayer(sealedPlayerUuid);
        if (hybride == null) {
            return;
        }
        HybridePlayer data = hybride.getData(HybrideAttachments.HYBRIDE_PLAYER);
        long now = server.overworld().getGameTime();
        long expiry = data.getScellementExpiry();
        if (expiry <= now) {
            sealedPlayerUuid = null;
            clearBossBar();
            return;
        }
        long duration = Math.max(1, FaiblesseConfig.get().scellementDurationTicks());
        float progress = (float) Math.min(1.0, (expiry - now) / (double) duration);
        bossEvent.setProgress(progress);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (bossEvent != null && event.getEntity() instanceof ServerPlayer player) {
            bossEvent.addPlayer(player);
        }
    }

    private static void clearBossBar() {
        if (bossEvent != null) {
            bossEvent.removeAllPlayers();
            bossEvent = null;
        }
    }

    private static void playLightEffect(ServerLevel level, BlockPos pos) {
        ScellementRitualConfig cfg = ScellementRitualConfig.get();
        ResourceLocation particleId = ResourceLocation.tryParse(cfg.particleId());
        if (particleId != null && BuiltInRegistries.PARTICLE_TYPE.get(particleId) instanceof ParticleOptions particleOptions) {
            level.sendParticles(particleOptions, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 30, 0.3, 0.5, 0.3, 0.03);
        }
        ResourceLocation soundId = ResourceLocation.tryParse(cfg.soundId());
        if (soundId != null) {
            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(soundId);
            if (sound != null) {
                level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
}
