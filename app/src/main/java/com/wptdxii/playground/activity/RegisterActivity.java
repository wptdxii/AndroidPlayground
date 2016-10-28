package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class RegisterActivity extends BaseActivity {

    private String user_type = "01";

    private EditText reg_input;

    private Button register_in;
    private CheckBox protocol_check;

    private RelativeLayout iv_back;
    private TextView top_title;
    private ImageView iv_right;


    private Map<String, String> key_value = new HashMap<String, String>();
    private TextView protocol_text;
    private String phonenum;
    private boolean agree_flg = false;
    private String message;
    private Dialog load_dialog;
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
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            load_dialog.dismiss();
            if (errcode.equals("0")) {


                String recomend_code = data.get("recomend_code");

                Intent intent = new Intent();


                intent.putExtra("phone", phonenum);
                intent.putExtra("recomend_code", recomend_code);
                intent.putExtra("user_type", user_type);
                //从Activity IntentTest跳转到Activity IntentTest01

                intent.setClass(RegisterActivity.this, RegisterMsgActivity.class);

                //启动intent的Activity

                RegisterActivity.this.startActivity(intent);

//                sp.edit().putString("USER_NAME", phonenum).commit();
//                EventBus.getDefault().post(new LoginEvent());

            } else {
                String errmsg = data.get("errmsg");
                Toast.makeText(RegisterActivity.this, errmsg,
                        Toast.LENGTH_LONG).show();

            }


        }

    };

    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
                .compile("^((17[0-9])|(14[0-9])|(13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);
        init();
        initEvent();

    }

    void init() {
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        top_title.setText("手机快速注册");
        iv_right.setVisibility(View.INVISIBLE);


        load_dialog = new Dialog(this, R.style.progress_dialog);
        load_dialog.setContentView(R.layout.progress_dialog);
        load_dialog.setCancelable(true);
        load_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) load_dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");

        reg_input = (EditText) findViewById(R.id.ed_reg_input);
        register_in = (Button) findViewById(R.id.register_in);
        protocol_check = (CheckBox) findViewById(R.id.protocol_check);

        protocol_text = (TextView) findViewById(R.id.protocol_text);

    }

    void initEvent() {
        iv_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        protocol_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                // TODO Auto-generated method stub

                agree_flg = isChecked;
            }
        });


        protocol_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                //final String url = IpConfig.getUri("register_templates");

                String url = IpConfig.getIp3() + "/templates/steward/service.tpl.php";
                Intent intent = new Intent();

                intent.putExtra("title", "条款");
                intent.putExtra("url", url);

                // 从Activity IntentTest跳转到Activity IntentTest01
                intent.setClass(RegisterActivity.this, JavaScriptActivity.class);
                RegisterActivity.this.startActivity(intent);
            }
        });
        register_in.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                phonenum = reg_input.getText().toString();
                int length = phonenum.length();
                if (user_type.equals("")) {
                    Toast.makeText(RegisterActivity.this, "请选择用户类型", Toast.LENGTH_LONG).show();

                } else if (length != 11) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(RegisterActivity.this);

                    builder.setTitle("提示");
                    builder.setMessage("请检查手机号");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    builder.create().show();
                } else if (!agree_flg) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(RegisterActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请同意保客云集注册协议");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {


                    load_dialog.show();

                    key_value.put("register_mobile", phonenum);

                    final String PRODUCT_URL = IpConfig.getUri("getRecommendationCode");
                    setdata(PRODUCT_URL);
                }


            }
        });

    }



    private void setdata(String url) {


        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
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

}
