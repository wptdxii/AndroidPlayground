/**
 * 
 */
package com.wujay.fund.common;

import android.content.Context;
import android.view.WindowManager;

public class AppUtil {
    

    public static int[] getScreenDispaly(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = windowManager.getDefaultDisplay().getWidth();
		int height = windowManager.getDefaultDisplay().getHeight();
		int result[] = { width, height };
		return result;
	}
    
}
