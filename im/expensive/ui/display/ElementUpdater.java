/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display;

import im.expensive.events.EventUpdate;
import im.expensive.utils.client.IMinecraft;

public interface ElementUpdater
extends IMinecraft {
    public void update(EventUpdate var1);
}

