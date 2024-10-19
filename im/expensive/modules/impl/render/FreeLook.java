/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventMotion;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.HitAura;
import im.expensive.modules.settings.impl.BooleanSetting;
import net.minecraft.client.settings.PointOfView;

@ModuleRegister(name="FreeLook", category=Category.Render)
public class FreeLook
extends Module {
    public BooleanSetting free = new BooleanSetting("\u0421\u0432\u043e\u0431\u043e\u0434\u043d\u0430\u044f \u043a\u0430\u043c\u0435\u0440\u0430", true);
    private float startYaw;
    private float startPitch;

    @Override
    public void onEnable() {
        if (this.isFree()) {
            this.startYaw = FreeLook.mc.player.rotationYaw;
            this.startPitch = FreeLook.mc.player.rotationPitch;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.isFree()) {
            FreeLook.mc.player.rotationYawOffset = -2.14748365E9f;
            FreeLook.mc.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
            FreeLook.mc.player.rotationYaw = this.startYaw;
            FreeLook.mc.player.rotationPitch = this.startPitch;
        }
        super.onDisable();
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        HitAura aura = Expensive.getInstance().getModuleManager().getHitAura();
        if (((Boolean)this.free.get()).booleanValue() && !aura.isState() && aura.getTarget() == null) {
            FreeLook.mc.gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
            FreeLook.mc.player.rotationYawOffset = this.startYaw;
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (((Boolean)this.free.get()).booleanValue()) {
            e.setYaw(this.startYaw);
            e.setPitch(this.startPitch);
            e.setOnGround(FreeLook.mc.player.isOnGround());
            FreeLook.mc.player.rotationYawHead = FreeLook.mc.player.rotationYawOffset;
            FreeLook.mc.player.renderYawOffset = FreeLook.mc.player.rotationYawOffset;
            FreeLook.mc.player.rotationPitchHead = this.startPitch;
        }
    }

    public boolean isFree() {
        return (Boolean)this.free.get();
    }
}

