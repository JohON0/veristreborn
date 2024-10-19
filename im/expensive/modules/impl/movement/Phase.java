/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;

@ModuleRegister(name="Phase", category=Category.Movement)
public class Phase
extends Module {
    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (!this.collisionPredict() && Phase.mc.gameSettings.keyBindJump.pressed) {
            Phase.mc.player.setOnGround(true);
        }
    }

    public boolean collisionPredict() {
        boolean prevCollision = Phase.mc.world.getCollisionShapes(Phase.mc.player, Phase.mc.player.getBoundingBox().shrink(0.0625)).toList().isEmpty();
        return Phase.mc.world.getCollisionShapes(Phase.mc.player, Phase.mc.player.getBoundingBox().shrink(0.0625)).toList().isEmpty() && prevCollision;
    }
}

