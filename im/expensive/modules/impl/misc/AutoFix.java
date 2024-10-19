/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.modules.settings.impl.StringSetting;
import im.expensive.utils.client.KeyStorage;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.PotionUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

@ModuleRegister(name="AutoFix", category=Category.Misc)
public class AutoFix
extends Module {
    public ModeSetting mode = new ModeSetting("\u0422\u0438\u043f \u043f\u043e\u0447\u0438\u043d\u043a\u0438", "\u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0438", "\u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0438", "\u041a\u043e\u043c\u0430\u043d\u0434\u0430");
    public StringSetting name = new StringSetting("\u041a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043b\u044f \u043f\u043e\u0447\u0438\u043d\u043a\u0438", "/fix all", "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0442\u0435\u043a\u0441\u0442 \u0434\u043b\u044f \u043f\u043e\u0447\u0438\u043d\u043a\u0438").setVisible(() -> this.mode.is("\u041a\u043e\u043c\u0430\u043d\u0434\u0430"));
    public BindSetting bind = new BindSetting("\u041a\u043d\u043e\u043f\u043a\u0430", -1).setVisible(() -> this.mode.is("\u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0438"));
    public SliderSetting delay = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 50.0f, 0.0f, 500.0f, 1.0f).setVisible(() -> this.mode.is("\u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0438"));
    private final StopWatch stopWatch = new StopWatch();
    private final StopWatch throwDelay = new StopWatch();
    private float previousPitch;
    private int selectedSlot;
    private final PotionUtil potionUtil = new PotionUtil();

    public AutoFix() {
        this.addSettings(this.mode, this.name, this.bind, this.delay);
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (!this.mode.is("\u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0438")) {
            return;
        }
        if (AutoFix.mc.currentScreen != null) {
            return;
        }
        if (this.isNotPressed()) {
            return;
        }
        if (this.isNotThrowing()) {
            return;
        }
        if (this.checkFixInv().equals(ItemStack.EMPTY) || this.getPotionIndexInv() == -1 && this.getPotionIndexHb() == -1) {
            return;
        }
        this.previousPitch = 90.0f;
        e.setPitch(this.previousPitch);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.mode.is("\u041a\u043e\u043c\u0430\u043d\u0434\u0430") && this.stopWatch.isReached(1000L) && this.checkFix(AutoFix.mc.player.getHeldItemMainhand())) {
            this.print((String)this.name.get());
            this.stopWatch.reset();
        }
        if (this.mode.is("\u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0438")) {
            if (AutoFix.mc.currentScreen != null) {
                return;
            }
            if (this.isNotPressed()) {
                return;
            }
            if (!this.checkFixInv().equals(ItemStack.EMPTY)) {
                if (!this.isNotThrowing() && this.previousPitch == AutoFix.mc.player.lastReportedPitch && this.throwDelay.isReached(((Float)this.delay.get()).intValue())) {
                    int oldItem = AutoFix.mc.player.inventory.currentItem;
                    this.selectedSlot = -1;
                    int slot = this.setSlotAndUseItem();
                    if (this.selectedSlot == -1) {
                        this.selectedSlot = slot;
                    }
                    if (this.selectedSlot > 8) {
                        AutoFix.mc.playerController.pickItem(this.selectedSlot);
                    }
                    AutoFix.mc.player.connection.sendPacket(new CHeldItemChangePacket(oldItem));
                    this.throwDelay.reset();
                }
                if (this.stopWatch.isReached(500L)) {
                    try {
                        this.selectedSlot = -2;
                    } catch (Exception exception) {
                        // empty catch block
                    }
                }
                this.potionUtil.changeItemSlot(this.selectedSlot == -2);
            }
        }
    }

    private boolean isNotPressed() {
        return !KeyStorage.isKeyDown((Integer)this.bind.get());
    }

    private int setSlotAndUseItem() {
        int hbSlot = this.getPotionIndexHb();
        if (hbSlot != -1) {
            this.potionUtil.setPreviousSlot(AutoFix.mc.player.inventory.currentItem);
            AutoFix.mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            PotionUtil.useItem(Hand.MAIN_HAND);
            this.stopWatch.reset();
            return hbSlot;
        }
        int invSlot = this.getPotionIndexInv();
        if (invSlot != -1) {
            this.potionUtil.setPreviousSlot(AutoFix.mc.player.inventory.currentItem);
            AutoFix.mc.playerController.pickItem(invSlot);
            PotionUtil.useItem(Hand.MAIN_HAND);
            AutoFix.mc.player.connection.sendPacket(new CHeldItemChangePacket(AutoFix.mc.player.inventory.currentItem));
            this.stopWatch.reset();
            return invSlot;
        }
        return -1;
    }

    public boolean isNotThrowing() {
        return !AutoFix.mc.player.isOnGround() && !AutoFix.mc.world.getBlockState(new BlockPos(AutoFix.mc.player.getPosX(), AutoFix.mc.player.getBoundingBox().minY - (double)0.3f, AutoFix.mc.player.getPosZ())).isSolid() || AutoFix.mc.player.isOnLadder() || AutoFix.mc.player.getRidingEntity() != null || AutoFix.mc.player.abilities.isFlying;
    }

    private int getPotionIndexHb() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = AutoFix.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != Items.EXPERIENCE_BOTTLE) continue;
            return i;
        }
        return -1;
    }

    private int getPotionIndexInv() {
        for (int i = 9; i < 36; ++i) {
            ItemStack stack = AutoFix.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != Items.EXPERIENCE_BOTTLE) continue;
            return i;
        }
        return -1;
    }

    private boolean checkFix(ItemStack item) {
        return item.getMaxDamage() != 0 && item.getMaxDamage() - item.getDamage() <= 3;
    }

    private ItemStack checkFixInv() {
        String stackItemEnchant;
        int j;
        boolean mending;
        for (ItemStack stack : AutoFix.mc.player.getArmorInventoryList()) {
            if (stack.isEmpty()) continue;
            mending = false;
            if (stack.isEnchanted()) {
                for (j = 0; j < stack.getEnchantmentTagList().size(); ++j) {
                    stackItemEnchant = stack.getEnchantmentTagList().getCompound(j).getString("id").replaceAll("minecraft:", "");
                    if (!stackItemEnchant.equalsIgnoreCase("mending")) continue;
                    mending = true;
                    break;
                }
            }
            if (stack.getMaxDamage() == 0 || stack.getDamage() == 0 || !mending) continue;
            return stack;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = AutoFix.mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            mending = false;
            if (stack.isEnchanted()) {
                for (j = 0; j < stack.getEnchantmentTagList().size(); ++j) {
                    stackItemEnchant = stack.getEnchantmentTagList().getCompound(j).getString("id").replaceAll("minecraft:", "");
                    if (!stackItemEnchant.equalsIgnoreCase("mending")) continue;
                    mending = true;
                    break;
                }
            }
            if (stack.getMaxDamage() == 0 || stack.getDamage() == 0 || !mending) continue;
            return stack;
        }
        return ItemStack.EMPTY;
    }
}

