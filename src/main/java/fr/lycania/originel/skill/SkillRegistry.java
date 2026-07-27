package fr.lycania.originel.skill;

import fr.lycania.originel.config.SkillsConfig;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.util.OriginelText;
import fr.lycania.originel.util.TargetingUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * All 15 Hybride skills plus the ultimate. Structure (branch, cost, cooldowns,
 * magnitudes) is entirely driven by skills.toml via SkillsConfig; only the
 * mechanism of each effect is code.
 */
public final class SkillRegistry {

    private static final ResourceLocation VELOCITE_MODIFIER = id("skill_velocite");
    private static final ResourceLocation FORCE_BESTIALE_MODIFIER = id("skill_force_bestiale");
    private static final ResourceLocation COLERE_SPEED_MODIFIER = id("skill_colere_speed");
    private static final ResourceLocation COLERE_DAMAGE_MODIFIER = id("skill_colere_damage");

    private static final Map<String, Skill> SKILLS = new LinkedHashMap<>();

    private SkillRegistry() {
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("originel", path);
    }

    private static void register(Skill skill) {
        SKILLS.put(skill.id(), skill);
    }

    public static Optional<Skill> byId(String id) {
        return Optional.ofNullable(SKILLS.get(id));
    }

    public static Map<String, Skill> all() {
        return SKILLS;
    }

    static {
        SkillsConfig cfg = SkillsConfig.get();

        // --- Branche Sang ---
        register(new PassiveSkill("velocite", Branch.SANG, cfg::velociteCost,
                (player, data) -> {
                    AttributeInstance instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (instance != null) {
                        instance.addOrReplacePermanentModifier(new AttributeModifier(VELOCITE_MODIFIER,
                                cfg.veloviteSpeedBonus(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                    }
                },
                (player, data) -> {
                    AttributeInstance instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (instance != null) {
                        instance.removeModifier(VELOCITE_MODIFIER);
                    }
                }));

        register(new ActiveSkill("regard_hypnotique", Branch.SANG, cfg::regardCost, cfg::regardCooldownTicks,
                (player, data) -> {
                    Optional<LivingEntity> target = TargetingUtil.getLookedAtEntity(player, cfg.regardRange());
                    if (target.isPresent()) {
                        target.get().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                                cfg.regardDurationTicks(), cfg.regardSlownessAmplifier()));
                        particles(player, new Vec3(target.get().getX(), target.get().getEyeY(), target.get().getZ()),
                                ParticleTypes.SOUL, 12, 0.25, 0.01);
                        player.sendSystemMessage(OriginelText.prefixed(Component.translatable(
                                "originel.msg.regard_success", target.get().getName().getString())));
                    } else {
                        player.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.regard_no_target")));
                    }
                }, false));

        register(new Skill("morsure_vampirique", Branch.SANG, SkillType.PASSIVE, cfg::morsureCost, false) {
        });

        register(new ActiveSkill("brume", Branch.SANG, cfg::brumeCost, cfg::brumeCooldownTicks,
                (player, data) -> {
                    Vec3 origin = player.position();
                    Vec3 look = player.getLookAngle();
                    particles(player, origin.add(0, 1, 0), ParticleTypes.CLOUD, 30, 0.4, 0.06);
                    player.teleportTo(player.getX() + look.x * cfg.brumeDistance(),
                            player.getY() + Math.max(0, look.y) * cfg.brumeDistance() * 0.3,
                            player.getZ() + look.z * cfg.brumeDistance());
                    particles(player, player.position().add(0, 1, 0), ParticleTypes.CLOUD, 30, 0.4, 0.06);
                    player.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.brume_success")));
                }, false));

        register(new PassiveSkill("odorat_sang", Branch.SANG, cfg::odoratSangCost,
                (player, data) -> {
                    double threshold = cfg.odoratSangHealthThreshold();
                    for (LivingEntity nearby : player.level().getEntitiesOfClass(LivingEntity.class,
                            player.getBoundingBox().inflate(cfg.odoratSangRadius()),
                            e -> e != player && e.isAlive() && e.getHealth() / e.getMaxHealth() <= threshold)) {
                        privateHighlight(player, nearby, ParticleTypes.DAMAGE_INDICATOR);
                    }
                },
                null));

        // --- Branche Lune ---
        register(new PassiveSkill("force_bestiale", Branch.LUNE, cfg::forceBestialeCost,
                (player, data) -> {
                    AttributeInstance instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (instance != null) {
                        instance.addOrReplacePermanentModifier(new AttributeModifier(FORCE_BESTIALE_MODIFIER,
                                cfg.forceBestialeDamageBonus(), AttributeModifier.Operation.ADD_VALUE));
                    }
                },
                (player, data) -> {
                    AttributeInstance instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (instance != null) {
                        instance.removeModifier(FORCE_BESTIALE_MODIFIER);
                    }
                }));

        register(new PassiveSkill("sens_aiguises", Branch.LUNE, cfg::sensAiguisesCost,
                (player, data) -> {
                    // Reapplied every 20 ticks (see HybrideSkillEventHandler); vanilla's
                    // GameRenderer.getNightVisionScale starts a sine-wave flicker once the
                    // remaining duration drops under 200 ticks, so anything close to that
                    // (220 previously) flickers on every refresh. 260 keeps a comfortable
                    // margin above the threshold at all times.
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false));
                    for (LivingEntity nearby : player.level().getEntitiesOfClass(LivingEntity.class,
                            player.getBoundingBox().inflate(cfg.sensAiguisesHighlightRadius()),
                            e -> e != player && e.isAlive())) {
                        privateHighlight(player, nearby, ParticleTypes.END_ROD);
                    }
                },
                null));

