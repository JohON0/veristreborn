/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.settings.impl;

import im.expensive.modules.settings.Setting;
import java.util.function.Supplier;

public class BindSetting
extends Setting<Integer> {
    public BindSetting(String name, Integer defaultVal) {
        super(name, defaultVal);
    }

    public BindSetting setVisible(Supplier<Boolean> bool) {
        return (BindSetting)super.setVisible(bool);
    }
}

