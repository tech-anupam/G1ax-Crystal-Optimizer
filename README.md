# 🔮 G1ax Crystal Optimizer

<div align="center">

![Mod Icon](src/main/resources/assets/g1axcrystaloptimizer/icon.png)

### **High-Performance Crystal PvP Optimization for Minecraft (Fabric)**

Developed by [**tech.anupam**](https://modrinth.com/user/tech.anupam)

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/Xqnzyc08?color=00AF5C&label=Modrinth%20Downloads&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![Modrinth Version](https://img.shields.io/modrinth/v/Xqnzyc08?color=00AF5C&label=Modrinth%20Version&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![Modrinth Followers](https://img.shields.io/modrinth/followers/Xqnzyc08?color=00AF5C&label=Followers&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)
[![Discord](https://img.shields.io/badge/Discord-Join-5865F2.svg?style=for-the-badge)](https://discord.gg/Dcmmg3x7M7)

[Download on Modrinth](https://modrinth.com/mod/g1axcrystaloptimizer) • [GitHub Repository](https://github.com/tech-anupam/G1ax-Crystal-Optimizer) • [Report Issues](https://github.com/tech-anupam/G1ax-Crystal-Optimizer/issues) • [Discord Support](https://discord.gg/Dcmmg3x7M7)

</div>

---

## 🚀 Overview

**G1ax Crystal Optimizer** is a high-performance Fabric client mod designed to optimize crystal PvP gameplay. By bypassing client-side placement cooldowns and managing packet traffic intelligently, it provides a seamless and responsive PvP experience. Running entirely client-side, it offers customizable modes to align with different server rules and anticheat configurations.

---

## ⚡ Mode Configuration

Adjust the mod's behavior dynamically in-game with simple command triggers:

### `/g1axoptimizer tweak` — AC-Safe Mode
Specifically designed for competitive environments with strict server anticheat solutions.
- **Bypasses 4-tick Placement Cooldown**: Overrides Minecraft's hardcoded `itemUseCooldown` (4 ticks/200ms) to trigger placement checks every tick (~50ms).
- **100% Vanilla Code Paths**: Uses the default client logic and placing mechanics. No custom packet injection, no predictive client-side entity removals, and no structural modifications. 

| Feature | `Tweak` Mode | `Default` Mode |
|---|:---:|:---:|
| Bypass `itemUseCooldown` (every tick) | ✅ | ✅ |
| Visual client-side crystal removal | ❌ | ✅ |
| Custom `interactBlock` packets | ❌ | ✅ |
| Ping-adaptive rate limits | ❌ | ✅ |
| Strict vanilla validation path | ✅ | ❌ |

---

### `/g1axoptimizer default` — Full Performance Mode
Optimized for anarchy and PvP servers where custom optimization mods are permitted.
- **Client-Side Visual Removal**: Instantly removes broken crystals visually on the client side, eliminating delays waiting for server packet confirmation.
- **Direct Block Interaction Packets**: Bypasses slow vanilla check sequences to send direct interaction requests.
- **Ping-Adaptive Rate Limiter**: Dynamically adjusts placement packet rate (sending 2–4 packets per tick) based on your real-time server latency.

---

### `/g1axoptimizer off` — Vanilla Behavior
Disables all modifications. Restores the game's default PvP engine and behavior.

---

## 🔧 Diagnostics & Compatibility Engine

To guarantee stable execution across versions and mods, G1ax Crystal Optimizer includes an autonomous pre-flight diagnostics system:

### 1. Pre-Flight Compatibility Verification (`CompatibilityChecker.java`)
On startup, the mod verifies environmental components:
- **Minecraft Version Verification**: Warns if running on unverified versions.
- **Fabric API Presence**: Checks for critical runtime APIs and versions.
- **Network Payload Registry**: Resolves registration capabilities.
- **Mixin Target Integrations**: Verifies target classes exist to prevent startup crashes.
- **Java Runtime Check**: Ensures Java 21+ is driving the client.

> [!NOTE]
> If any critical incompatibilities are detected, the mod logs the exact issue alongside step-by-step fix instructions and raises a warning in chat.

### 2. Live Custom Logging System (`Logger.java`)
A clean, specialized log file is output to:
```
.minecraft/logs/g1axoptimizer-latest.log
```
- Logs are formatted using a clean, human-readable structure: `[HH:mm:ss] [G1ax/LEVEL] Message`.
- Supports automated log rotation, keeping the last 3 logs (`g1axoptimizer-1.log`, etc.) to save disk space while preserving history.

---

## 🛠️ Resolved Issues & Fixes

### 🐛 NoSuchMethodError Crash (Fixed)
- **Problem**: Manual trigonometric vectors caused method-not-found exceptions across minor Minecraft releases.
- **Solution**: Migrated to Minecraft's built-in `getRotationVec()` API, ensuring 100% stability.

### 🛡️ Mod Coexistence & Stability (Fixed)
- **Problem**: Mixing multiple PvP helper mods caused thread contentions and crashes.
- **Solution**: Added robust error catch boundaries and thread-safe boundaries for async operations. Works seamlessly alongside:
  - Client Side Crystals
  - Crystal Anchor Counter
  - Marlow's Crystal Optimizer
  - Safe Crystals
  - Knockback Optimizer

---

## 📦 Project Architecture

```
src/main/java/dev/akatriggered/
├── Main.java                        (Mod initialization and logger setup)
├── command/
│   └── OptimizerCommand.java        (Dynamic in-game mode selections and instructions)
├── handler/
│   └── InteractHandler.java        (Stub for historical compatibility hooks)
├── mixin/
│   ├── MinecraftClientAccessor.java (Exposes native item cooldown properties)
│   ├── MinecraftClientMixin.java    (Orchestrates tick hooks and mode routing)
│   ├── EndCrystalItemMixin.java     (Implements fast placement packet overrides)
│   └── ClientConnectionMixin.java   (Intercepts outgoing packets for visual removal)
├── optimizer/
│   └── CrystalOptimizer.java        (Core engine managing packet rates and pings)
├── packets/
│   └── OptOutPacket.java            (Client opt-out network signaling payload)
└── util/
    ├── Logger.java                  (Autonomous formatted file logger)
    └── CompatibilityChecker.java    (Startup diagnostics system with fix instructions)
```

---

## 🔨 Building from Source

```bash
git clone https://github.com/tech-anupam/G1ax-Crystal-Optimizer.git
cd G1ax-Crystal-Optimizer
.\gradlew.bat build
```
The compiled mod JAR will be output in `build/libs/`.

---

## 📄 License

Distributed under the MIT License. See [LICENSE](LICENSE) for details.

---

<div align="center">

Made with ❤️ by [**tech.anupam**](https://modrinth.com/user/tech.anupam) & the G1ax Team • [Join Discord](https://discord.gg/Dcmmg3x7M7)

</div>
