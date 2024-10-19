/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.Expensive;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.ui.clickgui.DropDown;
import im.expensive.ui.clickgui.components.ModuleComponent;
import im.expensive.ui.clickgui.components.builder.Component;
import im.expensive.ui.clickgui.components.builder.IBuilder;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class Panel
implements IBuilder {
    private final Category category;
    protected float x;
    protected float y;
    protected final float width = 120.0f;
    protected float height;
    private List<ModuleComponent> modules = new ArrayList<ModuleComponent>();
    private float scroll;
    private float animatedScrool;
    float max = 0.0f;

    public Panel(Category category) {
        this.category = category;
        for (Module module : Expensive.getInstance().getModuleManager().getModules()) {
            if (module.getCategory() != category) continue;
            ModuleComponent component = new ModuleComponent(module);
            component.setPanel(this);
            this.modules.add(component);
        }
        this.updateHeight();
    }

    public void updateHeight() {
        float dynamicHeight = 20.0f;
        for (ModuleComponent component : this.modules) {
            if (!component.isOpen()) continue;
            dynamicHeight += component.getHeight() + 0.5f;
        }
        this.height = Math.max(25.0f, dynamicHeight += 10.0f);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        this.animatedScrool = MathUtil.fast(this.animatedScrool, this.scroll, 10.0f);
        float headerFont = 9.0f;
        this.updateHeight();
        float dynamicHeight = 20.0f;
        for (ModuleComponent component : this.modules) {
            dynamicHeight += component.getHeight() + 0.5f;
        }
        this.height = Math.max(this.height, dynamicHeight += 10.0f);
        DisplayUtils.drawStyledShadowRect(this.x, this.y, 120.0f, this.height);
        DisplayUtils.drawRoundedRect(this.x + 2.0f, this.y + 2.0f, ClientFonts.icons_wex[35].getWidth(this.category.getIcon()) + 5.0f, ClientFonts.icons_wex[35].getFontHeight() + 2.0f, 4.0f, ColorUtils.setAlpha(Theme.rectColor, 70));
        Fonts.montserrat.drawCenteredText(stack, this.category.name(), this.x + 60.0f, this.y + 11.0f - Fonts.montserrat.getHeight(headerFont) / 2.0f, ColorUtils.rgb(255, 255, 255), headerFont, 0.1f);
        ClientFonts.icons_wex[35].drawString(stack, this.category.getIcon(), (double)(this.x + 5.0f), (double)(this.y + 14.0f - ClientFonts.icons_wex[30].getFontHeight() / 2.0f), ColorUtils.getColor(255, 255, 255, 255));
        DisplayUtils.drawRectHorizontalW(this.x, this.y + 18.0f + Fonts.montserrat.getHeight(headerFont) / 2.0f, 120.0, 2.5, ColorUtils.rgba(0, 0, 0, 0), ColorUtils.rgba(0, 0, 0, 63));
        this.drawComponents(stack, mouseX, mouseY);
    }

    private void drawComponents(MatrixStack stack, float mouseX, float mouseY) {
        float animationValue = (float)DropDown.getAnimation().getValue() * DropDown.scale;
        float halfAnimationValueRest = (1.0f - animationValue) / 2.0f;
        float height = this.getHeight();
        float testX = this.getX() + this.getWidth() * halfAnimationValueRest;
        float testY = this.getY() + height * halfAnimationValueRest;
        float testW = this.getWidth() * animationValue;
        float testH = height * animationValue;
        testX = testX * animationValue + ((float)Minecraft.getInstance().getMainWindow().getScaledWidth() - testW) * halfAnimationValueRest;
        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW, testH);
        float offset = -1.0f;
        float header = 25.0f;
        if (this.max > height - header - 10.0f) {
            this.scroll = MathHelper.clamp(this.scroll, -this.max + height - header - 10.0f, 0.0f);
            this.animatedScrool = MathHelper.clamp(this.animatedScrool, -this.max + height - header - 10.0f, 0.0f);
        } else {
            this.scroll = 0.0f;
            this.animatedScrool = 0.0f;
        }
        for (ModuleComponent component : this.modules) {
            component.setX(this.getX() + 2.0f);
            component.setY(this.getY() + header + offset + 3.0f + this.animatedScrool);
            component.setWidth(this.getWidth() - 4.0f);
            component.setHeight(20.0f);
            component.animation.update();
            if (component.animation.getValue() > 0.0) {
                float componentOffset = 0.0f;
                for (Component component2 : component.getComponents()) {
                    if (!component2.isVisible()) continue;
                    componentOffset += component2.getHeight();
                }
                componentOffset = (float)((double)componentOffset * component.animation.getValue());
                component.setHeight(component.getHeight() + componentOffset);
            }
            component.render(stack, mouseX, mouseY);
            offset += component.getHeight() + 0.5f;
        }
        this.max = offset;
        Scissor.unset();
        Scissor.pop();
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        for (ModuleComponent component : this.modules) {
            component.mouseClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        for (ModuleComponent component : this.modules) {
            component.keyPressed(key, scanCode, modifiers);
        }
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (ModuleComponent component : this.modules) {
            component.charTyped(codePoint, modifiers);
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {
        for (ModuleComponent component : this.modules) {
            component.mouseRelease(mouseX, mouseY, button);
        }
    }

    public Category getCategory() {
        return this.category;
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

    public List<ModuleComponent> getModules() {
        return this.modules;
    }

    public float getScroll() {
        return this.scroll;
    }

    public float getAnimatedScrool() {
        return this.animatedScrool;
    }

    public float getMax() {
        return this.max;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setModules(List<ModuleComponent> modules) {
        this.modules = modules;
    }

    public void setScroll(float scroll) {
        this.scroll = scroll;
    }

    public void setAnimatedScrool(float animatedScrool) {
        this.animatedScrool = animatedScrool;
    }

    public void setMax(float max) {
        this.max = max;
    }
}

