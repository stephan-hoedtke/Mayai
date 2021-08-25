package com.stho.mayai;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.stho.mayai.Alarm.STATUS_FINISHED;
import static com.stho.mayai.Alarm.STATUS_NONE;
import static com.stho.mayai.Alarm.STATUS_PENDING;
import static com.stho.mayai.Alarm.STATUS_SCHEDULED;

import androidx.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class MayaiWorker {

    private final Context context;
    private final MayaiRepository repository;
    private final MayaiAlarmManager alarmManager;

    private MayaiWorker(final @NonNull Context context) {
        this.context = context.getApplicationContext();
        this.repository = MayaiRepository.getRepository(context);
        this.alarmManager = MayaiAlarmManager.build(context);
    }

    public static MayaiWorker build(final @NonNull Context context) {
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

    public void initialize() {
        scheduleNextPendingAlarm();
    }

    public void open(final @NonNull Alarm alarm) {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            Helpers.putAlarmToIntent(intent, alarm);
            Helpers.putActionDetailsToIntent(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE|FLAG_UPDATE_CURRENT);
            pendingIntent.send(context, 0, null);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void snooze(final @NonNull Alarm alarm) {
        try {
            Logger.log("Snooze alarm " + alarm.getName());
            cancelNotification();
            repository.getAlarm(alarm).reschedule(2.0).setStatus(STATUS_PENDING);
            scheduleNextPendingAlarm();
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void reschedule(final @NonNull Alarm alarm, final double minutes, final boolean scheduleNextPendingAlarm) {
        try {
            repository.getAlarm(alarm).reschedule(minutes).setStatus(STATUS_PENDING);
            if (scheduleNextPendingAlarm) {
                scheduleNextPendingAlarm();
            }
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void cancel(final @NonNull Alarm alarm) {
        try {
            Logger.log("Cancel alarm " + alarm.getName());
            cancelNotification();
            final Alarm reference = repository.getAlarm(alarm).setStatus(STATUS_FINISHED);
            alarmManager.cancelAlarm(reference);
            scheduleNextPendingAlarm();
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void scheduleAlarm(final @NonNull Alarm alarm) {
        try {
            repository.getAlarm(alarm).setStatus(STATUS_PENDING);
            scheduleNextPendingAlarm();
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void delete(final @NonNull Alarm alarm) {
        try {
            final Alarm reference = repository.getAlarm(alarm);
            switch (reference.getStatus()) {
                case STATUS_SCHEDULED:
                case STATUS_PENDING:
                    alarmManager.cancelAlarm(reference);
                    break;
            }
            repository.getAlarms().delete(reference);
            scheduleNextPendingAlarm();
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void undoDelete(final int position, final @NonNull Alarm alarm) {
        try {
            if (alarm.getRemainingSeconds() > 0) {
                alarm.setStatus(STATUS_PENDING);
            }
            repository.getAlarms().undoDelete(position, alarm);
            scheduleNextPendingAlarm();
            repository.save(context);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void cancel() {
        try {
            cancelNotification();
            alarmManager.cancelAlarm(Alarm.REQUEST_CODE);
            final Alarms alarms = repository.getAlarms();
            for (final Alarm alarm : alarms.getCollection()) {
                if (alarm.isPending()) {
                    alarm.setStatus(STATUS_NONE);
                }
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
        long id = -1;
        final Alarm nextAlarm = repository.getAlarms().getNextPendingAlarm();
        if (nextAlarm != null) {
            nextAlarm.setStatus(STATUS_SCHEDULED);
            alarmManager.scheduleAlarm(nextAlarm);
            id = nextAlarm.getId();
        }
        for (final Alarm alarm : repository.getAlarms().getCollection()) {
            if (alarm.isPending()) {
                if (alarm.getId() != id) {
                    alarm.setStatus(STATUS_PENDING);
                }
            }
        }
    }

    private void onError(Exception ex) {
        Logger.log("Error in MayaiWorker: " + ex.toString());
        Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
    }
}

