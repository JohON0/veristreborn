/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.interfaces;

import im.expensive.command.interfaces.Parameters;

public interface Command {
    public void execute(Parameters var1);

    public String name();

    public String description();
}

