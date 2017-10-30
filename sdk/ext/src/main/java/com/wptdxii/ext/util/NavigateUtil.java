package com.wptdxii.ext.util;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

/**
 * Created by wptdxii on 17-10-29 下午10:25
 * Email: wptdxii@gmail.com
 * Blog: https://wptdxii.github.io
 * Github: https://github.com/wptdxii
 */

public class NavigateUtil {
    private NavigateUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public static <V extends Parcelable> void startActivity(
            Context context, Class<?> cls, String key, V value) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(key, value);
        context.startActivity(intent);
    }
}
