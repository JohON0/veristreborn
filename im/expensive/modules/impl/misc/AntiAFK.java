/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.utils.player.MoveUtils;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomStringUtils;

@ModuleRegister(name="AntiAFK", category=Category.Misc)
public class AntiAFK
extends Module {
    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (MoveUtils.isMoving()) {
            return;
        }
        if (AntiAFK.mc.player.ticksExisted % 200 != 0) {
            return;
        }
        if (AntiAFK.mc.player.isOnGround()) {
            AntiAFK.mc.player.jump();
        }
        AntiAFK.mc.player.rotationYaw += ThreadLocalRandom.current().nextFloat(-10.0f, 10.0f);
        AntiAFK.mc.player.sendChatMessage("/" + RandomStringUtils.randomAlphabetic(5));
    }
}

