/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import im.expensive.events.CancelEvent;

public class EventRotate
extends CancelEvent {
    private double yaw;
    private double pitch;

    public double getYaw() {
        return this.yaw;
    }

    public double getPitch() {
        return this.pitch;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventRotate)) {
            return false;
        }
        EventRotate other = (EventRotate)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Double.compare(this.getYaw(), other.getYaw()) != 0) {
            return false;
        }
        return Double.compare(this.getPitch(), other.getPitch()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventRotate;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $yaw = Double.doubleToLongBits(this.getYaw());
        result = result * 59 + (int)($yaw >>> 32 ^ $yaw);
        long $pitch = Double.doubleToLongBits(this.getPitch());
        result = result * 59 + (int)($pitch >>> 32 ^ $pitch);
        return result;
    }

    public String toString() {
        return "EventRotate(yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ")";
    }

    public EventRotate(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}

