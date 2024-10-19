/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

public class EventInput {
    private float forward;
    private float strafe;
    private boolean jump;
    private boolean sneak;
    private double sneakSlowDownMultiplier;

    public float getForward() {
        return this.forward;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public boolean isJump() {
        return this.jump;
    }

    public boolean isSneak() {
        return this.sneak;
    }

    public double getSneakSlowDownMultiplier() {
        return this.sneakSlowDownMultiplier;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public void setSneakSlowDownMultiplier(double sneakSlowDownMultiplier) {
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventInput)) {
            return false;
        }
        EventInput other = (EventInput)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getForward(), other.getForward()) != 0) {
            return false;
        }
        if (Float.compare(this.getStrafe(), other.getStrafe()) != 0) {
            return false;
        }
        if (this.isJump() != other.isJump()) {
            return false;
        }
        if (this.isSneak() != other.isSneak()) {
            return false;
        }
        return Double.compare(this.getSneakSlowDownMultiplier(), other.getSneakSlowDownMultiplier()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventInput;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getForward());
        result = result * 59 + Float.floatToIntBits(this.getStrafe());
        result = result * 59 + (this.isJump() ? 79 : 97);
        result = result * 59 + (this.isSneak() ? 79 : 97);
        long $sneakSlowDownMultiplier = Double.doubleToLongBits(this.getSneakSlowDownMultiplier());
        result = result * 59 + (int)($sneakSlowDownMultiplier >>> 32 ^ $sneakSlowDownMultiplier);
        return result;
    }

    public String toString() {
        return "EventInput(forward=" + this.getForward() + ", strafe=" + this.getStrafe() + ", jump=" + this.isJump() + ", sneak=" + this.isSneak() + ", sneakSlowDownMultiplier=" + this.getSneakSlowDownMultiplier() + ")";
    }

    public EventInput(float forward, float strafe, boolean jump, boolean sneak, double sneakSlowDownMultiplier) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }
}

