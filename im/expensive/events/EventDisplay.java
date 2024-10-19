/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import com.mojang.blaze3d.matrix.MatrixStack;

public class EventDisplay {
    private MatrixStack matrixStack;
    private float partialTicks;
    private Type type;

    public EventDisplay(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
    }

    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public Type getType() {
        return this.type;
    }

    public void setMatrixStack(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventDisplay)) {
            return false;
        }
        EventDisplay other = (EventDisplay)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getPartialTicks(), other.getPartialTicks()) != 0) {
            return false;
        }
        MatrixStack this$matrixStack = this.getMatrixStack();
        MatrixStack other$matrixStack = other.getMatrixStack();
        if (this$matrixStack == null ? other$matrixStack != null : !this$matrixStack.equals(other$matrixStack)) {
            return false;
        }
        Type this$type = this.getType();
        Type other$type = other.getType();
        return !(this$type == null ? other$type != null : !((Object)((Object)this$type)).equals((Object)other$type));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventDisplay;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getPartialTicks());
        MatrixStack $matrixStack = this.getMatrixStack();
        result = result * 59 + ($matrixStack == null ? 43 : $matrixStack.hashCode());
        Type $type = this.getType();
        result = result * 59 + ($type == null ? 43 : ((Object)((Object)$type)).hashCode());
        return result;
    }

    public String toString() {
        return "EventDisplay(matrixStack=" + this.getMatrixStack() + ", partialTicks=" + this.getPartialTicks() + ", type=" + this.getType() + ")";
    }

    public static enum Type {
        PRE,
        POST,
        HIGH;

    }
}

