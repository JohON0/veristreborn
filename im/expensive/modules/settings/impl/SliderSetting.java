/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.settings.impl;

import im.expensive.modules.settings.Setting;
import java.util.function.Supplier;

public class SliderSetting
extends Setting<Float> {
    public float min;
    public float max;
    public float increment;

    public SliderSetting(String name, float defaultVal, float min, float max, float increment) {
        super(name, Float.valueOf(defaultVal));
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public SliderSetting setVisible(Supplier<Boolean> bool) {
        return (SliderSetting)super.setVisible(bool);
    }
}

