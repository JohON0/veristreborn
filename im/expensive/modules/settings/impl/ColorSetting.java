/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.settings.impl;

import im.expensive.modules.settings.Setting;
import java.util.function.Supplier;

public class ColorSetting
extends Setting<Integer> {
    public ColorSetting(String name, Integer defaultVal) {
        super(name, defaultVal);
    }

    public ColorSetting setVisible(Supplier<Boolean> bool) {
        return (ColorSetting)super.setVisible(bool);
    }
}

