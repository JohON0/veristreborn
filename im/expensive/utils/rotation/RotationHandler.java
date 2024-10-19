/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.rotation;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventUpdate;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.math.SensUtils;
import im.expensive.utils.rotation.FreeLookHandler;
import im.expensive.utils.rotation.Rotation;
import net.minecraft.util.math.MathHelper;

public class RotationHandler
implements IMinecraft {
    private static RotationTask currentTask = RotationTask.IDLE;
    private static float currentTurnSpeed;
    private static int currentPriority;
    private static int currentTimeout;
    private static int idleTicks;

    public RotationHandler() {
        Expensive.getInstance().getEventBus().register(this);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (currentTask == RotationTask.AIM && ++idleTicks > currentTimeout) {
            currentTask = RotationTask.RESET;
        }
        if (currentTask == RotationTask.RESET && RotationHandler.updateRotation(Rotation.getReal(), currentTurnSpeed)) {
            currentTask = RotationTask.IDLE;
            currentPriority = 0;
            FreeLookHandler.setActive(false);
        }
    }

    public static void update(Rotation rotation, float turnSpeed, int timeout, int priority) {
        if (currentPriority > priority) {
            return;
        }
        if (currentTask == RotationTask.IDLE) {
            FreeLookHandler.setActive(true);
        }
        currentTurnSpeed = turnSpeed;
        currentTimeout = timeout;
        currentPriority = priority;
        currentTask = RotationTask.AIM;
        RotationHandler.updateRotation(rotation, turnSpeed);
    }

    private static boolean updateRotation(Rotation rotation, float turnSpeed) {
        Rotation currentRotation = new Rotation(RotationHandler.mc.player);
        float yawDelta = MathHelper.wrapDegrees(rotation.getYaw() - currentRotation.getYaw());
        float pitchDelta = rotation.getPitch() - currentRotation.getPitch();
        float totalDelta = Math.abs(yawDelta) + Math.abs(pitchDelta);
        float yawSpeed = totalDelta == 0.0f ? 0.0f : Math.abs(yawDelta / totalDelta) * turnSpeed;
        float pitchSpeed = totalDelta == 0.0f ? 0.0f : Math.abs(pitchDelta / totalDelta) * turnSpeed;
        RotationHandler.mc.player.rotationYaw += SensUtils.getSensitivity(MathHelper.clamp(yawDelta, -yawSpeed, yawSpeed));
        RotationHandler.mc.player.rotationPitch = MathHelper.clamp(RotationHandler.mc.player.rotationPitch + SensUtils.getSensitivity(MathHelper.clamp(pitchDelta, -pitchSpeed, pitchSpeed)), -90.0f, 90.0f);
        Rotation finalRotation = new Rotation(RotationHandler.mc.player);
        idleTicks = 0;
        return finalRotation.getDelta(rotation) < (double)currentTurnSpeed;
    }

    private static enum RotationTask {
        AIM,
        RESET,
        IDLE;

    }
}

