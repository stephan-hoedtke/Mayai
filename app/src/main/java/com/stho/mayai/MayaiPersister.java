package com.stho.mayai;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class MayaiPersister {

    private final Context context;
    private final IRepository repository;

    public static MayaiPersister build(final @NonNull Context context, final @NonNull IRepository repository) {
        return new MayaiPersister(context, repository);
    }

    private MayaiPersister(final @NonNull Context context, final @NonNull IRepository repository) {
        this.context = context.getApplicationContext();
        this.repository = repository;
    }

    private final static String KEY_MAYAI = "Mayai";
    private final static String KEY_ALARMS = "Alarms";
    private final static String KEY_LOG = "Log";
    private final static String KEY_SETTINGS = "Settings";

    void load() {
        final SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            String value;
            value = preferences.getString(KEY_ALARMS, null);
            if (value != null) {
                final Collection<Alarm> alarms = parseAlarms(value);
                if (alarms != null) {
                    repository.setAlarms(filterAlarms(alarms));
                }
            }
            value = preferences.getString(KEY_LOG, null);
            if (value != null) {
                final Collection<LogEntry> log = parseLog(value);
                if (log != null) {
                    Logger.setLog(filterLog(log));
                }
            }
            value = preferences.getString(KEY_SETTINGS, null);
            if (value != null) {
                final Settings settings = Settings.parseSettings(value);
                if (settings != null) {
                    repository.setSettings(settings);
                }
            }
        }
    }

    void save() {
        save(
                repository.getAlarms(),
                repository.getSettings(),
                Logger.getLog());
    }

    private void save(final @NonNull Alarms alarms, final @NonNull Settings settings, final @NonNull Collection<LogEntry> log) {
        final SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_ALARMS, serializeAlarms(filterAlarms(alarms.getCollection())));
            editor.putString(KEY_LOG, serializeLog(filterLog(log)));
            editor.putString(KEY_SETTINGS, settings.serialize());
            editor.apply();
        }
    }

    private @Nullable SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(KEY_MAYAI, MODE_PRIVATE);
    }

    private final static String COLLECTION_DELIMITER = "§";

    private static @NonNull String serializeAlarms(final @NonNull Collection<Alarm> alarms) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final Alarm alarm : alarms) {
            if (first) {
                first = false;
            }
            else {
                sb.append(COLLECTION_DELIMITER);
            }
            sb.append(alarm.serialize());
        }
        return sb.toString();
    }

    private static @NonNull String serializeLog(final @NonNull Collection<LogEntry> log) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final LogEntry entry : log) {
            if (first) {
                first = false;
            }
            else {
                sb.append(COLLECTION_DELIMITER);
            }
            sb.append(entry.serialize());
        }
        return sb.toString();
    }

    private static @Nullable Collection<Alarm> parseAlarms(final @Nullable String value) {
        try {
            if (value != null && value.length() > 0) {
                final ArrayList<Alarm> alarms = new ArrayList<>();
                final String[] token = value.split(COLLECTION_DELIMITER);
                if (token.length > 0) {
                    for (final String str : token) {
                        final Alarm alarm = Alarm.parseAlarm(str);
                        if (alarm != null) {
                            alarms.add(alarm);
                        }
                    }
                }
                return alarms;
            }
            return null;
        }
        catch (Exception ex) {
            return null; // ignore the parse error
        }
    }

    private static @Nullable Collection<LogEntry> parseLog(final @Nullable String value) {
        try {
            if (value != null && value.length() > 0) {
                final ArrayList<LogEntry> log = new ArrayList<>();
                final String[] token = value.split(COLLECTION_DELIMITER);
                if (token.length > 0) {
                    for (final String str : token) {
                        final LogEntry entry = LogEntry.parseLogEntry(str);
                        if (entry != null) {
                            log.add(entry);
                        }
                    }
                }
                return log;
            }
            return null;
        }
        catch (Exception ex) {
            return null; // ignore the parse error
        }
    }

    private @NonNull Collection<LogEntry> filterLog(final @NonNull Collection<LogEntry> log) {
        final ArrayList<LogEntry> list = new ArrayList<>();
        final Calendar now = Calendar.getInstance();
        for (final LogEntry entry : log) {
            if (!isOutdated(entry.getTime(), now)) {
                list.add(entry);
            }
        }
        return list;
    }

    private @NonNull Collection<Alarm> filterAlarms(final @NonNull Collection<Alarm> alarms) {
        final ArrayList<Alarm> list = new ArrayList<>();
        final Calendar now = Calendar.getInstance();
        for (final Alarm alarm : alarms) {
            if (!isOutdated(alarm, now)) {
                list.add(alarm);
            }
        }
        return list;
    }

    private static boolean isOutdated(final @NonNull Alarm alarm, final @NonNull Calendar now) {
        return (alarm.isFinished() && isOutdated(alarm.getTriggerTime(), now));
    }

    private static boolean isOutdated(final @NonNull Calendar triggerTime, final @NonNull Calendar now) {
        final long difference = now.getTimeInMillis() - triggerTime.getTimeInMillis();
        return (difference > TWO_MINUTES_IN_MILLIS);
    }

    private static final long TWO_MINUTES_IN_MILLIS = 120000;
}

