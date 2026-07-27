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
| `/originel give dague_originel <joueur> [sang_gardien]` | Delivre la Dague de l'Originel (aussi obtenable par craft, voir plus bas). L'argument optionnel `sang_gardien` (true/false) determine si la lame porte deja le composant necessaire a la Faiblesse Cachee. |
| `/originel give pierre_clair_de_lune <joueur>` | Delivre la Pierre de Clair de Lune, composant du Rituel d'Hybridation. |
| `/originel give sang_gardien <joueur>` | Delivre le composant d'objet Sang de Gardien (a ne pas confondre avec le composant de donnees du meme nom sur la Dague, voir etape 6). |
| `/originel give eclat_voile <joueur>` | Delivre l'Eclat de Voile, composant du Rituel d'Hybridation. |
| `/originel give carnet_corvin <joueur>` | Delivre le Carnet de Corvin, livre lore lisible (titre/auteur/pages configures dans `rituel.toml#carnet`). |
| `/originel give autel_du_voile <joueur>` | Delivre le bloc Autel du Voile a placer. |
| `/originel give calice <joueur>` | Delivre le bloc Calice a placer (aussi obtenable par craft, voir Rituel de Scellement plus bas). |
| `/originel give briquet_special <joueur>` | Delivre le Briquet special qui allume le Calice (aussi obtenable par craft). |
| `/originel scellement <joueur>` | Pose le marqueur de scellement sur l'Hybride pour la duree configuree (`faiblesse.toml#scellement_duration_ticks`), une des conditions de la Faiblesse Cachee. |
| `/originel scellement stop` | Leve immediatement le scellement en cours et retire la barre de boss, quelle que soit son origine (commande ou Rituel de Scellement). Surtout utile en test. |
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

Les competences actives declenchent des particules en jeu (en plus des
messages/effets deja en place) : brume/nuage au depart et a l'arrivee de la
Brume, poussiere d'ames sur la cible du Regard hypnotique, impact griffu sur
les Griffes, fumee lors de la Metamorphose, marquage lumineux du Commandement,
flammes et eclats pour la Colere de l'Originel, un impact + le cri de meute
(`werewolves:entity.werewolf.howl` par defaut, configurable dans
`skills.toml#lune.hurlement_meute.sound`) pour le Hurlement de meute, et une
gerbe rouge sur chaque coup vole par la Morsure vampirique.

La trainee de brume derriere l'Hybride en mouvement (`hybride.toml#visuals`)
est **desactivee par defaut** - un serveur ou `hybride.toml` a deja ete genere
doit passer `trail_enabled` a `false` a la main pour que le changement de
defaut prenne effet (voir la section config plus bas).

Odorat du sang et Sens aiguises ne mettent plus les cibles en Glowing
(effet vanilla visible de *tous* les joueurs proches, pas seulement
l'Hybride) : ils envoient a la place une particule de reperage qui n'est
visible que par l'Hybride lui-meme (`ServerLevel#sendParticles` cible sur un
seul joueur). La vision nocturne de Sens aiguises dure aussi plus longtemps
(260 ticks au lieu de 220) pour eviter le clignotement du fondu-vanilla qui
se declenche sous 200 ticks restants.

L'Aura d'Abomination (message/son/nausee aux joueurs vampires/loups-garous
proches) ne se declenche plus qu'**une fois par entree en portee**, plus a
chaque pulsation - rester a cote d'un Hybride demasque ne spamme plus le chat
ni la nausee en continu ; il faut quitter puis revenir dans le rayon pour
recevoir un nouvel avertissement.

Pendant la Lune Rouge, le ciel (brouillard) et la lune elle-meme sont teintes
de rouge sang cote client (`lunerouge.toml#sky`, couleurs et intensite
configurables, desactivable via `sky.tint_enabled`) - la lune est redessinee
par-dessus celle de vanilla (pas de mixin), et le mur de brouillard est aussi
rapproche (`sky.fog_distance_blocks`, 56 blocs par defaut) en plus de la
teinte. Intensite volontairement adoucie suite a un retour de test
(`sky.fog_strength` 0.85 -> 0.45, `sky.fog_red` 0.45 -> 0.38) - un serveur ou
`lunerouge.toml` a deja ete genere garde les anciennes valeurs tant qu'elles
ne sont pas editees a la main.

