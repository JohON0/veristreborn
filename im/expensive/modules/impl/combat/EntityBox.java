/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

@ModuleRegister(name="EntityBox", category=Category.Combat)
public class EntityBox
extends Module {
    public final SliderSetting size = new SliderSetting("\u0420\u0430\u0437\u043c\u0435\u0440", 0.2f, 0.0f, 3.0f, 0.05f);
    public final BooleanSetting visible = new BooleanSetting("\u0412\u0438\u0434\u0438\u043c\u044b\u0435", false);

    public EntityBox() {
        this.addSettings(this.size, this.visible);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (!((Boolean)this.visible.get()).booleanValue() || EntityBox.mc.player == null) {
            return;
        }
        float sizeMultiplier = ((Float)this.size.get()).floatValue() * 2.5f;
        for (PlayerEntity playerEntity : EntityBox.mc.world.getPlayers()) {
            if (this.isNotValid(playerEntity)) continue;
            playerEntity.setBoundingBox(this.calculateBoundingBox(playerEntity, sizeMultiplier));
        }
    }

    private boolean isNotValid(PlayerEntity player) {
        return player == EntityBox.mc.player || !player.isAlive();
    }

    private AxisAlignedBB calculateBoundingBox(Entity entity, float size) {
        double minX = entity.getPosX() - (double)size;
        double minY = entity.getBoundingBox().minY;
        double minZ = entity.getPosZ() - (double)size;
        double maxX = entity.getPosX() + (double)size;
        double maxY = entity.getBoundingBox().maxY;
        double maxZ = entity.getPosZ() + (double)size;
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}

