package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
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
import java.util.StringTokenizer;

import okhttp3.Call;

public class MyOldOrderActivity extends BaseActivity implements OnClickListener {


    private String user_id;
    private String token;
    private String user_id_encode;
    private QuoteAdapter listadapter;
    private c_orderAdapter c_o_adapter;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ListView quote_list;
    private ListView c_order_list;
    private Dialog dialog;
    private RadioGroup quote_list_radio, c_order_list_radio;

    private List<Map<String, String>> resultdata = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> quote_listdata = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> c_order_listdata = new ArrayList<Map<String, String>>();
    private TextView tv_text;

    private RelativeLayout iv_back;
    private RelativeLayout rl_right;
    private RelativeLayout quote_list_rel, c_order_list_rel;
    private String toubao_status = "待支付";
    private String quote_status = "01";

    private String order_id = "01";
    private String ordertype = "";
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

                Toast.makeText(MyOldOrderActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };

    @SuppressLint("HandlerLeak")
    private Handler c_o_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;

            c_order_listdata.clear();
            resultdata.clear();


            for (int i = 0; i < list.size(); i++) {

                Map<String, String> map = list.get(i);

                // 如果遍历到的名字包含所输入字符串


                resultdata.add(map);


            }

            c_order_listdata = list;

            c_o_adapter.setData(c_order_listdata);

