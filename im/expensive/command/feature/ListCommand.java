/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.MultiNamedCommand;
import im.expensive.command.interfaces.Parameters;
import java.util.List;
import net.minecraft.util.text.TextFormatting;

public class ListCommand
implements Command,
MultiNamedCommand {
    private final List<Command> commands;
    private final Logger logger;

    @Override
    public void execute(Parameters parameters) {
        this.logger.log(TextFormatting.RED + "advice" + TextFormatting.WHITE + " | " + TextFormatting.GRAY + "\u041f\u043e\u043c\u043e\u0433\u0430\u0435\u0442 \u0443\u0437\u043d\u0430\u0442\u044c \u043a\u0430\u043a \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043a\u043e\u043c\u0430\u043d\u0434\u0443");
        for (Command command : this.commands) {
            if (command == this) continue;
            String alias = "";
            this.logger.log(TextFormatting.RED + command.name() + TextFormatting.WHITE + " | " + TextFormatting.GRAY + command.description());
        }
    }

    @Override
    public String name() {
        return "list";
    }

    @Override
    public String description() {
        return "\u0412\u044b\u0434\u0430\u0435\u0442 \u0441\u043f\u0438\u0441\u043e\u043a \u0432\u0441\u0435\u0445 \u043a\u043e\u043c\u0430\u043d\u0434";
    }

    @Override
    public List<String> aliases() {
        return List.of((Object)"", (Object)"help");
    }

    public ListCommand(List<Command> commands, Logger logger) {
        this.commands = commands;
        this.logger = logger;
    }
}

