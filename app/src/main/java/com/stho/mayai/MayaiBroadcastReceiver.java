package com.stho.mayai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class MayaiBroadcastReceiver extends BroadcastReceiver {

    // see:
    // https://medium.com/@sauge16/how-to-handle-alarm-in-all-android-version-e7aca16ae885
    // https://stackoverflow.com/questions/36110812/difference-between-setexact-and-setalarmclock
    // https://developer.android.com/training/scheduling/wakelock

    private PowerManager.WakeLock wakeLock;

    private static final String myWakeLockTag = "MAYAI:ALARM";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, myWakeLockTag);
            wakeLock.acquire(1000);

            Alarm alarm = Helpers.getAlarmFromIntent(intent);
            if (alarm != null) {

                MayaiRepository.log("Alarm broadcast received: " + alarm.getName());

                // Vibrate the mobile phone
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createOneShot(130, 70));

                // TODO:
                //  vibrator.vibrate(VibrationEffect.createWaveform(new long[] ?? times, 70, 3));

                // Start the notification service, which will send the heads up notification
                // Start it as foreground service to avoid the IllegalStateException: "app in background" in Android.O
                MayaiNotificationService.startAsForegroundService(context, alarm);
            }
        }
        catch (Exception ex) {
            MayaiRepository.log("Error in MayaiBroadcastReceiver: " + ex.toString());
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        finally {
            if (wakeLock != null)
                wakeLock.release();
        }
    }
}
