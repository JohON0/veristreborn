/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.math.animation.bezier;

import im.expensive.utils.math.animation.bezier.Point;

public abstract class Bezier {
    private final Point start = new Point(0.0, 0.0);
    private final Point end = new Point(1.0, 1.0);
    private Point point2;
    private Point point3;

    public Bezier(Point point2, Point point3) {
        this.setPoint2(point2);
        this.setPoint3(point3);
    }

    public Bezier() {
    }

    public abstract double getValue(double var1);

    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    public void setPoint3(Point point3) {
        this.point3 = point3;
    }

    public Point getStart() {
        return this.start;
    }

    public Point getEnd() {
        return this.end;
    }

    public Point getPoint2() {
        return this.point2;
    }

    public Point getPoint3() {
        return this.point3;
    }
}

