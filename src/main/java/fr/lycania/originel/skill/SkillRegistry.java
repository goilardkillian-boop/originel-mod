package fr.lycania.originel.skill;

import fr.lycania.originel.config.SkillsConfig;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.util.OriginelText;
import fr.lycania.originel.util.TargetingUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * All 13 Hybride skills plus the ultimate. Structure (branch, cost, cooldowns,
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
        register(new PassiveSkill("velocite", Branch.SANG, "Velocite", cfg::velociteCost,
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

        register(new ActiveSkill("regard_hypnotique", Branch.SANG, "Regard hypnotique", cfg::regardCost, cfg::regardCooldownTicks,
                (player, data) -> {
                    Optional<LivingEntity> target = TargetingUtil.getLookedAtEntity(player, cfg.regardRange());
                    if (target.isPresent()) {
                        target.get().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                                cfg.regardDurationTicks(), cfg.regardSlownessAmplifier()));
                        player.sendSystemMessage(OriginelText.prefixed("Le regard de l'Originel paralyse " + target.get().getName().getString() + "."));
                    } else {
                        player.sendSystemMessage(OriginelText.prefixed("Aucune cible dans le regard de l'Originel."));
                    }
                }, false));

        register(new Skill("morsure_vampirique", Branch.SANG, SkillType.PASSIVE, "Morsure vampirique", cfg::morsureCost, false) {
        });

        register(new ActiveSkill("brume", Branch.SANG, "Brume", cfg::brumeCost, cfg::brumeCooldownTicks,
                (player, data) -> {
                    Vec3 look = player.getLookAngle();
                    player.teleportTo(player.getX() + look.x * cfg.brumeDistance(),
                            player.getY() + Math.max(0, look.y) * cfg.brumeDistance() * 0.3,
                            player.getZ() + look.z * cfg.brumeDistance());
                    player.sendSystemMessage(OriginelText.prefixed("L'Originel se dissout en brume et se reforme plus loin."));
                }, false));

        // --- Branche Lune ---
        register(new PassiveSkill("force_bestiale", Branch.LUNE, "Force bestiale", cfg::forceBestialeCost,
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

        register(new PassiveSkill("sens_aiguises", Branch.LUNE, "Sens aiguises", cfg::sensAiguisesCost,
                (player, data) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
                    for (LivingEntity nearby : player.level().getEntitiesOfClass(LivingEntity.class,
                            player.getBoundingBox().inflate(cfg.sensAiguisesHighlightRadius()),
                            e -> e != player && e.isAlive())) {
                        nearby.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, true, false));
                    }
                },
                null));

        register(new ActiveSkill("griffes", Branch.LUNE, "Griffes", cfg::griffesCost, cfg::griffesCooldownTicks,
                (player, data) -> {
                    Vec3 look = player.getLookAngle();
                    player.setDeltaMovement(look.x * cfg.griffesLeapStrength(), Math.max(0.3, look.y * cfg.griffesLeapStrength()), look.z * cfg.griffesLeapStrength());
                    player.hurtMarked = true;
                    for (LivingEntity nearby : player.level().getEntitiesOfClass(LivingEntity.class,
                            player.getBoundingBox().inflate(3.0), e -> e != player && e.isAlive())) {
                        nearby.addEffect(new MobEffectInstance(MobEffects.POISON, cfg.griffesBleedDurationTicks(), 0));
                    }
                    player.sendSystemMessage(OriginelText.prefixed("L'Originel bondit, griffes en avant."));
                }, false));

        register(new Skill("peau_de_bete", Branch.LUNE, SkillType.PASSIVE, "Peau de bete", cfg::peauDeBeteCost, false) {
        });

        // --- Branche Originel ---
        register(new Skill("aura_abomination", Branch.ORIGINEL, SkillType.PASSIVE, "Aura d'Abomination", cfg::auraCost, false) {
        });

        register(new PassiveSkill("regeneration_impie", Branch.ORIGINEL, "Regeneration impie", cfg::regenerationImpieCost,
                (player, data) -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, cfg.regenerationImpieAmplifier(), true, false)),
                null));

        register(new ActiveSkill("metamorphose", Branch.ORIGINEL, "Metamorphose", cfg::metamorphoseCost, cfg::metamorphoseCooldownTicks,
                (player, data) -> {
                    boolean now = !data.isTransformed();
                    data.setTransformed(now);
                    player.sendSystemMessage(OriginelText.prefixed(now
                            ? "L'Originel abandonne son masque humain."
                            : "L'Originel reprend forme humaine."));
                }, false));

        register(new ActiveSkill("commandement", Branch.ORIGINEL, "Commandement", cfg::commandementCost, cfg::commandementCooldownTicks,
                (player, data) -> {
                    Optional<LivingEntity> target = TargetingUtil.getLookedAtEntity(player, 16);
                    if (target.isPresent()) {
                        target.get().addEffect(new MobEffectInstance(MobEffects.GLOWING, cfg.commandementDurationTicks(), 0));
                        player.sendSystemMessage(OriginelText.prefixed(target.get().getName().getString() + " est marque par l'Originel."));
                    } else {
                        player.sendSystemMessage(OriginelText.prefixed("Aucune cible pour le Commandement."));
                    }
                }, false));

        // --- Ultime ---
        register(new ActiveSkill("colere_originel", Branch.ULTIME, "Colere de l'Originel", cfg::colereCost, cfg::colereCooldownTicks,
                (player, data) -> {
                    long expiry = player.level().getGameTime() + cfg.colereDurationTicks();
                    applyModifier(player, Attributes.MOVEMENT_SPEED, COLERE_SPEED_MODIFIER,
                            cfg.veloviteSpeedBonus() * (cfg.colereMultiplier() - 1), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    applyModifier(player, Attributes.ATTACK_DAMAGE, COLERE_DAMAGE_MODIFIER,
                            cfg.forceBestialeDamageBonus() * (cfg.colereMultiplier() - 1), AttributeModifier.Operation.ADD_VALUE);
                    data.setCooldownExpiry("colere_originel_expiry", expiry);
                    player.sendSystemMessage(OriginelText.prefixed("La Colere de l'Originel se dechaine !"));
                }, true));
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
