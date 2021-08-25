package com.stho.mayai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class MayaiActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final @NonNull Context context, final @NonNull Intent intent) {
        final String action = intent.getAction();
        if (Helpers.isSnooze(action)) {
            final Alarm alarm = Helpers.getAlarmFromIntent(intent);
            if (alarm != null) {
                snooze(context, alarm);
            }
        }
        else if (Helpers.isCancel(action)) {
            final Alarm alarm = Helpers.getAlarmFromIntent(intent);
            if (alarm != null) {
                cancel(context, alarm);
            }
        }
    }

    private void snooze(final @NonNull Context context, final @NonNull Alarm alarm) {
        MayaiWorker.build(context).snooze(alarm);
    }

    private void cancel(final @NonNull Context context, final @NonNull Alarm alarm) {
        MayaiWorker.build(context).cancel(alarm);
    }
}
