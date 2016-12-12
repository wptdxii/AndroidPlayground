package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetBankList;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.ListDialog;
import com.cloudhome.view.customview.PublicLoadPage;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;

public class BankCardEditActivity extends BaseActivity implements NetResultListener {


    private EditText bankcard_name_edit;
    private EditText bankcard_num_edit;
    private String bankcard_num;
    private String user_id;
    private String token;
    private String phonenum;
    private Button bankcard_submit;
    private Map<String, String> key_value = new HashMap<String, String>();
    private RelativeLayout bankcard_back;

    private TimeCount time;
    private EditText reg_code;
    private TextView card_phonenum;
    private Button verify_msg_button;

    private String cardOwner = "";
    private EditText bankcard_account_name;

    private String truename;


    private String bank_name;
    private String bank_code;

    // 本页面title
    private TextView bankcard_title;
    private RelativeLayout rl_right;
    // 获取银行卡名字列表
    private final int GET_BANK_LIST = 0;
    private final int GET_BANK_CARD_DETAIL = 1;
    private Map<String, String> dataMap = new LinkedHashMap<String, String>();
    private Map<String, String> oldCardInfoMap = new HashMap<String, String>();
    private ArrayList<String> bankCodeList;
    private ArrayList<String> bankNameList;
    private TextView sp_bank_name;


