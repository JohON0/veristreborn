/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.ActionEvent;
import im.expensive.events.EventDamageReceive;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.events.MovingEvent;
import im.expensive.events.PostMoveEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.HitAura;
import im.expensive.modules.impl.movement.TargetStrafe;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.player.DamagePlayerUtil;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MoveUtils;
import im.expensive.utils.player.StrafeMovement;
import java.util.Random;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;

@ModuleRegister(name="Strafe", category=Category.Movement)
public class Strafe
extends Module {
    private final ModeSetting mode = new ModeSetting("\u041e\u0431\u0445\u043e\u0434", "Matrix Hard", "Matrix", "Matrix Hard");
    private final BooleanSetting elytra = new BooleanSetting("\u0411\u0443\u0441\u0442 \u0441 \u044d\u043b\u0438\u0442\u0440\u043e\u0439", false);
    private final SliderSetting setSpeed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 1.5f, 0.5f, 2.5f, 0.1f).setVisible(() -> (Boolean)this.elytra.get());
    private final BooleanSetting damageBoost = new BooleanSetting("\u0411\u0443\u0441\u0442 \u0441 \u0434\u0430\u043c\u0430\u0433\u043e\u043c", false);
    private final SliderSetting boostSpeed = new SliderSetting("\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u0431\u0443\u0441\u0442\u0430", 0.7f, 0.1f, 5.0f, 0.1f).setVisible(() -> (Boolean)this.damageBoost.get());
    private final BooleanSetting onlyGround = new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u043d\u0430 \u0437\u0435\u043c\u043b\u0435", false).setVisible(() -> this.mode.is("Matrix Hard"));
    private final BooleanSetting autoJump = new BooleanSetting("\u041f\u0440\u044b\u0433\u0430\u0442\u044c", false);
    private final BooleanSetting moveDir = new BooleanSetting("\u041d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435", true);
    private final DamagePlayerUtil damageUtil = new DamagePlayerUtil();
    private final StrafeMovement strafeMovement = new StrafeMovement();
    private final TargetStrafe targetStrafe;
    private final HitAura hitAura;
    public static int waterTicks;

    public boolean check() {
        return Expensive.getInstance().getModuleManager().getHitAura().getTarget() != null && Expensive.getInstance().getModuleManager().getHitAura().isState();
    }

    public Strafe(TargetStrafe targetStrafe, HitAura hitAura) {
        this.targetStrafe = targetStrafe;
        this.hitAura = hitAura;
        this.addSettings(this.mode, this.elytra, this.setSpeed, this.damageBoost, this.boostSpeed, this.onlyGround, this.autoJump, this.moveDir);
    }

    @Subscribe
    private void onAction(ActionEvent e) {
        if (this.mode.is("Grim")) {
            return;
        }
        this.handleEventAction(e);
    }

    @Subscribe
    private void onMoving(MovingEvent e) {
        if (this.mode.is("Grim")) {
            return;
        }
        this.handleEventMove(e);
    }

    @Subscribe
    private void onPostMove(PostMoveEvent e) {
        if (this.mode.is("Grim")) {
            return;
        }
        this.handleEventPostMove(e);
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        if (this.mode.is("Grim")) {
            return;
        }
        this.handleEventPacket(e);
    }

    @Subscribe
    private void onDamage(EventDamageReceive e) {
        if (this.mode.is("Grim")) {
            return;
        }
        this.handleDamageEvent(e);
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (((Boolean)this.moveDir.get()).booleanValue() && !this.check()) {
            Strafe.mc.player.renderYawOffset = Strafe.mc.player.rotationYawHead = MoveUtils.moveYaw(Strafe.mc.player.rotationYaw);
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (((Boolean)this.autoJump.get()).booleanValue() && Strafe.mc.player.isOnGround() && !Strafe.mc.player.isInWater() && !Strafe.mc.player.isInLava()) {
            Strafe.mc.player.jump();
        }
        if (!((Boolean)this.elytra.get()).booleanValue()) {
            return;
        }
        InventoryUtil.getInstance();
        int elytra = InventoryUtil.getHotbarSlotOfItem();
        if (Strafe.mc.player.isInWater() || Strafe.mc.player.isInLava() || waterTicks > 0 || elytra == -1) {
            return;
        }
        if (Strafe.mc.player.fallDistance != 0.0f && (double)Strafe.mc.player.fallDistance < 0.1 && Strafe.mc.player.motion.y < -0.1) {
            if (elytra != -2) {
                Strafe.mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, Strafe.mc.player);
                Strafe.mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, Strafe.mc.player);
            }
            mc.getConnection().sendPacket(new CEntityActionPacket(Strafe.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            mc.getConnection().sendPacket(new CEntityActionPacket(Strafe.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            if (elytra != -2) {
                Strafe.mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, Strafe.mc.player);
                Strafe.mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, Strafe.mc.player);
            }
        }
    }

    private void handleDamageEvent(EventDamageReceive damage) {
        if (((Boolean)this.damageBoost.get()).booleanValue()) {
            this.damageUtil.processDamage(damage);
        }
    }

    private void handleEventAction(ActionEvent action) {
        if (this.mode.is("Matrix Hard")) {
            if (this.strafes()) {
                this.handleStrafesEventAction(action);
            }
            if (this.strafeMovement.isNeedSwap()) {
                this.handleNeedSwapEventAction(action);
            }
        }
    }

    private void handleEventMove(MovingEvent eventMove) {
        InventoryUtil.getInstance();
        int elytraSlot = InventoryUtil.getHotbarSlotOfItem();
        if (((Boolean)this.elytra.get()).booleanValue() && elytraSlot != -1 && MoveUtils.isMoving() && !Strafe.mc.player.isOnGround() && (double)Strafe.mc.player.fallDistance >= 0.15 && eventMove.isToGround()) {
            MoveUtils.setMotion(((Float)this.setSpeed.get()).floatValue());
            this.strafeMovement.setOldSpeed((double)((Float)this.setSpeed.get()).floatValue() / 1.06);
        }
        waterTicks = Strafe.mc.player.isInWater() || Strafe.mc.player.isInLava() ? 10 : --waterTicks;
        if (this.mode.is("Matrix Hard")) {
            if (((Boolean)this.onlyGround.get()).booleanValue() && !Strafe.mc.player.isOnGround()) {
                return;
            }
            if (this.strafes()) {
                this.handleStrafesEventMove(eventMove);
            } else {
                this.strafeMovement.setOldSpeed(0.0);
            }
        }
        if (this.mode.is("Matrix")) {
            if (waterTicks > 0) {
                return;
            }
            if (MoveUtils.isMoving() && MoveUtils.getMotion() <= 0.289385188 && !eventMove.isToGround()) {
                MoveUtils.setStrafe(MoveUtils.reason(false) || Strafe.mc.player.isHandActive() ? MoveUtils.getMotion() - (double)1.0E-5f : (double)(0.245f - new Random().nextFloat() * 1.0E-6f));
            }
        }
    }

    private void handleEventPostMove(PostMoveEvent eventPostMove) {
        this.strafeMovement.postMove(eventPostMove.getHorizontalMove());
    }

    private void handleEventPacket(EventPacket packet) {
        if (packet.getType() == EventPacket.Type.RECEIVE) {
            if (((Boolean)this.damageBoost.get()).booleanValue()) {
                this.damageUtil.onPacketEvent(packet);
            }
            this.handleReceivePacketEventPacket(packet);
        }
    }

    private void handleStrafesEventAction(ActionEvent action) {
        if (CEntityActionPacket.lastUpdatedSprint != this.strafeMovement.isNeedSprintState()) {
            action.setSprintState(!CEntityActionPacket.lastUpdatedSprint);
        }
    }

    private void handleStrafesEventMove(MovingEvent eventMove) {
        if (this.targetStrafe.isState() && this.hitAura.isState() && this.hitAura.getTarget() != null) {
            return;
        }
        if (((Boolean)this.damageBoost.get()).booleanValue()) {
            this.damageUtil.time(700L);
        }
        float damageSpeed = ((Float)this.boostSpeed.get()).floatValue() / 10.0f;
        double speed = this.strafeMovement.calculateSpeed(eventMove, (Boolean)this.damageBoost.get(), this.damageUtil.isNormalDamage(), false, damageSpeed);
        MoveUtils.MoveEvent.setMoveMotion(eventMove, speed);
    }

    private void handleNeedSwapEventAction(ActionEvent action) {
        action.setSprintState(!Strafe.mc.player.serverSprintState);
        this.strafeMovement.setNeedSwap(false);
    }

    private void handleReceivePacketEventPacket(EventPacket packet) {
        if (packet.getPacket() instanceof SPlayerPositionLookPacket) {
            this.strafeMovement.setOldSpeed(0.0);
        }
    }

    public boolean strafes() {
        BlockPos belowPosition;
        if (this.isInvalidPlayerState()) {
            return false;
        }
        if (Strafe.mc.player.isInWater() || waterTicks > 0) {
            return false;
        }
        BlockPos playerPosition = new BlockPos(Strafe.mc.player.getPositionVec());
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
        return Strafe.mc.player == null || Strafe.mc.world == null || Strafe.mc.player.isSneaking() || Strafe.mc.player.isElytraFlying() || Strafe.mc.player.isInWater() || Strafe.mc.player.isInLava();
    }

    private boolean isSurfaceLiquid(BlockPos abovePosition, BlockPos belowPosition) {
        Block aboveBlock = Strafe.mc.world.getBlockState(abovePosition).getBlock();
        Block belowBlock = Strafe.mc.world.getBlockState(belowPosition).getBlock();
        return aboveBlock instanceof AirBlock && belowBlock == Blocks.WATER;
    }

    private boolean isPlayerInWebOrSoulSand(BlockPos playerPosition) {
        Material playerMaterial = Strafe.mc.world.getBlockState(playerPosition).getMaterial();
        Block oneBelowBlock = Strafe.mc.world.getBlockState(playerPosition.down()).getBlock();
        return playerMaterial == Material.WEB || oneBelowBlock instanceof SoulSandBlock;
    }

    private boolean isPlayerAbleToStrafe() {
        return !Strafe.mc.player.abilities.isFlying && !Strafe.mc.player.isPotionActive(Effects.LEVITATION);
    }

    @Override
    public void onEnable() {
        this.strafeMovement.setOldSpeed(0.0);
        super.onEnable();
    }
}

