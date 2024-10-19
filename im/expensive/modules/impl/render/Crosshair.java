/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventDisplay;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.render.HUD;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.rect.DisplayUtils;
import java.awt.Color;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.RayTraceResult;

@ModuleRegister(name="Crosshair", category=Category.Render)
public class Crosshair
extends Module {
    public final ModeSetting mode = new ModeSetting("\u0412\u0438\u0434", "\u041e\u0440\u0431\u0438\u0437", "\u041e\u0440\u0431\u0438\u0437", "\u041a\u043b\u0430\u0441\u0438\u0447\u0435\u0441\u043a\u0438\u0439", "\u041a\u0440\u0443\u0433");
    public final BooleanSetting staticCrosshair = new BooleanSetting("\u0421\u0442\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439", false).setVisible(() -> this.mode.is("\u041e\u0440\u0431\u0438\u0437"));
    private float lastYaw;
    private float lastPitch;
    public float animatedYaw;
    public float x;
    public float animatedPitch;
    public float y;
    private float animation;
    private float animationSize;
    private final int outlineColor = Color.BLACK.getRGB();
    private final int entityColor = Color.RED.getRGB();

    public Crosshair() {
        this.addSettings(this.mode, this.staticCrosshair);
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        if (Crosshair.mc.player == null || Crosshair.mc.world == null || e.getType() != EventDisplay.Type.POST) {
            return;
        }
        this.x = (float)mc.getMainWindow().getScaledWidth() / 2.0f;
        this.y = (float)mc.getMainWindow().getScaledHeight() / 2.0f;
        switch (this.mode.getIndex()) {
            case 0: {
                float size = 5.0f;
                this.animatedYaw = MathUtil.fast(this.animatedYaw, (this.lastYaw - Crosshair.mc.player.rotationYaw + Crosshair.mc.player.moveStrafing) * size, 5.0f);
                this.animatedPitch = MathUtil.fast(this.animatedPitch, (this.lastPitch - Crosshair.mc.player.rotationPitch + Crosshair.mc.player.moveForward) * size, 5.0f);
                this.animation = MathUtil.fast(this.animation, Crosshair.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY ? 1.0f : 0.0f, 5.0f);
                int color = ColorUtils.interpolate(Theme.rectColor, Theme.mainRectColor, 1.0f - this.animation);
                if (!((Boolean)this.staticCrosshair.get()).booleanValue()) {
                    this.x += this.animatedYaw;
                    this.y += this.animatedPitch;
                }
                this.animationSize = MathUtil.fast(this.animationSize, (1.0f - Crosshair.mc.player.getCooledAttackStrength(1.0f)) * 3.0f, 10.0f);
                float radius = 3.0f + ((Boolean)this.staticCrosshair.get() != false ? 0.0f : this.animationSize);
                if (Crosshair.mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                    DisplayUtils.drawShadowCircle(this.x, this.y, radius * 2.0f, ColorUtils.setAlpha(color, 64));
                    DisplayUtils.drawCircle(this.x, this.y, radius, color);
                }
                this.lastYaw = Crosshair.mc.player.rotationYaw;
                this.lastPitch = Crosshair.mc.player.rotationPitch;
                break;
            }
            case 1: {
                if (Crosshair.mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
                    return;
                }
                float cooldown = 1.0f - Crosshair.mc.player.getCooledAttackStrength(0.0f);
                this.animationSize = MathUtil.fast(this.animationSize, 1.0f - Crosshair.mc.player.getCooledAttackStrength(0.0f), 10.0f);
                float thickness = 1.0f;
                float length = 3.0f;
                float gap = 2.0f + 8.0f * this.animationSize;
                int color = Crosshair.mc.pointedEntity != null ? this.entityColor : -1;
                this.drawOutlined(this.x - thickness / 2.0f, this.y - gap - length, thickness, length, color);
                this.drawOutlined(this.x - thickness / 2.0f, this.y + gap, thickness, length, color);
                this.drawOutlined(this.x - gap - length, this.y - thickness / 2.0f, length, thickness, color);
                this.drawOutlined(this.x + gap, this.y - thickness / 2.0f, length, thickness, color);
                break;
            }
            case 2: {
                this.animationSize = MathUtil.fast(this.animationSize, (1.0f - Crosshair.mc.player.getCooledAttackStrength(1.0f)) * 260.0f, 10.0f);
                if (Crosshair.mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) break;
                DisplayUtils.drawCircleWithFill(this.x, this.y, 0.0f, 360.0f, 3.8f, 3.0f, false, ColorUtils.rgb(23, 21, 21));
                DisplayUtils.drawCircleWithFill(this.x, this.y, this.animationSize, 360.0f, 3.8f, 3.0f, false, HUD.getColor(Theme.rectColor, Theme.mainRectColor, 16, 0.0f));
            }
        }
    }

    private void drawOutlined(float x, float y, float w, float h, int hex) {
        DisplayUtils.drawRectW((double)x - 0.5, (double)y - 0.5, w + 1.0f, h + 1.0f, this.outlineColor);
        DisplayUtils.drawRectW(x, y, w, h, hex);
    }

    public float getAnimatedYaw() {
        return this.animatedYaw;
    }

    public float getX() {
        return this.x;
    }

    public float getAnimatedPitch() {
        return this.animatedPitch;
    }

    public float getY() {
        return this.y;
    }
}

