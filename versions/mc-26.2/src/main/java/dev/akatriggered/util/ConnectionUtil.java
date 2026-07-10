package dev.akatriggered.util;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public final class ConnectionUtil {
    private ConnectionUtil() {}

    public static @Nullable String currentServerKey(Minecraft client) {
        return client.getCurrentServer() == null ? null : client.getCurrentServer().ip;
    }
}
