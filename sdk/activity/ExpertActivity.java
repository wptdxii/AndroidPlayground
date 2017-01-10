package com.cloudhome.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.bean.ExpertBean;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.xlistview.XListView;
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

public class ExpertActivity extends BaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    private static final String TAG = "ExpertActivity";
    private RelativeLayout rlBack;
    private TextView tvTitle;
    private RelativeLayout rlShare;
    private RelativeLayout rlCity;
    private TextView tvCity;
    private RadioGroup rgCategory;
    private XListView lvExpert;
    private ImageView imgBackToTop;
    private Dialog mDialog;
    private String mLoginState;
    private String mUrl;
    private Map<String, String> mParams;
    private int mPageNum;
    private String mOrderType;
    private List<ExpertBean> mRecommendData;
    private List<ExpertBean> mJoinData;
    private List<ExpertBean> mEvaluateData;
    private List<ExpertBean> mAdapterData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert);
        initView();
        initData();
    }

    private void initData() {
        mLoginState = sp.getString("Login_STATE", "");
        mParams = new HashMap<>();
        mUrl = IpConfig.getUri("getExpertList");
        mPageNum = 1;
        mOrderType = "01";
        mRecommendData = new ArrayList<>();
        mJoinData = new ArrayList<>();
        mEvaluateData = new ArrayList<>();
        mAdapterData = new ArrayList<>();

        requestExpertList(mAdapterData, mOrderType, mPageNum, null);
    }

    private void requestExpertList(final List<ExpertBean> list, String orderType,
                                   int pageNum, @Nullable String city) {
        mParams.put("orderType", orderType);
        mParams.put("page", pageNum + "");
        if (city != null && !"".equals(city)) {
            mParams.put("city", city);
        }

        OkHttpUtils.get()
                .url(mUrl)
                .params(mParams)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(ExpertActivity.this, "获取专家列表失败，请检查网络",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response != null && !"".equals(response)) {
                            try {
                                JSONObject responseData = new JSONObject(response);
                                JSONArray expertData = responseData.getJSONArray("data");
                                if (expertData != null && expertData.length() > 0) {
                                    parseExpertData(list, expertData);
                                    bindExpertData(list);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void bindExpertData(List<ExpertBean> list) {

    }

    private List<ExpertBean> parseExpertData(List<ExpertBean> list, JSONArray expertData) throws JSONException {
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
        return list;
    }


    private void initView() {
        rlBack = (RelativeLayout) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_text);
        rlShare = (RelativeLayout) findViewById(R.id.rl_right);
        rlCity = (RelativeLayout) findViewById(R.id.rl_city);
        tvCity = (TextView) findViewById(R.id.tv_city);
        rgCategory = (RadioGroup) findViewById(R.id.rg_category);
        lvExpert = (XListView) findViewById(R.id.lv_expert);
        imgBackToTop = (ImageView) findViewById(R.id.img_back_to_top);
        initDialog();

        tvTitle.setText("保险专家");
        rlBack.setOnClickListener(this);
        rlShare.setVisibility(View.INVISIBLE);
        rlCity.setOnClickListener(this);
        rgCategory.setOnCheckedChangeListener(this);
        lvExpert.setOnItemClickListener(this);
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
                Log.e(TAG, "onClick: city");
                break;
            case R.id.img_back_to_top:
                // TODO: set position
                Log.e(TAG, "onClick: back to top");
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
