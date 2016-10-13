package com.wptdxii.uikit.widget.recyclerview.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by wptdxii on 2016/10/13 0013.
 */

public class AlphaInAnimation implements BaseAnimation {
    private static final float DEFAULT_ALPHA_FROM = 0f;
    private float mFrom;

    public AlphaInAnimation() {
        this(DEFAULT_ALPHA_FROM);
    }

    public AlphaInAnimation(float from) {
        this.mFrom = from;
    }

    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f)};
    }
}
