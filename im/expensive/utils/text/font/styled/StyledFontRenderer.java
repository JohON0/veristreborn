/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.text.font.styled;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.text.font.Wrapper;
import im.expensive.utils.text.font.styled.StyledFont;
import java.awt.Color;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public final class StyledFontRenderer
implements Wrapper {
    public static final String STYLE_CODES = "0123456789abcdefklmnor";
    public static final int[] COLOR_CODES = new int[32];

    public static float drawString(MatrixStack matrices, StyledFont font, String text, double x, double y, int color) {
        return StyledFontRenderer.renderString(matrices, font, text, x, y, false, color);
    }

    public static float drawString(MatrixStack matrices, StyledFont font, ITextComponent text, double x, double y, int color) {
        return StyledFontRenderer.renderString(matrices, font, text, x, y, false, color);
    }

    public static float drawShadowedString(MatrixStack matrices, StyledFont font, ITextComponent text, double x, double y, int color) {
        return StyledFontRenderer.drawShadowITextComponentString(matrices, font, text, x, y, color, StyledFontRenderer.getShadowColor(color));
    }

    public static float drawShadowITextComponentString(MatrixStack matrices, StyledFont font, ITextComponent text, double x, double y, int color, int shadowColor) {
        StyledFontRenderer.renderString(matrices, font, text, x + 1.0, y, true, shadowColor);
        return StyledFontRenderer.renderString(matrices, font, text, x, y - 1.0, false, color) + 1.0f;
    }

    public static float drawCenteredXString(MatrixStack matrices, StyledFont font, String text, double x, double y, int color) {
        return StyledFontRenderer.renderString(matrices, font, text, x - (double)(font.getWidth(text) / 2.0f), y, false, color);
    }

    public static void drawCenteredString(MatrixStack matrixStack, StyledFont font, ITextComponent text, double x, double y, int color) {
        StyledFontRenderer.renderString(matrixStack, font, text, x - (double)(font.getWidth(text.getString()) / 2.0f), y, false, color);
    }

    public static void drawCenteredString(MatrixStack matrixStack, StyledFont font, String text, double x, double y, int color) {
        StyledFontRenderer.renderString(matrixStack, font, text, x - (double)(font.getWidth(text) / 2.0f), y, false, color);
    }

    public static float drawCenteredYString(MatrixStack matrices, StyledFont font, String text, double x, double y, int color) {
        return StyledFontRenderer.renderString(matrices, font, text, x, y + (double)(font.getLifting() / 2.0f) + 0.5, false, color);
    }

    public static float drawCenteredXYString(MatrixStack matrices, StyledFont font, String text, double x, double y, int color) {
        return StyledFontRenderer.renderString(matrices, font, text, x - (double)(font.getWidth(text) / 2.0f), y + (double)(font.getLifting() / 2.0f) + 0.5, false, color);
    }

    public static float drawShadowedString(MatrixStack matrices, StyledFont font, String text, double x, double y, int color) {
        return StyledFontRenderer.renderStringWithShadow(matrices, font, text, x, y, color, StyledFontRenderer.getShadowColor(color));
    }

    private static float renderStringWithShadow(MatrixStack matrices, StyledFont font, String text, double x, double y, int color, int shadowColor) {
        StyledFontRenderer.renderString(matrices, font, text, x + 1.0, y, true, shadowColor);
        return StyledFontRenderer.renderString(matrices, font, text, x, y - 1.0, false, color) + 1.0f;
    }

    private static float renderString(MatrixStack matrices, StyledFont font, String text, double x, double y, boolean shadow, int color) {
        float startPos;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        float posX = startPos = (float)x * 2.0f;
        float posY = (float)(y -= 3.0) * 2.0f;
        float[] rgb = ColorUtils.rgba(color);
        float red = rgb[0];
        float green = rgb[1];
        float blue = rgb[2];
        float alpha = rgb[3];
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        matrices.push();
        matrices.scale(0.5f, 0.5f, 1.0f);
        Matrix4f matrix = matrices.getLast().getMatrix();
        int length = text.length();
        String lowerCaseText = text.toLowerCase();
        for (int i = 0; i < length; ++i) {
            char c0 = text.charAt(i);
            if (c0 == '\u00a7' && i + 1 < length && STYLE_CODES.indexOf(lowerCaseText.charAt(i + 1)) != -1) {
                int i1 = STYLE_CODES.indexOf(lowerCaseText.charAt(i + 1));
                if (i1 < 16) {
                    if (shadow) {
                        i1 += 16;
                    }
                    int j1 = COLOR_CODES[i1];
                    red = (float)(j1 >> 16 & 0xFF) / 255.0f;
                    green = (float)(j1 >> 8 & 0xFF) / 255.0f;
                    blue = (float)(j1 & 0xFF) / 255.0f;
                }
                ++i;
                continue;
            }
            posX += font.renderGlyph(matrix, c0, posX, posY, false, false, red, green, blue, alpha);
        }
        matrices.pop();
        GlStateManager.disableBlend();
        return (posX - startPos) / 2.0f;
    }

    public static float renderStringGradient(MatrixStack matrices, StyledFont font, ITextComponent text, double x, double y, boolean shadow, int color) {
        float startPos;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        float posX = startPos = (float)x * 2.0f;
        float posY = (float)(y -= 3.0) * 2.0f;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        matrices.push();
        matrices.scale(0.5f, 0.5f, 1.0f);
        Matrix4f matrix = matrices.getLast().getMatrix();
        float red = 1.0f;
        float green = 1.0f;
        float blue = 1.0f;
        for (ITextComponent textComponent : text.getSiblings()) {
            if (textComponent.getSiblings().isEmpty()) {
                Object texto = !text.getSiblings().get(0).getString().isEmpty() ? " " + textComponent.getString() : textComponent.getString();
                int length = ((String)texto).length();
                String lowerCaseText = ((String)texto).toLowerCase();
                for (int i = 0; i < length; ++i) {
                    char c0 = ((String)texto).charAt(i);
                    if (c0 == '\u00a7' && i + 1 < length && STYLE_CODES.indexOf(lowerCaseText.charAt(i + 1)) != -1) {
                        int i1 = STYLE_CODES.indexOf(lowerCaseText.charAt(i + 1));
                        if (i1 < 16) {
                            if (shadow) {
                                i1 += 16;
                            }
                            int j1 = COLOR_CODES[i1];
                            red = (float)(j1 >> 16 & 0xFF) / 255.0f;
                            green = (float)(j1 >> 8 & 0xFF) / 255.0f;
                            blue = (float)(j1 & 0xFF) / 255.0f;
                        }
                        ++i;
                        continue;
                    }
                    System.out.println(red + " " + green + " " + blue);
                    posX += font.renderGlyph(matrix, c0, posX, posY, false, false, red, green, blue, 1.0f);
                }
            }
            for (ITextComponent textComponent1 : textComponent.getSiblings()) {
                if (textComponent1.getString().isEmpty()) continue;
                char c0 = textComponent1.getString().charAt(0);
                float r1 = 1.0f;
                float r2 = 1.0f;
                float r3 = 1.0f;
                float r4 = 1.0f;
                if (textComponent1.getStyle().getColor() != null) {
                    float[] rgb = ColorUtils.rgba(textComponent1.getStyle().getColor().getColor());
                    r1 = rgb[0];
                    r2 = rgb[1];
                    r3 = rgb[2];
                    r4 = 1.0f;
                }
                posX += font.renderGlyph(matrix, c0, posX, posY, false, false, r1, r2, r3, r4);
            }
        }
        matrices.pop();
        GlStateManager.disableBlend();
        return (posX - startPos) / 2.0f;
    }

    private static float renderString(MatrixStack matrices, StyledFont font, ITextComponent text, double x, double y, boolean shadow, int color) {
        float startPos;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        float posX = startPos = (float)x * 2.0f;
        float posY = (float)(y -= 3.0) * 2.0f;
        float red = 1.0f;
        float green = 1.0f;
        float blue = 1.0f;
        float alpha = 1.0f;
        boolean bold = false;
        boolean italic = false;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        matrices.push();
        matrices.scale(0.5f, 0.5f, 1.0f);
        Matrix4f matrix = matrices.getLast().getMatrix();
        for (int i = 0; i < text.getString().length(); ++i) {
            char c0;
            if (i >= text.getSiblings().size()) {
                char c02 = text.getString().charAt(i);
                if (c02 == '\u00a7' && i + 1 < text.getString().length() && STYLE_CODES.indexOf(text.getString().toLowerCase().charAt(i + 1)) != -1) {
                    int i1 = STYLE_CODES.indexOf(text.getString().toLowerCase().charAt(i + 1));
                    if (i1 < 16) {
                        if (shadow) {
                            i1 += 16;
                        }
                        int j1 = COLOR_CODES[i1];
                        red = (float)(j1 >> 16 & 0xFF) / 255.0f;
                        green = (float)(j1 >> 8 & 0xFF) / 255.0f;
                        blue = (float)(j1 & 0xFF) / 255.0f;
                    }
                    ++i;
                    continue;
                }
                posX += font.renderGlyph(matrix, c02, posX, posY, false, false, red, green, blue, alpha);
                continue;
            }
            ITextComponent c = text.getSiblings().get(i);
            if (c.getString().isEmpty()) {
                c0 = text.getString().charAt(i);
                if (c0 == '\u00a7' && i + 1 < text.getString().length() && STYLE_CODES.indexOf(text.getString().toLowerCase().charAt(i + 1)) != -1) {
                    int i1 = STYLE_CODES.indexOf(text.getString().toLowerCase().charAt(i + 1));
                    if (i1 < 16) {
                        if (shadow) {
                            i1 += 16;
                        }
                        int j1 = COLOR_CODES[i1];
                        red = (float)(j1 >> 16 & 0xFF) / 255.0f;
                        green = (float)(j1 >> 8 & 0xFF) / 255.0f;
                        blue = (float)(j1 & 0xFF) / 255.0f;
                    }
                    ++i;
                    continue;
                }
                posX += font.renderGlyph(matrix, c0, posX, posY, false, false, red, green, blue, alpha);
                continue;
            }
            c0 = c.getString().charAt(0);
            if (c.getStyle().getColor() != null) {
                int col = c.getStyle().getColor().getColor();
                red = (float)(col >> 16 & 0xFF) / 255.0f;
                green = (float)(col >> 8 & 0xFF) / 255.0f;
                blue = (float)(col & 0xFF) / 255.0f;
            }
            float f = font.renderGlyph(matrix, c0, posX, posY, bold, italic, red, green, blue, alpha);
            posX += f;
        }
        matrices.pop();
        GlStateManager.disableBlend();
        return (posX - startPos) / 2.0f;
    }

    public static int getShadowColor(int color) {
        return new Color((color & 0xFCFCFC) >> 2 | color & 0xFF000000).getRGB();
    }

    static {
        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;
            if (i == 6) {
                k += 85;
            }
            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }
            StyledFontRenderer.COLOR_CODES[i] = (k & 0xFF) << 16 | (l & 0xFF) << 8 | i1 & 0xFF;
        }
    }
}

