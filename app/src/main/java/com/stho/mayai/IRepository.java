package com.stho.mayai;

import java.util.Collection;

public interface IRepository {
    Alarms getAlarms();
    void setAlarms(Collection<Alarm> alarms);
}
