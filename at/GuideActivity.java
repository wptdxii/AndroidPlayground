package com.cloudhome.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cloudhome.R;
import com.cloudhome.adapter.GuidePagerAdapter;
import com.cloudhome.utils.Common;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yangyu 功能描述：引导界面类
 */
public class GuideActivity extends BaseActivity {
    @BindView(R.id.vp_guide)
    ViewPager viewpager;
    private static final int[] pictures = new int[]{
            R.drawable.guide_one,
            R.drawable.guide_two,
            R.drawable.guide_three,
            R.drawable.guide_four
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        List<View> viewList = new ArrayList<>();
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int picture : pictures) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(layParams);
            imageView.setImageResource(picture);
            viewList.add(imageView);
        }

        GuidePagerAdapter vpAdapter = new GuidePagerAdapter(viewList);
        viewpager.setAdapter(vpAdapter);
        ImageView btnStart = (ImageView) viewList.get(viewList.size()-1);
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startbutton();
            }
        });
    }

    private void startbutton() {

        int curVersionCode = Common.getVerCode(getApplicationContext());
        SharedPreferences preferences = getSharedPreferences("isFirstUse", MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean("isFirstUse", false);
        editor.putInt("old_install_Code", curVersionCode);
        editor.commit();


        SharedPreferences preferences2 = getSharedPreferences("isFirstShowMain", MODE_PRIVATE);
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
