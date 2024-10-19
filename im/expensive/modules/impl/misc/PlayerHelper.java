/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventEntityLeave;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;

@ModuleRegister(name="PlayerHelper", category=Category.Misc)
public class PlayerHelper
extends Module {
    public final BooleanSetting portalgodmode = new BooleanSetting("PortalGodMode", false);
    public final BooleanSetting srpspoofer = new BooleanSetting("SrpSpoofer", false);
    public final BooleanSetting leaveTracker = new BooleanSetting("LeaveTracker", true);
    public final BooleanSetting speedmine = new BooleanSetting("SpeedMine", false);
    public BooleanSetting ultraFast = new BooleanSetting("\u041c\u0433\u043d\u043e\u0432\u0435\u043d\u043d\u043e", false).setVisible(() -> (Boolean)this.speedmine.get());
    public final BooleanSetting deathPosition = new BooleanSetting("DeathPosition", false);
    public BooleanSetting autoGPS = new BooleanSetting("\u0410\u0432\u0442\u043e GPS", false).setVisible(() -> (Boolean)this.deathPosition.get());
    public BooleanSetting autoWAY = new BooleanSetting("\u0410\u0432\u0442\u043e Way", false).setVisible(() -> (Boolean)this.deathPosition.get());

    public PlayerHelper() {
        this.addSettings(this.portalgodmode, this.srpspoofer, this.leaveTracker, this.speedmine, this.ultraFast, this.deathPosition, this.autoGPS, this.autoWAY);
    }

    @Subscribe
    private void onEntityLeave(EventEntityLeave eel) {
        if (((Boolean)this.leaveTracker.get()).booleanValue()) {
            Entity entity = eel.getEntity();
            if (!this.isEntityValid(entity)) {
                return;
            }
            String message = "\u0418\u0433\u0440\u043e\u043a " + entity.getDisplayName().getString() + " \u043b\u0438\u0432\u043d\u0443\u043b \u043d\u0430 " + entity.getStringPosition();
            this.print(message);
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof CConfirmTeleportPacket && ((Boolean)this.portalgodmode.get()).booleanValue()) {
            e.cancel();
        }
        if (e.getPacket() instanceof SSendResourcePackPacket && ((Boolean)this.srpspoofer.get()).booleanValue()) {
            PlayerHelper.mc.player.connection.sendPacket(new CResourcePackStatusPacket(CResourcePackStatusPacket.Action.ACCEPTED));
            PlayerHelper.mc.player.connection.sendPacket(new CResourcePackStatusPacket(CResourcePackStatusPacket.Action.SUCCESSFULLY_LOADED));
            if (PlayerHelper.mc.currentScreen != null) {
                PlayerHelper.mc.player.closeScreen();
            }
            e.cancel();
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (((Boolean)this.speedmine.get()).booleanValue()) {
            PlayerHelper.mc.playerController.blockHitDelay = 0;
            if (!((Boolean)this.ultraFast.get()).booleanValue()) {
                PlayerHelper.mc.playerController.resetBlockRemoving();
            }
            if (((Boolean)this.ultraFast.get()).booleanValue() && PlayerHelper.mc.player.isOnGround()) {
                PlayerHelper.mc.playerController.curBlockDamageMP = 1.0f;
            }
        }
    }

    private boolean isEntityValid(Entity entity) {
        if (!(entity instanceof AbstractClientPlayerEntity) || entity instanceof ClientPlayerEntity) {
            return false;
        }
        return !(PlayerHelper.mc.player.getDistance(entity) < 100.0f);
    }
}

