# 🔮 G1axCrystalOptimizer

### High-Performance Crystal PvP Optimization for Minecraft (Fabric)

<div align="center">

![Mod Icon](src/main/resources/assets/g1axcrystaloptimizer/icon.png)

Developed by [**tech.anupam**](https://modrinth.com/user/tech.anupam) & the G1ax Team

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/Xqnzyc08?color=00AF5C&label=Modrinth%20Downloads&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![Modrinth Version](https://img.shields.io/modrinth/v/Xqnzyc08?color=00AF5C&label=Modrinth%20Version&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![Modrinth Followers](https://img.shields.io/modrinth/followers/Xqnzyc08?color=00AF5C&label=Followers&style=for-the-badge)](https://modrinth.com/mod/g1axcrystaloptimizer)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)
[![Discord](https://img.shields.io/badge/Discord-Join-5865F2.svg?style=for-the-badge)](https://discord.gg/vF5bE4strk)

[Download on Modrinth](https://modrinth.com/mod/g1axcrystaloptimizer) • [GitHub Repository](https://github.com/AkaTriggered/G1ax-Crystal-Optimizer) • [Report Issues](https://github.com/AkaTriggered/G1ax-Crystal-Optimizer/issues) • [Discord Support](https://discord.gg/vF5bE4strk)

</div>

---

## 🚀 Overview

**G1ax Crystal Optimizer** is a client-side Fabric performance mod designed to optimize crystal PvP gameplay. By bypassing client-side placement cooldowns, predicting crystal breaks, and managing packet traffic dynamically, it provides a highly responsive PvP experience. Running entirely client-side, it offers customizable modes to align with different server rules and anticheat configurations.

---

## 🤔 Why Choose G1ax Crystal Optimizer?

- ⚡ **Zero Visual Delay**: Instantly removes broken crystals visually on the client side, eliminating delay while waiting for server verification.
- 📶 **Dynamic Latency Adaptation**: Evaluates real-time connection latency using a moving average and automatically adjusts packet rates (2–4 packets per tick).
- 🛡️ **Anticheat Compliance**: Provides a lightweight **tweak mode** that bypasses hardcoded cooldowns while retaining vanilla validation paths.
- 🔧 **Startup Safeguards**: Built-in environment scanner checks Minecraft versions, Fabric API, and mixin targets to prevent client crashes.
- 🤝 **Seamless Coexistence**: Fully compatible with other PvP helper mods without thread contention or rendering crashes.

---

## ⚡ Mode Configuration

Configure the mod's behavior dynamically in-game with `/g1axoptimizer <default|tweak|off>`:

### 🛡️ `/g1axoptimizer tweak` — AC-Safe Mode
Specifically designed for competitive environments with strict server anticheat solutions.
- **Bypasses Placement Cooldown**: Resets Minecraft's hardcoded `itemUseCooldown` (4 ticks/200ms) to trigger placement checks every tick (~50ms).
- **100% Vanilla Code Paths**: Uses the default client logic and placing mechanics. No custom packet injection, no predictive client-side entity removals, and no structural modifications.

---

### 🚀 `/g1axoptimizer default` — Full Performance Mode
Optimized for anarchy and PvP servers where custom optimization mods are permitted.
- **Client-Side Visual Removal**: Instantly removes broken crystals visually, providing zero-delay feedback without waiting for server response packets.
- **Direct Block Interaction**: Bypasses default validation paths to send direct block interaction requests.
- **Performance Guard**: Dynamically schedules packet rates and predictions using a nanosecond-precision adaptive EMA guard.

---

### ❌ `/g1axoptimizer off` — Vanilla Behavior
Disables all optimizations. Restores the default Minecraft PvP engine.

| Feature | `tweak` Mode | `default` Mode | `off` (Vanilla) |
|---|:---:|:---:|:---:|
| **Bypass `itemUseCooldown`** | Every Tick (~50ms) | Every Tick (~50ms) | Vanilla (4 Ticks / 200ms) |
| **Visual Client-Side Break Prediction** | ❌ | ✅ | ❌ |
| **Direct Packet Routing** | ❌ | ✅ | ❌ |
| **Ping-Adaptive Packet Rates** | ❌ | ✅ | ❌ |
| **Strict Vanilla Validation Path** | ✅ | ❌ | ✅ |

---

## 📊 Performance Comparison (70ms)

*slowed down to show more detail*

| With Mod | Without Mod |
|:---:|:---:|
| ![With Mod](https://raw.githubusercontent.com/AkaTriggered/G1ax-Crystal-Optimizer/main/media/with_mod.gif) | ![Without Mod](https://raw.githubusercontent.com/AkaTriggered/G1ax-Crystal-Optimizer/main/media/without_mod.gif) |

---

## 🔧 Diagnostics & Compatibility Engine

To guarantee stable execution across versions and mods, G1ax Crystal Optimizer includes an autonomous diagnostics system:

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
├── cache/
│   └── OptOutCache.java             (Per-server opt-out status retention)
├── command/
│   └── OptimizerCommand.java        (Dynamic in-game mode selections and instructions)
├── listener/
│   ├── ConnectEventListener.java    (Handles server connect handshake)
│   ├── DisconnectEventListener.java (Resets session flags on disconnect)
│   └── OptOutPacketListener.java    (Processes server-requested opt-out packets)
├── mixin/
│   ├── MinecraftClientAccessor.java (Exposes native item cooldown properties)
│   ├── MinecraftClientMixin.java    (Orchestrates tick hooks and mode routing)
│   └── ClientConnectionMixin.java   (Intercepts outgoing packets for visual removal)
├── optimizer/
│   └── CrystalOptimizer.java        (Core engine managing packet rates and pings)
├── packets/
│   ├── OptOutAckPacket.java         (Client acknowledgment packet)
│   ├── OptOutPacket.java            (Optional client announcement payload)
│   ├── ServerOptOutPacket.java      (S2C server-requested disable packet)
│   └── VersionPacket.java           (C2S client version payload)
└── util/
    ├── ConnectionUtil.java          (Generates server identification keys)
    ├── PerformanceGuard.java        (Nanosecond-precision adaptive scheduler)
    ├── VersionUtil.java             (Parses project version data)
    ├── Logger.java                  (Autonomous formatted file logger)
    ├── CompatibilityChecker.java    (Startup diagnostics check)
    └── datastructure/
        └── EvictingList.java        (Fixed-size eviction list structure)
```

---

## 🔨 Building from Source

```bash
git clone https://github.com/AkaTriggered/G1ax-Crystal-Optimizer.git
cd G1ax-Crystal-Optimizer
.\gradlew build
```
The compiled mod JAR will be output in `build/libs/`.

---

## 📄 License

Distributed under the MIT License. See [LICENSE](LICENSE) for details.

---

<div align="center">

Made with ❤️ by the G1ax Team & AkaTriggered • [Join Discord](https://discord.gg/vF5bE4strk)

</div>
