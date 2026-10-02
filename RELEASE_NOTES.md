# v1.0.7

### Fixes
- Fixed NoSuchFieldError on Minecraft 26.x by routing action bar and chat output directly through player messaging.
- Added offhand crystal placement support across all versions (Root 1.21.x, mc-1.16.5, mc-26.2).
- Fixed dead and despawning entities blocking crystal placement in space checks.
- Enforced top-face (Direction.UP) obsidian and bedrock placement only in default mode.
- Reset attack cooldown in default mode across all versions to eliminate miss penalties.
- Removed unused template mixins and synchronized logic across all version targets.
