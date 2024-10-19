/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.api;

import im.expensive.command.interfaces.Logger;

public class ConsoleLogger
implements Logger {
    @Override
    public void log(String message) {
        System.out.println("message = " + message);
    }
}

