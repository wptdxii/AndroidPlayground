package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.MyIncomeAdapter;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MyIncomeActivity extends BaseActivity {


    private Map<String, String> key_value = new HashMap<String, String>();
    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private String user_id;
    private String token;
    private ImageView myincome_back;
    private ListView my_income_list;
    private Dialog dialog;
    private MyIncomeAdapter mAdapter;

    private TextView balance;
    private View view, view3;
    private Handler errcode_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;

            dialog.dismiss();
            String status = data;
            // String errcode = data;
            Log.d("455454", "455445" + status);
            if (status.equals("false")) {

                Toast.makeText(MyIncomeActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };

    private Handler null_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errmsg = data.get("errmsg");
            dialog.dismiss();
            Toast.makeText(MyIncomeActivity.this, errmsg, Toast.LENGTH_SHORT)
                    .show();

        }

    };

    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, Object> total_map = (Map<String, Object>) msg.obj;

            list = (List<Map<String, String>>) total_map.get("detail");

            mAdapter.setData(list);

            my_income_list.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            String total = (String) total_map.get("total");
            balance.setText(total);

            dialog.dismiss();
        }

    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.myincome);


        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");

        init();
        initEvent();

    }

    void init() {

        my_income_list = (ListView) findViewById(R.id.my_income_list);
        myincome_back = (ImageView) findViewById(R.id.myincome_back);
        balance = (TextView) findViewById(R.id.balance);
        view = findViewById(R.id.view);
        view3 = findViewById(R.id.view3);

        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView p_dialog = (TextView) dialog
                .findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");

        mAdapter = new MyIncomeAdapter(MyIncomeActivity.this);

    }

    void initEvent() {
        Log.d("44444", user_id);
        key_value.put("user_id", user_id);
        key_value.put("token", token);

        dialog.show();

        final String url = IpConfig.getUri("getsettle_my_income");
        setlistdata(url);

        my_income_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

                if (list.get(pos).get("code").equals("01")) {
                    Intent intent = new Intent();

                    intent.setClass(MyIncomeActivity.this,
                            Promotion_ExpensesActivity.class);
                    MyIncomeActivity.this.startActivity(intent);

                } else if (list.get(pos).get("code").equals("02")) {
                    Intent intent = new Intent();

                    intent.setClass(MyIncomeActivity.this,
                            ManagementAllowanceActivity.class);
                    MyIncomeActivity.this.startActivity(intent);

                } else if (list.get(pos).get("code").equals("03")) {

                    Intent intent = new Intent();

                    intent.putExtra("type", list.get(pos).get("code"));
                    intent.putExtra("startdate", "");
                    intent.putExtra("enddate", "");
                    intent.setClass(MyIncomeActivity.this,
                            IncomeDetailActivity.class);

                    MyIncomeActivity.this.startActivity(intent);

                } else if (list.get(pos).get("code").equals("04")) {


                    Intent intent = new Intent();

                    intent.putExtra("type", list.get(pos).get("code"));
                    intent.putExtra("startdate", "");
                    intent.putExtra("enddate", "");
                    intent.setClass(MyIncomeActivity.this,
                            IncomeDetailActivity.class);

                    MyIncomeActivity.this.startActivity(intent);

                }

            }
        });

        myincome_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();

            }
        });

    }

    private void setlistdata(String url) {


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

                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

                        Map<String, Object> total_map = new HashMap<String, Object>();
                        Map<String, String> errcode_map = new HashMap<String, String>();
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

                                String errcode = jsonObject.getString("errcode");
                                if (!errcode.equals("0")) {
                                    String errmsg = jsonObject.getString("errmsg");

                                    errcode_map.put("errcode", errcode);
                                    errcode_map.put("errmsg", errmsg);

                                    Message message2 = Message.obtain();

                                    message2.obj = errcode_map;

                                    null_handler.sendMessage(message2);

                                } else {
                                    JSONObject dataObject = jsonObject
                                            .getJSONObject("data");

                                    JSONArray detailList = dataObject
                                            .getJSONArray("detail");

                                    for (int i = 0; i < detailList.length(); i++) {
                                        JSONArray dataList2 = detailList
                                                .getJSONArray(i);

                                        String value1 = dataList2.getString(0);
                                        String value2 = dataList2.getString(1);
                                        String value3 = dataList2.getString(2);

                                        Map<String, String> map = new HashMap<String, String>();

                                        map.put("title", value1);
                                        map.put("code", value2);
                                        map.put("price", value3);
                                        list.add(map);
                                    }

                                    String total = dataObject.getString("total");
                                    total_map.put("total", total);
                                    total_map.put("detail", list);

                                    Message message = Message.obtain();

                                    message.obj = total_map;

                                    handler.sendMessage(message);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


    }


}
