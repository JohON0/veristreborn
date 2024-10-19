/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.api;

import im.expensive.Expensive;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.ModuleManager;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.impl.misc.ClientTune;
import im.expensive.modules.settings.Setting;
import im.expensive.ui.notify.impl.NoNotify;
import im.expensive.ui.notify.impl.SuccessNotify;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.client.IMinecraft;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.text.TextFormatting;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

public abstract class Module
implements IMinecraft {
    private final String name;
    private final String desc;
    private final Category category;
    private boolean state;
    private int bind;
    private final List<Setting<?>> settings = new ObjectArrayList();
    private final Animation animation = new Animation();

    public Module() {
        this.name = this.getClass().getAnnotation(ModuleRegister.class).name();
        this.desc = this.getClass().getAnnotation(ModuleRegister.class).desc();
        this.category = this.getClass().getAnnotation(ModuleRegister.class).category();
        this.bind = this.getClass().getAnnotation(ModuleRegister.class).key();
    }

    public Module(String name, String desc, Category category) {
        this.name = name;
        this.category = category;
        this.desc = desc;
    }

    public void addSettings(Setting<?> ... settings) {
        this.settings.addAll(List.of(settings));
    }

    public List<Setting<?>> getAllValues() {
        ArrayList allValues = new ArrayList();
        this.settings.forEach(value -> allValues.add((Setting)((Object)this.settings)));
        return allValues;
    }

    public void onEnable() {
        this.animation.animate(1.0, 0.25, Easings.CIRC_OUT);
        Expensive.getInstance().getEventBus().register(this);
    }

    public void onDisable() {
        this.animation.animate(0.0, 0.25, Easings.CIRC_OUT);
        Expensive.getInstance().getEventBus().unregister(this);
    }

    public final void toggle() {
        this.setState(!this.state, false);
    }

    public final void setState(boolean newState, boolean config) {
        if (this.state == newState) {
            return;
        }
        this.state = newState;
        try {
            ModuleManager moduleManager;
            ClientTune clientTune;
            if (this.state) {
                this.onEnable();
                Expensive.getInstance().getNotifyManager().add(0, new SuccessNotify(this.name + " | " + TextFormatting.GREEN + "enabled", 1000L));
            } else {
                this.onDisable();
                Expensive.getInstance().getNotifyManager().add(0, new NoNotify(this.name + " | " + TextFormatting.RED + "disabled", 1000L));
            }
            if (!config && (clientTune = (moduleManager = Expensive.getInstance().getModuleManager()).getClientTune()) != null && clientTune.isState()) {
                String fileName = clientTune.getFileName(this.state);
                float volume = ((Float)clientTune.volume.get()).floatValue();
                ClientUtil.playSound(fileName, volume, false);
            }
        } catch (Exception e) {
            this.handleException(this.state ? "onEnable" : "onDisable", e);
        }
    }

    private void handleException(String methodName, Exception e) {
        if (Module.mc.player != null) {
            this.print("[" + this.name + "] \u041f\u0440\u043e\u0438\u0437\u043e\u0448\u043b\u0430 \u043e\u0448\u0438\u0431\u043a\u0430 \u0432 \u043c\u0435\u0442\u043e\u0434\u0435 " + TextFormatting.RED + methodName + TextFormatting.WHITE + "() \u041f\u0440\u0435\u0434\u043e\u0441\u0442\u0430\u0432\u044c\u0442\u0435 \u044d\u0442\u043e \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u0440\u0430\u0437\u0440\u0430\u0431\u043e\u0442\u0447\u0438\u043a\u0443 (discord: remila.x): " + TextFormatting.GRAY + e.getMessage());
            e.printStackTrace();
        } else {
            System.out.println("[" + this.name + " Error" + methodName + "() Message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isState() {
        return this.state;
    }

    public int getBind() {
        return this.bind;
    }

    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }
}

