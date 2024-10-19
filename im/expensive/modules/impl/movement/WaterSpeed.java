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
import im.expensive.utils.player.MoveUtils;
import im.expensive.utils.player.StrafeMovement;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.AirBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AirItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;

@ModuleRegister(name="WaterSpeed", category=Category.Movement)
public class WaterSpeed
extends Module {
    private ModeSetting mode = new ModeSetting("\u041e\u0431\u0445\u043e\u0434", "Matrix", "Matrix", "Grim", "Funtime");
    private SliderSetting speed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 4.0f, 1.0f, 10.0f, 0.1f).setVisible(() -> this.mode.is("Grim"));
    private StrafeMovement strafeMovement = new StrafeMovement();
    private BooleanSetting smartWork = new BooleanSetting("\u0423\u043c\u043d\u044b\u0439", true).setVisible(() -> this.mode.is("Matrix"));
    int tick;

    public WaterSpeed() {
        this.addSettings(this.mode, this.speed, this.smartWork);
    }

    @Subscribe
    public void onPlayer(MovingEvent e) {
        if (this.mode.is("Grim") && WaterSpeed.mc.player.isInWater() && MoveUtils.isMoving()) {
            if (WaterSpeed.mc.gameSettings.keyBindJump.isKeyDown() && WaterSpeed.mc.gameSettings.keyBindSneak.isKeyDown()) {
                WaterSpeed.mc.player.motion.y = 0.0;
            }
            WaterSpeed.mc.player.setSwimming(true);
            float moveSpeed = ((Float)this.speed.get()).floatValue() + new Random().nextFloat() * 1.1f;
            double moveX = WaterSpeed.mc.player.getForward().x * (double)(moveSpeed /= 100.0f);
            double moveZ = WaterSpeed.mc.player.getForward().z * (double)moveSpeed;
            WaterSpeed.mc.player.addVelocity(moveX, 0.0, moveZ);
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.mode.is("Funtime") && WaterSpeed.mc.player != null && WaterSpeed.mc.player.isAlive() && WaterSpeed.mc.player.isInWater()) {
            WaterSpeed.mc.player.setMotion(WaterSpeed.mc.player.getMotion().x * 1.0505, WaterSpeed.mc.player.getMotion().y, WaterSpeed.mc.player.getMotion().z * 1.0505);
        }
        if (this.mode.is("Matrix")) {
            ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
            WaterSpeed.mc.player.getArmorInventoryList().forEach(stacks::add);
            stacks.removeIf(w -> w.getItem() instanceof AirItem);
            float motion = (float)MoveUtils.getMotion();
            boolean hasEnchantments = false;
            for (ItemStack stack : stacks) {
                int enchantmentLevel = 0;
                if (this.buildEnchantments(stack, 1.0f)) {
                    enchantmentLevel = 1;
                }
                if (enchantmentLevel <= 0) continue;
                motion = 0.5f;
                hasEnchantments = true;
            }
            if (WaterSpeed.mc.player.collidedHorizontally) {
                this.tick = 0;
                return;
            }
            if (!WaterSpeed.mc.player.isInWater()) {
                return;
            }
            if (WaterSpeed.mc.gameSettings.keyBindJump.isKeyDown() && !WaterSpeed.mc.player.isSneaking() && !(WaterSpeed.mc.world.getBlockState(WaterSpeed.mc.player.getPosition().add(0, 1, 0)).getBlock() instanceof AirBlock)) {
                WaterSpeed.mc.player.motion.y = 0.12f;
            }
            if (WaterSpeed.mc.gameSettings.keyBindSneak.isKeyDown()) {
                WaterSpeed.mc.player.motion.y = -0.35f;
            }
            if (((Boolean)this.smartWork.get()).booleanValue()) {
                if (!WaterSpeed.mc.player.isPotionActive(Effects.SPEED)) {
                    this.tick = 0;
                    return;
                }
                if (!hasEnchantments) {
                    return;
                }
            }
            if (WaterSpeed.mc.world.getBlockState(WaterSpeed.mc.player.getPosition().add(0, 1, 0)).getBlock() instanceof AirBlock && WaterSpeed.mc.gameSettings.keyBindJump.isKeyDown()) {
                ++this.tick;
                WaterSpeed.mc.player.motion.y = 0.12f;
            }
            ++this.tick;
            MoveUtils.setMotion(0.4f);
            this.strafeMovement.setOldSpeed(0.4);
        }
    }

    public boolean buildEnchantments(ItemStack stack, float strenght) {
        if (stack != null) {
            if (stack.getItem() instanceof ArmorItem) {
                return EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, stack) > 0;
            }
        } else {
            return false;
        }
        return false;
    }
}

