/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AirItem;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;

@ModuleRegister(name="AutoGapple", category=Category.Combat)
public class AutoGapple
extends Module {
    private final SliderSetting healthSetting = new SliderSetting("\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435", 16.0f, 1.0f, 20.0f, 0.05f);
    private final BooleanSetting eatAtTheStart = new BooleanSetting("\u0421\u044a\u0435\u0441\u0442\u044c \u0432 \u043d\u0430\u0447\u0430\u043b\u0435", true);
    private boolean isEating;
    private final StopWatch stopWatch = new StopWatch();

    public AutoGapple() {
        this.addSettings(this.healthSetting, this.eatAtTheStart);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.shouldToTakeGApple() && ((Boolean)this.eatAtTheStart.get()).booleanValue()) {
            this.takeGappleInOffHand();
        }
        this.eatGapple();
    }

    private void eatGapple() {
        if (this.conditionToEat()) {
            this.startEating();
        } else if (this.isEating) {
            this.stopEating();
        }
    }

    private boolean shouldToTakeGApple() {
        boolean isTicksExisted = AutoGapple.mc.player.ticksExisted == 15;
        boolean appleNotEaten = AutoGapple.mc.player.getAbsorptionAmount() == 0.0f || !AutoGapple.mc.player.isPotionActive(Effects.REGENERATION);
        boolean appleIsNotOffHand = AutoGapple.mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE;
        boolean timeHasPassed = this.stopWatch.isReached(200L);
        boolean settingIsEnalbed = (Boolean)this.eatAtTheStart.get();
        return isTicksExisted && appleNotEaten && appleIsNotOffHand & timeHasPassed && settingIsEnalbed;
    }

    private void takeGappleInOffHand() {
        int gappleSlot = InventoryUtil.getInstance().getSlotInInventory(Items.GOLDEN_APPLE);
        if (gappleSlot >= 0) {
            this.moveGappleToOffhand(gappleSlot);
        }
    }

    private void moveGappleToOffhand(int gappleSlot) {
        if (gappleSlot < 9 && gappleSlot != -1) {
            gappleSlot += 36;
        }
        AutoGapple.mc.playerController.windowClick(0, gappleSlot, 0, ClickType.PICKUP, AutoGapple.mc.player);
        AutoGapple.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, AutoGapple.mc.player);
        if (!(AutoGapple.mc.player.getHeldItemOffhand().getItem() instanceof AirItem)) {
            AutoGapple.mc.playerController.windowClick(0, gappleSlot, 0, ClickType.PICKUP, AutoGapple.mc.player);
        }
        this.stopWatch.reset();
    }

    private void startEating() {
        if (AutoGapple.mc.currentScreen != null) {
            AutoGapple.mc.currentScreen.passEvents = true;
        }
        if (!AutoGapple.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            AutoGapple.mc.gameSettings.keyBindUseItem.setPressed(true);
            this.isEating = true;
        }
    }

    private void stopEating() {
        AutoGapple.mc.gameSettings.keyBindUseItem.setPressed(false);
        this.isEating = false;
    }

    private boolean conditionToEat() {
        float myHealth = AutoGapple.mc.player.getHealth() + AutoGapple.mc.player.getAbsorptionAmount();
        boolean appleNotEaten = AutoGapple.mc.player.getAbsorptionAmount() == 0.0f || !AutoGapple.mc.player.isPotionActive(Effects.REGENERATION);
        return (this.isHealthLow(myHealth) || AutoGapple.mc.player.ticksExisted < 100 && appleNotEaten) && this.hasGappleInHand() && !this.isGappleOnCooldown();
    }

    private boolean isGappleOnCooldown() {
        return AutoGapple.mc.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE);
    }

    private boolean isHealthLow(float health) {
        return health <= ((Float)this.healthSetting.get()).floatValue();
    }

    private boolean hasGappleInHand() {
        return AutoGapple.mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE || AutoGapple.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE;
    }

    private void reset() {
        this.stopWatch.reset();
    }

    @Override
    public void onDisable() {
        this.reset();
        super.onDisable();
    }
}

