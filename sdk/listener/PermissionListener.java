package com.cloudhome.listener;

/**
 * Created by wptdxii on 2017/1/4 0004.
 */

public interface PermissionListener {
    void onGranted();

    void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions);

    void onPermanentDenied(String[] permanentDeniedPermissions);
}
