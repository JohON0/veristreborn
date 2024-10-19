/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public final class VectorUtils {
    public static Vector3d getClosestVec(Vector3d vec, AxisAlignedBB AABB) {
        return new Vector3d(MathHelper.clamp(vec.getX(), AABB.minX, AABB.maxX), MathHelper.clamp(vec.getY(), AABB.minY, AABB.maxY), MathHelper.clamp(vec.getZ(), AABB.minZ, AABB.maxZ));
    }

    public static Vector3d getClosestVec(Vector3d vec, Entity entity) {
        return VectorUtils.getClosestVec(vec, entity.getBoundingBox());
    }

    private VectorUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

