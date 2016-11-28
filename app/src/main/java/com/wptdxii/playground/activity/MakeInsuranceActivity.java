package com.wptdxii.playground.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.bean.InsuranceTemplateBean;
import com.cloudhome.bean.MakeInsuranceTemplateBean;
import com.cloudhome.bean.MakeInsuranceTemplateInnerBean;
import com.cloudhome.bean.TableRowBean;
import com.cloudhome.utils.CircleImage;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.ListDialog;
import com.cloudhome.view.customview.ListViewScrollView;
import com.cloudhome.view.customview.NoAutoScrollView;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.WheelMain;
import com.zf.iosdialog.widget.MyAlertDialog;
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
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import okhttp3.Call;

public class MakeInsuranceActivity extends BaseActivity implements View.OnClickListener {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private int ageWeel=-1;
	private RelativeLayout iv_back;
	private TextView tv_text;
	private RelativeLayout rl_right;
	private RelativeLayout rl_btn1;
	private TextView btn1;
	private RelativeLayout rl_btn2;
	private Button btn2;
	private CheckBox cb;
	//投保人信息
	private RelativeLayout rl_second;
	public int holderAge=-1;
	//被保人信息
	private RelativeLayout rl_first;
	public String insurancedSexs="01";
	public String receiverSexs="01";
	//被保人性别
	private CheckBox cb_male;
	private CheckBox cb_female;
	//投保人性别
	private CheckBox cb_male_second;
	private CheckBox cb_female_second;
	//收件人姓名性别
	private EditText et_receiver_name;
	private CheckBox cb_reveiver_male;
	private CheckBox cb_reveiver_female;
	//被保人年龄点击事件
	private TextView tv_age_num;
	//投保人年龄点击事件
	private TextView tv_age_num_second;
	public ArrayList<InsuranceTemplateBean> insuranceTemplateLists;
	private String ids="";
	private String title;
	private Dialog dialog;
	private Map<String, String> key_value = new HashMap<String, String>();
	SharedPreferences sp;
	private String token;
	private String user_id;
	private ArrayList<String> ageRangeList;
	//主险的年龄适用范围
	private int min_age;
	private int max_age;
	//在添加主险的时候，第二次请求的时候保持被保人年龄不变
	private boolean isFirstFetchData=true;
	//外部item数据
	private ArrayList<MakeInsuranceTemplateBean> outlist;
	private ArrayList<MakeInsuranceTemplateInnerBean> innerlist;
	//主险名字
	private TextView tv_main_insurance;
	private ImageView iv_main_insurance;
	public String iconUrl="";
	//判断是否有豁免
	public boolean hasHuomian=false;
	private boolean isHasAdditionInsurance=false;//有没有附加险
	//附加险布局
	private RelativeLayout rl_additional;
	public ArrayList<InsuranceTemplateBean> submitInsuranceList=new ArrayList<InsuranceTemplateBean>();
	//是不是第一次调用getSelectProduct接口
	private boolean isFirstSelectProduct=true;
	//主险对应的附加险
	private ArrayList<MakeInsuranceTemplateBean> additionOutList;
	private ArrayList<MakeInsuranceTemplateInnerBean> additionInnerList;
	String[] insuredAgeArray={};
	private String[] insurerAgeArray={"18","19","20",
			"21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
			"41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60",
			"61","62","63","64","65","66","67","68","69","70","71","72","73","74","75","76","77","78","79","80",
			"81","82","83","84","85","86","87","88","89","90","91","92","93","94","95","96","97","98","99",
			"100","101","102","103","104","105","106"};
	//被保人的年龄
	public String insured_birthday="";
	//投保人的年龄
	public String holder_birthday="";
	//被保人年龄日历
	private ImageView iv_show_calendar;
	//投保人年龄日历
	private ImageView iv_show_calendar_second;
	WheelMain wheelMain;
	private String birthday;
	//记录totalPrice
	public double totalPrice=0;
	//首年总保费
	private TextView tv_total_insurance_price;
	public boolean isAdditionalAdd=false;

	private RelativeLayout rl_main_insurance;
	private RelativeLayout rl_main_insurance_tab;
	private ImageButton tv_main_insurance_modify;
	private ImageButton tv_additional_insurance_modify;
	private RelativeLayout rl_additional_insurance_tab;
	private ImageView iv_main_insurance_title_right;
	//被保人的年龄，性别
	public int insurancedAge=-1;
	public AlertDialog dialog1;
	//要提交的数据
	Map<String,String> submitMap=new HashMap<String,String>();
	ArrayList<String> submitStrsCode;
	ArrayList<String> submitStrsKey;
	public String holderSexs="";
	//传递给附加险计费页面的json串
	private String jsonStr="";
	//首年总保费
	private double sum_premium;
	private TextView tv_insurance_warn;
	private CircleImage iv_tab_main_insurance;//主险表格出现后的图标
	public boolean isMainInsuranceEdit=false;
	//表格
	private TextView tv_tab_main_insurance;
	private TextView tv_paytime;
	private TextView tv_insurance_money;
	private TextView tv_product_fee;
	private TextView tv_1;
	private TextView tv_2;
	private TextView tv_3;
	//添加附加险
	private ImageView iv_additional_insurance_title_right;
	//附加险的列表
	private ArrayList<TableRowBean> additionalTabList=new ArrayList<TableRowBean>();
	private ArrayList<TableRowBean> titleTabList=new ArrayList<TableRowBean>();
	private String jsonAll="";
	public ArrayList<HashMap<String,String>> backSubmitMap;

	private ListViewScrollView lv_additional_tab;
	//生成建议书
	private TextView tv_generate_insurance_template;
	//联网成功后，返回的网页地址,logo,title,描述
	private String resultUrl="";
	private String resultLogo="";
	private String resulTitle="";
	private String resultDescription="";
	private String forward_url="";

	private NoAutoScrollView scroll_top;
	private String user_id_encode="";

