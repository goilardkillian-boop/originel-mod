# Credits

## Code / API tierces (non embarquees dans le jar)

- **Vampirism** — TeamLapen — https://github.com/TeamLapen/Vampirism — Licence LGPL-3.0.
  Utilise via `compileOnly`/`runtimeOnly` pour l'API de factions. Non redistribue.
- **Werewolves** — TeamLapen — https://github.com/TeamLapen/Werewolves — Licence LGPL-3.0.
  Utilise via `compileOnly`/`runtimeOnly` pour la gestion du modele de joueur. Non redistribue.
- **Armourer's Workshop** — Armourers-Workshop / Plushie et contributeurs —
  https://github.com/Armourers-Workshop/Armourers-Workshop — Licence CC-BY-NC-SA-3.0.
  Utilise via `compileOnly`/`runtimeOnly` pour l'Anneau de Cendre. Non redistribue.

## Assets (textures, modeles, sons) du mod Originel

| Fichier | Source | Licence |
|---|---|---|
| `assets/originel/textures/item/dague_originel.png` | Pixel art original, cree pour ce mod (script Python generant un PNG 32x32, aucun asset tiers utilise) | CC0 / domaine public |
| `assets/originel/textures/item/pierre_clair_de_lune.png` | Pixel art original, cree pour ce mod (meme methode) | CC0 / domaine public |
| `assets/originel/textures/item/sang_gardien.png` | Pixel art original, cree pour ce mod (meme methode) | CC0 / domaine public |
| `assets/originel/textures/item/eclat_voile.png` | Pixel art original, cree pour ce mod (meme methode) | CC0 / domaine public |
| `assets/originel/textures/block/autel_du_voile.png` | Pixel art original, cree pour ce mod (meme methode, 16x16) | CC0 / domaine public |
| `assets/originel/textures/item/anneau_de_cendre.png` | Pixel art original, cree pour ce mod (meme methode) | CC0 / domaine public |
| `assets/originel/textures/item/carnet_corvin.png` | Pixel art original, cree pour ce mod (meme methode) ; remplace l'ancienne reference a la texture vanilla | CC0 / domaine public |
| `assets/originel/textures/skill/*.png` (6 icones : regard_hypnotique, brume, griffes, metamorphose, commandement, colere_originel) | Pixel art original, cree pour ce mod (meme methode), utilise par la roue de competences | CC0 / domaine public |

Toutes les textures d'items/bloc ci-dessus sont generees par un script Python
maison (`zlib`+`struct`, sans bibliotheque d'image tierce) qui compose des
formes vectorielles simples (polygones, cercles, gradients) en pixel art -
voir l'historique Git pour le detail des scripts. Aucun asset tiers,
proprietaire ou sous licence incompatible n'est utilise ou redistribue.

## Interface (roue de competences)

La roue de competences (etape 11) est une implementation originale, ecrite
pour ce mod, de la forme generale d'un menu radial (secteurs autour du
centre de l'ecran, hit-test par angle/distance de la souris). Elle s'inspire
du role et de l'allure du selecteur d'actions radial de Vampirism, mais son
moteur reel (`de.teamlapen.lib.lib.client.gui.screens.radialmenu`) vit dans
un package interne non public de Vampirism (pas son `api`) : aucun code n'en
a ete copie ni reutilise, uniquement la lecture de son fonctionnement general
avant d'ecrire une version independante avec l'API cliente NeoForge standard
(`Screen`, `GuiGraphics`, `KeyMapping`).
