/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.events.MovingEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="ElytraFly", category=Category.Movement)
public class ElytraFly
extends Module {
    private final StopWatch stopWatch = new StopWatch();
    private final StopWatch stopWatch1 = new StopWatch();
    public static long lastStartFalling;
    public ModeSetting mode = new ModeSetting("\u041c\u043e\u0434", "Matrix", "Matrix", "Matrix Glide", "Firework");
    private SliderSetting motionY = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c Y", 0.2f, 0.1f, 0.5f, 0.01f).setVisible(() -> this.mode.is("Matrix"));
    private SliderSetting motionX = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c XZ", 1.2f, 0.1f, 5.0f, 0.1f).setVisible(() -> !this.mode.is("Firework"));
    private BooleanSetting autojump = new BooleanSetting("\u0410\u0432\u0442\u043e \u043f\u0440\u044b\u0436\u043e\u043a", false).setVisible(() -> this.mode.is("Matrix"));
    private BooleanSetting saveMe = new BooleanSetting("\u0421\u043f\u0430\u0441\u0430\u0442\u044c", false).setVisible(() -> this.mode.is("Matrix"));
    private final SliderSetting timerStartFireWork = new SliderSetting("\u0422\u0430\u0439\u043c\u0435\u0440 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0430", 400.0f, 50.0f, 1500.0f, 10.0f).setVisible(() -> this.mode.is("Firework"));
    private final BooleanSetting onlyGrimBypass = new BooleanSetting("\u041e\u0431\u0445\u043e\u0434 Grim", true).setVisible(() -> this.mode.is("Firework"));

    public ElytraFly() {
        this.addSettings(this.mode, this.motionX, this.motionY, this.autojump, this.saveMe, this.timerStartFireWork, this.onlyGrimBypass);
    }

    @Subscribe
    public void onMoving(MovingEvent e) {
        if (this.mode.is("Matrix Glide")) {
            int elytra = InventoryUtil.getSlotIDFromItem(Items.ELYTRA);
            if (elytra == -1) {
                return;
            }
            Vector3d motion = e.motion;
            if (System.currentTimeMillis() - lastStartFalling > 1000L) {
                ElytraFly.disabler(elytra);
            }
            motion.y = System.currentTimeMillis() - lastStartFalling < 800L && !ElytraFly.mc.player.isSneaking() ? ((Float)this.motionY.get()).doubleValue() : (motion.y -= 0.05);
            ElytraFly.mc.player.jump();
            ElytraFly.mc.player.motion.y = motion.y;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.stopWatch.reset();
        this.stopWatch1.reset();
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.mode.is("Firework")) {
            long elytraSwapTime = 550L;
            boolean launchRocket = true;
            if (((Boolean)this.onlyGrimBypass.get()).booleanValue()) {
                elytraSwapTime = 0L;
                if (ElytraFly.mc.player.getActiveHand() == Hand.MAIN_HAND && ElytraFly.mc.player.getHeldItemMainhand().getUseAction() == UseAction.EAT) {
                    launchRocket = false;
                }
            }
            for (int i = 0; i < 9; ++i) {
                if (ElytraFly.mc.player.inventory.getStackInSlot(i).getItem() != Items.ELYTRA || ElytraFly.mc.world.getBlockState(new BlockPos(ElytraFly.mc.player.getPosX(), ElytraFly.mc.player.getPosY() - 0.01, ElytraFly.mc.player.getPosZ())).getBlock() != Blocks.AIR || ElytraFly.mc.player.isOnGround() || ElytraFly.mc.player.isInWater() || ElytraFly.mc.player.isInLava() || ElytraFly.mc.player.isElytraFlying()) continue;
                if (this.stopWatch1.isReached(elytraSwapTime)) {
                    ElytraFly.mc.playerController.windowClick(0, 6, i, ClickType.SWAP, ElytraFly.mc.player);
                    ElytraFly.mc.player.startFallFlying();
                    ElytraFly.mc.player.connection.sendPacket(new CEntityActionPacket(ElytraFly.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    ElytraFly.mc.playerController.windowClick(0, 6, i, ClickType.SWAP, ElytraFly.mc.player);
                    this.stopWatch1.reset();
                }
                if (!this.stopWatch.isReached(((Float)this.timerStartFireWork.get()).longValue()) || !ElytraFly.mc.player.isElytraFlying() || !launchRocket) continue;
                InventoryUtil.inventorySwapClick(Items.FIREWORK_ROCKET, false);
                this.stopWatch.reset();
            }
        }
        if (this.mode.is("Matrix")) {
            int elytra = InventoryUtil.getHotbarSlotOfItem();
            if (MoveUtils.reason(false)) {
                return;
            }
            if (elytra == -1) {
                return;
            }
            if (ElytraFly.mc.player.isOnGround()) {
                if (((Boolean)this.autojump.get()).booleanValue()) {
                    ElytraFly.mc.player.jump();
                }
                this.stopWatch.reset();
            } else if (this.stopWatch.isReached(350L)) {
                if (ElytraFly.mc.player.ticksExisted % 2 == 0) {
                    ElytraFly.disabler(elytra);
                }
                double d = ElytraFly.mc.player.motion.y = ElytraFly.mc.player.ticksExisted % 2 != 0 ? -0.25 : 0.25;
                if (!ElytraFly.mc.player.isSneaking() && ElytraFly.mc.gameSettings.keyBindJump.pressed) {
                    ElytraFly.mc.player.motion.y = ((Float)this.motionY.get()).floatValue();
                }
                if (ElytraFly.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    ElytraFly.mc.player.motion.y = -((Float)this.motionY.get()).floatValue();
                }
                MoveUtils.setMotion(((Float)this.motionX.get()).floatValue());
            }
            if (((Boolean)this.saveMe.get()).booleanValue() && (!MoveUtils.isBlockUnder(4.0f) || ElytraFly.mc.player.collidedHorizontally || ElytraFly.mc.player.collidedVertically)) {
                ElytraFly.mc.player.getRidingEntity().motion.y += (double)((Float)this.motionY.get()).floatValue();
            }
        }
    }

    public static void disabler(int elytra) {
        if (elytra != -2) {
            ElytraFly.mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, ElytraFly.mc.player);
            ElytraFly.mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, ElytraFly.mc.player);
        }
        mc.getConnection().sendPacket(new CEntityActionPacket(ElytraFly.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        mc.getConnection().sendPacket(new CEntityActionPacket(ElytraFly.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        if (elytra != -2) {
            ElytraFly.mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, ElytraFly.mc.player);
            ElytraFly.mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, ElytraFly.mc.player);
        }
        lastStartFalling = System.currentTimeMillis();
    }
}

