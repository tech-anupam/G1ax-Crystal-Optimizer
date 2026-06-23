package dev.akatriggered;

import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.util.CompatibilityChecker;
import dev.akatriggered.util.Logger;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public class Main implements ClientModInitializer {

    @Getter private static Main instance;
    @Getter private Logger logger;

    @Override
    public void onInitializeClient() {
        instance = this;

        Logger.init(FabricLoader.getInstance().getGameDir().toFile());
        logger = new Logger();

        CompatibilityChecker.runAll(logger);

        tryRegisterPacket();

        try {
            new OptimizerCommand().initializeCommands();
            logger.info("Commands registered: /g1axoptimizer <default|tweak|off>");
        } catch (Exception e) {
            logger.error("Command registration failed: " + e.getMessage());
            logger.error("Support: discord.gg/vF5bE4strk");
        }

        logger.info("Ready — use /g1axoptimizer to get started");

        Runtime.getRuntime().addShutdownHook(new Thread(Logger::shutdown, "G1ax-LogShutdown"));
    }

    private void tryRegisterPacket() {
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
}
