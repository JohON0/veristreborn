/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.settings;

import im.expensive.modules.settings.ISetting;
import java.util.function.Supplier;

public class Setting<Value>
implements ISetting {
    Value defaultVal;
    String settingName;
    public Supplier<Boolean> visible = () -> true;

    public Setting(String name, Value defaultVal) {
        this.settingName = name;
        this.defaultVal = defaultVal;
    }

    public String getName() {
        return this.settingName;
    }

    public void set(Value value) {
        this.defaultVal = value;
    }

    @Override
    public Setting<?> setVisible(Supplier<Boolean> bool) {
        this.visible = bool;
        return this;
    }

    public Value get() {
        return this.defaultVal;
    }
}

