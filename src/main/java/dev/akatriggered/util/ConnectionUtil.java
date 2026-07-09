package dev.akatriggered.util;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

public final class ConnectionUtil {
    private ConnectionUtil() {}

    public static @Nullable String currentServerKey(MinecraftClient client) {
        return client.getCurrentServerEntry() == null ? null : client.getCurrentServerEntry().address;
    }
}
