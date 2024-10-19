/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.mainmenu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.Expensive;
import im.expensive.config.AltConfig;
import im.expensive.ui.mainmenu.Alt;
import im.expensive.ui.mainmenu.MainScreen;
import im.expensive.utils.SoundUtil;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.client.Vec2i;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.player.MouseUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.gl.Stencil;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.shader.ShaderUtil;
import im.expensive.utils.text.font.ClientFonts;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

public class AltScreen
extends Screen
implements IMinecraft {
    public final StopWatch timer = new StopWatch();
    public final List<Alt> alts = new ArrayList<Alt>();
    public float scroll;
    public float scrollAn;
    private String altName = "";
    private boolean typing;
    float minus = 14.0f;

    public AltScreen() {
        super(new StringTextComponent(""));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        MainWindow mainWindow = mc.getMainWindow();
        this.scrollAn = MathUtil.lerp(this.scrollAn, this.scroll, 5.0f);
        for (float i = 0.0f; i < 1488.0f; i += 1.0f) {
            if (!this.timer.isReached(10L)) continue;
            MainScreen.o += 1.0f;
            i = 0.0f;
            this.timer.reset();
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        ShaderUtil.MainMenuShader.attach();
        ShaderUtil.MainMenuShader.setUniform("time", MainScreen.o / 22.0f);
        ShaderUtil.MainMenuShader.setUniform("width", new float[]{mainWindow.getWidth()});
        ShaderUtil.MainMenuShader.setUniform("height", new float[]{mainWindow.getHeight()});
        DisplayUtils.drawQuads(0.0f, 0.0f, mainWindow.getScaledWidth(), mainWindow.getScaledHeight(), 7);
        ShaderUtil.MainMenuShader.detach();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        AltScreen.mc.gameRenderer.setupOverlayRendering(2);
        float offset = 6.0f;
        float width = 250.0f;
        float height = 270.0f;
        float x = (float)mc.getMainWindow().getScaledWidth() / 2.0f - width / 2.0f;
        float y = (float)mc.getMainWindow().getScaledHeight() / 2.0f - height / 2.0f;
        DisplayUtils.drawShadow(x - offset, y - offset, width + offset * 2.0f, height + offset * 2.0f, 8, ColorUtils.rgba(31, 26, 43, 140));
        DisplayUtils.drawRoundedRect(x - offset, y - offset, width + offset * 2.0f, height + offset * 2.0f, 4.0f, ColorUtils.rgba(31, 26, 43, 140));
        float textLogoWidth = ClientFonts.msSemiBold[22].getWidth("AltManager") / 2.0f;
        DisplayUtils.drawShadow(x + offset + width / 2.0f - textLogoWidth - 6.0f, y + offset / 2.0f, textLogoWidth * 2.0f + 4.0f, ClientFonts.msSemiBold[22].getFontHeight(), 12, ColorUtils.reAlphaInt(-1, 40));
        ClientFonts.msSemiBold[22].drawCenteredString(matrixStack, "AltManager", (double)(x + width / 2.0f), (double)(y + offset / 2.0f + 3.0f), -1);
        DisplayUtils.drawShadow(x + offset, y + offset + 20.0f, width - offset * 2.0f, 20.0f, 8, ColorUtils.rgba(35, 30, 48, 160));
        DisplayUtils.drawRoundedRect(x + offset, y + offset + 20.0f, width - offset * 2.0f, 20.0f, 2.0f, ColorUtils.rgba(35, 30, 48, 160));
        Scissor.push();
        Scissor.setFromComponentCoordinates(x + offset, y + offset + 20.0f, width - offset - 20.0f, 20.0);
        ClientFonts.msSemiBold[15].drawString(matrixStack, (String)(this.typing ? this.altName + (this.typing ? "|" : "") : "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0441\u0432\u043e\u0439 \u043d\u0438\u043a!"), (double)(x + offset + 4.0f), (double)(y + offset + 28.0f), ColorUtils.rgb(152, 152, 152));
        Scissor.unset();
        Scissor.pop();
        int col = ColorUtils.rgb(38, 33, 54);
        DisplayUtils.drawRoundedRect(x + width - offset - 20.0f, y + offset + 20.0f, ClientFonts.interBold[22].getWidth("?") + 12.0f, 20.0f, new Vector4f(0.0f, 0.0f, 3.0f, 3.0f), new Vector4i(col, col, col, col));
        ClientFonts.interBold[22].drawCenteredString(matrixStack, "?", (double)(x + width - offset - 10.0f), (double)(y + offset + 26.0f), -1);
        DisplayUtils.drawShadow(x + width / 2.0f - ClientFonts.msSemiBold[22].getWidth("Accounts") / 2.0f - 3.0f, y + offset + 63.0f - this.minus - 4.0f, ClientFonts.msSemiBold[22].getWidth("Accounts") + 6.0f, ClientFonts.msSemiBold[22].getFontHeight() + 6.0f, 12, ColorUtils.reAlphaInt(-1, 25));
        ClientFonts.msSemiBold[22].drawCenteredString(matrixStack, "Accounts", (double)(x + width / 2.0f), (double)(y + offset + 63.0f - this.minus), -1);
        DisplayUtils.drawShadow(x + offset, y + offset + 80.0f - this.minus, width - offset * 2.0f, 177.5f + this.minus, 8, ColorUtils.rgba(35, 30, 48, 160));
        DisplayUtils.drawRoundedRect(x + offset, y + offset + 80.0f - this.minus, width - offset * 2.0f, 177.5f + this.minus, 2.0f, ColorUtils.rgba(35, 30, 48, 160));
        if (this.alts.isEmpty()) {
            ClientFonts.msSemiBold[22].drawCenteredString(matrixStack, "\u0427\u043e\u0442\u0430 \u043f\u0443\u0441\u0442\u0435\u043d\u044c\u043a\u043e >_<", (double)(x + width / 2.0f), (double)(y + offset + 168.75f - this.minus), -1);
        }
        float size = 0.0f;
        float iter = this.scrollAn;
        float offsetAccounts = 0.0f;
        Scissor.push();
        Scissor.setFromComponentCoordinates(x + offset, y + offset + 80.0f - this.minus, width - offset * 2.0f, 177.5f + this.minus);
        for (Alt alt : this.alts) {
            float scrollY = y + iter * 22.0f;
            int color = AltScreen.mc.session.getUsername().equals(alt.name) ? ColorUtils.rgba(88, 230, 78, 170) : ColorUtils.rgba(46, 42, 59, 140);
            DisplayUtils.drawShadow(x + offset + 2.0f, scrollY + offset + 82.0f + offsetAccounts - this.minus, width - offset * 2.0f - 4.0f, 20.0f, 6, color);
            DisplayUtils.drawRoundedRect(x + offset + 2.0f, scrollY + offset + 82.0f + offsetAccounts - this.minus, width - offset * 2.0f - 4.0f, 20.0f, 2.0f, color);
            ClientFonts.msSemiBold[15].drawString(matrixStack, alt.name, (double)(x + offset + 24.0f), (double)(scrollY + offset + 90.0f + offsetAccounts - this.minus), -1);
            Stencil.initStencilToWrite();
            DisplayUtils.drawRoundedRect(x + offset + 4.0f + 0.5f, scrollY + offset + 84.0f + offsetAccounts - this.minus, 16.0f, 16.0f, 2.0f, Color.BLACK.getRGB());
            Stencil.readStencilBuffer(1);
            mc.getTextureManager().bindTexture(alt.skin);
            AbstractGui.drawScaledCustomSizeModalRect(x + offset + 4.0f + 0.5f, scrollY + offset + 84.0f + offsetAccounts - this.minus, 8.0f, 8.0f, 8.0f, 8.0f, 16.0f, 16.0f, 64.0f, 64.0f);
            Stencil.uninitStencilBuffer();
            iter += 1.0f;
            size += 1.0f;
        }
        this.scroll = MathHelper.clamp(this.scroll, size > 8.0f ? -size + 4.0f : 0.0f, 0.0f);
        Scissor.unset();
        Scissor.pop();
        ClientFonts.msSemiBold[12].drawCenteredString(matrixStack, "\u0412\u0430\u0448 \u043d\u0438\u043a - " + AltScreen.mc.session.getUsername() + ".", (double)(x + width / 2.0f), (double)(y + height - offset / 2.0f + 1.0f), ColorUtils.rgb(180, 180, 180));
        AltScreen.mc.gameRenderer.setupOverlayRendering();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean ctrlDown;
        if (keyCode == 259 && !this.altName.isEmpty() && this.typing) {
            this.altName = this.altName.substring(0, this.altName.length() - 1);
        }
        if (keyCode == 257) {
            if (!this.altName.isEmpty() && this.altName.length() >= 3) {
                this.alts.add(new Alt(this.altName));
                AltConfig.updateFile();
                SoundUtil.playSound("friendadd.wav");
            }
            this.typing = false;
            this.altName = "";
        }
        if (keyCode == 256 && this.typing) {
            this.typing = false;
            this.altName = "";
        }
        boolean bl = ctrlDown = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), 341) == 1 || GLFW.glfwGetKey(mc.getMainWindow().getHandle(), 345) == 1;
        if (this.typing) {
            if (ctrlDown && keyCode == 86) {
                try {
                    this.altName = this.altName + GLFW.glfwGetClipboardString(mc.getMainWindow().getHandle());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ctrlDown && keyCode == 259) {
                try {
                    this.altName = "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.altName.length() <= 20) {
            this.altName = this.altName + Character.toString(codePoint);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float y;
        Vec2i fixed = MathUtil.getMouse2i((int)mouseX, (int)mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        float offset = 6.0f;
        float width = 250.0f;
        float height = 270.0f;
        float x = (float)mc.getMainWindow().getScaledWidth() / 2.0f - width / 2.0f;
        if (DisplayUtils.isInRegion(mouseX, mouseY, x + width - offset - 20.0f, (y = (float)mc.getMainWindow().getScaledHeight() / 2.0f - height / 2.0f) + offset + 22.0f, ClientFonts.msSemiBold[22].getWidth("?") + 12.0f, 20.0f)) {
            this.alts.add(new Alt(Expensive.getInstance().randomNickname()));
            AltConfig.updateFile();
            SoundUtil.playSound("friendadd.wav");
        }
        if (DisplayUtils.isInRegion(mouseX, mouseY, x + offset, y + offset + 25.0f, width - offset - 22.0f, 20.0f) && !DisplayUtils.isInRegion(mouseX, mouseY, x + width - offset - 12.5f, y + offset + 31.0f, ClientFonts.msSemiBold[22].getWidth("?"), ClientFonts.msSemiBold[22].getFontHeight())) {
            this.typing = !this.typing;
        }
        float iter = this.scrollAn;
        float offsetAccounts = 0.0f;
        Iterator<Alt> iterator2 = this.alts.iterator();
        while (iterator2.hasNext()) {
            Alt account = iterator2.next();
            float scrollY = y + iter * 22.0f;
            if (DisplayUtils.isInRegion(mouseX, mouseY, x + offset + 2.0f, scrollY + offset + 80.0f + offsetAccounts - this.minus, width - offset * 2.0f - 4.0f, 20.0f)) {
                if (button == 0) {
                    SoundUtil.playSound("warning.wav");
                    AltScreen.mc.session = new Session(account.name, "", "", "mojang");
                } else if (button == 1) {
                    iterator2.remove();
                    AltConfig.updateFile();
                    SoundUtil.playSound("friendremove.wav");
                }
            }
            iter += 1.0f;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float y;
        Vec2i fixed = MathUtil.getMouse2i((int)mouseX, (int)mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        float offset = 6.0f;
        float width = 250.0f;
        float height = 270.0f;
        float x = (float)mc.getMainWindow().getScaledWidth() / 2.0f - width / 2.0f;
        if (MouseUtil.isHovered(mouseX, mouseY, x + offset, (y = (float)mc.getMainWindow().getScaledHeight() / 2.0f - height / 2.0f) + offset + 80.0f, width - offset * 2.0f, 177.5f)) {
            this.scroll = (float)((double)this.scroll + delta * 1.0);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
    }

    @Override
    public void tick() {
        super.tick();
    }
}

