/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.rotation;

import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.rotation.FreeLookHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class Rotation
implements IMinecraft {
    private float yaw;
    private float pitch;

    public Rotation(Entity entity) {
        this.yaw = entity.rotationYaw;
        this.pitch = entity.rotationPitch;
    }

    public double getDelta(Rotation targetRotation) {
        double yawDelta = MathHelper.wrapDegrees(targetRotation.getYaw() - this.yaw);
        double pitchDelta = MathHelper.wrapDegrees(targetRotation.getPitch() - this.pitch);
        return Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));
    }

    public static Rotation getReal() {
        return new Rotation(FreeLookHandler.getFreeYaw(), FreeLookHandler.getFreePitch());
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Rotation() {
    }

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}

