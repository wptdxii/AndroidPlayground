package com.wptdxii.ext.util;

import android.annotation.SuppressLint;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;

/**
 * Created by wptdxii on 17-11-21 下午10:38
 * Email: wptdxii@gmail.com
 * Blog: https://wptdxii.github.io
 * Github: https://github.com/wptdxii
 */

public final class MenuUtil {
    private MenuUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void showOptionalIcons(Menu menu) {
        setOptionalIconsVisible(menu, true);
    }


    public static void hideOptionalIcons(Menu menu) {
        setOptionalIconsVisible(menu, false);
    }

    @SuppressLint("RestrictedApi")
    private static void setOptionalIconsVisible(Menu menu, boolean visible) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(visible);
        }
    }
}
