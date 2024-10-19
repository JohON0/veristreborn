/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.oldclickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.Expensive;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.ui.oldclickgui.ClickGuiScreen;
import im.expensive.ui.oldclickgui.components.ModuleComponent;
import im.expensive.ui.oldclickgui.impl.Component;
import im.expensive.ui.oldclickgui.impl.IBuilder;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;

public class Panel
implements IBuilder {
    private final Category category;
    public float x;
    public float y;
    public final float width = 120.0f;
    public final float height = 280.0f;
    private List<ModuleComponent> modules = new ArrayList<ModuleComponent>();
    private float scroll;
    private float animatedScrool;
    private float scrollbarHeight;
    private float scrollbarY;
    private boolean draggingScrollbar = false;
    private float lastMouseY;
    float max = 0.0f;

    public Panel(Category category) {
        this.category = category;
        for (Module module : Expensive.getInstance().getModuleManager().getModules()) {
            if (module.getCategory() != category) continue;
            ModuleComponent component = new ModuleComponent(module);
            component.setPanel(this);
            this.modules.add(component);
        }
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        float header = 20.0f;
        float headerFont = 8.0f;
        float i = 4.0f;
        float o = 10.0f;
        this.animatedScrool = MathUtil.fast(this.animatedScrool, this.scroll, o);
        this.scrollbarHeight = MathHelper.clamp((280.0f - header - o) * (280.0f - header - o) / this.max, o, 280.0f - header - o);
        this.scrollbarY = this.getY() + header + -this.getScroll() / (this.max - 280.0f + header + i) * (280.0f - header - i - this.scrollbarHeight) + o;
        this.scrollbarHeight = MathHelper.clamp(this.scrollbarHeight, 18.0f, 280.0f - header - o);
        this.scrollbarY = MathHelper.clamp(this.scrollbarY, this.getY() + header, this.getY() + 280.0f - this.scrollbarHeight - i);
        if (this.draggingScrollbar) {
            float deltaY = mouseY - this.lastMouseY;
            float scrollRange = this.max - 280.0f + header + 10.0f;
            this.scroll -= deltaY * 2.0f * (scrollRange / (280.0f - header - 10.0f));
            this.scroll = MathHelper.clamp(this.scroll, -scrollRange, 0.0f);
            this.lastMouseY = mouseY;
        }
        DisplayUtils.drawShadow(this.x, this.y, 120.0f, 280.0f, 8, ColorUtils.rgba(20, 20, 20, 180));
        DisplayUtils.drawRoundedRect(this.x, this.y, 120.0f, 280.0f, 8.0f, ColorUtils.rgba(20, 20, 20, 180));
        DisplayUtils.drawShadow(this.x + 2.5f, this.y + 24.0f, 115.0f, 253.0f, 8, ColorUtils.rgba(10, 10, 10, 90));
        DisplayUtils.drawRoundedRect(this.x + 2.5f, this.y + 24.0f, 115.0f, 253.0f, 6.0f, ColorUtils.rgba(10, 10, 10, 90));
        ClientFonts.icons_wex[30].drawString(stack, this.category.getIcon(), (double)(this.x + 5.0f), (double)(this.y + 9.0f), ColorUtils.getColor(255, 255, 255, 255));
        Fonts.montserrat.drawCenteredText(stack, this.category.name(), this.x + 60.0f, this.y + header / 2.0f - Fonts.montserrat.getHeight(headerFont) / 2.0f - 1.0f + 3.0f, ColorUtils.rgb(255, 255, 255), headerFont, 0.05f);
        this.drawComponents(stack, mouseX, mouseY);
        if (this.max > 280.0f - header - 10.0f) {
            this.setScroll(MathHelper.clamp(this.getScroll(), -this.max + 280.0f - header - 10.0f, 0.0f));
            this.setAnimatedScrool(MathHelper.clamp(this.animatedScrool, -this.max + 280.0f - header - 10.0f, 0.0f));
            if (this.scroll >= 0.0f) {
                this.setScroll(0.0f);
                this.setAnimatedScrool(0.0f);
            }
            int u = ColorUtils.rgba(90, 90, 90, 90);
            float scrollx = this.getX() + this.getWidth() - 2.5f;
            DisplayUtils.drawRoundedRect(scrollx, this.scrollbarY, 2.5f, this.scrollbarHeight, new Vector4f(4.0f, 4.0f, 0.0f, 0.0f), new Vector4i(u, u, u, u));
        } else {
            this.setScroll(0.0f);
            this.setAnimatedScrool(0.0f);
        }
    }

    public void drawComponents(MatrixStack stack, float mouseX, float mouseY) {
        float animationValue = (float)ClickGuiScreen.getAnimation().getValue() * ClickGuiScreen.scale;
        float halfAnimationValueRest = (1.0f - animationValue) / 2.0f;
        float height = this.getHeight();
        float testX = this.getX() + this.getWidth() * halfAnimationValueRest;
        float testY = this.getY() + 25.0f + height * halfAnimationValueRest;
        float testW = this.getWidth() * animationValue;
        float testH = height * animationValue;
        testX = testX * animationValue + ((float)Minecraft.getInstance().getMainWindow().getScaledWidth() - testW) * halfAnimationValueRest;
        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW, testH - 29.0f);
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
            component.setX(this.getX() + 5.0f);
            component.setY(this.getY() + header + offset + 3.0f + this.animatedScrool);
            component.setWidth(this.getWidth() - 10.0f);
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
            offset += component.getHeight() + 3.5f;
        }
        this.max = offset;
        Scissor.unset();
        Scissor.pop();
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (!DisplayUtils.isInRegion((double)mouseX, (double)mouseY, this.x + 2.5f, this.y + 24.0f, 115.0f, 253.0f)) {
            return;
        }
        for (ModuleComponent component : this.modules) {
            component.mouseClick(mouseX, mouseY, button);
        }
        float scrollx = this.getX() + this.getWidth() - 3.0f;
        if (button == 0 && MathUtil.isHovered(mouseX, mouseY, scrollx, this.scrollbarY - 1.0f, 3.5f, this.scrollbarHeight + 2.0f)) {
            System.out.println(true);
            this.draggingScrollbar = true;
            this.lastMouseY = mouseY;
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
        if (!DisplayUtils.isInRegion((double)mouseX, (double)mouseY, this.x + 2.5f, this.y + 24.0f, 115.0f, 253.0f)) {
            return;
        }
        for (ModuleComponent component : this.modules) {
            component.mouseRelease(mouseX, mouseY, button);
        }
        if (button == 0) {
            this.draggingScrollbar = false;
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

    public float getScrollbarHeight() {
        return this.scrollbarHeight;
    }

    public float getScrollbarY() {
        return this.scrollbarY;
    }

    public boolean isDraggingScrollbar() {
        return this.draggingScrollbar;
    }

    public float getLastMouseY() {
        return this.lastMouseY;
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

    public void setModules(List<ModuleComponent> modules) {
        this.modules = modules;
    }

    public void setScroll(float scroll) {
        this.scroll = scroll;
    }

    public void setAnimatedScrool(float animatedScrool) {
        this.animatedScrool = animatedScrool;
    }

    public void setScrollbarHeight(float scrollbarHeight) {
        this.scrollbarHeight = scrollbarHeight;
    }

    public void setScrollbarY(float scrollbarY) {
        this.scrollbarY = scrollbarY;
    }

    public void setDraggingScrollbar(boolean draggingScrollbar) {
        this.draggingScrollbar = draggingScrollbar;
    }

    public void setLastMouseY(float lastMouseY) {
        this.lastMouseY = lastMouseY;
    }

    public void setMax(float max) {
        this.max = max;
    }
}

