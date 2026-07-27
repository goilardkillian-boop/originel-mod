package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public final class ScellementRitualConfig extends TomlConfigFile {

    static final ScellementRitualConfig INSTANCE = new ScellementRitualConfig();

    private String messageLight;
    private String messageFailNoHybride;
    private String bossBarTitle;
    private String particleId;
    private String soundId;

    private ScellementRitualConfig() {
        super("scellement_ritual.toml");
    }

    public static ScellementRitualConfig get() {
        return INSTANCE;
    }

    @Override
    protected void reload(CommentedFileConfig config) {
        messageLight = value(config, "messages.light",
                "Le Calice s'embrase d'une flamme bleue. Le sceau se referme sur l'Originel.",
                "Message diffuse quand le Calice, complet, est allume avec le Briquet special.");
        messageFailNoHybride = value(config, "messages.fail_no_hybride",
                "Aucun Originel n'arpente Lycania a l'instant. Le rituel echoue.",
                "Message d'echec si personne n'est actuellement l'Hybride quand le Calice est allume.");
        bossBarTitle = value(config, "boss_bar.title", "Scellement de l'Originel",
                "Titre de la barre de boss affichee pendant le decompte du scellement.");
        particleId = value(config, "ritual.particle_id", "minecraft:soul_fire_flame",
                "Particule jouee autour du Calice quand il s'allume.");
        soundId = value(config, "ritual.sound_id", "minecraft:block.beacon.activate",
                "Son joue quand le Calice s'allume.");
    }

    public String messageLight() {
        return messageLight;
    }

    public String messageFailNoHybride() {
        return messageFailNoHybride;
    }

    public String bossBarTitle() {
        return bossBarTitle;
    }

    public String particleId() {
        return particleId;
    }

    public String soundId() {
        return soundId;
    }
}
