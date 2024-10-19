/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.events.EventUpdate;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

@ModuleRegister(name="Snow", category=Category.Render)
public class Snow
extends Module {
    private final ModeSetting fallModeSetting = new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u041f\u0440\u043e\u0441\u0442\u043e\u0439", "\u041f\u0440\u043e\u0441\u0442\u043e\u0439", "\u041e\u0442\u0441\u043a\u043e\u043a\u0438", "\u0412\u0437\u043b\u0435\u0442");
    public static final ModeSetting setting = new ModeSetting("\u0412\u0438\u0434", "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", "\u041c\u043e\u043b\u043d\u0438\u044f", "\u0421\u043d\u0435\u0436\u0438\u043d\u043a\u0438", "\u041e\u0440\u0431\u0438\u0437\u044b");
    public final SliderSetting size = new SliderSetting("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e", 350.0f, 100.0f, 5000.0f, 50.0f);
    MatrixStack matrixStack = new MatrixStack();
    private static final CopyOnWriteArrayList<ParticleBase> particles = new CopyOnWriteArrayList();
    private float dynamicSpeed = this.fallModeSetting.is("\u041e\u0442\u0441\u043a\u043e\u043a\u0438") ? 0.1f : 0.4f;

    public Snow() {
        this.addSettings(this.fallModeSetting, setting, this.size);
    }

    private boolean isInView(Vector3d pos) {
        WorldRenderer.frustum.setCameraPosition(Snow.mc.getRenderManager().info.getProjectedView().x, Snow.mc.getRenderManager().info.getProjectedView().y, Snow.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(new AxisAlignedBB(pos.add(-0.2, -0.2, -0.2), pos.add(0.2, 0.2, 0.2)));
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        particles.removeIf(ParticleBase::tick);
        int n = particles.size();
        while ((float)n < ((Float)this.size.get()).floatValue()) {
            if (Snow.mc.currentScreen instanceof IngameMenuScreen) {
                return;
            }
            particles.add(new ParticleBase((float)(Snow.mc.player.getPosX() + (double)MathUtil.random(-48.0f, 48.0f)), (float)(Snow.mc.player.getPosY() + (double)MathUtil.random(-20.0f, this.fallModeSetting.is("\u0412\u0437\u043b\u0435\u0442") ? 0.0f : 48.0f)), (float)(Snow.mc.player.getPosZ() + (double)MathUtil.random(-48.0f, 48.0f)), MathUtil.random(-this.dynamicSpeed, this.dynamicSpeed), MathUtil.random(-0.1f, 0.1f), MathUtil.random(-this.dynamicSpeed, this.dynamicSpeed)));
            ++n;
        }
        particles.removeIf(particleBase -> System.currentTimeMillis() - particleBase.time > 5000L);
    }

    @Subscribe
    private void onRender(WorldEvent e) {
        Snow.render(this.matrixStack);
    }

    public static void render(MatrixStack matrixStack) {
        if (Snow.mc.currentScreen instanceof IngameMenuScreen) {
            return;
        }
        matrixStack.push();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
        particles.forEach(particleBase -> particleBase.render(bufferBuilder));
        bufferBuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferBuilder);
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        matrixStack.pop();
    }

    public class ParticleBase {
        public long time;
        protected float prevposX;
        protected float prevposY;
        protected float prevposZ;
        protected float posX;
        protected float posY;
        protected float posZ;
        protected float motionX;
        protected float motionY;
        protected float motionZ;
        protected int age;
        protected int maxAge;
        private float alpha;
        private long collisionTime = -1L;

        public ParticleBase(float x, float y, float z, float motionX, float motionY, float motionZ) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            this.prevposX = x;
            this.prevposY = y;
            this.prevposZ = z;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.time = System.currentTimeMillis();
            this.maxAge = this.age = (int)MathUtil.random(120.0f, 200.0f);
        }

        public void update() {
            this.alpha = MathUtil.fast(this.alpha, 1.0f, 10.0f);
            if (Snow.this.fallModeSetting.is("\u041e\u0442\u0441\u043a\u043e\u043a\u0438")) {
                this.updateWithBounce();
            }
        }

        public boolean tick() {
            int n = this.age = IMinecraft.mc.player.getDistanceSq(this.posX, this.posY, this.posZ) > 4096.0 ? (this.age = this.age - 8) : (this.age = this.age - 1);
            if (this.age < 0) {
                return true;
            }
            this.prevposX = this.posX;
            this.prevposY = this.posY;
            this.prevposZ = this.posZ;
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            if (Snow.this.fallModeSetting.is("\u041f\u0440\u043e\u0441\u0442\u043e\u0439")) {
                this.motionX *= 0.9f;
                this.motionY *= 0.9f;
                this.motionZ *= 0.9f;
                this.motionY -= 0.001f;
            } else {
                if (Snow.this.fallModeSetting.is("\u0412\u0437\u043b\u0435\u0442")) {
                    this.motionY += 0.1f;
                }
                this.motionX = 0.0f;
                this.motionZ = 0.0f;
            }
            return false;
        }

        private void updateWithBounce() {
            if (this.collisionTime != -1L) {
                long timeSinceCollision = System.currentTimeMillis() - this.collisionTime;
                this.alpha = Math.max(0.0f, 1.0f - (float)timeSinceCollision / 3000.0f);
            }
            this.motionY = (float)((double)this.motionY - 8.0E-4);
            float newPosX = this.posX + this.motionX;
            float newPosY = this.posY + this.motionY;
            float newPosZ = this.posZ + this.motionZ;
            BlockPos particlePos = new BlockPos(newPosX, newPosY, newPosZ);
            BlockState blockState = IMinecraft.mc.world.getBlockState(particlePos);
            if (!blockState.isAir()) {
                if (this.collisionTime == -1L) {
                    this.collisionTime = System.currentTimeMillis();
                }
                if (!IMinecraft.mc.world.getBlockState(new BlockPos(this.posX + this.motionX, this.posY, this.posZ)).isAir()) {
                    this.motionX = 0.0f;
                }
                if (!IMinecraft.mc.world.getBlockState(new BlockPos(this.posX, this.posY + this.motionY, this.posZ)).isAir()) {
                    this.motionY = -this.motionY * 0.8f;
                }
                if (!IMinecraft.mc.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ + this.motionZ)).isAir()) {
                    this.motionZ = 0.0f;
                }
                this.posX += this.motionX;
                this.posY += this.motionY;
                this.posZ += this.motionZ;
            } else {
                this.posX = newPosX;
                this.posY = newPosY;
                this.posZ = newPosZ;
            }
        }

        public void render(BufferBuilder bufferBuilder) {
            if (setting.is("\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438")) {
                IMinecraft.mc.getTextureManager().bindTexture(new ResourceLocation("eva/images/heart.png"));
            } else if (setting.is("\u0421\u043d\u0435\u0436\u0438\u043d\u043a\u0438")) {
                IMinecraft.mc.getTextureManager().bindTexture(new ResourceLocation("eva/images/snowflake.png"));
            } else if (setting.is("\u041c\u043e\u043b\u043d\u0438\u044f")) {
                IMinecraft.mc.getTextureManager().bindTexture(new ResourceLocation("eva/images/thor.png"));
            } else if (setting.is("\u041e\u0440\u0431\u0438\u0437\u044b")) {
                IMinecraft.mc.getTextureManager().bindTexture(new ResourceLocation("eva/images/firefly.png"));
            }
            float size = 1.0f - (float)(System.currentTimeMillis() - this.time) / 5000.0f;
            this.update();
            ActiveRenderInfo camera = IMinecraft.mc.gameRenderer.getActiveRenderInfo();
            int color = ColorUtils.setAlpha(Theme.mainRectColor, (int)((float)((int)(this.alpha * 255.0f)) * size));
            Vector3d pos = MathUtil.interpolatePos(this.prevposX, this.prevposY, this.prevposZ, this.posX, this.posY, this.posZ);
            MatrixStack matrices = new MatrixStack();
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.rotate(Vector3f.YP.rotationDegrees(-camera.getYaw()));
            matrices.rotate(Vector3f.XP.rotationDegrees(camera.getPitch()));
            Matrix4f matrix1 = matrices.getLast().getMatrix();
            bufferBuilder.pos(matrix1, 0.0f, -0.9f * size, 0.0f).color(color).tex(0.0f, 1.0f).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, -0.9f * size, -0.9f * size, 0.0f).color(color).tex(1.0f, 1.0f).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, -0.9f * size, 0.0f, 0.0f).color(color).tex(1.0f, 0.0f).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, 0.0f, 0.0f, 0.0f).color(color).tex(0.0f, 0.0f).lightmap(0, 240).endVertex();
        }
    }
}

