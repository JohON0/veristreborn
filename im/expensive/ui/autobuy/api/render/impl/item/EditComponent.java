/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.autobuy.api.render.impl.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.Expensive;
import im.expensive.ui.autobuy.api.render.impl.AddedItemComponent;
import im.expensive.ui.autobuy.api.render.impl.AllItemComponent;
import im.expensive.ui.autobuy.api.render.impl.Component;
import im.expensive.ui.autobuy.api.render.impl.item.EnchantmentWidget;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.components.ButtonComponent;
import im.expensive.utils.components.FieldComponent;
import im.expensive.utils.components.SliderComponent;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class EditComponent
extends Component
implements IMinecraft {
    float x;
    float y;
    private ItemStack stack;
    private EnchantmentWidget enchantmentWidget;
    private AddedItemComponent addedItemComponents;
    private AllItemComponent allItemComponent;
    private final FieldComponent price = new FieldComponent(0.0f, 0.0f, 0.0f, 0.0f, "\u0426\u0435\u043d\u0430");
    private final SliderComponent damage = new SliderComponent(0.0f, 0.0f, 0.0f, 0.0f, 1, 100, "\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c");
    private final SliderComponent count = new SliderComponent(0.0f, 0.0f, 0.0f, 0.0f, 2, 64, "\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e");
    private final ButtonComponent add = new ButtonComponent(0.0f, 0.0f, 0.0f, 0.0f, "\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c", () -> {
        if (!this.price.get().isEmpty()) {
            Expensive.getInstance().getItemStorage().addItem(this.stack.getItem(), Integer.parseInt(this.price.get()), Integer.parseInt(this.count.fieldComponent.get()), Integer.parseInt(this.damage.fieldComponent.get()), this.enchantmentWidget.get());
            this.allItemComponent.component = null;
        }
    });

    public EditComponent(ItemStack stack, AddedItemComponent addedItemComponents, AllItemComponent allItemComponent) {
        this.stack = stack;
        this.allItemComponent = allItemComponent;
        this.addedItemComponents = addedItemComponents;
        this.enchantmentWidget = new EnchantmentWidget(stack);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY) {
        Scissor.push();
        Scissor.setFromComponentCoordinates(this.x, this.y, 100.0, 100.0);
        DisplayUtils.drawRoundedRect(this.x, this.y, 100.0f, 100.0f, 4.0f, ColorUtils.rgba(17, 17, 17, 255));
        float width = 100.0f;
        Fonts.montserrat.drawText(stack, TextFormatting.getTextWithoutFormattingCodes(this.stack.getDisplayName().getString()), this.x + 24.0f, this.y + 9.0f, -1, 6.0f, 0.05f);
        if (Fonts.montserrat.getWidth(TextFormatting.getTextWithoutFormattingCodes(this.stack.getDisplayName().getString()), 6.0f, 0.05f) + 24.0f > width) {
            DisplayUtils.drawRectVerticalW(this.x + width - 10.0f, this.y + 9.0f, 10.0, 6.0, ColorUtils.rgba(17, 17, 17, 255), ColorUtils.rgba(17, 17, 17, 0));
        }
        mc.getItemRenderer().renderItemAndEffectIntoGUI(this.stack, (int)this.x + 5, (int)this.y + 5);
        this.price.setX(this.x + 5.0f);
        this.price.setY(this.y + 25.0f);
        this.price.setWidth(50.0f);
        this.price.setHeight(17.0f);
        this.price.draw(stack, mouseX, mouseY);
        if (this.stack.getItem().isDamageable()) {
            this.damage.setX(this.x + 5.0f);
            this.damage.setY(this.y + 25.0f + 20.0f);
            this.damage.setWidth(50.0f);
            this.damage.setHeight(10.0f);
            this.damage.max = 100;
            this.damage.draw(stack, mouseX, mouseY);
        }
        if (this.stack.getItem().getMaxStackSize() > 1) {
            this.count.setX(this.x + 5.0f);
            this.count.setY(this.y + 25.0f + (float)(this.stack.getItem().isDamageable() ? 40 : 20));
            this.count.setWidth(50.0f);
            this.count.setHeight(10.0f);
            this.count.max = this.stack.getMaxStackSize();
            this.count.draw(stack, mouseX, mouseY);
        }
        this.add.setX(this.x + 5.0f);
        this.add.setY(this.y + 100.0f - 22.0f);
        this.add.setWidth(width - 10.0f);
        this.add.setHeight(17.0f);
        this.add.draw(stack, mouseX, mouseY);
        Scissor.unset();
        Scissor.pop();
        if (this.stack.isEnchantable()) {
            this.enchantmentWidget.setX(this.x);
            this.enchantmentWidget.setY(this.y + 125.0f);
            this.enchantmentWidget.render(stack, mouseX, mouseY);
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        this.enchantmentWidget.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.enchantmentWidget.mouseClicked(mouseX, mouseY, mouseButton);
        this.price.click((int)mouseX, (int)mouseY);
        this.add.click((int)mouseX, (int)mouseY);
        this.damage.click((int)mouseX, (int)mouseY);
        this.count.click((int)mouseX, (int)mouseY);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        this.enchantmentWidget.mouseReleased(mouseX, mouseY, mouseButton);
        this.damage.unpress();
        this.count.unpress();
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        this.price.key(keyCode);
        this.damage.key(keyCode);
        this.count.key(keyCode);
        this.enchantmentWidget.keyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        if (Character.isDigit(codePoint)) {
            this.price.charTyped(codePoint);
            this.damage.charTyped(codePoint);
            this.count.charTyped(codePoint);
        }
        this.enchantmentWidget.charTyped(codePoint, modifiers);
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

