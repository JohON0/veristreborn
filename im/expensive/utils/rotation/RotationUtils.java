/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.rotation;

import im.expensive.Expensive;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.math.VectorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2f;

public class RotationUtils
implements IMinecraft {
    public RotationUtils() {
        Expensive.getInstance().getEventBus().register(this);
    }

    public Vector3d getClosestVec(Entity entity) {
        Vector3d eyePosVec = RotationUtils.mc.player.getEyePosition(1.0f);
        return VectorUtils.getClosestVec(eyePosVec, entity).subtract(eyePosVec);
    }

    public Vector2f calculate(double x, double y, double z) {
        Vector3d pos = RotationUtils.mc.player.getPositionVec().add(0.0, RotationUtils.mc.player.getEyeHeight(), 0.0);
        return RotationUtils.calculate(new org.joml.Vector3d(pos.x, pos.y, pos.z), new org.joml.Vector3d(x, y, z));
    }

    public static Vector2f calculate(org.joml.Vector3d to) {
        Vector3d pos = RotationUtils.mc.player.getPositionVec().add(0.0, RotationUtils.mc.player.getEyeHeight(), 0.0);
        org.joml.Vector3d from = new org.joml.Vector3d(pos.x, pos.y, pos.z);
        return RotationUtils.calculate(from, to);
    }

    public static Vector2f calculate(org.joml.Vector3d from, org.joml.Vector3d to) {
        org.joml.Vector3d diff = to.sub(from);
        double distance = Math.hypot(diff.x(), diff.z());
        float yaw = (float)(MathHelper.atan2(diff.z(), diff.x()) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(MathHelper.atan2(diff.y(), distance) * 180.0 / Math.PI));
        yaw = RotationUtils.normalize(yaw);
        pitch = org.joml.Math.clamp(-90.0f, 90.0f, pitch);
        return new Vector2f(yaw, pitch);
    }

    public static float normalize(float value) {
        if ((value %= 360.0f) > 180.0f) {
            value -= 360.0f;
        } else if (value < -180.0f) {
            value += 360.0f;
        }
        return value;
    }

    public static Vector2f calculate(Entity entity) {
        Vector3d pos = entity.getPositionVec().add(0.0, Math.max(0.0, org.joml.Math.min(RotationUtils.mc.player.getPosY() - entity.getPosY() + (double)RotationUtils.mc.player.getEyeHeight(), (entity.getBoundingBox().maxY - entity.getBoundingBox().minY) * 0.75)), 0.0);
        org.joml.Vector3d to = new org.joml.Vector3d(pos.x, pos.y, pos.z);
        return RotationUtils.calculate(to);
    }

    public double getStrictDistance(Entity entity) {
        return this.getClosestVec(entity).length();
    }

    public static float[] getMatrixRots(Entity target) {
        double dX = target.getPosX() - RotationUtils.mc.player.getPosX();
        double dZ = target.getPosZ() - RotationUtils.mc.player.getPosZ();
        double dY = target.getPosY() + (double)target.getEyeHeight() - (RotationUtils.mc.player.getPosY() + (double)RotationUtils.mc.player.getEyeHeight());
        double dist = Math.sqrt(dX * dX + dZ * dZ);
        float yaw = (float)(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0);
        float pitch = (float)(-Math.toDegrees(Math.atan2(dY, dist)));
        return new float[]{yaw, pitch};
    }
}

