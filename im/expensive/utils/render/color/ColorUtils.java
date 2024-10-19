/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render.color;

import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.math.MathUtil;
import java.awt.Color;
import net.minecraft.util.math.MathHelper;

public final class ColorUtils {
    public static final int green = new Color(64, 255, 64).getRGB();
    public static final int yellow = new Color(255, 255, 64).getRGB();
    public static final int orange = new Color(255, 128, 32).getRGB();
    public static final int red = new Color(255, 64, 64).getRGB();

    public static int rgb(int r, int g, int b) {
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    public static int multDark(int c, float brpc) {
        return ColorUtils.getColor((float)ColorUtils.red(c) * brpc, (float)ColorUtils.green(c) * brpc, (float)ColorUtils.blue(c) * brpc, (float)ColorUtils.alpha(c));
    }

    public static int getColor() {
        return ColorUtils.gradient(Theme.mainRectColor, Theme.mainRectColor, 16, 10);
    }

    public static int getColor(int index) {
        return ColorUtils.gradient(Theme.mainRectColor, Theme.mainRectColor, index * 16, 10);
    }

    public static Color random() {
        return new Color(Color.HSBtoRGB((float)Math.random(), (float)(0.75 + Math.random() / 4.0), (float)(0.75 + Math.random() / 4.0)));
    }

    public static int overCol(int c1, int c2, float pc01) {
        return ColorUtils.getColor((float)ColorUtils.red(c1) * (1.0f - pc01) + (float)ColorUtils.red(c2) * pc01, (float)ColorUtils.green(c1) * (1.0f - pc01) + (float)ColorUtils.green(c2) * pc01, (float)ColorUtils.blue(c1) * (1.0f - pc01) + (float)ColorUtils.blue(c2) * pc01, (float)ColorUtils.alpha(c1) * (1.0f - pc01) + (float)ColorUtils.alpha(c2) * pc01);
    }

    public static int overCol(int c1, int c2) {
        return ColorUtils.overCol(c1, c2, 0.5f);
    }

    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static void setAlphaColor(int color, float alpha) {
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        RenderSystem.color4f(red, green, blue, alpha);
    }

    public static int red(int c) {
        return c >> 16 & 0xFF;
    }

    public static int green(int c) {
        return c >> 8 & 0xFF;
    }

    public static int blue(int c) {
        return c & 0xFF;
    }

    public static int alpha(int c) {
        return c >> 24 & 0xFF;
    }

    public static float redf(int c) {
        return (float)ColorUtils.red(c) / 255.0f;
    }

    public static float greenf(int c) {
        return (float)ColorUtils.green(c) / 255.0f;
    }

    public static float bluef(int c) {
        return (float)ColorUtils.blue(c) / 255.0f;
    }

    public static float alphaf(int c) {
        return (float)ColorUtils.alpha(c) / 255.0f;
    }

    public static void setColor(int color) {
        ColorUtils.setAlphaColor(color, (float)(color >> 24 & 0xFF) / 255.0f);
    }

    public static int toColor(String hexColor) {
        int argb = Integer.parseInt(hexColor.substring(1), 16);
        return ColorUtils.setAlpha(argb, 255);
    }

    public static int setAlpha(int color, int alpha) {
        return color & 0xFFFFFF | alpha << 24;
    }

    public static float[] rgba(int color) {
        return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f};
    }

    public static int reAlphaInt(int color, int alpha) {
        return MathHelper.clamp(alpha, 0, 255) << 24 | color & 0xFFFFFF;
    }

    public static int getColor(double d, double e, double f, double g) {
        return new Color((int)d, (int)e, (int)f, (int)g).getRGB();
    }

    public static int getColor(float r, float g, float b, float a) {
        return new Color((int)r, (int)g, (int)b, (int)a).getRGB();
    }

    public static int multAlpha(int c, float apc) {
        return ColorUtils.getColor((float)ColorUtils.red(c), (float)ColorUtils.green(c), (float)ColorUtils.blue(c), (float)ColorUtils.alpha(c) * apc);
    }

