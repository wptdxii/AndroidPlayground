package com.cloudhome.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.event.UnbindCardSuccessEvent;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MyBankCardInfoActivity extends BaseActivity {
    private static final String EXTRA_CARD_INFO = "card_info";
    private static final String API_UNBIND = "unBindBankCard";
    private static final String SP_USER_ID = "Login_UID";
    private static final String SP_TOKEN = "Login_TOKEN";
    private static final String PARAM_USER_ID = "userId";
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_BINDED_ID = "bindedId";
    private static final String PARAM_CARD_NUM = "cardNo";
    private static final String KEY_INFO_TEL = "bankTel";
    private static final String KEY_INFO_ID = "id";
    private static final String KEY_INFO_CARD_NUM = "bankCardNo";
    private static final String KEY_INFO_BANK_NAME = "bankName";
    private static final String KEY_INFO_CARD_TYPE = "cardsType";
    private static final String KEY_INFO_BANK_IMG = "bankLogoImg";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.iv_bank_logo)
    ImageView ivBankLogo;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.tv_card_type)
    TextView tvCardType;
    @BindView(R.id.tv_bank_card_num)
    TextView tvBankCardNum;
    @BindView(R.id.tv_service_num)
    TextView tvServiceNum;
    private Map<String, String> mParamsMap;
    private String mCardNum;
    private String mServiceNum;
    private HashMap<String, String> mCardInfoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bank_card_info);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mCardInfoMap = (HashMap<String, String>) intent.getSerializableExtra(EXTRA_CARD_INFO);
        mServiceNum = mCardInfoMap.get(KEY_INFO_TEL);

        mParamsMap = new HashMap<>();
        String userId = sp.getString(SP_USER_ID, "");
        String token = sp.getString(SP_TOKEN, "");
        String bindedId = mCardInfoMap.get(KEY_INFO_ID);
        String bankCardNo = mCardInfoMap.get(KEY_INFO_CARD_NUM);
        mParamsMap.put(PARAM_USER_ID, userId);
        mParamsMap.put(PARAM_TOKEN, token);
        mParamsMap.put(PARAM_BINDED_ID, bindedId);
        mParamsMap.put(PARAM_CARD_NUM, bankCardNo);

        mCardNum = getCardNum(bankCardNo);
    }

    private String getCardNum(String bankCardNo) {
        String rightNum = bankCardNo.substring(bankCardNo.length() - 4, bankCardNo.length());
        String leftAstarisk = "";
        for (int i = 0; bankCardNo.length() - 4 - i > 0; i++) {
            if (i % 4 == 0) {
                leftAstarisk = " " + leftAstarisk;
            }
            leftAstarisk = "*" + leftAstarisk;
        }
        return leftAstarisk + rightNum;
    }

    private void initView() {
        tvTitle.setText(R.string.activity_bank_card_info);
        ivShare.setVisibility(View.INVISIBLE);
        tvBankName.setText(mCardInfoMap.get(KEY_INFO_BANK_NAME));
        tvCardType.setText(mCardInfoMap.get(KEY_INFO_CARD_TYPE));
        tvServiceNum.setText(mServiceNum);
        tvBankCardNum.setText(mCardNum);
        Glide.with(MyBankCardInfoActivity.this)
                .load(mCardInfoMap.get(KEY_INFO_BANK_IMG))
                .placeholder(R.drawable.white_bg)
                .error(R.drawable.bank_logo_moren)
                .into(ivBankLogo);
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.rl_service_tel)
    public void callCustomerService() {
        requestPhonePermission(mServiceNum);
    }

    @OnClick(R.id.tv_unbind)
    public void unbindCard() {
        new CustomDialog.Builder(this)
                .setTitle(getString(R.string.activity_bank_card_info_dialog_title))
                .setMessage(getString(R.string.activity_bank_card_info_dialog_msg))
                .setPositiveButton(getString(R.string.activity_bank_card_info_dialgo_positive),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String unbindUrl = IpConfig.getUri2(API_UNBIND);
                                unBindCard(unbindUrl);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.activity_bank_card_info_dialog_negative),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create()
                .show();
    }

    private void requestPhonePermission(final String mobile) {
        String[] permissions = new String[]{Manifest.permission.CALL_PHONE};
        requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(String.format("tel:%1$s", mobile)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
                showRequestPermissionRationale(
                        getString(R.string.msg_callphone_denied));
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                showPermissionSettingDialog(
                        getString(R.string.msg_callphone_permanent_denied));
            }
        });
    }

    private void unBindCard(String url) {
        OkHttpUtils.post()
                .url(url)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyBankCardInfoActivity.this, R.string.msg_net_retry,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (response == null || "".equals(response)
                                    || "null".equals(response)) {
                                Toast.makeText(MyBankCardInfoActivity.this, R.string.msg_net_retry,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                String errcode = jsonObject.getString("errcode");
                                if (errcode.equals("0")) {
                                    Toast.makeText(MyBankCardInfoActivity.this,
                                            R.string.unbind_success, Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new UnbindCardSuccessEvent());
                                    finish();
                                } else {
                                    String errmsg = jsonObject.getString("errmsg");
                                    Toast.makeText(MyBankCardInfoActivity.this, errmsg,
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
