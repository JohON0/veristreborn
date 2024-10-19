/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.api;

import im.expensive.command.api.ParametersImpl;
import im.expensive.command.interfaces.Parameters;
import im.expensive.command.interfaces.ParametersFactory;

public class ParametersFactoryImpl
implements ParametersFactory {
    @Override
    public Parameters createParameters(String message, String delimiter) {
        return new ParametersImpl(message.split(delimiter));
    }
}

