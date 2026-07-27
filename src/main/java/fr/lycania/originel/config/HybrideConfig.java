package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.util.UUID;

public final class HybrideConfig extends TomlConfigFile {

    static final HybrideConfig INSTANCE = new HybrideConfig();

    private UUID whitelistedUuid;
    private String whitelistedName;
    private String denyMessage;

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

    public boolean isWhitelisted(UUID playerUuid) {
        return whitelistedUuid != null && whitelistedUuid.equals(playerUuid);
    }
}
