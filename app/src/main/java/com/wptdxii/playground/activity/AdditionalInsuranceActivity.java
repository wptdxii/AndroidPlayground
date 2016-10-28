package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.AdditionalInsuranceAdapter;
import com.cloudhome.bean.InsuranceTemplateBean;
import com.cloudhome.bean.MakeInsuranceTemplateBean;
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
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;

public class AdditionalInsuranceActivity extends BaseActivity implements OnClickListener {

	private String mainInsuranceName="";
	private AdditionalInsuranceAdapter adapter;
	public ArrayList<SubmitInsurancePlanBean> additionalInsuranceList=new ArrayList<SubmitInsurancePlanBean>();

	//存放提交的数据
	private ArrayList<InsuranceTemplateBean> insuranceTemplateLists;

	public ArrayList<MakeInsuranceTemplateBean> additionOutList;
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
	    private ArrayList<TableRowBean> titleTabList=new ArrayList<TableRowBean>();
		private double sum_premium;
		
	//存放提交的数据
	private ArrayList<InsuranceTemplateBean> backInsuranceTemplateLists=new ArrayList<InsuranceTemplateBean>();
	//点击adapter里面的check后会刷新界面，记录是哪一次
	public boolean isFirstRefresh=true;
	//记录错误信息
	public ArrayList<Map<String,String>> errorList=new ArrayList<Map<String,String>>();
	public boolean isAdditionalInsuranceEdit=false;
	
	//是否是编辑
	public boolean isModify;
	//带hashmap的集合，用于记录选择的数据
	public ArrayList<HashMap<String,String>> submitMap=new ArrayList<HashMap<String,String>>();
	private String jsonAll="";
	
	
	//数据获取成功后，设置数据
	private Handler AdditionalInsuranceHandler =new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				//成功的话说明不再显示warn信息
				for(int i=0;i<submitMap.size();i++){
					submitMap.get(i).put("warnShow", "false");
					submitMap.get(i).put("warnMsg", "no");
				}
				
				Intent intent=new Intent();
				intent.putExtra("additionalTabList", additionalTabList);
				intent.putExtra("titleTabList", titleTabList);
				intent.putExtra("sum_premium", sum_premium);
				intent.putExtra("jsonAll", jsonAll);
				intent.putExtra("submitMap", submitMap);
				intent.putExtra("backInsuranceTemplateLists", backInsuranceTemplateLists);

				setResult(11, intent);
				finish();
				
