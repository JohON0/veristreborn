/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import com.mojang.blaze3d.platform.PlatformDescriptors;
import im.expensive.Expensive;
import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.Parameters;
import im.expensive.ui.mainmenu.Alt;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.discord.DiscordWebHook;
import java.awt.Color;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public class ReportCommand
implements Command {
    private final Logger logger;

    @Override
    public void execute(Parameters parameters) {
        String message = parameters.collectMessage(0).trim();
        if (!message.isEmpty()) {
            this.sendReport(message);
        } else {
            this.sendError();
        }
    }

    private void sendReport(String message) {
        DiscordWebHook webhook = new DiscordWebHook("sosi");
        DiscordWebHook.EmbedObject embedObject = ReportCommand.getEmbedObject(message);
        webhook.addEmbed(embedObject);
        try {
            webhook.execute();
            this.logger.log(TextFormatting.BLUE + "\u0420\u0435\u043f\u043e\u0440\u0442 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d");
        } catch (Exception e) {
            this.logger.log(TextFormatting.RED + "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043e\u0442\u043f\u0440\u0430\u0432\u043a\u0435 \u0440\u0435\u043f\u043e\u0440\u0442\u0430: " + e.getMessage());
        }
    }

    public static DiscordWebHook.EmbedObject getEmbedObject(String message) throws IOException {
        DiscordWebHook.EmbedObject embedObject = new DiscordWebHook.EmbedObject();
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        embedObject.addField("report message", message, true);
        embedObject.addField("minecraft session", Minecraft.getInstance().getSession().getUsername(), true);
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        String username = System.getProperty("user.name");
        embedObject.addField("user name", username, true);
        embedObject.addField("os name", osBean.getName(), true);
        embedObject.addField("hwid", ReportCommand.getHWID(), true);
        embedObject.addField("cpu", PlatformDescriptors.getCpuInfo(), true);
        embedObject.addField("gpu", PlatformDescriptors.getGlRenderer(), true);
        embedObject.addField("time", formattedTime, true);
        embedObject.addField("client version", "1.7", true);
        StringBuilder altsString = new StringBuilder();
        for (int i = 0; i < Expensive.getInstance().getAltScreen().alts.size(); ++i) {
            Alt alt = Expensive.getInstance().getAltScreen().alts.get(i);
            altsString.append(alt.name);
            if (i >= Expensive.getInstance().getAltScreen().alts.size() - 1) continue;
            altsString.append(", ");
        }
        String result = altsString.toString();
        embedObject.addField("accounts:", result, false);
        embedObject.setColor(new Color(Theme.rectColor));
        return embedObject;
    }

    public static String getHWID() throws IOException {
        String command = "wmic csproduct get uuid";
        Process process = Runtime.getRuntime().exec(command);
        Scanner scanner = new Scanner(process.getInputStream());
        StringBuilder output = new StringBuilder();
        while (scanner.hasNext()) {
            output.append(scanner.next());
        }
        String uuid = output.toString().trim();
        if (uuid.contains("UUID")) {
            uuid = uuid.replace("UUID", "").trim();
        }
        return uuid;
    }

    private void sendError() {
        this.logger.log(TextFormatting.RED + "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0438:");
        this.logger.log(TextFormatting.GRAY + "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 .report <text>");
        this.logger.log(TextFormatting.GREEN + "\u041f\u0440\u0438\u043c\u0435\u0440: .report HitAura \u043d\u0435 \u0431\u044c\u0435\u0442 \u0441\u043b\u0430\u0439\u043c\u043e\u0432");
    }

    @Override
    public String name() {
        return "report";
    }

    @Override
    public String description() {
        return "\u041e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0435\u0442 \u0431\u0430\u0433\u0438 \u0432 \u043a\u043b\u0438\u0435\u043d\u0442\u0435.";
    }

    public ReportCommand(Logger logger) {
        this.logger = logger;
    }
}

