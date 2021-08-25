package com.stho.mayai;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class Helpers {

    private static final String ACTION_SNOOZE = "Snooze";
    private static final String ACTION_CANCEL = "Cancel";
    private static final String ACTION_DETAILS = "Details";
    private static final String ACTION_ALARM = "Alarm";
    private static final String KEY_ALARM = "Alarm"; // Mind the spelling, it must match exactly the parameter name in Navigation

    public static boolean isSnooze(final @Nullable String action) {
        return ACTION_SNOOZE.equalsIgnoreCase(action);
    }

    public static boolean isCancel(final @Nullable String action) {
        return ACTION_CANCEL.equalsIgnoreCase(action);
    }

    public static boolean isDetails(final @Nullable String action) { return ACTION_DETAILS.equalsIgnoreCase(action); }

    public static boolean isAlarm(final @Nullable String action) { return ACTION_ALARM.equalsIgnoreCase(action); }

    public static void putActionSnoozeToIntent(final @NonNull Intent intent) {
        intent.setAction(ACTION_SNOOZE);
    }

    public static void putActionCancelToIntent(final @NonNull Intent intent) {
        intent.setAction(ACTION_CANCEL);
    }

    public static void putActionDetailsToIntent(final @NonNull Intent intent) {
        intent.setAction(ACTION_DETAILS);
    }

    public static void putActionAlarmToIntent(final @NonNull Intent intent) { intent.setAction(ACTION_ALARM); }

    public static void putAlarmToIntent(final @NonNull Intent intent, final @NonNull Alarm alarm) {
        intent.putExtra(KEY_ALARM, alarm.serialize());
    }

    public static @Nullable Alarm getAlarmFromIntent(final @Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        return Alarm.parseAlarm(intent.getStringExtra(KEY_ALARM));
    }

    public static @Nullable Alarm getAlarmFromFragmentArguments(final @NonNull Fragment fragment) {
        final Bundle bundle = fragment.getArguments();
        if (bundle != null) {
            return Alarm.parseAlarm(bundle.getString(KEY_ALARM));
        }
        return null;
    }

    public static @NonNull String getSecondsAsString(final int seconds) {
        final int hours = seconds / 3600;
        final int seconds2 = seconds - hours * 3600;
        final int minutes = seconds2 / 60;
        final int seconds3 = seconds2 - minutes * 60;
        return getSecondsAsString(hours, minutes, seconds3);
    }

    private static @NonNull String getSecondsAsString(final int hours, final int minutes, final int seconds) {
        final StringBuilder sb = new StringBuilder();
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
