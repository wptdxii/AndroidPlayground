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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.A_I_IAdapter;
import com.cloudhome.adapter.C_P_CompanyAdapter;
import com.cloudhome.adapter.C_P_CompanyAdapter.ViewHolder;
import com.cloudhome.bean.A_I_I_Data;
import com.cloudhome.bean.C_P_C_Data;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.MyGridView;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class CheXianTBActivity extends BaseActivity {

    public static CheXianTBActivity CheXianTB_instance = null;

    List<String> listItemID = new ArrayList<String>();
    List<String> listItemName = new ArrayList<String>();
    private RelativeLayout xunjia_back;
    private RelativeLayout rl_right;
    private TextView tv_text;
    private MyGridView gd;
    private A_I_IAdapter mAdapter;
    private ArrayList<A_I_I_Data> persons;
    private Dialog dialog;
    private Map<String, String> key_value = new HashMap<String, String>();
    private String user_id;
    private String token;
    private Handler list_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {

            List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;

            dialog.dismiss();
            A_I_I_Data mPerson;

            for (int i = 0; i < list.size(); i++) {

                mPerson = new A_I_I_Data();
                mPerson.setName(list.get(i).get("name"));
                mPerson.setUrl(list.get(i).get("url"));
                mPerson.setImg(list.get(i).get("img"));
                mPerson.setAlert_msg(list.get(i).get("alert_msg"));
                persons.add(mPerson);

            }
            mAdapter = new A_I_IAdapter(persons, CheXianTBActivity.this);
            gd.setAdapter(mAdapter);

        }

    };
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

                Toast.makeText(CheXianTBActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }

        }

    };
    private MyGridView company_name_gd;
    private C_P_CompanyAdapter c_name_Adapter;
    private ArrayList<C_P_C_Data> c_name_p;
    private Button company_submit;
    private ImageView c_p_company_back;
    private int checkNum = 0;
    private Handler c_name_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {

            List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;

            dialog.dismiss();
            C_P_C_Data mPerson;
            for (int i = 0; i < list.size(); i++) {
                mPerson = new C_P_C_Data();
                mPerson.setName(list.get(i).get("company_name"));
                mPerson.setId(list.get(i).get("company_id"));
                c_name_p.add(mPerson);
            }

            c_name_Adapter = new C_P_CompanyAdapter(c_name_p,
                    CheXianTBActivity.this);

            company_name_gd.setAdapter(c_name_Adapter);

            company_submit.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    String c_name;
                    String[] c_id;
                    if (listItemID.size() == 0) {

                        CustomDialog.Builder builder = new CustomDialog.Builder(
                                CheXianTBActivity.this);

                        builder.setTitle("提示");
                        builder.setMessage("请选择承保公司");
                        builder.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        dialog.dismiss();

                                    }
                                });

                        builder.create().show();

                    } else {
                        StringBuilder sb = new StringBuilder();

                        c_id = new String[listItemID.size()];
                        for (int i = 0; i < listItemID.size(); i++) {

                            sb.append(listItemName.get(i)).append("，");
                            c_id[i] = listItemID.get(i);
                        }

                        c_name = sb.substring(0, sb.length() - 1);

                        Intent intent = new Intent();

                        intent.putExtra("company_name", c_name);
                        intent.putExtra("company_id", c_id);

                        intent.setClass(CheXianTBActivity.this,
                                CheXianBJActivity.class);

                        CheXianTBActivity.this.startActivity(intent);

                    }

                }
            });
        }

    };

    private Handler null_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errmsg = data.get("errmsg");

            CustomDialog.Builder builder = new CustomDialog.Builder(
                    CheXianTBActivity.this);
            builder.setTitle("提示");
            builder.setMessage(errmsg);
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
            builder.create().show();

        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chexian_x_j);
        CheXianTB_instance = this;
        init();
        initEvent();
    }

    void init() {
        xunjia_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right=(RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("车险投保");
        gd = (MyGridView) findViewById(R.id.x_j_gd);
        persons = new ArrayList<A_I_I_Data>();
        company_name_gd = (MyGridView) findViewById(R.id.c_name_gd);
        company_submit = (Button) findViewById(R.id.company_submit);
        c_p_company_back = (ImageView) findViewById(R.id.c_p_company_back);

        c_name_p = new ArrayList<C_P_C_Data>();
        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView p_dialog = (TextView) dialog
                .findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");

    }



    void initEvent() {

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");


        key_value.put("token", token);
        key_value.put("user_id", user_id);

        dialog.show();
        final String PRODUCT_URL = IpConfig
                .getUri("getAutoInsuranceInquiryApi");

        setdata(PRODUCT_URL);

        xunjia_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();

            }
        });

        gd.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

                String alert_msg = persons.get(pos).getAlert_msg();
                if (alert_msg.equals("") || alert_msg.equals("null")) {
                    Intent intent = new Intent();

                    // intent.setClass(MainActivity.this,
                    // ShopGuideActivity.class);
                    // intent.putExtra("id", abc);
                    // 启动intent的Activity
                    // MainActivity.this.startActivity(intent);
                    intent.putExtra("title", persons.get(pos).getName());
                    intent.putExtra("url", persons.get(pos).getUrl());

                    // 从Activity IntentTest跳转到Activity IntentTest01
                    intent.setClass(CheXianTBActivity.this,
                            CXTBWebActivity.class);
                    CheXianTBActivity.this.startActivity(intent);

                } else {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            CheXianTBActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage(alert_msg);
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
        });

        company_name_gd.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

                Log.d("88888", "7777");

                ViewHolder holder = (ViewHolder) arg1.getTag();

                if (checkNum < 3 || holder.cb.isChecked()) {
                    holder.cb.toggle();

                    C_P_CompanyAdapter.getIsSelected().put(pos,
                            holder.cb.isChecked());

                    if (holder.cb.isChecked()) {

                        holder.name.setTextColor(getResources().getColor(
                                R.color.orange_red));

                        holder.name
                                .setBackgroundResource(R.drawable.company_checkbox_pressed);

                        checkNum++;
                    } else {
                        holder.name.setTextColor(getResources().getColor(
                                R.color.black));
                        holder.name
                                .setBackgroundResource(R.drawable.company_checkbox_normal);
                        checkNum--;
                    }

                    listItemID.clear();
                    listItemName.clear();

                    for (int i = 0; i < C_P_CompanyAdapter.getIsSelected().size(); i++) {
                        if (C_P_CompanyAdapter.getIsSelected().get(i)) {
                            listItemID.add(c_name_p.get(i).getId());
                            listItemName.add(c_name_p.get(i).getName());
                        }
                    }

                } else {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            CheXianTBActivity.this);

                    builder.setTitle("提示");
                    builder.setMessage("最多选3家承保公司");
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
        });

        final String URL = IpConfig.getUri("getCompanyListForAuto");

        set_c_namedata(URL);
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

                        dialog.dismiss();
                        Toast.makeText(CheXianTBActivity.this,
                                "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onResponse(String response) {

                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                        try {

                            if (jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {

                                JSONObject jsonObject = new JSONObject(
                                        jsonString);

                                String errcode = jsonObject
                                        .getString("errcode");
                                if (!errcode.equals("0")) {
                                    String errmsg = jsonObject
                                            .getString("errmsg");

                                    Map<String, String> errcode_map = new HashMap<String, String>();
                                    errcode_map.put("errcode", errcode);
                                    errcode_map.put("errmsg", errmsg);

                                    Message message2 = Message.obtain();

                                    message2.obj = errcode_map;

                                    null_handler.sendMessage(message2);

                                } else {

                                    JSONArray dataList = jsonObject
                                            .getJSONArray("data");

                                    for (int i = 0; i < dataList.length(); i++) {
                                        JSONObject jsonObject2 = dataList
                                                .getJSONObject(i);
                                        Map<String, String> map = new HashMap<String, String>();
                                        Iterator<String> iterator = jsonObject2
                                                .keys();
                                        while (iterator.hasNext()) {
                                            String key = iterator.next();
                                            String value = jsonObject2
                                                    .getString(key);
                                            map.put(key, value);
                                        }
                                        list.add(map);
                                    }
                                    Message message = Message.obtain();

                                    message.obj = list;

                                    list_handler.sendMessage(message);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    private void set_c_namedata(String url) {

        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {

                        Log.e("error", "获取数据异常 ", e);

                        dialog.dismiss();
                        Toast.makeText(CheXianTBActivity.this,
                                "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onResponse(String response) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                        try {

                            if (jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {

                                JSONObject jsonObject = new JSONObject(
                                        jsonString);
                                JSONObject dataObject = jsonObject
                                        .getJSONObject("data");

                                // 迭代输出json的key作为map的key

                                Iterator<String> iterator = dataObject.keys();

                                while (iterator.hasNext()) {

                                    Map<String, String> map = new HashMap<String, String>();
                                    String key = iterator.next();
                                    String value = dataObject.getString(key);

                                    Log.d("8888", key);
                                    Log.d("77777", value);
                                    map.put("company_id", key);
                                    map.put("company_name", value);
                                    list.add(map);
                                }

                                Message message = Message.obtain();

                                message.obj = list;

                                c_name_handler.sendMessage(message);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

}