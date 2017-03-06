package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.ExpertAdapter;
import com.cloudhome.adapter.ExpertWheelAdapter;
import com.cloudhome.bean.ExpertBean;
import com.cloudhome.event.ExpertRefreshEvent;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.MyAlertDialog;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.wheel.wheelcity.AddressData;
import com.cloudhome.view.wheel.wheelcity.OnWheelChangedListener;
import com.cloudhome.view.wheel.wheelcity.WheelView;
import com.cloudhome.view.wheel.wheelcity.adapters.ArrayWheelAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ExpertActivity extends BaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener,
        XListView.IXListViewListener {
    private RelativeLayout rlBack;
    private TextView tvTitle;
    private RelativeLayout rlShare;
    private RelativeLayout rlCity;
    private TextView tvCity;
    private RadioGroup rgCategory;
    private XListView mListView;
    private ImageView imgBackToTop;
    private Dialog mDialog;
    private String mLoginState;
    private String mUrl;
    private Map<String, String> mParams;
    private int mPageNum;
    private String mOrderType;
    private List<ExpertBean> mData;
    private ExpertAdapter mAdapter;
    private boolean mRefresh = false;
    private String mArea;
    private int itemClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initData() {
        mLoginState = sp.getString("Login_STATE", "");
        mParams = new HashMap<>();
        mUrl = IpConfig.getUri("getExpertList");
        mPageNum = 1;
        mOrderType = "01";
        mArea = "";

        requestExpertList(mOrderType, mPageNum, mRefresh, true, mArea);
    }

    private void requestExpertList(String orderType,
                                   final int pageNum, final boolean isRefresh,
                                   final boolean showDialog, @Nullable String city) {

        if (mData == null) {
            mData = new ArrayList<>();
        }

        if (showDialog) {
            mDialog.show();
        }

        mParams.put("orderType", orderType);
        mParams.put("page", pageNum + "");
        mParams.put("city", city);

        OkHttpUtils.get()
                .url(mUrl)
                .params(mParams)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        stopRefresh(isRefresh);
                        Toast.makeText(ExpertActivity.this, "获取专家列表失败，请检查网络",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response != null && !"".equals(response)) {
                            try {
                                JSONObject responseData = new JSONObject(response);
                                JSONArray expertData = responseData.getJSONArray("data");
                                if (mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                                if (expertData != null && expertData.length() > 0) {
                                    mListView.setPullLoadEnable(true);
                                    parseExpertData(mData, expertData, pageNum);
                                    bindExpertData(mData, isRefresh, showDialog, pageNum);
                                } else {
                                    if (pageNum == 1) {
                                        mData.clear();
                                        mListView.setPullLoadEnable(false);
                                        mAdapter.notifyDataSetChanged();
                                        stopRefresh(isRefresh);
                                        Toast.makeText(ExpertActivity.this,
                                                "当前没有数据", Toast.LENGTH_SHORT).show();
                                        mListView.setPullLoadEnable(true);
                                    } else {
                                        mListView.stopLoadMore();
                                        mPageNum--;
                                        Toast.makeText(ExpertActivity.this, "暂无更多数据",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void bindExpertData(List<ExpertBean> list, boolean isRefresh, boolean showDialog, int pageNum) {
        stopRefresh(isRefresh);
        if (mAdapter == null) {
            mAdapter = new ExpertAdapter(this, list);
            mListView.setAdapter(mAdapter);
        } else {
            if (showDialog) {
                mListView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
        mListView.setPullLoadEnable(true);
    }

    private void stopRefresh(boolean isRefresh) {
        if (isRefresh) {
            mListView.stopRefresh();
            mRefresh = false;
        }
    }

    private void parseExpertData(List<ExpertBean> list, JSONArray expertData, int pageNum)
            throws JSONException {
        if (pageNum == 1) {
            list.clear();
        }
        for (int i = 0; i < expertData.length(); i++) {
            JSONObject expertObj = expertData.getJSONObject(i);
            ExpertBean expertBean = new ExpertBean();
            expertBean.setAvatar(expertObj.getString("avatar"));
            expertBean.setCert_a(expertObj.getString("cert_a"));
            expertBean.setCert_b(expertObj.getString("cert_b"));
            expertBean.setCert_num_isShowFlg(expertObj.getString("cert_num_isShowFlg"));
            expertBean.setCompany(expertObj.getString("company"));
            expertBean.setCompany_name(expertObj.getString("company_name"));
            expertBean.setGood_count(expertObj.getString("good_count"));
            expertBean.setId(expertObj.getString("id"));
            expertBean.setLicence(expertObj.getString("licence"));
            expertBean.setMobile(expertObj.getString("mobile"));
            expertBean.setMobile_area(expertObj.getString("mobile_area"));
            expertBean.setMobile_num_short(expertObj.getString("mobile_num_short"));
            expertBean.setPersonal_context(expertObj.getString("personal_context"));
            expertBean.setPersonal_specialty(expertObj.getString("personal_specialty"));
            expertBean.setState(expertObj.getString("state"));
            expertBean.setType(expertObj.getString("type"));
            expertBean.setUser_name(expertObj.getString("user_name"));
            list.add(expertBean);
        }
    }


    private void initView() {
        rlBack = (RelativeLayout) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_text);
        rlShare = (RelativeLayout) findViewById(R.id.rl_right);
        rlCity = (RelativeLayout) findViewById(R.id.rl_city);
        tvCity = (TextView) findViewById(R.id.tv_city);
        rgCategory = (RadioGroup) findViewById(R.id.rg_category);
        mListView = (XListView) findViewById(R.id.lv_expert);
        imgBackToTop = (ImageView) findViewById(R.id.img_back_to_top);
        initDialog();

        tvTitle.setText("保险专家");
        rlBack.setOnClickListener(this);
        rlShare.setVisibility(View.INVISIBLE);
        rlCity.setOnClickListener(this);
        rgCategory.setOnCheckedChangeListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        imgBackToTop.setOnClickListener(this);

    }

    private void initDialog() {
        mDialog = new Dialog(this, R.style.progress_dialog);
        mDialog.setContentView(R.layout.progress_dialog);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvDialogMsg = (TextView) mDialog.findViewById(R.id.id_tv_loadingmsg);
        tvDialogMsg.setText("卖力加载中...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_city:
                // TODO: show select city dialog
                showCitySelectedWheelView();
                break;
            case R.id.img_back_to_top:
                mListView.setSelection(0);
                break;
        }
    }

    private void showCitySelectedWheelView() {
        View wheelView = createWheelView();
        new MyAlertDialog(this)
                .builder()
                .setTitle("请选择地区")
                .setView(wheelView)
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mArea.equals("")) {
                            tvCity.setText("全国");
                        } else {
                            tvCity.setText(mArea);
                        }
                        mPageNum = 1;
                        requestExpertList(mOrderType, mPageNum, false, true, mArea);
                    }
                })
                .show();
    }

    private View createWheelView() {
        String[] countries = AddressData.PROVINCES2;
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView provinceWheelView = (WheelView) contentView
                .findViewById(R.id.wheelcity_country);
        final WheelView cityWheelView = (WheelView) contentView
                .findViewById(R.id.wheelcity_city);

        provinceWheelView.setVisibleItems(1);
        provinceWheelView.setViewAdapter(new ExpertWheelAdapter(this, R.layout.wheelcity_country_layout
                , R.id.wheelcity_country_name, countries));

        final String[][] cities = AddressData.CITIES2;
        cityWheelView.setVisibleItems(1);

        provinceWheelView.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                switchCity(cityWheelView, cities, newValue);
                String province = AddressData.PROVINCES2[provinceWheelView.getCurrentItem()];
                if (province.equals("北京") || province.equals("上海")
                        || province.equals("天津") || province.equals("重庆")) {
                    mArea = province;
                } else if (province.equals("全国")) {
                    mArea = "";
                } else {
                    mArea = AddressData
                            .CITIES2[provinceWheelView.getCurrentItem()][cityWheelView.getCurrentItem()];
                }

            }
        });

        cityWheelView.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String province = AddressData.PROVINCES2[provinceWheelView.getCurrentItem()];
                if (province.equals("北京") || province.equals("上海")
                        || province.equals("天津") || province.equals("重庆")) {
                    mArea = province;
                } else if (province.equals("全国")) {
                    mArea = "";
                } else {
                    mArea = AddressData
                            .CITIES2[provinceWheelView.getCurrentItem()][cityWheelView.getCurrentItem()];
                }

            }
        });

        provinceWheelView.setCurrentItem(0);
        cityWheelView.setCurrentItem(0);
        switchCity(cityWheelView, cities, 0);

        String province = AddressData.PROVINCES2[provinceWheelView.getCurrentItem()];
        if (province.equals("北京") || province.equals("上海")
                || province.equals("天津") || province.equals("重庆")) {
            mArea = province;
        } else if (province.equals("全国")) {
            mArea = "";
        } else {
            mArea = AddressData
                    .CITIES2[provinceWheelView.getCurrentItem()][cityWheelView.getCurrentItem()];
        }
        return contentView;
    }

    private void switchCity(WheelView cityWheelView, String[][] cities, int newValue) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, cities[newValue]);
        adapter.setTextSize(18);
        cityWheelView.setViewAdapter(adapter);
        cityWheelView.setCurrentItem(0);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_recommend:
                mOrderType = "01";
                break;
            case R.id.rb_join:
                mOrderType = "02";
                break;
            case R.id.rb_evaluate:
                mOrderType = "03";
                break;
        }
        mPageNum = 1;
        mListView.setPullLoadEnable(false);
        requestExpertList(mOrderType, mPageNum, false, true, mArea);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        itemClickedPosition = position - mListView.getHeaderViewsCount();
        ExpertBean expertBean = mData.get(itemClickedPosition);
        Intent intent = new Intent(this, ExpertMicroActivity.class);
        intent.putExtra("id", expertBean.getId());
        intent.putExtra("avatar", expertBean.getAvatar());
        intent.putExtra("user_name", expertBean.getUser_name());
        intent.putExtra("company_name", expertBean.getCompany_name());
        intent.putExtra("mobile_area", expertBean.getMobile_area());
        intent.putExtra("good_count", expertBean.getGood_count());
        intent.putExtra("cert_a", expertBean.getCert_a());
        intent.putExtra("cert_b", expertBean.getCert_b());
        intent.putExtra("licence", expertBean.getLicence());
        intent.putExtra("mobile", expertBean.getMobile());
        intent.putExtra("cert_num_isShowFlg", expertBean.getCert_num_isShowFlg());
        intent.putExtra("mobile_num_short", expertBean.getMobile_num_short());
        intent.putExtra("state", expertBean.getState());
        intent.putExtra("personal_specialty", expertBean.getPersonal_specialty());
        intent.putExtra("personal_context", expertBean.getPersonal_context());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mListView.setPullLoadEnable(false);
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPageNum = 1;
                mRefresh = true;
                requestExpertList(mOrderType, mPageNum, mRefresh, false, mArea);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPageNum++;
                requestExpertList(mOrderType, mPageNum, false, false, mArea);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExpertRefreshEvent(ExpertRefreshEvent event) {
        mData.get(itemClickedPosition).setGood_count(event.getThumbUpNum());
        mAdapter.notifyDataSetChanged();
    }
}
