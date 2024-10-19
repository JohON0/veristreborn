/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.client;

public final class Vec2i {
    private final int x;
    private final int y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Vec2i)) {
            return false;
        }
        Vec2i other = (Vec2i)o;
        if (this.getX() != other.getX()) {
            return false;
        }
        return this.getY() == other.getY();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getX();
        result = result * 59 + this.getY();
        return result;
    }

    public String toString() {
        return "Vec2i(x=" + this.getX() + ", y=" + this.getY() + ")";
    }
}