**Compatibilite shaders (Iris)** : un shaderpack (ex. Complementary Unbound)
remplace entierement le rendu du ciel par son propre shader, qui ignore la
teinte de brouillard vanilla et calcule sa propre position pour le soleil/la
lune - la lune redessinee par le mod se desalignait visiblement de celle du
shader. Detection automatique d'Iris par reflexion (dependance optionnelle,
`fr.lycania.originel.client.IrisCompat`) : shaderpack actif -> les 3 effets 3D
(teinte de brouillard, distance de brouillard, lune redessinee) se
desactivent tout seuls, remplaces par une legere teinte rouge en superposition
2D plein ecran (`sky.screen_tint_*`, desactivable via `sky.shader_fallback_
enabled`) qui fonctionne quel que soit le rendu 3D en dessous.

Rien de tout ca (particules, sons, brouillard, teinte plein ecran) n'est
verifiable visuellement dans l'environnement de developpement de ce mod -
voir README.

## Enjambee (masque retire)

Masque retire (Metamorphose activee), l'Hybride monte automatiquement sur les
blocs pleins en marchant dedans, sans avoir a sauter ni a construire - un
bonus sur l'attribut vanilla `minecraft:generic.step_height` (base 0.6,
`skills.toml#originel.metamorphose.step_height_bonus` = +1.0 par defaut, donc
1.6). C'est le meme attribut que vanilla utilise deja pour regler la hauteur
de marche normale des entites, pas un hack de mouvement personnalise : appele
une fois a l'activation/desactivation de Metamorphose (comme les modificateurs
de Force bestiale/Velocite), pas recalcule en boucle.

## Arbre de competences

Identifiants des 15 competences (+ 1 ultime) utilisables avec `skill give`/`skill use`,
tous configurables (cout, portee, duree, degats...) dans `skills.toml` :

| Branche | Competence (id) | Type | Effet |
|---|---|---|---|
| Sang | `velocite` | Passive | Vitesse de deplacement augmentee en permanence |
| Sang | `regard_hypnotique` | Active | Ralentit la cible visee |
| Sang | `morsure_vampirique` | Passive | Vol de vie et de nourriture/saturation au corps a corps |
| Sang | `brume` | Active | Court teleport dans la direction du regard |
| Sang | `odorat_sang` | Passive | Repere (particule privee, visible de l'Hybride seul) toute creature en dessous d'un seuil de vie a portee |
| Lune | `force_bestiale` | Passive | Degats d'attaque augmentes en permanence |
| Lune | `sens_aiguises` | Passive | Vision nocturne + entites proches reperees (particule privee) |
| Lune | `griffes` | Active | Bond griffu, saignement autour de l'impact |
| Lune | `peau_de_bete` | Passive | Degats subis reduits en permanence |
| Lune | `hurlement_meute` | Active | Effraie (Faiblesse + Lenteur) les monstres hostiles a portee, buff de vitesse/force pour soi |
| Originel | `aura_abomination` | Passive | Signal discret aux joueurs des factions creatures a portee, et fait fuir les mobs vampires/loups-garous a portee (voir plus bas). Actif uniquement masque retire (Metamorphose) |
| Originel | `regeneration_impie` | Passive | Regeneration de vie en continu. Actif uniquement masque retire (Metamorphose) |
| Originel | `metamorphose` | Active | Retire ou remet le masque humain : masque porte (etat par defaut) = degats reels, indetectable ; masque retire = invincible (Faiblesse Cachee), regenere, escalade les murs, mais Aura d'Abomination active (detectable) |
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
- Le deplacement (WASD, saut, sneak, sprint) continue de fonctionner pendant
  que la roue est ouverte - seule la visee/camera s'arrete (curseur libere
  pour viser la roue), comme n'importe quel autre menu.

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

Masque retire (Metamorphose activee - la vraie forme de l'Originel, exposee
et detectable via l'Aura d'Abomination), l'Hybride est immunise a tous les
degats (ricochet + son/particule), a une exception pres : **toutes** les
conditions activees dans `faiblesse.toml` doivent etre reunies **en meme
temps** au moment du coup pour que les degats passent (multiplies par
`damage_multiplier`) :

