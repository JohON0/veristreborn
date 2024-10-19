/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import im.expensive.utils.client.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public final class EntityUtils
implements IMinecraft {
    public static Vector3d getPrevPositionVec(Entity entity) {
        return new Vector3d(entity.prevPosX, entity.prevPosY, entity.prevPosZ);
    }

    public static Vector3d getInterpolatedPositionVec(Entity entity) {
        Vector3d prev = EntityUtils.getPrevPositionVec(entity);
        return prev.add(entity.getPositionVec().subtract(prev).scale(mc.getRenderPartialTicks()));
    }

    private EntityUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

