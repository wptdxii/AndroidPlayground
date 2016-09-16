package com.wptdxii.ext.component.info;

import android.os.Environment;

import com.wptdxii.ext.Ext;


/**
 * 存储器信息收集类
 * <p/>
 * Created by wptdxii on 2016/9/15 0015.
 */
public class StorageDash {

    private static final String TAG = "StorageDash";

    /**
     * 是否有外部存储
     */
    public static boolean hasExternal() {
        try {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        } catch (Exception e) {
//            Logger.w(TAG, "hasExternal exception" + e.toString());
        }
        return false;
    }

    /**
     * 是否有只读的外部存储
     */
    public static boolean hasExternalReadable() {
        try {
            String state = Environment.getExternalStorageState();

            return Environment.MEDIA_MOUNTED.equals(state) || (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
        } catch (Exception e) {
//            Logger.w(TAG, "hasExternalReadable exception" + e.toString());
        }
        return false;
    }

    /**
     * 获得外部存储器的信息
     */
    public static StorageInfo getExternalInfo() {
        if (!hasExternalReadable()) {
            return null;
        }

        return StorageInfo.fromFile(Environment.getExternalStorageDirectory());
    }

    /**
     * 获得内部存储器的信息
     */
    public static StorageInfo getInnerInfo() {
        return StorageInfo.fromFile(Ext.getContext().getFilesDir());
    }
}
