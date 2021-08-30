package com.stho.mayai;

public class Settings {

    private double minutesEgg;
    private double minutesChampagne;
    private double minutesBread;
    private double minutesPotatoes;
    private double minutesClock;

    Settings() {
        minutesEgg = 7.2;
        minutesChampagne = 26;
        minutesBread = 10.5;
        minutesPotatoes = 22;
        minutesClock = 3;
    }

    public void reset() {
        minutesEgg = 7.2;
        minutesChampagne = 26;
        minutesBread = 10.5;
        minutesPotatoes = 22;
        minutesClock = 3;
    }

    public void setMinutesEgg(final double value) {
        minutesEgg = value;
    }

    public void setMinutesChampagne(final double value) {
        minutesChampagne = value;
    }

    public void setMinutesBread(final double value) {
        this.minutesBread = value;
    }

    public void setMinutesPotatoes(final double value) {
        this.minutesPotatoes = value;
    }

    public void setMinutesClock(final double value) {
        this.minutesClock = value;
    }

    public double getMinutesEgg() {
        return minutesEgg;
    }

    public double getMinutesChampagne() {
        return minutesChampagne;
    }

    public double getMinutesBread() {
        return minutesBread;
    }

    public double getMinutesPotatoes() {
        return minutesPotatoes;
    }

    public double getMinutesClock() {
        return minutesClock;
    }

    public double getMinutes(int type) {
        switch (type) {
            case Alarm.TYPE_EGG:
                return minutesEgg;

            case Alarm.TYPE_CHAMPAGNE:
                return minutesChampagne;

            case Alarm.TYPE_BREAD:
                return minutesBread;

            case Alarm.TYPE_POTATOES:
                return minutesPotatoes;

            case Alarm.TYPE_CLOCK:
                return minutesClock;

            default:
                throw new IllegalArgumentException("Invalid alarm type");
        }
    }

    public void setMinutes(int type, double minutes) {
        switch (type) {
            case Alarm.TYPE_EGG:
                minutesEgg = minutes;
                break;

            case Alarm.TYPE_CHAMPAGNE:
                minutesChampagne = minutes;
                break;

            case Alarm.TYPE_BREAD:
                minutesBread = minutes;
                break;

            case Alarm.TYPE_POTATOES:
                minutesPotatoes = minutes;
                break;

            case Alarm.TYPE_CLOCK:
                minutesClock = minutes;
                break;

            default:
                throw new IllegalArgumentException("Invalid alarm type");
        }
    }


    private static final String DELIMITER = ":";

    @SuppressWarnings("UnnecessaryCallToStringValueOf")
    String serialize() {
        return Double.toString(this.minutesEgg)
                + DELIMITER
                + Double.toString(this.minutesChampagne)
                + DELIMITER
                + Double.toString(this.minutesBread)
                + DELIMITER
                + Double.toString(this.minutesPotatoes)
                + DELIMITER
                + Double.toString(this.minutesClock);
    }

    static Settings parseSettings(String value) {
        try {
            if (value != null && value.length() > 0) {
                String[] token = value.split(DELIMITER);
                if (token.length > 4) {
                    Settings settings = new Settings();
                    settings.setMinutesEgg(Double.parseDouble(token[0]));
                    settings.setMinutesChampagne(Double.parseDouble(token[1]));
                    settings.setMinutesBread(Double.parseDouble(token[2]));
                    settings.setMinutesPotatoes(Double.parseDouble(token[3]));
                    settings.setMinutesClock(Double.parseDouble(token[4]));
                    return settings;
                }
            }
            return null;
        } catch (Exception ex) {
            return null; // ignore the parse error
        }
    }
}
