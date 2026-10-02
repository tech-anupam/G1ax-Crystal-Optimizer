# 🔮 G1ax Crystal Optimizer

<div align="center">

![Mod Icon](src/main/resources/assets/g1axcrystaloptimizer/icon.png)

**High-performance crystal PvP optimization for Minecraft (Fabric)**

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/Xqnzyc08?color=00AF5C&label=Downloads&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![Modrinth Version](https://img.shields.io/modrinth/v/Xqnzyc08?color=00AF5C&label=Version&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)
[![Discord](https://img.shields.io/badge/Discord-Join-5865F2.svg?style=for-the-badge)](https://discord.gg/vF5bE4strk)

[Download](https://modrinth.com/mod/g1axcrystaloptimizer) · [Issues](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/issues) · [Discord](https://discord.gg/vF5bE4strk)

</div>

---

## What it does

Client-side Fabric mod that makes crystal PvP faster and smoother:
- ⚡ Bypasses client-side placement cooldowns
- 👁️ Instant visual crystal break prediction
- 📶 Ping-adaptive packet rates
- 🛡️ Multiple modes for different anticheat setups

---

## Supported Versions

| Directory | Minecraft | Java | Mappings |
|---|---|---|---|
| [`versions/mc-1.16.5/`](versions/mc-1.16.5/) | 1.16.5 | Java 8+ | Yarn |
| [`versions/mc-26.2/`](versions/mc-26.2/) | 26.1.2 & 26.2 | Java 25+ | Mojang |

Each version directory is a self-contained Gradle project with its own `build.gradle`, `README.md`, and source tree.

---

## Modes

Use `/g1axoptimizer <mode>` in-game:

| Mode | What it does |
|---|---|
| `default` | Full optimizer — fast placement, break prediction, vanilla placement rules |
| `tweak` | AC-safe — cooldown bypass only, 100% vanilla code paths |
| `off` | Disabled — vanilla crystal behavior |

---

## Performance

*70ms ping, slowed down:*

| With Mod | Without Mod |
|:---:|:---:|
| ![With](https://raw.githubusercontent.com/tech-anupam/G1ax-Crystal-Optimizer/main/media/with_mod.gif) | ![Without](https://raw.githubusercontent.com/tech-anupam/G1ax-Crystal-Optimizer/main/media/without_mod.gif) |

---

## Building

```bash
# Clone
git clone https://github.com/tech-anupam/G1ax-Crystal-Optimizer.git
cd G1ax-Crystal-Optimizer

# Build mc-1.16.5
cd versions/mc-1.16.5
.\gradlew build

# Build mc-26.2 (requires JDK 25+)
cd ../mc-26.2
.\gradlew build
```

Output JARs go to `build/libs/` inside each version directory.

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on porting to new versions, downgrading, or adding optimizations.

---

## License

MIT — see [LICENSE](LICENSE).

<div align="center">

Made with ❤️ by the G1ax Team & [tech.anupam](https://modrinth.com/user/tech.anupam) · [Discord](https://discord.gg/vF5bE4strk)

</div>
