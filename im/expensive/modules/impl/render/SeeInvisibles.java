/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import net.minecraft.entity.player.PlayerEntity;

@ModuleRegister(name="SeeInvisibles", category=Category.Render)
public class SeeInvisibles
extends Module {
    @Subscribe
    private void onUpdate(EventUpdate e) {
        for (PlayerEntity playerEntity : SeeInvisibles.mc.world.getPlayers()) {
            if (playerEntity != SeeInvisibles.mc.player && playerEntity.isInvisible()) {
                playerEntity.setInvisible(false);
            }
            if (playerEntity == SeeInvisibles.mc.player || !playerEntity.isGlowing()) continue;
            playerEntity.setGlowing(false);
        }
    }
}

