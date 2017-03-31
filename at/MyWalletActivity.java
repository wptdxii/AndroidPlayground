package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.MyWalletAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.BindCardSuccessEvent;
import com.cloudhome.event.UnbindCardSuccessEvent;
import com.cloudhome.event.WithdrawSuccessEvent;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.ListViewScrollView;
import com.cloudhome.view.customview.PublicLoadPage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;


/**
 * Created by yangguangbaoxian on 2016/5/13.
 */
public class MyWalletActivity extends BaseActivity {
    private static final String PARAM_USER_ID = "userid";
    private static final String PARAM_TOKEN = "token";
    private static final String SP_UID = "Login_UID";
    private static final String SP_TOKEN = "Login_TOKEN";
    private static final String EXTRA_BALANCE = "balance";
    private static final String EXTRA_IS_DEPOSIT = "isdeposit";
    private static final String EXTRA_DEPOSIT_MSG = "depositmsg";
    private static final String EXTRA_CARD_BINDED = "isCardBinded";
    private static final String EXTRA_TYPE = "type";
    private static final String API_INCOME_INFO = "queryUserIncomeLastSixMonth";
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.tv_income_title)
    TextView tvIncomeTitle;
    @BindView(R.id.tv_income_value)
    TextView tvIncomeValue;
    @BindView(R.id.rl_income)
    RelativeLayout rlIncome;
    @BindView(R.id.tv_expend_title)
    TextView tvExpendTitle;
    @BindView(R.id.tv_expend_value)
    TextView tvExpendValue;
    @BindView(R.id.rl_expend)
    RelativeLayout rlExpend;
    @BindView(R.id.tv_card_title)
    TextView tvCardTitle;
    @BindView(R.id.tv_card_count)
    TextView tvCardCount;
    @BindView(R.id.rl_card)
    RelativeLayout rlCard;
    @BindView(R.id.lv_detail)
    ListViewScrollView lvDetail;
    @BindView(R.id.common_load)
    LinearLayout llCommonLoad;
    @BindColor(R.color.red)
    int colorRed;
    @BindColor(R.color.color3)
    int colorGrey;
    private PublicLoadPage mLoadPage;
    private MyWalletAdapter mAdapter;
    private Map<String, String> mParams;
    private String mUrl;
    private List<Map<String, String>> mIncomeInfoList;
    private List<Map<String, String>> mAccountInfoList;
    private String mBalance;
    private String mIsDeposit;
    private String mDepositMsg;
    private boolean mIsCardBinded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
    }

    private void initData() {
        mLoadPage = new LoadPage(llCommonLoad);
        mParams = new HashMap<>();
        String userId = sp.getString(SP_UID, "");
        String token = sp.getString(SP_TOKEN, "");
        mParams.put(PARAM_USER_ID, userId);
        mParams.put(PARAM_TOKEN, token);
        mUrl = IpConfig.getUri2(API_INCOME_INFO);
        mAdapter = new MyWalletAdapter(this);
        mIncomeInfoList = new ArrayList();
        mAccountInfoList = new ArrayList();
        mAdapter.setData(mIncomeInfoList);
        lvDetail.setAdapter(mAdapter);
        getIncomeInfo();
    }

    private void getIncomeInfo() {
        mLoadPage.startLoad();
        OkHttpUtils.post()
                .url(mUrl)
                .params(mParams)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (response == null || response.equals("") || response.equals("null")) {
                                mLoadPage.loadFail(
                                        MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                String errcode = jsonObject.getString("errcode");
                                if (errcode.equals("0")) {
                                    mIncomeInfoList.clear();
                                    mAccountInfoList.clear();
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    parseData(dataObject);
                                    bindData();
                                } else {
                                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                                            MyApplication.BUTTON_RELOAD, 1);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mLoadPage.loadFail(
                                    MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                        }
                    }
                });
    }

    private void parseData(JSONObject dataObject) throws JSONException {
        mBalance = dataObject.getString("balance");
        mIsDeposit = dataObject.getString("isdeposit");
        mDepositMsg = dataObject.getString("depositmsg");
        JSONArray accoutInfoArr = dataObject.getJSONArray("accoutInfo");
        for (int i = 0; i < accoutInfoArr.length(); i++) {
            JSONObject obj = accoutInfoArr.getJSONObject(i);
            Map<String, String> map = new HashMap();
            Iterator<String> iterator = obj.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = obj.getString(key);
                map.put(key, value);
            }
            mAccountInfoList.add(map);
        }
        JSONArray sixmonlist = dataObject.getJSONArray("sixmonlist");
        for (int i = 0; i < sixmonlist.length(); i++) {
            JSONObject jsonObject2 = sixmonlist.getJSONObject(i);
            Map<String, String> map = new HashMap();
            Iterator<String> iterator = jsonObject2.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = jsonObject2.getString(key);
                map.put(key, value);
            }
            mIncomeInfoList.add(map);
        }
    }

    private void bindData() {
        tvBalance.setText(String.format(getString(R.string.activity_my_wallet_rmb), mBalance));
        int size = mAccountInfoList.size();
        if (size == 2) {
            rlIncome.setVisibility(View.VISIBLE);
            rlExpend.setVisibility(View.GONE);
            rlCard.setVisibility(View.VISIBLE);
            Map<String, String> map = mAccountInfoList.get(0);
            Map<String, String> map2 = mAccountInfoList.get(1);
            tvIncomeTitle.setText(map.get("name"));
            tvIncomeValue.setText(map.get("num"));
            tvCardTitle.setText(map2.get("name"));
            tvCardCount.setText(map2.get("num"));
        } else if (size == 3) {
            rlIncome.setVisibility(View.VISIBLE);
            rlExpend.setVisibility(View.VISIBLE);
            rlCard.setVisibility(View.VISIBLE);
            Map<String, String> map = mAccountInfoList.get(0);
            Map<String, String> map2 = mAccountInfoList.get(1);
            Map<String, String> map3 = mAccountInfoList.get(2);
            tvIncomeTitle.setText(map.get("name"));
            tvIncomeValue.setText(map.get("num"));
            tvExpendTitle.setText(map2.get("name"));
            tvExpendValue.setText(map2.get("num"));
            tvCardTitle.setText(map3.get("name"));
            tvCardCount.setText(map3.get("num"));
        }
        mLoadPage.loadSuccess(null, null);
        if ("0".equals(mAccountInfoList.get(size - 1).get("num"))) {
            mIsCardBinded = false;
            tvCardCount.setText(R.string.activity_my_wallet_bind);
            tvCardCount.setTextColor(colorRed);
        } else {
            mIsCardBinded = true;
            tvCardCount.setTextColor(colorGrey);
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.rl_wallet)
    public void checkAccountBalance() {
        Intent intent = new Intent(MyWalletActivity.this, AccountBalanceActivity.class);
        intent.putExtra(EXTRA_BALANCE, mBalance);
        intent.putExtra(EXTRA_IS_DEPOSIT, mIsDeposit);
        intent.putExtra(EXTRA_DEPOSIT_MSG, mDepositMsg);
        intent.putExtra(EXTRA_CARD_BINDED, mIsCardBinded);
        startActivity(intent);
    }

    @OnClick(R.id.rl_income)
    public void checkIncomeDetail() {
        Intent intent = new Intent(MyWalletActivity.this, IncomeExpendDetailActivity.class);
        intent.putExtra(EXTRA_TYPE, getString(R.string.activity_my_wallet_income));
        startActivity(intent);
    }

    @OnClick(R.id.rl_card)
    public void checkBackCard() {
        Intent intent = new Intent(this,
                mIsCardBinded ? MyBankCardActivity.class : BankCardEditActivity.class);
        startActivity(intent);
    }

    @OnItemClick(R.id.lv_detail)
    public void checkItemIncomeDetail(int position) {
        Intent intent = new Intent(MyWalletActivity.this, IncomeExpendDetailActivity2.class);
        intent.putExtra("title", mIncomeInfoList.get(position).get("title"));
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindCardSuccessEvent event) {
        getIncomeInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UnbindCardSuccessEvent event) {
        getIncomeInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WithdrawSuccessEvent event) {
        getIncomeInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class LoadPage extends PublicLoadPage {
        private LoadPage(LinearLayout layout) {
            super(layout);
        }

        @Override
        public void onReLoadCLick(LinearLayout layout, RelativeLayout rl_progress,
                                  ImageView iv_loaded, TextView tv_loaded, Button btLoad) {
            getIncomeInfo();
        }
    }

}
