/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.MouseUtil;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="Spider", category=Category.Movement)
public class Spider
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Grim", "Grim", "Grim 2", "Matrix", "Elytra");
    private final SliderSetting spiderSpeed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 2.0f, 1.0f, 10.0f, 0.05f).setVisible(() -> this.mode.is("Matrix"));
    private final BooleanSetting bypass = new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u043e\u0431\u0445\u043e\u0434", true).setVisible(() -> this.mode.is("Elytra"));
    StopWatch stopWatch = new StopWatch();
    int oldItem = -1;
    int oldItem1 = -1;
    int i;
    long speed;

    public Spider() {
        this.addSettings(this.mode, this.bypass, this.spiderSpeed);
    }

    @Subscribe
    private void onMotion(EventMotion motion) {
        switch ((String)this.mode.get()) {
            case "Matrix": {
                if (!Spider.mc.player.collidedHorizontally) {
                    return;
                }
                long speed = MathHelper.clamp(500L - ((Float)this.spiderSpeed.get()).longValue() / 2L * 100L, 0L, 500L);
                if (!this.stopWatch.isReached(speed)) break;
                motion.setOnGround(true);
                Spider.mc.player.setOnGround(true);
                Spider.mc.player.collidedVertically = true;
                Spider.mc.player.collidedHorizontally = true;
                Spider.mc.player.isAirBorne = true;
                Spider.mc.player.jump();
                this.stopWatch.reset();
                break;
            }
            case "Grim": {
                int slotInHotBar = this.getSlotInInventoryOrHotbar(true);
                if (slotInHotBar == -1) {
                    this.print("\u0411\u043b\u043e\u043a\u0438 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u044b!");
                    this.toggle();
                    return;
                }
                if (!Spider.mc.player.collidedHorizontally) {
                    return;
                }
                if (Spider.mc.player.isOnGround()) {
                    motion.setOnGround(true);
                    Spider.mc.player.setOnGround(true);
                    Spider.mc.player.jump();
                }
                if (!(Spider.mc.player.fallDistance > 0.0f) || !(Spider.mc.player.fallDistance < 2.0f)) break;
                this.placeBlocks(motion, slotInHotBar);
                break;
            }
            case "Grim 2": {
                if (Spider.mc.player.isOnGround()) break;
                this.speed = (long)MathHelper.clamp(500.0f - ((Float)this.spiderSpeed.get()).floatValue() / 2.0f * 100.0f, 0.0f, 500.0f);
                if (!this.stopWatch.isReached(this.speed)) break;
                Spider.mc.gameSettings.keyBindSneak.setPressed(true);
                motion.setOnGround(true);
                Spider.mc.player.setOnGround(true);
                Spider.mc.player.collidedVertically = true;
                Spider.mc.player.collidedHorizontally = true;
                Spider.mc.player.isAirBorne = true;
                if (Spider.mc.player.fallDistance != 0.0f) {
                    Spider.mc.gameSettings.keyBindForward.setPressed(true);
                    Spider.mc.gameSettings.keyBindForward.setPressed(false);
                }
                Spider.mc.player.jump();
                Spider.mc.gameSettings.keyBindSneak.setPressed(false);
                this.stopWatch.reset();
                break;
            }
            case "Elytra": {
                motion.setPitch(0.0f);
                Spider.mc.player.rotationPitchHead = 0.0f;
            }
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        switch ((String)this.mode.get()) {
            case "Elytra": {
                if (!((Boolean)this.bypass.get()).booleanValue()) {
                    this.i = 0;
                    while (this.i < 9) {
                        if (Spider.mc.player.inventory.getStackInSlot(this.i).getItem() == Items.ELYTRA && !Spider.mc.player.isOnGround() && Spider.mc.player.collidedHorizontally && Spider.mc.player.fallDistance == 0.0f) {
                            Spider.mc.playerController.windowClick(0, 6, this.i, ClickType.SWAP, Spider.mc.player);
                            Spider.mc.player.connection.sendPacket(new CEntityActionPacket(Spider.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                            MoveUtils.setMotion(0.06);
                            Spider.mc.player.motion.y = 0.366;
                            Spider.mc.playerController.windowClick(0, 6, this.i, ClickType.SWAP, Spider.mc.player);
                            this.oldItem = this.i;
                        }
                        ++this.i;
                    }
                    break;
                }
                if (Spider.mc.player.inventory.armorInventory.get(2).getItem() != Items.ELYTRA && Spider.mc.player.collidedHorizontally) {
                    this.i = 0;
                    while (this.i < 9) {
                        if (Spider.mc.player.inventory.getStackInSlot(this.i).getItem() == Items.ELYTRA) {
                            Spider.mc.playerController.windowClick(0, 6, this.i, ClickType.SWAP, Spider.mc.player);
                            this.oldItem1 = this.i;
                            this.stopWatch.reset();
                        }
                        ++this.i;
                    }
                }
                if (Spider.mc.player.collidedHorizontally) {
                    Spider.mc.gameSettings.keyBindJump.setPressed(false);
                    if (this.stopWatch.isReached(180L)) {
                        Spider.mc.gameSettings.keyBindJump.setPressed(true);
                    }
                }
                if (Spider.mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA && !Spider.mc.player.collidedHorizontally && this.oldItem1 != -1) {
                    Spider.mc.playerController.windowClick(0, 6, this.oldItem1, ClickType.SWAP, Spider.mc.player);
                    this.oldItem1 = -1;
                }
                if (Spider.mc.player.inventory.armorInventory.get(2).getItem() != Items.ELYTRA || Spider.mc.player.isOnGround() || !Spider.mc.player.collidedHorizontally) break;
                if (Spider.mc.player.fallDistance != 0.0f) {
                    return;
                }
                Spider.mc.player.connection.sendPacket(new CEntityActionPacket(Spider.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                MoveUtils.setMotion(0.02);
                Spider.mc.player.motion.y = 0.36;
            }
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        switch ((String)this.mode.get()) {
            case "Elytra": {
                if (!((Boolean)this.bypass.get()).booleanValue()) {
                    IPacket<?> var4 = e.getPacket();
                    if (var4 instanceof SPlayerPositionLookPacket) {
                        SPlayerPositionLookPacket p = (SPlayerPositionLookPacket)var4;
                        Spider.mc.player.func_242277_a(new Vector3d(p.getX(), p.getY(), p.getZ()));
                        Spider.mc.player.setRawPosition(p.getX(), p.getY(), p.getZ());
                        return;
                    }
                    if (!(e.getPacket() instanceof SEntityMetadataPacket) || ((SEntityMetadataPacket)e.getPacket()).getEntityId() != Spider.mc.player.getEntityId()) break;
                    e.cancel();
                    break;
                }
                if (!(e.getPacket() instanceof SEntityMetadataPacket) || ((SEntityMetadataPacket)e.getPacket()).getEntityId() != Spider.mc.player.getEntityId()) break;
                e.cancel();
            }
        }
    }

    private void placeBlocks(EventMotion motion, int block) {
        int last = Spider.mc.player.inventory.currentItem;
        Spider.mc.player.inventory.currentItem = block;
        motion.setPitch(80.0f);
        motion.setYaw(Spider.mc.player.getHorizontalFacing().getHorizontalAngle());
        BlockRayTraceResult r = (BlockRayTraceResult)MouseUtil.rayTrace(4.0, motion.getYaw(), motion.getPitch(), Spider.mc.player);
        Spider.mc.player.swingArm(Hand.MAIN_HAND);
        Spider.mc.playerController.processRightClickBlock(Spider.mc.player, Spider.mc.world, Hand.MAIN_HAND, r);
        Spider.mc.player.inventory.currentItem = last;
        Spider.mc.player.fallDistance = 0.0f;
    }

    public int getSlotInInventoryOrHotbar(boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        int finalSlot = -1;
        for (int i = firstSlot; i < lastSlot; ++i) {
            if (Spider.mc.player.inventory.getStackInSlot(i).getItem() == Items.TORCH || !(Spider.mc.player.inventory.getStackInSlot(i).getItem() instanceof BlockItem) && Spider.mc.player.inventory.getStackInSlot(i).getItem() != Items.WATER_BUCKET) continue;
            finalSlot = i;
        }
        return finalSlot;
    }
}

