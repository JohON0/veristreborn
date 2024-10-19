/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.SliderSetting;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.IPacket;

@ModuleRegister(name="SlowPackets", category=Category.Misc)
public class SlowPackets
extends Module {
    private SliderSetting delay = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 1000.0f, 100.0f, 5000.0f, 100.0f);
    public static final ConcurrentLinkedQueue<TimedPacket> packets = new ConcurrentLinkedQueue();

    public SlowPackets() {
        this.addSettings(this.delay);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (TimedPacket p : packets) {
            SlowPackets.mc.player.connection.getNetworkManager().sendPacketWithoutEvent(p.getPacket());
        }
        packets.clear();
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (e.isSend()) {
            IPacket<?> packet = e.getPacket();
            packets.add(new TimedPacket(packet, System.currentTimeMillis()));
            e.cancel();
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        for (TimedPacket timedPacket : packets) {
            if (System.currentTimeMillis() - timedPacket.getTime() < (long)((Float)this.delay.get()).intValue()) continue;
            SlowPackets.mc.player.connection.getNetworkManager().sendPacketWithoutEvent(timedPacket.getPacket());
            packets.remove(timedPacket);
        }
    }

    public static class TimedPacket {
        private final IPacket<?> packet;
        private final long time;

        public TimedPacket(IPacket<?> packet, long time) {
            this.packet = packet;
            this.time = time;
        }

        public IPacket<?> getPacket() {
            return this.packet;
        }

        public long getTime() {
            return this.time;
        }
    }
}

