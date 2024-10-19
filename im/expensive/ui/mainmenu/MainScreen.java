/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.mainmenu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.Expensive;
import im.expensive.utils.SoundUtil;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.client.Vec2i;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.shader.ShaderUtil;
import im.expensive.utils.text.BetterText;
import im.expensive.utils.text.font.ClientFonts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MainScreen
extends Screen
implements IMinecraft {
    public final StopWatch timer = new StopWatch();
    public static float o = 0.0f;
    private final ResourceLocation backmenu = new ResourceLocation("eva/images/backmenu.png");
    private final List<Button> buttons = new ArrayList<Button>();
    private final StopWatch stopWatch = new StopWatch();
    static boolean start = false;
    private final BetterText gavno = new BetterText(List.of(" <3"," >_<", " UwU", " O_O", " OwO", " :>", " <3", " >w<", "~~"), 2000);

    public MainScreen() {
        super(ITextComponent.getTextComponentOrEmpty(""));
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        float widthButton = 82.5f;
        float heightButton = 20.0f;
        float x = (float)ClientUtil.calc(width) / 2.0f - widthButton / 2.0f;
        float y = Math.round((float)ClientUtil.calc(height) / 2.0f + 1.0f) - 60;
        this.buttons.clear();
        this.buttons.add(new Button(x, y, widthButton, heightButton, "singleplayer", () -> mc.displayGuiScreen(new WorldSelectionScreen(this))));
        this.buttons.add(new Button(x, y += 25.0f, widthButton, heightButton, "multiplayer", () -> mc.displayGuiScreen(new MultiplayerScreen(this))));
        this.buttons.add(new Button(x, y += 25.0f, widthButton, heightButton, "altmanager", () -> mc.displayGuiScreen(Expensive.getInstance().getAltScreen())));
        this.buttons.add(new Button(x, y += 25.0f, widthButton, heightButton, "options", () -> mc.displayGuiScreen(new OptionsScreen(this, MainScreen.mc.gameSettings))));
        this.buttons.add(new Button(x, y += 25.0f, widthButton, heightButton, "exit", mc::shutdownMinecraftApplet));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        MainWindow mainWindow = mc.getMainWindow();
        DisplayUtils.drawRoundedRect(0.0f, 0.0f, (float)mainWindow.getScaledWidth(), (float)mainWindow.getScaledHeight(), 0.0f, ColorUtils.rgb(15, 15, 15));
        float y = Math.round((float)ClientUtil.calc(this.height) / 2.0f - ClientFonts.msSemiBold[22].getFontHeight()) - 60;
        MainScreen.mc.gameRenderer.setupOverlayRendering(2);
        for (float i = 0.0f; i < 1488.0f; i += 1.0f) {
            if (!this.timer.isReached(10L)) continue;
            o += 1.0f;
            i = 0.0f;
            this.timer.reset();
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        ShaderUtil.MainMenuShader.attach();
        ShaderUtil.MainMenuShader.setUniform("time", o / 30.0f);
        ShaderUtil.MainMenuShader.setUniform("width", new float[]{mainWindow.getWidth()});
        ShaderUtil.MainMenuShader.setUniform("height", new float[]{mainWindow.getHeight()});
        DisplayUtils.drawQuads(0.0f, 0.0f, mainWindow.getScaledWidth(), mainWindow.getScaledHeight(), 7);
        ShaderUtil.MainMenuShader.detach();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        float widthRect = 92.5f;
        float xRect = (float)mainWindow.getScaledWidth() / 2.0f - widthRect / 2.0f;
        float heightRect = 150.0f;
        DisplayUtils.drawShadow((float)(mainWindow.getScaledWidth() / 2 - 4) - ClientFonts.msSemiBold[22].getWidth("Verist") / 2.0f, y + 2.0f, ClientFonts.msSemiBold[22].getWidth("Verist") + 8.0f, ClientFonts.msSemiBold[22].getFontHeight(), 12, ColorUtils.reAlphaInt(-1, 80));
        DisplayUtils.drawShadow(xRect, y - 5.0f, widthRect, heightRect, 8, ColorUtils.rgba(31, 26, 43, 140));
        DisplayUtils.drawRoundedRect(xRect, y - 5.0f, widthRect, heightRect, 4.0f, ColorUtils.rgba(31, 26, 43, 140));
        ClientFonts.msSemiBold[22].drawCenteredString(matrixStack, "Verist", (double)(mainWindow.getScaledWidth() / 2), (double)(y + 2.0f), -1);
        ClientFonts.msSemiBold[14].drawString(matrixStack, this.setMessage(), 3.0, (double)((float)mainWindow.getScaledHeight() - ClientFonts.msSemiBold[14].getFontHeight() + 1.0f), -1);
        this.drawButtons(matrixStack, mouseX, mouseY, partialTicks);
        MainScreen.mc.gameRenderer.setupOverlayRendering();
    }

    private String setMessage() {
        this.gavno.update();
        String emoji = this.gavno.getOutput().toString();
        String userName = ClientUtil.getUsername() + emoji;
        return ClientUtil.getGreetingMessage() + ", " + userName;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ClientUtil.getMouse((int)mouseX, (int)mouseY);
        this.buttons.forEach(b -> b.click(fixed.getX(), fixed.getY(), button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawButtons(MatrixStack stack, int mX, int mY, float pt) {
        this.buttons.forEach(b -> b.render(stack, mX, mY, pt));
    }

    private class Button {
        private final float x;
        private final float y;
        private final float width;
        private final float height;
        private String text;
        private Runnable action;
        public float animation;

        public Button(float x, float y, float width, float height, String text, Runnable action) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.text = text;
            this.action = action;
        }

        public void render(MatrixStack stack, int mouseX, int mouseY, float pt) {
            DisplayUtils.drawShadow(this.x, this.y + 2.0f, this.width, this.height, 8, ColorUtils.rgba(35, 30, 48, 180));
            DisplayUtils.drawRoundedRect(this.x, this.y + 2.0f, this.width, this.height, 4.0f, ColorUtils.rgba(35, 30, 48, 180));
            int color = MathUtil.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height) ? -1 : ColorUtils.rgba(100, 100, 100, 255);
            Fonts.montserrat.drawCenteredText(stack, this.text, this.x + this.width / 2.0f, this.y + this.height / 2.0f - 2.0f, color, 8.0f, 0.05f);
        }

        public void click(int mouseX, int mouseY, int button) {
            if (MathUtil.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height)) {
                this.action.run();
                SoundUtil.playSound("select.wav", 0.1f);
            }
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
    }
}

