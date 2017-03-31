package com.wptdxii.uikit.widget.recyclerview.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by wptdxii on 2016/10/13 0013.
 */

public class SlideInRightAnimation implements BaseAnimation {
    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{ObjectAnimator.ofFloat(view, "translationX",
                view.getRootView().getWidth(), 0)};
    }
}
