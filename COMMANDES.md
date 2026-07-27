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
| `/originel skill use <joueur> <competence>` | Declenche une competence active deja debloquee (Regard hypnotique, Morsure vampirique n'est pas activable - passive -, Brume, Griffes, Metamorphose, Commandement, Colere de l'Originel). Respecte le delai de recharge configure. |

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
| Originel | `aura_abomination` | Passive (effet implemente a l'etape 10) |
| Originel | `regeneration_impie` | Passive |
| Originel | `metamorphose` | Active |
| Originel | `commandement` | Active |
| Ultime (niveau max) | `colere_originel` | Active |

_(Liste completee au fur et a mesure de l'implementation des etapes suivantes :
scellement, lune rouge, rituel, composants, anneau de cendre.)_
