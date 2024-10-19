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
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="Blink", category=Category.Misc)
public class Blink
extends Module {
    private final CopyOnWriteArrayList<IPacket> packets = new CopyOnWriteArrayList();
    private BooleanSetting delay = new BooleanSetting("\u041f\u0443\u043b\u044c\u0441", false);
    private SliderSetting delayS = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 100.0f, 50.0f, 1000.0f, 50.0f).setVisible(() -> (Boolean)this.delay.get());
    private long started;
    float animation;
    public StopWatch stopWatch = new StopWatch();
    Vector3d lastPos = new Vector3d(0.0, 0.0, 0.0);

    public Blink() {
        this.addSettings(this.delay, this.delayS);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.started = System.currentTimeMillis();
        this.lastPos = Blink.mc.player.getPositionVec();
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (Blink.mc.player != null && Blink.mc.world != null && !mc.isSingleplayer() && !Blink.mc.player.getShouldBeDead()) {
            if (e.isSend()) {
                this.packets.add(e.getPacket());
                e.cancel();
            }
        } else {
            this.toggle();
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (System.currentTimeMillis() - this.started >= 29900L) {
            this.toggle();
        }
        if (((Boolean)this.delay.get()).booleanValue() && this.stopWatch.isReached(((Float)this.delayS.get()).longValue())) {
            for (IPacket packet : this.packets) {
                Blink.mc.player.connection.getNetworkManager().sendPacketWithoutEvent(packet);
            }
            this.packets.clear();
            this.started = System.currentTimeMillis();
            this.stopWatch.reset();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (IPacket packet : this.packets) {
            Blink.mc.player.connection.sendPacket(packet);
        }
        this.packets.clear();
    }
}

