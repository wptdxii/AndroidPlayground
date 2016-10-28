package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ForgetPWActivity extends BaseActivity implements TextWatcher {
    private TimeCount time;
    private Button forget_msg_button;
    private String phonenum;
    private EditText forget_code;
    private EditText forget_password;
    private EditText forget_pw_confirm;
    private EditText forget_phone;
    private Button forget_complete;
    private String passwordstr;

    private RelativeLayout iv_back;
    private TextView top_title;
    private ImageView iv_right;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            Log.d("455454", "455445" + errcode);
            if (errcode.equals("0")) {
                finish();


            } else {
                CustomDialog.Builder builder = new CustomDialog.Builder(ForgetPWActivity.this);
                builder.setTitle("提示");

                builder.setMessage("验证码错误");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        }


    };

    private Handler errcode_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;

            String status = data;
            // String errcode = data;
            Log.d("455454", "455445" + status);
            if (status.equals("false")) {

                Toast.makeText(ForgetPWActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.forget_pw);
        init();
        initEvent();

    }

    void init() {
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        top_title.setText("忘记密码");
        iv_right.setVisibility(View.INVISIBLE);

        time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
        forget_msg_button = (Button) findViewById(R.id.forget_msg_button);
        forget_code = (EditText) findViewById(R.id.forget_code);
        forget_password = (EditText) findViewById(R.id.forget_password);
        forget_pw_confirm = (EditText) findViewById(R.id.forget_pw_confirm);
        forget_complete = (Button) findViewById(R.id.forget_complete);
        forget_phone = (EditText) findViewById(R.id.forget_phone);
//	    forget_code.addTextChangedListener(this);
//		forget_password.addTextChangedListener(this);
//		forget_pw_confirm.addTextChangedListener(this);
//		forget_phone.addTextChangedListener(this);

    }



    void initEvent() {

        iv_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        forget_complete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                phonenum = forget_phone.getText().toString();
                final String forgetcode = forget_code.getText().toString();
                passwordstr = forget_password.getText().toString();
                String pwstrconfirm = forget_pw_confirm.getText().toString();

                int phonelength = phonenum.length();
                int length = forgetcode.length();
                int pwstrlength = passwordstr.length();
                int pwclength = pwstrconfirm.length();
                if (phonelength != 11) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(ForgetPWActivity.this);
                    builder.setTitle("提示");
                    builder.setTitle("提示");
                    builder.setMessage("请检查手机号码");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                } else if (length != 6) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(ForgetPWActivity.this);
                    builder.setTitle("提示");
                    builder.setTitle("提示");
                    builder.setMessage("请检查验证码");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else if (pwstrlength == 0) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(ForgetPWActivity.this);
                    builder.setTitle("提示");
                    builder.setTitle("提示");
                    builder.setMessage("请输入新密码");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else if (pwstrlength < 6 || pwstrlength > 16) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(ForgetPWActivity.this);
                    builder.setTitle("提示");
                    builder.setTitle("提示");
                    builder.setMessage("请输入6 ~ 16位的密码");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else if (pwstrlength != pwclength) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(ForgetPWActivity.this);
                    builder.setTitle("提示");
                    builder.setTitle("提示");
                    builder.setMessage("密码不一致");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else if (!passwordstr.equals(pwstrconfirm)) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            ForgetPWActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("密码不一致");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {
                    String path = IpConfig.getUri("getForgetPassword");

                    OkHttpUtils.post().url(path)
                            .addParams("mobile", phonenum)
                            .addParams("password", passwordstr)
                            .addParams("verification", forgetcode)
                            .build()
                            .execute(new StringCallback() {

                                @Override
                                public void onError(Call call, Exception e) {
                                    Log.e("error", "获取数据异常 ", e);

                                    String status = "false";
                                    Message message = Message.obtain();

                                    message.obj = status;

                                    errcode_handler.sendMessage(message);

                                }

                                @Override
                                public void onResponse(String response) {

                                    Map<String, String> map = new HashMap<String, String>();

                                    String jsonString = response;

                                    Log.d("onSuccess", "onSuccess json = "
                                            + jsonString);

                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        Log.d("post", jsonObject + "");
                                        //String status = jsonObject.getString("status");
                                        String errcode = jsonObject.getString("errcode");

                                        map.put("errcode", errcode);

                                        Message message = Message.obtain();

                                        message.obj = map;

                                        handler.sendMessage(message);


                                    } catch (JSONException e) {
                                        // TODO 自动生成的 catch 块
                                        e.printStackTrace();
                                    }

                                }
                            });


                }
            }
        });

        forget_msg_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                phonenum = forget_phone.getText().toString();

                int phonelength = phonenum.length();

                if (phonelength != 11) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(ForgetPWActivity.this);
                    builder.setTitle("提示");
                    builder.setTitle("提示");
                    builder.setMessage("请检查手机号码");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                } else {


                    String path = IpConfig.getUri("getRegisterCode");

                    OkHttpUtils.post().url(path)
                            .addParams("mobile", phonenum)
                            .addParams("action", "forgetpassword")
                            .build()
                            .execute(new StringCallback() {

                                @Override
                                public void onError(Call call, Exception e) {
                                    Log.e("error", "获取数据异常 ", e);

                                    String status = "false";
                                    Message message = Message.obtain();

                                    message.obj = status;

                                    errcode_handler.sendMessage(message);

                                }

                                @Override
                                public void onResponse(String response) {

                                    Map<String, String> map = new HashMap<String, String>();

                                    String jsonString = response;

                                    Log.d("onSuccess", "onSuccess json = "
                                            + jsonString);

                                    if ("null".equals(jsonString) || "".equals(jsonString)) {
                                        Looper.prepare();
                                        Toast.makeText(
                                                ForgetPWActivity.this,
                                                "获取验证码失败", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                }
                            });


                    time.start();// 开始计时
                    Toast.makeText(ForgetPWActivity.this, "获取中",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(forget_code.getText().toString()) && !TextUtils.isEmpty(forget_password.getText().toString())
                && !TextUtils.isEmpty(forget_pw_confirm.getText().toString()) && !TextUtils.isEmpty(forget_phone.getText().toString())) {
            forget_complete.setEnabled(true);
            forget_complete.setAlpha(1);
        } else {
            forget_complete.setEnabled(false);
            forget_complete.setAlpha((float) 0.5);
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onTick(long millisUntilFinished) {
            forget_msg_button.setClickable(false);
            forget_msg_button.setText("重新获取" + "(" + millisUntilFinished / 1000 + ")");
        }

        @Override
        public void onFinish() {
            forget_msg_button.setText("获取验证码");
            forget_msg_button.setClickable(true);

        }
    }
}
