/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

@ModuleRegister(name="SafeWalk", category=Category.Movement)
public class SafeWalk
extends Module {
    @Subscribe
    private void onUpdate(EventUpdate e) {
        assert (SafeWalk.mc.player != null);
        BlockPos pos = new BlockPos(SafeWalk.mc.player.getPosX(), SafeWalk.mc.player.getPosY() - 1.0, SafeWalk.mc.player.getPosZ());
        assert (SafeWalk.mc.world != null);
        SafeWalk.mc.gameSettings.keyBindSneak.setPressed((MoveUtils.isBlockUnder(0.005f) || SafeWalk.mc.world.getBlockState(pos).getBlock() == Blocks.AIR) && !SafeWalk.mc.player.isInWater() && !SafeWalk.mc.player.isInLava());
    }
}

