package dev.akatriggered.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class OptOutPacket {

    public static final Identifier CHANNEL_ID = new Identifier("g1axcrystaloptimizer", "opt_out");

    private final boolean optOut;

    public OptOutPacket(boolean optOut) {
        this.optOut = optOut;
    }

    public boolean optOut() {
        return optOut;
    }

    public static void encode(OptOutPacket packet, PacketByteBuf buf) {
        buf.writeBoolean(packet.optOut);
    }

    public static OptOutPacket decode(PacketByteBuf buf) {
        return new OptOutPacket(buf.readBoolean());
    }
}
