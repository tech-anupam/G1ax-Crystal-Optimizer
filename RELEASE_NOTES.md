# 🔮 G1ax Crystal Optimizer Release Notes

## 🛠️ Version 1.0.6

### Bug Fixes

#### Mixin Startup Crash — `JAVA_25` Compatibility Level (mc-26.2)
- Fixed a fatal startup crash on the mc-26.2 build: `IllegalArgumentException: The requested compatibility level JAVA_25 could not be set. Level is not supported by the active JRE or ASM version`.
- Root cause: The mixin config for the mc-26.2 version declared `"compatibilityLevel": "JAVA_25"`, but the ASM version bundled with Fabric Loader (ASM 9.x) does not recognize `JAVA_25` as a valid level, causing Mixin initialization to abort before the game loads.
- Fix: Downgraded the mixin compatibility level to `JAVA_21`. No Java 25-specific bytecode features are used in mixin classes, so `JAVA_21` is fully sufficient. The mod still compiles and targets Java 25 bytecode for non-mixin classes.
- Reported by <@1017102539486928918> — thank you!

#### Edge Crystal Placement (Both Versions)
- Verified and confirmed the `findAdjacentBase` edge placement fix from v1.0.5 is active in both the root (1.21.11) and mc-26.2 builds.
- When your crosshair hits neighboring grass/ground on an edge flick, the optimizer detects adjacent obsidian/bedrock and executes the placement correctly.

### Improvements

#### Better Command Help
- `/g1axoptimizer` (no arguments) now displays a clean, detailed help screen showing:
  - Mod name and current version (v1.0.6)
  - Current active mode
  - All available modes with clear descriptions of what each one does
  - Discord support link
- Replaces the old single-line usage hint with a properly formatted multi-line help output.

---

## 🛠️ Version 1.0.5

### Bug Fixes

#### Crystal Placement on Block Edges (Default Mode)
- Fixed an issue where crystals could not be placed when targeting near the edge of an obsidian or bedrock block.
- Root cause: When aiming near the border or edge of obsidian, the crosshair raycast frequently hits an adjacent surface (such as grass, dirt, or cobblestone) or a side face, causing base validation to fail.
- Fix: Introduced adjacent base detection (`findAdjacentBase`). When an edge click lands on a non-base block, the optimizer checks immediate neighboring blocks for a valid obsidian/bedrock base and routes the interaction correctly.

#### Crystal Placement Under Blocks & Overhangs (Default Mode)
- Fixed an issue where crystals could not be placed under overhangs, ceilings, or structures with low vertical clearance.
- Root cause: The optimizer enforced a legacy 2-block air clearance check (`!isAir(above.up())`), rejecting valid placements whenever a block existed 2 blocks above the base.
- Fix: Relaxed the space check to match modern Minecraft mechanics by only checking entity collision obstructions, allowing smooth placement under ceilings, overhangs, and stacked block setups.

#### Stacked Obsidian Traversal Adjusted
- Only traversals on the top face (`Direction.UP`) walk up stacked obsidian, ensuring side-face hits target the intended block directly without jumping to unreachable heights.

### Improvements

#### Mode Toggle Feedback Moved to Action Bar
- `/g1axoptimizer default`, `tweak`, and `off` no longer dump lines of text into your chat.
- Mode changes now show a single clean confirmation above your hotbar (the action bar), which fades automatically — no chat clutter mid-fight.
  - ✔ **Default Mode** — Full optimizer enabled
  - ✔ **Tweak Mode** — AC-safe cooldown bypass enabled
  - ✗ **Optimizer Disabled** — Vanilla crystal behavior
- `/g1axoptimizer` with no arguments still prints the current mode and usage to chat since that's a deliberate status check.

---

## 🚀 Version 1.0.4


### New Features

