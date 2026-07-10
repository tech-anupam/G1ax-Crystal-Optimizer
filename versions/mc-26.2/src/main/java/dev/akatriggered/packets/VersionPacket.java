package dev.akatriggered.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record VersionPacket(int major, int minor, int patch, boolean snapshot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VersionPacket> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("g1axcrystaloptimizer", "version"));
    public static final StreamCodec<RegistryFriendlyByteBuf, VersionPacket> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public void encode(RegistryFriendlyByteBuf buf, VersionPacket v) {
                buf.writeVarInt(v.major());
                buf.writeVarInt(v.minor());
                buf.writeVarInt(v.patch());
                buf.writeBoolean(v.snapshot());
            }
            @Override
            public VersionPacket decode(RegistryFriendlyByteBuf buf) {
                return new VersionPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
            }
        };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
