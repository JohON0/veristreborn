/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.clickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.ui.clickgui.components.builder.Component;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.SoundUtil;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.Cursors;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ModeComponent
extends Component {
    final ModeSetting setting;
    float width = 0.0f;
    float heightplus = 0.0f;
    float spacing = 4.0f;

    public ModeComponent(ModeSetting setting) {
        this.setting = setting;
        this.setHeight(22.0f);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        Fonts.montserrat.drawText(stack, this.setting.getName(), this.getX() + 5.0f, this.getY() + 2.0f, Theme.textColor, 5.5f, 0.05f);
        DisplayUtils.drawShadow(this.getX() + 5.0f, this.getY() + 9.0f, this.width + 5.0f, 10.0f + this.heightplus, 10, ColorUtils.rgba(25, 25, 25, 45));
        DisplayUtils.drawRoundedRect(this.getX() + 5.0f, this.getY() + 9.0f, this.width + 5.0f, 10.0f + this.heightplus, 2.0f, ColorUtils.rgba(25, 25, 25, 45));
        float offset = 0.0f;
        float heightoff = 0.0f;
        boolean plused = false;
        boolean anyHovered = false;
        for (String text : this.setting.strings) {
            float textWidth = Fonts.montserrat.getWidth(text, 5.5f, 0.05f) + 2.0f;
            float textHeight = Fonts.montserrat.getHeight(5.5f) + 1.0f;
            if (offset + textWidth + this.spacing >= this.getWidth() - 10.0f) {
                offset = 0.0f;
                heightoff += textHeight + this.spacing / 2.0f;
                plused = true;
            }
            if (MathUtil.isHovered(mouseX, mouseY, this.getX() + 8.0f + offset, this.getY() + 11.5f + heightoff, textWidth, textHeight)) {
                anyHovered = true;
            }
            if (text.equals(this.setting.get())) {
                Fonts.montserrat.drawText(stack, text, this.getX() + 8.0f + offset, this.getY() + 11.5f + heightoff, Theme.textColor, 5.5f, 0.07f);
            } else {
                Fonts.montserrat.drawText(stack, text, this.getX() + 8.0f + offset, this.getY() + 11.5f + heightoff, Theme.darkTextColor, 5.5f, 0.05f);
            }
            offset += textWidth + this.spacing / 2.0f;
        }
        if (this.isHovered(mouseX, mouseY)) {
            if (anyHovered) {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.HAND);
            } else {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
            }
        }
        this.width = plused ? this.getWidth() - 15.0f : offset;
        this.setHeight(22.0f + heightoff);
        this.heightplus = heightoff;
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int mouse) {
        float offset = 0.0f;
        float heightoff = 0.0f;
        for (String text : this.setting.strings) {
            float textWidth = Fonts.montserrat.getWidth(text, 5.5f, 0.05f) + 2.0f;
            float textHeight = Fonts.montserrat.getHeight(5.5f) + 1.0f;
            if (offset + textWidth + this.spacing >= this.getWidth() - 10.0f) {
                offset = 0.0f;
                heightoff += textHeight + this.spacing / 2.0f;
            }
            if (mouse == 0 && MathUtil.isHovered(mouseX, mouseY, this.getX() + 8.0f + offset, this.getY() + 10.0f + heightoff, Fonts.montserrat.getWidth(text, 5.5f, 0.05f), Fonts.montserrat.getHeight(5.5f) + 1.0f)) {
                this.setting.set(text);
                SoundUtil.playSound("guichangemode.wav");
            }
            offset += textWidth + this.spacing / 2.0f;
        }
        super.mouseClick(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return (Boolean)this.setting.visible.get();
    }
}

