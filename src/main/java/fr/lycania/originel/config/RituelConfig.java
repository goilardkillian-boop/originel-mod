package fr.lycania.originel.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.util.List;

public final class RituelConfig extends TomlConfigFile {

    static final RituelConfig INSTANCE = new RituelConfig();

    private int altarSearchRadius;
    private int sequenceDurationTicks;
    private boolean triggerLuneRouge;
    private String particleId;
    private String soundId;
    private String messageStart;
    private String messageComplete;
    private String messageFailNoAltar;
    private String messageFailAlreadyHybride;
    private String messageFailNotWhitelisted;
    private String messageFailPlayerOffline;
    private String bookTitle;
    private String bookAuthor;
    private List<String> bookPages;

    private RituelConfig() {
        super("rituel.toml");
    }

    public static RituelConfig get() {
        return INSTANCE;
    }

    @Override
    protected void reload(CommentedFileConfig config) {
        altarSearchRadius = value(config, "altar.search_radius", 16,
                "Distance (blocs) autour du joueur whitelist dans laquelle chercher un Autel du Voile complet.");
        sequenceDurationTicks = value(config, "sequence.duration_ticks", 100,
                "Duree (ticks) entre le lancement de /originel rituel start et l'attribution de la faction.");
        triggerLuneRouge = value(config, "sequence.trigger_lune_rouge", true,
                "Si true, le rituel declenche aussi la Lune Rouge (etape 7).");
        particleId = value(config, "sequence.particle_id", "minecraft:soul",
                "Particule jouee autour de l'autel pendant le rituel.");
        soundId = value(config, "sequence.sound_id", "minecraft:entity.wither.spawn",
                "Son joue au lancement du rituel.");
        messageStart = value(config, "messages.start",
                "Les composants s'embrasent. Le Voile se dechire...",
                "Message diffuse au lancement du rituel.");
        messageComplete = value(config, "messages.complete",
                "Corvin se redresse. L'Originel est de retour parmi Lycania.",
                "Message diffuse a la fin du rituel, quand la faction est attribuee.");
        messageFailNoAltar = value(config, "messages.fail_no_altar",
                "Aucun Autel du Voile complet n'a ete trouve pres du joueur whitelist.",
                "Message d'echec si aucun autel complet n'est trouve.");
        messageFailAlreadyHybride = value(config, "messages.fail_already_hybride",
                "L'Originel marche deja parmi les vivants. Le rituel echoue.",
                "Message d'echec si le joueur whitelist est deja Hybride.");
        messageFailNotWhitelisted = value(config, "messages.fail_not_whitelisted",
                "Aucun joueur whitelist n'est configure pour devenir l'Hybride.",
                "Message d'echec si hybride.toml n'a pas d'UUID whitelist valide.");
        messageFailPlayerOffline = value(config, "messages.fail_player_offline",
                "Le joueur whitelist doit etre en ligne, pres de l'autel, pour que le rituel se produise.",
                "Message d'echec si le joueur whitelist n'est pas connecte.");
        bookTitle = value(config, "carnet.title", "Le Carnet de Corvin",
                "Titre du livre Carnet de Corvin.");
        bookAuthor = value(config, "carnet.author", "Corvin",
                "Auteur affiche du livre Carnet de Corvin.");
        bookPages = value(config, "carnet.pages", List.of(
                        "Nous etions quatre. Aldren, Lysandra, Elias, et moi. Le temple nous appelait depuis des semaines, et nous etions trop jeunes pour comprendre ce qu'il gardait endormi.",
                        "Deux artefacts reposaient sous la poussiere de neuf siecles. Nous les avons reveilles ensemble, une nuit, en riant, sans savoir.",
                        "La lune s'est teintee de sang cette nuit-la. On l'appelle depuis la nuit ecarlate. Aldren, Lysandra et Elias en sont ressortis changes : les uns loups, les autres vampires.",
                        "Moi, frappe en marge du rituel, je ne suis devenu ni l'un ni l'autre. Immortel. Hybride. Presque impossible a tuer. Et pour cela seul, on m'a banni.",
                        "Neuf cents ans. J'ai attendu neuf cents ans dans l'exil qu'ils m'ont impose. La Pierre de Clair de Lune maintient encore le Voile qu'ils ont leve contre moi.",
                        "Je reviens la chercher. Et avec elle, je rouvrirai ce que la nuit ecarlate a commence. - Corvin"),
                "Pages du livre Carnet de Corvin (une entree par page).");
    }

    public int altarSearchRadius() {
        return altarSearchRadius;
    }

    public int sequenceDurationTicks() {
        return sequenceDurationTicks;
    }

    public boolean triggerLuneRouge() {
        return triggerLuneRouge;
    }

    public String particleId() {
        return particleId;
    }

    public String soundId() {
        return soundId;
    }

    public String messageStart() {
        return messageStart;
    }

    public String messageComplete() {
        return messageComplete;
    }

    public String messageFailNoAltar() {
        return messageFailNoAltar;
    }

    public String messageFailAlreadyHybride() {
        return messageFailAlreadyHybride;
    }

    public String messageFailNotWhitelisted() {
        return messageFailNotWhitelisted;
    }

    public String messageFailPlayerOffline() {
        return messageFailPlayerOffline;
    }

    public String bookTitle() {
        return bookTitle;
    }

    public String bookAuthor() {
        return bookAuthor;
    }

    public List<String> bookPages() {
        return bookPages;
    }
}
