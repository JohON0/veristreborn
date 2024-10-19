/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import java.util.UUID;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

@ModuleRegister(name="FakePlayer", category=Category.Misc)
public class FakePlayer
extends Module {
    private boolean spawn = false;
    private RemoteClientPlayerEntity fakePlayer;

    @Subscribe
    private void onUpdate(EventUpdate e) {
        this.spawn = true;
    }

    private void spawnFakePlayer() {
        UUID var1 = UUID.nameUUIDFromBytes("1337".getBytes());
        this.fakePlayer = new RemoteClientPlayerEntity(FakePlayer.mc.world, new GameProfile(var1, "Verist Bot"));
        this.fakePlayer.copyLocationAndAnglesFrom(FakePlayer.mc.player);
        this.fakePlayer.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
        this.fakePlayer.renderYawOffset = FakePlayer.mc.player.renderYawOffset;
        this.fakePlayer.rotationPitchHead = FakePlayer.mc.player.rotationPitchHead;
        this.fakePlayer.container = FakePlayer.mc.player.container;
        this.fakePlayer.inventory = FakePlayer.mc.player.inventory;
        this.fakePlayer.setHealth(1337.0f);
        FakePlayer.mc.world.addEntity(1337, this.fakePlayer);
    }

    @Override
    public void onDisable() {
        this.removeFakePlayer();
        this.spawn = false;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        this.spawnFakePlayer();
        super.onEnable();
    }

    protected float[] rotations(PlayerEntity player) {
        return new float[0];
    }

    private void removeFakePlayer() {
        FakePlayer.mc.world.removeEntityFromWorld(1337);
    }
}

