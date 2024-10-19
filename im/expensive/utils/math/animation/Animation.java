/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.math.animation;

import im.expensive.utils.math.animation.AnimationType;
import im.expensive.utils.math.animation.bezier.Bezier;
import im.expensive.utils.math.animation.bezier.list.CubicBezier;
import im.expensive.utils.math.animation.util.Easing;
import im.expensive.utils.math.animation.util.Easings;

public class Animation {
    private long start;
    private double duration;
    private double fromValue;
    private double toValue;
    private double value;
    private Easing easing = Easings.LINEAR;
    private Bezier bezier = new CubicBezier();
    private AnimationType type = AnimationType.EASING;
    private boolean debug = false;

    public Animation run(double valueTo, double duration) {
        return this.run(valueTo, duration, Easings.LINEAR, false);
    }

    public Animation run(double valueTo, double duration, Easing easing) {
        return this.run(valueTo, duration, easing, false);
    }

    public Animation run(double valueTo, double duration, Bezier bezier) {
        return this.run(valueTo, duration, bezier, false);
    }

    public Animation run(double valueTo, double duration, boolean safe) {
        return this.run(valueTo, duration, Easings.LINEAR, safe);
    }

    public Animation run(double valueTo, double duration, Easing easing, boolean safe) {
        if (this.check(safe, valueTo)) {
            if (this.isDebug()) {
                System.out.println("Animate cancelled due to target val equals from val");
            }
        } else {
            this.setType(AnimationType.EASING).setEasing(easing).setDuration(duration * 1000.0).setStart(System.currentTimeMillis()).setFromValue(this.getValue()).setToValue(valueTo);
            if (this.isDebug()) {
                System.out.println("#animate {\n    to value: " + this.getToValue() + "\n    from value: " + this.getValue() + "\n    duration: " + this.getDuration() + "\n}");
            }
        }
        return this;
    }

    public Animation run(double valueTo, double duration, Bezier bezier, boolean safe) {
        if (this.check(safe, valueTo)) {
            if (this.isDebug()) {
                System.out.println("Animate cancelled due to target val equals from val");
            }
        } else {
            this.setType(AnimationType.BEZIER).setBezier(bezier).setDuration(duration * 1000.0).setStart(System.currentTimeMillis()).setFromValue(this.getValue()).setToValue(valueTo);
            if (this.isDebug()) {
                System.out.println("#animate {\n    to value: " + this.getToValue() + "\n    from value: " + this.getValue() + "\n    duration: " + this.getDuration() + "\n    type: " + this.getType().name() + "\n}");
            }
        }
        return this;
    }

    public boolean update() {
        boolean alive = this.isAlive();
        if (alive) {
            if (this.getType().equals((Object)AnimationType.BEZIER)) {
                this.setValue(this.interpolate(this.getFromValue(), this.getToValue(), this.getBezier().getValue(this.calculatePart())));
            } else {
                this.setValue(this.interpolate(this.getFromValue(), this.getToValue(), this.getEasing().ease(this.calculatePart())));
            }
        } else {
            this.setStart(0L);
            this.setValue(this.getToValue());
        }
        return alive;
    }

    public boolean isAlive() {
        return !this.isFinished();
    }

    public boolean isFinished() {
        return this.calculatePart() >= 1.0;
    }

    public double calculatePart() {
        return (double)(System.currentTimeMillis() - this.getStart()) / this.getDuration();
    }

    public boolean check(boolean safe, double valueTo) {
        return safe && this.isAlive() && (valueTo == this.getFromValue() || valueTo == this.getToValue() || valueTo == this.getValue());
    }

    public double interpolate(double start, double end, double pct) {
        return start + (end - start) * pct;
    }

    public Animation setStart(long start) {
        this.start = start;
        return this;
    }

    public Animation setDuration(double duration) {
        this.duration = duration;
        return this;
    }

    public Animation setFromValue(double fromValue) {
        this.fromValue = fromValue;
        return this;
    }

    public Animation setToValue(double toValue) {
        this.toValue = toValue;
        return this;
    }

    public Animation setValue(double value) {
        this.value = value;
        return this;
    }

    public Animation setEasing(Easing easing) {
        this.easing = easing;
        return this;
    }

    public Animation setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public Animation setBezier(Bezier bezier) {
        this.bezier = bezier;
        return this;
    }

    public Animation setType(AnimationType type) {
        this.type = type;
        return this;
    }

    public long getStart() {
        return this.start;
    }

    public double getDuration() {
        return this.duration;
    }

    public double getFromValue() {
        return this.fromValue;
    }

    public double getToValue() {
        return this.toValue;
    }

    public double getValue() {
        return this.value;
    }

    public Easing getEasing() {
        return this.easing;
    }

    public Bezier getBezier() {
        return this.bezier;
    }

    public AnimationType getType() {
        return this.type;
    }

    public boolean isDebug() {
        return this.debug;
    }
}

