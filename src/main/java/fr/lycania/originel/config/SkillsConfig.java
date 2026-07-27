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
    private double morsureFoodRestore;
    private int morsureCooldownTicks;
    private int brumeCost;
    private double brumeDistance;
    private int brumeCooldownTicks;
    private int odoratSangCost;
    private int odoratSangRadius;
    private double odoratSangHealthThreshold;

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
    private int hurlementCost;
    private int hurlementCooldownTicks;
    private int hurlementRadius;
    private int hurlementFearDurationTicks;
    private int hurlementFearAmplifier;
    private int hurlementSelfBuffDurationTicks;
    private int hurlementSelfBuffAmplifier;
    private String hurlementSound;

    // Originel
    private int auraCost;
    private int auraRadius;
    private int auraIntervalTicks;
    private String auraMessage;
    private String auraSound;
    private int auraMalaiseDurationTicks;
    private int auraMalaiseAmplifier;
    private double auraFleeWalkSpeedModifier;
    private double auraFleeSprintSpeedModifier;
    private int regenerationImpieCost;
    private int regenerationImpieAmplifier;
    private int metamorphoseCost;
    private int metamorphoseCooldownTicks;
    private double metamorphoseStepHeightBonus;
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
        pointsPerLevel = value(config, "general.points_per_level", 2,
                "Nombre de points de competence accordes par niveau d'Hybride gagne. "
                        + "Avec 2 par niveau et 14 niveaux (hybride.toml#progression.max_level), "
                        + "un Hybride au niveau maximum peut debloquer les 15 competences (cout total 17).");

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
        morsureFoodRestore = value(config, "sang.morsure_vampirique.food_restore_percent", 0.5,
                "Fraction des degats infliges rendue en nourriture/saturation par la Morsure vampirique.");
        morsureCooldownTicks = value(config, "sang.morsure_vampirique.cooldown_ticks", 100,
                "Delai de recharge (ticks) de la Morsure vampirique.");

        brumeCost = value(config, "sang.brume.cost", 1, "Cout en points de la Brume.");
        brumeDistance = value(config, "sang.brume.distance", 8.0, "Distance (blocs) parcourue par la Brume.");
        brumeCooldownTicks = value(config, "sang.brume.cooldown_ticks", 160, "Delai de recharge (ticks) de la Brume.");

        odoratSangCost = value(config, "sang.odorat_sang.cost", 1, "Cout en points de l'Odorat du sang.");
        odoratSangRadius = value(config, "sang.odorat_sang.radius", 20,
                "Rayon (blocs) dans lequel l'Odorat du sang detecte les entites blessees.");
        odoratSangHealthThreshold = value(config, "sang.odorat_sang.health_threshold_percent", 0.3,
                "Fraction de vie maximale en dessous de laquelle une entite est mise en surbrillance par l'Odorat du sang.");

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

        hurlementCost = value(config, "lune.hurlement_meute.cost", 1, "Cout en points du Hurlement de meute.");
        hurlementCooldownTicks = value(config, "lune.hurlement_meute.cooldown_ticks", 400,
                "Delai de recharge (ticks) du Hurlement de meute.");
        hurlementRadius = value(config, "lune.hurlement_meute.radius", 10,
                "Rayon (blocs) dans lequel le Hurlement de meute effraie les monstres hostiles.");
        hurlementFearDurationTicks = value(config, "lune.hurlement_meute.fear_duration_ticks", 140,
                "Duree (ticks) de la Faiblesse et de la Lenteur infligees aux monstres effrayes.");
        hurlementFearAmplifier = value(config, "lune.hurlement_meute.fear_amplifier", 1,
                "Amplificateur de la Faiblesse/Lenteur infligee (0 = niveau I).");
        hurlementSelfBuffDurationTicks = value(config, "lune.hurlement_meute.self_buff_duration_ticks", 140,
                "Duree (ticks) du regain de vitesse et de force que le Hurlement de meute t'accorde.");
        hurlementSelfBuffAmplifier = value(config, "lune.hurlement_meute.self_buff_amplifier", 1,
                "Amplificateur du regain de vitesse/force (0 = niveau I).");
        hurlementSound = value(config, "lune.hurlement_meute.sound", "werewolves:entity.werewolf.howl",
                "Son joue au declenchement du Hurlement de meute.");

        auraCost = value(config, "originel.aura_abomination.cost", 1, "Cout en points de l'Aura d'Abomination.");
        auraRadius = value(config, "originel.aura_abomination.radius", 16,
                "Rayon (blocs) dans lequel l'Aura d'Abomination affecte les joueurs des factions creatures (vampire, loup-garou).");
        auraIntervalTicks = value(config, "originel.aura_abomination.interval_ticks", 20,
                "Intervalle (ticks) de detection d'entree/sortie de portee. Le message/son/nausee n'est "
                        + "envoye qu'une fois par joueur cible qui entre dans le rayon (pas repete tant qu'il reste a portee).");
        auraMessage = value(config, "originel.aura_abomination.message",
                "Un malaise glacial vous parcourt. Quelque chose d'abominable rode pres de vous...",
                "Message discret envoye aux joueurs des factions creatures a portee de l'Aura d'Abomination.");
        auraSound = value(config, "originel.aura_abomination.sound", "minecraft:entity.warden.heartbeat",
                "Son joue (uniquement audible par les cibles) a chaque pulsation de l'Aura d'Abomination.");
        auraMalaiseDurationTicks = value(config, "originel.aura_abomination.malaise_duration_ticks", 60,
                "Duree (ticks) du malaise (Nausee) inflige aux cibles de l'Aura d'Abomination.");
        auraMalaiseAmplifier = value(config, "originel.aura_abomination.malaise_amplifier", 0,
                "Amplificateur de la Nausee infligee par l'Aura d'Abomination (0 = niveau I).");
        auraFleeWalkSpeedModifier = value(config, "originel.aura_abomination.flee_walk_speed_modifier", 1.3,
                "Multiplicateur de vitesse de deplacement des mobs vampires/loups-garous qui fuient l'Aura d'Abomination.");
        auraFleeSprintSpeedModifier = value(config, "originel.aura_abomination.flee_sprint_speed_modifier", 1.6,
                "Multiplicateur de vitesse de course des mobs vampires/loups-garous qui fuient l'Aura d'Abomination a courte distance.");

        regenerationImpieCost = value(config, "originel.regeneration_impie.cost", 1, "Cout en points de la Regeneration impie.");
        regenerationImpieAmplifier = value(config, "originel.regeneration_impie.amplifier", 0,
                "Amplificateur de l'effet Regeneration applique en continu (0 = niveau I).");

        metamorphoseCost = value(config, "originel.metamorphose.cost", 1, "Cout en points de la Metamorphose.");
        metamorphoseCooldownTicks = value(config, "originel.metamorphose.cooldown_ticks", 20,
                "Delai de recharge (ticks) entre deux Metamorphoses.");
        metamorphoseStepHeightBonus = value(config, "originel.metamorphose.step_height_bonus", 1.0,
                "Bonus de hauteur d'enjambee (blocs) masque retire - permet de monter les blocs pleins en marchant, "
                        + "sans sauter (base vanilla 0.6, donc 1.6 par defaut).");

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

    public double morsureFoodRestore() {
        return morsureFoodRestore;
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

    public int odoratSangCost() {
        return odoratSangCost;
    }

    public int odoratSangRadius() {
        return odoratSangRadius;
    }

    public double odoratSangHealthThreshold() {
        return odoratSangHealthThreshold;
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

    public int hurlementCost() {
        return hurlementCost;
    }

    public int hurlementCooldownTicks() {
        return hurlementCooldownTicks;
    }

    public int hurlementRadius() {
        return hurlementRadius;
    }

    public int hurlementFearDurationTicks() {
        return hurlementFearDurationTicks;
    }

    public int hurlementFearAmplifier() {
        return hurlementFearAmplifier;
    }

    public int hurlementSelfBuffDurationTicks() {
        return hurlementSelfBuffDurationTicks;
    }

    public int hurlementSelfBuffAmplifier() {
        return hurlementSelfBuffAmplifier;
    }

    public String hurlementSound() {
        return hurlementSound;
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

    public double auraFleeWalkSpeedModifier() {
        return auraFleeWalkSpeedModifier;
    }

    public double auraFleeSprintSpeedModifier() {
        return auraFleeSprintSpeedModifier;
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

    public double metamorphoseStepHeightBonus() {
        return metamorphoseStepHeightBonus;
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
