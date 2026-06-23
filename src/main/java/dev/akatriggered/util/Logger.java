package dev.akatriggered.util;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final org.slf4j.Logger SLF4J = LoggerFactory.getLogger("G1axCrystalOptimizer");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int MAX_ROTATIONS = 3;

    private static PrintWriter fileWriter;
    private static Path logFile;

    public static void init(File runDir) {
        try {
            Path logsDir = runDir.toPath().resolve("logs");
            Files.createDirectories(logsDir);
            logFile = logsDir.resolve("g1axoptimizer-latest.log");

            rotate(logsDir);

            fileWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(logFile.toFile(), false),
                    StandardCharsets.UTF_8
                )
            ));

            String mcVer = getMinecraftVersion();
            writeLine("INIT", "═══════════════════════════════════════");
            writeLine("INIT", " G1ax Crystal Optimizer  v1.0.1");
            writeLine("INIT", " Minecraft : " + mcVer);
            writeLine("INIT", " Log file  : " + logFile.toAbsolutePath());
            writeLine("INIT", " Discord   : discord.gg/vF5bE4strk");
            writeLine("INIT", "═══════════════════════════════════════");
        } catch (Exception e) {
            SLF4J.warn("[G1ax] Could not create log file: {}", e.getMessage());
        }
    }

    private static void rotate(Path logsDir) {
        try {
            Path latest = logsDir.resolve("g1axoptimizer-latest.log");
            if (!Files.exists(latest)) return;

            for (int i = MAX_ROTATIONS - 1; i >= 1; i--) {
                Path old = logsDir.resolve("g1axoptimizer-" + i + ".log");
                Path next = logsDir.resolve("g1axoptimizer-" + (i + 1) + ".log");
                if (Files.exists(old)) {
                    Files.move(old, next, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            Files.move(latest, logsDir.resolve("g1axoptimizer-1.log"), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            SLF4J.warn("[G1ax] Log rotation failed: {}", e.getMessage());
        }
    }

    private static String getMinecraftVersion() {
        try {
            Object ver = net.minecraft.SharedConstants.class
                .getMethod("getGameVersion").invoke(null);
            for (String m : new String[]{"getId", "getName", "getVersionId"}) {
                try { return (String) ver.getClass().getMethod(m).invoke(ver); }
                catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

    private static synchronized void writeLine(String level, String message) {
        String timestamp = LocalDateTime.now().format(TIME_FMT);
        String line = "[" + timestamp + "] [G1ax/" + level + "] " + message;
        SLF4J.info("{}", line);
        if (fileWriter != null) {
            fileWriter.println(line);
            fileWriter.flush();
        }
    }

    public void info(String message) {
        writeLine("INFO", message);
    }

    public void warn(String message) {
        writeLine("WARN", "⚠  " + message);
    }

    public void error(String message) {
        writeLine("ERROR", "✗  " + message);
    }

    public void mode(String message) {
        writeLine("MODE", "»  " + message);
    }

    public void crystal(String message) {
        writeLine("CRYSTAL", "◆  " + message);
    }

    public void compat(String message) {
        writeLine("COMPAT", "⊘  " + message);
    }

    public static void shutdown() {
        if (fileWriter != null) {
            writeLine("INIT", "═══════════════════════════════════════");
            writeLine("INIT", " Session ended — " + LocalDateTime.now().format(DATE_FMT));
            writeLine("INIT", "═══════════════════════════════════════");
            fileWriter.flush();
            fileWriter.close();
        }
    }
}
