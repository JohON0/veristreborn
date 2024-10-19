/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

public class CameraEvent {
    public float yaw;
    public float pitch;
    public float partialTicks;

    public CameraEvent(float yaw, float pitch, float partialTicks) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.partialTicks = partialTicks;
    }

    public CameraEvent(int yaw) {
    }
}

