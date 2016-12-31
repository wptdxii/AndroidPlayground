package com.cloudhome.utils;

import java.io.File;

/**
 * Created by wptdxii on 2016/9/23 0023.
 */

public class AdFileUtils {
    public static String getImgName(String imgUrl) {
        if (imgUrl == null || imgUrl.equals("")) {
            return "";
        }
        String[] strs = imgUrl.split("/");
        return strs[strs.length - 1];
    }

    public static void deleteFile(String path){
        deleteFile(new File(path));
    }

    public static void deleteFile(File oldPath) {
        try {
            if (oldPath.isDirectory()) {
                File[] files = oldPath.listFiles();
                for (File file : files) {
                    deleteFile(file);
                    file.delete();
                }
            } else {
                oldPath.delete();
            }
        } catch (Exception e) {

        }
    }
}
