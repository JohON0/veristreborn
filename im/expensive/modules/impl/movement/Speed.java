/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.events.MovingEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MoveUtils;
import im.expensive.utils.player.StrafeMovement;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="Speed", category=Category.Movement)
public class Speed
extends Module {
    private ModeSetting mode = new ModeSetting("\u041e\u0431\u0445\u043e\u0434", "Matrix", "Vanilla", "Matrix", "Grim", "NCP", "Timer", "Vulcan", "Funtime", "AAC", "RAC");
    private BooleanSetting autoJump = new BooleanSetting("Auto Jump", false).setVisible(() -> this.mode.is("Matrix") || this.mode.is("NCP") || this.mode.is("Vanilla"));
    private BooleanSetting spoofJump = new BooleanSetting("Spoof", false).setVisible(() -> this.mode.is("NCP") && (Boolean)this.autoJump.get() != false);
    private BooleanSetting longjump_aac = new BooleanSetting("LongJump", false).setVisible(() -> this.mode.is("AAC"));
    private BooleanSetting onground_aac = new BooleanSetting("OnGround", false).setVisible(() -> this.mode.is("AAC"));
    private BooleanSetting motionboost_matrix = new BooleanSetting("Motion", true).setVisible(() -> this.mode.is("Matrix"));
    private BooleanSetting airboost_matrix = new BooleanSetting("AirBoost", false).setVisible(() -> this.mode.is("Matrix"));
    private BooleanSetting timerboost_matrix = new BooleanSetting("Timer", false).setVisible(() -> this.mode.is("Matrix"));
    private BooleanSetting strafeMove = new BooleanSetting("Strafe", false).setVisible(() -> this.mode.is("Matrix") && (Boolean)this.motionboost_matrix.get() != false);
    private BooleanSetting entityboost_grim = new BooleanSetting("EntityBoost", true).setVisible(() -> this.mode.is("Grim"));
    private BooleanSetting blockboost_grim = new BooleanSetting("BlockBoost", true).setVisible(() -> this.mode.is("Grim"));
    private BooleanSetting timerboost_grim = new BooleanSetting("Timer", false).setVisible(() -> this.mode.is("Grim"));
    private BooleanSetting svboost_grim = new BooleanSetting("Second bypass", false).setVisible(() -> this.mode.is("Grim") && (Boolean)this.entityboost_grim.get() != false);
    private SliderSetting speed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> this.mode.is("Vanilla"));
    private StrafeMovement strafe = new StrafeMovement();
    private boolean enabled = false;
    public static int stage;
    public double less;
    public double stair;
    public double moveSpeed;
    public boolean slowDownHop;
    public boolean wasJumping;
    public boolean boosting;
    public boolean restart;
    private int prevSlot = -1;
    public StopWatch stopWatch = new StopWatch();
    public StopWatch racTimer = new StopWatch();

    public Speed() {
        this.addSettings(this.mode, this.speed, this.autoJump, this.spoofJump, this.blockboost_grim, this.entityboost_grim, this.svboost_grim, this.timerboost_grim, this.motionboost_matrix, this.strafeMove, this.airboost_matrix, this.timerboost_matrix, this.longjump_aac, this.onground_aac);
    }

    @Override
    public void onDisable() {
        Speed.mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (this.mode.is("Grim") && ((Boolean)this.timerboost_grim.get()).booleanValue()) {
            IPacket<IServerPlayNetHandler> p;
            IPacket<?> iPacket = e.getPacket();
            if (iPacket instanceof CConfirmTransactionPacket) {
                p = (CConfirmTransactionPacket)iPacket;
                e.cancel();
            }
            if ((iPacket = e.getPacket()) instanceof SPlayerPositionLookPacket) {
                p = (IPacket<IServerPlayNetHandler>) iPacket;
                this.toggle();
            }
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        switch ((String)this.mode.get()) {
            case "Matrix": {
                if (!Speed.mc.player.isOnGround() || !((Boolean)this.autoJump.get()).booleanValue() || Speed.mc.player.isInLava() || Speed.mc.player.isInWater() || ((Boolean)this.airboost_matrix.get()).booleanValue()) break;
                Speed.mc.player.jump();
                break;
            }
            case "Vanilla": {
                MoveUtils.setMotion(((Float)this.speed.get()).floatValue());
                if (!((Boolean)this.autoJump.get()).booleanValue() || !Speed.mc.player.isOnGround() || Speed.mc.player.isInWater() || Speed.mc.player.isInLava()) break;
                Speed.mc.player.jump();
                break;
            }
            case "RAC": {
                if (!this.racTimer.isReached(10L)) break;
                if (Speed.mc.player.isOnGround() && !Speed.mc.player.isJumping) {
                    MoveUtils.setSpeed((float)MathHelper.clamp(MoveUtils.getSpeed() * (Speed.mc.player.rayGround ? 1.8 : 0.8), 0.2, MoveUtils.w() && Speed.mc.player.isSprinting() ? (double)1.7155f : (double)1.745f));
                    Speed.mc.player.rayGround = Speed.mc.player.isOnGround();
                } else {
                    Speed.mc.player.serverSprintState = true;
                    MoveUtils.setSpeed((float)MathHelper.clamp(MoveUtils.getSpeed() * (!Speed.mc.player.isOnGround() && !Speed.mc.player.rayGround ? 1.2 : 1.0), 0.195, 1.823585033416748), 0.12f);
                    Speed.mc.player.rayGround = Speed.mc.player.isOnGround();
                }
                this.racTimer.reset();
                break;
            }
            case "Funtime": {
                boolean canBoost;
                AxisAlignedBB aabb = Speed.mc.player.getBoundingBox().grow(0.1);
                int armorstans = Speed.mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
                boolean bl = canBoost = armorstans > 1 || Speed.mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 1;
                if (!canBoost || Speed.mc.player.isOnGround()) break;
                Speed.mc.player.jumpMovementFactor = armorstans > 1 ? 1.0f / (float)armorstans : 0.16f;
                break;
            }
            case "Grim": {
                if (((Boolean)this.timerboost_grim.get()).booleanValue()) {
                    if (this.stopWatch.isReached(1150L)) {
                        this.boosting = true;
                    }
                    if (this.stopWatch.isReached(7000L)) {
                        this.boosting = false;
                        this.stopWatch.reset();
                    }
                    if (this.boosting) {
                        if (Speed.mc.player.isOnGround() && !Speed.mc.gameSettings.keyBindJump.pressed) {
                            Speed.mc.player.jump();
                        }
                        Speed.mc.timer.timerSpeed = Speed.mc.player.ticksExisted % 2 == 0 ? 1.5f : 1.2f;
                    } else {
                        Speed.mc.timer.timerSpeed = 0.05f;
                    }
                }
                if (!((Boolean)this.blockboost_grim.get()).booleanValue()) break;
                int block = InventoryUtil.findBlockInHotbar();
                if (block == -1 || Speed.mc.player.isInWater()) {
                    return;
                }
                if (Speed.mc.player.isOnGround()) {
                    if (!this.wasJumping) {
                        this.wasJumping = true;
                        this.placeBlock();
                        Speed.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Speed.mc.player.getPosX(), Speed.mc.player.getPosY(), Speed.mc.player.getPosZ(), Speed.mc.player.rotationYaw, 90.0f, Speed.mc.player.isOnGround()));
                    }
                } else {
                    this.wasJumping = false;
                    Speed.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Speed.mc.player.getPosX(), Speed.mc.player.getPosY(), Speed.mc.player.getPosZ(), Speed.mc.player.rotationYaw, 90.0f, Speed.mc.player.isOnGround()));
                }
                if (!Speed.mc.player.isOnGround()) break;
                Speed.mc.player.jump();
                break;
            }
            case "Timer": {
                float timerValue = 0;
                float f = Speed.mc.player.fallDistance <= 0.25f ? 2.2f : (timerValue = (double)Speed.mc.player.fallDistance != Math.ceil(Speed.mc.player.fallDistance) ? 0.4f : 1.0f);
                if (MoveUtils.isMoving()) {
                    Speed.mc.timer.timerSpeed = timerValue;
                    if (!Speed.mc.player.isOnGround()) break;
                    Speed.mc.player.jump();
                    break;
                }
                Speed.mc.timer.timerSpeed = 1.0f;
                break;
            }
            case "Vulcan": {
                Speed.mc.player.jumpMovementFactor = 0.025f;
                if (!Speed.mc.player.isOnGround() || !MoveUtils.isMoving()) break;
                if (Speed.mc.player.collidedHorizontally && Speed.mc.gameSettings.keyBindJump.pressed) {
                    if (!Speed.mc.gameSettings.keyBindJump.pressed) {
                        Speed.mc.player.jump();
                    }
                    return;
                }
                Speed.mc.player.jump();
                Speed.mc.player.motion.y = 0.1f;
                break;
            }
            case "AAC": {
                boolean longHop = (Boolean)this.longjump_aac.get() != false && (Speed.mc.player.isJumping || Speed.mc.player.fallDistance != 0.0f);
                boolean onGround = (Boolean)this.onground_aac.get() != false && !Speed.mc.player.isJumping && Speed.mc.player.isOnGround() && Speed.mc.player.collidedVertically && MoveUtils.getSpeed() < 0.9;
                Speed.mc.timer.timerSpeed = 1.2f;
                if (longHop) {
                    Speed.mc.player.jumpMovementFactor = 0.17f;
                    Speed.mc.player.multiplyMotionXZ(1.005f);
                }
                if (!onGround) break;
                Speed.mc.player.multiplyMotionXZ(1.212f);
            }
        }
    }

    @Subscribe
    public void onMotion(EventMotion move) {
        if (this.mode.is("NCP")) {
            if (!((Boolean)this.autoJump.get()).booleanValue() && !Speed.mc.gameSettings.keyBindJump.isKeyDown()) {
                return;
            }
            Speed.mc.player.jumpMovementFactor = (float)((double)Speed.mc.player.jumpMovementFactor * 1.04);
            boolean collided = Speed.mc.player.collidedHorizontally;
            if (collided) {
                stage = -1;
            }
            if (this.stair > 0.0) {
                this.stair -= 0.3;
            }
            this.less -= this.less > 1.0 ? 0.24 : 0.17;
            if (this.less < 0.0) {
                this.less = 0.0;
            }
            if (!Speed.mc.player.isInWater() && Speed.mc.player.isOnGround()) {
                collided = Speed.mc.player.collidedHorizontally;
                if (stage >= 0 || collided) {
                    stage = 0;
                    float motY = 0.42f;
                    if (((Boolean)this.spoofJump.get()).booleanValue()) {
                        Speed.mc.player.motion.y = motY;
                    } else {
                        Speed.mc.player.jump();
                    }
                    this.less += 1.0;
                    boolean bl = this.slowDownHop = this.less > 1.0 && !this.slowDownHop;
                    if (this.less > 1.15) {
                        this.less = 1.15;
                    }
                }
            }
            this.moveSpeed = this.getCurrentSpeed(stage) + 0.0335;
            this.moveSpeed *= 0.85;
            if (this.stair > 0.0) {
                this.moveSpeed *= 1.0;
            }
            if (this.slowDownHop) {
                this.moveSpeed *= 0.8575;
            }
            if (Speed.mc.player.isInWater()) {
                this.moveSpeed = 0.351;
            }
            if (MoveUtils.isMoving()) {
                MoveUtils.setSpeed((float)this.moveSpeed);
            }
            ++stage;
        }
        if (this.mode.is("Matrix") && ((Boolean)this.timerboost_matrix.get()).booleanValue()) {
            if (Speed.mc.player.isOnGround()) {
                Speed.mc.timer.timerSpeed = 1.1f;
            }
            if ((double)Speed.mc.player.fallDistance > 0.1 && Speed.mc.player.fallDistance < 1.0f) {
                Speed.mc.timer.timerSpeed = 1.0f + (1.0f - (float)Math.floorMod(2L, 2L));
            }
            if (Speed.mc.player.fallDistance >= 1.0f) {
                Speed.mc.timer.timerSpeed = 0.978f;
            }
        }
        if (this.mode.is("Matrix") && ((Boolean)this.airboost_matrix.get()).booleanValue()) {
            if (Speed.mc.player.isOnGround()) {
                this.enabled = true;
            } else if (Speed.mc.player.fallDistance > 0.0f) {
                this.enabled = false;
            }
            if (!Speed.mc.world.getCollisionShapes(Speed.mc.player, Speed.mc.player.getBoundingBox().expand(0.5, 0.0, 0.5).offset(0.0, -1.0, 0.0)).toList().isEmpty() && Speed.mc.player.ticksExisted % 2 == 0) {
                if (!((Boolean)this.motionboost_matrix.get()).booleanValue() && !((Boolean)this.autoJump.get()).booleanValue()) {
                    Speed.mc.player.fallDistance = 0.0f;
                    move.setOnGround(true);
                    Speed.mc.player.setOnGround(true);
                }
                if (this.enabled && !Speed.mc.player.movementInput.jump && ((Boolean)this.autoJump.get()).booleanValue()) {
                    Speed.mc.player.jump();
                }
                Speed.mc.player.jumpMovementFactor = 0.026523f;
            }
        }
    }

    @Subscribe
    public void onMove(MovingEvent e) {
        block7: {
            if (this.mode.is("Matrix") && ((Boolean)this.motionboost_matrix.get()).booleanValue() && !Speed.mc.player.isOnGround() && Speed.mc.player.fallDistance >= 0.5f && e.toGround) {
                double speed = 2.0;
                if (((Boolean)this.strafeMove.get()).booleanValue()) {
                    double[] newSpeed = MoveUtils.getSpeed((Math.hypot(Speed.mc.player.motion.x, Speed.mc.player.motion.z) - 1.0E-4) * speed);
                    e.motion.x = newSpeed[0];
                    e.motion.z = newSpeed[1];
                    Speed.mc.player.motion.x = e.motion.x;
                    Speed.mc.player.motion.z = e.motion.z;
                    return;
                }
                Speed.mc.player.motion.x *= speed;
                Speed.mc.player.motion.z *= speed;
                this.strafe.setOldSpeed(speed);
            }
            if (!this.mode.is("Grim") || !((Boolean)this.entityboost_grim.get()).booleanValue()) break block7;
            if (!((Boolean)this.svboost_grim.get()).booleanValue()) {
                for (AbstractClientPlayerEntity player : Speed.mc.world.getPlayers()) {
                    if (player == Speed.mc.player || !((double)Speed.mc.player.getDistance(player) <= 2.25)) continue;
                    float p = Speed.mc.world.getBlockState(new BlockPos(0, 0, 0)).getBlockId();
                    float f = Speed.mc.player.isOnGround() ? p * 0.91f : 0.91f;
                    float f2 = 0.99f;
                    Speed.mc.player.setVelocity(Speed.mc.player.getMotion().getX() / (double)f * (double)f2, Speed.mc.player.getMotion().getY(), Speed.mc.player.getMotion().getZ() / (double)f * (double)f2);
                }
            } else {
                for (Entity ent : Speed.mc.world.getAllEntities()) {
                    int collisions = 0;
                    if (ent != Speed.mc.player && (ent instanceof LivingEntity || ent instanceof BoatEntity) && Speed.mc.player.getBoundingBox().expand(0.0, 1.0, 0.0).intersects(ent.getBoundingBox())) {
                        ++collisions;
                    }
                    double[] motion = MoveUtils.forward(0.08 * (double)collisions);
                    Speed.mc.player.addVelocity(motion[0], 0.0, motion[1]);
                }
            }
        }
    }

    public void placeBlock() {
        if (Expensive.getInstance().getModuleManager().getHitAura().isState() && Expensive.getInstance().getModuleManager().getHitAura().getTarget() != null) {
            return;
        }
        BlockPos blockPos = new BlockPos(Speed.mc.player.getPosX(), Speed.mc.player.getPosY() - 0.6, Speed.mc.player.getPosZ());
        if (Speed.mc.world.getBlockState(blockPos).isAir()) {
            return;
        }
        int block = InventoryUtil.findBlockInHotbar();
        if (block == -1) {
            return;
        }
        Speed.mc.player.connection.sendPacket(new CHeldItemChangePacket(block));
        Speed.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
        Vector3d blockCenter = new Vector3d((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5);
        Speed.mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(blockCenter, Direction.UP, blockPos, false)));
        Speed.mc.player.connection.sendPacket(new CHeldItemChangePacket(this.prevSlot));
        Speed.mc.world.setBlockState(blockPos, Blocks.ICE.getDefaultState());
        this.prevSlot = Speed.mc.player.inventory.currentItem;
    }

    public double getCurrentSpeed(int stage) {
        double speed = MoveUtils.getBaseSpeed() + 0.028 * (double)MoveUtils.getSpeedEffect() + (double)MoveUtils.getSpeedEffect() / 15.0;
        double initSpeed = 0.4145 + (double)MoveUtils.getSpeedEffect() / 12.5;
        double decrease = (double)stage / 500.0 * 1.87;
        if (stage == 0) {
            speed = 0.64 + ((double)MoveUtils.getSpeedEffect() + 0.028 * (double)MoveUtils.getSpeedEffect()) * 0.134;
        } else if (stage == 1) {
            speed = initSpeed;
        } else if (stage >= 2) {
            speed = initSpeed - decrease;
        }
        return Math.max(speed, this.slowDownHop ? speed : MoveUtils.getBaseSpeed() + 0.028 * (double)MoveUtils.getSpeedEffect());
    }
}

