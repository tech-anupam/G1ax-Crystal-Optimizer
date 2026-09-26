package dev.akatriggered.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class VersionPacket {

    public static final Identifier CHANNEL_ID = new Identifier("g1axcrystaloptimizer", "version");

    private final int major;
    private final int minor;
    private final int patch;
    private final boolean snapshot;

    public VersionPacket(int major, int minor, int patch, boolean snapshot) {
        this.major    = major;
        this.minor    = minor;
        this.patch    = patch;
        this.snapshot = snapshot;
    }

    public int major()       { return major; }
    public int minor()       { return minor; }
    public int patch()       { return patch; }
    public boolean snapshot() { return snapshot; }

    public static void encode(VersionPacket packet, PacketByteBuf buf) {
        buf.writeVarInt(packet.major);
        buf.writeVarInt(packet.minor);
        buf.writeVarInt(packet.patch);
        buf.writeBoolean(packet.snapshot);
    }

    public static VersionPacket decode(PacketByteBuf buf) {
        return new VersionPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
    }
}
