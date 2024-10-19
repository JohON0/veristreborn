/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.themes;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.ModuleManager;
import im.expensive.modules.impl.render.HUD;
import im.expensive.utils.render.color.ColorUtils;

public class Theme {
    ModuleManager moduleManager = Expensive.getInstance().getModuleManager();
    HUD hud = this.moduleManager.getHud();
    public static int textColor;
    public static int darkTextColor;
    public static int mainRectColor;
    public static int darkMainRectColor;
    public static int rectColor;

    public Theme() {
        Expensive.getInstance().getEventBus().register(this);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        this.updateTheme();
    }

    public void updateTheme() {
        if (HUD.theme.is("\u0421\u0438\u043d\u0438\u0439")) {
            textColor = ColorUtils.rgb(124, 144, 222);
            darkMainRectColor = ColorUtils.rgb(33, 37, 54);
            mainRectColor = ColorUtils.rgb(48, 57, 94);
            rectColor = ColorUtils.rgb(69, 101, 181);
            darkTextColor = ColorUtils.rgb(95, 112, 176);
        }
        if (HUD.theme.is("\u0424\u0438\u043e\u043b\u0435\u0442\u043e\u0432\u044b\u0439")) {
            textColor = ColorUtils.rgb(165, 124, 222);
            darkMainRectColor = ColorUtils.rgb(45, 33, 54);
            mainRectColor = ColorUtils.rgb(76, 48, 94);
            rectColor = ColorUtils.rgb(119, 69, 181);
            darkTextColor = ColorUtils.rgb(129, 95, 176);
        }
        if (HUD.theme.is("\u041a\u0440\u0430\u0441\u043d\u044b\u0439")) {
            textColor = ColorUtils.rgb(222, 124, 124);
            darkMainRectColor = ColorUtils.rgb(54, 33, 33);
            mainRectColor = ColorUtils.rgb(94, 48, 48);
            rectColor = ColorUtils.rgb(181, 69, 69);
            darkTextColor = ColorUtils.rgb(176, 95, 100);
        }
        if (HUD.theme.is("\u0417\u0435\u043b\u0435\u043d\u044b\u0439")) {
            textColor = ColorUtils.rgb(124, 222, 137);
            darkMainRectColor = ColorUtils.rgb(33, 54, 35);
            mainRectColor = ColorUtils.rgb(48, 94, 57);
            rectColor = ColorUtils.rgb(69, 181, 101);
            darkTextColor = ColorUtils.rgb(95, 176, 106);
        }
        if (HUD.theme.is("\u0422\u0435\u043c\u043d\u044b\u0439")) {
            textColor = ColorUtils.rgb(255, 255, 255);
            darkMainRectColor = ColorUtils.rgb(20, 20, 20);
            mainRectColor = ColorUtils.rgb(48, 48, 48);
            rectColor = ColorUtils.rgb(61, 61, 61);
            darkTextColor = ColorUtils.rgb(100, 100, 100);
        }
        if (HUD.theme.is("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439")) {
            textColor = ColorUtils.rgb(255, 255, 255);
            rectColor = (Integer)HUD.customMainColor.get();
            darkMainRectColor = (Integer)HUD.customDarkColor.get();
            darkTextColor = ColorUtils.rgb(100, 100, 100);
        }
    }
}

