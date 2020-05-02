package com.stho.mayai;

public class Settings {

    private double minutesEgg;
    private double minutesChampagne;
    private double minutesBread;
    private double minutesPotatoes;
    private double minutesClock;
    private boolean simpleRotary;

    Settings() {
        minutesEgg = 7.2;
        minutesChampagne = 26;
        minutesBread = 10.5;
        minutesPotatoes = 22;
        minutesClock = 3;
        simpleRotary = true;
    }

    public void setMinutesEgg(double value) {
        minutesEgg = value;
    }

    public void setMinutesChampagne(double value) {
        minutesChampagne = value;
    }

    public void setMinutesBread(double value) {
        this.minutesBread = value;
    }

    public void setMinutesPotatoes(double value) {
        this.minutesPotatoes = value;
    }

    public void setMinutesClock(double value) {
        this.minutesClock = value;
    }

    public void setSimpleRotary(boolean value) { this.simpleRotary = value; }

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

    public boolean getSimpleRotary() { return simpleRotary; }

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
                + Boolean.toString(this.simpleRotary);
    }

    static Settings parseSettings(String value) {
        try {
            if (value != null && value.length() > 0) {
                String[] token = value.split(DELIMITER);
                if (token.length == 6) {
                    Settings settings = new Settings();
                    settings.setMinutesEgg(Double.parseDouble(token[0]));
                    settings.setMinutesChampagne(Double.parseDouble(token[1]));
                    settings.setMinutesBread(Double.parseDouble(token[2]));
                    settings.setMinutesPotatoes(Double.parseDouble(token[3]));
                    settings.setMinutesClock(Double.parseDouble(token[4]));
                    settings.setSimpleRotary(Boolean.parseBoolean(token[5]));
                    return settings;
                }
            }
            return null;
        } catch (Exception ex) {
            return null; // ignore the parse error
        }
    }
}
