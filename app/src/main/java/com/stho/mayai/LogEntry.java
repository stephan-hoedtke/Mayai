package com.stho.mayai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class LogEntry {
    private final Calendar time;
    private final String message;
    private final static SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);

    LogEntry(final @NonNull String message) {
        this.time = Calendar.getInstance();
        this.message = message;
    }

    private LogEntry(final long millis, final @NonNull String message) {
        this.time = Calendar.getInstance();
        this.time.setTimeInMillis(millis);
        this.message = message;
    }

    public @NonNull Calendar getTime() {
        return time;
    }

    public @NonNull String getTimeAsString() {
        return formatter.format(time.getTime());
    }

    public @NonNull String getMessage() {
        return message;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s: %s", getTimeAsString(), getMessage());
    }

    private final static String DELIMITER = ";";

    @SuppressWarnings("UnnecessaryCallToStringValueOf")
    public @NonNull String serialize() {
        return Long.toString(this.time.getTimeInMillis())
                + DELIMITER
                + EncodeBase64(getMessage());
    }

    public static @Nullable LogEntry parseLogEntry(String value) {
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

    private static @NonNull String EncodeBase64(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static @NonNull String DecodeBase64(String value) {
        byte[] bytes = Base64.getDecoder().decode(value);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
