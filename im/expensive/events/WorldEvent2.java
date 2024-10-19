/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import com.mojang.blaze3d.matrix.MatrixStack;

public class WorldEvent2 {
    private MatrixStack stack;
    private float partialTicks;

    public WorldEvent2(MatrixStack stack, float partialTicks) {
        this.stack = stack;
        this.partialTicks = partialTicks;
    }

    public MatrixStack getStack() {
        return this.stack;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setStack(MatrixStack stack) {
        this.stack = stack;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

