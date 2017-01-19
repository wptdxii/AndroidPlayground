package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.HavebeenpaidAdapter;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.xlistview.XListView.IXListViewListener;
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

public class HavebeenpaidActivity extends BaseActivity implements
        IXListViewListener {


    private Map<String, String> key_value = new HashMap<String, String>();
    private String user_id;
    private String token;
    private Dialog dialog;
    private RadioGroup radio_group;
    private ClearEditText my_orders_search;
    private ImageView search_img;
    private RelativeLayout h_b_p_back;
    private RelativeLayout rl_right;
    private TextView tv_text;
    private XListView to_be_paid_list;
    private HavebeenpaidAdapter adapter;
    private Handler mHandler;
    List<Map<String, String>> search_data = new ArrayList<Map<String, String>>();
    List<Map<String, String>> total_data = new ArrayList<Map<String, String>>();
    private int pagenum = 0;
    private String RBstr = "01";

    private String search_show = "false";
    public static HavebeenpaidActivity Havebeenpaidinstance = null;

    @SuppressLint("HandlerLeak")
    private Handler errcode_handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;
            dialog.dismiss();
            String status = data;

            Log.d("455454", "455445" + status);
            if (status.equals("false")) {

                Toast.makeText(HavebeenpaidActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;

            dialog.dismiss();
            if (search_show.equals("false")) {
                total_data.addAll(list);

                if (pagenum == 0) {

                    adapter.setData(total_data);
                    to_be_paid_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    if (list.size() < 1) {

                        Toast.makeText(HavebeenpaidActivity.this, "没有找到订单信息",
                                Toast.LENGTH_SHORT).show();

                    }
                } else if (list.size() < 1) {

                    Toast.makeText(HavebeenpaidActivity.this, "没有找到订单信息",
                            Toast.LENGTH_SHORT).show();
                    to_be_paid_list.stopLoadMore();
                } else {

                    adapter.notifyDataSetChanged();
                    to_be_paid_list.stopLoadMore();

                }
            } else {

                search_data.addAll(list);

                if (pagenum == 0) {

                    adapter.setData(list);
                    to_be_paid_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    if (list.size() < 1) {

                        Toast.makeText(HavebeenpaidActivity.this, "没有找到订单信息",
                                Toast.LENGTH_SHORT).show();

                    }
                } else if (list.size() < 1) {

                    Toast.makeText(HavebeenpaidActivity.this, "没有找到订单信息",
                            Toast.LENGTH_SHORT).show();
                    to_be_paid_list.stopLoadMore();
                } else {
                    adapter.notifyDataSetChanged();
                    to_be_paid_list.stopLoadMore();

                }

            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.have_been_paid);


        Havebeenpaidinstance = this;

        init();
        initEvent();

    }

    void init() {

        key_value.put("page", "0");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");

        key_value.put("user_id", user_id);
        key_value.put("token", token);

        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView p_dialog = (TextView) dialog
                .findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");

        adapter = new HavebeenpaidAdapter(HavebeenpaidActivity.this);

        radio_group = (RadioGroup) findViewById(R.id.radio_group);

        to_be_paid_list = (XListView) findViewById(R.id.to_be_paid_list);
        to_be_paid_list.setPullLoadEnable(true);
        to_be_paid_list.setXListViewListener(HavebeenpaidActivity.this);

        search_img = (ImageView) findViewById(R.id.search_img);
        h_b_p_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_text.setText("已支付订单");
        h_b_p_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                HavebeenpaidActivity.this.finish();

            }
        });

        my_orders_search = (ClearEditText) findViewById(R.id.my_orders_search);

        my_orders_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                int len = s.toString().length();
                if (len == 0 && search_show.equals("ture")) {

                    adapter.setData(total_data);
                    to_be_paid_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    to_be_paid_list.stopLoadMore();
                    search_show = "false";
                }

            }
        });

        mHandler = new Handler();
        closeInputMethod();
    }

    void initEvent() {

        final String orderurl = IpConfig.getUri("getRecOrderList");

        key_value.put("status", "03");

        dialog.show();

        setRecOrderList(orderurl);
        radio_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {

                switch (checkedId) {

                    case R.id.has_insured_radio:

                        pagenum = 0;
                        RBstr = "01";
                        key_value.put("status", "03");
                        key_value.put("page", "0");
                        dialog.show();
                        search_show = "false";
                        key_value.put("key", "");
                        total_data.clear();
                        adapter.notifyDataSetChanged();
                        setRecOrderList(orderurl);

                        break;
                    case R.id.not_insurance_radio:

                        pagenum = 0;
                        RBstr = "02";
                        dialog.show();
                        search_show = "false";
                        key_value.put("page", "0");
                        key_value.put("key", "");
                        key_value.put("status", "04");
                        total_data.clear();
                        adapter.notifyDataSetChanged();
                        setRecOrderList(orderurl);

                        break;
                    case R.id.has_expired_radio:
                        pagenum = 0;
                        RBstr = "03";
                        dialog.show();
                        search_show = "false";
                        key_value.put("page", "0");
                        key_value.put("key", "");
                        key_value.put("status", "05");
                        total_data.clear();
                        adapter.notifyDataSetChanged();
                        setRecOrderList(orderurl);

                        break;
                    default:
                        break;
                }
            }
        });

        search_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String searchstr = my_orders_search.getText().toString();
                if (searchstr.equals("null") || searchstr.equals("")) {
                    Toast.makeText(HavebeenpaidActivity.this, "请输入投保人姓名、被保人姓名",
                            Toast.LENGTH_SHORT).show();
                } else {

                    pagenum = 0;
                    search_show = "ture";

                    search_data.clear();

                    key_value.put("key", searchstr);
                    key_value.put("page", "0");
                    final String url = IpConfig.getUri("getRecOrderList");
                    dialog.show();
                    setRecOrderList(url);

                }
            }
        });

        to_be_paid_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                int pos = position - 1;
                if (pos >= 0 && pos < adapter.list.size()) {
                    Intent intent = new Intent();
                    intent.putExtra("id", adapter.list.get(pos).get("id"));
                    intent.putExtra("RBstr", RBstr);
                    intent.setClass(HavebeenpaidActivity.this,
                            Havebeenpaid_O_InfoActivity.class);
                    HavebeenpaidActivity.this.startActivity(intent);
                }
            }
        });

    }

    private void setRecOrderList(String url) {


        OkHttpUtils.post()
                .url(url)
                .params(key_value)
                .build()
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
                        try {

                            if (jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray dataList = jsonObject
                                        .getJSONArray("data");

                                for (int i = 0; i < dataList.length(); i++) {
                                    JSONObject jsonObject2 = dataList
                                            .getJSONObject(i);
                                    Map<String, String> map = new HashMap<String, String>();

                                    Iterator<String> iterator = jsonObject2.keys();
                                    while (iterator.hasNext()) {

                                        String key = iterator.next();
                                        String value = jsonObject2.getString(key);
                                        map.put(key, value);

                                    }
                                    list.add(map);
                                }

                                Message message = Message.obtain();

                                message.obj = list;

                                handler.sendMessage(message);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0,
            // InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(my_orders_search.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void getRefreshItem() {

        pagenum = 0;

        if (search_show.equals("false")) {
            total_data.clear();
        } else {
            search_data.clear();
        }
        key_value.put("page", "0");

        final String url = IpConfig.getUri("getRecOrderList");

        setRecOrderList(url);

    }

    private void getLoadMoreItem() {
        pagenum++;
        Log.d("555555", pagenum + "");
        key_value.put("page", pagenum + "");
        final String url = IpConfig.getUri("getRecOrderList");

        setRecOrderList(url);
    }

    @Override
    public void onRefresh() {

        mHandler.postDelayed(new Runnable() {
            public void run() {

                to_be_paid_list.stopLoadMore();
                getRefreshItem();

                adapter.notifyDataSetChanged();

                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {

        mHandler.postDelayed(new Runnable() {
            public void run() {

                to_be_paid_list.stopRefresh();
                getLoadMoreItem();

                // adapter.notifyDataSetChanged();

                // mListView.stopLoadMore();

            }
        }, 2000);

    }

    private void onLoad() {

        to_be_paid_list.stopRefresh();

        to_be_paid_list.stopLoadMore();

        to_be_paid_list.setRefreshTime("刚刚");

    }

}
