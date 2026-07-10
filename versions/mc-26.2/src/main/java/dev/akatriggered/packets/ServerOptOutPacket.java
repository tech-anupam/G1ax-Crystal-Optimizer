package dev.akatriggered.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class ServerOptOutPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerOptOutPacket> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("g1axcrystaloptimizer", "server_opt_out"));
    public static final ServerOptOutPacket INSTANCE = new ServerOptOutPacket();
    public static final StreamCodec<FriendlyByteBuf, ServerOptOutPacket> STREAM_CODEC =
        new StreamCodec<>() {
            @Override public void encode(FriendlyByteBuf buf, ServerOptOutPacket value) {}
            @Override public ServerOptOutPacket decode(FriendlyByteBuf buf) { return INSTANCE; }
        };

    private ServerOptOutPacket() {}

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