    public static int astolfo(int speed, int index) {
        double d = 0;
        double angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        return Color.getHSBColor(d / 360.0 < 0.5 ? -((float)(angle / 360.0)) : (float)((angle %= 360.0) / 360.0), 0.5f, 1.0f).hashCode();
    }

    public static int rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        float hue = (float)angle / 360.0f;
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return ColorUtils.getColor(ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color), Math.max(0, Math.min(255, (int)(opacity * 255.0f))));
    }

    private static int calculateHueDegrees(int divisor, int offset) {
        long currentTime = System.currentTimeMillis();
        long calculatedValue = (currentTime / (long)divisor + (long)offset) % 360L;
        return (int)calculatedValue;
    }

    public static int gradient(int start, int end, int index, int speed) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int color = ColorUtils.interpolate(start, end, MathHelper.clamp((float)angle / 180.0f - 1.0f, 0.0f, 1.0f));
        float[] hs = ColorUtils.rgba(color);
        float[] hsb = Color.RGBtoHSB((int)(hs[0] * 255.0f), (int)(hs[1] * 255.0f), (int)(hs[2] * 255.0f), null);
        hsb[1] = hsb[1] * 1.5f;
        hsb[1] = Math.min(hsb[1], 1.0f);
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static Color interpolate(Color color1, Color color2, double amount) {
        amount = 1.0 - amount;
        amount = (float)MathHelper.clamp(0.0, 1.0, amount);
        return new Color((int)MathUtil.lerp((double)color1.getRed(), (double)color2.getRed(), amount), (int)MathUtil.lerp((double)color1.getGreen(), (double)color2.getGreen(), amount), (int)MathUtil.lerp((double)color1.getBlue(), (double)color2.getBlue(), amount), (int)MathUtil.lerp((double)color1.getAlpha(), (double)color2.getAlpha(), amount));
    }

    public static int getColor(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int getColor(int r, int g, int b) {
        return new Color(r, g, b, 255).getRGB();
    }

    public static int getColor(int br, int a) {
        return new Color(br, br, br, a).getRGB();
    }

    public static int interpolate(int color1, int color2, double amount) {
        amount = (float)MathHelper.clamp(0.0, 1.0, amount);
        return ColorUtils.getColor(MathUtil.lerp((double)ColorUtils.red(color1), (double)ColorUtils.red(color2), amount), MathUtil.lerp((double)ColorUtils.green(color1), (double)ColorUtils.green(color2), amount), MathUtil.lerp((double)ColorUtils.blue(color1), (double)ColorUtils.blue(color2), amount), MathUtil.lerp((double)ColorUtils.alpha(color1), (double)ColorUtils.alpha(color2), amount));
    }

    public static int interpolate(int start, int end, float value) {
        float[] startColor = ColorUtils.rgba(start);
        float[] endColor = ColorUtils.rgba(end);
        return ColorUtils.rgba((int)MathUtil.interpolate(startColor[0] * 255.0f, endColor[0] * 255.0f, (double)value), (int)MathUtil.interpolate(startColor[1] * 255.0f, endColor[1] * 255.0f, (double)value), (int)MathUtil.interpolate(startColor[2] * 255.0f, endColor[2] * 255.0f, (double)value), (int)MathUtil.interpolate(startColor[3] * 255.0f, endColor[3] * 255.0f, (double)value));
    }

    public static Color lerp(int speed, int index, Color start, Color end) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return ColorUtils.interpolate(start, end, (double)((float)angle / 360.0f));
    }

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(0, 255, alpha));
    }

    private ColorUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static class IntColor {
        public static float[] rgb(int color) {
            return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f};
        }

        public static int rgba(int r, int g, int b, int a) {
            return a << 24 | r << 16 | g << 8 | b;
        }

        public static int rgb(int r, int g, int b) {
            return 0xFF000000 | r << 16 | g << 8 | b;
        }

        public static int getRed(int hex) {
            return hex >> 16 & 0xFF;
        }

        public static int getGreen(int hex) {
            return hex >> 8 & 0xFF;
        }

        public static int getBlue(int hex) {
            return hex & 0xFF;
        }

        public static int getAlpha(int hex) {
            return hex >> 24 & 0xFF;
        }
    }
}

