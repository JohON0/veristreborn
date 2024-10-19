/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.AttackEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.MathUtil;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="ClientTune", category=Category.Misc)
public class ClientTune
extends Module {
    public ModeSetting mode = new ModeSetting("\u0422\u0438\u043f", "\u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0438", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0438", "Pop", "Heavy", "Windows", "Slide", "Droplet");
    public SliderSetting volume = new SliderSetting("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 60.0f, 0.0f, 100.0f, 1.0f);
    public BooleanSetting other = new BooleanSetting("\u0413\u0443\u0438", true);
    public BooleanSetting hitSound = new BooleanSetting("HitSound", false);
    private final ModeSetting sound = new ModeSetting("\u0417\u0432\u0443\u043a", "bell", "bell", "metallic", "bubble", "crime", "uwu", "moan").setVisible(() -> (Boolean)this.hitSound.get());
    SliderSetting volumehitSound = new SliderSetting("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c HitSound", 35.0f, 5.0f, 100.0f, 5.0f).setVisible(() -> (Boolean)this.hitSound.get());

    public ClientTune() {
        this.addSettings(this.mode, this.volume, this.other, this.hitSound, this.sound, this.volumehitSound);
    }

    public String getFileName(boolean state) {
        switch ((String)this.mode.get()) {
            case "\u041e\u0431\u044b\u0447\u043d\u044b\u0439": {
                return state ? "enable" : "disable";
            }
            case "\u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0438": {
                return state ? "enableBubbles" : "disableBubbles";
            }
            case "Pop": {
                return state ? "popenable" : "popdisable";
            }
            case "Heavy": {
                return state ? "heavyenable" : "heavydisable";
            }
            case "Windows": {
                return state ? "winenable" : "windisable";
            }
            case "Droplet": {
                return state ? "dropletenable" : "dropletdisable";
            }
            case "Slide": {
                return state ? "slideenable" : "slidedisable";
            }
        }
        return "";
    }

    @Subscribe
    public void onPacket(AttackEvent e) {
        if (ClientTune.mc.player == null || ClientTune.mc.world == null) {
            return;
        }
        if (((Boolean)this.hitSound.get()).booleanValue()) {
            this.playSound(e.entity);
        }
    }

    public void playSound(Entity e) {
        try {
            Clip clip = AudioSystem.getClip();
            Object resourceSound = "";
            if (this.sound.is("moan")) {
                int i = MathUtil.randomInt(1, 4);
                resourceSound = "eva/sounds/moan" + i + ".wav";
            } else {
                resourceSound = "eva/sounds/" + (String)this.sound.get() + ".wav";
            }
            InputStream is = mc.getResourceManager().getResource(new ResourceLocation((String)resourceSound)).getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis);
            if (audioInputStream == null) {
                System.out.println("Sound not found!");
                return;
            }
            clip.open(audioInputStream);
            clip.start();
            FloatControl floatControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (e != null) {
                FloatControl balance = (FloatControl)clip.getControl(FloatControl.Type.BALANCE);
                Vector3d vec = e.getPositionVec().subtract(Minecraft.getInstance().player.getPositionVec());
                double yaw = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
                double delta = MathHelper.wrapDegrees(yaw - (double)ClientTune.mc.player.rotationYaw);
                if (Math.abs(delta) > 180.0) {
                    delta -= Math.signum(delta) * 360.0;
                }
                try {
                    balance.setValue((float)delta / 180.0f);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            floatControl.setValue(-(ClientTune.mc.player.getDistance(e) * 5.0f) - this.volume.max / ((Float)this.volume.get()).floatValue());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

