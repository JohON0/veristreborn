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
import net.minecraft.network.play.server.SHeldItemChangePacket;

@ModuleRegister(name="ItemSwapFix", category=Category.Misc)
public class ItemSwapFix
extends Module {
    @Subscribe
    private void onPacket(EventPacket e) {
        SHeldItemChangePacket wrapper;
        int serverSlot;
        if (ItemSwapFix.mc.player == null) {
            return;
        }
        IPacket<?> iPacket = e.getPacket();
        if (iPacket instanceof SHeldItemChangePacket && (serverSlot = (wrapper = (SHeldItemChangePacket)iPacket).getHeldItemHotbarIndex()) != ItemSwapFix.mc.player.inventory.currentItem) {
            ItemSwapFix.mc.player.connection.sendPacket(new CHeldItemChangePacket(Math.max(ItemSwapFix.mc.player.inventory.currentItem % 8 + 1, 0)));
            ItemSwapFix.mc.player.connection.sendPacket(new CHeldItemChangePacket(ItemSwapFix.mc.player.inventory.currentItem));
            e.cancel();
        }
    }
}

