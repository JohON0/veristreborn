/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.api;

import im.expensive.command.interfaces.Prefix;

public class PrefixImpl
implements Prefix {
    public String prefix = ".";

    @Override
    public void set(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String get() {
        return this.prefix;
    }
}

