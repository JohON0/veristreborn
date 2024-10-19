/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

public class EventKeyRelease {
    int key;

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventKeyRelease)) {
            return false;
        }
        EventKeyRelease other = (EventKeyRelease)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.getKey() == other.getKey();
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventKeyRelease;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getKey();
        return result;
    }

    public String toString() {
        return "EventKeyRelease(key=" + this.getKey() + ")";
    }

    public EventKeyRelease(int key) {
        this.key = key;
    }
}

