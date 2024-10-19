/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.events.JumpEvent;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.Setting;
import im.expensive.utils.render.color.ColorUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

@ModuleRegister(name="JumpCircle", category=Category.Render)
public class JumpCircle
extends Module {
    private final CopyOnWriteArrayList<Circle> circles = new CopyOnWriteArrayList();

    public JumpCircle() {
        this.addSettings(new Setting[0]);
    }

    @Subscribe
    private void onJump(JumpEvent e) {
        this.circles.add(new Circle(JumpCircle.mc.player.getPositon(mc.getRenderPartialTicks()).add(0.0, 0.05, 0.0)));
    }

    @Subscribe
    private void onRender(WorldEvent e) {
        GlStateManager.pushMatrix();
        GlStateManager.shadeModel(7425);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableCull();
        GlStateManager.translated(-JumpCircle.mc.getRenderManager().info.getProjectedView().x, -JumpCircle.mc.getRenderManager().info.getProjectedView().y, -JumpCircle.mc.getRenderManager().info.getProjectedView().z);
        for (Circle c : this.circles) {
            mc.getTextureManager().bindTexture(new ResourceLocation("eva/images/circle.png"));
            if (System.currentTimeMillis() - c.time > 2000L) {
                this.circles.remove(c);
            }
            if (System.currentTimeMillis() - c.time > 1500L && !c.isBack) {
                c.animation.animate(0.0, 0.5, Easings.BACK_IN);
                c.isBack = true;
            }
            c.animation.update();
            float rad = (float)c.animation.getValue();
            Vector3d vector3d = c.vector3d;
            vector3d = vector3d.add(-rad / 2.0f, 0.0, -rad / 2.0f);
            buffer.begin(6, DefaultVertexFormats.POSITION_COLOR_TEX);
            int alpha = (int)(255.0f * MathHelper.clamp(rad, 0.0f, 1.0f));
            buffer.pos(vector3d.x, vector3d.y, vector3d.z).color(ColorUtils.setAlpha(ColorUtils.getColor(5), alpha)).tex(0.0f, 0.0f).endVertex();
            buffer.pos(vector3d.x + (double)rad, vector3d.y, vector3d.z).color(ColorUtils.setAlpha(ColorUtils.getColor(10), alpha)).tex(1.0f, 0.0f).endVertex();
            buffer.pos(vector3d.x + (double)rad, vector3d.y, vector3d.z + (double)rad).color(ColorUtils.setAlpha(ColorUtils.getColor(15), alpha)).tex(1.0f, 1.0f).endVertex();
            buffer.pos(vector3d.x, vector3d.y, vector3d.z + (double)rad).color(ColorUtils.setAlpha(ColorUtils.getColor(20), alpha)).tex(0.0f, 1.0f).endVertex();
            tessellator.draw();
        }
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableAlphaTest();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private class Circle {
        private final Vector3d vector3d;
        private final long time;
        private final Animation animation = new Animation();
        private boolean isBack;

        public Circle(Vector3d vector3d) {
            this.vector3d = vector3d;
            this.time = System.currentTimeMillis();
            this.animation.animate(2.0, 0.5, Easings.BACK_OUT);
        }
    }
}

