package com.stho.mayai;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

public class Summary {

    private int counter;

    public static class AlarmInfo {
        int type;
        public int counter;
        public boolean isHot;

        private AlarmInfo(int type) {
            this.type = type;
            this.counter = 0;
            this.isHot = false;
        }
    }

    final private HashMap<Integer, AlarmInfo> map = new HashMap<>();

    public @NonNull AlarmInfo getAlarmInfo(int type) {
        AlarmInfo info = map.get(type);
        if (info == null) {
            info = new AlarmInfo(type);
            map.put(type, info);
        }
        return info;
    }

    public int getPendingAlarmsCounter() {
        return counter;
    }

    public void update(Collection<Alarm> alarms) {
        counter = 0;
        map.clear();
        final Calendar soon = Alarm.createCalendarForMinutes(1.0);
        for (Alarm alarm : alarms) {
            if (alarm.isPending()) {
                AlarmInfo info = getAlarmInfo(alarm.getType());
                info.counter++;
                counter++;
                if (alarm.getTriggerTime().before(soon)) {
                    info.isHot = true;
                }
            }
        }
    }
}
