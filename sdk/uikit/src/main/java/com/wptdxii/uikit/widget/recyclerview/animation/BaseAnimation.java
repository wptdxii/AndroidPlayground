package com.wptdxii.uikit.widget.recyclerview.animation;

import android.animation.Animator;
import android.view.View;

/**
 * strategy pattern : concrete animation need to implement this interface
 * Created by wptdxii on 2016/10/13 0013.
 */

public interface BaseAnimation {
    Animator[] getAnimators(View view);
}
