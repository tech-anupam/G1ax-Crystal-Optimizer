# 🔮 G1ax Crystal Optimizer — Minecraft 26.2

<div align="center">

![Mod Icon](src/main/resources/assets/g1axcrystaloptimizer/icon.png)

**Crystal PvP optimization for Minecraft 26.1.2 & 26.2 (Fabric)**

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/Xqnzyc08?color=00AF5C&label=Downloads&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)
[![Discord](https://img.shields.io/badge/Discord-Join-5865F2.svg?style=for-the-badge)](https://discord.gg/vF5bE4strk)

[Download](https://modrinth.com/mod/g1axcrystaloptimizer) · [Issues](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/issues) · [Discord](https://discord.gg/vF5bE4strk)

</div>

---

## Version Info

| | |
|---|---|
| **Minecraft** | 26.1.2 & 26.2 |
| **Java** | 25+ |
| **Mappings** | Mojang (official) |
| **Fabric Loader** | 0.19.3 |
| **Fabric API** | 0.154.2+26.2 |
| **Mod Version** | 1.0.6 |

---

## Modes

Use `/g1axoptimizer <mode>` in-game:

| Mode | What it does |
|---|---|
| `default` | Full optimizer — fast placement, break prediction, vanilla placement rules |
| `tweak` | AC-safe — cooldown bypass only, 100% vanilla code paths |
| `off` | Disabled — vanilla crystal behavior |

### Feature Matrix

| Feature | `tweak` | `default` | `off` |
|---|:---:|:---:|:---:|
| Bypass `itemUseCooldown` | ✅ | ✅ | ❌ |
| Visual break prediction | ❌ | ✅ | ❌ |
| Direct packet routing | ❌ | ✅ | ❌ |
| Ping-adaptive rates | ❌ | ✅ | ❌ |
| Vanilla placement rules | ✅ | ✅ | ✅ |

---

## 26.2 Migration Notes

Key API changes from older Minecraft versions:
- **Record packets** — `ServerboundInteractPacket` is now a Java record; attack detection uses `hand() == null`
- **Fabric registry** — `playS2C()`/`playC2S()` → `clientboundPlay()`/`serverboundPlay()`
- **Chat HUD** — `mc.gui.hud.getChat()` path + `addClientSystemMessage(Component)`

---

## Building

Requires **JDK 25+**.

```bash
cd versions/mc-26.2
.\gradlew build
```

Output JAR → `build/libs/`

---

## Project Structure

```
src/main/java/dev/akatriggered/
├── Main.java                     — Mod init & logger
├── cache/OptOutCache.java        — Per-server opt-out
├── command/OptimizerCommand.java — In-game mode switching
├── handler/InteractHandler.java  — (deprecated)
├── listener/
│   ├── ConnectEventListener.java
│   ├── DisconnectEventListener.java
│   └── OptOutPacketListener.java
├── mixin/
│   ├── MinecraftClientAccessor.java
│   ├── MinecraftClientMixin.java
│   ├── EndCrystalItemMixin.java
│   └── ClientConnectionMixin.java
├── optimizer/CrystalOptimizer.java — Core placement & break engine
├── packets/
│   ├── OptOutAckPacket.java
│   ├── OptOutPacket.java
│   ├── ServerOptOutPacket.java
│   └── VersionPacket.java
└── util/
    ├── ActionResultResolver.java
    ├── CompatibilityChecker.java
    ├── ConnectionUtil.java
    ├── HoverEventResolver.java
    ├── Logger.java
    ├── PerformanceGuard.java
    ├── VersionUtil.java
    └── datastructure/EvictingList.java
```

---

## Contributing

See [CONTRIBUTING.md](../../CONTRIBUTING.md) for porting, downgrading, or adding new optimizations.

## License

MIT — see [LICENSE](LICENSE).

<div align="center">

Made with ❤️ by the G1ax Team & [tech.anupam](https://modrinth.com/user/tech.anupam)

</div>
