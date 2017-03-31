package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.adapter.PopBanklistAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.BindCardSuccessEvent;
import com.cloudhome.event.WithdrawSuccessEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.FetchMoney;
import com.cloudhome.network.GetApplyCash;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.PublicLoadPage;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTouch;
import okhttp3.Call;

public class WithdrawActivity extends BaseActivity implements NetResultListener {
    private static final int REQUEST_APPLY_CASH = 0;
    private static final int REQUEST_FETCH_MONEY = 1;
    private static final String API_APPLY_CASH = "applyCash";
    private static final String SP_USER_ID = "Login_UID";
    private static final String SP_TOKEN = "Login_TOKEN";
    private static final String SP_USER_NAME = "USER_NAME";
    private static final String PARAM_USER_ID = "user_id";
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_MOBILE = "mobile";
    private static final String EXTRA_URL = "url";
    private static final String EXTRA_TITLE = "title";
    @BindView(R.id.pop_bank_list)
    View popBankList;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.iv_bank_logo)
    ImageView ivBankLogo;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.tv_bank_num)
    TextView tvBankNum;
    @BindView(R.id.et_withdraw)
    ClearEditText etWithdraw;
    @BindView(R.id.tv_balance_des)
    TextView tvBalanceDes;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.common_load)
    LinearLayout llCommonLoad;
    private PublicLoadPage mLoadPage;
    private Dialog mProgressDialog;
    private PopBanklistAdapter mPopBankListAdapter;
    private Map<String, String> mParamsMap;
    private String mUserId;
    private String mToken;
    private String mPhoneNum;
    private String mAccountId;
    private String mAvailableDeposit;
    private PopupWindow ppwBankList;
    private List<HashMap<String, String>> mBankCardList;
    private String mErrorMsg;
    private boolean mSubmitEnable = false;
    private String mDepositSubmit;
    private float mDepositAviailable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initView() {
        mLoadPage = new LoadPage(llCommonLoad);
        etWithdraw.addTextChangedListener(new WithdrawTextWatcher());
        btnSubmit.setEnabled(false);
        mPopBankListAdapter = new PopBanklistAdapter(this);
        mPopBankListAdapter.setBankPosition(0);
        initProgressDialog();
    }

    private void initProgressDialog() {
        mProgressDialog = new Dialog(this, R.style.progress_dialog);
        mProgressDialog.setContentView(R.layout.progress_dialog);
        mProgressDialog.setCancelable(true);
        mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvDialog = (TextView) mProgressDialog.findViewById(R.id.id_tv_loadingmsg);
        tvDialog.setText(R.string.dialog_progress_msg);
    }

    private void initData() {
        mErrorMsg = "";
        mBankCardList = new ArrayList<>();
        mParamsMap = new HashMap<>();
        mUserId = sp.getString(SP_USER_ID, "");
        mToken = sp.getString(SP_TOKEN, "");
        mPhoneNum = sp.getString(SP_USER_NAME, "");
        mParamsMap.put(PARAM_USER_ID, mUserId);
        mParamsMap.put(PARAM_TOKEN, mToken);
        mParamsMap.put(PARAM_MOBILE, mPhoneNum);

        getApplyCash();
    }

    /**
     * 请求提现接口
     */
    private void withdrawDeposit() {
        FetchMoney fetchMoneyRequest = new FetchMoney(WithdrawActivity.this);
        fetchMoneyRequest.execute(mUserId, mToken, mDepositSubmit, mPhoneNum, mAccountId, mErrorMsg,
                REQUEST_FETCH_MONEY);
        TextView tvDialog = (TextView) mProgressDialog.findViewById(R.id.id_tv_loadingmsg);
        tvDialog.setText(R.string.activity_withdraw_dialog_msg);
        mProgressDialog.show();
    }

    /**
     * 获取本用户的提现详情
     */
    private void getApplyCash() {
        mLoadPage.startLoad();
        GetApplyCash applyCashRequest = new GetApplyCash(this);
        applyCashRequest.execute(mUserId, mToken, REQUEST_APPLY_CASH, mBankCardList);
    }

    private void setApplyCash(String url) {
        OkHttpUtils.post()
                .url(url)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (response == null || response.equals("")
                                    || response.equals("null")) {
                                mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                String errcode = jsonObject.getString("errcode");
                                if (errcode.equals("0")) {
                                    JSONObject obj = jsonObject.getJSONObject("data");
                                    mLoadPage.loadSuccess(null, null);
                                    mAvailableDeposit = obj.getString("ava_money");
                                    tvBalanceDes.setText(String.format(
                                            getString(R.string.withdraw_activity_avilable), mAvailableDeposit));
                                    String result = mAvailableDeposit.replaceAll(",", "");
                                    mDepositAviailable = Float.parseFloat(result);
                                    if ("true".equals(obj.getBoolean("usable") + "")) {
                                        mSubmitEnable = true;
                                    } else {
                                        mSubmitEnable = false;
                                    }
                                } else {
                                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                                            MyApplication.BUTTON_RELOAD, 1);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case REQUEST_APPLY_CASH:
                if (flag == MyApplication.DATA_OK) {
                    String applyCashUrl = IpConfig.getUri(API_APPLY_CASH);
                    setApplyCash(applyCashUrl);
                    mLoadPage.loadSuccess(null, null);
                    Map<String, String> dataMap = mBankCardList.get(0);
                    mAccountId = dataMap.get("id");
                    String imgUrl = dataMap.get("bankLogoImg");
                    Glide.with(WithdrawActivity.this)
                            .load(imgUrl)
                            .centerCrop()
                            .placeholder(R.drawable.white_bg)
                            .error(R.drawable.bank_logo_moren)
                            .crossFade()
                            .into(ivBankLogo);
                    tvBankName.setText(dataMap.get("bankName"));
                    String cardNum = dataMap.get("bankCardNo");
                    cardNum = getCardNum(cardNum);
                    tvBankNum.setText(cardNum);

                } else if (flag == MyApplication.NET_ERROR) {
                    mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.JSON_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.DATA_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                }
                break;

            case REQUEST_FETCH_MONEY:
                mProgressDialog.dismiss();
                if (flag == MyApplication.DATA_OK) {
                    btnSubmit.setEnabled(false);
                    mSubmitEnable = false;
                    mErrorMsg = dataObj.toString();
                    CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                    builder.setTitle(getString(R.string.activity_bank_card_info_dialog_title));
                    builder.setMessage(mErrorMsg);
                    builder.setPositiveButton(getString(R.string.activity_bank_card_info_dialgo_positive),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    EventBus.getDefault().post(new WithdrawSuccessEvent());
                                    finish();
                                }
                            });
                    builder.create().show();
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(this, R.string.toast_net_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.toast_activity_withdrw_retry, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * get bank card num with asterisk
     *
     * @param cardNum
     * @return
     */
    private String getCardNum(String cardNum) {
        String rightNum = cardNum.substring(cardNum.length() - 4, cardNum.length());
        String leftAsterisk = "";
        for (int i = 0; cardNum.length() - 4 - i > 0; i++) {
            if (i % 4 == 0) {
                leftAsterisk = " " + leftAsterisk;
            }
            leftAsterisk = "*" + leftAsterisk;
        }
        return leftAsterisk + rightNum;
    }

    @OnClick(R.id.iv_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.tv_bar_statement)
    public void checkWithdrawRuels() {
        String url = IpConfig.getUri("applyCashRules");
        Intent intent = new Intent(this, ProtocolWebActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, getString(R.string.activity_withdraw_extra_rule));
        startActivity(intent);
    }

    @OnClick(R.id.rl_bank_card)
    public void showCardList() {
        RelativeLayout popBankListLayout = ((RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.pop_withdraw_card_list, null));
        ViewHolder popViewHolder = new ViewHolder(popBankListLayout);
        if (mBankCardList.size() >= 5) {
            int mHeight = Common.dip2px(WithdrawActivity.this, 222f);
            ViewGroup.LayoutParams layoutParams = popViewHolder.lvPopBankList.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = mHeight;
            popViewHolder.lvPopBankList.setLayoutParams(layoutParams);
        }
        mPopBankListAdapter.setData(mBankCardList);
        popViewHolder.lvPopBankList.setAdapter(mPopBankListAdapter);
        ppwBankList = new PopupWindow(WithdrawActivity.this);
        ppwBankList.setBackgroundDrawable(new ColorDrawable(0x00000000));
        ppwBankList.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        ppwBankList.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        ppwBankList.setTouchable(true);
        ppwBankList.setOutsideTouchable(true);
        ppwBankList.setFocusable(true);
        ppwBankList.setContentView(popBankListLayout);
        ppwBankList.showAsDropDown(popBankList);
    }

    @OnClick(R.id.tv_withdraw_all)
    public void withDrawAll() {
        String money;
        if (mAvailableDeposit.contains(",")) {
            money = mAvailableDeposit.replaceAll(",", "");
            etWithdraw.setText(money);
        } else {
            etWithdraw.setText(mAvailableDeposit);
        }
        Editable editable = etWithdraw.getText();
        int selEndIndex = editable.length();
        Selection.setSelection(editable, selEndIndex);
    }

    @OnClick(R.id.btn_submit)
    public void submit() {
        mDepositSubmit = etWithdraw.getText().toString();
        if (mDepositSubmit.equals("") || mDepositSubmit.equals("null")) {
            CustomDialog.Builder builder = new CustomDialog.Builder(
                    WithdrawActivity.this);
            builder.setTitle(R.string.activity_withdraw_dialog_title);
            builder.setMessage(getString(R.string.activity_withdraw_dialog_amout));
            builder.setPositiveButton(getString(R.string.activity_bank_card_info_dialgo_positive),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
            float edit_text_Money = Float.parseFloat(mDepositSubmit);
            if (edit_text_Money == 0) {
                CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                builder.setTitle(R.string.activity_withdraw_dialog_title);
                builder.setMessage(getString(R.string.withdraw_dialog_limit_zero));
                builder.setPositiveButton(getString(R.string.activity_bank_card_info_dialgo_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            } else if (edit_text_Money < 50) {
                CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                builder.setTitle(R.string.activity_withdraw_dialog_title);
                builder.setMessage(getString(R.string.withdraw_dialog_limit_greater));
                builder.setPositiveButton(getString(R.string.activity_bank_card_info_dialgo_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            } else if (edit_text_Money > mDepositAviailable) {
                CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                builder.setTitle(R.string.activity_withdraw_dialog_title);
                builder.setMessage(getString(R.string.withdraw_dialog_limit_less));
                builder.setPositiveButton(getString(R.string.activity_bank_card_info_dialgo_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            } else if (edit_text_Money > 800) {
                CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                builder.setTitle(getString(R.string.activity_withdraw_dialog_title));
                builder.setMessage(getString(R.string.withdraw_dialog_limit_tax));
                builder.setNegativeButton(getString(R.string.activity_withdraw_dialog_negative),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton(getString(R.string.activity_withdraw_dialog_limit_positive)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                withdrawDeposit();
                            }
                        });
                builder.create().show();
            } else {
                withdrawDeposit();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindCardSuccessEvent event) {
        mPopBankListAdapter.setBankPosition(0);
        mBankCardList.clear();
        mPopBankListAdapter.notifyDataSetChanged();
        if (ppwBankList != null) {
            ppwBankList.dismiss();
            ppwBankList = null;
        }
        getApplyCash();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class LoadPage extends PublicLoadPage {
        public LoadPage(LinearLayout loadLayout) {
            super(loadLayout);
        }

        @Override
        public void onReLoadCLick(LinearLayout layout, RelativeLayout rl_progress, ImageView iv_loaded,
                                  TextView tv_loaded, Button btLoad) {
            getApplyCash();
        }
    }

    private class WithdrawTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().contains(".")) {
                if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                    s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                    etWithdraw.setText(s);
                    etWithdraw.setSelection(s.length());
                }
            }
            if (s.toString().trim().substring(0).equals(".")) {
                s = "0" + s;
                etWithdraw.setText(s);
                etWithdraw.setSelection(2);
            }
            if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(".")) {
                    etWithdraw.setText(s.subSequence(0, 1));
                    etWithdraw.setSelection(1);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (TextUtils.isEmpty(text) || text.equals("null") || !mSubmitEnable) {
                btnSubmit.setEnabled(false);
            } else {
                btnSubmit.setEnabled(true);
            }
        }
    }

    class ViewHolder {
        @BindView(R.id.lv_pop_bank_list)
        ListView lvPopBankList;
        @BindView(R.id.rl_pop_bank_list)
        RelativeLayout rlPopBankList;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.iv_pop_cancel)
        public void cancel() {
            ppwBankList.dismiss();
            ppwBankList = null;
        }

        @OnClick(R.id.rl_add_bank)
        public void addBank() {
            Intent intent = new Intent(WithdrawActivity.this, BankCardEditActivity.class);
            startActivity(intent);
        }

        @OnItemClick(R.id.lv_pop_bank_list)
        public void selectBank(AdapterView<?> arg0, int pos) {
            mPopBankListAdapter.setBankPosition(pos);
            arg0.setVisibility(View.VISIBLE);
            Map<String, String> Map = mBankCardList.get(pos);
            mAccountId = Map.get("id");
            String url = Map.get("bankLogoImg");
            Glide.with(WithdrawActivity.this)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.white_bg)
                    .error(R.drawable.bank_logo_moren)
                    .crossFade()
                    .into(ivBankLogo);
            tvBankName.setText(Map.get("bankName"));
            String cardNum = Map.get("bankCardNo");
            cardNum = getCardNum(cardNum);
            tvBankNum.setText(cardNum);
            ppwBankList.dismiss();
            ppwBankList = null;
        }

        @OnTouch(R.id.rl_pop_bank_list)
        public boolean touchPopupWidnow(MotionEvent event) {
            int heightBottom = rlPopBankList.getBottom();
            int heightTop = rlPopBankList.getTop();
            int heightLeft = rlPopBankList.getLeft();
            int heightRight = rlPopBankList.getRight();

            int y = (int) event.getY();
            int x = (int) event.getX();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (y > heightBottom || y > heightTop || x < heightLeft
                        || x > heightRight) {
                    ppwBankList.dismiss();
                    ppwBankList = null;
                }
            }
            return true;
        }
    }
}
