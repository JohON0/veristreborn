/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.rotation;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.CameraEvent;
import im.expensive.events.EventRotate;
import im.expensive.utils.client.IMinecraft;
import net.minecraft.util.math.MathHelper;

public class FreeLookHandler
implements IMinecraft {
    private static boolean active;
    private static float freeYaw;
    private static float freePitch;

    public FreeLookHandler() {
        Expensive.getInstance().getEventBus().register(this);
    }

    @Subscribe
    public void onLook(EventRotate e) {
        if (active) {
            this.rotateTowards(e.getYaw(), e.getPitch());
            e.cancel();
        }
    }

    @Subscribe
    public void onCamera(CameraEvent e) {
        if (active) {
            e.yaw = freeYaw;
            e.pitch = freePitch;
        } else {
            freeYaw = e.yaw;
            freePitch = e.pitch;
        }
    }

    public static void setActive(boolean state) {
        if (active != state) {
            active = state;
            FreeLookHandler.resetRotation();
        }
    }

    private void rotateTowards(double yaw, double pitch) {
        double d0 = pitch * 0.15;
        double d1 = yaw * 0.15;
        freePitch = (float)((double)freePitch + d0);
        freeYaw = (float)((double)freeYaw + d1);
        freePitch = MathHelper.clamp(freePitch, -90.0f, 90.0f);
    }

    private static void resetRotation() {
        FreeLookHandler.mc.player.rotationYaw = freeYaw;
        FreeLookHandler.mc.player.rotationPitch = freePitch;
    }

    public static boolean isActive() {
        return active;
    }

    public static float getFreeYaw() {
        return freeYaw;
    }

    public static float getFreePitch() {
        return freePitch;
    }
}

