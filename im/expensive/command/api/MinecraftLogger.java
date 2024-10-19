/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.api;

import im.expensive.command.interfaces.Logger;
import im.expensive.utils.client.IMinecraft;

public class MinecraftLogger
implements Logger,
IMinecraft {
    @Override
    public void log(String message) {
        this.print(message);
    }
}

