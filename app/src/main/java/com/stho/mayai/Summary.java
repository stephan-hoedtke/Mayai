package com.stho.mayai;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

public class Summary {

    private int counter = 0;
    private Calendar triggerTime = null;

    public static class AlarmInfo {
        private int counter;
        private boolean isHot;

        private AlarmInfo() {
            this.counter = 0;
            this.isHot = false;
        }

        public int getCounter() {
            return counter;
        }

        public boolean isHot() {
            return isHot;
        }

        void setHot() {
            isHot = true;
        }
    }

    final private HashMap<Integer, AlarmInfo> map = new HashMap<>();

    public @NonNull AlarmInfo getAlarmInfo(int type) {
        AlarmInfo info = map.get(type);
        if (info == null) {
            info = new AlarmInfo();
            map.put(type, info);
        }
        return info;
    }

    public int getPendingAlarmsCounter() {
        return counter;
    }

    public boolean hasAlarm() {
        return counter > 0;
    }

    public Calendar getTriggerTime() {
        return triggerTime;
    }

    public void update(Collection<Alarm> alarms) {
        triggerTime = null;
        counter = 0;
        map.clear();
        final Calendar soon = Alarm.createCalendarForMinutes(1.0);
        for (Alarm alarm : alarms) {
            if (alarm.isPending()) {
                AlarmInfo info = getAlarmInfo(alarm.getType());
                info.counter++;
                counter++;
                if (alarm.getTriggerTime().before(soon)) {
                    info.setHot();
                    alarm.setHot(true);
                }
                else {
                    alarm.setHot(false);
                }
                if (this.triggerTime == null || alarm.getTriggerTime().before(this.triggerTime)) {
                    this.triggerTime = alarm.getTriggerTime();
                }
            }
        }
    }
}
