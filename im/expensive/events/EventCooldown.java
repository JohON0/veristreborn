/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import net.minecraft.item.Item;

public class EventCooldown {
    private Item item;
    private CooldownType cooldownType;

    public boolean isAdded() {
        return this.cooldownType == CooldownType.ADDED;
    }

    public boolean isRemoved() {
        return this.cooldownType == CooldownType.REMOVED;
    }

    public EventCooldown(Item item, CooldownType cooldownType) {
        this.item = item;
        this.cooldownType = cooldownType;
    }

    public Item getItem() {
        return this.item;
    }

    public CooldownType getCooldownType() {
        return this.cooldownType;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setCooldownType(CooldownType cooldownType) {
        this.cooldownType = cooldownType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventCooldown)) {
            return false;
        }
        EventCooldown other = (EventCooldown)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Item this$item = this.getItem();
        Item other$item = other.getItem();
        if (this$item == null ? other$item != null : !this$item.equals(other$item)) {
            return false;
        }
        CooldownType this$cooldownType = this.getCooldownType();
        CooldownType other$cooldownType = other.getCooldownType();
        return !(this$cooldownType == null ? other$cooldownType != null : !((Object)((Object)this$cooldownType)).equals((Object)other$cooldownType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventCooldown;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Item $item = this.getItem();
        result = result * 59 + ($item == null ? 43 : $item.hashCode());
        CooldownType $cooldownType = this.getCooldownType();
        result = result * 59 + ($cooldownType == null ? 43 : ((Object)((Object)$cooldownType)).hashCode());
        return result;
    }

    public String toString() {
        return "EventCooldown(item=" + this.getItem() + ", cooldownType=" + this.getCooldownType() + ")";
    }

    public static enum CooldownType {
        ADDED,
        REMOVED;

    }
}

