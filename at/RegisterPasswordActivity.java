package com.cloudhome.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UserBean;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.network.AuthLogin;
import com.cloudhome.network.AuthRegister;
import com.cloudhome.network.SaveDeviceMsg;
import com.cloudhome.network.SetReferral;
import com.cloudhome.network.okhttp.interceptor.MyInterceptor;
import com.cloudhome.utils.Constants;
import com.cloudhome.utils.HttpMd5;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册后设置密码的 Activity
 */
public class RegisterPasswordActivity extends BaseActivity implements NetResultListener {
    private static final String EXTRA_CODE = "code";
    private static final String EXTRA_MOBILE = "mobile";
    public static final int AUTH_REGISTER = 1;
    public static final int ACCOUNT_PASSWORD_LOGIN = 2;
    public static final int SAVE_DEVICE_MSG = 3;
    public static final int SET_REFERRAL = 4;
    private static final String PARAM_CLIENT_TYPE = "android";
    private static final String PARAM_QUICK_REGISTER = "quick_register";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.iv_hide_password)
    ImageView ivHidePassword;
    @BindView(R.id.et_password)
    ClearEditText etPassword;
    @BindView(R.id.et_invite_code)
    ClearEditText etInviteCode;
    private String mDeviceId;
    private String mOsVersion;
    private String mVerifyCode;
    private String mMobileNum;
    private Dialog mLoadingDialog;
    private boolean mIsPasswordHidden;
    private String mRecommendCode;
    private String mPasswordEncode;
    private UserBean mUserInfo;
    private HashMap<String, String> mReferralMap;

    public static void activityStart(Context context, String mobileNum, String verifyCode) {
        Intent intent = new Intent(context, RegisterPasswordActivity.class);
        intent.putExtra(EXTRA_MOBILE, mobileNum);
        intent.putExtra(EXTRA_CODE, verifyCode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);
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
            mDeviceId = Secure.getString(getContentResolver(),
                    Secure.ANDROID_ID);
            MyInterceptor.device_id = mDeviceId;
        }
    }

    private void init() {
        Intent intent = getIntent();
        mVerifyCode = intent.getStringExtra(EXTRA_CODE);
        mMobileNum = intent.getStringExtra(EXTRA_MOBILE);
        mOsVersion = Build.VERSION.RELEASE;
        mIsPasswordHidden = true;
        mRecommendCode = "";

        tvTitle.setText(R.string.activity_register_password_title);
        btnAction.setVisibility(View.INVISIBLE);

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

    @OnClick(R.id.rl_hide_password)
    public void changePasswordState() {
        String passText = etPassword.getText().toString().trim();
        ivHidePassword.setBackgroundResource(mIsPasswordHidden ?
                R.drawable.open_password : R.drawable.hide_password);
        etPassword.setTransformationMethod(mIsPasswordHidden ?
                HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        etPassword.setSelection(passText.length());
        mIsPasswordHidden = !mIsPasswordHidden;
    }

    @OnClick(R.id.btn_register_done)
    public void registerDone() {
        String password = etPassword.getText().toString().trim();
        mRecommendCode = etInviteCode.getText().toString().trim();
        if (password.length() < 6 || password.length() > 20) {
            showPromptDialog(getString(R.string.dialog_check_password));
        } else {
            mLoadingDialog.show();
            mPasswordEncode = HttpMd5.getMD5(password);
            AuthRegister authRegister = new AuthRegister(this);
            //手机号，推荐码，密码，头像地址，client_type,mold,module,验证码
            authRegister.execute(mMobileNum, mRecommendCode, mPasswordEncode, "",
                    PARAM_CLIENT_TYPE, "1", PARAM_QUICK_REGISTER, mVerifyCode, AUTH_REGISTER);
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
                }).create().show();
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case AUTH_REGISTER:
                if (flag == Constants.REQUEST.DATA_OK) {
                    //注册成功后调用登录接口
                    mUserInfo = new UserBean();
                    AuthLogin authLogin = new AuthLogin(this);
                    authLogin.execute(mMobileNum, mPasswordEncode, PARAM_CLIENT_TYPE, "", "", "1",
                            ACCOUNT_PASSWORD_LOGIN, mUserInfo);
                } else if (flag == Constants.REQUEST.NET_ERROR) {
                    mLoadingDialog.dismiss();
                    Toast.makeText(this, R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                } else if (flag == Constants.REQUEST.DATA_EMPTY || flag == Constants.REQUEST.JSON_ERROR) {
                    mLoadingDialog.dismiss();
                } else if (flag == Constants.REQUEST.DATA_ERROR) {
                    mLoadingDialog.dismiss();
                    String codeMsg = dataObj.toString();
                    Toast.makeText(RegisterPasswordActivity.this, codeMsg, Toast.LENGTH_SHORT).show();
                }
                break;
            case ACCOUNT_PASSWORD_LOGIN:
                mLoadingDialog.dismiss();
                if (flag == Constants.REQUEST.DATA_OK) {
                    loginSuccess();
                    PersonalSettingActivity.activityStart(this);
                } else if (flag == Constants.REQUEST.NET_ERROR || flag == Constants.REQUEST.DATA_EMPTY
                        || flag == Constants.REQUEST.JSON_ERROR || flag == Constants.REQUEST.DATA_ERROR) {
                    Toast.makeText(this,
                            R.string.activity_register_password_login_faliure, Toast.LENGTH_LONG).show();
                    AllPageActivity.activityStart(this);
                }
                break;
            case SAVE_DEVICE_MSG:
                if (flag == Constants.REQUEST.DATA_OK) {
                    String only_key = dataObj.toString();
                    MyApplication.only_key = only_key;
                }
                break;
            case SET_REFERRAL:
                if (flag == Constants.REQUEST.DATA_OK) {
                    String name = mReferralMap.get("name");
                    SharedPreferences.Editor editor1 = sp2.edit();
                    editor1.putString(Constants.SP.REFER_NAME, name);
                    editor1.putString(Constants.SP.REFER_USER_ID, "bky");
                    editor1.commit();
                }
                break;
        }
    }

    private void loginSuccess() {
        storageUserInfo();

        if (!TextUtils.isEmpty(mRecommendCode)) {
            mReferralMap = new HashMap<>();
            SetReferral setReferral = new SetReferral(this);
            setReferral.execute(mUserInfo.getUser_id(), mUserInfo.getToken(), mRecommendCode,
                    SET_REFERRAL, mReferralMap);
        }

        SaveDeviceMsg saveDeviceMsg = new SaveDeviceMsg(this);
        saveDeviceMsg.execute(mUserInfo.getUser_id(), mUserInfo.getToken(), mDeviceId,
                mOsVersion, SAVE_DEVICE_MSG);
        UnreadArticle unreadArticle = new UnreadArticle(true, this);
        unreadArticle.getUnreadArticle();
        EventBus.getDefault().post(new LoginEvent());
        Toast.makeText(RegisterPasswordActivity.this, R.string.toast_register_success,
                Toast.LENGTH_LONG).show();
    }

    private void storageUserInfo() {
        // TODO: 2017/3/30 0029 storage with sqlite
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
        if ("00".equals(mUserInfo.getState())) {
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
        if ("00".equals(stateCode)) {
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
}
