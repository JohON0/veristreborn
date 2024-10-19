/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.StringSetting;
import net.minecraft.client.Minecraft;

@ModuleRegister(name="NameProtect", category=Category.Misc)
public class NameProtect
extends Module {
    public static String fakeName = "";
    public StringSetting name = new StringSetting("\u0417\u0430\u043c\u0435\u043d\u044f\u0435\u043c\u043e\u0435 \u0418\u043c\u044f", "Protected", "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0442\u0435\u043a\u0441\u0442 \u0434\u043b\u044f \u0437\u0430\u043c\u0435\u043d\u044b \u0432\u0430\u0448\u0435\u0433\u043e \u0438\u0433\u0440\u043e\u0432\u043e\u0433\u043e \u043d\u0438\u043a\u0430");

    public NameProtect() {
        this.addSettings(this.name);
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        fakeName = (String)this.name.get();
    }

    public static String getReplaced(String input) {
        if (Expensive.getInstance() != null && Expensive.getInstance().getModuleManager().getNameProtect().isState()) {
            input = input.replace(Minecraft.getInstance().session.getUsername(), fakeName);
        }
        return input;
    }
}

