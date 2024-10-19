/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.settings.impl;

import im.expensive.modules.settings.Setting;
import im.expensive.modules.settings.impl.BooleanSetting;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ModeListSetting
extends Setting<List<BooleanSetting>> {
    public ModeListSetting(String name, BooleanSetting ... strings) {
        super(name, Arrays.asList(strings));
    }

    public BooleanSetting getValueByName(String settingName) {
        return ((List)this.get()).stream().filter(booleanSetting -> booleanSetting.wait().equalsIgnoreCase(settingName)).findFirst().orElse(null);
    }

    public BooleanSetting get(int index) {
        return (BooleanSetting)((List)this.get()).get(index);
    }

    public ModeListSetting setVisible(Supplier<Boolean> bool) {
        return (ModeListSetting)super.setVisible(bool);
    }
}

