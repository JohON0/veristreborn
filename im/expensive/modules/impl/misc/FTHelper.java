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
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;

@ModuleRegister(name="FTHelper", category=Category.Misc)
public class FTHelper
extends Module {
    private final ModeListSetting mode = new ModeListSetting("\u0422\u0438\u043f", new BooleanSetting("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043f\u043e \u0431\u0438\u043d\u0434\u0443", true), new BooleanSetting("\u0417\u0430\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u043c\u0435\u043d\u044e", true));
    private final BindSetting disorientationKey = new BindSetting("\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", -1).setVisible(() -> (Boolean)this.mode.getValueByName("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043f\u043e \u0431\u0438\u043d\u0434\u0443").get());
    private final BindSetting trapKey = new BindSetting("\u0422\u0440\u0430\u043f\u043a\u0430", -1).setVisible(() -> (Boolean)this.mode.getValueByName("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043f\u043e \u0431\u0438\u043d\u0434\u0443").get());
    final StopWatch stopWatch = new StopWatch();
    InventoryUtil.Hand handUtil = new InventoryUtil.Hand();
    long delay;
    boolean disorientationThrow;
    boolean trapThrow;

    public FTHelper() {
        this.addSettings(this.mode, this.disorientationKey, this.trapKey);
    }

    @Subscribe
    private void onKey(EventKey e) {
        if (e.getKey() == ((Integer)this.disorientationKey.get()).intValue()) {
            this.disorientationThrow = true;
        }
        if (e.getKey() == ((Integer)this.trapKey.get()).intValue()) {
            this.trapThrow = true;
        }
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        int invSlot;
        int hbSlot;
        if (this.disorientationThrow) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            hbSlot = this.getItemForName("\u0434\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", true);
            invSlot = this.getItemForName("\u0434\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430!");
                this.disorientationThrow = false;
                return;
            }
            if (!FTHelper.mc.player.getCooldownTracker().hasCooldown(Items.ENDER_EYE)) {
                this.print("\u0417\u0430\u044e\u0437\u0430\u043b \u0434\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044e!");
                int slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    FTHelper.mc.playerController.pickItem(slot);
                }
            }
            this.disorientationThrow = false;
        }
        if (this.trapThrow) {
            hbSlot = this.getItemForName("\u0442\u0440\u0430\u043f\u043a\u0430", true);
            invSlot = this.getItemForName("\u0442\u0440\u0430\u043f\u043a\u0430", false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("\u0422\u0440\u0430\u043f\u043a\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430");
                this.trapThrow = false;
                return;
            }
            if (!FTHelper.mc.player.getCooldownTracker().hasCooldown(Items.NETHERITE_SCRAP)) {
                this.print("\u0417\u0430\u044e\u0437\u0430\u043b \u0442\u0440\u0430\u043f\u043a\u0443!");
                int old = FTHelper.mc.player.inventory.currentItem;
                int slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    FTHelper.mc.playerController.pickItem(slot);
                }
                if (InventoryUtil.findEmptySlot(true) != -1 && FTHelper.mc.player.inventory.currentItem != old) {
                    FTHelper.mc.player.inventory.currentItem = old;
                }
            }
            this.trapThrow = false;
        }
        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        this.handUtil.onEventPacket(e);
    }

    private int findAndTrowItem(int hbSlot, int invSlot) {
        if (hbSlot != -1) {
            this.handUtil.setOriginalSlot(FTHelper.mc.player.inventory.currentItem);
            FTHelper.mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            FTHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            FTHelper.mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return hbSlot;
        }
        if (invSlot != -1) {
            this.handUtil.setOriginalSlot(FTHelper.mc.player.inventory.currentItem);
            FTHelper.mc.playerController.pickItem(invSlot);
            FTHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            FTHelper.mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return invSlot;
        }
        return -1;
    }

    @Override
    public void onDisable() {
        this.disorientationThrow = false;
        this.trapThrow = false;
        this.delay = 0L;
        super.onDisable();
    }

    private int getItemForName(String name, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; ++i) {
            String displayName;
            ItemStack itemStack = FTHelper.mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof AirItem || (displayName = TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().getString())) == null || !displayName.toLowerCase().contains(name)) continue;
            return i;
        }
        return -1;
    }
}

