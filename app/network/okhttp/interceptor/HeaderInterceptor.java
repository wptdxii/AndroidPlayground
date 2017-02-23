package com.cloudhome.network.okhttp.interceptor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.cloudhome.application.MyApplication;
import com.cloudhome.utils.Common;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wptdxii on 2016/12/29 0029.
 */

public class HeaderInterceptor implements Interceptor {
    private static final String USER_AGENT_HEADER_NAME = "User-Agent";
    private static final String DEVICE_ID_HEADER_NAME = "x-device-id";
    private static final String VERSION_NAME_HEADER_NAME = "x-app-version";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request requestAddUserAgent = originRequest.newBuilder()
                .removeHeader(USER_AGENT_HEADER_NAME)
                .addHeader(USER_AGENT_HEADER_NAME, getUserAgent())
                .addHeader(DEVICE_ID_HEADER_NAME, getDeviceId())
                .addHeader(VERSION_NAME_HEADER_NAME, Common.getVerName(MyApplication.getInstance()))
                .build();
        return chain.proceed(requestAddUserAgent);
    }

    private String getUserAgent() {
        String userAgent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            userAgent = "Mozilla/5.0 (Linux; Android " + Build.VERSION.RELEASE + "; " +
                    Build.MODEL + " Build/" + Build.DISPLAY + "; wv) " + "AppleWebKit/537.36 " +
                    "(KHTML, like Gecko)" + " Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36";
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            userAgent = "Mozilla/5.0 (Linux; Android " + Build.VERSION.RELEASE + "; " +
                    Build.MODEL + " Build/" + Build.DISPLAY + ") " + "AppleWebKit/537.36 " +
                    "(KHTML, like Gecko)" + " Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
        } else {

            userAgent = "Mozilla/5.0 (Linux; U; Android " + Build.VERSION.RELEASE + "; " +
                    Build.MODEL + " Build/" + Build.DISPLAY + ") " + "AppleWebKit/534.30 " +
                    "(KHTML, like Gecko)" + " Version/4.0 Safari/534.30";
        }

        return userAgent;
    }

    private String getDeviceId() {
        String deviceId;
        if (ContextCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            deviceId = "";
        } else {
            TelephonyManager tm = (TelephonyManager) (MyApplication.getInstance().getBaseContext())
                    .getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId() + "";
        }
        return deviceId;
    }
}
