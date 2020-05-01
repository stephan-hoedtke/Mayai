package com.stho.mayai;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ViewAnimation {
    private final View view;
    private final Handler handler = new Handler();

    public static ViewAnimation build(View view) {
        return new ViewAnimation(view);
    }

    public void toggle() {
        if (view.getVisibility() == View.INVISIBLE) {
            fadeIn();
        }
        else {
            fadeOut();
        }
    }

    public void removeCallbacks() {
        handler.removeCallbacksAndMessages(null);
    }

    private ViewAnimation(View view) {
        this.view = view;
        this.view.setVisibility(View.INVISIBLE);
        this.view.setAlpha(0);
    }

    private void fadeIn() {
        final Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
        this.view.setAlpha(1);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
        handler.postDelayed(this::fadeOut, 7000);
    }

    private void fadeOut() {
        handler.removeCallbacksAndMessages(null);
        final Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
        view.startAnimation(animation);
        view.setVisibility(View.INVISIBLE);
    }
}
