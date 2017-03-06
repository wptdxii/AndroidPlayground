package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.bean.InsuranceTemplateBean;
import com.cloudhome.bean.MakeInsuranceTemplateBean;
import com.cloudhome.bean.MakeInsuranceTemplateInnerBean;
import com.cloudhome.bean.SubmitInsurancePlanBean;
import com.cloudhome.bean.TableRowBean;
import com.cloudhome.view.customview.CircleImage;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.ListDialog;
import com.cloudhome.view.customview.ListViewScrollView;
import com.cloudhome.view.customview.NoAutoScrollView;
import com.cloudhome.view.iosalertview.MyAlertDialog;
import com.cloudhome.view.wheel.wheelview.ScreenInfo;
import com.cloudhome.view.wheel.wheelview.WheelMain;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import okhttp3.Call;


public class MakeInsurancePlanActivity extends BaseActivity implements OnClickListener{
	private String[] ageArray={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",
			"21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
			"41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60",
			"61","62","63","64","65","66","67","68","69","70","71","72","73","74","75","76","77","78","79","80",
			"81","82","83","84","85","86","87","88","89","90","91","92","93","94","95","96","97","98","99",
			"100","101","102","103","104","105","106"};
	private ArrayList<String> ageRangeList;
	
	
	private NoAutoScrollView scroll_top;
	private Dialog dialog;
	public static String insuranceAge;
	
	private ImageView iv_back;
	private TextView top_title;
	private ImageView iv_right;
	private ImageView holder_select_img;
	//被保人的年龄
	public String insured_birthday="";
	//投保人的年龄
	public String holder_birthday="";
	
	private RelativeLayout rl_holder_info;
	private RelativeLayout rl_all_holder_info;
	private boolean isShown;
	//生成建议书
	private TextView tv_generate_insurance_template;
	//被保人
	private EditText ed_insuranced_name;
	private RadioGroup rg_choice_sex;
	private TextView tv_age_num;
	private ImageView iv_show_calendar;
	//投保人
	private EditText ed_holder_name;
	private RadioGroup rg_holder_choice_sex;
	private TextView tv_holder_age_num;
	private ImageView iv_holder_show_calendar;
	
	//外部item数据
	private ArrayList<MakeInsuranceTemplateBean> outlist;
	private ArrayList<MakeInsuranceTemplateInnerBean> innerlist;
	//用户数据
	private Map<String, String> key_value = new HashMap<String, String>();

	private String token;
	private String user_id;
	
	WheelMain wheelMain;
	private String birthday;
	@SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private int ageWeel=-1;
	
	private ArrayList<HashMap<String, String>> select_infoList;
	private ArrayList<String> insuranceInfos;
	private String ids="";
	
	//被保人的年龄，性别
	public int insurancedAge=-1;
	public String insurancedSexs="01";
	public String insurancedName="";
	//投保人的年龄，性别
	public int holderAge=-1;
	public String holderSexs="";
	public String holderName="";
	//footer
	public TextView tv_total_insurance_money;
	//用于存放提交信息的集合
	public ArrayList<InsuranceTemplateBean> insuranceTemplateLists;
	public ArrayList<InsuranceTemplateBean> submitInsuranceList=new ArrayList<InsuranceTemplateBean>();
	
	//记录totalPrice
	public double totalPrice=0;
	//判断是否有豁免
	public boolean hasHuomian=false; 
	//联网成功后，返回的网页地址,logo,title,描述
	private String resultUrl="";
	private String resultLogo="";
	private String resulTitle="";
	private String resultDescription="";
	private String forward_url="";
	
	
	//添加主险
	
	private RelativeLayout rl_main_insurance;
	private RelativeLayout rl_main_insurance_tab;
	private ImageView iv_main_insurance_title_right;
	private ImageButton tv_main_insurance_modify;
	private TextView tv_insurance_warn;
	public boolean isMainInsuranceEdit=false;

	private CircleImage iv_main_insurance;
	private TextView tv_main_insurance;
	
	private int frequencyMain=-1;
	private int frequencyIndex=-1;
	private boolean isfrequencyMainShow=false;
	
	private int planMain=-1;
	private int planIndex=-1;
	private boolean isplanMainShow=false;
	
	private int periodMain=-1;
	private int periodIndex=-1;
	private boolean isperiodMainShow=false;
	
	private int payTimeMain=-1;
	private int payTimeIndex=-1;
	private boolean ispayTimeMainShow=false;
	
	//表格
	private TextView tv_tab_main_insurance;
	private TextView tv_paytime;
	private TextView tv_insurance_money;
	private TextView tv_product_fee;
	ArrayList<String> submitStrsCode;
	SubmitInsurancePlanBean mainBean =new SubmitInsurancePlanBean();
	//dialog中的下拉列表
	TextView tv_insurance_period_middle;
	TextView tv_pay_frequency_year;
	TextView tv_pay_time_year;
	TextView tv_plan_type;
	EditText ed_insurance_money;
	//添加附加险
	private ImageView iv_additional_insurance_title_right;
	private ImageButton tv_additional_insurance_modify;
	public AlertDialog  dialog1;
	public boolean isAdditionalAdd=false;
	public boolean isAdditionalEdit=false;
	//附加险表格
	private TextView tv_additional_insurance_main_title;
	private RelativeLayout rl_additional_insurance_tab;
	private ListViewScrollView lv_additional_tab;
	//主险的年龄适用范围
	private int min_age;
	private int max_age;
	//主险对应的附加险 
	private ArrayList<MakeInsuranceTemplateBean> additionOutList;
	private ArrayList<MakeInsuranceTemplateInnerBean> additionInnerList;
	//传递给附加险计费页面的json串
	private String jsonStr="";
	
	//附加险的列表
	private ArrayList<TableRowBean> additionalTabList=new ArrayList<TableRowBean>();
	private TextView tv_total_insurance_price;
	
	//首年总保费
	private double sum_premium;
	
	//是不是第一次调用getSelectProduct接口
	private boolean isFirstSelectProduct=true;
	
	//附加险保费计算成功后返回的集合，再次编辑的时候可以记住所选项
	public ArrayList<SubmitInsurancePlanBean> additionalInsuranceList;
	public String iconUrl="";
	private CircleImage iv_tab_main_insurance;//主险表格出现后的图标
	//主险没有附加险的时候要隐藏附加险的布局
	private RelativeLayout rl_additional_insurance_title;
	private boolean isHasAdditionInsurance=false;
	//title
	private String title;
	//附加险下面的提示，没有附加险的时候隐藏
	private RelativeLayout rl_additional_desc_info;
	//在添加主险的时候，第二次请求的时候保持被保人年龄不变
	private boolean isFirstFetchData=true;
	private String user_id_encode="";
	
	
	//数据获取成功后，设置数据
	private Handler resultHandler =new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				dialog.dismiss();
				//设置年龄的数据
				ageRangeList=new ArrayList<String>();
				for(int a=min_age;a<max_age+1;a++){
					ageRangeList.add(a+"");
				}
				if(isFirstFetchData){
					tv_age_num.setText(min_age+"");
					isFirstFetchData=false;
				}
				
				
				if(outlist.size()>0){
					tv_main_insurance.setText(outlist.get(0).getInsuranceName());
					//显示主险的icon
					
					iconUrl=outlist.get(0).getIcon();
					Log.i("主险icon的地址--------",iconUrl);

					Glide.with(MakeInsurancePlanActivity.this)
							.load(iconUrl)
							.placeholder(R.drawable.white_bg)
							.into(iv_main_insurance);


					hasHuomian = outlist.get(0).getHuomian().equals("true");
				}
				if(hasHuomian){
					rl_all_holder_info.setVisibility(View.VISIBLE);
					holder_select_img.setImageResource(R.drawable.p_s_select);
					isShown=true;
				}else{
					rl_all_holder_info.setVisibility(View.GONE);
					holder_select_img.setImageResource(R.drawable.p_s_not_select);
					isShown=false;
				}
				//如果有附加险就显示，没有就不显示
				if(isHasAdditionInsurance){
					rl_additional_insurance_title.setVisibility(View.VISIBLE);
					rl_additional_desc_info.setVisibility(View.VISIBLE);
				}else{
					rl_additional_insurance_title.setVisibility(View.GONE);
					rl_additional_desc_info.setVisibility(View.GONE);
				}
				
