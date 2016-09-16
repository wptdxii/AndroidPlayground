package com.wptdxii.ext.util;

/**
 * 提供简单的类似junit的assert功能，如果错误会抛出AssertionError
 *
 * Created by wptdxii on 2016/9/15 0015.
 */
public final class AssertUtils {

    private AssertUtils() {
        // static usage.
    }

    /**
     * Assert condition to be true.
     *
     * @param cond Condition.
     */
    public static void assertTrue(boolean cond) {
        if (!cond) {
            throw new AssertionError();
        }
    }

    /**
     * Assert condition to be true.
     *
     * @param cond Condition.
     * @param msg  Assert msg.
     */
    public static void assertTrue(boolean cond, String msg) {
        if (!cond) {
            throw new AssertionError(msg);
        }
    }
}