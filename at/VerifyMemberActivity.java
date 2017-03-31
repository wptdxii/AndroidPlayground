package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.event.SubmitAptitudeCertEvent;
import com.cloudhome.utils.Constants;
import com.cloudhome.utils.IdCheckUtil;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;

/**
 * 资质认证 Activity
 */
public class VerifyMemberActivity extends BaseActivity {
    private static final String EXTRA_IS_FORM_REGISTER = "isFromRegister";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_ID_NUM = "idno";
    private static final String API_CERT = "certificate";
    private static final String EVENT_VERIFY = "VerifyMemberActivity_Verify";
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.et_name)
    ClearEditText etName;
    @BindView(R.id.rl_name)
    RelativeLayout rlName;
    @BindView(R.id.et_id_num)
    ClearEditText etIdNum;
    @BindView(R.id.rl_id_num)
    RelativeLayout rlIdNum;
    @BindView(R.id.iv_submit)
    ImageView ivSubmit;
    private Dialog mLoadingDialog;
    private Map<String, String> mParamsMap;
    private boolean mIsFromRegister;

    public static void activityStart(Context context, boolean isFromRegister) {
        Intent intent = new Intent(context, VerifyMemberActivity.class);
        intent.putExtra(EXTRA_IS_FORM_REGISTER, isFromRegister);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_member);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mIsFromRegister = getIntent().getBooleanExtra(EXTRA_IS_FORM_REGISTER, false);
        mParamsMap = new HashMap<>();
        String userId = sp.getString(Constants.SP.USER_ID, "");
        String token = sp.getString(Constants.SP.TOKEN, "");
        mParamsMap.put(Constants.PARAM.USER_ID, userId);
        mParamsMap.put(Constants.PARAM.TOKEN, token);

        tvTitle.setText(R.string.activity_verify_member_title);
        rlShare.setVisibility(View.INVISIBLE);

        mLoadingDialog = new Dialog(this, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.progress_dialog);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvDialog = (TextView) mLoadingDialog.findViewById(R.id.id_tv_loadingmsg);
        tvDialog.setText(R.string.msg_dialog_later);
    }

    @OnClick(R.id.rl_back)
    public void back() {
        if (mIsFromRegister) {
            AllPageActivity.activityStart(this);
        } else {
            finish();
        }
    }

    @OnTextChanged(value = {R.id.et_name, R.id.et_id_num}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void inPut() {
        String name = etName.getText().toString();
        String idNum = etIdNum.getText().toString();
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(idNum)) {
            ivSubmit.setImageResource(R.drawable.gray_verify_button);
        } else {
            ivSubmit.setImageResource(R.drawable.orange_verify_button);
        }
    }

    @OnClick(R.id.iv_submit)
    public void submit() {
        String name = etName.getText().toString();
        String idNum = etIdNum.getText().toString();
        Boolean isIdNum = IdCheckUtil.IDCardValidate(idNum);
        if (TextUtils.isEmpty(name) || "null".equals(name)) {
            Toast.makeText(this, R.string.toast_check_name,
                    Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(idNum) || "null".equals(idNum)) {
            Toast.makeText(this, R.string.toast_check_id_num,
                    Toast.LENGTH_LONG).show();
        } else if (!isIdNum) {
            Toast.makeText(this, R.string.toast_check_id_num_format,
                    Toast.LENGTH_LONG).show();
        } else {
            mLoadingDialog.show();
            mParamsMap.put(PARAM_NAME, name);
            mParamsMap.put(PARAM_ID_NUM, idNum);
            requestCert();
            MobclickAgent.onEvent(VerifyMemberActivity.this, EVENT_VERIFY);
        }
    }

    private void requestCert() {
        String certUrl = IpConfig.getUri(API_CERT);
        OkHttpUtils.post()
                .url(certUrl)
                .params(mParamsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mLoadingDialog.dismiss();
                        Toast.makeText(VerifyMemberActivity.this, R.string.msg_net_retry,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mLoadingDialog.dismiss();
                        try {
                            if (response == null || response.equals("") || response.equals("null")) {
                                Toast.makeText(VerifyMemberActivity.this, R.string.msg_net_retry,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                String errcode = jsonObject.getString("errcode");
                                String errmsg = jsonObject.getString("errmsg");
                                if ("0".equals(errcode)) {
                                    String data = jsonObject.getString("data");
                                    JSONObject dataObject = new JSONObject(data);
                                    String idno = dataObject.getString("idno");
                                    String name = dataObject.getString("name");
                                    Editor editor1 = sp.edit();
                                    editor1.putString(Constants.SP.ID_NUM, idno);
                                    editor1.putString(Constants.SP.TRUE_NAME, name);
                                    editor1.putString(Constants.SP.USER_STATE, "02");
                                    editor1.commit();

                                    ivSubmit.setImageResource(R.drawable.gray_verify_button);
                                    ivSubmit.setClickable(false);
                                    etName.setKeyListener(null);
                                    etIdNum.setKeyListener(null);

                                    showPromtDialog(getString(R.string.activity_verify_member_submit_success));
                                } else {
                                    showPromtDialog(errmsg);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void showPromtDialog(String message) {
        new CustomDialog.Builder(this)
                .setTitle(R.string.prompt)
                .setMessage(message)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                EventBus.getDefault().post(new SubmitAptitudeCertEvent());
                            }
                        }).create().show();
    }
}
