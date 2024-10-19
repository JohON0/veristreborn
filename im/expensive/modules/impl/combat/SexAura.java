/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.utils.rotation.RotationUtils;
import java.util.ArrayList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="SexAura", category=Category.Combat)
public class SexAura
extends Module {
    boolean isActionEnabled;

    @Subscribe
    public void onUpdate(EventUpdate event) {
        this.handleUpdate();
    }

    @Subscribe
    private void onMotion(EventMotion e) {
        this.handleEventMotion(e);
    }

    private void handleEventMotion(EventMotion event) {
        LivingEntity target = this.getClosestEntity(25.0);
        if (target != null) {
            float[] rots = RotationUtils.getMatrixRots(target);
            event.setYaw(rots[0]);
            event.setPitch(rots[1]);
            SexAura.mc.player.rotationYaw = rots[0];
        }
    }

    public void handleUpdate() {
        LivingEntity target = this.getClosestEntity(25.0);
        if (target != null) {
            SexAura.mc.gameSettings.keyBindJump.setPressed(target.getPosY() > SexAura.mc.player.getPosY() || SexAura.mc.player.collidedHorizontally && target.getPosY() == SexAura.mc.player.getPosY());
            boolean isCloseEnough = this.moveTowardsTargetIfNecessary(this.getTargetPositionOffset(target), MathHelper.clamp(target.getWidth() / 2.0f, 0.1f, 2.0f), target);
            this.performActionIfRequired(isCloseEnough);
        }
    }

    private Vector3d getTargetPositionOffset(LivingEntity target) {
        return target.getPositionVec().add(Math.sin(Math.toRadians(target.renderYawOffset)) * (double)0.3f, 0.0, -Math.cos(Math.toRadians(target.renderYawOffset)) * (double)0.3f);
    }

    private boolean moveTowardsTargetIfNecessary(Vector3d vec, float checkR, LivingEntity target) {
        boolean isCloseEnough;
        boolean bl = isCloseEnough = SexAura.mc.player.getDistanceSq(vec) <= (double)checkR;
        if (!isCloseEnough) {
            KeyBinding.setKeyBindState(SexAura.mc.gameSettings.keyBindForward.getDefault(), true);
        } else {
            KeyBinding.setKeyBindState(SexAura.mc.gameSettings.keyBindForward.getDefault(), false);
        }
        return isCloseEnough;
    }

    private void performActionIfRequired(boolean DO) {
        if (DO) {
            boolean bl = SexAura.mc.gameSettings.keyBindSneak.pressed = SexAura.mc.player.ticksExisted % 2 == 0;
            if (!this.isActionEnabled) {
                this.isActionEnabled = true;
            }
        } else if (this.isActionEnabled) {
            SexAura.mc.gameSettings.keyBindSneak.pressed = false;
            this.isActionEnabled = false;
        }
    }

    public LivingEntity getClosestEntity(double range) {
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        for (Entity entity : SexAura.mc.world.getAllEntities()) {
            if (!(entity instanceof LivingEntity) || entity instanceof ArmorStandEntity || entity == SexAura.mc.player || !((double)SexAura.mc.player.getDistance(entity) <= range)) continue;
            targets.add((LivingEntity)entity);
        }
        LivingEntity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;
        for (LivingEntity target : targets) {
            double distance = SexAura.mc.player.getDistance(target);
            if (!(distance < closestDistance)) continue;
            closestEntity = target;
            closestDistance = distance;
        }
        return closestEntity;
    }

    @Override
    public void onEnable() {
        this.performActionIfRequired(false);
        super.onEnable();
    }
}

