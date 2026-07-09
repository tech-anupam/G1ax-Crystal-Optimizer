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
        PayloadTypeRegistry.playS2C().register(ServerOptOutPacket.TYPE, ServerOptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.configurationS2C().register(ServerOptOutPacket.TYPE, ServerOptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(VersionPacket.TYPE, VersionPacket.STREAM_CODEC);

        try {
            Class<?> reg = Class.forName("net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry");
            var playC2S = reg.getMethod("playC2S");
            Object registry = playC2S.invoke(null);
            for (var m : registry.getClass().getMethods()) {
                if (m.getName().equals("register") && m.getParameterCount() == 2) {
                    m.invoke(registry,
                        dev.akatriggered.packets.OptOutPacket.TYPE,
                        dev.akatriggered.packets.OptOutPacket.STREAM_CODEC
                    );
                    logger.info("OptOut packet registered");
                    return;
                }
            }
        } catch (NoSuchMethodException e) {
            logger.warn("OptOut packet skipped — Fabric API version mismatch (non-fatal)");
        } catch (Exception e) {
            logger.warn("OptOut packet skipped: " + e.getMessage());
        }
    }

    private void registerListeners() {
        ClientPlayConnectionEvents.JOIN.register(new ConnectEventListener());
        ClientPlayConnectionEvents.DISCONNECT.register(new DisconnectEventListener());
        OptOutPacketListener.register();
    }
}
