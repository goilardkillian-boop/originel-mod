# Commandes

Toutes les commandes ci-dessous sont sous `/originel` et reservees au staff
(niveau de permission configurable dans `general.toml`, `staff_permission_level`,
defaut 2).

| Commande | Effet |
|---|---|
| `/originel reload` | Recharge a chaud tous les fichiers `config/originel/*.toml`. |
| `/originel set <joueur>` | Attribue la faction Hybride au joueur cible (doit etre en ligne). Echoue proprement si ce n'est pas le joueur whitelist dans `hybride.toml`. Force le changement s'il etait deja dans une autre faction (vampire/loup-garou). |
| `/originel remove <joueur>` | Retire la faction Hybride au joueur cible (doit etre en ligne). |
| `/originel level set <joueur> <n>` | Fixe le niveau d'Hybride du joueur (borne a `hybride.toml#progression.max_level`). Chaque niveau applique les bonus de stats configures et accorde des points de competence. |
| `/originel skill give <joueur> <competence>` | Debloque une competence pour le joueur (doit etre l'Hybride), en depensant un point de competence. Echoue si la competence est deja debloquee, si le niveau requis n'est pas atteint (Colere de l'Originel : niveau max) ou si le joueur n'a pas assez de points. |
| `/originel skill use <joueur> <competence>` | Declenche une competence active deja debloquee (Regard hypnotique, Brume, Griffes, Metamorphose, Commandement, Colere de l'Originel). Respecte le delai de recharge configure. |
| `/originel give dague_originel <joueur> [sang_gardien]` | Delivre la Dague de l'Originel (rare, non craftable). L'argument optionnel `sang_gardien` (true/false) determine si la lame porte deja le composant necessaire a la Faiblesse Cachee. |
| `/originel give pierre_clair_de_lune <joueur>` | Delivre la Pierre de Clair de Lune, composant du Rituel d'Hybridation. |
| `/originel give sang_gardien <joueur>` | Delivre le composant d'objet Sang de Gardien (a ne pas confondre avec le composant de donnees du meme nom sur la Dague, voir etape 6). |
| `/originel give eclat_voile <joueur>` | Delivre l'Eclat de Voile, composant du Rituel d'Hybridation. |
| `/originel give carnet_corvin <joueur>` | Delivre le Carnet de Corvin, livre lore lisible (titre/auteur/pages configures dans `rituel.toml#carnet`). |
| `/originel give autel_du_voile <joueur>` | Delivre le bloc Autel du Voile a placer. |
| `/originel scellement <joueur>` | Pose le marqueur de scellement sur l'Hybride pour la duree configuree (`faiblesse.toml#scellement_duration_ticks`), une des conditions de la Faiblesse Cachee. |
| `/originel lunerouge start` | Declenche la Lune Rouge : message immersif a tous les joueurs, force la nuit si necessaire, bonus aux creatures Vampire/Loup-garou presentes, particules d'ambiance. Se termine automatiquement a l'aube (ou avec `stop`). |
| `/originel lunerouge stop` | Termine la Lune Rouge immediatement (message de fin, retire les bonus aux creatures). |
| `/originel rituel start` | Lance le Rituel d'Hybridation : cherche un Autel du Voile complet (les 4 composants deposes) pres du joueur whitelist connecte, joue une sequence (particules/son), declenche la Lune Rouge (configurable), puis attribue la faction Hybride. Echoue proprement si aucun joueur whitelist, hors ligne, deja Hybride, ou pas d'autel complet a proximite. |
| `/originel cendre convert <joueur>` | Convertit l'objet tenu en main principale par le joueur cible en Anneau de Cendre (ajoute le composant de charges, conserve l'objet et son apparence tels quels). Echoue proprement si la main est vide ou si l'objet est deja un Anneau de Cendre. |
| `/originel cendre give <joueur>` | Delivre un Anneau de Cendre neuf (item `originel:anneau_de_cendre`) directement dans l'inventaire du joueur cible, avec le nombre de charges maximal configure. |

Une frequence automatique de Lune Rouge (toutes les N nuits) peut aussi etre
activee sans commande via `lunerouge.toml#auto.enabled`.

## Arbre de competences

Identifiants des 13 competences (+ 1 ultime) utilisables avec `skill give`/`skill use`,
tous configurables (cout, portee, duree, degats...) dans `skills.toml` :

| Branche | Competence (id) | Type |
|---|---|---|
| Sang | `velocite` | Passive |
| Sang | `regard_hypnotique` | Active |
| Sang | `morsure_vampirique` | Passive (vol de vie au corps a corps) |
| Sang | `brume` | Active |
| Lune | `force_bestiale` | Passive |
| Lune | `sens_aiguises` | Passive |
| Lune | `griffes` | Active |
| Lune | `peau_de_bete` | Passive |
| Originel | `aura_abomination` | Passive (signal discret aux joueurs des factions creatures a portee, voir plus bas) |
| Originel | `regeneration_impie` | Passive |
| Originel | `metamorphose` | Active |
| Originel | `commandement` | Active |
| Ultime (niveau max) | `colere_originel` | Active |

## Rituel d'Hybridation

Deroule attendu (voir `rituel.toml` pour tous les parametres) :

1. Le staff obtient les 4 composants (`/originel give ...`) ou les trouve via
   loot/recette configurable par le serveur.
2. Le staff place l'Autel du Voile (`originel:autel_du_voile`) et clique
   dessus avec chaque composant en main pour le deposer (un clic par
   composant, ordre indifferent).
3. Une fois l'autel complet, `/originel rituel start` : le joueur whitelist
   doit etre en ligne et pres de l'autel (rayon configurable).
4. Si un Hybride vivant existe deja, le rituel echoue proprement.

## Aura d'Abomination

Une fois la competence `aura_abomination` debloquee, l'Hybride emet une
pulsation discrete toutes les `skills.toml#originel.aura_abomination.interval_ticks`,
qui affecte chaque joueur des factions creatures (Vampirism ou Werewolves) a
portee (`radius`, en blocs) : un message personnel discret (`message`, visible
uniquement par la cible), un son entendu uniquement par elle (`sound`), et un
malaise (Nausee, `malaise_duration_ticks` / `malaise_amplifier`). L'Hybride
lui-meme n'est jamais affecte, et rien n'est diffuse publiquement.

## Anneau de Cendre

Un vampire (faction Vampirism) portant (tenant en main principale ou
secondaire) un Anneau de Cendre reduit ou annule les degats solaires
(`cendre.toml#effect.sun_damage_reduction_percent`), au prix de deux
malus tant que l'anneau est porte :

- une perte de sang periodique accrue (`malus.thirst_drain_percent` toutes
  les `malus.thirst_drain_interval_ticks`), qui accelere la soif ;
- une reduction des degats d'attaque (`malus.power_weaken_percent`).

Chaque fois que l'anneau bloque des degats solaires, il perd des charges
(`charges.loss_per_exposure`). Un message d'avertissement est envoye sous
un seuil bas configurable (`charges.low_threshold_percent`), et l'anneau
est detruit (retire de la main) quand ses charges atteignent zero.

