package com.cloudhome.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.activity.AddAdditionalInsuranceActivity;
import com.cloudhome.bean.SubmitInsurancePlanBean;
import com.cloudhome.view.customview.ListDialog;

import java.util.ArrayList;

public class AddAdditionalInsuranceAdapter extends BaseAdapter {

	private Context context;
	private AddAdditionalInsuranceActivity activity;
	
	
	

	public AddAdditionalInsuranceAdapter(Context context) {
		super();
		this.context = context;
		activity=(AddAdditionalInsuranceActivity)context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return activity.additionalInsuranceList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return activity.additionalInsuranceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 final ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_add_additional_insurance, parent,false);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
			
		}
		
		final int indexPosition=position;
		holder.rl_outter_top=(RelativeLayout) convertView.findViewById(R.id.rl_outter_top);
		
		holder.rl_item_all=(RelativeLayout) convertView.findViewById(R.id.rl_item_all);
		holder.tv_insurance_title=(TextView) convertView.findViewById(R.id.tv_insurance_title);
		holder.iv_check=(ImageView) convertView.findViewById(R.id.iv_check);
		
		//职业
		holder.rl_item_insurance_job=(RelativeLayout) convertView.findViewById(R.id.rl_item_insurance_job);
		holder.tv_insurance_job_left=(TextView) convertView.findViewById(R.id.tv_insurance_job_left);
		holder.tv_insurance_job_middle=(TextView) convertView.findViewById(R.id.tv_insurance_job_middle);
		//保险期间
		holder.rl_item_insurance_period=(RelativeLayout) convertView.findViewById(R.id.rl_item_insurance_period);
		holder.tv_insurance_period_left=(TextView) convertView.findViewById(R.id.tv_insurance_period_left);
		holder.tv_insurance_period_middle=(TextView) convertView.findViewById(R.id.tv_insurance_period_middle);
		//缴费频率
		holder.rl_item_pay_frequency=(RelativeLayout) convertView.findViewById(R.id.rl_item_pay_frequency);
		holder.tv_pay_frequency_left=(TextView) convertView.findViewById(R.id.tv_pay_frequency_left);
		holder.tv_pay_frequency_year=(TextView) convertView.findViewById(R.id.tv_pay_frequency_year);
		//缴费期间
		holder.rl_item_pay_time=(RelativeLayout) convertView.findViewById(R.id.rl_item_pay_time);		
		holder.tv_pay_time_left=(TextView) convertView.findViewById(R.id.tv_pay_time_left);
		holder.tv_pay_time_year=(TextView) convertView.findViewById(R.id.tv_pay_time_year);
		//计划
		holder.rl_item_plan=(RelativeLayout) convertView.findViewById(R.id.rl_item_plan);
		holder.tv_plan_left=(TextView) convertView.findViewById(R.id.tv_plan_left);
		holder.tv_plan_type=(TextView) convertView.findViewById(R.id.tv_plan_type);
		
		holder.tv_insurance_money_left=(TextView) convertView.findViewById(R.id.tv_insurance_money_left);
		holder.ed_insurance_money=(EditText) convertView.findViewById(R.id.ed_insurance_money);
		holder.tv_insurance_warn=(TextView) convertView.findViewById(R.id.tv_insurance_warn);
		
		
		final SubmitInsurancePlanBean bean=activity.additionalInsuranceList.get(position);
		holder.tv_insurance_title.setText(bean.getInsuranceTitle());
		holder.tv_insurance_money_left.setText(bean.getMoneyLeft());
		holder.ed_insurance_money.setHint(bean.getInsuranceHint());
		
		//item是否显示
	
		if(bean.isPeriodShow()){
			holder.rl_item_insurance_period.setVisibility(View.VISIBLE);
			holder.tv_insurance_period_left.setText(bean.getPeriodLeftString());
			holder.tv_insurance_period_middle.setText(bean.getPeriodMiddleString());
		}else{
			holder.rl_item_insurance_period.setVisibility(View.GONE);
		}
		if(bean.isPayTimeShow()){
			holder.rl_item_pay_time.setVisibility(View.VISIBLE);
			holder.tv_pay_time_left.setText(bean.getPayTimeLeftString());
			holder.tv_pay_time_year.setText(bean.getPayTimeMiddleString());
		}else{
			holder.rl_item_pay_time.setVisibility(View.GONE);
		}
		if(bean.isFrequencyShow()){
			holder.rl_item_pay_frequency.setVisibility(View.VISIBLE);
			holder.tv_pay_frequency_left.setText(bean.getFrequencyLeftString());
			holder.tv_pay_frequency_year.setText(bean.getFrequencyMiddleString());
		}else{
			holder.rl_item_pay_frequency.setVisibility(View.GONE);
		}
		if(bean.isPlanShow()){
			holder.rl_item_plan.setVisibility(View.VISIBLE);
			holder.tv_plan_left.setText(bean.getPlanLeftString());
			holder.tv_plan_type.setText(bean.getPlanMiddleString());
		}else{
			holder.rl_item_plan.setVisibility(View.GONE);
		}
		
		
		
		if(bean.isShowWarn()){
			holder.tv_insurance_warn.setVisibility(View.VISIBLE);
			holder.tv_insurance_warn.setText(bean.getWarnText());
		}else{
			holder.tv_insurance_warn.setVisibility(View.GONE);
			holder.tv_insurance_warn.setText("");
		}
		
		
		//根据SubmitInsurancePlanBean记录的check值来控制是否选中
		if(bean.isCheck()){
			holder.iv_check.setImageResource(R.drawable.xuanzhong);
			holder.rl_item_all.setVisibility(View.VISIBLE);
		}else{
			holder.iv_check.setImageResource(R.drawable.weixuan);
			holder.rl_item_all.setVisibility(View.GONE);
		}
		
		
		//是否勾选
		holder.iv_check.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(bean.isCheck()){
					holder.iv_check.setImageResource(R.drawable.weixuan);
					holder.rl_item_all.setVisibility(View.GONE);
					activity.additionalInsuranceList.get(position).setCheck(false);
					
				}else{
					holder.iv_check.setImageResource(R.drawable.xuanzhong);
					holder.rl_item_all.setVisibility(View.VISIBLE);
					activity.additionalInsuranceList.get(position).setCheck(true);
				}
			}
		});
		
		final ViewHolder holder2=holder;
		//保险期间点击事件
		holder.rl_item_insurance_period.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.i("点击了保险期间-------------", "第"+indexPosition+"个条目");
				ArrayList<String> var = bean.getInsurancePeriodStringList();
				final String[] insurancePeriodArray= var.toArray(new String[var.size()]);
				ListDialog dialog0=new ListDialog(context,insurancePeriodArray,"请选择保险期限") {
					@Override
					public void item(int m) {
						Toast.makeText(context, "选择了"+insurancePeriodArray[m]+bean.getInsurancePeriodCodeList().get(m), Toast.LENGTH_SHORT).show();
						holder2.tv_insurance_period_middle.setText(insurancePeriodArray[m]);
						activity.additionalInsuranceList.get(position).setPeriodCheckItem(m);
						activity.additionalInsuranceList.get(position).setPeriodMiddleString(insurancePeriodArray[m]);
						activity.additionalInsuranceList.get(position).setPeriodValueCode(bean.getInsurancePeriodCodeList().get(m));
					}
				};
			
			}
		});
		
		//缴费频率点击事件
		holder.rl_item_pay_frequency.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<String> insuranceFrequencyStringList=bean.getInsuranceFrequencyStringList();
				final ArrayList<String> insuranceFrequencyCodeList=bean.getInsuranceFrequencyCodeList();
				final String[] insuranceFrequencyArray= insuranceFrequencyStringList.toArray(new String[insuranceFrequencyStringList.size()]);
				ListDialog dialog0=new ListDialog(context,insuranceFrequencyArray,"请选择交费方式") {
					@Override
					public void item(int m) {
						Toast.makeText(context, "选择了"+insuranceFrequencyArray[m]+insuranceFrequencyCodeList.get(m), Toast.LENGTH_SHORT).show();
						holder2.tv_pay_frequency_year.setText(insuranceFrequencyArray[m]);
						activity.additionalInsuranceList.get(position).setFrequencyCheckItem(m);
						activity.additionalInsuranceList.get(position).setFrequencyMiddleString(insuranceFrequencyArray[m]);
						activity.additionalInsuranceList.get(position).setFrequencyValueCode(insuranceFrequencyCodeList.get(m));
					}
				};
			}
		});
		//缴费期限点击事件
		holder.rl_item_pay_time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<String> insurancePayTimeStringList=bean.getInsurancePayTimeStringList();
				final ArrayList<String> insurancePayTimeCodeList=bean.getInsurancePayTimeCodeList();
				final String[] insurancePayTimeArray= insurancePayTimeStringList.toArray(new String[insurancePayTimeStringList.size()]);
				ListDialog dialog0=new ListDialog(context,insurancePayTimeArray,"请选择交费期限") {
					@Override
					public void item(int m) {
						Toast.makeText(context, "选择了"+insurancePayTimeArray[m]+insurancePayTimeCodeList.get(m), Toast.LENGTH_SHORT).show();
						holder2.tv_pay_time_year.setText(insurancePayTimeArray[m]);
						activity.additionalInsuranceList.get(position).setPayTimeCheckItem(m);
						activity.additionalInsuranceList.get(position).setPayTimeMiddleString(insurancePayTimeArray[m]);
						activity.additionalInsuranceList.get(position).setPayTimeValueCode(insurancePayTimeCodeList.get(m));
					}
				};
			}
		});
		//计划点击事件
		holder.rl_item_plan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<String> planStringList=bean.getInsurancePlanStringList();
				final ArrayList<String> planCodeList=bean.getInsurancePlanCodeList();
				final String[] planStringArray= planStringList.toArray(new String[planStringList.size()]);
				ListDialog dialog0=new ListDialog(context,planStringArray,"请选择计划") {
					@Override
					public void item(int m) {
						Toast.makeText(context, "选择了"+planStringArray[m]+planCodeList.get(m), Toast.LENGTH_SHORT).show();
						holder2.tv_plan_type.setText(planStringArray[m]);
						activity.additionalInsuranceList.get(position).setPlanCheckItem(m);
						activity.additionalInsuranceList.get(position).setPlanMiddleString(planStringArray[m]);
						activity.additionalInsuranceList.get(position).setPlanValueCode(planCodeList.get(m));
					}
				};
			
			}
		});
		
		
		
		holder.ed_insurance_money.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String num=holder2.ed_insurance_money.getText().toString().trim();
				if("".equals(num) || num.equals("0"))
					return;
				int number=Integer.parseInt(num);
				activity.additionalInsuranceList.get(position).setBaoe(number);
