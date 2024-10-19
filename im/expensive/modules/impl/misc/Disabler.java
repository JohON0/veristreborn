/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.events.MovingEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.math.BlockPos;

@ModuleRegister(name="Disabler", category=Category.Misc)
public class Disabler
extends Module {
    public static long lastStartFalling;
    public static BooleanSetting matrixElytraSpoof;
    public static BooleanSetting ncpmove;
    public static BooleanSetting vulcanstrafe;
    boolean canHackJesus;

    public Disabler() {
        this.addSettings(matrixElytraSpoof, ncpmove, vulcanstrafe);
    }

    @Subscribe
    public void onUpdate(MovingEvent e) {
        if (((Boolean)vulcanstrafe.get()).booleanValue()) {
            if (Disabler.mc.player.ticksExisted % 11 == 7) {
                Disabler.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, BlockPos.ZERO.down(61), Disabler.mc.player.getHorizontalFacing().getOpposite()));
            }
            this.setCanHackJesus(!(Disabler.mc.player.ticksExisted <= 8 || Disabler.mc.playerController.getIsHittingBlock() && Disabler.mc.playerController.curBlockDamageMP > 0.0f));
        }
        if (((Boolean)matrixElytraSpoof.get()).booleanValue()) {
            int elytra = InventoryUtil.getSlotIDFromItem(Items.ELYTRA);
            if (elytra == -1) {
                return;
            }
            if (System.currentTimeMillis() - lastStartFalling > 150L) {
                Disabler.disabler(elytra);
            }
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        ItemStack chestStack = Disabler.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (((Boolean)ncpmove.get()).booleanValue()) {
            if (chestStack.getItem() != Items.ELYTRA) {
                return;
            }
            if (Disabler.mc.player.isOnGround() && !Disabler.mc.player.isElytraFlying()) {
                Disabler.mc.player.jump();
            }
            if (!MoveUtils.isMoving()) {
                Disabler.mc.player.motion.z = 0.0;
                Disabler.mc.player.motion.x = 0.0;
            }
            if ((!MoveUtils.isBlockUnder(1.5f) || Disabler.mc.player.collidedVertically) && Disabler.mc.player.isElytraFlying()) {
                Disabler.mc.player.motion.y = Disabler.mc.player.collidedVertically ? 1.0 : 0.5;
            } else if (Disabler.mc.player.isElytraFlying()) {
                double d = Disabler.mc.player.motion.y = Disabler.mc.player.ticksExisted % 14 == 0 ? -0.25 : -0.05;
            }
            if (ElytraItem.isUsable(chestStack) && !Disabler.mc.player.isElytraFlying() && !Disabler.mc.player.abilities.isFlying && Disabler.mc.player.fallDistance >= 0.2f) {
                Disabler.mc.player.startFallFlying();
                Disabler.mc.player.connection.sendPacket(new CEntityActionPacket(Disabler.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            }
        }
    }

    public static void disabler(int elytra) {
        if (elytra != -2) {
            Disabler.mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, Disabler.mc.player);
            Disabler.mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, Disabler.mc.player);
        }
        mc.getConnection().sendPacket(new CEntityActionPacket(Disabler.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        mc.getConnection().sendPacket(new CEntityActionPacket(Disabler.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        if (elytra != -2) {
            Disabler.mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, Disabler.mc.player);
            Disabler.mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, Disabler.mc.player);
        }
        lastStartFalling = System.currentTimeMillis();
    }

    public void setCanHackJesus(boolean canHackJesus) {
        this.canHackJesus = canHackJesus;
    }

    static {
        matrixElytraSpoof = new BooleanSetting("MatrixElytraSpoofs", true);
        ncpmove = new BooleanSetting("NCPMovement", false);
        vulcanstrafe = new BooleanSetting("VulcanStrafe", false);
    }
}

