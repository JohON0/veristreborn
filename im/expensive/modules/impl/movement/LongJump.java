/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.events.MovingEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MouseUtil;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.Pose;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

@ModuleRegister(name="LongJump", category=Category.Movement)
public class LongJump
extends Module {
    public ModeSetting mode = new ModeSetting("\u041c\u043e\u0434", "Slap", "Slap", "FlagBoost", "InstantLong");
    boolean placed;
    int counter;
    public StopWatch slapTimer = new StopWatch();
    public StopWatch flagTimer = new StopWatch();

    public LongJump() {
        this.addSettings(this.mode);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.mode.is("Slap") && !LongJump.mc.player.isInWater()) {
            int slot = InventoryUtil.getSlotInInventoryOrHotbar();
            if (slot == -1) {
                this.print("\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u043e\u043b\u0443\u0431\u043b\u043e\u043a\u043e\u0432 \u0432 \u0445\u043e\u0442\u0431\u0430\u0440\u0435!");
                this.toggle();
                return;
            }
            int old = LongJump.mc.player.inventory.currentItem;
            RayTraceResult rayTraceResult = MouseUtil.rayTraceResult(2.0, LongJump.mc.player.rotationYaw, 90.0f, LongJump.mc.player);
            if (rayTraceResult instanceof BlockRayTraceResult) {
                BlockRayTraceResult result = (BlockRayTraceResult)rayTraceResult;
                if (MoveUtils.isMoving()) {
                    if ((double)LongJump.mc.player.fallDistance >= 0.8 && LongJump.mc.world.getBlockState(LongJump.mc.player.getPosition()).isAir() && !LongJump.mc.world.getBlockState(result.getPos()).isAir() && LongJump.mc.world.getBlockState(result.getPos()).isSolid() && !(LongJump.mc.world.getBlockState(result.getPos()).getBlock() instanceof SlabBlock) && !(LongJump.mc.world.getBlockState(result.getPos()).getBlock() instanceof StairsBlock)) {
                        LongJump.mc.player.inventory.currentItem = slot;
                        this.placed = true;
                        LongJump.mc.playerController.processRightClickBlock(LongJump.mc.player, LongJump.mc.world, Hand.MAIN_HAND, result);
                        LongJump.mc.player.inventory.currentItem = old;
                        LongJump.mc.player.fallDistance = 0.0f;
                    }
                    LongJump.mc.gameSettings.keyBindJump.pressed = false;
                    if (LongJump.mc.player.isOnGround() && !LongJump.mc.gameSettings.keyBindJump.pressed && this.placed && LongJump.mc.world.getBlockState(LongJump.mc.player.getPosition()).isAir() && !LongJump.mc.world.getBlockState(result.getPos()).isAir() && LongJump.mc.world.getBlockState(result.getPos()).isSolid() && !(LongJump.mc.world.getBlockState(result.getPos()).getBlock() instanceof SlabBlock) && this.slapTimer.isReached(750L)) {
                        LongJump.mc.player.setPose(Pose.STANDING);
                        this.slapTimer.reset();
                        this.placed = false;
                    } else if (LongJump.mc.player.isOnGround() && !LongJump.mc.gameSettings.keyBindJump.pressed) {
                        LongJump.mc.player.jump();
                        this.placed = false;
                    }
                }
            } else if (LongJump.mc.player.isOnGround() && !LongJump.mc.gameSettings.keyBindJump.pressed) {
                LongJump.mc.player.jump();
                this.placed = false;
            }
        }
        if (this.mode.is("FlagBoost")) {
            if (LongJump.mc.player.motion.y != -0.0784000015258789) {
                this.flagTimer.reset();
            }
            if (!MoveUtils.isMoving()) {
                this.flagTimer.setTime(this.flagTimer.getTime() + 50L);
            }
            if (this.flagTimer.isReached(100L) && MoveUtils.isMoving()) {
                this.flagHop();
                LongJump.mc.player.motion.y = 1.0;
            }
        }
        if (this.mode.is("InstantLong") && LongJump.mc.player.hurtTime == 7) {
            MoveUtils.setCuttingSpeed(6.603774070739746);
            LongJump.mc.player.motion.y = 0.42;
        }
    }

    @Subscribe
    public void onMoving(MovingEvent e) {
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        IPacket<?> iPacket;
        if (this.mode.is("Slap") && (iPacket = e.getPacket()) instanceof SPlayerPositionLookPacket) {
            SPlayerPositionLookPacket p = (SPlayerPositionLookPacket)iPacket;
            this.placed = false;
            this.counter = 0;
            LongJump.mc.player.setPose(Pose.STANDING);
        }
        if (this.mode.is("FlagBoost") && e.isReceive() && (iPacket = e.getPacket()) instanceof SPlayerPositionLookPacket) {
            SPlayerPositionLookPacket look = (SPlayerPositionLookPacket)iPacket;
            LongJump.mc.player.setPosition(look.getX(), look.getY(), look.getZ());
            LongJump.mc.player.connection.sendPacket(new CConfirmTeleportPacket(look.getTeleportId()));
            this.flagHop();
            e.cancel();
        }
    }

    public void flagHop() {
        LongJump.mc.player.motion.y = 0.4229;
        MoveUtils.setSpeed(1.953f);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.counter = 0;
        this.placed = false;
    }
}

