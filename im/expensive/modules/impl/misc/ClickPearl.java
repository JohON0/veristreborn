/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventKey;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.combat.ItemCooldown;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.player.InventoryUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;

@ModuleRegister(name="ClickPearl", category=Category.Misc)
public class ClickPearl
extends Module {
    private final BindSetting pearlKey = new BindSetting("\u041a\u043d\u043e\u043f\u043a\u0430", -98);
    private final InventoryUtil.Hand handUtil = new InventoryUtil.Hand();
    private final ItemCooldown itemCooldown;
    private long delay;
    private final StopWatch wait = new StopWatch();

    public ClickPearl(ItemCooldown itemCooldown) {
        this.itemCooldown = itemCooldown;
        this.addSettings(this.pearlKey);
    }

    @Subscribe
    public void onKey(EventKey e) {
        if (e.getKey() == ((Integer)this.pearlKey.get()).intValue() && !ClickPearl.mc.player.getCooldownTracker().hasCooldown(Items.ENDER_PEARL)) {
            KeyBinding[] pressedKeys = new KeyBinding[]{ClickPearl.mc.gameSettings.keyBindForward, ClickPearl.mc.gameSettings.keyBindBack, ClickPearl.mc.gameSettings.keyBindLeft, ClickPearl.mc.gameSettings.keyBindRight, ClickPearl.mc.gameSettings.keyBindJump, ClickPearl.mc.gameSettings.keyBindSprint};
            if (ClientUtil.isConnectedToServer("funtime") && !this.wait.isReached(400L)) {
                for (KeyBinding keyBinding : pressedKeys) {
                    keyBinding.setPressed(false);
                }
                return;
            }
            if (Expensive.getInstance().getModuleManager().getHitAura().getTarget() != null) {
                ClickPearl.mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(ClickPearl.mc.player.rotationYaw, ClickPearl.mc.player.rotationPitch, ClickPearl.mc.player.isOnGround()));
            }
            InventoryUtil.inventorySwapClick(Items.ENDER_PEARL, true);
        }
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        this.handUtil.onEventPacket(e);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}

