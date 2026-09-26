package dev.akatriggered.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class OptOutAckPacket {

    public static final Identifier CHANNEL_ID = new Identifier("g1axcrystaloptimizer", "opt_out_ack");
    public static final OptOutAckPacket INSTANCE = new OptOutAckPacket();

    private OptOutAckPacket() {}

    public static void encode(OptOutAckPacket packet, PacketByteBuf buf) {}

    public static OptOutAckPacket decode(PacketByteBuf buf) {
        return INSTANCE;
    }
}
