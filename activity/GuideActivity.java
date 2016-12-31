package com.cloudhome.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cloudhome.R;
import com.cloudhome.adapter.ViewPagerAdapter;
import com.cloudhome.utils.Common;

import java.util.ArrayList;
/**
 * @author yangyu 功能描述：引导界面activity类
 */
public class GuideActivity extends BaseActivity implements OnPageChangeListener {
    // 定义ViewPager对象
    private ViewPager viewPager;

    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;

    // 定义一个ArrayList来存放View
    private ArrayList<View> views;

    // 定义各个界面View对象
    private View view1, view2, view3, view4, view5;

    // 定义开始按钮对象
    private ImageView startBt;
    // 是否是第一次使用
    private boolean isFirstUse;
    private int old_install_Code, current_versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);


        SharedPreferences preferences = getSharedPreferences("isFirstUse", 1);
        // 读取SharedPreferences中需要的数据
        isFirstUse = preferences.getBoolean("isFirstUse", true);

        // Common.getVerCode 中包名一定要与本应用包名一致
        current_versionCode = Common.getVerCode(getApplicationContext());
        old_install_Code = preferences.getInt("old_install_Code", 0);


       initView();
    }


    /**
     * 初始化组件
     */
    private void initView() {
        // 实例化各个界面的布局对象
        LayoutInflater mLi = LayoutInflater.from(this);
        view1 = mLi.inflate(R.layout.guide_view01, null);
        view2 = mLi.inflate(R.layout.guide_view02, null);
        view3 = mLi.inflate(R.layout.guide_view03, null);
        view4 = mLi.inflate(R.layout.guide_view04, null);

        // 实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // 实例化ArrayList对象
        views = new ArrayList<View>();

        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);

        // 实例化开始按钮
        startBt = (ImageView) view4.findViewById(R.id.start_img);

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 设置监听
        viewPager.setOnPageChangeListener(this);

        // 将要分页显示的View装入数组中
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);

        // 设置适配器数据
        viewPager.setAdapter(vpAdapter);

        // 给开始按钮设置监听
        startBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startbutton();
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {

    }


    /**
     * 相应按钮点击事件
     */
    private void startbutton() {


        SharedPreferences preferences = getSharedPreferences("isFirstUse", 1);
        // 实例化Editor对象
        Editor editor = preferences.edit();
        // 存入数据
        editor.putBoolean("isFirstUse", false);

        editor.putInt("old_install_Code", current_versionCode);
        // 提交修改
        editor.commit();


        SharedPreferences preferences2 = getSharedPreferences("isFirstShowMain", 1);
        Editor editor2 = preferences2.edit();
        editor2.putBoolean("isFirstShowMain", true);
        editor2.commit();

        cancelFullScreen();
        Intent intent = new Intent(this, AllPageActivity.class);
        startActivity(intent);
        finish();
    }

    private void cancelFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    }
}
