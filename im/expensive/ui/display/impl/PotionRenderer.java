/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.events.EventDisplay;
import im.expensive.ui.display.ElementRenderer;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.animations.easing.CompactAnimation;
import im.expensive.utils.animations.easing.Easing;
import im.expensive.utils.drag.Dragging;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;

public class PotionRenderer
implements ElementRenderer {
    private final Dragging dragging;
    private final CompactAnimation widthAnimation = new CompactAnimation(Easing.EASE_OUT_QUART, 100L);
    private final CompactAnimation heightAnimation = new CompactAnimation(Easing.EASE_OUT_QUART, 100L);
    private float width;
    private float height;
    private Map<String, CompactAnimation> animations = new HashMap<String, CompactAnimation>();

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();
        float posX = this.dragging.getX();
        float posY = this.dragging.getY();
        float fontSize = 6.5f;
        float padding = 5.0f;
        float iconSize = 10.0f;
        String name = "Potions";
        DisplayUtils.drawStyledRect(posX, posY, this.width, this.height);
        ClientFonts.icons_nur[20].drawString(ms, "B", (double)(posX + 5.0f), (double)(posY + 7.0f), Theme.textColor);
        Fonts.montserrat.drawText(ms, name, posX + iconSize + 8.0f, posY + padding + 0.5f, Theme.textColor, fontSize, 0.07f);
        DisplayUtils.drawRectHorizontalW(posX, posY + 17.0f, this.width, 2.5, ColorUtils.rgba(0, 0, 0, 0), ColorUtils.rgba(0, 0, 0, 63));
        posY += fontSize + padding + 2.0f;
        posY += 5.0f;
        float maxWidth = Fonts.montserrat.getWidth(name, fontSize) + padding * 2.0f;
        float localHeight = fontSize + padding * 2.0f;
        for (EffectInstance effectInstance : PotionRenderer.mc.player.getActivePotionEffects()) {
            int amp = effectInstance.getAmplifier() + 1;
            Object ampStr = "";
            if (amp >= 1 && amp <= 9) {
                ampStr = " " + I18n.format("enchantment.level." + amp, new Object[0]);
            }
            String nameText = I18n.format(effectInstance.getEffectName(), new Object[0]) + (String)ampStr;
            float nameWidth = Fonts.montserrat.getWidth(nameText, fontSize);
            String durText = EffectUtils.getPotionDurationString(effectInstance, 1.0f);
            float durWidth = Fonts.montserrat.getWidth(durText, fontSize);
            float localWidth = nameWidth + durWidth + padding * 3.0f + 10.0f;
            PotionSpriteUploader potionspriteuploader = mc.getPotionSpriteUploader();
            Effect effect = effectInstance.getPotion();
            TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
            mc.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
            CompactAnimation efAnimation = this.animations.getOrDefault(effectInstance.getEffectName(), null);
            if (efAnimation == null) {
                efAnimation = new CompactAnimation(Easing.EASE_IN_OUT_CUBIC, 550L);
                this.animations.put(effectInstance.getEffectName(), efAnimation);
            }
            boolean potionActive = effectInstance.getDuration() > 5;
            efAnimation.run(potionActive ? 1.0 : 0.0);
            int color = ColorUtils.reAlphaInt(Theme.textColor, (int)(255.0 * efAnimation.getValue()));
            Scissor.push();
            Scissor.setFromComponentCoordinates(posX, posY, this.width, this.height);
            Fonts.montserrat.drawText(ms, nameText, posX + padding - 0.5f, posY + 2.0f, color, fontSize, 0.05f);
            Fonts.montserrat.drawText(ms, durText, posX + this.width - padding - durWidth, posY + 2.0f, color, fontSize, 0.05f);
            Scissor.unset();
            Scissor.pop();
            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }
            posY += fontSize + padding - 2.0f;
            localHeight += fontSize + padding - 2.0f;
        }
        this.widthAnimation.run(Math.max(maxWidth, 70.0f));
        this.width = (float)this.widthAnimation.getValue();
        this.heightAnimation.run(localHeight + 5.5f);
        this.height = (float)this.heightAnimation.getValue();
        this.dragging.setWidth(this.width);
        this.dragging.setHeight(this.height);
    }

    public PotionRenderer(Dragging dragging) {
        this.dragging = dragging;
    }
}

