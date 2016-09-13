package com.wptdxii.ext.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by wptdxii on 2016/7/7 0007.
 */
public class ActivityStack {
    private static Stack<Activity> activityStack;
    private static ActivityStack instance = new ActivityStack();

    private ActivityStack() {
    }

    public static ActivityStack getInstance() {
        return instance;
    }

    public int getCount() {
        return activityStack.size();
    }

    /**
     * 添加Activity到栈
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack();
        }

        activityStack.add(activity);
    }

    /**
     * 获取当前Activity(堆栈中最后一个压入的Activity)
     *
     * @return
     */
    public Activity getCurrentActivity() {
        if (activityStack == null) {
            return null;
        } else if (activityStack.isEmpty()) {
            return null;
        } else {
            Activity activity = activityStack.lastElement();
            return activity;
        }
    }

    /**
     * 获取指定类名的Activity
     *
     * @param cls
     * @return
     */
    public Activity getActivity(Class<?> cls) {
        Activity activity = null;
        Iterator<Activity> iterator = activityStack.iterator();

        while (iterator.hasNext()) {
            Activity aty = iterator.next();
            if (aty.getClass().equals(cls)) {
                activity = aty;
                break;
            }
        }

        return activity;
    }

    /**
     * 从管理栈中移除activity
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (activityStack != null && activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
    }
    /**
     * 结束当前Activity(最后压入堆栈的Activity)
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        Iterator<Activity> iterator = activityStack.iterator();

        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }

    }

    /**
     * 结束所有Activity
     */
    public void finishAll() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i) != null) {
                Activity activity = activityStack.get(i);
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
        activityStack.clear();
    }


    /**
     * 是否包含指定的Activity
     *
     * @param cls
     * @return
     */
    public boolean isContainActivity(Class<?> cls) {
        boolean isContain = false;
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                isContain = true;
                break;
            }
        }
        return isContain;
    }

    /**
     * 退出应用程序
     *
     * @param context
     */
    public void AppExit(Context context) {
        try {
            finishAll();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            Runtime.getRuntime().exit(-1);
        }

    }
}
