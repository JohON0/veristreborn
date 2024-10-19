/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.animations.easing.CompactAnimation;
import im.expensive.utils.animations.easing.Easing;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

@ModuleRegister(name="FullBright", category=Category.Render)
public class FullBright
extends Module {
    private final ModeSetting mode = new ModeSetting("\u041c\u043e\u0434", "Gamma", "Gamma", "Potion");
    private final CompactAnimation animation = new CompactAnimation(Easing.EASE_OUT_QUART, 500L);
    private final BooleanSetting dynamic = new BooleanSetting("\u0414\u0438\u043d\u0430\u043c\u0438\u0447\u0435\u0441\u043a\u0438\u0439", false).setVisible(() -> this.mode.is("Gamma"));
    private final SliderSetting bright = new SliderSetting("\u042f\u0440\u043a\u043e\u0441\u0442\u044c", 2.5f, 1.0f, 5.0f, 0.1f).setVisible(() -> (Boolean)this.dynamic.get() == false && this.mode.is("Gamma"));
    private float originalGamma;
    private boolean isGammaChanged = false;

    public FullBright() {
        this.addSettings(this.mode, this.dynamic, this.bright);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.saveGamma();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.restoreGamma();
        FullBright.mc.player.removeActivePotionEffect(new EffectInstance(Effects.NIGHT_VISION).getPotion());
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.mode.is("Gamma")) {
            FullBright.mc.player.removeActivePotionEffect(new EffectInstance(Effects.NIGHT_VISION).getPotion());
            if (((Boolean)this.dynamic.get()).booleanValue()) {
                float lightLevel = FullBright.mc.player.getBrightness();
                this.animation.run(this.calculateGamma(lightLevel));
                float gamma = (float)this.animation.getValue();
                this.setGamma(gamma);
            } else {
                this.setGamma(((Float)this.bright.get()).floatValue());
            }
        } else {
            FullBright.mc.player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 16360, 0));
        }
    }

    private float calculateGamma(float lightLevel) {
        float minGamma = 0.5f;
        float maxGamma = 5.0f;
        float gammaRange = maxGamma - minGamma;
        float lightRange = 1.0f;
        float gamma = minGamma + gammaRange * (1.0f - lightLevel / lightRange);
        return gamma;
    }

    public void saveGamma() {
        this.originalGamma = (float)FullBright.mc.gameSettings.gamma;
    }

    public void setGamma(float newGamma) {
        this.saveGamma();
        FullBright.mc.gameSettings.gamma = newGamma;
        this.isGammaChanged = true;
    }

    public void restoreGamma() {
        if (this.isGammaChanged) {
            FullBright.mc.gameSettings.gamma = this.originalGamma;
            this.isGammaChanged = false;
        }
    }
}

