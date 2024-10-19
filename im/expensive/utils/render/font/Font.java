/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render.font;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.FontData;
import im.expensive.utils.render.font.MsdfFont;
import im.expensive.utils.shader.ShaderUtil;
import java.awt.Color;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class Font
implements IMinecraft {
    private final String atlas;
    private final String data;
    private final MsdfFont font;

    public Font(String atlas, String data) {
        this.atlas = atlas;
        this.data = data;
        this.font = MsdfFont.builder().withAtlas(atlas).withData(data).build();
    }

    public void drawText(MatrixStack stack, String text, float x, float y, int color, float size) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderUtil shader = ShaderUtil.textShader;
        FontData.AtlasData atlas = this.font.getAtlas();
        shader.attach();
        shader.setUniform("Sampler", 0);
        shader.setUniform("EdgeStrength", 0.5f);
        shader.setUniform("TextureSize", atlas.width(), atlas.height());
        shader.setUniform("Range", atlas.range());
        shader.setUniform("Thickness", 0.0f);
        shader.setUniform("Outline", 0);
        shader.setUniform("OutlineThickness", 0.0f);
        shader.setUniform("OutlineColor", 1.0f, 1.0f, 1.0f, 1.0f);
        shader.setUniform("color", ColorUtils.rgba(color));
        this.font.bind();
        GlStateManager.enableBlend();
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, 0.0f, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
        Tessellator.getInstance().draw();
        this.font.unbind();
        shader.detach();
    }

    public void drawText(MatrixStack stack, ITextComponent text, float x, float y, float size, int alpha) {
        float offset = 0.0f;
        for (ITextComponent it : text.getSiblings()) {
            for (ITextComponent it1 : it.getSiblings()) {
                String draw = it1.getString();
                if (it1.getStyle().getColor() != null) {
                    this.drawText(stack, draw, x + offset, y, ColorUtils.setAlpha(ColorUtils.toColor(it1.getStyle().getColor().getHex()), alpha), size);
                } else {
                    this.drawText(stack, draw, x + offset, y, ColorUtils.setAlpha(Color.GRAY.getRGB(), alpha), size);
                }
                offset += this.getWidth(draw, size);
            }
            if (it.getSiblings().size() > 1) continue;
            String draw = TextFormatting.getTextWithoutFormattingCodes(it.getString());
            this.drawText(stack, draw, x + offset, y, ColorUtils.setAlpha(it.getStyle().getColor() == null ? Color.GRAY.getRGB() : it.getStyle().getColor().getColor(), alpha), size);
            offset += this.getWidth(draw, size);
        }
        if (text.getSiblings().isEmpty()) {
            String draw = TextFormatting.getTextWithoutFormattingCodes(text.getString());
            this.drawText(stack, draw, x + offset, y, ColorUtils.setAlpha(text.getStyle().getColor() == null ? Color.GRAY.getRGB() : text.getStyle().getColor().getColor(), alpha), size);
            offset += this.getWidth(draw, size);
        }
    }

    public float getWidth(ITextComponent text, float size) {
        float offset = 0.0f;
        for (ITextComponent it : text.getSiblings()) {
            for (ITextComponent it1 : it.getSiblings()) {
                String draw = it1.getString();
                offset += this.getWidth(draw, size);
            }
            if (it.getSiblings().size() > 1) continue;
            String draw = TextFormatting.getTextWithoutFormattingCodes(it.getString());
            offset += this.getWidth(draw, size);
        }
        if (text.getSiblings().isEmpty()) {
            String draw = TextFormatting.getTextWithoutFormattingCodes(text.getString());
            offset += this.getWidth(draw, size);
        }
        return offset;
    }

    public void drawTextBuilding(MatrixStack stack, String text, float x, float y, int color, float size) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderUtil shader = ShaderUtil.textShader;
        FontData.AtlasData atlas = this.font.getAtlas();
        shader.attach();
        shader.setUniform("Sampler", 0);
        shader.setUniform("EdgeStrength", 0.5f);
        shader.setUniform("TextureSize", atlas.width(), atlas.height());
        shader.setUniform("Range", atlas.range());
        shader.setUniform("Thickness", 0.0f);
        shader.setUniform("Outline", 0);
        shader.setUniform("OutlineThickness", 0.0f);
        shader.setUniform("OutlineColor", 1.0f, 1.0f, 1.0f, 1.0f);
        shader.setUniform("color", ColorUtils.rgba(color));
        this.font.bind();
        GlStateManager.enableBlend();
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, 0.0f, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
        this.font.unbind();
        shader.detach();
    }

    public void drawCenteredText(MatrixStack stack, String text, float x, float y, int color, float size) {
        this.drawText(stack, text, x - this.getWidth(text, size) / 2.0f, y, color, size);
    }

    public void drawCenteredText(MatrixStack stack, ITextComponent text, float x, float y, float size) {
        this.drawText(stack, text, x - this.getWidth(text, size) / 2.0f, y, size, 255);
    }

    public void drawCenteredTextWithOutline(MatrixStack stack, String text, float x, float y, int color, float size) {
        this.drawTextWithOutline(stack, text, x - this.getWidth(text, size) / 2.0f, y, color, size, 0.05f);
    }

    public void drawCenteredTextEmpty(MatrixStack stack, String text, float x, float y, int color, float size) {
        this.drawEmpty(stack, text, x - this.getWidth(text, size) / 2.0f, y, size, color, 0.0f);
    }

    public void drawCenteredTextEmptyOutline(MatrixStack stack, String text, float x, float y, int color, float size) {
        this.drawEmptyWithOutline(stack, text, x - this.getWidth(text, size) / 2.0f, y, size, color, 0.0f);
    }

    public void drawCenteredText(MatrixStack stack, String text, float x, float y, int color, float size, float thickness) {
        this.drawText(stack, text, x - this.getWidth(text, size, thickness) / 2.0f, y, color, size, thickness);
    }

    public void drawText(MatrixStack stack, String text, float x, float y, int color, float size, float thickness) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderUtil shader = ShaderUtil.textShader;
        FontData.AtlasData atlas = this.font.getAtlas();
        shader.attach();
        shader.setUniform("Sampler", 0);
        shader.setUniform("EdgeStrength", 0.5f);
        shader.setUniform("TextureSize", atlas.width(), atlas.height());
        shader.setUniform("Range", atlas.range());
        shader.setUniform("Thickness", thickness);
        shader.setUniform("color", ColorUtils.rgba(color));
        shader.setUniform("Outline", 0);
        shader.setUniform("OutlineThickness", 0.0f);
        shader.setUniform("OutlineColor", 1.0f, 1.0f, 1.0f, 1.0f);
        this.font.bind();
        GlStateManager.enableBlend();
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, thickness, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
        Tessellator.getInstance().draw();
        this.font.unbind();
        shader.detach();
    }

    public void drawTextWithOutline(MatrixStack stack, String text, float x, float y, int color, float size, float thickness) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderUtil shader = ShaderUtil.textShader;
        FontData.AtlasData atlas = this.font.getAtlas();
        shader.attach();
        shader.setUniform("Sampler", 0);
        shader.setUniform("EdgeStrength", 0.5f);
        shader.setUniform("TextureSize", atlas.width(), atlas.height());
        shader.setUniform("Range", atlas.range());
        shader.setUniform("Thickness", thickness);
        shader.setUniform("color", ColorUtils.rgba(color));
        shader.setUniform("Outline", 1);
        shader.setUniform("OutlineThickness", 0.2f);
        shader.setUniform("OutlineColor", 0.0f, 0.0f, 0.0f, 1.0f);
        this.font.bind();
        GlStateManager.enableBlend();
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, thickness, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
        Tessellator.getInstance().draw();
        this.font.unbind();
        shader.detach();
    }

    public void init(float thickness, float smoothness) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderUtil shader = ShaderUtil.textShader;
        FontData.AtlasData atlas = this.font.getAtlas();
        shader.attach();
        shader.setUniform("Sampler", 0);
        shader.setUniform("EdgeStrength", smoothness);
        shader.setUniform("TextureSize", atlas.width(), atlas.height());
        shader.setUniform("Range", atlas.range());
        shader.setUniform("Thickness", thickness);
        shader.setUniform("Outline", 0);
        shader.setUniform("OutlineThickness", 0.0f);
        shader.setUniform("OutlineColor", 1.0f, 1.0f, 1.0f, 1.0f);
        this.font.bind();
        GlStateManager.enableBlend();
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
    }

    public void drawEmpty(MatrixStack stack, String text, float x, float y, float size, int color, float thickness) {
        ShaderUtil shader = ShaderUtil.textShader;
        shader.setUniform("color", ColorUtils.rgba(color));
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, thickness, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
    }

    public void drawEmptyWithOutline(MatrixStack stack, String text, float x, float y, float size, int color, float thickness) {
        ShaderUtil shader = ShaderUtil.textShader;
        shader.setUniform("Outline", 1);
        shader.setUniform("OutlineThickness", 0.2f);
        shader.setUniform("OutlineColor", 0.0f, 0.0f, 0.0f, 1.0f);
        shader.setUniform("color", ColorUtils.rgba(color));
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, thickness, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
    }

    public void end() {
        Tessellator.getInstance().draw();
        this.font.unbind();
        ShaderUtil shader = ShaderUtil.textShader;
        shader.detach();
    }

    public float getWidth(String text, float size) {
        return this.font.getWidth(text, size);
    }

    public float getWidth(String text, float size, float thickness) {
        return this.font.getWidth(text, size, thickness);
    }

    public float getHeight(float size) {
        return size;
    }
}

