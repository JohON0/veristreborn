/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.schedules.impl;

import im.expensive.ui.schedules.Schedule;
import im.expensive.ui.schedules.TimeType;

public class AirDropSchedule
extends Schedule {
    @Override
    public String getName() {
        return "\u0410\u0438\u0440 \u0434\u0440\u043e\u043f";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.NINE, TimeType.ELEVEN, TimeType.THIRTEEN, TimeType.FIFTEEN, TimeType.SEVENTEEN, TimeType.NINETEEN, TimeType.TWENTY_ONE, TimeType.TWENTY_THREE, TimeType.ONE};
    }
}

