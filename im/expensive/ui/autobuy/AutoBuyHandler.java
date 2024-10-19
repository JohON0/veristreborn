/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.autobuy;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import im.expensive.Expensive;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.ui.autobuy.AutoBuy;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.ShulkerUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
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
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.server.SChatPacket;

public class AutoBuyHandler
implements IMinecraft {
    public static boolean process;
    public CopyOnWriteArrayList<AutoBuy> items = new CopyOnWriteArrayList();
    public List<ItemStack> buyedList = new LinkedList<ItemStack>();
    StopWatch leaveUpdate = new StopWatch();
    StopWatch refreshStopWatch = new StopWatch();
    StopWatch buyStopWatch = new StopWatch();
    int lastBalance;
    public static AutoBuyHandler instance;
    boolean isReset = false;

    public AutoBuyHandler() {
        Expensive.getInstance().getEventBus().register(this);
        instance = this;
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        if (process) {
            if (AutoBuyHandler.mc.player == null || AutoBuyHandler.mc.world == null) {
                return;
            }
            IPacket<?> iPacket = e.getPacket();
            if (iPacket instanceof SChatPacket) {
                SChatPacket p = (SChatPacket)iPacket;
                String raw = p.getChatComponent().getString().toLowerCase(Locale.ROOT);
                if (raw.contains("\u044d\u0442\u043e\u0442 \u0442\u043e\u0432\u0430\u0440 \u0443\u0436\u0435 \u043a\u0443\u043f\u0438\u043b\u0438!") || raw.contains("\u0423 \u0412\u0430\u0441 \u043d\u0435 \u0445\u0432\u0430\u0442\u0430\u0435\u0442 \u0434\u0435\u043d\u0435\u0433!")) {
                    this.returnToAuction();
                }
                if (raw.contains("\u0432\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441:")) {
                    String[] parts = raw.split("\u0432\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441:")[1].trim().split("\\s");
                    try {
                        this.lastBalance = Integer.parseInt(parts[0].replaceAll("[^\\d]", ""));
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                    e.cancel();
                }
            }
        }
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        Screen screen;
        if (process && (screen = AutoBuyHandler.mc.currentScreen) instanceof ChestScreen) {
            ChestScreen chestScreen = (ChestScreen)screen;
            this.processBuy(chestScreen);
        }
    }

    private void processBuy(ChestScreen chestScreen) {
        Object container = chestScreen.getContainer();
        if (chestScreen.getTitle().getString().toLowerCase().contains("\u043f\u043e\u0434\u043e\u0437\u0440\u0438\u0442\u0435\u043b\u044c\u043d\u0430\u044f \u0446\u0435\u043d\u0430!")) {
            AutoBuyHandler.mc.playerController.windowClick(((Container)container).windowId, 10, 0, ClickType.PICKUP, AutoBuyHandler.mc.player);
        }
        if (chestScreen.getTitle().getString().contains("\u0410\u0443\u043a\u0446\u0438\u043e\u043d") || chestScreen.getTitle().getString().contains("\u041f\u043e\u0438\u0441\u043a:")) {
            this.auctionBotLogic((Container)container, chestScreen);
        }
    }

    private void auctionBotLogic(Container container, ChestScreen chestScreen) {
        this.refreshAuction(container);
        for (Slot slot : container.inventorySlots) {
            this.processAuctionSlot(chestScreen, slot);
        }
    }

    private void processAuctionSlot(ChestScreen chestScreen, Slot slot) {
        Object container = chestScreen.getContainer();
        for (AutoBuy item : this.items) {
            boolean itemIsFound;
            int targetPrice = item.getPrice();
            int currentPrice = this.extractPriceFromStack(slot.getStack());
            boolean bl = itemIsFound = currentPrice != -1 && currentPrice <= targetPrice && this.isItemWasFound(item, slot);
            if (this.buyedList.contains(slot.getStack()) || slot.slotNumber > 48) continue;
            if (itemIsFound) {
                String sellerName;
                if (!this.checkItem(item, slot.getStack()) || (sellerName = this.extractPidorFromStack(slot.getStack())).isEmpty()) continue;
                if (!chestScreen.getTitle().getString().contains(sellerName)) {
                    this.leaveAuction(sellerName);
                    return;
                }
                if (!this.buyStopWatch.isReached(100L)) continue;
                this.buyItem((Container)container, slot);
                continue;
            }
            if (!chestScreen.getTitle().getString().contains("[1/1]")) continue;
            this.returnToAuction();
        }
    }

    private void refreshAuction(Container container) {
        if (this.refreshStopWatch.isReached(400L)) {
            this.silentClick(container, 49, ClickType.QUICK_MOVE);
            this.refreshStopWatch.reset();
        }
    }

    private void buyItem(Container container, Slot slot) {
        AutoBuyHandler.mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, AutoBuyHandler.mc.player);
        this.buyedList.add(slot.getStack());
        this.refreshStopWatch.reset();
        this.buyStopWatch.reset();
    }

    private void leaveAuction(String sellerName) {
        if (this.leaveUpdate.isReached(400L)) {
            AutoBuyHandler.mc.player.closeScreen();
            AutoBuyHandler.mc.player.sendChatMessage("/ah " + sellerName);
            this.leaveUpdate.reset();
        }
    }

    private void returnToAuction() {
        if (this.leaveUpdate.isReached(350L)) {
            AutoBuyHandler.mc.player.closeScreen();
            AutoBuyHandler.mc.player.sendChatMessage("/ah");
            this.leaveUpdate.reset();
        }
    }

    private boolean checkItem(AutoBuy autoBuy, ItemStack stack) {
        boolean don;
        boolean bl = don = stack.getTag() != null && stack.getTag().contains("don-item");
        if ((autoBuy.isItems() || autoBuy.isDon()) && Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock && ShulkerUtil.getItemInShulker(stack, autoBuy).isEmpty()) {
            return false;
        }
        if (stack.getCount() < autoBuy.getCount()) {
            return false;
        }
        if (autoBuy.isFake() && !don) {
            return false;
        }
        if (!autoBuy.getEnchanments().isEmpty()) {
            for (Map.Entry<Enchantment, Integer> enchantmentEntry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
                Enchantment enchantment = enchantmentEntry.getKey();
                Integer enchantmentValue = enchantmentEntry.getValue();
                if (enchantmentValue == null || autoBuy.getEnchanments().get(enchantment) == null) {
                    return false;
                }
                if (autoBuy.getEnchanments().get(enchantment) > enchantmentValue) continue;
                this.print(enchantmentValue + " " + autoBuy.getEnchanments().get(enchantment));
                return true;
            }
            return false;
        }
        return true;
    }

    private void silentClick(Container container, int slot, ClickType clickType) {
        short short1 = container.getNextTransactionID(AutoBuyHandler.mc.player.inventory);
        ItemStack itemstack = AutoBuyHandler.mc.player.inventory.getStackInSlot(slot);
        AutoBuyHandler.mc.player.connection.sendPacket(new CClickWindowPacket(AutoBuyHandler.mc.player.openContainer.windowId, slot, 0, clickType, itemstack, short1));
    }

    private boolean isItemWasFound(AutoBuy autoBuy, Slot slot) {
        return autoBuy.getItem() == slot.getStack().getItem();
    }

    private int extractPriceFromStack(ItemStack stack) {
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

    private String extractPidorFromStack(ItemStack stack) {
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

