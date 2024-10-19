/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.Expensive;
import im.expensive.events.EventDisplay;
import im.expensive.ui.display.ElementRenderer;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.animations.Animation;
import im.expensive.utils.animations.Direction;
import im.expensive.utils.animations.impl.EaseBackIn;
import im.expensive.utils.drag.Dragging;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.gl.Stencil;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.render.rect.RectUtil;
import im.expensive.utils.text.GradientUtil;
import im.expensive.utils.text.font.ClientFonts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.opengl.GL11;

public class TargetInfoRenderer
implements ElementRenderer {
    private final StopWatch stopWatch = new StopWatch();
    private final Dragging drag;
    private LivingEntity entity = null;
    private boolean allow;
    private final Animation animation = new EaseBackIn(400, 1.0, 1.0f);
    private float healthAnimation = 0.0f;
    private float absorptionAnimation = 0.0f;
    private float width = 112.0f;
    private float height = 36.666668f;

    @Override
    public void render(EventDisplay eventDisplay) {
        this.entity = this.getTarget(this.entity);
        float rounding = 6.0f;
        boolean out = !this.allow || this.stopWatch.isReached(1000L);
        this.animation.setDuration(out ? 600 : 500);
        this.animation.setDirection(out ? Direction.BACKWARDS : Direction.FORWARDS);
        FloatFormatter formatter = new FloatFormatter();
        if (this.animation.getOutput() == 0.0) {
            this.entity = null;
        }
        if (this.entity != null) {
            Vector4i vector4i;
            String header;
            String name = this.entity.getName().getString();
            float posX = this.drag.getX();
            float posY = this.drag.getY();
            int headSize = 25;
            float spacing = 5.0f;
            this.drag.setWidth(this.width);
            this.drag.setHeight(this.height);
            Score score = TargetInfoRenderer.mc.world.getScoreboard().getOrCreateScore(this.entity.getScoreboardName(), TargetInfoRenderer.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
            float hp = this.entity.getHealth();
            float maxHp = this.entity.getMaxHealth();
            String string = header = TargetInfoRenderer.mc.ingameGUI.getTabList().header == null ? " " : TargetInfoRenderer.mc.ingameGUI.getTabList().header.getString().toLowerCase();
            if (mc.getCurrentServerData() != null && TargetInfoRenderer.mc.getCurrentServerData().serverIP.contains("funtime") && (header.contains("\u0430\u043d\u0430\u0440\u0445\u0438\u044f") || header.contains("\u0433\u0440\u0438\u0444\u0435\u0440\u0441\u043a\u0438\u0439")) && this.entity instanceof PlayerEntity) {
                hp = score.getScorePoints();
                maxHp = 20.0f;
            }
            this.healthAnimation = MathUtil.fast(this.healthAnimation, MathHelper.clamp(hp / maxHp, 0.0f, 1.0f), 10.0f);
            this.absorptionAnimation = MathUtil.fast(this.absorptionAnimation, MathHelper.clamp(this.entity.getAbsorptionAmount() / maxHp, 0.0f, 1.0f), 10.0f);
            float animationValue = (float)this.animation.getOutput();
            float halfAnimationValueRest = (1.0f - animationValue) / 2.0f;
            float testX = posX + this.width * halfAnimationValueRest;
            float testY = posY + this.height * halfAnimationValueRest;
            float testW = this.width * animationValue;
            float testH = this.height * animationValue;
            float finalHp = mc.getCurrentServerData() != null && TargetInfoRenderer.mc.getCurrentServerData().serverIP.contains("funtime") ? formatter.format(hp) : formatter.format(hp + this.entity.getAbsorptionAmount());
            GlStateManager.pushMatrix();
            TargetInfoRenderer.sizeAnimation(posX + this.width / 2.0f, posY + this.height / 2.0f, this.animation.getOutput());
            if (Expensive.getInstance().getModuleManager().getHud().tHudMode.is("\u041e\u0431\u044b\u0447\u043d\u044b\u0439")) {
                DisplayUtils.drawStyledShadowRect(posX, posY, this.width, this.height);
                this.drawHead(eventDisplay.getMatrixStack(), this.entity, posX + spacing, posY + spacing + 1.0f, headSize);
                Scissor.push();
                Scissor.setFromComponentCoordinates(testX, testY, testW, testH);
                ClientFonts.msSemiBold[20].drawString(eventDisplay.getMatrixStack(), name, (double)(posX - 0.5f + (float)headSize + spacing + spacing), (double)(posY + 1.0f + spacing), Theme.textColor);
                Fonts.montserrat.drawText(eventDisplay.getMatrixStack(), "HP: " + finalHp, posX - 0.5f + (float)headSize + spacing + spacing, posY + 1.0f + spacing + spacing + spacing, Theme.darkTextColor, 7.0f, 0.05f);
                Scissor.unset();
                Scissor.pop();
                vector4i = new Vector4i(Theme.rectColor, Theme.rectColor, Theme.mainRectColor, Theme.mainRectColor);
                DisplayUtils.drawRoundedRect(posX + 5.0f + (float)headSize + spacing + spacing - 5.0f, posY + 3.0f + this.height - spacing * 2.0f - 4.0f, this.width - 40.0f, 5.0f, new Vector4f(3.0f, 3.0f, 3.0f, 3.0f), ColorUtils.rgb(32, 32, 32));
                DisplayUtils.drawRoundedRect(posX + 5.0f + (float)headSize + spacing + spacing - 5.0f, posY + 3.0f + this.height - spacing * 2.0f - 4.0f, (this.width - 40.0f) * this.healthAnimation, 5.0f, new Vector4f(3.0f, 3.0f, 3.0f, 3.0f), vector4i);
                DisplayUtils.drawShadow(posX + 5.0f + (float)headSize + spacing + spacing - 5.0f, posY + 3.0f + this.height - spacing * 2.0f - 3.5f, (this.width - 40.0f) * this.healthAnimation, 5.0f, 8, ColorUtils.setAlpha(Theme.rectColor, 80), ColorUtils.setAlpha(Theme.mainRectColor, 80));
            }
            if (Expensive.getInstance().getModuleManager().getHud().tHudMode.is("\u041d\u0435\u043e\u043d")) {
                this.width = 106.666664f;
                headSize = 35;
                DisplayUtils.drawShadow(posX, posY, this.width, this.height, 5, ColorUtils.setAlpha(Theme.darkMainRectColor, 70), ColorUtils.setAlpha(Theme.rectColor, 70));
                DisplayUtils.drawRoundedRect(posX, posY, this.width, this.height, 0.0f, ColorUtils.rgba(20, 20, 20, 170));
                Stencil.initStencilToWrite();
                DisplayUtils.drawRoundedRect(posX, posY, (float)headSize, (float)headSize, 0.0f, -1);
                Stencil.readStencilBuffer(1);
                this.drawHead(eventDisplay.getMatrixStack(), this.entity, posX, posY, headSize);
                Stencil.uninitStencilBuffer();
                vector4i = new Vector4i(Theme.rectColor, Theme.rectColor, Theme.mainRectColor, Theme.mainRectColor);
                DisplayUtils.drawRoundedRect(posX, posY + 3.0f + this.height - spacing, this.width, 5.0f, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), ColorUtils.rgb(32, 32, 32));
                DisplayUtils.drawRoundedRect(posX, posY + 3.0f + this.height - spacing, this.width * this.healthAnimation, 5.0f, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), vector4i);
                DisplayUtils.drawShadow(posX, posY + 3.0f + this.height - spacing, this.width * this.healthAnimation, 5.0f, 8, ColorUtils.setAlpha(Theme.rectColor, ColorUtils.setAlpha(Theme.mainRectColor, 80)));
                float x = posX + (float)headSize + spacing - 2.5f;
                float y = posY + spacing;
                Scissor.push();
                Scissor.setFromComponentCoordinates(testX, testY, testW - 1.0f, testH);
                Fonts.montserrat.drawText(eventDisplay.getMatrixStack(), name, x, y, -1, 8.0f, 0.05f);
                Fonts.montserrat.drawText(eventDisplay.getMatrixStack(), "HP: ", x, y + spacing * 2.0f, ColorUtils.rgb(200, 200, 200), 8.0f, 0.02f);
                Fonts.montserrat.drawText(eventDisplay.getMatrixStack(), GradientUtil.gradient("" + finalHp), x + Fonts.montserrat.getWidth("HP: ", 8.0f, 0.02f), y + spacing * 2.0f, 8.0f, 255);
                Scissor.unset();
                Scissor.pop();
                this.drawItemStack(posX + (float)headSize + spacing - 2.5f, posY - 1.0f + spacing * 5.0f, 9.0f, 0.6f);
            }
            GlStateManager.popMatrix();
        }
    }

    private LivingEntity getTarget(LivingEntity nullTarget) {
        LivingEntity auraTarget = Expensive.getInstance().getModuleManager().getHitAura().getTarget();
        LivingEntity target = nullTarget;
        if (auraTarget != null) {
            this.stopWatch.reset();
            this.allow = true;
            target = auraTarget;
        } else if (TargetInfoRenderer.mc.currentScreen instanceof ChatScreen) {
            this.stopWatch.reset();
            this.allow = true;
            target = TargetInfoRenderer.mc.player;
        } else {
            this.allow = false;
        }
        return target;
    }

    private void drawHead(MatrixStack matrix, Entity entity, double x, double y, int size) {
        if (entity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)entity;
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.alphaFunc(516, 0.0f);
            RenderSystem.enableTexture();
            mc.getTextureManager().bindTexture(player.getLocationSkin());
            float hurtPercent = ((float)((AbstractClientPlayerEntity)entity).hurtTime - (((AbstractClientPlayerEntity)entity).hurtTime != 0 ? TargetInfoRenderer.mc.timer.renderPartialTicks : 0.0f)) / 10.0f;
            RenderSystem.color4f(1.0f, 1.0f - hurtPercent, 1.0f - hurtPercent, 1.0f);
            AbstractGui.blit(matrix, (float)x, (float)y, (float)size, (float)size, 4.0f, 4.0f, 4.0f, 4.0f, 32.0f, 32.0f);
            DisplayUtils.scaleStart((float)(x + (double)((float)size / 2.0f)), (float)(y + (double)((float)size / 2.0f)), 1.1f);
            AbstractGui.blit(matrix, (float)x, (float)y, (float)size, (float)size, 20.0f, 4.0f, 4.0f, 4.0f, 32.0f, 32.0f);
            DisplayUtils.scaleEnd();
            RenderSystem.disableBlend();
        } else {
            int color = ColorUtils.getColor(20, 128);
            RectUtil.getInstance().drawRoundedRectShadowed(matrix, (float)x, (float)y, (float)(x + (double)size), (float)(y + (double)size), 2.0f, 1.0f, color, color, color, color, false, false, true, true);
            ClientFonts.interRegular[size * 2].drawCenteredString(matrix, "?", x + (double)((float)size / 2.0f), y + 3.0 + (double)((float)size / 2.0f) - (double)(ClientFonts.interRegular[size * 2].getFontHeight() / 2.0f), -1);
        }
    }

    private void drawItemStack(float x, float y, float offset, float scale) {
        ArrayList<ItemStack> stackList = new ArrayList<ItemStack>(Arrays.asList(this.entity.getHeldItemMainhand(), this.entity.getHeldItemOffhand()));
        stackList.addAll((Collection)this.entity.getArmorInventoryList());
        AtomicReference<Float> posX = new AtomicReference<Float>(Float.valueOf(x));
        stackList.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> this.drawItemStack((ItemStack)stack, posX.getAndAccumulate(Float.valueOf(offset), Float::sum).floatValue(), y, true, true, scale));
    }

    public void drawItemStack(ItemStack stack, float x, float y, boolean withoutOverlay, boolean scale, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0f);
        if (scale) {
            GL11.glScaled(scaleValue, scaleValue, scaleValue);
        }
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (withoutOverlay) {
            mc.getItemRenderer().renderItemOverlays(TargetInfoRenderer.mc.fontRenderer, stack, 0, 0);
        }
        RenderSystem.popMatrix();
    }

    public static void sizeAnimation(double width, double height, double scale) {
        GlStateManager.translated(width, height, 0.0);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.translated(-width, -height, 0.0);
    }

    public TargetInfoRenderer(Dragging drag) {
        this.drag = drag;
    }

    public class FloatFormatter {
        public float format(float value) {
            return (float)Math.round(value * 2.0f) / 2.0f;
        }
    }
}

