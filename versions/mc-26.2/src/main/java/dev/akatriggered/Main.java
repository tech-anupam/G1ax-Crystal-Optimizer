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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@Environment(EnvType.CLIENT)
public class Main implements ClientModInitializer {

    public static final MutableComponent PREFIX = Component.literal("[")
        .withStyle(ChatFormatting.GRAY)
        .append(Component.literal("G1ax").withStyle(ChatFormatting.GOLD))
        .append(Component.literal("] ").withStyle(ChatFormatting.GRAY));

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
            PayloadTypeRegistry.clientboundPlay().register(ServerOptOutPacket.TYPE, ServerOptOutPacket.STREAM_CODEC);
        } catch (Throwable t) {
            logger.warn("Skipped clientboundPlay registry: " + t.getMessage());
        }

        try {
            PayloadTypeRegistry.clientboundConfiguration().register(ServerOptOutPacket.TYPE, ServerOptOutPacket.STREAM_CODEC);
        } catch (Throwable t) {
            logger.warn("Skipped clientboundConfiguration registry: " + t.getMessage());
        }

        try {
            PayloadTypeRegistry.serverboundPlay().register(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC);
            PayloadTypeRegistry.serverboundPlay().register(VersionPacket.TYPE, VersionPacket.STREAM_CODEC);
            PayloadTypeRegistry.serverboundPlay().register(dev.akatriggered.packets.OptOutPacket.TYPE, dev.akatriggered.packets.OptOutPacket.STREAM_CODEC);
            logger.info("OptOut and Version packets registered");
        } catch (Throwable t) {
            logger.warn("Skipped serverboundPlay registry: " + t.getMessage());
        }
    }

    private void registerListeners() {
        ClientPlayConnectionEvents.JOIN.register(new ConnectEventListener());
        ClientPlayConnectionEvents.DISCONNECT.register(new DisconnectEventListener());
        OptOutPacketListener.register();
    }
}
