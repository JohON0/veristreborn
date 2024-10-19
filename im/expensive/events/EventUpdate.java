/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

public class EventUpdate {
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventUpdate)) {
            return false;
        }
        EventUpdate other = (EventUpdate)o;
        return other.canEqual(this);
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventUpdate;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }

    public String toString() {
        return "EventUpdate()";
    }
}

