package com.wptdxii.uikit.widget.recyclerview.animation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.ALPHAIN;
import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.SCALEIN;
import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.SLIDEIN_BOTTOM;
import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.SLIDEIN_LEFT;
import static com.wptdxii.uikit.widget.recyclerview.animation.AnimationType.SLIDEIN_RIGHT;

/**
 * Created by wptdxii on 2016/10/14 0014.
 */

@IntDef({ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT})
@Retention(RetentionPolicy.SOURCE)
public @interface AnimationType {

    int ALPHAIN = 0x00000001;
    int SCALEIN = 0x00000002;
    int SLIDEIN_BOTTOM = 0x00000003;
    int SLIDEIN_LEFT = 0x00000004;
    int SLIDEIN_RIGHT = 0x00000005;
}
