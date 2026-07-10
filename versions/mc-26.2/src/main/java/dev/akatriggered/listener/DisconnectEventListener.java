package dev.akatriggered.listener;

import dev.akatriggered.Main;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public final class DisconnectEventListener implements ClientPlayConnectionEvents.Disconnect {
    @Override
    public void onPlayDisconnect(ClientPacketListener handler, Minecraft client) {
        Main.getOptOutCache().clearCurrentSession();
    }
}
