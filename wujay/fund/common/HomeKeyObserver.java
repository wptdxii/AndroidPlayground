package com.wujay.fund.common;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
 
public class HomeKeyObserver {
    private Context mContext;
    private IntentFilter mIntentFilter;
    private OnHomeKeyListener mOnHomeKeyListener;
    private HomeKeyBroadcastReceiver mHomeKeyBroadcastReceiver;
    public HomeKeyObserver(Context context) {
        this.mContext = context;
    }
     
    //注册广播接收者
    public void startListen(){
        mIntentFilter=new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mHomeKeyBroadcastReceiver=new HomeKeyBroadcastReceiver();
        mContext.registerReceiver(mHomeKeyBroadcastReceiver, mIntentFilter);
        System.out.println("----> 开始监听");
    }
     
    //取消广播接收者
    public void stopListen(){
        if (mHomeKeyBroadcastReceiver!=null) {
            mContext.unregisterReceiver(mHomeKeyBroadcastReceiver);
            System.out.println("----> 停止监听");
        }
    }
     
    // 对外暴露接口
    public void setHomeKeyListener(OnHomeKeyListener homeKeyListener) {
        mOnHomeKeyListener = homeKeyListener;
    }
 
    // 回调接口
    public interface OnHomeKeyListener {
        public void onHomeKeyPressed();
        public void onHomeKeyLongPressed();
    }
 
    //广播接收者
    class HomeKeyBroadcastReceiver extends BroadcastReceiver{
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        //按下Home键
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        //长按Home键
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null && mOnHomeKeyListener != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        mOnHomeKeyListener.onHomeKeyPressed();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        mOnHomeKeyListener.onHomeKeyLongPressed();
                    }
                }
            }
        }
    }
      
}