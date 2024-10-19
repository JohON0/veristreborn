/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import im.expensive.events.CancelEvent;
import net.minecraft.util.math.vector.Vector3d;

public final class StrafeEvent
extends CancelEvent {
    private float friction;
    private Vector3d relative;
    private float yaw;

    public float getFriction() {
        return this.friction;
    }

    public Vector3d getRelative() {
        return this.relative;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setRelative(Vector3d relative) {
        this.relative = relative;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public String toString() {
        return "StrafeEvent(friction=" + this.getFriction() + ", relative=" + this.getRelative() + ", yaw=" + this.getYaw() + ")";
    }

    public StrafeEvent(float friction, Vector3d relative, float yaw) {
        this.friction = friction;
        this.relative = relative;
        this.yaw = yaw;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StrafeEvent)) {
            return false;
        }
        StrafeEvent other = (StrafeEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (Float.compare(this.getFriction(), other.getFriction()) != 0) {
            return false;
        }
        if (Float.compare(this.getYaw(), other.getYaw()) != 0) {
            return false;
        }
        Vector3d this$relative = this.getRelative();
        Vector3d other$relative = other.getRelative();
        return !(this$relative == null ? other$relative != null : !((Object)this$relative).equals(other$relative));
    }

    protected boolean canEqual(Object other) {
        return other instanceof StrafeEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + Float.floatToIntBits(this.getFriction());
        result = result * 59 + Float.floatToIntBits(this.getYaw());
        Vector3d $relative = this.getRelative();
        result = result * 59 + ($relative == null ? 43 : ((Object)$relative).hashCode());
        return result;
    }
}

