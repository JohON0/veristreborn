/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.api;

import im.expensive.command.interfaces.Logger;
import java.util.List;

public class MultiLogger
implements Logger {
    private final List<Logger> loggers;

    @Override
    public void log(String message) {
        for (Logger logger : this.loggers) {
            logger.log(message);
        }
    }

    public MultiLogger(List<Logger> loggers) {
        this.loggers = loggers;
    }
}

