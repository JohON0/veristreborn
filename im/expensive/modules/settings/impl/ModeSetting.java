/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.settings.impl;

import im.expensive.modules.settings.Setting;
import java.util.function.Supplier;

public class ModeSetting
extends Setting<String> {
    public String[] strings;

    public ModeSetting(String name, String defaultVal, String ... strings) {
        super(name, defaultVal);
        this.strings = strings;
    }

    public int getIndex() {
        int index = 0;
        for (String val2 : this.strings) {
            if (val2.equalsIgnoreCase((String)this.get())) {
                return index;
            }
            ++index;
        }
        return 0;
    }

    public boolean is(String s) {
        return ((String)this.get()).equalsIgnoreCase(s);
    }

    public ModeSetting setVisible(Supplier<Boolean> bool) {
        return (ModeSetting)super.setVisible(bool);
    }
}

