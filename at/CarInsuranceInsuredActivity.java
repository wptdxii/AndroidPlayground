package com.cloudhome.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cloudhome.R;
import com.cloudhome.adapter.CarInsuranceInsuredAdapter;
import com.cloudhome.bean.CarInsuranceInsuredBean;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class CarInsuranceInsuredActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private ListView mListView;
    private Dialog mDialog;
    private String mUrl;
    private Map<String, String> mParams;
    private ArrayList<CarInsuranceInsuredBean.DataBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_insurance_insured);
        initView();
        initData();
    }

    private void initData() {
        String userId = sp.getString("Login_UID", "");
        String token = sp.getString("Login_TOKEN", "");
        mUrl = IpConfig.getUri("getAutoInsuranceInquiryApi");
        mParams = new HashMap<>();
        mParams.put("user_id", userId);
        mParams.put("token", token);

        initCarInsuranceData();
    }

    private void initCarInsuranceData() {
        mDialog.show();
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
                        Toast.makeText(CarInsuranceInsuredActivity.this,
                                "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        if (response != null && !"".equals(response)) {
                            CarInsuranceInsuredBean bean = JSON.parseObject(response,
                                    CarInsuranceInsuredBean.class);
                            mData = (ArrayList<CarInsuranceInsuredBean.DataBean>) bean.getData();
                            if (mData != null && mData.size() > 1) {
                                bindData(mData);
                            } else {
                                Toast.makeText(CarInsuranceInsuredActivity.this, "当前没有数据",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void bindData(ArrayList<CarInsuranceInsuredBean.DataBean> data) {
        CarInsuranceInsuredAdapter adapter = new CarInsuranceInsuredAdapter(this, data);
        mListView.setAdapter(adapter);
    }

    private void initView() {
        RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_back);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        RelativeLayout rlShare = (RelativeLayout) findViewById(R.id.rl_share);
        mListView = (ListView) findViewById(R.id.lv_car_insurance);
        initDialog();

        tvTitle.setText("车险投保");
        rlBack.setOnClickListener(this);
        rlShare.setVisibility(View.INVISIBLE);
        mListView.setOnItemClickListener(this);
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
            case R.id.rl_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mData != null && mData.size() > 0) {
            CarInsuranceInsuredBean.DataBean bean = mData.get(position);
            String alertMsg = bean.getAlert_msg();
            if ("".equals(alertMsg)) {
                Intent intent = new Intent();
                intent.putExtra("title", bean.getName());
                intent.putExtra("url", bean.getUrl());
                intent.setClass(CarInsuranceInsuredActivity.this,
                        CXTBWebActivity.class);
                CarInsuranceInsuredActivity.this.startActivity(intent);
            } else {
                CustomDialog  dialog = new CustomDialog.Builder(
                        CarInsuranceInsuredActivity.this)
                        .setTitle("提示")
                        .setMessage(alertMsg)
                        .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        }

    }
}
