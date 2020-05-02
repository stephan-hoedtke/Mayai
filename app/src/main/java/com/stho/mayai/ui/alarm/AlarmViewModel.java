package com.stho.mayai.ui.alarm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Alarm;
import com.stho.mayai.Alarms;
import com.stho.mayai.MainViewModel;
import com.stho.mayai.MayaiRepository;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.Settings;

public class AlarmViewModel extends AndroidViewModel {

    private MutableLiveData<Alarm> alarmLiveData = new MutableLiveData<>();
    private MutableLiveData<String> statusNameLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> remainingSecondsLiveData = new MutableLiveData<>();
    private MutableLiveData<Double> secondsPerTurnLiveData = new MutableLiveData<>();
    private MayaiRepository repository;

    @SuppressWarnings("ConstantConditions")
    public static AlarmViewModel build(Fragment fragment) {
        // This view model is shared between two fragments: CountdownFragment + CountdownModifyFragment.
        // Therefore is shall depend on the activity, not on the fragment.
        return new ViewModelProvider(fragment.getActivity()).get(AlarmViewModel.class);
    }

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
        remainingSecondsLiveData.setValue(0);
        statusNameLiveData.setValue("");
        secondsPerTurnLiveData.setValue(3600.0);
    }

    LiveData<String> getStatusNameLD() { return statusNameLiveData; }
    LiveData<Integer> getRemainingSecondsLD() { return remainingSecondsLiveData; }
    LiveData<Float> getAngleLD() { return Transformations.map(remainingSecondsLiveData, this::getAngle); }
    LiveData<Boolean> getSimpleRotaryLD() { return Transformations.map(repository.getSettingsLD(), Settings::getSimpleRotary); }
    LiveData<Alarm> getAlarmLD() { return alarmLiveData; }

    Alarm getAlarm() { return alarmLiveData.getValue(); }

    void setAlarm(Alarm alarm) {
        Alarm reference = repository.getAlarm(alarm);
        alarmLiveData.setValue(reference);
    }

    @SuppressWarnings("ConstantConditions")
    void update() {
        Alarm alarm = alarmLiveData.getValue();
        if (alarm != null) {
            int seconds = alarm.getRemainingSeconds();
            if (remainingSecondsLiveData.getValue() != seconds) {
                remainingSecondsLiveData.postValue(seconds);
            }
            if (!statusNameLiveData.getValue().equals(alarm.getStatusName())) {
                statusNameLiveData.postValue(alarm.getStatusName());
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private float getAngle(int seconds) {
        double secondsPerTurn = secondsPerTurnLiveData.getValue();
        double angle = 360 * seconds / secondsPerTurn;

        while (angle > 360)
            angle -= 360;

        while (angle < 0)
            angle += 360;

        return (float)angle;
    }

    @SuppressWarnings("ConstantConditions")
    void rotate(double delta) {
        Alarm alarm = getAlarm();
        if (alarm != null) {
            double secondsPerTurn = secondsPerTurnLiveData.getValue();
            double seconds = alarm.getRemainingSeconds() + ((delta / 360) * secondsPerTurn);
            double minutes = seconds / 60;

            if (minutes < 0)
                minutes = 0;

            MayaiWorker.build(getApplication()).reschedule(alarm, minutes, false);
            touchAlarm();
        }
    }

    private void touchAlarm() {
        alarmLiveData.postValue(alarmLiveData.getValue());
    }
}

