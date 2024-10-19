/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.optifine.render.RenderUtils;

@ModuleRegister(name="StorageESP", category=Category.Render)
public class StorageESP
extends Module {
    private final Map<TileEntityType<?>, Integer> tiles = new HashMap(Map.of(new ChestTileEntity().getType(), (Object)new Color(243, 172, 82).getRGB(), new TrappedChestTileEntity().getType(), (Object)new Color(143, 109, 62).getRGB(), new BarrelTileEntity().getType(), (Object)new Color(250, 225, 62).getRGB(), new HopperTileEntity().getType(), (Object)new Color(62, 137, 250).getRGB(), new DispenserTileEntity().getType(), (Object)new Color(27, 64, 250).getRGB(), new DropperTileEntity().getType(), (Object)new Color(0, 23, 255).getRGB(), new FurnaceTileEntity().getType(), (Object)new Color(115, 115, 115).getRGB(), new EnderChestTileEntity().getType(), (Object)new Color(82, 49, 238).getRGB(), new ShulkerBoxTileEntity().getType(), (Object)new Color(246, 123, 123).getRGB(), new MobSpawnerTileEntity().getType(), (Object)new Color(112, 236, 166).getRGB()));

    @Subscribe
    private void onRender(WorldEvent e) {
        for (TileEntity tile : StorageESP.mc.world.loadedTileEntityList) {
            if (!this.tiles.containsKey(tile.getType())) continue;
            BlockPos pos = tile.getPos();
            RenderUtils.drawBlockBox(pos, this.tiles.get(tile.getType()));
        }
        for (Entity entity : StorageESP.mc.world.getAllEntities()) {
            if (!(entity instanceof ChestMinecartEntity)) continue;
            RenderUtils.drawBlockBox(entity.getPosition(), -1);
        }
    }
}

