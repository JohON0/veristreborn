/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

public class PostMoveEvent {
    private double horizontalMove;

    public double getHorizontalMove() {
        return this.horizontalMove;
    }

    public void setHorizontalMove(double horizontalMove) {
        this.horizontalMove = horizontalMove;
    }

    public PostMoveEvent(double horizontalMove) {
        this.horizontalMove = horizontalMove;
    }
}

