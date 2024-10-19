/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import im.expensive.utils.client.IMinecraft;
import net.minecraft.block.AirBlock;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PotionUtil
implements IMinecraft {
    public static boolean isChangingItem;
    private boolean isItemChangeRequested;
    private int previousSlot = -1;

    public static boolean isBlockAirBelowPlayer(float distance) {
        if (PotionUtil.mc.player == null) {
            return false;
        }
        BlockPos blockPos = new BlockPos(PotionUtil.mc.player.getPosX(), PotionUtil.mc.player.getPosY() - (double)distance, PotionUtil.mc.player.getPosZ());
        return PotionUtil.mc.world.getBlockState(blockPos).getBlock() instanceof AirBlock;
    }

    public void changeItemSlot(boolean resetAfter) {
        if (this.isItemChangeRequested && this.previousSlot != -1) {
            isChangingItem = true;
            PotionUtil.mc.player.inventory.currentItem = this.previousSlot;
            if (resetAfter) {
                this.isItemChangeRequested = false;
                this.previousSlot = -1;
                isChangingItem = false;
            }
        }
    }

    public void setPreviousSlot(int slot) {
        this.previousSlot = slot;
    }

    public static void useItem(Hand hand) {
        PotionUtil.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(hand));
        PotionUtil.mc.gameRenderer.itemRenderer.resetEquippedProgress(hand);
    }

    public static int calculateHorizontalDistance(long x1, long z1) {
        float deltaX = (float)(PotionUtil.mc.player.getPosX() - (double)x1);
        float deltaZ = (float)(PotionUtil.mc.player.getPosZ() - (double)z1);
        return (int)MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    public static int calculateDistance(long x1, long y1, long z1) {
        float deltaX = (float)(PotionUtil.mc.player.getPosX() - (double)x1);
        float deltaY = (float)(PotionUtil.mc.player.getPosY() - (double)y1);
        float deltaZ = (float)(PotionUtil.mc.player.getPosZ() - (double)z1);
        return (int)MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    public static double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        double deltaZ = z1 - z2;
        return MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
}

