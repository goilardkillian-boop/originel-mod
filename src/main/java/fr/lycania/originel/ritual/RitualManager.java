package fr.lycania.originel.ritual;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.block.AutelDuVoileBlockEntity;
import fr.lycania.originel.block.OriginelBlocks;
import fr.lycania.originel.config.HybrideConfig;
import fr.lycania.originel.config.RituelConfig;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.redmoon.RedMoonManager;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.UUID;

@EventBusSubscriber(modid = OriginelMod.MODID)
public final class RitualManager {

    public enum StartResult {
        SUCCESS,
        NOT_WHITELISTED,
        PLAYER_OFFLINE,
        ALREADY_HYBRIDE,
        NO_ALTAR
    }

    private static BlockPos pendingAltarPos;
    private static ServerLevel pendingLevel;
    private static UUID pendingPlayer;
    private static long completeAtTick = -1;

    private RitualManager() {
    }

    public static StartResult start(MinecraftServer server) {
        UUID whitelisted = HybrideConfig.get().whitelistedUuid();
        if (whitelisted == null) {
            return StartResult.NOT_WHITELISTED;
        }
        ServerPlayer player = server.getPlayerList().getPlayer(whitelisted);
        if (player == null) {
            return StartResult.PLAYER_OFFLINE;
        }
        if (HybrideFaction.isHybride(player)) {
            return StartResult.ALREADY_HYBRIDE;
        }
        BlockPos altarPos = findNearbyCompleteAltar(player);
        if (altarPos == null) {
            return StartResult.NO_ALTAR;
        }

        pendingAltarPos = altarPos;
        pendingLevel = (ServerLevel) player.level();
        pendingPlayer = whitelisted;
        completeAtTick = server.getTickCount() + Math.max(1, RituelConfig.get().sequenceDurationTicks());

        server.getPlayerList().broadcastSystemMessage(OriginelText.lore(RituelConfig.get().messageStart()), false);
        playSequenceEffect(pendingLevel, altarPos);
        OriginelMod.LOGGER.info("Rituel d'Hybridation lance, autel en {}.", altarPos);
        return StartResult.SUCCESS;
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (completeAtTick < 0) {
            return;
        }
        MinecraftServer server = event.getServer();
        if (server.getTickCount() < completeAtTick) {
            if (server.getTickCount() % 10 == 0) {
                playSequenceEffect(pendingLevel, pendingAltarPos);
            }
            return;
        }
        complete(server);
    }

    private static void complete(MinecraftServer server) {
        BlockPos altarPos = pendingAltarPos;
        ServerLevel level = pendingLevel;
        UUID playerUuid = pendingPlayer;
        completeAtTick = -1;
        pendingAltarPos = null;
        pendingLevel = null;
        pendingPlayer = null;

        ServerPlayer player = server.getPlayerList().getPlayer(playerUuid);
        if (player == null) {
            OriginelMod.LOGGER.warn("Le rituel n'a pas pu se terminer : le joueur whitelist s'est deconnecte.");
            return;
        }
        if (level.getBlockEntity(altarPos) instanceof AutelDuVoileBlockEntity altar) {
            altar.consumeAll();
        }

        IFactionPlayerHandler handler = VampirismAPI.factionPlayerHandler(player);
        handler.setFactionAndLevel(HybrideFaction.get(), 1);

        if (RituelConfig.get().triggerLuneRouge()) {
            RedMoonManager.start(server);
        }
        server.getPlayerList().broadcastSystemMessage(OriginelText.lore(RituelConfig.get().messageComplete()), false);
        OriginelMod.LOGGER.info("Rituel d'Hybridation termine : {} est devenu l'Originel.", player.getGameProfile().getName());
    }

    private static BlockPos findNearbyCompleteAltar(ServerPlayer player) {
        int radius = RituelConfig.get().altarSearchRadius();
        BlockPos center = player.blockPosition();
        ServerLevel level = (ServerLevel) player.level();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(OriginelBlocks.AUTEL_DU_VOILE.get())
                    && level.getBlockEntity(pos) instanceof AutelDuVoileBlockEntity altar
                    && altar.isComplete()) {
                return pos.immutable();
            }
        }
        return null;
    }

    private static void playSequenceEffect(ServerLevel level, BlockPos pos) {
        if (level == null || pos == null) {
            return;
        }
        ResourceLocation particleId = ResourceLocation.tryParse(RituelConfig.get().particleId());
        if (particleId != null && BuiltInRegistries.PARTICLE_TYPE.get(particleId) instanceof ParticleOptions particleOptions) {
            level.sendParticles(particleOptions, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 20, 0.4, 0.6, 0.4, 0.03);
        }
        ResourceLocation soundId = ResourceLocation.tryParse(RituelConfig.get().soundId());
        if (soundId != null) {
            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(soundId);
            if (sound != null) {
                level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
}
