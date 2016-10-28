package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IdCheckUtil;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.CharacterParser;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.gghl.view.wheelview.JudgeDate;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.WheelMain;
import com.zf.iosdialog.widget.MyAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FillInApplicantActivity extends BaseActivity {

	private TextView brith;
	private RelativeLayout brith_rel;
	private ClearEditText cert_num;
	private String cert_type = "";
	private CharacterParser characterParser;

	@SuppressLint("SimpleDateFormat")
	private
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private TextView document;
	private String[] document_code = { "01", "03", "06", "07", "00" };
	private String document_code_str = "";
	private String[] document_type = { "身份证", "护照", "港澳通行证", "台胞证", "其它" };
	private RelativeLayout document_type_rel;
	private ClearEditText email;
	private ClearEditText holder_e_name;
	private ClearEditText holder_name;
	private RelativeLayout holder_rel;
	private RelativeLayout layout;
	private ListView listViewSpinner;
	private ClearEditText phone_num;
	private PopupWindow popupWindow;
	private View popview;
	private String[] sex = { "男", "女" };
	private String[] sex_code = { "01", "02" };
	private String sex_code_str = "";
	private RelativeLayout sex_rel;
	private TextView sex_txt;

	private String token;
	private TextView save;
	private String txttime;
	private String user_id;
	private WheelMain wheelMain;

	private ImageView applicant_back;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




		setContentView(R.layout.fillinapplicant);


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		init();
		initEvent();

	}

	private void init() {
		applicant_back = (ImageView) findViewById(R.id.applicant_back);
		popview = findViewById(R.id.popview);
		document_type_rel = (RelativeLayout) findViewById(R.id.document_type_rel);
		sex_rel = (RelativeLayout) findViewById(R.id.sex_rel);
		holder_name = (ClearEditText) findViewById(R.id.holder_name);
		holder_name.addTextChangedListener(mTextWatcher);
		cert_num = (ClearEditText) findViewById(R.id.cert_num);
		phone_num = (ClearEditText) findViewById(R.id.phone_num);
		cert_num.addTextChangedListener(cert_watcher);
		brith_rel = (RelativeLayout) findViewById(R.id.brith_rel);
		holder_e_name = (ClearEditText) findViewById(R.id.holder_e_name);
		email = (ClearEditText) findViewById(R.id.email);
		document = (TextView) findViewById(R.id.document);
		sex_txt = (TextView) findViewById(R.id.sex_txt);
		brith = (TextView) findViewById(R.id.brith);
		characterParser = CharacterParser.getInstance();
		save = (TextView) findViewById(R.id.save);
		
		Intent intent=getIntent();
		String toubaorenInfo=intent.getStringExtra("toubaorenInfo");
		try {
			JSONObject obj=new JSONObject(toubaorenInfo);
			holder_name.setText(obj.getString("name"));
			holder_e_name.setText(obj.getString("pinyin"));
			cert_num.setText(obj.getString("id_no"));
			brith.setText(obj.getString("birthday"));
			phone_num.setText(obj.getString("tel"));
			email.setText(obj.getString("email"));
			String id_type=obj.getString("id_type");
			Log.i("ID_TYPE---------toubaorenInfo---------------", toubaorenInfo);
			Log.i("ID_TYPE------------------------", id_type);
			document_code_str=id_type;
			if(id_type.equals("01")||id_type.equals("身份证")){
				cert_type = document_type[0];
				document.setText(document_type[0]);
			}else if(id_type.equals("03")||id_type.equals("护照")){
				cert_type = document_type[1];
				document.setText(document_type[1]);
			}else if(id_type.equals("06")||id_type.equals("港澳通行证")){
				cert_type = document_type[2];
				document.setText(document_type[2]);
			}else if(id_type.equals("07")||id_type.equals("台胞证")){
				cert_type = document_type[3];
				document.setText(document_type[3]);
			}else{
				cert_type = document_type[4];
				document.setText(document_type[4]);
			}
			String sex=obj.getString("sex");
			if (sex.equals("02")) {
				sex_txt.setText("女");
				sex_code_str = "02";

			} else {
				sex_txt.setText("男");
				sex_code_str = "01";
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initEvent() {

		applicant_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = getIntent();

				setResult(0, intent);
				finish();

			}
		});

		document_type_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showPopupWindow();

			}
		});

		sex_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				SexPopupWindow();

			}
		});

		brith_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				LayoutInflater inflater1 = LayoutInflater
						.from(FillInApplicantActivity.this);
				final View timepickerview1 = inflater1.inflate(
						R.layout.timepicker, null);
				ScreenInfo screenInfo1 = new ScreenInfo(
						FillInApplicantActivity.this);
				wheelMain = new WheelMain(timepickerview1);
				wheelMain.screenheight = screenInfo1.getHeight();
				String time1 = txttime;
				Calendar calendar1 = Calendar.getInstance();
				if (JudgeDate.isDate(time1, "yyyy-MM-dd")) {
					try {
						calendar1.setTime(dateFormat.parse(time1));
					} catch (ParseException e) {

						e.printStackTrace();
					}
				}
				int year1 = calendar1.get(Calendar.YEAR);
				int month1 = calendar1.get(Calendar.MONTH);
				int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
				wheelMain.initDateTimePicker(year1, month1, day1);
				final MyAlertDialog dialog = new MyAlertDialog(
						FillInApplicantActivity.this).builder()
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
						String brithstr = wheelMain.getTime();

						Toast.makeText(getApplicationContext(),
								wheelMain.getTime(), Toast.LENGTH_SHORT).show();

					String brith_txt_str = brith.getText().toString();
						 if(brith_txt_str.equals("请选择"))
						 {
								brith.setText(brithstr);
						 }else if(!cert_type.equals("身份证")){
							 brith.setText(brithstr);
						 }else if (!Boolean.valueOf(
								IdCheckUtil.IDCardValidate(cert_num.getText()
										.toString())).booleanValue()) {
							brith.setText(brithstr);

						}

					}
				});
				dialog.show();

			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String holder_name_str = holder_name.getText().toString();
				String holder_e_name_str = holder_e_name.getText().toString();
				String cert_num_str = cert_num.getText().toString();
				String brith_str = brith.getText().toString();
				String phone_num_str = phone_num.getText().toString();
				String email_str = email.getText().toString();
				int	e_Length= email.length();
				if (holder_name_str.equals("null")
						|| holder_name_str.equals("")) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请输入投保人姓名");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();
				} else if (holder_e_name_str.equals("null")
						|| holder_e_name_str.equals("")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请输入投保人英文名");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if (document_code_str.equals("")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请选择证件类型");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if (document_code_str.equals("01")&&	!IdCheckUtil.IDCardValidate(cert_num_str)) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请输入正确的身份证号");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				}
				else if (cert_num_str.equals("null")
						|| cert_num_str.equals("")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请输入有效证件号码");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if (brith_str.equals("请选择")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请选择出生日期");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if (sex_code_str.equals("")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请选择性别");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if (phone_num_str.equals("null")
						|| phone_num_str.equals("")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("手机号不能为空");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				}else if (!isMobileNum(phone_num_str)) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请输入正确的手机号");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if (email_str.equals("null") || email_str.equals("")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("邮箱不能为空");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if(!isEmail(email_str)){
					
					CustomDialog.Builder builder = new CustomDialog.Builder(
							FillInApplicantActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请输入正确的邮箱地址");
					
					
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									
									dialog.dismiss();
									}
							});
					builder.create().show();
				}else {

					String holderstr = Json_Value(holder_name_str,
							holder_e_name_str, document_code_str, cert_num_str,
							brith_str, sex_code_str, phone_num_str, email_str);
					
					Log.d("888",holderstr);
					Intent intent = getIntent();
					// intent.putExtra("info", "has");
					intent.putExtra("name", holder_name_str);
					// intent.putExtra("pinyin", holder_e_name_str);
					// intent.putExtra("id_type", document_code_str);
					// intent.putExtra("id_no", cert_num_str);
					// intent.putExtra("birthday", brith_str);
					// intent.putExtra("sex", sex_code_str);
					// intent.putExtra("tel", phone_num_str);
					// intent.putExtra("email", email_str);
					intent.putExtra("holder", holderstr);
					setResult(1, intent);

					FillInApplicantActivity.this.finish();
				}
			}
		});

	}

	private static boolean isMobileNum(String mobiles) {
		 Pattern p = Pattern
		 .compile("^((17[0-9])|(14[0-9])|(13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		 Matcher m = p.matcher(mobiles);
		 System.out.println(m.matches() + "---");
		 return m.matches();

		 }
	
	private String Json_Value(String name, String pinyin, String id_type,
							  String id_no, String birthday, String sex, String tel, String email) {
		String jsonresult = "";// 定义返回字符串

		try {
			// JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
			// JSONObject jsonObj = new JSONObject();//pet对象，json形式
			// jsonObj.put("petid", pet.getPetid());//向pet对象里面添加值
			// jsonObj.put("petname", pet.getPetname());
			// jsonObj.put("pettype", pet.getPettype());
			// // 把每个数据当作一对象添加到数组里
			// jsonarray.put(jsonObj);//向json数组里面添加pet对象
			// object.put("pet", jsonarray);//向总对象里面添加包含pet的数组
			// jsonresult = object.toString();//生成返回字符串
			//
			// // {"pet":[{"petid":100,"petname":"name1","pettype":"type1"}]}
			//
			JSONObject jsonObj = new JSONObject();// pet对象，json形式
			jsonObj.put("name", name);// 向pet对象里面添加值
			jsonObj.put("pinyin", pinyin);
			jsonObj.put("id_type", id_type);
			jsonObj.put("id_no", id_no);
			jsonObj.put("birthday", birthday);
			jsonObj.put("sex", sex);
			jsonObj.put("tel", tel);
			jsonObj.put("email", email);
			jsonresult = jsonObj.toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
	}

	
	
	
	private boolean isEmail(String email) {

		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

		Pattern p = Pattern.compile(str);

		Matcher m = p.matcher(email);

		return m.matches();

		}
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;
		private int editStart;
		private int editEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
									  int arg3) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

		}

		@SuppressLint("DefaultLocale")
		@Override
		public void afterTextChanged(Editable s) {

			String text = s.toString();
			int index = holder_name.getSelectionStart();
			

			if (!isName(text)) {
				if (text.length() <= 1) {
					s.clear();
				} else if((index - 1)<0) {
					s.clear();
				}else{
					s.delete(index - 1, index);
				}
			}

			String e_name_str = characterParser.getSelling(text).toUpperCase();
			holder_e_name.setText(e_name_str);

		}
	};

	private TextWatcher cert_watcher = new TextWatcher() {
		private CharSequence temp;
		private int editStart;
		private int editEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
									  int arg3) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable s) {

			String text = s.toString();

			String lastValue="";
			if ((Boolean.valueOf(IdCheckUtil.IDCardValidate(text))
					.booleanValue()) && (cert_type.equals("身份证"))) {
				if (text.length() == 15) {

					text = text.substring(0, 6) + "19" + text.substring(6, 15);
					
					lastValue = text.substring(text.length() - 1,
							text.length());
				}else{
					
					lastValue = text.substring(text.length() - 2,
							text.length()-1);
				}

				String strYear = text.substring(6, 10);// 年份
				String strMonth = text.substring(10, 12);// 月份
				String strDay = text.substring(12, 14);// 日份

				String brithstr = strYear + "-" + strMonth + "-" + strDay;

				

				int sex_num = Integer.parseInt(lastValue) % 2;
				if (sex_num == 0) {
					sex_txt.setText("女");
					sex_code_str = "02";

				} else {
					sex_txt.setText("男");
					sex_code_str = "01";

				}

				brith.setText(brithstr);
			}

		}
	};

	private void SexPopupWindow() {
		layout = ((RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.type_pop, null));
		listViewSpinner = (ListView) this.layout.findViewById(R.id.lv_dialog);

		listViewSpinner
				.setAdapter(new ArrayAdapter<String>(
						FillInApplicantActivity.this, R.layout.text,
						R.id.tv_text, sex));

		popupWindow = new PopupWindow(this);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(LayoutParams.MATCH_PARENT);
		popupWindow.setHeight(LayoutParams.MATCH_PARENT);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setContentView(this.layout);
		popupWindow.showAsDropDown(this.popview);

		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		layout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int heightTop = layout.findViewById(R.id.c_type_rel).getTop();
				int heightBottom = layout.findViewById(R.id.c_type_rel)
						.getBottom();
				int heightLeft = layout.findViewById(R.id.c_type_rel).getLeft();
				int heightRight = layout.findViewById(R.id.c_type_rel)
						.getRight();

				int y = (int) event.getY();
				int x = (int) event.getX();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					 v.performClick();
					if (y < heightTop || y > heightBottom || x < heightLeft
							|| x > heightRight) {
						popupWindow.dismiss();
					}

				}

				return true;
			}
		});

		listViewSpinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				arg0.setVisibility(View.VISIBLE);

				sex_code_str = sex_code[pos];
				sex_txt.setText(sex[pos]);
				popupWindow.dismiss();
				popupWindow = null;

			}
		});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 如果是返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent intent = getIntent();

			setResult(0, intent);
			finish();

		}
		return super.onKeyDown(keyCode, event);
	}

	private void showPopupWindow() {

		layout = ((RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.type_pop, null));
		listViewSpinner = (ListView) this.layout.findViewById(R.id.lv_dialog);

		listViewSpinner.setAdapter(new ArrayAdapter<String>(
				FillInApplicantActivity.this, R.layout.text, R.id.tv_text,
				document_type));

		popupWindow = new PopupWindow(this);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(LayoutParams.MATCH_PARENT);
		popupWindow.setHeight(LayoutParams.MATCH_PARENT);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setContentView(this.layout);
		popupWindow.showAsDropDown(this.popview);

		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		layout.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility") public boolean onTouch(View v, MotionEvent event) {

				int heightTop = layout.findViewById(R.id.c_type_rel).getTop();
				int heightBottom = layout.findViewById(R.id.c_type_rel)
						.getBottom();
				int heightLeft = layout.findViewById(R.id.c_type_rel).getLeft();
				int heightRight = layout.findViewById(R.id.c_type_rel)
						.getRight();
				int y = (int) event.getY();
				int x = (int) event.getX();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < heightTop || y > heightBottom || x < heightLeft
							|| x > heightRight) {
						popupWindow.dismiss();
					}

				}

				return true;
			}
		});

		listViewSpinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				arg0.setVisibility(View.VISIBLE);

				document_code_str = document_code[pos];
				cert_type = document_type[pos];
				document.setText(document_type[pos]);

				
				String text = cert_num.getText().toString();
				String lastValue="";
				if (IdCheckUtil.IDCardValidate(text)&& cert_type.equals("身份证")) {
					if (text.length() == 15) {

						text = text.substring(0, 6) + "19" + text.substring(6, 15);
						
						lastValue = text.substring(text.length() - 1,
								text.length());
					}else{
						
						lastValue = text.substring(text.length() - 2,
								text.length()-1);
					}

					String strYear = text.substring(6, 10);// 年份
					String strMonth = text.substring(10, 12);// 月份
					String strDay = text.substring(12, 14);// 日份

					String brithstr = strYear + "-" + strMonth + "-" + strDay;

					int sex_num = Integer.parseInt(lastValue) % 2;
					if (sex_num == 0) {
						sex_txt.setText("女");
						sex_code_str = "02";
					} else {
						sex_txt.setText("男");
						sex_code_str = "01";
					}

					brith.setText(brithstr);
				}
				
				popupWindow.dismiss();
				popupWindow = null;

			}
		});

	}

	private static boolean isName(String text) {

		String text_OK = "[a-zA-Z\u4e00-\u9fa5]*";
		return !TextUtils.isEmpty(text) && text.matches(text_OK);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
	}

	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			return !(event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom);
		}
		return false;
	}
}
