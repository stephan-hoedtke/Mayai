package com.stho.mayai;

import androidx.annotation.NonNull;

import java.util.Collection;

public interface IRepository {
    @NonNull Alarms getAlarms();
    @NonNull Settings getSettings();
    void setAlarms(final @NonNull Collection<Alarm> alarms);
    void setSettings(final @NonNull Settings settings);
}
