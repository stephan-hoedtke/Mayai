package com.stho.mayai;

import java.util.Collection;

public interface IRepository {
    Alarms getAlarms();
    Settings getSettings();
    void setAlarms(Collection<Alarm> alarms);
    void setSettings(Settings settings);
}
