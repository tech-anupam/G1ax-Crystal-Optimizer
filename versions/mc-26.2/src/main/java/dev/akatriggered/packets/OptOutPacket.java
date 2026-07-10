package dev.akatriggered.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OptOutPacket(boolean optOut) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OptOutPacket> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("g1axcrystaloptimizer", "opt_out"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OptOutPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, OptOutPacket::optOut,
            OptOutPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
