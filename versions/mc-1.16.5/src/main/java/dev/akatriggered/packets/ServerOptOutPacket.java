package dev.akatriggered.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class ServerOptOutPacket {

    public static final Identifier CHANNEL_ID = new Identifier("g1axcrystaloptimizer", "server_opt_out");
    public static final ServerOptOutPacket INSTANCE = new ServerOptOutPacket();

    private ServerOptOutPacket() {}

    public static void encode(ServerOptOutPacket packet, PacketByteBuf buf) {}

    public static ServerOptOutPacket decode(PacketByteBuf buf) {
        return INSTANCE;
    }
}
