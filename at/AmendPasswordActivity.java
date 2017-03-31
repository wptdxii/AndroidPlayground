package com.cloudhome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.CheckCode;
import com.cloudhome.network.GetVerifyCode;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.RegexUtils;
import com.cloudhome.view.customview.SkipDialog;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;

public class AmendPasswordActivity extends BaseActivity  implements View.OnClickListener,NetResultListener {
    private TimeCount time;
    private ClearEditText forget_phone;

    private RelativeLayout iv_back;
    private TextView top_title;
    private ImageView iv_right;
    private Button btn_get_password_back;
    private GetVerifyCode getVerifyCode;
    public static final int GET_VERIFY_CODE=1;
    private CheckCode checkCode;
    private static final int CHECK_CODE=2;
    private String mobile="";
    private String code="";
    private SkipDialog builder;
    private TextView tv_get_verify_code;
    private TextView tv3;
    private String username;
    private boolean isVerifyDialogShow=false;//区分对话框之内获取验证码or点验证手机获取验证码
    private boolean canGetCodeAgain=true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amend_password);
        init();
        initEvent();
    }

    void init() {
        iv_back = (RelativeLayout) findViewById(R.id.rl_back);

        top_title = (TextView) findViewById(R.id.tv_title);
        iv_right = (ImageView) findViewById(R.id.iv_share);
        top_title.setText("修改密码");
        iv_right.setVisibility(View.INVISIBLE);
        tv3=(TextView) findViewById(R.id.tv3);

        btn_get_password_back= (Button) findViewById(R.id.btn_get_password_back);
        btn_get_password_back.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }



    void initEvent() {
        time = new TimeCount(90000, 1000);// 构造CountDownTimer对象
        username = sp.getString("USER_NAME", "none");
        mobile=username;
        if(!TextUtils.isEmpty(username)&&username.length()>8){
            String sub=username.substring(3,7);
            username=username.replace(sub,"****");
            tv3.setText(username);
        }
    }




    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_get_password_back:
                if(RegexUtils.isMobileNO(mobile)){
                    if(canGetCodeAgain){
                        getVerifyCode=new GetVerifyCode(AmendPasswordActivity.this);
                        getVerifyCode.execute("amend_password",mobile,"true",GET_VERIFY_CODE);
                    }else{
                        showVerifyCodeDialog();
                    }
                }else{
                    showPromptDialog("请检查手机号码");
                }
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }

    /**
     * 弹出输入验证码的dialog
     */
    private void showVerifyCodeDialog() {
        isVerifyDialogShow=true;
        View contentView = View.inflate(this,R.layout.dialog_input_verifycode,null);
        TextView tv_cancel= (TextView) contentView.findViewById(R.id.tv_cancel);
        TextView tv_confirm= (TextView) contentView.findViewById(R.id.tv_confirm);
        TextView tv_desc= (TextView) contentView.findViewById(R.id.tv_desc);
        final ClearEditText et_verify_code=(ClearEditText)contentView.findViewById(R.id.et_verify_code);
        tv_get_verify_code=(TextView) contentView.findViewById(R.id.tv_get_verify_code);
        String endNum=mobile.substring(7);
        tv_desc.setText("请输入手机尾号为"+endNum+"收到的短信验证码");
        builder = new SkipDialog(this,contentView, Common.dip2px(this,320),
                Common.dip2px(this,193),R.style.qiandao_dialog);
        builder.setCanceledOnTouchOutside(false);
        builder.show();
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                isVerifyDialogShow=false;
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code=et_verify_code.getText().toString().trim();
                if(code.length()<6){
                    Toast.makeText(AmendPasswordActivity.this, "请检查验证码", Toast.LENGTH_SHORT).show();
                }else{
                    //检验验证码
                    checkCode=new CheckCode(AmendPasswordActivity.this);
                    checkCode.execute("amend_password",mobile,code,CHECK_CODE);
                }
            }
        });
        tv_get_verify_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVerifyCode=new GetVerifyCode(AmendPasswordActivity.this);
                getVerifyCode.execute("amend_password",mobile,"true",GET_VERIFY_CODE);
            }
        });
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch(action){
            case GET_VERIFY_CODE:
                if (flag == MyApplication.DATA_OK) {
//                    String codeMsg=dataObj.toString();
//                    Toast.makeText(AmendPasswordActivity.this, codeMsg, Toast.LENGTH_SHORT).show();
                    if(!isVerifyDialogShow){
                        showVerifyCodeDialog();
                    }
                    time.start();
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(AmendPasswordActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                    String codeMsg=dataObj.toString();
                    showPromptDialog(codeMsg);
                }
                break;
            case CHECK_CODE:
                if (flag == MyApplication.DATA_OK) {
                    builder.dismiss();
                    isVerifyDialogShow=false;
                    Intent intent=new Intent(AmendPasswordActivity.this,ModifyPasswordActivity.class);
                    intent.putExtra("code",code);
                    intent.putExtra("mobile",mobile);
                    startActivity(intent);
                    finish();
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(AmendPasswordActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                    String checkMsg=dataObj.toString();
                    Toast.makeText(AmendPasswordActivity.this, checkMsg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onTick(long millisUntilFinished) {
            canGetCodeAgain=false;
            tv_get_verify_code.setClickable(false);
            tv_get_verify_code.setText("重新获取" + "(" + millisUntilFinished / 1000 + ")");
            tv_get_verify_code.setTextColor(getResources().getColor(R.color.color9));
        }

        @Override
        public void onFinish() {
            canGetCodeAgain=true;
            tv_get_verify_code.setText("获取验证码");
            tv_get_verify_code.setClickable(true);
            tv_get_verify_code.setTextColor(getResources().getColor(R.color.title_blue));
        }
    }

    private void showPromptDialog(String message){
        CustomDialog.Builder builder = new CustomDialog.Builder(AmendPasswordActivity.this);
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