#### Performance Enhancement
- Integrated **PerformanceGuard**: A nanosecond-precision, ping-adaptive rate limiter with independent budgets for placing and breaking to maximize PvP performance.
- Added **OptOutCache**: Multi-server session-persistent opt-out memory that retains server-requested disable states across logins (up to 10 servers).
- **Handshake Protocol Integration**:
  - `VersionPacket` (C2S): Announces current project version to server.
  - `ServerOptOutPacket` (S2C): Receives server instructions to disable the optimizer, displaying a clean hoverable notification in game.
  - `OptOutAckPacket` (C2S): Acknowledges server opt-out commands.

#### Modrinth Auto Update Checker
- Added `UpdateChecker.java` — a lightweight background update checker powered by the public Modrinth API (no API key required).
- On mod load, a daemon thread silently fetches the latest published version from `api.modrinth.com`.
- The first tick the player enters a world, if a newer version is available, a single beautifully formatted notification is displayed in chat with the download link and custom dev contact.
- Shows **once per session only** — no spam.
- If already on the latest version, completely silent.

### Bug Fixes

#### Command Output Colors Not Rendering
- Fixed all `§` color codes in chat messages being silently ignored.
- Root cause: `MutableText.formatted()` is non-mutating — the returned styled text was discarded, causing every color, bold, and italic code to fall back to plain white.
- All command output (`/g1axoptimizer default`, `tweak`, `off`, status) now renders with correct gold, green, red, yellow, and gray formatting.

#### Classloading Mismatch / Mixin Startup Crash (Minecraft 1.21)
- Resolved startup crash with `ClassMetadataNotFoundException: net.minecraft.class_1269$class_9859` when running on Minecraft 1.21.
- Root cause: `ActionResult` was refactored from an enum to a sealed interface in 1.21.2+. Compiling against 1.21.11 caused references to `ActionResult.PASS` to seek non-existent inner records.
- Solution: Introduced `ActionResultResolver.java` using reflection to dynamically retrieve `SUCCESS`, `CONSUME`, and `PASS` constants across all Minecraft 1.21–1.21.11 builds, resolving any compile-time bytecode dependency on the inner classes.

#### Incorrect Compatibility Warnings on Production/Obfuscated Clients
- Fixed startup log output displaying `Minecraft: unknown` and reporting `INCOMPATIBILITY: Mixin targets not found: MinecraftClient, EndCrystalItem, ClientConnection`.
- Root cause: `CompatibilityChecker` looked up named classes (`MinecraftClient`, etc.) which are obfuscated to intermediary names (`class_310`, etc.) in the production environment. Also, `SharedConstants` reflection failed due to obfuscated method names.
- Solution: Updated `CompatibilityChecker` to query both named and intermediary names for class presence, and integrated Fabric Loader's mod container version lookup for robust, obfuscation-safe Minecraft version detection.

#### NoSuchMethodError Registry Crash on Older Fabric API Versions
- Resolved startup crash throwing `NoSuchMethodError: 'net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S()'` when running on older Fabric API versions.
- Root cause: The mod registered packets directly via the `PayloadTypeRegistry` class APIs which did not exist or had different signatures on older Fabric API versions.
- Solution: Migrated all packet registrations to use reflection and dynamic method lookup (handling missing classes and method mismatches gracefully), allowing the mod to start up without crashes on all Fabric API versions.

#### NoClassDefFoundError HoverEvent$ShowText Crash on Minecraft 1.21/1.21.1
- Resolved startup crash throwing `java.lang.NoClassDefFoundError: net/minecraft/class_2568$class_10613` (HoverEvent$ShowText) when running on Minecraft 1.21/1.21.1.
- Root cause: Minecraft 1.21.2+ introduced `HoverEvent.ShowText` (obfuscated as `class_2568$class_10613`). Compiling against 1.21.11 introduced a direct bytecode reference to this class, which does not exist in Minecraft 1.21/1.21.1 client runtime environments.
- Solution: Created `HoverEventResolver.java` using dynamic reflection to detect class availability. It dynamically instantiates `HoverEvent.ShowText` on 1.21.2+ environments and falls back to passing `Text` directly on 1.21/1.21.1 environments, removing all static compile-time classloading references.


