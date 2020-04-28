package com.stho.mayai;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

public class Summary {

    private int counter;

    public static class AlarmInfo {
        private int type;
        private int counter;
        private boolean isHot;

        private AlarmInfo(int type) {
            this.type = type;
            this.counter = 0;
            this.isHot = false;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
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
                    info.setHot();
                    alarm.setHot(true);
                }
                else {
                    alarm.setHot(false);
                }
            }
        }
    }
}
