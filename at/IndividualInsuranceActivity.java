package com.cloudhome.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.OrderListAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.OrderListBean;
import com.cloudhome.event.CancelIndividualEvent;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.event.RefreshEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.QueryMobileOrderList;
import com.cloudhome.utils.Constants;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.OrderLoadPage;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.xlistview.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;

/**
 * 个险和卡单 Activity
 */
public class IndividualInsuranceActivity extends BaseActivity
        implements NetResultListener, XListView.IXListViewListener {
    private static final String API_CANCEL_ORDER = "updateMyOrderModelState";
    private static final String EXTRA_ORDER_TYPE = "ordertype";
    private static final String PARAM_USER_ID = "userid";
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_STATUS = "status";
    private static final String PARAM_ID = "id";
    private final int ACTION_ORDER_LIST = 0;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.rbtn_unclaimed)
    RadioButton rbtnUnclaimed;
    @BindView((R.id.rbtn_dealing))
    RadioButton rbtnDealing;
    @BindView((R.id.rbtn_completed))
    RadioButton rbtnCompleted;
    @BindView(R.id.lv_order_list)
    XListView lvOrderList;
    @BindView(R.id.common_load)
    RelativeLayout rlCommonLoad;
    private OrderLoadPage mLoadPage;
    private List<OrderListBean> mDataList;
    private OrderListAdapter mAdapter;
    private String mUserId;
    private String mToken;
    private String mOrderType;
    private String mOrderStatus;
    private int mPageNum;
    private List<String> mRadioBtnNameList;
    private int mListSize;
    private Map<String, String> mParamsMap;
    private boolean mIsRefreshing;
    private boolean mIsLoadingMore;

    public static void activityStart(Context context, String orderType) {
        Intent intent = new Intent(context, IndividualInsuranceActivity.class);
        intent.putExtra(EXTRA_ORDER_TYPE, orderType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_insurance);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
        mLoadPage.startLoad();
        requestOrderList();
    }

    private void initData() {
        mParamsMap = new HashMap<>();
        mUserId = sp.getString(Constants.SP.USER_ID, "");
        mToken = sp.getString(Constants.SP.TOKEN, "");
        mParamsMap.put(PARAM_USER_ID, mUserId);
        mParamsMap.put(PARAM_TOKEN, mToken);
        mParamsMap.put(PARAM_STATUS, "cancel");
        mOrderType = getIntent().getStringExtra(EXTRA_ORDER_TYPE);
        mOrderStatus = "全部";
        mDataList = new ArrayList<>();
        mRadioBtnNameList = new ArrayList<>();
        mAdapter = new OrderListAdapter(this, mDataList, mUserId);
        mIsRefreshing = false;
        mIsLoadingMore = false;
        resetData();
    }

    private void resetData() {
        mPageNum = 0;
        mListSize = 0;
        if (mDataList != null) {
            mDataList.clear();
        }
    }


    private void initView() {
        lvOrderList.setPullLoadEnable(false);
        lvOrderList.setXListViewListener(this);
        rlShare.setVisibility(View.INVISIBLE);
        tvTitle.setText(mOrderType);
        rbtnUnclaimed.setClickable(false);
        rbtnDealing.setClickable(false);
        rbtnCompleted.setClickable(false);
        mLoadPage = new OrderLoadPage(rlCommonLoad) {
            @Override
            public void onReLoadCLick(RelativeLayout layout, LinearLayout rlProgress, ImageView ivLoaded,
                                      TextView tvLoaded, Button btLoad) {
                mLoadPage.startLoad();
                mPageNum = 0;
                mListSize = 0;
                mDataList.clear();
                mOrderStatus = "全部";
                requestOrderList();
            }

            @Override
            public void onToShopMallCLick() {
                EventBus.getDefault().post(new DisorderJumpEvent(0));
                Intent intent = new Intent(IndividualInsuranceActivity.this, AllPageActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    /**
     * 请求订单列表
     */
    private void requestOrderList() {
        QueryMobileOrderList queryMobileOrderList = new QueryMobileOrderList(this);
        queryMobileOrderList.execute(mUserId, ACTION_ORDER_LIST, mDataList, String.valueOf(mPageNum),
                mOrderType, mOrderStatus, mRadioBtnNameList, mToken);
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case ACTION_ORDER_LIST:
                if (mIsRefreshing) {
                    mIsRefreshing = false;
                    lvOrderList.stopRefresh();
                }
                if (mIsLoadingMore) {
                    mIsLoadingMore = false;
                    lvOrderList.stopLoadMore();
                }
                lvOrderList.setPullLoadEnable(true);
                lvOrderList.setRefreshTime(getCurrentTime());
                if (flag == MyApplication.DATA_OK) {
                    if (mRadioBtnNameList != null && mRadioBtnNameList.size() >= 3) {
                        rbtnUnclaimed.setClickable(true);
                        rbtnDealing.setClickable(true);
                        rbtnCompleted.setClickable(true);
                        rbtnUnclaimed.setText(mRadioBtnNameList.get(0));
                        rbtnDealing.setText(mRadioBtnNameList.get(1));
                        rbtnCompleted.setText(mRadioBtnNameList.get(2));
                    }
                    if (mPageNum == 0) {
                        if (lvOrderList.getAdapter() == null) {
                            lvOrderList.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mDataList.size() < 1) {
                            mLoadPage.loadSuccess(null, null, 1);
                        } else {
                            mLoadPage.loadSuccess(null, null, 0);
                        }
                    } else {
                        mLoadPage.loadSuccess(null, null, 0);
                        mAdapter.notifyDataSetChanged();
                        if (mDataList.size() == mListSize) {
                            Toast.makeText(IndividualInsuranceActivity.this, R.string.msg_no_more_data,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (mDataList.size() > mListSize) {
                        mPageNum++;
                    }
                    mListSize = mDataList.size();
                } else if (flag == MyApplication.NET_ERROR) {
                    mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    mLoadPage.loadFail(MyApplication.NO_DATA, MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.JSON_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.DATA_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                }
                break;

        }
    }

    private String getCurrentTime() {
        return SimpleDateFormat.getTimeInstance().format(new Date());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CancelIndividualEvent event) {
        final String orderNum = event.getOrderNum();
        new CustomDialog.Builder(this)
                .setTitle(R.string.prompt)
                .setMessage(R.string.activity_individual_insurance_msg_dialog)
                .setPositiveButton(R.string.msg_dialog_positive_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestCancelOrder(orderNum);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.msg_dialog_negative_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * 取消订单
     *
     * @param orderNum
     */
    private void requestCancelOrder(String orderNum) {
        String url = IpConfig.getUri2(API_CANCEL_ORDER);
        mParamsMap.put(PARAM_ID, orderNum);
        OkHttpUtils.get()
                .url(url)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(IndividualInsuranceActivity.this, R.string.msg_net_retry,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (!(response == null || response.equals("") || response.equals("null"))) {
                                JSONObject jsonObject = new JSONObject(response);
                                String errcode = jsonObject.getString("errcode");
                                if ("0".equals(errcode)) {
                                    resetData();
                                    mLoadPage.startLoad();
                                    mAdapter.notifyDataSetChanged();
                                    requestOrderList();
                                } else {
                                    String errmsg = jsonObject.getString("errmsg");
                                    Toast.makeText(IndividualInsuranceActivity.this, errmsg,
                                            Toast.LENGTH_SHORT).show();
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

    @OnItemClick(R.id.lv_order_list)
    public void checkOrderDetail(int position) {
        OrderListBean bean = (OrderListBean) mAdapter.getItem(position - lvOrderList.getHeaderViewsCount());
        OrderDetailActivity.activityStart(this, bean);
    }

    @OnCheckedChanged({R.id.rbtn_unclaimed, R.id.rbtn_dealing, R.id.rbtn_completed})
    public void select(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            resetData();
            lvOrderList.setPullLoadEnable(false);
            mAdapter.notifyDataSetChanged();
            switch (buttonView.getId()) {
                case R.id.rbtn_unclaimed:
                    mOrderStatus = mRadioBtnNameList.get(0);
                    break;
                case R.id.rbtn_dealing:
                    mOrderStatus = mRadioBtnNameList.get(1);
                    break;
                case R.id.rbtn_completed:
                    mOrderStatus = mRadioBtnNameList.get(2);
                    break;
                default:
                    break;
            }
            requestOrderList();
        }
    }

    @Override
    public void onRefresh() {
        if (!mIsRefreshing) {
            mIsRefreshing = true;
            lvOrderList.setPullLoadEnable(false);
            lvOrderList.postDelayed(new Runnable() {
                public void run() {
                    resetData();
                    requestOrderList();
                }
            }, 2000);
        }
    }

    @Override
    public void onLoadMore() {
        if (!mIsLoadingMore) {
            mIsLoadingMore = true;
            lvOrderList.postDelayed(new Runnable() {
                public void run() {
                    requestOrderList();
                }
            }, 2000);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        resetData();
        mLoadPage.startLoad();
        mAdapter.notifyDataSetChanged();
        requestOrderList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
