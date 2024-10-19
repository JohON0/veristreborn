/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.settings.impl;

import im.expensive.modules.settings.Setting;
import java.util.function.Supplier;

public class StringSetting
extends Setting<String> {
    private final String description;
    private boolean onlyNumber;

    public StringSetting(String name, String defaultVal, String description) {
        super(name, defaultVal);
        this.description = description;
    }

    public StringSetting(String name, String defaultVal, String description, boolean onlyNumber) {
        super(name, defaultVal);
        this.description = description;
        this.onlyNumber = onlyNumber;
    }

    public StringSetting setVisible(Supplier<Boolean> bool) {
        return (StringSetting)super.setVisible(bool);
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isOnlyNumber() {
        return this.onlyNumber;
    }
}

