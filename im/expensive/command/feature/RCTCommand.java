/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.MultiNamedCommand;
import im.expensive.command.interfaces.Parameters;
import im.expensive.utils.client.ClientUtil;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;

public class RCTCommand
implements Command,
MultiNamedCommand {
    private final Logger logger;
    private final Minecraft mc;

    @Override
    public void execute(Parameters parameters) {
        if (ClientUtil.isConnectedToServer("funtime")) {
            String anca = "";
            for (ScoreObjective team : this.mc.world.getScoreboard().getScoreObjectives()) {
                String an = team.getDisplayName().getString();
                if (!an.contains("\u0410\u043d\u0430\u0440\u0445\u0438\u044f-")) continue;
                anca = an.split("\u0410\u043d\u0430\u0440\u0445\u0438\u044f-")[1];
                this.mc.player.sendChatMessage("/hub");
                break;
            }
            this.mc.player.sendChatMessage("/an" + anca);
            String finalAnca = anca;
            new Thread(() -> {
                try {
                    Thread.sleep(900L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.mc.player.sendChatMessage("/an" + finalAnca);
            }).start();
        } else {
            this.logger.log(this.name() + " \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u043d\u0430 FunTime!");
        }
    }

    @Override
    public String name() {
        return "rct";
    }

    @Override
    public String description() {
        return "\u041f\u0435\u0440\u0435\u0437\u0430\u0445\u043e\u0434\u0438\u0442 \u043d\u0430 \u0430\u043d\u0430\u0440\u0445\u0438\u044e";
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("reconnect");
    }

    public RCTCommand(Logger logger, Minecraft mc) {
        this.logger = logger;
        this.mc = mc;
    }
}

