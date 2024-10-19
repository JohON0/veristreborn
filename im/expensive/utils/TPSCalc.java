/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.math.MathHelper;

public class TPSCalc {
    private float TPS = 20.0f;
    private float adjustTicks = 0.0f;
    private long timestamp;

    public TPSCalc() {
        Expensive.getInstance().getEventBus().register(this);
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SUpdateTimePacket) {
            this.updateTPS();
        }
    }

    private void updateTPS() {
        long delay = System.nanoTime() - this.timestamp;
        float maxTPS = 20.0f;
        float rawTPS = maxTPS * (1.0E9f / (float)delay);
        float boundedTPS = MathHelper.clamp(rawTPS, 0.0f, maxTPS);
        this.TPS = (float)this.round(boundedTPS);
        this.adjustTicks = boundedTPS - maxTPS;
        this.timestamp = System.nanoTime();
    }

    public double round(double input) {
        return (double)Math.round(input * 100.0) / 100.0;
    }

    public float getTPS() {
        return this.TPS;
    }

    public float getAdjustTicks() {
        return this.adjustTicks;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}

