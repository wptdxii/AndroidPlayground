package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.AccurateQuotationAdapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;


public class AccurateQuotationActivity extends BaseActivity {
    private static final String API_ORDER_DETAIL = "getOrderDetial";
    private static final String SP_USER_ID = "Login_UID";
    private static final String SP_TOKEN = "Login_TOKEN";
    private static final String EXTRA_ID = "id";
    private static final String EXTRA_QUOTE_STATUS = "quote_status";
    private static final String PARAM_USER_ID = "user_id";
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_ORDER_ID = "order_id";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.tv_license_num)
    TextView tvLicenseNum;
    @BindView(R.id.tv_owner_name)
    TextView tvOwnerName;
    @BindView(R.id.tv_engine_num)
    TextView tvEngineNum;
    @BindView(R.id.tv_identify_num)
    TextView tvIdentifyNum;
    @BindView(R.id.lv_insurance)
    ListView lvInsurance;
    private String mOrderId;
    private String mQuoteStatus;
    private Dialog mLoadingDialog;
    private List<Map<String, Object>> mDataList;

    public static void activityStart(Context context, String id, String quoteStatus) {
        Intent intent = new Intent(context, AccurateQuotationActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_QUOTE_STATUS, quoteStatus);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accurate_quotation);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        rlShare.setVisibility(View.INVISIBLE);
        tvTitle.setText(R.string.activity_accurate_quotation_title);

        mLoadingDialog = new Dialog(this, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.progress_dialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvDialog = (TextView) mLoadingDialog.findViewById(R.id.id_tv_loadingmsg);
        tvDialog.setText(R.string.msg_dialog_later);
    }

    private void initData() {
        Intent intent = getIntent();
        mOrderId = intent.getStringExtra(EXTRA_ID);
        mQuoteStatus = intent.getStringExtra(EXTRA_QUOTE_STATUS);
        String userId = sp.getString(SP_USER_ID, "");
        String token = sp.getString(SP_TOKEN, "");

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(PARAM_USER_ID, userId);
        paramsMap.put(PARAM_TOKEN, token);
        paramsMap.put(PARAM_ORDER_ID, mOrderId);
        String orderDetailUrl = IpConfig.getUri(API_ORDER_DETAIL);
        requestOrderDetail(orderDetailUrl, paramsMap);
    }

    private void requestOrderDetail(String orderDetailUrl, Map<String, String> paramsMap) {
        OkHttpUtils.post()
                .url(orderDetailUrl)
                .params(paramsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(AccurateQuotationActivity.this,
                                R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (response.equals("") || response.equals("null")) {
                                Toast.makeText(AccurateQuotationActivity.this,
                                        R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject dataObject = jsonObject.getJSONObject("data");

                                String holderName = dataObject.getString("holder_name");
                                String carCode = dataObject.getString("car_code");
                                String identifyNum = dataObject.getString("cj_no");
                                String engineNum = dataObject.getString("engine_no");
                                if (carCode.equals("")) {
                                    tvLicenseNum.setText(R.string.activity_accurate_quotation_no_license);
                                } else {
                                    tvLicenseNum.setText(carCode);
                                }
                                tvOwnerName.setText(holderName);
                                tvEngineNum.setText(engineNum);
                                tvIdentifyNum.setText(identifyNum);

                                mDataList = new ArrayList<>();
                                JSONArray companyList = dataObject.getJSONArray("companys");
                                for (int i = 0; i < companyList.length(); i++) {
                                    JSONObject jsonObject2 = companyList
                                            .getJSONObject(i);
                                    Map<String, Object> map = new HashMap<>();
                                    Iterator<String> iterator = jsonObject2.keys();
                                    while (iterator.hasNext()) {
                                        String key = iterator.next();
                                        Object value = jsonObject2.get(key);
                                        map.put(key, value);
                                    }
                                    mDataList.add(map);
                                }
                                AccurateQuotationAdapter adapter = new AccurateQuotationAdapter(
                                        AccurateQuotationActivity.this, mDataList);
                                lvInsurance.setAdapter(adapter);
                                mLoadingDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnItemClick(R.id.lv_insurance)
    public void clickItem(int position) {
        Map<String, Object> dataMap = mDataList.get(position);
        if (!"01".equals(mQuoteStatus) && "".equals(dataMap.get("remarks").toString())
                && !("暂无报价").equals(dataMap.get("price").toString())) {
            InsuranceEnquiryActivity.activityStart(this, mOrderId, dataMap.get("price_id").toString());
        }
    }
}
