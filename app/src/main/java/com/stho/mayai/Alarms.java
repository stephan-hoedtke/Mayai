package com.stho.mayai;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Alarms implements IAlarms {
    private final HashMap<Long, Alarm> map = new HashMap<>();
    private final ArrayList<Long> keys = new ArrayList<>();

    @NonNull Alarm add(@NonNull Alarm alarm) {
        long id = alarm.getId();
        map.put(id, alarm);
        keys.add(id);
        return alarm;
    }

    @Nullable Alarm getReference(@NonNull Alarm alarm) {
        return map.get(alarm.getId());
    }

    Alarms() {
        // default
    }

    public int size() {
        return keys.size();
    }

    public Alarm get(int position) {
        if (0 <= position && position < keys.size()) {
            long id = keys.get(position);
            return map.get(id);
        }
        return null;
    }

    void delete(Alarm alarm) {
        long id = alarm.getId();
        map.remove(id);
        keys.remove(id);
    }

    void undoDelete(int position, Alarm alarm) {
        long id = alarm.getId();
        keys.add(position, id);
        map.put(id, alarm);
    }

    @Nullable Alarm getNextPendingAlarm() {
        Alarm nextAlarm = null;
        for (Alarm alarm : map.values()) {
            if (alarm.isPending()) {
                if (nextAlarm == null) {
                    nextAlarm = alarm;
                }
                else if (alarm.getTriggerTime().before(nextAlarm.getTriggerTime())) {
                    nextAlarm = alarm;
                }
            }
        }
        return nextAlarm;
    }

    public Collection<Alarm> getCollection() {
        return map.values();
    }

    void addRange(Collection<Alarm> alarms) {
        for (Alarm alarm : alarms) {
            add(alarm);
        }
    }

    boolean hasUnfinishedAlarms() {
        for (Alarm alarm : map.values()) {
            if (alarm.isUnfinished()) {
                return true;
            }
        }
        return false;
    }
}

