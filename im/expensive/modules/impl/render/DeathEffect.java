/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.AttackEvent;
import im.expensive.events.CameraEvent;
import im.expensive.events.EventDisplay;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.utils.SoundUtil;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.render.rect.DisplayUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

@ModuleRegister(name="DeathEffect", category=Category.Render)
public class DeathEffect
extends Module {
    private Animation animate = new Animation();
    private boolean useAnimation;
    private final BooleanSetting onlyPlayer = new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", true);
    LivingEntity target;
    long time;
    public StopWatch stopWatch = new StopWatch();
    private float yaw;
    private float pitch;
    private final List<Vector3d> position = new ArrayList<Vector3d>();
    private int current;
    private Vector3d setPosition;
    public float back;
    public Vector2f last;

    public DeathEffect() {
        this.addSettings(this.onlyPlayer);
    }

    @Subscribe
    public void onPacket(AttackEvent e) {
        if (DeathEffect.mc.player == null || DeathEffect.mc.world == null) {
            return;
        }
        if (((Boolean)this.onlyPlayer.get()).booleanValue()) {
            if (e.entity instanceof PlayerEntity) {
                this.target = (LivingEntity)e.entity;
            }
        } else {
            this.target = (LivingEntity)e.entity;
        }
        this.time = System.currentTimeMillis();
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (DeathEffect.mc.player == null || DeathEffect.mc.world == null) {
            return;
        }
        IPacket<?> iPacket = e.getPacket();
        if (iPacket instanceof SDestroyEntitiesPacket) {
            SDestroyEntitiesPacket p = (SDestroyEntitiesPacket) iPacket;
//            for (Object ids : p.getEntityIDs()) {
//                if ((this.target == null) || (ids == DeathEffect.mc.player.getEntityId()) || ((this.time + 400L) < System.currentTimeMillis()) || (this.target.getEntityId() != ids) || !(((LivingEntity) DeathEffect.mc.world.getEntityByID((int) ids)).getHealth() < 5.0f)) {
//                    continue;
            this.onKill(this.target);
            this.target = null;
        }
    }

    @Subscribe
    public void onUpdate(EventMotion e) {
        if (DeathEffect.mc.player == null || DeathEffect.mc.world == null) {
            return;
        }
        if (this.useAnimation) {
            if (DeathEffect.mc.player.ticksExisted % 5 == 0) {
                ++this.current;
            }
            Vector3d player = new Vector3d(MathUtil.interpolate(DeathEffect.mc.player.getPosX(), DeathEffect.mc.player.lastTickPosX, (double)mc.getRenderPartialTicks()), MathUtil.interpolate(DeathEffect.mc.player.getPosY(), DeathEffect.mc.player.lastTickPosY, (double)mc.getRenderPartialTicks()), MathUtil.interpolate(DeathEffect.mc.player.getPosZ(), DeathEffect.mc.player.lastTickPosZ, (double)mc.getRenderPartialTicks())).add(0.0, DeathEffect.mc.player.getEyeHeight(), 0.0);
            this.position.add(player);
        }
        if (this.target != null && this.time + 1000L >= System.currentTimeMillis() && this.target.getHealth() <= 0.0f) {
            this.onKill(this.target);
            this.target = null;
        }
        if (this.stopWatch.isReached(500L)) {
            this.animate = this.animate.animate(0.0, 1.0, Easings.CIRC_OUT);
        }
        if (this.stopWatch.isReached(2000L)) {
            this.useAnimation = false;
            this.last = null;
        }
    }

    @Subscribe
    public void onCameraController(CameraEvent e) {
        if (this.useAnimation) {
            DeathEffect.mc.getRenderManager().info.setDirection((float)((double)this.yaw + 6.0 * this.animate.getValue()), (float)((double)this.pitch + 6.0 * this.animate.getValue()));
            this.back = MathUtil.fast(this.back, this.stopWatch.isReached(1000L) ? 1.0f : 0.0f, 10.0f);
            Vector3d player = new Vector3d(MathUtil.interpolate(DeathEffect.mc.player.getPosX(), DeathEffect.mc.player.lastTickPosX, (double)mc.getRenderPartialTicks()), MathUtil.interpolate(DeathEffect.mc.player.getPosY(), DeathEffect.mc.player.lastTickPosY, (double)mc.getRenderPartialTicks()), MathUtil.interpolate(DeathEffect.mc.player.getPosZ(), DeathEffect.mc.player.lastTickPosZ, (double)mc.getRenderPartialTicks())).add(0.0, DeathEffect.mc.player.getEyeHeight(), 0.0);
            if (this.setPosition != null) {
                DeathEffect.mc.getRenderManager().info.setDirection((float)MathUtil.interpolate((float)((double)this.yaw + 6.0 * this.animate.getValue()), DeathEffect.mc.player.getYaw(e.partialTicks), (double)(1.0f - this.back)), (float)MathUtil.interpolate((float)((double)this.pitch + 6.0 * this.animate.getValue()), DeathEffect.mc.player.getPitch(e.partialTicks), (double)(1.0f - this.back)));
                DeathEffect.mc.getRenderManager().info.setPosition(MathUtil.interpolate(this.setPosition, player, 1.0f - this.back));
            }
            DeathEffect.mc.getRenderManager().info.moveForward(2.0 * this.animate.getValue());
        }
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        if (DeathEffect.mc.player == null || DeathEffect.mc.world == null || e.getType() != EventDisplay.Type.POST) {
            return;
        }
        this.animate.update();
        if (this.useAnimation && this.setPosition != null && this.position.size() > 1) {
            this.setPosition = MathUtil.fast(this.setPosition, this.position.get(this.current), 1.0f);
            DisplayUtils.drawWhite((float)this.animate.getValue());
        }
    }

    public void onKill(LivingEntity entity) {
        Vector3d player;
        this.position.clear();
        this.current = 0;
        this.animate = this.animate.animate(1.0, 1.0, Easings.CIRC_OUT);
        this.useAnimation = true;
        this.stopWatch.reset();
        this.setPosition = player = new Vector3d(MathUtil.interpolate(DeathEffect.mc.player.getPosX(), DeathEffect.mc.player.lastTickPosX, (double)mc.getRenderPartialTicks()), MathUtil.interpolate(DeathEffect.mc.player.getPosY(), DeathEffect.mc.player.lastTickPosY, (double)mc.getRenderPartialTicks()), MathUtil.interpolate(DeathEffect.mc.player.getPosZ(), DeathEffect.mc.player.lastTickPosZ, (double)mc.getRenderPartialTicks())).add(0.0, DeathEffect.mc.player.getEyeHeight(), 0.0);
        SoundUtil.playSound("fragsfx.wav");
        this.yaw = DeathEffect.mc.player.getYaw(mc.getRenderPartialTicks());
        this.pitch = DeathEffect.mc.player.getPitch(mc.getRenderPartialTicks());
    }

    public void createSound() {
        String[] soundFiles = new String[]{"strikesf-1.wav", "strikesf-2.wav", "strikesf-3.wav", "strikesf-4.wav"};
        int randomIndex = MathUtil.randomInt(0, soundFiles.length - 1);
        SoundUtil.playSound(soundFiles[randomIndex]);
    }
}

