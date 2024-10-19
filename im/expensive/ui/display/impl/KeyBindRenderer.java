/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.Expensive;
import im.expensive.events.EventDisplay;
import im.expensive.modules.api.Module;
import im.expensive.ui.display.ElementRenderer;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.animations.easing.CompactAnimation;
import im.expensive.utils.animations.easing.Easing;
import im.expensive.utils.client.KeyStorage;
import im.expensive.utils.drag.Dragging;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;

public class KeyBindRenderer
implements ElementRenderer {
    private final Dragging dragging;
    private final CompactAnimation widthAnimation = new CompactAnimation(Easing.EASE_OUT_QUART, 100L);
    private final CompactAnimation heightAnimation = new CompactAnimation(Easing.EASE_OUT_QUART, 100L);
    private double width;
    private float height;

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();
        float posX = this.dragging.getX();
        float posY = this.dragging.getY();
        float fontSize = 6.5f;
        float padding = 5.0f;
        float iconSize = 10.0f;
        float margin = 2.0f;
        DisplayUtils.drawStyledRect(posX, posY, (float)this.width, this.height + 2.0f);
        int textColor = ColorUtils.rgb(235, 235, 235);
        String name = "Keybinds";
        Fonts.montserrat.drawText(ms, name, posX + iconSize + 8.0f, posY + padding + 0.5f, Theme.textColor, fontSize, 0.07f);
        ClientFonts.icons_nur[20].drawString(ms, "F", (double)(posX + 5.0f), (double)(posY + 6.5f), Theme.textColor);
        DisplayUtils.drawRectHorizontalW(posX, posY + 17.0f, this.width, 2.5, ColorUtils.rgba(0, 0, 0, 0), ColorUtils.rgba(0, 0, 0, 63));
        float maxWidth = Fonts.montserrat.getWidth(name, fontSize) + padding * 2.0f;
        float localHeight = fontSize + padding * 2.0f;
        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, this.width, this.height);
        posY += fontSize + padding + 2.0f;
        posY += 4.5f;
        for (Module f : Expensive.getInstance().getModuleManager().getModules()) {
            f.getAnimation().update();
            if (!(f.getAnimation().getValue() > 0.0) || f.getBind() == 0) continue;
            String nameText = f.getName();
            float nameWidth = Fonts.montserrat.getWidth(nameText, fontSize);
            String bindText = "[" + KeyStorage.getKey(f.getBind()).replace("_", "").replace("LEFT", "L").replace("RIGHT", "R").replace("CONTROL", "CTRL") + "]";
            float bindWidth = Fonts.montserrat.getWidth(bindText, fontSize);
            float localWidth = nameWidth + bindWidth + padding * 3.0f;
            Fonts.montserrat.drawText(ms, nameText, posX + padding - 0.5f, posY + margin + 0.5f, ColorUtils.setAlpha(Theme.textColor, (int)(255.0 * f.getAnimation().getValue())), fontSize, 0.05f);
            Fonts.montserrat.drawText(ms, bindText, (float)((double)posX + this.width - (double)padding - (double)bindWidth + 1.0), posY + margin + 0.5f, ColorUtils.setAlpha(Theme.textColor, (int)(255.0 * f.getAnimation().getValue())), fontSize, 0.05f);
            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }
            posY += (float)((double)(fontSize + padding - 2.0f) * f.getAnimation().getValue());
            localHeight += fontSize + padding - 2.0f;
        }
        Scissor.unset();
        Scissor.pop();
        this.widthAnimation.run(Math.max(maxWidth, 70.0f));
        this.width = this.widthAnimation.getValue();
        this.heightAnimation.run((double)localHeight + 3.5);
        this.height = (float)this.heightAnimation.getValue();
        this.dragging.setWidth((float)this.width);
        this.dragging.setHeight(this.height);
    }

    public KeyBindRenderer(Dragging dragging) {
        this.dragging = dragging;
    }
}

