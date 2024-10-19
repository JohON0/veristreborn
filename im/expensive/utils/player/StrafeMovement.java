/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import im.expensive.events.MovingEvent;
import im.expensive.utils.client.IMinecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;

public class StrafeMovement
implements IMinecraft {
    private double oldSpeed;
    private double contextFriction;
    private boolean needSwap;
    private boolean needSprintState;
    private int counter;
    private int noSlowTicks;

    public double calculateSpeed(MovingEvent move, boolean damageBoost, boolean hasTime, boolean autoJump, float damageSpeed) {
        float n8;
        float n6;
        boolean fromGround = StrafeMovement.mc.player.isOnGround();
        boolean toGround = move.isToGround();
        boolean jump = move.getMotion().y > 0.0;
        float speedAttributes = this.getAIMoveSpeed(StrafeMovement.mc.player);
        float frictionFactor = this.getFrictionFactor(StrafeMovement.mc.player, move);
        float f = n6 = StrafeMovement.mc.player.isPotionActive(Effects.JUMP_BOOST) && StrafeMovement.mc.player.isHandActive() ? 0.88f : 0.91f;
        if (fromGround) {
            n6 = frictionFactor;
        }
        float n7 = 0.16277136f / (n6 * n6 * n6);
        if (fromGround) {
            n8 = speedAttributes * n7;
            if (jump) {
                n8 += 0.2f;
            }
        } else {
            n8 = damageBoost && hasTime && (autoJump || StrafeMovement.mc.gameSettings.keyBindJump.isKeyDown()) ? damageSpeed : 0.0255f;
        }
        boolean noslow = false;
        double max2 = this.oldSpeed + (double)n8;
        double max = 0.0;
        if (StrafeMovement.mc.player.isHandActive() && !jump) {
            double d;
            double n10 = this.oldSpeed + (double)n8 * 0.25;
            double motionY2 = move.getMotion().y;
            if (motionY2 != 0.0 && Math.abs(motionY2) < 0.08) {
                n10 += 0.055;
            }
            max = Math.max(0.043, n10);
            if (max2 > d) {
                noslow = true;
                ++this.noSlowTicks;
            } else {
                this.noSlowTicks = Math.max(this.noSlowTicks - 1, 0);
            }
        } else {
            this.noSlowTicks = 0;
        }
        max2 = this.noSlowTicks > 3 ? max - (StrafeMovement.mc.player.isPotionActive(Effects.JUMP_BOOST) && StrafeMovement.mc.player.isHandActive() ? 0.3 : 0.019) : Math.max(noslow ? 0.0 : 0.25, max2) - (this.counter++ % 2 == 0 ? 0.001 : 0.002);
        this.contextFriction = n6;
        if (!toGround && !fromGround) {
            this.needSwap = true;
        }
        if (!fromGround && !toGround) {
            boolean bl = this.needSprintState = !StrafeMovement.mc.player.serverSprintState;
        }
        if (toGround && fromGround) {
            this.needSprintState = false;
        }
        return max2;
    }

    public void postMove(double horizontal) {
        this.oldSpeed = horizontal * this.contextFriction;
    }

    private float getAIMoveSpeed(ClientPlayerEntity contextPlayer) {
        boolean prevSprinting = contextPlayer.isSprinting();
        contextPlayer.setSprinting(false);
        float speed = contextPlayer.getAIMoveSpeed() * 1.3f;
        contextPlayer.setSprinting(prevSprinting);
        return speed;
    }

    private float getFrictionFactor(ClientPlayerEntity contextPlayer, MovingEvent move) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        blockpos$mutable.setPos(move.getFrom().x, move.getAabbFrom().minY - 1.0, move.getFrom().z);
        return contextPlayer.world.getBlockState((BlockPos)blockpos$mutable).getBlock().slipperiness * 0.91f;
    }

    public double getOldSpeed() {
        return this.oldSpeed;
    }

    public double getContextFriction() {
        return this.contextFriction;
    }

    public boolean isNeedSwap() {
        return this.needSwap;
    }

    public boolean isNeedSprintState() {
        return this.needSprintState;
    }

    public int getCounter() {
        return this.counter;
    }

    public int getNoSlowTicks() {
        return this.noSlowTicks;
    }

    public void setOldSpeed(double oldSpeed) {
        this.oldSpeed = oldSpeed;
    }

    public void setContextFriction(double contextFriction) {
        this.contextFriction = contextFriction;
    }

    public void setNeedSwap(boolean needSwap) {
        this.needSwap = needSwap;
    }

    public void setNeedSprintState(boolean needSprintState) {
        this.needSprintState = needSprintState;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setNoSlowTicks(int noSlowTicks) {
        this.noSlowTicks = noSlowTicks;
    }
}

