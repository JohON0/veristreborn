/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.math;

import im.expensive.utils.client.IMinecraft;

public final class SensUtils
implements IMinecraft {
    public static float getSensitivity(float rot) {
        return SensUtils.getDeltaMouse(rot) * SensUtils.getGCDValue();
    }

    public static float getGCDValue() {
        return (float)((double)SensUtils.getGCD() * 0.15);
    }

    public static float getGCD() {
        float f1 = (float)(SensUtils.mc.gameSettings.mouseSensitivity * 0.6 + 0.2);
        return f1 * f1 * f1 * 8.0f;
    }

    public static float getDeltaMouse(float delta) {
        return Math.round(delta / SensUtils.getGCDValue());
    }

    private SensUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

