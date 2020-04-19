package com.stho.mayai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MayaiActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Helpers.isSnooze(action)) {
            Alarm alarm = Helpers.getAlarmFromIntent(intent);
            snooze(context, alarm);
        }
        else if (Helpers.isCancel(action)) {
            Alarm alarm = Helpers.getAlarmFromIntent(intent);
            cancel(context, alarm);
        }
    }

    private void snooze(Context context, Alarm alarm) {
        MayaiWorker.build(context).snooze(alarm);
    }

    private void cancel(Context context, Alarm alarm) {
        MayaiWorker.build(context).cancel(alarm);
    }
}
