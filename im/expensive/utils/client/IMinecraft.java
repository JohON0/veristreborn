/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.client;

import im.expensive.utils.math.MathUtil;
import im.expensive.utils.text.GradientUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public interface IMinecraft {
    public static final Minecraft mc = Minecraft.getInstance();
    public static final MainWindow window = mc.getMainWindow();
    public static final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
    public static final Tessellator tessellator = Tessellator.getInstance();
    public static final List<ITextComponent> clientMessages = new ArrayList<ITextComponent>();

    default public void print(String input) {
        if (IMinecraft.mc.player == null) {
            return;
        }
        IFormattableTextComponent text = GradientUtil.gradient("VeristClient").append(new StringTextComponent(TextFormatting.DARK_GRAY + " >> " + TextFormatting.RESET + input));
        clientMessages.add(text);
        IMinecraft.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 0);
    }
}