    private String cardId;
    private PublicLoadPage mLoadPage;
    private Dialog dialog;
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {


            dialog.dismiss();
            switch (msg.what) {
                case 0:

                    MyBankCardActivity.RefreshFlag = true;

                    Editor editor1 = sp2.edit();
                    editor1.putString("bank_no", bankcard_num);
                    editor1.putString("bank_name", bank_name);
                    editor1.commit();


                    Toast.makeText(BankCardEditActivity.this, "添加银行卡成功", Toast.LENGTH_SHORT).show();

                    if (AccountBalanceActivity.AccountBalanceinstance != null) {
                        AccountBalanceActivity.AccountBalanceinstance.finish();
                    }
                    MyWalletActivity.MyWalletRefresh = true;
                    //返回上一页
                    Intent dat = new Intent();
                    setResult(200, dat);

                    finish();


                    break;
                case 1:


                    Toast.makeText(BankCardEditActivity.this,
                            "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT)
                            .show();


                    break;
                case 2:

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            BankCardEditActivity.this);

                    builder.setTitle("提示");
                    builder.setMessage(msg.obj + "");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.create().show();


                    break;

                default:
                    break;
            }


        }

    };

    private GetBankList getBankRequest;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.bankcard);

        String user_type = sp.getString("Login_TYPE", "none");
        String user_state = sp.getString("Login_CERT", "none");
        truename = sp.getString("truename", "");

        Intent intent = getIntent();
        String b_name = intent.getStringExtra("bank_name");
        String b_num = intent.getStringExtra("bank_num");
        cardId = intent.getStringExtra("cardId");

        init();
        initEvent();
    }

    private void init() {
        mLoadPage = new PublicLoadPage(
                (LinearLayout) findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(LinearLayout layout,
                                      RelativeLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {
                mLoadPage.startLoad();
                dataMap.clear();
                getBankRequest = new GetBankList(BankCardEditActivity.this);
                getBankRequest.execute(user_id, token, GET_BANK_LIST, dataMap);
            }
        };

        time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
        bankcard_back = (RelativeLayout) findViewById(R.id.iv_back);
        // bankcard_name_edit = (ClearEditText)
        // findViewById(R.id.bankcard_name_edit);
        bankcard_num_edit = (EditText) findViewById(R.id.bankcard_num_edit);
        bankcard_account_name = (EditText) findViewById(R.id.bankcard_account_name);
        bankcard_submit = (Button) findViewById(R.id.bankcard_submit);
        verify_msg_button = (Button) findViewById(R.id.verify_msg_button);
        reg_code = (EditText) findViewById(R.id.reg_code);
        card_phonenum = (TextView) findViewById(R.id.tv_mobile_number);
        RelativeLayout rl_bankcard_account_name = (RelativeLayout) findViewById(R.id.rl_bankcard_account_name);
        bankcard_title = (TextView) findViewById(R.id.tv_text);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        // 下拉列表
        sp_bank_name = (TextView) findViewById(R.id.sp_bank_name);


        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");


        // bankcard_name_edit.setText(b_name);
        // bankcard_num_edit.setText(b_num);
    }


    private void initEvent() {

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        phonenum = sp.getString("USER_NAME", "none");


        bankcard_title.setText("添加银行卡");

        if (truename.equals("") || truename.equals("null")) {

            bankcard_account_name.setText("");
        } else {
            bankcard_account_name.setText(truename);
        }


        mLoadPage.startLoad();
        getBankRequest = new GetBankList(this);
        getBankRequest.execute(user_id, token, GET_BANK_LIST, dataMap);

        String phonepass = phonenum.substring(0, 3) + "****"
                + phonenum.substring(phonenum.length() - 4, phonenum.length());


        card_phonenum.setText(phonepass);
        bankcard_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });


        // 下拉列表
        sp_bank_name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != bankNameList && bankNameList.size() > 0) {
                    final String[] array = bankNameList.toArray(new String[bankNameList.size()]);
                    final String[] arrayCode = bankCodeList
                            .toArray(new String[bankCodeList.size()]);
                    ListDialog dialogHolder = new ListDialog(
                            BankCardEditActivity.this, array, "请选择银行") {
                        @Override
                        public void item(int m) {
                            sp_bank_name.setText(array[m]);
                            bank_name = array[m];
                            bank_code = arrayCode[m];
                            Log.i("------------", "bank_name:" + bank_name + "===bank_code:" + bank_code);
                        }
                    };
                }
            }
        });

        bankcard_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // 验证码
                final String vercode = reg_code.getText().toString();
                // 银行卡号
                bankcard_num = bankcard_num_edit.getText().toString();
                // 持卡人
                cardOwner = bankcard_account_name.getText().toString();
                // 银行名称
                bank_name = sp_bank_name.getText().toString();

                // 持卡人名称校验
                if (cardOwner == null || cardOwner.equals("")
                        || cardOwner.equals("null")) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            BankCardEditActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请输入开户姓名");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                    return;
                }

                // 银行名称校验
                if (TextUtils.isEmpty(bank_name)
                        || TextUtils.isEmpty(bank_code)) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            BankCardEditActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请选择银行名称");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                    return;
                }


                // 银行卡号校验
                int b_num_length = bankcard_num.length();
                // 验证码校验
                int length = vercode.length();
                if (b_num_length < 16 || b_num_length > 20) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            BankCardEditActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请检查银行卡号");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.create().show();

                } else if (length != 6) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            BankCardEditActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请检查验证码");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.create().show();

                } else {

                    dialog.show();
                    key_value.clear();
                    key_value.put("userId", user_id);
                    key_value.put("token", token);
                    key_value.put("mobile", phonenum);
                    key_value.put("shortMsgCode", vercode);
                    key_value.put("bankCardNo", bankcard_num);
                    key_value.put("cardOwner", cardOwner);
                    key_value.put("bankname", bank_name);// 银行名称
                    key_value.put("bankCode", bank_code);// 银行代码
                    key_value.put("branchBank", "");// 开户支行
                    Log.i("verification-------", vercode);
                    Log.i("bank_no-----", bankcard_num);
                    Log.i("bank_user_name-------", cardOwner);
                    Log.i("bank_short_name银行名称--", bank_name);
                    Log.i("bank_code--------", bank_code);
                    final String PRODUCT_URL = IpConfig.getUri2("bindBankCard");
                    setdata(PRODUCT_URL);
                }

            }
        });

        verify_msg_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                String url = IpConfig.getUri("getRegisterCode");
                key_value.clear();
                key_value.put("user_id", user_id);
                key_value.put("token", token);
                key_value.put("mobile", phonenum);
                key_value.put("action", "bank");
                Log.i("BankCard", key_value.get("user_id"));
                OkHttpUtils.post()//
                        .url(url)//
                        .params(key_value)//
                        .build()//
                        .execute(new StringCallback() {

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e("error", "获取数据异常 ", e);

                                Toast.makeText(BankCardEditActivity.this,
                                        "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT)
                                        .show();

                            }

                            @Override
                            public void onResponse(String response, int id) {

                                Map<String, String> map = new HashMap<String, String>();
                                String jsonString = response;
                                Log.d("onSuccess", "onSuccess json = "
                                        + jsonString);

                                Log.d("55555", jsonString + "555");
                                if (jsonString.equals("") || jsonString.equals("null")) {
                                    Toast.makeText(BankCardEditActivity.this,
                                            "网络连接失败，请确认网络连接后重试",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                time.start();// 开始计时
                Toast.makeText(BankCardEditActivity.this, "获取中",
                        Toast.LENGTH_SHORT).show();
            }

        });

    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onTick(long millisUntilFinished) {
            verify_msg_button.setClickable(false);
            verify_msg_button.setText("重新获取" + "(" + millisUntilFinished / 1000
                    + ")");
        }

        @Override
        public void onFinish() {
            verify_msg_button.setText("获取验证码");
            verify_msg_button.setClickable(true);
        }
    }

    private void setdata(String url) {
        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message message = Message.obtain();
                        message.what = 1;
                        handler.sendMessage(message);

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        try {
                            if (jsonString.equals("") || jsonString.equals("null")) {

                                Message message = Message.obtain();


                                message.what = 1;
                                handler.sendMessage(message);
                            } else {


                                JSONObject jsonObject = new JSONObject(
                                        jsonString);
                                String errcode = jsonObject
                                        .getString("errcode");
                                if (errcode.equals("0")) {

                                    Message message = Message.obtain();
                                    message.what = 0;
                                    handler.sendMessage(message);

                                } else {


                                    String errmsg = jsonObject.getString("errmsg");
                                    Message message = Message.obtain();


                                    message.what = 2;
                                    message.obj = errmsg;
                                    handler.sendMessage(message);


                                }


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 根据值, 设置spinner默认选中:
     *
     * @param spinner
     * @param value
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner,
                                                     String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter(); // 得到SpinnerAdapter对象
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_BANK_LIST:
                if (flag == MyApplication.DATA_OK) {
                    bankCodeList = new ArrayList<String>();
                    bankNameList = new ArrayList<String>();
                    Iterator iter = dataMap.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = (String) entry.getKey();
                        String value = (String) entry.getValue();
                        Log.i("解析银行列表-------", key + "-------" + value);
                        bankCodeList.add(key);
                        bankNameList.add(value);
                    }


                    mLoadPage.loadSuccess(null, null);

                } else if (flag == MyApplication.NET_ERROR) {
                    mLoadPage.loadFail(MyApplication.NO_NET,
                            MyApplication.BUTTON_RELOAD, 0);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                            MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.JSON_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                            MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.DATA_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                            MyApplication.BUTTON_RELOAD, 1);
                }
                break;

            case GET_BANK_CARD_DETAIL:
                if (flag == MyApplication.DATA_OK) {
                    mLoadPage.loadSuccess(null, null);
                    bankcard_num_edit.setText(oldCardInfoMap.get("bank_account"));
                    bankcard_account_name.setText(oldCardInfoMap.get("name"));

                    bank_name = oldCardInfoMap.get("bank_name");
                    bank_code = oldCardInfoMap.get("bank_code");
                    // 设置spinner选中的项
                    sp_bank_name.setText(bank_name);

                } else if (flag == MyApplication.NET_ERROR) {
                    mLoadPage.loadFail(MyApplication.NO_NET,
                            MyApplication.BUTTON_RELOAD, 0);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                            MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.JSON_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                            MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.DATA_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED,
                            MyApplication.BUTTON_RELOAD, 1);
                }
                break;
        }

    }
}
