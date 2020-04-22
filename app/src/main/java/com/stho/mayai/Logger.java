package com.stho.mayai;

import java.util.ArrayList;
import java.util.Collection;

public class Logger {

    private final static ArrayList<LogEntry> log = new ArrayList<>();

    public static ArrayList<LogEntry> getLog() {
        return Logger.log;
    }

    public static void setLog(Collection<LogEntry> entries) {
        log.addAll(entries);
    }

    public static void clearLog() {
        log.clear();
    }

    public static void log(String message) {
        log.add(new LogEntry(message));
    }
}
