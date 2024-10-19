/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;

@ModuleRegister(name="RWJoiner", category=Category.Misc)
public class RWJoiner
extends Module {
    private final SliderSetting griefSelection = new SliderSetting("\u041d\u043e\u043c\u0435\u0440 \u0433\u0440\u0438\u0444\u0430", 1.0f, 1.0f, 42.0f, 1.0f);
    private final StopWatch timerUtil = new StopWatch();
    public int grief;

    public RWJoiner() {
        this.addSettings(this.griefSelection);
    }

    @Override
    public void onEnable() {
        int slot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(Items.COMPASS, true);
        if (slot != -1) {
            RWJoiner.mc.player.inventory.currentItem = slot;
            RWJoiner.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
        }
        RWJoiner.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        super.onEnable();
    }

    @Subscribe
    private void onUpdate(EventUpdate event) {
        this.grief = ((Float)this.griefSelection.get()).intValue();
        this.handleEventUpdate();
    }

    @Subscribe
    public void onPacket(EventPacket eventPacket) {
        SChatPacket packet;
        IPacket<?> packetPacket;
        Object govno;
        if (eventPacket.getPacket() instanceof SJoinGamePacket) {
            try {
                govno = "\u0412\u044b \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0437\u0430\u0448\u043b\u0438 \u043d\u0430 " + this.grief + " \u0433\u0440\u0438\u0444!";
                this.print((String)govno);
                this.toggle();
            } catch (Exception exception) {
                // empty catch block
            }
        }
        if ((packetPacket = eventPacket.getPacket()) instanceof SChatPacket && (((String)(govno = TextFormatting.getTextWithoutFormattingCodes((packet = (SChatPacket)packetPacket).getChatComponent().getString()))).contains("\u041a \u0441\u043e\u0436\u0430\u043b\u0435\u043d\u0438\u044e \u0441\u0435\u0440\u0432\u0435\u0440 \u043f\u0435\u0440\u0435\u043f\u043e\u043b\u043d\u0435\u043d") || ((String)govno).contains("\u041f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435 20 \u0441\u0435\u043a\u0443\u043d\u0434!") || ((String)govno).contains("\u0431\u043e\u043b\u044c\u0448\u043e\u0439 \u043f\u043e\u0442\u043e\u043a \u0438\u0433\u0440\u043e\u043a\u043e\u0432") || ((String)govno).contains("\u0421\u0435\u0440\u0432\u0435\u0440 \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0430\u0435\u0442\u0441\u044f"))) {
            int slot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(Items.COMPASS, true);
            if (slot != -1) {
                RWJoiner.mc.player.inventory.currentItem = slot;
                RWJoiner.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
            }
            RWJoiner.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        }
    }

    private void handleEventUpdate() {
        if (RWJoiner.mc.currentScreen == null) {
            if (RWJoiner.mc.player.ticksExisted < 5 && this.timerUtil.isReached(100L)) {
                RWJoiner.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                this.timerUtil.reset();
            }
        } else if (RWJoiner.mc.currentScreen instanceof ChestScreen) {
            try {
                int numberGrief = this.grief;
                ContainerScreen container = (ContainerScreen)RWJoiner.mc.currentScreen;
                for (int i = 0; i < ((Container)container.getContainer()).inventorySlots.size(); ++i) {
                    String s = ((Container)container.getContainer()).inventorySlots.get(i).getStack().getDisplayName().getString();
                    if (ClientUtil.isConnectedToServer("reallyworld") && s.contains("\u0413\u0420\u0418\u0424\u0415\u0420\u0421\u041a\u041e\u0415 \u0412\u042b\u0416\u0418\u0412\u0410\u041d\u0418\u0415") && this.timerUtil.isReached(50L)) {
                        RWJoiner.mc.playerController.windowClick(RWJoiner.mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, RWJoiner.mc.player);
                        this.timerUtil.reset();
                    }
                    if (!s.contains("\u0413\u0420\u0418\u0424 #" + numberGrief + " (1.16.5") || !this.timerUtil.isReached(50L)) continue;
                    RWJoiner.mc.playerController.windowClick(RWJoiner.mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, RWJoiner.mc.player);
                    this.timerUtil.reset();
                }
            } catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

