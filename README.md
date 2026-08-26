# Chunk Deeds

[![CI](https://github.com/CineCraft/Chunk-Deeds/actions/workflows/ci.yml/badge.svg)](https://github.com/CineCraft/Chunk-Deeds/actions/workflows/ci.yml)
[![Fabric 1.21.1](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg)](https://fabricmc.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Chunk Deeds** is a modern Minecraft Fabric mod that introduces craftable, dynamic deeds allowing players to expand their **FTB Chunks** claim limit and force-loaded chunk limit!

---

## Features

- 📜 **Claim Deeds**: Craft with Paper + Diamonds. 1 Diamond = 1 Extra Chunk Claim (up to 8 in a 3x3 crafting grid).
- ⚡ **Force Load Deeds**: Craft with Paper + Netherite Scraps. 1 Netherite Scrap = 1 Extra Force Loaded Chunk (up to 8 in a 3x3 crafting grid).
- 🔄 **Deed Upgrades**: Combine an existing Deed with additional modifier items in the crafting grid to upgrade its value!
- 🔍 **JEI Integration**: Full Just Enough Items recipe viewing and in-game information for all configured deed crafting & upgrading recipes.
- 🖱️ **Instant Redemption**: Right-click a deed in hand to redeem it. Plays immersive sound and particle effects and updates FTB Chunks live.
- ⚙️ **Fully Configurable**: Easily change recipes, base items, modifier items, chunks granted per item, and maximum limits via `config/chunkdeeds.json`.
- 🛠️ **Admin Commands**:
  - `/chunkdeeds reload` — Hot-reload the configuration file in-game.
  - `/chunkdeeds give <player> <claim|forceload> <amount> [count]` — Give custom deeds to players.
  - `/chunkdeeds info [player]` — View a player's current FTB extra chunk claims and force loads.

---

## Crafting Recipes (Default)

| Deed Type | Base Item | Modifier Item | Value Formula |
| :--- | :--- | :--- | :--- |
| **Claim Deed** | Paper (1) | Diamond (1 to 8) | +1 Claim Chunk per Diamond |
| **Force Load Deed** | Paper (1) | Netherite Scrap (1 to 8) | +1 Force Load Chunk per Scrap |

*Deeds can be crafted in 2x2 player crafting grids (up to 3 items) or 3x3 Crafting Tables (up to 8 items).*

---

## Configuration

The config file is located at `config/chunkdeeds.json`:

```json
{
  "claimDeed": {
    "enabled": true,
    "baseItemId": "minecraft:paper",
    "modifierItemId": "minecraft:diamond",
    "chunksPerItem": 1,
    "maxDeedValue": 8,
    "maxTotalExtraClaims": -1
  },
  "forceLoadDeed": {
    "enabled": true,
    "baseItemId": "minecraft:paper",
    "modifierItemId": "minecraft:netherite_scrap",
    "chunksPerItem": 1,
    "maxDeedValue": 8,
    "maxTotalExtraForceLoads": -1
  },
  "general": {
    "playSounds": true,
    "spawnParticles": true,
    "allowDeedUpgrades": true,
    "consumeInCreative": false,
    "logRedemptions": true
  }
}
```

---

## Requirements

- **Minecraft**: `1.21.1`
- **Fabric Loader**: `0.15.0+`
- **Fabric API**
- **FTB Chunks** & **FTB Teams** / **FTB Library**

---

## Building from Source

```bash
git clone https://github.com/CineCraft/Chunk-Deeds.git
cd Chunk-Deeds
./gradlew clean build
```

---

## License

This project is licensed under the [MIT License](LICENSE).
