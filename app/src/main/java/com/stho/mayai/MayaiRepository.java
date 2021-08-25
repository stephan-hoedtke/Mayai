package com.stho.mayai;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class MayaiRepository implements IRepository {

    /*
        Singleton pattern
     */
    private static MayaiRepository singleton = null;

    /*
        List of all Alarms
     */
    private final MutableLiveData<Alarms> alarmsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Settings> settingsLiveData = new MutableLiveData<>();

    private MayaiRepository(final @NonNull Context context) {
        alarmsLiveData.setValue(new Alarms());
        settingsLiveData.setValue(new Settings());
        load(context);
    }

    /*
        Get the single instance of the repository, if it exists already, otherwise create it and load the data using the context.
     */
    public static synchronized MayaiRepository getRepository(final @NonNull Context context) {
        if (singleton == null) {
            singleton = new MayaiRepository(context);
        }
        return singleton;
    }

    private void load(final @NonNull Context context) {
        MayaiPersister.build(context, this).load();
    }

    /*
        Save the data using the context
     */
    public void save(final @NonNull Context context) {
        MayaiPersister.build(context, this).save();
        touch();
    }

    private void touch() {
        alarmsLiveData.postValue(alarmsLiveData.getValue());
        settingsLiveData.postValue(settingsLiveData.getValue());
    }

    @NonNull
    public Alarms getAlarms() {
        return Objects.requireNonNull(alarmsLiveData.getValue());
    }

    @NonNull
    public Settings getSettings() {
        return Objects.requireNonNull(settingsLiveData.getValue()); }

    boolean hasUnfinishedAlarms() {
       return getAlarms().hasUnfinishedAlarms();
    }

    public void setAlarms(final @NonNull Collection<Alarm> alarms) {
        final Alarms map = Objects.requireNonNull(alarmsLiveData.getValue());
        map.addRange(alarms);
    }

    public void setSettings(final @NonNull Settings settings) {
        settingsLiveData.postValue(settings);
    }

    public @Nullable Alarm getAlarmOrDefault(final @Nullable Alarm alarm) {
        if (alarm == null) {
            return null;
        }
        return getAlarm(alarm);
    }

    public @NonNull Alarm getAlarm(@NonNull Alarm alarm) {
        final Alarms map = Objects.requireNonNull(alarmsLiveData.getValue());
        Alarm reference = map.getReference(alarm);
        if (reference == null) {
            // the alarm was not in the list yet --> insert + update the live data
            reference = map.add(alarm);
            alarmsLiveData.postValue(map);
        }
        return reference;
    }

    public LiveData<Alarms> getAlarmsLD() { return alarmsLiveData; }
    public LiveData<Settings> getSettingsLD() { return settingsLiveData; }
}

