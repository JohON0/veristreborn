/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public final class PlayerUtils {
    static Minecraft mc = Minecraft.getInstance();
    private static final Pattern NAME_REGEX = Pattern.compile("^[A-z\u0410-\u044f0-9_]{3,16}$");

    public static boolean isNameValid(String name) {
        return NAME_REGEX.matcher(name).matches();
    }

    public static boolean isInHell() {
        if (PlayerUtils.mc.world == null) {
            return false;
        }
        return Objects.equals(PlayerUtils.mc.world.getDimensionKey(), "the_nether");
    }

    public static float calculateCorrectYawOffset(float yaw) {
        float renderYawOffset;
        double xDiff = PlayerUtils.mc.player.getPosX() - PlayerUtils.mc.player.prevPosX;
        double zDiff = PlayerUtils.mc.player.getPosZ() - PlayerUtils.mc.player.prevPosZ;
        float distSquared = (float)(xDiff * xDiff + zDiff * zDiff);
        float offset = renderYawOffset = PlayerUtils.mc.player.prevRenderYawOffset;
        if (distSquared > 0.0025000002f) {
            offset = (float)MathHelper.atan2(zDiff, xDiff) * 180.0f / (float)Math.PI - 90.0f;
        }
        if (PlayerUtils.mc.player != null && PlayerUtils.mc.player.swingProgress > 0.0f) {
            offset = yaw;
        }
        float yawOffsetDiff = MathHelper.wrapDegrees(yaw - (renderYawOffset + MathHelper.wrapDegrees(offset - renderYawOffset) * 0.3f));
        yawOffsetDiff = MathHelper.clamp(yawOffsetDiff, -75.0f, 75.0f);
        renderYawOffset = yaw - yawOffsetDiff;
        if (yawOffsetDiff * yawOffsetDiff > 2500.0f) {
            renderYawOffset += yawOffsetDiff * 0.2f;
        }
        return renderYawOffset;
    }

    private PlayerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

