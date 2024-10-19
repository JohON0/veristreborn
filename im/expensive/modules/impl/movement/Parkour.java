/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.utils.player.MoveUtils;

@ModuleRegister(name="Parkour", category=Category.Movement)
public class Parkour
extends Module {
    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (MoveUtils.isBlockUnder(0.001f) && Parkour.mc.player.isOnGround()) {
            Parkour.mc.player.jump();
        }
    }
}

