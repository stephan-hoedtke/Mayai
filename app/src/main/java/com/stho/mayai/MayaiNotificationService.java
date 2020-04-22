package com.stho.mayai;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MayaiNotificationService extends Service {

    private MayaiPlayer player;
    private Handler handler;
    private long startTime;

    public static void startAsForegroundService(Context context, Alarm alarm) {
        if (context != null) {
            Intent intent = new Intent(context, MayaiNotificationService.class);
            Helpers.putAlarmToIntent(intent, alarm);
            context.startForegroundService(intent);
        }
    }

    public static void stop(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, MayaiNotificationService.class);
            context.stopService(intent);
        }
    }

    public MayaiNotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void onHandleIntent(@Nullable Intent intent) {
        try {
            Alarm alarm = Helpers.getAlarmFromIntent(intent);
            if (alarm != null) {
                 startPlaying();
                startAutoStopHandler();
                // Send a foreground notification as the service was started as foreground service:
                MayaiNotificationManager
                        .build(this)
                        .setCountdown(alarm)
                        .sendForegroundNotificationFromService(this);
            }
        }
        catch (Exception ex) {
            Logger.log("Error in MayaiNotificationService: " + ex.toString());
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void startAutoStopHandler() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (wasRunningLongEnough()) {
                    stopPlaying();
                }
                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    private void stopAutoStopHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private boolean wasRunningLongEnough() {
        long time = System.currentTimeMillis() - startTime;
        return time > 180000; // 3 minutes
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAutoStopHandler();
        stopPlaying();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    private void startPlaying() {
        if (player == null) {
            player = MayaiPlayer.build(this);
        }
        player.ring();
        startTime = System.currentTimeMillis();
    }

    private void stopPlaying() {
        if (player != null) {
            player.silence();
        }
    }
}