package dev.akatriggered.util;

import net.minecraft.world.InteractionResult;
import java.lang.reflect.Field;

public final class ActionResultResolver {
    private static InteractionResult successValue;
    private static InteractionResult consumeValue;
    private static InteractionResult passValue;

    static {
        successValue = resolve("SUCCESS");
        consumeValue = resolve("CONSUME");
        passValue = resolve("PASS");
    }

    private static InteractionResult resolve(String named) {
        try {
            Field f = InteractionResult.class.getField(named);
            return (InteractionResult) f.get(null);
        } catch (Exception e) {
            try {
                if (InteractionResult.class.isEnum()) {
                    for (Object constant : InteractionResult.class.getEnumConstants()) {
                        if (constant.toString().equals(named)) {
                            return (InteractionResult) constant;
                        }
                    }
                }
            } catch (Exception ignored) {}
            try {
                for (Field f : InteractionResult.class.getDeclaredFields()) {
                    f.setAccessible(true);
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers())
                        && InteractionResult.class.isAssignableFrom(f.getType())) {
                        Object val = f.get(null);
                        if (val != null && val.toString().toUpperCase().contains(named)) {
                            return (InteractionResult) val;
                        }
                    }
                }
            } catch (Exception ignored) {}
            return null;
        }
    }

    public static InteractionResult success() { return successValue; }
    public static InteractionResult consume() { return consumeValue; }
    public static InteractionResult pass() { return passValue; }

    public static boolean isAccepted(InteractionResult result) {
        if (result == null) return false;
        return result == successValue || result == consumeValue;
    }
}
