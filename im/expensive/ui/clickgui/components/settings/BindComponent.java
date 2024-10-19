/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.clickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.ui.clickgui.components.builder.Component;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.SoundUtil;
import im.expensive.utils.client.KeyStorage;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.Cursors;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class BindComponent
extends Component {
    final BindSetting setting;
    boolean activated;
    boolean hovered = false;

    public BindComponent(BindSetting setting) {
        this.setting = setting;
        this.setHeight(16.0f);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        boolean next;
        super.render(stack, mouseX, mouseY);
        Fonts.montserrat.drawText(stack, this.setting.getName(), this.getX() + 5.0f, this.getY() + 3.25f + 1.0f, Theme.textColor, 6.5f, 0.05f);
        String bind = KeyStorage.getKey((Integer)this.setting.get());
        if (bind == null || (Integer)this.setting.get() == -1) {
            bind = "\u041d\u0435\u0442\u0443";
        }
        boolean bl = next = Fonts.montserrat.getWidth(bind, 5.5f, this.activated ? 0.1f : 0.05f) >= 16.0f;
        float x = next ? this.getX() + 5.0f : this.getX() + this.getWidth() - 7.0f - Fonts.montserrat.getWidth(bind, 5.5f, this.activated ? 0.1f : 0.05f);
        float y = this.getY() + 2.75f + 2.75f + (float)(next ? 8 : 0);
        DisplayUtils.drawRoundedRect(x - 2.0f + 0.5f, y - 2.0f, Fonts.montserrat.getWidth(bind, 5.5f, this.activated ? 0.1f : 0.05f) + 4.0f, 9.5f, 2.0f, Theme.mainRectColor);
        Fonts.montserrat.drawText(stack, bind, x, y, this.activated ? Theme.textColor : Theme.darkTextColor, 5.5f, this.activated ? 0.1f : 0.05f);
        if (this.isHovered(mouseX, mouseY)) {
            if (MathUtil.isHovered(mouseX, mouseY, x - 2.0f + 0.5f, y - 2.0f, Fonts.montserrat.getWidth(bind, 5.5f, this.activated ? 0.1f : 0.05f) + 4.0f, 9.5f)) {
                if (!this.hovered) {
                    GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.HAND);
                    this.hovered = true;
                }
            } else if (this.hovered) {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
                this.hovered = false;
            }
        }
        this.setHeight(next ? 22.0f : 16.0f);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        if (this.activated) {
            if (key == 261) {
                this.setting.set(-1);
                SoundUtil.playSound("guibindreset.wav");
                this.activated = false;
                return;
            }
            this.setting.set(key);
            SoundUtil.playSound("guibinding.wav");
            this.activated = false;
        }
        super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int mouse) {
        if (this.isHovered(mouseX, mouseY) && mouse == 0) {
            boolean bl = this.activated = !this.activated;
        }
        if (this.activated && mouse >= 1) {
            System.out.println(-100 + mouse);
            this.setting.set(-100 + mouse);
            this.activated = false;
        }
        super.mouseClick(mouseX, mouseY, mouse);
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int mouse) {
        super.mouseRelease(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return (Boolean)this.setting.visible.get();
    }
}

