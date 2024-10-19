/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.animations.impl;

import im.expensive.utils.animations.Animation;
import im.expensive.utils.animations.Direction;

public class SmoothStepAnimation
extends Animation {
    public SmoothStepAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public SmoothStepAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected double getEquation(double x) {
        double x1 = x / (double)this.duration;
        return -2.0 * Math.pow(x1, 3.0) + 3.0 * Math.pow(x1, 2.0);
    }
}

