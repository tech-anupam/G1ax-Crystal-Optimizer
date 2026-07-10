package dev.akatriggered.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class OptOutAckPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OptOutAckPacket> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("g1axcrystaloptimizer", "opt_out_ack"));
    public static final OptOutAckPacket INSTANCE = new OptOutAckPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, OptOutAckPacket> STREAM_CODEC =
        new StreamCodec<>() {
            @Override public void encode(RegistryFriendlyByteBuf buf, OptOutAckPacket value) {}
            @Override public OptOutAckPacket decode(RegistryFriendlyByteBuf buf) { return INSTANCE; }
        };

    private OptOutAckPacket() {}

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
