/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventKey;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.text.TextFormatting;

@ModuleRegister(name="ElytraHelper", category=Category.Misc)
public class ElytraHelper
extends Module {
    private final BindSetting swapChestKey = new BindSetting("\u041a\u043d\u043e\u043f\u043a\u0430 \u0441\u0432\u0430\u043f\u0430", -1);
    private final BindSetting fireWorkKey = new BindSetting("\u041a\u043d\u043e\u043f\u043a\u0430 \u0444\u0435\u0435\u0440\u0432\u0435\u0440\u043a\u043e\u0432", -1);
    private final BooleanSetting autoFireWork = new BooleanSetting("\u0410\u0432\u0442\u043e \u0444\u0435\u0435\u0440\u0432\u0435\u0440\u043a", true);
    private final SliderSetting timerFireWork = new SliderSetting("\u0422\u0430\u0439\u043c\u0435\u0440 \u0444\u0435\u0435\u0440\u0430", 400.0f, 100.0f, 2000.0f, 10.0f).setVisible(() -> (Boolean)this.autoFireWork.get());
    private final BooleanSetting autoFly = new BooleanSetting("\u0410\u0432\u0442\u043e \u0432\u0437\u043b\u0451\u0442", true);
    private final InventoryUtil.Hand handUtil = new InventoryUtil.Hand();
    private ItemStack currentStack = ItemStack.EMPTY;
    public static StopWatch stopWatch = new StopWatch();
    public static StopWatch fireWorkStopWatch = new StopWatch();
    private long delay;
    private boolean fireworkUsed;
    public StopWatch wait = new StopWatch();

    public ElytraHelper() {
        this.addSettings(this.swapChestKey, this.fireWorkKey, this.autoFly, this.autoFireWork, this.timerFireWork);
    }

    @Subscribe
    private void onEventKey(EventKey e) {
        if (e.getKey() == ((Integer)this.swapChestKey.get()).intValue() && stopWatch.isReached(100L)) {
            this.changeChestPlate(this.currentStack);
            stopWatch.reset();
        }
        if (e.getKey() == ((Integer)this.fireWorkKey.get()).intValue() && stopWatch.isReached(200L) && ElytraHelper.mc.player.isElytraFlying()) {
            InventoryUtil.inventorySwapClick(Items.FIREWORK_ROCKET, false);
        }
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        this.currentStack = ElytraHelper.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (ElytraHelper.mc.player != null) {
            KeyBinding[] pressedKeys = new KeyBinding[]{ElytraHelper.mc.gameSettings.keyBindForward, ElytraHelper.mc.gameSettings.keyBindBack, ElytraHelper.mc.gameSettings.keyBindLeft, ElytraHelper.mc.gameSettings.keyBindRight, ElytraHelper.mc.gameSettings.keyBindJump, ElytraHelper.mc.gameSettings.keyBindSprint};
            if (ClientUtil.isConnectedToServer("funtime") && !this.wait.isReached(400L)) {
                for (KeyBinding keyBinding : pressedKeys) {
                    keyBinding.setPressed(false);
                }
                return;
            }
        }
        if (((Boolean)this.autoFly.get()).booleanValue() && this.currentStack.getItem() == Items.ELYTRA) {
            if (ElytraHelper.mc.player.isOnGround()) {
                ElytraHelper.mc.player.jump();
            } else if (ElytraItem.isUsable(this.currentStack) && !ElytraHelper.mc.player.isElytraFlying() && !ElytraHelper.mc.player.abilities.isFlying) {
                ElytraHelper.mc.player.startFallFlying();
                ElytraHelper.mc.player.connection.sendPacket(new CEntityActionPacket(ElytraHelper.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            }
        }
        if (ElytraHelper.mc.player.isElytraFlying() && ((Boolean)this.autoFireWork.get()).booleanValue() && fireWorkStopWatch.isReached(((Float)this.timerFireWork.get()).longValue())) {
            InventoryUtil.inventorySwapClick(Items.FIREWORK_ROCKET, false);
            fireWorkStopWatch.reset();
        }
        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        this.handUtil.onEventPacket(e);
    }

    private void changeChestPlate(ItemStack stack) {
        int armorSlot;
        if (ElytraHelper.mc.currentScreen != null) {
            return;
        }
        if (stack.getItem() != Items.ELYTRA) {
            int elytraSlot = this.getItemSlot(Items.ELYTRA);
            if (elytraSlot >= 0) {
                InventoryUtil.moveItem(elytraSlot, 6);
                this.print(TextFormatting.RED + "\u0421\u0432\u0430\u043f\u043d\u0443\u043b \u043d\u0430 \u044d\u043b\u0438\u0442\u0440\u0443!");
                return;
            }
            this.print("\u042d\u043b\u0438\u0442\u0440\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430!");
        }
        if ((armorSlot = this.getChestPlateSlot()) >= 0) {
            InventoryUtil.moveItem(armorSlot, 6);
            this.print(TextFormatting.RED + "\u0421\u0432\u0430\u043f\u043d\u0443\u043b \u043d\u0430 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a!");
        } else {
            this.print("\u041d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
        }
    }

    private int getChestPlateSlot() {
        Item[] items;
        for (Item item : items = new Item[]{Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE}) {
            for (int i = 0; i < 36; ++i) {
                Item stack = ElytraHelper.mc.player.inventory.getStackInSlot(i).getItem();
                if (stack != item) continue;
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDisable() {
        stopWatch.reset();
        super.onDisable();
    }

    private int getItemSlot(Item input) {
        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = ElytraHelper.mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != input) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }
}

