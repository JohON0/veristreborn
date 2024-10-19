/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.oldclickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.ui.oldclickgui.impl.Component;
import im.expensive.utils.SoundUtil;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.Cursors;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class MultiBoxComponent
extends Component {
    final ModeListSetting setting;
    float width = 0.0f;
    float heightPadding = 0.0f;

    public MultiBoxComponent(ModeListSetting setting) {
        this.setting = setting;
        this.setHeight(22.0f);
    }

    @Override
    public boolean canExpand() {
        return true;
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        Fonts.montserrat.drawText(stack, this.setting.getName(), this.getX() + 5.0f, this.getY() + 2.0f, ColorUtils.rgb(46, 47, 51), 5.5f, 0.05f);
        DisplayUtils.drawShadow(this.getX() + 5.0f, this.getY() + 9.0f, this.width + 5.0f, 10.0f + this.heightPadding, 10, ColorUtils.rgba(10, 10, 10, 255));
        DisplayUtils.drawRoundedRect(this.getX() + 5.0f, this.getY() + 9.0f, this.width + 5.0f, 10.0f + this.heightPadding, 2.0f, ColorUtils.rgba(10, 10, 10, 160));
        float offset = 0.0f;
        float heightoff = 0.0f;
        boolean plused = false;
        boolean anyHovered = false;
        for (BooleanSetting text : (List)this.setting.get()) {
            float off = Fonts.montserrat.getWidth(text.getName(), 5.5f, 0.05f) + 2.0f;
            if (offset + off >= this.getWidth() - 10.0f) {
                offset = 0.0f;
                heightoff += 8.0f;
                plused = true;
            }
            if (MathUtil.isHovered(mouseX, mouseY, this.getX() + 8.0f + offset, this.getY() + 11.5f + heightoff, Fonts.montserrat.getWidth(text.getName(), 5.5f, 0.05f), Fonts.montserrat.getHeight(5.5f) + 1.0f)) {
                anyHovered = true;
            }
            if (((Boolean)text.get()).booleanValue()) {
                Fonts.montserrat.drawText(stack, text.getName(), this.getX() + 8.0f + offset, this.getY() + 11.5f + heightoff, ColorUtils.rgb(114, 118, 134), 5.5f, 0.07f);
            } else {
                Fonts.montserrat.drawText(stack, text.getName(), this.getX() + 8.0f + offset, this.getY() + 11.5f + heightoff, ColorUtils.rgb(46, 47, 51), 5.5f, 0.05f);
            }
            offset += off;
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
        this.heightPadding = heightoff;
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int mouse) {
        float offset = 0.0f;
        float heightoff = 0.0f;
        for (BooleanSetting text : (List)this.setting.get()) {
            float off = Fonts.montserrat.getWidth(text.getName(), 5.5f, 0.05f) + 2.0f;
            if (offset + off >= this.getWidth() - 10.0f) {
                offset = 0.0f;
                heightoff += 8.0f;
            }
            if (mouse == 0 && MathUtil.isHovered(mouseX, mouseY, this.getX() + 8.0f + offset, this.getY() + 11.5f + heightoff, Fonts.montserrat.getWidth(text.getName(), 5.5f, 0.05f), Fonts.montserrat.getHeight(5.5f) + 1.0f)) {
                text.set((Boolean)text.get() == false);
                SoundUtil.playSound("guichangemode.wav");
            }
            offset += off;
        }
        super.mouseClick(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return (Boolean)this.setting.visible.get();
    }
}

