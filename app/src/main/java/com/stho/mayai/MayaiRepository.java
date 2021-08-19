package com.stho.mayai;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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

    private MayaiRepository(Context context) {
        alarmsLiveData.setValue(new Alarms());
        settingsLiveData.setValue(new Settings());
        load(context);
    }

    /*
        Get the single instance of the repository, if it exists already, otherwise create it and load the data using the context.
     */
    public static synchronized MayaiRepository getRepository(Context context) {
        if (singleton == null) {
            singleton = new MayaiRepository(context);
        }
        return singleton;
    }

    private void load(Context context) {
        MayaiPersister.build(context, this).load();
    }

    /*
        Save the data using the context
     */
    public void save(Context context) {
        MayaiPersister.build(context, this).save();
        touch();
    }

    private void touch() {
        alarmsLiveData.postValue(alarmsLiveData.getValue());
        settingsLiveData.postValue(settingsLiveData.getValue());
    }

    public Alarms getAlarms() {
        return alarmsLiveData.getValue();
    }

    public Settings getSettings() { return settingsLiveData.getValue(); }

    boolean hasUnfinishedAlarms() {
       return getAlarms().hasUnfinishedAlarms();
    }

    @SuppressWarnings("ConstantConditions")
    public void setAlarms(Collection<Alarm> alarms) {
        Alarms map = alarmsLiveData.getValue();
        map.addRange(alarms);
    }

    public void setSettings(Settings settings) {
        settingsLiveData.postValue(settings);
    }

    public @Nullable Alarm getAlarmOrDefault(@Nullable Alarm alarm) {
        if (alarm == null) {
            return null;
        }
        return getAlarm(alarm);
    }

    @SuppressWarnings("ConstantConditions")
    public @NonNull Alarm getAlarm(@NonNull Alarm alarm) {
        Alarms map = alarmsLiveData.getValue();
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

