/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventPacket;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;

@ModuleRegister(name="NoServerDesync", category=Category.Misc)
public class NoServerDesync
extends Module {
    private float targetYaw;
    private float targetPitch;
    private boolean isPacketSent;

    @Subscribe
    private void onPacket(EventPacket e) {
        SHeldItemChangePacket wrapper;
        int serverSlot;
        if (NoServerDesync.mc.player == null) {
            return;
        }
        IPacket<?> iPacket = e.getPacket();
        if (iPacket instanceof SHeldItemChangePacket && (serverSlot = (wrapper = (SHeldItemChangePacket)iPacket).getHeldItemHotbarIndex()) != NoServerDesync.mc.player.inventory.currentItem) {
            NoServerDesync.mc.player.connection.sendPacket(new CHeldItemChangePacket(NoServerDesync.mc.player.inventory.currentItem));
            e.cancel();
        }
        if (e.isSend() && this.isPacketSent && (iPacket = e.getPacket()) instanceof CPlayerPacket) {
            CPlayerPacket playerPacket = (CPlayerPacket)iPacket;
            playerPacket.setRotation(this.targetYaw, this.targetPitch);
            this.isPacketSent = false;
        }
    }

    public void sendRotationPacket(float yaw, float pitch) {
        this.targetYaw = yaw;
        this.targetPitch = pitch;
        this.isPacketSent = true;
    }
}

