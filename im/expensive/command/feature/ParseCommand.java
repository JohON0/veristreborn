/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.Parameters;
import im.expensive.command.interfaces.Prefix;
import im.expensive.utils.client.IMinecraft;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.text.TextFormatting;

public class ParseCommand
implements Command,
IMinecraft {
    private final Prefix prefix;
    private final Logger logger;

    @Override
    public void execute(Parameters parameters) {
        this.savePlayerData();
    }

    @Override
    public String name() {
        return "parse";
    }

    @Override
    public String description() {
        return "\u041f\u0430\u0440\u0441\u0438\u0442 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0438\u0437 \u0442\u0430\u0431\u0430. (\u0412 \u043f\u0430\u043f\u043a\u0443 'saves/files/parse')";
    }

    public List<String> adviceMessage() {
        String commandPrefix = this.prefix.get();
        return List.of((Object)(commandPrefix + this.name() + " - \u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u043d\u0438\u043a\u0438 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0438\u0437 \u0442\u0430\u0431\u0430 \u0432 \u0444\u0430\u0439\u043b"));
    }

    private void savePlayerData() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        String formattedDateTime = now.format(formatter);
        File directory = new File("saves/files/parse");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String getServerIP = ParseCommand.mc.getCurrentServerData().serverIP;
        String fileName = (mc.isSingleplayer() ? "local" : getServerIP) + "_" + formattedDateTime;
        File file = new File(directory, fileName);
        LinkedHashMap prefixToPlayers = new LinkedHashMap();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));){
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayNetHandler networkHandler = minecraft.getConnection();
            if (networkHandler != null) {
                networkHandler.getPlayerInfoMap().forEach(playerInfo -> {
                    String playerName = playerInfo.getGameProfile().getName();
                    String playerPrefix = this.getPlayerPrefix(playerName);
                    prefixToPlayers.computeIfAbsent(playerPrefix, k -> new ArrayList()).add(playerName);
                });
                for (Map.Entry entry : prefixToPlayers.entrySet()) {
                    String prefix = (String)entry.getKey();
                    List players = (List)entry.getValue();
                    if (!prefix.isEmpty()) {
                        writer.write(prefix);
                        writer.newLine();
                    }
                    for (String player : players) {
                        writer.write("  " + player);
                        writer.newLine();
                    }
                    writer.newLine();
                }
            }
            String relativePath = "saves/files/parse/" + fileName + ".txt";
            this.logger.log(TextFormatting.GREEN + "\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0438\u0433\u0440\u043e\u043a\u0430\u0445 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0430 \u0432 " + relativePath);
        } catch (IOException e) {
            this.logger.log(TextFormatting.RED + "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0438 \u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u0438 \u043e \u0438\u0433\u0440\u043e\u043a\u0430\u0445: " + e.getMessage());
        }
    }

    private String getPlayerPrefix(String playerName) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayNetHandler networkHandler = minecraft.getConnection();
        if (networkHandler != null) {
            for (ScorePlayerTeam team : minecraft.world.getScoreboard().getTeams()) {
                if (!team.getMembershipCollection().contains(playerName)) continue;
                return team.getPrefix().getString();
            }
        }
        return "";
    }

    public ParseCommand(Prefix prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }
}

