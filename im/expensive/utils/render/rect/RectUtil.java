/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render.rect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.render.color.ColorUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

public class RectUtil
implements IMinecraft {
    public static RectUtil instance = new RectUtil();
    public static final List<Vec2fColored> VERTEXES_COLORED = new ArrayList<Vec2fColored>();
    public final List<Vector2f> VERTEXES = new ArrayList<Vector2f>();
    int[] LEFT_UP = new int[]{-90, 0};
    int[] RIGHT_UP = new int[]{0, 90};
    int[] RIGHT_DOWN = new int[]{90, 180};
    int[] LEFT_DOWN = new int[]{180, 270};

    public static void bindTexture(ResourceLocation location) {
        mc.getTextureManager().bindTexture(location);
    }

    public Vec2fColored getOfVec3f(Vector2f vec2f, int color) {
        return new Vec2fColored(vec2f.x, vec2f.y, color);
    }

    public void drawRect(MatrixStack matrix, float x, float y, float x2, float y2, int color, boolean bloom) {
        RectUtil.drawRect(matrix, x, y, x2, y2, color, color, color, color, bloom, false);
    }

    public static void drawSmoothRect(MatrixStack matrixStack, double left, double top, double right, double bottom, int color) {
        RectUtil.drawRect(left, top, right, bottom, color, matrixStack);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RectUtil.drawRect(left * 2.0 - 1.0, top * 2.0, left * 2.0, bottom * 2.0 - 1.0, color, matrixStack);
        RectUtil.drawRect(left * 2.0, top * 2.0 - 1.0, right * 2.0, top * 2.0, color, matrixStack);
        RectUtil.drawRect(right * 2.0, top * 2.0, right * 2.0 + 1.0, bottom * 2.0 - 1.0, color, matrixStack);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void drawRect(double left, double top, double right, double bottom, int color, MatrixStack matrixStack) {
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
        double finalLeft = left;
        double finalTop = top;
        double finalRight = right;
        double finalBottom = bottom;
        RectUtil.start2Draw(() -> {
            RectUtil.setColor(color);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
            bufferBuilder.pos(matrixStack, finalLeft, finalBottom).endVertex();
            bufferBuilder.pos(matrixStack, finalRight, finalBottom).endVertex();
            bufferBuilder.pos(matrixStack, finalRight, finalTop).endVertex();
            bufferBuilder.pos(matrixStack, finalLeft, finalTop).endVertex();
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);
        });
    }

    public static void start2Draw(Runnable runnable) {
        boolean isEnabled = GL11.glIsEnabled(3042);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        runnable.run();
        if (!isEnabled) {
            GL11.glDisable(3042);
        }
        GL11.glEnable(3553);
        GL11.glEnable(3008);
    }

    public static void setColor(int color) {
        GL11.glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
    }

    public void drawRect(MatrixStack matrix, float x, float y, float x2, float y2, int color) {
        this.drawRect(matrix, x, y, x2, y2, color, false);
    }

    public static void setupOrientationMatrix(MatrixStack matrix, float x, float y, float z) {
        RectUtil.setupOrientationMatrix(matrix, (double)x, (double)y, (double)z);
    }

    public static void setupOrientationMatrix(MatrixStack matrix, double x, double y, double z) {
        float partialTicks = mc.getRenderPartialTicks();
        EntityRendererManager rendererManager = mc.getRenderManager();
        Vector3d renderPos = rendererManager.info.getProjectedView();
        boolean flag = RectUtil.mc.gameSettings.getPointOfView().func_243192_a() || RectUtil.mc.gameSettings.getPointOfView().func_243194_c().func_243193_b();
        matrix.translate(x - renderPos.x, y - renderPos.y, z - renderPos.z);
    }

    public static void drawRect(MatrixStack matrix, float x, float y, float x2, float y2, int color1, int color2, int color3, int color4, boolean bloom, boolean texture) {
        VERTEXES_COLORED.clear();
        VERTEXES_COLORED.add(new Vec2fColored(x, y, color1));
        VERTEXES_COLORED.add(new Vec2fColored(x2, y, color2));
        VERTEXES_COLORED.add(new Vec2fColored(x2, y2, color3));
        VERTEXES_COLORED.add(new Vec2fColored(x, y2, color4));
        RectUtil.drawVertexesList(matrix, VERTEXES_COLORED, 9, texture, bloom);
    }

    public void drawRoundedRectShadowed(MatrixStack matrix, float x, float y, float x2, float y2, float round, float shadowSize, int color1, int color2, int color3, int color4, boolean bloom, boolean sageColor, boolean rect, boolean shadow) {
        float roundMax = Math.max(x2 - x, y2 - y);
        round = Math.max(Math.min(round, roundMax), 0.0f);
        shadowSize = Math.max(shadowSize, 0.0f);
        x += round;
        y += round;
        x2 -= round;
        y2 -= round;
        if (rect) {
            RectUtil.drawRect(matrix, x, y, x2, y2, color1, color2, color3, color4, bloom, false);
            if (round != 0.0f) {
                this.drawLimitersSegments(matrix, x, y, x2, y2, round, 0.0f, color1, color2, color3, color4, false, false, bloom);
                this.drawRoundSegments(matrix, x, y, x2, y2, round, color1, color2, color3, color4, bloom);
            }
        }
        if (shadow && shadowSize > 0.0f) {
            this.drawLimitersSegments(matrix, x - round, y - round, x2 + round, y2 + round, shadowSize, round, color1, color2, color3, color4, sageColor, true, bloom);
            this.drawShadowSegmentsExtract(matrix, x, y, x2, y2, round, shadowSize, color1, color2, color3, color4, sageColor, bloom);
        }
    }

    public void drawShadowSegment(MatrixStack matrix, float x, float y, double radius, int color, boolean sageColor, int[] side, boolean bloom) {
        int color2 = sageColor ? 0 : ColorUtils.reAlphaInt(color, 0);
        this.drawDuadsSegment(matrix, x, y, 0.0, radius, color, color2, bloom, side);
    }

    public void drawShadowSegment(MatrixStack matrix, float x, float y, double radiusRound, double radiusShadow, int color, boolean sageColor, int[] side, boolean bloom) {
        int color2 = sageColor ? 0 : ColorUtils.reAlphaInt(color, 0);
        this.drawDuadsSegment(matrix, x, y, radiusRound, radiusShadow, color, color2, bloom, side);
    }

    public void drawShadowSegment(MatrixStack matrix, float x, float y, double radius, int color, boolean sageColor, int[] side) {
        this.drawShadowSegment(matrix, x, y, radius, color, sageColor, side, false);
    }

    public void drawShadowSegments(MatrixStack matrix, float x, float y, float x2, float y2, double radius, int color1, int color2, int color3, int color4, boolean sageColor, boolean bloom) {
        this.drawShadowSegment(matrix, x, y, radius, color1, sageColor, this.LEFT_UP, bloom);
        this.drawShadowSegment(matrix, x2, y, radius, color2, sageColor, this.RIGHT_UP, bloom);
        this.drawShadowSegment(matrix, x2, y2, radius, color3, sageColor, this.RIGHT_DOWN, bloom);
        this.drawShadowSegment(matrix, x, y2, radius, color4, sageColor, this.LEFT_DOWN, bloom);
    }

    public void drawShadowSegmentsExtract(MatrixStack matrix, float x, float y, float x2, float y2, double radiusStart, double radiusEnd, int color1, int color2, int color3, int color4, boolean sageColor, boolean bloom) {
        this.drawShadowSegment(matrix, x, y, radiusStart, radiusEnd, color1, sageColor, this.LEFT_UP, bloom);
        this.drawShadowSegment(matrix, x2, y, radiusStart, radiusEnd, color2, sageColor, this.RIGHT_UP, bloom);
        this.drawShadowSegment(matrix, x2, y2, radiusStart, radiusEnd, color3, sageColor, this.RIGHT_DOWN, bloom);
        this.drawShadowSegment(matrix, x, y2, radiusStart, radiusEnd, color4, sageColor, this.LEFT_DOWN, bloom);
    }

    public void drawShadowSegments(MatrixStack matrix, float x, float y, float x2, float y2, double radius, int color1, int color2, int color3, int color4, boolean sageColor) {
        this.drawShadowSegments(matrix, x, y, x2, y2, radius, color1, color2, color3, color4, sageColor, false);
    }

    public void drawShadowSegmentsExtract(MatrixStack matrix, float x, float y, float x2, float y2, double radiusStart, double radiusEnd, int color1, int color2, int color3, int color4, boolean sageColor) {
        this.drawShadowSegmentsExtract(matrix, x, y, x2, y2, radiusStart, radiusEnd, color1, color2, color3, color4, sageColor, false);
    }

    public void drawLimitersSegments(MatrixStack matrix, float x, float y, float x2, float y2, float radius, float appendOffsets, int color1, int color2, int color3, int color4, boolean sageColor, boolean retainZero, boolean bloom) {
        int c7;
        int c6;
        int c5;
        int n = retainZero ? (sageColor ? 0 : ColorUtils.reAlphaInt(color1, 0)) : (c5 = color1);
        int n2 = retainZero ? (sageColor ? 0 : ColorUtils.reAlphaInt(color2, 0)) : (c6 = color2);
        int n3 = retainZero ? (sageColor ? 0 : ColorUtils.reAlphaInt(color3, 0)) : (c7 = color3);
        int c8 = retainZero ? (sageColor ? 0 : ColorUtils.reAlphaInt(color4, 0)) : color4;
        RectUtil.drawRect(matrix, x + appendOffsets, y - radius, x2 - appendOffsets, y, c5, c6, color2, color1, bloom, false);
        RectUtil.drawRect(matrix, x + appendOffsets, y2, x2 - appendOffsets, y2 + radius, color4, color3, c7, c8, bloom, false);
        RectUtil.drawRect(matrix, x - radius, y + appendOffsets, x, y2 - appendOffsets, c5, color1, color4, c8, bloom, false);
        RectUtil.drawRect(matrix, x2, y + appendOffsets, x2 + radius, y2 - appendOffsets, color2, c6, c7, color3, bloom, false);
    }

    public void drawRoundSegments(MatrixStack matrix, float x, float y, float x2, float y2, double radius, int color1, int color2, int color3, int color4, boolean bloom) {
        this.drawRoundSegment(matrix, x, y, radius, color1, this.LEFT_UP, bloom);
        this.drawRoundSegment(matrix, x2, y, radius, color2, this.RIGHT_UP, bloom);
        this.drawRoundSegment(matrix, x2, y2, radius, color3, this.RIGHT_DOWN, bloom);
        this.drawRoundSegment(matrix, x, y2, radius, color4, this.LEFT_DOWN, bloom);
    }

    public void drawRoundSegment(MatrixStack matrix, float x, float y, double radius, int color, int[] side, boolean bloom) {
        this.drawDuadsSegment(matrix, x, y, 0.0, radius, color, color, bloom, side);
    }

    public void drawRoundSegment(MatrixStack matrix, float x, float y, double radius, int color, int[] side) {
        this.drawDuadsSegment(matrix, x, y, 0.0, radius, color, color, false, side);
    }

    public void drawDuadsSegment(MatrixStack matrix, float x, float y, double radius, double expand, int color, int color2, boolean bloom, int[] side) {
        VERTEXES_COLORED.clear();
        int index = 0;
        for (Vector2f vec2f : this.generateRadiusCircledVertexes(matrix, x, y, radius, radius + expand, side[0], side[1], 9.0, true)) {
            VERTEXES_COLORED.add(this.getOfVec3f(vec2f, index % 2 == 1 ? color2 : color));
            ++index;
        }
        RectUtil.drawVertexesList(matrix, VERTEXES_COLORED, 5, false, bloom);
    }

    public List<Vector2f> generateRadiusCircledVertexes(MatrixStack matrix, float x, float y, double radius1, double radius2, double startRadius, double endRadius, double step, boolean doublepart) {
        this.VERTEXES.clear();
        for (double radius = startRadius; radius <= endRadius; radius += step) {
            if (radius > endRadius) {
                radius = endRadius;
            }
            float x1 = (float)(Math.sin(Math.toRadians(radius)) * radius1);
            float y1 = (float)(-Math.cos(Math.toRadians(radius)) * radius1);
            this.VERTEXES.add(new Vector2f(x + x1, y + y1));
            if (!doublepart) continue;
            x1 = (float)(Math.sin(Math.toRadians(radius)) * radius2);
            y1 = (float)(-Math.cos(Math.toRadians(radius)) * radius2);
            this.VERTEXES.add(new Vector2f(x + x1, y + y1));
        }
        return this.VERTEXES;
    }

    public static void drawVertexesList(MatrixStack matrix, List<Vec2fColored> vec2c, int begin, boolean texture, boolean bloom) {
        RectUtil.setupRenderRect(texture, bloom);
        buffer.begin(begin, texture ? DefaultVertexFormats.POSITION_TEX_COLOR : DefaultVertexFormats.POSITION_COLOR);
        int counter = 0;
        for (Vec2fColored vec : vec2c) {
            float[] rgba = ColorUtils.rgba(vec.getColor());
            buffer.pos(matrix.getLast().getMatrix(), vec.getX(), vec.getY(), 0.0f);
            if (texture) {
                buffer.tex(counter == 0 || counter == 3 ? 0.0f : 1.0f, counter == 0 || counter == 1 ? 0.0f : 1.0f);
            }
            buffer.color(rgba[0], rgba[1], rgba[2], rgba[3]);
            buffer.endVertex();
            ++counter;
        }
        tessellator.draw();
        RectUtil.endRenderRect(bloom);
    }

    public static void setupRenderRect(boolean texture, boolean bloom) {
        if (texture) {
            RenderSystem.enableTexture();
        } else {
            RenderSystem.disableTexture();
        }
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.shadeModel(7425);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, bloom ? GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA : GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.disableAlphaTest();
        GL11.glHint(3155, 4354);
        GL11.glEnable(2832);
    }

    public static void endRenderRect(boolean bloom) {
        RenderSystem.enableAlphaTest();
        if (bloom) {
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        RenderSystem.shadeModel(7424);
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.clearCurrentColor();
    }

    public static RectUtil getInstance() {
        return instance;
    }

    public static class Vec2fColored {
        float x;
        float y;
        int color;

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public int getColor() {
            return this.color;
        }

        public Vec2fColored(float x, float y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    public class Vec3fColored {
        MatrixStack matrix;
        float x;
        float y;
        float z;
        int color;

        public MatrixStack getMatrix() {
            return this.matrix;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public float getZ() {
            return this.z;
        }

        public int getColor() {
            return this.color;
        }

        public Vec3fColored(MatrixStack matrix, float x, float y, float z, int color) {
            this.matrix = matrix;
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
        }
    }

    public class Vec2fMatrix {
        MatrixStack matrix;
        float x;
        float y;
        int color;

        public MatrixStack getMatrix() {
            return this.matrix;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public int getColor() {
            return this.color;
        }

        public Vec2fMatrix(MatrixStack matrix, float x, float y, int color) {
            this.matrix = matrix;
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
}

