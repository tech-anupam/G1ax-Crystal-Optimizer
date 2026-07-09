package dev.akatriggered.listener;

import dev.akatriggered.Main;
import dev.akatriggered.cache.OptOutCache;
import dev.akatriggered.packets.OptOutAckPacket;
import dev.akatriggered.packets.ServerOptOutPacket;
import dev.akatriggered.util.ConnectionUtil;
import dev.akatriggered.util.HoverEventResolver;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class OptOutPacketListener {
    private OptOutPacketListener() {}

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ServerOptOutPacket.TYPE, (payload, context) -> {
            MinecraftClient client = context.client();
            OptOutCache cache = Main.getOptOutCache();
            String key = ConnectionUtil.currentServerKey(client);
            if (key != null) cache.markOptedOut(key);
            else cache.setOptedOut(true);

            if (!cache.hasNotified(key)) {
                CompletableFuture.delayedExecutor(2L, TimeUnit.SECONDS).execute(() ->
                    client.execute(() -> {
                        if (client.player != null && !cache.hasNotified(key)) {
                            cache.markNotified(key);
                            client.player.sendMessage(buildDisabledMessage(), false);
                        }
                    })
                );
            }

            context.responseSender().sendPacket(OptOutAckPacket.INSTANCE);
        });
    }

    private static Text buildDisabledMessage() {
        Text hover = Text.empty()
            .append(Text.literal("Why is this disabled?\n").formatted(Formatting.GOLD))
            .append(Text.literal("• This server requested the optimizer to be disabled.\n").formatted(Formatting.GRAY))
            .append(Text.literal("• Usually for rules enforcement or compatibility.\n").formatted(Formatting.GRAY))
            .append(Text.literal("\nApplies only while you stay on this server.").formatted(Formatting.ITALIC));
        Style hoverStyle = Style.EMPTY
            .withHoverEvent(HoverEventResolver.createShowTextHoverEvent(hover))
            .withColor(Formatting.YELLOW);
        Text msg = Text.literal("Optimizer disabled on this server.").setStyle(hoverStyle);
        return Main.PREFIX.copy().setStyle(hoverStyle).append(msg);
    }
}
