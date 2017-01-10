package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.gghl.view.wheelcity.OnWheelChangedListener;
import com.gghl.view.wheelcity.WheelView;
import com.gghl.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;
import com.zf.iosdialog.widget.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
public class SelectAssuranceActivity extends BaseActivity {

	private Boolean bjmp_cs_ON = false, bjmp_sz_ON = false,
			bjmp_sjck_ON = false, bjmp_fjx_ON = false, bjmp_dq_ON = false;

	static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			Bundle bd = msg.getData();

			sydszzrx_str = bd.getString("value") + "万";
			sydszzrx_txt.setText(sydszzrx_str);

			// menuWindow.dismiss();

		}
	};

	private Handler errcode_handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			String data = (String) msg.obj;

			String status = data;

			if (status.equals("false")) {

				Toast.makeText(SelectAssuranceActivity.this,
						"网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
			}
		}

	};

	// 自定义的弹出框类
	SydszzrxPopupWindow SydszzrxWindow;

	private ImageView s_a_back;
	private TextView s_a_car_no, s_a_owner_name, s_a_engine_no, s_a_vin;

	private RelativeLayout setting_rel5, sydszzrx_rel, sjzwzrx_rel,
			ckzwzrx_rel, blddpsx_rel, cshhssx_rel, dcjcd_rel;

	ScrollView main_scroll;
	private String user_id;
	private String token;

	private String car_no, owner_name, engine_no, frame_num, c_p_type,
			system_type, regist_date, running_area;
	private String[] insurance_company;
	private String user_name;
	private ToggleButton jqx_toggle, clssx_toggle, qcdqx_toggle, zrssx_toggle,
			ssxs_toggle, bjmp_cs_toggle, bjmp_sz_toggle, bjmp_dq_toggle,
			bjmp_sjck_toggle, bjmp_fjx_toggle;

	private String jqx, clssx = "0", sydszzrx = "0", qcdqx = "0",
			sjzwzrx = "0", ckzwzrx = "0", blddpsx = "0", cshhssx = "0",
			zrssx = "0", dcjcd = "0", ssxs = "0", bjmp_cs = "0", bjmp_sz = "0",
			bjmp_dq = "0", bjmp_sjck = "0", bjmp_fjx = "0";

	private String[] SydszzrxArray = { "5万", "10万", "15万", "20万", "30万", "50万",
			"100+", "不投保" };
	private String[] SjzwzrxArray = { "5000", "10000", "20000", "30000",
			"40000", "50000", "100000", "200000", "500000", "不投保" };
	private String[] CkzwzrxArray = { "10000", "20000", "30000", "50000",
			"100000", "200000", "500000", "不投保" };
	private String[] BlddpsxArray = { "国产", "进口", "不投保" };
	private String[] BlddpsxCode = { "1", "2", "0" };
	private String[] CshhssxArray = { "2000", "5000", "10000", "20000", "不投保" };
	private String[] CshhssxCode = { "1", "2", "3", "4", "0" };
	private String[] DcjcdArray = { "国产", "进口", "不投保" };
	private String[] DcjcdCode = { "1", "2", "0" };

	private String sydszzrxstr, sjzwzrxstr, ckzwzrxstr, blddpsxstr, cshhssxstr,
			dcjcdstr;
	private String sydszzrxold ="不投保", sjzwzrxold="不投保", ckzwzrxold="不投保", blddpsxold, cshhssxold,
			dcjcdold;

	private static String sydszzrx_str;
	private static TextView sydszzrx_txt;

	private TextView sjzwzrx_txt;

	private TextView ckzwzrx_txt;

	private TextView blddpsx_txt;

	private TextView cshhssx_txt;

	private TextView dcjcd_txt;

	private TextView p_num;
	private int policy_num = 0, bjmp_fjx_num = 0;

	private Button scan_price;
	private Map<String, String> key_value = new HashMap<String, String>();

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			// Map<String, String> data = (Map<String, String>) msg.obj;
			Map<String, String> data = (Map<String, String>) msg.obj;

			String errcode = data.get("errcode");

			if (errcode.equals("0")) {

				CustomDialog.Builder builder = new CustomDialog.Builder(
						SelectAssuranceActivity.this);

				builder.setTitle("提示");
				builder.setMessage("稍后请在我的-我的订单的询价单列表中查看报价信息");

				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
								CheXianBJActivity.CheXianBJ_instance.finish();
								CheXianTBActivity.CheXianTB_instance.finish();
								finish();
							}
						});

				builder.create().show();

			} else {

				String errmsg = data.get("errmsg");

				CustomDialog.Builder builder = new CustomDialog.Builder(
						SelectAssuranceActivity.this);

				builder.setTitle("提示");
				builder.setMessage(errmsg);

				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();

							}
						});
				builder.create().show();
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_assurance);

		Intent intent = getIntent();
		car_no = intent.getStringExtra("car_no");
		owner_name = intent.getStringExtra("owner_name");
		engine_no = intent.getStringExtra("engine_no");
		frame_num = intent.getStringExtra("frame_num");
		c_p_type = intent.getStringExtra("c_p_type");
		system_type = intent.getStringExtra("system_type");
		insurance_company = intent.getStringArrayExtra("insurance_company");
		regist_date = intent.getStringExtra("regist_date");
		running_area = intent.getStringExtra("running_area");


		user_id = sp.getString("Login_UID", "");
		token = sp.getString("Login_TOKEN", "");

		init();
		initEvent();

	}

	void init() {

		main_scroll = (ScrollView) findViewById(R.id.main_scroll);
		s_a_back = (ImageView) findViewById(R.id.s_a_back);
		s_a_car_no = (TextView) findViewById(R.id.s_a_car_no);
		s_a_owner_name = (TextView) findViewById(R.id.s_a_owner_name);
		s_a_engine_no = (TextView) findViewById(R.id.s_a_engine_no);
		s_a_vin = (TextView) findViewById(R.id.s_a_vin);

		setting_rel5 = (RelativeLayout) findViewById(R.id.setting_rel5);
		sydszzrx_rel = (RelativeLayout) findViewById(R.id.sydszzrx_rel);
		sjzwzrx_rel = (RelativeLayout) findViewById(R.id.sjzwzrx_rel);
		ckzwzrx_rel = (RelativeLayout) findViewById(R.id.ckzwzrx_rel);
		blddpsx_rel = (RelativeLayout) findViewById(R.id.blddpsx_rel);
		cshhssx_rel = (RelativeLayout) findViewById(R.id.cshhssx_rel);
		dcjcd_rel = (RelativeLayout) findViewById(R.id.dcjcd_rel);

		jqx_toggle = (ToggleButton) findViewById(R.id.jqx_toggle);
		clssx_toggle = (ToggleButton) findViewById(R.id.clssx_toggle);
		qcdqx_toggle = (ToggleButton) findViewById(R.id.qcdqx_toggle);
		zrssx_toggle = (ToggleButton) findViewById(R.id.zrssx_toggle);
		ssxs_toggle = (ToggleButton) findViewById(R.id.ssxs_toggle);
		bjmp_cs_toggle = (ToggleButton) findViewById(R.id.bjmp_cs_toggle);
		
		bjmp_sz_toggle = (ToggleButton) findViewById(R.id.bjmp_sz_toggle);
		bjmp_dq_toggle = (ToggleButton) findViewById(R.id.bjmp_dq_toggle);
		bjmp_sjck_toggle = (ToggleButton) findViewById(R.id.bjmp_sjck_toggle);
		bjmp_fjx_toggle = (ToggleButton) findViewById(R.id.bjmp_fjx_toggle);

		sydszzrx_txt = (TextView) findViewById(R.id.sydszzrx_txt);
		sjzwzrx_txt = (TextView) findViewById(R.id.sjzwzrx_txt);
		ckzwzrx_txt = (TextView) findViewById(R.id.ckzwzrx_txt);
		blddpsx_txt = (TextView) findViewById(R.id.blddpsx_txt);
		cshhssx_txt = (TextView) findViewById(R.id.cshhssx_txt);
		dcjcd_txt = (TextView) findViewById(R.id.dcjcd_txt);
		p_num = (TextView) findViewById(R.id.p_num);
		scan_price = (Button) findViewById(R.id.scan_price);

	}

	void initEvent() {

		s_a_car_no.setText(car_no);
		s_a_owner_name.setText(owner_name);
		s_a_engine_no.setText(engine_no);
		s_a_vin.setText(frame_num);

		s_a_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

		jqx_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				// TODO Auto-generated method stub

				if (on) {
					jqx = "1";
					policy_num++;
					bjmp_cs_ON = true;

					p_num.setText(policy_num + "");
				} else {

					jqx = "0";
					policy_num--;
					p_num.setText(policy_num + "");
				}

			}
		});

		clssx_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				// TODO Auto-generated method stub

				if (on) {
					clssx = "1";
					policy_num++;

					bjmp_cs_toggle.setClickable(true);
					bjmp_cs_toggle.setToggleOn();
					bjmp_cs_ON = true;
					bjmp_cs = "1";
					policy_num++;

					p_num.setText(policy_num + "");
				} else {

					clssx = "0";
					policy_num--;

					if (bjmp_cs_ON) {

						
						policy_num--;
						bjmp_cs_ON = false;
					}
					bjmp_cs = "0";
					bjmp_cs_toggle.setToggleOff();
					bjmp_cs_toggle.setClickable(false);
					p_num.setText(policy_num + "");
				}

			}
		});

		qcdqx_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {

				if (on) {

					qcdqx = "1";
					policy_num++;
					bjmp_dq_toggle.setClickable(true);
					bjmp_dq_toggle.setToggleOn();
					bjmp_dq_ON = true;
					bjmp_dq="0";
					policy_num++;
					p_num.setText(policy_num + "");

				} else {

					qcdqx = "0";
					policy_num--;
					if (bjmp_dq_ON) {

						policy_num--;
						bjmp_dq_ON = false;
					}
					bjmp_dq="1";
					bjmp_dq_toggle.setToggleOff();
					bjmp_dq_toggle.setClickable(false);
					p_num.setText(policy_num + "");

				}

			}
		});

		zrssx_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {

				if (on) {

					zrssx = "1";
					policy_num++;
					bjmp_fjx_num++;

					bjmp_fjx_toggle.setClickable(true);
					if (!bjmp_fjx_ON) {
						bjmp_fjx="1";
						bjmp_fjx_toggle.setToggleOn();
						bjmp_fjx_ON = true;
						policy_num++;
					}
					p_num.setText(policy_num + "");

				} else {

					zrssx = "0";
					policy_num--;
					bjmp_fjx_num--;
					if (bjmp_fjx_num == 0) {
						bjmp_fjx="0";
						bjmp_fjx_toggle.setClickable(false);
						bjmp_fjx_toggle.setToggleOff();
						bjmp_fjx_ON = false;
						policy_num--;
					}
					p_num.setText(policy_num + "");

				}

			}
		});

		ssxs_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {

				if (on) {

					ssxs = "1";
					policy_num++;
					bjmp_fjx_num++;
					if (!bjmp_fjx_ON) {
						bjmp_fjx="1";
						bjmp_fjx_toggle.setClickable(true);
						bjmp_fjx_toggle.setToggleOn();
						bjmp_fjx_ON = true;
						policy_num++;
					}
					p_num.setText(policy_num + "");

				} else {

					ssxs = "0";
					policy_num--;
					bjmp_fjx_num--;
					if (bjmp_fjx_num == 0) {
						bjmp_fjx="0";
						bjmp_fjx_toggle.setClickable(false);
						bjmp_fjx_toggle.setToggleOff();
						bjmp_fjx_ON = false;
						policy_num--;
					}
					p_num.setText(policy_num + "");

				}

			}
		});

		bjmp_cs_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {

				if (on) {

					bjmp_cs = "1";
					policy_num++;
					bjmp_cs_ON = true;
					p_num.setText(policy_num + "");

				} else {

					bjmp_cs = "0";
					policy_num--;
					bjmp_cs_ON = false;
					p_num.setText(policy_num + "");

				}

			}
		});

		bjmp_dq_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {

				if (on) {

					bjmp_dq = "1";
					policy_num++;
					bjmp_dq_ON = true;
					p_num.setText(policy_num + "");

				} else {

					bjmp_dq = "0";
					policy_num--;
					bjmp_dq_ON = false;
					p_num.setText(policy_num + "");

				}

			}
		});

		bjmp_sz_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {

				if (on) {

					bjmp_sz = "1";
					policy_num++;
					bjmp_sz_ON = true;
					p_num.setText(policy_num + "");

				} else {

					bjmp_sz = "0";
					policy_num--;
					bjmp_sz_ON = false;
					p_num.setText(policy_num + "");

				}
			}
		});

		bjmp_sjck_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {

				if (on) {

					bjmp_sjck = "1";
					policy_num++;
					bjmp_sjck_ON = true;
					p_num.setText(policy_num + "");

				} else {

					bjmp_sjck = "0";
					policy_num--;
					bjmp_sjck_ON = false;
					p_num.setText(policy_num + "");

				}

			}
		});

		bjmp_fjx_toggle.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {

				if (on) {

					bjmp_fjx = "1";
					policy_num++;
					bjmp_fjx_ON = true;
					p_num.setText(policy_num + "");

				} else {

					bjmp_fjx = "0";
					policy_num--;
					bjmp_fjx_ON = false;
					p_num.setText(policy_num + "");

				}

			}
		});

		sydszzrx_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				View view = sydszzrx_dialogm();
				final MyAlertDialog dialog = new MyAlertDialog(
						SelectAssuranceActivity.this).builder().setTitle("请选择")
						.setCancelable(false)

						// .setMsg("")
						// .setEditText("111")

						.setView(view)

						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {

								// user_type=user_typeing;

								sydszzrx_txt.setText(sydszzrxstr);
								if (!sydszzrxstr.equals(sydszzrxold)) {
									if (sydszzrxold.equals("不投保")) {
										policy_num++;

										bjmp_sz_toggle.setClickable(true);
										bjmp_sz_toggle.setToggleOn();
										bjmp_sz_ON = true;
										bjmp_sz = "0";
										policy_num++;
										p_num.setText(policy_num + "");

									} else if (sydszzrxstr.equals("不投保")) {

										policy_num--;

										if (bjmp_sz_ON) {

											policy_num--;
											bjmp_sz_ON = false;
										}
										bjmp_sz = "1";
										bjmp_sz_toggle.setToggleOff();
										bjmp_sz_toggle.setClickable(false);
										p_num.setText(policy_num + "");

									}
									sydszzrxold = sydszzrxstr;

								}

								if (sydszzrxstr.equals("100+")) {

									SydszzrxWindow = new SydszzrxPopupWindow(
											SelectAssuranceActivity.this);
									SydszzrxWindow.showAtLocation(main_scroll,
											Gravity.TOP, 0, 0);

								}

							}
						});
				dialog.show();

			}
		});

		sjzwzrx_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				View view = sjzwzrx_dialogm();
				final MyAlertDialog dialog = new MyAlertDialog(
						SelectAssuranceActivity.this).builder().setTitle("请选择")
						.setCancelable(false)

						// .setMsg("")
						// .setEditText("111")

						.setView(view)

						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {

								// user_type=user_typeing;
								sjzwzrx_txt.setText(sjzwzrxstr);

								if (!sjzwzrxstr.equals(sjzwzrxold)) {
									if (sjzwzrxold.equals("不投保")) {
										policy_num++;

										if (!bjmp_sjck_ON) {
											bjmp_sjck_toggle.setToggleOn();
											bjmp_sjck = "1";
											bjmp_sjck_ON = true;
											policy_num++;
										}
										p_num.setText(policy_num + "");

									} else if (sjzwzrxstr.equals("不投保")) {

										policy_num--;
										if (bjmp_sjck_ON&&ckzwzrxold.equals("不投保")) {
											bjmp_sjck = "0";
											bjmp_sjck_toggle.setToggleOff();
											bjmp_sjck_ON = false;
											policy_num--;
										}
									
										p_num.setText(policy_num + "");

									}

									sjzwzrxold = sjzwzrxstr;

								}
							}
						});
				dialog.show();
			}
		});

		ckzwzrx_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				View view = ckzwzrx_dialogm();
				final MyAlertDialog dialog = new MyAlertDialog(
						SelectAssuranceActivity.this).builder().setTitle("请选择")
						.setCancelable(false)
						// .setMsg("")
						// .setEditText("111")
						.setView(view)

						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {

								// user_type=user_typeing;
								ckzwzrx_txt.setText(ckzwzrxstr);

								if (!ckzwzrxstr.equals(ckzwzrxold)) {
									if (ckzwzrxold.equals("不投保")) {
										policy_num++;

										if (!bjmp_sjck_ON) {
											bjmp_sjck = "1";
											bjmp_sjck_toggle.setToggleOn();
											bjmp_sjck_ON = true;
											policy_num++;
										}

										p_num.setText(policy_num + "");

									} else if (ckzwzrxstr.equals("不投保")) {

										policy_num--;
										if (bjmp_sjck_ON&&sjzwzrxold.equals("不投保")) {
											bjmp_sjck = "0";
											policy_num--;
											bjmp_sjck_toggle.setToggleOff();
											bjmp_sjck_ON = false;
										}
										
										p_num.setText(policy_num + "");

									}
									ckzwzrxold = ckzwzrxstr;

								}
							}
						});
				dialog.show();
			}
		});

		blddpsx_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				View view = blddpsx_dialogm();
				final MyAlertDialog dialog = new MyAlertDialog(
						SelectAssuranceActivity.this).builder()
						.setTitle("请选择")
						.setCancelable(false)
						// .setMsg("")
						// .setEditText("111")
						.setView(view)
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {

								// user_type=user_typeing;
								blddpsx_txt.setText(blddpsxstr);

								if (!blddpsxstr.equals(blddpsxold)) {
									if (blddpsxold.equals("不投保")) {
										bjmp_fjx_toggle.setClickable(true);
										policy_num++;
										bjmp_fjx_num++;

										if (!bjmp_fjx_ON) {
											bjmp_fjx="1";
											bjmp_fjx_toggle.setToggleOn();
											bjmp_fjx_ON = true;
											policy_num++;
										}

										p_num.setText(policy_num + "");

									} else if (blddpsxstr.equals("不投保")) {

										bjmp_fjx_num--;
										policy_num--;
										if (bjmp_fjx_num == 0) {
											bjmp_fjx="0";
											bjmp_fjx_toggle.setClickable(false);
											bjmp_fjx_toggle.setToggleOff();
											bjmp_fjx_ON = false;
											policy_num--;
										}
										p_num.setText(policy_num + "");

									}
									blddpsxold = blddpsxstr;

								}
							}
						});
				dialog.show();
			}
		});

		cshhssx_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				View view = cshhssx_dialogm();
				final MyAlertDialog dialog = new MyAlertDialog(
						SelectAssuranceActivity.this).builder()
						.setTitle("请选择")
						.setCancelable(false)
						// .setMsg("")
						// .setEditText("111")
						.setView(view)
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {

								// user_type=user_typeing;
								cshhssx_txt.setText(cshhssxstr);

								if (!cshhssxstr.equals(cshhssxold)) {
									if (cshhssxold.equals("不投保")) {
										policy_num++;
										bjmp_fjx_num++;

										if (!bjmp_fjx_ON) {
											bjmp_fjx="1";
											bjmp_fjx_toggle.setClickable(true);
											bjmp_fjx_toggle.setToggleOn();
											bjmp_fjx_ON = true;
											policy_num++;
										}

										p_num.setText(policy_num + "");

									} else if (cshhssxstr.equals("不投保")) {

										policy_num--;
										bjmp_fjx_num--;

										if (bjmp_fjx_num == 0) {
											bjmp_fjx="0";
											bjmp_fjx_toggle.setClickable(false);
											bjmp_fjx_toggle.setToggleOff();
											bjmp_fjx_ON = false;
											policy_num--;
										}
										p_num.setText(policy_num + "");

									}
									cshhssxold = cshhssxstr;
								}
							}
						});
				dialog.show();
			}
		});

		dcjcd_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				View view = dcjcd_dialogm();
				final MyAlertDialog dialog = new MyAlertDialog(
						SelectAssuranceActivity.this).builder()
						.setTitle("请选择")
						.setCancelable(false)
						// .setMsg("")
						// .setEditText("111")
						.setView(view)
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {

								dcjcd_txt.setText(dcjcdstr);

								if (!dcjcdstr.equals(dcjcdold)) {
									if (dcjcdold.equals("不投保")) {
										policy_num++;
										bjmp_fjx_num++;
										if (!bjmp_fjx_ON) {
											bjmp_fjx="1";
											bjmp_fjx_toggle.setClickable(true);
											bjmp_fjx_toggle.setToggleOn();
											bjmp_fjx_ON = true;
											policy_num++;
										}
										p_num.setText(policy_num + "");

									} else if (dcjcdstr.equals("不投保")) {

										policy_num--;
										bjmp_fjx_num--;
										if (bjmp_fjx_num == 0) {
											bjmp_fjx="0";
											bjmp_fjx_toggle.setClickable(false);
											bjmp_fjx_toggle.setToggleOff();
											bjmp_fjx_ON = false;
											policy_num--;
										}
										p_num.setText(policy_num + "");

									}
									dcjcdold = dcjcdstr;
								}

							}
						});
				dialog.show();
			}
		});

		scan_price.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (policy_num == 0) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							SelectAssuranceActivity.this);

					builder.setTitle("提示");
					builder.setMessage("请选择险种");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else {

					String json_value = Json_Value();

					Log.d("8888888", json_value);
					key_value.put("user_id", user_id);
					key_value.put("token", token);
					key_value.put("order_info", json_value);
					String url = IpConfig.getUri("addOrderInfo");

					setdata(url);

				}
			}
		});
		if (c_p_type.equals("01")) {
			initSetClickFalse();
			jqx_toggle.toggleOn();
			jqx = "1";

		} else if (c_p_type.equals("02")) {
			policy_num++;
			jqx_toggle.setClickable(false);

			jqx = "0";
			initSetClickTrue();

		} else if (c_p_type.equals("03")) {
			policy_num++;
			initSetClickTrue();
			jqx_toggle.toggleOn();
			jqx = "1";

		}
	}

	public String Json_Value() {
		String jsonresult = "";// 定义返回字符串
		JSONObject jsonObj2 = new JSONObject();

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
			JSONObject jsonObj_5 = new JSONObject();
			jsonObj_5.put("clssx", clssx);
			if (sydszzrxold.equals("不投保")) {
				jsonObj_5.put("sydszzrx", "0");
			} else if (sydszzrxold.equals("100+")) {
				jsonObj_5.put("sydszzrx", sydszzrx_str);
			} else {
				jsonObj_5.put("sydszzrx", sydszzrxold);
			}
			jsonObj_5.put("qcdqx", qcdqx);

			if (sjzwzrxold.equals("不投保")) {
				jsonObj_5.put("sjzwzrx", "0");
			} else {
				jsonObj_5.put("sjzwzrx", sjzwzrxold);
			}

			if (ckzwzrxold.equals("不投保")) {
				jsonObj_5.put("ckzwzrx", "0");
			} else {
				jsonObj_5.put("ckzwzrx", ckzwzrxold);
			}
			jsonObj_5.put("blddpsx", blddpsx);

			jsonObj_5.put("cshhssx", cshhssx);
			jsonObj_5.put("zrssx", zrssx);
			jsonObj_5.put("dcjcd", dcjcd);

			jsonObj_5.put("ssxs", ssxs);
			jsonObj_5.put("bjmp_cs", bjmp_cs);
			jsonObj_5.put("bjmp_sz", bjmp_sz);
			jsonObj_5.put("bjmp_dq", bjmp_dq);
			Log.d("bjmp_sjck8888", bjmp_sjck);
			jsonObj_5.put("bjmp_sjck", bjmp_sjck);
			jsonObj_5.put("bjmp_fjx", bjmp_fjx);

			JSONObject jsonObj_4_syx = new JSONObject();
			JSONObject jsonObj_4_jqx = new JSONObject();

			jsonObj_4_syx.put("detail", jsonObj_5);
			if (c_p_type.equals("01")) {
				jsonObj_4_jqx.put("is_check", "1");
				jsonObj_4_syx.put("is_check", "0");

			} else if (c_p_type.equals("02")) {
				jsonObj_4_jqx.put("is_check", "0");
				jsonObj_4_syx.put("is_check", "1");

			} else {
				jsonObj_4_jqx.put("is_check", "1");
				jsonObj_4_syx.put("is_check", "1");
			}
			JSONObject jsonObj_3_type_detail = new JSONObject();

			jsonObj_3_type_detail.put("jqx", jsonObj_4_jqx);
			jsonObj_3_type_detail.put("syx", jsonObj_4_syx);
			JSONArray jsonarray_3_company = new JSONArray();

			for (int i = 0; i < insurance_company.length; i++) {
				jsonarray_3_company.put(insurance_company[i]);

			}

			jsonObj2.put("type", c_p_type);
			jsonObj2.put("car_model", system_type);
			jsonObj2.put("insurance_company", jsonarray_3_company);
			jsonObj2.put("engine_no", engine_no);
			jsonObj2.put("cj_no", frame_num);
			jsonObj2.put("holder_name", owner_name);
			jsonObj2.put("regist_date", regist_date);
			if (car_no.equals("新车未上牌")) {

				jsonObj2.put("car_code", "新车未上牌");
				jsonObj2.put("is_new", "1");
			} else {
				jsonObj2.put("car_code", car_no);
				jsonObj2.put("is_new", "0");
			}

			jsonObj2.put("running_area", running_area);

			jsonObj2.put("type_detail", jsonObj_3_type_detail);
			jsonresult = String.valueOf(jsonObj2);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonresult;
	}

	void initSetClickFalse() {

		clssx_toggle.setClickable(false);
		sydszzrx_rel.setClickable(false);
		qcdqx_toggle.setClickable(false);
		sjzwzrx_rel.setClickable(false);
		ckzwzrx_rel.setClickable(false);

		blddpsx_rel.setClickable(false);
		cshhssx_rel.setClickable(false);
		zrssx_toggle.setClickable(false);
		dcjcd_rel.setClickable(false);
		ssxs_toggle.setClickable(false);

		bjmp_cs_toggle.setClickable(false);
		bjmp_sz_toggle.setClickable(false);
		bjmp_dq_toggle.setClickable(false);
		bjmp_sjck_toggle.setClickable(false);
		bjmp_fjx_toggle.setClickable(false);

	}

	void initSetClickTrue() {

		bjmp_fjx_toggle.setClickable(false);
		bjmp_dq_toggle.setClickable(false);
	

		clssx_toggle.toggleOn();
		bjmp_sz_toggle.toggleOn();
		bjmp_sjck_toggle.toggleOn();
		policy_num++;
		policy_num++;
		
		bjmp_cs_ON = true;
		bjmp_sjck_ON= true;
		clssx = "1";
		bjmp_cs = "1";
		bjmp_sz = "1";
		bjmp_sjck = "1";
		
		
		sydszzrx_txt.setText("20万");
		sjzwzrx_txt.setText("5000");
		ckzwzrx_txt.setText("10000");
		sydszzrxold = "20万";
		sjzwzrxold = "5000";
		ckzwzrxold = "10000";
		blddpsxold = "不投保";
		cshhssxold = "不投保";
		dcjcdold = "不投保";

	}

	



	private void setdata(String url) {

		
		
		
		OkHttpUtils.post()//
		.url(url)//
		.params(key_value)//
		.build()//
		.execute(new StringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Log.e("error", "获取数据异常 ", e);
				String status = "false";
				Message message = Message.obtain();
				message.obj = status;
				errcode_handler.sendMessage(message);
			}

			@Override
			public void onResponse(String response, int id) {
				Map<String, String> map = new HashMap<String, String>();
				String jsonString = response;
				Log.d("onSuccess", "onSuccess json = " + jsonString);

				try {

					if (jsonString == null || jsonString.equals("")
							|| jsonString.equals("null")) {
						String status = "false";
						Message message = Message.obtain();

						message.obj = status;

						errcode_handler.sendMessage(message);
					} else {
						JSONObject jsonObject = new JSONObject(jsonString);
						String data = jsonObject.getString("data");

						String errmsg = jsonObject.getString("errmsg");

						String errcode = jsonObject.getString("errcode");

						map.put("errcode", errcode);
						map.put("errmsg", errmsg);
						Log.d("44444", errcode);
						Message message = Message.obtain();

						message.obj = map;

						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	

	}

	@SuppressLint("InflateParams")
	private View sydszzrx_dialogm() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.wheelcity_single_layout, null);
		final WheelView country = (WheelView) contentView
				.findViewById(R.id.wheelcity_single);
		country.setVisibleItems(1);
		country.setViewAdapter(new SydszzrxAdapter(this));
		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				sydszzrxstr = SydszzrxArray[country.getCurrentItem()];

			}
		});

		country.setCurrentItem(3);

		sydszzrxstr = SydszzrxArray[country.getCurrentItem()];

		return contentView;
	}

	/**
	 * Adapter for countries
	 */
	private class SydszzrxAdapter extends AbstractWheelTextAdapter {

		// Countries names
		private String countries[] = SydszzrxArray;

		/**
		 * Constructor
		 */
		protected SydszzrxAdapter(Context context) {
			super(context, R.layout.w_single_item_layout, NO_RESOURCE);
			setItemTextResource(R.id.wheelcity_country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}

	@SuppressLint("InflateParams")
	private View sjzwzrx_dialogm() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.wheelcity_single_layout, null);
		final WheelView country = (WheelView) contentView
				.findViewById(R.id.wheelcity_single);
		country.setVisibleItems(1);
		country.setViewAdapter(new SjzwzrxAdapter(this));
		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				sjzwzrxstr = SjzwzrxArray[country.getCurrentItem()];

			}
		});

		country.setCurrentItem(0);

		sjzwzrxstr = SjzwzrxArray[country.getCurrentItem()];

		return contentView;
	}

	/**
	 * Adapter for countries
	 */
	private class SjzwzrxAdapter extends AbstractWheelTextAdapter {

		// Countries names
		private String countries[] = SjzwzrxArray;

		/**
		 * Constructor
		 */
		protected SjzwzrxAdapter(Context context) {
			super(context, R.layout.w_single_item_layout, NO_RESOURCE);
			setItemTextResource(R.id.wheelcity_country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}

	@SuppressLint("InflateParams")
	private View ckzwzrx_dialogm() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.wheelcity_single_layout, null);
		final WheelView country = (WheelView) contentView
				.findViewById(R.id.wheelcity_single);
		country.setVisibleItems(1);
		country.setViewAdapter(new CkzwzrxAdapter(this));
		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				ckzwzrxstr = CkzwzrxArray[country.getCurrentItem()];

			}
		});

		country.setCurrentItem(0);

		ckzwzrxstr = CkzwzrxArray[country.getCurrentItem()];

		return contentView;
	}

	/**
	 * Adapter for countries
	 */
	private class CkzwzrxAdapter extends AbstractWheelTextAdapter {

		// Countries names
		private String countries[] = CkzwzrxArray;

		/**
		 * Constructor
		 */
		protected CkzwzrxAdapter(Context context) {
			super(context, R.layout.w_single_item_layout, NO_RESOURCE);
			setItemTextResource(R.id.wheelcity_country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}

	@SuppressLint("InflateParams")
	private View blddpsx_dialogm() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.wheelcity_single_layout, null);
		final WheelView country = (WheelView) contentView
				.findViewById(R.id.wheelcity_single);

		country.setVisibleItems(1);

		country.setViewAdapter(new BlddpsxAdapter(this));
		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				blddpsxstr = BlddpsxArray[country.getCurrentItem()];
				blddpsx = BlddpsxCode[country.getCurrentItem()];

			}
		});

		country.setCurrentItem(0);

		blddpsxstr = BlddpsxArray[country.getCurrentItem()];
		blddpsx = BlddpsxCode[country.getCurrentItem()];

		return contentView;
	}

	/**
	 * Adapter for countries
	 */
	private class BlddpsxAdapter extends AbstractWheelTextAdapter {

		// Countries names
		private String countries[] = BlddpsxArray;

		/**
		 * Constructor
		 */
		protected BlddpsxAdapter(Context context) {
			super(context, R.layout.w_single_item_layout, NO_RESOURCE);
			setItemTextResource(R.id.wheelcity_country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}

	@SuppressLint("InflateParams")
	private View cshhssx_dialogm() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.wheelcity_single_layout, null);
		final WheelView country = (WheelView) contentView
				.findViewById(R.id.wheelcity_single);
		country.setVisibleItems(1);
		country.setViewAdapter(new CshhssxAdapter(this));
		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				cshhssxstr = CshhssxArray[country.getCurrentItem()];
				cshhssx = CshhssxCode[country.getCurrentItem()];

			}
		});

		country.setCurrentItem(0);

		cshhssxstr = CshhssxArray[country.getCurrentItem()];
		cshhssx = CshhssxCode[country.getCurrentItem()];

		return contentView;
	}

	/**
	 * Adapter for countries
	 */

	private class CshhssxAdapter extends AbstractWheelTextAdapter {

		// Countries names
		private String countries[] = CshhssxArray;

		/**
		 * Constructor
		 */
		protected CshhssxAdapter(Context context) {
			super(context, R.layout.w_single_item_layout, NO_RESOURCE);
			setItemTextResource(R.id.wheelcity_country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}

	@SuppressLint("InflateParams")
	private View dcjcd_dialogm() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.wheelcity_single_layout, null);
		final WheelView country = (WheelView) contentView
				.findViewById(R.id.wheelcity_single);
		country.setVisibleItems(1);
		country.setViewAdapter(new DcjcdAdapter(this));
		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				dcjcdstr = DcjcdArray[country.getCurrentItem()];

				dcjcd = DcjcdCode[country.getCurrentItem()];

			}
		});

		country.setCurrentItem(0);

		dcjcdstr = DcjcdArray[country.getCurrentItem()];

		dcjcd = DcjcdCode[country.getCurrentItem()];

		return contentView;
	}

	/**
	 * Adapter for countries
	 */
	private class DcjcdAdapter extends AbstractWheelTextAdapter {

		// Countries names
		private String countries[] = DcjcdArray;

		/**
		 * Constructor
		 */
		protected DcjcdAdapter(Context context) {
			super(context, R.layout.w_single_item_layout, NO_RESOURCE);
			setItemTextResource(R.id.wheelcity_country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}

}
