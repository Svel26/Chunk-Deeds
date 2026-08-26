# Changelog

All notable changes to **Chunk Deeds** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0-beta.1] - 2026-08-26

### Added
- Initial release of Chunk Deeds for Minecraft 1.21.1 (Fabric).
- Craftable Claim Deeds (Paper + Diamonds) granting +1 claim chunk per diamond (up to 8).
- Craftable Force Load Deeds (Paper + Netherite Scrap) granting +1 force load chunk per scrap (up to 8).
- Deed upgrading system: Add more modifier items to an existing deed in crafting grids.
- Dynamic JEI (Just Enough Items) integration: displays all configured deed recipes, upgrading combinations, and usage information.
- FTB Chunks synchronization and data updates.
- Sound effects, particle bursts, and chat notifications on redemption.
- Full JSON configuration support at `config/chunkdeeds.json`.
- Commands: `/chunkdeeds reload`, `/chunkdeeds give`, and `/chunkdeeds info`.
- GitHub Actions CI and automated multi-platform release workflows.
