package com.stho.mayai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class RotaryView extends AppCompatImageView {

    public interface OnAngleChangedListener {
        void onAngleChanged(double delta);
    }

    private OnAngleChangedListener listener;
    private boolean simpleRotary;

    public RotaryView(Context context) {
        super(context);
        simpleRotary = true;
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

    public boolean getSimpleRotary() {
        return this.simpleRotary;
    }

    public void setSimpleRotary(boolean value) {
        this.simpleRotary = value;
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
        if (simpleRotary) {
            return onTouchEventSimpleMode(event);
        }
        else {
            return onTouchEventComplexMode(event);
        }
    }

    public boolean onTouchEventSimpleMode(MotionEvent event) {
        // The rotation follows the finger position directly. Wherever you tap the pointer will point to.
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            final double delta = ensureAngleRange(getAngle(event.getX(), event.getY()));
            addAngle(delta);
        }
        return true;
    }

    private double previousAngle = 0;

    public boolean onTouchEventComplexMode(MotionEvent event) {
        // The rotations changes as the finger changes. You can move the pointer from other positions by swiping.
         switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                previousAngle = getRotation() + getAngle(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                final double alpha = getRotation() + getAngle(event.getX(), event.getY());
                final double delta = ensureAngleRange(alpha - previousAngle);
                previousAngle = alpha;
                addAngle(delta);
                break;
        }
        return true;
    }

    private double getAngle(float x, float y) {
        float cx = getWidth() >> 1;
        float cy = getHeight() >> 1;
        return Math.atan2(y - cy, x - cx) * 180 / Math.PI + 90;
    }

    private static double ensureAngleRange(double delta) {
        while (delta > 180) {
            delta -= 360;
        }
        while (delta < -180) {
            delta += 360;
        }
        return delta;
    }
}
