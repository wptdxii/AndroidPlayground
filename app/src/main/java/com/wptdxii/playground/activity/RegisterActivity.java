package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.CheckCode;
import com.cloudhome.network.GetVerifyCode;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.utils.RegexUtils;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class RegisterActivity extends BaseActivity implements OnClickListener,NetResultListener{
    private String user_type = "01";
    private Button register_in;
    private RelativeLayout iv_back;
    private TextView top_title;
    private Button btn_right;
    private Map<String, String> key_value = new HashMap<String, String>();
    private TextView protocol_text;
    private String phonenum;
    private String message;
    private Dialog load_dialog;
    private boolean isFromLogin=false;

    private ClearEditText ed_reg_input;
    private ClearEditText et_verify_code;
    private TextView tv_get_verify_code;
    private GetVerifyCode getVerifyCode;
    public static final int GET_REGISTER_CODE=1;
    private CheckCode checkCode;
    private static final int CHECK_CODE=2;
    private TimeCount time;
    private Dialog dialog;
    private String code="";
    private String phoneNum1="";

    private Handler errcode_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            String data = (String) msg.obj;
            String status = data;
            if (status.equals("false")) {
                load_dialog.dismiss();
                Toast.makeText(RegisterActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;
            String errcode = data.get("errcode");
            load_dialog.dismiss();
            if (errcode.equals("0")) {
                String recomend_code = data.get("recomend_code");
                Intent intent = new Intent();
                intent.putExtra("phone", phonenum);
                intent.putExtra("recomend_code", recomend_code);
                intent.putExtra("user_type", user_type);
                intent.setClass(RegisterActivity.this, RegisterMsgActivity.class);
                RegisterActivity.this.startActivity(intent);
            } else {
                String errmsg = data.get("errmsg");
                Toast.makeText(RegisterActivity.this, errmsg, Toast.LENGTH_LONG).show();
            }
        }

    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);
        Intent intent=getIntent();
        isFromLogin=intent.getBooleanExtra("isFromLogin",false);
        init();
        initEvent();
    }



    void init() {
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        btn_right = (Button)findViewById(R.id.btn_right);
        top_title.setText("注册");
        btn_right.setText("登录");
        iv_back.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        load_dialog = new Dialog(this, R.style.progress_dialog);
        load_dialog.setContentView(R.layout.progress_dialog);
        load_dialog.setCancelable(true);
        load_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) load_dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
       ed_reg_input= (ClearEditText) findViewById(R.id.ed_reg_input);
        et_verify_code= (ClearEditText) findViewById(R.id.et_verify_code);
        tv_get_verify_code= (TextView) findViewById(R.id.tv_get_verify_code);;
        register_in = (Button) findViewById(R.id.register_in);
        protocol_text = (TextView) findViewById(R.id.protocol_text);
        protocol_text.setOnClickListener(this);
        register_in.setOnClickListener(this);
        tv_get_verify_code.setOnClickListener(this);

    }
    private void initEvent() {
        time = new TimeCount(90000, 1000);// 构造CountDownTimer对象
        getVerifyCode=new GetVerifyCode(this);
        checkCode=new CheckCode(this);
        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
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
                                // Log.d("44444", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONObject data = jsonObject.getJSONObject("data");
                                String errcode = jsonObject.getString("errcode");
                                String errmsg = jsonObject.getString("errmsg");
                                String recomend_code = data.getString("recomend_code");
                                map.put("errcode", errcode);
                                map.put("errmsg", errmsg);
                                map.put("recomend_code", recomend_code);
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


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.protocol_text:
                String url = IpConfig.getIp3() + "/templates/steward/service.tpl.php";
                intent.putExtra("title", "保客云集服务协议");
                intent.putExtra("url", url);
                intent.setClass(RegisterActivity.this, JavaScriptActivity.class);
                RegisterActivity.this.startActivity(intent);
                break;
            case R.id.register_in:
//                intent.setClass(RegisterActivity.this, RegisterPasswordActivity.class);
//                startActivity(intent);
//                phonenum = reg_input.getText().toString();
//                int length = phonenum.length();
//                if (user_type.equals("")) {
//                    Toast.makeText(RegisterActivity.this, "请选择用户类型", Toast.LENGTH_LONG).show();
//                } else if (length != 11) {
//                    CustomDialog.Builder builder = new CustomDialog.Builder(RegisterActivity.this);
//                    builder.setTitle("提示");
//                    builder.setMessage("请检查手机号");
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    builder.create().show();
//                } else {
//                    load_dialog.show();
//                    key_value.put("register_mobile", phonenum);
//                    final String PRODUCT_URL = IpConfig.getUri("getRecommendationCode");
//                    setdata(PRODUCT_URL);
//                }
                phoneNum1=ed_reg_input.getText().toString().trim();
                 code=et_verify_code.getText().toString().trim();
                if (!RegexUtils.isMobileNO(phoneNum1)) {
                    showPromptDialog("请检查手机号码");
                } else if (code.length() < 6) {
                    showPromptDialog("请检查验证码");
                }else {
                    dialog.show();
                    checkCode.execute("quick_register",phoneNum1,code,CHECK_CODE);
                }
                break;
            case R.id.btn_right:
                if(isFromLogin){
                    finish();
                }else{
                    intent.setClass(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("isFromRegister",true);
                    startActivity(intent);
                }
                break;
            case R.id.tv_get_verify_code:
                String phoneNum=ed_reg_input.getText().toString().trim();
                if(RegexUtils.isMobileNO(phoneNum)){
                    getVerifyCode.execute("quick_register",phoneNum,"false",GET_REGISTER_CODE);
                }else{
                    showPromptDialog("请检查手机号码");
                }
                break;
        }
    }

    private void showPromptDialog(String message){
        CustomDialog.Builder builder = new CustomDialog.Builder(RegisterActivity.this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch(action){
            case GET_REGISTER_CODE:
                if (flag == MyApplication.DATA_OK) {
                    String codeMsg=dataObj.toString();
                    Toast.makeText(RegisterActivity.this, codeMsg, Toast.LENGTH_SHORT).show();
                    time.start();
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(RegisterActivity.this, "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                    String codeMsg=dataObj.toString();
                    showPromptDialog(codeMsg);
                }
                break;
            case CHECK_CODE:
               dialog.dismiss();
                if (flag == MyApplication.DATA_OK) {
                   Intent intent=new Intent(RegisterActivity.this,RegisterPasswordActivity.class);
                    intent.putExtra("code",code);
                    intent.putExtra("mobile",phoneNum1);
                    startActivity(intent);
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(RegisterActivity.this, "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                    String checkMsg=dataObj.toString();
                    showPromptDialog(checkMsg);
                }
                break;
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onTick(long millisUntilFinished) {
            tv_get_verify_code.setClickable(false);
            tv_get_verify_code.setText("重新获取" + "(" + millisUntilFinished / 1000+ ")");
            tv_get_verify_code.setTextColor(getResources().getColor(R.color.color9));
        }
        @Override
        public void onFinish() {
            tv_get_verify_code.setText("获取验证码");
            tv_get_verify_code.setClickable(true);
            tv_get_verify_code.setTextColor(getResources().getColor(R.color.title_blue));
        }
    }
}
