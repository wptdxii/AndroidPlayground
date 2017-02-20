package com.cloudhome.utils;
/**
 * @author harvic
 * @date 2014-5-7
 * @address http://blog.csdn.net/harvic880925
 */
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.WindowManager;

public class Common {

	/**
	 * 获取软件版本号
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
        int verCode = -1;
        try {
        	//注意："com.example.try_downloadfile_progress"对应AndroidManifest.xml里的package="……"部分
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
        	Log.e("msg",e.getMessage());
        }
        return verCode;
    }
	
   /**
    * 获取版本名称
    * @param context
    * @return
    */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    "com.cloudhome", 0).versionName;
        } catch (NameNotFoundException e) {
        	Log.e("msg",e.getMessage());
        }
        return verName;   
}	
    
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }



    /**
     * 获取手机状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
    	  int result = 0;
    	  int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    	  if (resourceId > 0) {
    	      result = context.getResources().getDimensionPixelSize(resourceId);
    	  }
    	  return result;
    	}
	
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }

    /**
     * 获取手机宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }
    /**
     * 获取手机高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }
}