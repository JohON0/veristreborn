/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

@ModuleRegister(name="NoFall", category=Category.Movement)
public class NoFall
extends Module {
    private ModeSetting mode = new ModeSetting("\u041e\u0431\u0445\u043e\u0434", "FunTime", "FunTime", "FunTime New", "NCP", "MatrixOld");
    boolean fall = false;

    public NoFall() {
        this.addSettings(this.mode);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.mode.is("FunTime") && (double)NoFall.mc.player.fallDistance > 2.4) {
            NoFall.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(NoFall.mc.player.getPosX(), NoFall.mc.player.getPosY() + 1.0E-6, NoFall.mc.player.getPosZ(), NoFall.mc.player.lastReportedYaw, NoFall.mc.player.lastReportedPitch, false));
            NoFall.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(NoFall.mc.player.getPosX(), NoFall.mc.player.getPosY() + 1.0E-6, NoFall.mc.player.getPosZ(), NoFall.mc.player.lastReportedYaw, NoFall.mc.player.lastReportedPitch, false));
            NoFall.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            NoFall.mc.player.fallDistance = 0.0f;
        }
        if (this.mode.is("NCP") && NoFall.mc.player.fallDistance >= 3.0f) {
            NoFall.mc.player.onGround = false;
            NoFall.mc.player.motion.y = 0.02f;
            for (int i = 0; i < 30; ++i) {
                NoFall.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(NoFall.mc.player.getPosX(), NoFall.mc.player.getPosY() + 110000.0, NoFall.mc.player.getPosZ(), false));
                NoFall.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(NoFall.mc.player.getPosX(), NoFall.mc.player.getPosY() + 2.0, NoFall.mc.player.getPosZ(), false));
            }
            NoFall.mc.player.connection.sendPacket(new CPlayerPacket(true));
            NoFall.mc.player.fallDistance = 0.0f;
        }
        if (this.mode.is("MatrixOld")) {
            float f = NoFall.mc.player.fallDistance;
            int n = NoFall.mc.player.getHealth() > 6.0f ? 3 : 2;
            if (f > (float)n) {
                NoFall.mc.player.fallDistance = (float)(Math.random() * 1.0E-12);
                NoFall.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(NoFall.mc.player.getPosX(), NoFall.mc.player.getPosY(), NoFall.mc.player.getPosZ(), true));
                NoFall.mc.player.jumpMovementFactor = 0.0f;
            }
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (this.mode.is("FunTime New") && (double)NoFall.mc.player.fallDistance > 2.4 && !MoveUtils.isBlockUnder(2.0f)) {
            double up;
            NoFall.mc.player.motion.y = up = (double)0.0035f;
            NoFall.mc.player.rotationYaw = 0.0f;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.fall = false;
    }
}

