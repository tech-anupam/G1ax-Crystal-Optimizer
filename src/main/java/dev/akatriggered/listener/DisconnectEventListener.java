package dev.akatriggered.listener;

import dev.akatriggered.Main;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public final class DisconnectEventListener implements ClientPlayConnectionEvents.Disconnect {
    @Override
    public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        Main.getOptOutCache().clearCurrentSession();
    }
}
