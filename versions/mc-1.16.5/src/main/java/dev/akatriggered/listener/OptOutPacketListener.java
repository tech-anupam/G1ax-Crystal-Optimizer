package dev.akatriggered.listener;

import dev.akatriggered.Main;
import dev.akatriggered.cache.OptOutCache;
import dev.akatriggered.packets.OptOutAckPacket;
import dev.akatriggered.packets.ServerOptOutPacket;
import dev.akatriggered.util.ConnectionUtil;
import dev.akatriggered.util.HoverEventResolver;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class OptOutPacketListener {
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "G1ax-NotificationThread");
        t.setDaemon(true);
        return t;
    });

    private OptOutPacketListener() {}

    public static void registerChannels() {}

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ServerOptOutPacket.CHANNEL_ID, (client, handler, buf, responseSender) -> {
            ServerOptOutPacket.decode(buf);

            final OptOutCache cache = Main.getOptOutCache();
            final String key = ConnectionUtil.currentServerKey(client);
            if (key != null) cache.markOptedOut(key);
            else cache.setOptedOut(true);

            if (!cache.hasNotified(key)) {
                SCHEDULER.schedule(() ->
                    client.execute(() -> {
                        if (client.player != null && !cache.hasNotified(key)) {
                            cache.markNotified(key);
                            client.player.sendMessage(buildDisabledMessage(), false);
                        }
                    }), 2L, TimeUnit.SECONDS
                );
            }

            try {
                responseSender.sendPacket(OptOutAckPacket.CHANNEL_ID, PacketByteBufs.create());
            } catch (Throwable t) {
                Main.getLogger().warn("Failed to send opt-out ack: " + t.getMessage());
            }
        });
    }

    private static Text buildDisabledMessage() {
        Text hover = new LiteralText("")
            .append(new LiteralText("Why is this disabled?\n").formatted(Formatting.GOLD))
            .append(new LiteralText("• This server requested the optimizer to be disabled.\n").formatted(Formatting.GRAY))
            .append(new LiteralText("• Usually for rules enforcement or compatibility.\n").formatted(Formatting.GRAY))
            .append(new LiteralText("\nApplies only while you stay on this server.").formatted(Formatting.ITALIC));

        HoverEvent hoverEvent = HoverEventResolver.createShowTextHoverEvent(hover);
        Style hoverStyle = Style.EMPTY
            .withFormatting(Formatting.YELLOW);
        if (hoverEvent != null) hoverStyle = hoverStyle.withHoverEvent(hoverEvent);

        final Style finalStyle = hoverStyle;
        MutableText msg = new LiteralText("Optimizer disabled on this server.").setStyle(finalStyle);
        return Main.PREFIX.shallowCopy().setStyle(finalStyle).append(msg);
    }
}
