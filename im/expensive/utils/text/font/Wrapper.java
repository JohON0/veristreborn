/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.text.font;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

public interface Wrapper {
    public static final Tessellator TESSELLATOR = Tessellator.getInstance();
    public static final BufferBuilder BUILDER = TESSELLATOR.getBuffer();
}

