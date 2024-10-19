/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.player.MouseUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public final class RayTraceUtils
implements IMinecraft {
    public static boolean rayTraceSingleEntity(float yaw, float pitch, double distance, Entity entity) {
        Vector3d eyeVec = RayTraceUtils.mc.player.getEyePosition(1.0f);
        Vector3d lookVec = RayTraceUtils.mc.player.getVectorForRotation(pitch, yaw);
        Vector3d extendedVec = eyeVec.add(lookVec.scale(distance));
        AxisAlignedBB AABB = entity.getBoundingBox();
        return AABB.contains(eyeVec) || AABB.rayTrace(eyeVec, extendedVec).isPresent();
    }

    public static RayTraceResult rayTrace(double rayTraceDistance, float yaw, float pitch, Entity entity) {
        Vector3d startVec = RayTraceUtils.mc.player.getEyePosition(1.0f);
        Vector3d directionVec = MouseUtil.getVectorForRotation(pitch, yaw);
        Vector3d endVec = startVec.add(directionVec.x * rayTraceDistance, directionVec.y * rayTraceDistance, directionVec.z * rayTraceDistance);
        return RayTraceUtils.mc.world.rayTraceBlocks(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }

    public static boolean isHitBoxNotVisible(Vector3d vec3d) {
        RayTraceContext rayTraceContext = new RayTraceContext(RayTraceUtils.mc.player.getEyePosition(1.0f), vec3d, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, RayTraceUtils.mc.player);
        BlockRayTraceResult blockHitResult = RayTraceUtils.mc.world.rayTraceBlocks(rayTraceContext);
        return blockHitResult.getType() == RayTraceResult.Type.MISS;
    }

    private RayTraceUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

