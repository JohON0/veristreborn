/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.events.InventoryCloseEvent;
import im.expensive.events.NoSlowEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.MoveUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

@ModuleRegister(name="MoveHelper", category=Category.Movement)
public class MoveHelper
extends Module {
    private final List<IPacket<?>> packet = new ArrayList();
    public StopWatch stopWatchInv = new StopWatch();
    boolean eActiveNoSlow = false;
    boolean jumped = false;
    private StopWatch timerUtil = new StopWatch();
    public final BooleanSetting noJumpDelay = new BooleanSetting("NoJumpDelay", true);
    public final BooleanSetting dragonFly = new BooleanSetting("DragonFly", false);
    public final BooleanSetting invMove = new BooleanSetting("InvMove", true);
    public final BooleanSetting noSlow = new BooleanSetting("NoSlow", true);
    private final ModeSetting noSlowMode = new ModeSetting("NoSlow Mode", "Matrix", "Matrix", "Grim", "GrimNew", "NewRw").setVisible(() -> (Boolean)this.noSlow.get());
    public final BooleanSetting noWeb = new BooleanSetting("NoWeb", false);
    public ModeSetting noWebMode = new ModeSetting("NoWeb Mode", "Motion", "Motion", "Matrix").setVisible(() -> (Boolean)this.noWeb.get());
    public SliderSetting jumpMotion = new SliderSetting("\u0421\u0438\u043b\u0430 \u043f\u0440\u044b\u0436\u043a\u0430", 0.0f, 0.0f, 10.0f, 0.5f).setVisible(() -> this.noWebMode.is("Matrix"));
    public SliderSetting speed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 1.0f, 0.1f, 2.0f, 0.2f).setVisible(() -> (Boolean)this.noWeb.get());
    public final BooleanSetting levitationControl = new BooleanSetting("LevitationControl", false);
    public ModeSetting lcMode = new ModeSetting("LC Mode", "Control", "Remove", "Control").setVisible(() -> (Boolean)this.levitationControl.get());
    public SliderSetting moveUp = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u0432\u0435\u0440\u0445", 1.0f, 1.0f, 5.0f, 0.1f).setVisible(() -> this.lcMode.is("Control") && (Boolean)this.levitationControl.get() != false);
    public SliderSetting moveDown = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u043d\u0438\u0437", 1.0f, 1.0f, 5.0f, 0.1f).setVisible(() -> this.lcMode.is("Control") && (Boolean)this.levitationControl.get() != false);

    public MoveHelper() {
        this.addSettings(this.noJumpDelay, this.invMove, this.dragonFly, this.noSlow, this.noSlowMode, this.noWeb, this.noWebMode, this.jumpMotion, this.speed, this.levitationControl, this.lcMode, this.moveUp, this.moveDown);
    }

    @Subscribe
    public void onEating(NoSlowEvent e) {
        this.handleEventUpdate(e);
    }

    @Subscribe
    public void onClose(InventoryCloseEvent e) {
        if (ClientUtil.isConnectedToServer("funtime") && ((Boolean)this.invMove.get()).booleanValue() && MoveHelper.mc.currentScreen instanceof InventoryScreen && !this.packet.isEmpty() && MoveUtils.isMoving()) {
            new Thread(() -> {
                this.stopWatchInv.reset();
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                for (IPacket<?> p : this.packet) {
                    MoveHelper.mc.player.connection.sendPacketWithoutEvent(p);
                }
                this.packet.clear();
            }).start();
            e.cancel();
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        IPacket<?> iPacket;
        if (ClientUtil.isConnectedToServer("funtime") && ((Boolean)this.invMove.get()).booleanValue() && (iPacket = e.getPacket()) instanceof CClickWindowPacket) {
            CClickWindowPacket p = (CClickWindowPacket)iPacket;
            if (MoveUtils.isMoving() && MoveHelper.mc.currentScreen instanceof InventoryScreen) {
                this.packet.add(p);
                e.cancel();
            }
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (((Boolean)this.levitationControl.get()).booleanValue() && this.lcMode.is("Control") && MoveHelper.mc.player.isPotionActive(Effects.LEVITATION)) {
            int amplifier = MoveHelper.mc.player.getActivePotionEffect(Effects.LEVITATION).getAmplifier();
            MoveHelper.mc.player.motion.y = MoveHelper.mc.gameSettings.keyBindJump.pressed ? (0.05 * (double)(amplifier + 1) - MoveHelper.mc.player.motion.y) * 0.2 * (double)((Float)this.moveUp.get()).floatValue() : (MoveHelper.mc.gameSettings.keyBindSneak.pressed ? -((0.05 * (double)(amplifier + 1) - MoveHelper.mc.player.motion.y) * 0.2 * (double)((Float)this.moveDown.get()).floatValue()) : 0.0);
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (((Boolean)this.invMove.get()).booleanValue() && MoveHelper.mc.player != null) {
            KeyBinding[] pressedKeys = new KeyBinding[]{MoveHelper.mc.gameSettings.keyBindForward, MoveHelper.mc.gameSettings.keyBindBack, MoveHelper.mc.gameSettings.keyBindLeft, MoveHelper.mc.gameSettings.keyBindRight, MoveHelper.mc.gameSettings.keyBindJump, MoveHelper.mc.gameSettings.keyBindSprint};
            if (ClientUtil.isConnectedToServer("funtime") && !this.stopWatchInv.isReached(400L)) {
                for (KeyBinding keyBinding : pressedKeys) {
                    keyBinding.setPressed(false);
                }
                return;
            }
            if (MoveHelper.mc.currentScreen instanceof ChatScreen || MoveHelper.mc.currentScreen instanceof EditSignScreen || MoveHelper.mc.currentScreen instanceof AnvilScreen) {
                return;
            }
            this.updateKeyBindingState(pressedKeys);
        }
        if (((Boolean)this.noJumpDelay.get()).booleanValue()) {
            MoveHelper.mc.player.jumpTicks = 0;
        }
        if (((Boolean)this.noWeb.get()).booleanValue()) {
            if (this.noWebMode.is("Motion") && MoveHelper.mc.world.getBlockState(new BlockPos(MoveHelper.mc.player.getPosX(), MoveHelper.mc.player.getPosY(), MoveHelper.mc.player.getPosZ())).getBlock() != Blocks.AIR && MoveHelper.mc.world.getBlockState(new BlockPos(MoveHelper.mc.player.getPosX(), MoveHelper.mc.player.getPosY(), MoveHelper.mc.player.getPosZ())).getBlock() == Blocks.COBWEB) {
                MoveHelper.mc.player.motion.y = 0.0;
                if (MoveHelper.mc.gameSettings.keyBindJump.isKeyDown()) {
                    MoveHelper.mc.player.motion.y = 1.2f;
                }
                if (MoveHelper.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveHelper.mc.player.motion.y = -1.2f;
                }
                MoveUtils.setMotion(((Float)this.speed.get()).floatValue());
            }
            if (this.noWebMode.is("Matrix")) {
                if (MoveHelper.mc.world.getBlockState(new BlockPos(MoveHelper.mc.player.getPosX(), MoveHelper.mc.player.getPosY(), MoveHelper.mc.player.getPosZ())).getBlock() != Blocks.AIR && MoveHelper.mc.world.getBlockState(new BlockPos(MoveHelper.mc.player.getPosX(), MoveHelper.mc.player.getPosY(), MoveHelper.mc.player.getPosZ())).getBlock() == Blocks.COBWEB) {
                    MoveHelper.mc.player.motion.y += 2.0;
                } else if (MoveHelper.mc.world.getBlockState(new BlockPos(MoveHelper.mc.player.getPosX(), MoveHelper.mc.player.getPosY(), MoveHelper.mc.player.getPosZ())).getBlock() == Blocks.COBWEB) {
                    MoveHelper.mc.player.motion.y += (double)((Float)this.jumpMotion.get()).floatValue();
                    MoveUtils.setSpeed(((Float)this.speed.get()).floatValue());
                    MoveHelper.mc.gameSettings.keyBindJump.pressed = false;
                }
            }
        }
        if (((Boolean)this.levitationControl.get()).booleanValue() && this.lcMode.is("Remove") && MoveHelper.mc.player.isPotionActive(Effects.LEVITATION)) {
            MoveHelper.mc.player.removeActivePotionEffect(Effects.LEVITATION);
        }
        if (((Boolean)this.dragonFly.get()).booleanValue() && MoveHelper.mc.player.abilities.isFlying) {
            MoveUtils.setMotion(1.0);
            MoveHelper.mc.player.motion.y = 0.0;
            if (MoveHelper.mc.gameSettings.keyBindJump.isKeyDown()) {
                MoveHelper.mc.player.motion.y = 0.25;
                if (MoveHelper.mc.player.moveForward == 0.0f && !MoveHelper.mc.gameSettings.keyBindLeft.isKeyDown() && !MoveHelper.mc.gameSettings.keyBindRight.isKeyDown()) {
                    MoveHelper.mc.player.motion.y = 0.5;
                }
            }
            if (MoveHelper.mc.gameSettings.keyBindSneak.isKeyDown()) {
                MoveHelper.mc.player.motion.y = -0.25;
                if (MoveHelper.mc.player.moveForward == 0.0f && !MoveHelper.mc.gameSettings.keyBindLeft.isKeyDown() && !MoveHelper.mc.gameSettings.keyBindRight.isKeyDown()) {
                    MoveHelper.mc.player.motion.y = -0.5;
                }
            }
        }
    }

    private void updateKeyBindingState(KeyBinding[] keyBindings) {
        for (KeyBinding keyBinding : keyBindings) {
            boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
            keyBinding.setPressed(isKeyPressed);
        }
    }

    private void handleEventUpdate(NoSlowEvent eventNoSlow) {
        if (MoveHelper.mc.player.isHandActive() && ((Boolean)this.noSlow.get()).booleanValue()) {
            switch ((String)this.noSlowMode.get()) {
                case "Grim": {
                    this.handleGrimMode(eventNoSlow);
                    break;
                }
                case "Matrix": {
                    this.handleMatrixMode(eventNoSlow);
                    break;
                }
                case "GrimNew": {
                    this.handleGrimNewMode(eventNoSlow);
                    break;
                }
                case "NewRw": {
                    this.handleGrimNew(eventNoSlow);
                }
            }
        } else {
            this.eActiveNoSlow = false;
            this.timerUtil.reset();
            MoveHelper.mc.timer.timerSpeed = 1.0f;
        }
    }

    private void handleGrimNew(NoSlowEvent noSlow) {
        if (MoveHelper.mc.player == null || MoveHelper.mc.player.isElytraFlying()) {
            return;
        }
        if ((MoveHelper.mc.player.getHeldItemOffhand().getUseAction() == UseAction.BLOCK || MoveHelper.mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT) && MoveHelper.mc.player.getActiveHand() == Hand.MAIN_HAND) {
            return;
        }
        if ((float)MoveHelper.mc.player.getFoodStats().getFoodLevel() < 6.0f && MoveHelper.mc.player.isSprinting() || MoveHelper.mc.player.isSneaking() || MoveHelper.mc.player.isSwimming()) {
            return;
        }
        if (MoveUtils.isMoving()) {
            noSlow.cancel();
            if (MoveHelper.mc.player.getActiveHand() == Hand.MAIN_HAND) {
                MoveHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            } else {
                MoveHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            }
        }
    }

    private void handleMatrixMode(NoSlowEvent eventNoSlow) {
        boolean isFalling = (double)MoveHelper.mc.player.fallDistance > 0.725;
        eventNoSlow.cancel();
        if (MoveHelper.mc.player.isOnGround() && !MoveHelper.mc.player.movementInput.jump) {
            if (MoveHelper.mc.player.ticksExisted % 2 == 0) {
                boolean isNotStrafing = MoveHelper.mc.player.moveStrafing == 0.0f;
                float speedMultiplier = isNotStrafing ? 0.5f : 0.4f;
                MoveHelper.mc.player.motion.x *= (double)speedMultiplier;
                MoveHelper.mc.player.motion.z *= (double)speedMultiplier;
            }
        } else if (isFalling) {
            boolean isVeryFastFalling = (double)MoveHelper.mc.player.fallDistance > 1.4;
            float speedMultiplier = isVeryFastFalling ? 0.95f : 0.97f;
            MoveHelper.mc.player.motion.x *= (double)speedMultiplier;
            MoveHelper.mc.player.motion.z *= (double)speedMultiplier;
        }
    }

    private void handleGrimNewMode(NoSlowEvent e) {
        if (!(MoveHelper.mc.player.getHeldItemOffhand().getUseAction() == UseAction.BLOCK && MoveHelper.mc.player.getActiveHand() == Hand.MAIN_HAND || MoveHelper.mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT && MoveHelper.mc.player.getActiveHand() == Hand.MAIN_HAND)) {
            if (MoveHelper.mc.player.getActiveHand() == Hand.MAIN_HAND) {
                e.cancel();
                MoveHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            } else {
                e.cancel();
                MoveHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            }
        }
    }

    private void handleGrimMode(NoSlowEvent noSlow) {
        boolean mainHandActive;
        boolean offHandActive = MoveHelper.mc.player.isHandActive() && MoveHelper.mc.player.getActiveHand() == Hand.OFF_HAND;
        boolean bl = mainHandActive = MoveHelper.mc.player.isHandActive() && MoveHelper.mc.player.getActiveHand() == Hand.MAIN_HAND;
        if ((MoveHelper.mc.player.getItemInUseCount() >= 25 || MoveHelper.mc.player.getItemInUseCount() <= 4) && MoveHelper.mc.player.getHeldItemOffhand().getItem() != Items.SHIELD) {
            return;
        }
        if (MoveHelper.mc.player.isHandActive() && !MoveHelper.mc.player.isPassenger()) {
            MoveHelper.mc.playerController.syncCurrentPlayItem();
            if (offHandActive && !MoveHelper.mc.player.getCooldownTracker().hasCooldown(MoveHelper.mc.player.getHeldItemOffhand().getItem())) {
                int old = MoveHelper.mc.player.inventory.currentItem;
                MoveHelper.mc.player.connection.sendPacket(new CHeldItemChangePacket(old + 1 > 8 ? old - 1 : old + 1));
                MoveHelper.mc.player.connection.sendPacket(new CHeldItemChangePacket(MoveHelper.mc.player.inventory.currentItem));
                MoveHelper.mc.player.setSprinting(false);
                noSlow.cancel();
            }
            if (mainHandActive && !MoveHelper.mc.player.getCooldownTracker().hasCooldown(MoveHelper.mc.player.getHeldItemMainhand().getItem())) {
                MoveHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                if (MoveHelper.mc.player.getHeldItemOffhand().getUseAction().equals((Object)UseAction.NONE)) {
                    noSlow.cancel();
                }
            }
            MoveHelper.mc.playerController.syncCurrentPlayItem();
        }
    }

    private void sendItemChangePacket() {
        if (MoveUtils.isMoving()) {
            MoveHelper.mc.player.connection.sendPacket(new CHeldItemChangePacket(MoveHelper.mc.player.inventory.currentItem % 8 + 1));
            MoveHelper.mc.player.connection.sendPacket(new CHeldItemChangePacket(MoveHelper.mc.player.inventory.currentItem));
        }
    }

    private boolean canCancel() {
        boolean isHandActive = MoveHelper.mc.player.isHandActive();
        boolean isLevitation = MoveHelper.mc.player.isPotionActive(Effects.LEVITATION);
        boolean isOnGround = MoveHelper.mc.player.isOnGround();
        boolean isJumpPressed = MoveHelper.mc.gameSettings.keyBindJump.pressed;
        boolean isElytraFlying = MoveHelper.mc.player.isElytraFlying();
        if (isLevitation || isElytraFlying) {
            return false;
        }
        return (isOnGround || isJumpPressed) && isHandActive;
    }
}

