package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.xlistview.XListView.IXListViewListener;
import com.gghl.view.wheelcity.AddressData;
import com.gghl.view.wheelcity.OnWheelChangedListener;
import com.gghl.view.wheelcity.WheelView;
import com.gghl.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.gghl.view.wheelcity.adapters.ArrayWheelAdapter;
import com.zf.iosdialog.widget.MyAlertDialog;
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

public class MoreExpertActivity extends BaseActivity implements IXListViewListener {


    private MyAdapter adapter;
    private RadioGroup more_radio_select;
    private Map<String, String> key_value = new HashMap<String, String>();
    private List<Map<String, Object>> resultdata = new ArrayList<Map<String, Object>>();
    private Dialog dialog;
    private XListView mListView;
    private TextView city_select;
    private RelativeLayout city_select_rel;
    private RelativeLayout more_back;
    private RelativeLayout rl_right;
    private TextView tv_text;
    private ImageView top_arrow;
    private int pagenum = 1;
    private String R_button_select = "1";
    private Handler mHandler;
    private String areaing;
    private String loginString;
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;

            resultdata.addAll(list);

            if (pagenum == 1) {
                adapter.setData(resultdata);
                mListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                adapter.notifyDataSetChanged();
                mListView.stopLoadMore();
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.more_expert);

