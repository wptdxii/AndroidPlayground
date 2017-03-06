package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.iosalertview.MyAlertDialog;
import com.cloudhome.view.wheel.wheelview.JudgeDate;
import com.cloudhome.view.wheel.wheelview.ScreenInfo;
import com.cloudhome.view.wheel.wheelview.WheelMain;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
public class MyBrithActivity extends BaseActivity {

    WheelMain wheelMain;
    String txttime;
    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    List<Map<String, String>> Companylist = new ArrayList<Map<String, String>>();
    private TextView brith_edit;
    private String user_id;
    private String token;
    private String birthday;
    private Button mybrith_submit;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ImageView mybrith_back;

    private RelativeLayout brith_rel;

    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            if (errcode.equals("0")) {

                Editor editor2 = sp2.edit();
                editor2.putString("birthday", birthday);
                editor2.commit();
                finish();
            } else {

                String errmsg = data.get("errmsg");
                Toast.makeText(MyBrithActivity.this, errmsg,
                        Toast.LENGTH_LONG).show();
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

                Toast.makeText(MyBrithActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.mybrith);



        birthday = sp2.getString("birthday", "");

        key_value.put("birthday", birthday);
        Calendar calendar = Calendar.getInstance();
        txttime = calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH);
        init();
        initEvent();

    }

    void init() {
        brith_rel = (RelativeLayout) findViewById(R.id.brith_rel);
        mybrith_back = (ImageView) findViewById(R.id.mybrith_back);
        brith_edit = (TextView) findViewById(R.id.brith_edit);

        mybrith_submit = (Button) findViewById(R.id.mybrith_submit);
    }

    void initEvent() {

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        key_value.put("token", token);
        key_value.put("user_id", user_id);

        if (birthday.equals("") || birthday.equals("null")) {
            brith_edit.setText("请选择出生日期");
        } else {
            brith_edit.setText(birthday);
        }
        brith_rel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                LayoutInflater inflater1 = LayoutInflater
                        .from(MyBrithActivity.this);
                final View timepickerview1 = inflater1.inflate(
                        R.layout.timepicker, null);
                ScreenInfo screenInfo1 = new ScreenInfo(MyBrithActivity.this);
                wheelMain = new WheelMain(timepickerview1);
                wheelMain.screenheight = screenInfo1.getHeight();
                String time1 = txttime;
                Calendar calendar1 = Calendar.getInstance();
                if (JudgeDate.isDate(time1, "yyyy-MM-dd")) {
                    try {
                        calendar1.setTime(dateFormat.parse(time1));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                int year1 = calendar1.get(Calendar.YEAR);
                int month1 = calendar1.get(Calendar.MONTH);
                int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
                wheelMain.initDateTimePicker(year1, month1, day1);
                final MyAlertDialog dialog = new MyAlertDialog(
                        MyBrithActivity.this).builder()
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

                        brith_edit.setText(birthday);
                        key_value.put("birthday", birthday);
                        Toast.makeText(getApplicationContext(),
                                wheelMain.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });
        mybrith_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        mybrith_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (birthday.equals("") || birthday.equals("null")) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            MyBrithActivity.this);

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
                } else {
                    final String PRODUCT_URL = IpConfig.getUri("change_birthday");
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

                            if (jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {
                                // Log.d("44444", jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);

                                String data = jsonObject.getString("data");

                                String errcode = jsonObject.getString("errcode");
                                String errmsg = jsonObject.getString("errmsg");
                                Log.d("44444", data);

                                map.put("errcode", errcode);
                                map.put("errmsg", errmsg);

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
