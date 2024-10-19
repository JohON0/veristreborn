/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import im.expensive.events.CancelEvent;

public class EventMotion
extends CancelEvent {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;
    Runnable postMotion;

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public Runnable getPostMotion() {
        return this.postMotion;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setPostMotion(Runnable postMotion) {
        this.postMotion = postMotion;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventMotion)) {
            return false;
        }
        EventMotion other = (EventMotion)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Double.compare(this.getX(), other.getX()) != 0) {
            return false;
        }
        if (Double.compare(this.getY(), other.getY()) != 0) {
            return false;
        }
        if (Double.compare(this.getZ(), other.getZ()) != 0) {
            return false;
        }
        if (Float.compare(this.getYaw(), other.getYaw()) != 0) {
            return false;
        }
        if (Float.compare(this.getPitch(), other.getPitch()) != 0) {
            return false;
        }
        if (this.isOnGround() != other.isOnGround()) {
            return false;
        }
        Runnable this$postMotion = this.getPostMotion();
        Runnable other$postMotion = other.getPostMotion();
        return !(this$postMotion == null ? other$postMotion != null : !this$postMotion.equals(other$postMotion));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventMotion;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $x = Double.doubleToLongBits(this.getX());
        result = result * 59 + (int)($x >>> 32 ^ $x);
        long $y = Double.doubleToLongBits(this.getY());
        result = result * 59 + (int)($y >>> 32 ^ $y);
        long $z = Double.doubleToLongBits(this.getZ());
        result = result * 59 + (int)($z >>> 32 ^ $z);
        result = result * 59 + Float.floatToIntBits(this.getYaw());
        result = result * 59 + Float.floatToIntBits(this.getPitch());
        result = result * 59 + (this.isOnGround() ? 79 : 97);
        Runnable $postMotion = this.getPostMotion();
        result = result * 59 + ($postMotion == null ? 43 : $postMotion.hashCode());
        return result;
    }

    public String toString() {
        return "EventMotion(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", onGround=" + this.isOnGround() + ", postMotion=" + this.getPostMotion() + ")";
    }

    public EventMotion(double x, double y, double z, float yaw, float pitch, boolean onGround, Runnable postMotion) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.postMotion = postMotion;
    }
}

