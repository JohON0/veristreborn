/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.autobuy.api.render.impl.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.ui.autobuy.api.render.impl.Component;
import im.expensive.utils.components.SliderComponent;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class EnchantmentWidget
extends Component {
    float x;
    float y;
//    private final ItemStack stack;
    private final List<SliderComponent> sliderComponents = new ArrayList<SliderComponent>();
    int scroll = 0;

    public EnchantmentWidget(ItemStack stack) {
        /*this.stack = stack;*/
//        List enchantmentsMap = Registry.ENCHANTMENT.stream().filter(m -> m.canApply).toList();
//        for ((Object) Enchantment enchantment : enchantmentsMap) {
//            this.sliderComponents.add(new SliderComponent(0.0f, 0.0f, 0.0f, 0.0f, enchantment.getMinLevel(), enchantment.getMaxLevel(), enchantment, I18n.format(enchantment.getName(), new Object[0])));
//        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY) {
        Scissor.push();
        Scissor.setFromComponentCoordinates(this.x, this.y, 100.0, 100.0);
        DisplayUtils.drawRoundedRect(this.x, this.y, 100.0f, 100.0f, 4.0f, ColorUtils.rgba(17, 17, 17, 255));
        this.scroll = this.sliderComponents.size() * 13 > 100 ? MathHelper.clamp(this.scroll, -(this.sliderComponents.size() * 13) + 90, 0) : 0;
        int i = 0;
        for (SliderComponent s : this.sliderComponents) {
            s.setX(this.x + 5.0f);
            s.setY(this.y + 5.0f + (float)i * (s.getHeight() + 3.0f) + (float)this.scroll);
            s.setWidth(90.0f);
            s.setHeight(10.0f);
            s.draw(stack, mouseX, mouseY);
            ++i;
        }
        Scissor.unset();
        Scissor.pop();
    }

    public Map<Enchantment, Integer> get() {
        HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
        for (SliderComponent s : this.sliderComponents) {
            if (Integer.valueOf(s.fieldComponent.get()) <= 0) continue;
            enchantments.put(s.enchantment, Integer.valueOf(s.fieldComponent.get()));
        }
        return enchantments;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (SliderComponent s : this.sliderComponents) {
            s.click((int)mouseX, (int)mouseY);
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        for (SliderComponent s : this.sliderComponents) {
            s.unpress();
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        if (MathUtil.isHovered((float)mouseX, (float)mouseY, this.x, this.y, 100.0f, 100.0f)) {
            this.scroll = (int)((double)this.scroll + delta * 10.0);
        }
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        for (SliderComponent s : this.sliderComponents) {
            s.key(keyCode);
        }
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (SliderComponent s : this.sliderComponents) {
            s.charTyped(codePoint);
        }
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}

