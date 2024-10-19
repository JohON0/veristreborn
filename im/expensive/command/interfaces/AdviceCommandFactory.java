/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.interfaces;

import im.expensive.command.api.AdviceCommand;
import im.expensive.command.interfaces.CommandProvider;

public interface AdviceCommandFactory {
    public AdviceCommand adviceCommand(CommandProvider var1);
}

