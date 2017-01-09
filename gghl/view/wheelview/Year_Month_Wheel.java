package com.gghl.view.wheelview;

import android.view.View;

import com.cloudhome.R;

import java.util.Arrays;
import java.util.List;

public class Year_Month_Wheel {

	private View view;
	private WheelView wv_year;
	private WheelView wv_month;
	public int screenheight;
	private boolean hasSelectTime;
	private static int START_YEAR = 2012, END_YEAR = 2050;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public static int getSTART_YEAR() {
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR) {
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR() {
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR) {
		END_YEAR = eND_YEAR;
	}

	public Year_Month_Wheel(View view) {
		super();
		this.view = view;
		hasSelectTime = false;
		setView(view);
	}

	public Year_Month_Wheel(View view, boolean hasSelectTime) {
		super();
		this.view = view;
		this.hasSelectTime = hasSelectTime;
		setView(view);
	}

	public void initDateTimePicker(int year, int month, int day) {
		this.initDateTimePicker(year, month, day, 0, 0);
	}

	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	public void initDateTimePicker(final int year, final int month,
			final int day, int h, int m) {
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH);
		// int day = calendar.get(Calendar.DATE);
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month-1);

	

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				int year_num = newValue + START_YEAR;
				if (year_num >= year) {
				
					
					// 年
					wv_year.setAdapter(new NumericWheelAdapter(START_YEAR,
							END_YEAR));// 设置"年"的显示数据
					wv_year.setCyclic(true);// 可循环滚动
					wv_year.setLabel("年");// 添加文字
					wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

					wv_month.setAdapter(new NumericWheelAdapter(1, 12));
					wv_month.setCyclic(true);
					wv_month.setLabel("月");
				

					wv_month.setCurrentItem(month-1);
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				System.out.println("11111" + newValue);
				int current_y = wv_year.getCurrentItem() + START_YEAR;
		
				if (current_y == year && month_num > month) {
					
					
					wv_month.setAdapter(new NumericWheelAdapter(1, 12));
					wv_month.setCyclic(true);
					wv_month.setLabel("月");
					wv_month.setCurrentItem(month-1);

				} 
				
			}
		};


		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = 0;
		if (hasSelectTime)
			textSize = (screenheight / 100) * 3;
		else
			textSize = (screenheight / 100) * 4;
		
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		
		if((wv_month.getCurrentItem() + 1)>9)
		{
		sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
				.append((wv_month.getCurrentItem() + 1));

		}else{
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append("0")
			.append((wv_month.getCurrentItem() + 1));
		}
	return sb.toString();
	}
	
	public String getYear() {

		return String.valueOf((wv_year.getCurrentItem() + START_YEAR));
	}
	
	public String getMonth() {


//		if((wv_month.getCurrentItem() + 1)>9)
//		{
		//
//		}else{
//			sb.append("0").append((wv_month.getCurrentItem() + 1));
//		}
//		
	

	return String.valueOf((wv_month.getCurrentItem() + 1));
	}
	
}
