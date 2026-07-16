package dev.akatriggered.command;

import dev.akatriggered.Main;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class OptimizerCommand {

    private static final String DISCORD  = "discord.gg/vF5bE4strk";
    private static final String PREFIX   = "§8[§6G1ax§8] ";
    private static final String ERR_SUFFIX = " §7| Support: §b" + DISCORD;

    /**
     * DEFAULT: full optimizer — client-side crystal removal, direct packet injection,
     *          ping-adaptive rate limit. Most optimized but touches game logic.
     *
     * TWEAK:   AC-safe mode — ONLY resets Minecraft's built-in 4-tick placement cooldown.
     *          Zero changes to crystal logic, placement validation, packet content,
     *          entity removal, or any game state. 100% vanilla code path executes;
     *          it just executes more often. Safe for servers with anticheat.
     */
    public static boolean defaultMode = false;
    public static boolean tweakMode   = false;

    public void initializeCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("g1axoptimizer")

                .then(ClientCommandManager.literal("default").executes(ctx -> {
                    defaultMode = true;
                    tweakMode = false;
                    actionBar("§a✔ §aDefault Mode §r§7— Full optimizer enabled");
                    if (Main.getInstance() != null)
                        Main.getInstance().getLogger().mode("Switched to DEFAULT mode (full optimizer)");
                    return 1;
                }))

                .then(ClientCommandManager.literal("tweak").executes(ctx -> {
                    tweakMode = true;
                    defaultMode = false;
                    actionBar("§e✔ §eTweak Mode §r§7— AC-safe cooldown bypass enabled");
                    if (Main.getInstance() != null)
                        Main.getInstance().getLogger().mode("Switched to TWEAK mode (AC-safe, cooldown bypass only)");
                    return 1;
                }))

                .then(ClientCommandManager.literal("off").executes(ctx -> {
                    defaultMode = false;
                    tweakMode = false;
                    actionBar("§c✗ §cOptimizer Disabled §r§7— Vanilla crystal behavior");
                    if (Main.getInstance() != null)
                        Main.getInstance().getLogger().mode("Optimizer disabled (vanilla)");
                    return 1;
                }))

                .executes(ctx -> {
                    String mode = defaultMode ? "§aDefault" : tweakMode ? "§eTweak §7(AC-safe)" : "§cOff";
                    msg(PREFIX + "Mode: " + mode + "  §8| Usage: /g1axoptimizer <default | tweak | off>");
                    msg(PREFIX + "§8Support: §b" + DISCORD);
                    return 1;
                })
            )
        );
    }

    public static void error(String message) {
        msg(PREFIX + "§c" + message + ERR_SUFFIX);
    }

    /** Sends a message to the action bar (above the hotbar) — non-intrusive, no chat clutter. */
    public static void actionBar(String raw) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.inGameHud == null) return;
        mc.inGameHud.setOverlayMessage(fromLegacy(raw), false);
    }

    public static void msg(String raw) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.inGameHud == null || mc.inGameHud.getChatHud() == null) return;
        mc.inGameHud.getChatHud().addMessage(fromLegacy(raw));
    }

    private static MutableText fromLegacy(String raw) {
        MutableText root = Text.literal("");
        String[] parts = raw.split("§");
        if (parts.length == 0) return root;
        if (!raw.startsWith("§")) root.append(Text.literal(parts[0]));
        for (int i = raw.startsWith("§") ? 0 : 1; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;
            char code = part.charAt(0);
            String text = part.length() > 1 ? part.substring(1) : "";
            Formatting fmt = Formatting.byCode(code);
            MutableText seg = Text.literal(text);
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
