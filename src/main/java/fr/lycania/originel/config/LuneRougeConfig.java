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
    private boolean skyTintEnabled;
    private double fogRed;
    private double fogGreen;
    private double fogBlue;
    private double fogStrength;
    private double moonRed;
    private double moonGreen;
    private double moonBlue;

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

        skyTintEnabled = value(config, "sky.tint_enabled", true,
                "Client uniquement. Si true, le ciel (brouillard) et la lune sont teintes de rouge sang pendant la Lune Rouge.");
        fogRed = value(config, "sky.fog_red", 0.45, "Composante rouge (0-1) du brouillard pendant la Lune Rouge.");
        fogGreen = value(config, "sky.fog_green", 0.03, "Composante verte (0-1) du brouillard pendant la Lune Rouge.");
        fogBlue = value(config, "sky.fog_blue", 0.03, "Composante bleue (0-1) du brouillard pendant la Lune Rouge.");
        fogStrength = value(config, "sky.fog_strength", 0.85,
                "Force du melange (0 = brouillard normal, 1 = entierement remplace par la couleur ci-dessus).");
        moonRed = value(config, "sky.moon_red", 0.75, "Composante rouge (0-1) de la teinte appliquee a la lune.");
        moonGreen = value(config, "sky.moon_green", 0.05, "Composante verte (0-1) de la teinte appliquee a la lune.");
        moonBlue = value(config, "sky.moon_blue", 0.03, "Composante bleue (0-1) de la teinte appliquee a la lune.");
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

    public boolean skyTintEnabled() {
        return skyTintEnabled;
    }

    public double fogRed() {
        return fogRed;
    }

    public double fogGreen() {
        return fogGreen;
    }

    public double fogBlue() {
        return fogBlue;
    }

    public double fogStrength() {
        return fogStrength;
    }

    public double moonRed() {
        return moonRed;
    }

    public double moonGreen() {
        return moonGreen;
    }

    public double moonBlue() {
        return moonBlue;
    }
}
