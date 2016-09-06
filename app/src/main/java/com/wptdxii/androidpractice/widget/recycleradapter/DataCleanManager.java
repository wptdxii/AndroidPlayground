package com.wptdxii.androidpractice.widget.recycleradapter;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by zc on 2015/10/30.
 */
public class DataCleanManager {

    public static String getTotalCacheSize(Context context) throws Exception {
//        String path = Environment.getExternalStorageDirectory() + "/ubaby/cache/";
        File file = new File(FileUtils.CACHE_PATH);
        long cacheSize = getFolderSize(file);
        cacheSize += getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return FileUtils.getFormatSize(cacheSize);
    }


    public static void clearAllCache(Context context) {
//        String path = Environment.getExternalStorageDirectory() + "/ubaby/cache/";
        File file = new File(FileUtils.CACHE_PATH);
        deleteDir(file);
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

}
