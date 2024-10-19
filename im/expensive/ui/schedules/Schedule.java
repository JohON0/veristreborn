/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.schedules;

import im.expensive.ui.schedules.TimeType;

public abstract class Schedule {
    public abstract String getName();

    public abstract TimeType[] getTimes();
}

