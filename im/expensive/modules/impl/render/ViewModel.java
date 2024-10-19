/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.events.EventDisplay;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.HitAura;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.render.CustomFramebuffer;
import im.expensive.utils.render.KawaseBlur;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.shader.impl.Outline;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

@ModuleRegister(name="ViewModel", category=Category.Render)
public class ViewModel
extends Module {
    public BooleanSetting glassHand = new BooleanSetting("GlassHand", false);
    public BooleanSetting swingAnim = new BooleanSetting("SwingAnim", true);
    public ModeSetting animationMode = new ModeSetting("\u041c\u043e\u0434", "4", "1", "2", "3", "4", "5", "6").setVisible(() -> (Boolean)this.swingAnim.get());
    public SliderSetting swingPower = new SliderSetting("\u0421\u0438\u043b\u0430", 5.0f, 1.0f, 10.0f, 0.05f).setVisible(() -> (Boolean)this.swingAnim.get());
    public SliderSetting swingSpeed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 8.0f, 3.0f, 10.0f, 1.0f).setVisible(() -> (Boolean)this.swingAnim.get());
    public SliderSetting scale = new SliderSetting("\u0420\u0430\u0437\u043c\u0435\u0440", 1.0f, 0.5f, 1.5f, 0.05f);
    public final BooleanSetting onlyAura = new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u0441 HitAura", false).setVisible(() -> (Boolean)this.swingAnim.get());
    public final SliderSetting x = new SliderSetting("\u041f\u043e\u0437\u0438\u0446\u0438\u044f X", 0.0f, -2.0f, 2.0f, 0.1f);
    public final SliderSetting y = new SliderSetting("\u041f\u043e\u0437\u0438\u0446\u0438\u044f Y", 0.0f, -2.0f, 2.0f, 0.1f);
    public final SliderSetting z = new SliderSetting("\u041f\u043e\u0437\u0438\u0446\u0438\u044f Z", 0.0f, -2.0f, 2.0f, 0.1f);
    public HitAura hitAura;
    boolean flag;
    public CustomFramebuffer hands = new CustomFramebuffer(false).setLinear();
    public CustomFramebuffer mask = new CustomFramebuffer(false).setLinear();

    public ViewModel(HitAura hitAura) {
        this.hitAura = hitAura;
        this.addSettings(this.glassHand, this.swingAnim, this.animationMode, this.swingPower, this.swingSpeed, this.scale, this.onlyAura, this.x, this.y, this.z);
    }

    @Subscribe
    public void onRender(EventDisplay e) {
        if (e.getType() != EventDisplay.Type.HIGH) {
            return;
        }
        if (ViewModel.mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON && ((Boolean)this.glassHand.get()).booleanValue()) {
            KawaseBlur.blur.updateBlur(3.0f, 4);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            ColorUtils.setColor(ColorUtils.getColor(0));
            KawaseBlur.blur.render(() -> this.hands.draw());
            Outline.registerRenderCall(() -> this.hands.draw());
            GlStateManager.disableAlphaTest();
            GlStateManager.popMatrix();
        }
    }

    public static void setSaturation(float saturation) {
        float[] saturationMatrix = new float[]{0.3086f * (1.0f - saturation) + saturation, 0.6094f * (1.0f - saturation), 0.082f * (1.0f - saturation), 0.0f, 0.0f, 0.3086f * (1.0f - saturation), 0.6094f * (1.0f - saturation) + saturation, 0.082f * (1.0f - saturation), 0.0f, 0.0f, 0.3086f * (1.0f - saturation), 0.6094f * (1.0f - saturation), 0.082f * (1.0f - saturation) + saturation, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
        GL11.glLoadMatrixf(saturationMatrix);
    }

    public void animationProcess(MatrixStack stack, float swingProgress, Runnable runnable) {
        float anim = (float)Math.sin((double)swingProgress * 1.5707963267948966 * 2.0);
        float sin1 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float sin2 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        if (((Boolean)this.onlyAura.get()).booleanValue() && this.hitAura.getTarget() == null) {
            return;
        }
        switch ((String)this.animationMode.get()) {
            case "5": {
                stack.scale(((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue());
                stack.translate(0.0, 0.1f, -0.1);
                stack.rotate(Vector3f.YP.rotationDegrees(60.0f));
                stack.rotate(Vector3f.ZP.rotationDegrees(-60.0f));
                stack.rotate(Vector3f.YP.rotationDegrees(sin2 * sin1 * -5.0f));
                stack.rotate(Vector3f.XP.rotationDegrees(-10.0f - ((Float)this.swingPower.get()).floatValue() * 10.0f * anim));
                stack.rotate(Vector3f.XP.rotationDegrees(-60.0f));
                break;
            }
            case "6": {
                stack.scale(((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue());
                stack.translate(0.0, 0.1f, -0.1);
                stack.translate(0.5, -0.1, 0.0);
                stack.rotate(Vector3f.XP.rotationDegrees(sin2 * -45.0f));
                stack.translate(-0.5, 0.1, 0.0);
                stack.translate(0.5, -0.1, 0.0);
                stack.rotate(Vector3f.YP.rotationDegrees(sin2 * -20.0f));
                stack.translate(-0.5, 0.1, 0.0);
                stack.rotate(Vector3f.YP.rotationDegrees(50.0f));
                stack.rotate(Vector3f.XP.rotationDegrees(-90.0f));
                stack.rotate(Vector3f.YP.rotationDegrees(50.0f));
                break;
            }
            case "1": {
                stack.scale(((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue());
                stack.translate(0.4f, 0.1f, -0.5);
                stack.rotate(Vector3f.YP.rotationDegrees(90.0f));
                stack.rotate(Vector3f.ZP.rotationDegrees(-60.0f));
                stack.rotate(Vector3f.XP.rotationDegrees(-90.0f - ((Float)this.swingPower.get()).floatValue() * 10.0f * anim));
                break;
            }
            case "2": {
                stack.scale(((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue());
                stack.translate(0.0, 0.0, 0.0);
                stack.rotate(Vector3f.YP.rotationDegrees(15.0f * anim));
                stack.rotate(Vector3f.ZP.rotationDegrees(-60.0f * anim));
                stack.rotate(Vector3f.XP.rotationDegrees((-90.0f - ((Float)this.swingPower.get()).floatValue()) * anim));
                break;
            }
            case "3": {
                stack.scale(((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue());
                stack.translate(0.4f, 0.0, -0.5);
                stack.rotate(Vector3f.YP.rotationDegrees(90.0f));
                stack.rotate(Vector3f.ZP.rotationDegrees(-30.0f));
                stack.rotate(Vector3f.XP.rotationDegrees(-90.0f - ((Float)this.swingPower.get()).floatValue() * 10.0f * anim));
                break;
            }
            default: {
                stack.scale(((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue(), ((Float)this.scale.get()).floatValue());
                runnable.run();
            }
        }
    }
}

