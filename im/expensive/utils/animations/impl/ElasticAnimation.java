/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.animations.impl;

import im.expensive.utils.animations.Animation;
import im.expensive.utils.animations.Direction;
import net.minecraft.util.math.MathHelper;

public class ElasticAnimation
extends Animation {
    float easeAmount;
    float smooth;
    boolean reallyElastic;

    public ElasticAnimation(int ms, double endPoint, float elasticity, float smooth, boolean moreElasticity) {
        super(ms, endPoint);
        this.easeAmount = elasticity;
        this.smooth = smooth;
        this.reallyElastic = moreElasticity;
    }

    public ElasticAnimation(int ms, double endPoint, float elasticity, float smooth, boolean moreElasticity, Direction direction) {
        super(ms, endPoint, direction);
        this.easeAmount = elasticity;
        this.smooth = smooth;
        this.reallyElastic = moreElasticity;
    }

    @Override
    protected double getEquation(double x) {
        double x1 = Math.pow(x / (double)this.duration, this.smooth);
        double elasticity = this.easeAmount * 0.1f;
        return Math.pow(2.0, -10.0 * (this.reallyElastic ? Math.sqrt(x1) : x1)) * (double)MathHelper.sin((float)((x1 - elasticity / 4.0) * (Math.PI * 2 / elasticity))) + 1.0;
    }
}

