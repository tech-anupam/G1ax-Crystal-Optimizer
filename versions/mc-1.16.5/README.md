<div align="center">

# G1ax Crystal Optimizer (mc-1.16.5)

![Mod Icon](src/main/resources/assets/g1axcrystaloptimizer/icon.png)

**Crystal PvP optimization for Minecraft 1.16.5 (Fabric)**

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/Xqnzyc08?color=00AF5C&label=Downloads&logo=modrinth&logoColor=white&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![License](https://img.shields.io/github/license/tech-anupam/G1ax-Crystal-Optimizer?color=EAB308&logo=opensourceinitiative&logoColor=white&style=for-the-badge)](LICENSE)
[![Discord](https://img.shields.io/badge/Discord-Join-5865F2?logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/vF5bE4strk)

[Download](https://modrinth.com/mod/g1axcrystaloptimizer) · [Releases](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/releases) · [Issues](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/issues) · [Discord](https://discord.gg/vF5bE4strk)

</div>

---

## Version Info

| Property | Value |
|---|---|
| **Minecraft** | 1.16.5 |
| **Java** | 8+ |
| **Mappings** | Yarn (`1.16.5+build.10`) |
| **Fabric Loader** | 0.16.10 |
| **Fabric API** | 0.42.0+1.16 |
| **Mod Version** | 1.0.7 |

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

## Installation & Building

### Install
Download from [Modrinth](https://modrinth.com/mod/g1axcrystaloptimizer) or [Releases](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/releases) and drag and drop into `.minecraft/mods/`.

### Build
```bash
cd versions/mc-1.16.5
.\gradlew build
```
Output JAR in `build/libs/`.

---

## Contributing

See [`CONTRIBUTING.md`](../../CONTRIBUTING.md)

## License

[![MIT](https://img.shields.io/badge/License-MIT-EAB308?style=flat-square)](LICENSE)
