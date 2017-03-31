package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.InquiryListAdapter;
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
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;

public class InquiryListActivity extends BaseActivity {
    private static final String EXTRA_ORDER_TYPE = "ordertype";
    private static final String SP_USER_ID = "Login_UID";
    private static final String SP_TOKEN = "Login_TOKEN";
    private static final String PARAM_USER_ID = "user_id";
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_STATUS = "status";
    private static final String API_ORDER_LIST = "getOrderList";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.lv_inquiry)
    ListView lvInquiry;
    private Map<String, String> mParamsMap;
    private String mQuoteStatus;
    private List<Map<String, String>> mDataList;
    private InquiryListAdapter mAdapter;
    private Dialog mLoadingDialog;

    public static void activityStart(Context context, String orderType) {
        Intent intent = new Intent(context, InquiryListActivity.class);
        intent.putExtra(EXTRA_ORDER_TYPE, orderType);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_list);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        rlShare.setVisibility(View.INVISIBLE);
        tvTitle.setText(getIntent().getStringExtra(EXTRA_ORDER_TYPE));
        mLoadingDialog = new Dialog(this, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.progress_dialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView progressDialog = (TextView) mLoadingDialog.findViewById(R.id.id_tv_loadingmsg);
        progressDialog.setText(R.string.dialog_progress_msg);
        mDataList = new ArrayList<>();
        mAdapter = new InquiryListAdapter(this, mDataList);
        lvInquiry.setAdapter(mAdapter);
    }

    private void initData() {
        mQuoteStatus = "01";
        String userId = sp.getString(SP_USER_ID, "");
        String token = sp.getString(SP_TOKEN, "");
        mParamsMap = new HashMap<>();
        mParamsMap.put(PARAM_USER_ID, userId);
        mParamsMap.put(PARAM_TOKEN, token);
        requestInquiryList();
    }

    private void requestInquiryList() {
        mLoadingDialog.show();
        mParamsMap.put(PARAM_STATUS, mQuoteStatus);
        String orderListUrl = IpConfig.getUri(API_ORDER_LIST);
        OkHttpUtils.get()
                .url(orderListUrl)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(InquiryListActivity.this, R.string.msg_net_retry,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mLoadingDialog.dismiss();
                        mDataList.clear();
                        try {
                            if (response == null || response.equals("")
                                    || response.equals("null")) {
                                Toast.makeText(InquiryListActivity.this, R.string.msg_net_retry,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray dataList = jsonObject.getJSONArray("data");
                                for (int i = 0; i < dataList.length(); i++) {
                                    JSONObject jsonObject2 = dataList.getJSONObject(i);
                                    Map<String, String> map = new HashMap<>();
                                    Iterator<String> iterator = jsonObject2.keys();
                                    while (iterator.hasNext()) {
                                        String key = iterator.next();
                                        String value = jsonObject2.getString(key);
                                        map.put(key, value);
                                    }
                                    mDataList.add(map);
                                }
                                mAdapter.notifyDataSetChanged();
                                lvInquiry.requestLayout();
                                if (mDataList.size() < 1) {
                                    Toast.makeText(InquiryListActivity.this,
                                            R.string.toast_inquiry_empty, Toast.LENGTH_SHORT).show();
                                }
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

    @OnCheckedChanged({
            R.id.rbtn_to_quote, R.id.rbtn_quoting,
            R.id.rbtn_quote_completed,
            R.id.rbtn_quote_time_out
    })
    public void selectQuoteMode(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rbtn_to_quote:
                    mQuoteStatus = "01";
                    break;
                case R.id.rbtn_quoting:
                    mQuoteStatus = "02";
                    break;
                case R.id.rbtn_quote_completed:
                    mQuoteStatus = "03";
                    break;
                case R.id.rbtn_quote_time_out:
                    mQuoteStatus = "04";
                    break;
                default:
                    break;
            }
            requestInquiryList();
        }
    }

    @OnItemClick(R.id.lv_inquiry)
    public void checkAccurateQuotation(int position) {
        AccurateQuotationActivity.activityStart(this, mDataList.get(position).get("id"), mQuoteStatus);
    }
}
