package com.stho.mayai;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.stho.mayai.Alarm.STATUS_FINISHED;
import static com.stho.mayai.Alarm.STATUS_NONE;
import static com.stho.mayai.Alarm.STATUS_PENDING;
import static com.stho.mayai.Alarm.STATUS_SCHEDULED;
import static com.stho.mayai.Alarm.TYPE_BREAD;
import static com.stho.mayai.Alarm.TYPE_CHAMPAGNE;
import static com.stho.mayai.Alarm.TYPE_CLOCK;
import static com.stho.mayai.Alarm.TYPE_EGG;
import static com.stho.mayai.Alarm.TYPE_POTATOES;

public class MayaiWorker {

    private final Context context;
    private final MayaiRepository repository;
    private final MayaiAlarmManager alarmManager;

    private MayaiWorker(Context context) {
        this.context = context;
        this.repository = MayaiRepository.getRepository(context);
        this.alarmManager = MayaiAlarmManager.build(context);
    }

    public static MayaiWorker build(Context context) {
        return new MayaiWorker(context);
    }

    public void openMainActivity() {
        try {
            cancelNotification();
            context.startActivity(new Intent(context, MainActivity.class));
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void open(Alarm alarm) {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            Helpers.putAlarmToIntent(intent, alarm);
            Helpers.putActionDetailsToIntent(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);
            pendingIntent.send(context, 0, null);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void snooze(Alarm alarm) {
        try {
            cancelNotification();
            alarm = repository.getAlarm(alarm);
            alarm.reschedule(1.0);
            alarm.setStatus(STATUS_SCHEDULED);
            alarmManager.scheduleAlarm(alarm);
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void cancel(Alarm alarm) {
        try {
            cancelNotification();
            alarm = repository.getAlarm(alarm);
            alarm.setStatus(STATUS_FINISHED);
            alarmManager.cancelAlarm(alarm);
            scheduleNextPendingAlarm();
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void scheduleAlarm(Alarm alarm) {
        try {
            alarm = repository.getAlarm(alarm);
            alarm.setStatus(STATUS_PENDING);
            scheduleNextPendingAlarm();
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void reset(Alarm alarm) {
        try {
            alarm = repository.getAlarm(alarm);
            alarm.setStatus(STATUS_NONE);
            scheduleNextPendingAlarm();
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void delete(Alarm alarm) {
        try {
            alarm = repository.getAlarm(alarm);
            switch (alarm.getStatus()) {
                case STATUS_SCHEDULED:
                case STATUS_PENDING:
                    alarm.setStatus(STATUS_FINISHED);
                    alarmManager.cancelAlarm(alarm);
                    break;
            }
            repository.getAlarms().delete(alarm);
            scheduleNextPendingAlarm();
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void cancel() {
        try {
            alarmManager.cancelAlarm(Alarm.REQUEST_CODE);
            Alarms alarms = repository.getAlarms();
            for (Alarm alarm : alarms.getCollection()) {
                if (alarm.isPending()) {
                    alarm.setStatus(STATUS_NONE);
                };
            }
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    private void cancelNotification() {
        MayaiNotificationService.stop(context);
        MayaiNotificationManager.build(context).cancelNotification();
    }

    private void scheduleNextPendingAlarm() {
        Alarm alarm = repository.getAlarms().getNextPendingAlarm();
        if (alarm != null) {
            alarm.setStatus(STATUS_SCHEDULED);
            alarmManager.scheduleAlarm(alarm);
        }
    }

    private void onError(Exception ex) {
        MayaiRepository.log("Error in MayaiWorker: " + ex.toString());
        Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
    }
}

