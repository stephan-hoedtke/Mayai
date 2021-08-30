package com.stho.mayai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public class MayaiImageView extends androidx.appcompat.widget.AppCompatImageView {

    private Matrix matrix = null;

    private Bitmap timeBitmap = null;
    private Bitmap hoursBitmap = null;
    private Bitmap minutesBitmap = null;
    private Bitmap backgroundBitmap = null;

    private float timeAngle = 0f;
    private float hoursAngle = 0f;
    private float minutesAngle = 0f;

    private Calendar time = null;
    private boolean isClock = false;

    public MayaiImageView(final @NonNull Context context) {
        super(context);
    }

    public MayaiImageView(final @NonNull Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
        prepareImageWhenLayoutIsClock(attrs);
    }

    public void setAlarm(final @NonNull Alarm alarm) {
        if (alarm.isClock()) {
            super.setImageResource(0);
            isClock = true;
        } else {
            super.setImageResource(alarm.getIconId());
            isClock = false;
        }
        time = alarm.getTriggerTime();
        invalidate();
    }

    @Override
    public void setImageResource(final int resId) {
        if (isClock(resId)) {
            super.setImageResource(0);
            isClock = true;
        } else {
            super.setImageResource(resId);
            isClock = false;
        }
    }

    public void setAlarmTime(final @Nullable Calendar newTime) {
        time = newTime;
        invalidate();
    }

    public void removeAlarm() {
        time = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isClock) {
            calculateAngles();

            if (matrix == null) {
                onCreate(getContext());
            }

            float px = getWidth() >> 1;
            float py = getHeight() >> 1;
            float dx = Math.round(px - (backgroundBitmap.getWidth() >> 1));
            float dy = Math.round(py - (backgroundBitmap.getHeight() >> 1));

            matrix.reset();
            matrix.setTranslate(dx, dy);
            canvas.drawBitmap(backgroundBitmap, matrix, null);

            matrix.setTranslate(dx, dy);
            matrix.postRotate(hoursAngle, px, py);
            canvas.drawBitmap(hoursBitmap, matrix, null);

            matrix.setTranslate(dx, dy);
            matrix.postRotate(minutesAngle, px, py);
            canvas.drawBitmap(minutesBitmap, matrix, null);

            if (hasAlarm()) {
                matrix.setTranslate(dx, dy);
                matrix.postRotate(timeAngle, px, py);
                canvas.drawBitmap(timeBitmap, matrix, null);
            }
        }
    }

    private void onCreate(final @NonNull Context context) {
        final int width = getWidth();
        final int height = getHeight();
        timeBitmap = createBitmap(context, R.drawable.clock_time, width, height);
        hoursBitmap = createBitmap(context, R.drawable.clock_hours, width, height);
        minutesBitmap = createBitmap(context, R.drawable.clock_minutes, width, height);
        backgroundBitmap = createBitmap(context, R.drawable.clock_background, width, height);
        matrix = new Matrix();
    }

    private static Bitmap createBitmap(final @NonNull Context context, final int resourceId, final int width, final int height) {
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        final double h = bitmap.getHeight();
        final double w = bitmap.getWidth();
        final double f = Math.min(width / w, height / h);
        final int newWith = (int) (f * w + 0.5);
        final int newHeight = (int) (f * h + 0.5);
        return Bitmap.createScaledBitmap(bitmap, newWith, newHeight, false);
    }

    private void calculateAngles() {
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR);
        final int minute = calendar.get(Calendar.MINUTE);
        final int seconds = calendar.get(Calendar.SECOND);
        minutesAngle = minute * 6f + seconds / 10f;
        hoursAngle = hour * 30f + minute / 2f;

        if (hasAlarm()) {
            final int alarmHour = time.get(Calendar.HOUR);
            final int alarmMinute = time.get(Calendar.MINUTE);
            timeAngle = alarmHour * 30f + alarmMinute / 2f;
        }
    }

    private void prepareImageWhenLayoutIsClock(final @Nullable AttributeSet attrs) {
        if (attrs != null) {
            int resId = attrs.getAttributeResourceValue(NAMESPACE_ANDROID, SRC, -1);
            if (isClock(resId)) {
                super.setImageResource(0);
                isClock = true;
            }
        }
    }

    private boolean hasAlarm() {
        return time != null;
    }

    private static boolean isClock(final int resId) {
        return resId == R.drawable.clock || resId == R.drawable.clock_background;
    }

    private static final String NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";
    private static final String SRC = "src";
}
