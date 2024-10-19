/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.components.FieldComponent;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.math.MathHelper;

public class SliderComponent {
    float x;
    float y;
    float width;
    float height;
    public String name;
    public int min;
    public int max;
    public int current;
    public Enchantment enchantment;
    boolean drag;
    float widthSlider = 0.0f;
    public FieldComponent fieldComponent = new FieldComponent(0.0f, 0.0f, 0.0f, 0.0f);

    public SliderComponent(float x, float y, float width, float height, int min, int max, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.min = min;
        this.max = max;
        this.current = min - 1;
        this.fieldComponent.set(String.valueOf(this.current));
    }

    public SliderComponent(float x, float y, float width, float height, int min, int max, Enchantment enchantment, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.min = min;
        this.max = max;
        this.current = min - 1;
        this.enchantment = enchantment;
        this.fieldComponent.set(String.valueOf(this.current));
    }

    public void draw(MatrixStack stack, float mouseX, float mouseY) {
        int current = this.fieldComponent.get().isEmpty() ? 0 : Integer.parseInt(this.fieldComponent.get());
        Fonts.montserrat.drawText(stack, this.name, this.x, this.y, -1, 6.0f);
        this.fieldComponent.setX(this.x + Fonts.montserrat.getWidth(this.name, 6.0f) + 5.0f);
        this.fieldComponent.setY(this.y);
        this.fieldComponent.setWidth(10.0f);
        this.fieldComponent.setHeight(6.0f);
        this.fieldComponent.draw(stack, mouseX, mouseY);
        float widh = this.width * (float)(current - (this.min - 1)) / (float)(this.max - (this.min - 1));
        this.widthSlider = MathHelper.clamp(MathUtil.fast(this.widthSlider, widh, 15.0f), 0.0f, this.width);
        DisplayUtils.drawRoundedRect(this.x, this.y + 7.0f, this.width, this.height - 7.0f, 1.0f, ColorUtils.rgba(27, 27, 27, 255));
        DisplayUtils.drawRoundedRect(this.x, this.y + 7.0f, this.widthSlider, this.height - 7.0f, 1.0f, ColorUtils.getColor(0));
        if (this.drag) {
            current = (int)MathHelper.clamp(MathUtil.round((mouseX - this.x) / this.width * (float)(this.max - (this.min - 1)) + (float)(this.min - 1), 1.0), (double)(this.min - 1), (double)this.max);
            this.fieldComponent.set(String.valueOf(current));
        }
    }

    public void click(int mouseX, int mouseY) {
        if (MathUtil.isHovered(mouseX, mouseY, this.x, this.y + 7.0f - 2.0f, this.width, this.height - 7.0f + 4.0f)) {
            this.drag = true;
        }
        this.fieldComponent.click(mouseX, mouseY);
    }

    public void unpress() {
        this.drag = false;
    }

    public void key(int key) {
        this.fieldComponent.key(key);
    }

    public void charTyped(char c) {
        if (Character.isDigit(c)) {
            this.fieldComponent.charTyped(c);
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

