/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;

@ModuleRegister(name="AutoSprint", category=Category.Movement)
public class AutoSprint
extends Module {
    public BooleanSetting saveSprint = new BooleanSetting("\u0421\u043e\u0445\u0440\u0430\u043d\u044f\u0442\u044c \u0441\u043f\u0440\u0438\u043d\u0442", true);

    public AutoSprint() {
        this.addSettings(this.saveSprint);
    }
}

