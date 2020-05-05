package com.stho.mayai;

import android.animation.Animator;

public abstract class AnimatorOnAnimationEndListener implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animator) {
        // ignore
    }

    @Override
    abstract public void onAnimationEnd(Animator animator);

    @Override
    public void onAnimationCancel(Animator animator) {
        // ignore
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
        // ignore
    }
}
