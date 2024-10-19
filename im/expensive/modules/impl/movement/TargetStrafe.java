/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.ActionEvent;
import im.expensive.events.EventDamageReceive;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.events.MovingEvent;
import im.expensive.events.PostMoveEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.HitAura;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.player.DamagePlayerUtil;
import im.expensive.utils.player.MoveUtils;
import im.expensive.utils.player.StrafeMovement;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;

@ModuleRegister(name="TargetStrafe", category=Category.Movement)
public class TargetStrafe
extends Module {
    private final SliderSetting distanceSetting = new SliderSetting("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f", 1.0f, 0.1f, 6.0f, 0.05f);
    private final SliderSetting speedSetting = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 0.0f, -0.1f, 0.5f, 0.01f);
    private final BooleanSetting damageBoostSetting = new BooleanSetting("\u0411\u0443\u0441\u0442 \u0441 \u0434\u0430\u043c\u0430\u0433\u043e\u043c", true);
    private final SliderSetting boostValueSetting = new SliderSetting("\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u0431\u0443\u0441\u0442\u0430", 1.5f, 0.1f, 5.0f, 0.05f).setVisible(() -> (Boolean)this.damageBoostSetting.get());
    private final SliderSetting timeSetting = new SliderSetting("\u0412\u0440\u0435\u043c\u044f \u0431\u0443\u0441\u0442\u0430", 10.0f, 1.0f, 20.0f, 1.0f).setVisible(() -> (Boolean)this.damageBoostSetting.get());
    private final BooleanSetting saveTarget = new BooleanSetting("\u0421\u043e\u0445\u0440\u0430\u043d\u044f\u0442\u044c \u0446\u0435\u043b\u044c", true);
    private float side = 1.0f;
    private LivingEntity target = null;
    private final DamagePlayerUtil damageUtil = new DamagePlayerUtil();
    private String targetName = "";
    public StrafeMovement strafeMovement = new StrafeMovement();
    private final HitAura hitAura;

    public TargetStrafe(HitAura hitAura) {
        this.hitAura = hitAura;
        this.addSettings(this.distanceSetting, this.speedSetting, this.saveTarget, this.damageBoostSetting, this.timeSetting);
    }

    @Subscribe
    private void onAction(ActionEvent e) {
        if (TargetStrafe.mc.player == null || TargetStrafe.mc.world == null) {
            return;
        }
        this.handleEventAction(e);
    }

    @Subscribe
    public void onMotion(MovingEvent event) {
        if (TargetStrafe.mc.player == null || TargetStrafe.mc.world == null || TargetStrafe.mc.player.ticksExisted < 10) {
            return;
        }
        boolean isLeftKeyPressed = InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 65);
        boolean isRightKeyPressed = InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 68);
        LivingEntity auraTarget = this.getTarget();
        if (auraTarget != null) {
            this.targetName = auraTarget.getName().getString();
        }
        this.target = this.shouldSaveTarget(auraTarget) ? this.updateTarget(this.target) : auraTarget;
        if (this.target != null && this.target.isAlive() && this.target.getHealth() > 0.0f) {
            if (TargetStrafe.mc.player.collidedHorizontally) {
                this.side *= -1.0f;
            }
            if (isLeftKeyPressed) {
                this.side = 1.0f;
            }
            if (isRightKeyPressed) {
                this.side = -1.0f;
            }
            double angle = Math.atan2(TargetStrafe.mc.player.getPosZ() - this.target.getPosZ(), TargetStrafe.mc.player.getPosX() - this.target.getPosX());
            double x = this.target.getPosX() + (double)((Float)this.distanceSetting.get()).floatValue() * Math.cos(angle += MoveUtils.getMotion() / (double)Math.max(TargetStrafe.mc.player.getDistance(this.target), this.distanceSetting.min) * (double)this.side);
            double z = this.target.getPosZ() + (double)((Float)this.distanceSetting.get()).floatValue() * Math.sin(angle);
            double yaw = this.getYaw(TargetStrafe.mc.player, x, z);
            this.damageUtil.time(((Float)this.timeSetting.get()).longValue() * 100L);
            float damageSpeed = ((Float)this.boostValueSetting.get()).floatValue() / 10.0f;
            double speed = this.strafeMovement.calculateSpeed(event, (Boolean)this.damageBoostSetting.get(), this.damageUtil.isNormalDamage(), true, damageSpeed) + (double)((Float)this.speedSetting.get()).floatValue();
            event.getMotion().x = speed * -Math.sin(Math.toRadians(yaw));
            event.getMotion().z = speed * Math.cos(Math.toRadians(yaw));
        }
    }

    @Subscribe
    private void onPostMove(PostMoveEvent e) {
        if (TargetStrafe.mc.player == null || TargetStrafe.mc.world == null) {
            return;
        }
        this.strafeMovement.postMove(e.getHorizontalMove());
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        if (TargetStrafe.mc.player == null || TargetStrafe.mc.world == null) {
            return;
        }
        if (e.getType() == EventPacket.Type.RECEIVE) {
            this.damageUtil.onPacketEvent(e);
            if (e.getPacket() instanceof SPlayerPositionLookPacket) {
                this.strafeMovement.setOldSpeed(0.0);
            }
        }
    }

    @Subscribe
    private void onDamage(EventDamageReceive e) {
        if (TargetStrafe.mc.player == null || TargetStrafe.mc.world == null) {
            return;
        }
        this.damageUtil.processDamage(e);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (TargetStrafe.mc.player.isOnGround() && !TargetStrafe.mc.gameSettings.keyBindJump.pressed && this.target != null && this.target.isAlive()) {
            TargetStrafe.mc.player.jump();
        }
    }

    @Override
    public void onEnable() {
        this.strafeMovement.setOldSpeed(0.0);
        this.target = null;
        super.onEnable();
    }

    private void handleEventAction(ActionEvent action) {
        if (this.strafes() && CEntityActionPacket.lastUpdatedSprint != this.strafeMovement.isNeedSprintState()) {
            action.setSprintState(!CEntityActionPacket.lastUpdatedSprint);
        }
        if (this.strafeMovement.isNeedSwap()) {
            action.setSprintState(!TargetStrafe.mc.player.serverSprintState);
            this.strafeMovement.setNeedSprintState(false);
        }
    }

    private LivingEntity getTarget() {
        return this.hitAura.isState() ? this.hitAura.getTarget() : null;
    }

    private LivingEntity updateTarget(LivingEntity currentTarget) {
        for (Entity entity : TargetStrafe.mc.world.getAllEntities()) {
            if (!(entity instanceof PlayerEntity) || !entity.getName().getString().equalsIgnoreCase(this.targetName)) continue;
            return (LivingEntity)entity;
        }
        return currentTarget;
    }

    private boolean shouldSaveTarget(LivingEntity target) {
        boolean settingIsEnabled = (Boolean)this.saveTarget.get();
        boolean targetAndTargetNameExist = target != null && this.targetName != null;
        return settingIsEnabled && targetAndTargetNameExist && this.hitAura.isState();
    }

    private double getYaw(LivingEntity entity, double x, double z) {
        return Math.toDegrees(Math.atan2(z - entity.getPosZ(), x - entity.getPosX())) - 90.0;
    }

    public boolean strafes() {
        BlockPos belowPosition;
        if (this.isInvalidPlayerState()) {
            return false;
        }
        BlockPos playerPosition = new BlockPos(TargetStrafe.mc.player.getPositionVec());
        BlockPos abovePosition = playerPosition.up();
        if (this.isSurfaceLiquid(abovePosition, belowPosition = playerPosition.down())) {
            return false;
        }
        if (this.isPlayerInWebOrSoulSand(playerPosition)) {
            return false;
        }
        return this.isPlayerAbleToStrafe();
    }

    private boolean isInvalidPlayerState() {
        return TargetStrafe.mc.player == null || TargetStrafe.mc.world == null || TargetStrafe.mc.player.isSneaking() || TargetStrafe.mc.player.isElytraFlying() || TargetStrafe.mc.player.isInWater() || TargetStrafe.mc.player.isInLava();
    }

    private boolean isSurfaceLiquid(BlockPos abovePosition, BlockPos belowPosition) {
        Block aboveBlock = TargetStrafe.mc.world.getBlockState(abovePosition).getBlock();
        Block belowBlock = TargetStrafe.mc.world.getBlockState(belowPosition).getBlock();
        return aboveBlock instanceof AirBlock && belowBlock == Blocks.WATER;
    }

    private boolean isPlayerInWebOrSoulSand(BlockPos playerPosition) {
        Material playerMaterial = TargetStrafe.mc.world.getBlockState(playerPosition).getMaterial();
        Block oneBelowBlock = TargetStrafe.mc.world.getBlockState(playerPosition.down()).getBlock();
        return playerMaterial == Material.WEB || oneBelowBlock instanceof SoulSandBlock;
    }

    private boolean isPlayerAbleToStrafe() {
        return !TargetStrafe.mc.player.abilities.isFlying && !TargetStrafe.mc.player.isPotionActive(Effects.LEVITATION);
    }
}

