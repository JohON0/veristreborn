/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.events.MovingEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.movement.Strafe;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;

@ModuleRegister(name="Jesus", category=Category.Movement)
public class Jesus
extends Module {
    private ModeSetting mode = new ModeSetting("\u041c\u043e\u0434", "Matrix Solid", "Matrix Solid", "Matrix Zoom", "AAC", "NCP", "NCP New");
    private SliderSetting speed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 10.0f, 0.1f, 10.0f, 0.1f);
    private BooleanSetting noJump = new BooleanSetting("\u041d\u0435 \u043f\u0440\u044b\u0433\u0430\u0442\u044c", false).setVisible(() -> this.mode.is("Matrix Solid"));
    private BooleanSetting bypassboolean = new BooleanSetting("\u041d\u043e\u0432\u044b\u0439 Matrix", true).setVisible(() -> this.mode.is("Matrix Zoom"));
    private int ticks;
    public static boolean jesusTick;
    public static boolean swap;

    public Jesus() {
        this.addSettings(this.mode, this.speed, this.noJump, this.bypassboolean);
    }

    @Subscribe
    private void onUpdate(EventMotion motion) {
        double x = Jesus.mc.player.getPosX();
        double y = Jesus.mc.player.getPosY();
        double z = Jesus.mc.player.getPosZ();
        if (this.mode.is("Matrix Solid")) {
            this.updateMoveInWater();
            this.updateAirMove(motion);
        }
        if (this.mode.is("NCP New") && Jesus.mc.world.getBlockState(new BlockPos(Jesus.mc.player.getPosX(), Jesus.mc.player.getPosY() - (Jesus.mc.player.isJumping ? 0.01 : 0.45), Jesus.mc.player.getPosZ())).getBlock() == Blocks.WATER && !Jesus.mc.player.isInWater()) {
            motion.setOnGround(false);
            if (Jesus.mc.world.getBlockState(new BlockPos(Jesus.mc.player.getPosX(), Jesus.mc.player.getPosY() - 0.001, Jesus.mc.player.getPosZ())).getBlock() == Blocks.WATER && Jesus.mc.player.isJumping) {
                Jesus.mc.player.motion.y = 0.42f;
            }
            Jesus.mc.player.setOnGround(false);
            Jesus.mc.player.motion.x = 0.0;
            Jesus.mc.player.motion.z = 0.0;
            if (!Jesus.mc.player.isPotionActive(Effects.SPEED)) {
                Jesus.mc.player.jumpMovementFactor = 0.2865f;
            }
            if (Jesus.mc.player.isPotionActive(Effects.SPEED)) {
                Jesus.mc.player.jumpMovementFactor = 0.4005f;
            }
        }
        if (this.mode.is("NCP")) {
            if (Jesus.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.WATER) {
                Jesus.mc.player.motion.y = 0.0391;
                Jesus.mc.player.setOnGround(false);
                if (!Jesus.mc.player.isPotionActive(Effects.SPEED)) {
                    float f = Jesus.mc.player.jumpMovementFactor = Jesus.mc.player.isMoving() ? 0.2865f : 0.294f;
                }
                if (Jesus.mc.player.isPotionActive(Effects.SPEED)) {
                    Jesus.mc.player.jumpMovementFactor = 0.41f;
                }
                if (Jesus.mc.player.collidedHorizontally && Jesus.mc.gameSettings.keyBindForward.isKeyDown() && !Jesus.mc.player.isInWater() && !Jesus.mc.player.isInLava() && Jesus.mc.gameSettings.keyBindJump.isKeyDown()) {
                    Jesus.mc.player.jump();
                }
            }
            if (Jesus.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.LAVA) {
                Jesus.mc.player.motion.y = 0.04;
                Jesus.mc.player.setOnGround(false);
                if (!Jesus.mc.player.isPotionActive(Effects.SPEED)) {
                    Jesus.mc.player.jumpMovementFactor = 0.2865f;
                }
                if (Jesus.mc.player.isPotionActive(Effects.SPEED)) {
                    Jesus.mc.player.jumpMovementFactor = 0.4005f;
                }
                if (Jesus.mc.player.collidedHorizontally && Jesus.mc.gameSettings.keyBindForward.isKeyDown() && !Jesus.mc.player.isInWater() && !Jesus.mc.player.isInLava() && Jesus.mc.gameSettings.keyBindJump.isKeyDown()) {
                    Jesus.mc.player.jump();
                }
            }
            if (Jesus.mc.player.isInWater() || Jesus.mc.player.isInLava()) {
                Jesus.mc.player.motion.x = 0.0;
                Jesus.mc.player.motion.z = 0.0;
                if (!Jesus.mc.gameSettings.keyBindJump.isKeyDown()) {
                    Jesus.mc.player.motion.y += 0.07;
                }
            }
        }
        if (this.mode.is("AAC") && Jesus.mc.world.getBlockState(new BlockPos(Jesus.mc.player.getPosX(), Jesus.mc.player.getPosY() - (Jesus.mc.player.isJumping ? 0.01 : 0.45), Jesus.mc.player.getPosZ())).getBlock() == Blocks.WATER && !Jesus.mc.player.isInWater()) {
            motion.setOnGround(false);
            if (Jesus.mc.player.isOnGround() && Jesus.mc.player.ticksExisted % 2 == 0) {
                Jesus.mc.player.isAirBorne = motion.isOnGround();
            }
        }
        if (this.mode.is("Matrix Zoom")) {
            if (swap) {
                if (((Boolean)this.bypassboolean.get()).booleanValue()) {
                    motion.setY(motion.getY() + (this.ticks % 3 == 0 ? -0.02 : (this.ticks % 3 == 1 ? 0.02 : 0.03)));
                } else {
                    motion.setY(motion.getY() + (this.ticks % 2 == 0 ? -0.02 : 0.02));
                }
                ++this.ticks;
                if (motion.getY() % 1.0 == 0.0) {
                    motion.setY(motion.getY() - 0.02);
                }
                motion.setOnGround(false);
            }
            swap = false;
        }
    }

    @Subscribe
    private void onMove(MovingEvent move) {
        if (this.mode.is("Matrix Zoom")) {
            BlockPos pos = new BlockPos(move.getFrom());
            Block block = Jesus.mc.world.getBlockState(pos).getBlock();
            if (block instanceof FlowingFluidBlock) {
                Strafe.waterTicks = 10;
                move.motion.z = 0.0;
                move.motion.x = 0.0;
            } else if (Jesus.mc.world.getBlockState(new BlockPos(move.to())).getBlock() instanceof FlowingFluidBlock) {
                Strafe.waterTicks = 10;
                boolean bypass = false;
                try {
                    bypass = (Boolean)this.bypassboolean.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                float f = this.ticks % (bypass ? 3 : 2) == 0 ? ((Float)this.speed.get()).floatValue() - 0.01f : 0.14f;
                MoveUtils.setSpeed(f);
                if (Jesus.mc.player.getPosY() % 1.0 == 0.0) {
                    move.motion.y = 0.0;
                }
                jesusTick = true;
                swap = true;
                move.motion.x = Jesus.mc.player.motion.x;
                move.motion.z = Jesus.mc.player.motion.z;
                move.collisionOffset().y = -0.7;
                Jesus.mc.player.motion.y = 0.0;
                Jesus.mc.player.motion.x = 0.0;
                Jesus.mc.player.motion.z = 0.0;
            }
        }
    }

    private void updateMoveInWater() {
        BlockPos playerPos = new BlockPos(Jesus.mc.player.getPosX(), Jesus.mc.player.getPosY() + 0.008, Jesus.mc.player.getPosZ());
        Block playerBlock = Jesus.mc.world.getBlockState(playerPos).getBlock();
        if (playerBlock == Blocks.WATER && !Jesus.mc.player.isOnGround()) {
            boolean isUp = Jesus.mc.world.getBlockState(new BlockPos(Jesus.mc.player.getPosX(), Jesus.mc.player.getPosY() + 0.03, Jesus.mc.player.getPosZ())).getBlock() == Blocks.WATER;
            Jesus.mc.player.jumpMovementFactor = 0.0f;
            float yPort = MoveUtils.getMotion() > 0.1 ? 0.02f : 0.032f;
            Jesus.mc.player.setVelocity(Jesus.mc.player.motion.x *= (double)((Float)this.speed.get()).floatValue(), (double)Jesus.mc.player.fallDistance < 3.5 ? (double)(isUp ? yPort : -yPort) : -0.1, Jesus.mc.player.motion.z *= (double)((Float)this.speed.get()).floatValue());
        }
    }

    private void updateAirMove(EventMotion motion) {
        double posY = Jesus.mc.player.getPosY();
        if (posY > (double)((int)posY) + 0.89 && posY <= (double)((int)posY + 1) || (double)Jesus.mc.player.fallDistance > 3.5) {
            BlockPos waterBlockPos;
            Block waterBlock;
            Jesus.mc.player.getPositionVec().y = (double)((int)posY + 1) + 1.0E-45;
            if (!Jesus.mc.player.isInWater() && (waterBlock = Jesus.mc.world.getBlockState(waterBlockPos = new BlockPos(Jesus.mc.player.getPosX(), Jesus.mc.player.getPosY() - 0.1, Jesus.mc.player.getPosZ())).getBlock()) == Blocks.WATER) {
                this.moveInWater(motion);
            }
        }
    }

    private void moveInWater(EventMotion motion) {
        motion.setOnGround(true);
        this.collisionJump();
        if (this.ticks == 1) {
            MoveUtils.setMotion(1.1f);
            this.ticks = 0;
        } else {
            this.ticks = 1;
        }
    }

    private void collisionJump() {
        if (Jesus.mc.player.collidedHorizontally && (!((Boolean)this.noJump.get()).booleanValue() || Jesus.mc.gameSettings.keyBindJump.pressed)) {
            Jesus.mc.player.motion.y = 0.2;
            Jesus.mc.player.motion.x = 0.0;
            Jesus.mc.player.motion.z = 0.0;
        }
    }
}

