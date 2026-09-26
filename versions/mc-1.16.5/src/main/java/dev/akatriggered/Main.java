package dev.akatriggered;

import dev.akatriggered.cache.OptOutCache;
import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.listener.ConnectEventListener;
import dev.akatriggered.listener.DisconnectEventListener;
import dev.akatriggered.listener.OptOutPacketListener;
import dev.akatriggered.util.CompatibilityChecker;
import dev.akatriggered.util.Logger;
import dev.akatriggered.util.PerformanceGuard;
import dev.akatriggered.util.UpdateChecker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class Main implements ClientModInitializer {

    public static final MutableText PREFIX = new LiteralText("[")
        .formatted(Formatting.GRAY)
        .append(new LiteralText("G1ax").formatted(Formatting.GOLD))
        .append(new LiteralText("] ").formatted(Formatting.GRAY));

    private static Main instance;
    private static Logger logger;
    private static PerformanceGuard performanceGuard;
    private static OptOutCache optOutCache;

    public static Main getInstance() { return instance; }
    public static Logger getLogger() { return logger; }
    public static PerformanceGuard getPerformanceGuard() { return performanceGuard; }
    public static OptOutCache getOptOutCache() { return optOutCache; }

    @Override
    public void onInitializeClient() {
        instance = this;
        performanceGuard = new PerformanceGuard();
        optOutCache = new OptOutCache();

        Logger.init(FabricLoader.getInstance().getGameDir().toFile());
        logger = new Logger();

        CompatibilityChecker.runAll(logger);
        UpdateChecker.init(logger);

        registerPackets();
        registerListeners();

        try {
            new OptimizerCommand().initializeCommands();
            logger.info("Commands registered: /g1axoptimizer <default|tweak|off>");
        } catch (Exception e) {
            logger.error("Command registration failed: " + e.getMessage());
        }

        logger.info("Ready — use /g1axoptimizer to get started");
        Runtime.getRuntime().addShutdownHook(new Thread(Logger::shutdown, "G1ax-LogShutdown"));
    }

    private void registerPackets() {
        try {
            OptOutPacketListener.registerChannels();
            logger.info("OptOut and Version packet channels registered");
        } catch (Throwable t) {
            logger.warn("Skipped packet channel registration: " + t.getMessage());
        }
    }

    private void registerListeners() {
        ClientPlayConnectionEvents.JOIN.register(new ConnectEventListener());
        ClientPlayConnectionEvents.DISCONNECT.register(new DisconnectEventListener());
        OptOutPacketListener.register();
    }
}