1. L'attaquant tient la Dague de l'Originel Ecarlate (imbibee, voir
   rituel d'impregnation ci-dessous) en main principale.
2. L'Hybride porte le marqueur de scellement, pose par le staff via
   `/originel scellement <joueur>` **ou** par n'importe quel joueur via le
   Rituel de Scellement en survie (voir plus bas), et qui expire au bout de
   `faiblesse.toml#scellement_duration_ticks` (5 minutes par defaut) -
   **cette etape est facile a oublier** : sans elle, une dague imbibee
   frappee en pleine lune ne fait toujours rien.
3. C'est la pleine lune ou la Lune Rouge est active (selon les conditions
   activees dans la config).

Chacune de ces conditions peut etre desactivee individuellement dans
`faiblesse.toml#conditions`. Masque porte (etat par defaut), l'invincibilite
(et donc toute cette mecanique) ne s'applique plus du tout : les degats
passent normalement, quelle que soit l'arme, mais l'Hybride est indetectable
(voir Metamorphose).

## Rituel de Scellement (Calice)

Alternative en survie a `/originel scellement <joueur>` - n'importe quel
joueur peut sceller l'Hybride actuel sans intervention du staff :

1. Deposer un par un (clic droit sur le Calice, un objet en main) les trois
   composants : un croc de vampire (`vampirism:vampire_fang`), un croc de
   loup-garou (`werewolves:werewolf_tooth`), et un Sang de Gardien - le sang
   de Marcus, qu'il gardait cache en sachant qu'il aurait un role a jouer.
2. Une fois le Calice complet, l'allumer avec le Briquet special (clic droit
   dessus) : sa flamme bleue consomme les trois composants et pose le
   scellement (meme duree que la commande staff) sur quiconque est
   actuellement l'Hybride. Echoue proprement (message) si personne n'est
   Hybride a l'instant.
3. Une **barre de boss bleue** ("Scellement de l'Originel") apparait pour
   tous les joueurs connectes et decompte les 5 minutes ; elle disparait
   toute seule a expiration, ou immediatement avec `/originel scellement
   stop` (staff, surtout pour les tests).

Le Calice se craft avec 2 lingots d'argent (`werewolves:silver_ingot`) et un
silex, comme un bol vanilla (`S . S` / `. F .`). Le Briquet special se craft
en combinant un briquet vanilla avec du sol des ames (`minecraft:soul_soil`).
Textures et recettes non verifiees visuellement dans l'environnement de
developpement de ce mod, mais le serveur de dev demarre sans erreur avec les
deux recettes et les items modded referencees (Vampirism/Werewolves).

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

La Dague de l'Originel elle-meme (auparavant uniquement `/originel give`) se
craft desormais aussi, une par facette de l'Hybride - humaine, chasseuse,
vampire, loup-garou :

```
. L .
V S W
```

`L` = `werewolves:liver`, `V` = `vampirism:vampire_blood_bottle`,
`S` = `originel:sang_gardien`, `W` = `werewolves:silver_block`. Le resultat
n'est pas imbibee (comme `/originel give dague_originel <joueur>` sans
l'argument optionnel) - passer par le rituel d'impregnation ci-dessus ensuite.

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

## Onglet creatif

Tous les items/blocs du mod (Dague, dague imbibee, composants de rituels,
Autel du Voile, Calice, Briquet special, Anneau de Cendre...) sont regroupes
dans un onglet dedie "Lycania : L'Originel" du menu creatif, plutot que
disperses dans les onglets vanilla ou invisibles.

## Vie bonus et respawn

Les bonus de niveau de l'Hybride (`hybride.toml#progression`, vie/degats/
vitesse) sont appliques via des modificateurs d'attribut sur
`PlayerFactionEvent.FactionLevelChanged` - qui ne se redeclenche pas a la
mort. Comme la mort remplace entierement l'instance `ServerPlayer` (nouvelle
entite avec les attributs vanilla par defaut), les coeurs bonus disparaissaient
au respawn sans etre reappliques. Corrige via `PlayerEvent.Clone`, qui
reapplique les modificateurs et remet la vie au maximum (bonus inclus) des
que le nouveau joueur existe.

Chaque fois que l'anneau bloque des degats solaires, il perd des charges
(`charges.loss_per_exposure`). Un message d'avertissement est envoye sous
un seuil bas configurable (`charges.low_threshold_percent`), et l'anneau
est detruit (retire de la main) quand ses charges atteignent zero.

