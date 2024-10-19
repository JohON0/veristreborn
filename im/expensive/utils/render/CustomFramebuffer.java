/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render;

import im.expensive.utils.client.IMinecraft;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;

public class CustomFramebuffer
extends Framebuffer
implements IMinecraft {
    private boolean linear;

    public CustomFramebuffer(boolean useDepthIn) {
        super(1, 1, useDepthIn, Minecraft.IS_RUNNING_ON_MAC);
    }

    public void resizeFramebuffer(Framebuffer framebuffer) {
        Minecraft mc = Minecraft.getInstance();
        if (framebuffer.framebufferWidth != mc.getMainWindow().getWidth() || framebuffer.framebufferHeight != mc.getMainWindow().getFramebufferHeight()) {
            framebuffer.createBuffers(Math.max(mc.getMainWindow().getWidth(), 1), Math.max(mc.getMainWindow().getFramebufferHeight(), 1), Minecraft.IS_RUNNING_ON_MAC);
        }
    }

    public static void drawTexture() {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Tessellator tessellator = Tessellator.getInstance();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(0.0, 0.0, 0.0).tex(0.0f, 1.0f).endVertex();
        bufferBuilder.pos(0.0, (double)Math.max(mc.getMainWindow().getScaledHeight(), 1), 0.0).tex(0.0f, 0.0f).endVertex();
        bufferBuilder.pos(Math.max(mc.getMainWindow().getScaledWidth(), 1), (double)Math.max(mc.getMainWindow().getScaledHeight(), 1), 0.0).tex(1.0f, 0.0f).endVertex();
        bufferBuilder.pos(Math.max(mc.getMainWindow().getScaledWidth(), 1), 0.0, 0.0).tex(1.0f, 1.0f).endVertex();
        tessellator.draw();
    }

    public void setup(boolean clear) {
        this.resizeFramebuffer(this);
        if (clear) {
            this.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        }
        this.bindFramebuffer(true);
    }

    public void stop() {
        this.unbindFramebuffer();
        Minecraft.getInstance().getFramebuffer().bindFramebuffer(true);
    }

    public void draw() {
        this.bindFramebufferTexture();
        CustomFramebuffer.drawTexture();
    }

    public void draw(int color) {
        this.bindFramebufferTexture();
        CustomFramebuffer.drawTexture(color);
    }

    public void draw(Framebuffer bFramebuffer) {
        bFramebuffer.bindFramebufferTexture();
        CustomFramebuffer.drawTexture();
    }

    public static void drawTexture(int color) {
        Minecraft mc = Minecraft.getInstance();
        MainWindow sr = mc.getMainWindow();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float width = sr.getScaledWidth();
        float height = sr.getScaledHeight();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferBuilder.pos(0.0, 0.0, 0.0).color(color).tex(0.0f, 1.0f).endVertex();
        bufferBuilder.pos(0.0, (double)height, 0.0).color(color).tex(0.0f, 0.0f).endVertex();
        bufferBuilder.pos(width, (double)height, 0.0).color(color).tex(1.0f, 0.0f).endVertex();
        bufferBuilder.pos(width, 0.0, 0.0).color(color).tex(1.0f, 1.0f).endVertex();
        tessellator.draw();
    }

    public CustomFramebuffer setLinear() {
        this.linear = true;
        return this;
    }

    @Override
    public void setFramebufferFilter(int framebufferFilterIn) {
        super.setFramebufferFilter(this.linear ? 9729 : framebufferFilterIn);
    }

    public void setup() {
        this.resizeFramebuffer(this);
        this.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        this.bindFramebuffer(false);
    }
}

