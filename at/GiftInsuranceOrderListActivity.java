package com.cloudhome.activity;

import android.content.Context;
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
import com.cloudhome.adapter.GiftOrderListAdapter;
import com.cloudhome.adapter.GiftUnReceiveListAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.GiftUnclaimedBean;
import com.cloudhome.bean.OrderListBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetGiftOrderLeft;
import com.cloudhome.network.QueryMobileOrderList;
import com.cloudhome.utils.Constants;
import com.cloudhome.view.customview.PublicLoadPage;
import com.cloudhome.view.xlistview.XListView;
import com.umeng.socialize.UMShareAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class GiftInsuranceOrderListActivity
        extends BaseActivity implements NetResultListener, XListView.IXListViewListener {
    private static final String EXTRA_ORDER_TYPE = "ordertype";
    private static final String SP_USER_ID = "Login_UID";
    private static final String SP_TOKEN = "Login_TOKEN";
    private final int REQUEST_ORDER_LIST = 0;
    private final int REQUEST_UNCLAIMED_LSIT = 1;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.lv_order_list)
    XListView lvOrderList;
    @BindView(R.id.common_load)
    LinearLayout llCommonLoad;
    @BindView(R.id.rbtn_unclaimed)
    RadioButton rbtnUnclaimed;
    @BindView(R.id.rbtn_dealing)
    RadioButton rbtnDealing;
    @BindView(R.id.rbtn_completed)
    RadioButton rbtnCompleted;
    private String mParamStatus;
    private String mUserId;
    private String mToken;
    private int mPageNum;
    public String mOrderType;
    private List<GiftUnclaimedBean> mUnclaimedList;
    private GiftUnReceiveListAdapter mUnclaimedAdapter;
    private List<OrderListBean> mOrderList;
    private GiftOrderListAdapter mOrderListAdapter;
    private ArrayList<String> mRadioBtnNameList;
    private int mListSize;
    private PublicLoadPage mLoadPage;
    private boolean mIsRefreshing;
    private boolean mIsLoadingMore;

    public static void activityStart(Context context, String orderType) {
        Intent intent = new Intent(context, GiftInsuranceOrderListActivity.class);
        intent.putExtra(EXTRA_ORDER_TYPE, orderType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_insurance_order_list);
        ButterKnife.bind(this);
        initData();
        initView();
        mLoadPage.startLoad();
        requestList();
    }

    private void initData() {
        mParamStatus = "未领取";
        mUserId = sp.getString(SP_USER_ID, "");
        mToken = sp.getString(SP_TOKEN, "");
        mOrderType = getIntent().getStringExtra(EXTRA_ORDER_TYPE);
        mUnclaimedList = new ArrayList<>();
        mUnclaimedAdapter = new GiftUnReceiveListAdapter(this, mUnclaimedList);
        mOrderList = new ArrayList<>();
        mRadioBtnNameList = new ArrayList<>();
        mOrderListAdapter = new GiftOrderListAdapter(this, mOrderList);
        mIsRefreshing = false;
        mIsLoadingMore = false;
        resetData();
    }

    private void resetData() {
        mPageNum = 0;
        mListSize = 0;
        if (mUnclaimedList != null) {
            mUnclaimedList.clear();
        }
        if (mOrderList != null) {
            mOrderList.clear();
        }
        if (mRadioBtnNameList != null) {
            mRadioBtnNameList.clear();
        }
    }

    private void initView() {
        lvOrderList.setPullLoadEnable(false);
        lvOrderList.setXListViewListener(this);
        rlShare.setVisibility(View.INVISIBLE);
        tvTitle.setText(mOrderType);
        rbtnUnclaimed.setText("未领取");
        rbtnDealing.setText("处理中");
        rbtnCompleted.setText("已出单");
        lvOrderList.setAdapter(mUnclaimedAdapter);
        mLoadPage = new PublicLoadPage(llCommonLoad) {
            @Override
            public void onReLoadCLick(LinearLayout layout, RelativeLayout rlProgress, ImageView ivLoaded,
                                      TextView tvLoaded, Button btnLoad) {
                resetData();
                mLoadPage.startLoad();
                requestList();
            }
        };
    }

    /**
     * 获取列表数据
     */
    private void requestList() {
        if ("未领取".equals(mParamStatus)) {
            GetGiftOrderLeft requestUnclaimedList;
            requestUnclaimedList = new GetGiftOrderLeft(this);
            requestUnclaimedList.execute(mUserId, String.valueOf(mPageNum), REQUEST_UNCLAIMED_LSIT, mUnclaimedList, mToken);
        } else {
            QueryMobileOrderList mQueryMobileOrderList;
            mQueryMobileOrderList = new QueryMobileOrderList(this);
            mQueryMobileOrderList.execute(mUserId, REQUEST_ORDER_LIST, mOrderList, String.valueOf(mPageNum), mOrderType,
                    mParamStatus, mRadioBtnNameList, mToken);
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        if (mIsRefreshing) {
            mIsRefreshing = false;
            lvOrderList.stopRefresh();
        }
        if (mIsLoadingMore) {
            mIsLoadingMore = false;
            lvOrderList.stopLoadMore();
        }
        lvOrderList.setRefreshTime(getCurrentTime());
        lvOrderList.setPullLoadEnable(true);
        switch (action) {
            case REQUEST_ORDER_LIST:
                if (flag == Constants.REQUEST.DATA_OK) {
                    mLoadPage.loadSuccess(null, null);
                    lvOrderList.stopLoadMore();
                    mOrderListAdapter.notifyDataSetChanged();
                    if (mPageNum == 0) {
                        if (mOrderList.size() < 1) {
                            Toast.makeText(GiftInsuranceOrderListActivity.this,
                                    R.string.activity_gift_insurance_no_list, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (mOrderList.size() == mListSize) {
                            Toast.makeText(GiftInsuranceOrderListActivity.this,
                                    R.string.msg_no_more_data, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (mOrderList.size() > mListSize) {
                        mPageNum++;
                    }
                    mListSize = mOrderList.size();
                }
                break;
            case REQUEST_UNCLAIMED_LSIT:
                if (flag == Constants.REQUEST.DATA_OK) {
                    mLoadPage.loadSuccess(null, null);
                    lvOrderList.stopLoadMore();
                    mUnclaimedAdapter.notifyDataSetChanged();
                    if (mPageNum == 0) {
                        if (mUnclaimedList.size() < 1) {
                            Toast.makeText(GiftInsuranceOrderListActivity.this,
                                    R.string.activity_gift_insurance_no_list, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (mUnclaimedList.size() == mListSize) {
                            Toast.makeText(this, R.string.msg_no_more_data, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (mUnclaimedList.size() > mListSize) {
                        mPageNum++;
                    }
                    mListSize = mUnclaimedList.size();
                }
                break;
            default:
                break;
        }
        if (flag == Constants.REQUEST.NET_ERROR) {
            mLoadPage.loadFail(getString(R.string.msg_net_error), getString(R.string.msg_reload), 0);
        } else if (flag == Constants.REQUEST.DATA_EMPTY) {
            mLoadPage.loadFail(getString(R.string.msg_no_data), getString(R.string.msg_reload), 1);
        } else if (flag == Constants.REQUEST.JSON_ERROR || flag == Constants.REQUEST.DATA_ERROR) {
            mLoadPage.loadFail(getString(R.string.msg_fetch_failure), getString(R.string.msg_reload), 1);
        }
    }

    private String getCurrentTime() {
        return SimpleDateFormat.getTimeInstance().format(new Date());
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnCheckedChanged({R.id.rbtn_unclaimed, R.id.rbtn_dealing, R.id.rbtn_completed})
    public void select(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            resetData();
            lvOrderList.setPullLoadEnable(false);
            mUnclaimedAdapter.notifyDataSetChanged();
            mOrderListAdapter.notifyDataSetChanged();
            switch (buttonView.getId()) {
                case R.id.rbtn_unclaimed:
                    mParamStatus = "未领取";
                    lvOrderList.setAdapter(mUnclaimedAdapter);
                    break;
                case R.id.rbtn_dealing:
                    mParamStatus = "处理中";
                    lvOrderList.setAdapter(mOrderListAdapter);
                    break;
                case R.id.rbtn_completed:
                    mParamStatus = "已出单";
                    lvOrderList.setAdapter(mOrderListAdapter);
                    break;
                default:
                    break;
            }
            requestList();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!MyApplication.prepay_id.equals("")) {
            resetData();
            mParamStatus = "未领取";
            mUnclaimedAdapter.notifyDataSetChanged();
            mLoadPage.startLoad();
            requestList();
        }
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        if (!mIsRefreshing) {
            mIsRefreshing = true;
            lvOrderList.setPullLoadEnable(false);
            lvOrderList.postDelayed(new Runnable() {
                public void run() {
                    resetData();
                    requestList();
                }
            }, 2000);
        }
    }

    /**
     * 上拉加载更多或点击查看更多
     */
    @Override
    public void onLoadMore() {
        if (!mIsLoadingMore) {
            mIsLoadingMore = true;
            lvOrderList.postDelayed(new Runnable() {
                public void run() {
                    requestList();
                }
            }, 2000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
