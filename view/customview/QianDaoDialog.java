package com.cloudhome.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by bkyj-005 on 2016/8/22.
 */
public class QianDaoDialog extends Dialog{
    private static int default_width = 160; //默认宽度
    private static int default_height = 120;//默认高度

//    public QianDaoDialog(Context context, View layout, int style) {
//        this(context, default_width, default_height, layout, style);
//    }

    public QianDaoDialog(Context context,View layout,int width,int height, int style) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width=width;
        params.height=height;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


}
