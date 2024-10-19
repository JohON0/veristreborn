/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;

@ModuleRegister(name="BetterMinecraft", category=Category.Misc)
public class BetterMinecraft
extends Module {
    public final BooleanSetting smoothCamera = new BooleanSetting("\u041f\u043b\u0430\u0432\u043d\u0430\u044f \u043a\u0430\u043c\u0435\u0440\u0430", true);
    public final BooleanSetting smoothTab = new BooleanSetting("\u041f\u043b\u0430\u0432\u043d\u044b\u0439 \u0442\u0430\u0431", true);
    public final BooleanSetting betterTab = new BooleanSetting("\u0423\u043b\u0443\u0447\u0448\u0435\u043d\u043d\u044b\u0439 \u0442\u0430\u0431", true);
    public final BooleanSetting betterChat = new BooleanSetting("\u0423\u043b\u0443\u0447\u0448\u0435\u043d\u043d\u044b\u0439 \u0447\u0430\u0442", true);

    public BetterMinecraft() {
        this.addSettings(this.smoothCamera, this.betterTab);
    }
}

