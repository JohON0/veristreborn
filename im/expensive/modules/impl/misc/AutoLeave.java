/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.config.FriendStorage;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.player.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

@ModuleRegister(name="AutoLeave", category=Category.Misc)
public class AutoLeave
extends Module {
    private final ModeSetting action = new ModeSetting("\u0414\u0435\u0439\u0441\u0442\u0432\u0438\u0435", "Kick", "Kick", "/hub", "/spawn", "/home");
    private final SliderSetting distance = new SliderSetting("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f", 50.0f, 1.0f, 100.0f, 1.0f);

    public AutoLeave() {
        this.addSettings(this.action, this.distance);
    }

    @Subscribe
    private void onUpdate(EventUpdate event) {
        AutoLeave.mc.world.getPlayers().stream().filter(this::isValidPlayer).findFirst().ifPresent(this::performAction);
    }

    private boolean isValidPlayer(PlayerEntity player) {
        return player.isAlive() && player.getHealth() > 0.0f && player.getDistance(AutoLeave.mc.player) <= ((Float)this.distance.get()).floatValue() && player != AutoLeave.mc.player && PlayerUtils.isNameValid(player.getName().getString()) && !FriendStorage.isFriend(player.getName().getString());
    }

    private void performAction(PlayerEntity player) {
        if (!((String)this.action.get()).equalsIgnoreCase("Kick")) {
            AutoLeave.mc.player.sendChatMessage((String)this.action.get());
            AutoLeave.mc.ingameGUI.func_238452_a_(new StringTextComponent("[AutoLeave] " + player.getGameProfile().getName()), new StringTextComponent("test"), -1, -1, -1);
        } else {
            AutoLeave.mc.player.connection.getNetworkManager().closeChannel(new StringTextComponent("\u0412\u044b \u0432\u044b\u0448\u043b\u0438 \u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430! \n" + player.getGameProfile().getName()));
        }
        this.toggle();
    }
}