//				Toast.makeText(context, "第"+position+"个条目，存入"+number, Toast.LENGTH_SHORT).show();
//				Log.i("输入框事件-------",  "第"+position+"个条目，存入"+number);
			}
		});
/*		holder.ed_insurance_money.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					
				}else{
					String num=holder2.ed_insurance_money.getText().toString().trim();
					if("".equals(num)||num==null||num.equals("0"))
						return;
					int number=Integer.parseInt(num);
					activity.additionalInsuranceList.get(position).setBaoe(number);
					Toast.makeText(context, "第"+position+"个条目，存入"+number, Toast.LENGTH_SHORT).show();
					
				}
			}
		});*/
		
		//点击输入框的外部的时候，让它外部的布局获得焦点 ，从而让内部edittext失去焦点
/*		holder.rl_outter_top.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
					 holder2.rl_outter_top.setFocusable(true);
					 holder2.rl_outter_top.setFocusableInTouchMode(true);
					 holder2.rl_outter_top.requestFocus();
					 return false;
			}
		});*/
		
		
		return convertView;
	}
	
	
	class ViewHolder{
		private RelativeLayout rl_outter_top;
		
		private RelativeLayout rl_item_all;
		private TextView tv_insurance_title;
		private ImageView iv_check;
		
		private RelativeLayout rl_item_insurance_job;
		private TextView tv_insurance_job_left;
		private TextView tv_insurance_job_middle;
		
		private RelativeLayout rl_item_insurance_period;
		private TextView tv_insurance_period_left;
		private TextView tv_insurance_period_middle;
		
		private RelativeLayout rl_item_pay_frequency;
		private TextView tv_pay_frequency_left;
		private TextView tv_pay_frequency_year;
		
		private RelativeLayout rl_item_pay_time;
		private TextView tv_pay_time_left;
		private TextView tv_pay_time_year;
		
		private RelativeLayout rl_item_plan;
		private TextView tv_plan_left;
		private TextView tv_plan_type;
		
		
		private TextView tv_insurance_money_left;
		private EditText ed_insurance_money;
		private TextView tv_insurance_warn;
		
	
	}
}
