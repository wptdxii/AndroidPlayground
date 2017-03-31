package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.InsuranceEnquiryAdapter;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.LinearLayoutForListView;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.Call;

public class InsuranceEnquiryActivity extends BaseActivity {
    private static final String EXTRA_ORDER_ID = "order_id";
    private static final String EXTRA_PRICE_ID = "price_id";
    private static final String SP_USER_ID = "Login_UID";
    private static final String SP_TOKEN = "Login_TOKEN";
    private static final String PARAM_USER_ID = "user_id";
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_ORDER_ID = "order_id";
    private static final String PARAM_PRICE_ID = "price_id";
    private static final String PARAM_HADDLE_TYPE = "haddle_type";
    private static final String API_UPDATE_HADDLE_TYPE = "updateHaddleType";
    private static final String API_GET_OFFER_DETIAL = "getOfferDetial";
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
    @BindView(R.id.lv_insurance_price)
    LinearLayoutForListView lvInsurancePrice;
    @BindView(R.id.tv_total_des)
    TextView tvTotalDes;
    @BindView(R.id.tv_total_value)
    TextView tvTotalValue;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    private Dialog mLoadingDialog;
    private Map<String, String> mParamsMap;
    private HashMap<String, String> mPolicyNameMap;

    /**
     * 用于启动当前Activity
     *
     * @param context
     * @param orderId
     * @param priceId
     */
    public static void activityStart(Context context, String orderId, String priceId) {
        Intent intent = new Intent(context, InsuranceEnquiryActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        intent.putExtra(EXTRA_PRICE_ID, priceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_enquiry);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        rlShare.setVisibility(View.INVISIBLE);
        tvTitle.setText(R.string.activity_insurance_enquiry_title);

        mLoadingDialog = new Dialog(this, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.progress_dialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvDialog = (TextView) mLoadingDialog.findViewById(R.id.id_tv_loadingmsg);
        tvDialog.setText(R.string.msg_dialog_later);
    }

    private void initData() {
        Intent intent = getIntent();
        String userId = sp.getString(SP_USER_ID, "");
        String token = sp.getString(SP_TOKEN, "");
        String orderId = intent.getStringExtra(EXTRA_ORDER_ID);
        String priceId = intent.getStringExtra(EXTRA_PRICE_ID);
        mParamsMap = new HashMap<>();
        mParamsMap.put(PARAM_USER_ID, userId);
        mParamsMap.put(PARAM_TOKEN, token);
        mParamsMap.put(PARAM_ORDER_ID, orderId);
        mParamsMap.put(PARAM_PRICE_ID, priceId);

        mPolicyNameMap = new HashMap<>();
        mPolicyNameMap.put("jqx", "交强险/车船税");
        mPolicyNameMap.put("clssx", "车辆损失险");
        mPolicyNameMap.put("sydszzrx", "商业第三者责任险");
        mPolicyNameMap.put("qcdqx", "全车盗抢险");
        mPolicyNameMap.put("sjzwzrx", "司机座位责任险");
        mPolicyNameMap.put("ckzwzrx", "乘客座位责任险");
        mPolicyNameMap.put("blddpsx", "玻璃单独破碎险");
        mPolicyNameMap.put("cshhssx", "车身划痕损失险");
        mPolicyNameMap.put("zrssx", "自燃损失险");
        mPolicyNameMap.put("dcjcd", "倒车镜、车灯单独损失险");
        mPolicyNameMap.put("ssxs", "涉水行驶损失险");
        mPolicyNameMap.put("bjmp", "不计免赔");

        requestOfferDetail();
    }

    private void requestOfferDetail() {
        mLoadingDialog.show();
        String offerDetailUrl = IpConfig.getUri(API_GET_OFFER_DETIAL);
        OkHttpUtils.post()
                .url(offerDetailUrl)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mLoadingDialog.dismiss();
                        Toast.makeText(InsuranceEnquiryActivity.this, R.string.msg_net_retry,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mLoadingDialog.dismiss();
                        try {
                            if (response == null || response.equals("")
                                    || response.equals("null")) {
                                mLoadingDialog.dismiss();
                                Toast.makeText(InsuranceEnquiryActivity.this, R.string.msg_net_retry,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                JSONObject basicObject = dataObject.getJSONObject("basic");
                                String holderName = basicObject.getString("holder_name");
                                String dentifyNum = basicObject.getString("cj_no");
                                String engineNum = basicObject.getString("engine_no");
                                String carMsg = basicObject.getString("car_msg");
                                String isBought = basicObject.getString("is_bought");

                                List<Map<String, String>> list = new ArrayList<>();
                                Map<String, Object> dataMap = new HashMap<>();
                                JSONObject priceObject = dataObject.getJSONObject("price");
                                Iterator<String> iterator = priceObject.keys();
                                while (iterator.hasNext()) {
                                    Map<String, String> map = new HashMap<>();
                                    String key = iterator.next();
                                    String ty = mPolicyNameMap.get(key);
                                    String value = priceObject.getString(key);
                                    if (ty == null || ty.equals("")) {
                                        dataMap.put(key, value);
                                    } else {
                                        map.put("policy_name", ty);
                                        map.put("policy_price", value);
                                        list.add(map);
                                    }
                                }
                                tvLicenseNum.setText(carMsg);
                                tvOwnerName.setText(holderName);
                                tvEngineNum.setText(engineNum);
                                tvIdentifyNum.setText(dentifyNum);
                                tvTotalDes.setText(String.format(getString(
                                        R.string.activity_enquiry_selected_total), dataMap.get("count")));
                                tvTotalValue.setText((String) dataMap.get("total"));
                                if (isBought.equals("1")) {
                                    btnSubmit.setBackgroundResource(R.drawable.pub_grey_button_style);
                                    btnSubmit.setClickable(false);
                                }
                                InsuranceEnquiryAdapter adapter = new InsuranceEnquiryAdapter(
                                        InsuranceEnquiryActivity.this, list);
                                lvInsurancePrice.setAdapter(adapter);

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

    @OnCheckedChanged({R.id.rbtn_way_spot, R.id.rbtn_way_visit})
    public void selectTransactWay(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rbtn_way_spot:
                    mParamsMap.put(PARAM_HADDLE_TYPE, "01");
                    break;
                case R.id.rbtn_way_visit:
                    mParamsMap.put(PARAM_HADDLE_TYPE, "02");
                    break;
                default:
                    break;
            }
        }
    }

    @OnClick(R.id.btn_submit)
    public void submit() {
        String updateUrl = IpConfig.getUri(API_UPDATE_HADDLE_TYPE);
        OkHttpUtils.post()
                .url(updateUrl)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(InsuranceEnquiryActivity.this, R.string.msg_net_retry,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (response == null || response.equals("")
                                    || response.equals("null")) {
                                Toast.makeText(InsuranceEnquiryActivity.this, R.string.msg_net_retry,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                String errcode = jsonObject.getString("errcode");
                                String errmsg = jsonObject.getString("errmsg");
                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                String msg = dataObject.getString("msg");

                                if (errcode.equals("0")) {
                                    new CustomDialog.Builder(InsuranceEnquiryActivity.this)
                                            .setTitle(getString(R.string.prompt))
                                            .setMessage(msg)
                                            .setPositiveButton(getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            dialog.dismiss();
                                                            finish();
                                                        }
                                                    })
                                            .create()
                                            .show();
                                } else {
                                    Toast.makeText(InsuranceEnquiryActivity.this, errmsg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
