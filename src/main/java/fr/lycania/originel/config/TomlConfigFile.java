package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import fr.lycania.originel.OriginelMod;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * One TOML file under config/originel/. Missing keys are written back with
 * their default value and a comment on load, so the file on disk always
 * documents every option Originel reads.
 */
public abstract class TomlConfigFile {

    private final Path path;
    private CommentedFileConfig fileConfig;

    protected TomlConfigFile(String fileName) {
        this.path = FMLPaths.CONFIGDIR.get().resolve("originel").resolve(fileName);
    }

    public final void load() {
        if (fileConfig == null) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new UncheckedIOException("Impossible de creer le dossier de config " + path.getParent(), e);
            }
            fileConfig = CommentedFileConfig.builder(path, TomlFormat.instance())
                    .preserveInsertionOrder()
                    .sync()
                    .build();
        }
        fileConfig.load();
        reload(fileConfig);
        fileConfig.save();
        OriginelMod.LOGGER.info("Config chargee : {}", path.getFileName());
    }

    protected abstract void reload(CommentedFileConfig config);

    protected static <T> T value(CommentedFileConfig config, String key, T defaultValue, String comment) {
        if (!config.contains(key)) {
            config.set(key, defaultValue);
        }
        config.setComment(key, " " + comment);
        return config.getOrElse(key, defaultValue);
    }
}
