package com.cloudhome.activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.cloudhome.application.MyApplication;
import com.cloudhome.event.UnreadArticleEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetUnreadArticle;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by bkyj-005 on 2016/12/13.
 */

public class UnreadArticle implements NetResultListener {
    //获取未读文章数目
    private GetUnreadArticle getUnreadArticle;
    private String user_id="";
    private String token="";
    private String loginString="";
    private SharedPreferences sp;
    private boolean status;//true=获取未读数，false=清除未读
    private Context context;


    public UnreadArticle(boolean status,Context context) {
        this.status = status;
        this.context=context;
    }

    public void getUnreadArticle(){
        sp = context.getSharedPreferences("userInfo", 0);
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        if(!loginString.equals("none")){
            getUnreadArticle=new GetUnreadArticle(UnreadArticle.this);
            getUnreadArticle.execute(user_id,token,status);
        }

    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        if (flag == MyApplication.DATA_OK) {
            int num= Integer.parseInt(dataObj.toString());
            EventBus.getDefault().post(new UnreadArticleEvent(num));
        } else if (flag == MyApplication.NET_ERROR) {
        } else if (flag == MyApplication.DATA_EMPTY) {
        } else if (flag == MyApplication.JSON_ERROR) {
        } else if (flag == MyApplication.DATA_ERROR) {
        }
    }
}
