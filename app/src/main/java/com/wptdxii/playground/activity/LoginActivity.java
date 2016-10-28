package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.utils.HttpMd5;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.utils.MyInterceptor;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class LoginActivity extends BaseActivity {

    Map<String, String> key_value = new HashMap<String, String>();
    private ClearEditText et_username;
    private ClearEditText et_password;
    private Button login_in;
    private Button login_forget;
    private Button login_register;
    private String pwMd5;

    private String ccyj_reference_flag = "";
    private Dialog dialog;

    private RelativeLayout iv_back;
    private TextView tv_title;
    private Button btn_right;


    private String device_id, os_version;

    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            dialog.dismiss();
            String errcode = data.get("errcode");

            Log.d("455454", "455445" + errcode);
            if (errcode.equals("0")) {
                String user_id = data.get("user_id");
                String token = data.get("token");
                String user_id_encode=data.get("user_id_encode");
                String mobile = data.get("mobile");
                String type_code = data.get("type_code");
                String state_code = data.get("state_code");
                String avatar = data.get("avatar");
                String idno = data.get("idno");
                String cert_a = data.get("cert_a");
                String cert_b = data.get("cert_b");
                String licence = data.get("licence");
                String assessment = data.get("assessment");

                String nickname = data.get("nickname");
                String truename = data.get("truename");
                String company_name = data.get("company_name");
                String company = data.get("company");
                String mobile_area = data.get("mobile_area");
                String bank_name = data.get("bank_name");
                String bank_no = data.get("bank_no");
                String refer_name = data.get("refer_name");
                String recomend_code = data.get("recomend_code");
                String sex = data.get("sex");
                String birthday = data.get("birthday");

				/*if (!type_code.equals("02")) {

					String personal_specialty = data.get("personal_specialty");
					String good_count = data.get("good_count");
					String cert_num_isShowFlg = data.get("cert_num_isShowFlg");
					String isShow_in_expertlist = data
							.get("isShow_in_expertlist");
					String mobile_num_short = data.get("mobile_num_short");
					String personal_context = data.get("personal_context");

					Editor editor3 = sp3.edit();
					editor3.putString("personal_specialty", personal_specialty);
					editor3.putString("good_count", good_count);
					editor3.putString("cert_num_isShowFlg", cert_num_isShowFlg);
					editor3.putString("isShow_in_expertlist",
							isShow_in_expertlist);
					editor3.putString("mobile_num_short", mobile_num_short);
					editor3.putString("personal_context", personal_context);
					editor3.commit();

				}*/

                if (state_code.equals("00")) {

                    String personal_specialty = data.get("personal_specialty");
                    String good_count = data.get("good_count");
                    String cert_num_isShowFlg = data.get("cert_num_isShowFlg");
                    String isShow_in_expertlist = data.get("isShow_in_expertlist");
                    String isshow_card = data.get("isshow_card");
                    String mobile_num_short = data.get("mobile_num_short");
                    String personal_context = data.get("personal_context");

                    Editor editor3 = sp3.edit();
                    editor3.putString("personal_specialty", personal_specialty);
                    editor3.putString("good_count", good_count);
                    editor3.putString("cert_num_isShowFlg", cert_num_isShowFlg);
                    editor3.putString("isShow_in_expertlist", isShow_in_expertlist);
                    editor3.putString("isShowMicroCard", isshow_card);
                    editor3.putString("mobile_num_short", mobile_num_short);
                    editor3.putString("personal_context", personal_context);
                    editor3.commit();

                }
                setUserInfo(user_id, sex, birthday, idno, token, mobile,
                        type_code, state_code, avatar, cert_a, cert_b, licence,
                        truename, nickname, company_name, company, mobile_area,
                        bank_name, bank_no, refer_name, recomend_code,
                        ccyj_reference_flag, assessment,user_id_encode);
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG)
                        .show();
                //发送登陆状态改变的广播
                EventBus.getDefault().post(new LoginEvent());

            } else {
                String errmsg = data.get("errmsg");

                CustomDialog.Builder builder = new CustomDialog.Builder(
                        LoginActivity.this);

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

    private static final String TAG = "LoginActivity";
    @SuppressLint("HandlerLeak")
    private Handler device_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            MyApplication.only_key = data.get("only_key");

            Intent backIntent = new Intent();
            setResult(170, backIntent);
            finish();

        }

    };

    @SuppressLint("HandlerLeak")
    private Handler errcode_handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;

            dialog.dismiss();
            String status = data;
            // String errcode = data;
            Log.d("455454", "455445" + status);
            if (status.equals("false")) {

                Toast.makeText(LoginActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_main);


        init();
        initEvent();

    }

    void init() {
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_text);
        btn_right= (Button) findViewById(R.id.btn_right);
        tv_title.setText("登录");
        btn_right.setText("注册");

        et_username = (ClearEditText) findViewById(R.id.et_username);
        String olonum = sp4.getString("old_phone", "");
        et_username.setText(olonum);
        Editable editable = et_username.getText();
        int selEndIndex = Selection.getSelectionEnd(editable);
        selEndIndex = editable.length();
        Selection.setSelection(editable, selEndIndex);


        TelephonyManager tm = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        device_id = tm.getDeviceId() + "";
        if (device_id.equals("null") || device_id.equals("")) {
            device_id = Secure.getString(this.getContentResolver(),
                    Secure.ANDROID_ID);
        }

        os_version = android.os.Build.VERSION.RELEASE;

        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView p_dialog = (TextView) dialog
                .findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");

        et_password = (ClearEditText) findViewById(R.id.et_password);
        login_in = (Button) findViewById(R.id.login_in);
        login_register = (Button) findViewById(R.id.login_register);
        login_forget = (Button) findViewById(R.id.login_forget);
    }

    void initEvent() {

        iv_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        login_in.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                final String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                pwMd5 = HttpMd5.getMD5(password);

                Log.d("77777", pwMd5);
                int phonelength = username.length();
                int pwstrlength = password.length();
                if (phonelength != 11) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            LoginActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请检查手机号码");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else if (pwstrlength < 6) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            LoginActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请输入六位以上密码");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {

                    dialog.show();
                    String url = IpConfig.getUri2("login");
                    Log.i("loginUrl",url);
                    Log.i("loginUrl",username);
                    Log.i("loginUrl",pwMd5);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("mobile", username);
                    params.put("password", pwMd5);


                    OkHttpUtils.post().url(url).params(params).build()
                            .execute(new MyStringCallback());

                }
            }
        });

        login_register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);

            }
        });

        login_forget.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ForgetPWActivity.class);
                LoginActivity.this.startActivity(intent);

            }
        });


    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onError(okhttp3.Call call, Exception e) {

            String status = "false";
            Message message = Message.obtain();

            message.obj = status;

            errcode_handler.sendMessage(message);

        }

        @Override
        public void onResponse(String response) {

            String jsonString = response;
            Log.d("onSuccess", "onSuccess json = " + jsonString);

            Map<String, String> map = new HashMap<String, String>();

            try {

                if (jsonString.equals("") || jsonString.equals("null")) {
                    String status = "false";
                    Message message = Message.obtain();

                    message.obj = status;

                    errcode_handler.sendMessage(message);
                } else {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    // String info =
                    // jsonObject.getString("info");

                    String status = jsonObject.getString("status");

                    Log.d("post", jsonObject + "");

                    String errcode = jsonObject.getString("errcode");

                    if (status.equals("true")) {
                        String data = jsonObject.getString("data");
                        JSONObject dataObject = new JSONObject(data);
                        String sex = dataObject.getString("sex");
                        String birthday = dataObject.getString("birthday");
                        String mobile = dataObject.getString("mobile");
                        String user_id = dataObject.getString("user_id");
                        Log.i("encodeBefore",user_id);
                        String user_id_encode= URLEncoder.encode(user_id);
                        Log.i("encodeAfter",user_id_encode);
                        String token = dataObject.getString("token");
                        String cert_b = dataObject.getString("cert_b");
                        String cert_a = dataObject.getString("cert_a");
                        String licence = dataObject.getString("licence");

//                        String assessment = dataObject.getString("assessment");nexus

                        String assessment="";
                        String type_code = dataObject.getString("type");
                        String nickname = dataObject.getString("nickname");
                        String state_code = dataObject.getString("state");
                        String avatar = dataObject.getString("avatar");
                        String truename = dataObject.getString("name");
                        String idno = dataObject.getString("idno");
                        String company_name = dataObject
                                .getString("company_name");
                        String company = dataObject.getString("company");
                        String mobile_area = dataObject
                                .getString("mobile_area");
                        String bank_name = dataObject.getString("bank_name");
                        String bank_no = dataObject.getString("bank_no");
                        ccyj_reference_flag = dataObject
                                .getString("ccyj_reference_flag");

                        String referral = dataObject.getString("referral");
                        JSONObject referralObject = new JSONObject(referral);
                        String refer_name = referralObject.getString("name");

                        String recomend_code = dataObject
                                .getString("recomend_code");


                        /*if (!type_code.equals("02")) {
							String personal_specialty = dataObject
									.getString("personal_specialty");
							String good_count = dataObject
									.getString("good_count");
							String cert_num_isShowFlg = dataObject
									.getString("cert_num_isShowFlg");
							String isShow_in_expertlist = dataObject
									.getString("isShow_in_expertlist");
							String mobile_num_short = dataObject
									.getString("mobile_num_short");
							String personal_context = dataObject
									.getString("personal_context");

							map.put("personal_specialty", personal_specialty);
							map.put("good_count", good_count);
							map.put("cert_num_isShowFlg", cert_num_isShowFlg);
							map.put("isShow_in_expertlist",
									isShow_in_expertlist);
							map.put("mobile_num_short", mobile_num_short);
							map.put("personal_context", personal_context);
						}*/

                        if (state_code.equals("00")) {
                            String personal_specialty = dataObject
                                    .getString("personal_specialty");
                            String good_count = dataObject
                                    .getString("good_count");
                            String cert_num_isShowFlg = dataObject
                                    .getString("cert_num_isShowFlg");
                            String isShow_in_expertlist = dataObject
                                    .getString("isShow_in_expertlist");
                            String isshow_card = dataObject
                                    .getString("isshow_card");
                            String mobile_num_short = dataObject
                                    .getString("mobile_num_short");
                            String personal_context = dataObject
                                    .getString("personal_context");

                            map.put("personal_specialty", personal_specialty);
                            map.put("good_count", good_count);
                            map.put("cert_num_isShowFlg", cert_num_isShowFlg);
                            map.put("isShow_in_expertlist", isShow_in_expertlist);
                            map.put("isshow_card", isshow_card);
                            map.put("mobile_num_short", mobile_num_short);
                            map.put("personal_context", personal_context);

                        }
                        map.put("errcode", errcode);
                        map.put("mobile", mobile);
                        map.put("user_id", user_id);
                        map.put("user_id_encode",user_id_encode);
                        map.put("token", token);
                        map.put("idno", idno);
                        map.put("type_code", type_code);
                        map.put("state_code", state_code);
                        map.put("avatar", avatar);
                        map.put("cert_a", cert_a);
                        map.put("cert_b", cert_b);
                        map.put("licence", licence);
                        map.put("assessment", assessment);
                        map.put("nickname", nickname);
                        map.put("truename", truename);

                        map.put("sex", sex);
                        map.put("birthday", birthday);

                        map.put("company_name", company_name);
                        map.put("company", company);
                        map.put("mobile_area", mobile_area);
                        map.put("bank_name", bank_name);
                        map.put("bank_no", bank_no);
                        map.put("refer_name", refer_name);
                        map.put("recomend_code", recomend_code);

                        Message message = Message.obtain();

                        message.obj = map;

                        handler.sendMessage(message);
                    } else {

                        String errmsg = jsonObject.getString("errmsg");
                        map.put("errcode", errcode);
                        map.put("errmsg", errmsg);
                        Message message = Message.obtain();

                        message.obj = map;

                        handler.sendMessage(message);

                    }
                }
            } catch (JSONException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }

        }

    }

    private void setUserInfo(String user_id, String sex, String birthday,
                             String idno, String token, String mobile, String type,
                             String state, String avatar, String cert_a, String cert_b,
                             String licence, String truename, String nickname,
                             String company_name, String company, String mobile_area,
                             String bank_name, String bank_no, String refer_name,
                             String recomend_code, String ccyj_reference_flag, String assessment, String user_id_encode) {
        if (null == nickname || "null".equals(nickname) || "".equals(nickname)) {
            nickname = "用户" + user_id;
        }

        Editor editor1 = sp.edit();
        editor1.putString("Login_STATE", "SUCCESS");
        editor1.putString("Login_UID", user_id);
        editor1.putString("Login_TOKEN", token);
        editor1.putString("Login_UID_ENCODE", user_id_encode);

        MyInterceptor.sessionToken = token;

        editor1.putString("Login_TYPE", type);
        editor1.putString("Login_CERT", state);
        editor1.putString("USER_NAME", mobile);
        editor1.putString("idno", idno);
        editor1.putString("pwMd5", pwMd5);
        editor1.putString("avatar", avatar);
        editor1.putString("cert_a", cert_a);
        editor1.putString("cert_b", cert_b);
        editor1.putString("licence", licence);
        editor1.putString("assessment", assessment);
        editor1.putString("truename", truename);
        editor1.putString("nickname", nickname);
        editor1.commit();

        Editor editor2 = sp2.edit();
        editor2.putString("company_name", company_name);
        editor2.putString("company", company);
        editor2.putString("mobile_area", mobile_area);
        editor2.putString("bank_name", bank_name);
        editor2.putString("bank_no", bank_no);
        editor2.putString("refer_name", refer_name);
        editor2.putString("recomend_code", recomend_code);

		
		/*if (!sex.equals("01") || !sex.equals("02")) {
			sex = "01";
		}*/

        editor2.putString("sex", sex);
        editor2.putString("birthday", birthday);
        editor2.putString("ccyj_reference_flag", ccyj_reference_flag);
        editor2.commit();

        Editor editor4 = sp4.edit();

        editor4.putString("old_phone", mobile);
        editor4.putString("setting_g_show", "off");
        editor4.putString("ges_show", "has");
        editor4.putString("gesture_pw", "");

        editor4.commit();
        final String Device_URL = IpConfig.getUri("saveDeviceMsg");
        setDevice_data(Device_URL, user_id,token);


    }


    private void setDevice_data(String url, String user_id, String token) {

        OkHttpUtils.post().url(url)
                .addParams("user_id", user_id)
                .addParams("token",token)
                .addParams("device_id", device_id)
                .addParams("os_version", os_version).build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {


                        finish();

                    }

                    @Override
                    public void onResponse(String response) {

                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = new String(response);
                        Log.d("onmsg", "onmsg json = " + jsonString);

                        try {

                            if (jsonString.equals("") || jsonString.equals("null")) {


                                finish();

                            } else {

                                JSONObject jsonObject = new JSONObject(
                                        jsonString);

                                String status = jsonObject.getString("status");

                                if (status.equals("true")) {

                                    String only_key = jsonObject.getJSONObject(
                                            "data").getString("only_key");

                                    if (only_key == null || only_key.equals("null")) {


                                        only_key = "";
                                    }

                                    map.put("only_key", only_key);
                                    Message message = Message.obtain();
                                    message.obj = map;
                                    device_handler.sendMessage(message);

                                } else {


                                    finish();

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
}
