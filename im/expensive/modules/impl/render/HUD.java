/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventDisplay;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ColorSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.ui.display.impl.ArmorRenderer;
import im.expensive.ui.display.impl.InfoRenderer;
import im.expensive.ui.display.impl.KeyBindRenderer;
import im.expensive.ui.display.impl.PotionRenderer;
import im.expensive.ui.display.impl.SchedulesRenderer;
import im.expensive.ui.display.impl.StaffListRenderer;
import im.expensive.ui.display.impl.TargetInfoRenderer;
import im.expensive.ui.display.impl.TimerRenderer;
import im.expensive.ui.display.impl.WatermarkRenderer;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.drag.Dragging;
import im.expensive.utils.render.color.ColorUtils;

@ModuleRegister(name="HUD", category=Category.Render)
public class HUD
extends Module {
    public final ModeListSetting elements = new ModeListSetting("\u042d\u043b\u0435\u043c\u0435\u043d\u0442\u044b", new BooleanSetting("\u0412\u0430\u0442\u0435\u0440\u043c\u0430\u0440\u043a\u0430", true), new BooleanSetting("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f", true), new BooleanSetting("\u041b\u0438\u0441\u0442 \u044d\u0444\u0444\u0435\u043a\u0442\u043e\u0432", true), new BooleanSetting("\u041b\u0438\u0441\u0442 \u043c\u043e\u0434\u0435\u0440\u043e\u0432", true), new BooleanSetting("\u041b\u0438\u0441\u0442 \u0431\u0438\u043d\u0434\u043e\u0432", true), new BooleanSetting("\u041b\u0438\u0441\u0442 \u0441\u043e\u0431\u044b\u0442\u0438\u0439 (ReallyWorld)", false), new BooleanSetting("\u0422\u0430\u0440\u0433\u0435\u0442 \u0445\u0443\u0434", true), new BooleanSetting("\u0422\u0430\u0439\u043c\u0435\u0440", false), new BooleanSetting("\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f", true), new BooleanSetting("\u0411\u0440\u043e\u043d\u044f", true));
    public final ModeSetting waterMarkMode = new ModeSetting("\u0412\u0438\u0434 \u0432\u0430\u0442\u0435\u0440\u043c\u0430\u0440\u043a\u0438", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041f\u043b\u0438\u0442\u043a\u0430", "\u0412\u0440\u0435\u043c\u044f", "\u0422\u0430\u0431\u043b\u0438\u0447\u043a\u0430").setVisible(() -> (Boolean)this.elements.getValueByName("\u0412\u0430\u0442\u0435\u0440\u043c\u0430\u0440\u043a\u0430").get());
    public final ModeSetting tHudMode = new ModeSetting("\u0412\u0438\u0434 \u0442-\u0445\u0443\u0434\u0430", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041d\u0435\u043e\u043d").setVisible(() -> (Boolean)this.elements.getValueByName("\u0422\u0430\u0440\u0433\u0435\u0442 \u0445\u0443\u0434").get());
    public final ModeListSetting waterMarkOptions = new ModeListSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0432 \u0432\u0442", new BooleanSetting("\u0424\u043f\u0441", true), new BooleanSetting("\u041f\u0438\u043d\u0433", true), new BooleanSetting("\u0421\u0435\u0440\u0432\u0435\u0440", true)).setVisible(() -> (Boolean)this.elements.getValueByName("\u0412\u0430\u0442\u0435\u0440\u043c\u0430\u0440\u043a\u0430").get() != false && this.waterMarkMode.is("\u041e\u0431\u044b\u0447\u043d\u044b\u0439"));
    public final ModeListSetting infoOptions = new ModeListSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0432 \u0438\u043d\u0444\u043e", new BooleanSetting("\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b", true), new BooleanSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", true), new BooleanSetting("\u0422\u041f\u0421", true)).setVisible(() -> (Boolean)this.elements.getValueByName("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f").get());
    public static final ModeSetting theme = new ModeSetting("\u0422\u0435\u043c\u0430", "\u0421\u0438\u043d\u0438\u0439", "\u0421\u0438\u043d\u0438\u0439", "\u0424\u0438\u043e\u043b\u0435\u0442\u043e\u0432\u044b\u0439", "\u0422\u0435\u043c\u043d\u044b\u0439", "\u041a\u0440\u0430\u0441\u043d\u044b\u0439", "\u0417\u0435\u043b\u0435\u043d\u044b\u0439", "\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439");
    public static final ColorSetting customMainColor = new ColorSetting("\u041e\u0431\u0432\u043e\u0434\u043a\u0430", ColorUtils.rgb(255, 255, 255)).setVisible(() -> theme.is("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439"));
    public static final ColorSetting customDarkColor = new ColorSetting("\u0420\u0435\u043a\u0442\u044b", ColorUtils.rgb(0, 0, 0)).setVisible(() -> theme.is("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439"));
    private final WatermarkRenderer watermarkRenderer = new WatermarkRenderer();
    private final InfoRenderer infoRenderer = new InfoRenderer();
    private final PotionRenderer potionRenderer;
    private final TimerRenderer timerRenderer;
    private final KeyBindRenderer keyBindRenderer;
    private final TargetInfoRenderer targetInfoRenderer;
    private final ArmorRenderer armorRenderer;
    private final StaffListRenderer staffListRenderer;
    private final SchedulesRenderer schedulesRenderer;
    private Theme Theme;

    public HUD() {
        Dragging potions = Expensive.getInstance().createDrag(this, "Potions", 278.0f, 5.0f);
        this.armorRenderer = new ArmorRenderer();
        Dragging timerInfo = Expensive.getInstance().createDrag(this, "Timer", 300.0f, 20.0f);
        Dragging keyBinds = Expensive.getInstance().createDrag(this, "KeyBinds", 185.0f, 5.0f);
        Dragging dragging = Expensive.getInstance().createDrag(this, "TargetHUD", 74.0f, 128.0f);
        Dragging staffList = Expensive.getInstance().createDrag(this, "StaffList", 96.0f, 5.0f);
        Dragging schedules = Expensive.getInstance().createDrag(this, "Schedules", 165.0f, 5.0f);
        this.potionRenderer = new PotionRenderer(potions);
        this.keyBindRenderer = new KeyBindRenderer(keyBinds);
        this.staffListRenderer = new StaffListRenderer(staffList);
        this.targetInfoRenderer = new TargetInfoRenderer(dragging);
        this.timerRenderer = new TimerRenderer(timerInfo);
        this.schedulesRenderer = new SchedulesRenderer(schedules);
        this.addSettings(this.elements, this.waterMarkMode, this.tHudMode, this.waterMarkOptions, this.infoOptions, theme, customMainColor, customDarkColor);
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (HUD.mc.gameSettings.showDebugInfo) {
            return;
        }
        this.Theme.updateTheme();
        if (((Boolean)this.elements.getValueByName("\u041b\u0438\u0441\u0442 \u043c\u043e\u0434\u0435\u0440\u043e\u0432").get()).booleanValue()) {
            this.staffListRenderer.update(e);
        }
        if (((Boolean)this.elements.getValueByName("\u0422\u0430\u0439\u043c\u0435\u0440").get()).booleanValue()) {
            this.timerRenderer.update(e);
        }
        if (((Boolean)this.elements.getValueByName("\u041b\u0438\u0441\u0442 \u0441\u043e\u0431\u044b\u0442\u0438\u0439 (ReallyWorld)").get()).booleanValue()) {
            this.schedulesRenderer.update(e);
        }
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        if (HUD.mc.gameSettings.showDebugInfo || e.getType() != EventDisplay.Type.POST) {
            return;
        }
        if (((Boolean)this.elements.getValueByName("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f").get()).booleanValue()) {
            this.infoRenderer.render(e);
        }
        if (((Boolean)this.elements.getValueByName("\u041b\u0438\u0441\u0442 \u0441\u043e\u0431\u044b\u0442\u0438\u0439 (ReallyWorld)").get()).booleanValue()) {
            this.schedulesRenderer.render(e);
        }
        if (((Boolean)this.elements.getValueByName("\u041b\u0438\u0441\u0442 \u044d\u0444\u0444\u0435\u043a\u0442\u043e\u0432").get()).booleanValue()) {
            this.potionRenderer.render(e);
        }
        if (((Boolean)this.elements.getValueByName("\u041b\u0438\u0441\u0442 \u0431\u0438\u043d\u0434\u043e\u0432").get()).booleanValue()) {
            this.keyBindRenderer.render(e);
        }
        if (((Boolean)this.elements.getValueByName("\u041b\u0438\u0441\u0442 \u043c\u043e\u0434\u0435\u0440\u043e\u0432").get()).booleanValue()) {
            this.staffListRenderer.render(e);
        }
        if (((Boolean)this.elements.getValueByName("\u0422\u0430\u0440\u0433\u0435\u0442 \u0445\u0443\u0434").get()).booleanValue()) {
            this.targetInfoRenderer.render(e);
        }
        if (((Boolean)this.elements.getValueByName("\u0412\u0430\u0442\u0435\u0440\u043c\u0430\u0440\u043a\u0430").get()).booleanValue()) {
            this.watermarkRenderer.render(e);
        }
        if (((Boolean)this.elements.getValueByName("\u0411\u0440\u043e\u043d\u044f").get()).booleanValue()) {
            this.armorRenderer.render(e);
        }
        if (((Boolean)this.elements.getValueByName("\u0422\u0430\u0439\u043c\u0435\u0440").get()).booleanValue()) {
            this.timerRenderer.render(e);
        }
    }

    public void updateThemeColors() {
        if (theme.is("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439")) {
            im.expensive.ui.themes.Theme.darkMainRectColor = (Integer)customDarkColor.get();
            im.expensive.ui.themes.Theme.mainRectColor = (Integer)customMainColor.get();
        }
    }

    @Subscribe
    public void onUpdateTheme(EventUpdate e) {
        this.updateThemeColors();
    }

    public static int getColor(int firstColor, int secondColor, int index, float mult) {
        return ColorUtils.gradient(firstColor, secondColor, (int)((float)index * mult), 10);
    }
}

