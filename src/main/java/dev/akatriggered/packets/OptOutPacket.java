package dev.akatriggered.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OptOutPacket(boolean optOut) implements CustomPayload {
    public static final CustomPayload.Id<OptOutPacket> TYPE = new CustomPayload.Id<>(Identifier.of("g1axcrystaloptimizer", "opt_out"));
    public static final PacketCodec<RegistryByteBuf, OptOutPacket> STREAM_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, OptOutPacket::optOut,
            OptOutPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
