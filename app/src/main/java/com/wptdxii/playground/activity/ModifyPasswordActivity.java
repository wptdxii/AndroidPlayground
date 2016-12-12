package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UserBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.AmendPassword;
import com.cloudhome.network.AuthLogin;
import com.cloudhome.network.SaveDeviceMsg;
import com.cloudhome.utils.HttpMd5;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;


public class ModifyPasswordActivity extends BaseActivity implements View.OnClickListener,NetResultListener{
    private RelativeLayout iv_back;
    private TextView top_title;
    private ImageView iv_right;
    private ClearEditText et_password_first;
    private ClearEditText et_password_second;
    private Button btn_done;
    private String mobile;
    private String code;
    private AmendPassword amendPassword;
    public static final int MODIFY_PASSWORD =1;
    public static final int ACCOUNT_PASSWORD_LOGIN=2;
    public static final int SAVE_DEVICE_MSG=3;
    private SaveDeviceMsg saveDeviceMsg;
    private AuthLogin authLogin;
    private String passMd5="";
    private Dialog dialog;
    private UserBean bean;
    private String device_id, os_version;
    private String user_id;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        Intent intent=getIntent();
        mobile=intent.getStringExtra("mobile");
        code=intent.getStringExtra("code");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        initView();
        initEvent();
    }



    private void initView() {
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        top_title.setText("修改密码");
        et_password_first= (ClearEditText) findViewById(R.id.et_password_first);
        et_password_second= (ClearEditText) findViewById(R.id.et_password_second);
        btn_done= (Button) findViewById(R.id.btn_done);
        iv_right.setVisibility(View.INVISIBLE);
        iv_back.setOnClickListener(this);
        btn_done.setOnClickListener(this);
    }

    private void initEvent() {
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        device_id = tm.getDeviceId() + "";
        if (device_id.equals("null") || device_id.equals("")) {
            device_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
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
            case R.id.btn_done:
                String et_first=et_password_first.getText().toString().trim();
                String et_second=et_password_second.getText().toString().trim();
//                if(TextUtils.isEmpty(et_first)||TextUtils.isEmpty(et_second)){
//                    showPromptDialog("请输入密码");
//                }else if(et_first.length()<6||et_second.length()<6){
//                    showPromptDialog("密码长度应大于6");
//                }else if(et_first.length()>20||et_second.length()>20){
//                    showPromptDialog("密码长度应小于20");
//                }else if(!et_first.equals(et_second)){
//                    showPromptDialog("两次输入密码不一致");
//                }
                if(et_first.length()<6||et_second.length()<6||et_first.length()>20
                        ||et_second.length()>20||!et_first.equals(et_second)){
                    showPromptDialog("请检查密码");
                }else{
                    dialog.show();
                    passMd5= HttpMd5.getMD5(et_second);
                    amendPassword=new AmendPassword(this);
                    amendPassword.execute(mobile,passMd5,"amend_password",code, MODIFY_PASSWORD,user_id,token);
                }

                break;
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch(action){
            case MODIFY_PASSWORD:
                if (flag == MyApplication.DATA_OK) {
                    Toast.makeText(ModifyPasswordActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (flag == MyApplication.NET_ERROR) {
                    dialog.dismiss();
                    Toast.makeText(ModifyPasswordActivity.this, "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                    dialog.dismiss();
                } else if (flag == MyApplication.JSON_ERROR) {
                    dialog.dismiss();
                } else if (flag == MyApplication.DATA_ERROR) {
                    dialog.dismiss();
                    String checkMsg=dataObj.toString();
                    Toast.makeText(ModifyPasswordActivity.this, checkMsg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showPromptDialog(String message){
        CustomDialog.Builder builder = new CustomDialog.Builder(ModifyPasswordActivity.this);
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



}
