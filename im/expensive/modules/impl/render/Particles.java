/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.AttackEvent;
import im.expensive.events.EventDisplay;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.projections.ProjectionUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="Particles", category=Category.Render)
public class Particles
extends Module {
    private final ModeSetting setting = new ModeSetting("\u0412\u0438\u0434", "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", "\u041e\u0440\u0431\u0438\u0437\u044b", "\u041c\u043e\u043b\u043d\u0438\u044f", "\u0421\u043d\u0435\u0436\u0438\u043d\u043a\u0438");
    private final SliderSetting value = new SliderSetting("\u041a\u043e\u043b-\u0432\u043e \u0437\u0430 \u0443\u0434\u0430\u0440", 20.0f, 1.0f, 50.0f, 1.0f);
    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList();

    public Particles() {
        this.addSettings(this.setting, this.value);
    }

    private boolean isInView(Vector3d pos) {
        WorldRenderer.frustum.setCameraPosition(Particles.mc.getRenderManager().info.getProjectedView().x, Particles.mc.getRenderManager().info.getProjectedView().y, Particles.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(new AxisAlignedBB(pos.add(-0.2, -0.2, -0.2), pos.add(0.2, 0.2, 0.2)));
    }

    private boolean isVisible(Vector3d pos) {
        Vector3d cameraPos = Particles.mc.getRenderManager().info.getProjectedView();
        RayTraceContext context = new RayTraceContext(cameraPos, pos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, Particles.mc.player);
        BlockRayTraceResult result = Particles.mc.world.rayTraceBlocks(context);
        return result.getType() == RayTraceResult.Type.MISS;
    }

    @Subscribe
    private void onUpdate(AttackEvent e) {
        if (e.entity == Particles.mc.player) {
            return;
        }
        Entity entity = e.entity;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            Vector3d center = livingEntity.getPositionVec().add(0.0, livingEntity.getHeight() / 2.0f, 0.0);
            int i = 0;
            while ((float)i < ((Float)this.value.get()).floatValue()) {
                this.particles.add(new Particle(center));
                ++i;
            }
        }
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        if (Particles.mc.player == null || Particles.mc.world == null || e.getType() != EventDisplay.Type.PRE) {
            return;
        }
        for (Particle p : this.particles) {
            if (System.currentTimeMillis() - p.time > 7000L || p.alpha <= 0.0f) {
                this.particles.remove(p);
                continue;
            }
            if (Particles.mc.player.getPositionVec().distanceTo(p.pos) > 30.0) {
                this.particles.remove(p);
                continue;
            }
            if (this.isInView(p.pos) && this.isVisible(p.pos)) {
                p.update();
                Vector2f pos = ProjectionUtil.project(p.pos.x, p.pos.y, p.pos.z);
                float size = 1.0f - (float)(System.currentTimeMillis() - p.time) / 7000.0f;
                float margin = this.setting.is("\u041e\u0440\u0431\u0438\u0437\u044b") ? 0.0f : 3.0f;
                DisplayUtils.drawShadowCircle(pos.x + margin, pos.y + margin, 10.0f, ColorUtils.setAlpha(Theme.mainRectColor, (int)(64.0f * p.alpha * size)));
                switch ((String)this.setting.get()) {
                    case "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438": {
                        Fonts.damage.drawText(e.getMatrixStack(), "B", pos.x - 3.0f * size, pos.y - 3.0f * size, ColorUtils.setAlpha(Theme.mainRectColor, (int)(200.0f * p.alpha * size)), 15.0f * size, 0.05f);
                        break;
                    }
                    case "\u0421\u043d\u0435\u0436\u0438\u043d\u043a\u0438": {
                        Fonts.damage.drawText(e.getMatrixStack(), "A", pos.x - 3.0f * size, pos.y - 3.0f * size, ColorUtils.setAlpha(Theme.mainRectColor, (int)(200.0f * p.alpha * size)), 15.0f * size, 0.05f);
                        break;
                    }
                    case "\u041c\u043e\u043b\u043d\u0438\u044f": {
                        Fonts.damage.drawText(e.getMatrixStack(), "C", pos.x - 3.0f * size, pos.y - 3.0f * size, ColorUtils.setAlpha(Theme.mainRectColor, (int)(200.0f * p.alpha * size)), 15.0f * size, 0.05f);
                        break;
                    }
                    case "\u041e\u0440\u0431\u0438\u0437\u044b": {
                        DisplayUtils.drawCircle(pos.x, pos.y, 5.0f * size, ColorUtils.setAlpha(Theme.mainRectColor, (int)(200.0f * p.alpha * size)));
                    }
                }
                continue;
            }
            this.particles.remove(p);
        }
    }

    private class Particle {
        private Vector3d pos;
        private Vector3d end;
        private long time;
        private long collisionTime = -1L;
        private Vector3d velocity;
        private float alpha;

        public Particle(Vector3d pos) {
            this.pos = pos;
            this.end = pos.add(-ThreadLocalRandom.current().nextFloat(-1.0f, 1.0f), -ThreadLocalRandom.current().nextFloat(-1.0f, 1.0f), -ThreadLocalRandom.current().nextFloat(-1.0f, 1.0f));
            this.time = System.currentTimeMillis();
            double speed = ThreadLocalRandom.current().nextDouble(0.01, 0.05);
            this.velocity = new Vector3d(ThreadLocalRandom.current().nextDouble(-speed, speed), ThreadLocalRandom.current().nextDouble(-speed, speed), ThreadLocalRandom.current().nextDouble(-speed, speed));
            this.alpha = 1.0f;
        }

        public void update() {
            Vector3d newPos;
            BlockPos particlePos;
            BlockState blockState;
            if (this.collisionTime != -1L) {
                long timeSinceCollision = System.currentTimeMillis() - this.collisionTime;
                this.alpha = Math.max(0.0f, 1.0f - (float)timeSinceCollision / 1000.0f);
            }
            if (!(blockState = IMinecraft.mc.world.getBlockState(particlePos = new BlockPos(newPos = this.pos.add(this.velocity)))).isAir()) {
                if (this.collisionTime == -1L) {
                    this.collisionTime = System.currentTimeMillis();
                }
                if (!IMinecraft.mc.world.getBlockState(new BlockPos(this.pos.x + this.velocity.x, this.pos.y, this.pos.z)).isAir()) {
                    this.velocity = new Vector3d(-this.velocity.x * 0.7, this.velocity.y, this.velocity.z);
                }
                if (!IMinecraft.mc.world.getBlockState(new BlockPos(this.pos.x, this.pos.y + this.velocity.y, this.pos.z)).isAir()) {
                    this.velocity = new Vector3d(this.velocity.x, -this.velocity.y * 0.7, this.velocity.z);
                }
                if (!IMinecraft.mc.world.getBlockState(new BlockPos(this.pos.x, this.pos.y, this.pos.z + this.velocity.z)).isAir()) {
                    this.velocity = new Vector3d(this.velocity.x, this.velocity.y, -this.velocity.z * 0.7);
                }
                this.pos = this.pos.add(this.velocity);
            } else {
                this.pos = newPos;
            }
            this.velocity = this.velocity.scale(0.98);
        }
    }
}

