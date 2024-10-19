/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventPacket;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import net.minecraft.network.play.server.SExplosionPacket;

@ModuleRegister(name="AntiPush", category=Category.Misc)
public class AntiPush
extends Module {
    public static ModeListSetting modes = new ModeListSetting("\u0422\u0438\u043f", new BooleanSetting("\u0418\u0433\u0440\u043e\u043a\u0438", true), new BooleanSetting("\u0412\u043e\u0434\u0430", false), new BooleanSetting("\u0412\u0437\u0440\u044b\u0432\u044b", false), new BooleanSetting("\u0411\u043b\u043e\u043a\u0438", true));

    public AntiPush() {
        this.addSettings(modes);
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (e.isReceive() && ((Boolean)modes.getValueByName("\u0412\u0437\u0440\u044b\u0432\u044b").get()).booleanValue() && e.getPacket() instanceof SExplosionPacket) {
            e.cancel();
        }
    }
}

