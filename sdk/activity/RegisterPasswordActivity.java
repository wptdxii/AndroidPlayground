package com.cloudhome.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.cloudhome.network.AuthRegister;
import com.cloudhome.network.SaveDeviceMsg;
import com.cloudhome.network.SetReferral;
import com.cloudhome.network.interceptor.MyInterceptor;
import com.cloudhome.utils.HttpMd5;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;


public class RegisterPasswordActivity extends BaseActivity implements View.OnClickListener,NetResultListener{
    private RelativeLayout iv_back;
    private TextView top_title;
    private Button btn_right;
    private ClearEditText et_register_password;
    private ClearEditText et_invite_code;
    private Button btn_register_done;
    private RelativeLayout rl_hide_password;
    private ImageView iv_hide_password;
    private boolean isPassworldHide=true;
    private AuthRegister authRegister;
    public static final int AUTH_REGISTER=1;
    public static final int ACCOUNT_PASSWORD_LOGIN=2;
    public static final int SAVE_DEVICE_MSG=3;
    public static final int SET_REFERRAL=4;
    private SaveDeviceMsg saveDeviceMsg;
    private SetReferral setReferral;
    private String device_id, os_version;
    private String code="";
    private String mobile="";
    private Dialog dialog;
    private AuthLogin authLogin;
    private UserBean bean;
    private String pwMd5="";
    private String invite_code="";
    private HashMap<String,String> referralMap;
    private UnreadArticle unreadArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);
        Intent intent=getIntent();
        code=intent.getStringExtra("code");
        mobile=intent.getStringExtra("mobile");
        initView();
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
                initEvent();
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
                if (device_id == null || device_id.equals("")) {
                    device_id = Settings.Secure.getString(RegisterPasswordActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    MyInterceptor.device_id = device_id;
                }
                initEvent();
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                if (device_id == null || device_id.equals("")) {
                    device_id = Settings.Secure.getString(RegisterPasswordActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    MyInterceptor.device_id = device_id;

                }
                initEvent();
            }
        });
    }

    private void initView() {
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        btn_right = (Button)findViewById(R.id.btn_right);
        et_register_password= (ClearEditText) findViewById(R.id.et_register_password);
        et_invite_code= (ClearEditText) findViewById(R.id.et_invite_code);
        btn_register_done= (Button) findViewById(R.id.btn_register_done);
        rl_hide_password= (RelativeLayout) findViewById(R.id.rl_hide_password);
        iv_hide_password= (ImageView) findViewById(R.id.iv_hide_password);
        top_title.setText("注册");
        btn_right.setVisibility(View.INVISIBLE);
        iv_back.setOnClickListener(this);
        rl_hide_password.setOnClickListener(this);
        btn_register_done.setOnClickListener(this);
    }
    private void initEvent() {
        os_version = android.os.Build.VERSION.RELEASE;
        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_hide_password:
                String passText=et_register_password.getText().toString().trim();
                if(isPassworldHide){
                    iv_hide_password.setBackgroundResource(R.drawable.open_password);
                    et_register_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_register_password.setSelection(passText.length());
                    isPassworldHide=false;
                }else{
                    iv_hide_password.setBackgroundResource(R.drawable.hide_password);
                    et_register_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_register_password.setSelection(passText.length());
                    isPassworldHide=true;
                }
                break;
            case R.id.btn_register_done:
                String password=et_register_password.getText().toString().trim();
                invite_code=et_invite_code.getText().toString().trim();
                if(password.length()<6||password.length()>20){
                    showPromptDialog("请检查密码");
                }else{
                    dialog.show();
                    pwMd5= HttpMd5.getMD5(password);
                    authRegister=new AuthRegister(this);
                    //手机号，推荐码，密码，头像地址，client_type,mold,module,验证码
                    authRegister.execute(mobile,invite_code,pwMd5,"","android","1","quick_register",code,AUTH_REGISTER);
                }
                break;
        }
    }

    private void showPromptDialog(String message){
        CustomDialog.Builder builder = new CustomDialog.Builder(RegisterPasswordActivity.this);
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

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch(action){
            case AUTH_REGISTER:

                if (flag == MyApplication.DATA_OK) {
                    //注册成功后调用登录接口
                    bean=new UserBean();
                    authLogin=new AuthLogin(this);
                    authLogin.execute(mobile,pwMd5,"android","","","1",ACCOUNT_PASSWORD_LOGIN,bean);
                } else if (flag == MyApplication.NET_ERROR) {
                    dialog.dismiss();
                    Toast.makeText(RegisterPasswordActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                    dialog.dismiss();
                } else if (flag == MyApplication.JSON_ERROR) {
                    dialog.dismiss();
                } else if (flag == MyApplication.DATA_ERROR) {
                    dialog.dismiss();
                    String codeMsg=dataObj.toString();
                    Toast.makeText(RegisterPasswordActivity.this, codeMsg, Toast.LENGTH_SHORT).show();
                }
                break;
            case ACCOUNT_PASSWORD_LOGIN:
                dialog.dismiss();
                if (flag == MyApplication.DATA_OK) {
                    invite_code=et_invite_code.getText().toString().trim();
                    loginSuccess();
                    //注册并且登录成功之后，跳到设置头像用户名页面
                    Intent intent=new Intent(RegisterPasswordActivity.this,PersonalSettingActivity.class);
                    startActivity(intent);
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(RegisterPasswordActivity.this, "注册成功，登录失败", Toast.LENGTH_LONG).show();
                    Toast.makeText(RegisterPasswordActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterPasswordActivity.this,AllPageActivity.class);
                    startActivity(intent);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    Toast.makeText(RegisterPasswordActivity.this, "注册成功，登录失败", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(RegisterPasswordActivity.this,AllPageActivity.class);
                    startActivity(intent);
                } else if (flag == MyApplication.JSON_ERROR) {
                    Intent intent=new Intent(RegisterPasswordActivity.this,AllPageActivity.class);
                    startActivity(intent);
                } else if (flag == MyApplication.DATA_ERROR) {
                    Toast.makeText(RegisterPasswordActivity.this, "注册成功，登录失败", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(RegisterPasswordActivity.this,AllPageActivity.class);
                    startActivity(intent);
                }
                break;
            case SAVE_DEVICE_MSG:
                if (flag == MyApplication.DATA_OK) {
                    String only_key=dataObj.toString();
                    Log.i("only_key",only_key);
                    only_key = only_key;
                } else if (flag == MyApplication.NET_ERROR) {
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                }
                break;
            case SET_REFERRAL:
                if (flag == MyApplication.DATA_OK) {
                    String name = referralMap.get("name");
//                    String user_id = referralMap.get("user_id");
                    SharedPreferences.Editor editor1 = sp2.edit();
                    editor1.putString("refer_name", name);
                    editor1.putString("refer_user_id", "bky");
                    Log.i("onSuccess++++++++",name);
                    editor1.commit();
                } else if (flag == MyApplication.NET_ERROR) {
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                }
                break;
        }
    }

    private void loginSuccess(){
        String state_code = bean.getState();
        setUserInfo(bean.getUser_id(), bean.getSex(), bean.getBirthday(), bean.getIdno(), bean.getToken(),
                bean.getMobile(),bean.getType(), bean.getState(), bean.getAvatar(), bean.getCert_a(),
                bean.getCert_b(), bean.getLicence(),bean.getName(), bean.getNickname(), bean.getCompany_name(),
                bean.getCompany(), bean.getMobile_area(),bean.getBank_name(), bean.getBank_no(),
                bean.getReferral_name(), bean.getRecomend_code(),bean.getCcyj_reference_flag(),
                bean.getAssessment(),bean.getUser_id_encode(),bean.getReferral_user_id());
        if (state_code.equals("00")) {
            SharedPreferences.Editor editor3 = sp3.edit();
            editor3.putString("personal_specialty", bean.getPersonal_specialty());
            editor3.putString("good_count", bean.getGood_count());
            editor3.putString("cert_num_isShowFlg", bean.getCert_num_isShowFlg());
            editor3.putString("isShow_in_expertlist", bean.getIsShow_in_expertlist());
            editor3.putString("isShowMicroCard", bean.getIsshow_card());
            editor3.putString("mobile_num_short", bean.getMobile_num_short());
            editor3.putString("personal_context", bean.getPersonal_context());
            editor3.commit();
        }
        Toast.makeText(RegisterPasswordActivity.this, "注册成功", Toast.LENGTH_LONG).show();
        //发送登陆状态改变的广播
        EventBus.getDefault().post(new LoginEvent());
        //获得未读的文章
        unreadArticle=new UnreadArticle(true,this);
        unreadArticle.getUnreadArticle();
    }

    private void setUserInfo(String user_id, String sex, String birthday,String idno, String token, String mobile,
                             String type,String state, String avatar, String cert_a, String cert_b,String licence,
                             String truename, String nickname,String company_name, String company, String mobile_area,
                             String bank_name, String bank_no, String refer_name,String recomend_code,
                             String ccyj_reference_flag, String assessment,String user_id_encode,String refer_user_id) {

        if (null == nickname || "null".equals(nickname) || "".equals(nickname)) {
            nickname = "匿名";
        }
        SharedPreferences.Editor editor1 = sp.edit();
        editor1.putString("Login_STATE", "SUCCESS");
        editor1.putString("Login_UID", user_id);
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

        SharedPreferences.Editor editor2 = sp2.edit();
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

        SharedPreferences.Editor editor4 = sp4.edit();

        editor4.putString("old_phone", mobile);
        editor4.putString("setting_g_show", "off");
        editor4.putString("ges_show", "has");
        editor4.putString("gesture_pw", "");

        editor4.commit();

        saveDeviceMsg=new SaveDeviceMsg(this);
        saveDeviceMsg.execute(user_id,token,device_id,os_version,SAVE_DEVICE_MSG);

        if(!TextUtils.isEmpty(invite_code)){
            referralMap=new HashMap<String,String>();
            setReferral=new SetReferral(this);
            setReferral.execute(user_id,token,invite_code,SET_REFERRAL,referralMap);
        }

    }
}
