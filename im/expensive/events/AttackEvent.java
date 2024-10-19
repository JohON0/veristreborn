/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import net.minecraft.entity.Entity;

public class AttackEvent {
    public Entity entity;

    public AttackEvent(Entity entity) {
        this.entity = entity;
    }
}

