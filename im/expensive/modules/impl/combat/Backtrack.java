/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.AttackEvent;
import im.expensive.events.EventKey;
import im.expensive.events.EventPacket;
import im.expensive.events.TickEvent;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.render.RenderUtils;
import org.lwjgl.opengl.GL11;

@ModuleRegister(name="Backtrack", category=Category.Combat)
public class Backtrack
extends Module {
    private final BindSetting skip = new BindSetting("\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c", 0);
    private final SliderSetting range = new SliderSetting("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f", 3.0f, 3.0f, 6.0f, 0.1f);
    private final SliderSetting delay = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 500.0f, 100.0f, 1000.0f, 50.0f);
    private final List<PacketData> queue = new LinkedList<PacketData>();
    private Entity target;
    private Vector3d realPos;
    private Vector3d interpolatedrealPos;

    public Backtrack() {
        this.addSettings(this.skip, this.range, this.delay);
    }

    @Subscribe
    private void onKey(EventKey e) {
        if (e.isKeyDown((Integer)this.skip.get())) {
            this.reset();
        }
    }

    @Subscribe
    private void onAttack(AttackEvent e) {
        if (e.entity == this.target || e.entity.isInvulnerable()) {
            return;
        }
        this.target = e.entity;
        this.interpolatedrealPos = this.realPos = this.target.getPositionVec();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Subscribe
    private void onPacket(EventPacket e) {
        SEntityTeleportPacket etp;
        if (e.isSend() || !this.shouldLagging() || mc.isSingleplayer()) {
            return;
        }
        IPacket<?> packet = e.getPacket();
        if (packet instanceof SPlaySoundEffectPacket || packet instanceof SEntityStatusPacket) {
            return;
        }
        if (packet instanceof SPlayerPositionLookPacket || packet instanceof SDisconnectPacket) {
            this.reset();
            return;
        }
        if (packet instanceof SEntityTeleportPacket && (etp = (SEntityTeleportPacket)packet).getEntityId() == this.target.getEntityId()) {
            this.realPos = new Vector3d(etp.getX(), etp.getY(), etp.getZ());
        }
        if (packet instanceof SEntityPacket) {
            SEntityPacket ep = (SEntityPacket)packet;
            if (ep.entityId == this.target.getEntityId()) {
                this.realPos = this.realPos.add(new Vector3d((double)ep.posX / 4096.0, (double)ep.posY / 4096.0, (double)ep.posZ / 4096.0));
            }
        }
        e.cancel();
        List<PacketData> list = this.queue;
        synchronized (list) {
            if (e.isReceive()) {
                this.queue.add(new PacketData(packet, System.currentTimeMillis()));
            }
        }
    }

    @Subscribe
    private void onTick(TickEvent e) {
        if (this.queue.isEmpty() && this.isTargetNull() || mc.isSingleplayer()) {
            return;
        }
        if (this.shouldLagging()) {
            this.handle(false);
        } else {
            this.reset();
        }
    }

    @Subscribe
    private void onRender(WorldEvent e) {
        if (this.realPos == null || mc.isSingleplayer()) {
            return;
        }
        double half = this.target.getWidth() / 2.0f;
        if (this.interpolatedrealPos == null || this.realPos.distanceTo(this.interpolatedrealPos) >= 2.0) {
            this.interpolatedrealPos = this.realPos;
        }
        this.interpolatedrealPos = MathUtil.fast(this.interpolatedrealPos, this.realPos, 15.0f);
        GL11.glPushMatrix();
        Vector3d renderOffset = Backtrack.mc.getRenderManager().info.getProjectedView();
        GL11.glTranslated(-renderOffset.x, -renderOffset.y, -renderOffset.z);
        int hurtTime = 0;
        Entity entity = this.target;
        if (entity instanceof LivingEntity) {
            LivingEntity l = (LivingEntity)entity;
            hurtTime = l.hurtTime;
        }
        RenderUtils.drawBox(new AxisAlignedBB(this.interpolatedrealPos.getX() - half, this.interpolatedrealPos.getY(), this.interpolatedrealPos.getZ() - half, this.interpolatedrealPos.getX() + half, this.interpolatedrealPos.getY() + (double)this.target.getHeight(), this.interpolatedrealPos.getZ() + half), ColorUtils.interpolate(-1, ColorUtils.getColor(0), 1.0f - (float)hurtTime / 9.0f));
        GL11.glPopMatrix();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handle(boolean all) {
        List<PacketData> list = this.queue;
        synchronized (list) {
            Iterator<PacketData> iterator2 = this.queue.iterator();
            while (iterator2.hasNext()) {
                PacketData packetData = iterator2.next();
                double factor = Backtrack.mc.player.getPositionVec().distanceTo(this.realPos) / (double)((Float)this.range.get()).floatValue();
                if (!all && (double)packetData.timestamp() + (double)((Float)this.delay.get()).longValue() * factor > (double)System.currentTimeMillis()) break;
                try {
                    NetworkManager.processPacket(packetData.packet(), Backtrack.mc.player.connection);
                } catch (ThreadQuickExitException threadQuickExitException) {
                    // empty catch block
                }
                iterator2.remove();
            }
        }
    }

    private boolean isTargetNull() {
        return this.target == null && this.realPos == null;
    }

    private boolean shouldLagging() {
        return this.target != null && this.target.isAlive() && !this.target.isInvulnerable() && Backtrack.mc.player.getPositionVec().distanceTo(this.realPos) <= (double)((Float)this.range.get()).floatValue();
    }

    private void reset() {
        this.handle(true);
        this.target = null;
        this.realPos = null;
        this.interpolatedrealPos = null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.isSingleplayer()) {
            return;
        }
        this.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.isSingleplayer()) {
            return;
        }
        this.reset();
    }

    private record PacketData(IPacket<?> packet, long timestamp) {
    }
}

