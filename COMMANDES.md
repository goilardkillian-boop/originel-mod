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
| `/originel skill give <joueur> <competence>` | Debloque une competence pour le joueur (doit etre l'Hybride), en depensant un point de competence. Echoue si la competence est deja debloquee, si le niveau requis n'est pas atteint (Colere de l'Originel : niveau max) ou si le joueur n'a pas assez de points. Equivalent staff de l'arbre de competences (voir plus bas), que l'Hybride peut desormais utiliser lui-meme. |
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

## Textes de lore

Chaque item du mod (Dague de l'Originel, Pierre de Clair de Lune, Sang de
Gardien, Eclat de Voile, Carnet de Corvin, Anneau de Cendre, Autel du Voile)
affiche une ligne de lore italique sous son nom, tiree du fichier de langue
(`item.originel.<id>.lore` / `block.originel.<id>.lore`) - un seul handler
generique (`ItemLoreHandler`) la recherche pour n'importe quel item du mod,
pas besoin d'en coder une par item. La Dague de l'Originel affiche en plus
une ligne dediee expliquant le rituel d'impregnation tant qu'elle n'est pas
imbibee, remplacee par une ligne differente une fois imbibee.

**Important** : tous les textes du mod (noms d'items, lore, competences,
messages) sont volontairement identiques en francais dans `fr_fr.json` et
`en_us.json` - meme un client dont Minecraft est configure en anglais (ou
dans toute autre langue non fournie, qui retombe sur `en_us.json` par
defaut) verra le mod entierement en francais.

## Effets visuels

Les competences actives declenchent desormais des particules en jeu (en plus
des messages/effets deja en place) : brume/nuage au depart et a l'arrivee de
la Brume, poussiere d'ames sur la cible du Regard hypnotique, impact griffu
sur les Griffes, fumee lors de la Metamorphose, marquage lumineux du
Commandement, flammes et eclats pour la Colere de l'Originel, et une gerbe
rouge sur chaque coup vole par la Morsure vampirique. L'Hybride laisse aussi
une legere trainee de brume derriere lui en se deplacant (`hybride.toml#visuals`,
desactivable). Pendant la Lune Rouge, le ciel (brouillard) et la lune
elle-meme sont teintes de rouge sang cote client (`lunerouge.toml#sky`,
couleurs et intensite configurables, desactivable via `sky.tint_enabled`) -
la lune est redessinee par-dessus celle de vanilla (pas de mixin), et le
brouillard est melange vers la couleur configuree. Rien de tout ca n'est
verifiable visuellement dans l'environnement de developpement de ce mod -
voir README.

## Arbre de competences

Identifiants des 15 competences (+ 1 ultime) utilisables avec `skill give`/`skill use`,
tous configurables (cout, portee, duree, degats...) dans `skills.toml` :

| Branche | Competence (id) | Type | Effet |
|---|---|---|---|
| Sang | `velocite` | Passive | Vitesse de deplacement augmentee en permanence |
| Sang | `regard_hypnotique` | Active | Ralentit la cible visee |
| Sang | `morsure_vampirique` | Passive | Vol de vie et de nourriture/saturation au corps a corps |
| Sang | `brume` | Active | Court teleport dans la direction du regard |
| Sang | `odorat_sang` | Passive | Met en surbrillance toute creature en dessous d'un seuil de vie a portee |
| Lune | `force_bestiale` | Passive | Degats d'attaque augmentes en permanence |
| Lune | `sens_aiguises` | Passive | Vision nocturne + entites proches surlignees |
| Lune | `griffes` | Active | Bond griffu, saignement autour de l'impact |
| Lune | `peau_de_bete` | Passive | Degats subis reduits en permanence |
| Lune | `hurlement_meute` | Active | Effraie (Faiblesse + Lenteur) les monstres hostiles a portee, buff de vitesse/force pour soi |
| Originel | `aura_abomination` | Passive | Signal discret aux joueurs des factions creatures a portee, et fait fuir les mobs vampires/loups-garous a portee (voir plus bas). Actif uniquement masque retire (Metamorphose) |
| Originel | `regeneration_impie` | Passive | Regeneration de vie en continu. Actif uniquement masque porte (Metamorphose) |
| Originel | `metamorphose` | Active | Retire ou remet le masque humain : masque porte = invincible (Faiblesse Cachee) et regen, masque retire = degats reels mais Aura d'Abomination active |
| Originel | `commandement` | Active | Marque la cible visee d'une lueur prolongee |
| Ultime (niveau max) | `colere_originel` | Active | Buff temporaire cumulant les bonus des trois branches |

## Roue de competences (interface)

En plus des commandes ci-dessus, tout joueur (typiquement l'Hybride en jeu,
pas le staff) peut ouvrir une **roue de selection radiale** listant ses
competences **actives deja debloquees** (les passives n'y apparaissent pas,
rien a "activer") avec une touche dediee :

- Touche par defaut : **K** (configurable dans Options > Commandes > "Lycania : L'Originel").
- Survoler un secteur affiche son nom et une courte description ; s'il est
  en recharge, le temps restant s'affiche et le secteur est teinte en rouge.
- Clic gauche sur un secteur disponible declenche la competence (envoie une
  demande au serveur, qui revalide tout exactement comme `/originel skill
  use` - le client ne fait que proposer). Clic droit ou toucher `Echap` ferme
  la roue sans rien declencher.
- Si aucune competence active n'est debloquee, un message l'indique et la
  roue ne s'ouvre pas.

Cette interface est une implementation originale (pas une reutilisation du
code de Vampirism, qui vit dans un package interne non public) inspiree de
son selecteur d'actions radial - voir `CREDITS.md`. Son rendu visuel en jeu
n'a pas pu etre verifie dans l'environnement de developpement de ce mod
(pas de client graphique connectable) : merci de signaler tout probleme
d'affichage ou de reactivite au clic.

## Arbre de competences (interface)

Le deblocage des competences n'est plus reserve au staff : l'Hybride peut
depenser lui-meme ses points de competence via un **arbre de competences**
en grille (une colonne par branche), affichant toutes les competences
(actives et passives) avec leur cout et leur description au survol.

- Touche par defaut : **L** (configurable, meme categorie que la roue).
- Alternative "inventaire" : accroupi + clic droit sur le Carnet de Corvin
  ouvre l'arbre au lieu de lire le livre (un clic droit normal, non
  accroupi, continue de l'ouvrir en lecture comme avant).
- Un bouton grise (non cliquable) signale une competence deja debloquee,
  un niveau insuffisant (Colere de l'Originel : niveau max requis), ou pas
  assez de points ; le survol precise laquelle de ces raisons s'applique.
- Cliquer sur une competence disponible envoie une demande au serveur, qui
  revalide tout exactement comme `/originel skill give` (meme logique
  partagee, `SkillUnlock`) - le client ne fait que proposer.

Comme pour la roue, le rendu reel (positionnement des colonnes, lisibilite,
reactivite au clic) n'a pas pu etre verifie visuellement dans cet
environnement de developpement.

## Faiblesse Cachee (invincibilite)

Masque porte (etat par defaut, voir Metamorphose plus haut), l'Hybride est
immunise a tous les degats (ricochet + son/particule), a une exception
pres : **toutes** les conditions activees dans `faiblesse.toml` doivent
etre reunies **en meme temps** au moment du coup pour que les degats
passent (multiplies par `damage_multiplier`) :

1. L'attaquant tient la Dague de l'Originel Ecarlate (imbibee, voir
   rituel d'impregnation ci-dessous) en main principale.
2. L'Hybride porte le marqueur de scellement, pose par le staff via
   `/originel scellement <joueur>` et qui expire au bout de
   `faiblesse.toml#scellement_duration_ticks` (5 minutes par defaut) -
   **cette etape est facile a oublier** : sans elle, une dague imbibee
   frappee en pleine lune ne fait toujours rien.
3. C'est la pleine lune ou la Lune Rouge est active (selon les conditions
   activees dans la config).

Chacune de ces conditions peut etre desactivee individuellement dans
`faiblesse.toml#conditions`. Masque retire, l'invincibilite (et donc toute
cette mecanique) ne s'applique plus du tout : les degats passent normalement,
quelle que soit l'arme (voir Metamorphose).

## Rituel d'impregnation de sang (Dague de l'Originel)

La Dague de l'Originel ne peut blesser l'Hybride que si elle porte le
composant "Sang de Gardien" (voir Faiblesse Cachee, etape 6). En plus de
`/originel give dague_originel <joueur> sang_gardien:true` (staff), n'importe
quel joueur possedant la dague peut desormais l'imbiber lui-meme en survie :

1. Avoir la Dague de l'Originel en main principale et au moins
   `impregnation.toml#ritual.blood_cost` (3 par defaut) Sang de Gardien en
   main secondaire.
2. Rester accroupi et faire un clic droit (dans le vide ou sur un bloc).
3. La dague porte desormais le composant, le Sang de Gardien consomme
   disparait, un son et des particules confirment le rituel. Sa texture et
   son nom changent (Dague de l'Originel -> Dague de l'Originel Ecarlate).

Echoue proprement (message explicite) si la dague est deja imbibee ou s'il
n'y a pas assez de Sang de Gardien en main secondaire.

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
qui affecte a portee (`radius`, en blocs) :

- chaque **joueur** des factions creatures (Vampirism ou Werewolves) : un
  message personnel discret (`message`, visible uniquement par la cible),
  un son entendu uniquement par elle (`sound`), et un malaise (Nausee,
  `malaise_duration_ticks` / `malaise_amplifier`) ;
- chaque **mob** (non-joueur) des memes factions : une vraie fuite
  persistante (le mob se voit ajouter, une fois pour toutes a son
  apparition dans le monde, l'`AvoidEntityGoal` vanilla - celui-la meme
  qu'utilisent d'autres mobs pour fuir un danger - configure pour fuir
  tout Hybride ayant l'Aura d'Abomination a portee ; vitesse de fuite
  `flee_walk_speed_modifier` / `flee_sprint_speed_modifier`). Le mob
  navigue reellement pour s'eloigner (pathfinding), pas une simple
  poussee ponctuelle, et ca s'applique a **tous** les vampires/loups-garous
  du monde des qu'ils croisent la route d'un tel Hybride, pas seulement
  ceux presents lors d'une pulsation.

L'Hybride lui-meme n'est jamais affecte, et rien n'est diffuse publiquement.

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

