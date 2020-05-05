package com.stho.mayai;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class Helpers {

    private static final String ACTION_SNOOZE = "Snooze";
    private static final String ACTION_CANCEL = "Cancel";
    private static final String ACTION_DETAILS = "Details";
    private static final String ACTION_ALARM = "Alarm";

    private static final String KEY_ALARM = "Alarm"; // Don't use "ALARM", as it must match the parameter name in Navigation

    public static boolean isSnooze(String action) {
        return ACTION_SNOOZE.equalsIgnoreCase(action);
    }

    public static boolean isCancel(String action) {
        return ACTION_CANCEL.equalsIgnoreCase(action);
    }

    public static boolean isDetails(String action) { return ACTION_DETAILS.equalsIgnoreCase(action); }

    public static boolean isAlarm(String action) { return ACTION_ALARM.equalsIgnoreCase(action); }

    public static void putActionSnoozeToIntent(Intent intent) {
        intent.setAction(ACTION_SNOOZE);
    }

    public static void putActionCancelToIntent(Intent intent) {
        intent.setAction(ACTION_CANCEL);
    }

    public static void putActionDetailsToIntent(Intent intent) {
        intent.setAction(ACTION_DETAILS);
    }

    public static void putActionAlarmToIntent(Intent intent) { intent.setAction(ACTION_ALARM); }

    public static void putAlarmToIntent(Intent intent, Alarm alarm) {
        intent.putExtra(KEY_ALARM, alarm.serialize());
    }

    public static Alarm getAlarmFromIntent(@Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        return Alarm.parseAlarm(intent.getStringExtra(KEY_ALARM));
    }

    public static Alarm getAlarmFromFragmentArguments(Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (bundle != null) {
            return Alarm.parseAlarm(bundle.getString(KEY_ALARM));
        }
        return null;
    }

    public static String getSecondsAsString(int seconds) {
        int hours = seconds / 3600;
        seconds = seconds - hours * 3600;
        int minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        return getSecondsAsString(hours, minutes, seconds);
    }

    private static String getSecondsAsString(int hours, int minutes, int seconds) {
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours);
            sb.append(":");
        }
        sb.append(String.format(Locale.ENGLISH, "%02d", minutes));
        sb.append(":");
        sb.append(String.format(Locale.ENGLISH, "%02d", seconds));
        return sb.toString();
    }
}
