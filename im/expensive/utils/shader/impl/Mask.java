/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.shader.ShaderUtil;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL30;

public final class Mask
implements IMinecraft {
    private static final Framebuffer in = new Framebuffer(1, 1, true, false);
    private static final Framebuffer out = new Framebuffer(1, 1, true, false);

    public static void renderMask(float x, float y, float width, float height, Runnable mask) {
        Mask.setupBuffer(in);
        Mask.setupBuffer(out);
        in.bindFramebuffer(true);
        mask.run();
        out.bindFramebuffer(true);
        ShaderUtil.mask.attach();
        ShaderUtil.mask.setUniformf("location", (float)((double)x * mc.getMainWindow().getGuiScaleFactor()), (float)((double)mc.getMainWindow().getHeight() - (double)height * mc.getMainWindow().getGuiScaleFactor() - (double)y * mc.getMainWindow().getGuiScaleFactor()));
        ShaderUtil.mask.setUniformf("rectSize", (double)width * mc.getMainWindow().getGuiScaleFactor(), (double)height * mc.getMainWindow().getGuiScaleFactor());
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(1, 770);
        GL30.glAlphaFunc(516, 1.0E-4f);
        in.bindFramebufferTexture();
        ShaderUtil.drawQuads();
        mc.getFramebuffer().bindFramebuffer(false);
        GlStateManager.blendFunc(770, 771);
        out.bindFramebufferTexture();
        GL30.glActiveTexture(34004);
        in.bindFramebufferTexture();
        GL30.glActiveTexture(33984);
        ShaderUtil.drawQuads();
        ShaderUtil.mask.detach();
        GlStateManager.bindTexture(0);
        GlStateManager.disableBlend();
    }

    private static Framebuffer setupBuffer(Framebuffer frameBuffer) {
        if (frameBuffer.framebufferWidth != mc.getMainWindow().getWidth() || frameBuffer.framebufferHeight != mc.getMainWindow().getHeight()) {
            frameBuffer.resize(Math.max(1, mc.getMainWindow().getWidth()), Math.max(1, mc.getMainWindow().getHeight()), false);
        } else {
            frameBuffer.framebufferClear(false);
        }
        frameBuffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        return frameBuffer;
    }

    private Mask() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

