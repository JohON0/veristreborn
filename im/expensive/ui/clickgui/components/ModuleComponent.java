/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.clickgui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.modules.api.Module;
import im.expensive.modules.settings.Setting;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ColorSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.modules.settings.impl.StringSetting;
import im.expensive.ui.clickgui.Panel;
import im.expensive.ui.clickgui.components.builder.Component;
import im.expensive.ui.clickgui.components.settings.BindComponent;
import im.expensive.ui.clickgui.components.settings.BooleanComponent;
import im.expensive.ui.clickgui.components.settings.ColorComponent;
import im.expensive.ui.clickgui.components.settings.ModeComponent;
import im.expensive.ui.clickgui.components.settings.MultiBoxComponent;
import im.expensive.ui.clickgui.components.settings.SliderComponent;
import im.expensive.ui.clickgui.components.settings.StringComponent;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.SoundUtil;
import im.expensive.utils.client.KeyStorage;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.render.Cursors;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Stencil;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.BetterText;
import im.expensive.utils.text.font.ClientFonts;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.glfw.GLFW;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

public class ModuleComponent
extends Component {
    private final Vector4f ROUNDING_VECTOR = new Vector4f(5.0f, 5.0f, 5.0f, 5.0f);
    private final Vector4i BORDER_COLOR = new Vector4i(ColorUtils.rgb(45, 46, 53), ColorUtils.rgb(25, 26, 31), ColorUtils.rgb(45, 46, 53), ColorUtils.rgb(25, 26, 31));
    private final Module module;
    protected Panel panel;
    public Animation animation = new Animation();
    public boolean open;
    private boolean bind;
    private final ObjectArrayList<Component> components = new ObjectArrayList();
    private boolean hovered = false;
    private final BetterText gavno = new BetterText(List.of(), 100);

    public ModuleComponent(Module module) {
        this.module = module;
        for (Setting<?> setting : module.getSettings()) {
            Setting mode;
            if (setting instanceof BooleanSetting) {
                BooleanSetting bool = (BooleanSetting)setting;
                this.components.add(new BooleanComponent(bool));
            }
            if (setting instanceof SliderSetting) {
                SliderSetting slider = (SliderSetting)setting;
                this.components.add(new SliderComponent(slider));
            }
            if (setting instanceof BindSetting) {
                BindSetting bind = (BindSetting)setting;
                this.components.add(new BindComponent(bind));
            }
            if (setting instanceof ModeSetting) {
                mode = (ModeSetting)setting;
                this.components.add(new ModeComponent((ModeSetting)mode));
            }
            if (setting instanceof ModeListSetting) {
                mode = (ModeListSetting)setting;
                this.components.add(new MultiBoxComponent((ModeListSetting)mode));
            }
            if (setting instanceof StringSetting) {
                StringSetting string = (StringSetting)setting;
                this.components.add(new StringComponent(string));
            }
            if (!(setting instanceof ColorSetting)) continue;
            ColorSetting color = (ColorSetting)setting;
            this.components.add(new ColorComponent(color));
        }
        this.animation = this.animation.animate(this.open ? 1.0 : 0.0, 0.3);
    }

    public void drawComponents(MatrixStack stack, float mouseX, float mouseY) {
        if (this.animation.getValue() > 0.0) {
            if (this.animation.getValue() > 0.1 && this.components.stream().filter(Component::isVisible).count() >= 1L) {
                DisplayUtils.drawRectVerticalW(this.getX() + 5.0f, this.getY() + 18.0f, this.getWidth() - 10.0f, 0.5, Theme.rectColor, Theme.rectColor);
            }
            Stencil.initStencilToWrite();
            DisplayUtils.drawRoundedRect(this.getX() + 0.5f, this.getY() + 0.5f, this.getWidth() - 1.0f, this.getHeight() - 1.0f, this.ROUNDING_VECTOR, ColorUtils.rgba(23, 23, 23, 84));
            Stencil.readStencilBuffer(1);
            float y = this.getY() + 20.0f;
            for (Component component : this.components) {
                if (!component.isVisible()) continue;
                component.setX(this.getX());
                component.setY(y);
                component.setWidth(this.getWidth());
                component.render(stack, mouseX, mouseY);
                y += component.getHeight();
            }
            Stencil.uninitStencilBuffer();
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int mouse) {
        for (Component component : this.components) {
            component.mouseRelease(mouseX, mouseY, mouse);
        }
        super.mouseRelease(mouseX, mouseY, mouse);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        int color = ColorUtils.interpolate(Theme.textColor, ColorUtils.rgb(161, 164, 177), (float)this.module.getAnimation().getValue());
        this.module.getAnimation().update();
        super.render(stack, mouseX, mouseY);
        this.drawOutlinedRect(mouseX, mouseY, color);
        this.drawText(stack, color);
        this.drawComponents(stack, mouseX, mouseY);
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (this.isHovered(mouseX, mouseY, 20.0f)) {
            if (button == 0) {
                this.module.toggle();
            }
            if (button == 1 && !this.module.getSettings().isEmpty()) {
                this.open = !this.open;
                SoundUtil.playSound(this.open ? "moduleopen.wav" : "moduleclose.wav");
                this.animation = this.animation.animate(this.open ? 1.0 : 0.0, this.open ? 0.2 : 0.1, Easings.CIRC_OUT);
            }
            if (button == 2) {
                boolean bl = this.bind = !this.bind;
            }
        }
        if (this.isHovered(mouseX, mouseY) && this.open) {
            for (Component component : this.components) {
                if (!component.isVisible()) continue;
                component.mouseClick(mouseX, mouseY, button);
            }
        }
        super.mouseClick(mouseX, mouseY, button);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (Component component : this.components) {
            if (!component.isVisible()) continue;
            component.charTyped(codePoint, modifiers);
        }
        super.charTyped(codePoint, modifiers);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        for (Component component : this.components) {
            if (!component.isVisible()) continue;
            component.keyPressed(key, scanCode, modifiers);
        }
        if (this.bind) {
            if (key == 261) {
                this.module.setBind(0);
            } else {
                this.module.setBind(key);
            }
            this.bind = false;
        }
        super.keyPressed(key, scanCode, modifiers);
    }

    private void drawOutlinedRect(float mouseX, float mouseY, int color) {
        int alpha = (int)(20.0 * this.module.getAnimation().getValue());
        int i = this.module.isState() ? ColorUtils.setAlpha(Theme.rectColor, alpha) : ColorUtils.setAlpha(Theme.mainRectColor, 25);
        DisplayUtils.drawRoundedRect(this.getX() + 0.7f, this.getY() + 1.0f, this.getWidth() - 2.0f, this.getHeight() - 2.0f, 2.0f, i);
        if (MathUtil.isHovered(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), 20.0f)) {
            if (!this.hovered) {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.HAND);
                this.hovered = true;
            }
        } else if (this.hovered) {
            GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
            this.hovered = false;
        }
    }

    private void drawText(MatrixStack stack, int color) {
        this.gavno.update();
        int i = ColorUtils.interpolate(Theme.rectColor, Theme.mainRectColor, (float)this.module.getAnimation().getValue());
        float fontWidth = ClientFonts.msMedium[17].getWidth(this.module.getName());
        if (!this.bind) {
            DisplayUtils.drawShadow(this.getX() + this.getWidth() / 2.0f - fontWidth / 2.0f, this.getY() + 6.0f, Fonts.montserrat.getWidth(this.module.getName(), 7.0f) + 3.0f, Fonts.montserrat.getHeight(7.0f), 10, ColorUtils.setAlpha(color, (int)(72.0 * this.module.getAnimation().getValue())));
        }
        ClientFonts.msMedium[17].drawCenteredString(stack, !this.bind ? this.module.getName() : "", (double)((int)(this.getX() + this.getWidth() / 2.0f)), (double)(this.getY() + 7.5f), color);
        if (this.components.stream().filter(Component::isVisible).count() >= 1L) {
            if (this.bind) {
                Fonts.montserrat.drawCenteredText(stack, (String)(this.module.getBind() == 0 ? "Bind" + this.gavno.getOutput().toString() : KeyStorage.getReverseKey(this.module.getBind())), this.getX() + this.getWidth() / 2.0f, this.getY() + 7.5f, ColorUtils.rgb(161, 164, 177), 6.0f, 0.1f);
            } else {
                DisplayUtils.drawShadowCircle(this.getX() + this.getWidth() - 6.0f, this.getY() + 10.0f, 3.0f, i);
                DisplayUtils.drawCircle(this.getX() + this.getWidth() - 6.0f, this.getY() + 10.0f + 0.5f, 1.5f, i);
            }
        } else if (this.bind) {
            Fonts.montserrat.drawCenteredText(stack, (String)(this.module.getBind() == 0 ? "Bind" + this.gavno.getOutput().toString() : KeyStorage.getReverseKey(this.module.getBind())), this.getX() + this.getWidth() / 2.0f, this.getY() + 7.5f, ColorUtils.rgb(161, 164, 177), 6.0f, 0.1f);
        }
    }

    public Vector4f getROUNDING_VECTOR() {
        return this.ROUNDING_VECTOR;
    }

    public Vector4i getBORDER_COLOR() {
        return this.BORDER_COLOR;
    }

    public Module getModule() {
        return this.module;
    }

    @Override
    public Panel getPanel() {
        return this.panel;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public boolean isOpen() {
        return this.open;
    }

    public boolean isBind() {
        return this.bind;
    }

    public ObjectArrayList<Component> getComponents() {
        return this.components;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public BetterText getGavno() {
        return this.gavno;
    }
}

