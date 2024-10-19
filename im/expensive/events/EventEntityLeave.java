/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

public class EventEntityLeave
extends Event {
    private Entity entity;

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String toString() {
        return "EventEntityLeave(entity=" + this.getEntity() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventEntityLeave)) {
            return false;
        }
        EventEntityLeave other = (EventEntityLeave)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Entity this$entity = this.getEntity();
        Entity other$entity = other.getEntity();
        return !(this$entity == null ? other$entity != null : !((Object)this$entity).equals(other$entity));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventEntityLeave;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Entity $entity = this.getEntity();
        result = result * 59 + ($entity == null ? 43 : ((Object)$entity).hashCode());
        return result;
    }

    public EventEntityLeave(Entity entity) {
        this.entity = entity;
    }
}

