/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.client;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventPacket;
import java.util.Arrays;
import net.minecraft.util.math.MathHelper;

public class ServerTPS {
    protected final float[] ticks = new float[20];
    protected int index = 0;
    protected long lastPacketTime = -1L;

    public ServerTPS() {
        Arrays.fill(this.ticks, 0.0f);
        Expensive.getInstance().getEventBus().register(this);
    }

    public float getTPS() {
        float numTicks = 0.0f;
        float sumTickRates = 0.0f;
        for (float tickRate : this.ticks) {
            if (!(tickRate > 0.0f)) continue;
            sumTickRates += tickRate;
            numTicks += 1.0f;
        }
        return MathHelper.clamp(0.0f, 20.0f, sumTickRates / numTicks);
    }

    public void update() {
        if (this.lastPacketTime != -1L) {
            float timeElapsed = (float)(System.currentTimeMillis() - this.lastPacketTime) / 1000.0f;
            this.ticks[this.index % this.ticks.length] = MathHelper.clamp(0.0f, 20.0f, 20.0f / timeElapsed);
            ++this.index;
        }
        this.lastPacketTime = System.currentTimeMillis();
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        this.update();
    }
}

