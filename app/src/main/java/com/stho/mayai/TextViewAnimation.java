package com.stho.mayai;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class TextViewAnimation {
    private final TextView textView;
    private final Handler handler = new Handler();

    public static TextViewAnimation build(TextView textView) {
        return new TextViewAnimation(textView);
    }

    public void toggle() {
        if (textView.getVisibility() == View.INVISIBLE) {
            fadeIn();
        }
        else {
            fadeOut();
        }
    }

    public void removeCallbacks() {
        handler.removeCallbacksAndMessages(null);
    }

    private TextViewAnimation(TextView textView) {
        this.textView = textView;
        this.textView.setVisibility(View.INVISIBLE);
    }

    private void fadeIn() {
        final Animation animation = AnimationUtils.loadAnimation(textView.getContext(), R.anim.fade_in);
        textView.setVisibility(View.VISIBLE);
        textView.startAnimation(animation);
        handler.postDelayed(this::fadeOut, 7000);
    }

    private void fadeOut() {
        handler.removeCallbacksAndMessages(null);
        final Animation animation = AnimationUtils.loadAnimation(textView.getContext(), R.anim.fade_out);
        textView.startAnimation(animation);
        textView.setVisibility(View.INVISIBLE);
    }
}
