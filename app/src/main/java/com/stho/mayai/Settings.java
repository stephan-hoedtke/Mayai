package com.stho.mayai;

import androidx.annotation.NonNull;

public class Settings implements ISettings {

    private double minutesEgg;
    private double minutesChampagne;
    private double minutesBread;
    private double minutesPotatoes;
    private double minutesClock;
    private double minutesCake;

    Settings() {
        minutesEgg = 7.2;
        minutesChampagne = 26;
        minutesBread = 10.5;
        minutesPotatoes = 22;
        minutesClock = 45;
        minutesCake = 30;
    }

    Settings(final @NonNull ISettings template) {
        minutesEgg = template.getMinutes(Alarm.TYPE_EGG);
        minutesChampagne = template.getMinutes(Alarm.TYPE_CHAMPAGNE);
        minutesBread = template.getMinutes(Alarm.TYPE_BREAD);
        minutesPotatoes = template.getMinutes(Alarm.TYPE_POTATOES);
        minutesClock = template.getMinutes(Alarm.TYPE_CLOCK);
        minutesCake = template.getMinutes(Alarm.TYPE_CAKE);
    }

    public void takeOver(final @NonNull ISettings template) {
        minutesEgg = template.getMinutes(Alarm.TYPE_EGG);
        minutesChampagne = template.getMinutes(Alarm.TYPE_CHAMPAGNE);
        minutesBread = template.getMinutes(Alarm.TYPE_BREAD);
        minutesPotatoes = template.getMinutes(Alarm.TYPE_POTATOES);
        minutesClock = template.getMinutes(Alarm.TYPE_CLOCK);
        minutesCake = template.getMinutes(Alarm.TYPE_CAKE);
    }

    public boolean areDifferent(final @NonNull ISettings template) {
        return areDifferent(minutesEgg, template.getMinutes(Alarm.TYPE_EGG))
                || areDifferent(minutesChampagne, template.getMinutes(Alarm.TYPE_CHAMPAGNE))
                || areDifferent(minutesBread, template.getMinutes(Alarm.TYPE_BREAD))
                || areDifferent(minutesPotatoes, template.getMinutes(Alarm.TYPE_POTATOES))
                || areDifferent(minutesClock, template.getMinutes(Alarm.TYPE_CLOCK))
                || areDifferent(minutesCake, template.getMinutes(Alarm.TYPE_CAKE));

    }

    public @NonNull ISettings clone() {
        return new Settings(this);
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

    public void setMinutesCake(final double value) { this.minutesCake = value; }

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

    public double getMinutesCake() { return minutesCake; }

    @Override
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

            case Alarm.TYPE_CAKE:
                return minutesCake;

            default:
                throw new IllegalArgumentException("Invalid alarm type");
        }
    }

    @Override
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

            case Alarm.TYPE_CAKE:
                minutesCake = minutes;
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
                + Double.toString(this.minutesClock)
                + DELIMITER
                + Double.toString(this.minutesCake);
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
                    if (token.length > 5) { // With version 1.6.0
                        settings.setMinutesCake(Double.parseDouble(token[5]));
                    }
                    return settings;
                }
            }
            return null;
        } catch (Exception ex) {
            return null; // ignore the parse error
        }
    }

    public static @NonNull ISettings defaultValues() {
        return new Settings();
    }

    public static boolean areDifferent(double a, double b) {
        return !(Math.abs(a - b) < 0.01);
    }
}
