# Contributing

## Repo Layout

```
versions/
├── mc-1.16.5/   ← Yarn, Java 8
└── mc-26.2/     ← Mojang, Java 25
```

---

## Add a New MC Version

```bash
cp -r versions/mc-26.2 versions/mc-NEW
```

Edit `gradle.properties` → update `minecraft_version`, `loader_version`, `fabric_version`.
Fix imports & API changes → build → test.

---

## What Breaks Between Versions

| Area | Files to fix |
|---|---|
| Class renames | All `import` statements |
| Method signatures | `CrystalOptimizer.java`, mixins |
| ActionResult type | `ActionResultResolver.java` |
| Chat/HUD API | `OptimizerCommand.java` |
| Packet structure | `ClientConnectionMixin.java`, `packets/` |
| Command API | `OptimizerCommand.java` |
| Entity removal | `CrystalOptimizer.java` |

---

## Where to Add Optimizations

| Optimization | File |
|---|---|
| Placement logic | `CrystalOptimizer.java` |
| Break prediction | `CrystalOptimizer.java` + `ClientConnectionMixin.java` |
| Cooldown bypass | `MinecraftClientMixin.java` |
| New mode | `OptimizerCommand.java` + `CrystalOptimizer.java` |

---

## Rules

- **Mode-gate everything** — check `OptimizerCommand.defaultMode` / `tweakMode`
- **Default mode = vanilla placement** — top face of obsidian/bedrock only
- **Rate limit** — use `PerformanceGuard` for packet throttling
- **Catch exceptions** — never crash the client
- **Update all versions** — same logic in every version directory

---

## Before Submitting

```bash
cd versions/mc-1.16.5 && .\gradlew build
cd ../mc-26.2 && .\gradlew build
```

Test all 3 modes: `default`, `tweak`, `off`.

---

[Discord](https://discord.gg/vF5bE4strk) · [Issues](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/issues)
