/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventCooldown;
import im.expensive.events.EventKey;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.AutoTotem;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.potion.Effects;

@ModuleRegister(name="AutoSwap", category=Category.Combat)
public class AutoSwap
extends Module {
    private final ModeSetting swapMode = new ModeSetting("\u0422\u0438\u043f", "\u0423\u043c\u043d\u044b\u0439", "\u0423\u043c\u043d\u044b\u0439", "\u041f\u043e \u0431\u0438\u043d\u0434\u0443");
    private final ModeSetting itemType = new ModeSetting("\u041f\u0440\u0435\u0434\u043c\u0435\u0442", "\u0429\u0438\u0442", "\u0429\u0438\u0442", "\u0413\u0435\u043f\u043b\u044b", "\u0422\u043e\u0442\u0435\u043c", "\u0428\u0430\u0440");
    private final ModeSetting swapType = new ModeSetting("\u0421\u0432\u0430\u043f\u0430\u0442\u044c \u043d\u0430", "\u0413\u0435\u043f\u043b\u044b", "\u0429\u0438\u0442", "\u0413\u0435\u043f\u043b\u044b", "\u0422\u043e\u0442\u0435\u043c", "\u0428\u0430\u0440");
    private final BindSetting keyToSwap = new BindSetting("\u041a\u043d\u043e\u043f\u043a\u0430", -1).setVisible(() -> this.swapMode.is("\u041f\u043e \u0431\u0438\u043d\u0434\u0443"));
    private final SliderSetting health = new SliderSetting("\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435", 11.0f, 5.0f, 19.0f, 0.5f).setVisible(() -> this.swapMode.is("\u0423\u043c\u043d\u044b\u0439"));
    private final StopWatch stopWatch = new StopWatch();
    private boolean shieldIsCooldown;
    private int oldItem = -1;
    private final StopWatch delay = new StopWatch();
    private final AutoTotem autoTotem;

    public AutoSwap(AutoTotem autoTotem) {
        this.autoTotem = autoTotem;
        this.addSettings(this.swapMode, this.itemType, this.swapType, this.keyToSwap, this.health);
    }

    @Subscribe
    public void onEventKey(EventKey e) {
        boolean isOffhandNotEmpty;
        if (!this.swapMode.is("\u041f\u043e \u0431\u0438\u043d\u0434\u0443")) {
            return;
        }
        ItemStack offhandItemStack = AutoSwap.mc.player.getHeldItemOffhand();
        boolean bl = isOffhandNotEmpty = !(offhandItemStack.getItem() instanceof AirItem);
        if (e.isKeyDown((Integer)this.keyToSwap.get()) && this.stopWatch.isReached(200L)) {
            Item currentItem = offhandItemStack.getItem();
            boolean isHoldingSwapItem = currentItem == this.getSwapItem();
            boolean isHoldingSelectedItem = currentItem == this.getSelectedItem();
            int selectedItemSlot = this.getSlot(this.getSelectedItem());
            int swapItemSlot = this.getSlot(this.getSwapItem());
            if (selectedItemSlot >= 0 && !isHoldingSelectedItem) {
                InventoryUtil.moveItem(selectedItemSlot, 45, isOffhandNotEmpty);
                this.stopWatch.reset();
                return;
            }
            if (swapItemSlot >= 0 && !isHoldingSwapItem) {
                InventoryUtil.moveItem(swapItemSlot, 45, isOffhandNotEmpty);
                this.stopWatch.reset();
            }
        }
    }

    @Subscribe
    private void onCooldown(EventCooldown e) {
        this.shieldIsCooldown = this.isCooldown(e);
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (!this.swapMode.is("\u0423\u043c\u043d\u044b\u0439")) {
            return;
        }
        Item currentItem = AutoSwap.mc.player.getHeldItemOffhand().getItem();
        if (this.stopWatch.isReached(400L)) {
            this.swapIfShieldIsBroken(currentItem);
            this.swapIfHealthToLow(currentItem);
            this.stopWatch.reset();
        }
        boolean isRightClickWithGoldenAppleActive = false;
        if (currentItem == Items.GOLDEN_APPLE && !AutoSwap.mc.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE)) {
            isRightClickWithGoldenAppleActive = AutoSwap.mc.gameSettings.keyBindUseItem.isKeyDown();
        }
        if (isRightClickWithGoldenAppleActive) {
            this.stopWatch.reset();
        }
    }

    @Override
    public void onDisable() {
        this.shieldIsCooldown = false;
        this.oldItem = -1;
        super.onDisable();
    }

    private void swapIfHealthToLow(Item currentItem) {
        boolean isOffhandNotEmpty = !(currentItem instanceof AirItem);
        boolean isHoldingGoldenApple = currentItem == this.getSwapItem();
        boolean isHoldingSelectedItem = currentItem == this.getSelectedItem();
        boolean gappleIsNotCooldown = !AutoSwap.mc.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE);
        int goldenAppleSlot = this.getSlot(this.getSwapItem());
        if (this.shieldIsCooldown || !gappleIsNotCooldown) {
            return;
        }
        if (this.isLowHealth() && !isHoldingGoldenApple && isHoldingSelectedItem) {
            InventoryUtil.moveItem(goldenAppleSlot, 45, isOffhandNotEmpty);
            if (isOffhandNotEmpty && this.oldItem == -1) {
                this.oldItem = goldenAppleSlot;
            }
        } else if (!this.isLowHealth() && isHoldingGoldenApple && this.oldItem >= 0) {
            InventoryUtil.moveItem(this.oldItem, 45, isOffhandNotEmpty);
            this.oldItem = -1;
        }
    }

    private void swapIfShieldIsBroken(Item currentItem) {
        boolean isOffhandNotEmpty = !(currentItem instanceof AirItem);
        boolean isHoldingGoldenApple = currentItem == this.getSwapItem();
        boolean isHoldingSelectedItem = currentItem == this.getSelectedItem();
        boolean gappleIsNotCooldown = !AutoSwap.mc.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE);
        int goldenAppleSlot = this.getSlot(this.getSwapItem());
        if (this.shieldIsCooldown && !isHoldingGoldenApple && isHoldingSelectedItem && gappleIsNotCooldown) {
            InventoryUtil.moveItem(goldenAppleSlot, 45, isOffhandNotEmpty);
            if (isOffhandNotEmpty && this.oldItem == -1) {
                this.oldItem = goldenAppleSlot;
            }
            this.print("" + this.shieldIsCooldown);
        } else if (!this.shieldIsCooldown && isHoldingGoldenApple && this.oldItem >= 0) {
            InventoryUtil.moveItem(this.oldItem, 45, isOffhandNotEmpty);
            this.oldItem = -1;
        }
    }

    private boolean isLowHealth() {
        float currentHealth = AutoSwap.mc.player.getHealth() + (AutoSwap.mc.player.isPotionActive(Effects.ABSORPTION) ? AutoSwap.mc.player.getAbsorptionAmount() : 0.0f);
        return currentHealth <= ((Float)this.health.get()).floatValue();
    }

    private boolean isCooldown(EventCooldown cooldown) {
        Item item = cooldown.getItem();
        if (!this.itemType.is("Shield")) {
            return false;
        }
        return cooldown.isAdded() && item instanceof ShieldItem;
    }

    private Item getSwapItem() {
        return this.getItemByType((String)this.swapType.get());
    }

    private Item getSelectedItem() {
        return this.getItemByType((String)this.itemType.get());
    }

    private Item getItemByType(String itemType) {
        return switch (itemType) {
            case "\u0429\u0438\u0442" -> Items.SHIELD;
            case "\u0422\u043e\u0442\u0435\u043c" -> Items.TOTEM_OF_UNDYING;
            case "\u0413\u0435\u043f\u043b\u044b" -> Items.GOLDEN_APPLE;
            case "\u0428\u0430\u0440" -> Items.PLAYER_HEAD;
            default -> Items.AIR;
        };
    }

    private int getSlot(Item item) {
        int finalSlot = -1;
        for (int i = 0; i < 36; ++i) {
            if (AutoSwap.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            if (AutoSwap.mc.player.inventory.getStackInSlot(i).isEnchanted()) {
                finalSlot = i;
                break;
            }
            finalSlot = i;
        }
        if (finalSlot < 9 && finalSlot != -1) {
            finalSlot += 36;
        }
        return finalSlot;
    }
}

