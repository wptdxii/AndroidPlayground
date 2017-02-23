package com.cloudhome.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.cloudhome.utils.HttpMd5;
import com.cloudhome.utils.RegexUtils;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends BaseActivity implements OnClickListener,NetResultListener{

    Map<String, String> key_value = new HashMap<String, String>();
    private ClearEditText et_username;
    private ClearEditText et_password;
    private Button login_in;
    private Button login_forget;
    private String pwMd5;

    private String ccyj_reference_flag = "";
    private Dialog dialog;

    private RelativeLayout iv_back;
    private TextView tv_title;
    private Button btn_right;
    private String device_id, os_version;
    private RelativeLayout rl_fast_login;
    private RelativeLayout rl_account_login;
    private RadioGroup rg_login;
    private RadioButton rb_fast_login;
    private RadioButton rb_account_login;
    private TimeCount time;

    private TextView tv_get_verify_code;
    private ClearEditText et_phone_num;
    private ClearEditText et_verify_code;
    private Button fast_login_btn;
    public static final int GET_CODE_FAST_LOGIN=1;
    public static final int FAST_LOGIN=2;
    public static final int ACCOUNT_PASSWORD_LOGIN=3;
    public static final int SAVE_DEVICE_MSG=4;
    private SaveDeviceMsg saveDeviceMsg;
    private GetVerifyCode getVerifyCode;
    private AuthLogin authLogin;
    //是否来自注册
    private boolean isFromRegister=false;
    private UserBean bean;

    private static final String TAG = "LoginActivity";
    private UnreadArticle unreadArticle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        Intent intent=getIntent();
        isFromRegister=intent.getBooleanExtra("isFromRegister",false);
        requestPhoneStatePermission();

    }

    private void requestPhoneStatePermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        requestPermissions(perms, new PermissionListener() {
            @Override
            public void onGranted() {
                TelephonyManager tm = (TelephonyManager) getBaseContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);
                device_id = tm.getDeviceId() + "";
                MyInterceptor.device_id = device_id;
                init();
                initEvent();
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
                if (device_id == null || device_id.equals("")) {
                    device_id = Secure.getString(LoginActivity.this.getContentResolver(),
                            Secure.ANDROID_ID);
                    MyInterceptor.device_id = device_id;
                }
                init();
                initEvent();
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                if (device_id == null || device_id.equals("")) {
                    device_id = Secure.getString(LoginActivity.this.getContentResolver(),
                            Secure.ANDROID_ID);
                    MyInterceptor.device_id = device_id;
                }
                init();
                initEvent();
            }
        });
    }

    void init() {
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_text);
        btn_right= (Button) findViewById(R.id.btn_right);
        tv_title.setText("登录");
        btn_right.setText("注册");
        rl_fast_login= (RelativeLayout) findViewById(R.id.rl_fast_login);
        rl_account_login= (RelativeLayout) findViewById(R.id.rl_account_login);
        rg_login= (RadioGroup) findViewById(R.id.rg_login);
        rb_fast_login= (RadioButton) findViewById(R.id.rb_fast_login);
        rb_account_login= (RadioButton) findViewById(R.id.rb_account_login);
        et_phone_num= (ClearEditText) findViewById(R.id.et_phone_num);
        et_verify_code= (ClearEditText) findViewById(R.id.et_verify_code);
        fast_login_btn= (Button) findViewById(R.id.fast_login_btn);
        tv_get_verify_code= (TextView) findViewById(R.id.tv_get_verify_code);
        iv_back.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        tv_get_verify_code.setOnClickListener(this);
        fast_login_btn.setOnClickListener(this);

        et_username = (ClearEditText) findViewById(R.id.et_username);
        String olonum = sp4.getString("old_phone", "");
        et_username.setText(olonum);
        et_phone_num.setText(olonum);
        Editable editable = et_username.getText();
        int selEndIndex = Selection.getSelectionEnd(editable);
        selEndIndex = editable.length();
        Selection.setSelection(editable, selEndIndex);
        Selection.setSelection(et_phone_num.getText(), et_phone_num.getText().length());

        os_version = android.os.Build.VERSION.RELEASE;
        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");

        et_password = (ClearEditText) findViewById(R.id.et_password);
        login_in = (Button) findViewById(R.id.login_in);
        login_forget = (Button) findViewById(R.id.login_forget);
        login_forget.setOnClickListener(this);
        login_in.setOnClickListener(this);
    }

    void initEvent() {
        time = new TimeCount(90000, 1000);// 构造CountDownTimer对象
        getVerifyCode=new GetVerifyCode(this);
        authLogin=new AuthLogin(this);
        rg_login.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_fast_login:
                        rl_fast_login.setVisibility(View.VISIBLE);
                        rl_account_login.setVisibility(View.GONE);
                        break;
                    case R.id.rb_account_login:
                        rl_fast_login.setVisibility(View.GONE);
                        rl_account_login.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_right:
                if(isFromRegister){
                    finish();
                }else {
                    intent.setClass(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("isFromLogin",true);
                    LoginActivity.this.startActivity(intent);
                }
                break;
            case R.id.login_forget:
                intent.setClass(LoginActivity.this, ForgetPWActivity.class);
                LoginActivity.this.startActivity(intent);
                break;
            case R.id.login_in:
                checkLoginInput();
                break;
            case R.id.tv_get_verify_code:
                String phoneNum=et_phone_num.getText().toString().trim();
                if(RegexUtils.isMobileNO(phoneNum)){
                    getVerifyCode.execute("quick_login",phoneNum,"true",GET_CODE_FAST_LOGIN);
                }else{
                    showPromptDialog("请检查手机号码");
                }
                break;
            case R.id.fast_login_btn:
                String phoneNum1=et_phone_num.getText().toString().trim();
                String code=et_verify_code.getText().toString().trim();
                if (!RegexUtils.isMobileNO(phoneNum1)) {
                    showPromptDialog("请检查手机号码");
                } else if (code.length() < 6) {
                    showPromptDialog("请检查验证码");
                }else {
                    dialog.show();
                    bean=new UserBean();
                    authLogin.execute(phoneNum1,"","android","quick_login",code,"2",FAST_LOGIN,bean);
                }
                break;

        }
    }

    //账号密码登录事件
    private void checkLoginInput() {
        final String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        pwMd5 = HttpMd5.getMD5(password);
        Log.d("77777", pwMd5);
        int phonelength = username.length();
        int pwstrlength = password.length();
        if (!RegexUtils.isMobileNO(username)) {
           showPromptDialog("请检查手机号码");
        } else if (pwstrlength < 6) {
            showPromptDialog("请输入六位以上密码");
        } else {
            dialog.show();
            bean=new UserBean();
            authLogin.execute(username,pwMd5,"android","","","1",ACCOUNT_PASSWORD_LOGIN,bean);
            //旧版登录，user_id不加密
//            AuthLogin1 authLogin1=new AuthLogin1(this);
//            authLogin1.execute(username,pwMd5,ACCOUNT_PASSWORD_LOGIN,bean);
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
//
        switch(action){
            case FAST_LOGIN:
                dialog.dismiss();
                if (flag == MyApplication.DATA_OK) {
                    loginSuccess();
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(LoginActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                    Toast.makeText(LoginActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                    String errMsg=dataObj.toString();
                    showPromptDialog(errMsg);
                }
                break;
            case GET_CODE_FAST_LOGIN:
                if (flag == MyApplication.DATA_OK) {
                    String codeMsg=dataObj.toString();
                    Toast.makeText(LoginActivity.this, codeMsg, Toast.LENGTH_SHORT).show();
                    time.start();
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(LoginActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                    String codeMsg=dataObj.toString();
                    showPromptDialog(codeMsg);
                }
                break;
            case ACCOUNT_PASSWORD_LOGIN:
                dialog.dismiss();
                if (flag == MyApplication.DATA_OK) {
                    loginSuccess();
                } else if (flag == MyApplication.NET_ERROR) {
                     Toast.makeText(LoginActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                    Toast.makeText(LoginActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                    String errMsg=dataObj.toString();
                    showPromptDialog(errMsg);
                }
                break;
            case SAVE_DEVICE_MSG:
                if (flag == MyApplication.DATA_OK) {
                    String only_key=dataObj.toString();
                    Log.i("only_key",only_key);
                    MyApplication.only_key = only_key;
                    Intent backIntent = new Intent();
                    setResult(170, backIntent);
                    if(isFromRegister){
                        Intent intent=new Intent(LoginActivity.this,AllPageActivity.class);
                        startActivity(intent);
                    }
                        finish();
                } else {
                    if(isFromRegister){
                        Intent intent=new Intent(LoginActivity.this,AllPageActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
                break;
        }

    }


    private void showPromptDialog(String message){
        CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void loginSuccess(){
        String state_code = bean.getState();
        setUserInfo(bean.getUser_id(),bean.getUsrId(), bean.getSex(), bean.getBirthday(), bean.getIdno(), bean.getToken(),
                bean.getMobile(),bean.getType(), bean.getState(), bean.getAvatar(), bean.getCert_a(),
                bean.getCert_b(), bean.getLicence(),bean.getName(), bean.getNickname(), bean.getCompany_name(),
                bean.getCompany(), bean.getMobile_area(),bean.getBank_name(), bean.getBank_no(),
                bean.getReferral_name(), bean.getRecomend_code(),bean.getCcyj_reference_flag(),
                bean.getAssessment(),bean.getUser_id_encode(),bean.getReferral_user_id());
        if (state_code.equals("00")) {
            Editor editor3 = sp3.edit();
            editor3.putString("personal_specialty", bean.getPersonal_specialty());
            editor3.putString("good_count", bean.getGood_count());
            editor3.putString("cert_num_isShowFlg", bean.getCert_num_isShowFlg());
            editor3.putString("isShow_in_expertlist", bean.getIsShow_in_expertlist());
            editor3.putString("isShowMicroCard", bean.getIsshow_card());
            editor3.putString("mobile_num_short", bean.getMobile_num_short());
            editor3.putString("personal_context", bean.getPersonal_context());
            editor3.commit();
        }
        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
        //发送登陆状态改变的广播
        EventBus.getDefault().post(new LoginEvent());
        //获得未读的文章
        unreadArticle=new UnreadArticle(true,this);
        unreadArticle.getUnreadArticle();
    }

    private void setUserInfo(String user_id,String usrId, String sex, String birthday,String idno, String token, String mobile,
                             String type,String state, String avatar, String cert_a, String cert_b,String licence,
                             String truename, String nickname,String company_name, String company, String mobile_area,
                             String bank_name, String bank_no, String refer_name,String recomend_code,
                             String ccyj_reference_flag, String assessment,String user_id_encode,String refer_user_id) {

        if (null == nickname || "null".equals(nickname) || "".equals(nickname)) {
            nickname = "匿名";
        }
        Editor editor1 = sp.edit();
        editor1.putString("Login_STATE", "SUCCESS");
        editor1.putString("Login_UID", user_id);
        editor1.putString("Encrypt_UID", usrId);
        editor1.putString("Login_TOKEN", token);
        editor1.putString("Login_UID_ENCODE", user_id_encode);
        MyInterceptor.sessionToken = token;
        editor1.putString("Login_TYPE", type);
        editor1.putString("Login_CERT", state);
        editor1.putString("USER_NAME", mobile);
        editor1.putString("idno", idno);
        editor1.putString("pwMd5", pwMd5);
        editor1.putString("avatar", avatar);
        editor1.putString("cert_a", cert_a);
        editor1.putString("cert_b", cert_b);
        editor1.putString("licence", licence);
        editor1.putString("assessment", assessment);
        editor1.putString("truename", truename);
        editor1.putString("nickname", nickname);
        editor1.commit();

        Editor editor2 = sp2.edit();
        editor2.putString("company_name", company_name);
        editor2.putString("company", company);
        editor2.putString("mobile_area", mobile_area);
        editor2.putString("bank_name", bank_name);
        editor2.putString("bank_no", bank_no);
        editor2.putString("refer_name", refer_name);
        editor2.putString("refer_user_id", refer_user_id);
        editor2.putString("recomend_code", recomend_code);


        editor2.putString("sex", sex);
        editor2.putString("birthday", birthday);
        editor2.putString("ccyj_reference_flag", ccyj_reference_flag);
        editor2.commit();

        Editor editor4 = sp4.edit();

        editor4.putString("old_phone", mobile);
        editor4.putString("setting_g_show", "off");
        editor4.putString("ges_show", "has");
        editor4.putString("gesture_pw", "");

        editor4.commit();

        Editor editor5 = sp5.edit();
        if (state.equals("00")) {
            if ("".equals(cert_a) && "".equals(cert_b) && "".equals(licence)) {
                editor5.putBoolean(user_id,false);
            } else {
                editor5.putBoolean(user_id,true);
            }
        }else{
            editor5.putBoolean(user_id,false);
        }
        editor5.commit();


//        final String Device_URL = IpConfig.getUri("saveDeviceMsg");
//        setDevice_data(Device_URL, user_id,token);

        saveDeviceMsg=new SaveDeviceMsg(this);
        saveDeviceMsg.execute(user_id,token,device_id,os_version,SAVE_DEVICE_MSG);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onTick(long millisUntilFinished) {
            tv_get_verify_code.setClickable(false);
            tv_get_verify_code.setText("重新获取" + "(" + millisUntilFinished / 1000+ ")");
            tv_get_verify_code.setTextColor(getResources().getColor(R.color.color9));
        }
        @Override
        public void onFinish() {
            tv_get_verify_code.setText("获取验证码");
            tv_get_verify_code.setClickable(true);
            tv_get_verify_code.setTextColor(getResources().getColor(R.color.title_blue));
        }
    }
}
