# More Leather

A Fabric mod that makes leather more accessible and useful by renaming/reskinning rabbit hide into "Leather Scraps," adding a decorative Block of Leather, and adding a wide set of data-driven recipes for turning leather items into scraps and back.

## Features

### Renamed Item
- **Rabbit Hide** → **Leather Scraps** (name and texture overridden for Pandorical clients; see below)
- The vanilla recipe still works: 4 Leather Scraps = 1 Leather

### Block of Leather
- Craft 9 leather into a decorative **Block of Leather** (leather roll)
- Directional placement like logs
- Decompresses back to 9 leather

### Deconstruction Recipes
Break down leather items into scraps (shapeless crafting):

| Item | → Scraps |
|------|----------|
| Leather Helmet | 15 |
| Leather Chestplate | 24 |
| Leather Leggings | 21 |
| Leather Boots | 12 |
| Leather Horse Armor | 21 |
| Leather | 4 |
| Book | 3 |
| Item Frame | 3 |
| Glow Item Frame | 3 |
| Book and Quill | 3 |
| Written Book | 3 |
| Bundle | 3 |
| Saddle | 9 |
| Harness (all colors) | 9 |

### New Crafting Recipes
- **Saddle**: 3 leather + 2 string + 2 iron ingots
- **Leather Horse Armor**: 7 leather

### Smelting Recipes
- **Rotten Flesh** → Leather Scraps (furnace or smoker)

### Animal/Mob Leather Drops (temporarily unavailable)
The mod is designed to also make animals and undead mobs drop leather and/or leather scraps on death (including bonus scraps for leather armor worn by armor-capable mobs) and to add leather scraps to fishing junk loot. **This part of the mod is currently disabled**: it depends on `fabric-loot-api-v3`, which has no published build for this Minecraft version upstream yet. The rest of the mod (the block, item rename, and all recipes above) is unaffected and works normally. This feature is intended to come back once upstream support lands.

## Learning It

Rotten flesh cooks down into hide. That is the best idea here and it is invisible: a furnace gives no hint what it will accept, and the thing every player has too much of is the last thing anyone would think to put in one.

With [village-quests](https://github.com/fatlard1993/village-quests) installed, a leatherworker or shepherd asks for a full block of leather and says the trick out loud. Nine leather is past the point where "there is another way to get this" stops being trivia.

Optional and guarded: without village-quests the mod behaves exactly as before.

## Pandorical

More Leather registers the Block of Leather's block/item models and renames + reskins vanilla rabbit hide into "Leather Scraps" through Pandorical's content sync, including `overrideVanillaItem` for the rabbit hide rename.

**The Pandorical mod must be installed client-side** to see the Block of Leather rendered and the "Leather Scraps" name/texture on rabbit hide. Without it, the underlying items and recipes still work, but a connecting client sees vanilla names/textures (e.g. "Rabbit Hide" instead of "Leather Scraps") and the Block of Leather may not render correctly.

## Installation

Install server-side alongside its declared dependencies (see `fabric.mod.json`); connecting clients need only Pandorical. Version targets live in `gradle.properties` (Minecraft, loader, Fabric API) and `fabric.mod.json` (Java).

## License

MIT, see [LICENSE](LICENSE).
