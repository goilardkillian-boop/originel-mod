package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.util.UUID;

public final class HybrideConfig extends TomlConfigFile {

    static final HybrideConfig INSTANCE = new HybrideConfig();

    private UUID whitelistedUuid;
    private String whitelistedName;
    private String denyMessage;
    private int maxLevel;
    private int factionColor;
    private String assignMessage;
    private String removeMessage;
    private double bonusHealthPerLevel;
    private double bonusDamagePerLevel;
    private double bonusSpeedPerLevel;
    private boolean xpPerKillEnabled;
    private int killsPerLevel;
    private boolean trailEnabled;
    private int trailIntervalTicks;
    private double trailMinSpeed;
    private String trailParticle;

    private HybrideConfig() {
        super("hybride.toml");
    }

    public static HybrideConfig get() {
        return INSTANCE;
    }

    @Override
    protected void reload(CommentedFileConfig config) {
        String uuidString = value(config, "whitelist.uuid", "",
                "UUID (avec tirets) de l'unique joueur autorise a devenir l'Hybride. Laisser vide pour desactiver.");
        whitelistedUuid = parseUuid(uuidString);
        whitelistedName = value(config, "whitelist.name", "",
                "Pseudo indicatif du joueur whitelist (informatif uniquement, l'UUID fait foi).");
        denyMessage = value(config, "messages.deny", "Le Voile ne te reconnait pas. Tu n'es pas celui qui doit porter cette malediction.",
                "Message envoye a un joueur non-whitelist lorsqu'on tente de lui attribuer la faction Hybride.");
        assignMessage = value(config, "messages.assign", "Le Voile se souvient de toi, Corvin. Tu es desormais l'Originel.",
                "Message envoye au joueur whitelist quand la faction Hybride lui est attribuee.");
        removeMessage = value(config, "messages.remove", "La malediction de l'Originel te quitte, pour l'instant.",
                "Message envoye au joueur quand la faction Hybride lui est retiree.");
        maxLevel = value(config, "progression.max_level", 14,
                "Niveau maximum atteignable par l'Hybride.");
        bonusHealthPerLevel = value(config, "progression.bonus_health_per_level", 2.0,
                "Points de vie bonus (max) accordes par niveau d'Hybride.");
        bonusDamagePerLevel = value(config, "progression.bonus_damage_per_level", 0.5,
                "Degats de mains nues/attaque bonus accordes par niveau d'Hybride.");
        bonusSpeedPerLevel = value(config, "progression.bonus_speed_per_level", 0.0,
                "Vitesse de deplacement bonus (fraction de la vitesse de base) accordee par niveau d'Hybride.");
        xpPerKillEnabled = value(config, "progression.xp_per_kill_enabled", false,
                "Si true, l'Hybride gagne aussi des niveaux en eliminant des creatures (en plus de /originel level set).");
        killsPerLevel = value(config, "progression.kills_per_level", 20,
                "Nombre d'eliminations necessaires pour gagner un niveau, si xp_per_kill_enabled est actif.");
        trailEnabled = value(config, "visuals.trail_enabled", true,
                "Si true, l'Hybride laisse une trainee de brume derriere lui en se deplacant.");
        trailIntervalTicks = value(config, "visuals.trail_interval_ticks", 4,
                "Intervalle (ticks) entre deux emissions de particules de la trainee de brume.");
        trailMinSpeed = value(config, "visuals.trail_min_speed", 0.02,
                "Vitesse horizontale minimale (blocs/tick) pour que la trainee de brume s'emette.");
        trailParticle = value(config, "visuals.trail_particle", "minecraft:cloud",
                "Particule utilisee pour la trainee de brume de l'Hybride en mouvement.");
        String colorHex = value(config, "faction.color", "8B0000",
                "Couleur (hexadecimal RRGGBB, sans #) associee a la faction Hybride dans les commandes/UI de Vampirism.");
        factionColor = parseColor(colorHex);
    }

    private static int parseColor(String hex) {
        try {
            return Integer.parseInt(hex.replace("#", "").trim(), 16);
        } catch (NumberFormatException e) {
            return 0x8B0000;
        }
    }

    private static UUID parseUuid(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public UUID whitelistedUuid() {
        return whitelistedUuid;
    }

    public String whitelistedName() {
        return whitelistedName;
    }

    public String denyMessage() {
        return denyMessage;
    }

    public String assignMessage() {
        return assignMessage;
    }

    public String removeMessage() {
        return removeMessage;
    }

    public int maxLevel() {
        return maxLevel;
    }

    public int factionColor() {
        return factionColor;
    }

    public double bonusHealthPerLevel() {
        return bonusHealthPerLevel;
    }

    public double bonusDamagePerLevel() {
        return bonusDamagePerLevel;
    }

    public double bonusSpeedPerLevel() {
        return bonusSpeedPerLevel;
    }

    public boolean xpPerKillEnabled() {
        return xpPerKillEnabled;
    }

    public int killsPerLevel() {
        return killsPerLevel;
    }

    public boolean trailEnabled() {
        return trailEnabled;
    }

    public int trailIntervalTicks() {
        return trailIntervalTicks;
    }

    public double trailMinSpeed() {
        return trailMinSpeed;
    }

    public String trailParticle() {
        return trailParticle;
    }

    public boolean isWhitelisted(UUID playerUuid) {
        return whitelistedUuid != null && whitelistedUuid.equals(playerUuid);
    }
}
