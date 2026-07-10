package dev.akatriggered.command;

import dev.akatriggered.Main;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class OptimizerCommand {

    private static final String DISCORD  = "discord.gg/vF5bE4strk";
    private static final String PREFIX   = "§8[§6G1ax§8] ";
    private static final String ERR_SUFFIX = " §7| Support: §b" + DISCORD;

    public static boolean defaultMode = false;
    public static boolean tweakMode   = false;

    private static final String[] TWEAK_INFO = {
        PREFIX + "§e§lTWEAK MODE §r§7— What it does:",
        PREFIX + "§7  §a✓ §7Removes Minecraft's built-in 4-tick (200ms) placement delay",
        PREFIX + "§7  §a✓ §7Crystal placement runs at tick rate instead of every 4 ticks",
        PREFIX + "§7  §a✓ §7100% vanilla crystal logic and validation unchanged",
        PREFIX + "§7  §c✗ §7No client-side entity removal",
        PREFIX + "§7  §c✗ §7No custom packets or packet injection",
        PREFIX + "§7  §c✗ §7No game state or entity prediction",
        PREFIX + "§7  §8AC-safe: only affects placement attempt frequency",
    };

    private static final String[] DEFAULT_INFO = {
        PREFIX + "§a§lDEFAULT MODE §r§7— What it does:",
        PREFIX + "§7  §a✓ §7Everything TWEAK does (cooldown bypass)",
        PREFIX + "§7  §a✓ §7Client-side crystal removal on attack (instant visual feedback)",
        PREFIX + "§7  §a✓ §7Direct interactBlock packets (bypasses useOnBlock validation)",
        PREFIX + "§7  §a✓ §7Ping-adaptive packet rate (2–4 packets/tick based on latency)",
        PREFIX + "§7  §8For servers that allow client-side optimizers",
    };

    public void initializeCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommands.literal("g1axoptimizer")

                .then(ClientCommands.literal("default").executes(ctx -> {
                    defaultMode = true;
                    tweakMode = false;
                    for (String line : DEFAULT_INFO) msg(line);
                    if (Main.getInstance() != null)
                        Main.getInstance().getLogger().mode("Switched to DEFAULT mode (full optimizer)");
                    return 1;
                }))

                .then(ClientCommands.literal("tweak").executes(ctx -> {
                    tweakMode = true;
                    defaultMode = false;
                    for (String line : TWEAK_INFO) msg(line);
                    if (Main.getInstance() != null)
                        Main.getInstance().getLogger().mode("Switched to TWEAK mode (AC-safe, cooldown bypass only)");
                    return 1;
                }))

                .then(ClientCommands.literal("off").executes(ctx -> {
                    defaultMode = false;
                    tweakMode = false;
                    msg(PREFIX + "§cOptimizer disabled. §7Using pure vanilla crystal behavior.");
                    if (Main.getInstance() != null)
                        Main.getInstance().getLogger().mode("Optimizer disabled (vanilla)");
                    return 1;
                }))

                .executes(ctx -> {
                    String mode = defaultMode ? "§aDefault" : tweakMode ? "§eTweak (AC-safe)" : "§cOff";
                    msg(PREFIX + "Current mode: " + mode);
                    msg(PREFIX + "§8Usage: /g1axoptimizer <default | tweak | off>");
                    msg(PREFIX + "§8Support: §b" + DISCORD);
                    return 1;
                })
            )
        );
    }

    public static void error(String message) {
        msg(PREFIX + "§c" + message + ERR_SUFFIX);
    }

    public static void msg(String raw) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gui == null || mc.gui.hud == null || mc.gui.hud.getChat() == null) return;
        mc.gui.hud.getChat().addClientSystemMessage(fromLegacy(raw));
    }

    private static MutableComponent fromLegacy(String raw) {
        MutableComponent root = Component.literal("");
        String[] parts = raw.split("§");
        if (parts.length == 0) return root;
        if (!raw.startsWith("§")) root.append(Component.literal(parts[0]));
        for (int i = raw.startsWith("§") ? 0 : 1; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;
            char code = part.charAt(0);
            String text = part.length() > 1 ? part.substring(1) : "";
            ChatFormatting fmt = ChatFormatting.getByCode(code);
            MutableComponent seg = Component.literal(text);
            if (fmt != null) seg = seg.withStyle(fmt);
            root.append(seg);
        }
        return root;
    }

    public static boolean inGame() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.getConnection() != null;
    }
}
