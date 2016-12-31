package com.cloudhome.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cloudhome.bean.SplashAdBean;

/**
 * Created by wptdxii on 16/09/23.
 */
public class AdPreference {

    private static final String SPLASH_AD = "splashAd";

    private static Context mContext;

    public AdPreference(Context context) {
        mContext = context;
    }

    private static AdPreference adPreference = new AdPreference(mContext);

    public static synchronized AdPreference getInstance() {
        return adPreference;
    }

    public void saveSplashAdPage(SplashAdBean.DataBean data) {
        SharedPreferences preferences = mContext.getSharedPreferences(SPLASH_AD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (data.getBrief() != null ) {
            editor.putString("brief", data.getBrief());
        }
        if (data.getImg() != null) {
            editor.putString("img", data.getImg());
        }
        if (data.getIsShare() != -1) {
            editor.putInt("isShare", data.getIsShare());
        }
        if (data.getShare_img() != null ) {
            editor.putString("share_img", data.getShare_img());
        }
        if (data.getTitle() != null ) {
            editor.putString("title", data.getTitle());
        }
        if (data.getUrl() != null) {
            editor.putString("url", data.getUrl());
        }
        editor.apply();
    }

    public SplashAdBean.DataBean getSplashAdPage() throws ClassCastException {
        SharedPreferences preferences = mContext.getSharedPreferences(SPLASH_AD, Context.MODE_PRIVATE);
        SplashAdBean.DataBean data = new SplashAdBean.DataBean();
        data.setBrief(preferences.getString("brief",""));
        data.setImg(preferences.getString("img",""));
        data.setIsShare(preferences.getInt("isShare", 1));
        data.setShare_img(preferences.getString("share_img",""));
        data.setTitle(preferences.getString("title",""));
        data.setUrl(preferences.getString("url",""));
        return data;
    }

    public void saveIfShowAd(boolean isAdShow) {
        SharedPreferences preferences = mContext.getSharedPreferences(SPLASH_AD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isAdShow", isAdShow);
        editor.apply();
    }

    public boolean getIfShowAd() {
        SharedPreferences preferences = mContext.getSharedPreferences(SPLASH_AD, Context.MODE_PRIVATE);
        return preferences.getBoolean("isAdShow", true);
    }

    public void clear() {
        SharedPreferences preferences = mContext.getSharedPreferences(SPLASH_AD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
