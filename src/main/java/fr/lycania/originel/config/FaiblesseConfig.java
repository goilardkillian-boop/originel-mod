package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public final class FaiblesseConfig extends TomlConfigFile {

    static final FaiblesseConfig INSTANCE = new FaiblesseConfig();

    private boolean conditionLunePleineEnabled;
    private boolean conditionLuneRougeEnabled;
    private boolean conditionScellementEnabled;
    private boolean conditionSangGardienEnabled;
    private double damageMultiplier;
    private int scellementDurationTicks;
    private String ricochetSound;
    private String ricochetParticle;

    private FaiblesseConfig() {
        super("faiblesse.toml");
    }

    public static FaiblesseConfig get() {
        return INSTANCE;
    }

    @Override
    protected void reload(CommentedFileConfig config) {
        conditionLunePleineEnabled = value(config, "conditions.lune_pleine_enabled", true,
                "La nuit de pleine lune fait partie des conditions de la Faiblesse Cachee.");
        conditionLuneRougeEnabled = value(config, "conditions.lune_rouge_enabled", true,
                "La Lune Rouge (etape 7) fait partie des conditions de la Faiblesse Cachee.");
        conditionScellementEnabled = value(config, "conditions.scellement_enabled", true,
                "La cible doit porter le marqueur de scellement (/originel scellement) pour que la faiblesse s'applique.");
        conditionSangGardienEnabled = value(config, "conditions.sang_gardien_enabled", true,
                "La Dague de l'Originel doit porter le composant Sang de Gardien pour que la faiblesse s'applique.");
        damageMultiplier = value(config, "damage_multiplier", 3.0,
                "Multiplicateur applique aux degats de la Dague de l'Originel quand toutes les conditions sont reunies.");
        scellementDurationTicks = value(config, "scellement_duration_ticks", 6000,
                "Duree (ticks) du marqueur de scellement pose par /originel scellement.");
        ricochetSound = value(config, "ricochet.sound", "minecraft:item.shield.block",
                "Son joue quand un coup est bloque par l'invincibilite de l'Hybride.");
        ricochetParticle = value(config, "ricochet.particle", "minecraft:crit",
                "Particule affichee quand un coup est bloque par l'invincibilite de l'Hybride.");
    }

    public boolean conditionLunePleineEnabled() {
        return conditionLunePleineEnabled;
    }

    public boolean conditionLuneRougeEnabled() {
        return conditionLuneRougeEnabled;
    }

    public boolean conditionScellementEnabled() {
        return conditionScellementEnabled;
    }

    public boolean conditionSangGardienEnabled() {
        return conditionSangGardienEnabled;
    }

    public double damageMultiplier() {
        return damageMultiplier;
    }

    public int scellementDurationTicks() {
        return scellementDurationTicks;
    }

    public String ricochetSound() {
        return ricochetSound;
    }

    public String ricochetParticle() {
        return ricochetParticle;
    }
}