	private Handler generateTemplateHandler =new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
				case 0:
					dialog.dismiss();
					Intent intent=new Intent(MakeInsuranceActivity.this,MakeInsuranceTemplateResultActivity.class);
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
					Toast.makeText(MakeInsuranceActivity.this, "生成建议书失败", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "生成建议书失败", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "生成建议书失败", Toast.LENGTH_SHORT).show();
					break;
				case 4:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "联网失败，请检查网络", Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

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



						Glide.with(MakeInsuranceActivity.this)
								.load(iconUrl)
								.placeholder(R.drawable.white_bg)
								.into(iv_main_insurance);


						hasHuomian = outlist.get(0).getHuomian().equals("true");
					}
					if(hasHuomian){
						cb.setChecked(true);
					}else{
						cb.setChecked(false);
					}
					//如果有附加险就显示，没有就不显示
					if(isHasAdditionInsurance){
						rl_additional.setVisibility(View.VISIBLE);
					}else{
						rl_additional.setVisibility(View.GONE);
					}

					submitInsuranceList.clear();
					submitInsuranceList.add(insuranceTemplateLists.get(0));
					//添加主险的时候需要先将被保人的年龄传过去，才能获取到对应的保额
					if(!isFirstSelectProduct){
						showAddMainInsuranceDialog();
					}
					break;

				case 1:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
					if(!isFirstSelectProduct){
						showAddMainInsuranceDialog();
					}else{
						scroll_top.setVisibility(View.GONE);
					}
					break;
				case 2:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
					if(!isFirstSelectProduct){
						showAddMainInsuranceDialog();
					}else{
						scroll_top.setVisibility(View.GONE);
					}
					break;
				case 3:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "获取数据为空", Toast.LENGTH_SHORT).show();
					if(!isFirstSelectProduct){
						showAddMainInsuranceDialog();
					}else{
						scroll_top.setVisibility(View.GONE);
					}
					break;
				case 4:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "联网失败，请检查网络", Toast.LENGTH_SHORT).show();
					if(!isFirstSelectProduct){
						showAddMainInsuranceDialog();
					}else{
						scroll_top.setVisibility(View.GONE);
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



					Glide.with(MakeInsuranceActivity.this)
							.load(iconUrl)
							.placeholder(R.drawable.white_bg)
							.into(iv_tab_main_insurance);



					//首年总保费
					DecimalFormat df = new DecimalFormat("#####0.00");
					tv_total_insurance_price.setText("￥"+df.format(sum_premium));

					totalPrice=sum_premium;

					//访问网络前将数据存储在mainBean中
//					if(isperiodMainShow){
//						mainBean.setPeriodValueCode(submitStrsCode.get(0));
//						mainBean.setPeriodMiddleString(tv_insurance_period_middle.getText().toString());
//					}
//					if(isfrequencyMainShow){
//						mainBean.setFrequencyValueCode(submitStrsCode.get(1));
//						mainBean.setFrequencyMiddleString(tv_pay_frequency_year.getText().toString());
//					}
//					if(ispayTimeMainShow){
//						mainBean.setPayTimeValueCode(submitStrsCode.get(2));
//						mainBean.setPayTimeMiddleString(tv_pay_time_year.getText().toString());
//					}
//					if(isplanMainShow){
//						mainBean.setPlanValueCode(submitStrsCode.get(3));
//						mainBean.setPlanMiddleString(tv_plan_type.getText().toString());
//					}
//				 mainBean.setPeriodValueCode(submitStrsCode.get(0));
//					mainBean.setFrequencyValueCode(submitStrsCode.get(1));
//					mainBean.setPayTimeValueCode(submitStrsCode.get(2));
//					mainBean.setPlanValueCode(submitStrsCode.get(3));
//					mainBean.setPeriodMiddleString(tv_insurance_period_middle.getText().toString());
//					mainBean.setFrequencyMiddleString(tv_pay_frequency_year.getText().toString());
//					mainBean.setPayTimeMiddleString(tv_pay_time_year.getText().toString());
//					mainBean.setPeriodMain(periodMain);
//					mainBean.setFrequencyMain(frequencyMain);
//					mainBean.setPayTimeMain(payTimeMain);
//					mainBean.setPlanMain(planMain);
//					mainBean.setRiskMount(ed_insurance_money.getText().toString().trim());
					dialog1.dismiss();
					//附加险可以添加保险
					if(outlist.get(0).isHasAdditionalInsurance()){
						isAdditionalAdd=true;
					}
					//更改主险的费率因子后附加险需要重新计算
					tv_additional_insurance_modify.setVisibility(View.GONE);
					rl_additional_insurance_tab.setVisibility(View.VISIBLE);
					lv_additional_tab.setVisibility(View.GONE);

					isMainInsuranceEdit=true;//主险保费试算成功，改为可编辑模式
					JSONObject object= (JSONObject) msg.obj;
					try {
						JSONArray obj=object.getJSONArray("row");
						JSONArray obj1=object.getJSONArray("title");
						tv_tab_main_insurance.setText(obj.get(0).toString());
						tv_paytime.setText(obj.get(1).toString());
						tv_insurance_money.setText(obj.get(2).toString());
						tv_product_fee.setText(obj.get(3).toString());
						tv_1.setText(obj1.get(1).toString());
						tv_2.setText(obj1.get(2).toString());
						tv_3.setText(obj1.get(3).toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case 1:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					dialog.dismiss();
					String errmsg=(String) msg.obj;
					tv_insurance_warn.setVisibility(View.VISIBLE);
					tv_insurance_warn.setText(errmsg);
					break;
				case 3:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "获取数据为空", Toast.LENGTH_SHORT).show();
					break;
				case 4:
					dialog.dismiss();
					Toast.makeText(MakeInsuranceActivity.this, "联网失败，请检查网络", Toast.LENGTH_SHORT).show();
					break;

			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_make_insurance);
		insuranceTemplateLists=new ArrayList<InsuranceTemplateBean>();
		Intent intent = getIntent();
		ids=intent.getStringExtra("id");
		title=intent.getStringExtra("title");
		dialog = new Dialog(this,R.style.progress_dialog);
		dialog.setContentView(R.layout.progress_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
		p_dialog.setText("卖力加载中...");

		Log.i("ids-------",ids);

		initView();
	}

	private void initView() {
		sp = this.getSharedPreferences("userInfo", 0);
		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");
		user_id_encode=sp.getString("Login_UID_ENCODE", "");
		scroll_top=(NoAutoScrollView) findViewById(R.id.scroll_top);
		iv_back= (RelativeLayout) findViewById(R.id.iv_back);
		tv_text= (TextView) findViewById(R.id.tv_text);
		tv_text.setText(title + "建议书");
		rl_right= (RelativeLayout) findViewById(R.id.rl_right);
		rl_right.setVisibility(View.INVISIBLE);
		rl_btn1= (RelativeLayout) findViewById(R.id.rl_btn1);
		rl_btn2=(RelativeLayout) findViewById(R.id.rl_btn2);
		btn1= (TextView) findViewById(R.id.btn1);
		btn2=(Button) findViewById(R.id.btn2);
		cb= (CheckBox) findViewById(R.id.cb);
		rl_first=(RelativeLayout) findViewById(R.id.rl_first);
		rl_second=(RelativeLayout) findViewById(R.id.rl_second);
		cb_male=(CheckBox) findViewById(R.id.cb_male);
		cb_female=(CheckBox) findViewById(R.id.cb_female);
		cb_male_second=(CheckBox) findViewById(R.id.cb_male_second);
		cb_female_second=(CheckBox) findViewById(R.id.cb_female_second);
		cb_reveiver_male=(CheckBox) findViewById(R.id.cb_reveiver_male);
		cb_reveiver_female=(CheckBox) findViewById(R.id.cb_reveiver_female);
		tv_age_num=(TextView) findViewById(R.id.tv_age_num);
		tv_age_num_second=(TextView) findViewById(R.id.tv_age_num_second);
		tv_main_insurance=(TextView) findViewById(R.id.tv_main_insurance);
		iv_main_insurance= (ImageView) findViewById(R.id.iv_main_insurance);
		rl_additional= (RelativeLayout) findViewById(R.id.rl_additional);
		iv_show_calendar=(ImageView) findViewById(R.id.iv_show_calendar);
		iv_show_calendar_second=(ImageView) findViewById(R.id.iv_show_calendar_second);
		tv_total_insurance_price=(TextView) findViewById(R.id.tv_total_insurance_price);
		rl_main_insurance=(RelativeLayout) findViewById(R.id.rl_main_insurance);
		rl_main_insurance_tab=(RelativeLayout) findViewById(R.id.rl_main_insurance_tab);
		tv_main_insurance_modify=(ImageButton) findViewById(R.id.tv_main_insurance_modify);
		tv_additional_insurance_modify=(ImageButton) findViewById(R.id.tv_additional_insurance_modify);
		rl_additional_insurance_tab=(RelativeLayout) findViewById(R.id.rl_additional_insurance_tab);
		iv_main_insurance_title_right=(ImageView) findViewById(R.id.iv_main_insurance_title_right);
		iv_tab_main_insurance=(CircleImage) findViewById(R.id.iv_tab_main_insurance);
		tv_tab_main_insurance=(TextView) findViewById(R.id.tv_tab_main_insurance);
		tv_paytime=(TextView) findViewById(R.id.tv_paytime);
		tv_insurance_money=(TextView) findViewById(R.id.tv_insurance_money);
		tv_product_fee=(TextView) findViewById(R.id.tv_product_fee);
		tv_1= (TextView) findViewById(R.id.tv_1);
		tv_2= (TextView) findViewById(R.id.tv_2);
		tv_3= (TextView) findViewById(R.id.tv_3);
		tv_generate_insurance_template=(TextView)findViewById(R.id.tv_generate_insurance_template);

		//附加险表格
		iv_additional_insurance_title_right=(ImageView) findViewById(R.id.iv_additional_insurance_title_right);
		et_receiver_name= (EditText) findViewById(R.id.et_receiver_name);
		lv_additional_tab=(ListViewScrollView) findViewById(R.id.lv_additional_tab);
//		TextWatcher inauranced_name_watcher=new TextWatcher() {
//			@Override
//			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//			}
//			@Override
//			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//				String editable = et_receiver_name.getText().toString();
//				if(editable.length()>9){
//					Toast.makeText(MakeInsuranceActivity.this,"姓名不能超过8个字符",0).show();
//					et_receiver_name.setText(editable);
//				}
//			}
//			@Override
//			public void afterTextChanged(Editable editable) {
//			}
//		};
//		et_receiver_name.addTextChangedListener(inauranced_name_watcher);

		iv_back.setOnClickListener(this);
		rl_btn1.setOnClickListener(this);
		rl_btn2.setOnClickListener(this);
		btn2.setOnClickListener(this);
		cb.setOnClickListener(this);
		cb_male.setOnClickListener(this);
		cb_female.setOnClickListener(this);
		cb_male_second.setOnClickListener(this);
		cb_female_second.setOnClickListener(this);
		tv_age_num.setOnClickListener(this);
		tv_age_num_second.setOnClickListener(this);
		iv_show_calendar.setOnClickListener(this);
		iv_show_calendar_second.setOnClickListener(this);
		iv_main_insurance_title_right.setOnClickListener(this);
		cb_reveiver_male.setOnClickListener(this);
		cb_reveiver_female.setOnClickListener(this);
		tv_main_insurance_modify.setOnClickListener(this);
		iv_additional_insurance_title_right.setOnClickListener(this);
		tv_additional_insurance_modify.setOnClickListener(this);
		tv_generate_insurance_template.setOnClickListener(this);

		String url= IpConfig.getUri2("getSelectProduct");
//		String url="http://10.10.10.72:8080/gateway/proposal/selectProduct?";

		url=url+"user_id="+user_id_encode+"&product_ids="+ids;
//		ids="30003";
//		url=url+"user_id="+user_id+"&product_ids=30003";

		Log.i("传过去的数据地址--------------", url);
		Log.i("传过去的数据token--------------", token);
		key_value.put("token", token);
		fetchData(url);



	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.iv_back:
				finish();
				break;
			case R.id.rl_btn1:
				rl_btn1.setBackgroundResource(R.color.white);
				btn1.setTextColor(R.color.color3);
				btn2.setTextColor(R.color.color9);
				rl_btn2.setBackgroundResource(R.color.check1);
				rl_first.setVisibility(View.VISIBLE);
				rl_second.setVisibility(View.GONE);
				break;
			case R.id.btn2:
				btn2.setTextColor(R.color.color3);
				btn1.setTextColor(R.color.color9);
				rl_btn1.setBackgroundResource(R.color.check1);
				rl_btn2.setBackgroundResource(R.color.white);
				rl_first.setVisibility(View.GONE);
				rl_second.setVisibility(View.VISIBLE);
				cb.setChecked(true);
				break;
			case R.id.cb:
				if(hasHuomian){
					cb.setChecked(true);
				}else {
					if(cb.isChecked()){
						rl_btn1.setBackgroundResource(R.color.check1);
						rl_btn2.setBackgroundResource(R.color.white);
						rl_first.setVisibility(View.GONE);
						rl_second.setVisibility(View.VISIBLE);
					}
				}

				break;
			case R.id.cb_male:
				if(cb_male.isChecked()){
					cb_female.setChecked(false);
				}else{
					cb_male.setChecked(true);
				}
				if(insurancedSexs.equals("02")){
					clearPrem();
				}
				insurancedSexs="01";
				break;
			case R.id.cb_female:
				if(cb_female.isChecked()){
					cb_male.setChecked(false);
				}else{
					cb_female.setChecked(true);
				}
				if(insurancedSexs.equals("01")){
					clearPrem();
				}
				insurancedSexs="02";
				break;
			case R.id.cb_male_second:
				if(cb_male_second.isChecked()){
					cb_female_second.setChecked(false);
					//当操作投保人的性别年龄的时候，自动勾选设置投保人
					cb.setChecked(true);
				}else{
					cb_male_second.setChecked(true);
				}
				holderSexs="01";
				break;
			case R.id.cb_female_second:
				if(cb_female_second.isChecked()){
					cb_male_second.setChecked(false);
					//当操作投保人的性别年龄的时候，自动勾选设置投保人
					cb.setChecked(true);
				}else{
					cb_female_second.setChecked(true);
				}
				holderSexs="02";
				break;
			case R.id.cb_reveiver_male:
				if(cb_reveiver_male.isChecked()){
					cb_reveiver_female.setChecked(false);
				}else{
					cb_reveiver_male.setChecked(true);
				}
				receiverSexs="01";
				break;
			case R.id.cb_reveiver_female:
				if(cb_reveiver_female.isChecked()){
					cb_reveiver_male.setChecked(false);
				}else{
					cb_reveiver_female.setChecked(true);
				}
				receiverSexs="02";
				break;
			case R.id.tv_age_num:
				final String oldAge=tv_age_num.getText().toString();
				if(ageRangeList.size()>0){
					insuredAgeArray= ageRangeList.toArray(new String[ageRangeList.size()]);
				}
				ListDialog dialog=new ListDialog(MakeInsuranceActivity.this,insuredAgeArray,"请选择年龄") {
					@Override
					public void item(int m) {
						int age= Integer.parseInt(insuredAgeArray[m]);
						if(age>=min_age&&age<=max_age){
							//清除被保人的生日
							insured_birthday="";
							tv_age_num.setText(insuredAgeArray[m]);
							String newAge=tv_age_num.getText().toString();
							if(!oldAge.equals(newAge)){
								clearPrem();
//								tv_total_insurance_money.setText("");
							}
						}else{
							Toast.makeText(MakeInsuranceActivity.this,"年龄范围应在"+min_age+"至"+max_age+"之间", Toast.LENGTH_SHORT).show();
						}

					}
				};
				break;
			case R.id.tv_age_num_second:
				ListDialog dialogHolder=new ListDialog(MakeInsuranceActivity.this,insurerAgeArray,"请选择年龄") {
					@Override
					public void item(int m) {
						tv_age_num_second.setText(insurerAgeArray[m]);
						//当操作投保人的性别年龄的时候，自动勾选设置投保人
						cb.setChecked(true);
					}
				};
				break;
			case R.id.iv_show_calendar:
				showCalendar(1);
				break;
			case R.id.iv_show_calendar_second:
				showCalendar(2);
				break;
			case R.id.iv_main_insurance_title_right:
				//发送年龄信息，根据年龄获取保额的范围
				String url=IpConfig.getUri2("getSelectProduct");
//				String url="http://10.10.10.72:8080/gateway/proposal/selectProduct?";
				String age=getInsurancedAge()+"";
				url=url+"age="+age+"&user_id="+user_id_encode+"&product_ids="+ids;
//				url=url+"age="+age+"&user_id="+user_id+"&product_ids=30003";

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
					Intent intent=new Intent(MakeInsuranceActivity.this,AdditionalInsuranceActivity.class);
					intent.putExtra("additionOutList", additionOutList);
					intent.putExtra("isModify", false);
					intent.putExtra("insuranceTemplateLists",insuranceTemplateLists);
					//添加主险的信息
					intent.putExtra("jsonStr", jsonStr);
					intent.putExtra("mainIds", ids);
					intent.putExtra("mainInsuranceName", outlist.get(0).getInsuranceName());
					startActivityForResult(intent, 0);
				}
				break;
			case R.id.tv_additional_insurance_modify:

					Intent intent=new Intent(MakeInsuranceActivity.this,AdditionalInsuranceActivity.class);
					intent.putExtra("additionOutList", additionOutList);
					intent.putExtra("isModify", true);
					intent.putExtra("submitMap", backSubmitMap);
					//添加主险的信息
					intent.putExtra("jsonStr", jsonStr);
					intent.putExtra("mainIds", ids);
					intent.putExtra("mainInsuranceName", outlist.get(0).getInsuranceName());
					startActivityForResult(intent, 0);

				break;
			case R.id.tv_generate_insurance_template:
				generateTemplate();
				break;


		}
	}

	//刚进页面时，获取数据
	private void fetchData(String url) {
		dialog.show();
		OkHttpUtils.post()//
				.url(url)//
				.params(key_value)//
				.build()//
				.execute(new StringCallback() {

					@Override
					public void onError(Call call, Exception e) {

						Message msg=new Message();
						msg.what=4;
						resultHandler.sendMessage(msg);

					}

					@Override
					public void onResponse(String response) {

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
								min_age= Integer.parseInt(ageObj.getString("min_age"));
								max_age= Integer.parseInt(ageObj.getString("max_age"));

								JSONArray dataList = dataObj.getJSONArray("products");
								outlist=new ArrayList<MakeInsuranceTemplateBean>();
								//先清空数据，避免重复添加
								insuranceTemplateLists.clear();
								for(int i=0;i<dataList.length();i++){
									MakeInsuranceTemplateBean bean=new MakeInsuranceTemplateBean();
									JSONObject obj=(JSONObject) dataList.get(i);

									JSONObject insuranceObj=obj.getJSONObject("prodinfo");
									JSONArray arrayInput=obj.getJSONArray("inputfields");
									//这个集合存储用于提交制作建议书
									InsuranceTemplateBean templateBean=new InsuranceTemplateBean();
									templateBean.setProduct_id(insuranceObj.getString("id"));
									templateBean.setProduct_name(insuranceObj.getString("name"));
									templateBean.setCompany_id(insuranceObj.getString("company_code"));
									templateBean.setHuomian(insuranceObj.getString("ishuomian"));
									templateBean.setMust(insuranceObj.getString("is_main"));
									templateBean.setIs_main(insuranceObj.getString("is_main"));
									templateBean.setMaster_id(insuranceObj.getString("parent"));
									templateBean.setDisplay_order(0);
									templateBean.setId("");
									templateBean.setSuggest_id("");
									templateBean.setAdd_time("");
									insuranceTemplateLists.add(templateBean);
									Log.i("insuranceTemplateLists添加一个templateBean", "templateBean--------");


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
											JSONArray additionalArrayInput=additionalObj.getJSONArray("inputfields");

											//这个集合存储用于提交制作建议书
											InsuranceTemplateBean templateBean1=new InsuranceTemplateBean();
											templateBean1.setProduct_id(additionalInsuranceObj.getString("id"));
											templateBean1.setProduct_name(additionalInsuranceObj.getString("name"));
											templateBean1.setCompany_id(additionalInsuranceObj.getString("company_code"));
											templateBean1.setHuomian(additionalInsuranceObj.getString("ishuomian"));
											templateBean1.setMust(additionalInsuranceObj.getString("is_main"));
											templateBean1.setIs_main(additionalInsuranceObj.getString("is_main"));
											templateBean1.setMaster_id(additionalInsuranceObj.getString("parent"));
											templateBean1.setDisplay_order(0);
											templateBean1.setId("");
											templateBean1.setSuggest_id("");
											templateBean1.setAdd_time("");
											insuranceTemplateLists.add(n+1,templateBean1);
											Log.i("insuranceTemplateLists添加一个templateBean", "templateBean--------");

											additionalBean.setInsuranceId(additionalInsuranceObj.getString("id"));
											additionalBean.setHuomian(additionalInsuranceObj.getString("ishuomian"));
											additionalBean.setIcon(additionalInsuranceObj.getString("icon"));
											additionalBean.setInsuranceName(additionalInsuranceObj.getString("name"));
											additionalBean.setInsuranceType(additionalInsuranceObj.getString("type"));
											additionalBean.setInsuranceType_name(additionalInsuranceObj.getString("type_name"));

											additionInnerList=new ArrayList<MakeInsuranceTemplateInnerBean>();
											for(int k=0;k<additionalArrayInput.length();k++){
												MakeInsuranceTemplateInnerBean innerBean=new MakeInsuranceTemplateInnerBean();
												JSONObject innerObj=(JSONObject) additionalArrayInput.get(k);
												innerBean.setInput_type(innerObj.getString("input_type"));
												innerBean.setTitle(innerObj.getString("title"));
												innerBean.setTitle_key(innerObj.getString("title_key"));
												String input_type=innerObj.getString("input_type");
												if("01".equals(input_type)){
													JSONArray innerEnd=innerObj.getJSONArray("valueoptions");
													ArrayList<String> valueCode=new ArrayList<String>();
													ArrayList<String> valueString=new ArrayList<String>();
													for(int p=0;p<innerEnd.length();p++){
														JSONArray array=(JSONArray) innerEnd.get(p);
														valueCode.add(p, array.get(0).toString());
														Log.i("valueCode-------------------"+p, array.get(0).toString());
														valueString.add(p, array.get(1).toString());
														Log.i("valueString-------------------"+p, array.get(1).toString());
													}
													innerBean.setValueCode(valueCode);
													innerBean.setValueString(valueString);
												}else if("02".equals(input_type)){
													innerBean.setHint(innerObj.getString("hint"));
													innerBean.setEdit_able(innerObj.getString("edit_able"));
												}
												additionInnerList.add(innerBean);
											}
											additionalBean.setInnerBeanList(additionInnerList);
											//最外层的集合添加数据
											additionOutList.add(additionalBean);
										}
									}else{
										bean.setHasAdditionalInsurance(false);
									}

									//解析inner
									innerlist=new ArrayList<MakeInsuranceTemplateInnerBean>();
									for(int m=0;m<arrayInput.length();m++){
										MakeInsuranceTemplateInnerBean innerBean=new MakeInsuranceTemplateInnerBean();
										JSONObject innerObj=(JSONObject) arrayInput.get(m);
										innerBean.setInput_type(innerObj.getString("input_type"));
										innerBean.setTitle(innerObj.getString("title"));
										innerBean.setTitle_key(innerObj.getString("title_key"));
										//新加，通过input_type来判断是输入框还是列表
										if(innerObj.getString("input_type").equals("01")){
											JSONArray innerEnd=innerObj.getJSONArray("valueoptions");
											ArrayList<String> valueCode=new ArrayList<String>();
											ArrayList<String> valueString=new ArrayList<String>();
											for(int n=0;n<innerEnd.length();n++){
												JSONArray array=(JSONArray) innerEnd.get(n);
												valueCode.add(n, array.get(0).toString());
												Log.i("valueCode-------------------"+n, array.get(0).toString());
												valueString.add(n, array.get(1).toString());
												Log.i("valueString-------------------"+n, array.get(1).toString());
											}
											innerBean.setValueCode(valueCode);
											innerBean.setValueString(valueString);
										}else if(innerObj.getString("input_type").equals("02")){
											innerBean.setEdit_able(innerObj.getString("edit_able"));
											innerBean.setHint(innerObj.getString("hint"));
										}
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

	//选择被保人的年龄，弹出选择框
	private void showCalendar(final int whichAge) {
		// TODO Auto-generated method stub

		LayoutInflater inflater1 = LayoutInflater.from(MakeInsuranceActivity.this);
		final View timepickerview1 = inflater1.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo1 = new ScreenInfo(MakeInsuranceActivity.this);
		wheelMain = new WheelMain(timepickerview1);
		wheelMain.screenheight = screenInfo1.getHeight();
		Calendar calendar1 = Calendar.getInstance();
		try {
			calendar1.setTime(dateFormat.parse(dateFormat.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int year1 = calendar1.get(Calendar.YEAR);
		int month1 = calendar1.get(Calendar.MONTH);
		int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year1, month1, day1);
		final MyAlertDialog dialog = new MyAlertDialog(
				MakeInsuranceActivity.this).builder()
				.setTitle("请选择")
						// .setMsg("22")
						// .setEditText("111")
				.setView(timepickerview1)
				.setNegativeButton("取消", new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
		dialog.setPositiveButton("确定", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				birthday = wheelMain.getTime();
				Log.i("选择的年龄------------------", birthday);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
//								tv_total_insurance_money.setText("");
							}
							Toast.makeText(getApplicationContext(),
									wheelMain.getTime(), Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(MakeInsuranceActivity.this,"年龄范围应在"+min_age+"至"+max_age+"之间", Toast.LENGTH_SHORT).show();
						}

					}else{
						if(ageWeel<18){
							Toast.makeText(MakeInsuranceActivity.this,"投保人年龄应该大于或等于18岁", Toast.LENGTH_SHORT).show();
						}else{
							holder_birthday=birthday;
							tv_age_num_second.setText(ageWeel+"");
							//当操作投保人的性别年龄的时候，自动勾选设置投保人
							cb.setChecked(true);
							Toast.makeText(getApplicationContext(),wheelMain.getTime(), Toast.LENGTH_SHORT).show();
						}

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

	//更改被保人的性别与年龄的时候将每个条目的保费清空
	public void clearPrem(){
		//清空主险和附加险
		rl_main_insurance.setVisibility(View.VISIBLE);
		rl_main_insurance_tab.setVisibility(View.GONE);
		iv_main_insurance_title_right.setVisibility(View.VISIBLE);
		tv_main_insurance_modify.setVisibility(View.GONE);
		isMainInsuranceEdit=false;
		submitMap=new HashMap<String,String>();

//		iv_additional_insurance_title_right.setVisibility(View.VISIBLE);
		tv_additional_insurance_modify.setVisibility(View.GONE);
		rl_additional_insurance_tab.setVisibility(View.VISIBLE);
		lv_additional_tab.setVisibility(View.GONE);
		isAdditionalAdd=false;
		tv_total_insurance_price.setText("");
		totalPrice=0;
	}

	/**得到被保人的年龄
	 * @return
	 */
	public int getInsurancedAge(){
		String ageStr=tv_age_num.getText().toString();
		if("".equals(ageStr) || "null".equals(ageStr)){
			insurancedAge=-1;
		}else{
			insurancedAge= Integer.parseInt(ageStr);
		}
		return insurancedAge;
	}
	/**得到投保人的年龄
	 * @return
	 */
	public int getHolderAge(){
		String ageStr=tv_age_num_second.getText().toString();
		if("".equals(ageStr) || "null".equals(ageStr)){
			holderAge=18;
		}else{
			holderAge= Integer.parseInt(ageStr);
		}
		return holderAge;
	}



	private void showAddMainInsuranceDialog() {
		AlertDialog.Builder build = new AlertDialog.Builder(MakeInsuranceActivity.this);
		View contentView = View.inflate(MakeInsuranceActivity.this, R.layout.add_main_insurance, null);
		TextView tv_insurance_title=(TextView) contentView.findViewById(R.id.tv_insurance_title);
		tv_insurance_warn=(TextView) contentView.findViewById(R.id.tv_insurance_warn);
		LinearLayout ll_content= (LinearLayout) contentView.findViewById(R.id.ll_content);
		TextView tv_cancel=(TextView) contentView.findViewById(R.id.tv_cancel);
		TextView tv_ok=(TextView) contentView.findViewById(R.id.tv_ok);
		tv_insurance_title.setText(outlist.get(0).getInsuranceName());

		final ArrayList<MakeInsuranceTemplateInnerBean> list=outlist.get(0).getInnerBeanList();
		for(int i=0;i<list.size();i++){
			View view=null;
			final MakeInsuranceTemplateInnerBean bean=list.get(i);
			final String title_key=bean.getTitle_key();
			if("01".equals(bean.getInput_type())){
				view= LayoutInflater.from(MakeInsuranceActivity.this).inflate(R.layout.add_insurance_item1,null);
				TextView tv_left= (TextView) view.findViewById(R.id.tv_left);
				ImageView iv_insurance= (ImageView) view.findViewById(R.id.iv_insurance);
				final TextView tv_middle= (TextView) view.findViewById(R.id.tv_middle);
				//当只有一个选项的时候，隐藏下拉箭头
				final ArrayList<String> valueString=bean.getValueString();
				if(valueString.size()>1){
					iv_insurance.setVisibility(View.VISIBLE);
				}else{
					iv_insurance.setVisibility(View.INVISIBLE);
				}
				tv_left.setText(bean.getTitle());

				if(isMainInsuranceEdit){
					String sbCode=submitMap.get(title_key);
					ArrayList<String> sbCodeList=bean.getValueCode();
					int pos=sbCodeList.indexOf(sbCode);
					tv_middle.setText(bean.getValueString().get(pos));
				}else{
					tv_middle.setText(bean.getValueString().get(0));
					String defaultCode=bean.getValueCode().get(0);
					submitMap.put(title_key, defaultCode);
				}



//				Log.i("submitMap", title_key + "----" + defaultCode);
				final int o = i;
				tv_middle.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (valueString.size() <= 1) {
							return;
						}
						ArrayList<String> stringList = list.get(o).getValueString();
						final ArrayList<String> codeList = list.get(o).getValueCode();
						final String[] insurancePeriodArray = stringList.toArray(new String[stringList.size()]);
						ListDialog dialog0 = new ListDialog(MakeInsuranceActivity.this, insurancePeriodArray, "请选择" + bean.getTitle()) {
							@Override
							public void item(int m) {
								tv_middle.setText(insurancePeriodArray[m]);
								submitMap.put(title_key, codeList.get(m));
								Log.i("submitMap", title_key + "----" + codeList.get(m));
							}
						};
					}
				});

			}else if("02".equals(bean.getInput_type())){
				view= LayoutInflater.from(MakeInsuranceActivity.this).inflate(R.layout.add_insurance_item2,null);
				TextView tv_insurance_left= (TextView) view.findViewById(R.id.tv_insurance_left);
				final EditText ed_insurance_money= (EditText) view.findViewById(R.id.ed_insurance_money);
				tv_insurance_left.setText(bean.getTitle());
				if(isMainInsuranceEdit){
					String sbCode=submitMap.get(title_key);
					ed_insurance_money.setText(sbCode);
				} else {
					ed_insurance_money.setHint(bean.getHint());
					submitMap.put(title_key,"");
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
						submitMap.put(title_key,etValue);
						Log.i("submitMap", title_key + "----" + etValue);
					}
				};
				ed_insurance_money.addTextChangedListener(watcher);
			}
			LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Common.dip2px(MakeInsuranceActivity.this,44));
			view.setLayoutParams(params1);
			ll_content.addView(view,i);
		}


		tv_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog1.dismiss();
			}
		});

		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				submitStrsCode=new ArrayList<String>();
				submitStrsKey=new ArrayList<String>();
				Iterator iter = submitMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String,String> entry = (Map.Entry) iter.next();
					String key = entry.getKey();
					String val = entry.getValue();
					if("".equals(val)){
						return;
					}else {
						submitStrsKey.add(key);
						submitStrsCode.add(val);
					}
				}
				String product_id=outlist.get(0).getInsuranceId();
				String sexs=insurancedSexs;
				int pay_age=getInsurancedAge();
				fetchData(product_id,sexs,pay_age,submitStrsKey,submitStrsCode,0);
			}
		});

		dialog1 = build.create();
		dialog1.setView(contentView, 0, 0, 0, 0);
		dialog1.show();

		dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}


	public void fetchData(String product_id, String sexs, int pay_age,
						  ArrayList listName, ArrayList listCode, final int indexPosition) {
		// TODO Auto-generated method stub
		//产品id,保额，被保人性别，被保人年龄，
		try {
			//新的解析方式
			//被保人信息
			JSONObject insuranceObj=new JSONObject();
			insuranceObj.put("name", "");
			insuranceObj.put("sexs", sexs);
			insuranceObj.put("birthday", insured_birthday);
			insuranceObj.put("age",pay_age+"");
			//投保人信息
			JSONObject holderObj=new JSONObject();
			holderObj.put("name", "");
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

//			String url="http://10.10.10.72:8080/gateway/proposal/calInsuranceCharge?";
			String url=IpConfig.getUri2("calInsuranceCharge");
			Log.i("url----------", url);
			Log.i("提交----的json串----------", str);
			Log.i("提交----的json串----------", user_id);
			Log.i("提交----的json串----------", token);
			key_value.put("user_id",user_id);
			key_value.put("token",token);
			key_value.put("prod_info",str);
			key_value.put("time","");

			OkHttpUtils.post()//
					.url(url)//
					.params(key_value)//
					.build()//
					.execute(new StringCallback() {

						@Override
						public void onError(Call call, Exception e) {

							Log.i("联网失败了------------", "获取主险费率");
							MainInsuranceHandler.sendEmptyMessage(4);

						}

						@Override
						public void onResponse(String response) {

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
//									msg.obj=rowArray;
									msg.obj=tableObject;
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
			tv_additional_insurance_modify.setVisibility(View.VISIBLE);
			rl_additional_insurance_tab.setVisibility(View.GONE);
			lv_additional_tab.setVisibility(View.VISIBLE);
			additionalTabList=(ArrayList<TableRowBean>) data.getSerializableExtra("additionalTabList");
			titleTabList=(ArrayList<TableRowBean>) data.getSerializableExtra("titleTabList");
			jsonAll=data.getStringExtra("jsonAll");
			backSubmitMap= (ArrayList<HashMap<String, String>>) data.getSerializableExtra("submitMap");
			ArrayList<InsuranceTemplateBean> list=(ArrayList<InsuranceTemplateBean>) data.getSerializableExtra("backInsuranceTemplateLists");
			if(submitInsuranceList.size()>1){
				submitInsuranceList.clear();
				submitInsuranceList.add(insuranceTemplateLists.get(0));
			}
			submitInsuranceList.addAll(list);
			//接收返回的附加险信息
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


	class AdditionalTabAdapter extends BaseAdapter {

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
				convertView = LayoutInflater.from(MakeInsuranceActivity.this).inflate(
						R.layout.item_additional_tab, null);
				holder = new ViewHolder();
				holder.iv_tab_main_insurance=(CircleImage) convertView.findViewById(R.id.iv_tab_main_insurance);
				holder.tv_tab_main_insurance = (TextView) convertView.findViewById(R.id.tv_tab_main_insurance);
				holder.tv_paytime = (TextView) convertView.findViewById(R.id.tv_paytime);
				holder.tv_insurance_money = (TextView) convertView.findViewById(R.id.tv_insurance_money);
				holder.tv_product_fee = (TextView) convertView.findViewById(R.id.tv_product_fee);
				holder.viewLine= convertView.findViewById(R.id.line_below_main_tab);
				holder.tv_ad1 = (TextView) convertView.findViewById(R.id.tv_ad1);
				holder.tv_ad2 = (TextView) convertView.findViewById(R.id.tv_ad2);
				holder.tv_ad3 = (TextView) convertView.findViewById(R.id.tv_ad3);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Log.i("additionalTabList",""+additionalTabList.size()+"---"+titleTabList.size());

			TableRowBean bean=additionalTabList.get(position);
			holder.tv_paytime.setText(bean.getPayTime());
			holder.tv_tab_main_insurance.setText(bean.getInsuranceName());
			holder.tv_insurance_money.setText(bean.getBaoe());
			holder.tv_product_fee.setText(bean.getBaofei());
			TableRowBean beanTitle=titleTabList.get(position);
			holder.tv_ad1.setText(beanTitle.getTitle1());
			holder.tv_ad2.setText(beanTitle.getTitle2());
			holder.tv_ad3.setText(beanTitle.getTitle3());

			if(position==additionalTabList.size()-1){
				holder.viewLine.setVisibility(View.GONE);
			}else{
				holder.viewLine.setVisibility(View.VISIBLE);
			}
			String iconUrl=bean.getIconUrl();


			Glide.with(MakeInsuranceActivity.this)
					.load(iconUrl)
					.placeholder(R.drawable.white_bg)
					.into(holder.iv_tab_main_insurance);



			return convertView;
		}

		class ViewHolder{
			private TextView tv_ad1;
			private TextView tv_ad2;
			private TextView tv_ad3;
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
		String regEx  =  "[^a-zA-Z\u4E00-\u9FA5]";
		Pattern p   =   Pattern.compile(regEx);
		Matcher m   =   p.matcher(str);
		return   m.replaceAll("").trim();
	}


	/**
	 * 生成建议书
	 */
	public void generateTemplate(){
		try
		{
			if(TextUtils.isEmpty(tv_total_insurance_price.getText())||submitInsuranceList.size()<1){
				Toast.makeText(MakeInsuranceActivity.this, "您还没计算任何保险", Toast.LENGTH_SHORT).show();
				return;
			}
			String nameStr=et_receiver_name.getText().toString().trim();
//			if(TextUtils.isEmpty(nameStr)){
//				Toast.makeText(MakeInsuranceActivity.this, "请输入收件人姓名", Toast.LENGTH_SHORT).show();
//				return;
//			}
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
				obj1.put("is_main", bean.getIs_main());
				obj1.put("ishuomian", bean.getIshuomian());
				obj1.put("display_order", bean.getDisplay_order());
				obj1.put("add_time", bean.getAdd_time());

				array.put(c, obj1);
			}

			JSONObject obj2=new JSONObject();
			obj2.put("id","");
			obj2.put("user_id",user_id);
			obj2.put("token",token);
			obj2.put("holder_name","");
			if(cb.isChecked()){
				obj2.put("holder_sex",holderSexs);
				obj2.put("holder_age",getHolderAge());
			}
			obj2.put("insured_birthday",insured_birthday);
			obj2.put("holder_phone","");
			obj2.put("insured_name","");
			obj2.put("insured_sex",insurancedSexs);
			obj2.put("insured_age",getInsurancedAge());
			obj2.put("insured_phone","");
			obj2.put("total_prem",totalPrice);
			obj2.put("recipient_name",nameStr);
			obj2.put("recipient_sex",receiverSexs);
			JSONObject obj=new JSONObject();
			obj.put("suggest_products", array);
			obj.put("suggest", obj2);
			String str=obj.toString();
			//提交数据
//			String url=IpConfig.getUri4("saveSuggestInfo");
			String url=IpConfig.getUri2("saveSuggestInfo");
//			String url="http://192.168.1.24:8080/M1/proposal/saveSuggestInfo.do?";
			Log.i("提交----的json串----------", str);


//			Log.i("制作建议书----------", url+"user_id="+user_id+"&token="+token);
			key_value.put("user_id",user_id);
			key_value.put("token",  token);
			key_value.put("suggest_info",  str);
			dialog.show();
			Log.i("========start====","");
			Log.i("------------token:",token);
			Log.i("------------url:",url);
			Log.i("------------user_id:",user_id);
			Log.i("------------suggest_info:",str);
			Log.i("========end====","");

			OkHttpUtils.post()//
					.url(url)//
					.params(key_value)//
					.build()//
					.execute(new StringCallback() {
						@Override
						public void onError(Call call, Exception e) {
							Message msg=new Message();
							msg.what=4;
							generateTemplateHandler.sendMessage(msg);
						}
						@Override
						public void onResponse(String response) {
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
}
