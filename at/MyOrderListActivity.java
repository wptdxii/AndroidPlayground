package com.cloudhome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.PublicLoadPage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;

/**
 * 我的订单 Activity
 */
public class MyOrderListActivity extends BaseActivity {
    private static final String EXTRA_DISORDER_JUMP = "needDisorderJump";
    private static final String API_MY_ORDER_LIST = "queryordercodelist";
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.common_load)
    LinearLayout llCommonLoad;
    @BindView(R.id.lv_order_list)
    ListView lvOrderList;
    private List<String> mDataList;
    boolean mNeedDisorderJump;
    private PublicLoadPage mLoadPage;

    public static void activityStart(Context context, boolean needDisorderJump) {
        Intent intent = new Intent(context, MyOrderListActivity.class);
        intent.putExtra(EXTRA_DISORDER_JUMP, needDisorderJump);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_list);
        ButterKnife.bind(this);
        initView();
        requestOrderList();
        mNeedDisorderJump = getIntent().getBooleanExtra(EXTRA_DISORDER_JUMP, false);
    }

    private void initView() {
        rlShare.setVisibility(View.INVISIBLE);
        tvTitle.setText(R.string.activity_my_order_list_title);
        mLoadPage = new PublicLoadPage(llCommonLoad) {
            @Override
            public void onReLoadCLick(LinearLayout layout, RelativeLayout lrProgress,
                                      ImageView ivLoaded, TextView tvLoaded, Button btnLoad) {
                requestOrderList();
            }
        };
    }

    private void requestOrderList() {
        String orderListUrl = IpConfig.getUri2(API_MY_ORDER_LIST);
        mLoadPage.startLoad();
        OkHttpUtils.get()
                .url(orderListUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mLoadPage.loadFail(getString(R.string.msg_no_data),
                                getString(R.string.msg_reload), 0);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (!(response == null || response.equals("") || response.equals("null"))) {
                                JSONObject jsonObject = new JSONObject(response);
                                String errcode = jsonObject.getString("errcode");
                                if (errcode.equals("0")) {
                                    mDataList = new ArrayList<>();
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        mDataList.add(dataArray.getJSONObject(i).get("name").toString());
                                    }

                                    mLoadPage.loadSuccess(null, null);
                                    String[] ordertypeArray = mDataList.toArray(new String[mDataList.size()]);
                                    lvOrderList.setAdapter(new ArrayAdapter<>(MyOrderListActivity.this,
                                            R.layout.item_my_order_list, R.id.tv_title, ordertypeArray));
                                } else {
                                    mLoadPage.loadFail(getString(R.string.msg_fetch_failure),
                                            getString(R.string.msg_reload), 1);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mLoadPage.loadFail(getString(R.string.msg_fetch_failure),
                                    getString(R.string.msg_reload), 1);
                        }
                    }
                });
    }

    @OnClick(R.id.rl_back)
    public void back() {
        if (mNeedDisorderJump) {
            EventBus.getDefault().post(new DisorderJumpEvent(3));
            Intent intent = new Intent(this, AllPageActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @OnItemClick(R.id.lv_order_list)
    public void checkOrderList(int position) {
        String orderType = mDataList.get(position);
        Intent intent;
        switch (orderType) {
            case "个险":
            case "卡单":
                IndividualInsuranceActivity.activityStart(this, orderType);
                break;
            case "车险":
                CarInsuranceOrderListActivity.activityStart(this, orderType);
                break;
            case "赠险":
                GiftInsuranceOrderListActivity.activityStart(this, orderType);
                break;
            case "询价单":
                InquiryListActivity.activityStart(this, orderType);
                break;
            default:
                break;
        }
    }
}
