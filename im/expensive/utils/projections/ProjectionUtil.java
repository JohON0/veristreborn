/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.projections;

import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.math.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.joml.Vector2d;

public class ProjectionUtil
implements IMinecraft {
    public static Vector2f project(double x, double y, double z) {
        Entity renderViewEntity;
        Vector3d camera_pos = ProjectionUtil.mc.getRenderManager().info.getProjectedView();
        Quaternion cameraRotation = mc.getRenderManager().getCameraOrientation().copy();
        cameraRotation.conjugate();
        Vector3f result3f = new Vector3f((float)(camera_pos.x - x), (float)(camera_pos.y - y), (float)(camera_pos.z - z));
        result3f.transform(cameraRotation);
        if (ProjectionUtil.mc.gameSettings.viewBobbing && (renderViewEntity = mc.getRenderViewEntity()) instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)renderViewEntity;
            ProjectionUtil.calculateViewBobbing(playerentity, result3f);
        }
        double fov = ProjectionUtil.mc.gameRenderer.getFOVModifier(ProjectionUtil.mc.getRenderManager().info, mc.getRenderPartialTicks(), true);
        return ProjectionUtil.calculateScreenPosition(result3f, fov);
    }

    public static Vector3d interpolate(Entity entity, float partialTicks) {
        double posX = MathUtil.lerp(entity.lastTickPosX, entity.getPosX(), (double)partialTicks);
        double posY = MathUtil.lerp(entity.lastTickPosY, entity.getPosY(), (double)partialTicks);
        double posZ = MathUtil.lerp(entity.lastTickPosZ, entity.getPosZ(), (double)partialTicks);
        return new Vector3d(posX, posY, posZ);
    }

    private static void calculateViewBobbing(PlayerEntity playerentity, Vector3f result3f) {
        float walked = playerentity.distanceWalkedModified;
        float f = walked - playerentity.prevDistanceWalkedModified;
        float f1 = -(walked + f * mc.getRenderPartialTicks());
        float f2 = MathHelper.lerp(mc.getRenderPartialTicks(), playerentity.prevCameraYaw, playerentity.cameraYaw);
        Quaternion quaternion = new Quaternion(Vector3f.XP, Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2f) * f2) * 5.0f, true);
        quaternion.conjugate();
        result3f.transform(quaternion);
        Quaternion quaternion1 = new Quaternion(Vector3f.ZP, MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0f, true);
        quaternion1.conjugate();
        result3f.transform(quaternion1);
        Vector3f bobTranslation = new Vector3f(MathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5f, -Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2), 0.0f);
        bobTranslation.setY(-bobTranslation.getY());
        result3f.add(bobTranslation);
    }

    private static Vector2f calculateScreenPosition(Vector3f result3f, double fov) {
        float halfHeight = (float)mc.getMainWindow().getScaledHeight() / 2.0f;
        float scaleFactor = halfHeight / (result3f.getZ() * (float)Math.tan(Math.toRadians(fov / 2.0)));
        if (result3f.getZ() < 0.0f) {
            return new Vector2f(-result3f.getX() * scaleFactor + (float)mc.getMainWindow().getScaledWidth() / 2.0f, (float)mc.getMainWindow().getScaledHeight() / 2.0f - result3f.getY() * scaleFactor);
        }
        return new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public static Vector2d project2D(Vector3d vec) {
        return ProjectionUtil.project2D(vec.x, vec.y, vec.z);
    }

    public static Vector2d project2D(double x, double y, double z) {
        Entity renderViewEntity;
        if (ProjectionUtil.mc.getRenderManager().info == null) {
            return new Vector2d();
        }
        Vector3d cameraPosition = ProjectionUtil.mc.getRenderManager().info.getProjectedView();
        Quaternion cameraRotation = mc.getRenderManager().getCameraOrientation().copy();
        cameraRotation.conjugate();
        Vector3f relativePosition = new Vector3f((float)(cameraPosition.x - x), (float)(cameraPosition.y - y), (float)(cameraPosition.z - z));
        relativePosition.transform(cameraRotation);
        if (ProjectionUtil.mc.gameSettings.viewBobbing && (renderViewEntity = mc.getRenderViewEntity()) instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)renderViewEntity;
            float walkedDistance = playerEntity.distanceWalkedModified;
            float deltaDistance = walkedDistance - playerEntity.prevDistanceWalkedModified;
            float interpolatedDistance = -(walkedDistance + deltaDistance * mc.getRenderPartialTicks());
            float cameraYaw = MathHelper.lerp(mc.getRenderPartialTicks(), playerEntity.prevCameraYaw, playerEntity.cameraYaw);
            Quaternion bobQuaternionX = new Quaternion(Vector3f.XP, Math.abs(MathHelper.cos(interpolatedDistance * (float)Math.PI - 0.2f) * cameraYaw) * 5.0f, true);
            bobQuaternionX.conjugate();
            relativePosition.transform(bobQuaternionX);
            Quaternion bobQuaternionZ = new Quaternion(Vector3f.ZP, MathHelper.sin(interpolatedDistance * (float)Math.PI) * cameraYaw * 3.0f, true);
            bobQuaternionZ.conjugate();
            relativePosition.transform(bobQuaternionZ);
            Vector3f bobTranslation = new Vector3f(MathHelper.sin(interpolatedDistance * (float)Math.PI) * cameraYaw * 0.5f, -Math.abs(MathHelper.cos(interpolatedDistance * (float)Math.PI) * cameraYaw), 0.0f);
            bobTranslation.setY(-bobTranslation.getY());
            relativePosition.add(bobTranslation);
        }
        double fieldOfView = (float)ProjectionUtil.mc.gameRenderer.getFOVModifier(ProjectionUtil.mc.getRenderManager().info, mc.getRenderPartialTicks(), true);
        float halfHeight = (float)mc.getMainWindow().getScaledHeight() / 2.0f;
        float scaleFactor = halfHeight / (relativePosition.getZ() * (float)Math.tan(Math.toRadians(fieldOfView / 2.0)));
        if (relativePosition.getZ() < 0.0f) {
            return new Vector2d(-relativePosition.getX() * scaleFactor + (float)(mc.getMainWindow().getScaledWidth() / 2), (float)(mc.getMainWindow().getScaledHeight() / 2) - relativePosition.getY() * scaleFactor);
        }
        return null;
    }
}

