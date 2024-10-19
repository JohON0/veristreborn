/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.api;

import im.expensive.command.api.AdviceCommand;
import im.expensive.command.interfaces.AdviceCommandFactory;
import im.expensive.command.interfaces.CommandProvider;
import im.expensive.command.interfaces.Logger;

public class AdviceCommandFactoryImpl
implements AdviceCommandFactory {
    private final Logger logger;

    @Override
    public AdviceCommand adviceCommand(CommandProvider commandProvider) {
        return new AdviceCommand(commandProvider, this.logger);
    }

    public AdviceCommandFactoryImpl(Logger logger) {
        this.logger = logger;
    }
}

