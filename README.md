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

_(Cette section sera completee au fur et a mesure de l'implementation.)_

## Commandes

_(Voir `COMMANDES.md`, a completer au fur et a mesure de l'implementation.)_

## Etat d'avancement

- [x] Etape 1 — Squelette du projet NeoForge
- [x] Etape 2 — Systeme de configuration TOML + `/originel reload`
- [x] Etape 3 — Faction Hybride (l'Originel)
- [ ] Etape 4 — Progression par niveaux
- [ ] Etape 5 — Arbre de competences
- [ ] Etape 6 — Invincibilite, Faiblesse Cachee, Dague de l'Originel
- [ ] Etape 7 — La Lune Rouge
- [ ] Etape 8 — Le Rituel d'Hybridation
- [ ] Etape 9 — L'Anneau de Cendre
- [ ] Etape 10 — Aura d'Abomination, finitions, documentation

## Licence

LGPL-3.0. Voir [LICENSE](LICENSE).
