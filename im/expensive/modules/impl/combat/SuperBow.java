/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventPacket;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.SliderSetting;
import net.minecraft.item.BowItem;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Hand;

@ModuleRegister(name="SuperBow", category=Category.Combat)
public class SuperBow
extends Module {
    private final SliderSetting power = new SliderSetting("\u0421\u0438\u043b\u0430", 40.0f, 1.0f, 100.0f, 1.0f);

    public SuperBow() {
        this.addSettings(this.power);
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        CPlayerDiggingPacket packet;
        if (e.isSend() && e.getPacket() instanceof CPlayerDiggingPacket && (packet = (CPlayerDiggingPacket)e.getPacket()).getAction() == CPlayerDiggingPacket.Action.RELEASE_USE_ITEM && SuperBow.mc.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof BowItem) {
            SuperBow.mc.player.connection.sendPacket(new CEntityActionPacket(SuperBow.mc.player, CEntityActionPacket.Action.START_SPRINTING));
            int i = 0;
            while ((float)i < ((Float)this.power.get()).floatValue()) {
                SuperBow.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(SuperBow.mc.player.getPosX(), SuperBow.mc.player.getPosY() + 1.0E-10, SuperBow.mc.player.getPosZ(), false));
                SuperBow.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(SuperBow.mc.player.getPosX(), SuperBow.mc.player.getPosY() - 1.0E-10, SuperBow.mc.player.getPosZ(), true));
                ++i;
            }
        }
    }
}

