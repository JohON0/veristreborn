/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.config.FriendStorage;
import im.expensive.events.EventInput;
import im.expensive.events.EventMotion;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.AntiBot;
import im.expensive.modules.impl.combat.Criticals;
import im.expensive.modules.impl.combat.PotionThrower;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.ui.oldclickgui.ClickGuiScreen;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.SensUtils;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.AttackUtil;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MouseUtil;
import im.expensive.utils.player.MoveUtils;
import im.expensive.utils.player.PlayerUtils;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="HitAura", category=Category.Combat)
public class HitAura
extends Module {
    final ModeSetting type = new ModeSetting("\u0422\u0438\u043f", "\u041f\u043b\u0430\u0432\u043d\u0430\u044f", "\u041f\u043b\u0430\u0432\u043d\u0430\u044f", "\u0420\u0435\u0437\u043a\u0430\u044f", "FunTime");
    final ModeSetting speedType = new ModeSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0440\u043e\u0442\u0430\u0446\u0438\u0438", "\u0421\u0440\u0435\u0434\u043d\u044f\u044f", "\u0411\u044b\u0441\u0442\u0440\u0430\u044f", "\u0421\u0440\u0435\u0434\u043d\u044f\u044f", "\u041c\u0435\u0434\u043b\u0435\u043d\u043d\u0430\u044f").setVisible(() -> !this.type.is("FunTime"));
    final SliderSetting attackRange = new SliderSetting("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0430\u0442\u0442\u0430\u043a\u0438", 3.0f, 2.5f, 6.0f, 0.05f);
    final SliderSetting elytraRange = new SliderSetting("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043d\u0430 \u044d\u043b\u0438\u0442\u0440\u0435", 6.0f, 0.0f, 16.0f, 0.05f);
    final SliderSetting preRange = new SliderSetting("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043d\u0430\u0432\u043e\u0434\u043a\u0438", 0.3f, 0.0f, 3.0f, 0.05f).setVisible(() -> !this.type.is("\u0420\u0435\u0437\u043a\u0430\u044f"));
    final SliderSetting tick = new SliderSetting("\u0422\u0438\u043a\u0438", 2.0f, 1.0f, 10.0f, 1.0f).setVisible(() -> this.type.is("\u0420\u0435\u0437\u043a\u0430\u044f"));
    final ModeListSetting targets = new ModeListSetting("\u0422\u0430\u0440\u0433\u0435\u0442\u044b", new BooleanSetting("\u0418\u0433\u0440\u043e\u043a\u0438", true), new BooleanSetting("\u0413\u043e\u043b\u044b\u0435", true), new BooleanSetting("\u041c\u043e\u0431\u044b", false), new BooleanSetting("\u0416\u0438\u0432\u043e\u0442\u043d\u044b\u0435", false), new BooleanSetting("\u0414\u0440\u0443\u0437\u044c\u044f", false), new BooleanSetting("\u0413\u043e\u043b\u044b\u0435 \u043d\u0435\u0432\u0438\u0434\u0438\u043c\u043a\u0438", true), new BooleanSetting("\u041d\u0435\u0432\u0438\u0434\u0438\u043c\u043a\u0438", true));
    final ModeListSetting consider = new ModeListSetting("\u0423\u0447\u0438\u0442\u044b\u0432\u0430\u0442\u044c", new BooleanSetting("\u0425\u043f", true), new BooleanSetting("\u0411\u0440\u043e\u043d\u044e", true), new BooleanSetting("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044e", true), new BooleanSetting("\u0411\u0430\u0444\u0444\u044b", true));
    final ModeListSetting options = new ModeListSetting("\u041e\u043f\u0446\u0438\u0438", new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u043a\u0440\u0438\u0442\u044b", true), new BooleanSetting("\u041b\u043e\u043c\u0430\u0442\u044c \u0449\u0438\u0442", true), new BooleanSetting("\u041e\u0442\u0436\u0438\u043c\u0430\u0442\u044c \u0449\u0438\u0442", false), new BooleanSetting("\u0423\u0441\u043a\u043e\u0440\u044f\u0442\u044c \u0440\u043e\u0442\u0430\u0446\u0438\u044e \u043f\u0440\u0438 \u0430\u0442\u0430\u043a\u0435", false), new BooleanSetting("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0441 TPS", false), new BooleanSetting("\u0424\u043e\u043a\u0443\u0441\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043e\u0434\u043d\u0443 \u0446\u0435\u043b\u044c", true), new BooleanSetting("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f", true), new BooleanSetting("\u041e\u043f\u0442\u0438\u043c\u0430\u043b\u044c\u043d\u0430\u044f \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0430\u0442\u0430\u043a\u0438", false), new BooleanSetting("\u0420\u0435\u0437\u043e\u043b\u044c\u0432\u0435\u0440", true));
    final ModeListSetting moreOptions = new ModeListSetting("\u0422\u0440\u0438\u0433\u0433\u0435\u0440\u044b", new BooleanSetting("\u041f\u0440\u043e\u0432\u0435\u0440\u043a\u0430 \u043b\u0443\u0447\u0430", true), new BooleanSetting("\u041f\u0435\u0440\u0435\u043b\u0435\u0442\u0430\u0442\u044c \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430", true), new BooleanSetting("\u0411\u0438\u0442\u044c \u0447\u0435\u0440\u0435\u0437 \u0441\u0442\u0435\u043d\u044b", true), new BooleanSetting("\u041d\u0435 \u0431\u0438\u0442\u044c \u0435\u0441\u043b\u0438 \u043a\u0443\u0448\u0430\u0435\u0448\u044c", false), new BooleanSetting("\u041d\u0435 \u0431\u0438\u0442\u044c \u0435\u0441\u043b\u0438 \u0432 \u0433\u0443\u0438", false));
    final SliderSetting elytraForward = new SliderSetting("\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u043f\u0435\u0440\u0435\u043b\u0435\u0442\u0430", 3.5f, 0.5f, 8.0f, 0.05f).setVisible(() -> (Boolean)this.moreOptions.getValueByName("\u041f\u0435\u0440\u0435\u043b\u0435\u0442\u0430\u0442\u044c \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430").get());
    public BooleanSetting wallBypass = new BooleanSetting("Wall Bypass", false).setVisible(() -> (Boolean)this.moreOptions.getValueByName("\u0411\u0438\u0442\u044c \u0447\u0435\u0440\u0435\u0437 \u0441\u0442\u0435\u043d\u044b").get());
    public BooleanSetting noRotate = new BooleanSetting("\u041d\u0430\u0432\u043e\u0434\u0438\u0442\u044c\u0441\u044f", false).setVisible(() -> (Boolean)this.moreOptions.getValueByName("\u041d\u0435 \u0431\u0438\u0442\u044c \u0435\u0441\u043b\u0438 \u043a\u0443\u0448\u0430\u0435\u0448\u044c").get() != false || (Boolean)this.moreOptions.getValueByName("\u041d\u0435 \u0431\u0438\u0442\u044c \u0435\u0441\u043b\u0438 \u0432 \u0433\u0443\u0438").get() != false);
    public BooleanSetting smartCrits = new BooleanSetting("\u0423\u043c\u043d\u044b\u0435 \u043a\u0440\u0438\u0442\u044b", false).setVisible(() -> (Boolean)this.options.getValueByName("\u0422\u043e\u043b\u044c\u043a\u043e \u043a\u0440\u0438\u0442\u044b").get());
    final ModeSetting correctionType = new ModeSetting("\u0422\u0438\u043f \u043a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u0438", "\u041d\u0435\u0437\u0430\u043c\u0435\u0442\u043d\u044b\u0439", "\u041d\u0435\u0437\u0430\u043c\u0435\u0442\u043d\u044b\u0439", "\u0421\u0444\u043e\u043a\u0443\u0441\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439").setVisible(() -> (Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get());
    final ModeSetting critType = new ModeSetting("\u041a\u0440\u0438\u0442 \u0445\u0435\u043b\u043f\u0435\u0440", "None", "None", "Matrix", "NCP", "NCP+", "Grim");
    private final StopWatch stopWatch = new StopWatch();
    public Vector2f rotateVector = new Vector2f(0.0f, 0.0f);
    private LivingEntity target;
    private Entity selected;
    float health = 0.0f;
    int ticks = 0;
    boolean isRotated = false;
    boolean canWork = true;
    boolean tpAuraRule = false;
    StopWatch yawUpdate = new StopWatch();
    StopWatch pitchUpdate = new StopWatch();
    final PotionThrower autoPotion;
    float lastYaw;
    float lastPitch;

    float aimDistance() {
        return !this.type.is("\u0420\u0435\u0437\u043a\u0430\u044f") ? ((Float)this.preRange.get()).floatValue() : 0.0f;
    }

    float maxRange() {
        return this.attackDistance() + (HitAura.mc.player.isElytraFlying() ? ((Float)this.elytraRange.get()).floatValue() : 0.0f) + this.aimDistance();
    }

    public HitAura(PotionThrower autoPotion) {
        this.autoPotion = autoPotion;
        this.addSettings(this.type, this.speedType, this.attackRange, this.preRange, this.elytraRange, this.tick, this.targets, this.consider, this.options, this.moreOptions, this.elytraForward, this.wallBypass, this.smartCrits, this.noRotate, this.correctionType, this.critType);
    }

    @Subscribe
    public void onInput(EventInput eventInput) {
        if (((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue() && this.correctionType.is("\u041d\u0435\u0437\u0430\u043c\u0435\u0442\u043d\u044b\u0439") && this.canWork) {
            MoveUtils.fixMovement(eventInput, this.rotateVector.x);
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (!this.canWork) {
            return;
        }
        if (((Boolean)this.options.getValueByName("\u0424\u043e\u043a\u0443\u0441\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043e\u0434\u043d\u0443 \u0446\u0435\u043b\u044c").get()).booleanValue() && (this.target == null || !this.isValid(this.target)) || !((Boolean)this.options.getValueByName("\u0424\u043e\u043a\u0443\u0441\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043e\u0434\u043d\u0443 \u0446\u0435\u043b\u044c").get()).booleanValue()) {
            this.updateTarget();
        }
        if (((Boolean)this.options.getValueByName("\u0420\u0435\u0437\u043e\u043b\u044c\u0432\u0435\u0440").get()).booleanValue()) {
            this.resolvePlayers();
            this.releaseResolver();
        }
        if (!(this.target == null || this.autoPotion.isState() && this.autoPotion.isActive())) {
            float rotateSpeedYaw = this.speedType.is("\u0421\u0440\u0435\u0434\u043d\u044f\u044f") ? (this.type.is("\u041f\u043b\u0430\u0432\u043d\u0430\u044f") ? 115 : 180) : 40;
            float rotateSpeedPitch = this.speedType.is("\u0421\u0440\u0435\u0434\u043d\u044f\u044f") ? (this.type.is("\u041f\u043b\u0430\u0432\u043d\u0430\u044f") ? 65 : 90) : 35;
            this.isRotated = false;
            if (this.shouldPlayerFalling() && this.stopWatch.hasTimeElapsed()) {
                this.ticks = ((Float)this.tick.get()).intValue();
                this.tpAuraRule = true;
                this.updateAttack();
                this.tpAuraRule = false;
            }
            if (this.type.is("\u0420\u0435\u0437\u043a\u0430\u044f")) {
                if (this.ticks > 0 || HitAura.mc.player.isElytraFlying()) {
                    this.setRotate(rotateSpeedYaw, rotateSpeedPitch);
                    --this.ticks;
                } else {
                    this.reset();
                }
            } else if (!this.isRotated) {
                this.setRotate(rotateSpeedYaw, rotateSpeedPitch);
            }
        } else {
            this.stopWatch.setLastMS(0L);
            this.reset();
        }
        if (this.target != null && this.isRotated && !HitAura.mc.player.isElytraFlying() && HitAura.mc.player.getDistanceEyePos(this.target) <= (double)this.attackDistance()) {
            this.critHelper();
        }
    }

    @Subscribe
    private void onWalking(EventMotion e) {
        if (this.target == null || this.autoPotion.isState() && this.autoPotion.isActive() || !this.canWork) {
            return;
        }
        float yaw = this.rotateVector.x;
        float pitch = this.rotateVector.y;
        e.setYaw(yaw);
        e.setPitch(pitch);
        HitAura.mc.player.rotationYawHead = yaw;
        HitAura.mc.player.renderYawOffset = PlayerUtils.calculateCorrectYawOffset(yaw);
        HitAura.mc.player.rotationPitchHead = pitch;
    }

    public void setRotate(float yawSpeed, float pitchSpeed) {
        if (HitAura.mc.player.isElytraFlying() || this.speedType.is("\u0411\u044b\u0441\u0442\u0440\u0430\u044f") && !this.type.is("FunTime")) {
            this.smartRotation();
        } else {
            this.baseRotation(yawSpeed, pitchSpeed);
        }
        if (((Boolean)this.moreOptions.getValueByName("\u041d\u0435 \u0431\u0438\u0442\u044c \u0435\u0441\u043b\u0438 \u043a\u0443\u0448\u0430\u0435\u0448\u044c").get()).booleanValue() && HitAura.mc.player.isHandActive() && HitAura.mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT && !((Boolean)this.noRotate.get()).booleanValue()) {
            this.rotateVector = new Vector2f(HitAura.mc.player.rotationYaw, HitAura.mc.player.rotationPitch);
        }
        if (!(!((Boolean)this.moreOptions.getValueByName("\u041d\u0435 \u0431\u0438\u0442\u044c \u0435\u0441\u043b\u0438 \u0432 \u0433\u0443\u0438").get()).booleanValue() || ((Boolean)this.noRotate.get()).booleanValue() || HitAura.mc.currentScreen == null || HitAura.mc.currentScreen instanceof ClickGuiScreen || HitAura.mc.currentScreen instanceof ChatScreen || HitAura.mc.currentScreen instanceof IngameMenuScreen)) {
            this.rotateVector = new Vector2f(HitAura.mc.player.rotationYaw, HitAura.mc.player.rotationPitch);
        }
    }

    public float attackDistance() {
        if (((Boolean)this.options.getValueByName("\u041e\u043f\u0442\u0438\u043c\u0430\u043b\u044c\u043d\u0430\u044f \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0430\u0442\u0430\u043a\u0438").get()).booleanValue() && !Expensive.getInstance().getModuleManager().getTPInfluence().isState()) {
            if (!HitAura.mc.player.isSwimming()) {
                return 3.6f;
            }
            return 3.0f;
        }
        if (Expensive.getInstance().getModuleManager().getTPInfluence().isState()) {
            return ((Float)Expensive.getInstance().getModuleManager().getTPInfluence().range.get()).floatValue();
        }
        return ((Float)this.attackRange.get()).floatValue();
    }

    public void resolvePlayers() {
        for (PlayerEntity playerEntity : HitAura.mc.world.getPlayers()) {
            if (!(playerEntity instanceof RemoteClientPlayerEntity)) continue;
            ((RemoteClientPlayerEntity)playerEntity).resolve();
        }
    }

    public void releaseResolver() {
        for (PlayerEntity playerEntity : HitAura.mc.world.getPlayers()) {
            if (!(playerEntity instanceof RemoteClientPlayerEntity)) continue;
            ((RemoteClientPlayerEntity)playerEntity).releaseResolver();
        }
    }

    private void updateTarget() {
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        for (Entity entity2 : HitAura.mc.world.getAllEntities()) {
            LivingEntity living;
            if (!(entity2 instanceof LivingEntity) || !this.isValid(living = (LivingEntity)entity2)) continue;
            targets.add(living);
        }
        if (targets.isEmpty()) {
            this.target = null;
            return;
        }
        if (targets.size() == 1) {
            this.target = (LivingEntity)targets.get(0);
            return;
        }
        targets.sort(Comparator.comparingDouble(entity -> MathUtil.entity(entity, (Boolean)this.consider.getValueByName("\u0425\u043f").get(), (Boolean)this.consider.getValueByName("\u0411\u0440\u043e\u043d\u044e").get(), (Boolean)this.consider.getValueByName("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044e").get(), this.maxRange(), (Boolean)this.consider.getValueByName("\u0411\u0430\u0444\u0444\u044b").get())));
        this.target = (LivingEntity)targets.get(0);
    }

    private void smartRotation() {
        this.isRotated = true;
        Vector3d vec3d = this.target.getPositionVec().add(0.0, MathHelper.clamp(HitAura.mc.player.getPosYEye() - this.target.getPosY(), 0.0, (double)this.target.getHeight() * (HitAura.mc.player.getDistanceEyePos(this.target) / (double)this.attackDistance())), 0.0).subtract(HitAura.mc.player.getEyePosition(1.0f));
        if (HitAura.mc.player.isElytraFlying()) {
            if (((Boolean)this.moreOptions.getValueByName("\u041f\u0435\u0440\u0435\u043b\u0435\u0442\u0430\u0442\u044c \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430").get()).booleanValue()) {
                Vector3d targetPosition = this.target.getPositionVec();
                Vector3d scale = this.target.getForward().normalize().scale(((Float)this.elytraForward.get()).floatValue());
                vec3d = targetPosition.add(scale);
            } else {
                vec3d = MathUtil.getVector(this.target);
            }
        }
        double vecX = vec3d.x - (HitAura.mc.player.isElytraFlying() && (Boolean)this.moreOptions.getValueByName("\u041f\u0435\u0440\u0435\u043b\u0435\u0442\u0430\u0442\u044c \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430").get() != false ? HitAura.mc.player.getPosX() : 0.0);
        double vecY = vec3d.y - (HitAura.mc.player.isElytraFlying() && (Boolean)this.moreOptions.getValueByName("\u041f\u0435\u0440\u0435\u043b\u0435\u0442\u0430\u0442\u044c \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430").get() != false ? HitAura.mc.player.getPosY() : 0.0);
        double vecZ = vec3d.z - (HitAura.mc.player.isElytraFlying() && (Boolean)this.moreOptions.getValueByName("\u041f\u0435\u0440\u0435\u043b\u0435\u0442\u0430\u0442\u044c \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430").get() != false ? HitAura.mc.player.getPosZ() : 0.0);
        float[] rotations = new float[]{(float)Math.toDegrees(Math.atan2(vecZ, vecX)) - 90.0f, (float)(-Math.toDegrees(Math.atan2(vecY, Math.hypot(vecX, vecZ))))};
        float deltaYaw = MathHelper.wrapDegrees(MathUtil.calculateDelta(rotations[0], this.rotateVector.x));
        float deltaPitch = MathUtil.calculateDelta(rotations[1], this.rotateVector.y);
        float limitedYaw = Math.min(Math.max(Math.abs(deltaYaw), 1.0f), 360.0f);
        float limitedPitch = Math.min(Math.max(Math.abs(deltaPitch), 1.0f), 90.0f);
        float finalYaw = this.rotateVector.x + (deltaYaw > 0.0f ? limitedYaw : -limitedYaw);
        float finalPitch = MathHelper.clamp(this.rotateVector.y + (deltaPitch > 0.0f ? limitedPitch : -limitedPitch), -90.0f, 90.0f);
        float gcd = SensUtils.getGCDValue();
        finalYaw -= (finalYaw - this.rotateVector.x) % gcd;
        finalPitch -= (finalPitch - this.rotateVector.y) % gcd;
        this.rotateVector = new Vector2f(finalYaw, finalPitch);
        if (((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) {
            HitAura.mc.player.rotationYawOffset = finalYaw;
        }
    }

    private void baseRotation(float rotationYawSpeed, float rotationPitchSpeed) {
        Vector3d vec = this.target.getPositionVec().add(0.0, MathHelper.clamp(HitAura.mc.player.getPosYEye() - this.target.getPosY(), 0.0, (double)this.target.getHeight() * (HitAura.mc.player.getDistanceEyePos(this.target) / (double)this.attackDistance())), 0.0).subtract(HitAura.mc.player.getEyePosition(1.0f));
        this.isRotated = true;
        float yawToTarget = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        float pitchToTarget = (float)(-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))));
        float yawDelta = MathHelper.wrapDegrees(yawToTarget - this.rotateVector.x);
        float pitchDelta = MathHelper.wrapDegrees(pitchToTarget - this.rotateVector.y);
        int roundYawDelta = (int)Math.abs(yawDelta);
        int roundPitchDelta = (int)Math.abs(pitchDelta);
        switch ((String)this.type.get()) {
            case "FunTime": {
                double pitchSpeed;
                double yawSpeed;
                if (MouseUtil.getMouseOver(this.target, this.rotateVector.x, this.rotateVector.y, this.attackDistance()) != null) {
                    yawSpeed = MathUtil.randomWithUpdate(5.0, 25.0, 200L, this.yawUpdate);
                    pitchSpeed = 0.0;
                } else {
                    yawSpeed = MathUtil.randomWithUpdate(19.0, 113.0, 60L, this.yawUpdate);
                    pitchSpeed = MathUtil.randomWithUpdate(2.0, 17.0, 40L, this.pitchUpdate);
                }
                float clampedYaw = (float)Math.min((double)Math.max((float)roundYawDelta, 1.0f), yawSpeed);
                float clampedPitch = (float)Math.min((double)Math.max((float)roundPitchDelta * 0.33f, 1.0f), pitchSpeed);
                float yaw = this.rotateVector.x + (yawDelta > 0.0f ? clampedYaw : -clampedYaw);
                float pitch = MathHelper.clamp(this.rotateVector.y + (pitchDelta > 0.0f ? clampedPitch : -clampedPitch), -90.0f, 90.0f);
                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - this.rotateVector.x) % gcd;
                pitch -= (pitch - this.rotateVector.y) % gcd;
                this.rotateVector = new Vector2f(yaw, pitch);
                this.lastYaw = clampedYaw;
                this.lastPitch = clampedPitch;
                if (!((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) break;
                HitAura.mc.player.rotationYawOffset = yaw;
                break;
            }
            case "\u041f\u043b\u0430\u0432\u043d\u0430\u044f": {
                float clampedYaw = Math.min(Math.max((float)roundYawDelta, 1.0f), rotationYawSpeed);
                float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta) * 0.33f, 1.0f), rotationPitchSpeed);
                if (Math.abs(clampedYaw - this.lastYaw) <= 3.0f) {
                    clampedYaw = this.lastYaw + 3.1f;
                }
                float yaw = this.rotateVector.x + (yawDelta > 0.0f ? clampedYaw : -clampedYaw);
                float pitch = MathHelper.clamp(this.rotateVector.y + (pitchDelta > 0.0f ? clampedPitch : -clampedPitch), -89.0f, 89.0f);
                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - this.rotateVector.x) % gcd;
                pitch -= (pitch - this.rotateVector.y) % gcd;
                this.rotateVector = new Vector2f(yaw, pitch);
                this.lastYaw = clampedYaw;
                this.lastPitch = clampedPitch;
                if (!((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) break;
                HitAura.mc.player.rotationYawOffset = yaw;
                break;
            }
            case "\u0420\u0435\u0437\u043a\u0430\u044f": {
                float yaw = this.rotateVector.x + yawDelta;
                float pitch = MathHelper.clamp(this.rotateVector.y + pitchDelta, -90.0f, 90.0f);
                float gcd = SensUtils.getGCDValue();
                yaw -= (yaw - this.rotateVector.x) % gcd;
                pitch -= (pitch - this.rotateVector.y) % gcd;
                this.rotateVector = new Vector2f(yaw, pitch);
                if (!((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) break;
                HitAura.mc.player.rotationYawOffset = yaw;
            }
        }
    }

    public void critHelper() {
        switch ((String)this.critType.get()) {
            case "None": {
                return;
            }
            case "Matrix": {
                if (!HitAura.mc.player.isJumping || !(HitAura.mc.player.motion.getY() < -0.1) || !((double)HitAura.mc.player.fallDistance > 0.5) || !(MoveUtils.getMotion() < 0.12)) break;
                HitAura.mc.player.motion.y = -1.0;
                break;
            }
            case "NCP": {
                if (!HitAura.mc.player.isJumping || HitAura.mc.player.fallDistance == 0.0f) break;
                HitAura.mc.player.motion.y -= 0.078;
                break;
            }
            case "NCP+": {
                if ((double)HitAura.mc.player.fallDistance > 0.7 && (double)HitAura.mc.player.fallDistance < 0.8 && this.target != null) {
                    HitAura.mc.timer.timerSpeed = 2.0f;
                    break;
                }
                HitAura.mc.timer.timerSpeed = 1.0f;
                break;
            }
            case "Grim": {
                if (!HitAura.mc.player.isJumping || !(HitAura.mc.player.fallDistance > 0.0f) || !((double)HitAura.mc.player.fallDistance <= 1.2) || MoveUtils.moveKeysPressed()) break;
                HitAura.mc.player.jumpTicks = 0;
                if ((double)HitAura.mc.timer.timerSpeed != 1.0) break;
                HitAura.mc.timer.timerSpeed = 1.005f;
            }
        }
    }

    private void updateAttack() {
        this.selected = MouseUtil.getMouseOver(this.target, this.rotateVector.x, this.rotateVector.y, this.attackDistance());
        if (HitAura.mc.player.getDistanceEyePos(this.target) > (double)this.attackDistance()) {
            return;
        }
        if (((Boolean)this.moreOptions.getValueByName("\u041f\u0440\u043e\u0432\u0435\u0440\u043a\u0430 \u043b\u0443\u0447\u0430").get()).booleanValue() && !((Boolean)this.moreOptions.getValueByName("\u041f\u0435\u0440\u0435\u043b\u0435\u0442\u0430\u0442\u044c \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430").get()).booleanValue() && !HitAura.mc.player.isElytraFlying() && this.selected == null) {
            return;
        }
        if (HitAura.mc.player.isBlocking() && ((Boolean)this.options.getValueByName("\u041e\u0442\u0436\u0438\u043c\u0430\u0442\u044c \u0449\u0438\u0442").get()).booleanValue()) {
            HitAura.mc.playerController.onStoppedUsingItem(HitAura.mc.player);
        }
        if (!((Boolean)this.moreOptions.getValueByName("\u0411\u0438\u0442\u044c \u0447\u0435\u0440\u0435\u0437 \u0441\u0442\u0435\u043d\u044b").get()).booleanValue()) {
            if (!HitAura.mc.player.canEntityBeSeen(this.target)) {
                return;
            }
        } else if (((Boolean)this.wallBypass.get()).booleanValue() && !HitAura.mc.player.canEntityBeSeen(this.target)) {
            this.target.getPosition().add(MathUtil.random(-0.15f, 0.15f), this.target.getBoundingBox().getYSize(), MathUtil.random(-0.15f, 0.15f));
        }
        if (((Boolean)this.moreOptions.getValueByName("\u041d\u0435 \u0431\u0438\u0442\u044c \u0435\u0441\u043b\u0438 \u043a\u0443\u0448\u0430\u0435\u0448\u044c").get()).booleanValue() && HitAura.mc.player.isHandActive() && HitAura.mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT) {
            return;
        }
        if (((Boolean)this.moreOptions.getValueByName("\u041d\u0435 \u0431\u0438\u0442\u044c \u0435\u0441\u043b\u0438 \u0432 \u0433\u0443\u0438").get()).booleanValue() && HitAura.mc.currentScreen != null && !(HitAura.mc.currentScreen instanceof ClickGuiScreen) && !(HitAura.mc.currentScreen instanceof ChatScreen) && !(HitAura.mc.currentScreen instanceof IngameMenuScreen)) {
            return;
        }
        this.tpAuraRule = true;
        if (((Boolean)this.options.getValueByName("\u0423\u0441\u043a\u043e\u0440\u044f\u0442\u044c \u0440\u043e\u0442\u0430\u0446\u0438\u044e \u043f\u0440\u0438 \u0430\u0442\u0430\u043a\u0435").get()).booleanValue()) {
            this.setRotate(70.0f, 45.0f);
        }
        this.stopWatch.setLastMS(500L);
        Criticals.cancelCrit = true;
        if (Expensive.getInstance().getModuleManager().getCriticals().isState()) {
            Expensive.getInstance().getModuleManager().getCriticals().sendCrit();
        }
        HitAura.mc.playerController.attackEntity(HitAura.mc.player, this.target);
        HitAura.mc.player.swingArm(Hand.MAIN_HAND);
        Criticals.cancelCrit = false;
        LivingEntity livingEntity = this.target;
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)livingEntity;
            if (((Boolean)this.options.getValueByName("\u041b\u043e\u043c\u0430\u0442\u044c \u0449\u0438\u0442").get()).booleanValue()) {
                this.breakShieldPlayer(player);
            }
        }
    }

    public boolean shouldPlayerFalling() {
        return AttackUtil.isPlayerFalling((Boolean)this.options.getValueByName("\u0422\u043e\u043b\u044c\u043a\u043e \u043a\u0440\u0438\u0442\u044b").get() != false && !Expensive.getInstance().getModuleManager().getCriticals().isState(), (Boolean)this.smartCrits.get(), (Boolean)this.options.getValueByName("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0441 TPS").get());
    }

    private boolean isValid(LivingEntity entity) {
        if (entity instanceof ClientPlayerEntity) {
            return false;
        }
        if (entity.ticksExisted < 3) {
            return false;
        }
        if (HitAura.mc.player.getDistanceEyePos(entity) > (double)this.maxRange()) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity p = (PlayerEntity)entity;
            if (AntiBot.isBot(entity)) {
                return false;
            }
            if (!((Boolean)this.targets.getValueByName("\u0414\u0440\u0443\u0437\u044c\u044f").get()).booleanValue() && FriendStorage.isFriend(p.getName().getString())) {
                return false;
            }
            if (p.getName().getString().equalsIgnoreCase(HitAura.mc.player.getName().getString())) {
                return false;
            }
        }
        if (entity instanceof PlayerEntity && !((Boolean)this.targets.getValueByName("\u0418\u0433\u0440\u043e\u043a\u0438").get()).booleanValue()) {
            return false;
        }
        if (entity instanceof PlayerEntity && entity.getTotalArmorValue() == 0 && !((Boolean)this.targets.getValueByName("\u0413\u043e\u043b\u044b\u0435").get()).booleanValue()) {
            return false;
        }
        if (entity instanceof PlayerEntity && entity.isInvisible() && entity.getTotalArmorValue() == 0 && !((Boolean)this.targets.getValueByName("\u0413\u043e\u043b\u044b\u0435 \u043d\u0435\u0432\u0438\u0434\u0438\u043c\u043a\u0438").get()).booleanValue()) {
            return false;
        }
        if (entity instanceof PlayerEntity && entity.isInvisible() && !((Boolean)this.targets.getValueByName("\u041d\u0435\u0432\u0438\u0434\u0438\u043c\u043a\u0438").get()).booleanValue()) {
            return false;
        }
        if (entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative()) {
            return false;
        }
        if ((entity instanceof MonsterEntity || entity instanceof SlimeEntity || entity instanceof VillagerEntity) && !((Boolean)this.targets.getValueByName("\u041c\u043e\u0431\u044b").get()).booleanValue()) {
            return false;
        }
        if (entity instanceof AnimalEntity && !((Boolean)this.targets.getValueByName("\u0416\u0438\u0432\u043e\u0442\u043d\u044b\u0435").get()).booleanValue()) {
            return false;
        }
        return !entity.isInvulnerable() && entity.isAlive() && !(entity instanceof ArmorStandEntity);
    }

    private void breakShieldPlayer(PlayerEntity entity) {
        if (entity.isBlocking()) {
            int invSlot = InventoryUtil.getInstance().getAxeInInventory(false);
            int hotBarSlot = InventoryUtil.getInstance().getAxeInInventory(true);
            if (hotBarSlot == -1 && invSlot != -1) {
                int bestSlot = InventoryUtil.getInstance().findBestSlotInHotBar();
                HitAura.mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, HitAura.mc.player);
                HitAura.mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, HitAura.mc.player);
                HitAura.mc.player.connection.sendPacket(new CHeldItemChangePacket(bestSlot));
                HitAura.mc.playerController.attackEntity(HitAura.mc.player, entity);
                HitAura.mc.player.swingArm(Hand.MAIN_HAND);
                HitAura.mc.player.connection.sendPacket(new CHeldItemChangePacket(HitAura.mc.player.inventory.currentItem));
                HitAura.mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, HitAura.mc.player);
                HitAura.mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, HitAura.mc.player);
            }
            if (hotBarSlot != -1) {
                HitAura.mc.player.connection.sendPacket(new CHeldItemChangePacket(hotBarSlot));
                HitAura.mc.playerController.attackEntity(HitAura.mc.player, entity);
                HitAura.mc.player.swingArm(Hand.MAIN_HAND);
                HitAura.mc.player.connection.sendPacket(new CHeldItemChangePacket(HitAura.mc.player.inventory.currentItem));
            }
        }
    }

    private void reset() {
        if (((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) {
            HitAura.mc.player.rotationYawOffset = -2.14748365E9f;
        }
        this.rotateVector = new Vector2f(HitAura.mc.player.rotationYaw, HitAura.mc.player.rotationPitch);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
        this.target = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
        this.stopWatch.setLastMS(0L);
        this.target = null;
        HitAura.mc.timer.timerSpeed = 1.0f;
    }

    public ModeSetting getType() {
        return this.type;
    }

    public ModeListSetting getOptions() {
        return this.options;
    }

    public ModeListSetting getMoreOptions() {
        return this.moreOptions;
    }

    public StopWatch getStopWatch() {
        return this.stopWatch;
    }

    public LivingEntity getTarget() {
        return this.target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }
}

