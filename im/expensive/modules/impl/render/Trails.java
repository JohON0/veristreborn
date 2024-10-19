/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.events.EventUpdate;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.render.WorldTweaks;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.render.color.ColorUtils;
import java.util.ArrayList;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

@ModuleRegister(name="Trails", category=Category.Render)
public class Trails
extends Module {
    private SliderSetting delay = new SliderSetting("\u0414\u043b\u0438\u043d\u0430", 5.0f, 1.0f, 5.0f, 1.0f);

    public Trails() {
        this.addSettings(this.delay);
    }

    @Subscribe
    public void onRender(WorldEvent event) {
        MatrixStack matrixStack = event.getStack();
        for (PlayerEntity playerEntity : Trails.mc.world.getPlayers()) {
            playerEntity.points.removeIf(p -> p.time.isReached(((Float)this.delay.get()).longValue() * 100L));
            if (!(playerEntity instanceof ClientPlayerEntity) || playerEntity == Trails.mc.player && Trails.mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) continue;
            Vector3d player = new Vector3d(MathUtil.interpolate(playerEntity.getPosX(), playerEntity.lastTickPosX, (double)event.getPartialTicks()), MathUtil.interpolate(playerEntity.getPosY(), playerEntity.lastTickPosY, (double)event.getPartialTicks()), MathUtil.interpolate(playerEntity.getPosZ(), playerEntity.lastTickPosZ, (double)event.getPartialTicks()));
            playerEntity.points.add(new Point(player));
        }
        RenderSystem.pushMatrix();
        Vector3d projection = Trails.mc.getRenderManager().info.getProjectedView();
        RenderSystem.translated(-projection.x, -projection.y, -projection.z);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableTexture();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.shadeModel(7425);
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(3.0f);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        for (Entity entity : Trails.mc.world.getAllEntities()) {
            float index;
            float alpha;
            GL11.glBegin(8);
            ArrayList<Point> points = entity.points;
            for (Point point : points) {
                float dynamicHeight = entity.getHeight();
                if (WorldTweaks.child) {
                    dynamicHeight = entity.getHeight() / 2.0f;
                }
                float index2 = points.indexOf(point);
                alpha = index2 / (float)points.size();
                ColorUtils.setAlphaColor(ColorUtils.getColor(points.indexOf(point)), alpha * 0.5f);
                GL11.glVertex3d(point.getPosition().x, point.getPosition().y, point.getPosition().z);
                GL11.glVertex3d(point.getPosition().x, point.getPosition().y + (double)dynamicHeight, point.getPosition().z);
            }
            GL11.glEnd();
            GL11.glBegin(3);
            for (Point point : points) {
                index = points.indexOf(point);
                float alpha2 = index / (float)points.size();
                ColorUtils.setAlphaColor(ColorUtils.getColor(points.indexOf(point)), alpha2);
                GL11.glVertex3d(point.getPosition().x, point.getPosition().y, point.getPosition().z);
            }
            GL11.glEnd();
            GL11.glBegin(3);
            for (Point point : points) {
                index = points.indexOf(point);
                float dynamicHeight = entity.getHeight();
                if (WorldTweaks.child) {
                    dynamicHeight = entity.getHeight() / 2.0f;
                }
                alpha = index / (float)points.size();
                ColorUtils.setAlphaColor(ColorUtils.getColor(points.indexOf(point)), alpha);
                GL11.glVertex3d(point.getPosition().x, point.getPosition().y + (double)dynamicHeight, point.getPosition().z);
            }
            GL11.glEnd();
        }
        GL11.glHint(3154, 4352);
        GL11.glDisable(2848);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.shadeModel(7424);
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static class Point {
        private final Vector3d position;
        private final StopWatch time = new StopWatch();

        public Point(Vector3d position) {
            this.position = position;
        }

        public Vector3d getPosition() {
            return this.position;
        }

        public StopWatch getTime() {
            return this.time;
        }
    }
}

