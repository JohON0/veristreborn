/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import com.viaversion.viaversion.util.MathUtil;
import im.expensive.Expensive;
import im.expensive.events.EventMotion;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.player.PathUtil;
import im.expensive.utils.render.color.ColorUtils;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.render.RenderUtils;
import org.lwjgl.opengl.GL11;

@ModuleRegister(name="TPInfluence", category=Category.Combat)
public class TPInfluence
extends Module {
    public ModeSetting mode = new ModeSetting("\u041c\u043e\u0434", "VanillaH", "VanillaH", "VanillaVH", "StepV", "StepVH", "StepHG");
    public SliderSetting range = new SliderSetting("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f", 15.0f, 10.0f, 100.0f, 1.0f);
    public SliderSetting step = new SliderSetting("\u0420\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435", 5.0f, 2.0f, 50.0f, 1.0f);
    public BooleanSetting render = new BooleanSetting("\u0412\u0438\u0437\u0443\u0430\u043b\u0438\u0437\u0430\u0446\u0438\u044f", true);
    private PathUtil path;

    public TPInfluence() {
        this.addSettings(this.mode, this.range, this.step, this.render);
    }

    @Subscribe
    public void onRender(WorldEvent e) {
        if (((Boolean)this.render.get()).booleanValue() && this.path != null && Expensive.getInstance().getModuleManager().getHitAura().getTarget() != null) {
            for (Vector3d vec : this.path.getPath()) {
                GL11.glPushMatrix();
                Vector3d renderOffset = TPInfluence.mc.getRenderManager().info.getProjectedView();
                GL11.glTranslated(-renderOffset.x, -renderOffset.y, -renderOffset.z);
                float half = Expensive.getInstance().getModuleManager().getHitAura().getTarget().getWidth() / 2.0f;
                RenderUtils.drawBox(new AxisAlignedBB(vec.x - (double)half, vec.y, vec.z - (double)half, vec.x + (double)half, vec.y + (double)Expensive.getInstance().getModuleManager().getHitAura().getTarget().getHeight(), vec.z + (double)half), ColorUtils.setAlpha(-1, TPInfluence.getAlpha()));
                GL11.glPopMatrix();
            }
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (TPInfluence.mc.player == null || TPInfluence.mc.world == null) {
            return;
        }
        if (Expensive.getInstance().getModuleManager().getHitAura().getTarget() == null) {
            this.path = null;
            return;
        }
        this.path = new PathUtil(TPInfluence.mc.player.getPositionVec(), Expensive.getInstance().getModuleManager().getHitAura().getTarget().getPositionVec());
        this.path.calculatePath(((Float)this.step.get()).floatValue());
        if (this.path != null && Expensive.getInstance().getModuleManager().getHitAura().getStopWatch().hasTimeElapsed()) {
            for (Vector3d vec : this.path.getPath()) {
                this.sendPosition(vec, (String)this.mode.get());
            }
        }
    }

    public static int getAlpha() {
        Vector3d from = TPInfluence.mc.player.getPositionVec();
        Vector3d to = Expensive.getInstance().getModuleManager().getHitAura().getTarget().getPositionVec();
        double maxDistance = Expensive.getInstance().getModuleManager().getHitAura().maxRange();
        double distance = (float)from.distanceTo(to);
        double normalizedDistance = MathUtil.clamp((int)(distance / maxDistance), 0, 1);
        int alpha = (int)(255.0 * (1.0 - normalizedDistance));
        return alpha;
    }

    private void sendPosition(Vector3d vec, String mode) {
        int grInt = TPInfluence.mc.player.onGround ? 1 : 0;
        switch (mode) {
            case "StepVH": {
                this.sendPacket(vec.x, vec.y + 0.15, vec.z);
                this.sendPacket(vec.x, vec.y, vec.z);
                break;
            }
            case "StepV": {
                this.sendPacket(vec.x, vec.y, vec.z);
                this.sendPacket(vec.x, vec.y + (grInt == 1 ? 0.1 : -1.0E-13), vec.z);
                break;
            }
            case "StepHG": {
                this.sendPacket(vec.x, vec.y - this.positive(grInt - 1) * 1.0E-4 * 2.0, vec.z);
                break;
            }
            case "VanillaVH": {
                this.sendPacket(vec.x, vec.y + 0.0016, vec.z);
                break;
            }
            case "VanillaH": {
                this.sendPacket(vec.x, vec.y, vec.z);
            }
        }
    }

    private void sendPacket(double x, double y, double z) {
        mc.getConnection().sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
    }

    private double positive(double val2) {
        return val2 < 0.0 ? -val2 : val2;
    }

    private double calculate(double val1, double val2, double val3) {
        return Math.sqrt(val1 * val1 + val2 * val2 + val3 * val3);
    }

    private double calculate(double val1, double val2) {
        return Math.sqrt(val1 * val1 + val2 * val2);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.path = null;
    }
}