				break;
			case 2:
				for(int m=0;m<errorList.size();m++){
					Map<String,String> map=errorList.get(m);
					for(int h=0;h<submitMap.size();h++){
						HashMap<String,String> hmap=submitMap.get(h);
						if(map.containsKey(hmap.get("product_id"))){
							submitMap.get(h).put("warnShow","true");
							submitMap.get(h).put("warnMsg",map.get(hmap.get("product_id")));
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
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_additional_insurance);
		setFinishOnTouchOutside(false);
		Intent intent=getIntent();
		mainInsuranceName=intent.getStringExtra("mainInsuranceName");
		additionOutList=(ArrayList<MakeInsuranceTemplateBean>) intent.getSerializableExtra("additionOutList");
		insuranceTemplateLists=(ArrayList<InsuranceTemplateBean>) intent.getSerializableExtra("insuranceTemplateLists");
		jsonStr=intent.getStringExtra("jsonStr");
		mainIds=intent.getStringExtra("mainIds");
		//是否是编辑
		isModify=intent.getBooleanExtra("isModify", false);
		if(isModify){
			submitMap= (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("submitMap");
		}else{
			for(int k=0;k<additionOutList.size();k++){
				HashMap<String,String> map=new HashMap<String,String>();
				map.put("check", "false");
				map.put("product_id",additionOutList.get(k).getInsuranceId());
				map.put("warnShow","false");
				map.put("warnMsg", "no");
				submitMap.add(k, map);
			}
		}
		init();
	}


	private void init() {
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		View view= LayoutInflater.from(this).inflate(R.layout.footer_additional_insurance, null);

		TextView tv_top_title = (TextView) findViewById(R.id.tv_top_title);
		tv_top_title.setText(mainInsuranceName);
		ListView lv_additional_insurance = (ListView) findViewById(R.id.lv_additional_insurance);
		lv_additional_insurance.addFooterView(view);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
		adapter=new AdditionalInsuranceAdapter(AdditionalInsuranceActivity.this);
		
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
			break;
		case R.id.tv_cancel:
			finish();
			break;
		}
	}


	private void computeAdditionalFee() {
		ArrayList<HashMap<String,String>> actualMap=new ArrayList<HashMap<String,String>>();
		ArrayList<ArrayList<String>> sbNames=new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> sbCodes=new ArrayList<ArrayList<String>>();
		for(int i=0;i<submitMap.size();i++){
			HashMap<String,String> map=submitMap.get(i);
			if(map.get("check").equals("true")){
				actualMap.add(map);
			}
		}
		if(actualMap.size()>0){
			for(int m=0;m<actualMap.size();m++){
				ArrayList<String> str=new ArrayList<String>();
				ArrayList<String> cod=new ArrayList<String>();
				HashMap<String,String> map = actualMap.get(m);
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String,String> entry = (Map.Entry) iter.next();
					String key = entry.getKey();
					String val = entry.getValue();
					if("".equals(val)){
						return;
					}else{
						str.add(key);
						cod.add(val);
					}
					}
				sbNames.add(str);
				sbCodes.add(cod);
			}
		}else{
			Toast.makeText(this, "请至少选择一款产品", Toast.LENGTH_SHORT).show();
			return;
		}
		//取出上一页主险的json串
		if(!TextUtils.isEmpty(jsonStr)){
			try {
				JSONObject obj=new JSONObject(jsonStr);
				JSONObject planObj=obj.getJSONObject("plan");
				JSONArray productArray=planObj.getJSONArray("products");

				for(int b=0;b<sbNames.size();b++){
					JSONObject pruduct=new JSONObject();
					pruduct.put("is_main","false");
					pruduct.put("parent",mainIds);

					JSONArray array1=new JSONArray();
					for(int y=0;y<sbNames.get(b).size();y++){
						JSONArray array2=new JSONArray();
						if("product_id".equals(sbNames.get(b).get(y))){
							pruduct.put("product_id",sbCodes.get(b).get(y));
							continue;
						}
						if("check".equals(sbNames.get(b).get(y))||"warnShow".equals(sbNames.get(b).get(y))
								||"warnMsg".equals(sbNames.get(b).get(y))){
							continue;
						}
							array2.put(0, sbNames.get(b).get(y));
							array2.put(1, sbCodes.get(b).get(y));
							array1.put(array2);

					}
					pruduct.put("inputfields",array1);
					productArray.put(b+1,pruduct);
				}
				String str=obj.toString();
				jsonAll=str;
				String url=IpConfig.getUri2("calInsuranceCharge");
//				String url="http://10.10.10.72:8080/gateway/proposal/calInsuranceCharge?";
				Log.i("url----------", url);
				Log.i("提交----的json串----------", str);
				Log.i("提交----的json串----------", user_id);
				Log.i("提交----的json串----------", token);
				key_value.put("user_id", user_id);
				key_value.put("token", token);
				key_value.put("prod_info",  str);
				key_value.put("time",  "");

				OkHttpUtils.post()//
						.url(url)//
						.params(key_value)//
						.build()//
						.execute(new StringCallback() {

							@Override
							public void onError(Call call, Exception e) {

								Log.i("联网失败了------------", "获取主险费率");
								AdditionalInsuranceHandler.sendEmptyMessage(4);

							}

							@Override
							public void onResponse(String response) {

								// TODO Auto-generated method stub
								String jsonString = response;
								Log.i("返回的数据---------------", jsonString);
								try {
									JSONObject objResult = new JSONObject(jsonString);
									String status = objResult.getString("status");
									if (status.equals("true")) {
//							holder2.tv_insurance_warn.setText("");
//							holder2.tv_insurance_warn.setVisibility(View.GONE);
										JSONObject dataObj = objResult.getJSONObject("data");
										JSONObject planObj = dataObj.getJSONObject("plan");
										JSONArray productsArray = planObj.getJSONArray("products");
										sum_premium = planObj.getDouble("sum_premium");


										for (int y = 1; y < productsArray.length(); y++) {
											JSONObject productObject = productsArray.getJSONObject(y);
											JSONArray rulesArray = productObject.getJSONArray("rules");

											JSONObject tableObject = productObject.getJSONObject("table");
											JSONArray rowArray = tableObject.getJSONArray("row");
											JSONArray titleArray = tableObject.getJSONArray("title");


											JSONArray inputfieldsArray = productObject.getJSONArray("inputfields");
											ArrayList<String> factorsName = new ArrayList<String>();
											ArrayList<String> factorsValue = new ArrayList<String>();
											for (int b = 0; b < inputfieldsArray.length(); b++) {
												JSONArray array1 = (JSONArray) inputfieldsArray.get(b);
												factorsName.add(b, array1.getString(0));
												factorsValue.add(b, array1.getString(1));
											}
											String product_id = productObject.getString("product_id");
											Log.i("返回的附加险product_id", product_id + "------------------");
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

											TableRowBean bean = new TableRowBean();
											bean.setInsuranceName(rowArray.get(0).toString());
											bean.setPayTime(rowArray.get(1).toString());
											bean.setBaoe(rowArray.get(2).toString());
											bean.setBaofei(rowArray.get(3).toString());
											bean.setId(product_id);
											TableRowBean titleBean = new TableRowBean();
											titleBean.setTitle1(titleArray.get(1).toString());
											titleBean.setTitle2(titleArray.get(2).toString());
											titleBean.setTitle3(titleArray.get(3).toString());

											additionalTabList.add(bean);
											titleTabList.add(titleBean);
										}
										Message msg = new Message();
										msg.what = 0;
										AdditionalInsuranceHandler.sendMessage(msg);
									} else {

										String errmsg = objResult.getString("errmsg");
										String errcode = objResult.getString("errcode");
										if ("1".equals(errcode)) {
											JSONObject dataObj = objResult.getJSONObject("data");
											JSONObject planObj = dataObj.getJSONObject("plan");
											JSONArray productsArray = planObj.getJSONArray("products");
											Log.i(productsArray.length() + "------------------", "------------------");
											for (int y = 1; y < productsArray.length(); y++) {
												JSONObject productObject = productsArray.getJSONObject(y);
												JSONArray rulesArray = productObject.getJSONArray("rules");
												String product_id = productObject.getString("product_id");
												if (rulesArray.length() > 0) {
													String rule = (String) rulesArray.get(0);
													Map<String, String> map = new HashMap<String, String>();
													map.put(product_id, rule);
													errorList.add(map);
												}

											}
										}
										Message msg = new Message();
										msg.what = 2;
										AdditionalInsuranceHandler.sendMessage(msg);
									}
								} catch (JSONException e) {
									e.printStackTrace();
									AdditionalInsuranceHandler.sendEmptyMessage(1);
								}
							}
						});


			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
