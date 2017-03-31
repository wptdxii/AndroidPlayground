package com.wptdxii.uiframework.callback;

/**
 * Created by wptdxii on 2017/1/4 0004.
 */

public interface PermissionListener {
    /**
     * be called when all of the permissions requested are granted
     */
    void onGranted();

    /**
     * be called when any one of the permissions requested is denied
     * @param impermanentDeniedPermissions the permissions that are granted
     * @param permanentDeniedPermissions the permissions that are denied
     */
    void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions);

    /**
     * be called when all of the permissions requested are permanent denied
     * @param permanentDeniedPermissions
     */
    void onPermanentDenied(String[] permanentDeniedPermissions);
}
