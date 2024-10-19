/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.config.FriendStorage;
import im.expensive.events.EventKey;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.utils.player.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;

@ModuleRegister(name="ClickFriend", category=Category.Misc)
public class ClickFriend
extends Module {
    final BindSetting throwKey = new BindSetting("\u041a\u043d\u043e\u043f\u043a\u0430", -98);

    public ClickFriend() {
        this.addSettings(this.throwKey);
    }

    @Subscribe
    public void onKey(EventKey e) {
        if (e.getKey() == ((Integer)this.throwKey.get()).intValue() && ClickFriend.mc.pointedEntity instanceof PlayerEntity) {
            if (ClickFriend.mc.player == null || ClickFriend.mc.pointedEntity == null) {
                return;
            }
            String playerName = ClickFriend.mc.pointedEntity.getName().getString();
            if (!PlayerUtils.isNameValid(playerName)) {
                this.print("\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0434\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0431\u043e\u0442\u0430 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f, \u0443\u0432\u044b, \u043a\u0430\u043a \u0431\u044b \u0432\u0430\u043c \u043d\u0435 \u0445\u043e\u0442\u0435\u043b\u043e\u0441\u044c \u044d\u0442\u043e \u0441\u0434\u0435\u043b\u0430\u0442\u044c");
                return;
            }
            if (FriendStorage.isFriend(playerName)) {
                FriendStorage.remove(playerName);
                this.printStatus(playerName, true);
            } else {
                FriendStorage.add(playerName);
                this.printStatus(playerName, false);
            }
        }
    }

    void printStatus(String name, boolean remove) {
        if (remove) {
            this.print(name + " \u0443\u0434\u0430\u043b\u0451\u043d \u0438\u0437 \u0434\u0440\u0443\u0437\u0435\u0439");
        } else {
            this.print(name + " \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u0432 \u0434\u0440\u0443\u0437\u044c\u044f");
        }
    }
}

