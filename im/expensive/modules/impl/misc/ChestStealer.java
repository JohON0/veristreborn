/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

@ModuleRegister(name="ChestStealer", category=Category.Misc)
public class ChestStealer
extends Module {
    private final SliderSetting delay = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 100.0f, 0.0f, 1000.0f, 1.0f);
    private final StopWatch stopWatch = new StopWatch();
    private final List<Item> ingotItemList = List.of();

    public ChestStealer() {
        this.addSettings(this.delay);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        Container container = ChestStealer.mc.player.openContainer;
        if (container instanceof ChestContainer) {
            ChestContainer container2 = (ChestContainer)container;
            IInventory lowerChestInventory = container2.getLowerChestInventory();
            for (int index = 0; index < lowerChestInventory.getSizeInventory(); ++index) {
                ItemStack stack = lowerChestInventory.getStackInSlot(index);
                if (!this.shouldMoveItem(container2, index) || this.isContainerEmpty(stack)) continue;
                if (((Float)this.delay.get()).floatValue() == 0.0f) {
                    this.moveItem(container2, index, lowerChestInventory.getSizeInventory());
                    continue;
                }
                if (!this.stopWatch.isReached(((Float)this.delay.get()).longValue())) continue;
                ChestStealer.mc.playerController.windowClick(container2.windowId, index, 0, ClickType.QUICK_MOVE, ChestStealer.mc.player);
                this.stopWatch.reset();
            }
        }
    }

    private boolean shouldMoveItem(ChestContainer container, int index) {
        ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(index);
        return itemStack.getItem() != Item.getItemById(0);
    }

    private void moveItem(ChestContainer container, int index, int multi) {
        for (int i = 0; i < multi; ++i) {
            ChestStealer.mc.playerController.windowClick(container.windowId, index + i, 0, ClickType.QUICK_MOVE, ChestStealer.mc.player);
        }
    }

    public boolean isWhiteListItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return itemStack.isFood() || itemStack.isEnchanted() || this.ingotItemList.contains(item) || item == Items.PLAYER_HEAD || item == Items.NAUTILUS_SHELL || item == Items.GUNPOWDER || item == Items.GRAY_DYE || item == Items.PHANTOM_MEMBRANE || item instanceof ArmorItem || item instanceof EnderPearlItem || item instanceof SwordItem || item instanceof ToolItem || item instanceof PotionItem || item instanceof ArrowItem || item instanceof SkullItem || item.getGroup() == ItemGroup.COMBAT;
    }

    private boolean isContainerEmpty(ItemStack stack) {
        return !this.isWhiteListItem(stack);
    }
}

