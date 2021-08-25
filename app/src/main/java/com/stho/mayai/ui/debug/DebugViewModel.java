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
import androidx.lifecycle.ViewModelProvider;

import com.stho.mayai.BuildConfig;
import com.stho.mayai.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * In Debug hard coded English texts are acceptable
 */
public class DebugViewModel extends AndroidViewModel {

    private final MutableLiveData<String> infoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> versionLiveData = new MutableLiveData<>();

    public static DebugViewModel build(final @NonNull Fragment fragment) {
        final DebugViewModel debugViewModel = new ViewModelProvider(fragment.requireActivity()).get(DebugViewModel.class);
        debugViewModel.update(fragment.requireContext());
        return debugViewModel;
    }

    public DebugViewModel(final @NonNull Application application) {
        super(application);
    }

    LiveData<String> getInfoLD() { return infoLiveData; }
    LiveData<String> getVersionLD() { return versionLiveData; }

    void update(final @NonNull Context context) {
        infoLiveData.setValue(getAlarmInfo(context));
        versionLiveData.setValue(BuildConfig.VERSION_NAME);
    }

    private String getAlarmInfo(Context context) {
        final StringBuilder sb = new StringBuilder();
        try {
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                final AlarmManager.AlarmClockInfo clockInfo = alarmManager.getNextAlarmClock();
                if (clockInfo != null) {
                    final long nextAlarmTime = clockInfo.getTriggerTime();
                    final Date nextAlarmDate = new Date(nextAlarmTime);
                    sb.append("Time: ");
                    sb.append(new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(nextAlarmDate));
                    sb.append(EOL);

                    final PendingIntent intent = clockInfo.getShowIntent();
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

    void openAlarmFromClockInfo(final @NonNull Context context) {
        try {
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                final AlarmManager.AlarmClockInfo clockInfo = alarmManager.getNextAlarmClock();
                if (clockInfo != null) {
                    final PendingIntent intent = clockInfo.getShowIntent();
                    intent.send(context, 0, null);
                }
            }
        } catch (Exception ex) {
            Logger.log("Error in SettingsViewModel: " + ex.toString());
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private final static String EOL = "\n";
}
