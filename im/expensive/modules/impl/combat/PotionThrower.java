/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MoveUtils;
import java.util.Comparator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="PotionThrower", category=Category.Combat)
public class PotionThrower
extends Module {
    private float previousPitch;
    private final StopWatch stopWatch = new StopWatch();
    private final BooleanSetting heal = new BooleanSetting("\u0417\u0435\u043b\u044c\u0435 \u0438\u0441\u0446\u0435\u043b\u0435\u043d\u0438\u044f", false);
    private final SliderSetting healthThreshold = new SliderSetting("\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435", 10.0f, 5.0f, 20.0f, 0.5f).setVisible(() -> (Boolean)this.heal.get());

    public PotionThrower() {
        this.addSettings(this.heal, this.healthThreshold);
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (!this.canThrowPotion()) {
            return;
        }
        if (this.isActive()) {
            Vector3d posPoint = this.findNearestCollision();
            Vector2f rotationVector = posPoint == null ? new Vector2f(PotionThrower.mc.player.rotationYaw, 90.0f) : MathUtil.rotationToVec(posPoint);
            this.previousPitch = rotationVector.y;
            e.setYaw(rotationVector.x);
            e.setPitch(this.previousPitch);
            PotionThrower.mc.player.rotationPitchHead = this.previousPitch;
        }
        e.setPostMotion(() -> {
            boolean pitchIsValid = this.previousPitch == e.getPitch();
            int oldCurrentItem = PotionThrower.mc.player.inventory.currentItem;
            for (Potions potion : Potions.values()) {
                potion.state = true;
                if (potion == Potions.HEALING && !this.hasPotionInInventory(potion) || this.shouldUsePotion(potion) || !potion.state || !pitchIsValid) continue;
                this.sendPotion(potion);
                PotionThrower.mc.player.connection.sendPacket(new CHeldItemChangePacket(oldCurrentItem));
                PotionThrower.mc.playerController.syncCurrentPlayItem();
            }
        });
    }

    public boolean isActive() {
        for (Potions potionType : Potions.values()) {
            if (!(potionType == Potions.HEALING ? (Boolean)this.heal.get() != false && !this.shouldUsePotion(potionType) && potionType.isState() && this.hasPotionInInventory(potionType) : !this.shouldUsePotion(potionType) && potionType.isState())) continue;
            return true;
        }
        return false;
    }

    public boolean canThrowPotion() {
        boolean isOnGround = !MoveUtils.isBlockUnder(0.5f) || PotionThrower.mc.player.isOnGround();
        boolean timeIsReached = this.stopWatch.isReached(700L);
        boolean ticksExisted = PotionThrower.mc.player.ticksExisted > 100;
        return isOnGround && timeIsReached && ticksExisted;
    }

    private boolean shouldUsePotion(Potions potions) {
        if (potions == Potions.HEALING) {
            if (((Boolean)this.heal.get()).booleanValue() && PotionThrower.mc.player.getHealth() < ((Float)this.healthThreshold.get()).floatValue() && this.hasPotionInInventory(potions)) {
                potions.state = true;
                return false;
            }
            potions.state = false;
            return false;
        }
        if (PotionThrower.mc.player.isPotionActive(potions.getPotion())) {
            potions.state = false;
            return true;
        }
        int potionId = potions.getId();
        if (this.findPotionSlot(potionId, true) == -1 && this.findPotionSlot(potionId, false) == -1) {
            potions.state = false;
            return true;
        }
        return false;
    }

    private boolean hasPotionInInventory(Potions potion) {
        int potionId = potion.getId();
        return this.findPotionSlot(potionId, true) != -1 || this.findPotionSlot(potionId, false) != -1;
    }

    private void sendPotion(Potions potions) {
        int potionId = potions.getId();
        int hotBarSlot = this.findPotionSlot(potionId, true);
        int inventorySlot = this.findPotionSlot(potionId, false);
        if (PotionThrower.mc.player.isPotionActive(potions.getPotion())) {
            potions.state = false;
        }
        if (hotBarSlot != -1) {
            this.sendUsePacket(hotBarSlot, Hand.MAIN_HAND);
        } else if (inventorySlot != -1) {
            int bestSlotInHotBar = 0;
            InventoryUtil.moveItem(inventorySlot, bestSlotInHotBar + 36, PotionThrower.mc.player.inventory.getStackInSlot(bestSlotInHotBar = InventoryUtil.getInstance().findBestSlotInHotBar()).getItem() != Items.AIR);
            this.sendUsePacket(bestSlotInHotBar, Hand.MAIN_HAND);
        }
    }

    private void sendUsePacket(int slot, Hand hand) {
        PotionThrower.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
        PotionThrower.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(hand));
        PotionThrower.mc.player.swingArm(Hand.MAIN_HAND);
        this.previousPitch = 0.0f;
        this.stopWatch.reset();
    }

    private int findPotionSlot(int id, boolean inHotBar) {
        int start = inHotBar ? 0 : 9;
        int end = inHotBar ? 9 : 36;
        for (int i = start; i < end; ++i) {
            ItemStack stack = PotionThrower.mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof SplashPotionItem)) continue;
            List<EffectInstance> potionEffects = PotionUtils.getEffectsFromStack(stack);
            for (EffectInstance effectInstance : potionEffects) {
                if (effectInstance.getPotion() != Effect.get(id)) continue;
                return i;
            }
        }
        return -1;
    }

    private Vector3d findNearestCollision() {
        return PotionThrower.mc.world.getCollisionShapes(PotionThrower.mc.player, PotionThrower.mc.player.getBoundingBox().grow(0.0, 0.5, 0.0)).toList().stream().min(Comparator.comparingDouble(box -> box.getBoundingBox().getCenter().squareDistanceTo(PotionThrower.mc.player.getPositionVec()))).map(box -> box.getBoundingBox().getCenter()).orElse(null);
    }

    public static enum Potions {
        STRENGTH(Effects.STRENGTH, 5),
        SPEED(Effects.SPEED, 1),
        FIRE_RESIST(Effects.FIRE_RESISTANCE, 12),
        HEALING(Effects.INSTANT_HEALTH, 6);

        private final Effect potion;
        private final int id;
        private boolean state;

        private Potions(Effect potion, int potionId) {
            this.potion = potion;
            this.id = potionId;
        }

        public Effect getPotion() {
            return this.potion;
        }

        public int getId() {
            return this.id;
        }

        public boolean isState() {
            return this.state;
        }
    }
}

