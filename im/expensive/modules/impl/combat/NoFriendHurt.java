/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.config.FriendStorage;
import im.expensive.events.EventPacket;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CUseEntityPacket;

@ModuleRegister(name="NoFriendHurt", category=Category.Combat)
public class NoFriendHurt
extends Module {
    @Subscribe
    public void onEvent(EventPacket event) {
        CUseEntityPacket cUseEntityPacket;
        Entity entity;
        if (event.getPacket() instanceof CUseEntityPacket && (entity = (cUseEntityPacket = (CUseEntityPacket)event.getPacket()).getEntityFromWorld(NoFriendHurt.mc.world)) instanceof RemoteClientPlayerEntity && FriendStorage.isFriend(entity.getName().getString()) && cUseEntityPacket.getAction() == CUseEntityPacket.Action.ATTACK) {
            event.cancel();
        }
    }
}