        register(new ActiveSkill("griffes", Branch.LUNE, cfg::griffesCost, cfg::griffesCooldownTicks,
                (player, data) -> {
                    Vec3 look = player.getLookAngle();
                    particles(player, player.position().add(0, 0.2, 0), ParticleTypes.POOF, 16, 0.3, 0.05);
                    player.setDeltaMovement(look.x * cfg.griffesLeapStrength(), Math.max(0.3, look.y * cfg.griffesLeapStrength()), look.z * cfg.griffesLeapStrength());
                    player.hurtMarked = true;
                    for (LivingEntity nearby : player.level().getEntitiesOfClass(LivingEntity.class,
                            player.getBoundingBox().inflate(3.0), e -> e != player && e.isAlive())) {
                        nearby.addEffect(new MobEffectInstance(MobEffects.POISON, cfg.griffesBleedDurationTicks(), 0));
                        particles(player, nearby.position().add(0, nearby.getBbHeight() * 0.5, 0), ParticleTypes.CRIT, 10, 0.2, 0.2);
                    }
                    player.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.griffes_success")));
                }, false));

        register(new Skill("peau_de_bete", Branch.LUNE, SkillType.PASSIVE, cfg::peauDeBeteCost, false) {
        });

        register(new ActiveSkill("hurlement_meute", Branch.LUNE, cfg::hurlementCost, cfg::hurlementCooldownTicks,
                (player, data) -> {
                    particles(player, player.position().add(0, player.getBbHeight() * 0.5, 0), ParticleTypes.SWEEP_ATTACK, 6, 0.4, 0.05);
                    playSound(player, cfg.hurlementSound());
                    for (LivingEntity nearby : player.level().getEntitiesOfClass(LivingEntity.class,
                            player.getBoundingBox().inflate(cfg.hurlementRadius()), e -> e instanceof Monster && e.isAlive())) {
                        nearby.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, cfg.hurlementFearDurationTicks(), cfg.hurlementFearAmplifier()));
                        nearby.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, cfg.hurlementFearDurationTicks(), cfg.hurlementFearAmplifier()));
                    }
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, cfg.hurlementSelfBuffDurationTicks(), cfg.hurlementSelfBuffAmplifier(), true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, cfg.hurlementSelfBuffDurationTicks(), cfg.hurlementSelfBuffAmplifier(), true, false));
                    player.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.hurlement_success")));
                }, false));

        // --- Branche Originel ---
        register(new Skill("aura_abomination", Branch.ORIGINEL, SkillType.PASSIVE, cfg::auraCost, false) {
        });

        register(new PassiveSkill("regeneration_impie", Branch.ORIGINEL, cfg::regenerationImpieCost,
                (player, data) -> {
                    if (!data.isTransformed()) {
                        return;
                    }
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, cfg.regenerationImpieAmplifier(), true, false));
                },
                null));

        register(new ActiveSkill("metamorphose", Branch.ORIGINEL, cfg::metamorphoseCost, cfg::metamorphoseCooldownTicks,
                (player, data) -> {
                    boolean now = !data.isTransformed();
                    data.setTransformed(now);
                    particles(player, player.position().add(0, player.getBbHeight() * 0.5, 0), ParticleTypes.LARGE_SMOKE, 24, 0.4, 0.03);
                    player.sendSystemMessage(OriginelText.prefixed(Component.translatable(
                            now ? "originel.msg.metamorphose_on" : "originel.msg.metamorphose_off")));
                }, false));

        register(new ActiveSkill("commandement", Branch.ORIGINEL, cfg::commandementCost, cfg::commandementCooldownTicks,
                (player, data) -> {
                    Optional<LivingEntity> target = TargetingUtil.getLookedAtEntity(player, 16);
                    if (target.isPresent()) {
                        target.get().addEffect(new MobEffectInstance(MobEffects.GLOWING, cfg.commandementDurationTicks(), 0));
                        particles(player, target.get().position().add(0, target.get().getBbHeight() + 0.3, 0),
                                ParticleTypes.END_ROD, 14, 0.3, 0.04);
                        player.sendSystemMessage(OriginelText.prefixed(Component.translatable(
                                "originel.msg.commandement_success", target.get().getName().getString())));
                    } else {
                        player.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.commandement_no_target")));
                    }
                }, false));

        // --- Ultime ---
        register(new ActiveSkill("colere_originel", Branch.ULTIME, cfg::colereCost, cfg::colereCooldownTicks,
                (player, data) -> {
                    long expiry = player.level().getGameTime() + cfg.colereDurationTicks();
                    applyModifier(player, Attributes.MOVEMENT_SPEED, COLERE_SPEED_MODIFIER,
                            cfg.veloviteSpeedBonus() * (cfg.colereMultiplier() - 1), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    applyModifier(player, Attributes.ATTACK_DAMAGE, COLERE_DAMAGE_MODIFIER,
                            cfg.forceBestialeDamageBonus() * (cfg.colereMultiplier() - 1), AttributeModifier.Operation.ADD_VALUE);
                    data.setCooldownExpiry("colere_originel_expiry", expiry);
                    particles(player, player.position().add(0, player.getBbHeight() * 0.5, 0), ParticleTypes.FLAME, 40, 0.5, 0.08);
                    particles(player, player.position(), ParticleTypes.CRIT, 24, 0.6, 0.15);
                    player.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.colere_success")));
                }, true));
    }

    private static void particles(ServerPlayer player, Vec3 pos, ParticleOptions type, int count, double spread, double speed) {
        if (player.level() instanceof ServerLevel level) {
            level.sendParticles(type, pos.x, pos.y, pos.z, count, spread, spread, spread, speed);
        }
    }

    /**
     * Unlike {@link #particles}, sent only to {@code viewer} (ServerLevel's
     * per-player overload) - used for hunting-utility highlights (Odorat du
     * sang, Sens aiguises) so only the Hybride sees who's being tracked,
     * instead of vanilla's MobEffects.GLOWING which outlines the target for
     * every nearby player regardless of who applied it.
     */
    private static void playSound(ServerPlayer player, String soundId) {
        ResourceLocation id = ResourceLocation.tryParse(soundId);
        var sound = id != null ? BuiltInRegistries.SOUND_EVENT.get(id) : null;
        if (sound != null && player.level() instanceof ServerLevel level) {
            level.playSound(null, player.blockPosition(), sound, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    private static void privateHighlight(ServerPlayer viewer, LivingEntity target, ParticleOptions type) {
        if (viewer.level() instanceof ServerLevel level) {
            level.sendParticles(viewer, type, true,
                    target.getX(), target.getY() + target.getBbHeight() + 0.3, target.getZ(),
                    1, 0.15, 0.1, 0.15, 0.0);
        }
    }

    private static void applyModifier(ServerPlayer player, Holder<Attribute> attribute, ResourceLocation modifierId, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.addOrReplacePermanentModifier(new AttributeModifier(modifierId, amount, operation));
        }
    }

    public static void clearColereModifiers(ServerPlayer player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(COLERE_SPEED_MODIFIER);
        }
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(COLERE_DAMAGE_MODIFIER);
        }
    }
}
