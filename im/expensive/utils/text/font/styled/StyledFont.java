/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.text.font.styled;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.text.font.common.AbstractFont;
import im.expensive.utils.text.font.common.Lang;
import im.expensive.utils.text.font.styled.GlyphPage;
import im.expensive.utils.text.font.styled.StyledFontRenderer;
import java.awt.Color;
import java.util.Locale;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;

public final class StyledFont {
    private final GlyphPage regular;

    public StyledFont(String fileName, int size, float stretching, float spacing, float lifting, boolean antialiasing, Lang lang) {
        int[] codes = lang.getCharCodes();
        char[] chars = new char[codes[1] - codes[0] + codes[3] - codes[2]];
        int c = 0;
        for (int d = 0; d <= 2; d += 2) {
            for (int i = codes[d]; i <= codes[d + 1] - 1; ++i) {
                chars[c] = (char)i;
                ++c;
            }
        }
        this.regular = new GlyphPage(AbstractFont.getFont(fileName, 0, size), chars, stretching, spacing, lifting, antialiasing);
    }

    public StyledFont(String fileName, int size, float stretching, float spacing, float lifting, boolean antialiasing, Lang lang, boolean wind) {
        int[] codes = lang.getCharCodes();
        char[] chars = new char[codes[1] - codes[0] + codes[3] - codes[2]];
        int c = 0;
        for (int d = 0; d <= 2; d += 2) {
            for (int i = codes[d]; i <= codes[d + 1] - 1; ++i) {
                chars[c] = (char)i;
                ++c;
            }
        }
        this.regular = new GlyphPage(AbstractFont.getFontWindows(fileName, 0, size), chars, stretching, spacing, lifting, antialiasing);
    }

    public float renderGlyph(Matrix4f matrix, char c, float x, float y, boolean bold, boolean italic, float red, float green, float blue, float alpha) {
        return this.getGlyphPage().renderGlyph(matrix, c, x, y, red, green, blue, alpha);
    }

    public void drawStringWithShadow(MatrixStack matrixStack, ITextComponent text, double x, double y, int color) {
        StyledFontRenderer.drawShadowedString(matrixStack, this, text, x, y, color);
    }

    public void drawString(MatrixStack matrixStack, String text, double x, double y, int color) {
        StyledFontRenderer.drawString(matrixStack, this, text, x, y, color);
    }

    public void drawStringTest(MatrixStack matrixStack, ITextComponent text, double x, double y, int color) {
        StyledFontRenderer.renderStringGradient(matrixStack, this, text, x, y, false, color);
    }

    public void drawString(MatrixStack matrixStack, ITextComponent text, double x, double y, int color) {
        StyledFontRenderer.drawString(matrixStack, this, text, x, y, color);
    }

    public void drawStringWithShadow(MatrixStack matrixStack, String text, double x, double y, int color) {
        StyledFontRenderer.drawShadowedString(matrixStack, this, text, x, y, color);
    }

    public void drawCenteredString(MatrixStack matrixStack, String text, double x, double y, int color) {
        StyledFontRenderer.drawCenteredXString(matrixStack, this, text, x, y, color);
    }

    public void drawCenteredString(MatrixStack matrixStack, ITextComponent text, double x, double y, int color) {
        StyledFontRenderer.drawCenteredString(matrixStack, this, text, x, y, color);
    }

    public void drawStringWithOutline(MatrixStack stack, String text, double x, double y, int color) {
        Color c = new Color(0, 0, 0, 128);
        x = MathUtil.round(x, 0.5);
        y = MathUtil.round(y, 0.5);
        StyledFontRenderer.drawString(stack, this, text, x - 0.5, y, c.getRGB());
        StyledFontRenderer.drawString(stack, this, text, x + 0.5, y, c.getRGB());
        StyledFontRenderer.drawString(stack, this, text, x, y - 0.5, c.getRGB());
        StyledFontRenderer.drawString(stack, this, text, x, y + 0.5, c.getRGB());
        this.drawString(stack, text, x, y, color);
    }

    public void drawCenteredStringWithOutline(MatrixStack stack, String text, double x, double y, int color) {
        this.drawStringWithOutline(stack, text, x - (double)(this.getWidth(text) / 2.0f), y, color);
    }

    public float getWidth(String text) {
        float width = 0.0f;
        for (int i = 0; i < text.length(); ++i) {
            char c0 = text.charAt(i);
            if (c0 == '\u00a7' && i + 1 < text.length() && "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1)) != -1) {
                ++i;
                continue;
            }
            width += this.getGlyphPage().getWidth(c0) + this.regular.getSpacing();
        }
        return (width - this.regular.getSpacing()) / 2.0f;
    }

    private GlyphPage getGlyphPage() {
        return this.regular;
    }

    public float getFontHeight() {
        return this.regular.getFontHeight();
    }

    public float getLifting() {
        return this.regular.getLifting();
    }
}

