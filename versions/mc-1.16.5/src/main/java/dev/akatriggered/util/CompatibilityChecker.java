package dev.akatriggered.util;

import dev.akatriggered.command.OptimizerCommand;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityChecker {

    private static final String DISCORD = "discord.gg/vF5bE4strk";
    private static final String ISSUES  = "github.com/tech-anupam/G1ax-Crystal-Optimizer/issues";

    private static final List<String> TESTED_VERSIONS = new ArrayList<>();

    static {
        TESTED_VERSIONS.add("1.16.1");
        TESTED_VERSIONS.add("1.16.2");
        TESTED_VERSIONS.add("1.16.3");
        TESTED_VERSIONS.add("1.16.4");
        TESTED_VERSIONS.add("1.16.5");
    }

    public static class CheckResult {
        private final boolean ok;
        private final String issue;
        private final String[] fixSteps;

        public CheckResult(boolean ok, String issue, String[] fixSteps) {
            this.ok = ok;
            this.issue = issue;
            this.fixSteps = fixSteps;
        }

        public boolean ok()        { return ok; }
        public String issue()      { return issue; }
        public String[] fixSteps() { return fixSteps; }
    }

    public static void runAll(Logger log) {
        String mcVer = getMcVersion();

        List<CheckResult> results = new ArrayList<>();
        results.add(checkMcVersion(mcVer));
        results.add(checkFabricApi());
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
                "This mod is tested on: " + join(TESTED_VERSIONS),
                "Your version (" + version + ") may work but is not guaranteed",
                "HOW TO FIX:",
                "  1. Check " + ISSUES + " for your version",
                "  2. If not reported, open a new issue with this log file",
                "  3. Join " + DISCORD + " for faster support",
                "  4. Try using 1.16.5 which is fully tested"
            }
        );
    }

    private static CheckResult checkFabricApi() {
        try {
            Class.forName("net.fabricmc.fabric.api.client.command.v1.ClientCommandManager");
            return new CheckResult(true, null, null);
        } catch (ClassNotFoundException e) {
            return new CheckResult(false,
                "Fabric API not found or wrong version",
                new String[]{
                    "HOW TO FIX:",
                    "  1. Download Fabric API from modrinth.com/mod/fabric-api",
                    "  2. Make sure you downloaded Fabric API for Minecraft 1.16.5",
                    "  3. Place the Fabric API JAR in your mods folder",
                    "  4. Do NOT confuse Fabric Loader with Fabric API — both are needed",
                    "  Support: " + DISCORD
                }
            );
        }
    }

    private static CheckResult checkMixinTargets() {
        boolean clientOk = classExists("net.minecraft.client.MinecraftClient");
        boolean crystalOk = classExists("net.minecraft.item.EndCrystalItem");
        boolean connOk    = classExists("net.minecraft.network.ClientConnection");

        if (clientOk && crystalOk && connOk) return new CheckResult(true, null, null);

        List<String> missing = new ArrayList<>();
        if (!clientOk) missing.add("MinecraftClient");
        if (!crystalOk) missing.add("EndCrystalItem");
        if (!connOk)   missing.add("ClientConnection");

        return new CheckResult(false,
            "Mixin targets not found: " + join(missing),
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
        int javaVer = getJavaVersion();
        if (javaVer >= 8) return new CheckResult(true, null, null);

        return new CheckResult(false,
            "Java " + javaVer + " detected — Java 8+ required for 1.16.x",
            new String[]{
                "HOW TO FIX:",
                "  1. Your launcher must use Java 8 or newer",
                "  2. For Prism/Modrinth launcher: Instance Settings → Java → Auto-detect",
                "  3. For official launcher: Installations → Edit → More Options → Java executable",
                "  4. Download Java 8 from: adoptium.net",
                "  Support: " + DISCORD
            }
        );
    }

    private static int getJavaVersion() {
        try {
            String ver = System.getProperty("java.version");
            if (ver.startsWith("1.")) {
                return Integer.parseInt(ver.split("\\.")[1]);
            }
            return Integer.parseInt(ver.split("\\.")[0]);
        } catch (Exception e) {
            return 8;
        }
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

    private static void scheduleInGameMessage(final String msg) {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc != null) {
            mc.execute(new Runnable() {
                @Override
                public void run() {
                    OptimizerCommand.msg(msg);
                }
            });
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

    private static String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i));
        }
        return sb.toString();
    }
}
