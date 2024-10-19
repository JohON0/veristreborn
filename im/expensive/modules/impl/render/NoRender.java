/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventCancelOverlay;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import net.minecraft.potion.Effects;

@ModuleRegister(name="NoRender", category=Category.Render)
public class NoRender
extends Module {
    public ModeListSetting element = new ModeListSetting("\u0423\u0434\u0430\u043b\u044f\u0442\u044c", new BooleanSetting("\u041e\u0433\u043e\u043d\u044c \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435", true), new BooleanSetting("\u041b\u0438\u043d\u0438\u044f \u0431\u043e\u0441\u0441\u0430", false), new BooleanSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0442\u043e\u0442\u0435\u043c\u0430", true), new BooleanSetting("\u0422\u0430\u0439\u0442\u043b\u044b", false), new BooleanSetting("\u0422\u0430\u0431\u043b\u0438\u0446\u0430", false), new BooleanSetting("\u0422\u0443\u043c\u0430\u043d", true), new BooleanSetting("\u0422\u0440\u044f\u0441\u043a\u0443 \u043a\u0430\u043c\u0435\u0440\u044b", true), new BooleanSetting("\u041f\u043b\u043e\u0445\u0438\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b", true), new BooleanSetting("\u0414\u043e\u0436\u0434\u044c", true), new BooleanSetting("\u041a\u0430\u043c\u0435\u0440\u0430 \u043a\u043b\u0438\u043f", true), new BooleanSetting("\u0411\u0440\u043e\u043d\u044f", false), new BooleanSetting("\u041f\u043b\u0430\u0449", false), new BooleanSetting("\u042d\u0444\u0444\u0435\u043a\u0442 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", true), new BooleanSetting("\u042d\u0444\u0444\u0435\u043a\u0442 \u0432\u043e\u0434\u044b", true));

    public NoRender() {
        this.addSettings(this.element);
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        this.handleEventUpdate(e);
    }

    @Subscribe
    private void onEventCancelOverlay(EventCancelOverlay e) {
        this.handleEventOverlaysRender(e);
    }

    private void handleEventOverlaysRender(EventCancelOverlay event) {
        boolean cancelOverlay = false;
        switch (event.overlayType) {
            default: {
                throw new IncompatibleClassChangeError();
            }
            case FIRE_OVERLAY: {
                boolean bl = (Boolean)this.element.getValueByName("\u041e\u0433\u043e\u043d\u044c \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435").get();
                break;
            }
            case BOSS_LINE: {
                boolean bl = (Boolean)this.element.getValueByName("\u041b\u0438\u043d\u0438\u044f \u0431\u043e\u0441\u0441\u0430").get();
                break;
            }
            case SCOREBOARD: {
                boolean bl = (Boolean)this.element.getValueByName("\u0422\u0430\u0431\u043b\u0438\u0446\u0430").get();
                break;
            }
            case TITLES: {
                boolean bl = (Boolean)this.element.getValueByName("\u0422\u0430\u0439\u0442\u043b\u044b").get();
                break;
            }
            case TOTEM: {
                boolean bl = (Boolean)this.element.getValueByName("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0442\u043e\u0442\u0435\u043c\u0430").get();
                break;
            }
            case FOG: {
                boolean bl = (Boolean)this.element.getValueByName("\u0422\u0443\u043c\u0430\u043d").get();
                break;
            }
            case HURT: {
                boolean bl = (Boolean)this.element.getValueByName("\u0422\u0440\u044f\u0441\u043a\u0443 \u043a\u0430\u043c\u0435\u0440\u044b").get();
                break;
            }
            case UNDER_WATER: {
                boolean bl = (Boolean)this.element.getValueByName("\u042d\u0444\u0444\u0435\u043a\u0442 \u0432\u043e\u0434\u044b").get();
                break;
            }
            case CAMERA_CLIP: {
                boolean bl = (Boolean)this.element.getValueByName("\u041a\u0430\u043c\u0435\u0440\u0430 \u043a\u043b\u0438\u043f").get();
                break;
            }
            case ARMOR: {
                boolean bl = cancelOverlay = ((Boolean)this.element.getValueByName("\u0411\u0440\u043e\u043d\u044f").get()).booleanValue();
            }
        }
        if (cancelOverlay) {
            event.cancel();
        }
    }

    private void handleEventUpdate(EventUpdate event) {
        boolean hasEffects;
        boolean isRaining = NoRender.mc.world.isRaining() && (Boolean)this.element.getValueByName("\u0414\u043e\u0436\u0434\u044c").get() != false;
        boolean bl = hasEffects = (NoRender.mc.player.isPotionActive(Effects.BLINDNESS) || NoRender.mc.player.isPotionActive(Effects.NAUSEA)) && (Boolean)this.element.getValueByName("\u041f\u043b\u043e\u0445\u0438\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b").get() != false;
        if (isRaining) {
            NoRender.mc.world.setRainStrength(0.0f);
            NoRender.mc.world.setThunderStrength(0.0f);
        }
        if (hasEffects) {
            NoRender.mc.player.removePotionEffect(Effects.NAUSEA);
            NoRender.mc.player.removePotionEffect(Effects.BLINDNESS);
        }
    }
}

