package com.cloudhome.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;

import java.util.ArrayList;
public class FunctionIntroduceActivity extends BaseActivity implements OnPageChangeListener{
	private RelativeLayout iv_back;
	private ImageView iv_pic;
	private TextView top_title;
	private ImageView iv_right;
	
	// 定义ViewPager对象
		private ViewPager viewPager;

		// 定义ViewPager适配器
		private MyViewPagerAdapter vpAdapter;

		// 定义一个ArrayList来存放View
		private ArrayList<View> views;

		// 定义各个界面View对象
		private View view1, view2, view3, view4, view5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_function_introduce);
		initView();
	}
	
	private void initView() {
		iv_pic=(ImageView) findViewById(R.id.iv_pic);
		iv_back=(RelativeLayout) findViewById(R.id.iv_back);
		iv_pic.setImageResource(R.drawable.icon_cancel);
		top_title= (TextView) findViewById(R.id.tv_text);
		iv_right=(ImageView) findViewById(R.id.iv_right);
		top_title.setText(R.string.function_introduce);
		iv_right.setVisibility(View.INVISIBLE);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 实例化各个界面的布局对象
		LayoutInflater mLi = LayoutInflater.from(this);
		view1 = mLi.inflate(R.layout.guide_view01, null);
		view2 = mLi.inflate(R.layout.guide_view02, null);
		view3 = mLi.inflate(R.layout.guide_view03, null);
	    view4 = mLi.inflate(R.layout.guide_view04_about, null);
		 
		// 实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);

		// 实例化ArrayList对象
		views = new ArrayList<View>();

		// 实例化ViewPager适配器
		vpAdapter = new MyViewPagerAdapter(views);

		
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub

		// 设置监听
		viewPager.setOnPageChangeListener(this);
		
		// 将要分页显示的View装入数组中
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		 
		// 设置适配器数据
	    viewPager.setAdapter(vpAdapter);

		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	public class MyViewPagerAdapter extends PagerAdapter {
		
		//界面列表
	    private ArrayList<View> views;
	    
	    public MyViewPagerAdapter (ArrayList<View> views){
	        this.views = views;
	    }
	       
		/**
		 * 获得当前界面数
		 */
		@Override
		public int getCount() {
			 if (views != null) {
	             return Integer.MAX_VALUE;
	         }      
	         return 0;
		}

		/**
		 * 初始化position位置的界面
		 */
	    @Override
	    public Object instantiateItem(View view, int position) {
	       
	        ((ViewPager) view).addView(views.get(position%views.size()), 0);
	       
	        return views.get(position%views.size());
	    }
	    
	    /**
		 * 判断是否由对象生成界面
		 */
		@Override
		public boolean isViewFromObject(View view, Object arg1) {
			return (view == arg1);
		}

		/**
		 * 销毁position位置的界面
		 */
	    @Override
	    public void destroyItem(View view, int position, Object arg2) {
	        ((ViewPager) view).removeView(views.get(position%views.size()));       
	    }
	}
	

}
