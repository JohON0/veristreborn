/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.misc.SelfDestruct;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ColorSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.ui.notify.impl.WarningNotify;
import im.expensive.utils.render.color.ColorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.ResourceLocation;

@ModuleRegister(name="WorldTweaks", category=Category.Render)
public class WorldTweaks
extends Module {
    public static ModeListSetting options = new ModeListSetting("\u041e\u043f\u0446\u0438\u0438", new BooleanSetting("\u041c\u0430\u043b\u0435\u043d\u044c\u043a\u0438\u0439 \u0438\u0433\u0440\u043e\u043a", false), new BooleanSetting("\u0412\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u043a\u0438\u043d", true), new BooleanSetting("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u0442\u0443\u043c\u0430\u043d", true), new BooleanSetting("\u0412\u0440\u0435\u043c\u044f", true));
    public static ModeSetting mode = new ModeSetting("\u0412\u0438\u0434", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u0432\u043e\u0439").setVisible(() -> (Boolean)options.getValueByName("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u0442\u0443\u043c\u0430\u043d").get());
    public static ColorSetting colorFog = new ColorSetting("\u0426\u0432\u0435\u0442 \u0442\u0443\u043c\u0430\u043d\u0430", ColorUtils.rgb(255, 255, 255)).setVisible(() -> mode.is("\u0421\u0432\u043e\u0439"));
    private static ModeSetting selfSkinType = new ModeSetting("\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0430", "Boy3", "Boy", "Boy2", "Boy3", "Sonic", "Sonic2", "Sonic3", "Sonic4", "Tails", "Shadow", "Knuckles", "Cattail", "Cattail2").setVisible(() -> (Boolean)options.getValueByName("\u0412\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u043a\u0438\u043d").get());
    private static ModeSetting time = new ModeSetting("\u0412\u0440\u0435\u043c\u044f", "\u041d\u043e\u0447\u044c", "\u041d\u043e\u0447\u044c", "\u0414\u0435\u043d\u044c").setVisible(() -> (Boolean)options.getValueByName("\u0412\u0440\u0435\u043c\u044f").get());
    public static boolean child;

    public WorldTweaks() {
        this.addSettings(options, selfSkinType, time, mode, colorFog);
    }

    public static ResourceLocation updateSkin(ResourceLocation resourceLocation, Entity entity) {
        if (entity == WorldTweaks.mc.player && ((Boolean)options.getValueByName("\u0412\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u043a\u0438\u043d").get()).booleanValue() && !SelfDestruct.unhooked && Expensive.getInstance().getModuleManager().getWorldTweaks().isState()) {
            resourceLocation = new ResourceLocation("eva/images/skins/" + ((String)selfSkinType.get()).toLowerCase() + ".png");
        }
        return resourceLocation;
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        IPacket<?> iPacket = e.getPacket();
        if (iPacket instanceof SUpdateTimePacket) {
            SUpdateTimePacket p = (SUpdateTimePacket)iPacket;
            if (this.isEnabled("\u0412\u0440\u0435\u043c\u044f")) {
                p.worldTime = ((String)time.get()).equalsIgnoreCase("\u0414\u0435\u043d\u044c") ? 1000L : 18000L;
            }
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (!(this.isEnabled("\u041c\u0430\u043b\u0435\u043d\u044c\u043a\u0438\u0439 \u0438\u0433\u0440\u043e\u043a") || this.isEnabled("\u0412\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u043a\u0438\u043d") || this.isEnabled("\u0412\u0440\u0435\u043c\u044f"))) {
            this.toggle();
            Expensive.getInstance().getNotifyManager().add(0, new WarningNotify("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u0435 \u0447\u0442\u043e-\u043d\u0438\u0431\u0443\u0434\u044c!", 3000L));
        }
        child = this.isEnabled("\u041c\u0430\u043b\u0435\u043d\u044c\u043a\u0438\u0439 \u0438\u0433\u0440\u043e\u043a");
    }

    public boolean isEnabled(String name) {
        return (Boolean)options.getValueByName(name).get();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        child = false;
    }
}