        loginString = sp.getString("Login_STATE", "none");
        init();
        initEvent();

    }

    @Override
    protected void onRestart() {
        super.onRestart();


        getRefreshItem();
        loginString = sp.getString("Login_STATE", "none");
        adapter.notifyDataSetChanged();
        mListView.stopLoadMore();
        onLoad();


    }

    void init() {
        more_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right= (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("保险专家");
        city_select_rel = (RelativeLayout) findViewById(R.id.city_select_rel);
        city_select = (TextView) findViewById(R.id.city_select);
        more_radio_select = (RadioGroup) findViewById(R.id.more_radio_select);
        mListView = (XListView) findViewById(R.id.more_expert_xlist);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(MoreExpertActivity.this);

        mHandler = new Handler();
        adapter = new MyAdapter(MoreExpertActivity.this);


        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");

        top_arrow = (ImageView) findViewById(R.id.top_arrow);


    }

    void initEvent() {

        key_value.put("orderType", "01");
        key_value.put("page", "1");

        final String PRODUCT_URL = IpConfig.getUri("getExpertList");
        // new MyTask().execute(PRODUCT_URL);
        setRadio(PRODUCT_URL);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                int pos = position - 1;

                if (loginString.equals("none")) {

                    Intent intent = new Intent();
                    intent.setClass(MoreExpertActivity.this, LoginActivity.class);
                    MoreExpertActivity.this.startActivity(intent);

                } else if (pos >= 0 && pos < adapter.list.size()) {
                    Intent intent = new Intent();

                    intent.putExtra("id", adapter.list.get(pos).get("id")
                            .toString());
                    intent.putExtra("avatar",
                            adapter.list.get(pos).get("avatar").toString());
                    intent.putExtra("user_name",
                            adapter.list.get(pos).get("user_name").toString());
                    intent.putExtra("company_name",
                            adapter.list.get(pos).get("company_name")
                                    .toString());
                    intent.putExtra("mobile_area",
                            adapter.list.get(pos).get("mobile_area")
                                    .toString());
                    intent.putExtra("good_count",
                            adapter.list.get(pos).get("good_count").toString());
                    intent.putExtra("cert_a",
                            adapter.list.get(pos).get("cert_a").toString());
                    intent.putExtra("cert_b",
                            adapter.list.get(pos).get("cert_b").toString());

                    intent.putExtra("licence",
                            adapter.list.get(pos).get("licence")
                                    .toString());

                    intent.putExtra("mobile",
                            adapter.list.get(pos).get("mobile").toString());

                    intent.putExtra("cert_num_isShowFlg", adapter.list
                            .get(pos).get("cert_num_isShowFlg").toString());

                    intent.putExtra("mobile_num_short", adapter.list.get(pos)
                            .get("mobile_num_short").toString());

                    intent.putExtra("state", adapter.list.get(pos)
                            .get("state").toString());

                    intent.putExtra("personal_specialty", adapter.list
                            .get(pos).get("personal_specialty").toString());
                    intent.putExtra("personal_context", adapter.list.get(pos)
                            .get("personal_context").toString());

                    intent.setClass(MoreExpertActivity.this,
                            ExpertMicroActivity.class);

                    MoreExpertActivity.this.startActivity(intent);
                }
            }
        });

        more_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        more_radio_select
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @SuppressWarnings("null")
                    @SuppressLint("ShowToast")
                    @Override
                    public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                        // TODO Auto-generated method stub
                        switch (checkedId) {
                            case R.id.radio_select_recommend:

                                key_value.put("orderType", "01");
                                setListview();
                                break;

                            case R.id.radio_select_join:
                                key_value.put("orderType", "02");

                                setListview();

                                break;
                            case R.id.radio_select_evaluat:
                                key_value.put("orderType", "03");

                                setListview();

                                break;

                            default:
                                break;
                        }

                    }

                    private void elseif(boolean b) {
                        // TODO Auto-generated method stub

                    }
                });

        city_select_rel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                View view = dialogm();
                final MyAlertDialog dialog = new MyAlertDialog(
                        MoreExpertActivity.this).builder()
                        .setTitle("请选择地区")
                        .setView(view)
                        .setNegativeButton("取消", new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                dialog.setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (areaing.equals("")) {
                            city_select.setText("全国");
                        } else {
                            city_select.setText(areaing);
                        }
                        key_value.put("city", areaing);

                        setListview();

                    }
                });
                dialog.show();


            }
        });
        top_arrow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                setListViewPos(0);
            }
        });
    }

    /**
     * 滚动ListView到指定位置
     *
     * @param pos
     */
    private void setListViewPos(int pos) {
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            mListView.smoothScrollToPosition(pos);
        } else {
            mListView.setSelection(pos);
        }
    }

    private void setRadio(String url) {

        if (pagenum == 1) {
            dialog.show();
        }


        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        //	Log.d("onSuccess", "onSuccess json = " + jsonString);
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        try {

                            Log.d("44444", jsonString);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            JSONArray dataList = jsonObject.getJSONArray("data");

                            for (int i = 0; i < dataList.length(); i++) {
                                JSONObject jsonObject2 = dataList.getJSONObject(i);
                                Map<String, Object> map = new HashMap<String, Object>();
                                // 迭代输出json的key作为map的key

                                Iterator<String> iterator = jsonObject2.keys();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    Object value = jsonObject2.get(key);
                                    map.put(key, value);
                                }
                                list.add(map);
                            }
                            Message message = Message.obtain();

                            message.obj = list;

                            handler.sendMessage(message);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    private void setListview() {
        pagenum = 1;
        resultdata.clear();
        // /////// key_value.clear();

        key_value.put("page", "1");

        final String PRODUCT_URL = IpConfig.getUri("getExpertList");
        // new MyTask().execute(PRODUCT_URL);
        setRadio(PRODUCT_URL);
    }

    private void getRefreshItem() {
        Log.d("444444", pagenum + "");
        pagenum = 1;
        resultdata.clear();

        key_value.put("page", "1");

        final String PRODUCT_URL;

        PRODUCT_URL = IpConfig.getUri("getExpertList");

        // new MyTask().execute(PRODUCT_URL);
        setRadio(PRODUCT_URL);

    }

    private void getLoadMoreItem() {
        pagenum++;
        Log.d("555555", pagenum + "");

        key_value.put("page", pagenum + "");
        final String PRODUCT_URL;

        PRODUCT_URL = IpConfig.getUri("getExpertList");

        // new MyTask().execute(PRODUCT_URL);
        setRadio(PRODUCT_URL);
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            public void run() {


                mListView.stopLoadMore();
                getRefreshItem();

                adapter.notifyDataSetChanged();

                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            public void run() {


                mListView.stopRefresh();
                getLoadMoreItem();

                adapter.notifyDataSetChanged();

                mListView.stopLoadMore();

            }
        }, 2000);
    }

    protected void resetViewHolder(ViewHolder p_ViewHolder) {
        p_ViewHolder.username.setText(null);
        p_ViewHolder.company_name.setText(null);
        p_ViewHolder.mobile_area.setText(null);
        p_ViewHolder.imageView.setImageResource(R.drawable.expert_head);
        p_ViewHolder.like_num.setText(null);
    }

    private void onLoad() {

        mListView.stopRefresh();

        mListView.stopLoadMore();

        mListView.setRefreshTime("刚刚");

    }



    @Override
    protected void onStop() {
        super.onStop();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @SuppressLint("InflateParams")
    private View dialogm() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_country);
        country.setVisibleItems(1);
        country.setViewAdapter(new CountryAdapter(this));

        final String cities[][] = AddressData.CITIES2;
        final WheelView city = (WheelView) contentView
                .findViewById(R.id.wheelcity_city);
        city.setVisibleItems(1);

        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateCities(city, cities, newValue);

                String province = AddressData.PROVINCES2[country
                        .getCurrentItem()];
                if (province.equals("北京") || province.equals("上海")
                        || province.equals("天津") || province.equals("重庆")) {
                    areaing = province;
                } else if (province.equals("全国")) {
                    areaing = "";
                } else {
                    areaing = AddressData.CITIES2[country.getCurrentItem()][city
                            .getCurrentItem()];
                }

            }
        });

        city.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String province = AddressData.PROVINCES2[country
                        .getCurrentItem()];
                if (province.equals("北京") || province.equals("上海")
                        || province.equals("天津") || province.equals("重庆")) {
                    areaing = province;
                } else if (province.equals("全国")) {
                    areaing = "";
                } else {
                    areaing = AddressData.CITIES2[country.getCurrentItem()][city
                            .getCurrentItem()];
                }

            }
        });

        country.setCurrentItem(0);

        city.setCurrentItem(0);
        updateCities(city, cities, 0);

        String province = AddressData.PROVINCES2[country.getCurrentItem()];
        if (province.equals("北京") || province.equals("上海")
                || province.equals("天津") || province.equals("重庆")) {
            areaing = province;
        } else if (province.equals("全国")) {
            areaing = "";
        } else {
            areaing = AddressData.CITIES2[country.getCurrentItem()][city
                    .getCurrentItem()];
        }

        return contentView;
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
                cities[index]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    public class MyAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<Map<String, Object>> list = null;

        public MyAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<Map<String, Object>> list) {
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
            //	View view = null;


            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.listview_item, null);

                viewHolder = new ViewHolder();
                viewHolder.username = (TextView) convertView
                        .findViewById(R.id.pro_user_name);
                viewHolder.company_name = (TextView) convertView
                        .findViewById(R.id.pro_company_name);
                viewHolder.mobile_area = (TextView) convertView
                        .findViewById(R.id.pro_mobile_area);
                viewHolder.like_num = (TextView) convertView.findViewById(R.id.pro_like_num);

                // 如何加载图片
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.q_image);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                //	viewHolder = (ViewHolder) convertView.getTag();
                resetViewHolder(viewHolder);

            }


            viewHolder.username.setText(list.get(position).get("user_name").toString());
            viewHolder.company_name.setText(list.get(position).get("company_name")
                    .toString()
                    + "|");
            viewHolder.mobile_area.setText(list.get(position).get("mobile_area")
                    .toString());
            viewHolder.like_num.setText(list.get(position).get("good_count").toString()
                    + "赞");


            String icon_url = list.get(position).get("avatar").toString();
            Log.d("icon_url", icon_url);


            if (!(icon_url == null) && !(icon_url.length() < 5)) {



                Glide.with(MoreExpertActivity.this)
                        .load(icon_url)
                        .placeholder(R.drawable.white_bg)
                        .into(viewHolder.imageView);


            }


            return convertView;
        }

    }

    public class ViewHolder {

        public TextView username;
        public TextView company_name;
        public TextView mobile_area;
        public TextView like_num;
        public ImageView imageView;

    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] = AddressData.PROVINCES2;

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries[index];
        }
    }


}
