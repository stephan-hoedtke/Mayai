package com.stho.mayai;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class MayaiPersister {

    private final Context context;
    private final IRepository repository;

    public static MayaiPersister build(Context context, IRepository repository) {
        return new MayaiPersister(context, repository);
    }

    private MayaiPersister(Context context, IRepository repository) {
        this.context = context.getApplicationContext();
        this.repository = repository;
    }

    private final static String KEY_MAYAI = "Mayai";
    private final static String KEY_ALARMS = "Alarms";
    private final static String KEY_LOG = "Log";
    private final static String KEY_SETTINGS = "Settings";

    void load() {
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            String value;
            value = preferences.getString(KEY_ALARMS, null);
            if (value != null) {
                Collection<Alarm> alarms = parseAlarms(value);
                if (alarms != null) {
                    repository.setAlarms(filterAlarms(alarms));
                }
            }
            value = preferences.getString(KEY_LOG, null);
            if (value != null) {
                Collection<LogEntry> log = parseLog(value);
                if (log != null) {
                    Logger.setLog(filterLog(log));
                }
            }
            value = preferences.getString(KEY_SETTINGS, null);
            if (value != null) {
                Settings settings = Settings.parseSettings(value);
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

    private void save(Alarms alarms, Settings settings, Collection<LogEntry> log) {
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_ALARMS, serializeAlarms(filterAlarms(alarms.getCollection())));
            editor.putString(KEY_LOG, serializeLog(filterLog(log)));
            editor.putString(KEY_SETTINGS, settings.serialize());
            editor.apply();
        }
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(KEY_MAYAI, MODE_PRIVATE);
    }

    private final static String COLLECTION_DELIMITER = "§";

    private static String serializeAlarms(Collection<Alarm> alarms) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Alarm alarm : alarms) {
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

    private static String serializeLog(Collection<LogEntry> log) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (LogEntry entry : log) {
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

    private static Collection<Alarm> parseAlarms(String value) {
        try {
            if (value != null && value.length() > 0) {
                ArrayList<Alarm> alarms = new ArrayList<>();
                String[] token = value.split(COLLECTION_DELIMITER);
                if (token.length > 0) {
                    for (String str : token) {
                        Alarm alarm = Alarm.parseAlarm(str);
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

    private static Collection<LogEntry> parseLog(String value) {
        try {
            if (value != null && value.length() > 0) {
                ArrayList<LogEntry> log = new ArrayList<>();
                String[] token = value.split(COLLECTION_DELIMITER);
                if (token.length > 0) {
                    for (String str : token) {
                        LogEntry entry = LogEntry.parseLogEntry(str);
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

    private Collection<LogEntry> filterLog(Collection<LogEntry> log) {
        ArrayList<LogEntry> list = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        for (LogEntry entry : log) {
            if (!isOutdated(entry.getTime(), now)) {
                list.add(entry);
            }
        }
        return list;
    }

    private Collection<Alarm> filterAlarms(Collection<Alarm> alarms) {
        ArrayList<Alarm> list = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        for (Alarm alarm : alarms) {
            if (!isOutdated(alarm, now)) {
                list.add(alarm);
            }
        }
        return list;
    }

    private static boolean isOutdated(Alarm alarm, Calendar now) {
        return (alarm.isFinished() && isOutdated(alarm.getTriggerTime(), now));
    }

    private static boolean isOutdated(Calendar triggerTime, Calendar now) {
        long difference = now.getTimeInMillis() - triggerTime.getTimeInMillis();
        return (difference > TWO_MINUTES_IN_MILLIS);
    }

    private static final long TWO_MINUTES_IN_MILLIS = 120000;
}

