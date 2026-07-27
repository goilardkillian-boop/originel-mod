package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public final class SkillsConfig extends TomlConfigFile {

    static final SkillsConfig INSTANCE = new SkillsConfig();

    private int pointsPerLevel;

    // Sang
    private int velociteCost;
    private double veloviteSpeedBonus;
    private int regardCost;
    private int regardRange;
    private int regardSlownessAmplifier;
    private int regardDurationTicks;
    private int regardCooldownTicks;
    private int morsureCost;
    private double morsureLifestealPercent;
    private int morsureCooldownTicks;
    private int brumeCost;
    private double brumeDistance;
    private int brumeCooldownTicks;

    // Lune
    private int forceBestialeCost;
    private double forceBestialeDamageBonus;
    private int sensAiguisesCost;
    private int sensAiguisesHighlightRadius;
    private int griffesCost;
    private double griffesLeapStrength;
    private int griffesBleedDurationTicks;
    private int griffesCooldownTicks;
    private int peauDeBeteCost;
    private double peauDeBeteDamageReductionPercent;

    // Originel
    private int auraCost;
    private int auraRadius;
    private int auraIntervalTicks;
    private String auraMessage;
    private String auraSound;
    private int auraMalaiseDurationTicks;
    private int auraMalaiseAmplifier;
    private int regenerationImpieCost;
    private int regenerationImpieAmplifier;
    private int metamorphoseCost;
    private int metamorphoseCooldownTicks;
    private int commandementCost;
    private int commandementDurationTicks;
    private int commandementCooldownTicks;

    // Ultime
    private int colereCost;
    private int colereDurationTicks;
    private int colereCooldownTicks;
    private double colereMultiplier;

    private SkillsConfig() {
        super("skills.toml");
    }

    public static SkillsConfig get() {
        return INSTANCE;
    }

    @Override
    protected void reload(CommentedFileConfig config) {
        pointsPerLevel = value(config, "general.points_per_level", 1,
                "Nombre de points de competence accordes par niveau d'Hybride gagne.");

        velociteCost = value(config, "sang.velocite.cost", 1, "Cout en points de la competence Velocite.");
        veloviteSpeedBonus = value(config, "sang.velocite.speed_bonus", 0.15,
                "Bonus de vitesse de deplacement (fraction) tant que Velocite est debloquee.");

        regardCost = value(config, "sang.regard_hypnotique.cost", 1, "Cout en points du Regard hypnotique.");
        regardRange = value(config, "sang.regard_hypnotique.range", 12, "Portee (blocs) du Regard hypnotique.");
        regardSlownessAmplifier = value(config, "sang.regard_hypnotique.slowness_amplifier", 2,
                "Amplificateur de lenteur applique a la cible (0 = Lenteur I).");
        regardDurationTicks = value(config, "sang.regard_hypnotique.duration_ticks", 100,
                "Duree (ticks) de la lenteur infligee par le Regard hypnotique.");
        regardCooldownTicks = value(config, "sang.regard_hypnotique.cooldown_ticks", 200,
                "Delai de recharge (ticks) du Regard hypnotique.");

        morsureCost = value(config, "sang.morsure_vampirique.cost", 1, "Cout en points de la Morsure vampirique.");
        morsureLifestealPercent = value(config, "sang.morsure_vampirique.lifesteal_percent", 0.5,
                "Fraction des degats infliges rendue en vie par la Morsure vampirique.");
        morsureCooldownTicks = value(config, "sang.morsure_vampirique.cooldown_ticks", 100,
                "Delai de recharge (ticks) de la Morsure vampirique.");

        brumeCost = value(config, "sang.brume.cost", 1, "Cout en points de la Brume.");
        brumeDistance = value(config, "sang.brume.distance", 8.0, "Distance (blocs) parcourue par la Brume.");
        brumeCooldownTicks = value(config, "sang.brume.cooldown_ticks", 160, "Delai de recharge (ticks) de la Brume.");

        forceBestialeCost = value(config, "lune.force_bestiale.cost", 1, "Cout en points de la Force bestiale.");
        forceBestialeDamageBonus = value(config, "lune.force_bestiale.damage_bonus", 2.0,
                "Bonus de degats en melee tant que Force bestiale est debloquee.");

        sensAiguisesCost = value(config, "lune.sens_aiguises.cost", 1, "Cout en points des Sens aiguises.");
        sensAiguisesHighlightRadius = value(config, "lune.sens_aiguises.highlight_radius", 24,
                "Rayon (blocs) dans lequel les entites hostiles sont mises en surbrillance.");

        griffesCost = value(config, "lune.griffes.cost", 1, "Cout en points des Griffes.");
        griffesLeapStrength = value(config, "lune.griffes.leap_strength", 1.4, "Force du bond des Griffes.");
        griffesBleedDurationTicks = value(config, "lune.griffes.bleed_duration_ticks", 100,
                "Duree (ticks) du saignement (Poison) inflige par les Griffes.");
        griffesCooldownTicks = value(config, "lune.griffes.cooldown_ticks", 140, "Delai de recharge (ticks) des Griffes.");

        peauDeBeteCost = value(config, "lune.peau_de_bete.cost", 1, "Cout en points de la Peau de bete.");
        peauDeBeteDamageReductionPercent = value(config, "lune.peau_de_bete.damage_reduction_percent", 0.2,
                "Fraction des degats subis absorbee tant que la Peau de bete est debloquee.");

        auraCost = value(config, "originel.aura_abomination.cost", 1, "Cout en points de l'Aura d'Abomination.");
        auraRadius = value(config, "originel.aura_abomination.radius", 16,
                "Rayon (blocs) dans lequel l'Aura d'Abomination affecte les joueurs des factions creatures (vampire, loup-garou).");
        auraIntervalTicks = value(config, "originel.aura_abomination.interval_ticks", 100,
                "Intervalle (ticks) entre deux pulsations de l'Aura d'Abomination.");
        auraMessage = value(config, "originel.aura_abomination.message",
                "Un malaise glacial vous parcourt. Quelque chose d'abominable rode pres de vous...",
                "Message discret envoye aux joueurs des factions creatures a portee de l'Aura d'Abomination.");
        auraSound = value(config, "originel.aura_abomination.sound", "minecraft:entity.warden.heartbeat",
                "Son joue (uniquement audible par les cibles) a chaque pulsation de l'Aura d'Abomination.");
        auraMalaiseDurationTicks = value(config, "originel.aura_abomination.malaise_duration_ticks", 60,
                "Duree (ticks) du malaise (Nausee) inflige aux cibles de l'Aura d'Abomination.");
        auraMalaiseAmplifier = value(config, "originel.aura_abomination.malaise_amplifier", 0,
                "Amplificateur de la Nausee infligee par l'Aura d'Abomination (0 = niveau I).");

        regenerationImpieCost = value(config, "originel.regeneration_impie.cost", 1, "Cout en points de la Regeneration impie.");
        regenerationImpieAmplifier = value(config, "originel.regeneration_impie.amplifier", 0,
                "Amplificateur de l'effet Regeneration applique en continu (0 = niveau I).");

        metamorphoseCost = value(config, "originel.metamorphose.cost", 1, "Cout en points de la Metamorphose.");
        metamorphoseCooldownTicks = value(config, "originel.metamorphose.cooldown_ticks", 20,
                "Delai de recharge (ticks) entre deux Metamorphoses.");

        commandementCost = value(config, "originel.commandement.cost", 1, "Cout en points du Commandement.");
        commandementDurationTicks = value(config, "originel.commandement.duration_ticks", 1200,
                "Duree (ticks) du marquage impose par le Commandement.");
        commandementCooldownTicks = value(config, "originel.commandement.cooldown_ticks", 2400,
                "Delai de recharge (ticks) du Commandement.");

        colereCost = value(config, "ultime.colere_originel.cost", 3,
                "Cout en points de la Colere de l'Originel (necessite le niveau maximum).");
        colereDurationTicks = value(config, "ultime.colere_originel.duration_ticks", 400,
                "Duree (ticks) du cumul temporaire des trois branches.");
        colereCooldownTicks = value(config, "ultime.colere_originel.cooldown_ticks", 12000,
                "Delai de recharge (ticks) de la Colere de l'Originel.");
        colereMultiplier = value(config, "ultime.colere_originel.multiplier", 2.0,
                "Multiplicateur applique aux bonus des competences passives pendant la Colere de l'Originel.");
    }

    public int pointsPerLevel() {
        return pointsPerLevel;
    }

    public int velociteCost() {
        return velociteCost;
    }

    public double veloviteSpeedBonus() {
        return veloviteSpeedBonus;
    }

    public int regardCost() {
        return regardCost;
    }

    public int regardRange() {
        return regardRange;
    }

    public int regardSlownessAmplifier() {
        return regardSlownessAmplifier;
    }

    public int regardDurationTicks() {
        return regardDurationTicks;
    }

    public int regardCooldownTicks() {
        return regardCooldownTicks;
    }

    public int morsureCost() {
        return morsureCost;
    }

    public double morsureLifestealPercent() {
        return morsureLifestealPercent;
    }

    public int morsureCooldownTicks() {
        return morsureCooldownTicks;
    }

    public int brumeCost() {
        return brumeCost;
    }

    public double brumeDistance() {
        return brumeDistance;
    }

    public int brumeCooldownTicks() {
        return brumeCooldownTicks;
    }

    public int forceBestialeCost() {
        return forceBestialeCost;
    }

    public double forceBestialeDamageBonus() {
        return forceBestialeDamageBonus;
    }

    public int sensAiguisesCost() {
        return sensAiguisesCost;
    }

    public int sensAiguisesHighlightRadius() {
        return sensAiguisesHighlightRadius;
    }

    public int griffesCost() {
        return griffesCost;
    }

    public double griffesLeapStrength() {
        return griffesLeapStrength;
    }

    public int griffesBleedDurationTicks() {
        return griffesBleedDurationTicks;
    }

    public int griffesCooldownTicks() {
        return griffesCooldownTicks;
    }

    public int peauDeBeteCost() {
        return peauDeBeteCost;
    }

    public double peauDeBeteDamageReductionPercent() {
        return peauDeBeteDamageReductionPercent;
    }

    public int auraCost() {
        return auraCost;
    }

    public int auraRadius() {
        return auraRadius;
    }

    public int auraIntervalTicks() {
        return auraIntervalTicks;
    }

    public String auraMessage() {
        return auraMessage;
    }

    public String auraSound() {
        return auraSound;
    }

    public int auraMalaiseDurationTicks() {
        return auraMalaiseDurationTicks;
    }

    public int auraMalaiseAmplifier() {
        return auraMalaiseAmplifier;
    }

    public int regenerationImpieCost() {
        return regenerationImpieCost;
    }

    public int regenerationImpieAmplifier() {
        return regenerationImpieAmplifier;
    }

    public int metamorphoseCost() {
        return metamorphoseCost;
    }

    public int metamorphoseCooldownTicks() {
        return metamorphoseCooldownTicks;
    }

    public int commandementCost() {
        return commandementCost;
    }

    public int commandementDurationTicks() {
        return commandementDurationTicks;
    }

    public int commandementCooldownTicks() {
        return commandementCooldownTicks;
    }

    public int colereCost() {
        return colereCost;
    }

    public int colereDurationTicks() {
        return colereDurationTicks;
    }

    public int colereCooldownTicks() {
        return colereCooldownTicks;
    }

    public double colereMultiplier() {
        return colereMultiplier;
    }
}
