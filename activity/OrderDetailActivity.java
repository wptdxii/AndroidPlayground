package com.cloudhome.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
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

import okhttp3.Call;


public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rlBack;
    private TextView tvTitle;
    private RelativeLayout rlShare;
    private LinearLayout mContainer;
    private LayoutInflater mInflater;
    private OrderDetailBean mData;
    private List<GuaranteeBean> mGuaranteeData;
    private TextView tvCancelOrder;
    private TextView tvPay;
    private LinearLayout llOrderDetailDefault;
    private LinearLayout llOrderDetailFooter;
    private OrderListBean orderData;
    private String mUserId;
    private String mUserIdEncode;
    private String mToken;
    private String mUrl;
    private Map<String, String> mParams;
    private Dialog dialog;
    private String mOrderNum;
    private String mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initView();
        initDialog();
        initData();
    }

    private void initDialog() {
        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView dialogContent = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        dialogContent.setText("加载中...");
        dialog.show();
    }

    private void initData() {
        mData = new OrderDetailBean();
        mParams = new HashMap<>();
        mUserId = sp.getString("Login_UID", "");
        mUserIdEncode = sp.getString("Login_UID_ENCODE", "");
        mToken = sp.getString("Login_TOKEN", "");
        MyApplication.prepay_id="";

        orderData = (OrderListBean) getIntent().getSerializableExtra("orderListBean");
        mOrderNum = orderData.getOrderNo();
        mSource = orderData.getSource() != null ? orderData.getSource() : "remote";

        mGuaranteeData = new ArrayList<>();
        mParams.put("user_id", mUserId);
        mParams.put("order_no", mOrderNum);
        mParams.put("source", mSource);
        mUrl = IpConfig.getUri2("getOrderDetail");
        //        mUrl = "http://10.10.10.127:8080/gateway/order/getOrderDetail?" + "user_id=" + mUserId + "&order_no=" + mOrderNum
        //                + "&source=" + mSource;
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
                        Toast.makeText(OrderDetailActivity.this, "获取订单详情失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
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


    private void bindData() {
        if ("待支付".equals(orderData.getStatus())) {

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
        if ("已取消".equals(orderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_cancel);
        } else if ("已关闭".equals(orderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_closed);
        } else if ("处理中".equals(orderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_dealing);
        } else if ("待支付".equals(orderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_notpay);
        } else if ("已支付".equals(orderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_payed);
        } else if ("已出单".equals(orderData.getStatus())) {
            imgOrderStatus.setBackgroundResource(R.drawable.icon_order_detail_presentation);
        }
        mContainer.addView(rlTitle);

        if (order.size() > 0) {

            RelativeLayout titleDivider = (RelativeLayout) mInflater.inflate(R.layout.item_order_detail_divider, null);
            mContainer.addView(titleDivider);
        }


        for (int i = 0; i < order.size(); i++) {
            OrderDetailBean.ItemBean data = order.get(i);
            RelativeLayout rlOrder = (RelativeLayout) mInflater.inflate(R.layout.item_order_detail, null);
            ((TextView) rlOrder.findViewById(R.id.tv_order_detail_key)).setText(data.getItemKey());
            TextView tvItemValue = ((TextView) rlOrder.findViewById(R.id.tv_order_detail_value));
            if ("订单状态".equals(data.getItemKey())) {
                tvItemValue.setTextColor(Color.parseColor("#fc0404"));
            }
            tvItemValue.setText(data.getItemValue());
            mContainer.addView(rlOrder);
        }


        if (policySum.size() > 0 && isShowPolicySum(policySum)) {

            RelativeLayout orderDivider = (RelativeLayout) mInflater.inflate(R.layout.item_order_detail_divider, null);
            mContainer.addView(orderDivider);
        }


        for (int i = 0; i < policySum.size(); i++) {
            OrderDetailBean.ItemBean data = policySum.get(i);
            if ("".equals(data.getItemValue())) {
                continue;
            }
            RelativeLayout rlPolicy = (RelativeLayout) mInflater.inflate(R.layout.item_order_detail, null);

            ((TextView) rlPolicy.findViewById(R.id.tv_order_detail_key)).setText(data.getItemKey());
            ((TextView) rlPolicy.findViewById(R.id.tv_order_detail_value)).setText(data.getItemValue());
            mContainer.addView(rlPolicy);
        }


        RelativeLayout policySumDivider = (RelativeLayout) mInflater.inflate(R.layout.item_order_detail_divider, null);
        mContainer.addView(policySumDivider);

        RelativeLayout rlMoreInfo = (RelativeLayout) mInflater.inflate(R.layout.item_order_detail_more, null);
        rlMoreInfo.setOnClickListener(this);
        mContainer.addView(rlMoreInfo);

        if (personSum.size() > 0) {

            RelativeLayout moreivider = (RelativeLayout) mInflater.inflate(R.layout.item_order_detail_divider, null);
            mContainer.addView(moreivider);
        }

        for (int i = 0; i < personSum.size(); i++) {
            RelativeLayout rlPersonSum = (RelativeLayout) mInflater.inflate(R.layout.item_order_detail, null);

            OrderDetailBean.ItemBean data = personSum.get(i);
            ((TextView) rlPersonSum.findViewById(R.id.tv_order_detail_key)).setText(data.getItemKey());
            ((TextView) rlPersonSum.findViewById(R.id.tv_order_detail_value)).setText(data.getItemValue());
            mContainer.addView(rlPersonSum);
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

    private void initView() {
        mInflater = LayoutInflater.from(this);
        rlBack = (RelativeLayout) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_text);
        rlShare = (RelativeLayout) findViewById(R.id.rl_right);
        mContainer = (LinearLayout) findViewById(R.id.ll_container);
        tvCancelOrder = (TextView) findViewById(R.id.tv_cancel_order);
        tvPay = (TextView) findViewById(R.id.tv_pay);
        llOrderDetailDefault = (LinearLayout) findViewById(R.id.ll_order_detail_default);
        llOrderDetailFooter = (LinearLayout) findViewById(R.id.ll_order_detail_footer);

        rlBack.setOnClickListener(this);
        tvTitle.setText("订单详情");
        rlShare.setVisibility(View.INVISIBLE);
        tvCancelOrder.setOnClickListener(this);
        tvPay.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.rl_order_detail_more_info) {
            GuaranteeDetailActivity.actionStart(this, mGuaranteeData);
        } else if (v.getId() == R.id.tv_cancel_order) {
            cancelOrder(orderData.getId());
        } else if (v.getId() == R.id.tv_pay) {

            String payUrl = IpConfig.getUri4("webPay") + "orderid=" + orderData.getId()
                    + "&userid=" + mUserId ;
            Intent intent = new Intent(this, CommonWebActivity.class);
            intent.putExtra("title", "确认支付");
            intent.putExtra("web_address", payUrl);
            intent.putExtra("needShare", false);
            intent.putExtra("isBackRefresh", true);
            startActivity(intent);
            finish();

        }
    }

    private void cancelOrder(final String id) {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);

        builder.setTitle("提示");
        builder.setMessage("是否取消订单");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                String url = IpConfig.getUri2("updateMyOrderModelState");
                OkHttpUtils.post()
                        .url(url)
                        .addParams("userid", mUserId)
                        .addParams("token", mToken)
                        .addParams("id", id)
                        .addParams("status", "cancel")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(OrderDetailActivity.this, "网络连接失败，请确认网络连接后重试",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                String jsonString = response;
                                try {
                                    if (jsonString == null || jsonString.equals("") || jsonString.equals("null")) {
                                        String status = "false";
                                        Message message = Message.obtain();
                                        message.obj = status;


                                    } else {
                                        JSONObject jsonObject = new JSONObject(jsonString);
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
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.create().show();


    }
}
