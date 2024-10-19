/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.events.MovingEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="Fly", category=Category.Movement)
public class Fly
extends Module {
    private final ModeSetting mode = new ModeSetting("\u041c\u043e\u0434", "Vanilla", "Vanilla", "MatrixJump", "MatrixGlide", "Grim", "FunTime", "BlockPlace", "MatrixChunk", "AAC");
    private final SliderSetting horizontal = new SliderSetting("\u041f\u043e \u0433\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u0438", 1.0f, 0.0f, 5.0f, 0.1f).setVisible(() -> !this.mode.is("BlockPlace") && !this.mode.is("FunTime") && !this.mode.is("AAC") && !this.mode.is("Grim"));
    private final SliderSetting vertical = new SliderSetting("\u041f\u043e \u0432\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u0438", 0.5f, 0.0f, 5.0f, 0.1f).setVisible(() -> !this.mode.is("BlockPlace") && !this.mode.is("FunTime") && !this.mode.is("AAC") && !this.mode.is("Grim") && !this.mode.is("MatrixChunk"));
    private BooleanSetting superbow = new BooleanSetting("\u0414\u0435\u0440\u0433\u0430\u0442\u044c Y", false).setVisible(() -> this.mode.is("Vanilla"));
    final ModeListSetting options = new ModeListSetting("\u041e\u043f\u0446\u0438\u0438", new BooleanSetting("\u0410\u0432\u0442\u043e \u043f\u0440\u044b\u0436\u043e\u043a", false), new BooleanSetting("\u0420\u043e\u0442\u0430\u0446\u0438\u044f", true)).setVisible(() -> this.mode.is("BlockPlace"));
    private final ModeSetting speedMode = new ModeSetting("\u0422\u0438\u043f \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u0438", "Vanilla", "Vanilla", "Motion").setVisible(() -> this.mode.is("BlockPlace"));
    public SliderSetting vanillaSpeed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c Vanilla", 1.0f, 1.0f, 2.0f, 0.01f).setVisible(() -> this.mode.is("BlockPlace") && this.speedMode.is("Vanilla"));
    public SliderSetting motionSpeed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c Motion", 0.3f, 0.1f, 1.0f, 0.01f).setVisible(() -> this.mode.is("BlockPlace") && this.speedMode.is("Motion"));
    public BooleanSetting upWard = new BooleanSetting("\u0412\u0432\u0435\u0440\u0445", true).setVisible(() -> this.mode.is("MatrixChunk"));
    public BooleanSetting downWard = new BooleanSetting("\u0412\u043d\u0438\u0437", true).setVisible(() -> this.mode.is("MatrixChunk"));
    public BooleanSetting autoUp = new BooleanSetting("\u041f\u043e\u0434\u043d\u0438\u043c\u0430\u0442\u044c", true).setVisible(() -> this.mode.is("MatrixChunk"));
    public BooleanSetting smoothSpeed = new BooleanSetting("\u041f\u043b\u0430\u0432\u043d\u0430\u044f \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c", true).setVisible(() -> this.mode.is("MatrixChunk"));
    public BooleanSetting noVanillaKick = new BooleanSetting("Vanilla Disabler", true).setVisible(() -> this.mode.is("MatrixChunk"));
    public Entity vehicle;
    int tickerFinalling;
    boolean enableGround;
    StopWatch timeFlying = new StopWatch();

    public Fly() {
        this.addSettings(this.mode, this.horizontal, this.vertical, this.superbow, this.options, this.speedMode, this.vanillaSpeed, this.motionSpeed, this.upWard, this.downWard, this.autoUp, this.smoothSpeed, this.noVanillaKick);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (Fly.mc.player == null || Fly.mc.world == null) {
            return;
        }
        if (((Boolean)this.options.getValueByName("\u0410\u0432\u0442\u043e \u043f\u0440\u044b\u0436\u043e\u043a").get()).booleanValue() && Fly.mc.player.isOnGround() && this.mode.is("BlockPlace") && !MoveUtils.isMoving()) {
            Fly.mc.player.jump();
        }
        switch ((String)this.mode.get()) {
            case "Vanilla": {
                this.updatePlayerMotion();
                break;
            }
            case "MatrixChunk": {
                MoveUtils.setSpeed((float)((Boolean)this.smoothSpeed.get() != false ? MoveUtils.getSpeed() / 5.0 : MoveUtils.getSpeed() / 30.0), (Boolean)this.smoothSpeed.get() != false ? 0.3f : 0.0f);
                break;
            }
            case "FunTime": {
                if (!(Fly.mc.player.fallDistance > 3.0f)) break;
                Fly.mc.player.connection.sendPacket(new CEntityActionPacket(Fly.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                for (int i1 = 0; i1 < 60; ++i1) {
                    Fly.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Fly.mc.player.getPosX(), Fly.mc.player.getPosY(), Fly.mc.player.getPosZ(), Fly.mc.player.rotationYaw, Fly.mc.player.rotationPitch, true));
                    for (int i = 0; i < 3; ++i) {
                        Fly.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Fly.mc.player.getPosX(), Fly.mc.player.getPosY() + 1.0E-10, Fly.mc.player.getPosZ(), Fly.mc.player.rotationYaw, Fly.mc.player.rotationPitch, false));
                    }
                    Fly.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Fly.mc.player.getPosX(), Fly.mc.player.getPosY(), Fly.mc.player.getPosZ(), Fly.mc.player.rotationYaw, Fly.mc.player.rotationPitch, true));
                }
                Fly.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Fly.mc.player.getPosX(), Fly.mc.player.getPosY(), Fly.mc.player.getPosZ(), Fly.mc.player.rotationYaw, Fly.mc.player.rotationPitch, true));
                Fly.mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.OFF_HAND));
                if (!Fly.mc.player.isSprinting()) {
                    Fly.mc.player.connection.sendPacket(new CEntityActionPacket(Fly.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                }
                Fly.mc.player.fallDistance = 0.0f;
                break;
            }
            case "MatrixJump": {
                if (Fly.mc.player.isOnGround()) {
                    Fly.mc.player.jump();
                    break;
                }
                MoveUtils.setMotion(Math.min(((Float)this.horizontal.get()).floatValue(), 1.97f));
                Fly.mc.player.motion.y = ((Float)this.vertical.get()).floatValue();
                break;
            }
            case "MatrixGlide": {
                MoveUtils.setMotion(((Float)this.horizontal.get()).floatValue());
                Fly.mc.player.motion.y = -0.003;
                break;
            }
            case "BlockPlace": {
                BlockPos posBelow;
                if (this.speedMode.is("Vanilla") && Fly.mc.player.isOnGround()) {
                    Fly.mc.player.motion.x *= (double)((Float)this.vanillaSpeed.get()).floatValue();
                    Fly.mc.player.motion.z *= (double)((Float)this.vanillaSpeed.get()).floatValue();
                }
                if (this.speedMode.is("Motion") && Fly.mc.player.isOnGround()) {
                    MoveUtils.setMotion(((Float)this.motionSpeed.get()).floatValue());
                }
                if (!Fly.mc.world.getBlockState(posBelow = Fly.mc.player.getPosition().down()).getMaterial().isReplaceable()) break;
                Fly.placeBlockUnderPlayer(posBelow, MoveUtils.isMoving() ? 0.2f : 0.1f);
                break;
            }
            case "Grim": {
                Fly.mc.player.multiplyMotionXZ(0.0f);
                int crate = 10;
                float ySpeed = 0.02f * (float)crate;
                Fly.mc.player.motion.y = Fly.mc.player.isJumping ? (double)ySpeed : (Fly.mc.player.isSneaking() ? (double)(-ySpeed) : 0.0);
                float speed = 0.05f * (float)crate;
                MoveUtils.setSpeed(speed);
                for (int i = 0; i < crate; ++i) {
                    Fly.mc.player.connection.sendPacket(new CPlayerPacket(false));
                }
                break;
            }
            case "AAC": {
                double setedMotY;
                if (!Fly.mc.player.isJumping && !Fly.posBlock(Fly.mc.player.getPosX(), Fly.mc.player.getPosY() - 0.044, Fly.mc.player.getPosZ())) {
                    this.enableGround = false;
                }
                if (Fly.mc.player.isOnGround() && Fly.mc.player.collidedVertically) {
                    this.enableGround = true;
                }
                Fly.mc.player.motion.y = setedMotY = Fly.mc.player.isJumping ? (this.enableGround ? (double)Fly.mc.player.getJumpUpwardsMotion() : Fly.mc.player.motion.y) : Fly.mc.player.motion.y;
                if (MoveUtils.getSpeed() > 0.19) {
                    Fly.mc.player.jumpMovementFactor = 0.17f;
                }
                Fly.mc.player.multiplyMotionXZ(1.005f);
            }
        }
    }

    @Subscribe
    public void onMove(MovingEvent e) {
        if (this.mode.is("MatrixChunk") && Fly.mc.player.fallDistance != 0.0f) {
            double motion;
            float curMotion = ((Float)this.horizontal.get()).floatValue() / 10.0f;
            double d = motion = (Boolean)this.smoothSpeed.get() != false ? MoveUtils.getCuttingSpeed() / 1.3 : 0.0;
            if (Fly.mc.player.fallDistance != 0.0f || !((Boolean)this.autoUp.get()).booleanValue()) {
                motion = (Fly.mc.player.isJumping && (Boolean)this.upWard.get() != false ? 8.2675f : 9.4675f) * curMotion;
            }
            if (!MoveUtils.moveKeysPressed() && ((Boolean)this.smoothSpeed.get()).booleanValue()) {
                MoveUtils.setSpeed((float)motion, 0.6f);
            } else {
                MoveUtils.setCuttingSpeed(motion / 1.06);
                MoveUtils.setSpeed((float)motion);
            }
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (((Boolean)this.options.getValueByName("\u0420\u043e\u0442\u0430\u0446\u0438\u044f").get()).booleanValue() && this.mode.is("BlockPlace")) {
            if (Expensive.getInstance().getModuleManager().getHitAura().isState() && Expensive.getInstance().getModuleManager().getHitAura().getTarget() != null) {
                return;
            }
            Fly.mc.player.rotationPitchHead = 90.0f;
            e.setPitch(90.0f);
        }
        if (this.mode.is("MatrixChunk")) {
            if (Fly.mc.player.isOnGround() && Fly.mc.player.fallDistance == 0.0f && ((Boolean)this.autoUp.get()).booleanValue()) {
                MoveUtils.setSpeed(0.0f);
                Fly.mc.player.jumpMovementFactor = 0.0f;
                if (!Fly.mc.player.isJumping) {
                    Fly.mc.player.motion.y = Fly.mc.player.getJumpUpwardsMotion();
                }
                return;
            }
            if (Fly.mc.player.fallDistance != 0.0f) {
                ++this.tickerFinalling;
                if (Fly.mc.player.isJumping && ((Boolean)this.upWard.get()).booleanValue() && MoveUtils.moveKeysPressed() && MoveUtils.getCuttingSpeed() > 0.1) {
                    double d = Fly.mc.player.fallDistance != 0.0f ? 0.42 : (Fly.mc.player.motion.y = (Boolean)this.noVanillaKick.get() != false ? -0.02 : 0.0);
                    if (this.tickerFinalling % 2 == 0) {
                        Fly.mc.player.motion.y = (Boolean)this.noVanillaKick.get() != false ? -0.05 : -1.0E-6;
                    }
                } else if (!Fly.mc.player.isSneaking() || !((Boolean)this.downWard.get()).booleanValue()) {
                    if (((Boolean)this.noVanillaKick.get()).booleanValue() && this.timeFlying.isReached(50L)) {
                        Fly.mc.player.motion.y = 0.035 * (double)(this.tickerFinalling % 2 != 1 ? -1 : 1);
                    } else {
                        Fly.mc.player.motion.y = 0.0;
                        Fly.mc.player.motion.y = -1.0E-6;
                    }
                }
            } else {
                this.tickerFinalling = 0;
                this.timeFlying.reset();
            }
        }
    }

    public void onToggle(boolean actived) {
        if (actived) {
            this.timeFlying.reset();
            if (this.mode.is("MatrixChunk")) {
                this.tickerFinalling = 0;
            }
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (Fly.mc.player == null || Fly.mc.world == null) {
            return;
        }
        switch ((String)this.mode.get()) {
            case "MatrixJump": {
                IPacket<?> iPacket = e.getPacket();
                if (!(iPacket instanceof SPlayerPositionLookPacket)) break;
                SPlayerPositionLookPacket p = (SPlayerPositionLookPacket)iPacket;
                if (Fly.mc.player == null) {
                    this.toggle();
                }
                Fly.mc.player.setPosition(p.getX(), p.getY(), p.getZ());
                Fly.mc.player.connection.sendPacket(new CConfirmTeleportPacket(p.getTeleportId()));
                e.cancel();
                this.toggle();
            }
        }
    }

    public static boolean posBlock(double x, double y, double z) {
        return Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.AIR && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.WATER && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.LAVA && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.CAKE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.TALL_GRASS && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.STONE_BUTTON && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.FLOWER_POT && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.CHORUS_FLOWER && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.FLOWER_POT && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SUNFLOWER && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.VINE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.ACACIA_FENCE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.ACACIA_FENCE_GATE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.BIRCH_FENCE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.BIRCH_FENCE_GATE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DARK_OAK_FENCE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DARK_OAK_FENCE_GATE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.JUNGLE_FENCE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.JUNGLE_FENCE_GATE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.NETHER_BRICK_FENCE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.OAK_FENCE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.OAK_FENCE_GATE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SPRUCE_FENCE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SPRUCE_FENCE_GATE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.ENCHANTING_TABLE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.END_PORTAL_FRAME && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.PLAYER_HEAD && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DAYLIGHT_DETECTOR && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DAYLIGHT_DETECTOR && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.PURPUR_SLAB && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.STONE_SLAB && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.DEAD_BUSH && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.REDSTONE_WIRE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.REDSTONE_TORCH && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.TORCH && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.REDSTONE_WIRE && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.WATER && Fly.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.SNOW;
    }

    public static void placeBlockUnderPlayer(BlockPos pos, float fallDistance) {
        int blockSlot = InventoryUtil.findBlockInHotbar();
        if (blockSlot == -1) {
            return;
        }
        if (Fly.mc.player.fallDistance > fallDistance) {
            int lastSlot = Fly.mc.player.inventory.currentItem;
            Fly.mc.player.inventory.currentItem = blockSlot;
            Vector3d vector3d = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
            Fly.mc.playerController.processRightClickBlock(Fly.mc.player, Fly.mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(vector3d, Direction.UP, pos, false));
            Fly.mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
            Fly.mc.player.inventory.currentItem = lastSlot;
        }
    }

    private void updatePlayerMotion() {
        double motionX = Fly.mc.player.getMotion().x;
        double motionY = this.getMotionY();
        double motionZ = Fly.mc.player.getMotion().z;
        MoveUtils.setMotion(((Float)this.horizontal.get()).floatValue());
        Fly.mc.player.motion.y = motionY;
        if (((Boolean)this.superbow.get()).booleanValue()) {
            float i = 0.0f;
            i = Fly.mc.player.ticksExisted % 2 == 0 ? 0.1f : -0.1f;
            if (!Fly.mc.gameSettings.keyBindSneak.pressed && !Fly.mc.gameSettings.keyBindJump.pressed) {
                Fly.mc.player.motion.y += (double)i;
            }
        }
    }

    private double getMotionY() {
        return Fly.mc.gameSettings.keyBindSneak.pressed ? (double)(-((Float)this.vertical.get()).floatValue()) : (Fly.mc.gameSettings.keyBindJump.pressed ? (double)((Float)this.vertical.get()).floatValue() : 0.0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.onToggle(true);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.onToggle(false);
        Fly.mc.player.abilities.isFlying = false;
    }
}