				submitInsuranceList.clear();
				submitInsuranceList.add(insuranceTemplateLists.get(0));
				if(!isFirstSelectProduct){
					showAddMainInsuranceDialog();
				}
				break;
			
			case 1:
				scroll_top.setVisibility(View.GONE);
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
				if(!isFirstSelectProduct){
					showAddMainInsuranceDialog();
				}
				break;
			case 2:
				scroll_top.setVisibility(View.GONE);
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
				if(!isFirstSelectProduct){
					showAddMainInsuranceDialog();
				}
				break;
			case 3:
				scroll_top.setVisibility(View.GONE);
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "获取数据为空", Toast.LENGTH_SHORT).show();
				if(!isFirstSelectProduct){
					showAddMainInsuranceDialog();
				}
				break;
			case 4:
				scroll_top.setVisibility(View.GONE);
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "联网失败，请检查网络", Toast.LENGTH_SHORT).show();
				if(!isFirstSelectProduct){
					showAddMainInsuranceDialog();
				}
				break;
			
			}
		}
	};
	
	//数据获取成功后，设置数据
	private Handler MainInsuranceHandler =new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				 tv_insurance_warn.setVisibility(View.GONE);
				 tv_insurance_warn.setText("");
				 rl_main_insurance.setVisibility(View.GONE);
				 rl_main_insurance_tab.setVisibility(View.VISIBLE);
				 iv_main_insurance_title_right.setVisibility(View.GONE);
				 tv_main_insurance_modify.setVisibility(View.VISIBLE);
				 

				Glide.with(MakeInsurancePlanActivity.this)
						.load(iconUrl)
						.placeholder(R.drawable.white_bg)
						.into(iv_tab_main_insurance);



				 //首年总保费
				 DecimalFormat df = new DecimalFormat("#####0.00");  
				 tv_total_insurance_price.setText("￥"+df.format(sum_premium));
				 
				 totalPrice=sum_premium;
				 
				//访问网络前将数据存储在mainBean中
					if(isperiodMainShow){
						mainBean.setPeriodValueCode(submitStrsCode.get(0));
						mainBean.setPeriodMiddleString(tv_insurance_period_middle.getText().toString());
					}
					if(isfrequencyMainShow){
						mainBean.setFrequencyValueCode(submitStrsCode.get(1));
						mainBean.setFrequencyMiddleString(tv_pay_frequency_year.getText().toString());
					}
					if(ispayTimeMainShow){
						mainBean.setPayTimeValueCode(submitStrsCode.get(2));
						mainBean.setPayTimeMiddleString(tv_pay_time_year.getText().toString());
					}
					if(isplanMainShow){
						mainBean.setPlanValueCode(submitStrsCode.get(3));
						mainBean.setPlanMiddleString(tv_plan_type.getText().toString());
					}
//				 mainBean.setPeriodValueCode(submitStrsCode.get(0));
//					mainBean.setFrequencyValueCode(submitStrsCode.get(1));
//					mainBean.setPayTimeValueCode(submitStrsCode.get(2));
//					mainBean.setPlanValueCode(submitStrsCode.get(3));
//					mainBean.setPeriodMiddleString(tv_insurance_period_middle.getText().toString());
//					mainBean.setFrequencyMiddleString(tv_pay_frequency_year.getText().toString());
//					mainBean.setPayTimeMiddleString(tv_pay_time_year.getText().toString());
					mainBean.setPeriodMain(periodMain);
					mainBean.setFrequencyMain(frequencyMain);
					mainBean.setPayTimeMain(payTimeMain);
					mainBean.setPlanMain(planMain);
					mainBean.setRiskMount(ed_insurance_money.getText().toString().trim());
				   dialog1.dismiss();
				 //附加险可以添加保险
				 if(outlist.get(0).isHasAdditionalInsurance()){
					 iv_additional_insurance_title_right.setImageResource(R.drawable.add_additional_risk_enable);
					 isAdditionalAdd=true;
				 }
				 //更改主险的费率因子后附加险需要重新计算
				 iv_additional_insurance_title_right.setVisibility(View.VISIBLE);
					tv_additional_insurance_modify.setVisibility(View.GONE);
					rl_additional_insurance_tab.setVisibility(View.GONE);
				 
				 isMainInsuranceEdit=true;//主险保费试算成功，改为可编辑模式
				
				JSONArray obj=(JSONArray) msg.obj;
				try {
					
					tv_tab_main_insurance.setText(obj.get(0).toString());
					tv_paytime.setText(obj.get(1).toString());
					tv_insurance_money.setText(obj.get(2).toString());
					tv_product_fee.setText(obj.get(3).toString());
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			case 1:
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
				break;
			
			}
		}
	};
	
	private Handler generateTemplateHandler =new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				dialog.dismiss();
				Intent intent=new Intent(MakeInsurancePlanActivity.this,MakeInsuranceTemplateResultActivity.class);
				intent.putExtra("description", resultDescription);
				intent.putExtra("logourl", resultLogo);
				intent.putExtra("title", resulTitle);
				intent.putExtra("url", resultUrl);
				intent.putExtra("forward_url", forward_url);
				
				Log.i("resultUrl----------", resultUrl);
				startActivity(intent);
				break;
			
			case 1:
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "生成建议书失败", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "生成建议书失败", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "生成建议书失败", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				scroll_top.setVisibility(View.GONE);
				dialog.dismiss();
				Toast.makeText(MakeInsurancePlanActivity.this, "联网失败，请检查网络", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_make_insurance_plan);
		
		insuranceTemplateLists=new ArrayList<InsuranceTemplateBean>();
		
		Intent intent = getIntent();
		ids=intent.getStringExtra("id");
		title=intent.getStringExtra("title");
