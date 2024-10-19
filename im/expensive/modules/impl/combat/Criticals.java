/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventPacket;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Hand;

@ModuleRegister(name="Criticals", category=Category.Combat)
public class Criticals
extends Module {
    public static boolean cancelCrit;
    public final ModeSetting mode = new ModeSetting("\u041e\u0431\u0445\u043e\u0434", "NCP", "NCP", "OldNCP", "NCPUpdate", "Grim", "Matrix", "FunTime");

    public Criticals() {
        this.addSettings(this.mode);
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        CUseEntityPacket packet;
        IPacket<?> iPacket;
        if (e.isSend() && (iPacket = e.getPacket()) instanceof CUseEntityPacket && (packet = (CUseEntityPacket)iPacket).getAction() == CUseEntityPacket.Action.ATTACK) {
            Entity entity = packet.getEntityFromWorld(Criticals.mc.world);
            if (entity == null || entity instanceof EnderCrystalEntity || cancelCrit) {
                return;
            }
            this.sendCrit();
        }
    }

    public void sendCrit() {
        if (Criticals.mc.player == null || Criticals.mc.world == null && !this.isState()) {
            return;
        }
        if (Criticals.mc.player.isOnGround() || Criticals.mc.player.abilities.isFlying || this.mode.is("Grim") && !Criticals.mc.player.isInLava() && !Criticals.mc.player.isInWater()) {
            if (this.mode.is("NCP")) {
                this.critPacket(0.0625, false);
                this.critPacket(0.0, false);
            }
            if (this.mode.is("NCPUpdate")) {
                this.critPacket(2.71875E-7, false);
                this.critPacket(0.0, false);
            }
            if (this.mode.is("OldNCP")) {
                this.critPacket(1.058293536E-5, false);
                this.critPacket(9.16580235E-6, false);
                this.critPacket(1.0371854E-7, false);
            }
            if (this.mode.is("Grim") && !Criticals.mc.player.isOnGround()) {
                this.critPacket(-1.0E-6, false);
            }
            if (this.mode.is("Matrix")) {
                this.critPacket(1.0E-6, false);
                this.critPacket(0.0, false);
            }
            if (this.mode.is("FunTime")) {
                if (Criticals.mc.player.isOnGround()) {
                    this.critPacket(1.0E-8, false);
                }
                Criticals.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Criticals.mc.player.getPosX(), Criticals.mc.player.getPosY() - 1.0E-9, Criticals.mc.player.getPosZ(), Criticals.mc.player.lastReportedYaw, Criticals.mc.player.lastReportedPitch, false));
                Criticals.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Criticals.mc.player.getPosX(), Criticals.mc.player.getPosY() - 1.0E-9, Criticals.mc.player.getPosZ(), Criticals.mc.player.lastReportedYaw, Criticals.mc.player.lastReportedPitch, false));
                Criticals.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            }
        }
    }

    private void critPacket(double yDelta, boolean full) {
        if (full) {
            Criticals.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(Criticals.mc.player.getPosX(), Criticals.mc.player.getPosY() + yDelta, Criticals.mc.player.getPosZ(), false));
        } else {
            Criticals.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Criticals.mc.player.getPosX(), Criticals.mc.player.getPosY() + yDelta, Criticals.mc.player.getPosZ(), Criticals.mc.player.lastReportedYaw, Criticals.mc.player.lastReportedPitch, false));
        }
    }
}

