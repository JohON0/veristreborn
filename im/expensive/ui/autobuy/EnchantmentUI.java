/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.autobuy;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.utils.animations.Animation;
import im.expensive.utils.animations.impl.EaseBackIn;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class EnchantmentUI
implements IMinecraft {
    public ItemStack stack;
    public boolean drag;
    public int draggi;
    public int scroll;
    Map<Enchantment, Integer> enchantmentIntegerMap = new HashMap<Enchantment, Integer>();
    Map<Enchantment, Float> animations = new HashMap<Enchantment, Float>();
    final Animation openAnimation = new EaseBackIn(400, 1.0, 1.0f);

    public EnchantmentUI(ItemStack stack) {
        this.stack = stack;
    }

    public void render(MatrixStack stack, float mouseX, float mouseY, float pt) {
        List enchantmentsMap = Registry.ENCHANTMENT.stream().filter(m -> m.canApply(this.stack)).toList();
        if (this.stack == null || enchantmentsMap.isEmpty()) {
            return;
        }
        int windowWidth = ClientUtil.calc(mc.getMainWindow().getScaledWidth());
        int windowHeight = ClientUtil.calc(mc.getMainWindow().getScaledHeight());
        float x = windowWidth / 2 - 200;
        float y = windowHeight / 2 - 150;
        x -= 150.0f;
        y += 110.0f;
        float width = 100.0f;
        float height = 100.0f;
        float animation = (float)this.openAnimation.getOutput();
        float halfAnimationValueRest = (1.0f - animation) / 2.0f;
        float testX = x + width * halfAnimationValueRest;
        float testY = y + height * halfAnimationValueRest;
        float testW = width * animation;
        float testH = height * animation;
        GlStateManager.pushMatrix();
        EnchantmentUI.sizeAnimation(x + width / 2.0f, y + height / 2.0f, animation);
        DisplayUtils.drawShadow(x, y, width, height, 10, ColorUtils.rgba(17, 17, 17, 128));
        DisplayUtils.drawRoundedRect(x, y, 100.0f, 100.0f, 4.0f, ColorUtils.rgba(17, 17, 17, 255));
        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW, testH);
        int i = 0;
        this.scroll = enchantmentsMap.size() * 16 >= 100 ? MathHelper.clamp(this.scroll, -(enchantmentsMap.size() * 16 - 90), 0) : 0;
        float size = 6.0f;
        for (Enchantment enchantment : enchantmentsMap) {
            if (!this.animations.containsKey(enchantment)) {
                this.animations.put(enchantment, Float.valueOf(0.0f));
            }
            Fonts.montserrat.drawText(stack, this.enchantmentIntegerMap.containsKey(enchantment) ? enchantment.getDisplayName(this.enchantmentIntegerMap.get(enchantment)).getString() : I18n.format(enchantment.getName(), new Object[0]), x + 5.0f, y + 5.0f + (float)i * (size + 10.0f) + (float)this.scroll, -1, size);
            DisplayUtils.drawRoundedRect(x + 5.0f, y + 5.0f + (float)i * (size + 10.0f) + 10.0f + (float)this.scroll, width - 10.0f, 3.0f, 1.0f, ColorUtils.rgba(25, 25, 25, 255));
            float widh = (width - 10.0f) * (float)(EnchantmentHelper.getEnchantmentLevel(enchantment, this.stack) - (enchantment.getMinLevel() - 1)) / (float)(5 - (enchantment.getMinLevel() - 1));
            this.animations.put(enchantment, Float.valueOf(MathUtil.fast(this.animations.get(enchantment).floatValue(), widh, 10.0f)));
            DisplayUtils.drawRoundedRect(x + 5.0f, y + 5.0f + (float)i * (size + 10.0f) + 10.0f + (float)this.scroll, this.animations.get(enchantment).floatValue(), 3.0f, 1.0f, ColorUtils.getColor(0));
            ++i;
        }
        if (this.drag) {
            i = 0;
            for (Enchantment enchantment : enchantmentsMap) {
                Integer i1;
                int level = (int)MathHelper.clamp(MathUtil.round((mouseX - x - 5.0f) / (width - 10.0f) * (float)(5 - (enchantment.getMinLevel() - 1)) + (float)(enchantment.getMinLevel() - 1), 1.0), (double)(enchantment.getMinLevel() - 1), 5.0);
                if (i == this.draggi) {
                    this.enchantmentIntegerMap.put(enchantment, level);
                }
                if ((i1 = this.enchantmentIntegerMap.get(enchantment)) != null && i1 == 0) {
                    this.enchantmentIntegerMap.remove(enchantment);
                }
                ++i;
            }
            EnchantmentHelper.setEnchantments(this.enchantmentIntegerMap, this.stack);
        }
        Scissor.unset();
        Scissor.pop();
        GlStateManager.popMatrix();
    }

    public static void sizeAnimation(double width, double height, double scale) {
        GlStateManager.translated(width, height, 0.0);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.translated(-width, -height, 0.0);
    }

    public void press(int mX, int mY) {
        if (this.stack == null) {
            return;
        }
        int windowWidth = ClientUtil.calc(mc.getMainWindow().getScaledWidth());
        int windowHeight = ClientUtil.calc(mc.getMainWindow().getScaledHeight());
        float x = windowWidth / 2 - 200;
        float y = windowHeight / 2 - 150;
        x -= 150.0f;
        y += 110.0f;
        float width = 100.0f;
        float height = 100.0f;
        List enchantmentsMap = Registry.ENCHANTMENT.stream().filter(m -> m.canApply(this.stack)).toList();
        int i = 0;
        float size = 6.0f;
        for (Enchantment enchantment : enchantmentsMap) {
            if (MathUtil.isHovered(mX, mY, x + 5.0f, y + 5.0f + (float)i * (size + 10.0f) + 10.0f - 5.0f + (float)this.scroll, width - 10.0f, 13.0f)) {
                this.drag = true;
                this.draggi = i;
            }
            ++i;
        }
    }

    public void scroll(float delta, float mouseX, float mouseY) {
        int windowWidth = ClientUtil.calc(mc.getMainWindow().getScaledWidth());
        int windowHeight = ClientUtil.calc(mc.getMainWindow().getScaledHeight());
        float x = windowWidth / 2 - 200;
        float y = windowHeight / 2 - 150;
        float width = 100.0f;
        float height = 100.0f;
        if (MathUtil.isHovered(mouseX, mouseY, x -= 150.0f, y += 150.0f, width, height)) {
            this.scroll = (int)((float)this.scroll + delta * 10.0f);
        }
    }

    public void rel() {
        this.drag = false;
        this.draggi = -1;
    }
}

