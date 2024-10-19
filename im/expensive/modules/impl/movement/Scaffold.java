/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventInput;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.misc.SlowPackets;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.MoveUtils;
import im.expensive.utils.player.RayTraceUtils;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="Scaffold", category=Category.Movement)
public class Scaffold
extends Module {
    public BooleanSetting blink = new BooleanSetting("\u0411\u043b\u0438\u043d\u043a", false);
    private BlockCache blockCache;
    private BlockCache lastBlockCache;
    public Vector2f rotation;
    private float savedY;
    public static final ConcurrentLinkedQueue<SlowPackets.TimedPacket> packets = new ConcurrentLinkedQueue();
    public boolean sneak;
    public StopWatch stopWatch = new StopWatch();

    public Scaffold() {
        this.addSettings(this.blink);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Scaffold.mc.timer.timerSpeed = 1.0f;
        this.rotation = new Vector2f(Scaffold.mc.player.rotationYaw, Scaffold.mc.player.rotationPitch);
        for (SlowPackets.TimedPacket p : packets) {
            Scaffold.mc.player.connection.getNetworkManager().sendPacketWithoutEvent(p.getPacket());
        }
        packets.clear();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.rotation = new Vector2f(Scaffold.mc.player.rotationYaw, Scaffold.mc.player.rotationPitch);
        if (Scaffold.mc.player != null) {
            this.savedY = (float)Scaffold.mc.player.getPosY();
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (((Boolean)this.blink.get()).booleanValue()) {
            if (Scaffold.mc.player != null && Scaffold.mc.world != null && !mc.isSingleplayer() && !Scaffold.mc.player.getShouldBeDead()) {
                if (e.isSend()) {
                    IPacket<?> packet = e.getPacket();
                    packets.add(new SlowPackets.TimedPacket(packet, System.currentTimeMillis()));
                    e.cancel();
                }
            } else {
                this.setState(false, false);
            }
        }
    }

    @Subscribe
    public void onInput(EventInput e) {
        RayTraceResult result;
        if (this.rotation != null && (result = RayTraceUtils.rayTrace(3.0, this.rotation.x, this.rotation.y, Scaffold.mc.player)).getType() != RayTraceResult.Type.BLOCK && Scaffold.mc.world.getBlockState(Scaffold.mc.player.getPosition().add(0.0, -0.5, 0.0)).getBlock() == Blocks.AIR) {
            e.setSneak(true);
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        Scaffold.mc.player.setSprinting(false);
        if (Scaffold.mc.player.isOnGround()) {
            this.savedY = (float)Math.floor(Scaffold.mc.player.getPosY() - 1.0);
        }
        this.blockCache = this.getBlockInfo();
        if (this.blockCache == null) {
            return;
        }
        this.lastBlockCache = this.getBlockInfo();
        float[] rot = this.getRotations(this.blockCache.position, this.blockCache.facing);
        this.rotation = new Vector2f(rot[0], rot[1]);
        e.setYaw(this.rotation.x);
        e.setPitch(this.rotation.y);
        Scaffold.mc.player.rotationYawHead = this.rotation.x;
        Scaffold.mc.player.renderYawOffset = this.rotation.x;
        Scaffold.mc.player.rotationPitchHead = this.rotation.y;
        if (((Boolean)this.blink.get()).booleanValue()) {
            for (SlowPackets.TimedPacket timedPacket : packets) {
                if (System.currentTimeMillis() - timedPacket.getTime() < 1000L) continue;
                Scaffold.mc.player.connection.getNetworkManager().sendPacketWithoutEvent(timedPacket.getPacket());
                packets.remove(timedPacket);
            }
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.blockCache == null || this.lastBlockCache == null) {
            return;
        }
        int block = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack s = Scaffold.mc.player.inventory.getStackInSlot(i);
            if (!(s.getItem() instanceof BlockItem)) continue;
            block = i;
            break;
        }
        MoveUtils.setMotion(0.05);
        if (block == -1) {
            this.print("\u041d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e \u0431\u043b\u043e\u043a\u043e\u0432!");
            this.toggle();
            return;
        }
        if (this.rotation == null) {
            return;
        }
        RayTraceResult result = RayTraceUtils.rayTrace(3.0, this.rotation.x, this.rotation.y, Scaffold.mc.player);
        int last = Scaffold.mc.player.inventory.currentItem;
        Scaffold.mc.player.inventory.currentItem = block;
        Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(this.getVector(this.lastBlockCache), this.lastBlockCache.getFacing(), this.lastBlockCache.getPosition(), false));
        Scaffold.mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
        this.blockCache = null;
        Scaffold.mc.player.inventory.currentItem = last;
    }

    public float[] getRotations(BlockPos blockPos, Direction enumFacing) {
        double d = (double)blockPos.getX() + 0.5 - Scaffold.mc.player.getPosX() + (double)enumFacing.getXOffset() * 0.25;
        double d2 = (double)blockPos.getZ() + 0.5 - Scaffold.mc.player.getPosZ() + (double)enumFacing.getZOffset() * 0.25;
        double d3 = Scaffold.mc.player.getPosY() + (double)Scaffold.mc.player.getEyeHeight() - (double)blockPos.getY() - (double)enumFacing.getYOffset() * 0.25;
        double d4 = MathHelper.sqrt(d * d + d2 * d2);
        float f = (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(Math.atan2(d3, d4) * 180.0 / Math.PI);
        return new float[]{MathHelper.wrapDegrees(f), f2};
    }

    public BlockCache getBlockInfo() {
        int y = (int)(Scaffold.mc.player.getPosY() - 1.0 >= (double)this.savedY && Math.max(Scaffold.mc.player.getPosY(), (double)this.savedY) - Math.min(Scaffold.mc.player.getPosY(), (double)this.savedY) <= 3.0 && !Scaffold.mc.gameSettings.keyBindJump.isKeyDown() ? (double)this.savedY : Scaffold.mc.player.getPosY() - 1.0);
        BlockPos belowBlockPos = new BlockPos(Scaffold.mc.player.getPosX(), (double)(y - (Scaffold.mc.player.isSneaking() ? -1 : 0)), Scaffold.mc.player.getPosZ());
        for (int x = 0; x < 1; ++x) {
            for (int z = 0; z < 1; ++z) {
                for (int i = -1; i < 1; ++i) {
                    BlockPos blockPos = belowBlockPos.add(x * i, 0, z * i);
                    for (Direction direction : Direction.values()) {
                        BlockPos block = blockPos.offset(direction);
                        Material material = Scaffold.mc.world.getBlockState(block).getBlock().getDefaultState().getMaterial();
                        if (!material.isSolid() || material.isLiquid()) continue;
                        return new BlockCache(block, direction.getOpposite());
                    }
                }
            }
        }
        return null;
    }

    public Vector3d getVector(BlockCache data) {
        BlockPos pos = data.position;
        Direction face = data.facing;
        double x = (double)pos.getX() + 0.5;
        double y = (double)pos.getY() + 0.5;
        double z = (double)pos.getZ() + 0.5;
        if (face != Direction.UP && face != Direction.DOWN) {
            y += 0.5;
        } else {
            x += 0.3;
            z += 0.3;
        }
        if (face == Direction.WEST || face == Direction.EAST) {
            z += 0.15;
        }
        if (face == Direction.SOUTH || face == Direction.NORTH) {
            x += 0.15;
        }
        return new Vector3d(x, y, z);
    }

    public class BlockCache {
        private final BlockPos position;
        private final Direction facing;

        public BlockCache(BlockPos position, Direction facing) {
            this.position = position;
            this.facing = facing;
        }

        public BlockPos getPosition() {
            return this.position;
        }

        public Direction getFacing() {
            return this.facing;
        }
    }
}