            c_order_list.requestLayout();
            c_order_list.setAdapter(c_o_adapter);
            c_o_adapter.notifyDataSetChanged();
            dialog.dismiss();
            if (list.size() < 1) {
                Toast.makeText(MyOldOrderActivity.this, "没有找到订单信息",
                        Toast.LENGTH_SHORT).show();
            }


        }

    };

    @SuppressLint("HandlerLeak")
    private Handler c_p_inquiry_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;

            quote_listdata.clear();
            resultdata.clear();
            quote_listdata = list;

            for (int i = 0; i < list.size(); i++) {

                Map<String, String> map = list.get(i);

                // 如果遍历到的名字包含所输入字符串


                resultdata.add(map);


            }

            listadapter.setData(quote_listdata);

            quote_list.setAdapter(listadapter);
            quote_list.requestLayout();
            listadapter.notifyDataSetChanged();
            dialog.dismiss();

            if (list.size() < 1) {
                Toast.makeText(MyOldOrderActivity.this, "没有找到询价信息",
                        Toast.LENGTH_SHORT).show();
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_order);


        Intent intent = getIntent();
        ordertype = intent.getStringExtra("ordertype");
        init();

        if (ordertype.equals("车险")) {


            c_p_order_init();
        } else {

            c_p_inquiry_init();
        }


    }



    void init() {

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");

        key_value.put("user_id", user_id_encode);
        key_value.put("token", token);
        dialog = new Dialog(MyOldOrderActivity.this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");

        listadapter = new QuoteAdapter(MyOldOrderActivity.this);
        c_o_adapter = new c_orderAdapter(MyOldOrderActivity.this);

        quote_list_rel = (RelativeLayout) findViewById(R.id.quote_list_rel);
        quote_list_rel.setVisibility(View.VISIBLE);
        c_order_list_rel = (RelativeLayout) findViewById(R.id.c_order_list_rel);

        quote_list = (ListView) findViewById(R.id.quote_list);
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right=(RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        iv_back.setOnClickListener(this);

        c_order_list = (ListView) findViewById(R.id.c_order_list);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText(ordertype);
        quote_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

                Intent intent = new Intent();
                intent.putExtra("id", listadapter.list.get(pos).get("id"));

                intent.putExtra("quote_status", quote_status);
                intent.setClass(MyOldOrderActivity.this,
                        AccurateQuotationActivity.class);

                MyOldOrderActivity.this.startActivity(intent);

            }
        });

        c_order_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

                if (order_id.equals("01")) {

                    String notice = c_o_adapter.list.get(pos).get("notice");
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            MyOldOrderActivity.this);

                    builder.setTitle("提示");
                    builder.setMessage(notice);
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.create().show();

                } else {
                    Intent intent = new Intent();
                    intent.putExtra("order_id", order_id);
                    intent.putExtra("id", c_o_adapter.list.get(pos).get("id"));
                    intent.putExtra("toubao_status", toubao_status);
                    intent.setClass(MyOldOrderActivity.this,
                            C_P_O_DetailsActivity.class);
                    MyOldOrderActivity.this.startActivity(intent);
                }

            }
        });


    }


    void c_p_inquiry_init() {

        c_order_list_rel.setVisibility(View.GONE);
        quote_list_rel.setVisibility(View.VISIBLE);
        key_value.put("status", "01");


        dialog.show();

        final String orderurl = IpConfig.getUri("getOrderList");
        setinquiryList(orderurl);
        quote_list_radio = (RadioGroup) findViewById(R.id.quote_list_radio);
        quote_list_radio
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup arg0, int checkedId) {

                        switch (checkedId) {

                            case R.id.quote_wait:

                                quote_status = "01";
                                key_value.put("status", "01");
                                dialog.show();

                                setinquiryList(orderurl);

                                break;

                            case R.id.quote_underway:

                                quote_status = "02";

                                dialog.show();

                                key_value.put("status", "02");

                                setinquiryList(orderurl);

                                break;

                            case R.id.quote_complete:

                                quote_status = "03";

                                dialog.show();

                                key_value.put("status", "03");

                                setinquiryList(orderurl);

                                break;
                            case R.id.quote_timed_out:

                                quote_status = "04";

                                dialog.show();

                                key_value.put("status", "04");

                                setinquiryList(orderurl);
                                break;

                            default:
                                break;
                        }
                    }
                });


    }

    void c_p_order_init() {

        dialog.show();

        key_value.put("status", "01");
        quote_list_rel.setVisibility(View.GONE);
        c_order_list_rel.setVisibility(View.VISIBLE);
        final String c_p_url = IpConfig.getUri("getOfferList");
        set_c_order_List(c_p_url);

        c_order_list_radio = (RadioGroup) findViewById(R.id.c_order_list_radio);
        c_order_list_radio
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup arg0, int checkedId) {

                        switch (checkedId) {

                            case R.id.pay_wait:

                                toubao_status = "待支付";
                                order_id = "01";
                                key_value.put("status", "01");
                                dialog.show();

                                set_c_order_List(c_p_url);

                                break;

                            case R.id.pay_complete:

                                toubao_status = "已支付";
                                dialog.show();
                                order_id = "02";
                                key_value.put("status", "02");

                                set_c_order_List(c_p_url);

                                break;

                            case R.id.cover_complete:

                                toubao_status = "已承保";
                                dialog.show();
                                order_id = "03";
                                key_value.put("status", "03");

                                set_c_order_List(c_p_url);

                                break;
                            case R.id.cover_failure:
                                toubao_status = "承保失败";
                                dialog.show();
                                order_id = "04";
                                key_value.put("status", "04");

                                set_c_order_List(c_p_url);
                                break;

                            default:
                                break;
                        }
                    }
                });


    }


    private void setinquiryList(String url) {


        OkHttpUtils.get()//
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
                        try {

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
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

                                c_p_inquiry_handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:

                finish();

                break;


        }

    }

    private void set_c_order_List(String url) {


        OkHttpUtils.get()
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

                            Log.d("44444", jsonString);
                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
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

                                c_o_handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    private class QuoteAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<Map<String, String>> list = null;

        public QuoteAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO 自动生成的方法存根
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO 自动生成的方法存根
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO 自动生成的方法存根
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {

                holder = new ViewHolder();

                convertView = layoutInflater.inflate(R.layout.order_quote_item,
                        null);

                holder.holder_name = (TextView) convertView
                        .findViewById(R.id.holder_name);

                holder.car_msg = (TextView) convertView
                        .findViewById(R.id.car_msg);

                holder.cj_no = (TextView) convertView.findViewById(R.id.cj_no);
                holder.engine_no = (TextView) convertView
                        .findViewById(R.id.engine_no);
                holder.update_time = (TextView) convertView
                        .findViewById(R.id.update_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.holder_name.setText(list.get(position).get("holder_name"));
            holder.cj_no.setText(list.get(position).get("cj_no"));
            holder.engine_no.setText(list.get(position).get("engine_no"));
            holder.car_msg
                    .setText(list.get(position).get("car_msg"));
            holder.update_time.setText(list.get(position).get("update_time"));

            return convertView;
        }

    }

    class ViewHolder {
        TextView holder_name;
        TextView car_msg;
        TextView cj_no;
        TextView engine_no;
        TextView update_time;
    }

    private class c_orderAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<Map<String, String>> list = null;

        public c_orderAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO 自动生成的方法存根
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO 自动生成的方法存根
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO 自动生成的方法存根
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.car_p_order_item, null);
            } else {
                view = convertView;
            }

            TextView show_id = (TextView) view.findViewById(R.id.show_id);
            TextView car_msg = (TextView) view.findViewById(R.id.car_msg);
            TextView insured_name = (TextView) view
                    .findViewById(R.id.insured_name);
            TextView period1 = (TextView) view.findViewById(R.id.period1);
            TextView period2 = (TextView) view.findViewById(R.id.period2);

            TextView period_syx1 = (TextView) view
                    .findViewById(R.id.period_syx1);
            TextView period_syx2 = (TextView) view
                    .findViewById(R.id.period_syx2);

            TextView plan = (TextView) view.findViewById(R.id.plan);
            TextView number = (TextView) view.findViewById(R.id.number);
            TextView paid_total = (TextView) view.findViewById(R.id.paid_total);
            RelativeLayout c_p_o_relat5 = (RelativeLayout) view
                    .findViewById(R.id.c_p_o_relat5);
            RelativeLayout c_p_o_relat6 = (RelativeLayout) view
                    .findViewById(R.id.c_p_o_relat6);

            car_msg.setText(list.get(position).get("car_msg"));
            insured_name.setText(list.get(position).get("insured_name"));

            String period_str = list.get(position).get("period");
            if (period_str.equals("") || period_str.equals("null")) {

                c_p_o_relat5.setVisibility(View.GONE);

            } else {

                c_p_o_relat5.setVisibility(View.VISIBLE);
                int i = 0;
                String split = "至";
                StringTokenizer token = new StringTokenizer(period_str, split);

                String[] period_array = new String[token.countTokens()];

                while (token.hasMoreTokens()) {

                    period_array[i] = token.nextToken();

                    i++;
                }

                if (period_array.length < 2) {
                    period1.setText(period_str);
                } else {
                    period1.setText(period_array[0] + "至");
                    period2.setText(period_array[1]);
                }

            }

            String period_syx_str = list.get(position).get("period");
            if (period_syx_str.equals("") || period_syx_str.equals("null")) {

                c_p_o_relat6.setVisibility(View.GONE);

            } else {
                c_p_o_relat6.setVisibility(View.VISIBLE);
                int j = 0;
                String split = "至";
                StringTokenizer token2 = new StringTokenizer(period_syx_str,
                        split);

                String[] period_syx_array = new String[token2.countTokens()];

                while (token2.hasMoreTokens()) {

                    period_syx_array[j] = token2.nextToken();

                    j++;
                }
                if (period_syx_array.length < 2) {
                    period1.setText(period_syx_str);
                } else {

                    period_syx1.setText(period_syx_array[0] + "至");
                    period_syx2.setText(period_syx_array[1]);
                }

            }


            show_id.setText(list.get(position).get("show_id"));
            plan.setText(list.get(position).get("plan"));
            number.setText(list.get(position).get("number"));
            String money = list.get(position).get("paid_total");
            if (!money.equals("") && !money.equals("null")) {
                paid_total.setText("￥" + money);
            }
            return view;
        }

    }

}
