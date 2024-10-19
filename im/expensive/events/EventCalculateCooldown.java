/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.Event;

public class EventCalculateCooldown
extends Event {
    public Item itemStack;
    public float cooldown;

    public EventCalculateCooldown(Item item) {
        this.itemStack = item;
    }

    public Item getItemStack() {
        return this.itemStack;
    }

    public float getCooldown() {
        return this.cooldown;
    }

    public void setItemStack(Item itemStack) {
        this.itemStack = itemStack;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public String toString() {
        return "EventCalculateCooldown(itemStack=" + this.getItemStack() + ", cooldown=" + this.getCooldown() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventCalculateCooldown)) {
            return false;
        }
        EventCalculateCooldown other = (EventCalculateCooldown)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (Float.compare(this.getCooldown(), other.getCooldown()) != 0) {
            return false;
        }
        Item this$itemStack = this.getItemStack();
        Item other$itemStack = other.getItemStack();
        return !(this$itemStack == null ? other$itemStack != null : !this$itemStack.equals(other$itemStack));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventCalculateCooldown;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + Float.floatToIntBits(this.getCooldown());
        Item $itemStack = this.getItemStack();
        result = result * 59 + ($itemStack == null ? 43 : $itemStack.hashCode());
        return result;
    }
}

