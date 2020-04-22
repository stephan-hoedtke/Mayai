package com.stho.mayai;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class MayaiAlarmManager {

    private final Context context;

    private MayaiAlarmManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static MayaiAlarmManager build(Context context) {
        return new MayaiAlarmManager(context);
    }

    @SuppressWarnings("ConstantConditions")
    public void scheduleAlarm(@NonNull Alarm alarm) {
        // read out here:
        // https://medium.com/@igordias/android-scheduling-alarms-with-precise-delivery-time-using-alarmmanager-75c409f3bde0
        // Notes:
        // using "setAlarmClock": alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, countDown.getTriggerTimeInMillis(), pendingIntent);
        //      - registers the alarm so that it wakes up the phone
        //      - makes it visible via "alarmManager.getNextAlarmClock()"
        //
        // using "setAndAllowWhileIdle":  alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(countDown.getTriggerTimeInMillis(), pendingIntent), pendingIntent);
        //      - registers the alarm so that it wakes up the phone
        //      - the alarm will NOT be visible via "alarmManager.getNextAlarmClock()"
        //
        PendingIntent alarmIntent = createAlarmIntentFor(alarm);
        PendingIntent showIntent = createShowAlarmDetailsIntentFor(alarm);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(alarm.getTriggerTimeInMillis(), showIntent), alarmIntent);
        MayaiRepository.log("Alarm for " + alarm.getName() + " set: " + alarm.getTriggerTimeAsString());
    }

    public void cancelAlarm(Alarm alarm) {
        cancelAlarm(alarm.getRequestCode());
    }

        @SuppressWarnings("ConstantConditions")
    public void cancelAlarm(int requestCode) {
        MayaiRepository.log("Cancel alarm");
        PendingIntent cancelIntent = getCancelIntentFor(requestCode);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(cancelIntent);
    }

    /*
        Note: the intent must have
        - the same class
        - the same action
        - the same request code
        as the alarm intent it shall cancel. The Context and the flags don't matter.
        See als: Intent.filterEquals(Intent)
     */
    private @NonNull PendingIntent getCancelIntentFor(int requestCode) {
        Intent intent = new Intent(context, MayaiBroadcastReceiver.class);
        Helpers.putActionAlarmToIntent(intent);
        return PendingIntent.getBroadcast(context, requestCode, intent, 0);
    }

    private @NonNull PendingIntent createAlarmIntentFor(Alarm alarm) {
        return createAlarmIntentFor(alarm, alarm.getRequestCode());
    }

    private @NonNull PendingIntent createAlarmIntentFor(Alarm alarm, int requestCode) {
        Intent intent = new Intent(context, MayaiBroadcastReceiver.class);
        Helpers.putAlarmToIntent(intent, alarm);
        Helpers.putActionAlarmToIntent(intent);
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private @NonNull PendingIntent createShowAlarmDetailsIntentFor(Alarm alarm) {
        Intent intent = new Intent(context, MainActivity.class);
        Helpers.putAlarmToIntent(intent, alarm);
        Helpers.putActionDetailsToIntent(intent);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
