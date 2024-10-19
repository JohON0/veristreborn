/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render.gl;

import com.mojang.blaze3d.platform.GlStateManager;

public final class GLUtils {
    public static void enableDepth() {
        GlStateManager.enableDepthTest();
        GlStateManager.depthMask(true);
    }

    public static void disableDepth() {
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
    }

    public static void startBlend() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
    }

    public static void endBlend() {
        GlStateManager.disableBlend();
    }

    public static void setup2DRendering(boolean blend) {
        if (blend) {
            GLUtils.startBlend();
        }
        GlStateManager.disableTexture();
    }

    public static void setup2DRendering() {
        GLUtils.setup2DRendering(true);
    }

    public static void end2DRendering() {
        GlStateManager.enableTexture();
        GLUtils.endBlend();
    }

    public static void startRotate(float x, float y, float rotate) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 0.0f);
        GlStateManager.rotatef(rotate, 0.0f, 0.0f, -1.0f);
        GlStateManager.translatef(-x, -y, 0.0f);
    }

    public static void endRotate() {
        GlStateManager.popMatrix();
    }

    public static void scaleStart(float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);
        GlStateManager.translated(-x, -y, 0.0);
    }

    public static void scaleEnd() {
        GlStateManager.popMatrix();
    }

    public static void rotate(float x, float y, float rotate, Runnable runnable) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 0.0f);
        GlStateManager.rotatef(rotate, 0.0f, 0.0f, -1.0f);
        GlStateManager.translatef(-x, -y, 0.0f);
        runnable.run();
        GlStateManager.popMatrix();
    }

    private GLUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

