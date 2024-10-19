/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.math;

import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.client.Vec2i;
import im.expensive.utils.math.StopWatch;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import org.joml.Vector2f;

public final class MathUtil
implements IMinecraft {
    private static final int SCALE = 2;

    public static <T extends Number> T lerp(T input, T target, double step) {
        double start = input.doubleValue();
        double end = target.doubleValue();
        double result = start + step * (end - start);
        if (input instanceof Integer) {
            return (T)Integer.valueOf((int)Math.round(result));
        }
        if (input instanceof Double) {
            return (T)Double.valueOf(result);
        }
        if (input instanceof Float) {
            return (T)Float.valueOf((float)result);
        }
        if (input instanceof Long) {
            return (T)Long.valueOf(Math.round(result));
        }
        if (input instanceof Short) {
            return (T)Short.valueOf((short)Math.round(result));
        }
        if (input instanceof Byte) {
            return (T)Byte.valueOf((byte)Math.round(result));
        }
        throw new IllegalArgumentException("Unsupported type: " + input.getClass().getSimpleName());
    }

    public static int hpResolver(LivingEntity entity, Scoreboard scoreboard) {
        return scoreboard.getObjectiveNames().size();
    }

    private static double armor(ItemStack stack) {
        if (!stack.isEnchanted()) {
            return 0.0;
        }
        Item item = stack.getItem();
        if (!(item instanceof ArmorItem)) {
            return 0.0;
        }
        ArmorItem armor = (ArmorItem)item;
        return (double)armor.getDamageReduceAmount() + (double)EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.25;
    }

    public static double armor(LivingEntity entity) {
        double armor = entity.getTotalArmorValue();
        for (ItemStack item : entity.getArmorInventoryList()) {
            armor += MathUtil.armor(item);
        }
        return armor;
    }

    public static double health(LivingEntity entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public static double buffs(LivingEntity entity) {
        double buffs = 0.0;
        for (EffectInstance effect : entity.getActivePotionEffects()) {
            if (effect.getPotion() == Effects.ABSORPTION) {
                buffs += 1.2 * (double)(effect.getAmplifier() + 1);
                continue;
            }
            if (effect.getPotion() == Effects.RESISTANCE) {
                buffs += 1.0 * (double)(effect.getAmplifier() + 1);
                continue;
            }
            if (effect.getPotion() != Effects.REGENERATION) continue;
            buffs += 1.1 * (double)(effect.getAmplifier() + 1);
        }
        return buffs;
    }

    public static double entity(LivingEntity entity, boolean health, boolean armor, boolean distance, double maxDistance, boolean buffs) {
        double a = 1.0;
        double b = 1.0;
        double c = 1.0;
        double d = 1.0;
        if (health) {
            a += MathUtil.health(entity);
        }
        if (armor) {
            b += MathUtil.armor(entity);
        }
        if (distance) {
            c += entity.getDistanceSq(Minecraft.getInstance().player) / maxDistance;
        }
        if (buffs) {
            d += MathUtil.buffs(entity);
        }
        return a * b * d * c;
    }

    public static Vector3d interpolatePos(float oldx, float oldy, float oldz, float x, float y, float z) {
        double getx = (double)(oldx + (x - oldx) * mc.getRenderPartialTicks()) - MathUtil.mc.getRenderManager().info.getProjectedView().getX();
        double gety = (double)(oldy + (y - oldy) * mc.getRenderPartialTicks()) - MathUtil.mc.getRenderManager().info.getProjectedView().getY();
        double getz = (double)(oldz + (z - oldz) * mc.getRenderPartialTicks()) - MathUtil.mc.getRenderManager().info.getProjectedView().getZ();
        return new Vector3d(getx, gety, getz);
    }

    public static Vector2d getMouse(double mouseX, double mouseY) {
        return new Vector2d(mouseX * mc.getMainWindow().getGuiScaleFactor() / 2.0, mouseY * mc.getMainWindow().getScaleFactor() / 2.0);
    }

    public static Vec2i getMouse2i(int mouseX, int mouseY) {
        return new Vec2i((int)((double)mouseX * Minecraft.getInstance().getMainWindow().getGuiScaleFactor() / 2.0), (int)((double)mouseY * Minecraft.getInstance().getMainWindow().getGuiScaleFactor() / 2.0));
    }

    public static Vector3d getVector(LivingEntity target) {
        double wHalf = target.getWidth() / 2.0f;
        double yExpand = MathHelper.clamp(target.getPosYEye() - target.getPosY(), 0.0, (double)target.getHeight());
        double xExpand = MathHelper.clamp(MathUtil.mc.player.getPosX() - target.getPosX(), -wHalf, wHalf);
        double zExpand = MathHelper.clamp(MathUtil.mc.player.getPosZ() - target.getPosZ(), -wHalf, wHalf);
        return new Vector3d(target.getPosX() - MathUtil.mc.player.getPosX() + xExpand, target.getPosY() - MathUtil.mc.player.getPosYEye() + yExpand, target.getPosZ() - MathUtil.mc.player.getPosZ() + zExpand);
    }

    public static int randomInt(int min, int max) {
        if (min >= max) {
            System.out.println("\u041d\u0443 \u0442\u044b \u0438 \u0434\u0446\u043f...");
            return -1;
        }
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public static int calculatePing() {
        return MathUtil.mc.player.connection.getPlayerInfo(MathUtil.mc.player.getUniqueID()) != null ? MathUtil.mc.player.connection.getPlayerInfo(MathUtil.mc.player.getUniqueID()).getResponseTime() : 0;
    }

    public static float random(float min, float max) {
        return (float)(Math.random() * (double)(max - min) + (double)min);
    }

    public static double randomWithUpdate(double min, double max, long ms, StopWatch stopWatch) {
        double randomValue = 0.0;
        if (stopWatch.isReached(ms)) {
            randomValue = MathUtil.random((float)min, (float)max);
            stopWatch.reset();
        }
        return randomValue;
    }

    public static net.minecraft.util.math.vector.Vector2f rotationToVec(Vector3d vec) {
        Vector3d eyesPos = MathUtil.mc.player.getEyePosition(1.0f);
        double diffX = vec != null ? vec.x - eyesPos.x : 0.0;
        double diffY = vec != null ? vec.y - (MathUtil.mc.player.getPosY() + (double)MathUtil.mc.player.getEyeHeight() + 0.5) : 0.0;
        double diffZ = vec != null ? vec.z - eyesPos.z : 0.0;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        yaw = MathUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - MathUtil.mc.player.rotationYaw);
        pitch = MathUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - MathUtil.mc.player.rotationPitch);
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        return new net.minecraft.util.math.vector.Vector2f(yaw, pitch);
    }

    public static net.minecraft.util.math.vector.Vector2f rotationToEntity(Entity target) {
        Vector3d vector3d = target.getPositionVec().subtract(Minecraft.getInstance().player.getPositionVec());
        double magnitude = Math.hypot(vector3d.x, vector3d.z);
        return new net.minecraft.util.math.vector.Vector2f((float)Math.toDegrees(Math.atan2(vector3d.z, vector3d.x)) - 90.0f, (float)(-Math.toDegrees(Math.atan2(vector3d.y, magnitude))));
    }

    public static net.minecraft.util.math.vector.Vector2f rotationToVec(net.minecraft.util.math.vector.Vector2f rotationVector, Vector3d target) {
        double x = target.x - MathUtil.mc.player.getPosX();
        double y = target.y - MathUtil.mc.player.getEyePosition((float)1.0f).y;
        double z = target.z - MathUtil.mc.player.getPosZ();
        double dst = Math.sqrt(Math.pow(x, 2.0) + Math.pow(z, 2.0));
        float yaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90.0);
        float pitch = (float)(-Math.toDegrees(Math.atan2(y, dst)));
        float yawDelta = MathHelper.wrapDegrees(yaw - rotationVector.x);
        float pitchDelta = pitch - rotationVector.y;
        if (Math.abs(yawDelta) > 180.0f) {
            yawDelta -= Math.signum(yawDelta) * 360.0f;
        }
        return new net.minecraft.util.math.vector.Vector2f(yawDelta, pitchDelta);
    }

    public static double round(double num, double increment) {
        double v = (double)Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double d0 = x1 - x2;
        double d1 = y1 - y2;
        double d2 = z1 - z2;
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        return Math.sqrt(x * x + y * y);
    }

    public static double deltaTime() {
        return MathUtil.mc.debugFPS > 0 ? 1.0 / (double)MathUtil.mc.debugFPS : 1.0;
    }

    public static float fast(float end, float start, float multiple) {
        return (1.0f - MathHelper.clamp((float)(MathUtil.deltaTime() * (double)multiple), 0.0f, 1.0f)) * end + MathHelper.clamp((float)(MathUtil.deltaTime() * (double)multiple), 0.0f, 1.0f) * start;
    }

    public static Vector3d interpolate(Vector3d end, Vector3d start, float multiple) {
        return new Vector3d(MathUtil.interpolate(end.getX(), start.getX(), (double)multiple), MathUtil.interpolate(end.getY(), start.getY(), (double)multiple), MathUtil.interpolate(end.getZ(), start.getZ(), (double)multiple));
    }

    public static Vector2f interpolate(Vector2f end, Vector2f start, float multiple) {
        return new Vector2f((float)MathUtil.interpolate(end.x, start.x, (double)multiple), (float)MathUtil.interpolate(end.y, start.y, (double)multiple));
    }

    public static Vector3d fast(Vector3d end, Vector3d start, float multiple) {
        return new Vector3d(MathUtil.fast((float)end.getX(), (float)start.getX(), multiple), MathUtil.fast((float)end.getY(), (float)start.getY(), multiple), MathUtil.fast((float)end.getZ(), (float)start.getZ(), multiple));
    }

    public static Vector2f fast(Vector2f end, Vector2f start, float multiple) {
        return new Vector2f(MathUtil.fast(end.x, start.x, multiple), MathUtil.fast(end.y, start.y, multiple));
    }

    public static float lerp(float end, float start, float multiple) {
        return (float)((double)end + (double)(start - end) * MathHelper.clamp(MathUtil.deltaTime() * (double)multiple, 0.0, 1.0));
    }

    public static double lerp(double end, double start, double multiple) {
        return end + (start - end) * MathHelper.clamp(MathUtil.deltaTime() * multiple, 0.0, 1.0);
    }

    public static float calculateDelta(float a, float b) {
        return a - b;
    }

    private MathUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

