package com.stho.mayai;

/*
    Thread safe implementation.
    call touch() to prolong waiting time
    call isReady() to check if there was a touch after the last release and if that touch is past long enough past, and if yes, release the lock and return true.
 */
public class Touch {

    private final Object mutex = new Object();
    private final long delayInMillis;
    private long millis = 0;

    public Touch(final long delayInMillis) {
        this.delayInMillis = delayInMillis;
    }

    public void touch() {
        final long newValue = System.currentTimeMillis() + delayInMillis;
        synchronized (mutex) {
            millis = newValue;
        }
    }

    public boolean isReady() {
        final long currentTimeMillis = System.currentTimeMillis();
        synchronized (mutex) {
            if (millis > 0 && millis < currentTimeMillis) {
                millis = 0;
                return true;
            }
            else {
                return false;
            }
        }
    }
}
