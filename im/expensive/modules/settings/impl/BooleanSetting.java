/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.settings.impl;

import im.expensive.modules.settings.Setting;
import java.util.function.Supplier;

public class BooleanSetting
extends Setting<Boolean> {
    public BooleanSetting(String name, Boolean defaultVal) {
        super(name, defaultVal);
    }

    public BooleanSetting setVisible(Supplier<Boolean> bool) {
        return (BooleanSetting)super.setVisible(bool);
    }
}

