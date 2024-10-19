/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.text.font.styled;

import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.utils.text.font.common.AbstractFont;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Locale;
import net.minecraft.util.math.vector.Matrix4f;

public final class GlyphPage
extends AbstractFont {
    private final int italicSpacing;
    private final float stretching;
    private final float spacing;
    private final float lifting;

    public GlyphPage(Font font, char[] chars, float stretching, float spacing, float lifting, boolean antialiasing) {
        FontRenderContext fontRenderContext = new FontRenderContext(font.getTransform(), true, true);
        double maxWidth = 0.0;
        double maxHeight = 0.0;
        for (char c : chars) {
            Rectangle2D bound = font.getStringBounds(Character.toString(c), fontRenderContext);
            maxWidth = Math.max(maxWidth, bound.getWidth());
            maxHeight = Math.max(maxHeight, bound.getHeight());
        }
        this.italicSpacing = font.isItalic() ? 5 : 0;
        int d = (int)Math.ceil(Math.sqrt((maxHeight + 2.0) * (maxWidth + 2.0 + (double)this.italicSpacing) * (double)chars.length));
        this.fontName = font.getFontName(Locale.ENGLISH);
        this.fontHeight = (float)(maxHeight / 2.0);
        this.imgHeight = d;
        this.imgWidth = d;
        this.stretching = stretching;
        this.spacing = spacing;
        this.lifting = lifting;
        this.antialiasing = antialiasing;
        BufferedImage image = new BufferedImage(this.imgWidth, this.imgHeight, 2);
        Graphics2D graphics = this.setupGraphics(image, font);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int posX = 1;
        int posY = 2;
        for (char c : chars) {
            AbstractFont.Glyph glyph = new AbstractFont.Glyph();
            Rectangle2D bounds = fontMetrics.getStringBounds(Character.toString(c), graphics);
            glyph.width = (int)bounds.getWidth() + 1 + this.italicSpacing;
            glyph.height = (int)bounds.getHeight() + 2;
            if (posX + glyph.width >= this.imgWidth) {
                posX = 1;
                posY = (int)((double)posY + (maxHeight + (double)fontMetrics.getDescent() + 2.0));
            }
            glyph.x = posX;
            glyph.y = posY;
            graphics.drawString(Character.toString(c), posX, posY + fontMetrics.getAscent());
            posX += glyph.width + 4;
            this.glyphs.put(Character.valueOf(c), glyph);
        }
        RenderSystem.recordRenderCall(() -> this.setTexture(image));
    }

    @Override
    public float renderGlyph(Matrix4f matrix, char c, float x, float y, float red, float green, float blue, float alpha) {
        this.bindTex();
        float w = super.renderGlyph(matrix, c, x, y, red, green, blue, alpha) - (float)this.italicSpacing;
        this.unbindTex();
        return w;
    }

    @Override
    public float getStretching() {
        return this.stretching;
    }

    @Override
    public float getSpacing() {
        return this.spacing;
    }

    @Override
    public float getLifting() {
        return this.fontHeight + this.lifting;
    }
}

