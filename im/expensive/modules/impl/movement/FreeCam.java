/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventLivingUpdate;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.player.MoveUtils;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.screen.DownloadTerrainScreen;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="FreeCam", category=Category.Movement)
public class FreeCam
extends Module {
    private final SliderSetting speed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e XZ", 1.0f, 0.1f, 5.0f, 0.05f);
    private final SliderSetting motionY = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e Y", 0.5f, 0.1f, 1.0f, 0.05f);
    private Vector3d clientPosition = null;
    private RemoteClientPlayerEntity fakePlayer;

    public FreeCam() {
        this.addSettings(this.speed, this.motionY);
    }

    @Subscribe
    public void onLivingUpdate(EventLivingUpdate e) {
        if (FreeCam.mc.player != null) {
            FreeCam.mc.player.noClip = true;
            FreeCam.mc.player.setOnGround(false);
            MoveUtils.setMotion(((Float)this.speed.get()).floatValue());
            if (FreeCam.mc.gameSettings.keyBindJump.isKeyDown()) {
                FreeCam.mc.player.motion.y = ((Float)this.motionY.get()).floatValue();
            }
            if (FreeCam.mc.gameSettings.keyBindSneak.isKeyDown()) {
                FreeCam.mc.player.motion.y = -((Float)this.motionY.get()).floatValue();
            }
            FreeCam.mc.player.abilities.isFlying = true;
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (FreeCam.mc.player.ticksExisted % 10 == 0) {
            FreeCam.mc.player.connection.sendPacket(new CPlayerPacket(FreeCam.mc.player.isOnGround()));
        }
        if (FreeCam.mc.player != null) {
            e.cancel();
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (FreeCam.mc.player != null && FreeCam.mc.world != null && !(FreeCam.mc.currentScreen instanceof DownloadTerrainScreen) && e.isReceive()) {
            SEntityVelocityPacket sEntityVelocityPacket;
            IPacket<?> iPacket;
            if (e.getPacket() instanceof SConfirmTransactionPacket || (iPacket = e.getPacket()) instanceof SEntityVelocityPacket && (sEntityVelocityPacket = (SEntityVelocityPacket)iPacket).getEntityID() == FreeCam.mc.player.getEntityId()) {
                e.cancel();
            } else {
                iPacket = e.getPacket();
                if (iPacket instanceof SPlayerPositionLookPacket) {
                    SPlayerPositionLookPacket packet = (SPlayerPositionLookPacket)iPacket;
                    if (this.fakePlayer != null) {
                        this.fakePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
                    }
                    e.cancel();
                }
            }
            if (e.getPacket() instanceof SRespawnPacket) {
                FreeCam.mc.player.abilities.isFlying = false;
                if (this.clientPosition != null) {
                    FreeCam.mc.player.setPositionAndRotation(this.clientPosition.x, this.clientPosition.y, this.clientPosition.z, FreeCam.mc.player.rotationYaw, FreeCam.mc.player.rotationPitch);
                }
                this.removeFakePlayer();
                FreeCam.mc.player.motion = Vector3d.ZERO;
            }
        }
    }

    @Override
    public void onEnable() {
        if (FreeCam.mc.player == null) {
            return;
        }
        this.clientPosition = FreeCam.mc.player.getPositionVec();
        this.spawnFakePlayer();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (FreeCam.mc.player == null) {
            return;
        }
        FreeCam.mc.player.abilities.isFlying = false;
        if (this.clientPosition != null) {
            FreeCam.mc.player.setPositionAndRotation(this.clientPosition.x, this.clientPosition.y, this.clientPosition.z, FreeCam.mc.player.rotationYaw, FreeCam.mc.player.rotationPitch);
        }
        this.removeFakePlayer();
        FreeCam.mc.player.motion = Vector3d.ZERO;
        super.onDisable();
    }

    private void spawnFakePlayer() {
        this.fakePlayer = new RemoteClientPlayerEntity(FreeCam.mc.world, FreeCam.mc.player.getGameProfile());
        this.fakePlayer.copyLocationAndAnglesFrom(FreeCam.mc.player);
        this.fakePlayer.rotationYawHead = FreeCam.mc.player.rotationYawHead;
        this.fakePlayer.renderYawOffset = FreeCam.mc.player.renderYawOffset;
        this.fakePlayer.rotationPitchHead = FreeCam.mc.player.rotationPitchHead;
        this.fakePlayer.container = FreeCam.mc.player.container;
        this.fakePlayer.inventory = FreeCam.mc.player.inventory;
        FreeCam.mc.world.addEntity(1337, this.fakePlayer);
    }

    private void removeFakePlayer() {
        FreeCam.mc.world.removeEntityFromWorld(1337);
    }
}

