package dev.akatriggered.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record VersionPacket(int major, int minor, int patch, boolean snapshot) implements CustomPayload {
    public static final CustomPayload.Id<VersionPacket> TYPE =
        new CustomPayload.Id<>(Identifier.of("g1axcrystaloptimizer", "version"));
    public static final PacketCodec<RegistryByteBuf, VersionPacket> STREAM_CODEC =
        new PacketCodec<>() {
            @Override
            public void encode(RegistryByteBuf buf, VersionPacket v) {
                buf.writeVarInt(v.major());
                buf.writeVarInt(v.minor());
                buf.writeVarInt(v.patch());
                buf.writeBoolean(v.snapshot());
            }
            @Override
            public VersionPacket decode(RegistryByteBuf buf) {
                return new VersionPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
            }
        };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
