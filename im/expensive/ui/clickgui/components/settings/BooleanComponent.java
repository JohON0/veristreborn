/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.clickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.modules.settings.impl.BooleanSetting;
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
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

public class BooleanComponent
extends Component {
    private final BooleanSetting setting;
    private Animation animation = new Animation();
    private float width;
    private float height;
    private boolean hovered = false;

    public BooleanComponent(BooleanSetting setting) {
        this.setting = setting;
        this.setHeight(16.0f);
        this.animation = this.animation.animate((Boolean)setting.get() != false ? 1.0 : 0.0, 0.2, Easings.CIRC_OUT);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        this.animation.update();
        Fonts.montserrat.drawText(stack, this.setting.getName(), this.getX() + 5.0f, this.getY() + 3.25f + 1.0f, ColorUtils.rgb(255, 255, 255), 6.5f, 0.02f);
        this.width = 15.0f;
        this.height = 7.0f;
        DisplayUtils.drawRoundedRect(this.getX() + this.getWidth() - this.width - 7.0f, this.getY() + this.getHeight() / 2.0f - this.height / 2.0f, this.width, this.height, 3.0f, ColorUtils.setAlpha(Theme.textColor, 10));
        int color = ColorUtils.interpolate(Theme.mainRectColor, Theme.rectColor, 1.0f - (float)this.animation.getValue());
        DisplayUtils.drawCircle((float)((double)(this.getX() + this.getWidth() - this.width - 7.0f + 4.0f) + 7.0 * this.animation.getValue()), this.getY() + this.getHeight() / 2.0f - this.height / 2.0f + 3.5f, 5.0f, color);
        DisplayUtils.drawShadowCircle((float)((double)(this.getX() + this.getWidth() - this.width - 7.0f + 4.0f) + 7.0 * this.animation.getValue()), this.getY() + this.getHeight() / 2.0f - this.height / 2.0f + 3.5f, 7.0f, ColorUtils.setAlpha(color, (int)(128.0 * this.animation.getValue())));
        if (this.isHovered(mouseX, mouseY)) {
            if (MathUtil.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - this.width - 7.0f, this.getY() + this.getHeight() / 2.0f - this.height / 2.0f, this.width, this.height)) {
                if (!this.hovered) {
                    GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.HAND);
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
        if (mouse == 0 && MathUtil.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - this.width - 7.0f, this.getY() + this.getHeight() / 2.0f - this.height / 2.0f, this.width, this.height)) {
            this.setting.set((Boolean)this.setting.get() == false);
            this.animation = this.animation.animate((Boolean)this.setting.get() != false ? 1.0 : 0.0, 0.2, Easings.CIRC_OUT);
            SoundUtil.playSound((Boolean)this.setting.get() != false ? "guienablecheckbox.wav" : "guidisablecheckbox.wav");
        }
        super.mouseClick(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return (Boolean)this.setting.visible.get();
    }
}

