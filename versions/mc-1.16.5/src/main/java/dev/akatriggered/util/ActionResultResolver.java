package dev.akatriggered.util;

import net.minecraft.util.ActionResult;

import java.lang.reflect.Field;

public final class ActionResultResolver {
    private static ActionResult successValue;
    private static ActionResult consumeValue;
    private static ActionResult passValue;

    static {
        successValue = resolve("SUCCESS");
        consumeValue = resolve("CONSUME");
        passValue    = resolve("PASS");
    }

    private static ActionResult resolve(String named) {
        try {
            if (ActionResult.class.isEnum()) {
                for (Object constant : ActionResult.class.getEnumConstants()) {
                    if (constant.toString().equals(named)) {
                        return (ActionResult) constant;
                    }
                }
            }
        } catch (Exception ignored) {}
        try {
            Field f = ActionResult.class.getField(named);
            return (ActionResult) f.get(null);
        } catch (Exception ignored) {}
        return null;
    }

    public static ActionResult success() { return successValue; }
    public static ActionResult consume() { return consumeValue; }
    public static ActionResult pass()    { return passValue; }

    public static boolean isAccepted(ActionResult result) {
        if (result == null) return false;
        return result == successValue || result == consumeValue;
    }
}
