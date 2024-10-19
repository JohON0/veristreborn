/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.math.MathHelper;

@ModuleRegister(name="Timer", category=Category.Movement)
public class Timer
extends Module {
    public final SliderSetting speed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 2.0f, 0.1f, 10.0f, 0.1f);
    public final BooleanSetting smart = new BooleanSetting("\u0423\u043c\u043d\u044b\u0439", true);
    public SliderSetting ticks = new SliderSetting("\u0422\u0438\u043a\u0438", 1.0f, 0.15f, 3.0f, 0.1f);
    public BooleanSetting moveUp = new BooleanSetting("\u0412\u043e\u0441\u0441\u0442\u0430\u0432\u043b\u0438\u0432\u0430\u0442\u044c", false).setVisible(() -> (Boolean)this.smart.get());
    public SliderSetting moveUpValue = new SliderSetting("\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435", 0.05f, 0.01f, 0.1f, 0.01f).setVisible(() -> (Boolean)this.moveUp.get() != false && (Boolean)this.smart.get() != false);
    public double value;
    public float maxViolation = 100.0f;
    public float violation = 0.0f;

    public Timer() {
        this.addSettings(this.speed, this.ticks, this.smart, this.moveUp, this.moveUpValue);
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        IPacket<IClientPlayNetHandler> p;
        IPacket<?> iPacket = e.getPacket();
//        if (iPacket instanceof SEntityVelocityPacket && (p = (SEntityVelocityPacket)iPacket).() == Timer.mc.player.getEntityId()) {
//            this.resetSpeed();
//        }
        if ((iPacket = e.getPacket()) instanceof CConfirmTransactionPacket) {
            p = (IPacket<IClientPlayNetHandler>) iPacket;
            e.cancel();
        }
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (!Timer.mc.player.isOnGround()) {
            this.violation += 0.1f;
            this.violation = MathHelper.clamp(this.violation, 0.0f, this.maxViolation / ((Float)this.speed.get()).floatValue());
        }
        Timer.mc.timer.timerSpeed = ((Float)this.speed.get()).floatValue();
        if (!((Boolean)this.smart.get()).booleanValue() || Timer.mc.timer.timerSpeed <= 1.0f) {
            return;
        }
        if (this.violation < this.maxViolation / ((Float)this.speed.get()).floatValue()) {
            this.violation += ((Float)this.ticks.get()).floatValue();
            this.violation = MathHelper.clamp(this.violation, 0.0f, this.maxViolation / ((Float)this.speed.get()).floatValue());
        } else {
            this.resetSpeed();
        }
    }

    private void reset() {
        Timer.mc.timer.timerSpeed = 1.0f;
    }

    public void resetSpeed() {
        this.setState(false, false);
        Timer.mc.timer.timerSpeed = 1.0f;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
    }
}

