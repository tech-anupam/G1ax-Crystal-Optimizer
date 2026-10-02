<div align="center">

# G1ax Crystal Optimizer

![Mod Icon](src/main/resources/assets/g1axcrystaloptimizer/icon.png)

**Client-side crystal PvP optimization for Minecraft (Fabric)**

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/Xqnzyc08?color=00AF5C&label=Downloads&logo=modrinth&logoColor=white&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![Modrinth Version](https://img.shields.io/modrinth/v/Xqnzyc08?color=00AF5C&label=Latest&logo=modrinth&logoColor=white&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![License](https://img.shields.io/github/license/tech-anupam/G1ax-Crystal-Optimizer?color=EAB308&logo=opensourceinitiative&logoColor=white&style=for-the-badge)](LICENSE)
[![Discord](https://img.shields.io/badge/Discord-Join-5865F2?logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/vF5bE4strk)

[Download](https://modrinth.com/mod/g1axcrystaloptimizer) · [Releases](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/releases) · [Issues](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/issues) · [Discord](https://discord.gg/vF5bE4strk)

</div>

---

## Supported Versions

| Version | Minecraft | Java | Mappings | Docs |
|---|---|---|---|---|
| [`mc-1.16.5`](versions/mc-1.16.5/) | 1.16.5 | 8+ | Yarn | [![README](https://img.shields.io/badge/docs-README-blue?style=flat-square)](versions/mc-1.16.5/README.md) |
| [`mc-26.2`](versions/mc-26.2/) | 26.1.2 / 26.2 | 25+ | Mojang | [![README](https://img.shields.io/badge/docs-README-blue?style=flat-square)](versions/mc-26.2/README.md) |

---

## Modes

```
/g1axoptimizer <mode>
```

| Mode | Description |
|---|---|
| `default` | Full optimizer: fast placement, break prediction, vanilla placement rules |
| `tweak` | AC-safe: cooldown bypass only, vanilla code paths |
| `off` | Vanilla crystal behavior |

---


## Comparison (90 ms/ping)

| Optimized | Vanilla |
| --- | --- |
| <img src="https://cdn.modrinth.com/data/Xqnzyc08/images/7767bacd211b1067e496cee460a32039e95a3b0f.gif" width="360"> | <img src="https://cdn.modrinth.com/data/Xqnzyc08/images/d9e6880b21ff5314f430337592de27b417fd1a0c.gif" width="360"> |

---

## Installation & Building

### Install (Recommended)
1. Download the JAR for your Minecraft version from [Modrinth](https://modrinth.com/mod/g1axcrystaloptimizer) or [Releases](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/releases).
2. Drag and drop the JAR into `.minecraft/mods/`.

### Build from Source
```bash
git clone https://github.com/tech-anupam/G1ax-Crystal-Optimizer.git

# mc-1.16.5
cd versions/mc-1.16.5 && .\gradlew build

# mc-26.2 (requires JDK 25+)
cd versions/mc-26.2 && .\gradlew build
```
Output JARs are in `build/libs/`.

---

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md)

## License

[![MIT](https://img.shields.io/badge/License-MIT-EAB308?style=flat-square)](LICENSE)

<div align="center">

Made by the G1ax Team & [tech.anupam](https://modrinth.com/user/tech.anupam) · [Discord](https://discord.gg/vF5bE4strk)

</div>
