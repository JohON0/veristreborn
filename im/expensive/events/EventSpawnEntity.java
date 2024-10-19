/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import net.minecraft.entity.Entity;

public class EventSpawnEntity {
    private Entity entity;

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventSpawnEntity)) {
            return false;
        }
        EventSpawnEntity other = (EventSpawnEntity)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Entity this$entity = this.getEntity();
        Entity other$entity = other.getEntity();
        return !(this$entity == null ? other$entity != null : !((Object)this$entity).equals(other$entity));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventSpawnEntity;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Entity $entity = this.getEntity();
        result = result * 59 + ($entity == null ? 43 : ((Object)$entity).hashCode());
        return result;
    }

    public String toString() {
        return "EventSpawnEntity(entity=" + this.getEntity() + ")";
    }

    public EventSpawnEntity(Entity entity) {
        this.entity = entity;
    }
}

