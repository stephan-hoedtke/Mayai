package com.stho.mayai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class Alarm {

    public static final int TYPE_EGG = 1;
    public static final int TYPE_BREAD = 2;
    public static final int TYPE_POTATOES = 3;
    public static final int TYPE_CHAMPAGNE = 4;
    public static final int TYPE_CLOCK = 5;
    public static final int TYPE_CAKE = 6;

    public static final int STATUS_NONE = 0;
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_SCHEDULED = 2;
    public static final int STATUS_FINISHED = 3;

    private final long id;
    private final int type;
    private final String name;
    private Calendar triggerTime;
    private long durationInMillis;
    private int status;
    private boolean isHot;

    private final static String DEFAULT_TIME = "--:--:--";
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    public Alarm(final int type, final String name, final double durationInMinutes) {
        this.id = System.currentTimeMillis();
        this.type = type;
        this.name = name;
        this.durationInMillis = (long) (durationInMinutes * 60000);
        this.triggerTime = Alarm.createCalendar(Alarm.getCurrentTimeInMillis() + durationInMillis);
        this.status = STATUS_NONE;
    }

    public Alarm reschedule(final double durationInMinutes) {
        durationInMillis = (long) (durationInMinutes * 60000);
        triggerTime = Alarm.createCalendar(Alarm.getCurrentTimeInMillis() + durationInMillis);
        status = STATUS_NONE;
        return this;
    }

    public Alarm setStatus(final int status) {
        this.status = status;
        return this;
    }

    private Alarm(final long id, final int type, final String name, final long durationInMillis, final long endTimeInMillis, final int status) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.durationInMillis = durationInMillis;
        this.triggerTime = Alarm.createCalendar(endTimeInMillis);
        this.status = status;
    }

    public boolean isUnfinished() {
        return status != STATUS_FINISHED;
    }

    public boolean isFinished() {
        return (status == STATUS_FINISHED);
    }

    public boolean isPending() {
        return (status == STATUS_PENDING || status == STATUS_SCHEDULED);
    }

    public long getId() {
        return id;
    }

    public int getType() { return type; }

    public String getName() { return name; }

    public int getStatus() {
        return status;
    }

    public boolean isHot() { return isHot; }

    public boolean isClock() { return type == TYPE_CLOCK; }

    public void setHot(final boolean isHot) {
        this.isHot = isHot;
    }

    // TODO: replace strings with resources

    public @NonNull String getStatusName() {
        switch (status) {
            case STATUS_FINISHED:
                return "Finished";

            case STATUS_PENDING:
                return "Scheduled (Pending)";

            case STATUS_SCHEDULED:
                return "Scheduled";

            default:
                return "";
        }
    }

    public @NonNull String getNotificationTitle() {
        return name;
    }

    public @NonNull String getNotificationText() {
        return "Ready: " + getDurationAsString();
    }

    public @NonNull Calendar getTriggerTime() { return triggerTime; }

    public long getTriggerTimeInMillis() {
        return triggerTime.getTimeInMillis();
    }

    public int getDurationInSeconds() { return Alarm.toSeconds(durationInMillis); }

    public @NonNull String getTriggerTimeAsString() {
        return Alarm.toTimeString(triggerTime);
    }

    public @NonNull String getDurationAsString() { return Helpers.getSecondsAsString(getDurationInSeconds()); }

    public int getIconId() {
        return getIconId(getType());
    }

    public static int getIconId(final int type) {

        switch (type) {
            case TYPE_EGG:
                return R.drawable.egg;

            case TYPE_CHAMPAGNE:
                return R.drawable.champagne;

            case TYPE_POTATOES:
                return R.drawable.potatoes;

            case TYPE_BREAD:
                return R.drawable.bread;

            case TYPE_CAKE:
                    return R.drawable.cake;

            default:
                return R.drawable.clock_background;
        }
    }

    public static int getTypeStringId(final int type) {
        switch (type) {
            case TYPE_EGG:
                return R.string.title_egg;

            case TYPE_CHAMPAGNE:
                return R.string.title_champagne;

            case TYPE_POTATOES:
                return R.string.title_potatoes;

            case TYPE_BREAD:
                return R.string.title_bread;

            case TYPE_CAKE:
                return R.string.title_cake;

            default:
                return R.string.title_clock;
        }
    }

    public int getNotificationIconId() {
        switch (type) {
            case TYPE_EGG:
                return R.drawable.egg256;

            case TYPE_CHAMPAGNE:
                return R.drawable.champagne256;

            case TYPE_POTATOES:
                return R.drawable.potatoes256;

            case TYPE_BREAD:
                return R.drawable.bread256;

            case TYPE_CAKE:
                return R.drawable.cake256;

            default:
                return R.drawable.clock256;
        }
    }

    public static final int REQUEST_CODE = 103840284;

    @SuppressWarnings("SameReturnValue")
    public int getRequestCode() {
        return REQUEST_CODE;
    }

    public int getRemainingSeconds() {
        long millis = getRemainingMillis();
        return millis > 0 ? Alarm.toSeconds(millis) : 0;
    }

    public static Calendar createCalendarForMinutes(final double minutes) {
        long millis = (long) (minutes * 60000);
        return createCalendar(Alarm.getCurrentTimeInMillis() + millis);
    }

    private long getRemainingMillis() {
        return triggerTime.getTimeInMillis() - Alarm.getCurrentTimeInMillis();
    }

    private static long getCurrentTimeInMillis() {
        return GregorianCalendar.getInstance().getTimeInMillis();
    }

    private static Calendar createCalendar(final long timeInMillis) {
        Calendar time = GregorianCalendar.getInstance();
        time.setTimeInMillis(timeInMillis);
        return time;
    }

    private static String toTimeString(final @Nullable Calendar calendar) {
        return (calendar == null) ? DEFAULT_TIME : timeFormat.format(calendar.getTime());
    }

    private static int toSeconds(final long millis) {
        return (int)((500 + millis) / 1000);
    }

    @SuppressWarnings("UnnecessaryCallToStringValueOf")
    public String serialize() {
        return Long.toString(this.id)
                + DELIMITER
                + Integer.toString(this.type)
                + DELIMITER
                + this.name
                + DELIMITER
                + Long.toString(this.durationInMillis)
                + DELIMITER
                + Long.toString(this.triggerTime.getTimeInMillis())
                + DELIMITER
                + Integer.toString(this.status);
    }

    public static Alarm parseAlarm(String value) {
        try {
            if (value != null && value.length() > 0) {
                String[] token = value.split(DELIMITER);
                if (token.length == 6) {
                    long id = Long.parseLong(token[0]);
                    int key = Integer.parseInt(token[1]);
                    String name = token[2];
                    long durationInMillis = Long.parseLong(token[3]);
                    long endTimeInMillis = Long.parseLong(token[4]);
                    int status = Integer.parseInt(token[5]);
                    return new Alarm(id, key, name, durationInMillis, endTimeInMillis, status);
                }
            }
            return null;
        }
        catch (Exception ex) {
            return null; // ignore the parse error
        }
    }

    @SuppressWarnings("UnnecessaryCallToStringValueOf")
    @NonNull
    @Override
    public String toString() {
        return Long.toString(this.id) +
                DELIMITER +
                this.name +
                " for " +
                this.getDurationAsString() +
                " until " +
                this.getTriggerTimeAsString();
    }

    private static final String DELIMITER = ":";
}






