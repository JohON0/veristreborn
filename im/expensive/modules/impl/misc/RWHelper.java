/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventPacket;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.utils.math.StopWatch;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.util.UUID;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.text.TextFormatting;

@ModuleRegister(name="RWHelper", category=Category.Misc)
public class RWHelper
extends Module {
    boolean joined;
    StopWatch stopWatch = new StopWatch();
    private final ModeListSetting s = new ModeListSetting("\u0424\u0443\u043d\u043a\u0446\u0438\u0438", new BooleanSetting("\u0411\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0437\u0430\u043f\u0440\u0435\u0442\u043a\u0438", true), new BooleanSetting("\u0417\u0430\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u043c\u0435\u043d\u044e", true), new BooleanSetting("\u0410\u0432\u0442\u043e \u0442\u043e\u0447\u043a\u0430", true), new BooleanSetting("\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f", true));
    private UUID uuid;
    int x = -1;
    int z = -1;
    private TrayIcon trayIcon;
    String[] banWords = new String[]{"\u044d\u043a\u0441\u043f\u0430", "\u044d\u043a\u0441\u043f\u0435\u043d\u0441\u0438\u0432", "\u044d\u043a\u0441\u043f\u043e\u0439", "\u043d\u0443\u0440\u0438\u043a\u043e\u043c", "\u0446\u0435\u043b\u043a\u043e\u0439", "\u043d\u0443\u0440\u043b\u0430\u043d", "\u043d\u0443\u0440\u0441\u0443\u043b\u0442\u0430\u043d", "\u0446\u0435\u043b\u0435\u0441\u0442\u0438\u0430\u043b", "\u0446\u0435\u043b\u043a\u0430", "\u043d\u0443\u0440\u0438\u043a", "\u0430\u0442\u0435\u0440\u043d\u043e\u0441", "expa", "celka", "nurik", "expensive", "celestial", "nursultan", "\u0444\u0430\u043d\u043f\u0435\u0439", "funpay", "fluger", "\u0430\u043a\u0440\u0438\u0435\u043d", "akrien", "\u0444\u0430\u043d\u0442\u0430\u0439\u043c", "ft", "funtime", "\u0431\u0435\u0437\u043c\u0430\u043c\u043d\u044b\u0439", "rich", "\u0440\u0438\u0447", "\u0431\u0435\u0437 \u043c\u0430\u043c\u043d\u044b\u0439", "wild", "\u0432\u0438\u043b\u0434", "excellent", "\u044d\u043a\u0441\u0435\u043b\u043b\u0435\u043d\u0442", "hvh", "\u0445\u0432\u0445", "matix", "impact", "\u043c\u0430\u0442\u0438\u043a\u0441", "\u0438\u043c\u043f\u0430\u043a\u0442", "wurst", "wexisde", "wex", "\u0432\u0435\u043a\u0441", "\u0432\u0435\u043a\u0441\u0430\u0439\u0434"};

    public RWHelper() {
        this.addSettings(this.s);
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        IPacket<IServerPlayNetHandler> p;
        IPacket<?> iPacket;
        if (e.isSend() && (iPacket = e.getPacket()) instanceof CChatMessagePacket) {
            p = (CChatMessagePacket)iPacket;
            boolean contains = false;
            if (((Boolean)this.s.getValueByName("\u0411\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0437\u0430\u043f\u0440\u0435\u0442\u043a\u0438").get()).booleanValue()) {
                for (String str : this.banWords) {
                    if (!((CChatMessagePacket)p).getMessage().toLowerCase().contains(str)) continue;
                    contains = true;
                    break;
                }
                if (contains) {
                    this.print("RW Helper |" + TextFormatting.RED + " \u041e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u044b \u0437\u0430\u043f\u0440\u0435\u0449\u0435\u043d\u043d\u044b\u0435 \u0441\u043b\u043e\u0432\u0430 \u0432 \u0432\u0430\u0448\u0435\u043c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0438. \u041e\u0442\u043f\u0440\u0430\u0432\u043a\u0430 \u043e\u0442\u043c\u0435\u043d\u0435\u043d\u0430, \u0447\u0442\u043e\u0431\u044b \u0438\u0437\u0431\u0435\u0436\u0430\u0442\u044c \u0431\u0430\u043d\u0430 \u043d\u0430 ReallyWorld.");
                    e.cancel();
                }
            }
        }
        if (e.isReceive()) {
            iPacket = e.getPacket();
            if (iPacket instanceof SUpdateBossInfoPacket) {
                SUpdateBossInfoPacket packet = (SUpdateBossInfoPacket)iPacket;
                if (((Boolean)this.s.getValueByName("\u0410\u0432\u0442\u043e \u0442\u043e\u0447\u043a\u0430").get()).booleanValue()) {
                    this.updateBossInfo(packet);
                }
            }
            if (((Boolean)this.s.getValueByName("\u0417\u0430\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u043c\u0435\u043d\u044e").get()).booleanValue()) {
                SOpenWindowPacket w;
                iPacket = e.getPacket();
                if (iPacket instanceof SRespawnPacket) {
                    p = (SRespawnPacket)iPacket;
                    this.joined = true;
                    this.stopWatch.reset();
                }
                if ((iPacket = e.getPacket()) instanceof SOpenWindowPacket && (w = (SOpenWindowPacket)iPacket).getTitle().getString().contains("\u041c\u0435\u043d\u044e") && this.joined && !this.stopWatch.isReached(2000L)) {
                    RWHelper.mc.player.closeScreen();
                    e.cancel();
                    this.joined = false;
                }
            }
        }
    }

    public void updateBossInfo(SUpdateBossInfoPacket packet) {
        if (packet.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
            String name = packet.getName().getString().toLowerCase().replaceAll("\\s+", " ");
            if (name.contains("\u0430\u0438\u0440\u0434\u0440\u043e\u043f")) {
                this.parseAirDrop(name);
                this.uuid = packet.getUniqueId();
            } else if (name.contains("\u0442\u0430\u043b\u0438\u0441\u043c\u0430\u043d")) {
                this.parseMascot(name);
                this.uuid = packet.getUniqueId();
            } else if (name.contains("\u0441\u043a\u0440\u0443\u0434\u0436")) {
                this.parseScrooge(name);
                this.uuid = packet.getUniqueId();
            }
        } else if (packet.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE && packet.getUniqueId().equals(this.uuid)) {
            this.resetCoordinatesAndRemoveWaypoints();
        }
    }

    private void parseAirDrop(String name) {
        this.x = RWHelper.extractCoordinate(name, "x: ");
        this.z = RWHelper.extractCoordinate(name, "z: ");
        if (((Boolean)this.s.getValueByName("\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u0435").get()).booleanValue()) {
            this.windows("RWHelper", "\u041f\u043e\u044f\u0432\u0438\u043b\u0441\u044f \u0430\u0438\u0440\u0434\u0440\u043e\u043f!", false);
        }
        RWHelper.mc.player.sendChatMessage(".way add \u0410\u0438\u0440\u0414\u0440\u043e\u043f " + this.x + " 100 " + this.z);
    }

    private void parseMascot(String name) {
        String[] words = name.split("\\s+");
        for (int i = 0; i < words.length; ++i) {
            if (!RWHelper.isInteger(words[i]) || i + 1 >= words.length || !RWHelper.isInteger(words[i + 1])) continue;
            this.x = Integer.parseInt(words[i]);
            this.z = Integer.parseInt(words[i + 1]);
            if (((Boolean)this.s.getValueByName("\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u0435").get()).booleanValue()) {
                this.windows("RWHelper", "\u041f\u043e\u044f\u0432\u0438\u043b\u0441\u044f \u0442\u0430\u043b\u0438\u0441\u043c\u0430\u043d!", false);
            }
            RWHelper.mc.player.sendChatMessage(".gps add \u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d " + this.x + " 100 " + this.z);
        }
    }

    private void parseScrooge(String name) {
        int startIndex = name.indexOf("\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b");
        if (startIndex == -1) {
            return;
        }
        String coordinatesSubstring = name.substring(startIndex + "\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b".length()).trim();
        String[] words = coordinatesSubstring.split("\\s+");
        if (words.length >= 2) {
            this.x = Integer.parseInt(words[0]);
            this.z = Integer.parseInt(words[1]);
            if (((Boolean)this.s.getValueByName("\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u0435").get()).booleanValue()) {
                this.windows("RWHelper", "\u041f\u043e\u044f\u0432\u0438\u043b\u0441\u044f \u0441\u043a\u0440\u0443\u0434\u0436!", false);
            }
            RWHelper.mc.player.sendChatMessage(".gps add \u0421\u043a\u0440\u0443\u0434\u0436 " + this.x + " 100 " + this.z);
        }
    }

    private void resetCoordinatesAndRemoveWaypoints() {
        this.x = 0;
        this.z = 0;
        RWHelper.mc.player.sendChatMessage(".gps remove \u0410\u0438\u0440\u0414\u0440\u043e\u043f");
        RWHelper.mc.player.sendChatMessage(".gps remove \u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d");
        RWHelper.mc.player.sendChatMessage(".gps remove \u0421\u043a\u0440\u0443\u0434\u0436");
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int extractCoordinate(String text, String coordinateIdentifier) {
        int coordinateStartIndex = text.indexOf(coordinateIdentifier);
        if (coordinateStartIndex != -1) {
            int coordinateValueStart = coordinateStartIndex + coordinateIdentifier.length();
            int coordinateValueEnd = text.indexOf(" ", coordinateValueStart);
            if (coordinateValueEnd == -1) {
                coordinateValueEnd = text.length();
            }
            String coordinateValueString = text.substring(coordinateValueStart, coordinateValueEnd);
            return Integer.parseInt(coordinateValueString.trim());
        }
        return 0;
    }

    private void windows(String name, String desc, boolean error) {
        this.print(desc);
        if (SystemTray.isSupported()) {
            try {
                if (this.trayIcon == null) {
                    SystemTray systemTray = SystemTray.getSystemTray();
                    Image image = Toolkit.getDefaultToolkit().createImage("");
                    this.trayIcon = new TrayIcon(image, "Baritone");
                    this.trayIcon.setImageAutoSize(true);
                    this.trayIcon.setToolTip(name);
                    systemTray.add(this.trayIcon);
                }
                this.trayIcon.displayMessage(name, desc, error ? TrayIcon.MessageType.ERROR : TrayIcon.MessageType.INFO);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}

