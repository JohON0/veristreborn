/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;

@ModuleRegister(name="ElytraBounce", category=Category.Movement)
public class ElytraBounce
extends Module {
    ItemStack currentStack = ItemStack.EMPTY;

    @Subscribe
    private void onUpdate(EventUpdate e) {
        this.currentStack = ElytraBounce.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (this.currentStack.getItem() == Items.ELYTRA) {
            if (ElytraBounce.mc.player.isOnGround() && !ElytraBounce.mc.player.isInWater() && !ElytraBounce.mc.player.isSwimming()) {
                ElytraBounce.mc.player.jump();
            } else if (ElytraItem.isUsable(this.currentStack) && !ElytraBounce.mc.player.isElytraFlying() && !ElytraBounce.mc.player.isInWater() && !ElytraBounce.mc.player.isSwimming()) {
                ElytraBounce.mc.player.startFallFlying();
                ElytraBounce.mc.player.connection.sendPacket(new CEntityActionPacket(ElytraBounce.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            }
        }
    }
}

