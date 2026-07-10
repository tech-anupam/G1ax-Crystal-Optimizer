package dev.akatriggered.listener;

import dev.akatriggered.Main;
import dev.akatriggered.cache.OptOutCache;
import dev.akatriggered.packets.OptOutAckPacket;
import dev.akatriggered.packets.ServerOptOutPacket;
import dev.akatriggered.util.ConnectionUtil;
import dev.akatriggered.util.HoverEventResolver;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class OptOutPacketListener {
    private OptOutPacketListener() {}

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ServerOptOutPacket.TYPE, (payload, context) -> {
            Minecraft client = context.client();
            OptOutCache cache = Main.getOptOutCache();
            String key = ConnectionUtil.currentServerKey(client);
            if (key != null) cache.markOptedOut(key);
            else cache.setOptedOut(true);

            if (!cache.hasNotified(key)) {
                CompletableFuture.delayedExecutor(2L, TimeUnit.SECONDS).execute(() ->
                    client.execute(() -> {
                        if (client.player != null && !cache.hasNotified(key)) {
                            cache.markNotified(key);
                            client.player.sendSystemMessage(buildDisabledMessage());
                        }
                    })
                );
            }

            context.responseSender().sendPacket(OptOutAckPacket.INSTANCE);
        });
    }

    private static Component buildDisabledMessage() {
        Component hover = Component.empty()
            .copy()
            .append(Component.literal("Why is this disabled?\n").withStyle(ChatFormatting.GOLD))
            .append(Component.literal("• This server requested the optimizer to be disabled.\n").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("• Usually for rules enforcement or compatibility.\n").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("\nApplies only while you stay on this server.").withStyle(ChatFormatting.ITALIC));
        Style hoverStyle = Style.EMPTY
            .withHoverEvent(HoverEventResolver.createShowTextHoverEvent(hover))
            .withColor(ChatFormatting.YELLOW);
        Component msg = Component.literal("Optimizer disabled on this server.").withStyle(s -> hoverStyle);
        return Main.PREFIX.copy().withStyle(s -> hoverStyle).append(msg);
    }
}
