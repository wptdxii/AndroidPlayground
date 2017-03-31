package com.cloudhome.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UserBean;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.network.AuthLogin;
import com.cloudhome.network.GetVerifyCode;
import com.cloudhome.network.SaveDeviceMsg;
import com.cloudhome.network.okhttp.interceptor.MyInterceptor;
import com.cloudhome.utils.Constants;
import com.cloudhome.utils.HttpMd5;
import com.cloudhome.utils.RegexUtils;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity implements NetResultListener {
    public static final int GET_CODE_FAST_LOGIN = 1;
    public static final int FAST_LOGIN = 2;
    public static final int ACCOUNT_PASSWORD_LOGIN = 3;
    public static final int SAVE_DEVICE_MSG = 4;
    private static final String EXTRA_IS_FROM_REGISTER = "isFromRegister";
    private static final Object PARAM_CLIENT_TYPE = "android";
    private static final Object PARAM_QUICK_LIGIN = "quick_login";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.et_fast_num)
    ClearEditText etFastNum;
    @BindView(R.id.tv_get_verify_code)
    TextView tvGetVerifyCode;
    @BindView(R.id.et_verify_code)
    ClearEditText etVerifyCode;
    @BindView(R.id.rl_fast_login)
    RelativeLayout rlFastLogin;
    @BindView(R.id.et_account_num)
    ClearEditText etAccountNum;
    @BindView(R.id.et_password)
    ClearEditText etPassword;
    @BindView(R.id.btn_forget_password)
    Button btnForgetPassword;
    @BindView(R.id.rl_account_login)
    RelativeLayout rlAccountLogin;
    @BindColor(R.color.color9)
    int colorLightGray;
    @BindColor(R.color.title_blue)
    int colorBlue;
    private boolean mIsFromRegister;
    private Dialog mLoadingDialog;
    private String mDeviceId;
    private String mOsVersion;
    private String mPasswordEncode;
    private UserBean mUserInfo;

    public static void activityStart(Context context, boolean isFromRegister) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(EXTRA_IS_FROM_REGISTER, isFromRegister);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        requestPhoneStatePermission();
    }

    @SuppressLint("HardwareIds")
    private void requestPhoneStatePermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        requestPermissions(perms, new PermissionListener() {
            @Override
            public void onGranted() {
                TelephonyManager tm = (TelephonyManager) getBaseContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);
                mDeviceId = tm.getDeviceId() + "";
                MyInterceptor.device_id = mDeviceId;
                init();
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDenied) {
                getDeviceId();
                init();
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                getDeviceId();
                init();
            }
        });
    }

    @SuppressLint("HardwareIds")
    private void getDeviceId() {
        if (mDeviceId == null || "".equals(mDeviceId)) {
            mDeviceId = Secure.getString(LoginActivity.this.getContentResolver(),
                    Secure.ANDROID_ID);
            MyInterceptor.device_id = mDeviceId;
        }
    }

    private void init() {
        mIsFromRegister = getIntent().getBooleanExtra(EXTRA_IS_FROM_REGISTER, false);
        mOsVersion = Build.VERSION.RELEASE;

        tvTitle.setText(R.string.activity_login_title);
        btnAction.setText(R.string.activity_login_action);
        String oldNum = sp4.getString(Constants.SP.OLD_NUM, "");
        etAccountNum.setText(oldNum);
        etFastNum.setText(oldNum);
        Editable editable = etAccountNum.getText();
        int selectionEndIndex = editable.length();
        Selection.setSelection(editable, selectionEndIndex);
        Selection.setSelection(etFastNum.getText(), etFastNum.getText().length());

        mLoadingDialog = new Dialog(this, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.progress_dialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvDialogMsg = (TextView) mLoadingDialog.findViewById(R.id.id_tv_loadingmsg);
        tvDialogMsg.setText(R.string.msg_dialog_later);
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.btn_action)
    public void register() {
        if (mIsFromRegister) {
            finish();
        } else {
            RegisterActivity.activityStart(this, true);
        }
    }

    @OnClick(R.id.btn_forget_password)
    public void getPassword() {
        ForgetPWActivity.activityStart(this);
    }

    @OnClick(R.id.btn_account_login)
    public void accountLogin() {
        checkLoginInput();
    }

    @OnClick(R.id.tv_get_verify_code)
    public void getVerifyCode() {
        String phoneNum = etFastNum.getText().toString().trim();
        if (RegexUtils.isMobileNO(phoneNum)) {
            GetVerifyCode getVerifyCode = new GetVerifyCode(this);
            getVerifyCode.execute(PARAM_QUICK_LIGIN, phoneNum, "true", GET_CODE_FAST_LOGIN);
        } else {
            showPromptDialog(getString(R.string.dialog_check_num));
        }
    }

    @OnClick(R.id.btn_fast_login)
    public void fastLogin() {
        String phoneNum = etFastNum.getText().toString().trim();
        String code = etVerifyCode.getText().toString().trim();
        if (!RegexUtils.isMobileNO(phoneNum)) {
            showPromptDialog(getString(R.string.dialog_check_num));
        } else if (code.length() < 6) {
            showPromptDialog(getString(R.string.dialog_check_verify_code));
        } else {
            mLoadingDialog.show();
            mUserInfo = new UserBean();
            AuthLogin authLogin = new AuthLogin(this);
            authLogin.execute(phoneNum, "", PARAM_CLIENT_TYPE, PARAM_QUICK_LIGIN, code, "2",
                    FAST_LOGIN, mUserInfo);
        }
    }

    @OnCheckedChanged({R.id.rbtn_fast_login, R.id.rbtn_account_login})
    public void selectLoginWay(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rbtn_fast_login:
                    rlFastLogin.setVisibility(View.VISIBLE);
                    rlAccountLogin.setVisibility(View.GONE);
                    break;
                case R.id.rbtn_account_login:
                    rlFastLogin.setVisibility(View.GONE);
                    rlAccountLogin.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    private void checkLoginInput() {
        String username = etAccountNum.getText().toString();
        String password = etPassword.getText().toString();
        mPasswordEncode = HttpMd5.getMD5(password);
        int pwstrlength = password.length();
        if (!RegexUtils.isMobileNO(username)) {
            showPromptDialog(getString(R.string.dialog_check_num));
        } else if (pwstrlength < 6) {
            showPromptDialog(getString(R.string.activity_login_dialog_check_pwd));
        } else {
            mLoadingDialog.show();
            mUserInfo = new UserBean();
            AuthLogin authLogin = new AuthLogin(this);
            authLogin.execute(username, mPasswordEncode, PARAM_CLIENT_TYPE, "", "", "1",
                    ACCOUNT_PASSWORD_LOGIN, mUserInfo);
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case FAST_LOGIN:
                mLoadingDialog.dismiss();
                if (flag == Constants.REQUEST.DATA_OK) {
                    loginSuccess();
                } else if (flag == Constants.REQUEST.NET_ERROR || flag == Constants.REQUEST.DATA_EMPTY) {
                    Toast.makeText(this, R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                } else if (flag == Constants.REQUEST.JSON_ERROR || flag == Constants.REQUEST.DATA_ERROR) {
                    String errMsg = dataObj.toString();
                    showPromptDialog(errMsg);
                }
                break;
            case GET_CODE_FAST_LOGIN:
                if (flag == Constants.REQUEST.DATA_OK) {
                    TimeCount timeCount = new TimeCount(90000, 1000);
                    timeCount.start();
                } else if (flag == Constants.REQUEST.NET_ERROR) {
                    Toast.makeText(this, R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY || flag == Constants.REQUEST.JSON_ERROR
                        || flag == Constants.REQUEST.DATA_ERROR) {
                    String codeMsg = dataObj.toString();
                    showPromptDialog(codeMsg);
                }
                break;
            case ACCOUNT_PASSWORD_LOGIN:
                mLoadingDialog.dismiss();
                if (flag == Constants.REQUEST.DATA_OK) {
                    loginSuccess();
                } else if (flag == Constants.REQUEST.NET_ERROR || flag == Constants.REQUEST.DATA_EMPTY) {
                    Toast.makeText(this, R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                } else if (flag == Constants.REQUEST.DATA_ERROR || flag == Constants.REQUEST.JSON_ERROR) {
                    String errMsg = dataObj.toString();
                    showPromptDialog(errMsg);
                }
                break;
            case SAVE_DEVICE_MSG:
                if (flag == Constants.REQUEST.DATA_OK) {
                    MyApplication.only_key = dataObj.toString();
                    Intent backIntent = new Intent();
                    setResult(170, backIntent);
                }
                if (mIsFromRegister) {
                    Intent intent = new Intent(this, AllPageActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            default:
                break;
        }
    }

    private void showPromptDialog(String message) {
        new CustomDialog.Builder(this)
                .setTitle(R.string.prompt)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void loginSuccess() {
        storageUserInfo();
        SaveDeviceMsg saveDeviceMsg = new SaveDeviceMsg(this);
        saveDeviceMsg.execute(mUserInfo.getUser_id(), mUserInfo.getToken(), mDeviceId,
                mOsVersion, SAVE_DEVICE_MSG);
        UnreadArticle unreadArticle = new UnreadArticle(true, this);
        unreadArticle.getUnreadArticle();
        EventBus.getDefault().post(new LoginEvent());
        Toast.makeText(LoginActivity.this, R.string.toast_login_success, Toast.LENGTH_LONG).show();
    }

    private void storageUserInfo() {
        // TODO: 2017/3/28 0028 storage with sqlite
        Editor editor1 = sp.edit();
        editor1.putString(Constants.SP.LOGIN_STATE, "SUCCESS");
        editor1.putString(Constants.SP.USER_ID, mUserInfo.getUser_id());
        editor1.putString(Constants.SP.S_USER_ID, mUserInfo.getUsrId());
        editor1.putString(Constants.SP.TOKEN, mUserInfo.getToken());
        editor1.putString(Constants.SP.USER_ID_ENCODE, mUserInfo.getUser_id_encode());
        editor1.putString(Constants.SP.USER_TYPE, mUserInfo.getType());
        editor1.putString(Constants.SP.USER_STATE, mUserInfo.getState());
        editor1.putString(Constants.SP.USER_NAME, mUserInfo.getMobile());
        editor1.putString(Constants.SP.ID_NUM, mUserInfo.getIdno());
        editor1.putString(Constants.SP.PWD_ENCODE, mPasswordEncode);
        editor1.putString(Constants.SP.AVATAR, mUserInfo.getAvatar());
        editor1.putString(Constants.SP.CERT_A, mUserInfo.getCert_a());
        editor1.putString(Constants.SP.CERT_B, mUserInfo.getCert_b());
        editor1.putString(Constants.SP.LICENCE, mUserInfo.getLicence());
        editor1.putString(Constants.SP.ASSESSMENT, mUserInfo.getAssessment());
        editor1.putString(Constants.SP.TRUE_NAME, mUserInfo.getName());
        MyInterceptor.sessionToken = mUserInfo.getToken();
        String nickName = mUserInfo.getNickname();
        if (null == nickName || "null".equals(nickName) || "".equals(nickName)) {
            nickName = "匿名";
        }
        editor1.putString(Constants.SP.NICK_NAME, nickName);
        if (TextUtils.isEmpty(mUserInfo.getCert_a()) && TextUtils.isEmpty(mUserInfo.getCert_b())
                && TextUtils.isEmpty(mUserInfo.getLicence()) &&
                TextUtils.isEmpty(mUserInfo.getAssessment())) {
            editor1.putBoolean(Constants.SP.HAS_CERT, false);
        } else {
            editor1.putBoolean(Constants.SP.HAS_CERT, true);
        }
        editor1.commit();

        Editor editor2 = sp2.edit();
        editor2.putString(Constants.SP.COMPANY_NAME, mUserInfo.getCompany_name());
        editor2.putString(Constants.SP.COMPANY, mUserInfo.getCompany());
        editor2.putString(Constants.SP.MOBILE_AREA, mUserInfo.getMobile_area());
        editor2.putString(Constants.SP.BANK_NAME, mUserInfo.getBank_name());
        editor2.putString(Constants.SP.BANK_NUM, mUserInfo.getBank_no());
        editor2.putString(Constants.SP.REFER_NAME, mUserInfo.getReferral_name());
        editor2.putString(Constants.SP.REFER_USER_ID, mUserInfo.getReferral_user_id());
        editor2.putString(Constants.SP.RECOMMEND_CODE, mUserInfo.getRecomend_code());
        editor2.putString(Constants.SP.GENDER, mUserInfo.getSex());
        editor2.putString(Constants.SP.BIRTHDAY, mUserInfo.getBirthday());
        editor2.putString(Constants.SP.REFER_FLAG, mUserInfo.getCcyj_reference_flag());
        editor2.commit();

        Editor editor4 = sp4.edit();
        editor4.putString(Constants.SP.OLD_PHONE, mUserInfo.getMobile());
        editor4.putString(Constants.SP.SETTING_SHOW, "off");
        editor4.putString(Constants.SP.GES_SHOW, "has");
        editor4.putString(Constants.SP.GESTURE_PWD, "");
        editor4.commit();

        Editor editor5 = sp5.edit();
        if (mUserInfo.getState().equals("00")) {
            if ("".equals(mUserInfo.getCert_a()) && "".equals(mUserInfo.getCert_b())
                    && "".equals(mUserInfo.getLicence())) {
                editor5.putBoolean(mUserInfo.getUser_id(), false);
            } else {
                editor5.putBoolean(mUserInfo.getUser_id(), true);
            }
        } else {
            editor5.putBoolean(mUserInfo.getUser_id(), false);
        }
        editor5.commit();

        String stateCode = mUserInfo.getState();
        if (stateCode.equals("00")) {
            Editor editor3 = sp3.edit();
            editor3.putString(Constants.SP.PERSONAL_SPECIALTY, mUserInfo.getPersonal_specialty());
            editor3.putString(Constants.SP.GOOD_COUNT, mUserInfo.getGood_count());
            editor3.putString(Constants.SP.CERT_NUM_FLAG, mUserInfo.getCert_num_isShowFlg());
            editor3.putString(Constants.SP.IS_SHOW_IN_EXPERT, mUserInfo.getIsShow_in_expertlist());
            editor3.putString(Constants.SP.IS_SHOW_CARD, mUserInfo.getIsshow_card());
            editor3.putString(Constants.SP.SHORT_MOBILE_NUM, mUserInfo.getMobile_num_short());
            editor3.putString(Constants.SP.PERSONAL_CONTEXT, mUserInfo.getPersonal_context());
            editor3.commit();
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
            tvGetVerifyCode.setTextColor(colorLightGray);
        }

        @Override
        public void onFinish() {
            tvGetVerifyCode.setText(R.string.get_verify_code);
            tvGetVerifyCode.setClickable(true);
            tvGetVerifyCode.setTextColor(colorBlue);
        }
    }
}
