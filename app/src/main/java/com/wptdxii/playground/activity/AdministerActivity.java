package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.AdministerExpandableListAdapter;
import com.cloudhome.bean.AdministerChildBean;
import com.cloudhome.bean.AdministerGroupBean;
import com.cloudhome.bean.BarChartBean;
import com.cloudhome.bean.PieChartBean;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.HorizontalBarChartView;
import com.cloudhome.view.customview.PieChartView;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.xlistview.ExpandListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class AdministerActivity extends BaseActivity
        implements View.OnClickListener, ExpandListView.IXListViewListener,
        ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupExpandListener {

    private List<AdministerGroupBean> mGroup;
    private List<List<AdministerChildBean>> mChild;
    private List<AdministerGroupBean> mAdapterGroup;
    private List<List<AdministerChildBean>> mAdapterChild;

    private RelativeLayout rlBack;
    private TextView tvTitle;
    private RelativeLayout rlShare;
    private RelativeLayout rlStatistics;
    private ImageView imgStatisticsArrow;
    private RelativeLayout rlStatisticsContent;
    private RelativeLayout rlAdminister;
    private ImageView imgAdministerArrow;
    private ExpandListView elvAdminister;
    private boolean mStatisticsShown = false;
    private boolean mAdministerShown = false;
    private AdministerExpandableListAdapter mAdapter;
    private String mToken;
    private String mUserId;
    private String mUserType;
    private String mUserState;
    private String mUrl;
    private Map<String, String> mParams;

    private View mHeader;
    private TextView tvTotalPeriod;
    private HorizontalBarChartView mBarChart;
    private View stasticsDivider;
    private TextView tvStasitcs;
    private PieChartView mPieChart;
    private boolean isRefershing = false;
    private Dialog dialog;

    private int lastExpandedPosition = -1;
    private Double totalPeriod;
    private List<BarChartBean> mBarChartData;
    private List<PieChartBean> mPieChartData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administer);

        initView();
        initDialog();
        initData();
    }


    private void initData() {

        mUserId = sp.getString("Login_UID_ENCODE", "");
        mToken = sp.getString("Login_TOKEN", "");
        mUserType = sp.getString("Login_TYPE", "");
        mUserState = sp.getString("Login_CERT", "");
        mParams = new HashMap<>();
        mParams.put("user_id", mUserId);
        mParams.put("token", mToken);
        mUrl = IpConfig.getUri2("getUnderData");
        //        mUrl = IpConfig.getUri2("getAdminister");
        mBarChartData = new ArrayList<>();
        mPieChartData = new ArrayList<>();

        mGroup = new ArrayList<>();
        mChild = new ArrayList<>();
        mAdapterGroup = new ArrayList<>();
        mAdapterChild = new ArrayList<>();
        mAdapter = new AdministerExpandableListAdapter(this,
                mAdapterGroup, mAdapterChild, mUserType);
        elvAdminister.setAdapter(mAdapter);


        initAdministerData();
    }

    private void initDialog() {
        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView dialogContent = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        dialogContent.setText("加载中...");
        dialog.show();
    }

    private static final String TAG = "AdministerActivity";

    private void initAdministerData() {
        OkHttpUtils.get()
                .url(mUrl)
                .params(mParams)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (isRefershing) {
                            Toast.makeText(AdministerActivity.this, "刷新失败",
                                    Toast.LENGTH_SHORT).show();
                            elvAdminister.stopRefresh();
                            elvAdminister.setRefreshTime("刚刚");
                            isRefershing = false;
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + response);

                        try {


                            if (!response.equals("") && !response.equals("null")) {
                                if (isRefershing) {
                                    mGroup.clear();
                                    mChild.clear();
                                    mAdapterGroup.clear();
                                    mAdapterChild.clear();
                                    elvAdminister.setRefreshTime("刚刚");
                                }
                                JSONObject jsonObj = new JSONObject(response);

                                JSONObject underObj = jsonObj.getJSONObject("data");
                                JSONArray jsonArray = underObj.getJSONArray("underList");

                                // JSONArray jsonArray = jsonObj.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject dataObj = jsonArray.getJSONObject(i);
                                    AdministerGroupBean groupBean = new AdministerGroupBean();
                                    groupBean.setDirectUserTotal(dataObj.getInt("directUserTotal"));
                                    groupBean.setProvinceName(dataObj.getString("provinceName"));
                                    mGroup.add(groupBean);

                                    List<AdministerChildBean> chidList = new ArrayList<>();
                                    JSONArray underUserArray = dataObj.getJSONArray("underUsers");
                                    for (int j = 0; j < underUserArray.length(); j++) {
                                        JSONObject userObj = underUserArray.getJSONObject(j);
                                        AdministerChildBean childBean = new AdministerChildBean();
                                        childBean.setAmount(userObj.getDouble("amount"));
                                        childBean.setTotal(userObj.getInt("total"));
                                        childBean.setMobile(userObj.getString("mobile"));
                                        childBean.setId(userObj.getInt("id"));
                                        childBean.setAvatar(userObj.getString("avatar"));
                                        childBean.setState(userObj.getString("state"));
                                        if ("00".equals(mUserType)) {
                                            childBean.setDirectUserCount(
                                                    userObj.getInt("directUserCount"));
                                        }
                                        childBean.setName(userObj.getString("name"));
                                        chidList.add(childBean);
                                    }
                                    mChild.add(chidList);
                                }


                                JSONObject statisticOjb = underObj.getJSONObject("statisticData");
                                totalPeriod = statisticOjb.getDouble("totalPeriod");
                                mBarChartData.clear();
                                mBarChartData.add(new BarChartBean("寿险", "#e8382b",
                                        statisticOjb.getInt("lifeInsurance")));
                                mBarChartData.add(new BarChartBean("非车", "#fca600",
                                        statisticOjb.getInt("noCarInsurance")));
                                mBarChartData.add(new BarChartBean("车险", "#028be6",
                                        statisticOjb.getInt("carInsurance")));
                                if ("00".equals(mUserType)) {

                                    mPieChartData.clear();
                                    mPieChartData.add(new PieChartBean("直接推荐人", "#028be6",
                                            statisticOjb.getInt("directCount")));
                                    mPieChartData.add(new PieChartBean("间接推荐人", "#fca600",
                                            statisticOjb.getInt("indirectCount")));
                                }

                                bindStatisticData();

                                if (isRefershing) {
                                    if (mAdministerShown) {

                                        mAdapterGroup.addAll(mGroup);
                                        mAdapterChild.addAll(mChild);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    elvAdminister.stopRefresh();
                                    isRefershing = false;
                                    showStastics(mStatisticsShown);

                                } else {
                                    if ("00".equals(mUserState)) {
                                        showStastics(true);

                                    } else {

                                        showStastics(false);
                                    }
                                }

                                if (dialog.isShowing()) {

                                    dialog.dismiss();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void showStastics(boolean showStastics) {
        if (showStastics) {
            rlStatisticsContent.setVisibility(View.VISIBLE);
            imgStatisticsArrow.setBackgroundResource(R.drawable.icon_down);
        } else {
            rlStatisticsContent.setVisibility(View.GONE);
            imgStatisticsArrow.setBackgroundResource(R.drawable.icon_right);
        }
        mStatisticsShown = showStastics;
    }

    private void bindStatisticData() {
        tvTotalPeriod.setText(totalPeriod + "");
        mBarChart.setBarChartData(mBarChartData);

        int visible;
        if ("00".equals(mUserType)) {

            visible = View.VISIBLE;
            mPieChart.setPieChartData(mPieChartData);

        } else {

            visible = View.GONE;
        }
        stasticsDivider.setVisibility(visible);
        tvStasitcs.setVisibility(visible);
        mPieChart.setVisibility(visible);

    }


    private void initView() {
        rlBack = (RelativeLayout) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_text);
        rlShare = (RelativeLayout) findViewById(R.id.rl_right);
        mHeader = LayoutInflater.from(this).inflate(R.layout.layout_header_administer, null, false);
        rlStatistics = (RelativeLayout) mHeader.findViewById(R.id.rl_statistics);
        imgStatisticsArrow = (ImageView) mHeader.findViewById(R.id.img_arrow_statistics);
        rlStatisticsContent = (RelativeLayout) mHeader.findViewById(R.id.rl_statistics_content);
        tvTotalPeriod = (TextView) mHeader.findViewById(R.id.tv_total_period);
        mBarChart = (HorizontalBarChartView) mHeader.findViewById(R.id.bar_chart);
        stasticsDivider = mHeader.findViewById(R.id.divider_statistics);
        tvStasitcs = (TextView) mHeader.findViewById(R.id.tv_statistics_administer);
        mPieChart = (PieChartView) mHeader.findViewById(R.id.pie_chart);
        rlAdminister = (RelativeLayout) mHeader.findViewById(R.id.rl_administer);
        imgAdministerArrow = (ImageView) mHeader.findViewById(R.id.img_arrow_administer);
        elvAdminister = (ExpandListView) findViewById(R.id.elv_administer);

        rlBack.setOnClickListener(this);
        tvTitle.setText("辖下");
        rlShare.setVisibility(View.GONE);
        rlStatistics.setOnClickListener(this);
        rlAdminister.setOnClickListener(this);
        elvAdminister.addHeaderView(mHeader);
        elvAdminister.setPullRefreshEnable(true);
        elvAdminister.setPullLoadEnable(false);
        elvAdminister.setXListViewListener(this);
        elvAdminister.setOnGroupExpandListener(this);
        elvAdminister.setOnChildClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        } else if (view.getId() == R.id.rl_statistics) {

            if (!mStatisticsShown) {

                if ("00".equals(mUserState)) {

                    if ((mPieChartData == null || mPieChartData.size() == 0)
                            && (mBarChartData == null || mBarChartData.size() == 0)) {
                        Toast.makeText(this, "对不起，您还没有统计数据", Toast.LENGTH_SHORT).show();
                    } else {

                        rlStatisticsContent.setVisibility(View.VISIBLE);
                    }


                } else {

                    showNotAuthDialog();
                }

                imgStatisticsArrow.setBackgroundResource(R.drawable.icon_down);
            } else {
                imgStatisticsArrow.setBackgroundResource(R.drawable.icon_right);
                rlStatisticsContent.setVisibility(View.GONE);
            }
            mStatisticsShown = !mStatisticsShown;

        } else if (view.getId() == R.id.rl_administer) {

            mAdapterGroup.clear();
            mAdapterChild.clear();
            if (!mAdministerShown) {

                if ("00".equals(mUserState)) {
                    mAdapterGroup.addAll(mGroup);
                    mAdapterChild.addAll(mChild);
                    imgAdministerArrow.setBackgroundResource(R.drawable.icon_down);
                    if (mAdapterGroup.size() <= 0) {
                        Toast.makeText(this, "对不起，您还没有辖下人员", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    showNotAuthDialog();
                }
                imgAdministerArrow.setBackgroundResource(R.drawable.icon_down);
            } else {
                imgAdministerArrow.setBackgroundResource(R.drawable.icon_right);
            }
            mAdapter.notifyDataSetChanged();
            mAdministerShown = !mAdministerShown;
        }
    }

    private void showCustomDialog(String message) {

        new CustomDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.dismiss();

                            }
                        })
                .create()
                .show();
    }

    private void showNotAuthDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(
                R.layout.dialog_content_commission, null);
        builder.setContentView(dialogView);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface
                                                dialog,
                                        int which) {

                        dialog.dismiss();

                    }
                });
        builder.create().show();
    }

    @Override
    public void onRefresh() {
        isRefershing = true;
        elvAdminister.postDelayed(new Runnable() {
            @Override
            public void run() {
                initAdministerData();
            }
        }, 2000);

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {

        AdministerChildBean bean = mAdapterChild.get(groupPosition).get(childPosition);
        String state = bean.getState();
        if ("00".equals(state)) {
            String childId = bean.getId() + "";
            String url = IpConfig.getIp() + "user_id=" + childId + "&token=" +
                    mToken + "&mod=getHomepageForExpert";
            Intent intent = new Intent();
            intent.putExtra("title", "我的微站");
            intent.putExtra("url", url);
            intent.putExtra("needShare", false);
            intent.setClass(AdministerActivity.this, MicroShareWebActivity.class);
            startActivity(intent);
        } else {
            showCustomDialog("该用户还未认证");
        }

        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {

        if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
            elvAdminister.collapseGroup(lastExpandedPosition);
        }

        elvAdminister.setSelection(groupPosition + elvAdminister.getHeaderViewsCount());
        lastExpandedPosition = groupPosition;

    }
}