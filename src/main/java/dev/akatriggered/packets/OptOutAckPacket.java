package dev.akatriggered.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class OptOutAckPacket implements CustomPayload {
    public static final CustomPayload.Id<OptOutAckPacket> TYPE =
        new CustomPayload.Id<>(Identifier.of("g1axcrystaloptimizer", "opt_out_ack"));
    public static final OptOutAckPacket INSTANCE = new OptOutAckPacket();
    public static final PacketCodec<RegistryByteBuf, OptOutAckPacket> STREAM_CODEC =
        new PacketCodec<>() {
            @Override public void encode(RegistryByteBuf buf, OptOutAckPacket value) {}
            @Override public OptOutAckPacket decode(RegistryByteBuf buf) { return INSTANCE; }
        };

    private OptOutAckPacket() {}

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
