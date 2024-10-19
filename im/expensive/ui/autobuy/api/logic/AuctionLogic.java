/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.autobuy.api.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import im.expensive.ui.autobuy.api.logic.ActivationLogic;
import im.expensive.ui.autobuy.api.model.IItem;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.math.StopWatch;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.client.CClickWindowPacket;

public class AuctionLogic
implements IMinecraft {
    private final ActivationLogic parent;
    private final Minecraft mc;
    final StopWatch refreshStopWatch = new StopWatch();
    final StopWatch buyStopWatch = new StopWatch();
    final StopWatch leaveAuctionStopWatch = new StopWatch();
    final StopWatch returnAuctionStopWatch = new StopWatch();
    boolean leave;
    boolean returnAuc;
    private long lastClickTime = System.currentTimeMillis();

    public AuctionLogic(ActivationLogic parent) {
        this.parent = parent;
        this.mc = Minecraft.getInstance();
    }

    public void processActive() {
        Screen screen = this.mc.currentScreen;
        if (screen instanceof ChestScreen) {
            ChestScreen chestScreen = (ChestScreen)screen;
            this.processBuy(chestScreen);
        }
    }

    public void processBuy(ChestScreen chestScreen) {
        Object container = chestScreen.getContainer();
        if (chestScreen.getTitle().getString().toLowerCase().contains("\u043f\u043e\u0434\u043e\u0437\u0440\u0438\u0442\u0435\u043b\u044c\u043d\u0430\u044f \u0446\u0435\u043d\u0430!")) {
            this.mc.playerController.windowClick(((Container)container).windowId, 10, 0, ClickType.PICKUP, this.mc.player);
        }
        if (chestScreen.getTitle().getString().contains("\u0410\u0443\u043a\u0446\u0438\u043e\u043d") || chestScreen.getTitle().getString().contains("\u041f\u043e\u0438\u0441\u043a:")) {
            this.auctionBotLogic((Container)container, chestScreen);
        }
    }

    public void auctionBotLogic(Container container, ChestScreen chestScreen) {
        for (Slot slot : container.inventorySlots) {
            this.processAuctionSlot(chestScreen, slot);
        }
    }

    public void processAuctionSlot(ChestScreen chestScreen, Slot slot) {
        Object container = chestScreen.getContainer();
        if (this.parent.itemStorage == null) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        CopyOnWriteArrayList<IItem> items = this.parent.itemStorage.getItems();
        for (IItem item : items) {
            String sellerName;
            boolean itemIsFound;
            int targetPrice = item.getPrice();
            int currentPrice = this.extractPriceFromStack(slot.getStack());
            boolean bl = itemIsFound = currentPrice != -1 && currentPrice <= targetPrice && this.isItemWasFound(item, slot);
            if (this.parent.itemList.contains(slot.getStack()) || slot.slotNumber > 48 || !itemIsFound) continue;
            this.refreshStopWatch.reset();
            if (!this.checkItem(item, slot.getStack()) || (sellerName = this.extractPidorFromStack(slot.getStack())).isEmpty()) continue;
            this.buyItem((Container)container, slot, currentTime);
        }
        if (this.refreshStopWatch.isReached(500L)) {
            this.refreshAuction((Container)container, currentTime);
            this.refreshStopWatch.reset();
        }
    }

    protected void refreshAuction(Container container, long currentTime) {
        if (this.refreshStopWatch.isReached(500L)) {
            this.silentClick(container, 49, ClickType.QUICK_MOVE);
            this.lastClickTime = currentTime;
            this.refreshStopWatch.reset();
        }
    }

    protected void buyItem(Container container, Slot slot, long currentTime) {
        if (currentTime - this.lastClickTime > 1000L) {
            this.mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, this.mc.player);
            this.parent.itemList.add(slot.getStack());
            this.lastClickTime = currentTime;
        }
    }

    protected void returnToAuction() {
        if (this.returnAuctionStopWatch.isReached(350L)) {
            this.mc.player.closeScreen();
            this.mc.player.sendChatMessage("/ah");
            this.returnAuctionStopWatch.reset();
        }
    }

    protected boolean checkItem(IItem item, ItemStack stack) {
        boolean don;
        boolean bl = don = stack.getTag() != null && stack.getTag().contains("don-item");
        if (stack.getCount() < item.getQuantity()) {
            return false;
        }
        if (!item.getEnchantments().isEmpty()) {
            for (Map.Entry<Enchantment, Integer> enchantmentEntry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
                Enchantment enchantment = enchantmentEntry.getKey();
                Integer enchantmentValue = enchantmentEntry.getValue();
                if (enchantmentValue == null || item.getEnchantments().get(enchantment) == null) {
                    return false;
                }
                if (item.getEnchantments().get(enchantment) > enchantmentValue) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    private void silentClick(Container container, int slot, ClickType clickType) {
        short short1 = container.getNextTransactionID(this.mc.player.inventory);
        ItemStack itemstack = this.mc.player.inventory.getStackInSlot(slot);
        this.mc.player.connection.sendPacket(new CClickWindowPacket(this.mc.player.openContainer.windowId, slot, 0, clickType, itemstack, short1));
    }

    private boolean isItemWasFound(IItem item, Slot slot) {
        return item.getItem() == slot.getStack().getItem();
    }

    protected int extractPriceFromStack(ItemStack stack) {
        CompoundNBT display;
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10) && (display = tag.getCompound("display")).contains("Lore", 9)) {
            ListNBT lore = display.getList("Lore", 8);
            for (int j = 0; j < lore.size(); ++j) {
                JsonObject title;
                JsonArray array;
                JsonObject object = JsonParser.parseString(lore.getString(j)).getAsJsonObject();
                if (!object.has("extra") || (array = object.getAsJsonArray("extra")).size() <= 2 || !(title = array.get(1).getAsJsonObject()).get("text").getAsString().trim().toLowerCase().contains("\u0446\u0435\u043da")) continue;
                String line = array.get(2).getAsJsonObject().get("text").getAsString().trim().substring(1).replaceAll(" ", "");
                return Integer.parseInt(line);
            }
        }
        return -1;
    }

    protected String extractPidorFromStack(ItemStack stack) {
        CompoundNBT display;
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10) && (display = tag.getCompound("display")).contains("Lore", 9)) {
            ListNBT lore = display.getList("Lore", 8);
            for (int j = 0; j < lore.size(); ++j) {
                JsonObject title;
                JsonArray array;
                JsonObject object = JsonParser.parseString(lore.getString(j)).getAsJsonObject();
                if (!object.has("extra") || (array = object.getAsJsonArray("extra")).size() <= 2 || !(title = array.get(1).getAsJsonObject()).get("text").getAsString().trim().toLowerCase().startsWith("\u043f\u0440o\u0434a\u0432e\u0446")) continue;
                return array.get(2).getAsJsonObject().get("text").getAsString().trim().replaceAll(" ", "");
            }
        }
        return "";
    }
}

