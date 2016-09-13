package com.wptdxii.ext.util;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * SD卡相关的辅助类
 */
public class SDCardUtils {
    private SDCardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getAllAvailableBytes() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks;
            // 获取单个数据块的大小（byte）
            long blockSize;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                availableBlocks = stat.getAvailableBlocksLong();
                blockSize = stat.getBlockSizeLong();
            } else {
                availableBlocks = (long) stat.getAvailableBlocks();
                blockSize = stat.getBlockSize();
            }
            return blockSize * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getAvailableBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks;
        long blockSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
            blockSize = stat.getBlockSizeLong();
        } else {
            availableBlocks = (long) stat.getAvailableBlocks();
            blockSize = stat.getBlockSize();
        }
        return blockSize * availableBlocks;
    }

    /**
     * 获取SD的全部容量大小 返回 bytes
     * @return
     */
    public  static long getAllBytes() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long allBlocks;
            // 获取单个数据块的大小（byte）
            long blockSize;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                allBlocks = stat.getBlockCountLong();
                blockSize = stat.getAvailableBlocksLong();
            } else {
                allBlocks = (long) stat.getBlockCount();
                blockSize = stat.getAvailableBlocks();
            }
            return blockSize * allBlocks;
        }
        return 0;
    }
    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }


}
