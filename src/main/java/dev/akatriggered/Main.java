package dev.akatriggered;

import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.handler.InteractHandler;
import dev.akatriggered.packets.OptOutPacket;
import dev.akatriggered.util.Logger;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

@Environment(EnvType.CLIENT)
public class Main implements ClientModInitializer {

    @Getter
    private static Main instance;

    @Getter
    private Logger logger;

    @Getter
    private InteractHandler interactHandler;

    @Override
    public void onInitializeClient() {
        instance = this;
        logger = new Logger();
        PayloadTypeRegistry.playC2S().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        OptimizerCommand command = new OptimizerCommand();
        command.initializeCommands();
        logger.info("G1ax Crystal Optimizer initialized successfully");
    }
}
