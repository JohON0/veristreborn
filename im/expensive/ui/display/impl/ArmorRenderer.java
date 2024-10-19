/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display.impl;

import im.expensive.Expensive;
import im.expensive.events.EventDisplay;
import im.expensive.modules.impl.render.Crosshair;
import im.expensive.ui.display.ElementRenderer;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.text.font.ClientFonts;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

public class ArmorRenderer
implements ElementRenderer {
    private static final ResourceLocation TOTEM_TEXTURE = new ResourceLocation("minecraft", "textures/item/totem_of_undying.png");
    private float animation;

    @Override
    public void render(EventDisplay eventDisplay) {
        this.animation = MathUtil.lerp(this.animation, ArmorRenderer.mc.currentScreen instanceof ChatScreen ? (float)(window.getScaledHeight() - 33) : (float)(window.getScaledHeight() - 18), 15.0f);
        int posX = (int)(this.scaled().x / 2.0 + 95.0);
        int posY = (int)this.animation;
        float addX = 0.0f;
        float addY = 0.0f;
        Crosshair crosshair = Expensive.getInstance().getModuleManager().getCrosshair();
        if (crosshair.isState() && crosshair.mode.is("\u041e\u0440\u0431\u0438\u0437") && !((Boolean)crosshair.staticCrosshair.get()).booleanValue() && ArmorRenderer.mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
            addX = crosshair.getAnimatedYaw();
            addY = crosshair.getAnimatedPitch();
        }
        float totemX = (float)window.getScaledWidth() / 2.0f + 5.0f + addX;
        float totemY = (float)window.getScaledHeight() / 2.0f - 5.0f + addY;
        float size = 14.0f;
        int countItems = this.countItems(Items.TOTEM_OF_UNDYING);
        if ((countItems += this.countItemsInOffhand(Items.TOTEM_OF_UNDYING)) > 0) {
            mc.getTextureManager().bindTexture(TOTEM_TEXTURE);
            AbstractGui.blit(eventDisplay.getMatrixStack(), (int) totemX, (int) totemY, 0.0f, 0.0f, (int) size, (int) size, (int) size, (int) size);
            ClientFonts.msMedium[20].drawStringWithOutline(eventDisplay.getMatrixStack(), String.valueOf(countItems), totemX + size, totemY, -1);
        }
        for (ItemStack itemStack : ArmorRenderer.mc.player.getArmorInventoryList()) {
            if (itemStack.isEmpty()) continue;
            mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, posX, posY);
            mc.getItemRenderer().renderItemOverlayIntoGUI(ArmorRenderer.mc.fontRenderer, itemStack, posX, posY, null);
            posX += 18;
        }
    }

    private int countItems(Item item) {
        int i = 0;
        for (ItemStack itemStack : ArmorRenderer.mc.player.inventory.mainInventory) {
            if (itemStack.getItem() != item) continue;
            i += itemStack.getCount();
        }
        return i;
    }

    private int countItemsInOffhand(Item item) {
        ItemStack offhand = ArmorRenderer.mc.player.getHeldItemOffhand();
        return offhand.getItem() == item ? offhand.getCount() : 0;
    }
}

