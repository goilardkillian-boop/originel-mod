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

    public boolean isWhitelisted(UUID playerUuid) {
        return whitelistedUuid != null && whitelistedUuid.equals(playerUuid);
    }
}
