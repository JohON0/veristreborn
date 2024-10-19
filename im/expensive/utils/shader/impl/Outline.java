/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader.impl;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.IRenderCall;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.shader.ShaderUtil;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL30;

public class Outline
implements IMinecraft {
    private static final ConcurrentLinkedQueue<IRenderCall> renderQueue = Queues.newConcurrentLinkedQueue();
    private static final Framebuffer inFrameBuffer = new Framebuffer(1, 1, true, false);
    private static final Framebuffer outFrameBuffer = new Framebuffer(1, 1, true, false);

    public static void registerRenderCall(IRenderCall rc) {
        renderQueue.add(rc);
    }

    public static void draw(int radius, int color) {
        if (renderQueue.isEmpty()) {
            return;
        }
        Outline.setupBuffer(inFrameBuffer);
        Outline.setupBuffer(outFrameBuffer);
        inFrameBuffer.bindFramebuffer(true);
        while (!renderQueue.isEmpty()) {
            renderQueue.poll().execute();
        }
        outFrameBuffer.bindFramebuffer(true);
        ShaderUtil.outline.attach();
        ShaderUtil.outline.setUniformf("size", new float[]{radius});
        ShaderUtil.outline.setUniform("textureIn", 0);
        ShaderUtil.outline.setUniform("textureToCheck", 20);
        ShaderUtil.outline.setUniformf("texelSize", 1.0f / (float)mc.getMainWindow().getWidth(), 1.0f / (float)mc.getMainWindow().getHeight());
        ShaderUtil.outline.setUniformf("direction", 1.0f, 0.0f);
        float[] col = ColorUtils.rgba(color);
        ShaderUtil.outline.setUniformf("color", col[0], col[1], col[2]);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(1, 770);
        GL30.glAlphaFunc(516, 1.0E-4f);
        inFrameBuffer.bindFramebufferTexture();
        ShaderUtil.drawQuads();
        mc.getFramebuffer().bindFramebuffer(false);
        GlStateManager.blendFunc(770, 771);
        ShaderUtil.outline.setUniformf("direction", 0.0f, 1.0f);
        outFrameBuffer.bindFramebufferTexture();
        GL30.glActiveTexture(34004);
        inFrameBuffer.bindFramebufferTexture();
        GL30.glActiveTexture(33984);
        ShaderUtil.drawQuads();
        ShaderUtil.outline.detach();
        GlStateManager.bindTexture(0);
        GlStateManager.disableBlend();
    }

    public static Framebuffer setupBuffer(Framebuffer frameBuffer) {
        if (frameBuffer.framebufferWidth != mc.getMainWindow().getWidth() || frameBuffer.framebufferHeight != mc.getMainWindow().getHeight()) {
            frameBuffer.resize(Math.max(1, mc.getMainWindow().getWidth()), Math.max(1, mc.getMainWindow().getHeight()), false);
        } else {
            frameBuffer.framebufferClear(false);
        }
        frameBuffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        return frameBuffer;
    }
}

