package com.stho.mayai.ui.settings;

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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.BuildConfig;
import com.stho.mayai.MainViewModel;
import com.stho.mayai.MayaiRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsViewModel extends AndroidViewModel {

    private final MutableLiveData<String> infoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> versionLiveData = new MutableLiveData<>();

    @SuppressWarnings("ConstantConditions")
    public static SettingsViewModel build(Fragment fragment) {
        SettingsViewModel settingsViewModel = new ViewModelProvider(fragment.getActivity()).get(SettingsViewModel.class);
        settingsViewModel.update(fragment.getContext());
        return settingsViewModel;
    }

    public SettingsViewModel(@NonNull Application application) {
        super(application);
    }


    LiveData<String> getInfoLD() { return infoLiveData; }
    LiveData<String> getVersionLD() { return versionLiveData; }

    private final static String EOL = "\n";

    void update(Context context) {
        infoLiveData.setValue(getAlarmInfo(context));
        versionLiveData.setValue(BuildConfig.VERSION_NAME);
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
            MayaiRepository.log("Error in SettingsViewModel: " + ex.toString());
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
            MayaiRepository.log("Error in SettingsViewModel: " + ex.toString());
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
