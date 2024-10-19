/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import im.expensive.ui.autobuy.AutoBuy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class ShulkerUtil {
    public static List<ItemStack> getItemInShulker(ItemStack s, AutoBuy autoBuy) {
        CompoundNBT compoundnbt = s.getChildTag("BlockEntityTag");
        if (compoundnbt != null && compoundnbt.contains("Items", 9)) {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
            if (autoBuy.isDon()) {
                return nonnulllist.stream().filter(item -> item.getItem() != Items.AIR).filter(item -> item.getTag() != null && item.getTag().contains("don-item")).toList();
            }
            return nonnulllist.stream().filter(item -> item.getItem() != Items.AIR).toList();
        }
        return new ArrayList<ItemStack>();
    }
}

