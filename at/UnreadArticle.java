package com.cloudhome.activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.cloudhome.application.MyApplication;
import com.cloudhome.event.UnreadArticleEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetUnreadArticle;
import com.cloudhome.utils.Constants;

import org.greenrobot.eventbus.EventBus;


/**
 * 获取未读文章数目
 * Created by bkyj-005 on 2016/12/13.
 */

public class UnreadArticle implements NetResultListener {
    private Context mContext;
    private boolean mStatus;

    /**
     *
     * @param status true 表示获取未读数，false 表示清除未读
     * @param context
     */
    public UnreadArticle(boolean status, Context context) {
        this.mStatus = status;
        this.mContext = context;
    }

    public void getUnreadArticle() {
        SharedPreferences sp = mContext.getSharedPreferences("userInfo", 0);
        String loginString = sp.getString(Constants.SP.LOGIN_STATE, "none");
        String userId = sp.getString(Constants.SP.USER_ID, "");
        String token = sp.getString(Constants.SP.TOKEN, "");
        if (!loginString.equals("none")) {
            GetUnreadArticle getUnreadArticle = new GetUnreadArticle(UnreadArticle.this);
            getUnreadArticle.execute(userId, token, mStatus);
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        if (flag == MyApplication.DATA_OK) {
            int num = Integer.parseInt(dataObj.toString());
            EventBus.getDefault().post(new UnreadArticleEvent(num));
        }
    }
}
