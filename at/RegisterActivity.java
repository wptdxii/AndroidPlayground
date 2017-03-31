package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.CheckCode;
import com.cloudhome.network.GetVerifyCode;
import com.cloudhome.utils.Constants;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.utils.RegexUtils;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegisterActivity extends BaseActivity implements NetResultListener {
    private static final String EXTRA_IS_FROM_LOGIN = "isFromLogin";
    public static final int GET_REGISTER_CODE = 1;
    private static final int CHECK_CODE = 2;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.et_phone_num)
    ClearEditText etPhoneNum;
    @BindView(R.id.tv_get_verify_code)
    TextView tvGetVerifyCode;
    @BindView(R.id.et_verify_code)
    ClearEditText etVerifyCode;
    @BindView(R.id.rl_verify_code)
    RelativeLayout rlVerifyCode;
    @BindView(R.id.rl_protocol)
    RelativeLayout rlProtocol;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindColor(R.color.color9)
    int colorGray;
    @BindColor(R.color.title_blue)
    int colorBlue;
    private Dialog mLoadingDialog;
    private boolean mIsFromLogin;
    private String mPhoneNum;
    private String mVerifyCode;

    public static void activityStart(Context context, boolean isFromLogin) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra(EXTRA_IS_FROM_LOGIN, isFromLogin);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        mIsFromLogin = getIntent().getBooleanExtra(EXTRA_IS_FROM_LOGIN, false);
        tvTitle.setText(R.string.activity_register_title);
        btnAction.setText(R.string.activity_register_action);

        mLoadingDialog = new Dialog(this, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.progress_dialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvDialog = (TextView) mLoadingDialog.findViewById(R.id.id_tv_loadingmsg);
        tvDialog.setText(R.string.msg_dialog_later);
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.tv_protocol)
    public void checkProtocol() {
        String url = Uri.parse(IpConfig.getIp3()).buildUpon()
                .appendEncodedPath("/templates/steward/service.tpl.php")
                .build()
                .toString();
        ProtocolWebActivity.activityStart(this, "保客云集服务协议", url);
    }

    @OnClick(R.id.btn_action)
    public void login() {
        if (mIsFromLogin) {
            finish();
        } else {
            LoginActivity.activityStart(this, true);
        }
    }

    @OnClick(R.id.btn_register)
    public void register() {
        mPhoneNum = etPhoneNum.getText().toString().trim();
        mVerifyCode = etVerifyCode.getText().toString().trim();
        if (!RegexUtils.isMobileNO(mPhoneNum)) {
            showPromptDialog(getString(R.string.dialog_check_num));
        } else if (mVerifyCode.length() < 6) {
            showPromptDialog(getString(R.string.dialog_check_verify_code));
        } else {
            mLoadingDialog.show();
            CheckCode checkCode = new CheckCode(this);
            checkCode.execute("quick_register", mPhoneNum, mVerifyCode, CHECK_CODE);
        }
    }

    @OnClick(R.id.tv_get_verify_code)
    public void getVerifyCode() {
        String phoneNum = etPhoneNum.getText().toString().trim();
        if (RegexUtils.isMobileNO(phoneNum)) {
            GetVerifyCode getVerifyCode = new GetVerifyCode(this);
            getVerifyCode.execute("quick_register", phoneNum, "false", GET_REGISTER_CODE);
        } else {
            showPromptDialog(getString(R.string.dialog_check_num));
        }
    }

    private void showPromptDialog(String message) {
        new CustomDialog.Builder(RegisterActivity.this)
                .setTitle(R.string.prompt)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_REGISTER_CODE:
                if (flag == Constants.REQUEST.DATA_OK) {
                    TimeCount timeCount = new TimeCount(90000, 1000);
                    timeCount.start();
                } else if (flag == Constants.REQUEST.NET_ERROR) {
                    Toast.makeText(RegisterActivity.this, R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                } else if (flag == Constants.REQUEST.DATA_EMPTY || flag == Constants.REQUEST.JSON_ERROR
                        || flag == Constants.REQUEST.DATA_ERROR) {
                    showPromptDialog(dataObj.toString());
                }
                break;
            case CHECK_CODE:
                mLoadingDialog.dismiss();
                if (flag == Constants.REQUEST.DATA_OK) {
                    RegisterPasswordActivity.activityStart(this, mPhoneNum, mVerifyCode);
                } else if (flag == Constants.REQUEST.NET_ERROR) {
                    Toast.makeText(RegisterActivity.this, R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                } else if (flag == Constants.REQUEST.DATA_EMPTY || flag == Constants.REQUEST.JSON_ERROR
                        || flag == Constants.REQUEST.DATA_ERROR) {
                    showPromptDialog(dataObj.toString());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 倒计时
     */
    private class TimeCount extends CountDownTimer {
        /**
         * @param millisInFuture    总时长
         * @param countDownInterval 计时的时间间隔
         */
        private TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvGetVerifyCode.setClickable(false);
            tvGetVerifyCode.setText(String.format(getString(
                    R.string.verify_obtain_again), millisUntilFinished / 1000));
            tvGetVerifyCode.setTextColor(colorGray);
        }

        @Override
        public void onFinish() {
            tvGetVerifyCode.setText(R.string.get_verify_code);
            tvGetVerifyCode.setClickable(true);
            tvGetVerifyCode.setTextColor(colorBlue);
        }
    }
}
