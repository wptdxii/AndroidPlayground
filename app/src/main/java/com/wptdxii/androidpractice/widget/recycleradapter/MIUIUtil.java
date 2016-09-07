package com.wptdxii.androidpractice.widget.recycleradapter;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by fuhongjie on 4/13/16.
 */
public class MIUIUtil {



    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

    public static boolean isMIUIRom() {
        String property = MIUIUtil.getSystemProperty("ro.miui.ui.version.name");
        return !TextUtils.isEmpty(property);
    }


    public static String getMIUIProperty() {
        String line = null;
        BufferedReader reader = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name" );
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = reader.readLine();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Unknow";
    }
}
