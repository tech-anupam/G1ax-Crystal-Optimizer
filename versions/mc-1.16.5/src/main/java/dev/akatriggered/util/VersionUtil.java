package dev.akatriggered.util;

import dev.akatriggered.packets.VersionPacket;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionUtil {
    private static final Pattern SEMVER = Pattern.compile(
        "^(\\d+)\\.(\\d+)\\.(\\d+)(?:-SNAPSHOT)?$", Pattern.CASE_INSENSITIVE);

    private VersionUtil() {}

    public static String getModVersion() {
        return FabricLoader.getInstance()
            .getModContainer("g1axcrystaloptimizer")
            .map(ModContainer::getMetadata)
            .map(m -> m.getVersion().getFriendlyString())
            .orElse("unknown");
    }

    public static VersionPacket createVersionPacket() {
        return parseToVersionPacket(getModVersion());
    }

    public static VersionPacket parseToVersionPacket(String v) {
        if (v == null) return new VersionPacket(0, 0, 0, false);
        v = v.trim();
        boolean snapshot = v.toUpperCase(Locale.ROOT).endsWith("-SNAPSHOT");
        Matcher m = SEMVER.matcher(v);
        if (!m.matches()) return new VersionPacket(0, 0, 0, snapshot);
        return new VersionPacket(
            Integer.parseInt(m.group(1)),
            Integer.parseInt(m.group(2)),
            Integer.parseInt(m.group(3)),
            snapshot
        );
    }
}
