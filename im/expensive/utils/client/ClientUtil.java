/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.client;

import im.expensive.Expensive;
import im.expensive.ui.mainmenu.AltScreen;
import im.expensive.ui.mainmenu.MainScreen;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.client.Vec2i;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.UUID;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public final class ClientUtil
implements IMinecraft {
    private static Clip currentClip = null;
    private static boolean pvpMode;
    private static UUID uuid;
    public static String state;
    public static String alt;

    public static String getUsername() {
        return System.getProperty("user.name");
    }

    public static String getGreetingMessage() {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isBefore(LocalTime.of(6, 0))) {
            return "\u0414\u043e\u0431\u0440\u043e\u0439 \u043d\u043e\u0447\u0438";
        }
        if (currentTime.isBefore(LocalTime.NOON)) {
            return "\u0414\u043e\u0431\u0440\u043e\u0435 \u0443\u0442\u0440\u043e";
        }
        if (currentTime.isBefore(LocalTime.of(18, 0))) {
            return "\u0414\u043e\u0431\u0440\u044b\u0439 \u0434\u0435\u043d\u044c";
        }
        return "\u0414\u043e\u0431\u0440\u044b\u0439 \u0432\u0435\u0447\u0435\u0440";
    }

//    public static void startRPC() {
//        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
//        discordRPC.Discord_Initialize("1190380952640294962", eventHandlers, true, null);
//        ClientUtil.discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
//        ClientUtil.discordRichPresence.largeImageText = "\u0412\u0435\u0440\u0441\u0438\u044f: 1.7 | \u0411\u0438\u043b\u0434: " + Expensive.build;
//        discordRPC.Discord_UpdatePresence(discordRichPresence);
//        new Thread(() -> {
//            while (true) {
//                try {
//                    while (true) {
//                        state = ClientUtil.mc.currentScreen instanceof MainMenuScreen || ClientUtil.mc.currentScreen instanceof MainScreen ? "\u0412 \u0433\u043b\u0430\u0432\u043d\u043e\u043c \u043c\u0435\u043d\u044e" : (ClientUtil.mc.currentScreen instanceof MultiplayerScreen ? "\u0412\u044b\u0431\u0438\u0440\u0430\u0435\u0442 \u0441\u0435\u0440\u0432\u0435\u0440" : (mc.isSingleplayer() ? "\u0412 \u043e\u0434\u0438\u043d\u043e\u0447\u043d\u043e\u043c \u043c\u0438\u0440\u0435" : (mc.getCurrentServerData() != null ? "\u0418\u0433\u0440\u0430\u0435\u0442 \u043d\u0430 " + ClientUtil.mc.getCurrentServerData().serverIP.replace("mc.", "").replace("play.", "").replace("gg.", "").replace("go.", "").replace("join.", "").replace("creative.", "").replace(".top", "").replace(".ru", "").replace(".cc", "").replace(".space", "").replace(".eu", "").replace(".com", "").replace(".net", "").replace(".xyz", "").replace(".gg", "").replace(".me", "").replace(".su", "").replace(".fun", "").replace(".org", "").replace(".host", "").replace("localhost", "LocalServer").replace(":25565", "") : (ClientUtil.mc.currentScreen instanceof OptionsScreen ? "\u0412 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430\u0445" : (ClientUtil.mc.currentScreen instanceof WorldSelectionScreen ? "\u0412\u044b\u0431\u0438\u0440\u0430\u0435\u0442 \u043c\u0438\u0440" : (ClientUtil.mc.currentScreen instanceof AltScreen ? "\u0412 \u043c\u0435\u043d\u044e \u0432\u044b\u0431\u043e\u0440\u0430 \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u043e\u0432" : "\u0417\u0430\u0433\u0440\u0443\u0437\u043a\u0430..."))))));
//                        ClientUtil.discordRichPresence.largeImageKey = "Verist";
//                        ClientUtil.discordRichPresence.details = state;
//                        ClientUtil.discordRichPresence.state = "\u041c\u043e\u0434\u044b: " + Expensive.getInstance().getModuleManager().countEnabledModules() + "/" + Expensive.getInstance().getModuleManager().getModules().size();
//                        discordRPC.Discord_UpdatePresence(discordRichPresence);
//                        Thread.sleep(2000L);
//                    }
//                } catch (InterruptedException interruptedException) {
//                    continue;
//                }
//                break;
//            }
//        }).start();
//    }
//
//    public static void stopRPC() {
//        discordRPC.Discord_Shutdown();
//        discordRPC.Discord_ClearPresence();
//    }

    public static void updateBossInfo(SUpdateBossInfoPacket packet) {
        if (packet.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
            if (StringUtils.stripControlCodes(packet.getName().getString()).toLowerCase().contains("pvp")) {
                pvpMode = true;
                uuid = packet.getUniqueId();
            }
        } else if (packet.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE && packet.getUniqueId().equals(uuid)) {
            pvpMode = false;
        }
    }

    public static boolean isConnectedToServer(String ip) {
        return mc.getCurrentServerData() != null && ClientUtil.mc.getCurrentServerData().serverIP != null && ClientUtil.mc.getCurrentServerData().serverIP.contains(ip);
    }

    public static boolean isPvP() {
        return pvpMode;
    }

    public static void playSound(String sound, float value, boolean nonstop) {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
        }
        try {
            currentClip = AudioSystem.getClip();
            InputStream is = mc.getResourceManager().getResource(new ResourceLocation("eva/sounds/" + sound + ".wav")).getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis);
            if (audioInputStream == null) {
                System.out.println("Sound not found!");
                return;
            }
            currentClip.open(audioInputStream);
            currentClip.start();
            FloatControl floatControl = (FloatControl)currentClip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = floatControl.getMinimum();
            float max = floatControl.getMaximum();
            float volumeInDecibels = (float)((double)min * (1.0 - (double)value / 100.0) + (double)max * ((double)value / 100.0));
            floatControl.setValue(volumeInDecibels);
            if (nonstop) {
                currentClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        currentClip.setFramePosition(0);
                        currentClip.start();
                    }
                });
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void stopSound() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }

    public static int calc(int value) {
        MainWindow rs = mc.getMainWindow();
        return (int)((double)value * rs.getGuiScaleFactor() / 2.0);
    }

    public static Vec2i getMouse(int mouseX, int mouseY) {
        return new Vec2i((int)((double)mouseX * Minecraft.getInstance().getMainWindow().getGuiScaleFactor() / 2.0), (int)((double)mouseY * Minecraft.getInstance().getMainWindow().getGuiScaleFactor() / 2.0));
    }

    private ClientUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        state = "";
        alt = "";
//        discordRichPresence = new DiscordRichPresence();
//        discordRPC = DiscordRPC.INSTANCE;
    }
}

