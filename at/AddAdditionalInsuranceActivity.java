package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.AddAdditionalInsuranceAdapter;
import com.cloudhome.bean.InsuranceTemplateBean;
import com.cloudhome.bean.MakeInsuranceTemplateBean;
import com.cloudhome.bean.MakeInsuranceTemplateInnerBean;
import com.cloudhome.bean.SubmitInsurancePlanBean;
import com.cloudhome.bean.TableRowBean;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
public class AddAdditionalInsuranceActivity extends BaseActivity implements OnClickListener{

	private String mainInsuranceName="";
	private AddAdditionalInsuranceAdapter adapter;
	public ArrayList<SubmitInsurancePlanBean> additionalInsuranceList=new ArrayList<SubmitInsurancePlanBean>();
	
	private ArrayList<MakeInsuranceTemplateBean> additionOutList;
	private String jsonStr="";
	private String mainIds="";
	
	private Map<String, String> key_value = new HashMap<String, String>();

	private String token;
	private String user_id;
	
	public boolean isPeriodFirst=true;
	public boolean isPayTime=true;
	public boolean isFrequencyFirst=true;
	public boolean isPlanFirst=true;
	
	
	//附加险的列表
		private ArrayList<TableRowBean> additionalTabList=new ArrayList<TableRowBean>();
		private double sum_premium;
		
	//存放提交的数据
	private ArrayList<InsuranceTemplateBean> insuranceTemplateLists;
	//存放提交的数据
	private ArrayList<InsuranceTemplateBean> backInsuranceTemplateLists=new ArrayList<InsuranceTemplateBean>();
	//点击adapter里面的check后会刷新界面，记录是哪一次
	public boolean isFirstRefresh=true;
	//记录错误信息
	private ArrayList<Map<String,String>> errorList=new ArrayList<Map<String,String>>();
	
	//是否是编辑
	private boolean isModify;
	
	
	//数据获取成功后，设置数据
	private Handler AdditionalInsuranceHandler =new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				//成功的话说明不再显示warn信息
				for(int i=0;i<additionalInsuranceList.size();i++){
					additionalInsuranceList.get(i).setShowWarn(false);
					additionalInsuranceList.get(i).setWarnText("");
				}
				
				Intent intent=new Intent();
				intent.putExtra("additionalTabList", additionalTabList);
				intent.putExtra("sum_premium", sum_premium);
				intent.putExtra("backInsuranceTemplateLists", backInsuranceTemplateLists);
				//将存储数据的集合返回到上一个页面，再次编辑的时候可以记住所选项
				intent.putExtra("additionalInsuranceList", additionalInsuranceList);
				setResult(11, intent);
				finish();
				
				break;
			case 2:
				//显示错误信息warn
				for(int i=0;i<additionalInsuranceList.size();i++){
					SubmitInsurancePlanBean bean=additionalInsuranceList.get(i);
					for(int m=0;m<errorList.size();m++){
						Map<String,String> map=errorList.get(m);
						if(map.containsKey(bean.getId())){
							additionalInsuranceList.get(i).setShowWarn(true);
							additionalInsuranceList.get(i).setWarnText(map.get(bean.getId()));
						}
					}
				}
				adapter.notifyDataSetChanged();
			
				break;
			
