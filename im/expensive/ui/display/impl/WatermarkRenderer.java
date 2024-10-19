/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.Expensive;
import im.expensive.events.EventDisplay;
import im.expensive.ui.display.ElementRenderer;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.render.rect.RectUtil;
import im.expensive.utils.text.BetterText;
import im.expensive.utils.text.font.ClientFonts;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.TextFormatting;

public class WatermarkRenderer
implements ElementRenderer {
    private final BetterText watermarkText = new BetterText(List.of((Object)"\u0422\u0435\u043f\u043b\u043e \u0438 \u043a\u043e\u043c\u0444\u043e\u0440\u0442\u043d\u043e", (Object)"\u041a\u0430\u043a \u0441\u0432\u0435\u0442 \u0432 \u043e\u043a\u043d\u0435", (Object)"\u041d\u0435\u043f\u043e\u0432\u0442\u043e\u0440\u0438\u043c\u044b\u0439 \u0441\u0442\u0438\u043b\u044c", (Object)"\u041e\u0442\u043b\u0438\u0447\u043d\u044b\u0439 \u0432\u044b\u0431\u043e\u0440", (Object)"\u041a\u0438\u0441\u043b\u0430 \u043a\u0430\u043a \u0442\u0443\u0441\u0430 <3", (Object)"\u041f\u0440\u043e\u0441\u0442\u043e \u0432\u0435\u043b\u0438\u043a\u043e\u043b\u0435\u043f\u043d\u043e!"), 2000);
    private final BetterText secondWatermarkText = new BetterText(List.of((Object)" <3", (Object)" >_<", (Object)" UwU", (Object)" O_O", (Object)" OwO", (Object)" :>", (Object)" <3", (Object)" >w<", (Object)"~~"), 2000);

    @Override
    public void render(EventDisplay eventDisplay) {
        float off;
        float margin;
        this.secondWatermarkText.update();
        MatrixStack ms = eventDisplay.getMatrixStack();
        float x = 4.0f;
        float y = 4.0f;
        float padding = 5.0f;
        float fontSize = 16.0f;
        float iconSize = 10.0f;
        if (Expensive.getInstance().getModuleManager().getHud().waterMarkMode.is("\u0422\u0430\u0431\u043b\u0438\u0447\u043a\u0430")) {
            margin = 5.0f;
            off = 2.0f;
            String watermarkTextString = "Verist " + WatermarkRenderer.mc.debugFPS + "\u0424\u041f\u0421";
            String secondText = ClientUtil.getGreetingMessage() + ", " + ClientUtil.getUsername() + this.secondWatermarkText.getOutput().toString();
            float width = Math.max(Fonts.montserrat.getWidth(secondText, 6.0f), Fonts.montserrat.getWidth(watermarkTextString, 8.0f)) + margin * 1.5f;
            float height = 18.0f;
            DisplayUtils.drawStyledShadowRect(x + off, y + off, width, height);
            DisplayUtils.drawRoundedRect(x + off + 1.0f, y + off + 1.0f, 2.0f, height - 2.0f, new Vector4f(3.0f, 3.0f, 0.0f, 0.0f), new Vector4i(Theme.rectColor, Theme.rectColor, Theme.mainRectColor, Theme.mainRectColor));
            DisplayUtils.drawShadow(x + off + 1.0f, y + off + 1.0f, 2.0f, height - 2.0f, 8, ColorUtils.reAlphaInt(Theme.rectColor, 255), ColorUtils.reAlphaInt(Theme.mainRectColor, 255));
            Fonts.montserrat.drawText(ms, watermarkTextString, x + off + 4.5f, y + off + 1.5f, Theme.textColor, 8.0f, 0.05f);
            Fonts.montserrat.drawText(ms, secondText, x + off + 4.5f, y + off + 9.5f, Theme.textColor, 6.0f, 0.025f);
        }
        if (Expensive.getInstance().getModuleManager().getHud().waterMarkMode.is("\u0412\u0440\u0435\u043c\u044f")) {
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = currentTime.format(formatter);
            DisplayUtils.drawShadow(1.0f, 1.0f, ClientFonts.tech[30].getWidth(formattedTime) + 3.0f, ClientFonts.tech[30].getFontHeight() + 1.0f, 10, ColorUtils.reAlphaInt(Theme.textColor, 90));
            ClientFonts.tech[30].drawString(ms, formattedTime, 3.0, 6.0, Theme.textColor);
        }
        if (Expensive.getInstance().getModuleManager().getHud().waterMarkMode.is("\u041e\u0431\u044b\u0447\u043d\u044b\u0439")) {
            String text = this.watermarkText().toString();
            float textWidth = ClientFonts.msMedium[(int)fontSize].getWidth(text) - 8.0f;
            float width = iconSize + padding + textWidth;
            float height = iconSize + padding;
            DisplayUtils.drawStyledShadowRect(x, y, width, height);
            ClientFonts.msMedium[(int)fontSize].drawString(ms, text, (double)(x + padding * 1.5f - 4.0f), (double)(y + iconSize / 2.0f - 0.5f), Theme.textColor);
        }
        if (Expensive.getInstance().getModuleManager().getHud().waterMarkMode.is("\u041f\u043b\u0438\u0442\u043a\u0430")) {
            this.watermarkText.update();
            margin = 5.0f;
            off = 2.0f;
            int bgcolor1 = Theme.rectColor;
            int bgcolor2 = Theme.darkMainRectColor;
            int bgcolor3 = Theme.rectColor;
            int bgcolor4 = Theme.darkMainRectColor;
            float of = 2.0f;
            float pc = 0.1f;
            int white = ColorUtils.getColor(255, 255, 255, 255);
            int color1 = ColorUtils.overCol(white, Theme.rectColor, pc);
            int color2 = ColorUtils.overCol(white, Theme.darkMainRectColor, pc);
            int color3 = ColorUtils.overCol(white, Theme.rectColor, pc);
            int color4 = ColorUtils.overCol(white, Theme.darkMainRectColor, pc);
            String watermarkTextString = "Verist " + WatermarkRenderer.mc.debugFPS + "\u0424\u041f\u0421";
            String small = this.watermarkText.getOutput().toString();
            float width = Math.max(ClientFonts.small_pixel[20].getWidth(small), ClientFonts.small_pixel[24].getWidth(watermarkTextString)) + margin * 1.5f;
            float height = 22.0f;
            RectUtil.getInstance().drawRoundedRectShadowed(eventDisplay.getMatrixStack(), x, y + off, x + width - off, y + height, 3.0f, 1.0f, bgcolor1, bgcolor2, bgcolor3, bgcolor4, false, true, true, true);
            RectUtil.getInstance().drawRoundedRectShadowed(eventDisplay.getMatrixStack(), x + off, y, x + width, y + height - off, 2.0f, 1.0f, color1, color2, color3, color4, false, true, true, true);
            ClientFonts.small_pixel[24].drawString(eventDisplay.getMatrixStack(), watermarkTextString, (double)(x + margin) + 0.5, (double)((int)y) + 0.5 + (double)off, ColorUtils.multDark(Theme.textColor, 0.5f));
            ClientFonts.small_pixel[24].drawString(eventDisplay.getMatrixStack(), watermarkTextString, (double)(x + margin), (double)((float)((int)y) + off), ColorUtils.getColor(0, 0, 0, 255));
            ClientFonts.small_pixel[20].drawString(eventDisplay.getMatrixStack(), small, (double)(x + margin) + 0.25, (double)((float)((int)y) + (off + 4.0f) + height - ClientFonts.small_pixel[28].getFontHeight() - off) + 0.25, Theme.textColor);
            ClientFonts.small_pixel[20].drawString(eventDisplay.getMatrixStack(), small, (double)(x + margin), (double)((float)((int)y) + (off + 4.0f) + height - ClientFonts.small_pixel[28].getFontHeight() - off), ColorUtils.getColor(20, 20, 20, 255));
        }
    }

    private StringBuilder watermarkText() {
        StringBuilder watermarkText = new StringBuilder();
        watermarkText.append("Verist");
        if (this.isEnabled("\u0424\u043f\u0441") || this.isEnabled("\u041f\u0438\u043d\u0433") || this.isEnabled("\u0421\u0435\u0440\u0432\u0435\u0440")) {
            watermarkText.append((Object)TextFormatting.GRAY).append(" - ").append((Object)TextFormatting.WHITE);
        }
        if (this.isEnabled("\u0424\u043f\u0441")) {
            watermarkText.append(Minecraft.getInstance().debugFPS).append("fps");
            if (this.isEnabled("\u041f\u0438\u043d\u0433") || this.isEnabled("\u0421\u0435\u0440\u0432\u0435\u0440")) {
                watermarkText.append((Object)TextFormatting.GRAY).append(" - ").append((Object)TextFormatting.WHITE);
            }
        }
        if (this.isEnabled("\u041f\u0438\u043d\u0433")) {
            watermarkText.append(MathUtil.calculatePing() + "ms");
            if (this.isEnabled("\u0421\u0435\u0440\u0432\u0435\u0440")) {
                watermarkText.append((Object)TextFormatting.GRAY).append(" - ").append((Object)TextFormatting.WHITE);
            }
        }
        if (this.isEnabled("\u0421\u0435\u0440\u0432\u0435\u0440")) {
            if (mc.getCurrentServerData() != null && WatermarkRenderer.mc.getCurrentServerData().serverIP != null && !WatermarkRenderer.mc.getCurrentServerData().serverIP.equals("45.93.200.8:25610")) {
                watermarkText.append(WatermarkRenderer.mc.getCurrentServerData().serverIP.toLowerCase());
            } else {
                watermarkText.append("localhost");
            }
        }
        return watermarkText;
    }

    private boolean isEnabled(String check) {
        return (Boolean)Expensive.getInstance().getModuleManager().getHud().waterMarkOptions.getValueByName(check).get();
    }
}

