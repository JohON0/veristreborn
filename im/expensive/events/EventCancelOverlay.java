/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import im.expensive.events.CancelEvent;

public class EventCancelOverlay
extends CancelEvent {
    public final Overlays overlayType;

    public EventCancelOverlay(Overlays overlayType) {
        this.overlayType = overlayType;
    }

    public static enum Overlays {
        FIRE_OVERLAY,
        BOSS_LINE,
        SCOREBOARD,
        TITLES,
        TOTEM,
        FOG,
        HURT,
        UNDER_WATER,
        CAMERA_CLIP,
        ARMOR;

    }
}

