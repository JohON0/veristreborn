/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import im.expensive.events.CancelEvent;

public class InventoryCloseEvent
extends CancelEvent {
    public int windowId;

    public InventoryCloseEvent(int windowId) {
        this.windowId = windowId;
    }
}

