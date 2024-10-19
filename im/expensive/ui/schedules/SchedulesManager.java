/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.schedules;

import im.expensive.ui.schedules.Schedule;
import im.expensive.ui.schedules.impl.AirDropSchedule;
import im.expensive.ui.schedules.impl.CompetitionSchedule;
import im.expensive.ui.schedules.impl.MascotSchedule;
import im.expensive.ui.schedules.impl.ScroogeSchedule;
import im.expensive.ui.schedules.impl.SecretMerchantSchedule;
import im.expensive.utils.client.IMinecraft;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchedulesManager
implements IMinecraft {
    private final List<Schedule> schedules = new ArrayList<Schedule>();

    public SchedulesManager() {
        this.schedules.addAll(Arrays.asList(new AirDropSchedule(), new ScroogeSchedule(), new SecretMerchantSchedule(), new MascotSchedule(), new CompetitionSchedule()));
    }

    public List<Schedule> getSchedules() {
        return this.schedules;
    }
}

