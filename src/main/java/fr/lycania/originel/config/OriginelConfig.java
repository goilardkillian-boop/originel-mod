package fr.lycania.originel.config;

import java.util.List;

/**
 * Central registry of every config/originel/*.toml file. Loaded once at
 * startup and re-loaded in full by /originel reload.
 */
public final class OriginelConfig {

    private static final List<TomlConfigFile> FILES = List.of(
            GeneralConfig.get(),
            HybrideConfig.get(),
            SkillsConfig.get()
    );

    private OriginelConfig() {
    }

    public static void loadAll() {
        FILES.forEach(TomlConfigFile::load);
    }

    public static void reloadAll() {
        loadAll();
    }
}
