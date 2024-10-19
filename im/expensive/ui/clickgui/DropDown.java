/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.Expensive;
import im.expensive.modules.api.Category;
import im.expensive.modules.impl.render.ClickGui;
import im.expensive.ui.clickgui.Panel;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.SoundUtil;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.client.Vec2i;
import im.expensive.utils.render.Cursors;
import im.expensive.utils.render.GifUtils;
import im.expensive.utils.render.KawaseBlur;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

public class DropDown
extends Screen
implements IMinecraft {
    private static final Animation animation = new Animation();
    private static final Animation gradientAnimation = new Animation();
    public static float scale = 1.0f;
    private final List<Panel> panels = new ArrayList<Panel>();
    private float updownPanel = 130.0f;
    private float movePanel = 0.0f;

    public DropDown(ITextComponent titleIn) {
        super(titleIn);
        Category[] categories;
        for (Category category : categories = Category.values()) {
            this.panels.add(new Panel(category));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        SoundUtil.playSound("guiopen.wav");
        animation.animate(1.0, 0.25, Easings.BACK_OUT);
        gradientAnimation.animate(1.0, 0.25, Easings.EXPO_OUT);
        super.init();
    }

    @Override
    public void closeScreen() {
        SoundUtil.playSound("guiclose.wav");
        super.closeScreen();
        GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        boolean ctrlDown;
        boolean bl = ctrlDown = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), 341) == 1 || GLFW.glfwGetKey(mc.getMainWindow().getHandle(), 345) == 1;
        if (ctrlDown) {
            this.movePanel = (float)((double)this.movePanel + delta * 5.0);
        } else {
            this.updownPanel = (float)((double)this.updownPanel - delta * 20.0);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        animation.update();
        gradientAnimation.update();
        if (animation.getValue() < 0.1 && gradientAnimation.getValue() < 0.1) {
            this.closeScreen();
        }
        float off = 10.0f;
        float width = (float)this.panels.size() * 130.0f;
        this.updateScaleBasedOnScreenWidth();
        int windowWidth = ClientUtil.calc(mc.getMainWindow().getScaledWidth());
        int windowHeight = ClientUtil.calc(mc.getMainWindow().getScaledHeight());
        Vec2i fixMouse = this.adjustMouseCoordinates(mouseX, mouseY);
        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();
        ClickGui clickGui = Expensive.getInstance().getModuleManager().getClickGui();
        if (((Boolean)ClickGui.background.get()).booleanValue()) {
            DisplayUtils.drawContrast(1.0f - (float)(gradientAnimation.getValue() / 3.0) * 0.7f);
            DisplayUtils.drawWhite((float)gradientAnimation.getValue() * 0.7f);
        }
        if (((Boolean)ClickGui.blur.get()).booleanValue()) {
            KawaseBlur.blur.updateBlur(((Float)ClickGui.blurPower.get()).floatValue() - 1.0f, ((Float)ClickGui.blurPower.get()).intValue());
            KawaseBlur.blur.BLURRED.draw();
        }
        if (((Boolean)ClickGui.gradient.get()).booleanValue()) {
            DisplayUtils.drawRectHorizontalW(0.0, 0.0 - this.scaled().y / 4.0, Minecraft.getInstance().getMainWindow().getScaledWidth(), ((double)Minecraft.getInstance().getMainWindow().getScaledHeight() + this.scaled().y / 3.0) / gradientAnimation.getValue(), ColorUtils.setAlpha(Theme.mainRectColor, (int)(255.0 * gradientAnimation.getValue())), ColorUtils.rgba(0, 0, 0, 0));
        }
        if (((Boolean)ClickGui.images.get()).booleanValue()) {
            GifUtils gifUtils = new GifUtils();
            String image = ((String)ClickGui.imageType.get()).toLowerCase();
            Object path = "eva/images/gui/";
            int totalFrames = 0;
            int frameDelay = 0;
            boolean fromZero = false;
            if (ClickGui.imageType.is("Miku")) {
                totalFrames = 9;
                frameDelay = 40;
            } else if (ClickGui.imageType.is("Novoura")) {
                totalFrames = 4;
                frameDelay = 80;
            }
            if (Arrays.asList("Miku", "Novoura").contains(ClickGui.imageType.get())) {
                int i = gifUtils.getFrame(totalFrames, frameDelay, fromZero);
                path = "eva/images/gif/" + ((String)ClickGui.imageType.get()).toLowerCase() + "/frame_" + i;
                image = "";
            }
            int size = 256;
            float x1 = windowWidth - size;
            float x2 = windowWidth;
            float y1 = windowHeight - size;
            float y2 = windowHeight;
            DisplayUtils.drawImage(new ResourceLocation((String)path + image + ".png"), x1, y1, x2 - x1, y2 - y1, ColorUtils.reAlphaInt(-1, (int)(255.0 * gradientAnimation.getValue())));
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)windowWidth / 2.0f, (float)windowHeight / 2.0f, 0.0f);
        GlStateManager.scaled(animation.getValue(), animation.getValue(), 1.0);
        GlStateManager.scaled(scale, scale, 1.0);
        GlStateManager.translatef((float)(-windowWidth) / 2.0f, (float)(-windowHeight) / 2.0f, 0.0f);
        for (Panel panel : this.panels) {
            panel.setY((float)windowHeight / 2.0f - 110.0f - this.updownPanel);
            panel.setX((float)windowWidth / 2.0f - width / 2.0f + (float)panel.getCategory().ordinal() * (120.0f + off) + off / 2.0f - this.movePanel);
            float animationValue = (float)animation.getValue() * scale;
            float halfAnimationValueRest = (1.0f - animationValue) / 2.0f;
            float testX = panel.getX() + panel.getWidth() * halfAnimationValueRest;
            float testY = panel.getY() + panel.getHeight() * halfAnimationValueRest;
            float testW = panel.getWidth() * animationValue;
            float testH = panel.getHeight() * animationValue;
            testX = testX * animationValue + ((float)windowWidth - testW) * halfAnimationValueRest;
            Scissor.push();
            Scissor.setFromComponentCoordinates(0.0, 0.0, this.scaled().x, this.scaled().y);
            panel.render(matrixStack, mouseX, mouseY);
            Scissor.unset();
            Scissor.pop();
        }
        GlStateManager.popMatrix();
        DropDown.mc.gameRenderer.setupOverlayRendering();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)windowWidth / 2.0f, (float)windowHeight / 2.0f, 0.0f);
        GlStateManager.scaled(animation.getValue(), animation.getValue(), 1.0);
        GlStateManager.scaled(scale, scale, 1.0);
        GlStateManager.translatef((float)(-windowWidth) / 2.0f, (float)(-windowHeight) / 2.0f, 0.0f);
        GlStateManager.popMatrix();
    }

    private void updateScaleBasedOnScreenWidth() {
        float screenWidth;
        float totalPanelWidth = (float)this.panels.size() * 115.0f;
        if (totalPanelWidth >= (screenWidth = (float)mc.getMainWindow().getScaledWidth())) {
            scale = screenWidth / totalPanelWidth;
            scale = MathHelper.clamp(scale, 0.5f, 1.0f);
        } else {
            scale = 1.0f;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Panel panel : this.panels) {
            panel.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == 256) {
            animation.animate(0.0, 0.25, Easings.BACK_OUT);
            gradientAnimation.animate(0.0, 0.25, Easings.EXPO_OUT);
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private Vec2i adjustMouseCoordinates(int mouseX, int mouseY) {
        int windowWidth = mc.getMainWindow().getScaledWidth();
        int windowHeight = mc.getMainWindow().getScaledHeight();
        float adjustedMouseX = ((float)mouseX - (float)windowWidth / 2.0f) / scale + (float)windowWidth / 2.0f;
        float adjustedMouseY = ((float)mouseY - (float)windowHeight / 2.0f) / scale + (float)windowHeight / 2.0f;
        return new Vec2i((int)adjustedMouseX, (int)adjustedMouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixMouse = this.adjustMouseCoordinates((int)mouseX, (int)mouseY);
        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();
        for (Panel panel : this.panels) {
            panel.mouseClick((float)mouseX, (float)mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vec2i fixMouse = this.adjustMouseCoordinates((int)mouseX, (int)mouseY);
        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();
        for (Panel panel : this.panels) {
            panel.mouseRelease((float)mouseX, (float)mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public static Animation getAnimation() {
        return animation;
    }
}

