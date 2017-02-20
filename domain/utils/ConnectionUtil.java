package com.cloudhome.utils;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.cloudhome.activity.AllPageActivity;

public class ConnectionUtil {

	private static SharedPreferences sp;
	private static SharedPreferences sp2;
	private static SharedPreferences sp3;
	
	/*
     * 判断网络连接是否已开
     * 2012-08-20
     *true 已打开  false 未打开
     * */
    public static boolean isConn(Context context){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }
    
    /*
     * 打开设置网络界面
     * */
    public static void setNetworkMethod(final Context context){
    	
    	sp = context.getSharedPreferences("userInfo", 0);
		sp2 = context.getSharedPreferences("otherinfo", 0);
		sp3 = context.getSharedPreferences("expertmicro", 0);
        //提示对话框
        AlertDialog.Builder builder=new Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent=null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本 
                if(android.os.Build.VERSION.SDK_INT>10){
                    intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                }else{
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                
                
            	Editor edit;
				edit = sp.edit();
				edit.clear();
				edit.commit();

				Editor edit2;
				edit2 = sp2.edit();
				edit2.clear();
				edit2.commit();
				Editor edit3;
				edit3 = sp3.edit();
				edit3.clear();
				edit3.commit();
				Intent intent = new Intent(context,AllPageActivity.class);
				
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//				context.startActivity(intent);
//				((Activity)context).finish();

			
				
            }
        }).show();
    }
	
}
