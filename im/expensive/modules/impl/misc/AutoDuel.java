/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.utils.math.StopWatch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;

@ModuleRegister(name="AutoDuel", category=Category.Misc)
public class AutoDuel
extends Module {
    private static final Pattern pattern = Pattern.compile("^\\w{3,16}$");
    private final ModeSetting mode = new ModeSetting("Mode", "\u0428\u0430\u0440\u044b", "\u0428\u0430\u0440\u044b", "\u0429\u0438\u0442", "\u0428\u0438\u043f\u044b 3", "\u041d\u0435\u0437\u0435\u0440\u0438\u0442\u043a\u0430", "\u0427\u0438\u0442\u0435\u0440\u0441\u043a\u0438\u0439 \u0440\u0430\u0439", "\u041b\u0443\u043a", "\u041a\u043b\u0430\u0441\u0441\u0438\u043a", "\u0422\u043e\u0442\u0435\u043c\u044b", "\u041d\u043e\u0434\u0435\u0431\u0430\u0444\u0444");
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private final List<String> sent = Lists.newArrayList();
    private final StopWatch counter = new StopWatch();
    private final StopWatch counter2 = new StopWatch();
    private final StopWatch counterChoice = new StopWatch();
    private final StopWatch counterTo = new StopWatch();

    public AutoDuel() {
        this.addSettings(this.mode);
    }

    /*
     * WARNING - void declaration
     */
    @Subscribe
    private void onUpdt(EventUpdate e) {
        List<String> players = this.getOnlinePlayers();
        double distance = Math.sqrt(Math.pow(this.lastPosX - AutoDuel.mc.player.getPosX(), 2.0) + Math.pow(this.lastPosY - AutoDuel.mc.player.getPosY(), 2.0) + Math.pow(this.lastPosZ - AutoDuel.mc.player.getPosZ(), 2.0));
        if (distance > 500.0) {
            this.toggle();
        }
        this.lastPosX = AutoDuel.mc.player.getPosX();
        this.lastPosY = AutoDuel.mc.player.getPosY();
        this.lastPosZ = AutoDuel.mc.player.getPosZ();
        if (this.counter2.isReached(800L * (long)players.size())) {
            this.sent.clear();
            this.counter2.reset();
        }
        for (String string : players) {
            if (this.sent.contains(string) || string.equals(AutoDuel.mc.session.getProfile().getName()) || !this.counter.isReached(1000L)) continue;
            AutoDuel.mc.player.sendChatMessage("/duel " + string);
            this.sent.add(string);
            this.counter.reset();
        }
        Container container = AutoDuel.mc.player.openContainer;
        if (container instanceof ChestContainer) {
            ChestContainer chest = (ChestContainer)container;
            if (AutoDuel.mc.currentScreen.getTitle().getString().contains("\u0412\u044b\u0431\u043e\u0440 \u043d\u0430\u0431\u043e\u0440\u0430 (1/1)")) {
                boolean bl = false;
                int var6_8 = 0;
                while (var6_8 < chest.getLowerChestInventory().getSizeInventory()) {
                    ArrayList<Integer> slotsID = new ArrayList<Integer>();
                    int index = 0;
                    slotsID.add(index);
                    ++index;
                    Collections.shuffle(slotsID);
                    if (this.counterChoice.isReached(150L)) {
                        if (this.mode.is("\u0429\u0438\u0442")) {
                            AutoDuel.mc.playerController.windowClick(chest.windowId, 0, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        }
                        if (this.mode.is("\u0428\u0438\u043f\u044b 3")) {
                            AutoDuel.mc.playerController.windowClick(chest.windowId, 1, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        }
                        if (this.mode.is("\u041b\u0443\u043a")) {
                            AutoDuel.mc.playerController.windowClick(chest.windowId, 2, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        }
                        if (this.mode.is("\u0422\u043e\u0442\u0435\u043c\u044b")) {
                            AutoDuel.mc.playerController.windowClick(chest.windowId, 3, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        }
                        if (this.mode.is("\u041d\u043e\u0434\u0435\u0431\u0430\u0444\u0444")) {
                            AutoDuel.mc.playerController.windowClick(chest.windowId, 4, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        }
                        if (this.mode.is("\u0428\u0430\u0440\u044b")) {
                            AutoDuel.mc.playerController.windowClick(chest.windowId, 5, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        }
                        if (this.mode.is("\u041a\u043b\u0430\u0441\u0441\u0438\u043a")) {
                            AutoDuel.mc.playerController.windowClick(chest.windowId, 6, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        }
                        if (this.mode.is("\u0427\u0438\u0442\u0435\u0440\u0441\u043a\u0438\u0439 \u0440\u0430\u0439")) {
                            AutoDuel.mc.playerController.windowClick(chest.windowId, 7, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        }
                        if (this.mode.is("\u041d\u0435\u0437\u0435\u0440\u043a\u0430")) {
                            AutoDuel.mc.playerController.windowClick(chest.windowId, 8, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        }
                        this.counterChoice.reset();
                    }
                    ++var6_8;
                }
            } else if (AutoDuel.mc.currentScreen.getTitle().getString().contains("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u043f\u043e\u0435\u0434\u0438\u043d\u043a\u0430") && this.counterTo.isReached(150L)) {
                AutoDuel.mc.playerController.windowClick(chest.windowId, 0, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                this.counterTo.reset();
            }
        }
    }

    @Subscribe
    private void onPacket(EventPacket event) {
        SChatPacket chat;
        String text;
        IPacket<?> packet;
        if (event.isReceive() && (packet = event.getPacket()) instanceof SChatPacket && ((text = (chat = (SChatPacket)packet).getChatComponent().getString().toLowerCase()).contains("\u043d\u0430\u0447\u0430\u043b\u043e") && text.contains("\u0447\u0435\u0440\u0435\u0437") && text.contains("\u0441\u0435\u043a\u0443\u043d\u0434!") || text.equals("\u0434\u0443\u044d\u043b\u0438 \u00bb \u0432\u043e \u0432\u0440\u0435\u043c\u044f \u043f\u043e\u0435\u0434\u0438\u043d\u043a\u0430 \u0437\u0430\u043f\u0440\u0435\u0449\u0435\u043d\u043e \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043a\u043e\u043c\u0430\u043d\u0434\u044b"))) {
            this.toggle();
        }
    }

    private List<String> getOnlinePlayers() {
        return AutoDuel.mc.player.connection.getPlayerInfoMap().stream().map(NetworkPlayerInfo::getGameProfile).map(GameProfile::getName).filter(profileName -> pattern.matcher((CharSequence)profileName).matches()).collect(Collectors.toList());
    }
}

