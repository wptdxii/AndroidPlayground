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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.activity.AdditionalInsuranceActivity;
import com.cloudhome.bean.MakeInsuranceTemplateBean;
import com.cloudhome.bean.MakeInsuranceTemplateInnerBean;
import com.cloudhome.utils.Common;
import com.cloudhome.view.customview.ListDialog;

import java.util.ArrayList;

public class AdditionalInsuranceAdapter extends BaseAdapter {

	private Context context;
	private AdditionalInsuranceActivity activity;

	public AdditionalInsuranceAdapter(Context context) {
		super();
		this.context = context;
		activity=(AdditionalInsuranceActivity)context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return activity.additionOutList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return activity.additionOutList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 final ViewHolder holder;
		final  MakeInsuranceTemplateBean bean=activity.additionOutList.get(position);
		final ArrayList<MakeInsuranceTemplateInnerBean> list=bean.getInnerBeanList();
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_additional_insurance, parent,false);
			holder.rl_outter_top=(RelativeLayout) convertView.findViewById(R.id.rl_outter_top);
			holder.tv_insurance_title=(TextView) convertView.findViewById(R.id.tv_insurance_title);
			holder.iv_check=(ImageView) convertView.findViewById(R.id.iv_check);
			holder.tv_insurance_warn=(TextView) convertView.findViewById(R.id.tv_insurance_warn);
			holder.rl_item_all=(RelativeLayout) convertView.findViewById(R.id.rl_item_all);
			holder.ll_content= (LinearLayout) convertView.findViewById(R.id.ll_content);

			for(int i=0;i<list.size();i++){
				View view=null;
				final MakeInsuranceTemplateInnerBean bean2=list.get(i);
				final String title_key=bean2.getTitle_key();
				if("01".equals(bean2.getInput_type())){
					view=LayoutInflater.from(context).inflate(R.layout.add_insurance_item1,null);
					TextView tv_left= (TextView) view.findViewById(R.id.tv_left);
					ImageView iv_insurance= (ImageView) view.findViewById(R.id.iv_insurance);
					final TextView tv_middle= (TextView) view.findViewById(R.id.tv_middle);
					//当只有一个选项的时候，隐藏下拉箭头
					final ArrayList<String> valueString=bean2.getValueString();
					if(valueString.size()>1){
						iv_insurance.setVisibility(View.VISIBLE);
					}else{
						iv_insurance.setVisibility(View.INVISIBLE);
					}
					tv_left.setText(bean2.getTitle());

				if(activity.isModify){
					String sbCode=activity.submitMap.get(position).get(title_key);
					ArrayList<String> sbCodeList=bean2.getValueCode();
					int pos=sbCodeList.indexOf(sbCode);
					tv_middle.setText(bean2.getValueString().get(pos));
				}else{
					tv_middle.setText(bean2.getValueString().get(0));
					String defaultCode=bean2.getValueCode().get(0);
					activity.submitMap.get(position).put(title_key, defaultCode);
					Log.i("submitMap" + position, title_key + "----" +defaultCode);
				}



//				Log.i("submitMap", title_key + "----" + defaultCode);
					tv_middle.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							if (valueString.size() <= 1) {
								return;
							}
							ArrayList<String> stringList = bean2.getValueString();
							final ArrayList<String> codeList = bean2.getValueCode();
							final String[] insurancePeriodArray = stringList.toArray(new String[stringList.size()]);
							ListDialog dialog0 = new ListDialog(context, insurancePeriodArray, "请选择" + bean2.getTitle()) {
								@Override
								public void item(int m) {
									tv_middle.setText(insurancePeriodArray[m]);
									activity.submitMap.get(position).put(title_key, codeList.get(m));
									Log.i("submitMap"+ position, title_key + "----" + codeList.get(m));

								}
							};
						}
					});

				}else if("02".equals(bean2.getInput_type())){
					view=LayoutInflater.from(context).inflate(R.layout.add_insurance_item2,null);
					TextView tv_insurance_left= (TextView) view.findViewById(R.id.tv_insurance_left);
					final EditText ed_insurance_money= (EditText) view.findViewById(R.id.ed_insurance_money);
					tv_insurance_left.setText(bean2.getTitle());
				if(activity.isModify){
					String sbCode=activity.submitMap.get(position).get(title_key);
					ed_insurance_money.setText(sbCode);
				} else {
					ed_insurance_money.setHint(bean2.getHint());
					activity.submitMap.get(position).put(title_key, "");
					Log.i("submitMap" + position, title_key +"--");
				}

					TextWatcher watcher=new TextWatcher() {
						@Override
						public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
						}
						@Override
						public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
						}
						@Override
						public void afterTextChanged(Editable editable) {
							String etValue=ed_insurance_money.getText().toString().trim();
							activity.submitMap.get(position).put(title_key,etValue);
							Log.i("submitMap"+position, title_key + "----" + etValue);
						}
					};
					ed_insurance_money.addTextChangedListener(watcher);
				}
				LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Common.dip2px(context, 44));
				view.setLayoutParams(params1);
				holder.ll_content.addView(view, i);
			}
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
			
		}

//		final SubmitInsurancePlanBean bean=activity.additionalInsuranceList.get(position);
//		holder.tv_insurance_title.setText(bean.getInsuranceTitle());
//		if(){}

		holder.tv_insurance_title.setText(bean.getInsuranceName());






		if("true".equals(activity.submitMap.get(position).get("warnShow"))){
			holder.tv_insurance_warn.setVisibility(View.VISIBLE);
			holder.tv_insurance_warn.setText(activity.submitMap.get(position).get("warnMsg"));
		}else{
			holder.tv_insurance_warn.setVisibility(View.GONE);
			holder.tv_insurance_warn.setText("");
		}
		
		
		//根据SubmitInsurancePlanBean记录的check值来控制是否选中
		if(activity.submitMap.get(position).get("check").equals("true")){
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
				if(activity.submitMap.get(position).get("check").equals("true")){
					holder.iv_check.setImageResource(R.drawable.weixuan);
					holder.rl_item_all.setVisibility(View.GONE);
					activity.submitMap.get(position).put("check", "false");
					Log.i("submitMap" + position, "check----false");

				}else{
					holder.iv_check.setImageResource(R.drawable.xuanzhong);
					holder.rl_item_all.setVisibility(View.VISIBLE);
					activity.submitMap.get(position).put("check", "true");
					Log.i("submitMap" + position, "check----true");
				}
			}
		});

//		holder.ed_insurance_money.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				String num=holder2.ed_insurance_money.getText().toString().trim();
//				if("".equals(num) || num.equals("0"))
//					return;
//				int number=Integer.parseInt(num);
//				activity.additionalInsuranceList.get(position).setBaoe(number);
////				Toast.makeText(context, "第"+position+"个条目，存入"+number, Toast.LENGTH_SHORT).show();
////				Log.i("输入框事件-------",  "第"+position+"个条目，存入"+number);
//			}
//		});
		return convertView;
	}
	
	
	class ViewHolder{
		private RelativeLayout rl_outter_top;
		private TextView tv_insurance_title;
		private ImageView iv_check;
		private TextView tv_insurance_warn;
		private LinearLayout ll_content;
		private RelativeLayout rl_item_all;
	}
}
