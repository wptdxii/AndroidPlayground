package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.GuaranteeBean;
import com.cloudhome.bean.OrderDetailBean;
import com.cloudhome.bean.OrderListBean;
import com.cloudhome.event.RefreshEvent;
import com.cloudhome.utils.Constants;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class OrderDetailActivity extends BaseActivity {
    private static final String EXTRA_DATA = "orderListBean";
    private static final String API_UPDATE_ORDER = "updateMyOrderModelState";
    private static final String API_ORDER_DETALI = "getOrderDetail";
    private static final String PARAM_ORDER_ID = "order_no";
    private static final String PARAM_SOURCE = "source";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.ll_order_detail_default)
    LinearLayout llOrderDetailDefault;
    @BindView(R.id.ll_order_detail_footer)
    LinearLayout llOrderDetailFooter;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    @BindColor(R.color.activity_order_detail_status_red)
    int mColorRed;
    private String mUserId;
    private Map<String, String> mParamsMap;
    private LayoutInflater mInflater;
    private OrderDetailBean mData;
    private List<GuaranteeBean> mGuaranteeData;
    private OrderListBean mOrderData;
    private Dialog mDialog;

    public static void activityStart(Context context, OrderListBean listBean) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(EXTRA_DATA, listBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        mInflater = LayoutInflater.from(this);
        tvTitle.setText(R.string.activity_order_detail_title);
        rlShare.setVisibility(View.INVISIBLE);
        initDialog();
    }

    private void initDialog() {
        mDialog = new Dialog(this, R.style.progress_dialog);
        mDialog.setContentView(R.layout.progress_dialog);
        mDialog.setCancelable(true);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView dialogContent = (TextView) mDialog.findViewById(R.id.id_tv_loadingmsg);
        dialogContent.setText(R.string.dialog_loading);
        mDialog.show();
    }

    private void initData() {
        mData = new OrderDetailBean();
        mParamsMap = new HashMap<>();
        mUserId = sp.getString(Constants.SP.USER_ID, "");
        String token = sp.getString(Constants.SP.TOKEN, "");
        MyApplication.prepay_id = "";
        mOrderData = (OrderListBean) getIntent().getSerializableExtra(EXTRA_DATA);
        String orderNum = mOrderData.getOrderNo();
        String source = mOrderData.getSource() != null ? mOrderData.getSource() : "remote";
        mGuaranteeData = new ArrayList<>();
        mParamsMap.put(Constants.PARAM.USER_ID, mUserId);
        mParamsMap.put(Constants.PARAM.TOKEN, token);
        mParamsMap.put(PARAM_ORDER_ID, orderNum);
        mParamsMap.put(PARAM_SOURCE, source);
        requestOrderDetail();
    }

    /**
     * 请求订单详情接口
     */
    private void requestOrderDetail() {
        String orderDetailUrl = IpConfig.getUri2(API_ORDER_DETALI);
        OkHttpUtils.get()
                .url(orderDetailUrl)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Toast.makeText(OrderDetailActivity.this, R.string.activity_order_detail_toast_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                            if (!"".equals(response) && !"null".equals(response)) {
                                JSONObject jsonObj = new JSONObject(response);
                                String errorCode = jsonObj.getString("errcode");
                                if ("0".equals(errorCode)) {
                                    JSONObject dataObj = jsonObj.getJSONObject("data");
                                    mData.setOrder(parseOrderData(dataObj, "order"));
                                    JSONArray orderTitleArray = dataObj.getJSONArray("order_title");
                                    String titleKey = (String) orderTitleArray.get(0);
                                    String titleValue = (String) orderTitleArray.get(1);
                                    mData.setOrderTitle(new OrderDetailBean.ItemBean(titleKey, titleValue));
                                    mData.setPersonSum(parseOrderData(dataObj, "person_sum"));
                                    mData.setPolicySum(parseOrderData(dataObj, "policy_sum"));
                                    JSONObject policyObj = dataObj.getJSONObject("policy");
                                    mGuaranteeData.add(parseGuaranteeData(policyObj, "保障计划", "products"));
                                    mGuaranteeData.add(parseGuaranteeData(policyObj, "投保人信息", "holder"));
                                    mGuaranteeData.add(parseGuaranteeData(policyObj, "被保人信息", "insured"));
                                    mGuaranteeData.add(parseGuaranteeData(policyObj, "受益人信息", "beneficiary"));
                                    bindData();
                                } else if ("1".equals(errorCode)) {
                                    String errorMsg = jsonObj.getString("errmsg");
                                    bindDefaultData(errorMsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private GuaranteeBean parseGuaranteeData(JSONObject jsonObj, String category, String jsonArrayKey)
            throws JSONException {
        GuaranteeBean bean = new GuaranteeBean();
        bean.setCategory(category);
        JSONArray jsonArray = jsonObj.getJSONArray(jsonArrayKey);
        List<GuaranteeBean.ItemBean> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray item = (JSONArray) jsonArray.get(i);
            String itemKey = (String) item.get(0);
            String itemValue = (String) item.get(1);
            list.add(new GuaranteeBean.ItemBean(itemKey, itemValue));
        }
        bean.setCategoryItems(list);
        return bean;
    }

    private List<OrderDetailBean.ItemBean> parseOrderData(JSONObject jsonObj, String jsonArrayKey)
            throws JSONException {
        JSONArray jsonArray = jsonObj.getJSONArray(jsonArrayKey);
        List<OrderDetailBean.ItemBean> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray item = (JSONArray) jsonArray.get(i);
            String itemKey = (String) item.get(0);
            String itemValue = (String) item.get(1);
            list.add(new OrderDetailBean.ItemBean(itemKey, itemValue));
        }
        return list;
    }

    private void bindDefaultData(String errorMsg) {
        llOrderDetailDefault.setVisibility(View.VISIBLE);
        String[] msgArray = errorMsg.split("\n");
        ((TextView) llOrderDetailDefault.findViewById(R.id.tv_order_detail_default_msg1)).setText(msgArray[0]);
        ((TextView) llOrderDetailDefault.findViewById(R.id.tv_order_detail_default_msg2)).setText(msgArray[1]);
    }

    @SuppressLint("inflateparams")
    private void bindData() {
        if ("待支付".equals(mOrderData.getStatus())) {
            llOrderDetailFooter.setVisibility(View.VISIBLE);
        }
        OrderDetailBean.ItemBean title = mData.getOrderTitle();
        List<OrderDetailBean.ItemBean> order = mData.getOrder();
        List<OrderDetailBean.ItemBean> policySum = mData.getPolicySum();
        List<OrderDetailBean.ItemBean> personSum = mData.getPersonSum();

        RelativeLayout rlTitle = (RelativeLayout) mInflater.inflate(R.layout.item_order_detail_title, null);
        ((TextView) rlTitle.findViewById(R.id.tv_order_detail_title)).setText(title.getItemKey());
        ((TextView) rlTitle.findViewById(R.id.tv_order_detail_company_name)).setText(title.getItemValue());
        ImageView imgOrderStatus = (ImageView) rlTitle.findViewById(R.id.img_order_detail_status);
        if ("已取消".equals(mOrderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_cancel);
        } else if ("已关闭".equals(mOrderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_closed);
        } else if ("处理中".equals(mOrderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_dealing);
        } else if ("待支付".equals(mOrderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_notpay);
        } else if ("已支付".equals(mOrderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_payed);
        } else if ("已出单".equals(mOrderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_presentation);
        }
        llContainer.addView(rlTitle);

        if (order.size() > 0) {
            RelativeLayout titleDivider = (RelativeLayout) mInflater.inflate(
                    R.layout.item_order_detail_divider, null);
            llContainer.addView(titleDivider);
        }

        for (int i = 0; i < order.size(); i++) {
            OrderDetailBean.ItemBean data = order.get(i);
            RelativeLayout rlOrder = (RelativeLayout) mInflater.inflate(
                    R.layout.item_order_detail, null);
            ((TextView) rlOrder.findViewById(R.id.tv_order_detail_key)).setText(data.getItemKey());
            TextView tvItemValue = ((TextView) rlOrder.findViewById(R.id.tv_order_detail_value));
            if ("订单状态".equals(data.getItemKey())) {
                tvItemValue.setTextColor(mColorRed);
            }
            tvItemValue.setText(data.getItemValue());
            llContainer.addView(rlOrder);
        }

        if (policySum.size() > 0 && isShowPolicySum(policySum)) {
            RelativeLayout orderDivider = (RelativeLayout) mInflater.inflate(
                    R.layout.item_order_detail_divider, null);
            llContainer.addView(orderDivider);
        }

        for (int i = 0; i < policySum.size(); i++) {
            OrderDetailBean.ItemBean data = policySum.get(i);
            if ("".equals(data.getItemValue())) {
                continue;
            }
            RelativeLayout rlPolicy = (RelativeLayout) mInflater.inflate(
                    R.layout.item_order_detail, null);
            ((TextView) rlPolicy.findViewById(R.id.tv_order_detail_key)).setText(data.getItemKey());
            ((TextView) rlPolicy.findViewById(R.id.tv_order_detail_value)).setText(data.getItemValue());
            llContainer.addView(rlPolicy);
        }

        RelativeLayout policySumDivider = (RelativeLayout) mInflater.inflate(
                R.layout.item_order_detail_divider, null);
        llContainer.addView(policySumDivider);

        RelativeLayout rlMoreInfo = (RelativeLayout) mInflater.inflate(
                R.layout.item_order_detail_more, null);
        rlMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuaranteeDetailActivity.activityStart(OrderDetailActivity.this, mGuaranteeData);
            }
        });
        llContainer.addView(rlMoreInfo);

        if (personSum.size() > 0) {

            RelativeLayout moreivider = (RelativeLayout) mInflater.inflate(
                    R.layout.item_order_detail_divider, null);
            llContainer.addView(moreivider);
        }

        for (int i = 0; i < personSum.size(); i++) {
            RelativeLayout rlPersonSum = (RelativeLayout) mInflater.inflate(
                    R.layout.item_order_detail, null);
            OrderDetailBean.ItemBean data = personSum.get(i);
            ((TextView) rlPersonSum.findViewById(R.id.tv_order_detail_key)).setText(data.getItemKey());
            ((TextView) rlPersonSum.findViewById(R.id.tv_order_detail_value)).setText(data.getItemValue());
            llContainer.addView(rlPersonSum);
        }

    }

    private boolean isShowPolicySum(List<OrderDetailBean.ItemBean> policySum) {
        for (int i = 0; i < policySum.size(); i++) {
            OrderDetailBean.ItemBean data = policySum.get(i);
            if (!"".equals(data.getItemValue())) {
                return true;
            }
        }
        return false;
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.tv_cancel_order)
    public void cancelOrder() {
        mParamsMap.put("id", mOrderData.getId());
        mParamsMap.put("status", "cancel");
        new CustomDialog.Builder(this)
                .setTitle(R.string.prompt)
                .setMessage(getString(R.string.activity_order_detail_msg_dialog))
                .setPositiveButton(R.string.msg_dialog_positive_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String url = IpConfig.getUri2(API_UPDATE_ORDER);
                                OkHttpUtils.post()
                                        .url(url)
                                        .params(mParamsMap)
                                        .build()
                                        .execute(new StringCallback() {
                                            @Override
                                            public void onError(Call call, Exception e, int id) {
                                                Toast.makeText(OrderDetailActivity.this,
                                                        R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onResponse(String response, int id) {
                                                try {
                                                    if (!(response == null || response.equals("") ||
                                                            response.equals("null"))) {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        String errcode = jsonObject.getString("errcode");
                                                        if (errcode.equals("0")) {
                                                            EventBus.getDefault().post(new RefreshEvent());
                                                            finish();
                                                        } else {
                                                            String errmsg = jsonObject.getString("errmsg");
                                                            Toast.makeText(OrderDetailActivity.this, errmsg,
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.msg_dialog_negative_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    @OnClick(R.id.tv_pay)
    public void toPay() {
        String payUrl = Uri.parse(IpConfig.getUri4("webPay")).buildUpon()
                .appendQueryParameter("orderid", mOrderData.getId())
                .appendQueryParameter("userid", mUserId)
                .build()
                .toString();
        CommonWebActivity.activityStart(this, payUrl, "确认支付", false, true);
        finish();
    }
}
