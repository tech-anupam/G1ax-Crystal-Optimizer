<div align="center">

# G1ax Crystal Optimizer

![Mod Icon](src/main/resources/assets/g1axcrystaloptimizer/icon.png)

**Client-side crystal PvP optimization for Minecraft (Fabric)**

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/Xqnzyc08?color=00AF5C&label=Downloads&logo=modrinth&logoColor=white&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![Modrinth Version](https://img.shields.io/modrinth/v/Xqnzyc08?color=00AF5C&label=Latest&logo=modrinth&logoColor=white&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![License](https://img.shields.io/github/license/tech-anupam/G1ax-Crystal-Optimizer?color=EAB308&logo=opensourceinitiative&logoColor=white&style=for-the-badge)](LICENSE)
[![Discord](https://img.shields.io/discord/1234567890?color=5865F2&label=Discord&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/vF5bE4strk)

[Download](https://modrinth.com/mod/g1axcrystaloptimizer) · [Issues](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/issues) · [Discord](https://discord.gg/vF5bE4strk)

</div>

---

## Supported Versions

| Version | Minecraft | Java | Mappings | Build |
|---|---|---|---|---|
| [`mc-1.16.5`](versions/mc-1.16.5/) | 1.16.5 | 8+ | Yarn | [![mc-1.16.5](https://img.shields.io/badge/build-passing-brightgreen?logo=gradle&logoColor=white)](versions/mc-1.16.5/) |
| [`mc-26.2`](versions/mc-26.2/) | 26.1.2 / 26.2 | 25+ | Mojang | [![mc-26.2](https://img.shields.io/badge/build-passing-brightgreen?logo=gradle&logoColor=white)](versions/mc-26.2/) |

Each version directory is a standalone Gradle project with its own source tree.

---

## Modes

```
/g1axoptimizer <mode>
```

| Mode | Description |
|---|---|
| `default` | Full optimizer — fast placement, break prediction, vanilla placement rules |
| `tweak` | AC-safe — cooldown bypass only, vanilla code paths |
| `off` | Vanilla crystal behavior |

---

## Performance

<div align="center">

*70ms ping — slowed down for detail*

| With Mod | Without Mod |
|:---:|:---:|
| ![With](https://raw.githubusercontent.com/tech-anupam/G1ax-Crystal-Optimizer/main/media/with_mod.gif) | ![Without](https://raw.githubusercontent.com/tech-anupam/G1ax-Crystal-Optimizer/main/media/without_mod.gif) |

</div>

---

## Building

```bash
git clone https://github.com/tech-anupam/G1ax-Crystal-Optimizer.git

# mc-1.16.5
cd versions/mc-1.16.5 && .\gradlew build

# mc-26.2 (requires JDK 25+)
cd versions/mc-26.2 && .\gradlew build
```

Output JARs in `build/libs/`.

---

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md)

## License

[![MIT](https://img.shields.io/badge/License-MIT-EAB308?style=flat-square)](LICENSE)

<div align="center">

Made by the G1ax Team & [tech.anupam](https://modrinth.com/user/tech.anupam) · [Discord](https://discord.gg/vF5bE4strk)

</div>
