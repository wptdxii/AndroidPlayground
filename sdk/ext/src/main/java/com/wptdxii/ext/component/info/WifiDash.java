package com.wptdxii.ext.component.info;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.wptdxii.ext.Ext;
import com.wptdxii.ext.util.StringUtils;


/**
 * WIFI网卡信息收集类
 * <p>
 * Created by zhaiyifan on 2015/7/31.
 */
public class WifiDash {
    /**
     * 获得当前接入点的BSSID<br>
     * <br>
     * <i>BSSID可以作为WIFI接入点的唯一标识</i>
     *
     * @return 形如MAC地址的字符串，{@code XX:XX:XX:XX:XX:XX}
     * @see WifiInfo
     */
    public static String getBSSID() {
        WifiManager wifiManager = (WifiManager) Ext.getContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) {
            return null;
        }

        WifiInfo wifiInfo = null;

        try {
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            wifiInfo = null;
        }

        if (wifiInfo == null) {
            return null;
        }

        String bssid = wifiInfo.getBSSID();

        if (StringUtils.NOT_AVAILABLE.equals(bssid) || "00:00:00:00:00:00".equals(bssid)
                || "FF:FF:FF:FF:FF:FF".equalsIgnoreCase(bssid)) {
            return null;
        } else {
            return bssid;
        }
    }

    public static String getSSID() {
        WifiManager wifiManager = (WifiManager) Ext.getContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) {
            return null;
        }

        WifiInfo wifiInfo = null;

        try {
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            wifiInfo = null;
        }

        if (wifiInfo == null) {
            return null;
        }

        return wifiInfo.getSSID();
    }

    public static int getNetworkId() {
        WifiManager wifiManager = (WifiManager) Ext.getContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) {
            return -1;
        }

        WifiInfo wifiInfo = null;

        try {
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            wifiInfo = null;
        }

        if (wifiInfo == null) {
            return -1;
        }
        return wifiInfo.getNetworkId();

    }

    public static int getSignalLevel() {
        Object wifiInfo = queryWifiInfo(StringUtils.NOT_AVAILABLE);

        if (wifiInfo == StringUtils.NOT_AVAILABLE) {
            return -1;
        }

        return WifiManager.calculateSignalLevel(((WifiInfo) wifiInfo).getRssi(), 5);
    }

    private static Object queryWifiInfo(Object defValue) {
        WifiManager wifiManager = (WifiManager) Ext.getContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) {
            return defValue;
        }

        WifiInfo wifiInfo = null;

        try {
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            wifiInfo = null;
        }

        if (wifiInfo == null) {
            return defValue;
        }

        return wifiInfo;
    }

    public static String getWifiInfo() {
        WifiManager wifiManager = (WifiManager) Ext.getContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) {
            return "[-]";
        }

        WifiInfo wifiInfo = null;

        try {
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            wifiInfo = null;
        }

        if (wifiInfo == null) {
            return "[-]";
        }

        String ssid = wifiInfo.getSSID();

        String signal = String.valueOf(WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5));

        String speed = String.valueOf(wifiInfo.getLinkSpeed()) + " " + WifiInfo.LINK_SPEED_UNITS;

        String bssid = wifiInfo.getBSSID();

        StringBuffer buffer = new StringBuffer();

        buffer.append('[').append(signal).append(", ").append(ssid).append(", ").append(speed).append(", ")
                .append(bssid).append(']');

        return buffer.toString();
    }
}