//		ids="30003";

		  dialog = new Dialog(this,R.style.progress_dialog);
          dialog.setContentView(R.layout.progress_dialog);
          dialog.setCancelable(true);
          dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
          TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
          p_dialog.setText("卖力加载中...");
		init();
	}



	private void init() {

		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		Log.i("user_id---------------", user_id);
		user_id_encode=sp.getString("Login_UID_ENCODE", "");
		scroll_top=(NoAutoScrollView) findViewById(R.id.scroll_top);
				
		iv_back=(ImageView) findViewById(R.id.iv_back);
		top_title= (TextView) findViewById(R.id.tv_text);
		iv_right=(ImageView) findViewById(R.id.iv_right);
		holder_select_img=(ImageView) findViewById(R.id.holder_select_img);
		rl_additional_desc_info=(RelativeLayout) findViewById(R.id.rl_additional_desc_info);
		
		top_title.setText(title + "建议书");
		iv_right.setVisibility(View.INVISIBLE);
		//被保人信息
		ed_insuranced_name=(EditText) findViewById(R.id.ed_insuranced_name);
		rg_choice_sex=(RadioGroup) findViewById(R.id.rg_choice_sex);
		tv_age_num=(TextView) findViewById(R.id.tv_age_num);
		iv_show_calendar=(ImageView) findViewById(R.id.iv_show_calendar);
		TextWatcher inauranced_name_watcher=new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				String editable = ed_insuranced_name.getText().toString();
				String str = stringFilter(editable.toString());
				if(!editable.equals(str)){
					ed_insuranced_name.setText(str);
					//设置新的光标所在位置
					ed_insuranced_name.setSelection(str.length());
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		};
		ed_insuranced_name.addTextChangedListener(inauranced_name_watcher);
		//投保人信息
		ed_holder_name=(EditText) findViewById(R.id.ed_holder_name);
		rg_holder_choice_sex=(RadioGroup) findViewById(R.id.rg_holder_choice_sex);  
		tv_holder_age_num=(TextView) findViewById(R.id.tv_holder_age_num);
		iv_holder_show_calendar=(ImageView) findViewById(R.id.iv_holder_show_calendar);
		TextWatcher holder_name_watcher=new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				String editable = ed_holder_name.getText().toString();
				String str = stringFilter(editable.toString());
				if(!editable.equals(str)){
					ed_holder_name.setText(str);
					//设置新的光标所在位置
					ed_holder_name.setSelection(str.length());
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		};
		ed_holder_name.addTextChangedListener(holder_name_watcher);

		rl_holder_info=(RelativeLayout) findViewById(R.id.rl_holder_info);
		rl_all_holder_info=(RelativeLayout) findViewById(R.id.rl_all_holder_info);
		
		iv_main_insurance_title_right=(ImageView) findViewById(R.id.iv_main_insurance_title_right);
		tv_main_insurance_modify=(ImageButton) findViewById(R.id.tv_main_insurance_modify);
		rl_main_insurance=(RelativeLayout) findViewById(R.id.rl_main_insurance);
		rl_main_insurance_tab=(RelativeLayout) findViewById(R.id.rl_main_insurance_tab);
		iv_main_insurance=(CircleImage) findViewById(R.id.iv_main_insurance);
		tv_main_insurance=(TextView) findViewById(R.id.tv_main_insurance);
		
		tv_tab_main_insurance=(TextView) findViewById(R.id.tv_tab_main_insurance);
		tv_paytime=(TextView) findViewById(R.id.tv_paytime);
		tv_insurance_money=(TextView) findViewById(R.id.tv_insurance_money);
		tv_product_fee=(TextView) findViewById(R.id.tv_product_fee);
		iv_tab_main_insurance=(CircleImage) findViewById(R.id.iv_tab_main_insurance);
		
		tv_total_insurance_price=(TextView) findViewById(R.id.tv_total_insurance_price);
		
		rl_additional_insurance_title=(RelativeLayout) findViewById(R.id.rl_additional_insurance_title);
		
		//附加险表格
		iv_additional_insurance_title_right=(ImageView) findViewById(R.id.iv_additional_insurance_title_right);
		tv_additional_insurance_modify=(ImageButton) findViewById(R.id.tv_additional_insurance_modify);
		tv_additional_insurance_main_title=(TextView) findViewById(R.id.tv_additional_insurance_main_title);
		rl_additional_insurance_tab=(RelativeLayout) findViewById(R.id.rl_additional_insurance_tab);
		lv_additional_tab=(ListViewScrollView) findViewById(R.id.lv_additional_tab);
		
		
		View footer=View.inflate(MakeInsurancePlanActivity.this, R.layout.make_insurance_template_listview_footer, null);
		tv_total_insurance_money=(TextView) footer.findViewById(R.id.tv_total_insurance_money);
		
		tv_generate_insurance_template=(TextView)findViewById(R.id.tv_generate_insurance_template);
		
		
		
		iv_back.setOnClickListener(this);
		tv_age_num.setOnClickListener(this);
		iv_show_calendar.setOnClickListener(this);
		tv_holder_age_num.setOnClickListener(this);
		iv_holder_show_calendar.setOnClickListener(this);
		rl_holder_info.setOnClickListener(this);
		tv_generate_insurance_template.setOnClickListener(this);
		
		iv_main_insurance_title_right.setOnClickListener(this);
		tv_main_insurance_modify.setOnClickListener(this);
		iv_additional_insurance_title_right.setOnClickListener(this);
		tv_additional_insurance_modify.setOnClickListener(this);
		
		
		//被保人年龄选择
		rg_choice_sex.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				switch(arg1){
				case R.id.rb_man:
					if(insurancedSexs.equals("02")){
						clearPrem();
						tv_total_insurance_money.setText("");
					}
					insurancedSexs="01";	
					break;
				case R.id.rb_woman:
					if(insurancedSexs.equals("01")){
						clearPrem();
						tv_total_insurance_money.setText("");
					}
					insurancedSexs="02";	
					break;
				}
			}
		});
		
		//投保人年龄选择
		rg_holder_choice_sex.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				switch(arg1){
				case R.id.rb_holder_man:
					holderSexs="01";
					break;
				case R.id.rb_holder_woman:
					holderSexs="02";
					break;
				}
			}
		});
		
		String url=IpConfig.getUri2("getSelectProduct");
		url=url+"user_id="+user_id_encode+"&product_ids="+ids;
		Log.i("传过去的数据地址--------------", url);
	    key_value.put("token",  token);
		fetchData(url);
	}

	private void fetchData(String url) {
		dialog.show();
		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Message msg=new Message();
				msg.what=4;
				resultHandler.sendMessage(msg);
			}

			@Override
			public void onResponse(String response, int id) {
				String jsonString = response;
				Log.d("获取到数据了-----------------", "onSuccess json = " + jsonString);

				try {

					Log.d("44444", jsonString);
					if (jsonString.equals("") || jsonString.equals("null")) {
						String status = "false";
						Message msg=new Message();
						msg.what=3;
						resultHandler.sendMessage(msg);
					} else {

						JSONObject jsonObject = new JSONObject(jsonString);
						if(jsonObject.getString("status").equals("false")){
							Message msg=new Message();
							msg.what=2;
							resultHandler.sendMessage(msg);
							return;
						}
						JSONObject dataObj=jsonObject.getJSONObject("data");
						//年龄范围
						JSONObject paramsObj=dataObj.getJSONObject("params");
						JSONObject ageObj=paramsObj.getJSONObject("age");
						min_age=Integer.parseInt(ageObj.getString("min_age"));
						max_age=Integer.parseInt(ageObj.getString("max_age"));



						JSONArray dataList = dataObj.getJSONArray("products");
						outlist=new ArrayList<MakeInsuranceTemplateBean>();
						//先清空数据，避免重复添加
						insuranceTemplateLists.clear();
						for(int i=0;i<dataList.length();i++){
							MakeInsuranceTemplateBean bean=new MakeInsuranceTemplateBean();
							JSONObject obj=(JSONObject) dataList.get(i);

							JSONObject insuranceObj=obj.getJSONObject("prodinfo");
							JSONObject riskObj=obj.getJSONObject("riskamount");
							//							JSONObject premObj=obj.getJSONObject("prem");
							JSONArray arrayInput=obj.getJSONArray("inputfields");

							//							bean.setProduct_id(map.get("id"));
							//							bean.setProduct_name(map.get("name"));
							//							bean.setCompany_id(map.get("company_code"));
							//							bean.setIshuomian(Integer.parseInt(map.get("ishuomian")));
							//							bean.setIsmust(Integer.parseInt(map.get("ismust")));
							//							bean.setMaster_id(map.get("master_id"));
							//可有可无的属性
							//							bean.setDisplay_order(0);
							//							bean.setId("");
							//							bean.setSuggest_id("");
							//							bean.setAdd_time("");
							//这个集合存储用于提交制作建议书
							InsuranceTemplateBean templateBean=new InsuranceTemplateBean();
							templateBean.setProduct_id(insuranceObj.getString("id"));
							templateBean.setProduct_name(insuranceObj.getString("name"));
							templateBean.setCompany_id(insuranceObj.getString("company_code"));
							templateBean.setHuomian(insuranceObj.getString("ishuomian"));
							templateBean.setMust(insuranceObj.getString("is_main"));
							templateBean.setMaster_id(insuranceObj.getString("parent"));
							templateBean.setDisplay_order(0);
							templateBean.setId("");
							templateBean.setSuggest_id("");
							templateBean.setAdd_time("");
							insuranceTemplateLists.add(templateBean);


							bean.setInsuranceId(insuranceObj.getString("id"));
							bean.setHuomian(insuranceObj.getString("ishuomian"));
							bean.setIcon(insuranceObj.getString("icon"));
							bean.setInsuranceName(insuranceObj.getString("name"));
							bean.setInsuranceType(insuranceObj.getString("type"));
							bean.setInsuranceType_name(insuranceObj.getString("type_name"));
							JSONArray additionsArray=insuranceObj.getJSONArray("additions");
							if(additionsArray.length()>0){
								//有附加险
								isHasAdditionInsurance=true;
								bean.setHasAdditionalInsurance(true);
								additionOutList=new ArrayList<MakeInsuranceTemplateBean>();
								for(int n=0;n<additionsArray.length();n++){
									MakeInsuranceTemplateBean additionalBean=new MakeInsuranceTemplateBean();
									JSONObject additionalObj=(JSONObject) additionsArray.get(n);

									JSONObject additionalInsuranceObj=additionalObj.getJSONObject("prodinfo");
									JSONObject additionalRiskObj=additionalObj.getJSONObject("riskamount");
									JSONArray additionalArrayInput=additionalObj.getJSONArray("inputfields");

									//这个集合存储用于提交制作建议书
									InsuranceTemplateBean templateBean1=new InsuranceTemplateBean();
									templateBean1.setProduct_id(additionalInsuranceObj.getString("id"));
									templateBean1.setProduct_name(additionalInsuranceObj.getString("name"));
									templateBean1.setCompany_id(additionalInsuranceObj.getString("company_code"));
									templateBean1.setHuomian(additionalInsuranceObj.getString("ishuomian"));
									templateBean1.setMust(additionalInsuranceObj.getString("is_main"));
									templateBean1.setMaster_id(additionalInsuranceObj.getString("parent"));
									templateBean1.setDisplay_order(0);
									templateBean1.setId("");
									templateBean1.setSuggest_id("");
									templateBean1.setAdd_time("");
									insuranceTemplateLists.add(n+1,templateBean1);

									additionalBean.setInsuranceId(additionalInsuranceObj.getString("id"));
									additionalBean.setHuomian(additionalInsuranceObj.getString("ishuomian"));
									additionalBean.setIcon(additionalInsuranceObj.getString("icon"));
									additionalBean.setInsuranceName(additionalInsuranceObj.getString("name"));
									additionalBean.setInsuranceType(additionalInsuranceObj.getString("type"));
									additionalBean.setInsuranceType_name(additionalInsuranceObj.getString("type_name"));

									additionalBean.setRiskHint(additionalRiskObj.getString("hint"));
									additionalBean.setRiskTitle(additionalRiskObj.getString("title"));
									additionalBean.setRiskUnitTitle(additionalRiskObj.getString("unit_title"));

									additionInnerList=new ArrayList<MakeInsuranceTemplateInnerBean> ();
									for(int k=0;k<additionalArrayInput.length();k++){
										MakeInsuranceTemplateInnerBean innerBean=new MakeInsuranceTemplateInnerBean();
										JSONObject innerObj=(JSONObject) additionalArrayInput.get(k);
										innerBean.setInput_type(innerObj.getString("input_type"));
										innerBean.setTitle(innerObj.getString("title"));
										innerBean.setTitle_key(innerObj.getString("title_key"));

										JSONArray innerEnd=innerObj.getJSONArray("valueoptions");
										ArrayList<String> valueCode=new ArrayList<String>();
										ArrayList<String> valueString=new ArrayList<String>();
										for(int p=0;p<innerEnd.length();p++){
											JSONArray array=(JSONArray) innerEnd.get(p);
											valueCode.add(p, array.get(0).toString());
											valueString.add(p, array.get(1).toString());
										}
										innerBean.setValueCode(valueCode);
										innerBean.setValueString(valueString);
										additionInnerList.add(innerBean);
									}

									additionalBean.setInnerBeanList(additionInnerList);
									//最外层的集合添加数据
									additionOutList.add(additionalBean);

								}
							}else{
								bean.setHasAdditionalInsurance(false);
							}



							bean.setRiskHint(riskObj.getString("hint"));
							bean.setRiskTitle(riskObj.getString("title"));
							bean.setRiskUnitTitle(riskObj.getString("unit_title"));

							//解析inner
							innerlist=new ArrayList<MakeInsuranceTemplateInnerBean> ();
							for(int m=0;m<arrayInput.length();m++){
								MakeInsuranceTemplateInnerBean innerBean=new MakeInsuranceTemplateInnerBean();
								JSONObject innerObj=(JSONObject) arrayInput.get(m);
								innerBean.setInput_type(innerObj.getString("input_type"));
								innerBean.setTitle(innerObj.getString("title"));
								innerBean.setTitle_key(innerObj.getString("title_key"));

								JSONArray innerEnd=innerObj.getJSONArray("valueoptions");
								ArrayList<String> valueCode=new ArrayList<String>();
								ArrayList<String> valueString=new ArrayList<String>();
								for(int n=0;n<innerEnd.length();n++){
									JSONArray array=(JSONArray) innerEnd.get(n);
									valueCode.add(n, array.get(0).toString());
									valueString.add(n, array.get(1).toString());
								}
								innerBean.setValueCode(valueCode);
								innerBean.setValueString(valueString);
								innerlist.add(innerBean);
							}

							bean.setInnerBeanList(innerlist);
							//最外层的集合添加数据
							outlist.add(bean);
						}
						Message msg=new Message();
						msg.what=0;
						resultHandler.sendMessage(msg);

					}

				} catch (Exception e) {
					e.printStackTrace();
					Message msg=new Message();
					msg.what=1;
					resultHandler.sendMessage(msg);
				}

			}

		});
		
		
		

		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_holder_info:
			if(isShown){
				rl_all_holder_info.setVisibility(View.GONE);
				holder_select_img.setImageResource(R.drawable.p_s_not_select);
				
				isShown=false;
			}else{
				holder_select_img.setImageResource(R.drawable.p_s_select);
				rl_all_holder_info.setVisibility(View.VISIBLE);
				isShown=true;
			}
			break;
		case R.id.tv_age_num:
			final String oldAge=tv_age_num.getText().toString();
			if(ageRangeList.size()>0){
				ageArray= ageRangeList.toArray(new String[ageRangeList.size()]);
			}
			ListDialog dialog=new ListDialog(MakeInsurancePlanActivity.this,ageArray,"请选择年龄") {
				@Override
				public void item(int m) {
					int age=Integer.parseInt(ageArray[m]);
					if(age>=min_age&&age<=max_age){
						//清除被保人的生日
						insured_birthday="";
						tv_age_num.setText(ageArray[m]);
						String newAge=tv_age_num.getText().toString();
						if(!oldAge.equals(newAge)){
							clearPrem();
							tv_total_insurance_money.setText("");
						}
					}else{
						Toast.makeText(MakeInsurancePlanActivity.this,"年龄范围应在"+min_age+"至"+max_age+"之间", Toast.LENGTH_SHORT).show();
					}
					
				}
			};
		
			break;
		case R.id.iv_show_calendar:
			showCalendar(1);
			break;
		case R.id.tv_holder_age_num:
			ListDialog dialogHolder=new ListDialog(MakeInsurancePlanActivity.this,ageArray,"请选择年龄") {
				@Override
				public void item(int m) {
					tv_holder_age_num.setText(ageArray[m]);
				}
			};
			break;
		case R.id.iv_holder_show_calendar:
			showCalendar(2);
			break;
		case R.id.tv_generate_insurance_template:
			generateTemplate();
			break;
		case R.id.iv_main_insurance_title_right:
			//发送年龄信息，根据年龄获取保额的范围
			String url=IpConfig.getUri2("getSelectProduct");
			String age=getInsurancedAge()+"";
			url=url+"age="+age+"&user_id="+user_id_encode+"&product_ids="+ids;
			Log.i("传过去的数据地址--------------", url);
			fetchData(url);
			isFirstSelectProduct=false;
			
			break;
		case R.id.tv_main_insurance_modify:
			showAddMainInsuranceDialog();
			break;
		case R.id.iv_additional_insurance_title_right:
			if(!isAdditionalAdd){
				return;
			}else{
				Intent intent=new Intent(MakeInsurancePlanActivity.this,AddAdditionalInsuranceActivity.class);
				intent.putExtra("additionOutList", additionOutList);
				intent.putExtra("additionInnerList", additionInnerList);
				intent.putExtra("insuranceTemplateLists", insuranceTemplateLists);
				intent.putExtra("isModify", false);
				//添加主险的信息
				intent.putExtra("jsonStr", jsonStr);
				intent.putExtra("mainIds", ids);
				intent.putExtra("mainInsuranceName", outlist.get(0).getInsuranceName());
				startActivityForResult(intent, 0);  
			}
			break;
		case R.id.tv_additional_insurance_modify:
			
				Intent intent=new Intent(MakeInsurancePlanActivity.this,AddAdditionalInsuranceActivity.class);
				intent.putExtra("additionOutList", additionOutList);
				intent.putExtra("additionInnerList", additionInnerList);
				intent.putExtra("insuranceTemplateLists", insuranceTemplateLists);
				intent.putExtra("isModify", true);
				if(additionalInsuranceList.size()>0){
					intent.putExtra("modifyInfos", additionalInsuranceList);
				}
				
				//添加主险的信息
				intent.putExtra("jsonStr", jsonStr);
				intent.putExtra("mainIds", ids);
				intent.putExtra("mainInsuranceName", outlist.get(0).getInsuranceName());
				startActivityForResult(intent, 0);
			break;
		}
	}

	
	
	private void showAddMainInsuranceDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder build = new Builder(MakeInsurancePlanActivity.this);
		View contentView = View.inflate(MakeInsurancePlanActivity.this, R.layout.dialog_add_main_insurance, null);
		TextView tv_is_group=(TextView) contentView.findViewById(R.id.tv_is_group);
		TextView tv_insurance_title=(TextView) contentView.findViewById(R.id.tv_insurance_title);
		ImageView iv_check=(ImageView) contentView.findViewById(R.id.iv_check);
		
		RelativeLayout rl_item_insurance_period=(RelativeLayout) contentView.findViewById(R.id.rl_item_insurance_period);
		TextView tv_insurance_period_left=(TextView) contentView.findViewById(R.id.tv_insurance_period_left);
		tv_insurance_period_middle=(TextView) contentView.findViewById(R.id.tv_insurance_period_middle);
		
		RelativeLayout rl_item_pay_frequency=(RelativeLayout) contentView.findViewById(R.id.rl_item_pay_frequency);
		TextView tv_pay_frequency_left=(TextView) contentView.findViewById(R.id.tv_pay_frequency_left);
		tv_pay_frequency_year=(TextView) contentView.findViewById(R.id.tv_pay_frequency_year);
		
		RelativeLayout rl_item_pay_time=(RelativeLayout) contentView.findViewById(R.id.rl_item_pay_time);	
		TextView tv_pay_time_left=(TextView) contentView.findViewById(R.id.tv_pay_time_left);
		tv_pay_time_year=(TextView) contentView.findViewById(R.id.tv_pay_time_year);
		
		RelativeLayout rl_item_plan=(RelativeLayout) contentView.findViewById(R.id.rl_item_plan);
		TextView tv_plan_left=(TextView) contentView.findViewById(R.id.tv_plan_left);
		tv_plan_type=(TextView) contentView.findViewById(R.id.tv_plan_type);
		
		TextView tv_insurance_money_left=(TextView) contentView.findViewById(R.id.tv_insurance_money_left);
		ed_insurance_money=(EditText) contentView.findViewById(R.id.ed_insurance_money);
		tv_insurance_warn=(TextView) contentView.findViewById(R.id.tv_insurance_warn);
		
		ImageView iv_choice_insurance_period=(ImageView) contentView.findViewById(R.id.iv_choice_insurance_period);
		ImageView iv_choice_pay_frequency=(ImageView) contentView.findViewById(R.id.iv_choice_pay_frequency);
		ImageView iv_choice_pay_time=(ImageView) contentView.findViewById(R.id.iv_choice_pay_time);
		
		final MakeInsuranceTemplateBean bean=outlist.get(0);
		tv_insurance_title.setText(bean.getInsuranceName());
		tv_insurance_money_left.setText(bean.getRiskTitle());
		
		if(isMainInsuranceEdit){
			ed_insurance_money.setText(mainBean.getRiskMount());
		}else{
			ed_insurance_money.setHint(bean.getRiskHint());
		}
		
		
		int listSize=bean.getInnerBeanList().size();
		for(int i=0;i<listSize;i++){
			if(bean.getInnerBeanList().get(i).getTitle_key().equals("insurance_period_code")){
				//保险期间
				rl_item_insurance_period.setVisibility(View.VISIBLE);
				isperiodMainShow=true;
				tv_insurance_period_left.setText(bean.getInnerBeanList().get(i).getTitle());
				if(isMainInsuranceEdit){
					tv_insurance_period_middle.setText(mainBean.getPeriodMiddleString());
					periodMain=mainBean.getPeriodMain();
					
				}else{
					tv_insurance_period_middle.setText(bean.getInnerBeanList().get(i).getValueString().get(0));
					periodMain=0;
				}
				periodIndex=i;
			}else if(bean.getInnerBeanList().get(i).getTitle_key().equals("pay_period_code")){
				//缴费期间
				rl_item_pay_time.setVisibility(View.VISIBLE);
				ispayTimeMainShow=true;
				tv_pay_time_left.setText(bean.getInnerBeanList().get(i).getTitle());
				if(isMainInsuranceEdit){
					tv_pay_time_year.setText(mainBean.getPayTimeMiddleString());
					payTimeMain=mainBean.getPayTimeMain();
				}else{
					tv_pay_time_year.setText(bean.getInnerBeanList().get(i).getValueString().get(0));
					payTimeMain=0;
				}
				payTimeIndex=i;
			}else if(bean.getInnerBeanList().get(i).getTitle_key().equals("pay_type")){
				//缴费频率
				rl_item_pay_frequency.setVisibility(View.VISIBLE);
				isfrequencyMainShow=true;
				tv_pay_frequency_left.setText(bean.getInnerBeanList().get(i).getTitle());
				if(isMainInsuranceEdit){
					tv_pay_frequency_year.setText(mainBean.getFrequencyMiddleString());
					frequencyMain=mainBean.getFrequencyMain();
				}else{
					tv_pay_frequency_year.setText(bean.getInnerBeanList().get(i).getValueString().get(0));
					frequencyMain=0;
				}
				frequencyIndex=i;
			}else if(bean.getInnerBeanList().get(i).getTitle_key().equals("disc_code")){
				//计划
				isplanMainShow=true;
				rl_item_plan.setVisibility(View.VISIBLE);
				tv_plan_left.setText(bean.getInnerBeanList().get(i).getTitle());
				if(isMainInsuranceEdit){
					tv_plan_type.setText(mainBean.getPlanMiddleString());
					planMain=mainBean.getPlanMain();
					
				}else{
					tv_plan_type.setText(bean.getInnerBeanList().get(i).getValueString().get(0));
					planMain=0;
				}
				planIndex=i;
			}
		}
		
		//保险期间点击事件
		tv_insurance_period_middle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				ArrayList<String> insurancePeriodStringList=bean.getInnerBeanList().get(periodIndex).getValueString();
				final ArrayList<String> insurancePeriodCodeList=bean.getInnerBeanList().get(periodIndex).getValueCode();
				final String[] insurancePeriodArray= insurancePeriodStringList.toArray(new String[insurancePeriodStringList.size()]);
				ListDialog dialog0=new ListDialog(MakeInsurancePlanActivity.this,insurancePeriodArray,"请选择保险期限") {
					@Override
					public void item(int m) {
						// TODO Auto-generated method stub
//						Toast.makeText(context, "选择了"+insurancePeriodArray[m]+insurancePeriodCodeList.get(m), Toast.LENGTH_SHORT).show();
						tv_insurance_period_middle.setText(insurancePeriodArray[m]);
						periodMain=m;
					}
				};
			
			}
		});
		
		//缴费频率点击事件
		tv_pay_frequency_year.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<String> insuranceFrequencyStringList=bean.getInnerBeanList().get(frequencyIndex).getValueString();
				final ArrayList<String> insuranceFrequencyCodeList=bean.getInnerBeanList().get(frequencyIndex).getValueCode();
				final String[] insuranceFrequencyArray= insuranceFrequencyStringList.toArray(new String[insuranceFrequencyStringList.size()]);
				ListDialog dialog0=new ListDialog(MakeInsurancePlanActivity.this,insuranceFrequencyArray,"请选择交费方式") {
					@Override
					public void item(int m) {
						// TODO Auto-generated method stub
//						Toast.makeText(context, "选择了"+insuranceFrequencyArray[m]+insuranceFrequencyCodeList.get(m), Toast.LENGTH_SHORT).show();
						tv_pay_frequency_year.setText(insuranceFrequencyArray[m]);
						frequencyMain=m;
					}
				};
			}
		});
		//缴费期限点击事件
		tv_pay_time_year.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<String> insurancePayTimeStringList=bean.getInnerBeanList().get(payTimeIndex).getValueString();
				final ArrayList<String> insurancePayTimeCodeList=bean.getInnerBeanList().get(payTimeIndex).getValueCode();
				final String[] insurancePayTimeArray= insurancePayTimeStringList.toArray(new String[insurancePayTimeStringList.size()]);
				ListDialog dialog0=new ListDialog(MakeInsurancePlanActivity.this,insurancePayTimeArray,"请选择交费期限") {
					@Override
					public void item(int m) {
						// TODO Auto-generated method stub
//						Toast.makeText(context, "选择了"+insurancePayTimeArray[m]+insurancePayTimeCodeList.get(m), Toast.LENGTH_SHORT).show();
						tv_pay_time_year.setText(insurancePayTimeArray[m]);
						payTimeMain=m;
					}
				};
			}
		});
		
		
		//计划点击事件
		tv_plan_type.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				ArrayList<String> insurancePlanStringList=bean.getInnerBeanList().get(planIndex).getValueString();
				final ArrayList<String> insurancePlanCodeList=bean.getInnerBeanList().get(planIndex).getValueCode();
				final String[] insurancePlanArray= insurancePlanStringList.toArray(new String[insurancePlanStringList.size()]);
				ListDialog dialog0=new ListDialog(MakeInsurancePlanActivity.this,insurancePlanArray,"请选择计划") {
					@Override
					public void item(int m) {
						// TODO Auto-generated method stub
//						Toast.makeText(context, "选择了"+insurancePeriodArray[m]+insurancePeriodCodeList.get(m), Toast.LENGTH_SHORT).show();
						tv_plan_type.setText(insurancePlanArray[m]);
						planMain=m;
					}
				};
			
			}
		});
		
		
		TextView ok = (TextView) contentView.findViewById(R.id.tv_ok);
		TextView cancel = (TextView) contentView.findViewById(R.id.tv_cancel);
		build.setCancelable(false);
		//取消键
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 消掉对话框
				dialog1.dismiss();
			}
		});
		
		//确定键的动作
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String riskMount=ed_insurance_money.getText().toString().trim();
				if("请选择".equals(riskMount) || "null".equals(riskMount) || "".equals(riskMount)){
					Toast.makeText(MakeInsurancePlanActivity.this, "请输入保额", Toast.LENGTH_SHORT).show();
					return;
				}
				
				ArrayList<String> submitStrsName=new ArrayList<String>();
				if(isperiodMainShow){
					submitStrsName.add("insurance_period_code");
				}
				if(isfrequencyMainShow){
					submitStrsName.add("pay_type");//缴费频率
				}
				if(ispayTimeMainShow){
					submitStrsName.add("pay_period_code");//缴费期间
				}
				if(isplanMainShow){
					submitStrsName.add("disc");//计划
				}
				
				
				
				
				submitStrsCode=new ArrayList<String>();
				if(isperiodMainShow){
					submitStrsCode.add(bean.getInnerBeanList().get(periodIndex).getValueCode().get(periodMain));
				}
				if(isfrequencyMainShow){
					submitStrsCode.add(bean.getInnerBeanList().get(frequencyIndex).getValueCode().get(frequencyMain));
				}
				if(ispayTimeMainShow){
					submitStrsCode.add(bean.getInnerBeanList().get(payTimeIndex).getValueCode().get(payTimeMain));
				}
				if(isplanMainShow){
					submitStrsCode.add(bean.getInnerBeanList().get(planIndex).getValueCode().get(planMain));
				}
				
				
				
				
				String product_id=bean.getInsuranceId();
				int insurance_charge=Integer.parseInt(riskMount);
				String sexs=insurancedSexs;
				int pay_age=getInsurancedAge();
				
				fetchData(product_id,insurance_charge,sexs,pay_age,submitStrsName,submitStrsCode,0);
				
			}
		});
		dialog1 = build.create();
		dialog1.setView(contentView, 0, 0, 0, 0);
		dialog1.show();
		
		dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); 
	}

	//选择被保人的年龄，弹出选择框
	private void showCalendar(final int whichAge) {
		// TODO Auto-generated method stub

		LayoutInflater inflater1 = LayoutInflater
				.from(MakeInsurancePlanActivity.this);
		final View timepickerview1 = inflater1.inflate(
				R.layout.timepicker, null);
		ScreenInfo screenInfo1 = new ScreenInfo(MakeInsurancePlanActivity.this);
		wheelMain = new WheelMain(timepickerview1);
		wheelMain.screenheight = screenInfo1.getHeight();
		Calendar calendar1 = Calendar.getInstance();
		try {
			calendar1.setTime(dateFormat.parse(dateFormat
					.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int year1 = calendar1.get(Calendar.YEAR);
		int month1 = calendar1.get(Calendar.MONTH);
		int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year1, month1, day1);
		final MyAlertDialog dialog = new MyAlertDialog(
				MakeInsurancePlanActivity.this).builder()
				.setTitle("请选择")
			 // .setMsg("22")
		     // .setEditText("111")
				.setView(timepickerview1)
				.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
		dialog.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				birthday = wheelMain.getTime();
				Log.i("选择的年龄------------------", birthday);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
				try {
					Date date = sdf.parse(birthday);
					ageWeel=getAge(date);
					if(whichAge==1){
						if(ageWeel>=min_age&&ageWeel<=max_age){
							String oldAge=tv_age_num.getText().toString();
							tv_age_num.setText(ageWeel+"");
							insured_birthday=birthday;
							String newAge=tv_age_num.getText().toString();
							if(!oldAge.equals(newAge)){
								clearPrem();
								tv_total_insurance_money.setText("");
							}
							Toast.makeText(getApplicationContext(),
									wheelMain.getTime(), Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(MakeInsurancePlanActivity.this,"年龄范围应在"+min_age+"至"+max_age+"之间", Toast.LENGTH_SHORT).show();
						}
						
					}else{
						holder_birthday=birthday;
						tv_holder_age_num.setText(ageWeel+"");
						Toast.makeText(getApplicationContext(),
								wheelMain.getTime(), Toast.LENGTH_SHORT).show();
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		});
		dialog.show();
	}

	public static int getAge(Date birthDay) throws Exception { 
        //获取当前系统时间
        Calendar cal = Calendar.getInstance(); 
        //如果出生日期大于当前时间，则抛出异常
//        if (cal.before(birthDay)) { 
//            throw new IllegalArgumentException( 
//                "The birthDay is before Now.It's unbelievable!"); 
//        } 
        //取出系统当前时间的年、月、日部分
        int yearNow = cal.get(Calendar.YEAR); 
        int monthNow = cal.get(Calendar.MONTH); 
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); 
         
        //将日期设置为出生日期
        cal.setTime(birthDay); 
        //取出出生日期的年、月、日部分  
        int yearBirth = cal.get(Calendar.YEAR); 
        int monthBirth = cal.get(Calendar.MONTH); 
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH); 
        //当前年份与出生年份相减，初步计算年龄
        int age = yearNow - yearBirth; 
        //当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
       //
       if (monthNow <= monthBirth) {
           if (monthNow == monthBirth) {
               //monthNow==monthBirth
               if (dayOfMonthNow < dayOfMonthBirth) {
                   age--;
               } else {
               }
           } else {
               age--;
           }
       } else {
       }

       return age;
    }
	
	
	/**得到被保人的年龄
	 * @return
	 */
	public int getInsurancedAge(){
		String ageStr=tv_age_num.getText().toString();
		if("".equals(ageStr) || "null".equals(ageStr)){
			insurancedAge=-1;
		}else{
			insurancedAge=Integer.parseInt(ageStr);
		}
		return insurancedAge;
	}
	
	/**得到被保人的姓名
	 * @return
	 */
	public String getInsurancedName(){
		insurancedName=ed_insuranced_name.getText().toString().trim();
		return insurancedName;
			
	}
	
	
	/**得到投保人的年龄
	 * @return
	 */
	public int getHolderAge(){
		String ageStr=tv_holder_age_num.getText().toString();
		if("".equals(ageStr) || "null".equals(ageStr)){
			holderAge=0;
		}else{
			holderAge=Integer.parseInt(ageStr);
		}
		return holderAge;
	}
	
	/**得到投保人保人的姓名
	 * @return
	 */
	public String getHolderName(){
		holderName=ed_holder_name.getText().toString().trim();
		return holderName;
	}
	
	
	/**
	 * 生成建议书
	 */
	public void generateTemplate(){
		
		try 
		{
//		public ArrayList<InsuranceTemplateBean> insuranceTemplateLists;
			if(TextUtils.isEmpty(tv_total_insurance_price.getText())||submitInsuranceList.size()<1){
				Toast.makeText(MakeInsurancePlanActivity.this, "您还没计算任何保险", Toast.LENGTH_SHORT).show();
				return;
			}
			
			
			
			JSONArray array=new JSONArray();
			for(int c=0;c<submitInsuranceList.size();c++){
			InsuranceTemplateBean bean=submitInsuranceList.get(c);
			JSONArray arrayFactors=new JSONArray();
		
				for(int g=0;g<bean.getFactorsName().size();g++){
					JSONArray array01=new JSONArray();
					array01.put(0, bean.getFactorsName().get(g));
					array01.put(1, bean.getFactorsValue().get(g));
					arrayFactors.put(g, array01);
				} 
				JSONObject obj1=new JSONObject();
				obj1.put("factors", arrayFactors);
				obj1.put("id",bean.getId());
				obj1.put("suggest_id", bean.getSuggest_id());
				obj1.put("product_id", bean.getProduct_id());
				obj1.put("product_name", bean.getProduct_name());
				obj1.put("company_id", bean.getCompany_id());
				obj1.put("master_id", bean.getMaster_id());
				obj1.put("prem", bean.getPrem());
				obj1.put("amount", bean.getAmount());
				obj1.put("ismust", bean.getIsmust());
				obj1.put("ishuomian", bean.getIshuomian());
				obj1.put("display_order", bean.getDisplay_order());
				obj1.put("add_time", bean.getAdd_time());
				
				array.put(c, obj1);
			}
			
			JSONObject obj2=new JSONObject();
			obj2.put("id","");
			obj2.put("user_id",user_id);
			obj2.put("token",token);
			obj2.put("holder_name",getHolderName());
			obj2.put("holder_sex",holderSexs);
			obj2.put("insured_birthday",insured_birthday);
			obj2.put("holder_phone","");
			obj2.put("insured_name",getInsurancedName());
			obj2.put("insured_sex",insurancedSexs);
			obj2.put("insured_age",getInsurancedAge());
			obj2.put("insured_phone","");
			obj2.put("total_prem",totalPrice);
			obj2.put("proposal_url","");
			obj2.put("add_time","");
			JSONObject obj=new JSONObject();
			obj.put("suggest_products", array);
			obj.put("suggest", obj2);
			String str=obj.toString();
			//提交数据
//			String url=IpConfig.getUri4("saveSuggestInfo");
			String url=IpConfig.getUri2("saveSuggestInfo");
//			String url="http://192.168.1.24:8080/M1/proposal/saveSuggestInfo.do?";
//			Log.i("提交----的json串----------", str);
			
			
//			Log.i("制作建议书----------", url+"user_id="+user_id+"&token="+token);
			key_value.put("user_id",user_id);
		    key_value.put("token",  token);
		    key_value.put("suggest_info",  str);
		    dialog.show();
		    Log.i("========start====","");
		    Log.i("------------token:",token);
		    Log.i("------------url:",url);
		    Log.i("------------user_id:",user_id);
		    Log.i("========end====","");
		    
			OkHttpUtils.post()//
			.url(url)//
			.params(key_value)//
			.build()//
			.execute(new StringCallback() {

				@Override
				public void onError(Call call, Exception e, int id) {
					Message msg=new Message();
					msg.what=4;
					generateTemplateHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(String response, int id) {
					String jsonString= response;
					Log.i("返回的数据---------------", jsonString);
					try {
						if (jsonString.equals("") || jsonString.equals("null")) {
							Message message = new Message();
							message.what = 3;
							generateTemplateHandler.sendMessage(message);}
						else{
							JSONObject objResult=new JSONObject(jsonString);
							String status=objResult.getString("status");
							if(status.equals("true")){
								JSONObject obj=objResult.getJSONObject("data");
								resultUrl=obj.getString("url");
								resultDescription=obj.getString("description");
								resulTitle=obj.getString("title");
								resultLogo=obj.getString("logourl");
								forward_url=obj.getString("forward_url");
								Message msg=new Message();
								msg.what=0;
								generateTemplateHandler.sendMessage(msg);
							}else{
								Message msg=new Message();
								msg.what=2;
								generateTemplateHandler.sendMessage(msg);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Message msg=new Message();
						msg.what=1;
						generateTemplateHandler.sendMessage(msg);
					}
				}
			});
			
			

		    
		    
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	//更改被保人的性别与年龄的时候将每个条目的保费清空
	public void clearPrem(){
		//清空主险和附加险
		 rl_main_insurance.setVisibility(View.VISIBLE);
		 rl_main_insurance_tab.setVisibility(View.GONE);
		 iv_main_insurance_title_right.setVisibility(View.VISIBLE);
		 tv_main_insurance_modify.setVisibility(View.GONE);
		 iv_additional_insurance_title_right.setVisibility(View.VISIBLE);
		 iv_additional_insurance_title_right.setImageResource(R.drawable.add_additional_risk_disable);
		 tv_additional_insurance_modify.setVisibility(View.GONE);
		 rl_additional_insurance_tab.setVisibility(View.GONE);
		 isAdditionalAdd=false;
		 tv_total_insurance_price.setText("");
		 totalPrice=0;
	}
	
	
	
	
	
	
	
	
	
	public void fetchData(String product_id,
			int insurance_charge, String sexs, int pay_age,
			ArrayList listName,ArrayList listCode,final int indexPosition) {
		// TODO Auto-generated method stub
		//产品id,保额，被保人性别，被保人年龄，
		try {
		    //新的解析方式
		    //被保人信息
			
		    JSONObject insuranceObj=new JSONObject(); 
		    insuranceObj.put("name", getInsurancedName());
		    insuranceObj.put("sexs", sexs);
		    insuranceObj.put("birthday", insured_birthday);
		    insuranceObj.put("age",pay_age+"");
		    //投保人信息
		    JSONObject  holderObj=new JSONObject();
		    holderObj.put("name", getHolderName());
		    holderObj.put("sexs", holderSexs);
		    holderObj.put("birthday", holder_birthday);
		    holderObj.put("age",getHolderAge()+"");
			//产品参数
		    JSONArray array1=new JSONArray();
			for(int y=0;y<listName.size();y++){
				JSONArray array2=new JSONArray();
				array2.put(0, listName.get(y));
				array2.put(1, listCode.get(y));
				array1.put(y, array2);
			}
			 JSONArray array3=new JSONArray();
			 array3.put(0,"premium");
			 array3.put(1,0);
			 JSONArray array4=new JSONArray();
			 array4.put(0,"amount");//保额
			 array4.put(1,insurance_charge);
			 array1.put(listName.size(), array3);
			 array1.put(listName.size()+1, array4);
			 
		    JSONObject productObj=new JSONObject();
		    productObj.put("product_id",product_id);
		    productObj.put("is_main","true");
		    productObj.put("parent","");
		    productObj.put("inputfields",array1);
		    
		    JSONArray productsArray=new JSONArray();
		    productsArray.put(0, productObj);
		    
		    JSONObject planObj=new JSONObject();
		    planObj.put("insurance", insuranceObj);
		    planObj.put("products", productsArray);
		    
		    JSONObject obj=new JSONObject();
		    obj.put("policyHolder", holderObj);
		    obj.put("plan", planObj);
		    
		    String str=obj.toString();
		   //把要传递给附加险的json串赋值
		    jsonStr=str;
		    
//		    String url="http://192.168.1.24:8080/M1/proposal/calInsuranceCharge.do?";
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
					MainInsuranceHandler.sendEmptyMessage(4);
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
							JSONObject productObject=productsArray.getJSONObject(0);

							JSONArray inputfieldsArray=productObject.getJSONArray("inputfields");
							ArrayList<String> factorsName=new ArrayList<String>();
							ArrayList<String> factorsValue=new ArrayList<String>();
							for(int b=0;b<inputfieldsArray.length();b++){
								JSONArray array1=(JSONArray) inputfieldsArray.get(b);
								factorsName.add(b,array1.getString(0));
								factorsValue.add(b,array1.getString(1));
							}

							insuranceTemplateLists.get(0).setFactorsName(factorsName);
							insuranceTemplateLists.get(0).setFactorsValue(factorsValue);

							JSONObject tableObject=productObject.getJSONObject("table");
							JSONArray rowArray=tableObject.getJSONArray("row");
							insuranceTemplateLists.get(0).setPrem(Double.parseDouble(rowArray.get(3).toString()));
							insuranceTemplateLists.get(0).setAmount(Integer.parseInt(rowArray.get(2).toString()));

							Message msg=new Message();
							msg.what=0;
							msg.obj=rowArray;
							MainInsuranceHandler.sendMessage(msg);
						}else{
							String errmsg=objResult.getString("errmsg");
							Message msg=new Message();
							msg.what=2;
							msg.obj=errmsg;
							MainInsuranceHandler.sendMessage(msg);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						MainInsuranceHandler.sendEmptyMessage(1);
					}
				}
			});
			
			

	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		  	
		if(resultCode==11){
			iv_additional_insurance_title_right.setVisibility(View.GONE);
			tv_additional_insurance_modify.setVisibility(View.VISIBLE);
			rl_additional_insurance_tab.setVisibility(View.VISIBLE);
			additionalTabList=(ArrayList<TableRowBean>) data.getSerializableExtra("additionalTabList");
			//接收返回的附加险信息
			additionalInsuranceList=(ArrayList<SubmitInsurancePlanBean>) data.getSerializableExtra("additionalInsuranceList");
			ArrayList<InsuranceTemplateBean> list=(ArrayList<InsuranceTemplateBean>) data.getSerializableExtra("backInsuranceTemplateLists");
			if(submitInsuranceList.size()>1){
				submitInsuranceList.clear();
				submitInsuranceList.add(insuranceTemplateLists.get(0));
			}
			submitInsuranceList.addAll(list);
			sum_premium=data.getDoubleExtra("sum_premium", 0);
			 DecimalFormat df = new DecimalFormat("#####0.00");  
			 tv_total_insurance_price.setText("￥"+df.format(sum_premium));
			 totalPrice=sum_premium;
			 
			 //设置附加险的icon
			 for(int k=0;k<additionalTabList.size();k++){
				 TableRowBean bean=additionalTabList.get(k);
				 for(int b=0;b<additionOutList.size();b++){
					 if(bean.getId().equals(additionOutList.get(b).getInsuranceId())){
						 additionalTabList.get(k).setIconUrl(additionOutList.get(b).getIcon());
					 }
				 }
			 }
			AdditionalTabAdapter additionAdapter=new AdditionalTabAdapter();
			lv_additional_tab.setAdapter(additionAdapter);
		}
	}
	
	
	class AdditionalTabAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return additionalTabList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return additionalTabList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(MakeInsurancePlanActivity.this).inflate(
						R.layout.item_additional_tab, null);
				holder = new ViewHolder();
				holder.iv_tab_main_insurance=(CircleImage) convertView.findViewById(R.id.iv_tab_main_insurance);
				holder.tv_tab_main_insurance = (TextView) convertView.findViewById(R.id.tv_tab_main_insurance);
				holder.tv_paytime = (TextView) convertView.findViewById(R.id.tv_paytime);
				holder.tv_insurance_money = (TextView) convertView.findViewById(R.id.tv_insurance_money);
				holder.tv_product_fee = (TextView) convertView.findViewById(R.id.tv_product_fee);
				holder.viewLine= convertView.findViewById(R.id.line_below_main_tab);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TableRowBean bean=additionalTabList.get(position);
			holder.tv_paytime.setText(bean.getPayTime());
			holder.tv_tab_main_insurance.setText(bean.getInsuranceName());
			holder.tv_insurance_money.setText(bean.getBaoe());
			holder.tv_product_fee.setText(bean.getBaofei());
			if(position==additionalTabList.size()-1){
				holder.viewLine.setVisibility(View.GONE);
			}else{
				holder.viewLine.setVisibility(View.VISIBLE);
			}
			String iconUrl=bean.getIconUrl();

			Glide.with(MakeInsurancePlanActivity.this)
					.load(iconUrl)
					.placeholder(R.drawable.white_bg)
					.into( holder.iv_tab_main_insurance);



			return convertView;
		}
		
		class ViewHolder{
			private CircleImage iv_tab_main_insurance;
			private TextView tv_tab_main_insurance;
			private TextView tv_paytime;
			private TextView tv_insurance_money;
			private TextView tv_product_fee;
			private View viewLine;
		}
		
	}

	//限制姓名只能输入汉字和英文
	public static String stringFilter(String str)throws PatternSyntaxException {
		// 只允许字母、数字和汉字
		String   regEx  =  "[^a-zA-Z\u4E00-\u9FA5]";
		Pattern p   =   Pattern.compile(regEx);
		Matcher m   =   p.matcher(str);
		return   m.replaceAll("").trim();
	}
	
	
}
