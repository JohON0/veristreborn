/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.config.FriendStorage;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.AttackUtil;
import im.expensive.utils.player.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

@ModuleRegister(name="TriggerBot", category=Category.Combat)
public class TriggerBot
extends Module {
    private final BooleanSetting players = new BooleanSetting("\u0418\u0433\u0440\u043e\u043a\u0438", true);
    private final BooleanSetting mobs = new BooleanSetting("\u041c\u043e\u0431\u044b", true);
    private final BooleanSetting animals = new BooleanSetting("\u0416\u0438\u0432\u043e\u0442\u043d\u044b\u0435", true);
    private final BooleanSetting onlyCrit = new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u043a\u0440\u0438\u0442\u044b", true);
    private final BooleanSetting shieldBreak = new BooleanSetting("\u041b\u043e\u043c\u0430\u0442\u044c \u0449\u0438\u0442", false);
    private final StopWatch stopWatch = new StopWatch();

    public TriggerBot() {
        this.addSettings(this.players, this.mobs, this.animals, this.onlyCrit, this.shieldBreak);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        Entity entity = this.getValidEntity();
        if (entity == null || TriggerBot.mc.player == null) {
            return;
        }
        if (this.shouldAttack()) {
            this.stopWatch.setLastMS(500L);
            this.attackEntity(entity);
        }
    }

    private boolean shouldAttack() {
        return AttackUtil.isPlayerFalling((Boolean)this.onlyCrit.get(), true, false) && this.stopWatch.hasTimeElapsed();
    }

    private void attackEntity(Entity entity) {
        boolean shouldStopSprinting = false;
        if (((Boolean)this.onlyCrit.get()).booleanValue() && CEntityActionPacket.lastUpdatedSprint) {
            TriggerBot.mc.player.connection.sendPacket(new CEntityActionPacket(TriggerBot.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            shouldStopSprinting = true;
        }
        TriggerBot.mc.playerController.attackEntity(TriggerBot.mc.player, entity);
        TriggerBot.mc.player.swingArm(Hand.MAIN_HAND);
        if (((Boolean)this.shieldBreak.get()).booleanValue() && entity instanceof PlayerEntity) {
            TriggerBot.breakShieldPlayer(entity);
        }
        if (shouldStopSprinting) {
            TriggerBot.mc.player.connection.sendPacket(new CEntityActionPacket(TriggerBot.mc.player, CEntityActionPacket.Action.START_SPRINTING));
        }
    }

    private Entity getValidEntity() {
        Entity entity;
        if (TriggerBot.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY && this.checkEntity((LivingEntity)(entity = ((EntityRayTraceResult)TriggerBot.mc.objectMouseOver).getEntity()))) {
            return entity;
        }
        return null;
    }

    public static void breakShieldPlayer(Entity entity) {
        if (((LivingEntity)entity).isBlocking()) {
            int invSlot = InventoryUtil.getInstance().getAxeInInventory(false);
            int hotBarSlot = InventoryUtil.getInstance().getAxeInInventory(true);
            if (hotBarSlot == -1 && invSlot != -1) {
                int bestSlot = InventoryUtil.getInstance().findBestSlotInHotBar();
                TriggerBot.mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, TriggerBot.mc.player);
                TriggerBot.mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, TriggerBot.mc.player);
                TriggerBot.mc.player.connection.sendPacket(new CHeldItemChangePacket(bestSlot));
                TriggerBot.mc.playerController.attackEntity(TriggerBot.mc.player, entity);
                TriggerBot.mc.player.swingArm(Hand.MAIN_HAND);
                TriggerBot.mc.player.connection.sendPacket(new CHeldItemChangePacket(TriggerBot.mc.player.inventory.currentItem));
                TriggerBot.mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, TriggerBot.mc.player);
                TriggerBot.mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, TriggerBot.mc.player);
            }
            if (hotBarSlot != -1) {
                TriggerBot.mc.player.connection.sendPacket(new CHeldItemChangePacket(hotBarSlot));
                TriggerBot.mc.playerController.attackEntity(TriggerBot.mc.player, entity);
                TriggerBot.mc.player.swingArm(Hand.MAIN_HAND);
                TriggerBot.mc.player.connection.sendPacket(new CHeldItemChangePacket(TriggerBot.mc.player.inventory.currentItem));
            }
        }
    }

    private boolean checkEntity(LivingEntity entity) {
        if (FriendStorage.isFriend(entity.getName().getString())) {
            return false;
        }
        AttackUtil entitySelector = new AttackUtil();
        if (((Boolean)this.players.get()).booleanValue()) {
            entitySelector.apply(AttackUtil.EntityType.PLAYERS);
        }
        if (((Boolean)this.mobs.get()).booleanValue()) {
            entitySelector.apply(AttackUtil.EntityType.MOBS);
        }
        if (((Boolean)this.animals.get()).booleanValue()) {
            entitySelector.apply(AttackUtil.EntityType.ANIMALS);
        }
        return entitySelector.ofType(entity, entitySelector.build()) != null && entity.isAlive();
    }

    @Override
    public void onDisable() {
        this.stopWatch.reset();
        super.onDisable();
    }
}

