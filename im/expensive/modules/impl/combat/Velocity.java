/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.misc.AntiPush;
import im.expensive.modules.settings.impl.ModeSetting;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

@ModuleRegister(name="Velocity", category=Category.Combat)
public class Velocity
extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Grim Skip", "Grim Cancel", "Grim Cancel 2", "Grim New", "Funtime");
    private int skip = 0;
    private boolean cancel;
    boolean damaged;
    private boolean flag;
    private int ccCooldown;
    boolean work = false;
    BlockPos blockPos;

    public Velocity() {
        this.addSettings(this.mode);
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (Velocity.mc.player == null) {
            return;
        }
        if (e.isReceive()) {
            SEntityVelocityPacket wrapper;
            SEntityVelocityPacket p;
            IPacket<?> iPacket = e.getPacket();
            if (iPacket instanceof SEntityVelocityPacket && (p = (SEntityVelocityPacket)iPacket).getEntityID() != Velocity.mc.player.getEntityId()) {
                return;
            }
            switch (this.mode.getIndex()) {
                case 0: {
                    if (!(e.getPacket() instanceof SEntityVelocityPacket)) break;
                    e.cancel();
                    break;
                }
                case 1: {
                    if (e.getPacket() instanceof SEntityVelocityPacket) {
                        this.skip = 6;
                        e.cancel();
                    }
                    if (!(e.getPacket() instanceof CPlayerPacket) || this.skip <= 0) break;
                    --this.skip;
                    e.cancel();
                    break;
                }
                case 2: {
                    if (e.getPacket() instanceof SEntityVelocityPacket) {
                        e.cancel();
                        this.cancel = true;
                    }
                    if (e.getPacket() instanceof SPlayerPositionLookPacket) {
                        this.skip = 3;
                    }
                    if (!(e.getPacket() instanceof CPlayerPacket)) break;
                    --this.skip;
                    if (!this.cancel) break;
                    if (this.skip <= 0) {
                        BlockPos blockPos = new BlockPos(Velocity.mc.player.getPositionVec());
                        Velocity.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Velocity.mc.player.getPosX(), Velocity.mc.player.getPosY(), Velocity.mc.player.getPosZ(), Velocity.mc.player.rotationYaw, Velocity.mc.player.rotationPitch, Velocity.mc.player.isOnGround()));
                        Velocity.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
                    }
                    this.cancel = false;
                }
            }
            if (this.mode.is("Grim Cancel 2")) {
                iPacket = e.getPacket();
                if (iPacket instanceof SEntityVelocityPacket) {
                    wrapper = (SEntityVelocityPacket)iPacket;
                    if (wrapper.getEntityID() != Velocity.mc.player.getEntityId() || this.skip < 0) {
                        return;
                    }
                    this.skip = 8;
                    e.cancel();
                }
                if (e.getPacket() instanceof SConfirmTransactionPacket) {
                    if (this.skip < 0) {
                        ++this.skip;
                    } else if (this.skip > 1) {
                        --this.skip;
                        e.cancel();
                    }
                }
                if (e.getPacket() instanceof SPlayerPositionLookPacket) {
                    this.skip = -8;
                }
            }
            if (this.mode.is("Funtime")) {
                iPacket = e.getPacket();
                if (iPacket instanceof SEntityVelocityPacket) {
                    p = (SEntityVelocityPacket)iPacket;
                    if (this.skip >= 2) {
                        return;
                    }
                    if (p.getEntityID() != Velocity.mc.player.getEntityId()) {
                        return;
                    }
                    e.cancel();
                    this.damaged = true;
                }
                if (e.getPacket() instanceof SPlayerPositionLookPacket) {
                    this.skip = 3;
                }
            }
            if (this.mode.is("Grim New")) {
                if (this.ccCooldown > 0) {
                    --this.ccCooldown;
                } else {
                    iPacket = e.getPacket();
                    if (iPacket instanceof SEntityVelocityPacket && (wrapper = (SEntityVelocityPacket)iPacket).getEntityID() == Velocity.mc.player.getEntityId()) {
                        e.cancel();
                        this.flag = true;
                    }
                    if (e.getPacket() instanceof SExplosionPacket) {
                        Expensive.getInstance().getModuleManager().getAntiPush();
                        if (!((Boolean)AntiPush.modes.getValueByName("\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u044b").get()).booleanValue()) {
                            e.cancel();
                        }
                        this.flag = true;
                    }
                    if (e.getPacket() instanceof SPlayerPositionLookPacket) {
                        this.ccCooldown = 5;
                    }
                }
            }
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.mode.is("Funtime")) {
            --this.skip;
            if (this.damaged) {
                BlockPos blockPos = Velocity.mc.player.getPosition();
                Velocity.mc.player.connection.sendPacketWithoutEvent(new CPlayerPacket.PositionRotationPacket(Velocity.mc.player.getPosX(), Velocity.mc.player.getPosY(), Velocity.mc.player.getPosZ(), Velocity.mc.player.rotationYaw, Velocity.mc.player.rotationPitch, Velocity.mc.player.isOnGround()));
                Velocity.mc.player.connection.sendPacketWithoutEvent(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
                this.damaged = false;
            }
        }
        if (this.mode.is("Grim New") && this.flag) {
            if (this.ccCooldown <= 0) {
                Velocity.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Velocity.mc.player.getPosX(), Velocity.mc.player.getPosY(), Velocity.mc.player.getPosZ(), Velocity.mc.player.rotationYaw, Velocity.mc.player.rotationPitch, Velocity.mc.player.isOnGround()));
                Velocity.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(Math.floor(Velocity.mc.player.getPositionVec().x), Math.floor(Velocity.mc.player.getPositionVec().y), Math.floor(Velocity.mc.player.getPositionVec().z)), Direction.UP));
            }
            this.flag = false;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.skip = 0;
        this.cancel = false;
        this.damaged = false;
    }
}

