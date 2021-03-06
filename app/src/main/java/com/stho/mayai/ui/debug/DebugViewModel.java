package com.stho.mayai.ui.debug;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.BuildConfig;
import com.stho.mayai.Logger;
import com.stho.mayai.MayaiRepository;
import com.stho.mayai.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DebugViewModel extends AndroidViewModel {

    private final MutableLiveData<String> infoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> versionLiveData = new MutableLiveData<>();
    private MayaiRepository repository;

    @SuppressWarnings("ConstantConditions")
    public static DebugViewModel build(Fragment fragment) {
        DebugViewModel debugViewModel = new ViewModelProvider(fragment.getActivity()).get(DebugViewModel.class);
        debugViewModel.update(fragment.getContext());
        return debugViewModel;
    }

    public DebugViewModel(@NonNull Application application) {
        super(application);
        repository = MayaiRepository.getRepository(application.getBaseContext());
    }


    LiveData<String> getInfoLD() { return infoLiveData; }
    LiveData<String> getVersionLD() { return versionLiveData; }
    LiveData<Boolean> getSimpleRotaryLD() { return Transformations.map(repository.getSettingsLD(), Settings::getSimpleRotary); }

    private final static String EOL = "\n";

    void update(Context context) {
        infoLiveData.setValue(getAlarmInfo(context));
        versionLiveData.setValue(BuildConfig.VERSION_NAME);
    }

    void setSimpleRotary(boolean value) {
        repository.getSettings().setSimpleRotary(value);
    }

    private String getAlarmInfo(Context context) {
        StringBuilder sb = new StringBuilder();

        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                AlarmManager.AlarmClockInfo clockInfo = alarmManager.getNextAlarmClock();
                if (clockInfo != null) {
                    long nextAlarmTime = clockInfo.getTriggerTime();
                    Date nextAlarmDate = new Date(nextAlarmTime);
                    sb.append("Time: ");
                    sb.append(new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(nextAlarmDate));
                    sb.append(EOL);

                    PendingIntent intent = clockInfo.getShowIntent();
                    if (intent != null) {
                        sb.append("Package: ");
                        sb.append(intent.getCreatorPackage());
                        sb.append(EOL);
                    }
                }
            }
        }
        catch (Exception ex) {
            Logger.log("Error in SettingsViewModel: " + ex.toString());
            sb.append("Error: ");
            sb.append(ex.toString());
            sb.append(EOL);
        }

        return sb.toString();
    }

    void openAlarmFromClockInfo(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                AlarmManager.AlarmClockInfo clockInfo = alarmManager.getNextAlarmClock();
                if (clockInfo != null) {
                    PendingIntent intent = clockInfo.getShowIntent();
                    intent.send(context, 0, null);
                }
            }
        } catch (Exception ex) {
            Logger.log("Error in SettingsViewModel: " + ex.toString());
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
