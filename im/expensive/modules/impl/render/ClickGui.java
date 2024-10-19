/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.ui.notify.impl.WarningNotify;

@ModuleRegister(name="ClickGui", category=Category.Render)
public class ClickGui
extends Module {
    public static BindSetting bind = new BindSetting("\u041a\u043d\u043e\u043f\u043a\u0430 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f", 344);
    public static BooleanSetting gradient = new BooleanSetting("\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442", true);
    public static BooleanSetting background = new BooleanSetting("\u0424\u043e\u043d", true);
    public static BooleanSetting blur = new BooleanSetting("\u0420\u0430\u0437\u043c\u044b\u0442\u044c", false);
    public static SliderSetting blurPower = new SliderSetting("\u0421\u0438\u043b\u0430 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f", 2.0f, 1.0f, 4.0f, 1.0f).setVisible(() -> (Boolean)blur.get());
    public static BooleanSetting images = new BooleanSetting("\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0438", true);
    public static ModeSetting imageType = new ModeSetting("\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0430", "MyLove", "MyLove", "CatGirl", "Pinky", "KisKis", "FurryMaid", "Nyashka", "Miku", "Novoura").setVisible(() -> (Boolean)images.get());

    public ClickGui() {
        this.addSettings(bind, background, gradient, blur, blurPower, images, imageType);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        this.toggle();
        Expensive.getInstance().getNotifyManager().add(0, new WarningNotify("\u0417\u0430\u0447\u0435\u043c, \u0431\u0440\u043e\u0443?", 3000L));
    }
}

