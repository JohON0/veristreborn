/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventDisplay;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.projections.ProjectionUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

@ModuleRegister(name="Predictions", category=Category.Render)
public class Predictions
extends Module {
    private Map<String, Float> predictedTimes = new HashMap<String, Float>();
    private Map<String, Vector3d> startPositionMap = new HashMap<String, Vector3d>();
    private Map<String, Vector3d> endPositionMap = new HashMap<String, Vector3d>();
    private Map<String, Vector3d> smoothedEndPositionMap = new HashMap<String, Vector3d>();
    private Map<String, Float> smoothedTimeMap = new HashMap<String, Float>();
    private static final float SMOOTHING_FACTOR = 0.1f;

    @Subscribe
    public void onRender(WorldEvent event) {
        for (Entity entity : Predictions.mc.world.getAllEntities()) {
            Vector3d pos;
            if (!(entity instanceof EnderPearlEntity)) continue;
            EnderPearlEntity throwable = (EnderPearlEntity)entity;
            Vector3d motion = throwable.getMotion();
            Vector3d startPos = pos = throwable.getPositionVec();
            int steps = 0;
            boolean hitGround = false;
            for (int i = 0; i < 150; ++i) {
                boolean isLast;
                Vector3d prevPos = pos;
                pos = pos.add(motion);
                motion = this.getNextMotion(throwable, motion);
                ++steps;
                RayTraceContext rayTraceContext = new RayTraceContext(prevPos, pos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, throwable);
                BlockRayTraceResult blockHitResult = Predictions.mc.world.rayTraceBlocks(rayTraceContext);
                boolean bl = isLast = blockHitResult.getType() == RayTraceResult.Type.BLOCK;
                if (isLast) {
                    pos = blockHitResult.getHitVec();
                    hitGround = true;
                    break;
                }
                if (pos.y < 0.0) break;
            }
            if (hitGround) {
                float timeToHitMillis = (float)steps * 50.0f;
                this.predictedTimes.put(throwable.getUniqueID().toString(), Float.valueOf(timeToHitMillis));
                this.startPositionMap.put(throwable.getUniqueID().toString(), startPos);
                this.endPositionMap.put(throwable.getUniqueID().toString(), pos);
                continue;
            }
            float existingTime = this.predictedTimes.getOrDefault(throwable.getUniqueID().toString(), Float.valueOf(0.0f)).floatValue();
            float newTimeToHitMillis = existingTime - 50.0f;
            this.predictedTimes.put(throwable.getUniqueID().toString(), Float.valueOf(Math.max(newTimeToHitMillis, 0.0f)));
        }
        this.updateSmoothedValues();
        this.drawLines();
    }

    private void updateSmoothedValues() {
        for (String id : this.endPositionMap.keySet()) {
            Vector3d targetEndPos = this.endPositionMap.get(id);
            Vector3d currentSmoothedPos = this.smoothedEndPositionMap.getOrDefault(id, targetEndPos);
            Vector3d newSmoothedPos = new Vector3d(MathUtil.lerp(currentSmoothedPos.x, targetEndPos.x, (double)0.1f), MathUtil.lerp(currentSmoothedPos.y, targetEndPos.y, (double)0.1f), MathUtil.lerp(currentSmoothedPos.z, targetEndPos.z, (double)0.1f));
            this.smoothedEndPositionMap.put(id, newSmoothedPos);
            float targetTime = this.predictedTimes.getOrDefault(id, Float.valueOf(0.0f)).floatValue();
            float currentTime = this.smoothedTimeMap.getOrDefault(id, Float.valueOf(targetTime)).floatValue();
            float newSmoothedTime = MathUtil.lerp(currentTime, targetTime, 0.1f);
            this.smoothedTimeMap.put(id, Float.valueOf(newSmoothedTime));
        }
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        for (Entity entity : Predictions.mc.world.getAllEntities()) {
            if (!(entity instanceof EnderPearlEntity)) continue;
            EnderPearlEntity throwable = (EnderPearlEntity)entity;
            String id = throwable.getUniqueID().toString();
            float timeInMillis = this.predictedTimes.getOrDefault(id, Float.valueOf(0.0f)).floatValue();
            int seconds = (int)(timeInMillis / 1000.0f);
            int millis = (int)(timeInMillis % 1000.0f) / 10;
            String timeString = String.format("%d.%02d \u0441\u0435\u043a.", seconds, millis);
            Vector3d projectedPos = ProjectionUtil.interpolate(throwable, e.getPartialTicks());
            Vector2f screenPos = ProjectionUtil.project(projectedPos.x, projectedPos.y + (double)throwable.getHeight() + 0.5, projectedPos.z);
            if (screenPos == null) continue;
            float width = ClientFonts.interBold[16].getWidth(timeString) + 4.0f;
            float height = ClientFonts.interBold[16].getFontHeight();
            float x = screenPos.x;
            float y = screenPos.y;
            int backgroundColor = ColorUtils.getColor(10, 10, 10, 140);
            DisplayUtils.drawRoundedRect(x - width / 2.0f - 2.0f, y - 2.0f, width + 4.0f, height + 4.0f, 2.0f, backgroundColor);
            ClientFonts.interBold[16].drawCenteredString(e.getMatrixStack(), timeString, (double)x, (double)(y + 2.5f), -1);
        }
    }

    private void drawLines() {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        for (Entity entity : Predictions.mc.world.getAllEntities()) {
            if (!(entity instanceof EnderPearlEntity)) continue;
            EnderPearlEntity throwable = (EnderPearlEntity)entity;
            String id = throwable.getUniqueID().toString();
            Vector3d startPos = this.startPositionMap.get(id);
            Vector3d endPos = this.smoothedEndPositionMap.get(id);
            if (startPos == null || endPos == null) continue;
            buffer.pos(startPos.x - Predictions.mc.getRenderManager().info.getProjectedView().x, startPos.y - Predictions.mc.getRenderManager().info.getProjectedView().y, startPos.z - Predictions.mc.getRenderManager().info.getProjectedView().z).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
            buffer.pos(endPos.x - Predictions.mc.getRenderManager().info.getProjectedView().x, endPos.y - Predictions.mc.getRenderManager().info.getProjectedView().y, endPos.z - Predictions.mc.getRenderManager().info.getProjectedView().z).color(1.0f, 0.0f, 0.0f, 1.0f).endVertex();
        }
        Tessellator.getInstance().draw();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    private Vector3d getNextMotion(ThrowableEntity throwable, Vector3d motion) {
        motion = throwable.isInWater() ? motion.scale(0.8) : motion.scale(0.99);
        if (!throwable.hasNoGravity()) {
            motion = new Vector3d(motion.x, motion.y - (double)throwable.getGravityVelocity(), motion.z);
        }
        return motion;
    }
}

