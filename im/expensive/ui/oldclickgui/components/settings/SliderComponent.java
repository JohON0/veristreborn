/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.oldclickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.ui.oldclickgui.impl.Component;
import im.expensive.utils.SoundUtil;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.Cursors;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class SliderComponent
extends Component {
    private final SliderSetting setting;
    private float newValue;
    private float lastValue;
    private float anim;
    private boolean drag;
    private boolean hovered = false;

    public SliderComponent(SliderSetting setting) {
        this.setting = setting;
        this.setHeight(18.0f);
    }

    @Override
    public boolean canExpand() {
        return true;
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        Fonts.montserrat.drawText(stack, this.setting.getName(), this.getX() + 5.0f, this.getY() + 2.25f + 1.0f, ColorUtils.rgb(160, 163, 175), 5.5f, 0.05f);
        Fonts.montserrat.drawText(stack, String.valueOf(this.setting.get()), this.getX() + this.getWidth() - 5.0f - Fonts.montserrat.getWidth(String.valueOf(this.setting.get()), 5.5f), this.getY() + 2.25f + 1.0f, ColorUtils.rgb(160, 163, 175), 5.5f, 0.05f);
        DisplayUtils.drawRoundedRect(this.getX() + 5.0f, this.getY() + 11.0f, this.getWidth() - 10.0f, 2.0f, 0.6f, ColorUtils.rgba(10, 10, 10, 255));
        float sliderWidth = this.anim = MathUtil.fast(this.anim, (this.getWidth() - 10.0f) * (((Float)this.setting.get()).floatValue() - this.setting.min) / (this.setting.max - this.setting.min), 20.0f);
        DisplayUtils.drawRoundedRect(this.getX() + 5.0f, this.getY() + 11.0f, sliderWidth, 2.0f, 0.6f, ColorUtils.rgb(128, 132, 150));
        DisplayUtils.drawCircle(this.getX() + 5.0f + sliderWidth, this.getY() + 12.0f, 5.0f, ColorUtils.rgb(128, 132, 150));
        DisplayUtils.drawShadowCircle(this.getX() + 5.0f + sliderWidth, this.getY() + 12.0f, 6.0f, ColorUtils.rgba(128, 132, 150, 64));
        if (this.drag) {
            GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.glfwCreateStandardCursor(221189));
            float newValue = (float)MathHelper.clamp(MathUtil.round((mouseX - this.getX() - 5.0f) / (this.getWidth() - 10.0f) * (this.setting.max - this.setting.min) + this.setting.min, this.setting.increment), (double)this.setting.min, (double)this.setting.max);
            if (newValue != this.lastValue) {
                this.setting.set(Float.valueOf(newValue));
                this.lastValue = newValue;
                SoundUtil.playSound("guislidermove.wav");
            }
        }
        if (this.isHovered(mouseX, mouseY)) {
            if (MathUtil.isHovered(mouseX, mouseY, this.getX() + 5.0f, this.getY() + 10.0f, this.getWidth() - 10.0f, 3.0f)) {
                if (!this.hovered) {
                    GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.RESIZEH);
                    this.hovered = true;
                }
            } else if (this.hovered) {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
                this.hovered = false;
            }
        }
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int mouse) {
        if (mouse == 0 && MathUtil.isHovered(mouseX, mouseY, this.getX() + 5.0f, this.getY() + 10.0f, this.getWidth() - 10.0f, 3.0f)) {
            this.drag = true;
        }
        super.mouseClick(mouseX, mouseY, mouse);
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int mouse) {
        this.drag = false;
        super.mouseRelease(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return (Boolean)this.setting.visible.get();
    }
}

