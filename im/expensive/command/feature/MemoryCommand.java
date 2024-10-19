/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.MultiNamedCommand;
import im.expensive.command.interfaces.Parameters;
import java.util.List;

public class MemoryCommand
implements Command,
MultiNamedCommand {
    private final Logger logger;

    @Override
    public void execute(Parameters parameters) {
        System.gc();
        this.logger.log("\u041e\u0447\u0438\u0441\u0442\u0438\u043b \u043f\u0430\u043c\u044f\u0442\u044c.");
    }

    @Override
    public String name() {
        return "memory";
    }

    @Override
    public String description() {
        return "\u041e\u0447\u0438\u0449\u0430\u0435\u0442 \u043f\u0430\u043c\u044f\u0442\u044c";
    }

    @Override
    public List<String> aliases() {
        return List.of((Object)"gc");
    }

    public MemoryCommand(Logger logger) {
        this.logger = logger;
    }
}

