/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.settings;

import im.expensive.modules.settings.Setting;
import java.util.function.Supplier;

public interface ISetting {
    public Setting<?> setVisible(Supplier<Boolean> var1);
}

