/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.Expensive;
import im.expensive.events.EventDisplay;
import im.expensive.events.EventUpdate;
import im.expensive.modules.impl.movement.Timer;
import im.expensive.ui.display.ElementRenderer;
import im.expensive.ui.display.ElementUpdater;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.drag.Dragging;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.player.MoveUtils;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;

public class TimerRenderer
implements ElementRenderer,
ElementUpdater {
    private final Dragging dragging;
    private float perc;

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();
        float posX = this.dragging.getX();
        float posY = this.dragging.getY();
        Timer timer = Expensive.getInstance().getModuleManager().getTimer();
        float quotient = timer.maxViolation / ((Float)timer.speed.get()).floatValue();
        float minimumValue = Math.min(timer.violation, quotient);
        this.perc = MathUtil.lerp(this.perc, (quotient - minimumValue) / quotient, 10.0f);
        String text = (int)(this.perc * 100.0f) + "%";
        float width = 60.0f;
        float height = 15.0f;
        this.dragging.setWidth(width);
        this.dragging.setHeight(height);
        DisplayUtils.drawShadow(posX, posY, width, height, 8, ColorUtils.rgba(20, 20, 20, 80));
        DisplayUtils.drawRoundedRect(posX, posY, width, height, new Vector4f(3.0f, 3.0f, 3.0f, 3.0f), ColorUtils.rgba(20, 20, 20, 80));
        DisplayUtils.drawShadow(posX, posY, width * this.perc - 2.0f, height, 8, Theme.mainRectColor);
        DisplayUtils.drawRoundedRect(posX, posY, width * this.perc - 2.0f, height, new Vector4f(3.0f, 3.0f, 3.0f, 3.0f), new Vector4i(Theme.mainRectColor, Theme.mainRectColor, Theme.mainRectColor, Theme.mainRectColor));
        ClientFonts.msMedium[16].drawCenteredString(ms, text, (double)(posX + width / 2.0f + 2.0f), (double)(posY - 2.0f + height / 2.0f), -1);
    }

    @Override
    public void update(EventUpdate e) {
        Timer timer = Expensive.getInstance().getModuleManager().getTimer();
        if (!MoveUtils.isMoving()) {
            timer.violation = (float)((double)timer.violation - ((double)((Float)timer.ticks.get()).floatValue() + 0.4));
        } else if (((Boolean)timer.moveUp.get()).booleanValue()) {
            timer.violation -= ((Float)timer.moveUpValue.get()).floatValue();
        }
        timer.violation = (float)MathHelper.clamp((double)timer.violation, 0.0, Math.floor(timer.maxViolation));
    }

    public TimerRenderer(Dragging dragging) {
        this.dragging = dragging;
    }
}

