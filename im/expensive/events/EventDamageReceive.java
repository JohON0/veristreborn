/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

public class EventDamageReceive {
    private final DamageType damageType;

    public EventDamageReceive(DamageType damageType) {
        this.damageType = damageType;
    }

    public DamageType getDamageType() {
        return this.damageType;
    }

    public static enum DamageType {
        FALL,
        ARROW,
        ENDER_PEARL;

    }
}

