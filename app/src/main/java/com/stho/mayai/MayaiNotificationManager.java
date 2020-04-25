package com.stho.mayai;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.stho.mayai.ui.target.MayaiAlarmTargetActivity;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


// for review:
// https://www.journaldev.com/19421/android-notification-channel-dots
@SuppressWarnings("WeakerAccess")
public class MayaiNotificationManager {

    private static final String MY_CHANNEL_ID = "com.stho.mayai.ALARM";
    private static final int MY_REQUEST_CODE = 17;
    private static final int MY_REQUEST_CODE_SNOOZE = 19;
    private static final int MY_REQUEST_CODE_CANCEL = 21;
    private static final int MY_NOTIFICATION_ID = 13579;

    private final Context context;
    private Alarm alarm;

    private MayaiNotificationManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static MayaiNotificationManager build(Context context) {
        return new MayaiNotificationManager(context);
    }

    public MayaiNotificationManager setCountdown(Alarm alarm) {
        this.alarm = alarm;
        return this;
    }

    @SuppressWarnings("ConstantConditions")
    public void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MY_NOTIFICATION_ID);
    }

    @SuppressWarnings("ConstantConditions")
    public void sendNotification() {
        try {
            Notification notification = createNotification();
            notification.flags = Notification.FLAG_ONLY_ALERT_ONCE
                    | Notification.FLAG_INSISTENT
                    | Notification.FLAG_FOREGROUND_SERVICE
                    | Notification.BADGE_ICON_LARGE
                    | NotificationManager.INTERRUPTION_FILTER_PRIORITY;
            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID, notification);
        }
        catch(Exception ex) {
            Logger.log("Error in MayaiNotificationManager: " + ex.toString());
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void sendForegroundNotificationFromService(Service service) {
        try {
            Notification notification = createNotification();
            notification.flags = Notification.FLAG_ONLY_ALERT_ONCE
                    | Notification.FLAG_INSISTENT
                    | Notification.FLAG_FOREGROUND_SERVICE
                    | Notification.BADGE_ICON_LARGE
                    | NotificationManager.INTERRUPTION_FILTER_PRIORITY;
            service.startForeground(MY_NOTIFICATION_ID, notification);
        } catch (Exception ex) {
            Logger.log("Error in MayaiNotificationManager: " + ex.toString());
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private Notification createNotification() {
        registerNotificationChannel();
        final PendingIntent pendingIntent = getTargetPendingIntent();
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MY_CHANNEL_ID);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.drawable.bell128);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), alarm.getNotificationIconId()));
        builder.setContentTitle(alarm.getNotificationTitle());
        builder.setContentText(alarm.getNotificationText());
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(false);
        builder.addAction(getSnoozeAction());
        builder.addAction(getCancelAction());
        builder.setFullScreenIntent(pendingIntent, true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        return builder.build();
    }

    private PendingIntent getTargetPendingIntent() {
        Intent targetActivityIntent = new Intent(context, MayaiAlarmTargetActivity.class);
        Helpers.putAlarmToIntent(targetActivityIntent, alarm);
        targetActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, MY_REQUEST_CODE, targetActivityIntent, FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Action getSnoozeAction() {
        Intent action = new Intent(context, MayaiActionReceiver.class);
        Helpers.putActionSnoozeToIntent(action);
        Helpers.putAlarmToIntent(action, alarm);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MY_REQUEST_CODE_SNOOZE, action, FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(alarm.getIconId(), "Snooze", pendingIntent).build();
    }

    private NotificationCompat.Action getCancelAction() {
        Intent action = new Intent(context, MayaiActionReceiver.class);
        Helpers.putActionCancelToIntent(action);
        Helpers.putAlarmToIntent(action, alarm);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MY_REQUEST_CODE_CANCEL, action, FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(alarm.getIconId(), "Cancel", pendingIntent).build();
    }

    @SuppressWarnings("ConstantConditions")
    void registerNotificationChannel() {
        try {
            NotificationChannel channel = new NotificationChannel(MY_CHANNEL_ID, "Mayai", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Mayai:Alarm");
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.setBypassDnd(true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        } catch (Exception ex) {
            Logger.log("Error in MayaiNotificationManager: " + ex.toString());
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /*
        Context: the context of an activity context.
        Don't use getApplicationContext() here. Or you will see this error message:
        Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
     */
    public static void openChannelSettings(Context context) {

        try {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, MY_CHANNEL_ID);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);
        } catch (Exception ex) {
            Logger.log("Error in MayaiNotificationManager: " + ex.toString());
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
