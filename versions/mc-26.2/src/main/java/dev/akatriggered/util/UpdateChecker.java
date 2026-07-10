package dev.akatriggered.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import dev.akatriggered.command.OptimizerCommand;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateChecker {

    private static final String MOD_ID      = "g1axcrystaloptimizer";
    private static final String PAGE        = "modrinth.com/mod/" + MOD_ID;
    private static final String API_URL     = "https://api.modrinth.com/v2/project/" + MOD_ID + "/version";
    private static final String PREFIX      = "§8[§6G1ax§8] ";
    private static final String CONTACT     = "anupambuilds.shop";

    private static volatile String  latestVersion = null;
    private static volatile boolean notified      = false;

    public static void init(Logger logger) {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> notifyIfReady());

        Thread t = new Thread(() -> fetch(logger), "G1ax-UpdateChecker");
        t.setDaemon(true);
        t.start();
    }

    private static void fetch(Logger logger) {
        try {
            String current = currentVersion();
            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setRequestProperty("User-Agent",
                "G1ax-Crystal-Optimizer/" + current + " (modrinth.com/mod/" + MOD_ID + ")");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) return;

            JsonArray arr = JsonParser.parseReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            ).getAsJsonArray();

            if (arr.isEmpty()) return;
            latestVersion = arr.get(0).getAsJsonObject().get("version_number").getAsString();

            if (!current.equals(latestVersion))
                logger.info("Update available: " + current + " -> " + latestVersion);

        } catch (Exception e) {
            logger.warn("Update check failed: " + e.getMessage());
        }
    }

    private static void notifyIfReady() {
        if (notified || latestVersion == null) return;
        if (!OptimizerCommand.inGame()) return;
        notified = true;
        String current = currentVersion();
        if (current.equals(latestVersion)) return;
        showMessage(current, latestVersion);
    }

    private static void showMessage(String current, String latest) {
        OptimizerCommand.msg(PREFIX + "§8§m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        OptimizerCommand.msg(PREFIX + "§e§l ✦ §r§aUpdate Available!");
        OptimizerCommand.msg(PREFIX + "§7 Installed §8» §c" + current + "  §7Latest §8» §a" + latest);
        OptimizerCommand.msg(PREFIX + "§b §f" + PAGE);
        OptimizerCommand.msg(PREFIX + "§d ★ §7Custom Dev §8» §d" + CONTACT);
        OptimizerCommand.msg(PREFIX + "§8§m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private static String currentVersion() {
        return FabricLoader.getInstance()
            .getModContainer(MOD_ID)
            .map(c -> c.getMetadata().getVersion().getFriendlyString())
            .orElse("0.0.0");
    }
}
