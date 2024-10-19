/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import im.expensive.command.api.CommandException;
import im.expensive.command.api.PrefixImpl;
import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.CommandWithAdvice;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.Parameters;
import im.expensive.command.interfaces.Prefix;
import im.expensive.config.PrefixStorage;
import java.util.List;
import net.minecraft.util.text.TextFormatting;

public class PrefixCommand
implements Command,
CommandWithAdvice {
    private final Prefix prefix;
    private final Logger logger;

    @Override
    public void execute(Parameters parameters) {
        String commandType;
        switch (commandType = parameters.asString(0).orElse("")) {
            case "set": {
                this.setPrefix(parameters, this.logger);
            }
        }
    }

    public void setPrefix(Parameters parameters, Logger logger) {
        String prefixSymbol = parameters.asString(1).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043f\u0440\u0435\u0444\u0438\u043a\u0441!"));
        PrefixImpl prefixSet = new PrefixImpl();
        prefixSet.set(prefixSymbol);
        PrefixStorage.updatePrefix(prefixSymbol);
        logger.log(TextFormatting.GREEN + "\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u043f\u043e\u0441\u0442\u0430\u0432\u043b\u0435\u043d \u043f\u0440\u0435\u0444\u0438\u043a\u0441: " + TextFormatting.RED + prefixSymbol);
    }

    @Override
    public String name() {
        return "prefix";
    }

    @Override
    public String description() {
        return "\u0423\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u0435\u0442 \u043a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u043f\u0440\u0435\u0444\u0438\u043a\u0441 \u043a\u043e\u043c\u043c\u0430\u043d\u0434.";
    }

    @Override
    public List<String> adviceMessage() {
        String commandPrefix = this.prefix.get();
        return List.of((Object)(commandPrefix + "prefix set <prefix>"), (Object)("\u041f\u0440\u0438\u043c\u0435\u0440: " + TextFormatting.RED + commandPrefix + "prefix set @"));
    }

    public PrefixCommand(Prefix prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }
}

