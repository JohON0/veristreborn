/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.render.HUD;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.color.ColorUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

@ModuleRegister(name="China Hat", category=Category.Render)
public class ChinaHat
extends Module {
    public ModeSetting mod = new ModeSetting("\u0420\u0435\u043d\u0434\u0435\u0440", "ChinaHat", "ChinaHat", "Nimb");
    public static boolean hatrender;

    public ChinaHat(ChinaHat chinaHat) {
        this.addSettings(this.mod);
    }

    @Subscribe
    private void onRender(WorldEvent worldEvent) {
        if (this.mod.is("Nimb")) {
            hatrender = false;
            if (ChinaHat.mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                return;
            }
            float f = 0.47f;
            GlStateManager.pushMatrix();
            RenderSystem.translated(-ChinaHat.mc.getRenderManager().info.getProjectedView().x, -ChinaHat.mc.getRenderManager().info.getProjectedView().y, -ChinaHat.mc.getRenderManager().info.getProjectedView().z);
            Vector3d vector3d = MathUtil.interpolate(ChinaHat.mc.player.getPositionVec(), new Vector3d(ChinaHat.mc.player.lastTickPosX, ChinaHat.mc.player.lastTickPosY, ChinaHat.mc.player.lastTickPosZ), worldEvent.getPartialTicks());
            vector3d.y += (double)0.1f;
            RenderSystem.translated(vector3d.x, vector3d.y + (double)ChinaHat.mc.player.getHeight(), vector3d.z);
            double d = ChinaHat.mc.getRenderManager().info.getYaw();
            GL11.glRotatef((float)(-d), 0.0f, 1.0f, 0.0f);
            RenderSystem.translated(-vector3d.x, -(vector3d.y + (double)ChinaHat.mc.player.getHeight()), -vector3d.z);
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            RenderSystem.disableTexture();
            RenderSystem.disableCull();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.shadeModel(7425);
            RenderSystem.lineWidth(3.0f);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(2, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i <= 360; ++i) {
                float f2 = (float)(vector3d.x + (double)(MathHelper.sin((float)Math.toRadians(i)) * f));
                float f3 = (float)(vector3d.z + (double)(-MathHelper.cos((float)Math.toRadians(i)) * f));
                bufferBuilder.pos(f2, vector3d.y + (double)ChinaHat.mc.player.getHeight(), (double)f3).color(ColorUtils.setAlpha(HUD.getColor(i, 10, 10, 10.0f), 255)).endVertex();
            }
            Tessellator.getInstance().draw();
            GL11.glHint(3154, 4352);
            GL11.glDisable(2848);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.shadeModel(7424);
            GlStateManager.popMatrix();
        }
        if (this.mod.is("ChinaHat")) {
            hatrender = true;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        hatrender = false;
    }
}

