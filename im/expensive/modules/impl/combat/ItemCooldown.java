/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventCalculateCooldown;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.client.ClientUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

@ModuleRegister(name="ItemCooldown", category=Category.Combat)
public class ItemCooldown
extends Module {
    public static final ModeListSetting items = new ModeListSetting("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", new BooleanSetting("\u0413\u0435\u043f\u043b\u044b", true), new BooleanSetting("\u041f\u0435\u0440\u043a\u0438", true), new BooleanSetting("\u0425\u043e\u0440\u0443\u0441\u044b", true), new BooleanSetting("\u0427\u0430\u0440\u043a\u0438", false));
    static final SliderSetting gappleTime = new SliderSetting("\u041a\u0443\u043b\u0434\u0430\u0443\u043d \u0433\u0435\u043f\u043b\u0430", 4.5f, 1.0f, 10.0f, 0.05f).setVisible(() -> (Boolean)items.getValueByName("\u0413\u0435\u043f\u043b\u044b").get());
    static final SliderSetting pearlTime = new SliderSetting("\u041a\u0443\u043b\u0434\u0430\u0443\u043d \u043f\u0435\u0440\u043e\u043a", 14.05f, 1.0f, 15.0f, 0.05f).setVisible(() -> (Boolean)items.getValueByName("\u041f\u0435\u0440\u043a\u0438").get());
    static final SliderSetting horusTime = new SliderSetting("\u041a\u0443\u043b\u0434\u0430\u0443\u043d \u0445\u043e\u0440\u0443\u0441\u043e\u0432", 2.3f, 1.0f, 10.0f, 0.05f).setVisible(() -> (Boolean)items.getValueByName("\u0425\u043e\u0440\u0443\u0441\u044b").get());
    static final SliderSetting enchantmentGappleTime = new SliderSetting("\u041a\u0443\u043b\u0434\u0430\u0443\u043d \u0447\u0430\u0440\u043e\u043a", 4.5f, 1.0f, 10.0f, 0.05f).setVisible(() -> (Boolean)items.getValueByName("\u0427\u0430\u0440\u043a\u0438").get());
    private final BooleanSetting onlyPvP = new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u0432 PVP", true);
    public HashMap<Item, Long> lastUseItemTime = new HashMap();
    public boolean isCooldown;

    public ItemCooldown() {
        this.addSettings(items, gappleTime, pearlTime, horusTime, enchantmentGappleTime, this.onlyPvP);
    }

    @Subscribe
    public void onCalculateCooldown(EventCalculateCooldown e) {
        this.applyGoldenAppleCooldown(e);
    }

    private void applyGoldenAppleCooldown(EventCalculateCooldown calcCooldown) {
        ArrayList<Item> itemsToRemove = new ArrayList<Item>();
        for (Map.Entry<Item, Long> entry : this.lastUseItemTime.entrySet()) {
            float timeSetting;
            ItemEnum itemEnum = ItemEnum.getItemEnum(entry.getKey());
            if (itemEnum == null || calcCooldown.getItemStack() != itemEnum.getItem() || !itemEnum.getActive().get().booleanValue() || this.isNotPvP()) continue;
            long time = System.currentTimeMillis() - entry.getValue();
            if ((float)time < (timeSetting = itemEnum.getTime().get().floatValue() * 1000.0f) && itemEnum.getActive().get().booleanValue()) {
                calcCooldown.setCooldown((float)time / timeSetting);
                this.isCooldown = true;
                continue;
            }
            this.isCooldown = false;
            itemsToRemove.add(itemEnum.getItem());
        }
        itemsToRemove.forEach(this.lastUseItemTime::remove);
    }

    public boolean isNotPvP() {
        return (Boolean)this.onlyPvP.get() != false && !ClientUtil.isPvP();
    }

    public boolean isCurrentItem(ItemEnum item) {
        if (!item.getActive().get().booleanValue()) {
            return false;
        }
        return item.getActive().get() != false && Arrays.stream(ItemEnum.values()).anyMatch(e -> e == item);
    }

    public static enum ItemEnum {
        CHORUS(Items.CHORUS_FRUIT, () -> (Boolean)items.getValueByName("\u0425\u043e\u0440\u0443\u0441\u044b").get(), horusTime::get),
        GOLDEN_APPLE(Items.GOLDEN_APPLE, () -> (Boolean)items.getValueByName("\u0413\u0435\u043f\u043b\u044b").get(), gappleTime::get),
        ENCHANTED_GOLDEN_APPLE(Items.ENCHANTED_GOLDEN_APPLE, () -> (Boolean)items.getValueByName("\u0427\u0430\u0440\u043a\u0438").get(), enchantmentGappleTime::get),
        ENDER_PEARL(Items.ENDER_PEARL, () -> (Boolean)items.getValueByName("\u041f\u0435\u0440\u043a\u0438").get(), pearlTime::get);

        private final Item item;
        private final Supplier<Boolean> active;
        private final Supplier<Float> time;

        private ItemEnum(Item item, Supplier<Boolean> active, Supplier<Float> time) {
            this.item = item;
            this.active = active;
            this.time = time;
        }

        public static ItemEnum getItemEnum(Item item) {
            return Arrays.stream(ItemEnum.values()).filter(e -> e.getItem() == item).findFirst().orElse(null);
        }

        public Item getItem() {
            return this.item;
        }

        public Supplier<Boolean> getActive() {
            return this.active;
        }

        public Supplier<Float> getTime() {
            return this.time;
        }
    }
}

