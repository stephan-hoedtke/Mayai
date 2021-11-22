package com.stho.mayai.ui.alarm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.Alarm;
import com.stho.mayai.Helpers;
import com.stho.mayai.MayaiRepository;
import com.stho.mayai.MayaiWorker;

public class AlarmViewModel extends AndroidViewModel {

    private final MutableLiveData<Alarm> alarmLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> statusNameLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> remainingSecondsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> permanentRotaryLiveData = new MutableLiveData<>();
    private final MayaiRepository repository;

    public static AlarmViewModel build(final @NonNull Fragment fragment) {
        // This view model is shared between two fragments: CountdownFragment + CountdownModifyFragment.
        // Therefore is shall depend on the activity, not on the fragment.
        return new ViewModelProvider(fragment.requireActivity()).get(AlarmViewModel.class);
    }

    public AlarmViewModel(final @NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
        remainingSecondsLiveData.setValue(0);
        statusNameLiveData.setValue("");
        permanentRotaryLiveData.setValue(false);
    }

    LiveData<String> getStatusNameLD() { return statusNameLiveData; }
    LiveData<Integer> getRemainingSecondsLD() { return remainingSecondsLiveData; }
    LiveData<Float> getAngleLD() { return Transformations.map(remainingSecondsLiveData, this::getAngle); }
    LiveData<Alarm> getAlarmLD() { return alarmLiveData; }

    Alarm getAlarm() { return alarmLiveData.getValue(); }

    void setAlarm(final @Nullable Alarm alarm) {
        final Alarm reference = (alarm == null) ? null : repository.getAlarm(alarm);
        alarmLiveData.setValue(reference);
    }

    boolean isPermanent() {
        Boolean value = permanentRotaryLiveData.getValue();
        return value != null && value;
    }
    void setPermanent(boolean value) {
        permanentRotaryLiveData.postValue(value);
    }

    void update() {
        final Alarm alarm = alarmLiveData.getValue();
        if (alarm != null) {
            final int newRemainingSeconds = alarm.getRemainingSeconds();
            final Integer remainingSeconds = remainingSecondsLiveData.getValue();
            if (remainingSeconds == null || remainingSeconds != newRemainingSeconds) {
                remainingSecondsLiveData.postValue(newRemainingSeconds);
            }
            final String newStatusName = alarm.getStatusName();
            final String statusName = statusNameLiveData.getValue();
            if (statusName == null || !statusNameLiveData.getValue().equals(newStatusName)) {
                statusNameLiveData.postValue(newStatusName);
            }
        }
    }

    private float getAngle(final int seconds) {
        double angle = Helpers.normalizeAngle360(seconds / SECONDS_PER_DEGREE);
        return (float)angle;
    }

    void rotateDelaySchedule(final double delta) {
        final Alarm alarm = getAlarm();
        if (alarm != null) {
            double seconds = alarm.getRemainingSeconds() + (delta * SECONDS_PER_DEGREE);
            double minutes = Helpers.toMinutes(seconds);

            // Note, while rotating the alarm state will be changed to pending.
            // Once the rotation is done, the schedule will be updated.
            // See: touch.isReady()
            MayaiWorker.build(getApplication()).reschedule(alarm, minutes, false);
            touchAlarm();
        }
    }

    void repeatCountdown() {
        final Alarm alarm = getAlarm();
        if (alarm != null)
        {
            double seconds = alarm.getDurationInSeconds();
            double minutes = Helpers.toMinutes(seconds);

            MayaiWorker.build(getApplication()).reschedule(alarm, minutes, true);
            touchAlarm();
        }
    }

    private void touchAlarm() {
        alarmLiveData.postValue(alarmLiveData.getValue());
    }

    private final static double SECONDS_PER_DEGREE = 10f; // 1 minute = 6 degrees
}

