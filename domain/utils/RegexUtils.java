package com.cloudhome.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bkyj-005 on 2016/10/31.
 */

public class RegexUtils {

    public static boolean isMobileNO(String mobiles) {
        if (mobiles.length() != 11) {
            return false;
        }
        String mobileRegex = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$";
        Pattern p = Pattern.compile(mobileRegex);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
