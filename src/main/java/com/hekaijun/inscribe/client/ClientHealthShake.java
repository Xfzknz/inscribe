package com.hekaijun.inscribe.client;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public final class ClientHealthShake {
    public static void start() {
        Minecraft minecraft = Minecraft.getInstance();
        Gui gui = minecraft.gui;
        if (gui == null) {
            return;
        }

        long target = gui.getGuiTicks() + 10L;
        Field field = findBlinkField();
        if (field != null) {
            try {
                field.setLong(gui, target);
                return;
            } catch (ReflectiveOperationException ignored) {
                // 失败时继续尝试其他字段。
            }
        }

        for (Field candidate : Gui.class.getDeclaredFields()) {
            if (candidate.getType() != long.class) {
                continue;
            }
            try {
                candidate.setAccessible(true);
                long value = candidate.getLong(gui);
                // lastHealthTime 是毫秒时间戳，healthBlinkTime 是 GUI tick。
                if (value >= 0L && value < 1_000_000L) {
                    candidate.setLong(gui, target);
                    return;
                }
            } catch (ReflectiveOperationException ignored) {
                // 继续尝试下一个字段。
            }
        }
    }

    private static Field findBlinkField() {
        // 开发环境是 official 名，生产环境是 SRG 名。
        for (String name : new String[]{"healthBlinkTime", "f_92976_"}) {
            try {
                Field field = Gui.class.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (ReflectiveOperationException ignored) {
                // 尝试下一个名字。
            }
        }
        return null;
    }

    private ClientHealthShake() {
    }
}
