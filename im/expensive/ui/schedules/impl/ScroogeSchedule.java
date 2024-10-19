/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.schedules.impl;

import im.expensive.ui.schedules.Schedule;
import im.expensive.ui.schedules.TimeType;

public class ScroogeSchedule
extends Schedule {
    @Override
    public String getName() {
        return "\u0421\u043a\u0440\u0443\u0434\u0436";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.FIFTEEN_HALF};
    }
}

