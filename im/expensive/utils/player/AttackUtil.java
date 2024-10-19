/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import im.expensive.Expensive;
import im.expensive.config.FriendStorage;
import im.expensive.utils.client.IMinecraft;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;

public class AttackUtil
implements IMinecraft {
    public final List<EntityType> entityTypes = new ArrayList<EntityType>();

    public static boolean isPlayerFalling(boolean onlyCrit, boolean onlySpace, boolean sync) {
        boolean cancelReason = AttackUtil.mc.player.areEyesInFluid(FluidTags.WATER) && AttackUtil.mc.player.movementInput.jump || AttackUtil.mc.player.areEyesInFluid(FluidTags.LAVA) && AttackUtil.mc.player.movementInput.jump || AttackUtil.mc.player.isOnLadder() || AttackUtil.mc.world.getBlockState(new BlockPos(AttackUtil.mc.player.getPosX(), AttackUtil.mc.player.getPosY(), AttackUtil.mc.player.getPosZ())).getBlock() == Blocks.COBWEB || AttackUtil.mc.player.isPassenger() || AttackUtil.mc.player.abilities.isFlying || AttackUtil.mc.player.isPotionActive(Effects.LEVITATION) || AttackUtil.mc.player.isPotionActive(Effects.BLINDNESS) || AttackUtil.mc.player.isPotionActive(Effects.SLOW_FALLING);
        boolean onSpace = !AttackUtil.mc.gameSettings.keyBindJump.isKeyDown() && AttackUtil.mc.player.isOnGround() && onlySpace;
        float attackStrength = AttackUtil.mc.player.getCooledAttackStrength(sync ? Expensive.getInstance().getTpsCalc().getAdjustTicks() : 1.0f);
        if ((double)attackStrength < 0.92) {
            return false;
        }
        if (!cancelReason && onlyCrit) {
            return onSpace || !AttackUtil.mc.player.isOnGround() && AttackUtil.mc.player.fallDistance > 0.0f;
        }
        return true;
    }

    public EntityType ofType(Entity entity, EntityType ... types) {
        List<EntityType> typeList = Arrays.asList(types);
        if (entity instanceof PlayerEntity) {
            if (AttackUtil.entityIsMe(entity, typeList)) {
                return EntityType.SELF;
            }
            if (entity != AttackUtil.mc.player) {
                if (AttackUtil.entityIsPlayer(entity, typeList)) {
                    return EntityType.PLAYERS;
                }
                if (AttackUtil.entityIsFriend(entity, typeList)) {
                    return EntityType.FRIENDS;
                }
                if (AttackUtil.entityIsNakedPlayer(entity, typeList)) {
                    return EntityType.NAKED;
                }
            }
        } else {
            if (AttackUtil.entityIsMob(entity, typeList)) {
                return EntityType.MOBS;
            }
            if (this.entityIsAnimal(entity, typeList)) {
                return EntityType.ANIMALS;
            }
        }
        return null;
    }

    private static boolean entityIsMe(Entity entity, List<EntityType> typeList) {
        return entity == AttackUtil.mc.player && typeList.contains((Object)EntityType.SELF);
    }

    private static boolean entityIsPlayer(Entity entity, List<EntityType> typeList) {
        return typeList.contains((Object)EntityType.PLAYERS) && !FriendStorage.isFriend(entity.getName().getString());
    }

    private static boolean entityIsFriend(Entity entity, List<EntityType> typeList) {
        return typeList.contains((Object)EntityType.FRIENDS) && FriendStorage.isFriend(entity.getName().getString());
    }

    private static boolean entityIsMob(Entity entity, List<EntityType> typeList) {
        return entity instanceof MonsterEntity && typeList.contains((Object)EntityType.MOBS);
    }

    private static boolean entityIsNakedPlayer(Entity entity, List<EntityType> typeList) {
        return entity instanceof PlayerEntity && ((LivingEntity)entity).getTotalArmorValue() == 0;
    }

    private boolean entityIsAnimal(Entity entity, List<EntityType> typeList) {
        return (entity instanceof AnimalEntity || entity instanceof GolemEntity || entity instanceof VillagerEntity) && typeList.contains((Object)EntityType.ANIMALS);
    }

    public AttackUtil apply(EntityType type) {
        this.entityTypes.add(type);
        return this;
    }

    public EntityType[] build() {
        return this.entityTypes.toArray(new EntityType[0]);
    }

    public static enum EntityType {
        PLAYERS,
        MOBS,
        ANIMALS,
        NAKED,
        FRIENDS,
        NPC,
        SELF;

    }
}

