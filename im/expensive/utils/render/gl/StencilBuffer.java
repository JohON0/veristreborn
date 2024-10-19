/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.utils.client.IMinecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public final class StencilBuffer
implements IMinecraft {
    private static void recreate(Framebuffer framebuffer) {
        GL30.glDeleteRenderbuffers(framebuffer.depthBuffer);
        int depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(36161, depthBuffer);
        GL30.glRenderbufferStorage(36161, 34041, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight());
        GL30.glFramebufferRenderbuffer(36160, 36128, 36161, depthBuffer);
        GL30.glFramebufferRenderbuffer(36160, 36096, 36161, depthBuffer);
    }

    public static void checkSetupFBO(Framebuffer framebuffer) {
        if (framebuffer != null && framebuffer.depthBuffer > -1) {
            StencilBuffer.recreate(framebuffer);
            framebuffer.depthBuffer = -1;
        }
    }

    public static void init() {
        mc.getFramebuffer().bindFramebuffer(false);
        StencilBuffer.checkSetupFBO(mc.getFramebuffer());
        GL11.glClear(1024);
        GL11.glEnable(2960);
        GL11.glStencilFunc(519, 1, 1);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glColorMask(false, false, false, false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.0f);
    }

    public static void bind() {
        GL11.glStencilFunc(519, 1, 1);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glColorMask(false, false, false, false);
    }

    public static void read(int ref) {
        GL11.glColorMask(true, true, true, true);
        GL11.glStencilFunc(514, ref, 1);
        GL11.glStencilOp(7680, 7680, 7680);
    }

    public static void cleanup() {
        GL11.glDisable(2960);
        GlStateManager.disableAlphaTest();
        GlStateManager.disableBlend();
    }

    private StencilBuffer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static enum Action {
        OUTSIDE(1),
        INSIDE(2);

        private final int action;

        public int getAction() {
            return this.action;
        }

        private Action(int action) {
            this.action = action;
        }
    }
}

