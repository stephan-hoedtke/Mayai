package com.stho.mayai;

import java.util.ArrayList;
import java.util.Collection;

public interface IRepository {
    Alarms getAlarms();
    void setAlarms(Collection<Alarm> alarms);
    ArrayList<LogEntry> getLog();
    void setLog(Collection<LogEntry> entries);
}
