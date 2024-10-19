/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventInput;
import im.expensive.events.EventMotion;
import im.expensive.events.EventUpdate;
import im.expensive.events.PlaceObsidianEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MoveUtils;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="AutoExplosion", category=Category.Combat)
public class AutoExplosion
extends Module {
    public final ModeListSetting options = new ModeListSetting("\u041e\u043f\u0446\u0438\u0438", new BooleanSetting("\u041d\u0435 \u0432\u0437\u0440\u044b\u0432\u0430\u0442\u044c \u0441\u0435\u0431\u044f", true), new BooleanSetting("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f", true));
    private final SliderSetting delayAttack = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 5.0f, 0.0f, 10.0f, 1.0f);
    private Entity crystalEntity = null;
    private BlockPos obsidianPos = null;
    private int oldCurrentSlot = -1;
    public Vector2f rotationVector = new Vector2f(0.0f, 0.0f);
    StopWatch attackStopWatch = new StopWatch();
    int bestSlot = -1;
    int oldSlot = -1;

    public boolean check() {
        return this.rotationVector != null && (Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false && this.crystalEntity != null && this.obsidianPos != null && this.isState();
    }

    public AutoExplosion() {
        this.addSettings(this.options, this.delayAttack);
    }

    @Subscribe
    public void onMoveInput(EventInput e) {
        if (this.check()) {
            MoveUtils.fixMovement(e, this.rotationVector.x);
        }
    }

    @Subscribe
    public void onObsidianPlace(PlaceObsidianEvent e) {
        boolean slotNotNull;
        BlockPos obsidianPos = e.getPos();
        boolean isOffHand = AutoExplosion.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        int slotInInventory = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(Items.END_CRYSTAL, false);
        int slotInHotBar = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(Items.END_CRYSTAL, true);
        this.bestSlot = InventoryUtil.getInstance().findBestSlotInHotBar();
        boolean bl = slotNotNull = AutoExplosion.mc.player.inventory.getStackInSlot(this.bestSlot).getItem() != Items.AIR;
        if (isOffHand && obsidianPos != null) {
            this.setAndUseCrystal(this.bestSlot, obsidianPos);
            this.obsidianPos = obsidianPos;
        }
        if (slotInHotBar == -1 && slotInInventory != -1 && this.bestSlot != -1) {
            InventoryUtil.moveItem(slotInInventory, this.bestSlot + 36, slotNotNull);
            if (slotNotNull && this.oldSlot == -1) {
                this.oldSlot = slotInInventory;
            }
            if (obsidianPos != null) {
                this.oldCurrentSlot = AutoExplosion.mc.player.inventory.currentItem;
                this.setAndUseCrystal(this.bestSlot, obsidianPos);
                AutoExplosion.mc.player.inventory.currentItem = this.oldCurrentSlot;
                this.obsidianPos = obsidianPos;
            }
            AutoExplosion.mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, AutoExplosion.mc.player);
            AutoExplosion.mc.playerController.windowClick(0, this.bestSlot + 36, 0, ClickType.PICKUP, AutoExplosion.mc.player);
            AutoExplosion.mc.playerController.windowClickFixed(0, this.oldSlot, 0, ClickType.PICKUP, AutoExplosion.mc.player, 250);
        } else if (slotInHotBar != -1 && obsidianPos != null) {
            this.oldCurrentSlot = AutoExplosion.mc.player.inventory.currentItem;
            this.setAndUseCrystal(slotInHotBar, obsidianPos);
            AutoExplosion.mc.player.inventory.currentItem = this.oldCurrentSlot;
            this.obsidianPos = obsidianPos;
        }
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (this.obsidianPos != null) {
            this.findEnderCrystals(this.obsidianPos).forEach(this::attackCrystal);
        }
        if (this.crystalEntity != null && !this.crystalEntity.isAlive()) {
            this.reset();
        }
    }

    @Subscribe
    private void onMotion(EventMotion e) {
        if (this.isValid(this.crystalEntity)) {
            this.rotationVector = MathUtil.rotationToEntity(this.crystalEntity);
            e.setYaw(this.rotationVector.x);
            e.setPitch(this.rotationVector.y);
            AutoExplosion.mc.player.renderYawOffset = this.rotationVector.x;
            AutoExplosion.mc.player.rotationYawHead = this.rotationVector.x;
            AutoExplosion.mc.player.rotationPitchHead = this.rotationVector.y;
            if (((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) {
                AutoExplosion.mc.player.rotationYawOffset = this.rotationVector.x;
            }
        } else if (((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) {
            AutoExplosion.mc.player.rotationYawOffset = -2.14748365E9f;
        }
    }

    @Override
    public void onDisable() {
        this.reset();
        super.onDisable();
        if (((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) {
            AutoExplosion.mc.player.rotationYawOffset = -2.14748365E9f;
        }
    }

    private void attackCrystal(Entity entity) {
        if (this.isValid(entity) && AutoExplosion.mc.player.getCooledAttackStrength(1.0f) >= 1.0f && this.attackStopWatch.hasTimeElapsed()) {
            long delay = ((Float)this.delayAttack.get()).longValue() * 100L;
            this.attackStopWatch.setLastMS(delay);
            AutoExplosion.mc.playerController.attackEntity(AutoExplosion.mc.player, entity);
            AutoExplosion.mc.player.swingArm(Hand.MAIN_HAND);
            this.crystalEntity = entity;
        }
        if (!entity.isAlive()) {
            this.reset();
        }
    }

    private void setAndUseCrystal(int slot, BlockPos pos) {
        Hand hand;
        boolean isOffHand = AutoExplosion.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        Vector3d center = new Vector3d((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f);
        if (!isOffHand) {
            AutoExplosion.mc.player.inventory.currentItem = slot;
        }
        Hand hand2 = hand = isOffHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (AutoExplosion.mc.playerController.processRightClickBlock(AutoExplosion.mc.player, AutoExplosion.mc.world, hand, new BlockRayTraceResult(center, Direction.UP, pos, false)) == ActionResultType.SUCCESS) {
            AutoExplosion.mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    private boolean isValid(Entity base) {
        if (base == null) {
            return false;
        }
        if (this.obsidianPos == null) {
            return false;
        }
        if (((Boolean)this.options.getValueByName("\u041d\u0435 \u0432\u0437\u0440\u044b\u0432\u0430\u0442\u044c \u0441\u0435\u0431\u044f").get()).booleanValue() && AutoExplosion.mc.player.getPosY() > (double)this.obsidianPos.getY()) {
            return false;
        }
        return this.isCorrectDistance();
    }

    private boolean isCorrectDistance() {
        if (this.obsidianPos == null) {
            return false;
        }
        return AutoExplosion.mc.player.getPositionVec().distanceTo(new Vector3d(this.obsidianPos.getX(), this.obsidianPos.getY(), this.obsidianPos.getZ())) <= (double)AutoExplosion.mc.playerController.getBlockReachDistance();
    }

    public List<Entity> findEnderCrystals(BlockPos position) {
        return AutoExplosion.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(position.getX(), position.getY(), position.getZ(), (double)position.getX() + 1.0, (double)position.getY() + 2.0, (double)position.getZ() + 1.0)).stream().filter(entity -> entity instanceof EnderCrystalEntity).collect(Collectors.toList());
    }

    private void reset() {
        this.crystalEntity = null;
        this.obsidianPos = null;
        this.rotationVector = new Vector2f(AutoExplosion.mc.player.rotationYaw, AutoExplosion.mc.player.rotationPitch);
        this.oldCurrentSlot = -1;
        this.bestSlot = -1;
    }
}

