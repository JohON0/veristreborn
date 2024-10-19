/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;

public class ButtonComponent {
    float x;
    float y;
    float width;
    float height;
    public String name;
    Runnable action;

    public ButtonComponent(float x, float y, float width, float height, String name, Runnable action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.action = action;
    }

    public void draw(MatrixStack stack, float mouseX, float mouseY) {
        DisplayUtils.drawRoundedRect(this.x, this.y, this.width, this.height, 3.0f, ColorUtils.rgba(27, 27, 27, 255));
        Fonts.montserrat.drawCenteredText(stack, this.name, this.x + this.width / 2.0f, this.y + this.height / 2.0f - 3.0f, -1, 6.0f);
    }

    public void click(int mouseX, int mouseY) {
        if (MathUtil.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height)) {
            this.action.run();
        }
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }
}

