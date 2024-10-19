/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render.rect;

import com.jhlabs.image.GaussianFilter;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.gl.Stencil;
import im.expensive.utils.shader.ShaderUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

public class DisplayUtils
implements IMinecraft {
    private static final HashMap<Integer, Integer> shadowCache = new HashMap();
    private static Framebuffer whiteCache = new Framebuffer(1, 1, false, true);
    private static Framebuffer contrastCache = new Framebuffer(1, 1, false, true);

    public static void quads(float x, float y, float width, float height, int glQuads, int color) {
        buffer.begin(glQuads, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(x, (double)y, 0.0).tex(0.0f, 0.0f).color(color).endVertex();
        buffer.pos(x, (double)(y + height), 0.0).tex(0.0f, 1.0f).color(color).endVertex();
        buffer.pos(x + width, (double)(y + height), 0.0).tex(1.0f, 1.0f).color(color).endVertex();
        buffer.pos(x + width, (double)y, 0.0).tex(1.0f, 0.0f).color(color).endVertex();
        tessellator.draw();
    }

    public static void drawCircleWithFill(float x, float y, float start, float end, float radius, float width, boolean filled, int color) {
        float sin;
        float cos;
        float i;
        if (start > end) {
            float endOffset = end;
            end = start;
            start = endOffset;
        }
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(3);
        for (i = end; i >= start; i -= 1.0f) {
            ColorUtils.setColor(color);
            cos = MathHelper.cos((float)((double)i * Math.PI / 180.0)) * radius;
            sin = MathHelper.sin((float)((double)i * Math.PI / 180.0)) * radius;
            GL11.glVertex2f(x + cos, y + sin);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        if (filled) {
            GL11.glBegin(6);
            for (i = end; i >= start; i -= 1.0f) {
                ColorUtils.setColor(color);
                cos = MathHelper.cos((float)((double)i * Math.PI / 180.0)) * radius;
                sin = MathHelper.sin((float)((double)i * Math.PI / 180.0)) * radius;
                GL11.glVertex2f(x + cos, y + sin);
            }
            GL11.glEnd();
        }
        GL11.glEnable(3553);
        GlStateManager.disableBlend();
    }

    public static void drawCircleWithFill(float x, float y, float start, float end, float radius, float width, boolean filled) {
        float sin;
        float cos;
        float i;
        if (start > end) {
            float endOffset = end;
            end = start;
            start = endOffset;
        }
        GlStateManager.enableBlend();
        RenderSystem.disableAlphaTest();
        GL11.glDisable(3553);
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.shadeModel(7425);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(3);
        for (i = end; i >= start; i -= 1.0f) {
            ColorUtils.setColor(ColorUtils.getColor((int)(i * 1.0f)));
            cos = MathHelper.cos((float)((double)i * Math.PI / 180.0)) * radius;
            sin = MathHelper.sin((float)((double)i * Math.PI / 180.0)) * radius;
            GL11.glVertex2f(x + cos, y + sin);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        if (filled) {
            GL11.glBegin(6);
            for (i = end; i >= start; i -= 1.0f) {
                ColorUtils.setColor(ColorUtils.getColor((int)(i * 1.0f)));
                cos = MathHelper.cos((float)((double)i * Math.PI / 180.0)) * radius;
                sin = MathHelper.sin((float)((double)i * Math.PI / 180.0)) * radius;
                GL11.glVertex2f(x + cos, y + sin);
            }
            GL11.glEnd();
        }
        RenderSystem.enableAlphaTest();
        RenderSystem.shadeModel(7424);
        GL11.glEnable(3553);
        GlStateManager.disableBlend();
    }

    public static boolean isInRegion(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static boolean isInRegion(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= (double)x && mouseX <= (double)(x + width) && mouseY >= (double)y && mouseY <= (double)(y + height);
    }

    public static boolean isInRegion(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= (double)x && mouseX <= (double)(x + width) && mouseY >= (double)y && mouseY <= (double)(y + height);
    }

    public static void drawStyledRect(float x, float y, float width, float height) {
        DisplayUtils.renderRectClient(x, y, width, height, 140, 80);
    }

    public static void drawStyledShadowRect(float x, float y, float width, float height) {
        DisplayUtils.shadowRect(x, y, width, height, 140);
    }

    public static void drawStyledShadowRect(float x, float y, float width, float height, int alpha) {
        DisplayUtils.shadowRect(x, y, width, height, alpha);
    }

    public static void drawStyledRect(float x, float y, float width, float height, int alpha, int upRectAlpha) {
        DisplayUtils.renderRectClient(x, y, width, height, alpha, upRectAlpha);
    }

    private static void shadowRect(float x, float y, float width, float height, int alpha) {
        int colorWithAlpha1 = ColorUtils.reAlphaInt(Theme.darkMainRectColor, alpha);
        int colorWithAlpha2 = ColorUtils.reAlphaInt(Theme.darkMainRectColor, alpha);
        float off = 1.5f;
        Stencil.initStencilToWrite();
        DisplayUtils.drawRoundedRect(x, y, width, height, new Vector4f(5.5f, 5.5f, 5.5f, 5.5f), new Vector4i(colorWithAlpha1, colorWithAlpha1, colorWithAlpha2, colorWithAlpha2));
        Stencil.readStencilBuffer(0);
        DisplayUtils.drawRoundedRect(x - off, y - off, width + off * 2.0f, height + off * 2.0f, new Vector4f(6.0f, 6.0f, 6.0f, 6.0f), new Vector4i(Theme.mainRectColor, Theme.mainRectColor, Theme.mainRectColor, Theme.mainRectColor));
        DisplayUtils.drawShadow(x - off, y - off, width + off * 2.0f, height + off * 2.0f, 8, Theme.mainRectColor, Theme.mainRectColor);
        Stencil.uninitStencilBuffer();
        DisplayUtils.drawRoundedRect(x, y, width, height, new Vector4f(4.0f, 4.0f, 4.0f, 4.0f), new Vector4i(colorWithAlpha1, colorWithAlpha1, colorWithAlpha2, colorWithAlpha2));
        DisplayUtils.drawShadow(x, y, width, height, 8, colorWithAlpha1, colorWithAlpha2);
    }

    private static void renderRectClient(float x, float y, float width, float height, int alpha, int upRectAlpha) {
        int colorWithAlpha1 = ColorUtils.reAlphaInt(Theme.darkMainRectColor, alpha);
        int colorWithAlpha2 = ColorUtils.reAlphaInt(Theme.darkMainRectColor, alpha);
        boolean off = true;
        Stencil.initStencilToWrite();
        DisplayUtils.drawRoundedRect(x, y, width, 17.0f, new Vector4f(4.0f, 0.0f, 4.0f, 0.0f), -1);
        Stencil.readStencilBuffer(0);
        DisplayUtils.drawRoundedRect(x, y, width, height, new Vector4f(4.0f, 4.0f, 4.0f, 4.0f), new Vector4i(colorWithAlpha1, colorWithAlpha1, colorWithAlpha2, colorWithAlpha2));
        Stencil.uninitStencilBuffer();
        DisplayUtils.drawShadow(x, y, width, height, 8, colorWithAlpha1, colorWithAlpha2);
        DisplayUtils.drawRoundedRect(x, y, width, 17.0f, new Vector4f(4.0f, 0.0f, 4.0f, 0.0f), ColorUtils.reAlphaInt(Theme.mainRectColor, upRectAlpha));
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

    public static void horizontalGradient(double x1, double y1, double x2, double y2, int startColor, int endColor) {
        x2 += x1;
        y2 += y1;
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.ordinal(), GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.ordinal(), GlStateManager.SourceFactor.ONE.ordinal(), GlStateManager.DestFactor.ZERO.ordinal());
        GlStateManager.shadeModel(7425);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x1, y1, 0.0).color(f1, f2, f3, f).endVertex();
        buffer.pos(x1, y2, 0.0).color(f1, f2, f3, f).endVertex();
        buffer.pos(x2, y2, 0.0).color(f5, f6, f7, f4).endVertex();
        buffer.pos(x2, y1, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
    }

    public static void scissor(double x, double y, double width, double height) {
        double scale = mc.getMainWindow().getGuiScaleFactor();
        y = (double)mc.getMainWindow().getScaledHeight() - y;
        GL11.glScissor((int)(x *= scale), (int)((y *= scale) - (height *= scale)), (int)(width *= scale), (int)height);
    }

    public static void drawShadow(float x, float y, float width, float height, int radius, int color, int i) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.alphaFunc(516, 0.01f);
        GlStateManager.disableAlphaTest();
        GL11.glShadeModel(7425);
        x -= (float)radius;
        y -= (float)radius;
        x -= 0.25f;
        y += 0.25f;
        int identifier = Objects.hash(Float.valueOf(width += (float)(radius * 2)), Float.valueOf(height += (float)(radius * 2)), radius);
        if (shadowCache.containsKey(identifier)) {
            int textureId = shadowCache.get(identifier);
            GlStateManager.bindTexture(textureId);
        } else {
            if (width <= 0.0f) {
                width = 1.0f;
            }
            if (height <= 0.0f) {
                height = 1.0f;
            }
            BufferedImage originalImage = new BufferedImage((int)width, (int)height, 3);
            Graphics2D graphics = originalImage.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(radius, radius, (int)(width - (float)(radius * 2)), (int)(height - (float)(radius * 2)));
            graphics.dispose();
            GaussianFilter filter = new GaussianFilter(radius);
            BufferedImage blurredImage = filter.filter(originalImage, null);
            DynamicTexture texture = new DynamicTexture(TextureUtils.toNativeImage(blurredImage));
            texture.setBlurMipmap(true, true);
            int textureId = texture.getGlTextureId();
            shadowCache.put(identifier, textureId);
        }
        float[] startColorComponents = ColorUtils.rgba(color);
        float[] i1 = ColorUtils.rgba(i);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(x, (double)y, 0.0).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(0.0f, 0.0f).endVertex();
        buffer.pos(x, (double)(y + (float)((int)height)), 0.0).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(0.0f, 1.0f).endVertex();
        buffer.pos(x + (float)((int)width), (double)(y + (float)((int)height)), 0.0).color(i1[0], i1[1], i1[2], i1[3]).tex(1.0f, 1.0f).endVertex();
        buffer.pos(x + (float)((int)width), (double)y, 0.0).color(i1[0], i1[1], i1[2], i1[3]).tex(1.0f, 0.0f).endVertex();
        tessellator.draw();
        GlStateManager.enableAlphaTest();
        GL11.glShadeModel(7424);
        GlStateManager.bindTexture(0);
        GlStateManager.disableBlend();
    }

    public static void drawShadowVertical(float x, float y, float width, float height, int radius, int color, int i) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.alphaFunc(516, 0.01f);
        GlStateManager.disableAlphaTest();
        GL11.glShadeModel(7425);
        x -= (float)radius;
        y -= (float)radius;
        x -= 0.25f;
        y += 0.25f;
        int identifier = Objects.hash(Float.valueOf(width += (float)(radius * 2)), Float.valueOf(height += (float)(radius * 2)), radius);
        if (shadowCache.containsKey(identifier)) {
            int textureId = shadowCache.get(identifier);
            GlStateManager.bindTexture(textureId);
        } else {
            int textureId;
            if (width <= 0.0f) {
                width = 1.0f;
            }
            if (height <= 0.0f) {
                height = 1.0f;
            }
            BufferedImage originalImage = new BufferedImage((int)width, (int)height, 3);
            Graphics2D graphics = originalImage.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(radius, radius, (int)(width - (float)(radius * 2)), (int)(height - (float)(radius * 2)));
            graphics.dispose();
            GaussianFilter filter = new GaussianFilter(radius);
            BufferedImage blurredImage = filter.filter(originalImage, null);
            DynamicTexture texture = new DynamicTexture(TextureUtils.toNativeImage(blurredImage));
            texture.setBlurMipmap(true, true);
            try {
                textureId = texture.getGlTextureId();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            shadowCache.put(identifier, textureId);
        }
        float[] startColorComponents = ColorUtils.rgba(color);
        float[] i1 = ColorUtils.rgba(i);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(x, (double)y, 0.0).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(0.0f, 0.0f).endVertex();
        buffer.pos(x, (double)(y + (float)((int)height)), 0.0).color(i1[0], i1[1], i1[2], i1[3]).tex(0.0f, 1.0f).endVertex();
        buffer.pos(x + (float)((int)width), (double)(y + (float)((int)height)), 0.0).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(1.0f, 1.0f).endVertex();
        buffer.pos(x + (float)((int)width), (double)y, 0.0).color(i1[0], i1[1], i1[2], i1[3]).tex(1.0f, 0.0f).endVertex();
        tessellator.draw();
        GlStateManager.enableAlphaTest();
        GL11.glShadeModel(7424);
        GlStateManager.bindTexture(0);
        GlStateManager.disableBlend();
    }

    public static void drawShadow(float x, float y, float width, float height, int radius, int color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.alphaFunc(516, 0.01f);
        GlStateManager.disableAlphaTest();
        x -= (float)radius;
        y -= (float)radius;
        x -= 0.25f;
        y += 0.25f;
        int identifier = Objects.hash(Float.valueOf(width += (float)(radius * 2)), Float.valueOf(height += (float)(radius * 2)), radius);
        if (shadowCache.containsKey(identifier)) {
            int textureId = shadowCache.get(identifier);
            GlStateManager.bindTexture(textureId);
        } else {
            int textureId;
            if (width <= 0.0f) {
                width = 1.0f;
            }
            if (height <= 0.0f) {
                height = 1.0f;
            }
            BufferedImage originalImage = new BufferedImage((int)width, (int)height, 3);
            Graphics2D graphics = originalImage.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(radius, radius, (int)(width - (float)(radius * 2)), (int)(height - (float)(radius * 2)));
            graphics.dispose();
            GaussianFilter filter = new GaussianFilter(radius);
            BufferedImage blurredImage = filter.filter(originalImage, null);
            DynamicTexture texture = new DynamicTexture(TextureUtils.toNativeImage(blurredImage));
            texture.setBlurMipmap(true, true);
            try {
                textureId = texture.getGlTextureId();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            shadowCache.put(identifier, textureId);
        }
        float[] startColorComponents = ColorUtils.rgba(color);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(x, (double)y, 0.0).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(0.0f, 0.0f).endVertex();
        buffer.pos(x, (double)(y + (float)((int)height)), 0.0).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(0.0f, 1.0f).endVertex();
        buffer.pos(x + (float)((int)width), (double)(y + (float)((int)height)), 0.0).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(1.0f, 1.0f).endVertex();
        buffer.pos(x + (float)((int)width), (double)y, 0.0).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(1.0f, 0.0f).endVertex();
        tessellator.draw();
        GlStateManager.enableAlphaTest();
        GlStateManager.bindTexture(0);
        GlStateManager.disableBlend();
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float width, float height, int color) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        mc.getTextureManager().bindTexture(resourceLocation);
        DisplayUtils.quads(x, y, width, height, 7, color);
        RenderSystem.shadeModel(7424);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.popMatrix();
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float width, float height, Vector4i color) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        mc.getTextureManager().bindTexture(resourceLocation);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(x, (double)y, 0.0).tex(0.0f, 0.0f).color(color.x).endVertex();
        buffer.pos(x, (double)(y + height), 0.0).tex(0.0f, 1.0f).color(color.y).endVertex();
        buffer.pos(x + width, (double)(y + height), 0.0).tex(1.0f, 1.0f).color(color.z).endVertex();
        buffer.pos(x + width, (double)y, 0.0).tex(1.0f, 0.0f).color(color.w).endVertex();
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.popMatrix();
    }

    public static void drawRectWBuilding(double left, double top, double right, double bottom, int color) {
        right += left;
        bottom += top;
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.pos(left, bottom, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, top, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(left, top, 0.0).color(f, f1, f2, f3).endVertex();
    }

    public static void drawRectBuilding(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.pos(left, bottom, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, top, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(left, top, 0.0).color(f, f1, f2, f3).endVertex();
    }

    public static void drawMCVerticalBuilding(double x, double y, double width, double height, int start, int end) {
        float f = (float)(start >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(start >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(start >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(start & 0xFF) / 255.0f;
        float f4 = (float)(end >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(end >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(end >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(end & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.pos(x, height, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(width, height, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(width, y, 0.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(f5, f6, f7, f4).endVertex();
    }

    public static void drawMCHorizontalBuilding(double x, double y, double width, double height, int start, int end) {
        float f = (float)(start >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(start >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(start >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(start & 0xFF) / 255.0f;
        float f4 = (float)(end >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(end >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(end >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(end & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.pos(x, height, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(width, height, 0.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(width, y, 0.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(f1, f2, f3, f).endVertex();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, bottom, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, top, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(left, top, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawRectW(double x, double y, double w, double h, int color) {
        w = x + w;
        h = y + h;
        if (x < w) {
            double i = x;
            x = w;
            w = i;
        }
        if (y < h) {
            double j = y;
            y = h;
            h = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawRectHorizontalW(double x, double y, double w, double h, int color, int color1) {
        w = x + w;
        h = y + h;
        if (x < w) {
            double i = x;
            x = w;
            w = i;
        }
        if (y < h) {
            double j = y;
            y = h;
            h = j;
        }
        float[] colorOne = ColorUtils.rgba(color);
        float[] colorTwo = ColorUtils.rgba(color1);
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.shadeModel(7425);
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(colorTwo[0], colorTwo[1], colorTwo[2], colorTwo[3]).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(colorTwo[0], colorTwo[1], colorTwo[2], colorTwo[3]).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(colorOne[0], colorOne[1], colorOne[2], colorOne[3]).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(colorOne[0], colorOne[1], colorOne[2], colorOne[3]).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.shadeModel(7424);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawRectVerticalW(double x, double y, double w, double h, int color, int color1) {
        w = x + w;
        h = y + h;
        if (x < w) {
            double i = x;
            x = w;
            w = i;
        }
        if (y < h) {
            double j = y;
            y = h;
            h = j;
        }
        float[] colorOne = ColorUtils.rgba(color);
        float[] colorTwo = ColorUtils.rgba(color1);
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(colorOne[0], colorOne[1], colorOne[2], colorOne[3]).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(colorTwo[0], colorTwo[1], colorTwo[2], colorTwo[3]).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(colorTwo[0], colorTwo[1], colorTwo[2], colorTwo[3]).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(colorOne[0], colorOne[1], colorOne[2], colorOne[3]).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
    }

    public static void drawGradientRoundedRect(float x, float y, float w, float h, Vector4f r) {
        DisplayUtils.drawRoundedRect(x, y, w, h, new Vector4f(r.x, r.y, r.z, r.w), new Vector4i(ColorUtils.getColor((int)(10L + System.currentTimeMillis() / 1000L % 360L)), ColorUtils.getColor((int)(5L + System.currentTimeMillis() / 1000L % 360L)), ColorUtils.getColor((int)(1L + System.currentTimeMillis() / 1000L % 360L)), ColorUtils.getColor((int)(-1L + System.currentTimeMillis() / 1000L % 360L))));
    }

    public static void drawGradientRoundedRect(float x, float y, float w, float h, float r) {
        DisplayUtils.drawRoundedRect(x, y, w, h, new Vector4f(r, r, r, r), new Vector4i(ColorUtils.getColor((int)(10L + System.currentTimeMillis() / 1000L % 360L)), ColorUtils.getColor((int)(5L + System.currentTimeMillis() / 1000L % 360L)), ColorUtils.getColor((int)(1L + System.currentTimeMillis() / 1000L % 360L)), ColorUtils.getColor((int)(-1L + System.currentTimeMillis() / 1000L % 360L))));
    }

    public static void drawRoundedRect(float x, float y, float width, float height, Vector4f vector4f, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        ShaderUtil.rounded.attach();
        ShaderUtil.rounded.setUniform("size", width * 2.0f, height * 2.0f);
        ShaderUtil.rounded.setUniform("round", vector4f.x * 2.0f, vector4f.y * 2.0f, vector4f.z * 2.0f, vector4f.w * 2.0f);
        ShaderUtil.rounded.setUniform("smoothness", 0.0f, 1.5f);
        ShaderUtil.rounded.setUniform("color1", ColorUtils.rgba(color));
        ShaderUtil.rounded.setUniform("color2", ColorUtils.rgba(color));
        ShaderUtil.rounded.setUniform("color3", ColorUtils.rgba(color));
        ShaderUtil.rounded.setUniform("color4", ColorUtils.rgba(color));
        DisplayUtils.drawQuads(x, y, width, height, 7);
        ShaderUtil.rounded.detach();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawRoundedRect(float x, float y, float width, float height, Vector4f vector4f, Vector4i color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        ShaderUtil.rounded.attach();
        ShaderUtil.rounded.setUniform("size", width * 2.0f, height * 2.0f);
        ShaderUtil.rounded.setUniform("round", vector4f.x * 2.0f, vector4f.y * 2.0f, vector4f.z * 2.0f, vector4f.w * 2.0f);
        ShaderUtil.rounded.setUniform("smoothness", 0.0f, 1.5f);
        ShaderUtil.rounded.setUniform("color1", ColorUtils.rgba(color.getX()));
        ShaderUtil.rounded.setUniform("color2", ColorUtils.rgba(color.getY()));
        ShaderUtil.rounded.setUniform("color3", ColorUtils.rgba(color.getZ()));
        ShaderUtil.rounded.setUniform("color4", ColorUtils.rgba(color.getW()));
        DisplayUtils.drawQuads(x, y, width, height, 7);
        ShaderUtil.rounded.detach();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float outline, int color1, Vector4f vector4f, Vector4i color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        ShaderUtil.roundedout.attach();
        ShaderUtil.roundedout.setUniform("size", width * 2.0f, height * 2.0f);
        ShaderUtil.roundedout.setUniform("round", vector4f.x * 2.0f, vector4f.y * 2.0f, vector4f.z * 2.0f, vector4f.w * 2.0f);
        ShaderUtil.roundedout.setUniform("smoothness", 0.0f, 1.5f);
        ShaderUtil.roundedout.setUniform("outlineColor", ColorUtils.rgba(color.getX()));
        ShaderUtil.roundedout.setUniform("outlineColor1", ColorUtils.rgba(color.getY()));
        ShaderUtil.roundedout.setUniform("outlineColor2", ColorUtils.rgba(color.getZ()));
        ShaderUtil.roundedout.setUniform("outlineColor3", ColorUtils.rgba(color.getW()));
        ShaderUtil.roundedout.setUniform("color", ColorUtils.rgba(color1));
        ShaderUtil.roundedout.setUniform("outline", outline);
        DisplayUtils.drawQuads(x, y, width, height, 7);
        ShaderUtil.rounded.detach();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawContrast(float state) {
        state = MathHelper.clamp(state, 0.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.glBlendFuncSeparate(770, 771, 1, 0);
        contrastCache = ShaderUtil.createFrameBuffer(contrastCache);
        contrastCache.framebufferClear(false);
        contrastCache.bindFramebuffer(true);
        ShaderUtil.contrast.attach();
        ShaderUtil.contrast.setUniform("texture", 0);
        ShaderUtil.contrast.setUniformf("contrast", state);
        GlStateManager.bindTexture(DisplayUtils.mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        contrastCache.unbindFramebuffer();
        ShaderUtil.contrast.detach();
        mc.getFramebuffer().bindFramebuffer(true);
        ShaderUtil.contrast.attach();
        ShaderUtil.contrast.setUniform("texture", 0);
        ShaderUtil.contrast.setUniformf("contrast", state);
        GlStateManager.bindTexture(DisplayUtils.contrastCache.framebufferTexture);
        ShaderUtil.drawQuads();
        ShaderUtil.contrast.detach();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }

    public static void drawWhite(float state) {
        state = MathHelper.clamp(state, 0.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.glBlendFuncSeparate(770, 771, 1, 0);
        whiteCache = ShaderUtil.createFrameBuffer(whiteCache);
        whiteCache.framebufferClear(false);
        whiteCache.bindFramebuffer(true);
        ShaderUtil.white.attach();
        ShaderUtil.white.setUniform("texture", 0);
        ShaderUtil.white.setUniformf("state", state);
        GlStateManager.bindTexture(DisplayUtils.mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        whiteCache.unbindFramebuffer();
        ShaderUtil.white.detach();
        mc.getFramebuffer().bindFramebuffer(true);
        ShaderUtil.white.attach();
        ShaderUtil.white.setUniform("texture", 0);
        ShaderUtil.white.setUniformf("state", state);
        GlStateManager.bindTexture(DisplayUtils.whiteCache.framebufferTexture);
        ShaderUtil.drawQuads();
        ShaderUtil.white.detach();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        ShaderUtil.smooth.attach();
        ShaderUtil.smooth.setUniformf("location", (float)((double)x * mc.getMainWindow().getGuiScaleFactor()), (float)((double)mc.getMainWindow().getHeight() - (double)height * mc.getMainWindow().getGuiScaleFactor() - (double)y * mc.getMainWindow().getGuiScaleFactor()));
        ShaderUtil.smooth.setUniformf("rectSize", (double)width * mc.getMainWindow().getGuiScaleFactor(), (double)height * mc.getMainWindow().getGuiScaleFactor());
        ShaderUtil.smooth.setUniformf("radius", (double)radius * mc.getMainWindow().getGuiScaleFactor());
        ShaderUtil.smooth.setUniform("blur", 0);
        ShaderUtil.smooth.setUniform("color", ColorUtils.rgba(color));
        DisplayUtils.drawQuads(x, y, width, height, 7);
        ShaderUtil.smooth.detach();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawCircle(float x, float y, float radius, int color) {
        DisplayUtils.drawRoundedRect(x - radius / 2.0f, y - radius / 2.0f, radius, radius, radius / 2.0f, color);
    }

    public static void drawShadowCircle(float x, float y, float radius, int color) {
        DisplayUtils.drawShadow(x - radius / 2.0f, y - radius / 2.0f, radius, radius, (int)radius, color);
    }

    public static void drawQuads(float x, float y, float width, float height, int glQuads) {
        buffer.begin(glQuads, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, (double)y, 0.0).tex(0.0f, 0.0f).endVertex();
        buffer.pos(x, (double)(y + height), 0.0).tex(0.0f, 1.0f).endVertex();
        buffer.pos(x + width, (double)(y + height), 0.0).tex(1.0f, 1.0f).endVertex();
        buffer.pos(x + width, (double)y, 0.0).tex(1.0f, 0.0f).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void drawBox(double x, double y, double width, double height, double size, int color) {
        DisplayUtils.drawRectBuilding(x + size, y, width - size, y + size, color);
        DisplayUtils.drawRectBuilding(x, y, x + size, height, color);
        DisplayUtils.drawRectBuilding(width - size, y, width, height, color);
        DisplayUtils.drawRectBuilding(x + size, height - size, width - size, height, color);
    }

    public static void drawBoxTest(double x, double y, double width, double height, double size, Vector4i colors) {
        DisplayUtils.drawMCHorizontalBuilding(x + size, y, width - size, y + size, colors.x, colors.z);
        DisplayUtils.drawMCVerticalBuilding(x, y, x + size, height, colors.z, colors.x);
        DisplayUtils.drawMCVerticalBuilding(width - size, y, width, height, colors.x, colors.z);
        DisplayUtils.drawMCHorizontalBuilding(x + size, height - size, width - size, height, colors.z, colors.x);
    }

    public static void drawImageMultiColor(MatrixStack stack, ResourceLocation image, double x, double y, double z, double width, double height, int color1, int color2, int color3, int color4) {
        Minecraft minecraft = Minecraft.getInstance();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        GL11.glShadeModel(7425);
        GL11.glAlphaFunc(516, 0.0f);
        minecraft.getTextureManager().bindTexture(image);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
        bufferBuilder.pos(stack.getLast().getMatrix(), (float)x, (float)(y + height), (float)z).color(color1 >> 16 & 0xFF, color1 >> 8 & 0xFF, color1 & 0xFF, color1 >>> 24).tex(0.0f, 0.99f).lightmap(0, 240).endVertex();
        bufferBuilder.pos(stack.getLast().getMatrix(), (float)(x + width), (float)(y + height), (float)z).color(color2 >> 16 & 0xFF, color2 >> 8 & 0xFF, color2 & 0xFF, color2 >>> 24).tex(1.0f, 0.99f).lightmap(0, 240).endVertex();
        bufferBuilder.pos(stack.getLast().getMatrix(), (float)(x + width), (float)y, (float)z).color(color3 >> 16 & 0xFF, color3 >> 8 & 0xFF, color3 & 0xFF, color3 >>> 24).tex(1.0f, 0.0f).lightmap(0, 240).endVertex();
        bufferBuilder.pos(stack.getLast().getMatrix(), (float)x, (float)y, (float)z).color(color4 >> 16 & 0xFF, color4 >> 8 & 0xFF, color4 & 0xFF, color4 >>> 24).tex(0.0f, 0.0f).lightmap(0, 240).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
    }

    public static void drawImageAlpha(ResourceLocation resourceLocation, float x, float y, float width, float height, int color) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        mc.getTextureManager().bindTexture(resourceLocation);
        DisplayUtils.quads(x, y, width, height, 7, color);
        RenderSystem.shadeModel(7424);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.popMatrix();
    }

    public static void drawImageAlpha(ResourceLocation resourceLocation, float x, float y, float width, float height, Vector4i color) {
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(770, 1, 0, 1);
        mc.getTextureManager().bindTexture(resourceLocation);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(x, (double)y, 0.0).tex(0.0f, 0.99f).lightmap(0, 240).color(color.x).endVertex();
        buffer.pos(x, (double)(y + height), 0.0).tex(1.0f, 0.99f).lightmap(0, 240).color(color.y).endVertex();
        buffer.pos(x + width, (double)(y + height), 0.0).tex(1.0f, 0.0f).lightmap(0, 240).color(color.z).endVertex();
        buffer.pos(x + width, (double)y, 0.0).tex(0.0f, 0.0f).lightmap(0, 240).color(color.w).endVertex();
        tessellator.draw();
        RenderSystem.defaultBlendFunc();
        GlStateManager.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
    }

    public static final class FrameBuffer {
        public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
            return FrameBuffer.createFrameBuffer(framebuffer, false);
        }

        public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
            if (FrameBuffer.needsNewFramebuffer(framebuffer)) {
                if (framebuffer != null) {
                    framebuffer.deleteFramebuffer();
                }
                int frameBufferWidth = IMinecraft.mc.getMainWindow().getFramebufferWidth();
                int frameBufferHeight = IMinecraft.mc.getMainWindow().getFramebufferHeight();
                return new Framebuffer(frameBufferWidth, frameBufferHeight, depth);
            }
            return framebuffer;
        }

        public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
            return framebuffer == null || framebuffer.framebufferWidth != IMinecraft.mc.getMainWindow().getFramebufferWidth() || framebuffer.framebufferHeight != IMinecraft.mc.getMainWindow().getFramebufferHeight();
        }

        private FrameBuffer() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }
}

