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
            Class<?> actionClass = null;
            try {
                actionClass = Class.forName("net.minecraft.class_2568$class_5247");
            } catch (ClassNotFoundException e) {
                actionClass = Class.forName("net.minecraft.text.HoverEvent$Action");
            }

            Object showTextAction = null;
            try {
                Field f = actionClass.getDeclaredField("field_24314");
                f.setAccessible(true);
                showTextAction = f.get(null);
            } catch (NoSuchFieldException e) {
                try {
                    Field f = actionClass.getDeclaredField("SHOW_TEXT");
                    f.setAccessible(true);
                    showTextAction = f.get(null);
                } catch (NoSuchFieldException ex) {
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
            }

            if (showTextAction == null) {
                throw new IllegalStateException("Could not resolve SHOW_TEXT action");
            }

            Class<?> hoverEventClass = HoverEvent.class;
            Class<?> showTextClass = null;
            try {
                showTextClass = Class.forName("net.minecraft.class_2568$class_10613");
            } catch (ClassNotFoundException e) {
                try {
                    showTextClass = Class.forName("net.minecraft.text.HoverEvent$ShowText");
                } catch (ClassNotFoundException ex) {
                    // 1.21 / 1.21.1 environment
                }
            }

            Constructor<?> hoverConst = null;
            for (Constructor<?> c : hoverEventClass.getConstructors()) {
                Class<?>[] params = c.getParameterTypes();
                if (params.length == 2 && params[0].isAssignableFrom(actionClass)) {
                    hoverConst = c;
                    break;
                }
            }
            if (hoverConst == null) {
                throw new NoSuchMethodException("HoverEvent constructor not found");
            }
            hoverConst.setAccessible(true);

            if (showTextClass != null) {
                Constructor<?> showTextConst = null;
                for (Constructor<?> c : showTextClass.getConstructors()) {
                    Class<?>[] params = c.getParameterTypes();
                    if (params.length == 1 && params[0].isAssignableFrom(Text.class)) {
                        showTextConst = c;
                        break;
                    }
                }
                if (showTextConst == null) {
                    throw new NoSuchMethodException("HoverEvent.ShowText constructor not found");
                }
                showTextConst.setAccessible(true);
                Object showTextInstance = showTextConst.newInstance(text);
                return (HoverEvent) hoverConst.newInstance(showTextAction, showTextInstance);
            } else {
                return (HoverEvent) hoverConst.newInstance(showTextAction, text);
            }

        } catch (Throwable t) {
            Main.getLogger().error("Failed to resolve HoverEvent: " + t.getMessage());
            return null;
        }
    }
}
