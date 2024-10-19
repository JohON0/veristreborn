/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.ModeSetting;

@ModuleRegister(name="AirJump", category=Category.Movement)
public class AirJump
extends Module {
    private ModeSetting mode = new ModeSetting("\u041e\u0431\u0445\u043e\u0434", "Matrix", "Default", "Matrix");

    public AirJump() {
        this.addSettings(this.mode);
    }

    @Subscribe
    public void onUpdate(EventMotion e) {
        if (this.mode.is("Default")) {
            AirJump.mc.player.onGround = true;
        }
        if (this.mode.is("Matrix") && !AirJump.mc.world.getCollisionShapes(AirJump.mc.player, AirJump.mc.player.getBoundingBox().expand(0.5, 0.0, 0.5).offset(0.0, -1.0, 0.0)).toList().isEmpty() && AirJump.mc.player.ticksExisted % 2 == 0) {
            AirJump.mc.player.fallDistance = 0.0f;
            AirJump.mc.player.jumpTicks = 0;
            e.setOnGround(true);
            AirJump.mc.player.onGround = true;
        }
    }
}

