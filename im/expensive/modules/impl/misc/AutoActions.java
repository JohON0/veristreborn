/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.config.FriendStorage;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.utils.math.StopWatch;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;

@ModuleRegister(name="AutoActions", category=Category.Misc)
public class AutoActions
extends Module {
    public static ModeListSetting actions = new ModeListSetting("\u0414\u0435\u0439\u0441\u0442\u0432\u0438\u044f", new BooleanSetting("AutoTPAccept", false), new BooleanSetting("AutoFish", false), new BooleanSetting("AutoTool", false), new BooleanSetting("AutoRespawn", true));
    private final BooleanSetting onlyFriend = new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u0434\u0440\u0443\u0437\u044c\u044f", true).setVisible(() -> (Boolean)actions.getValueByName("AutoTPAccept").get());
    public final BooleanSetting silent = new BooleanSetting("\u041d\u0435\u0437\u0430\u043c\u0435\u0442\u043d\u044b\u0439", true).setVisible(() -> (Boolean)actions.getValueByName("AutoTool").get());
    private final String[] teleportMessages = new String[]{"has requested teleport", "\u043f\u0440\u043e\u0441\u0438\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f", "\u0445\u043e\u0447\u0435\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043a \u0432\u0430\u043c", "\u043f\u0440\u043e\u0441\u0438\u0442 \u043a \u0432\u0430\u043c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f"};
    private final StopWatch delay = new StopWatch();
    private boolean isHooked = false;
    private boolean needToHook = false;
    public int itemIndex = -1;
    public int oldSlot = -1;
    boolean status;

    public AutoActions() {
        this.addSettings(actions, this.onlyFriend);
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (AutoActions.mc.player == null || AutoActions.mc.world == null) {
            return;
        }
        if (((Boolean)actions.getValueByName("AutoTool").get()).booleanValue() && !AutoActions.mc.player.isCreative()) {
            if (this.isMousePressed()) {
                this.itemIndex = this.findBestToolSlotInHotBar();
                if (this.itemIndex != -1) {
                    this.status = true;
                    if (this.oldSlot == -1) {
                        this.oldSlot = AutoActions.mc.player.inventory.currentItem;
                    }
                    if (((Boolean)this.silent.get()).booleanValue()) {
                        AutoActions.mc.player.connection.sendPacket(new CHeldItemChangePacket(this.itemIndex));
                    } else {
                        AutoActions.mc.player.inventory.currentItem = this.itemIndex;
                    }
                }
            } else if (this.status && this.oldSlot != -1) {
                if (((Boolean)this.silent.get()).booleanValue()) {
                    AutoActions.mc.player.connection.sendPacket(new CHeldItemChangePacket(this.oldSlot));
                } else {
                    AutoActions.mc.player.inventory.currentItem = this.oldSlot;
                }
                this.itemIndex = this.oldSlot;
                this.status = false;
                this.oldSlot = -1;
            }
        }
        if (((Boolean)actions.getValueByName("AutoRespawn").get()).booleanValue() && AutoActions.mc.currentScreen instanceof DeathScreen && AutoActions.mc.player.deathTime > 5) {
            AutoActions.mc.player.respawnPlayer();
            mc.displayGuiScreen(null);
        }
        if (((Boolean)actions.getValueByName("AutoFish").get()).booleanValue()) {
            if (this.delay.isReached(600L) && this.isHooked) {
                AutoActions.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                this.isHooked = false;
                this.needToHook = true;
                this.delay.reset();
            }
            if (this.delay.isReached(300L) && this.needToHook) {
                AutoActions.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                this.needToHook = false;
                this.delay.reset();
            }
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        IPacket<IClientPlayNetHandler> p;
        IPacket<?> iPacket;
        if (AutoActions.mc.player == null || AutoActions.mc.world == null) {
            return;
        }
        if (((Boolean)actions.getValueByName("AutoFish").get()).booleanValue() && (iPacket = e.getPacket()) instanceof SPlaySoundEffectPacket && ((SPlaySoundEffectPacket)(p = (SPlaySoundEffectPacket)iPacket)).getSound().getName().getPath().equals("entity.fishing_bobber.splash")) {
            this.isHooked = true;
            this.delay.reset();
        }
        if (((Boolean)actions.getValueByName("AutoTPAccept").get()).booleanValue() && (iPacket = e.getPacket()) instanceof SChatPacket) {
            p = (SChatPacket)iPacket;
            String raw = ((SChatPacket)p).getChatComponent().getString().toLowerCase(Locale.ROOT);
            String message = TextFormatting.getTextWithoutFormattingCodes(((SChatPacket)p).getChatComponent().getString());
            if (this.isTeleportMessage(message)) {
                if (((Boolean)this.onlyFriend.get()).booleanValue()) {
                    boolean yes = false;
                    for (String friend : FriendStorage.getFriends()) {
                        if (!raw.contains(friend.toLowerCase(Locale.ROOT))) continue;
                        yes = true;
                        break;
                    }
                    if (!yes) {
                        return;
                    }
                }
                AutoActions.mc.player.sendChatMessage("/tpaccept");
            }
        }
    }

    private boolean isTeleportMessage(String message) {
        return Arrays.stream(this.teleportMessages).map(String::toLowerCase).anyMatch(message::contains);
    }

    private int findBestToolSlotInHotBar() {
        RayTraceResult rayTraceResult = AutoActions.mc.objectMouseOver;
        if (rayTraceResult instanceof BlockRayTraceResult) {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)rayTraceResult;
            Block block = AutoActions.mc.world.getBlockState(blockRayTraceResult.getPos()).getBlock();
            int bestSlot = -1;
            float bestSpeed = 1.0f;
            for (int slot = 0; slot < 9; ++slot) {
                float speed = AutoActions.mc.player.inventory.getStackInSlot(slot).getDestroySpeed(block.getDefaultState());
                if (!(speed > bestSpeed)) continue;
                bestSpeed = speed;
                bestSlot = slot;
            }
            return bestSlot;
        }
        return -1;
    }

    private boolean isMousePressed() {
        return AutoActions.mc.objectMouseOver != null && AutoActions.mc.gameSettings.keyBindAttack.isKeyDown();
    }

    @Override
    public void onDisable() {
        this.status = false;
        this.itemIndex = -1;
        this.oldSlot = -1;
        super.onDisable();
    }
}

