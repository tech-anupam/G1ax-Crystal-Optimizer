package dev.akatriggered;

import dev.akatriggered.cache.OptOutCache;
import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.listener.ConnectEventListener;
import dev.akatriggered.listener.DisconnectEventListener;
import dev.akatriggered.listener.OptOutPacketListener;
import dev.akatriggered.packets.OptOutAckPacket;
import dev.akatriggered.packets.ServerOptOutPacket;
import dev.akatriggered.packets.VersionPacket;
import dev.akatriggered.util.CompatibilityChecker;
import dev.akatriggered.util.Logger;
import dev.akatriggered.util.PerformanceGuard;
import dev.akatriggered.util.UpdateChecker;
import dev.akatriggered.util.VersionUtil;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class Main implements ClientModInitializer {

    public static final MutableText PREFIX = Text.literal("[")
        .formatted(Formatting.GRAY)
        .append(Text.literal("G1ax").formatted(Formatting.GOLD))
        .append(Text.literal("] ").formatted(Formatting.GRAY));

    @Getter private static Main instance;
    @Getter private static Logger logger;
    @Getter private static PerformanceGuard performanceGuard;
    @Getter private static OptOutCache optOutCache;

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
            Class<?> registryClass = Class.forName("net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry");

            // register playS2C packets
            try {
                Object playS2CRegistry = registryClass.getMethod("playS2C").invoke(null);
                registerPacket(playS2CRegistry, ServerOptOutPacket.TYPE, ServerOptOutPacket.STREAM_CODEC);
            } catch (Throwable t) {
                logger.warn("Skipped playS2C registry: " + t.getMessage());
            }

            // register configurationS2C packets
            try {
                Object configS2CRegistry = registryClass.getMethod("configurationS2C").invoke(null);
                registerPacket(configS2CRegistry, ServerOptOutPacket.TYPE, ServerOptOutPacket.STREAM_CODEC);
            } catch (Throwable t) {
                logger.warn("Skipped configurationS2C registry: " + t.getMessage());
            }

            // register playC2S packets
            try {
                Object playC2SRegistry = registryClass.getMethod("playC2S").invoke(null);
                registerPacket(playC2SRegistry, OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC);
                registerPacket(playC2SRegistry, VersionPacket.TYPE, VersionPacket.STREAM_CODEC);
                registerPacket(playC2SRegistry, dev.akatriggered.packets.OptOutPacket.TYPE, dev.akatriggered.packets.OptOutPacket.STREAM_CODEC);
                logger.info("OptOut and Version packets registered");
            } catch (Throwable t) {
                logger.warn("Skipped playC2S registry: " + t.getMessage());
            }

        } catch (ClassNotFoundException e) {
            logger.warn("PayloadTypeRegistry not found — skipping packet registration (non-fatal)");
        } catch (Throwable e) {
            logger.warn("Packet registration failed: " + e.getMessage());
        }
    }

    private void registerPacket(Object registry, Object type, Object codec) throws Exception {
        for (var method : registry.getClass().getMethods()) {
            if (method.getName().equals("register") && method.getParameterCount() == 2) {
                method.invoke(registry, type, codec);
                return;
            }
        }
        throw new NoSuchMethodException("register method not found on " + registry.getClass().getName());
    }

    private void registerListeners() {
        ClientPlayConnectionEvents.JOIN.register(new ConnectEventListener());
        ClientPlayConnectionEvents.DISCONNECT.register(new DisconnectEventListener());
        OptOutPacketListener.register();
    }
}
