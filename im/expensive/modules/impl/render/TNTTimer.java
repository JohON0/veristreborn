package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventDisplay;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.utils.animations.easing.CompactAnimation;
import im.expensive.utils.animations.easing.Easing;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.projections.ProjectionUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="TNTTimer", category=Category.Render)
public class TNTTimer
extends Module {
    private Map<String, CompactAnimation> animations = new HashMap<String, CompactAnimation>();

    @Subscribe
    public void onDisplay(EventDisplay e) {
        for (Entity entity : TNTTimer.mc.world.getAllEntities()) {
            if (!(entity instanceof TNTEntity)) continue;
            TNTEntity tnt = (TNTEntity)entity;
            String name = MathUtil.round((float)tnt.getFuse() / 20.0f, 1.0) + " \u0441\u0435\u043a";
            Vector3d pos = ProjectionUtil.interpolate(tnt, e.getPartialTicks());
            Vector2f vec = ProjectionUtil.project(pos.x, pos.y + (double)tnt.getHeight() + 0.5, pos.z);
            if (vec == null) {
                return;
            }
            float width = ClientFonts.interBold[16].getWidth(name) + 4.0f;
            float height = ClientFonts.interBold[16].getFontHeight();
            CompactAnimation easing = this.animations.getOrDefault(tnt.getDisplayName().getString(), null);
            if (easing == null) {
                easing = new CompactAnimation(Easing.EASE_IN_OUT_CUBIC, 250L);
                this.animations.put(tnt.getDisplayName().getString(), easing);
            }
            boolean tntActive = tnt.getFuse() > 10;
            easing.run(tntActive ? 1.0 : 0.0);
            float x = vec.x;
            float y = vec.y;
            int black = ColorUtils.getColor(10.0, 10.0, 10.0, 140.0 * easing.getValue());
            DisplayUtils.drawRoundedRect(x - width / 2.0f - 2.0f, y - 2.0f, width + 4.0f, height + 4.0f, 2.0f, black);
            ClientFonts.interBold[16].drawCenteredString(e.getMatrixStack(), name, (double)x, (double)(y + 2.5f), ColorUtils.setAlpha(-1, (int)(255.0 * easing.getValue())));
        }
    }
}

