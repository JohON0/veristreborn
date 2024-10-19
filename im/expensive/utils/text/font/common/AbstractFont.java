/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.text.font.common;

import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.utils.text.font.ClientFonts;
import im.expensive.utils.text.font.TextureHelper;
import im.expensive.utils.text.font.Wrapper;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

public abstract class AbstractFont
implements Wrapper {
    protected final Map<Character, Glyph> glyphs = new HashMap<Character, Glyph>();
    protected int texId;
    protected int imgWidth;
    protected int imgHeight;
    protected float fontHeight;
    protected String fontName;
    protected boolean antialiasing;

    public abstract float getStretching();

    public abstract float getSpacing();

    public abstract float getLifting();

    public float getFontHeight() {
        return this.fontHeight;
    }

    public final String getFontName() {
        return this.fontName;
    }

    protected final void setTexture(BufferedImage img) {
        this.texId = TextureHelper.loadTexture(img);
    }

    public final void bindTex() {
        GlStateManager.bindTexture(this.texId);
    }

    public final void unbindTex() {
        GlStateManager.bindTexture(0);
    }

    public static final Font getFont(String fileName, int style, int size) {
        String path = "/assets/minecraft/eva/fonts/normal/".concat(fileName);
        Font font = null;
        try {
            font = Font.createFont(0, Objects.requireNonNull(ClientFonts.class.getResourceAsStream(path))).deriveFont(style, size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return font;
    }

    public static final Font getFontWindows(String fileName, int style, int size) {
        String path = "/assets/minecraft/eva/fonts/normal/".concat(fileName);
        Font font = null;
        try {
            font = Font.createFont(0, Files.newInputStream(new File("C:/Windows/ClientFonts/" + fileName).toPath(), new OpenOption[0])).deriveFont(style, size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return font;
    }

    public final Graphics2D setupGraphics(BufferedImage img, Font font) {
        Graphics2D graphics = img.createGraphics();
        graphics.setFont(font);
        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, this.imgWidth, this.imgHeight);
        graphics.setColor(Color.WHITE);
        if (this.antialiasing) {
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        return graphics;
    }

    public float renderGlyph(Matrix4f matrix, char c, float x, float y, float red, float green, float blue, float alpha) {
        Glyph glyph = this.glyphs.get(Character.valueOf(c));
        if (glyph == null) {
            return 0.0f;
        }
        float pageX = (float)glyph.x / (float)this.imgWidth;
        float pageY = (float)glyph.y / (float)this.imgHeight;
        float pageWidth = (float)glyph.width / (float)this.imgWidth;
        float pageHeight = (float)glyph.height / (float)this.imgHeight;
        float width = (float)glyph.width + this.getStretching();
        float height = glyph.height;
        BUILDER.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        BUILDER.pos(matrix, x, y + height, 0.0f).color(red, green, blue, alpha).tex(pageX, pageY + pageHeight).endVertex();
        BUILDER.pos(matrix, x + width, y + height, 0.0f).color(red, green, blue, alpha).tex(pageX + pageWidth, pageY + pageHeight).endVertex();
        BUILDER.pos(matrix, x + width, y, 0.0f).color(red, green, blue, alpha).tex(pageX + pageWidth, pageY).endVertex();
        BUILDER.pos(matrix, x, y, 0.0f).color(red, green, blue, alpha).tex(pageX, pageY).endVertex();
        TESSELLATOR.draw();
        return width + this.getSpacing();
    }

    public float getWidth(char ch) {
        Glyph glyph = this.glyphs.get(Character.valueOf(ch));
        return glyph != null ? (float)glyph.width + this.getStretching() : 0.0f;
    }

    public float getWidth(String text) {
        if (text == null) {
            return 0.0f;
        }
        float width = 0.0f;
        float sp = this.getSpacing();
        for (int i = 0; i < text.length(); ++i) {
            width += this.getWidth(text.charAt(i)) + sp;
        }
        return (width - sp) / 2.0f;
    }

    public class Glyph {
        public int x;
        public int y;
        public int width;
        public int height;
    }
}

