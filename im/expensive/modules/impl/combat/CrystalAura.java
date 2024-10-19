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
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.AntiBot;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import im.expensive.utils.player.MoveUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.render.RenderUtils;

@ModuleRegister(name="CrystalAura", category=Category.Combat)
public class CrystalAura
extends Module {
    public final ModeListSetting options = new ModeListSetting("\u041e\u043f\u0446\u0438\u0438", new BooleanSetting("\u041d\u0435 \u0432\u0437\u0440\u044b\u0432\u0430\u0442\u044c \u0441\u0435\u0431\u044f", true), new BooleanSetting("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f", false), new BooleanSetting("\u0421\u0442\u0430\u0432\u0438\u0442\u044c \u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b", true), new BooleanSetting("\u0420\u043e\u0442\u0430\u0446\u0438\u044f", true), new BooleanSetting("\u0412\u0438\u0437\u0443\u0430\u043b\u0438\u0437\u0430\u0446\u0438\u044f", true));
    private final ModeSetting distanceMode = new ModeSetting("\u0422\u0438\u043f \u0440\u0430\u0434\u0438\u0443\u0441\u0430", "Vanilla", "Vanilla", "Custom");
    private final SliderSetting customDistance = new SliderSetting("\u0420\u0430\u0434\u0438\u0443\u0441", 5.0f, 2.5f, 6.0f, 0.05f).setVisible(() -> this.distanceMode.is("Custom"));
    private final SliderSetting customUp = new SliderSetting("\u0412\u0432\u0435\u0440\u0445", 2.0f, 1.0f, 6.0f, 0.05f);
    private final SliderSetting customDown = new SliderSetting("\u0412\u043d\u0438\u0437", 2.0f, 1.0f, 6.0f, 0.05f);
    private final SliderSetting breakDelay = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 (\u043c\u0441)", 100.0f, 0.0f, 500.0f, 1.0f);
    private Entity crystalTarget = null;
    public Vector2f rotate = new Vector2f(0.0f, 0.0f);
    private Vector3d obsidianVec = new Vector3d(0.0, 0.0, 0.0);
    private BlockPos closestObsidian = null;
    private Entity closestCrystal;
    private List<BlockPos> obsidianPositions = new ArrayList<BlockPos>();
    private StopWatch stopWatch = new StopWatch();
    private boolean crystalAttack = false;

    public CrystalAura() {
        this.addSettings(this.options, this.distanceMode, this.customDistance, this.customUp, this.customDown, this.breakDelay);
    }

    double distance() {
        return this.distanceMode.is("Vanilla") ? (double)CrystalAura.mc.playerController.getBlockReachDistance() : (double)((Float)this.customDistance.get()).floatValue();
    }

    public boolean check() {
        return (this.crystalTarget != null || this.closestObsidian != null) && this.rotate != null && (Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get() != false && (Boolean)this.options.getValueByName("\u0420\u043e\u0442\u0430\u0446\u0438\u044f").get() != false;
    }

    @Override
    public void onDisable() {
        this.reset();
        super.onDisable();
    }

    @Subscribe
    public void onRender3D(WorldEvent e) {
        if (((Boolean)this.options.getValueByName("\u0412\u0438\u0437\u0443\u0430\u043b\u0438\u0437\u0430\u0446\u0438\u044f").get()).booleanValue() && this.obsidianVec != null) {
            RenderUtils.drawBlockBox(new BlockPos(this.obsidianVec.getX(), this.obsidianVec.getY(), this.obsidianVec.getZ()), Theme.mainRectColor);
        }
    }

    @Subscribe
    public void onMoveInput(EventInput e) {
        if (this.check()) {
            MoveUtils.fixMovement(e, this.rotate.x);
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        this.findAndAttackCrystal();
        this.findAndClickObsidian();
    }

    private void findAndAttackCrystal() {
        this.closestCrystal = null;
        double closestDistanceToTarget = Double.MAX_VALUE;
        double maxDistanceToCrystal = this.distance();
        if (!((Boolean)this.options.getValueByName("\u0421\u0442\u0430\u0432\u0438\u0442\u044c \u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b").get()).booleanValue()) {
            this.crystalAttack = true;
        }
        for (Entity entity : CrystalAura.mc.world.getAllEntities()) {
            EnderCrystalEntity enderCrystal;
            double distanceToCrystal;
            if (!(entity instanceof EnderCrystalEntity) || (distanceToCrystal = (double)CrystalAura.mc.player.getDistance(enderCrystal = (EnderCrystalEntity)entity)) > maxDistanceToCrystal || CrystalAura.mc.player.getPosY() >= enderCrystal.getPosY() && ((Boolean)this.options.getValueByName("\u041d\u0435 \u0432\u0437\u0440\u044b\u0432\u0430\u0442\u044c \u0441\u0435\u0431\u044f").get()).booleanValue() || !(distanceToCrystal < closestDistanceToTarget)) continue;
            closestDistanceToTarget = distanceToCrystal;
            this.closestCrystal = enderCrystal;
        }
        if (this.closestCrystal != null && this.crystalAttack) {
            this.crystalTarget = this.closestCrystal;
            if (this.stopWatch.isReached(((Float)this.breakDelay.get()).longValue())) {
                CrystalAura.mc.playerController.attackEntity(CrystalAura.mc.player, this.closestCrystal);
                CrystalAura.mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
                this.crystalTarget = null;
                this.stopWatch.reset();
            }
        } else {
            this.reset();
        }
    }

    private void findAndClickObsidian() {
        int crystal = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(Items.END_CRYSTAL, true);
        if (crystal == -1 || !((Boolean)this.options.getValueByName("\u0421\u0442\u0430\u0432\u0438\u0442\u044c \u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b").get()).booleanValue()) {
            return;
        }
        double closestDistanceToTarget = Double.MAX_VALUE;
        double maxDistanceToObsidian = this.distance() * 2.0;
        this.closestObsidian = null;
        this.obsidianPositions.clear();
        this.crystalAttack = false;
        for (Entity entity : CrystalAura.mc.world.getAllEntities()) {
            if (entity == CrystalAura.mc.player || FriendStorage.isFriend(entity.getName().getString()) || entity instanceof EnderCrystalEntity || entity instanceof ArrowEntity || entity instanceof ProjectileItemEntity || entity instanceof ItemEntity || entity instanceof ThrowableEntity || entity instanceof FallingBlockEntity) continue;
            int x = (int)(-this.distance());
            while ((double)x <= this.distance()) {
                int z = (int)(-this.distance());
                while ((double)z <= this.distance()) {
                    for (int y = -((Float)this.customDown.get()).intValue(); y <= ((Float)this.customUp.get()).intValue(); ++y) {
                        double distanceToTarget;
                        double distanceToPlayer;
                        Block blockAbove;
                        BlockPos pos = new BlockPos(entity.getPosX() + (double)x, entity.getPosY() - 0.5 + (double)y, entity.getPosZ() + (double)z);
                        if (CrystalAura.mc.world.getBlockState(pos).getBlock() != Blocks.OBSIDIAN || !((blockAbove = CrystalAura.mc.world.getBlockState(pos.up()).getBlock()) instanceof AirBlock) || (double)pos.getY() < CrystalAura.mc.player.getPosY() && ((Boolean)this.options.getValueByName("\u041d\u0435 \u0432\u0437\u0440\u044b\u0432\u0430\u0442\u044c \u0441\u0435\u0431\u044f").get()).booleanValue() && !CrystalAura.mc.player.isCreative() || !entity.isAlive() || AntiBot.isBot(entity) || entity.getPosition().equals(pos.up()) || CrystalAura.mc.player.getPosition().equals(pos.up()) || entity.getPosY() - 0.5 < (double)pos.getY() || (distanceToPlayer = CrystalAura.mc.player.getDistanceSq(Vector3d.copyCentered(pos))) > maxDistanceToObsidian || !((distanceToTarget = entity.getDistanceSq(Vector3d.copyCentered(pos))) < closestDistanceToTarget)) continue;
                        closestDistanceToTarget = distanceToTarget;
                        this.closestObsidian = pos;
                        this.obsidianPositions.clear();
                        this.obsidianPositions.add(this.closestObsidian);
                        if (!(entity.getPosY() + 0.5 > (double)pos.getY()) || !Expensive.getInstance().getModuleManager().getHitAura().isState() || Expensive.getInstance().getModuleManager().getHitAura().getTarget() == null || !Expensive.getInstance().getModuleManager().getHitAura().canWork) continue;
                        Expensive.getInstance().getModuleManager().getHitAura().canWork = false;
                        Expensive.getInstance().getModuleManager().getHitAura().setTarget(null);
                    }
                    ++z;
                }
                ++x;
            }
        }
        if (!this.obsidianPositions.isEmpty()) {
            int last = CrystalAura.mc.player.inventory.currentItem;
            CrystalAura.mc.player.inventory.currentItem = crystal;
            this.obsidianVec = new Vector3d((double)this.closestObsidian.getX() + 0.5, (double)this.closestObsidian.getY() + 0.5, (double)this.closestObsidian.getZ() + 0.5);
            BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(this.obsidianVec, Direction.UP, this.closestObsidian, false);
            CrystalAura.mc.playerController.processRightClickBlock(CrystalAura.mc.player, CrystalAura.mc.world, Hand.MAIN_HAND, rayTraceResult);
            CrystalAura.mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
            this.crystalAttack = true;
            CrystalAura.mc.player.inventory.currentItem = last;
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (this.obsidianVec != null && this.crystalTarget == null && ((Boolean)this.options.getValueByName("\u0420\u043e\u0442\u0430\u0446\u0438\u044f").get()).booleanValue()) {
            this.rotate = MathUtil.rotationToVec(this.obsidianVec);
            this.applyRotation(e, this.rotate);
        } else if (this.closestCrystal != null && ((Boolean)this.options.getValueByName("\u0420\u043e\u0442\u0430\u0446\u0438\u044f").get()).booleanValue()) {
            this.rotate = MathUtil.rotationToEntity(this.closestCrystal);
            this.applyRotation(e, this.rotate);
        }
    }

    private void applyRotation(EventMotion e, Vector2f rotate) {
        e.setYaw(rotate.x);
        e.setPitch(rotate.y);
        CrystalAura.mc.player.renderYawOffset = rotate.x;
        CrystalAura.mc.player.rotationYawHead = rotate.x;
        CrystalAura.mc.player.rotationPitchHead = rotate.y;
        if (((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) {
            CrystalAura.mc.player.rotationYawOffset = rotate.x;
        }
    }

    public void reset() {
        this.closestObsidian = null;
        this.closestCrystal = null;
        this.crystalTarget = null;
        this.obsidianVec = null;
        this.obsidianPositions.clear();
        this.crystalAttack = false;
        Expensive.getInstance().getModuleManager().getHitAura().canWork = true;
        if (((Boolean)this.options.getValueByName("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").get()).booleanValue()) {
            CrystalAura.mc.player.rotationYawOffset = -2.14748365E9f;
        }
    }
}

