/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.config.FriendStorage;
import im.expensive.events.EventChangeWorld;
import im.expensive.events.EventDisplay;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.AntiBot;
import im.expensive.modules.impl.render.Crosshair;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.math.animation.Animation;
import im.expensive.utils.math.animation.util.Easings;
import im.expensive.utils.player.MoveUtils;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.rect.DisplayUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

@ModuleRegister(name="Arrows", category=Category.Render)
public class Arrows
extends Module {
    public ModeListSetting targets = new ModeListSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c", new BooleanSetting("\u0418\u0433\u0440\u043e\u043a\u0438", true), new BooleanSetting("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", false), new BooleanSetting("\u041c\u043e\u0431\u044b", false));
    private boolean render = false;
    private final Animation yawAnimation = new Animation();
    private final Animation moveAnimation = new Animation();
    private final Animation openAnimation = new Animation();
    private float addX;
    private float addY;
    private float lastYaw;
    private float lastPitch;
    private ResourceLocation arrow = new ResourceLocation("eva/images/triangle.png");

    public Arrows() {
        this.addSettings(this.targets);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.setRender(false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.setRender(true);
    }

    @Subscribe
    public void onWorldChange(EventChangeWorld e) {
        this.setRender(this.isRender());
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        this.openAnimation.update();
        this.moveAnimation.update();
        this.yawAnimation.update();
        if (!this.render && this.openAnimation.getValue() == 0.0 && this.openAnimation.isFinished()) {
            return;
        }
        float moveAnim = this.calculateMoveAnimation();
        this.openAnimation.run(this.render ? 1.0 : 0.0, 0.3, Easings.BACK_OUT, true);
        this.moveAnimation.run(this.render ? (double)moveAnim : 0.0, 0.5, Easings.BACK_OUT, true);
        this.yawAnimation.run((double)Arrows.mc.gameRenderer.getActiveRenderInfo().getYaw(), 0.3, Easings.BACK_OUT, true);
        double cos = Math.cos(Math.toRadians(this.yawAnimation.getValue()));
        double sin = Math.sin(Math.toRadians(this.yawAnimation.getValue()));
        double radius = this.moveAnimation.getValue();
        double xOffset = this.scaled().x / 2.0 - radius;
        double yOffset = this.scaled().y / 2.0 - radius;
        for (Entity entity : Arrows.mc.world.getAllEntities()) {
            if (AntiBot.isBot(entity) || (!(entity instanceof PlayerEntity) || !((Boolean)this.targets.getValueByName("\u0418\u0433\u0440\u043e\u043a\u0438").get()).booleanValue()) && (!(entity instanceof ItemEntity) || !((Boolean)this.targets.getValueByName("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b").get()).booleanValue()) && (!(entity instanceof AnimalEntity) && !(entity instanceof MobEntity) || !((Boolean)this.targets.getValueByName("\u041c\u043e\u0431\u044b").get()).booleanValue()) || entity == Arrows.mc.player) continue;
            Vector3d vector3d = Arrows.mc.gameRenderer.getActiveRenderInfo().getProjectedView();
            double xWay = (entity.getPosX() + (entity.getPosX() - entity.lastTickPosX) * (double)mc.getRenderPartialTicks() - vector3d.x) * 0.01;
            double zWay = (entity.getPosZ() + (entity.getPosZ() - entity.lastTickPosZ) * (double)mc.getRenderPartialTicks() - vector3d.z) * 0.01;
            double rotationY = -(zWay * cos - xWay * sin);
            double rotationX = -(xWay * cos + zWay * sin);
            double angle = Math.toDegrees(Math.atan2(rotationY, rotationX));
            double x = radius * Math.cos(Math.toRadians(angle)) + xOffset + radius;
            double y = radius * Math.sin(Math.toRadians(angle)) + yOffset + radius;
            Crosshair crosshair = Expensive.getInstance().getModuleManager().getCrosshair();
            if (crosshair.isState() && crosshair.mode.is("\u041e\u0440\u0431\u0438\u0437") && !((Boolean)crosshair.staticCrosshair.get()).booleanValue() && Arrows.mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                this.addX = crosshair.getAnimatedYaw();
                this.addY = crosshair.getAnimatedPitch();
            } else {
                this.addY = 0.0f;
                this.addX = 0.0f;
            }
            x += (double)this.addX;
            y += (double)this.addY;
            if (!this.isValidRotation(rotationX, rotationY, radius)) continue;
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, 0.0);
            GL11.glRotated(angle, 0.0, 0.0, 1.0);
            GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            int color = FriendStorage.isFriend(TextFormatting.getTextWithoutFormattingCodes(entity.getName().getString())) ? ColorUtils.getColor(25, 227, 142) : (Expensive.getInstance().getModuleManager().getHitAura().getTarget() == entity ? ColorUtils.getColor(242, 63, 67) : Theme.mainRectColor);
            DisplayUtils.drawImage(new ResourceLocation("eva/images/arrow.png"), -8.0f, -9.0f, 16.0f, 16.0f, color);
            GL11.glPopMatrix();
        }
        this.lastYaw = Arrows.mc.player.rotationYaw;
        this.lastPitch = Arrows.mc.player.rotationPitch;
    }

    private float calculateMoveAnimation() {
        float set = 25.0f;
        Screen screen = Arrows.mc.currentScreen;
        if (screen instanceof ContainerScreen) {
            ContainerScreen container = (ContainerScreen)screen;
            set = (float)Math.max(container.ySize, container.xSize) / 2.0f + 50.0f;
        }
        float moveAnim = set;
        if (MoveUtils.isMoving()) {
            moveAnim += Arrows.mc.player.isSneaking() ? 5.0f : 10.0f;
        } else if (Arrows.mc.player.isSneaking()) {
            moveAnim -= 10.0f;
        }
        return moveAnim;
    }

    private boolean isValidRotation(double rotationX, double rotationY, double radius) {
        double mrotX = -rotationX;
        double mrotY = -rotationY;
        return (double)MathHelper.sqrt(mrotX * mrotX + mrotY * mrotY) < radius;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public boolean isRender() {
        return this.render;
    }
}

