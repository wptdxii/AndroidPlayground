package com.cloudhome.utils;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by wptdxii on 2017/1/5 0005.
 */

public class IntentUtils {
    private IntentUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static Intent getAppDetailsSettingsIntent(String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
