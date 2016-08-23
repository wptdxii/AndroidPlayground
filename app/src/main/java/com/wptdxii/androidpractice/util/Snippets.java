package com.wptdxii.androidpractice.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wptdxii.androidpractice.R;

/**
 * Created by wptdxii on 2016/7/30 0030.
 */
public class Snippets {
    /**
     * show a AlertDialot   a ActivityContext required
     * 兼容旧版本 android.support.v7.app.AlertDialog
     *
     * @param activity
     */
    public void showAlertDialot(Activity activity) {
        AlertDialog builder = new AlertDialog.Builder(activity)
                .setTitle("Title")
                .setMessage("AlertDialog Content")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO
                    }
                })
                .show();
    }


    /**
     * 添加依赖  compile 'com.android.support:design:24.1.1'
     *
     * @param view 当前布局中的任意一个view，Snackbar可以通过此view查找最外层的布局
     */
    public void showSnackbar(View view) {
        Snackbar.make(view, "Snackbar Content", Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.WHITE)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        // TODO
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                        // TODO
                    }
                })
                .setAction("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO
                    }
                })
                .show();


        //自定义Snackbar
        Snackbar snackbar = Snackbar.make(view, "Snackbar Content", Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.BLACK);//改变背景颜色
        ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
        CoordinatorLayout.LayoutParams coordinatorLayoutParams = new CoordinatorLayout.LayoutParams(layoutParams.width, layoutParams.height);
        coordinatorLayoutParams.gravity = Gravity.CENTER;//居中显示 
        snackbarView.setLayoutParams(coordinatorLayoutParams);
        ((TextView) snackbarView.findViewById(R.id.snackbar_text)).setTextColor(Color.RED);//设置内容文本颜色
        snackbarView.setBackgroundColor(Color.GRAY);//设置snackbar背景
        //snackbarView.setAnimation(); //设置动画
        //设置icon
        //Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbarView;
        //layout.addView(icon);
        snackbar.setActionTextColor(Color.WHITE);  //设置按钮颜色
        //snackbar.setAction();
        // snackbar.setCallback();
        snackbar.show();
    }

    /**
     * 设置为全屏显示
     * 在Activity中调用
     */
    private void setFullScreen() {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 退出全屏显示
     */
    private void cancelFullScreen() {
       // SplashActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
         //       WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    }

}
