package fr.lycania.originel.skill;

import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.HybrideConfig;
import fr.lycania.originel.config.SkillsConfig;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.faction.HybridePlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = OriginelMod.MODID)
public final class HybrideSkillEventHandler {

    private static final String COLERE_EXPIRY_KEY = "colere_originel_expiry";

    private HybrideSkillEventHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!HybrideFaction.isHybride(player)) {
            return;
        }
        HybridePlayer data = player.getData(HybrideAttachments.HYBRIDE_PLAYER);

        if (player.tickCount % 20 == 0) {
            for (Skill skill : SkillRegistry.all().values()) {
                if (skill instanceof PassiveSkill passive && data.hasSkill(skill.id())) {
                    passive.tick(player, data);
                }
            }
            // Attachments don't auto-sync on in-place mutation (unlockSkill/setCooldownExpiry/
            // addSkillPoints all mutate the object returned by getData rather than calling
            // setData) - the client-side skill wheel needs this pushed periodically instead.
            player.syncData(HybrideAttachments.HYBRIDE_PLAYER);
        }

        long expiry = data.getCooldownExpiry(COLERE_EXPIRY_KEY);
        if (expiry != 0 && player.level().getGameTime() >= expiry) {
            SkillRegistry.clearColereModifiers(player);
            data.setCooldownExpiry(COLERE_EXPIRY_KEY, 0);
        }

        emitMistTrail(player);
    }

    private static void emitMistTrail(ServerPlayer player) {
        HybrideConfig cfg = HybrideConfig.get();
        if (!cfg.trailEnabled() || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        int interval = Math.max(1, cfg.trailIntervalTicks());
        if (player.tickCount % interval != 0) {
            return;
        }
        double horizontalSpeed = player.getDeltaMovement().horizontalDistance();
        if (horizontalSpeed < cfg.trailMinSpeed() || player.isFallFlying()) {
            return;
        }
        ResourceLocation particleId = ResourceLocation.tryParse(cfg.trailParticle());
        if (particleId != null && BuiltInRegistries.PARTICLE_TYPE.get(particleId) instanceof ParticleOptions particle) {
            level.sendParticles(particle, player.getX(), player.getY() + 0.1, player.getZ(),
                    2, 0.25, 0.05, 0.25, 0.01);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!HybrideFaction.isHybride(player)) {
            return;
        }
        HybridePlayer data = player.getData(HybrideAttachments.HYBRIDE_PLAYER);
        if (data.hasSkill("peau_de_bete")) {
            double reduction = SkillsConfig.get().peauDeBeteDamageReductionPercent();
            event.setAmount((float) (event.getAmount() * (1 - reduction)));
        }
    }

    @SubscribeEvent
    public static void onDamagePost(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) {
            return;
        }
        if (!HybrideFaction.isHybride(attacker)) {
            return;
        }
        HybridePlayer data = attacker.getData(HybrideAttachments.HYBRIDE_PLAYER);
        if (data.hasSkill("morsure_vampirique")) {
            float heal = event.getNewDamage() * (float) SkillsConfig.get().morsureLifestealPercent();
            if (heal > 0) {
                attacker.heal(heal);
            }
            int foodPoints = Math.round(event.getNewDamage() * (float) SkillsConfig.get().morsureFoodRestore());
            if (foodPoints > 0) {
                attacker.getFoodData().eat(foodPoints, 1.0f);
            }
            if (attacker.level() instanceof ServerLevel level) {
                var victim = event.getEntity();
                level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, victim.getX(), victim.getEyeY(), victim.getZ(),
                        8, 0.3, 0.3, 0.3, 0.3);
            }
        }
    }
}
