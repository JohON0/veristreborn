/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.events.EventDisplay;
import im.expensive.events.EventUpdate;
import im.expensive.ui.display.ElementRenderer;
import im.expensive.ui.display.ElementUpdater;
import im.expensive.ui.schedules.Schedule;
import im.expensive.ui.schedules.SchedulesManager;
import im.expensive.ui.schedules.TimeType;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.animations.easing.CompactAnimation;
import im.expensive.utils.animations.easing.Easing;
import im.expensive.utils.drag.Dragging;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

public class SchedulesRenderer
implements ElementRenderer,
ElementUpdater {
    private final Dragging dragging;
    private float width;
    private float height;
    private final CompactAnimation widthAnimation = new CompactAnimation(Easing.EASE_OUT_QUART, 100L);
    private final CompactAnimation heightAnimation = new CompactAnimation(Easing.EASE_OUT_QUART, 100L);
    private final SchedulesManager schedulesManager = new SchedulesManager();
    private final TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
    private List<Schedule> activeSchedules = new ArrayList<Schedule>();
    private static final int MINUTES_IN_DAY = 1440;
    private boolean sorted = false;

    @Override
    public void update(EventUpdate e) {
        this.activeSchedules = this.schedulesManager.getSchedules();
        if (!this.sorted) {
            this.activeSchedules.sort(Comparator.comparingInt(schedule -> (int)(-Fonts.montserrat.getWidth(schedule.getName(), 6.5f))));
            this.sorted = true;
        }
    }

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();
        float posX = this.dragging.getX();
        float posY = this.dragging.getY();
        float fontSize = 6.5f;
        float padding = 5.0f;
        String name = "Schedules";
        DisplayUtils.drawStyledRect(posX, posY, this.width, this.height);
        int textColor = Theme.textColor;
        ClientFonts.icons_nur[20].drawString(ms, "G", (double)(posX + 5.0f), (double)(posY + 5.5f), textColor);
        Fonts.montserrat.drawText(ms, name, posX + 10.0f + 8.0f, posY + padding + 0.5f, textColor, fontSize, 0.07f);
        DisplayUtils.drawRectHorizontalW(posX, posY + 17.0f, this.width, 2.5, ColorUtils.rgba(0, 0, 0, 0), ColorUtils.rgba(0, 0, 0, 63));
        float maxWidth = Fonts.montserrat.getWidth(name, fontSize) + padding * 2.0f;
        float localHeight = fontSize + padding * 2.0f;
        posY += fontSize + padding + 2.0f;
        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY += 5.0f, this.width, this.height);
        for (Schedule schedule : this.activeSchedules) {
            String nameText = schedule.getName();
            String timeString = this.getTimeString(schedule);
            float nameWidth = Fonts.montserrat.getWidth(nameText, fontSize);
            float bindWidth = Fonts.montserrat.getWidth(timeString, fontSize);
            float localWidth = nameWidth + bindWidth + padding * 3.0f;
            Fonts.montserrat.drawText(ms, nameText, posX + padding - 0.5f, posY + 2.0f, textColor, fontSize, 0.05f);
            Fonts.montserrat.drawText(ms, timeString, posX + this.width - padding - bindWidth + 1.0f, posY + 2.0f, textColor, fontSize, 0.05f);
            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }
            posY += fontSize + padding - 2.0f;
            localHeight += fontSize + padding - 2.0f;
        }
        Scissor.unset();
        Scissor.pop();
        this.widthAnimation.run(Math.max(maxWidth, 70.0f));
        this.heightAnimation.run(localHeight + 5.5f);
        this.width = (float)this.widthAnimation.getValue();
        this.height = (float)this.heightAnimation.getValue();
        this.dragging.setWidth(this.width);
        this.dragging.setHeight(this.height);
    }

    private String formatTime(Calendar calendar, int minutes) {
        int hours = minutes / 60;
        int secondsLeft = 59 - calendar.get(13);
        if ((minutes %= 60) > 0) {
            --minutes;
        }
        return hours + "\u0447 " + minutes + "\u043c " + secondsLeft + "\u0441";
    }

    private int calculateTimeDifference(int[] times, int minutes) {
        int index = Arrays.binarySearch(times, minutes);
        if (index < 0) {
            index = -index - 1;
        }
        if (index >= times.length) {
            return times[0] + 1440 - minutes;
        }
        return times[index] - minutes;
    }

    private String getTimeString(Schedule schedule, Calendar calendar) {
        int minutes = calendar.get(11) * 60 + calendar.get(12);
        int[] timeArray = Arrays.stream(schedule.getTimes()).mapToInt(TimeType::getMinutesSinceMidnight).toArray();
        int timeDifference = this.calculateTimeDifference(timeArray, minutes);
        return this.formatTime(calendar, timeDifference);
    }

    public String getTimeString(Schedule schedule) {
        return this.getTimeString(schedule, Calendar.getInstance(this.timeZone));
    }

    public SchedulesRenderer(Dragging dragging) {
        this.dragging = dragging;
    }
}

