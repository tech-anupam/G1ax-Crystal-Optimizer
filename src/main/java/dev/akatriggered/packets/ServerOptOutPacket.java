package dev.akatriggered.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class ServerOptOutPacket implements CustomPayload {
    public static final CustomPayload.Id<ServerOptOutPacket> TYPE =
        new CustomPayload.Id<>(Identifier.of("g1axcrystaloptimizer", "server_opt_out"));
    public static final ServerOptOutPacket INSTANCE = new ServerOptOutPacket();
    public static final PacketCodec<PacketByteBuf, ServerOptOutPacket> STREAM_CODEC =
        new PacketCodec<>() {
            @Override public void encode(PacketByteBuf buf, ServerOptOutPacket value) {}
            @Override public ServerOptOutPacket decode(PacketByteBuf buf) { return INSTANCE; }
        };

    private ServerOptOutPacket() {}

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
