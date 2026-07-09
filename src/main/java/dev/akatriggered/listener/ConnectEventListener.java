package dev.akatriggered.listener;

import dev.akatriggered.Main;
import dev.akatriggered.cache.OptOutCache;
import dev.akatriggered.util.ConnectionUtil;
import dev.akatriggered.util.VersionUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public final class ConnectEventListener implements ClientPlayConnectionEvents.Join {
    private final OptOutCache optOutCache = Main.getOptOutCache();

    @Override
    public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (client.isInSingleplayer()) return;
        sender.sendPacket(VersionUtil.createVersionPacket());
        String key = ConnectionUtil.currentServerKey(client);
        if (optOutCache.isServerOptedOut(key)) {
            optOutCache.setOptedOut(true);
        }
    }
}
