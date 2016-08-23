package com.wptdxii.androidpractice.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Toast统一管理类
 */
public class T {
    private static Toast toast;
    private T() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;
    private static void showToast(Context context, CharSequence message, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, message, duration);
        } else {
            toast.setText(message);
            toast.setDuration(duration);
        } 
        
        toast.show();
    }
    
    private static void showToast(Context context, @StringRes int resId, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, resId, duration);
        } else {
            toast.setText(resId);
            toast.setDuration(duration);
        } 
        
        toast.show();
    }
    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        if (isShow) {
            showToast(context,message,Toast.LENGTH_SHORT);
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param resId
     */
    public static void showShort(Context context, @StringRes int resId) {
        if (isShow) {
            showToast(context,resId,Toast.LENGTH_SHORT);
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        if (isShow) {
            showToast(context,message,Toast.LENGTH_LONG);
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param resId
     */
    public static void showLong(Context context, @StringRes int resId) {
        if (isShow) {
            showToast(context,resId,Toast.LENGTH_LONG);
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (isShow) {
            showToast(context,message,duration);
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param resId
     * @param duration
     */
    public static void show(Context context, @StringRes int resId, int duration) {
        if (isShow) {
            showToast(context,resId,duration);
        }
    }

}