/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

public class TickEvent {
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TickEvent)) {
            return false;
        }
        TickEvent other = (TickEvent)o;
        return other.canEqual(this);
    }

    protected boolean canEqual(Object other) {
        return other instanceof TickEvent;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }

    public String toString() {
        return "TickEvent()";
    }
}

