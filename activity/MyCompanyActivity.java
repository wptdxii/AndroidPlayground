package com.cloudhome.activity;

import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
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
public class MyCompanyActivity extends BaseActivity {

    private static TextView company_edit;
    private static String code;
    private static String name;
    private static Map<String, String> key_value = new HashMap<String, String>();
    static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            Bundle bd = msg.getData();

            Log.d("c_name", bd.getString("c_name"));
            Log.d("c_code", bd.getString("c_code"));

            code = bd.getString("c_code");
            name = bd.getString("c_name");

            company_edit.setText(bd.getString("c_name"));

            key_value.put("company_code", bd.getString("c_code"));

            // menuWindow.dismiss();

        }
    };
    List<Map<String, String>> Companylist = new ArrayList<Map<String, String>>();
    private PopupWindow popupWindow;
    private ListView listViewSpinner;
    private LinearLayout layout;
    private String user_id;
    private String token;
    private C_Type_PopupWindow C_Type_PopupWindow;
    private int posi;
    private Button company_submit;
    private RelativeLayout mycompany_back;

    private RelativeLayout companyname_rel;
    private RelativeLayout m_c_main;
    private Dialog dialog;
    private List<List<String>> companyList = new ArrayList<List<String>>();
    private List<List<String>> company_code_List = new ArrayList<List<String>>();
    private TextView title;
    private RelativeLayout rl_right;
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            if (errcode.equals("0")) {

                Editor editor2 = sp2.edit();
                editor2.putString("company_name", name);
                editor2.putString("company", code);
                editor2.commit();
                finish();
            } else {
                Toast.makeText(MyCompanyActivity.this, "修改公司失败",
                        Toast.LENGTH_LONG).show();
            }
            // m_d_content.setText(content);

        }

    };
    private Handler window_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, Object> data = (Map<String, Object>) msg.obj;


            final String[] c_type = (String[]) data.get("company_type");

            companyList = (List<List<String>>) data.get("name");
            company_code_List = (List<List<String>>) data.get("code");

            companyname_rel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {


                    C_Type_PopupWindow = new C_Type_PopupWindow(MyCompanyActivity.this, c_type, companyList, company_code_List, m_c_main);
                    C_Type_PopupWindow.showAtLocation(m_c_main, Gravity.TOP, 0, 0);
                    //	showPopupWindow();
                }
            });


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

                Toast.makeText(MyCompanyActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }

        }

    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.mycompany);


        name = sp2.getString("company_name", "");
        code = sp2.getString("company", "");


        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");

        key_value.put("company_code", code);
        init();
        initEvent();

    }

    void init() {
        companyname_rel = (RelativeLayout) findViewById(R.id.companyname_rel);
        m_c_main = (RelativeLayout) findViewById(R.id.m_c_main);
        mycompany_back = (RelativeLayout) findViewById(R.id.iv_back);
       title= (TextView) findViewById(R.id.tv_text);
        title.setText("所属公司");
        rl_right= (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        company_edit = (TextView) findViewById(R.id.company_edit);
        if (name.equals("") || name.equals("null")) {
            company_edit.setText("暂未设定");
        } else {
            company_edit.setText(name);
        }
        // company_edit.setText(company_name);
        company_submit = (Button) findViewById(R.id.company_submit);
    }

    void initEvent() {

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        key_value.put("token", token);
        key_value.put("user_id", user_id);

        final String PRODUCT_URL = IpConfig.getUri("getCompanyType");

        initdata(PRODUCT_URL);


        mycompany_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();
            }
        });
        company_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final String PRODUCT_URL = IpConfig.getUri("modifyCompanyCode");
                // new MyTask().execute(PRODUCT_URL);
                setdata(PRODUCT_URL);

            }
        });

    }

    private void initdata(String url) {


        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);
                        dialog.dismiss();
                        Toast.makeText(MyCompanyActivity.this, "网络连接失败，请确认网络连接后重试",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<List<String>> name = new ArrayList<List<String>>();

                        List<List<String>> code = new ArrayList<List<String>>();

                        List<String> company_type = new ArrayList<String>();

                        String jsonString = response;

                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        Map<String, Object> map = new HashMap<String, Object>();

                        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                        try {

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {


                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray dataList = jsonObject.getJSONArray("data");

                                for (int i = 0; i < dataList.length(); i++) {
                                    JSONObject jsonObject2 = dataList.getJSONObject(i);

                                    // 迭代输出json的key作为map的key

                                    Iterator<String> iterator = jsonObject2.keys();
                                    while (iterator.hasNext()) {

                                        String key = iterator.next();


                                        if (key.equals("list")) {

                                            List<String> list1 = new ArrayList<String>();

                                            List<String> list2 = new ArrayList<String>();
                                            JSONArray listvalue = jsonObject2
                                                    .getJSONArray(key);

                                            for (int j = 0; j < listvalue.length(); j++) {
                                                JSONObject jsonObject3 = listvalue
                                                        .getJSONObject(j);

                                                Iterator<String> iterator2 = jsonObject3
                                                        .keys();

                                                while (iterator2.hasNext()) {

                                                    String key2 = iterator2.next();
                                                    if (key2.equals("name")) {
                                                        list1.add(jsonObject3
                                                                .getString("name"));

                                                    } else if (key2.equals("code")) {

                                                        list2.add(jsonObject3
                                                                .getString("code"));

                                                    }

                                                }

                                            }
                                            name.add(list1);
                                            code.add(list2);
                                        }


                                    }

                                    company_type.add(jsonObject2.getString("name"));

                                    String[] c_type = company_type.toArray(new String[company_type.size()]);


                                    map.put("company_type", c_type);
                                    map.put("name", name);
                                    map.put("code", code);

                                    Message message = Message.obtain();

                                    message.obj = map;

                                    window_handler.sendMessage(message);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
                        dialog.dismiss();
                        Toast.makeText(MyCompanyActivity.this, "网络连接失败，请确认网络连接后重试",
                                Toast.LENGTH_SHORT).show();
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
                                String data = jsonObject.getString("data");

                                String errcode = jsonObject.getString("errcode");

                                Log.d("44444", data);

                                map.put("errcode", errcode);

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
