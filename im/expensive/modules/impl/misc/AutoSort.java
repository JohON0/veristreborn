/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SignItem;
import net.minecraft.item.SkullItem;
import org.lwjgl.glfw.GLFW;

@ModuleRegister(name="AutoSort", category=Category.Misc)
public class AutoSort
extends Module {
    private final StopWatch second = new StopWatch();
    private BooleanSetting onPressAltKey = new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 Alt", false);
    private SliderSetting droperDelay = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u0432\u044b\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u043d\u0438\u044f", 100.0f, 0.0f, 1000.0f, 10.0f);
    private BooleanSetting armor = new BooleanSetting("\u0412\u044b\u043a\u0438\u0434\u044b\u0432\u0430\u0442\u044c \u0431\u0440\u043e\u043d\u044e", false);

    public AutoSort() {
        this.addSettings(this.onPressAltKey, this.droperDelay, this.armor);
    }

    @Subscribe
    private void onUpdt(EventUpdate event) {
        boolean ctrlDown;
        boolean bl = ctrlDown = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), 342) == 1 || GLFW.glfwGetKey(mc.getMainWindow().getHandle(), 262) == 1;
        if (!ctrlDown && ((Boolean)this.onPressAltKey.get()).booleanValue()) {
            return;
        }
        if (AutoSort.mc.currentScreen == null || AutoSort.mc.currentScreen instanceof InventoryScreen) {
            for (int i = 9; i < 45; ++i) {
                ItemStack is = AutoSort.mc.player.inventory.getStackInSlot(i);
                if (is == AutoSort.mc.player.inventory.armorInventory.get(0) || is == AutoSort.mc.player.inventory.armorInventory.get(1) || is == AutoSort.mc.player.inventory.armorInventory.get(2) || is == AutoSort.mc.player.inventory.armorInventory.get(3) || !this.shouldDrop(is, i) || !this.second.isReached(((Float)this.droperDelay.get()).longValue()) || AutoSort.mc.player.getHeldItemMainhand().getItem() == Items.FIREWORK_ROCKET) continue;
                AutoSort.mc.playerController.windowClick(0, i, 1, ClickType.THROW, AutoSort.mc.player);
                this.second.reset();
            }
        }
    }

    public boolean shouldDrop(ItemStack stack, int slot) {
        if (stack.getItem() instanceof BlockItem && !(stack.getItem() instanceof SkullItem) && stack.getItem() != BlockItem.getItemById(7) && stack.getItem() != BlockItem.getItemById(33) && stack.getItem() != BlockItem.getItemById(29) && stack.getItem() != BlockItem.getItemById(46) && stack.getItem() != BlockItem.getItemById(120) && stack.getItem() != BlockItem.getItemById(41) && stack.getItem() != BlockItem.getItemById(89) && stack.getItem() != BlockItem.getItemById(14) && stack.getItem() != BlockItem.getItemById(133) && stack.getItem() != BlockItem.getItemById(129) && stack.getItem() != BlockItem.getItemById(56) && stack.getItem() != BlockItem.getItemById(57) && stack.getItem() != BlockItem.getItemById(165) && stack.getItem() != BlockItem.getItemById(52) && stack.getItem() != BlockItem.getItemById(30) && stack.getItem() != BlockItem.getItemById(49) && stack.getItem() != BlockItem.getItemById(130) && stack.getItem() != BlockItem.getItemById(49) && stack.getItem() != BlockItem.getItemById(219) && stack.getItem() != BlockItem.getItemById(220) && stack.getItem() != BlockItem.getItemById(221) && stack.getItem() != BlockItem.getItemById(222) && stack.getItem() != BlockItem.getItemById(223) && stack.getItem() != BlockItem.getItemById(224) && stack.getItem() != BlockItem.getItemById(225) && stack.getItem() != BlockItem.getItemById(226) && stack.getItem() != BlockItem.getItemById(227) && stack.getItem() != BlockItem.getItemById(228) && stack.getItem() != BlockItem.getItemById(229) && stack.getItem() != BlockItem.getItemById(230) && stack.getItem() != BlockItem.getItemById(231) && stack.getItem() != BlockItem.getItemById(232) && stack.getItem() != BlockItem.getItemById(233) && stack.getItem() != BlockItem.getItemById(234)) {
            return true;
        }
        if (stack.getItem() == Items.STICK || stack.getItem() == BlockItem.getItemById(324) || stack.getItem() == BlockItem.getItemById(330) || stack.getItem() == BlockItem.getItemById(427) || stack.getItem() == BlockItem.getItemById(428) || stack.getItem() == BlockItem.getItemById(429) || stack.getItem() == BlockItem.getItemById(430) || stack.getItem() == BlockItem.getItemById(431) || stack.getItem() == BlockItem.getItemById(328) || stack.getItem() == BlockItem.getItemById(407) || stack.getItem() == BlockItem.getItemById(342) || stack.getItem() == BlockItem.getItemById(333) || stack.getItem() == BlockItem.getItemById(444) || stack.getItem() == BlockItem.getItemById(445) || stack.getItem() == BlockItem.getItemById(446) || stack.getItem() == BlockItem.getItemById(447) || stack.getItem() == BlockItem.getItemById(448) || stack.getItem() == Items.BEETROOT_SEEDS || stack.getItem() == Items.WHEAT || stack.getItem() == Items.MELON_SEEDS || stack.getItem() == Items.PUMPKIN_SEEDS || stack.getItem() == Items.WHEAT_SEEDS || stack.getItem() instanceof DyeItem || stack.getItem() == Items.CLOCK || stack.getItem() == Items.COMPASS || stack.getItem() == Items.PAPER || stack.getItem() == Items.FISHING_ROD || stack.getItem() == Items.SLIME_BALL || stack.getItem() == Items.CLAY_BALL || stack.getItem() == Items.BONE || stack.getItem() == Items.BOWL || stack.getItem() == Items.CARROT_ON_A_STICK || stack.getItem() == Items.FEATHER || stack.getItem() == Items.GLASS_BOTTLE || stack.getItem() == Items.SADDLE || stack.getItem() instanceof SignItem || stack.getItem() == Items.MAP || stack.getItem() == Items.EGG || stack.getItem() == Items.HOPPER_MINECART || stack.getItem() == Items.FLOWER_POT || stack.getItem() == Items.LEATHER || stack.getItem() == Items.NAME_TAG || stack.getItem() == Items.SHEARS || stack.getItem() == Items.ENCHANTED_BOOK || stack.getItem() == Items.LEAD) {
            return true;
        }
        if (stack.getItem() == Items.ROTTEN_FLESH || stack.getItem() == Items.POTATO || stack.getItem() == Items.CHICKEN || stack.getItem() == Items.BEEF || stack.getItem() == Items.TROPICAL_FISH || stack.getItem() == Items.PUFFERFISH || stack.getItem() == Items.MUTTON || stack.getItem() == Items.PORKCHOP || stack.getItem() == Items.RABBIT || stack.getItem() == Items.COOKIE) {
            return true;
        }
        if (((Boolean)this.armor.get()).booleanValue() && stack.getItem() instanceof ArmorItem || stack.getItem() instanceof BucketItem || stack.getItem() instanceof FlintAndSteelItem || stack.getItem() == Items.COAL || stack.getItem() == Items.GOLD_INGOT || stack.getItem() == Items.IRON_INGOT || stack.getItem() == Items.COBBLESTONE) {
            return true;
        }
        return stack.getItem() == Items.WOODEN_SWORD || stack.getItem() == Items.WOODEN_PICKAXE || stack.getItem() == Items.WOODEN_AXE || stack.getItem() == Items.WOODEN_SHOVEL || stack.getItem() == Items.WOODEN_HOE || stack.getItem() == Items.STONE_SWORD || stack.getItem() == Items.STONE_PICKAXE || stack.getItem() == Items.STONE_AXE || stack.getItem() == Items.STONE_SHOVEL || stack.getItem() == Items.STONE_HOE || stack.getItem() == Items.IRON_SWORD || stack.getItem() == Items.IRON_PICKAXE || stack.getItem() == Items.IRON_AXE || stack.getItem() == Items.IRON_SHOVEL || stack.getItem() == Items.IRON_HOE || stack.getItem() == Items.GOLDEN_SWORD || stack.getItem() == Items.GOLDEN_PICKAXE || stack.getItem() == Items.GOLDEN_AXE || stack.getItem() == Items.GOLDEN_SHOVEL || stack.getItem() == Items.GOLDEN_HOE;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.print(this.getName() + " \u043a \u0441\u043e\u0436\u0430\u043b\u0435\u043d\u0438\u044e \u043d\u0435 \u043e\u0447\u0438\u0449\u0430\u0435\u0442 \u0445\u043e\u0442\u0431\u0430\u0440");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}

