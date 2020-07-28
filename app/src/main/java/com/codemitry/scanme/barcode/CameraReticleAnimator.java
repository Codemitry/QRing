package com.codemitry.scanme.barcode;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

class CameraReticleAnimator {

    private final long DURATION_RIPPLE_FADE_IN_MS = 333;
    private final long DURATION_RIPPLE_FADE_OUT_MS = 500;
    private final long DURATION_RIPPLE_EXPAND_MS = 833;
    private final long DURATION_RIPPLE_STROKE_WIDTH_SHRINK_MS = 833;
    private final long DURATION_RESTART_DORMANCY_MS = 1333;
    private final long START_DELAY_RIPPLE_FADE_OUT_MS = 667;
    private final long START_DELAY_RIPPLE_EXPAND_MS = 333;
    private final long START_DELAY_RIPPLE_STROKE_WIDTH_SHRINK_MS = 333;
    private final long START_DELAY_RESTART_DORMANCY_MS = 1167;

    double rippleAlphaScale = 0;
    double rippleSizeScale = 0;
    double rippleStrokeWidthScale = 1;

    private AnimatorSet animatorSet;

    public CameraReticleAnimator(GraphicOverlay graphicOverlay) {
        ValueAnimator rippleFadeInAnimator = ValueAnimator.ofFloat(0, 1).setDuration(DURATION_RIPPLE_FADE_IN_MS);
        rippleFadeInAnimator.addUpdateListener((ValueAnimator animation) -> {
            rippleAlphaScale = (Float) animation.getAnimatedValue();
            graphicOverlay.postInvalidate();
        });

        ValueAnimator rippleFadeOutAnimator = ValueAnimator.ofFloat(1, 0).setDuration(DURATION_RIPPLE_FADE_OUT_MS);
        rippleFadeOutAnimator.setStartDelay(START_DELAY_RIPPLE_FADE_OUT_MS);
        rippleFadeOutAnimator.addUpdateListener((ValueAnimator animation) -> {
            rippleAlphaScale = (Float) animation.getAnimatedValue();
            graphicOverlay.postInvalidate();
        });

        ValueAnimator rippleExpandAnimator = ValueAnimator.ofFloat(0, 1).setDuration(DURATION_RIPPLE_EXPAND_MS);
        rippleExpandAnimator.setStartDelay(START_DELAY_RIPPLE_EXPAND_MS);
        rippleExpandAnimator.setInterpolator(new FastOutSlowInInterpolator());
        rippleExpandAnimator.addUpdateListener((ValueAnimator animation) -> {
            rippleSizeScale = (Float) animation.getAnimatedValue();
            graphicOverlay.postInvalidate();
        });

        ValueAnimator rippleStrokeWidthShrinkAnimator = ValueAnimator.ofFloat(1, 0.5f).setDuration(DURATION_RIPPLE_STROKE_WIDTH_SHRINK_MS);
        rippleStrokeWidthShrinkAnimator.setStartDelay(START_DELAY_RIPPLE_STROKE_WIDTH_SHRINK_MS);
        rippleStrokeWidthShrinkAnimator.setInterpolator(new FastOutSlowInInterpolator());
        rippleStrokeWidthShrinkAnimator.addUpdateListener((ValueAnimator animation) -> {
            rippleStrokeWidthScale = (Float) animation.getAnimatedValue();
            graphicOverlay.postInvalidate();
        });

        ValueAnimator fakeAnimatorForRestartDelay = ValueAnimator.ofInt(0, 0).setDuration(DURATION_RESTART_DORMANCY_MS);
        fakeAnimatorForRestartDelay.setStartDelay(START_DELAY_RESTART_DORMANCY_MS);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                rippleFadeInAnimator,
                rippleFadeOutAnimator,
                rippleExpandAnimator,
                rippleStrokeWidthShrinkAnimator,
                fakeAnimatorForRestartDelay
        );

    }

    public void start() {
        if (!animatorSet.isRunning())
            animatorSet.start();
    }

    public void cancel() {
        animatorSet.cancel();
        rippleAlphaScale = 0;
        rippleSizeScale = 0;
        rippleStrokeWidthScale = 1;
    }
}
