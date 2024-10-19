/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.ui.notify.impl.WarningNotify;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;

@ModuleRegister(name="ElytraSpeed", category=Category.Movement)
public class ElytraSpeed
extends Module {
    private final StopWatch stopWatch = new StopWatch();
    private final ModeSetting mode = new ModeSetting("\u0422\u0438\u043f", "Grim", "Grim", "ReallyWorld");
    private final SliderSetting boostSpeed = new SliderSetting("C\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0431\u0443\u0441\u0442\u0430", 0.3f, 0.0f, 0.8f, 1.0E-4f);
    private final BooleanSetting safeMode = new BooleanSetting("\u0411\u0435\u0437\u043e\u043f\u0430\u0441\u043d\u044b\u0439 \u0440\u0435\u0436\u0438\u043c", true);
    private final BooleanSetting autoJump = new BooleanSetting("\u0410\u0432\u0442\u043e \u043f\u0440\u044b\u0436\u043e\u043a", false);
    int oldItem = -1;

    public ElytraSpeed() {
        this.addSettings(this.mode, this.boostSpeed, this.safeMode, this.autoJump);
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SEntityMetadataPacket && ((SEntityMetadataPacket)e.getPacket()).getEntityId() == ElytraSpeed.mc.player.getEntityId()) {
            e.cancel();
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (((Boolean)this.safeMode.get()).booleanValue() && ElytraSpeed.mc.player.collidedHorizontally) {
            this.print(TextFormatting.RED + "\u0412\u044b \u0441\u0442\u043e\u043b\u043a\u043d\u0443\u043b\u0438\u0441\u044c \u0441 \u0431\u043b\u043e\u043a\u043e\u043c!");
            Expensive.getInstance().getNotifyManager().add(0, new WarningNotify(this.getName() + ": \u0412\u044b \u0441\u0442\u043e\u043b\u043a\u043d\u0443\u043b\u0438\u0441\u044c \u0441 \u0431\u043b\u043e\u043a\u043e\u043c!", 1000L));
            this.toggle();
            return;
        }
        ElytraSpeed.mc.gameSettings.keyBindBack.setPressed(false);
        ElytraSpeed.mc.gameSettings.keyBindLeft.setPressed(false);
        ElytraSpeed.mc.gameSettings.keyBindRight.setPressed(false);
        if (((Boolean)this.autoJump.get()).booleanValue() && !ElytraSpeed.mc.gameSettings.keyBindJump.isKeyDown() && ElytraSpeed.mc.player.isOnGround()) {
            ElytraSpeed.mc.gameSettings.keyBindJump.setPressed(true);
        }
        int timeSwap = 600;
        if (this.mode.is("Grim")) {
            timeSwap = 200;
        }
        if (ElytraSpeed.mc.player.isElytraFlying()) {
            ElytraSpeed.mc.gameSettings.keyBindSneak.setPressed(true);
        } else {
            ElytraSpeed.mc.gameSettings.keyBindSneak.setPressed(false);
        }
        if (InventoryUtil.getItemSlot(Items.FIREWORK_ROCKET) == -1 || ElytraSpeed.mc.player.collidedHorizontally || !InventoryUtil.doesHotbarHaveItem(Items.ELYTRA)) {
            return;
        }
        if (((Boolean)this.autoJump.get()).booleanValue() && !ElytraSpeed.mc.gameSettings.keyBindJump.isKeyDown() && ElytraSpeed.mc.player.isOnGround() && !ElytraSpeed.mc.player.isInWater() && !ElytraSpeed.mc.player.isInLava()) {
            ElytraSpeed.mc.player.jump();
        }
        if (ElytraSpeed.mc.player.getActiveHand() == Hand.MAIN_HAND) {
            ElytraSpeed.mc.playerController.onStoppedUsingItem(ElytraSpeed.mc.player);
        }
        for (int i = 0; i < 9; ++i) {
            if (ElytraSpeed.mc.player.inventory.getStackInSlot(i).getItem() != Items.ELYTRA || !(ElytraSpeed.mc.player.fallDistance < 4.0f) || ElytraSpeed.mc.player.isOnGround() || ElytraSpeed.mc.player.isInWater() || ElytraSpeed.mc.player.isInLava() || ElytraSpeed.mc.player.collidedHorizontally || !this.stopWatch.hasTimeElapsed2(timeSwap)) continue;
            ElytraSpeed.mc.playerController.windowClick(0, 6, i, ClickType.SWAP, ElytraSpeed.mc.player);
            ElytraSpeed.mc.player.connection.sendPacket(new CEntityActionPacket(ElytraSpeed.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            for (Entity entity : ElytraSpeed.mc.world.getAllEntities()) {
                if (!(entity instanceof FireworkRocketEntity) || !(ElytraSpeed.mc.player.getDistance(entity) < 4.0f) || entity.ticksExisted >= 30) continue;
                float speed = 0.9f + ((Float)this.boostSpeed.get()).floatValue();
                MoveUtils.setMotion(speed);
            }
            ElytraSpeed.mc.playerController.windowClick(0, 6, i, ClickType.SWAP, ElytraSpeed.mc.player);
            this.oldItem = i;
            if (!this.stopWatch.hasTimeElapsed2(timeSwap)) continue;
            InventoryUtil.inventorySwapClick(Items.FIREWORK_ROCKET, false);
            this.stopWatch.reset();
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (!Expensive.getInstance().getModuleManager().getHitAura().isState() || Expensive.getInstance().getModuleManager().getHitAura().getTarget() == null) {
            ElytraSpeed.mc.player.rotationPitchHead = 15.0f;
            e.setPitch(15.0f);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.oldItem != -1) {
            if (ElytraSpeed.mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA) {
                ElytraSpeed.mc.playerController.windowClick(0, this.oldItem < 9 ? this.oldItem + 36 : this.oldItem, 38, ClickType.SWAP, ElytraSpeed.mc.player);
            }
            this.oldItem = -1;
        }
        this.stopWatch.reset();
    }
}

