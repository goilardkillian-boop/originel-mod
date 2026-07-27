package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

/**
 * Anneau de Cendre (etape 9) - protection solaire pour un vampire, au prix
 * d'une soif accrue, de pouvoirs affaiblis et d'un nombre de charges limite.
 */
public final class CendreConfig extends TomlConfigFile {

    static final CendreConfig INSTANCE = new CendreConfig();

    private double sunDamageReductionPercent;
    private double thirstDrainPercent;
    private int thirstDrainIntervalTicks;
    private double powerWeakenPercent;
    private int maxCharges;
    private int chargeLossPerExposure;
    private double chargeLowThresholdPercent;
    private String messageConverted;
    private String messageLowCharges;
    private String messageDestroyed;

    private CendreConfig() {
        super("cendre.toml");
    }

    public static CendreConfig get() {
        return INSTANCE;
    }

    @Override
    protected void reload(CommentedFileConfig config) {
        sunDamageReductionPercent = value(config, "effect.sun_damage_reduction_percent", 1.0,
                "Fraction des degats solaires (vampirism:sun_damage) annulee pour un vampire portant l'Anneau de Cendre (1.0 = immunite totale).");
        thirstDrainPercent = value(config, "malus.thirst_drain_percent", 0.02,
                "Fraction du sang retiree a chaque intervalle tant que l'anneau est porte par un vampire (soif acceleree).");
        thirstDrainIntervalTicks = value(config, "malus.thirst_drain_interval_ticks", 100,
                "Intervalle en ticks entre deux pertes de sang dues a l'anneau.");
        powerWeakenPercent = value(config, "malus.power_weaken_percent", 0.25,
                "Fraction en moins sur les degats d'attaque tant que l'anneau est porte par un vampire.");
        maxCharges = value(config, "charges.max", 200,
                "Nombre de charges de l'Anneau de Cendre avant destruction.");
        chargeLossPerExposure = value(config, "charges.loss_per_exposure", 1,
                "Charges perdues chaque fois que l'anneau bloque des degats solaires.");
        chargeLowThresholdPercent = value(config, "charges.low_threshold_percent", 0.1,
                "Seuil (fraction des charges max) sous lequel le message d'avertissement est envoye.");
        messageConverted = value(config, "messages.converted",
                "L'anneau se couvre de cendres. Il vous protegera du soleil, au prix de votre soif.",
                "Message envoye au porteur quand un Anneau de Cendre est converti ou donne.");
        messageLowCharges = value(config, "messages.low_charges",
                "L'Anneau de Cendre s'effrite. Il ne tiendra plus longtemps.",
                "Message envoye quand les charges de l'anneau passent sous le seuil bas.");
        messageDestroyed = value(config, "messages.destroyed",
                "L'Anneau de Cendre se desagrege en poussiere.",
                "Message envoye quand l'anneau est detruit (charges epuisees).");
    }

    public double sunDamageReductionPercent() {
        return sunDamageReductionPercent;
    }

    public double thirstDrainPercent() {
        return thirstDrainPercent;
    }

    public int thirstDrainIntervalTicks() {
        return thirstDrainIntervalTicks;
    }

    public double powerWeakenPercent() {
        return powerWeakenPercent;
    }

    public int maxCharges() {
        return maxCharges;
    }

    public int chargeLossPerExposure() {
        return chargeLossPerExposure;
    }

    public double chargeLowThresholdPercent() {
        return chargeLowThresholdPercent;
    }

    public String messageConverted() {
        return messageConverted;
    }

    public String messageLowCharges() {
        return messageLowCharges;
    }

    public String messageDestroyed() {
        return messageDestroyed;
    }
}
