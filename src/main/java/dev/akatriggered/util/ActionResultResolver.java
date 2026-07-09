package dev.akatriggered.util;

import net.minecraft.util.ActionResult;
import java.lang.reflect.Field;

public final class ActionResultResolver {
    private static ActionResult successValue;
    private static ActionResult consumeValue;
    private static ActionResult passValue;

    static {
        successValue = resolve("field_5812", "SUCCESS");
        consumeValue = resolve("field_5808", "CONSUME");
        passValue = resolve("field_5811", "PASS");
    }

    private static ActionResult resolve(String intermediary, String named) {
        try {
            Field f;
            try {
                f = ActionResult.class.getField(intermediary);
            } catch (NoSuchFieldException e) {
                f = ActionResult.class.getField(named);
            }
            return (ActionResult) f.get(null);
        } catch (Exception e) {
            try {
                if (ActionResult.class.isEnum()) {
                    for (Object constant : ActionResult.class.getEnumConstants()) {
                        if (constant.toString().equals(named)) {
                            return (ActionResult) constant;
                        }
                    }
                }
            } catch (Exception ignored) {}
            return null;
        }
    }

    public static ActionResult success() { return successValue; }
    public static ActionResult consume() { return consumeValue; }
    public static ActionResult pass() { return passValue; }

    public static boolean isAccepted(ActionResult result) {
        if (result == null) return false;
        return result == successValue || result == consumeValue;
    }
}
