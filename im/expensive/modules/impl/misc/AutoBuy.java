/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BindSetting;

@ModuleRegister(name="AutoBuy", category=Category.Misc)
public class AutoBuy
extends Module {
    public BindSetting setting = new BindSetting("\u041a\u043d\u043e\u043f\u043a\u0430 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f", -1);

    public AutoBuy() {
        this.addSettings(this.setting);
    }
}

