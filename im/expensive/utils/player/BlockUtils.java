/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import im.expensive.utils.client.IMinecraft;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public final class BlockUtils
implements IMinecraft {
    public static Block getBlock(BlockPos pos) {
        return BlockUtils.getState(pos).getBlock();
    }

    public static Block getBlock(double x, double y, double z) {
        return BlockUtils.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static Block getBlockAbovePlayer(PlayerEntity inPlayer, double blocks) {
        return BlockUtils.getBlockAtPos(new BlockPos(inPlayer.getPosX(), inPlayer.getPosY() + (blocks += (double)inPlayer.getHeight()), inPlayer.getPosZ()));
    }

    public static Block getBlockAtPos(BlockPos inBlockPos) {
        BlockState s = BlockUtils.mc.world.getBlockState(inBlockPos);
        return s.getBlock();
    }

    public static Block getBlockAtPosC(PlayerEntity inPlayer, double x, double y, double z) {
        return BlockUtils.getBlockAtPos(new BlockPos(inPlayer.getPosX() - x, inPlayer.getPosY() - y, inPlayer.getPosZ() - z));
    }

    public static float getBlockDistance(float xDiff, float yDiff, float zDiff) {
        return MathHelper.sqrt((xDiff - 0.5f) * (xDiff - 0.5f) + (yDiff - 0.5f) * (yDiff - 0.5f) + (zDiff - 0.5f) * (zDiff - 0.5f));
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        ClientPlayerEntity player = BlockUtils.mc.player;
        double x = posX - player.getPosX();
        double y = posY - (player.getPosY() + (double)player.getEyeHeight());
        double z = posZ - player.getPosZ();
        double dist = MathHelper.sqrt(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static BlockPos getBlockPos(BlockPos inBlockPos) {
        return inBlockPos;
    }

    public static BlockPos getBlockPos(double x, double y, double z) {
        return BlockUtils.getBlockPos(new BlockPos(x, y, z));
    }

    public static BlockPos getBlockPosUnderPlayer(PlayerEntity inPlayer) {
        return new BlockPos(inPlayer.getPosX(), inPlayer.getPosY() + (BlockUtils.mc.player.motion.y + 0.1) - 1.0, inPlayer.getPosZ());
    }

    public static Block getBlockUnderPlayer(PlayerEntity inPlayer) {
        return BlockUtils.getBlockAtPos(new BlockPos(inPlayer.getPosX(), inPlayer.getPosY() + (BlockUtils.mc.player.motion.y + 0.1) - 1.0, inPlayer.getPosZ()));
    }

    public static float getHorizontalPlayerBlockDistance(BlockPos blockPos) {
        float xDiff = (float)(BlockUtils.mc.player.getPosX() - (double)blockPos.getX());
        float zDiff = (float)(BlockUtils.mc.player.getPosZ() - (double)blockPos.getZ());
        return MathHelper.sqrt((xDiff - 0.5f) * (xDiff - 0.5f) + (zDiff - 0.5f) * (zDiff - 0.5f));
    }

    public static float getPlayerBlockDistance(BlockPos blockPos) {
        return BlockUtils.getPlayerBlockDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static float getPlayerBlockDistance(double posX, double posY, double posZ) {
        float xDiff = (float)(BlockUtils.mc.player.getPosX() - posX);
        float yDiff = (float)(BlockUtils.mc.player.getPosY() - posY);
        float zDiff = (float)(BlockUtils.mc.player.getPosZ() - posZ);
        return BlockUtils.getBlockDistance(xDiff, yDiff, zDiff);
    }

    public static AbstractBlock.AbstractBlockState getState(BlockPos pos) {
        return BlockUtils.mc.world.getBlockState(pos);
    }

    private BlockUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

