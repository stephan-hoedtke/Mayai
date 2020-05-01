package com.stho.mayai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private OnAngleChangedListener listener;

    public RotaryView(Context context) {
        super(context);
    }

    public void addAngle(double delta) {
        float angle = getRotation();
        angle += delta;
        setRotation(angle);

        if (listener != null)
            listener.onAngleChanged(delta);
    }

    public void setAngle(float angle) {
        this.setRotation(angle);
    }

    public RotaryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnAngleChangedListener(OnAngleChangedListener listener) {
        this.listener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        if (event.getAction() == MotionEvent.ACTION_MOVE) {

            float cx = getWidth() >> 1;
            float cy = getHeight() >> 1;
            float x = event.getX();
            float y = event.getY();
            double delta = Math.atan2(y - cy, x - cx) * 180 / Math.PI + 90;

            if (delta > 180)
                delta -= 360;

            if (delta < -180)
                delta += 360;

            addAngle(delta);
        }
        return true;
    }
}
