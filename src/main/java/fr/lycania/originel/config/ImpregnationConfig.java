package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public final class ImpregnationConfig extends TomlConfigFile {

    static final ImpregnationConfig INSTANCE = new ImpregnationConfig();

    private int bloodCost;
    private String sound;
    private String particle;

    private ImpregnationConfig() {
        super("impregnation.toml");
    }

    public static ImpregnationConfig get() {
        return INSTANCE;
    }

    @Override
    protected void reload(CommentedFileConfig config) {
        bloodCost = value(config, "ritual.blood_cost", 3,
                "Nombre de Sang de Gardien consommes (en main secondaire) pour imbiber la Dague de l'Originel.");
        sound = value(config, "ritual.sound", "minecraft:entity.evoker.cast_spell",
                "Son joue quand la Dague de l'Originel est imbibee de sang.");
        particle = value(config, "ritual.particle", "minecraft:crit",
                "Particule affichee quand la Dague de l'Originel est imbibee de sang.");
    }

    public int bloodCost() {
        return bloodCost;
    }

    public String sound() {
        return sound;
    }

    public String particle() {
        return particle;
    }
}
