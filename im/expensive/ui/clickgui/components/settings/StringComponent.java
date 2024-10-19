/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.clickgui.components.settings;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.modules.settings.impl.StringSetting;
import im.expensive.ui.clickgui.components.builder.Component;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.Cursors;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.glfw.GLFW;

public class StringComponent
extends Component {
    private final StringSetting setting;
    private boolean typing;
    private String text = "";
    private static final int X_OFFSET = 5;
    private static final int Y_OFFSET = 10;
    private static final int WIDTH_OFFSET = -9;
    private static final int TEXT_Y_OFFSET = -7;
    private boolean hovered = false;

    public StringComponent(StringSetting setting) {
        this.setting = setting;
        this.setHeight(24.0f);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        this.text = (String)this.setting.get();
        if (this.setting.isOnlyNumber() && !NumberUtils.isNumber(this.text)) {
            this.text = this.text.replaceAll("[a-zA-Z]", "");
        }
        float x = this.calculateX();
        float y = this.calculateY();
        float width = this.calculateWidth();
        String settingName = this.setting.getName();
        String settingDesc = this.setting.getDescription();
        String textToDraw = (String)this.setting.get();
        if (!this.typing && ((String)this.setting.get()).isEmpty()) {
            textToDraw = settingDesc;
        }
        if (this.setting.isOnlyNumber() && !NumberUtils.isNumber(textToDraw)) {
            textToDraw = textToDraw.replaceAll("[a-zA-Z]", "");
        }
        float height = this.calculateHeight(textToDraw, width - 1.0f);
        this.drawSettingName(stack, settingName, x, y);
        this.drawBackground(x, y, width, height);
        this.drawTextWithLineBreaks(stack, textToDraw + (this.typing && this.text.length() < 59 && System.currentTimeMillis() % 1000L > 500L ? "_" : ""), x + 1.0f, y + Fonts.montserrat.getHeight(6.0f) / 2.0f, width - 1.0f);
        if (this.isHovered(mouseX, mouseY)) {
            if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height)) {
                if (!this.hovered) {
                    GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.IBEAM);
                    this.hovered = true;
                }
            } else if (this.hovered) {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
                this.hovered = false;
            }
        }
        this.setHeight(height + 12.0f);
    }

    private void drawTextWithLineBreaks(MatrixStack stack, String text, float x, float y, float maxWidth) {
        String[] lines = text.split("\n");
        float currentY = y;
        for (String line : lines) {
            List<String> wrappedLines = this.wrapText(line, 6.0f, maxWidth);
            for (String wrappedLine : wrappedLines) {
                Fonts.montserrat.drawText(stack, wrappedLine, x, currentY, ColorUtils.rgba(255, 255, 255, 255), 6.0f);
                currentY += Fonts.montserrat.getHeight(6.0f);
            }
        }
    }

    private List<String> wrapText(String text, float size, float maxWidth) {
        ArrayList<String> lines = new ArrayList<String>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (Fonts.montserrat.getWidth(word, size) <= maxWidth) {
                if (Fonts.montserrat.getWidth(currentLine.toString() + word, size) <= maxWidth) {
                    currentLine.append(word).append(" ");
                    continue;
                }
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word).append(" ");
                continue;
            }
            if (!currentLine.toString().isEmpty()) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
            currentLine = this.breakAndAddWord(word, currentLine, size, maxWidth, lines);
        }
        if (!currentLine.toString().isEmpty()) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private StringBuilder breakAndAddWord(String word, StringBuilder currentLine, float size, float maxWidth, List<String> lines) {
        int wordLength = word.length();
        for (int i = 0; i < wordLength; ++i) {
            char c = word.charAt(i);
            String nextPart = currentLine.toString() + c;
            if (Fonts.montserrat.getWidth(nextPart, size) <= maxWidth) {
                currentLine.append(c);
                continue;
            }
            lines.add(currentLine.toString());
            currentLine = new StringBuilder(String.valueOf(c));
        }
        return currentLine;
    }

    private float calculateX() {
        return this.getX() + 5.0f;
    }

    private float calculateY() {
        return this.getY() + 10.0f;
    }

    private float calculateWidth() {
        return this.getWidth() + -9.0f;
    }

    private float calculateHeight(String text, float maxWidth) {
        List<String> wrappedLines = this.wrapText(text, 6.0f, maxWidth);
        int numberOfLines = wrappedLines.size();
        float lineHeight = Fonts.montserrat.getHeight(6.0f);
        float spacingBetweenLines = 1.5f;
        float initialHeight = 5.0f;
        return initialHeight + (float)numberOfLines * lineHeight + (float)(numberOfLines - 1);
    }

    private void drawSettingName(MatrixStack stack, String settingName, float x, float y) {
        Fonts.montserrat.drawText(stack, settingName, x, y + -7.0f, ColorUtils.rgba(255, 255, 255, 255), 6.0f);
    }

    private void drawBackground(float x, float y, float width, float height) {
        DisplayUtils.drawRoundedRect(x, y, width, height, 4.0f, ColorUtils.rgba(25, 26, 40, 165));
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        if (this.setting.isOnlyNumber() && !NumberUtils.isNumber(String.valueOf(codePoint))) {
            return;
        }
        if (this.typing && this.text.length() < 60) {
            this.text = this.text + codePoint;
            this.setting.set(this.text);
        }
        super.charTyped(codePoint, modifiers);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        if (this.typing) {
            if (Screen.isPaste(key)) {
                this.pasteFromClipboard();
            }
            if (key == 259) {
                this.deleteLastCharacter();
            }
            if (key == 257) {
                this.typing = false;
            }
        }
        super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int mouse) {
        this.typing = this.isHovered(mouseX, mouseY) ? !this.typing : false;
        super.mouseClick(mouseX, mouseY, mouse);
    }

    private boolean isControlDown() {
        return GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), 341) == 1 || GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), 345) == 1;
    }

    private void pasteFromClipboard() {
        try {
            this.text = this.text + GLFW.glfwGetClipboardString(Minecraft.getInstance().getMainWindow().getHandle());
            this.setting.set(this.text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteLastCharacter() {
        if (!this.text.isEmpty()) {
            this.text = this.text.substring(0, this.text.length() - 1);
            this.setting.set(this.text);
        }
    }

    @Override
    public boolean isVisible() {
        return (Boolean)this.setting.visible.get();
    }
}

