package com.cloudhome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.BankCardListAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.BindCardSuccessEvent;
import com.cloudhome.event.UnbindCardSuccessEvent;
import com.cloudhome.utils.IpConfig;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;


public class MyBankCardActivity extends BaseActivity {
    private static final String API_GET_CARDS = "getmyBankCards";
    private static final String EXTRA_CARD_INFO = "card_info";
    private static final String SP_USER_ID = "Login_UID";
    private static final String SP_TOKEN = "Login_TOKEN";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.lv_bank_card)
    ListView lvBankCard;
    @BindView(R.id.common_load)
    LinearLayout llCommonLoad;
    private ViewHolder mViewHolder;
    private View mFooterView;
    private String mUrl;
    private HashMap<String, String> mParams;
    private List<HashMap<String, String>> mCardList;
    private BankCardListAdapter mAdapter;
    private PublicLoadPage mLoadPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bank_card);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initView() {
        mFooterView = LayoutInflater.from(this).inflate(R.layout.item_footer_my_bank_card_, null);
        mViewHolder = new ViewHolder(this, mFooterView);
        mLoadPage = new LoadPage(llCommonLoad);
        tvTitle.setText(R.string.activity_my_bank_card_title);
        ivShare.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        mCardList = new ArrayList<>();
        mAdapter = new BankCardListAdapter(this, mCardList);
        lvBankCard.setAdapter(mAdapter);
        mParams = new HashMap<>();
        String userId = sp.getString(SP_USER_ID, "");
        String token = sp.getString(SP_TOKEN, "");
        mParams.put("userId", userId);
        mParams.put("token", token);
        mUrl = IpConfig.getUri2(API_GET_CARDS);
        getBankCards();
    }

    private void getBankCards() {
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
                            if (response == null || "".equals(response) || "null".equals(response)) {
                                mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                                        MyApplication.BUTTON_RELOAD, 1);
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                String errcode = jsonObject.getString("errcode");
                                if ("0".equals(errcode)) {
                                    mCardList.clear();
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject obj = (JSONObject) dataArray.get(i);
                                        parseData(obj);
                                    }
                                    bindData();
                                } else {
                                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                                            MyApplication.BUTTON_RELOAD, 1);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                                    MyApplication.BUTTON_RELOAD, 1);
                        }
                    }
                });
    }

    private void bindData() {
        mLoadPage.loadSuccess(null, null);
        if (lvBankCard.getFooterViewsCount() == 0) {
            lvBankCard.addFooterView(mFooterView);
        }
        mViewHolder.rlAddBankCard.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    private void parseData(JSONObject obj) throws JSONException {
        HashMap<String, String> dataMap = new HashMap();
        dataMap.put("id", obj.getString("id"));
        dataMap.put("bankCardNo", obj.getString("bankCardNo"));
        JSONObject bankbinDtoobj = obj.getJSONObject("bankbinDto");
        dataMap.put("bankColor", bankbinDtoobj.getString("bankColor"));
        dataMap.put("bankName", bankbinDtoobj.getString("bankName"));
        dataMap.put("cardsType", bankbinDtoobj.getString("cardsType"));
        dataMap.put("bankLogoImg", bankbinDtoobj.getString("bankLogoImg"));
        dataMap.put("bankTel", bankbinDtoobj.getString("bankTel"));
        mCardList.add(dataMap);
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnItemClick(R.id.lv_bank_card)
    public void checkCardInfo(int position) {
        HashMap<String, String> cardInfo = mCardList.get(position);
        Intent intent = new Intent(MyBankCardActivity.this, MyBankCardInfoActivity.class);
        intent.putExtra(EXTRA_CARD_INFO, cardInfo);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindCardSuccessEvent event) {
        getBankCards();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UnbindCardSuccessEvent event) {
        getBankCards();
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
                                  ImageView ivLoaded, TextView tvLoaded, Button btnLoad) {
            mCardList.clear();
            getBankCards();
        }
    }

    static class ViewHolder {
        private Context mContext;
        @BindView(R.id.rl_add_card)
        RelativeLayout rlAddBankCard;

        public ViewHolder(Context context, View view) {
            this.mContext = context;
            ButterKnife.bind(this,view);
        }

        @OnClick(R.id.rl_add_card)
        public void addBankCard() {
            Intent intent = new Intent(mContext, BankCardEditActivity.class);
            mContext.startActivity(intent);
        }
    }
}
