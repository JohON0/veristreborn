/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.notify;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventDisplay;
import im.expensive.modules.impl.misc.SelfDestruct;
import im.expensive.ui.notify.Notify;
import im.expensive.ui.notify.NotifyType;
import im.expensive.ui.notify.impl.NoNotify;
import im.expensive.ui.notify.impl.SuccessNotify;
import im.expensive.ui.notify.impl.WarningNotify;
import im.expensive.utils.client.IMinecraft;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class NotifyManager
extends ArrayList<Notify>
implements IMinecraft {
    public void init() {
        Expensive.getInstance().getEventBus().register(this);
    }

    public void register(String content, NotifyType type, long delay) {
        Notify notification = switch (type) {
            default -> throw new IncompatibleClassChangeError();
            case NotifyType.YES -> new SuccessNotify(content, delay);
            case NotifyType.NO -> new NoNotify(content, delay);
            case NotifyType.WARN -> new WarningNotify(content, delay);
        };
        this.add(notification);
    }

    @Subscribe
    public void onRender(EventDisplay e) {
        if (SelfDestruct.unhooked) {
            return;
        }
        if (!((Boolean)Expensive.getInstance().getModuleManager().getHud().elements.getValueByName("\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f").get()).booleanValue() || !Expensive.getInstance().getModuleManager().getHud().isState()) {
            return;
        }
        if (this.size() == 0 || NotifyManager.mc.player == null || NotifyManager.mc.world == null) {
            return;
        }
        int i = 0;
        Iterator iterator2 = this.iterator();
        try {
            while (iterator2.hasNext()) {
                Notify notification = (Notify)iterator2.next();
                notification.render(e.getMatrixStack(), i);
                if (notification.hasExpired()) {
                    iterator2.remove();
                }
                ++i;
            }
        } catch (ConcurrentModificationException concurrentModificationException) {
            // empty catch block
        }
        if (this.size() > 16) {
            this.clear();
        }
    }
}

