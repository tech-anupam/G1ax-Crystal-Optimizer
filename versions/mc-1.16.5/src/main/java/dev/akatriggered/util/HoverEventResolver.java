package dev.akatriggered.util;

import dev.akatriggered.Main;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class HoverEventResolver {
    private HoverEventResolver() {}

    public static HoverEvent createShowTextHoverEvent(Text text) {
        try {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
        } catch (Throwable t) {
            try {
                Class<?> actionClass;
                try {
                    actionClass = Class.forName("net.minecraft.text.HoverEvent$Action");
                } catch (ClassNotFoundException e) {
                    actionClass = Class.forName("net.minecraft.class_2568$class_5247");
                }

                Object showTextAction = null;
                try {
                    Field f = actionClass.getDeclaredField("SHOW_TEXT");
                    f.setAccessible(true);
                    showTextAction = f.get(null);
                } catch (NoSuchFieldException e) {
                    for (Field f : actionClass.getDeclaredFields()) {
                        if (Modifier.isStatic(f.getModifiers()) && f.getType().equals(actionClass)) {
                            f.setAccessible(true);
                            Object val = f.get(null);
                            if (val != null && val.toString().contains("show_text")) {
                                showTextAction = val;
                                break;
                            }
                        }
                    }
                }

                if (showTextAction == null) throw new IllegalStateException("Could not resolve SHOW_TEXT action");

                Constructor<?> hoverConst = null;
                for (Constructor<?> c : HoverEvent.class.getConstructors()) {
                    Class<?>[] params = c.getParameterTypes();
                    if (params.length == 2 && params[0].isAssignableFrom(actionClass)) {
                        hoverConst = c;
                        break;
                    }
                }
                if (hoverConst == null) throw new NoSuchMethodException("HoverEvent constructor not found");
                hoverConst.setAccessible(true);
                return (HoverEvent) hoverConst.newInstance(showTextAction, text);

            } catch (Throwable ex) {
                Main.getLogger().error("Failed to resolve HoverEvent: " + ex.getMessage());
                return null;
            }
        }
    }
}
