package com.stho.mayai;

import android.widget.TextView;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

public class LogEntry {
    private final Calendar time;
    private String message;
    private final static SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);

    LogEntry(String message) {
        this.time = Calendar.getInstance();
        this.message = message;
    }

    private LogEntry(long millis, String message) {
        this.time = Calendar.getInstance();
        this.time.setTimeInMillis(millis);
        this.message = message;
    }

    public Calendar getTime() {
        return time;
    }

    public String getTimeAsString() {
        return formatter.format(time.getTime());
    }

    public String getMessage() {
        return message;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s: %s", getTimeAsString(), getMessage());
    }

    private final static String DELIMITER = ";";

    public String serialize() {
        return Long.toString(this.time.getTimeInMillis())
                + DELIMITER
                + EncodeBase64(getMessage());
    }

    public static LogEntry parseLogEntry(String value) {
        try {
            if (value != null && value.length() > 0) {
                String[] token = value.split(DELIMITER);
                if (token.length == 2) {
                    long millis = Long.parseLong(token[0]);
                    String message = DecodeBase64(token[1]);
                    return new LogEntry(millis, message);
                }
            }
            return null;
        }
        catch (Exception ex) {
            return null; // ignore the parse error
        }
    }

    private static String EncodeBase64(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String DecodeBase64(String value) {
        byte[] bytes = Base64.getDecoder().decode(value);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
