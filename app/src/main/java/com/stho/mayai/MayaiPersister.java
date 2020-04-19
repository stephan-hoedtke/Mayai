package com.stho.mayai;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;

import static android.content.Context.MODE_PRIVATE;

public class MayaiPersister {

    private final Context applicationContext;
    private final IRepository repository;

    public static MayaiPersister build(Context context, IRepository repository) {
        return new MayaiPersister(context, repository);
    }

    private MayaiPersister(Context context, IRepository repository) {
        this.applicationContext = context.getApplicationContext();
        this.repository = repository;
    }

    private final static String KEY_MAYAI = "Mayai";
    private final static String KEY_ALARMS = "Alarms";
    private final static String KEY_LOG = "Log";

    void load() {
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            String value;
            value = preferences.getString(KEY_ALARMS, null);
            if (value != null) {
                Collection<Alarm> alarms = parseAlarms(value);
                if (alarms != null) {
                    repository.setAlarms(filter(alarms));
                }
            }
            value = preferences.getString(KEY_LOG, null);
            if (value != null) {
                Collection<LogEntry> log = parseLog(value);
                if (log != null) {
                    repository.setLog(log);
                }
            }
        }
    }

    void save() {
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_ALARMS, serializeAlarms(repository.getAlarms().getCollection()));
            editor.putString(KEY_LOG, serializeLog(repository.getLog()));
            editor.apply();
        }
    }


    private SharedPreferences getSharedPreferences() {
        return applicationContext.getSharedPreferences(KEY_MAYAI, MODE_PRIVATE);
    }

    private Collection<Alarm> filter(Collection<Alarm> alarms) {
        ArrayList<Alarm> list = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (!alarm.isOutdated()) {
                list.add(alarm);
            }
        }
        return list;
    }

    private final static String COLLECTION_DELIMITER = "$";

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
}

