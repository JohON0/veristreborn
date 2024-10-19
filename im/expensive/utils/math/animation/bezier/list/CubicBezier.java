/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.math.animation.bezier.list;

import im.expensive.utils.math.animation.bezier.Bezier;
import im.expensive.utils.math.animation.bezier.Point;

public class CubicBezier
extends Bezier {
    @Override
    public double getValue(double t) {
        double dt = 1.0 - t;
        double dt2 = dt * dt;
        double t2 = t * t;
        Point temp = this.getPoint2().copy();
        return this.getStart().copy().scale(dt2, dt).add(temp.scale(3.0 * dt2 * t)).add(temp.set(this.getPoint3()).scale(3.0 * dt * t2)).add(temp.set(this.getEnd()).scale(t2 * t)).getY();
    }

    public static class Builder {
        private CubicBezier bezier = new CubicBezier();

        public Builder(CubicBezier bezier) {
            this.bezier = bezier;
        }

        public Builder() {
        }

        public Builder setPoint2(Point point) {
            this.bezier.setPoint2(point);
            return this;
        }

        public Builder setPoint3(Point point) {
            this.bezier.setPoint3(point);
            return this;
        }

        public CubicBezier build() {
            return this.bezier;
        }
    }
}

