package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public final class GeneralConfig extends TomlConfigFile {

    static final GeneralConfig INSTANCE = new GeneralConfig();

    private String chatPrefix;
    private boolean debugLogging;
    private int staffPermissionLevel;

    private GeneralConfig() {
        super("general.toml");
    }

    public static GeneralConfig get() {
        return INSTANCE;
    }

    @Override
    protected void reload(CommentedFileConfig config) {
        chatPrefix = value(config, "chat_prefix", "Originel",
                "Prefixe affiche devant les messages envoyes par le mod.");
        debugLogging = value(config, "debug_logging", false,
                "Journalise des informations detaillees dans les logs serveur.");
        staffPermissionLevel = value(config, "staff_permission_level", 2,
                "Niveau de permission (0-4) requis pour les commandes /originel reservees au staff.");
    }

    public String chatPrefix() {
        return chatPrefix;
    }

    public boolean debugLogging() {
        return debugLogging;
    }

    public int staffPermissionLevel() {
        return staffPermissionLevel;
    }
}
