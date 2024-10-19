/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import im.expensive.Expensive;
import im.expensive.events.EventInput;
import im.expensive.events.MovingEvent;
import im.expensive.modules.impl.combat.HitAura;
import im.expensive.utils.client.IMinecraft;
import java.util.Objects;
import net.minecraft.block.AirBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.potion.Effects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public final class MoveUtils
implements IMinecraft {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean isMoving() {
        if (MoveUtils.mc.player.movementInput.moveForward != 0.0f) return true;
        MovementInput cfr_ignored_0 = MoveUtils.mc.player.movementInput;
        if (MovementInput.moveStrafe == 0.0f) return false;
        return true;
    }

    public static boolean reason(boolean water) {
        boolean critWater = water && MoveUtils.mc.world.getBlockState(new BlockPos(MoveUtils.mc.player.getPosX(), MoveUtils.mc.player.getPosY(), MoveUtils.mc.player.getPosZ())).getBlock() instanceof FlowingFluidBlock && MoveUtils.mc.world.getBlockState(new BlockPos(MoveUtils.mc.player.getPosX(), MoveUtils.mc.player.getPosY() + 1.0, MoveUtils.mc.player.getPosZ())).getBlock() instanceof AirBlock;
        return MoveUtils.mc.player.isPotionActive(Effects.BLINDNESS) || MoveUtils.mc.player.isOnLadder() || MoveUtils.mc.player.isInWater() && !critWater || MoveUtils.mc.player.abilities.isFlying;
    }

    public static void fixMovement(EventInput event, float yaw) {
        float forward = event.getForward();
        float strafe = event.getStrafe();
        double angle = MathHelper.wrapDegrees(Math.toDegrees(MoveUtils.direction(MoveUtils.mc.player.isElytraFlying() ? yaw : MoveUtils.mc.player.rotationYaw, forward, strafe)));
        if (forward != 0.0f || strafe != 0.0f) {
            float closestForward = 0.0f;
            float closestStrafe = 0.0f;
            float closestDifference = Float.MAX_VALUE;
            for (float predictedForward = -1.0f; predictedForward <= 1.0f; predictedForward += 1.0f) {
                for (float predictedStrafe = -1.0f; predictedStrafe <= 1.0f; predictedStrafe += 1.0f) {
                    double predictedAngle;
                    double difference;
                    if (predictedStrafe == 0.0f && predictedForward == 0.0f || !((difference = Math.abs(angle - (predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(MoveUtils.direction(yaw, predictedForward, predictedStrafe)))))) < (double)closestDifference)) continue;
                    closestDifference = (float)difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
            event.setForward(closestForward);
            event.setStrafe(closestStrafe);
        }
    }

    public static int getSpeedEffect() {
        if (MoveUtils.mc.player.isPotionActive(Effects.SPEED)) {
            return Objects.requireNonNull(MoveUtils.mc.player.getActivePotionEffect(Effects.SPEED)).getAmplifier() + 1;
        }
        return 0;
    }

    public static double getBaseSpeed() {
        double baseSpeed = 0.2873;
        if (MoveUtils.mc.player.isPotionActive(Effects.SPEED)) {
            int amplifier = MoveUtils.mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static double direction(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (moveForward < 0.0) {
            forward = -0.5f;
        } else if (moveForward > 0.0) {
            forward = 0.5f;
        }
        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0f * forward;
        }
        if (moveStrafing < 0.0) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static double getMotion() {
        return Math.hypot(MoveUtils.mc.player.getMotion().x, MoveUtils.mc.player.getMotion().z);
    }

    public static double getSpeed() {
        return Math.sqrt(MoveUtils.mc.player.motion.x * MoveUtils.mc.player.motion.x + MoveUtils.mc.player.motion.z * MoveUtils.mc.player.motion.z);
    }

    public static double[] getSpeed(double speed) {
        HitAura hitAura = Expensive.getInstance().getModuleManager().getHitAura();
        boolean check = hitAura.isState() && hitAura.getTarget() != null && (Boolean)hitAura.getOptions().getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false;
        float yaw = check ? hitAura.rotateVector.x : MoveUtils.mc.player.rotationYaw;
        float forward = MoveUtils.mc.player.movementInput.moveForward;
        MovementInput cfr_ignored_0 = MoveUtils.mc.player.movementInput;
        float strafe = MovementInput.moveStrafe;
        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (strafe < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            strafe = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        return new double[]{(double)forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + (double)strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)), (double)forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - (double)strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)), yaw};
    }

    public static void setMotion(double motion) {
        float yaw;
        double forward = MoveUtils.mc.player.movementInput.moveForward;
        MovementInput cfr_ignored_0 = MoveUtils.mc.player.movementInput;
        double strafe = MovementInput.moveStrafe;
        HitAura hitAura = Expensive.getInstance().getModuleManager().getHitAura();
        boolean check = hitAura.isState() && hitAura.getTarget() != null && (Boolean)hitAura.getOptions().getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false;
        float f = yaw = check ? hitAura.rotateVector.x : MoveUtils.mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            MoveUtils.mc.player.motion.x = 0.0;
            MoveUtils.mc.player.motion.z = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            MoveUtils.mc.player.motion.x = forward * motion * (double)MathHelper.cos(Math.toRadians(yaw + 90.0f)) + strafe * motion * (double)MathHelper.sin(Math.toRadians(yaw + 90.0f));
            MoveUtils.mc.player.motion.z = forward * motion * (double)MathHelper.sin(Math.toRadians(yaw + 90.0f)) - strafe * motion * (double)MathHelper.cos(Math.toRadians(yaw + 90.0f));
        }
    }

    public static void setCuttingSpeed(double speed) {
        boolean check;
        boolean tickTime = MoveUtils.mc.player.ticksExisted % 2 == 0;
        double forward = MoveUtils.mc.player.movementInput.moveForward;
        MovementInput cfr_ignored_0 = MoveUtils.mc.player.movementInput;
        double strafe = MovementInput.moveStrafe;
        float yaw = MoveUtils.mc.player.rotationYaw - (MoveUtils.mc.player.lastReportedYaw - MoveUtils.mc.player.rotationYaw) * 2.0f;
        HitAura hitAura = Expensive.getInstance().getModuleManager().getHitAura();
        boolean bl = check = hitAura.isState() && hitAura.getTarget() != null && (Boolean)hitAura.getOptions().getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false;
        if (check) {
            yaw = hitAura.rotateVector.x;
        }
        if (!(MoveUtils.mc.gameSettings.keyBindForward.isKeyDown() || MoveUtils.mc.gameSettings.keyBindBack.isKeyDown() || MoveUtils.mc.gameSettings.keyBindLeft.isKeyDown() || MoveUtils.mc.gameSettings.keyBindRight.isKeyDown())) {
            MoveUtils.mc.player.motion.x = tickTime ? 1.0E-10 : -1.0E-10;
            MoveUtils.mc.player.motion.z = tickTime ? 1.0E-10 : -1.0E-10;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double cos = Math.cos(Math.toRadians(yaw + 89.5f));
            double sin = Math.sin(Math.toRadians(yaw + 89.5f));
            MoveUtils.mc.player.motion.x = forward * speed * cos + strafe * speed * sin;
            MoveUtils.mc.player.motion.z = forward * speed * sin - strafe * speed * cos;
        }
    }

    public static void setSpeed(float speed) {
        HitAura hitAura = Expensive.getInstance().getModuleManager().getHitAura();
        boolean check = hitAura.isState() && hitAura.getTarget() != null && (Boolean)hitAura.getOptions().getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false;
        float yaw = check ? hitAura.rotateVector.x : MoveUtils.mc.player.rotationYaw;
        float forward = MoveUtils.mc.player.movementInput.moveForward;
        MovementInput cfr_ignored_0 = MoveUtils.mc.player.movementInput;
        float strafe = MovementInput.moveStrafe;
        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (strafe < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            strafe = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        MoveUtils.mc.player.motion.x = (double)(forward * speed) * Math.cos(Math.toRadians(yaw + 90.0f)) + (double)(strafe * speed) * Math.sin(Math.toRadians(yaw + 90.0f));
        MoveUtils.mc.player.motion.z = (double)(forward * speed) * Math.sin(Math.toRadians(yaw + 90.0f)) - (double)(strafe * speed) * Math.cos(Math.toRadians(yaw + 90.0f));
    }

    public static void setSpeed(float speed, float noMoveSpeed) {
        HitAura hitAura = Expensive.getInstance().getModuleManager().getHitAura();
        boolean check = hitAura.isState() && hitAura.getTarget() != null && (Boolean)hitAura.getOptions().getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false;
        float yaw = check ? hitAura.rotateVector.x : MoveUtils.mc.player.rotationYaw;
        float forward = MoveUtils.mc.player.movementInput.moveForward;
        MovementInput cfr_ignored_0 = MoveUtils.mc.player.movementInput;
        float strafe = MovementInput.moveStrafe;
        if (MoveUtils.isMoving()) {
            if (forward != 0.0f) {
                if (strafe > 0.0f) {
                    yaw += (float)(forward > 0.0f ? -45 : 45);
                } else if (strafe < 0.0f) {
                    yaw += (float)(forward > 0.0f ? 45 : -45);
                }
                strafe = 0.0f;
                if (forward > 0.0f) {
                    forward = 1.0f;
                } else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            MoveUtils.mc.player.motion.x = (double)(forward * speed) * Math.cos(Math.toRadians(yaw + 90.0f)) + (double)(strafe * speed) * Math.sin(Math.toRadians(yaw + 90.0f));
            MoveUtils.mc.player.motion.z = (double)(forward * speed) * Math.sin(Math.toRadians(yaw + 90.0f)) - (double)(strafe * speed) * Math.cos(Math.toRadians(yaw + 90.0f));
        } else {
            MoveUtils.mc.player.motion.x *= (double)noMoveSpeed;
            MoveUtils.mc.player.motion.z *= (double)noMoveSpeed;
        }
    }

    public static boolean moveKeysPressed() {
        return MoveUtils.mc.gameSettings.keyBindForward.isKeyDown() || MoveUtils.mc.gameSettings.keyBindBack.isKeyDown() || MoveUtils.mc.gameSettings.keyBindLeft.isKeyDown() || MoveUtils.mc.gameSettings.keyBindRight.isKeyDown();
    }

    public static double getCuttingSpeed() {
        return Math.sqrt(MoveUtils.mc.player.getMotion().x * MoveUtils.mc.player.getMotion().x + MoveUtils.mc.player.getMotion().z * MoveUtils.mc.player.getMotion().z);
    }

    public static double[] forward(double d) {
        float f3;
        float f = MoveUtils.mc.player.movementInput.moveForward;
        MovementInput cfr_ignored_0 = MoveUtils.mc.player.movementInput;
        float f2 = MovementInput.moveStrafe;
        HitAura hitAura = Expensive.getInstance().getModuleManager().getHitAura();
        boolean check = hitAura.isState() && hitAura.getTarget() != null && (Boolean)hitAura.getOptions().getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false;
        float f4 = f3 = check ? hitAura.rotateVector.x : MoveUtils.mc.player.rotationYaw;
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += (float)(f > 0.0f ? -45 : 45);
            } else if (f2 < 0.0f) {
                f3 += (float)(f > 0.0f ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        double d4 = (double)f * d * d3 + (double)f2 * d * d2;
        double d5 = (double)f * d * d2 - (double)f2 * d * d3;
        return new double[]{d4, d5};
    }

    public static boolean isBlockUnder(float under) {
        if (MoveUtils.mc.player.getPosY() < 0.0) {
            return false;
        }
        AxisAlignedBB aab = MoveUtils.mc.player.getBoundingBox().offset(0.0, -under, 0.0);
        return MoveUtils.mc.world.getCollisionShapes(MoveUtils.mc.player, aab).toList().isEmpty();
    }

    public static double getDirection(boolean toRadians) {
        float rotationYaw;
        HitAura hitAura = Expensive.getInstance().getModuleManager().getHitAura();
        boolean check = hitAura.isState() && hitAura.getTarget() != null && (Boolean)hitAura.getOptions().getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false;
        float f = rotationYaw = check ? hitAura.rotateVector.x : MoveUtils.mc.player.rotationYaw;
        if (MoveUtils.mc.player.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (MoveUtils.mc.player.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (MoveUtils.mc.player.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (MoveUtils.mc.player.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (MoveUtils.mc.player.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return toRadians ? Math.toRadians(rotationYaw) : (double)rotationYaw;
    }

    public static float getDirection() {
        HitAura hitAura = Expensive.getInstance().getModuleManager().getHitAura();
        boolean check = hitAura.isState() && hitAura.getTarget() != null && (Boolean)hitAura.getOptions().getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false;
        float rotationYaw = check ? hitAura.rotateVector.x : MoveUtils.mc.player.rotationYaw;
        float strafeFactor = 0.0f;
        if (MoveUtils.mc.player.movementInput.moveForward > 0.0f) {
            strafeFactor = 1.0f;
        }
        if (MoveUtils.mc.player.movementInput.moveForward < 0.0f) {
            strafeFactor = -1.0f;
        }
        if (strafeFactor == 0.0f) {
            MovementInput cfr_ignored_0 = MoveUtils.mc.player.movementInput;
            if (MovementInput.moveStrafe > 0.0f) {
                rotationYaw -= 90.0f;
            }
            MovementInput cfr_ignored_1 = MoveUtils.mc.player.movementInput;
            if (MovementInput.moveStrafe < 0.0f) {
                rotationYaw += 90.0f;
            }
        } else {
            MovementInput cfr_ignored_2 = MoveUtils.mc.player.movementInput;
            if (MovementInput.moveStrafe > 0.0f) {
                rotationYaw -= 45.0f * strafeFactor;
            }
            MovementInput cfr_ignored_3 = MoveUtils.mc.player.movementInput;
            if (MovementInput.moveStrafe < 0.0f) {
                rotationYaw += 45.0f * strafeFactor;
            }
        }
        if (strafeFactor < 0.0f) {
            rotationYaw -= 180.0f;
        }
        return (float)Math.toRadians(rotationYaw);
    }

    public static void setStrafe(double motion) {
        if (!MoveUtils.isMoving()) {
            return;
        }
        double radians = MoveUtils.getDirection();
        MoveUtils.mc.player.motion.x = -Math.sin(radians) * motion;
        MoveUtils.mc.player.motion.z = Math.cos(radians) * motion;
    }

    public static final boolean moveKeyPressed(int keyNumber) {
        boolean w = MoveUtils.mc.gameSettings.keyBindForward.isKeyDown();
        boolean a = MoveUtils.mc.gameSettings.keyBindLeft.isKeyDown();
        boolean s = MoveUtils.mc.gameSettings.keyBindBack.isKeyDown();
        boolean d = MoveUtils.mc.gameSettings.keyBindRight.isKeyDown();
        return keyNumber == 0 ? w : (keyNumber == 1 ? a : (keyNumber == 2 ? s : keyNumber == 3 && d));
    }

    public static final boolean w() {
        return MoveUtils.moveKeyPressed(0);
    }

    public static final boolean a() {
        return MoveUtils.moveKeyPressed(1);
    }

    public static final boolean s() {
        return MoveUtils.moveKeyPressed(2);
    }

    public static final boolean d() {
        return MoveUtils.moveKeyPressed(3);
    }

    public static final float moveYaw(float entityYaw) {
        return entityYaw + (float)(!MoveUtils.a() || !MoveUtils.d() || MoveUtils.w() && MoveUtils.s() || !MoveUtils.w() && !MoveUtils.s() ? (MoveUtils.w() && MoveUtils.s() && (!MoveUtils.a() || !MoveUtils.d()) && (MoveUtils.a() || MoveUtils.d()) ? (MoveUtils.a() ? -90 : (MoveUtils.d() ? 90 : 0)) : (MoveUtils.a() && MoveUtils.d() && (!MoveUtils.w() || !MoveUtils.s()) || MoveUtils.w() && MoveUtils.s() && (!MoveUtils.a() || !MoveUtils.d()) ? 0 : (!(MoveUtils.a() || MoveUtils.d() || MoveUtils.s()) ? 0 : (MoveUtils.w() && !MoveUtils.s() ? 45 : (MoveUtils.s() && !MoveUtils.w() ? (!MoveUtils.a() && !MoveUtils.d() ? 180 : 135) : (!(!MoveUtils.w() && !MoveUtils.s() || MoveUtils.w() && MoveUtils.s()) ? 0 : 90))) * (MoveUtils.a() ? -1 : 1)))) : (MoveUtils.w() ? 0 : (MoveUtils.s() ? 180 : 0)));
    }

    private MoveUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static class MoveEvent {
        public static void setMoveMotion(MovingEvent move, double motion) {
            float yaw;
            double forward = IMinecraft.mc.player.movementInput.moveForward;
            MovementInput cfr_ignored_0 = IMinecraft.mc.player.movementInput;
            double strafe = MovementInput.moveStrafe;
            HitAura hitAura = Expensive.getInstance().getModuleManager().getHitAura();
            boolean check = hitAura.isState() && hitAura.getTarget() != null && (Boolean)hitAura.getOptions().getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false;
            float f = yaw = check ? hitAura.rotateVector.x : IMinecraft.mc.player.rotationYaw;
            if (forward == 0.0 && strafe == 0.0) {
                move.getMotion().x = 0.0;
                move.getMotion().z = 0.0;
            } else {
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += (float)(forward > 0.0 ? -45 : 45);
                    } else if (strafe < 0.0) {
                        yaw += (float)(forward > 0.0 ? 45 : -45);
                    }
                    strafe = 0.0;
                    if (forward > 0.0) {
                        forward = 1.0;
                    } else if (forward < 0.0) {
                        forward = -1.0;
                    }
                }
                move.getMotion().x = forward * motion * (double)MathHelper.cos((float)Math.toRadians(yaw + 90.0f)) + strafe * motion * (double)MathHelper.sin((float)Math.toRadians(yaw + 90.0f));
                move.getMotion().z = forward * motion * (double)MathHelper.sin((float)Math.toRadians(yaw + 90.0f)) - strafe * motion * (double)MathHelper.cos((float)Math.toRadians(yaw + 90.0f));
            }
        }
    }
}

