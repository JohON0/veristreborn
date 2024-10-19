/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.MovingEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="NoClip", category=Category.Movement)
public class NoClip
extends Module {
    @Subscribe
    private void onMoving(MovingEvent move) {
        if (!this.collisionPredict(move.getTo())) {
            if (move.isCollidedHorizontal()) {
                move.setIgnoreHorizontal(true);
            }
            if (move.getMotion().y > 0.0 || NoClip.mc.player.isSneaking()) {
                move.setIgnoreVertical(true);
            }
            move.getMotion().y = Math.min(move.getMotion().y, 99999.0);
        }
    }

    public boolean collisionPredict(Vector3d to) {
        boolean prevCollision = NoClip.mc.world.getCollisionShapes(NoClip.mc.player, NoClip.mc.player.getBoundingBox().shrink(0.0625)).toList().isEmpty();
        Vector3d backUp = new Vector3d(NoClip.mc.player.getPosX(), NoClip.mc.player.getPosY(), NoClip.mc.player.getPosZ());
        NoClip.mc.player.setPosition(to.x, to.y, to.z);
        boolean collision = NoClip.mc.world.getCollisionShapes(NoClip.mc.player, NoClip.mc.player.getBoundingBox().shrink(0.0625)).toList().isEmpty() && prevCollision;
        NoClip.mc.player.setPosition(backUp.x, backUp.y, backUp.z);
        return collision;
    }
}

