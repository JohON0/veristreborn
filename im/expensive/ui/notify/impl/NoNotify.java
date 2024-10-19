/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.notify.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.ui.notify.Notify;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.vector.Vector4f;

public class NoNotify
extends Notify {
    public NoNotify(String content, long delay) {
        super(content, delay);
        this.animationY.setValue(window.getScaledHeight());
        this.alphaAnimation.setValue(0.0);
    }

    @Override
    public void render(MatrixStack matrixStack, int multiplierY) {
        boolean bl = end = this.getInit() + this.getDelay() - System.currentTimeMillis() <= this.getDelay() - 500L - this.getDelay();
        if (NoNotify.mc.currentScreen instanceof ChatScreen) {
            this.chatOffset.run(ClientFonts.msSemiBold[12].getFontHeight() + 4.0f + 26.0f);
        } else {
            this.chatOffset.run(ClientFonts.msSemiBold[12].getFontHeight() + 2.0f);
        }
        float contentWidth = ClientFonts.msMedium[15].getWidth(this.getContent());
        float width = this.margin + contentWidth + this.margin;
        float height = this.margin / 2.0f + ClientFonts.msMedium[15].getFontHeight() + this.margin / 2.0f;
        float x = (float)window.getScaledWidth() - width - this.margin + 2.0f;
        float y = (float)((double)((float)window.getScaledHeight() - height - 1.0f - height * (float)multiplierY - (float)(multiplierY * 4)) - this.chatOffset.getValue());
        float iconSize = ClientFonts.icons_wex[26].getWidth("I");
        this.alphaAnimation.run(end ? 0.0 : 1.0);
        this.animationY.run(end ? (double)window.getScaledHeight() : (double)y);
        float posX = x;
        float posY = (float)this.animationY.getValue();
        int i = ColorUtils.rgba(255, 86, 86, 60);
        int o = ColorUtils.rgba(255, 44, 44, 20);
        DisplayUtils.drawShadow(posX - iconSize, posY, width + iconSize, height, 6, i, o);
        DisplayUtils.drawRoundedRect(posX - iconSize, posY, width + iconSize, height, new Vector4f(3.0f, 3.0f, 3.0f, 3.0f), new Vector4i(i, i, o, o));
        ClientFonts.icons_wex[26].drawString(matrixStack, "I", (double)posX - ((double)iconSize - 2.5), (double)(posY + this.margin / 2.0f + 2.0f), ColorUtils.reAlphaInt(-1, (int)(255.0 * this.alphaAnimation.getValue())));
        ClientFonts.msMedium[15].drawString(matrixStack, this.getContent(), (double)(posX + this.margin), (double)(posY + this.margin / 2.0f + 2.5f), ColorUtils.reAlphaInt(-1, (int)(255.0 * this.alphaAnimation.getValue())));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean hasExpired() {
        if (!this.animationY.isFinished()) return false;
        if (!end) return false;
        return true;
    }
}

