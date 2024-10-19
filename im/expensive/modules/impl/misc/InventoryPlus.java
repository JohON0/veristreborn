/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.misc.AutoActions;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.MoveUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CCloseWindowPacket;

@ModuleRegister(name="InventoryPlus", category=Category.Misc)
public class InventoryPlus
extends Module {
    public BooleanSetting xcarry = new BooleanSetting("XCarry", false);
    public BooleanSetting itemScroller = new BooleanSetting("ItemScroller", true);
    public BooleanSetting autoArmor = new BooleanSetting("AutoArmor", true);
    final SliderSetting delay = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 100.0f, 0.0f, 1000.0f, 1.0f).setVisible(() -> (Boolean)this.autoArmor.get());
    final BooleanSetting onlyInv = new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u0432 \u0438\u043d\u0432\u0435", false).setVisible(() -> (Boolean)this.autoArmor.get());
    final BooleanSetting workInMove = new BooleanSetting("\u0420\u0430\u0431\u043e\u0442\u0430\u0442\u044c \u0432 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0438", true).setVisible(() -> (Boolean)this.autoArmor.get());
    final StopWatch stopWatchAutoArmor = new StopWatch();

    public InventoryPlus() {
        this.addSettings(this.xcarry, this.itemScroller, this.autoArmor, this.delay, this.onlyInv, this.workInMove);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (((Boolean)this.autoArmor.get()).booleanValue()) {
            int i;
            if (!((Boolean)this.workInMove.get()).booleanValue() && MoveUtils.isMoving()) {
                return;
            }
            if (((Boolean)this.onlyInv.get()).booleanValue() && !(InventoryPlus.mc.currentScreen instanceof InventoryScreen)) {
                return;
            }
            PlayerInventory inventoryPlayer = AutoActions.mc.player.inventory;
            int[] bestIndexes = new int[4];
            int[] bestValues = new int[4];
            for (i = 0; i < 4; ++i) {
                Item item;
                bestIndexes[i] = -1;
                ItemStack stack = inventoryPlayer.armorItemInSlot(i);
                if (!this.isItemValid(stack) || !((item = stack.getItem()) instanceof ArmorItem)) continue;
                ArmorItem armorItem = (ArmorItem)item;
                bestValues[i] = this.calculateArmorValue(armorItem, stack);
            }
            for (i = 0; i < 36; ++i) {
                Item item;
                ItemStack stack = inventoryPlayer.getStackInSlot(i);
                if (!this.isItemValid(stack) || !((item = stack.getItem()) instanceof ArmorItem)) continue;
                ArmorItem armorItem = (ArmorItem)item;
                int armorTypeIndex = armorItem.getSlot().getIndex();
                int value = this.calculateArmorValue(armorItem, stack);
                if (value <= bestValues[armorTypeIndex]) continue;
                bestIndexes[armorTypeIndex] = i;
                bestValues[armorTypeIndex] = value;
            }
            ArrayList<Integer> randomIndexes = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
            Collections.shuffle(randomIndexes);
            for (int index : randomIndexes) {
                int bestIndex = bestIndexes[index];
                if (bestIndex == -1 || this.isItemValid(inventoryPlayer.armorItemInSlot(index)) && inventoryPlayer.getFirstEmptyStack() == -1) continue;
                if (bestIndex < 9) {
                    bestIndex += 36;
                }
                if (!this.stopWatchAutoArmor.isReached(((Float)this.delay.get()).longValue())) break;
                ItemStack armorItemStack = inventoryPlayer.armorItemInSlot(index);
                if (this.isItemValid(armorItemStack)) {
                    AutoActions.mc.playerController.windowClick(0, 8 - index, 0, ClickType.QUICK_MOVE, AutoActions.mc.player);
                }
                AutoActions.mc.playerController.windowClick(0, bestIndex, 0, ClickType.QUICK_MOVE, AutoActions.mc.player);
                this.stopWatchAutoArmor.reset();
                break;
            }
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (InventoryPlus.mc.player == null) {
            return;
        }
        if (e.getPacket() instanceof CCloseWindowPacket && ((Boolean)this.xcarry.get()).booleanValue()) {
            e.cancel();
        }
    }

    private boolean isItemValid(ItemStack stack) {
        return stack != null && !stack.isEmpty();
    }

    private int calculateArmorValue(ArmorItem armor, ItemStack stack) {
        int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);
        IArmorMaterial armorMaterial = armor.getArmorMaterial();
        int damageReductionAmount = armorMaterial.getDamageReductionAmount(armor.getEquipmentSlot());
        return armor.getDamageReduceAmount() * 20 + protectionLevel * 12 + (int)(armor.getToughness() * 2.0f) + damageReductionAmount * 5 >> 3;
    }
}

