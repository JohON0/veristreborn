/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.Expensive;
import im.expensive.config.FriendStorage;
import im.expensive.events.EventDisplay;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.AntiBot;
import im.expensive.modules.impl.render.HUD;
import im.expensive.modules.impl.render.WorldTweaks;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ColorSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.projections.ProjectionUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

@ModuleRegister(name="ESP", category=Category.Render)
public class ESP
extends Module {
    public ModeListSetting remove = new ModeListSetting("\u0423\u0431\u0440\u0430\u0442\u044c", new BooleanSetting("\u0411\u043e\u043a\u0441\u044b", false), new BooleanSetting("\u041f\u043e\u043b\u043e\u0441\u043a\u0443 \u0445\u043f", false), new BooleanSetting("\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u0438\u044f", false), new BooleanSetting("\u0421\u043f\u0438\u0441\u043e\u043a \u044d\u0444\u0444\u0435\u043a\u0442\u043e\u0432", false));
    public ModeListSetting targets = new ModeListSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c", new BooleanSetting("\u0421\u0435\u0431\u044f", true), new BooleanSetting("\u0418\u0433\u0440\u043e\u043a\u0438", true), new BooleanSetting("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", false), new BooleanSetting("\u041c\u043e\u0431\u044b", false));
    float healthAnimation = 0.0f;
    float length;
    private final HashMap<Entity, Vector4f> positions = new HashMap();
    public ColorSetting color = new ColorSetting("Color", -1);
    int index = 0;

    public ESP() {
        this.addSettings(this.targets, this.remove);
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        if (ESP.mc.world == null || e.getType() != EventDisplay.Type.PRE) {
            return;
        }
        this.positions.clear();
        Vector4i colors = new Vector4i(Theme.rectColor, Theme.rectColor, Theme.mainRectColor, Theme.mainRectColor);
        Vector4i friendColors = new Vector4i(HUD.getColor(ColorUtils.rgb(144, 238, 144), ColorUtils.rgb(0, 139, 0), 0, 1.0f), HUD.getColor(ColorUtils.rgb(144, 238, 144), ColorUtils.rgb(0, 139, 0), 90, 1.0f), HUD.getColor(ColorUtils.rgb(144, 238, 144), ColorUtils.rgb(0, 139, 0), 180, 1.0f), HUD.getColor(ColorUtils.rgb(144, 238, 144), ColorUtils.rgb(0, 139, 0), 270, 1.0f));
        for (Entity entity : ESP.mc.world.getAllEntities()) {
            if (!this.isValid(entity)) continue;
            if (!(entity instanceof PlayerEntity && entity != ESP.mc.player && ((Boolean)this.targets.getValueByName("\u0418\u0433\u0440\u043e\u043a\u0438").get()).booleanValue() || entity instanceof ItemEntity && ((Boolean)this.targets.getValueByName("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b").get()).booleanValue() || (entity instanceof AnimalEntity || entity instanceof MobEntity) && ((Boolean)this.targets.getValueByName("\u041c\u043e\u0431\u044b").get()).booleanValue())) {
                if (entity != ESP.mc.player || !((Boolean)this.targets.getValueByName("\u0421\u0435\u0431\u044f").get()).booleanValue() || ESP.mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) continue;
                Expensive.getInstance().getModuleManager().getWorldTweaks();
                if (WorldTweaks.child) continue;
            }
            double x = MathUtil.interpolate(entity.getPosX(), entity.lastTickPosX, (double)e.getPartialTicks());
            double y = MathUtil.interpolate(entity.getPosY(), entity.lastTickPosY, (double)e.getPartialTicks());
            double z = MathUtil.interpolate(entity.getPosZ(), entity.lastTickPosZ, (double)e.getPartialTicks());
            Vector3d size = new Vector3d(entity.getBoundingBox().maxX - entity.getBoundingBox().minX, entity.getBoundingBox().maxY - entity.getBoundingBox().minY, entity.getBoundingBox().maxZ - entity.getBoundingBox().minZ);
            AxisAlignedBB aabb = new AxisAlignedBB(x - size.x / 2.0, y, z - size.z / 2.0, x + size.x / 2.0, y + size.y, z + size.z / 2.0);
            Vector4f position = null;
            for (int i = 0; i < 8; ++i) {
                Vector2f vector = ProjectionUtil.project(i % 2 == 0 ? aabb.minX : aabb.maxX, i / 2 % 2 == 0 ? aabb.minY : aabb.maxY, i / 4 % 2 == 0 ? aabb.minZ : aabb.maxZ);
                if (position == null) {
                    position = new Vector4f(vector.x, vector.y, 1.0f, 1.0f);
                    continue;
                }
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
            this.positions.put(entity, position);
        }
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        for (Map.Entry entry : this.positions.entrySet()) {
            Vector4f position = (Vector4f)entry.getValue();
            Object y = entry.getKey();
            if (!(y instanceof LivingEntity)) continue;
            LivingEntity entity = (LivingEntity)y;
            if (!((Boolean)this.remove.getValueByName("\u0411\u043e\u043a\u0441\u044b").get()).booleanValue()) {
                DisplayUtils.drawBox(position.x - 0.5f, position.y - 0.5f, position.z + 0.5f, position.w + 0.5f, 2.0, ColorUtils.rgba(0, 0, 0, 128));
                DisplayUtils.drawBoxTest(position.x, position.y, position.z, position.w, 1.0, FriendStorage.isFriend(entity.getName().getString()) ? friendColors : colors);
            }
            float hpOffset = 3.0f;
            float out = 0.5f;
            if (((Boolean)this.remove.getValueByName("\u041f\u043e\u043b\u043e\u0441\u043a\u0443 \u0445\u043f").get()).booleanValue()) continue;
            String header = ESP.mc.ingameGUI.getTabList().header == null ? " " : ESP.mc.ingameGUI.getTabList().header.getString().toLowerCase();
            DisplayUtils.drawRectBuilding(position.x - hpOffset - out, position.y - out, position.x - hpOffset + 1.0f + out, position.w + out, ColorUtils.rgba(0, 0, 0, 128));
            DisplayUtils.drawRectBuilding(position.x - hpOffset, position.y, position.x - hpOffset + 1.0f, position.w, ColorUtils.rgba(0, 0, 0, 128));
            Score score = ESP.mc.world.getScoreboard().getOrCreateScore(entity.getScoreboardName(), ESP.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
            float hp = entity.getHealth();
            float maxHp = entity.getMaxHealth();
            if (mc.getCurrentServerData() != null && ESP.mc.getCurrentServerData().serverIP.contains("funtime") && (header.contains("\u0430\u043d\u0430\u0440\u0445\u0438\u044f") || header.contains("\u0433\u0440\u0438\u0444\u0435\u0440\u0441\u043a\u0438\u0439"))) {
                hp = score.getScorePoints();
                maxHp = 20.0f;
            }
            DisplayUtils.drawMCVerticalBuilding(position.x - hpOffset, position.y + (position.w - position.y) * (1.0f - MathHelper.clamp(hp / maxHp, 0.0f, 1.0f)), position.x - hpOffset + 1.0f, position.w, FriendStorage.isFriend(entity.getName().getString()) ? friendColors.w : colors.w, FriendStorage.isFriend(entity.getName().getString()) ? friendColors.x : colors.x);
        }
        Tessellator.getInstance().draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        for (Map.Entry entry : this.positions.entrySet()) {
            Entity entity = (Entity)entry.getKey();
            if (entity instanceof LivingEntity) {
                String header;
                LivingEntity living = (LivingEntity)entity;
                Score score = ESP.mc.world.getScoreboard().getOrCreateScore(living.getScoreboardName(), ESP.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
                float hp = living.getHealth();
                float maxHp = living.getMaxHealth();
                String string = header = ESP.mc.ingameGUI.getTabList().header == null ? " " : ESP.mc.ingameGUI.getTabList().header.getString().toLowerCase();
                if (mc.getCurrentServerData() != null && ESP.mc.getCurrentServerData().serverIP.contains("funtime") && (header.contains("\u0430\u043d\u0430\u0440\u0445\u0438\u044f") || header.contains("\u0433\u0440\u0438\u0444\u0435\u0440\u0441\u043a\u0438\u0439"))) {
                    hp = score.getScorePoints();
                    maxHp = 20.0f;
                }
                Vector4f position = (Vector4f)entry.getValue();
                float width = position.z - position.x;
                GL11.glPushMatrix();
                Object friendPrefix = FriendStorage.isFriend(entity.getName().getString()) ? TextFormatting.GREEN + "[F] " : "";
                Object creativePrefix = "";
                creativePrefix = entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative() ? TextFormatting.GRAY + " [" + TextFormatting.RED + "GM" + TextFormatting.GRAY + "]" : (mc.getCurrentServerData() != null && ESP.mc.getCurrentServerData().serverIP.contains("funtime") && (header.contains("\u0430\u043d\u0430\u0440\u0445\u0438\u044f") || header.contains("\u0433\u0440\u0438\u0444\u0435\u0440\u0441\u043a\u0438\u0439")) ? TextFormatting.GRAY + " [" + TextFormatting.RED + (int)hp + TextFormatting.GRAY + "]" : TextFormatting.GRAY + " [" + TextFormatting.RED + ((int)hp + (int)living.getAbsorptionAmount()) + TextFormatting.GRAY + "]");
                this.healthAnimation = MathUtil.fast(this.healthAnimation, MathHelper.clamp(hp / maxHp, 0.0f, 1.0f), 10.0f);
                TextComponent name = (TextComponent)ITextComponent.getTextComponentOrEmpty((String)friendPrefix);
                int colorRect = FriendStorage.isFriend(entity.getName().getString()) ? ColorUtils.rgba(66, 163, 60, 120) : ColorUtils.rgba(10, 10, 10, 120);
                name.append(FriendStorage.isFriend(entity.getName().getString()) ? ITextComponent.getTextComponentOrEmpty(TextFormatting.RED + "protected") : entity.getDisplayName());
                name.appendString((String)creativePrefix);
                this.glCenteredScale(position.x + width / 2.0f - this.length / 2.0f - 4.0f, position.y - 9.0f, this.length + 8.0f, 13.0f, 0.5f);
                this.length = ESP.mc.fontRenderer.getStringPropertyWidth(name);
                DisplayUtils.drawRoundedRect(position.x + width / 2.0f - this.length / 2.0f - 4.0f, position.y - 15.5f, this.length + 8.0f, 13.0f, 2.0f, colorRect);
                ESP.mc.fontRenderer.func_243246_a(e.getMatrixStack(), name, position.x + width / 2.0f - this.length / 2.0f, position.y - 12.5f, -1);
                GL11.glPopMatrix();
                if (!((Boolean)this.remove.getValueByName("\u0421\u043f\u0438\u0441\u043e\u043a \u044d\u0444\u0444\u0435\u043a\u0442\u043e\u0432").get()).booleanValue()) {
                    this.drawPotions(e.getMatrixStack(), living, position.z + 2.0f, position.y);
                }
                this.drawItems(e.getMatrixStack(), living, (int)(position.x + width / 2.0f), (int)(position.y - 14.5f));
                continue;
            }
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity item = (ItemEntity)entity;
            Vector4f position = (Vector4f)entry.getValue();
            float width = position.z - position.x;
            float length = ESP.mc.fontRenderer.getStringPropertyWidth(entity.getDisplayName());
            GL11.glPushMatrix();
            this.glCenteredScale(position.x + width / 2.0f - length / 2.0f, position.y - 7.0f, length, 10.0f, 0.5f);
            ClientFonts.msBold[24].drawString(e.getMatrixStack(), entity.getDisplayName(), (double)(position.x + width / 2.0f - length / 2.0f), (double)(position.y - 7.0f), -1);
            GL11.glPopMatrix();
        }
    }

    public boolean isInView(Entity ent) {
        if (mc.getRenderViewEntity() == null) {
            return false;
        }
        WorldRenderer.frustum.setCameraPosition(ESP.mc.getRenderManager().info.getProjectedView().x, ESP.mc.getRenderManager().info.getProjectedView().y, ESP.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(ent.getBoundingBox()) || ent.ignoreFrustumCheck;
    }

    private void drawPotions(MatrixStack matrixStack, LivingEntity entity, float posX, float posY) {
        for (EffectInstance effectInstance : entity.getActivePotionEffects()) {
            int amp = effectInstance.getAmplifier() + 1;
            Object ampStr = "";
            if (amp >= 1 && amp <= 9) {
                ampStr = " " + I18n.format("enchantment.level." + (amp + 1), new Object[0]);
            }
            String text = I18n.format(effectInstance.getEffectName(), new Object[0]) + (String)ampStr + " - " + EffectUtils.getPotionDurationString(effectInstance, 1.0f);
            PotionSpriteUploader potionspriteuploader = mc.getPotionSpriteUploader();
            Effect effect = effectInstance.getPotion();
            int iconSize = 8;
            TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
            mc.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
            DisplayEffectsScreen.blit(matrixStack, (int)posX, (int)posY - 1, iconSize, iconSize, iconSize, textureatlassprite);
            Fonts.consolas.drawTextWithOutline(matrixStack, text, posX + (float)iconSize, posY, -1, 6.0f, 0.05f);
            posY += Fonts.consolas.getHeight(6.0f) + 4.0f;
            ++this.index;
        }
    }

    private void drawItems(MatrixStack matrixStack, LivingEntity entity, int posX, int posY) {
        int size = 8;
        int padding = 6;
        float fontHeight = Fonts.consolas.getHeight(6.0f);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ItemStack mainStack = entity.getHeldItemMainhand();
        if (!mainStack.isEmpty()) {
            items.add(mainStack);
        }
        for (ItemStack itemStack : entity.getArmorInventoryList()) {
            if (itemStack.isEmpty()) continue;
            items.add(itemStack);
        }
        ItemStack offStack = entity.getHeldItemOffhand();
        if (!offStack.isEmpty()) {
            items.add(offStack);
        }
        posX = (int)((float)posX - (float)(items.size() * (size + padding)) / 2.0f);
        for (int i = 0; i < items.size(); ++i) {
            ItemStack itemStack = (ItemStack)items.get(i);
            if (itemStack.isEmpty()) continue;
            int bgColor = ColorUtils.rgba(10, 10, 10, 120);
            if (i == items.size() - 1 && entity.getHeldItemOffhand().equals(itemStack)) {
                bgColor = ColorUtils.rgba(139, 0, 0, 180);
            }
            DisplayUtils.drawRoundedRect((float)(posX - 2), (float)(posY - 7), (float)(size + 4), (float)(size + 4), 0.0f, bgColor);
            GL11.glPushMatrix();
            this.glCenteredScale(posX - 3, posY - 6, (float)size / 2.0f + 1.0f, (float)size / 2.0f, 0.5f);
            mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, posX, posY - 5);
            mc.getItemRenderer().renderItemOverlayIntoGUI(ESP.mc.fontRenderer, itemStack, posX, posY - 5, null);
            GL11.glPopMatrix();
            if (itemStack.isEnchanted() && !((Boolean)this.remove.getValueByName("\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u0438\u044f").get()).booleanValue()) {
                int ePosY = (int)((float)posY - fontHeight);
                Map<Enchantment, Integer> enchantmentsMap = EnchantmentHelper.getEnchantments(itemStack);
                for (Enchantment enchantment : enchantmentsMap.keySet()) {
                    int level = enchantmentsMap.get(enchantment);
                    if (level < 1 || !enchantment.canApply(itemStack)) continue;
                    TranslationTextComponent iformattabletextcomponent = new TranslationTextComponent(enchantment.getName());
                    String enchText = iformattabletextcomponent.getString().substring(0, 2) + level;
                    Fonts.consolas.drawText(matrixStack, enchText, posX, ePosY - 5, -1, 6.0f, 0.05f);
                    ePosY -= (int)fontHeight;
                }
            }
            posX += size + padding;
        }
    }

    public boolean isValid(Entity e) {
        if (AntiBot.isBot(e)) {
            return false;
        }
        return this.isInView(e);
    }

    public static void drawMcRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.pos(left, bottom, 1.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, bottom, 1.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, top, 1.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(left, top, 1.0).color(f, f1, f2, f3).endVertex();
    }

    public void glCenteredScale(float x, float y, float w, float h, float f) {
        GL11.glTranslatef(x + w / 2.0f, y + h / 2.0f, 0.0f);
        GL11.glScalef(f, f, 1.0f);
        GL11.glTranslatef(-x - w / 2.0f, -y - h / 2.0f, 0.0f);
    }
}

