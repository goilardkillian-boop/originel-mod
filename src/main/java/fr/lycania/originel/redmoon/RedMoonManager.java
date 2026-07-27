package fr.lycania.originel.redmoon;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.werewolves.api.WReference;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.LuneRougeConfig;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = OriginelMod.MODID)
public final class RedMoonManager {

    private static final ResourceLocation DAMAGE_MODIFIER = ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "lunerouge_damage");
    private static final ResourceLocation SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "lunerouge_speed");

    private static boolean wasNight = false;
    private static int nightsSinceLastAuto = 0;
    private static long activatedAtTick = -1;

    /** Grace period after activation before the "ended at dawn" check kicks in: the world's
     * skyDarken (which isNight()/isDay() read) only catches up to a forced setDayTime() on the
     * environment tick, not instantly, so checking too early would see stale day/night state. */
    private static final long DAWN_CHECK_GRACE_TICKS = 100;
    private static final long NIGHT_START_DAY_TIME = 13000L;

    private RedMoonManager() {
    }

    public static void start(MinecraftServer server) {
        if (RedMoonState.isActive()) {
            return;
        }
        ServerLevel overworld = server.overworld();
        if (!overworld.isNight()) {
            // The Lune Rouge is by definition a night event; forcing nightfall also
            // avoids immediately auto-ending the event (see onServerTick) if it's
            // triggered manually during the day.
            long current = overworld.getDayTime();
            overworld.setDayTime(current - (current % 24000) + NIGHT_START_DAY_TIME);
        }
        wasNight = true;
        activatedAtTick = server.getTickCount();
        RedMoonState.setActive(true);
        server.getPlayerList().broadcastSystemMessage(OriginelText.lore(LuneRougeConfig.get().messageStart()), false);
        if (LuneRougeConfig.get().creatureBonusEnabled()) {
            forEachCreature(server, RedMoonManager::applyCreatureBonus);
        }
        OriginelMod.LOGGER.info("La Lune Rouge se leve.");
    }

    public static void stop(MinecraftServer server) {
        if (!RedMoonState.isActive()) {
            return;
        }
        RedMoonState.setActive(false);
        server.getPlayerList().broadcastSystemMessage(OriginelText.lore(LuneRougeConfig.get().messageEnd()), false);
        forEachCreature(server, RedMoonManager::clearCreatureBonus);
        OriginelMod.LOGGER.info("La Lune Rouge s'eteint avec l'aube.");
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ServerLevel overworld = server.overworld();
        boolean isNight = overworld.isNight();

        if (RedMoonState.isActive() && !isNight && server.getTickCount() - activatedAtTick > DAWN_CHECK_GRACE_TICKS) {
            stop(server);
        }

        if (isNight && !wasNight) {
            nightsSinceLastAuto++;
            if (LuneRougeConfig.get().autoEnabled() && !RedMoonState.isActive()
                    && nightsSinceLastAuto >= Math.max(1, LuneRougeConfig.get().autoFrequencyNights())) {
                nightsSinceLastAuto = 0;
                start(server);
            }
        }
        wasNight = isNight;

        if (RedMoonState.isActive() && LuneRougeConfig.get().particleEnabled() && server.getTickCount() % 10 == 0) {
            spawnAmbianceParticles(server);
        }
        if (RedMoonState.isActive() && LuneRougeConfig.get().creatureBonusEnabled() && server.getTickCount() % 200 == 0) {
            forEachCreature(server, RedMoonManager::applyCreatureBonus);
        }
    }

    private static void spawnAmbianceParticles(MinecraftServer server) {
        ResourceLocation particleId = ResourceLocation.tryParse(LuneRougeConfig.get().particleId());
        if (particleId == null) {
            return;
        }
        var particleType = BuiltInRegistries.PARTICLE_TYPE.get(particleId);
        if (!(particleType instanceof ParticleOptions particleOptions)) {
            return;
        }
        int radius = LuneRougeConfig.get().particleRadius();
        int count = LuneRougeConfig.get().particleCount();
        for (Player player : server.getPlayerList().getPlayers()) {
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(particleOptions,
                        player.getX() + (Math.random() - 0.5) * radius,
                        player.getY() + Math.random() * 4,
                        player.getZ() + (Math.random() - 0.5) * radius,
                        count, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }

    private static void forEachCreature(MinecraftServer server, java.util.function.Consumer<LivingEntity> action) {
        for (ServerLevel level : server.getAllLevels()) {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, level.getWorldBorder().getCollisionShape().bounds(), e -> true)) {
                if (isCreatureFaction(entity)) {
                    action.accept(entity);
                }
            }
        }
    }

    private static boolean isCreatureFaction(LivingEntity entity) {
        var faction = VampirismAPI.factionRegistry().getFaction(entity);
        return faction == VReference.VAMPIRE_FACTION || faction == WReference.WEREWOLF_FACTION;
    }

    private static void applyCreatureBonus(LivingEntity entity) {
        LuneRougeConfig cfg = LuneRougeConfig.get();
        AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.addOrReplacePermanentModifier(new AttributeModifier(DAMAGE_MODIFIER,
                    cfg.creatureBonusDamageMultiplier() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.addOrReplacePermanentModifier(new AttributeModifier(SPEED_MODIFIER,
                    cfg.creatureBonusSpeedMultiplier() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private static void clearCreatureBonus(LivingEntity entity) {
        AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(DAMAGE_MODIFIER);
        }
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_MODIFIER);
        }
    }
}
