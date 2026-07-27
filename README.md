# Lycania : L'Originel

Mod NeoForge pour Minecraft 1.21.1, developpe pour le serveur semi-RP **Lycania**.

## Lore

Il y a 900 ans, quatre enfants — Aldren, Lysandra, Elias et Corvin — ont reveille deux
artefacts endormis dans un temple oublie. Une **nuit ecarlate** (lune rouge) a declenche
une malediction : loups-garous et vampires sont nes cette nuit-la.

Corvin, frappe en marge du rituel, n'est devenu ni l'un ni l'autre. Il est devenu
**immortel et hybride**, presque impossible a tuer. Banni injustement par les siens, il
revient aujourd'hui pour voler la **Pierre de Clair de Lune**, la relique qui maintient le
**Voile** protecteur — et rouvrir la malediction qu'il porte depuis 900 ans.

L'Hybride, l'Originel, c'est Corvin. Un seul joueur (le staff) peut l'incarner.

Ce mod s'appuie sur l'API de factions de [Vampirism](https://github.com/TeamLapen/Vampirism)
et coexiste avec [Werewolves](https://github.com/TeamLapen/Werewolves), tous deux sous
licence LGPL-3.0.

## Prerequis

- Java 21
- Minecraft 1.21.1
- NeoForge 21.1.x
- [Vampirism](https://modrinth.com/mod/vampirism) 1.10.x pour Minecraft 1.21.x
- [Werewolves](https://modrinth.com/mod/werewolves) 2.0.3.3 pour Minecraft 1.21.x
- [Armourer's Workshop](https://modrinth.com/mod/armourers-workshop) pour Minecraft 1.21.1 (optionnel, requis pour l'Anneau de Cendre)

## Build

```bash
./gradlew build
```

Le jar est genere dans `build/libs/originel-<version>.jar`.

## Lancer un client de developpement

```bash
./gradlew runClient
```

Vampirism, Werewolves et Armourer's Workshop sont declares en dependance de developpement
(`runtimeOnly`) : ils sont automatiquement telecharges et charges dans l'environnement de
run Gradle, sans etre embarques dans le jar final du mod.

## Installation (serveur/client de jeu)

1. Installer NeoForge 21.1.x pour Minecraft 1.21.1.
2. Placer dans `mods/` : `originel-<version>.jar`, ainsi que Vampirism, Werewolves, et
   (optionnellement) Armourer's Workshop.
3. Demarrer le jeu : la configuration est generee dans `config/originel/`.

## Configuration

Toute la logique du mod (stats, delais, textes, conditions) est pilotee par des fichiers
TOML rechargeables a chaud, dans `config/originel/` :

| Fichier | Role |
|---|---|
| `general.toml` | Options generales du mod |
| `hybride.toml` | Whitelist du joueur Hybride, parametres de la faction |
| `skills.toml` | Arbre de competences (branches Sang / Lune / Originel) |
| `faiblesse.toml` | Conditions de la Faiblesse Cachee |
| `cendre.toml` | Anneau de Cendre (Armourer's Workshop) |
| `rituel.toml` | Composants et deroule du Rituel d'Hybridation |
| `lunerouge.toml` | Frequence et effets de la Lune Rouge |

Chaque fichier est cree avec ses valeurs par defaut (et un commentaire
explicatif au-dessus de chaque cle) au premier demarrage s'il n'existe pas
deja, et rechargeable a chaud via `/originel reload` sans redemarrer le
serveur. Vue d'ensemble des sections de chaque fichier :

- **general.toml** : `chat_prefix`, `debug_logging`, `staff_permission_level`.
- **hybride.toml** : `whitelist` (UUID/pseudo autorise), les messages
  d'assignation/retrait/refus, `progression.max_level` et les bonus de
  stats par niveau, ainsi que la progression par kills (optionnelle).
- **skills.toml** : `general.points_per_level`, puis une table par
  competence (`sang.*`, `lune.*`, `originel.*`, `ultime.*`) avec son cout et
  ses parametres propres (portee, duree, degats, cooldown...).
- **faiblesse.toml** : les 4 conditions individuellement activables
  (lune pleine, lune rouge, scellement, sang de gardien), le multiplicateur
  de degats et l'effet de ricochet.
- **cendre.toml** : reduction des degats solaires, malus (soif, degats
  affaiblis), systeme de charges, messages.
- **rituel.toml** : rayon de recherche de l'autel, duree de la sequence,
  declenchement de la Lune Rouge, textes du Carnet de Corvin.
- **lunerouge.toml** : frequence automatique, messages, particules,
  bonus aux factions creatures.

Le detail exact de chaque cle (avec sa valeur par defaut) est documente en
commentaire directement dans le fichier TOML genere, et les effets qu'elles
pilotent sont decrits dans `COMMANDES.md`.

## Commandes

Voir [`COMMANDES.md`](COMMANDES.md) pour la liste complete des commandes
staff, l'arbre de competences, et le fonctionnement detaille du Rituel
d'Hybridation, de l'Aura d'Abomination et de l'Anneau de Cendre.

## Etat d'avancement

- [x] Etape 1 — Squelette du projet NeoForge
- [x] Etape 2 — Systeme de configuration TOML + `/originel reload`
- [x] Etape 3 — Faction Hybride (l'Originel)
- [x] Etape 4 — Progression par niveaux
- [x] Etape 5 — Arbre de competences (pilote par commandes, `skills.toml`)
- [x] Etape 6 — Invincibilite, Faiblesse Cachee, Dague de l'Originel
- [x] Etape 7 — La Lune Rouge
- [x] Etape 8 — Le Rituel d'Hybridation (Autel du Voile a bloc unique)
- [x] Etape 9 — L'Anneau de Cendre
- [x] Etape 10 — Aura d'Abomination, finitions, documentation

## Limitations connues

- L'arbre de competences (etape 5) est pilote par commandes
  (`/originel skill give|use`) plutot que par l'ecran de competences natif
  de Vampirism, pour rester simple et testable sans client graphique
  (voir le message de commit de l'etape 5 pour le detail du choix).
- L'Autel du Voile (etape 8) est un bloc unique (avec un block entity a 4
  emplacements, un par composant), pas un vrai multibloc - fallback
  explicitement autorise par le cahier des charges.
- L'ambiance visuelle de la Lune Rouge (etape 7) repose sur des particules
  et un message, pas sur une reelle teinte du ciel/de la lune (qui
  demanderait des hooks de rendu client difficiles a verifier dans
  l'environnement de developpement utilise pour ce mod).
- L'Anneau de Cendre (etape 9) : les versions d'Armourer's Workshop
  disponibles pour ce mod n'ont ni item "anneau" ni emplacement
  d'equipement dedie (verifie dans son code source). "Porter" l'anneau est
  donc implemente comme "tenir en main principale ou secondaire", et
  `/originel cendre convert` attache simplement le composant de charges a
  l'objet deja tenu (y compris un accessoire Armourer's Workshop, dont
  l'apparence/les donnees sont preservees telles quelles) plutot que
  d'exiger un item "anneau" specifique d'Armourer's Workshop.
- L'Aura d'Abomination (etape 10) implemente le "malaise" du cahier des
  charges avec l'effet vanilla Nausee (duree/amplificateur configurables) ;
  aucun effet personnalise n'a ete cree pour cela.

## Tests effectues

Ce mod a ete developpe et valide dans un environnement sans client
Minecraft graphique connectable. Chaque etape a ete testee via un serveur
de developpement demarre avec `./gradlew runServer`, en injectant des
commandes console (executees avec les permissions du serveur, niveau
staff) :

- **Etape 1** : `./gradlew build` reussit, le serveur demarre avec
  Vampirism, Werewolves et Armourer's Workshop charges sans erreur.
- **Etape 2** : les 7 fichiers `config/originel/*.toml` sont generes avec
  leurs valeurs par defaut et leurs commentaires ; modifier une valeur puis
  lancer `/originel reload` change le comportement sans redemarrage.
- **Etape 3** : `/originel set <joueur whitelist>` assigne la faction
  Hybride ; la meme commande sur un joueur non whitelist echoue proprement
  avec le message configure ; `/originel remove` retire la faction.
- **Etape 4** : `/originel level set <joueur> <n>` applique les bonus de
  stats configures et accorde les points de competence correspondants.
- **Etape 5** : `/originel skill give/use` deverrouille et declenche
  chaque competence, respecte les couts et les delais de recharge.
- **Etape 6** : un Hybride encaisse les degats sans effet (ricochet) ;
  avec la Dague de l'Originel imbibee de Sang de Gardien, un joueur scelle
  et sous lune rouge, les degats passent et sont multiplies.
- **Etape 7** : `/originel lunerouge start` declenche l'evenement
  (message, particules, bonus aux creatures), reste actif dans la duree,
  et `/originel lunerouge stop`/l'aube le terminent proprement (bug de
  fin-a-l'aube premature reproduit puis corrige et reverifie en direct).
- **Etape 8** : deposer les 4 composants sur l'Autel du Voile puis lancer
  `/originel rituel start` declenche la sequence et assigne la faction ;
  la commande echoue proprement sans joueur whitelist connecte, sans
  autel complet, ou si un Hybride vivant existe deja.
- **Etape 9** : `cendre.toml` se genere avec les valeurs par defaut
  attendues ; `/originel cendre convert|give` sont bien enregistrees et
  echouent proprement sans joueur cible connecte.
- **Etape 10** : les nouvelles cles `originel.aura_abomination.*` de
  `skills.toml` se generent correctement ; le serveur demarre sans erreur
  avec le handler de l'Aura d'Abomination actif.

**Limite assumee** : les mecaniques qui necessitent un vrai joueur en jeu
exposé au soleil, frappé, ou physiquement proche d'un autre joueur (degats
solaires reellement bloques par l'Anneau de Cendre, pulsation de l'Aura
d'Abomination percue par un joueur voisin, etc.) reposent sur les memes
hooks NeoForge/Vampirism (`LivingIncomingDamageEvent`, `PlayerTickEvent`)
deja eprouves en direct pour la Faiblesse Cachee et les competences, mais
n'ont pas pu etre rejouees de bout en bout avec un client connecte dans cet
environnement de developpement.

## Licence

LGPL-3.0. Voir [LICENSE](LICENSE).
