/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.config.FriendStorage;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.AntiBot;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.utils.player.EntityUtils;
import im.expensive.utils.render.color.ColorUtils;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

@ModuleRegister(name="Tracers", category=Category.Render)
public class Tracers
extends Module {
    public ModeListSetting targets = new ModeListSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c", new BooleanSetting("\u0418\u0433\u0440\u043e\u043a\u0438", true), new BooleanSetting("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", false), new BooleanSetting("\u041c\u043e\u0431\u044b", false));

    public Tracers() {
        this.addSettings(this.targets);
    }

    @Subscribe
    public void onRender(WorldEvent e) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glLineWidth(1.0f);
        Vector3d cam = new Vector3d(0.0, 0.0, 150.0).rotatePitch((float)(-Math.toRadians(Tracers.mc.getRenderManager().info.getPitch()))).rotateYaw((float)(-Math.toRadians(Tracers.mc.getRenderManager().info.getYaw())));
        for (Entity entity : Tracers.mc.world.getAllEntities()) {
            if ((!(entity instanceof PlayerEntity) || entity == Tracers.mc.player || !((Boolean)this.targets.getValueByName("\u0418\u0433\u0440\u043e\u043a\u0438").get()).booleanValue()) && (!(entity instanceof ItemEntity) || !((Boolean)this.targets.getValueByName("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b").get()).booleanValue()) && (!(entity instanceof AnimalEntity) && !(entity instanceof MobEntity) || !((Boolean)this.targets.getValueByName("\u041c\u043e\u0431\u044b").get()).booleanValue()) || AntiBot.isBot(entity) || !entity.isAlive()) continue;
            Vector3d pos = EntityUtils.getInterpolatedPositionVec(entity).subtract(Tracers.mc.getRenderManager().info.getProjectedView());
            ColorUtils.setColor(FriendStorage.isFriend(entity.getName().getString()) ? FriendStorage.getColor() : -1);
            buffer.begin(1, DefaultVertexFormats.POSITION);
            buffer.pos(cam.x, cam.y, cam.z).endVertex();
            buffer.pos(pos.x, pos.y, pos.z).endVertex();
            tessellator.draw();
        }
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glPopMatrix();
    }
}

