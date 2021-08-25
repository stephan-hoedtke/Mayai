package com.stho.mayai;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class Logger {

    private final static ArrayList<LogEntry> log = new ArrayList<>();

    public static @NonNull ArrayList<LogEntry> getLog() {
        return Logger.log;
    }

    public static void setLog(final @NonNull Collection<LogEntry> entries) {
        log.addAll(entries);
    }

    public static void clearLog() {
        log.clear();
    }

    public static void log(final @NonNull String message) {
        log.add(new LogEntry(message));
    }
}
