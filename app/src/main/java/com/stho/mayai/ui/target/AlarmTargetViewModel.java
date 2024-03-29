package com.stho.mayai.ui.target;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Alarm;

public class AlarmTargetViewModel extends AndroidViewModel {

    private final MutableLiveData<Alarm> alarmLiveData = new MutableLiveData<>();

    public static AlarmTargetViewModel build(final @NonNull AppCompatActivity activity) {
        return new ViewModelProvider(activity).get(AlarmTargetViewModel.class);
    }

    public AlarmTargetViewModel(final @NonNull Application application) {
        super(application);
    }

    LiveData<Alarm> getAlarmLD() { return alarmLiveData; }

    public Alarm getAlarm() { return alarmLiveData.getValue(); }

    public void setAlarm(final Alarm alarm) {
        alarmLiveData.setValue(alarm);
    }
}

