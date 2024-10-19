/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.autobuy.api.factory;

import im.expensive.ui.autobuy.api.model.IItem;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

public interface ItemFactory {
    public IItem createNewItem(Item var1, int var2, int var3, int var4, Map<Enchantment, Integer> var5);
}

