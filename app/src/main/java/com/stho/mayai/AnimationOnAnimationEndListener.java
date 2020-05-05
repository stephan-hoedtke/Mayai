package com.stho.mayai;

import android.view.animation.Animation;

public abstract class AnimationOnAnimationEndListener implements Animation.AnimationListener {

    @Override
    public void onAnimationStart(Animation animation) {
        // ignore
    }

    abstract public void onAnimationEnd(Animation animation);

    @Override
    public void onAnimationRepeat(Animation animation) {
        // ignore
    }
}
