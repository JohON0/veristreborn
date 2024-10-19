/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;

public class FieldComponent {
    float x;
    float y;
    float width;
    float height;
    public String name;
    String input = "";
    boolean inputing;
    boolean hide = false;

    public FieldComponent(float x, float y, float width, float height, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public FieldComponent(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hide = true;
    }

    public void draw(MatrixStack stack, float mouseX, float mouseY) {
        if (this.hide) {
            DisplayUtils.drawRoundedRect(this.x, this.y, this.width, this.height, 1.0f, MathUtil.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height) ? ColorUtils.rgba(31, 31, 31, 255) : ColorUtils.rgba(27, 27, 27, 255));
            Fonts.montserrat.drawCenteredText(stack, this.input + (this.inputing ? (System.currentTimeMillis() % 1000L >= 500L ? "|" : "") : ""), this.x + 2.0f + this.width / 2.0f / 2.0f, this.y + 0.5f, -1, 5.0f);
            return;
        }
        Fonts.montserrat.drawText(stack, this.name, this.x, this.y, -1, 6.0f);
        DisplayUtils.drawRoundedRect(this.x, this.y + 7.0f, this.width, this.height - 7.0f, 3.0f, MathUtil.isHovered(mouseX, mouseY, this.x, this.y + 7.0f, this.width, this.height - 7.0f) ? ColorUtils.rgba(31, 31, 31, 255) : ColorUtils.rgba(27, 27, 27, 255));
        Fonts.montserrat.drawText(stack, this.input + (this.inputing ? (System.currentTimeMillis() % 1000L >= 500L ? "|" : "") : ""), this.x + 2.0f, this.y + this.height / 2.0f + 1.0f, -1, 5.0f);
    }

    public void click(int mouseX, int mouseY) {
        if (MathUtil.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height)) {
            this.inputing = !this.inputing;
        }
    }

    public void key(int key) {
        if (this.inputing && key == 259 && !this.input.isEmpty()) {
            this.input = this.input.substring(0, this.input.length() - 1);
        }
        if (this.inputing && key == 257) {
            this.inputing = false;
        }
    }

    public String get() {
        return this.input;
    }

    public void set(String text) {
        this.input = text;
    }

    public void charTyped(char c) {
        if (this.inputing) {
            if (Fonts.montserrat.getWidth(this.input + c, 5.0f) > this.width - Fonts.montserrat.getWidth(String.valueOf(c), 5.0f)) {
                return;
            }
            this.input = this.input + c;
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

