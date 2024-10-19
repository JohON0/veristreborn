/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.Expensive;
import im.expensive.events.EventDisplay;
import im.expensive.ui.display.ElementRenderer;
import im.expensive.utils.animations.easing.CompactAnimation;
import im.expensive.utils.animations.easing.Easing;
import im.expensive.utils.text.font.ClientFonts;
import im.expensive.utils.text.font.styled.StyledFont;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.text.TextFormatting;

public class InfoRenderer
implements ElementRenderer {
    final CompactAnimation animation = new CompactAnimation(Easing.EASE_OUT_QUAD, 100L);

    @Override
    public void render(EventDisplay eventDisplay) {
        StyledFont font = ClientFonts.msMedium[12];
        boolean isChatScreen = InfoRenderer.mc.currentScreen instanceof ChatScreen;
        int margin = 2;
        this.animation.run(isChatScreen ? 10 + margin : 0);
        double chat = this.animation.getValue();
        MatrixStack ms = eventDisplay.getMatrixStack();
        String bps = String.format("%.2f", Math.hypot(InfoRenderer.mc.player.prevPosX - InfoRenderer.mc.player.getPosX(), InfoRenderer.mc.player.prevPosZ - InfoRenderer.mc.player.getPosZ()) * 20.0 * (double)InfoRenderer.mc.timer.timerSpeed);
        String tps = String.format("%.1f", Float.valueOf(Expensive.getInstance().getTpsCalc().getTPS()));
        StringBuilder leftSide = new StringBuilder();
        if (this.isEnabled("\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b")) {
            leftSide.append((Object)TextFormatting.WHITE).append("XYZ: ").append((Object)TextFormatting.GRAY).append((int)InfoRenderer.mc.player.getPosX()).append(", ").append((int)InfoRenderer.mc.player.getPosY()).append(", ").append((int)InfoRenderer.mc.player.getPosZ()).append((Object)TextFormatting.WHITE);
        }
        if (this.isEnabled("\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b") && this.isEnabled("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c")) {
            leftSide.append(", ");
        }
        if (this.isEnabled("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c")) {
            leftSide.append((Object)TextFormatting.WHITE).append("BPS: ").append((Object)TextFormatting.GRAY).append(bps);
        }
        StringBuilder rightSide = new StringBuilder();
        if (this.isEnabled("\u0422\u041f\u0421")) {
            rightSide.append((Object)TextFormatting.WHITE).append("TPS: ").append((Object)TextFormatting.GRAY).append(tps);
        }
        float y = (float)(this.scaled().y - (double)font.getFontHeight() - (double)margin + 2.0);
        font.drawStringWithOutline(ms, leftSide.toString(), margin, (double)y - chat, -1);
        font.drawStringWithOutline(ms, rightSide.toString(), this.scaled().x - (double)margin - (double)font.getWidth(rightSide.toString()), (double)y - chat * 2.62, -1);
    }

    private boolean isEnabled(String check) {
        return (Boolean)Expensive.getInstance().getModuleManager().getHud().infoOptions.getValueByName(check).get();
    }
}

