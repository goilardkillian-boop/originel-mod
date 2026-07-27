package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public final class LuneRougeConfig extends TomlConfigFile {

    static final LuneRougeConfig INSTANCE = new LuneRougeConfig();

    private boolean autoEnabled;
    private int autoFrequencyNights;
    private String messageStart;
    private String messageEnd;
    private boolean particleEnabled;
    private String particleId;
    private int particleRadius;
    private int particleCount;
    private boolean creatureBonusEnabled;
    private double creatureBonusDamageMultiplier;
    private double creatureBonusSpeedMultiplier;

    private LuneRougeConfig() {
        super("lunerouge.toml");
    }

    public static LuneRougeConfig get() {
        return INSTANCE;
    }

    @Override
    protected void reload(CommentedFileConfig config) {
        autoEnabled = value(config, "auto.enabled", false,
                "Si true, une Lune Rouge se declenche automatiquement selon la frequence ci-dessous, en plus de /originel lunerouge start.");
        autoFrequencyNights = value(config, "auto.frequency_nights", 7,
                "Frequence (en nombre de nuits) des Lunes Rouges automatiques, si auto.enabled est actif.");
        messageStart = value(config, "messages.start",
                "La lune se teinte de sang. Le Voile gemit... Une nuit ecarlate s'abat sur Lycania.",
                "Message diffuse a tous les joueurs au declenchement de la Lune Rouge.");
        messageEnd = value(config, "messages.end",
                "L'aube se leve. La lune retrouve sa clarte, et le Voile se tait de nouveau.",
                "Message diffuse a tous les joueurs a la fin de la Lune Rouge (a l'aube).");
        particleEnabled = value(config, "ambiance.particle_enabled", true,
                "Particules d'ambiance (cendres) autour des joueurs pendant la Lune Rouge.");
        particleId = value(config, "ambiance.particle_id", "minecraft:ash",
                "Identifiant de la particule d'ambiance.");
        particleRadius = value(config, "ambiance.particle_radius", 8,
                "Rayon (blocs) autour de chaque joueur dans lequel les particules d'ambiance apparaissent.");
        particleCount = value(config, "ambiance.particle_count", 3,
                "Nombre de particules generees par joueur a chaque cycle d'ambiance.");
        creatureBonusEnabled = value(config, "creatures.bonus_enabled", true,
                "Si true, les creatures des factions Vampire/Loup-garou presentes recoivent un bonus pendant la Lune Rouge.");
        creatureBonusDamageMultiplier = value(config, "creatures.bonus_damage_multiplier", 1.5,
                "Multiplicateur de degats applique aux creatures des factions creatures pendant la Lune Rouge.");
        creatureBonusSpeedMultiplier = value(config, "creatures.bonus_speed_multiplier", 1.2,
                "Multiplicateur de vitesse applique aux creatures des factions creatures pendant la Lune Rouge.");
    }

    public boolean autoEnabled() {
        return autoEnabled;
    }

    public int autoFrequencyNights() {
        return autoFrequencyNights;
    }

    public String messageStart() {
        return messageStart;
    }

    public String messageEnd() {
        return messageEnd;
    }

    public boolean particleEnabled() {
        return particleEnabled;
    }

    public String particleId() {
        return particleId;
    }

    public int particleRadius() {
        return particleRadius;
    }

    public int particleCount() {
        return particleCount;
    }

    public boolean creatureBonusEnabled() {
        return creatureBonusEnabled;
    }

    public double creatureBonusDamageMultiplier() {
        return creatureBonusDamageMultiplier;
    }

    public double creatureBonusSpeedMultiplier() {
        return creatureBonusSpeedMultiplier;
    }
}
