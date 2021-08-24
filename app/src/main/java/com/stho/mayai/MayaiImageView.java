package com.stho.mayai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;

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

    public MayaiImageView(Context context) {
        super(context);
    }

    public MayaiImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getLayoutAttributes(context, attrs);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (isClock(resId)) {
            setIsClock();
        }
    }

    public void setAlarmTime(Calendar newTime) {
        time = newTime;
        invalidate();
    }

    public void removeAlarm() {
        time = null;
        invalidate();
    }

    public void setIsClock() {
        isClock = true;
        invalidate();
    }

    public void setIsClock(boolean value) {
        isClock = value;
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

    private void onCreate(Context context) {
        int width = getWidth();
        int height = getHeight();
        timeBitmap = createBitmap(context, R.drawable.clock_time, width, height);
        hoursBitmap = createBitmap(context, R.drawable.clock_hours, width, height);
        minutesBitmap = createBitmap(context, R.drawable.clock_minutes, width, height);
        backgroundBitmap = createBitmap(context, R.drawable.clock_background, width, height);
        matrix = new Matrix();
    }

    private static Bitmap createBitmap(Context context, int resourceId, int width, int height) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        double h = bitmap.getHeight();
        double w = bitmap.getWidth();
        double f = Math.min(width / w, height / h);
        int newWith = (int) (f * w + 0.5);
        int newHeight = (int) (f * h + 0.5);
        return Bitmap.createScaledBitmap(bitmap, newWith, newHeight, false);
    }

    private void calculateAngles() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        minutesAngle = minute * 6f + seconds / 10f;
        hoursAngle = hour * 30f + minute / 2f;

        if (hasAlarm()) {
            int alarmHour = time.get(Calendar.HOUR);
            int alarmMinute = time.get(Calendar.MINUTE);
            timeAngle = alarmHour * 30f + alarmMinute / 2f;
        }
    }

    private void getLayoutAttributes(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            int resId = attrs.getAttributeResourceValue(NAMESPACE_ANDROID, SRC, -1);
            if (isClock(resId)) {
                setIsClock();
            }
        }
    }

    private boolean hasAlarm() {
        return time != null;
    }

    private static boolean isClock(int resId) {
        return resId == R.drawable.clock || resId == R.drawable.clock_background;
    }

    private static final String NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";
    private static final String SRC = "src";
}