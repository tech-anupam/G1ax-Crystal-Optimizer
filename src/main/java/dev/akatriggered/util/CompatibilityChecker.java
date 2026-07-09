package dev.akatriggered.util;

import dev.akatriggered.command.OptimizerCommand;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CompatibilityChecker {

    private static final String DISCORD = "discord.gg/vF5bE4strk";
    private static final String ISSUES = "github.com/tech-anupam/G1ax-Crystal-Optimizer/issues";

    private static final List<String> TESTED_VERSIONS = List.of(
        "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4",
        "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9",
        "1.21.10", "1.21.11", "26.1.0", "26.1.1", "26.1.2"
    );

    public record CheckResult(boolean ok, String issue, String[] fixSteps) {}

    public static void runAll(Logger log) {
        String mcVer = getMcVersion();

        List<CheckResult> results = new ArrayList<>();
        results.add(checkMcVersion(mcVer));
        results.add(checkFabricApi());
        results.add(checkPayloadRegistry());
        results.add(checkMixinTargets());
        results.add(checkJavaVersion());

        boolean anyFailed = false;
        for (CheckResult r : results) {
            if (!r.ok()) {
                anyFailed = true;
                reportFailure(log, r);
            }
        }

        if (!anyFailed) {
            log.compat("All compatibility checks passed (" + mcVer + ")");
        } else {
            log.compat("Some checks failed — see above for fix guides");
            log.compat("Support: " + DISCORD + " | Issues: " + ISSUES);
            scheduleInGameMessage(
                "§8[§6G1ax§8] §cCompatibility issues detected! Check §b.minecraft/logs/g1axoptimizer-latest.log §cfor fix guide."
            );
        }
    }

    private static CheckResult checkMcVersion(String version) {
        boolean known = TESTED_VERSIONS.contains(version);
        if (known) return new CheckResult(true, null, null);

        return new CheckResult(false,
            "Untested Minecraft version: " + version,
            new String[]{
                "This mod is tested on: " + String.join(", ", TESTED_VERSIONS),
                "Your version (" + version + ") may work but is not guaranteed",
                "HOW TO FIX:",
                "  1. Check " + ISSUES + " for your version",
                "  2. If not reported, open a new issue with this log file",
                "  3. Join " + DISCORD + " for faster support",
                "  4. Try downgrading to 1.21.11 which is fully tested"
            }
        );
    }

    private static CheckResult checkFabricApi() {
        try {
            Class.forName("net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback");
            return new CheckResult(true, null, null);
        } catch (ClassNotFoundException e) {
            return new CheckResult(false,
                "Fabric API not found or wrong version",
                new String[]{
                    "HOW TO FIX:",
                    "  1. Download Fabric API from modrinth.com/mod/fabric-api",
                    "  2. Make sure you downloaded Fabric API for YOUR Minecraft version",
                    "  3. Place the Fabric API JAR in your mods folder",
                    "  4. Do NOT confuse Fabric Loader with Fabric API — both are needed",
                    "  Support: " + DISCORD
                }
            );
        }
    }

    private static CheckResult checkPayloadRegistry() {
        try {
            Class<?> reg = Class.forName("net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry");
            Method m = reg.getMethod("playC2S");
            m.invoke(null);
            return new CheckResult(true, null, null);
        } catch (NoSuchMethodException e) {
            return new CheckResult(false,
                "PayloadTypeRegistry.playC2S() not found — Fabric API too old or changed API",
                new String[]{
                    "HOW TO FIX:",
                    "  This mod's OptOut packet cannot register (non-fatal, mod still works)",
                    "  1. Update Fabric API to the latest version for your MC version",
                    "  2. Download from: modrinth.com/mod/fabric-api",
                    "  3. If issue persists after updating, report at: " + ISSUES,
                    "  Note: The core crystal optimizer still works without this packet",
                    "  Support: " + DISCORD
                }
            );
        } catch (Exception e) {
            return new CheckResult(true, null, null);
        }
    }

    private static CheckResult checkMixinTargets() {
        boolean clientOk = classExists("net.minecraft.client.MinecraftClient") || classExists("net.minecraft.class_310");
        boolean crystalOk = classExists("net.minecraft.item.EndCrystalItem") || classExists("net.minecraft.class_1774");
        boolean connOk = classExists("net.minecraft.network.ClientConnection") || classExists("net.minecraft.class_2535");

        if (clientOk && crystalOk && connOk) return new CheckResult(true, null, null);

        List<String> missing = new ArrayList<>();
        if (!clientOk) missing.add("MinecraftClient");
        if (!crystalOk) missing.add("EndCrystalItem");
        if (!connOk) missing.add("ClientConnection");

        return new CheckResult(false,
            "Mixin targets not found: " + String.join(", ", missing),
            new String[]{
                "WHY THIS HAPPENS:",
                "  The mod was built for a different MC version than you're running",
                "  Named classes can't be found because mappings don't match",
                "HOW TO FIX:",
                "  1. Download the correct version of this mod for MC " + getMcVersion(),
                "  2. Check releases at: github.com/AkaTriggered/G1ax-Crystal-Optimizer/releases",
                "  3. If no release exists for your version, report it at: " + ISSUES,
                "  4. Temporary: the mod will run in degraded mode (no mixins, optimizer disabled)",
                "  Support: " + DISCORD
            }
        );
    }

    private static CheckResult checkJavaVersion() {
        int javaVer = Runtime.version().feature();
        if (javaVer >= 21) return new CheckResult(true, null, null);

        return new CheckResult(false,
            "Java " + javaVer + " detected — Java 21+ required",
            new String[]{
                "HOW TO FIX:",
                "  1. Your launcher must use Java 21 or newer",
                "  2. For Prism/Modrinth launcher: Instance Settings → Java → Auto-detect",
                "  3. For official launcher: Installations → Edit → More Options → Java executable",
                "  4. Download Java 21 from: adoptium.net",
                "  Support: " + DISCORD
            }
        );
    }

    private static void reportFailure(Logger log, CheckResult r) {
        log.compat("INCOMPATIBILITY: " + r.issue());
        if (r.fixSteps() != null) {
            for (String step : r.fixSteps()) {
                log.compat("  " + step);
            }
        }
        log.compat("─────────────────────────────────────────");
    }

    private static void scheduleInGameMessage(String msg) {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc != null) {
            mc.execute(() -> OptimizerCommand.msg(msg));
        }
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static String getMcVersion() {
        try {
            return net.fabricmc.loader.api.FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        } catch (Throwable t) {
            try {
                Object ver = Class.forName("net.minecraft.SharedConstants")
                    .getMethod("getGameVersion").invoke(null);
                for (String m : new String[]{"getId", "getName", "getVersionId"}) {
                    try { return (String) ver.getClass().getMethod(m).invoke(ver); }
                    catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}
            return "unknown";
        }
    }
}
