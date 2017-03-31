package com.cloudhome.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.CyclePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * @author wptdxii  2017/03/02
 */
public class FunctionIntroduceActivity extends BaseActivity {
    @BindView(R.id.vp_guide)
    ViewPager vpGuide;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    private static final int[] pictures = new int[]{
            R.drawable.guide_one,
            R.drawable.guide_two,
            R.drawable.guide_three,
            R.drawable.guide_four_about
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_introduce);
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
        CyclePagerAdapter cyclePagerAdapter = new CyclePagerAdapter(viewList);
        vpGuide.setAdapter(cyclePagerAdapter);

        ivBack.setImageResource(R.drawable.icon_cancel);
        tvTitle.setText(R.string.function_introduce);
        ivShare.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }
}