			/*case 1:
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				dialog.dismiss();
				String errmsg=(String) msg.obj;
				 tv_insurance_warn.setVisibility(View.VISIBLE);
				 tv_insurance_warn.setText(errmsg);
				break;
			case 3:
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "获取数据为空", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "联网失败，请检查网络", Toast.LENGTH_SHORT).show();
				break;*/
			
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_additional_insurance);
		setFinishOnTouchOutside(false);
		Intent intent=getIntent();
		mainInsuranceName=intent.getStringExtra("mainInsuranceName");
		additionOutList=(ArrayList<MakeInsuranceTemplateBean>) intent.getSerializableExtra("additionOutList");
		ArrayList<MakeInsuranceTemplateInnerBean> additionInnerList = (ArrayList<MakeInsuranceTemplateInnerBean>) intent.getSerializableExtra("additionInnerList");
		insuranceTemplateLists=(ArrayList<InsuranceTemplateBean>) intent.getSerializableExtra("insuranceTemplateLists");

		jsonStr=intent.getStringExtra("jsonStr");
		mainIds=intent.getStringExtra("mainIds");
		//是否是编辑
		isModify=intent.getBooleanExtra("isModify", false);
		if(isModify){
			additionalInsuranceList=(ArrayList<SubmitInsurancePlanBean>) intent.getSerializableExtra("modifyInfos");
			for(int i=0;i<additionalInsuranceList.size();i++){
				additionalInsuranceList.get(i).setBaoe(0);
			}
		}else{
			dataExchange();
		}
		init();
	}


	private void init() {

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		//记录选中与否，选择的数据
	/*	for(int i=0;i<additionOutList.size();i++){
			SubmitInsurancePlanBean bean=new SubmitInsurancePlanBean();
			bean.setId(additionOutList.get(i).getInsuranceId());
			bean.setCheck(false);
			additionalInsuranceList.add(bean);
		}*/
	
		
		
		
		View view=LayoutInflater.from(this).inflate(R.layout.footer_additional_insurance, null);


		TextView tv_top_title = (TextView) findViewById(R.id.tv_top_title);
		tv_top_title.setText(mainInsuranceName);
		ListView lv_additional_insurance = (ListView) findViewById(R.id.lv_additional_insurance);
		lv_additional_insurance.addFooterView(view);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
		
		
//		adapter=new AddAdditionalInsuranceAdapter(AddAdditionalInsuranceActivity.this,additionOutList,additionInnerList);
		adapter=new AddAdditionalInsuranceAdapter(AddAdditionalInsuranceActivity.this);
		
		lv_additional_insurance.setAdapter(adapter);
		tv_ok.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_ok:
			//计算附加险保费
			computeAdditionalFee();
//			Intent intent=new Intent();
//			setResult(11, intent);
//			finish();
			break;
		case R.id.tv_cancel:
			finish();
			break;
		}
	}


	private void computeAdditionalFee() {
		errorList.clear();
		
		ArrayList<SubmitInsurancePlanBean> finalAdditionalList=new ArrayList<SubmitInsurancePlanBean>(); 
		for(int i=0;i<additionalInsuranceList.size();i++){
			SubmitInsurancePlanBean bean=additionalInsuranceList.get(i);
			if(bean.isCheck()){
				finalAdditionalList.add(bean);
			}
		}
		if(finalAdditionalList.size()>0){
			for(int i=0;i<finalAdditionalList.size();i++){
				if(finalAdditionalList.get(i).getBaoe()==0){
					Toast.makeText(this, "请输入保额", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		}else{
			Toast.makeText(this, "请至少选择一款产品", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try {
			if(TextUtils.isEmpty(jsonStr)){
				Toast.makeText(this, "主险为空", Toast.LENGTH_SHORT).show();
				return;
			}
			JSONObject jsonObj=new JSONObject(jsonStr);
			JSONObject planObj=jsonObj.getJSONObject("plan");
			JSONArray productsArray=planObj.getJSONArray("products");
			for(int m=0;m<finalAdditionalList.size();m++){
				SubmitInsurancePlanBean bean1=finalAdditionalList.get(m);
				    JSONArray array1=new JSONArray();
					
				  	JSONArray array2=new JSONArray();
					array2.put(0, "insurance_period_code");
					array2.put(1, bean1.getPeriodValueCode());
					array1.put(0, array2);
					
					JSONArray array3=new JSONArray();
					array3.put(0, "pay_type");
					array3.put(1, bean1.getFrequencyValueCode());
					array1.put(1, array3);
					
					JSONArray array4=new JSONArray();
					array4.put(0, "pay_period_code");
					array4.put(1, bean1.getPayTimeValueCode());
					array1.put(2, array4);
					
					JSONArray array5=new JSONArray();
					array5.put(0, "disc");
					array5.put(1, bean1.getPlanValueCode());
					array1.put(3, array5);
					
					 JSONArray array6=new JSONArray();
					 array6.put(0,"premium");
					 array6.put(1,0);
					 JSONArray array7=new JSONArray();
					 array7.put(0,"amount");//保额
					 array7.put(1,bean1.getBaoe());
					 array1.put(4, array6);
					 array1.put(5, array7);
					 
				    JSONObject productObj=new JSONObject();
				    productObj.put("product_id",bean1.getId());
				    productObj.put("is_main","false");
				    productObj.put("parent",mainIds);
				    productObj.put("inputfields",array1);
				    
				    productsArray.put(m+1, productObj);
			}
		    
		    String str=jsonObj.toString();
			String url=IpConfig.getUri2("calInsuranceCharge");
			Log.i("url----------", url);
			Log.i("提交----的json串----------", str);
			Log.i("提交----的json串----------", user_id);
			Log.i("提交----的json串----------", token);
			key_value.put("user_id",user_id);
		    key_value.put("token",  token);
		    key_value.put("prod_info",  str);
		    
			
		    
		    OkHttpUtils.post()//
			.url(url)//
			.params(key_value)//
			.build()//
			.execute(new StringCallback() {

				@Override
				public void onError(Call call, Exception e, int id) {

					Log.i("联网失败了------------", "获取主险费率");
					AdditionalInsuranceHandler.sendEmptyMessage(4);
				}

				@Override
				public void onResponse(String response, int id) {


					String jsonString= response;
					Log.i("返回的数据---------------", jsonString);
					try {
						JSONObject objResult=new JSONObject(jsonString);
						String status=objResult.getString("status");
						if(status.equals("true")){
							//							holder2.tv_insurance_warn.setText("");
							//							holder2.tv_insurance_warn.setVisibility(View.GONE);
							JSONObject dataObj=objResult.getJSONObject("data");
							JSONObject planObj=dataObj.getJSONObject("plan");
							JSONArray productsArray=planObj.getJSONArray("products");
							sum_premium=planObj.getDouble("sum_premium");


							for(int y=1;y<productsArray.length();y++){
								JSONObject productObject=productsArray.getJSONObject(y);
								JSONArray rulesArray=productObject.getJSONArray("rules");

								JSONObject tableObject=productObject.getJSONObject("table");
								JSONArray rowArray=tableObject.getJSONArray("row");

								JSONArray inputfieldsArray=productObject.getJSONArray("inputfields");
								ArrayList<String> factorsName=new ArrayList<String>();
								ArrayList<String> factorsValue=new ArrayList<String>();
								for(int b=0;b<inputfieldsArray.length();b++){
									JSONArray array1=(JSONArray) inputfieldsArray.get(b);
									factorsName.add(b,array1.getString(0));
									factorsValue.add(b,array1.getString(1));
								}
								String product_id=productObject.getString("product_id");
								Log.i("返回的附加险product_id", product_id+"------------------");
								for(int a=1;a<insuranceTemplateLists.size();a++){

									Log.i("传过来的product_id", insuranceTemplateLists.get(a).getProduct_id()+"------------------");
									if(product_id.equals(insuranceTemplateLists.get(a).getProduct_id())){
										insuranceTemplateLists.get(a).setFactorsName(factorsName);
										insuranceTemplateLists.get(a).setFactorsValue(factorsValue);
										insuranceTemplateLists.get(a).setPrem(Double.parseDouble(rowArray.get(3).toString()));
										insuranceTemplateLists.get(a).setAmount(Integer.parseInt(rowArray.get(2).toString()));
										backInsuranceTemplateLists.add(insuranceTemplateLists.get(a));
										Log.i("添加了一个backBean", "------------------");
									}
								}

								TableRowBean bean=new TableRowBean();
								bean.setInsuranceName(rowArray.get(0).toString());
								bean.setPayTime(rowArray.get(1).toString());
								bean.setBaoe(rowArray.get(2).toString());
								bean.setBaofei(rowArray.get(3).toString());
								bean.setId(product_id);

								additionalTabList.add(bean);
							}
							Message msg=new Message();
							msg.what=0;
							AdditionalInsuranceHandler.sendMessage(msg);
						}else{

							String errmsg=objResult.getString("errmsg");
							String errcode=objResult.getString("errcode");
							if("1".equals(errcode)){
								JSONObject dataObj=objResult.getJSONObject("data");
								JSONObject planObj=dataObj.getJSONObject("plan");
								JSONArray productsArray=planObj.getJSONArray("products");
								Log.i(productsArray.length()+"------------------", "------------------");
								for(int y=1;y<productsArray.length();y++){
									JSONObject productObject=productsArray.getJSONObject(y);
									JSONArray rulesArray=productObject.getJSONArray("rules");
									String product_id=productObject.getString("product_id");
									if(rulesArray.length()>0){
										String rule=(String) rulesArray.get(0);
										Map<String,String> map=new HashMap<String,String>();
										map.put(product_id, rule);
										errorList.add(map);
									}

								}
							}
							Message msg=new Message();
							msg.what=2;
							AdditionalInsuranceHandler.sendMessage(msg);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						AdditionalInsuranceHandler.sendEmptyMessage(1);
					}

				}
			});




		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//将的到的数据存入集合，保存数据，刷新数据
	private void dataExchange(){
		
		for(int m=0;m<additionOutList.size();m++){
			MakeInsuranceTemplateBean bean=additionOutList.get(m);
			//存放数据
			SubmitInsurancePlanBean submitBean=new SubmitInsurancePlanBean();
			int listSize=bean.getInnerBeanList().size();
			for(int i=0;i<listSize;i++){
				if(bean.getInnerBeanList().get(i).getTitle_key().equals("insurance_period_code")){
					
					//保险期间
					ArrayList<String> insurancePeriodCodeList=bean.getInnerBeanList().get(i).getValueCode();
					ArrayList<String> insurancePeriodStringList=bean.getInnerBeanList().get(i).getValueString();
					submitBean.setPeriodShow(true);
					submitBean.setPeriodMain(i);
					submitBean.setPeriodLeftString(bean.getInnerBeanList().get(i).getTitle());
					submitBean.setPeriodCheckItem(0);
					submitBean.setPeriodValueCode(insurancePeriodCodeList.get(0));
					submitBean.setPeriodMiddleString(insurancePeriodStringList.get(0));
					submitBean.setInsurancePeriodCodeList(insurancePeriodCodeList);
					submitBean.setInsurancePeriodStringList(insurancePeriodStringList);
					
					
				}else if(bean.getInnerBeanList().get(i).getTitle_key().equals("pay_period_code")){
					//缴费期间
					
					ArrayList<String> insurancePayTimeCodeList=bean.getInnerBeanList().get(i).getValueCode();
					ArrayList<String> insurancePayTimeStringList=bean.getInnerBeanList().get(i).getValueString();
					submitBean.setPayTimeShow(true);
					submitBean.setPayTimeMain(i);
					submitBean.setPayTimeLeftString(bean.getInnerBeanList().get(i).getTitle());
					submitBean.setPayTimeCheckItem(0);
					submitBean.setPayTimeValueCode(insurancePayTimeCodeList.get(0));
					submitBean.setPayTimeMiddleString(insurancePayTimeStringList.get(0));
					submitBean.setInsurancePayTimeCodeList(insurancePayTimeCodeList);
					submitBean.setInsurancePayTimeStringList(insurancePayTimeStringList);
					
				}else if(bean.getInnerBeanList().get(i).getTitle_key().equals("pay_type")){
					//缴费频率
					ArrayList<String> insuranceFrequencyCodeList=bean.getInnerBeanList().get(i).getValueCode();
					ArrayList<String> insuranceFrequencyStringList=bean.getInnerBeanList().get(i).getValueString();
					submitBean.setFrequencyShow(true);
					submitBean.setFrequencyMain(i);
					submitBean.setFrequencyLeftString(bean.getInnerBeanList().get(i).getTitle());
					submitBean.setFrequencyCheckItem(0);
					submitBean.setFrequencyValueCode(insuranceFrequencyCodeList.get(0));
					submitBean.setFrequencyMiddleString(insuranceFrequencyStringList.get(0));
					submitBean.setInsuranceFrequencyCodeList(insuranceFrequencyCodeList);
					submitBean.setInsuranceFrequencyStringList(insuranceFrequencyStringList);
				}else if(bean.getInnerBeanList().get(i).getTitle_key().equals("disc_code")){
					ArrayList<String> insurancePlanCodeList=bean.getInnerBeanList().get(i).getValueCode();
					ArrayList<String> insurancePlanStringList=bean.getInnerBeanList().get(i).getValueString();
					submitBean.setPlanShow(true);
					submitBean.setPlanLeftString(bean.getInnerBeanList().get(i).getTitle());
					submitBean.setPlanCheckItem(0);
					submitBean.setPlanValueCode(insurancePlanCodeList.get(0));
					submitBean.setPlanMiddleString(insurancePlanStringList.get(0));
					submitBean.setInsurancePlanCodeList(insurancePlanCodeList);
					submitBean.setInsurancePlanStringList(insurancePlanStringList);
				}
				submitBean.setWarnText("");
				submitBean.setShowWarn(false);
				submitBean.setCheck(false);
				submitBean.setInsuranceTitle(bean.getInsuranceName());
				submitBean.setMoneyLeft(bean.getRiskTitle());
				submitBean.setInsuranceHint(bean.getRiskHint());
				submitBean.setId(bean.getInsuranceId());
				
			}
			additionalInsuranceList.add(submitBean);
			
		}
		
		
	
	}
	



}
