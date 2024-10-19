/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.clickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.modules.settings.impl.ColorSetting;
import im.expensive.ui.clickgui.components.builder.Component;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import java.awt.Color;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;

public class ColorComponent
extends Component {
    final ColorSetting colorSetting;
    float colorRectX;
    float colorRectY;
    float colorRectWidth;
    float colorRectHeight;
    float pickerX;
    float pickerY;
    float pickerWidth;
    float pickerHeight;
    float sliderX;
    float sliderY;
    float sliderWidth;
    float sliderHeight;
    final float padding = 5.0f;
    float textX;
    float textY;
    final int textColor = -1;
    private float[] hsb = new float[2];
    boolean panelOpened;
    boolean draggingHue;
    boolean draggingPicker;

    public ColorComponent(ColorSetting colorSetting) {
        this.colorSetting = colorSetting;
        this.hsb = Color.RGBtoHSB(ColorUtils.IntColor.getRed((Integer)colorSetting.get()), ColorUtils.IntColor.getGreen((Integer)colorSetting.get()), ColorUtils.IntColor.getBlue((Integer)colorSetting.get()), null);
        this.setHeight(22.0f);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        this.renderTextAndColorRect(stack);
        if (this.panelOpened) {
            this.colorSetting.set(Color.getHSBColor(this.hsb[0], this.hsb[1], this.hsb[2]).getRGB());
            this.renderSlider(mouseX, mouseY);
            this.renderPickerPanel(mouseX, mouseY);
            this.setHeight(18.0f + this.pickerHeight + 5.0f);
        } else {
            this.setHeight(18.0f);
        }
        super.render(stack, mouseX, mouseY);
    }

    private void renderTextAndColorRect(MatrixStack stack) {
        String settingName = this.colorSetting.getName();
        int colorValue = (Integer)this.colorSetting.get();
        this.textX = this.getX() + 5.0f;
        this.textY = this.getY() + 3.25f;
        this.colorRectX = this.getX() + this.getWidth() - this.colorRectWidth - 5.0f;
        this.colorRectY = this.getY() + 5.0f - 4.0f;
        this.colorRectWidth = 20.0f;
        this.colorRectHeight = 10.0f;
        this.pickerX = this.getX() + 5.0f;
        this.pickerY = this.getY() + 5.0f + 16.0f - 4.0f;
        this.pickerWidth = this.getWidth() - 20.0f;
        this.pickerHeight = 60.0f;
        this.sliderX = this.pickerX + this.pickerWidth + 5.0f;
        this.sliderY = this.pickerY;
        this.sliderWidth = 3.0f;
        this.sliderHeight = this.pickerHeight;
        Fonts.montserrat.drawText(stack, settingName, this.textX, this.textY, -1, 6.5f, 0.02f);
        DisplayUtils.drawRoundedRect(this.colorRectX, this.colorRectY, this.colorRectWidth, this.colorRectHeight, 3.5f, colorValue);
    }

    private void renderPickerPanel(float mouseX, float mouseY) {
        Vector4i vector4i = new Vector4i(Color.WHITE.getRGB(), Color.BLACK.getRGB(), Color.getHSBColor(this.hsb[0], 1.0f, 1.0f).getRGB(), Color.BLACK.getRGB());
        float offset = 4.0f;
        float xRange = this.pickerWidth - 8.0f;
        float yRange = this.pickerHeight - 8.0f;
        if (this.draggingPicker) {
            float saturation = MathHelper.clamp(mouseX - this.pickerX - offset, 0.0f, xRange) / xRange;
            float brightness = MathHelper.clamp(mouseY - this.pickerY - offset, 0.0f, yRange) / yRange;
            this.hsb[1] = saturation;
            this.hsb[2] = 1.0f - brightness;
        }
        DisplayUtils.drawRoundedRect(this.pickerX, this.pickerY, this.pickerWidth, this.pickerHeight, new Vector4f(6.0f, 6.0f, 6.0f, 6.0f), vector4i);
        float circleX = this.pickerX + offset + this.hsb[1] * xRange;
        float circleY = this.pickerY + offset + (1.0f - this.hsb[2]) * yRange;
        DisplayUtils.drawCircle(circleX, circleY, 8.0f, Color.BLACK.getRGB());
        DisplayUtils.drawCircle(circleX, circleY, 6.0f, Color.WHITE.getRGB());
    }

    private void renderSlider(float mouseX, float mouseY) {
        int i = 0;
        while ((float)i < this.sliderHeight) {
            float hue = (float)i / this.sliderHeight;
            DisplayUtils.drawCircle(this.sliderX + 1.0f, this.sliderY + (float)i, 3.0f, Color.HSBtoRGB(hue, 1.0f, 1.0f));
            ++i;
        }
        DisplayUtils.drawCircle(this.sliderX + this.sliderWidth - 2.0f, this.sliderY + this.hsb[0] * this.sliderHeight, 8.0f, Color.BLACK.getRGB());
        DisplayUtils.drawCircle(this.sliderX + this.sliderWidth - 2.0f, this.sliderY + this.hsb[0] * this.sliderHeight, 6.0f, -1);
        if (this.draggingHue) {
            float hue = (mouseY - this.sliderY) / this.sliderHeight;
            this.hsb[0] = MathHelper.clamp(hue, 0.0f, 1.0f);
        }
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int mouse) {
        if (DisplayUtils.isInRegion((double)mouseX, (double)mouseY, this.colorRectX, this.colorRectY, this.colorRectWidth, this.colorRectHeight) && mouse == 1) {
            boolean bl = this.panelOpened = !this.panelOpened;
        }
        if (this.panelOpened) {
            if (DisplayUtils.isInRegion((double)mouseX, (double)mouseY, this.sliderX - 2.0f, this.sliderY, this.sliderWidth + 4.0f, this.pickerHeight - 12.0f)) {
                this.draggingHue = true;
            } else if (DisplayUtils.isInRegion((double)mouseX, (double)mouseY, this.pickerX, this.pickerY, this.pickerWidth, this.pickerHeight)) {
                this.draggingPicker = true;
            }
        }
        super.mouseClick(mouseX, mouseY, mouse);
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int mouse) {
        if (this.draggingHue) {
            this.draggingHue = false;
        }
        if (this.draggingPicker) {
            this.draggingPicker = false;
        }
        super.mouseRelease(mouseX, mouseY, mouse);
    }
}

