package dev.akatriggered.command;

import dev.akatriggered.Main;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class OptimizerCommand {

    private static final String DISCORD   = "discord.gg/vF5bE4strk";
    private static final String PREFIX    = "§8[§6G1ax§8] ";
    private static final String ERR_SUFFIX = " §7| Support: §b" + DISCORD;

    public static boolean defaultMode = false;
    public static boolean tweakMode   = false;

    public void initializeCommands() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("g1axoptimizer")

            .then(ClientCommandManager.literal("default").executes(ctx -> {
                defaultMode = true;
                tweakMode   = false;
                actionBar("§a✔ §aDefault Mode §r§7— Full optimizer enabled");
                if (Main.getInstance() != null)
                    Main.getInstance().getLogger().mode("Switched to DEFAULT mode (full optimizer)");
                return 1;
            }))

            .then(ClientCommandManager.literal("tweak").executes(ctx -> {
                tweakMode   = true;
                defaultMode = false;
                actionBar("§e✔ §eTweak Mode §r§7— AC-safe cooldown bypass enabled");
                if (Main.getInstance() != null)
                    Main.getInstance().getLogger().mode("Switched to TWEAK mode (AC-safe, cooldown bypass only)");
                return 1;
            }))

            .then(ClientCommandManager.literal("off").executes(ctx -> {
                defaultMode = false;
                tweakMode   = false;
                actionBar("§c✗ §cOptimizer Disabled §r§7— Vanilla crystal behavior");
                if (Main.getInstance() != null)
                    Main.getInstance().getLogger().mode("Optimizer disabled (vanilla)");
                return 1;
            }))

            .executes(ctx -> {
                String mode = defaultMode ? "§aDefault" : tweakMode ? "§eTweak §7(AC-safe)" : "§cOff";
                msg("");
                msg(PREFIX + "§f§lG1ax Crystal Optimizer §7v1.0.6");
                msg(PREFIX + "§7Current Mode: " + mode);
                msg("");
                msg(PREFIX + "§e/g1axoptimizer default §8— §7Full optimizer: fast placement,");
                msg(PREFIX + "  §7client-side break prediction, ping-adaptive packets");
                msg(PREFIX + "§e/g1axoptimizer tweak §8— §7AC-safe: bypasses cooldown only,");
                msg(PREFIX + "  §7100% vanilla code path, safe for strict anticheat");
                msg(PREFIX + "§e/g1axoptimizer off §8— §7Disable all optimizations");
                msg("");
                msg(PREFIX + "§8Discord: §b" + DISCORD);
                return 1;
            })
        );
    }

    public static void error(String message) {
        msg(PREFIX + "§c" + message + ERR_SUFFIX);
    }

    public static void actionBar(String raw) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.inGameHud == null) return;
        mc.inGameHud.setOverlayMessage(fromLegacy(raw), false);
    }

    public static void msg(String raw) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.inGameHud == null) return;
        mc.inGameHud.getChatHud().addMessage(fromLegacy(raw));
    }

    private static MutableText fromLegacy(String raw) {
        MutableText root = new LiteralText("");
        String[] parts = raw.split("§");
        if (parts.length == 0) return root;
        if (!raw.startsWith("§")) root.append(new LiteralText(parts[0]));
        for (int i = raw.startsWith("§") ? 0 : 1; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;
            char code = part.charAt(0);
            String text = part.length() > 1 ? part.substring(1) : "";
            Formatting fmt = Formatting.byCode(code);
            MutableText seg = new LiteralText(text);
            if (fmt != null) seg = seg.formatted(fmt);
            root.append(seg);
        }
        return root;
    }

    public static boolean inGame() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.player != null && mc.getNetworkHandler() != null;
    }
}
