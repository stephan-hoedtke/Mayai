package com.stho.mayai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class RotaryView extends AppCompatImageView {

    public interface OnAngleChangedListener {
        void onAngleChanged(double angle);
    }

    private float angle = 45;
    private OnAngleChangedListener listener;
    private Matrix matrix = new Matrix();

    public RotaryView(Context context) {
        super(context);
    }

    public void setAngle(float angle) {
        this.angle = angle;
        invalidate();
    }

    public RotaryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public void setOnAngleChangedListener(OnAngleChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onCreate();
        rotateImage();
    }

    private void rotateImage() {
        int w = this.getDrawable().getIntrinsicWidth();
        int h = this.getDrawable().getIntrinsicHeight();
        matrix.reset();
        matrix.setRotate(angle, w >> 1, h >> 1);
        this.setScaleType(ScaleType.MATRIX);
        this.setImageMatrix(matrix);
    }

    private void onCreate() {
        setImageResource(R.drawable.knob);
    }

    private double previousAlpha;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float cx = getWidth() >> 1;
        float cy = getHeight() >> 1;
        float x = event.getX();
        float y = event.getY();
        double alpha = Math.atan2(y - cy, x - cx) * 180 / Math.PI;

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            double delta = (alpha - previousAlpha);

            if (delta > 180)
                delta -= 360;

            if (delta < -180)
                delta += 360;

            angle += delta;

            if (listener != null)
                listener.onAngleChanged(delta);

            invalidate();
        }

        previousAlpha = alpha;
        return true;
    }
}
