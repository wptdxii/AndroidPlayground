package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.adapter.PopBanklistAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.FetchMoney;
import com.cloudhome.network.GetApplyCash;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.PublicLoadPage;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class WithdrawActivity extends BaseActivity implements NetResultListener, OnClickListener {
    private final int GET_APPLY_CASH = 0;
    private final int FETCH_MONEY = 1;
    public int bankpos;
    String url;

    /**
     * 输入框小数的位数
     */
    private static final int DECIMAL_DIGITS = 2;


    private String user_id;
    private String token;

    private String phonenum;
    private String consume_Money;
    private String account_id;
    private TextView tv_withdraw_all_money;

    private ClearEditText withdraw_edit;
    private TextView withdraw_bank;
    private TextView withdraw_statement;
    //银行卡号
    private TextView card_num;
    //银行logo
    private ImageView iv_bank_logo;
    //可提现金额
    private TextView tv_balance_left_value;
    private float available_money;

    private String errmsg = "";
    private ImageView withdraw_back;
    private Button withdraw_submit;
    private Map<String, String> key_value = new HashMap<String, String>();

    private Map<String, String> applycashMap = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> cardList = new ArrayList<HashMap<String, String>>();


    private Dialog dialog;
    private PublicLoadPage mLoadPage;

    private View pop_banklist_view;
    private boolean Enabled = false;

    private PopupWindow banklist_pop;
    private RelativeLayout pop_banklist_layout, rl_which_bank_card;
    private PopBanklistAdapter madapter;
    private String ava_money_str;
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    mLoadPage.loadSuccess(null, null);
                    ava_money_str = applycashMap.get("ava_money");
                    tv_balance_left_value.setText(ava_money_str + "元");
                    tv_withdraw_all_money.setOnClickListener(WithdrawActivity.this);

                    String result = ava_money_str.replaceAll(",", "");

                    available_money = Float.parseFloat(result);

                    if ("true".equals(applycashMap.get("usable"))) {
                        //  withdraw_submit.setEnabled(true);

                        Enabled = true;
                    } else {
                        //   withdraw_submit.setEnabled(false);
                        Enabled = false;
                    }

                    break;
                case 1:
                    mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                    break;
                case 2:
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                    break;

                default:
                    break;
            }
        }

    };

    private void showCardListPopupWindow(final ArrayList<HashMap<String, String>> List) {
        pop_banklist_layout = ((RelativeLayout) LayoutInflater.from(WithdrawActivity.this).inflate(
                R.layout.withdraw_cardlist_pop, null));

        ListView mybanklist = (ListView) pop_banklist_layout
                .findViewById(R.id.pop_banklist);
        ImageView iv_pop_dismiss = (ImageView) pop_banklist_layout
                .findViewById(R.id.iv_pop_dismiss);
        RelativeLayout add_bank_rel = (RelativeLayout) pop_banklist_layout
                .findViewById(R.id.add_bank_rel);
        if (List.size() >= 5) {
            int mHeight = Common.dip2px(WithdrawActivity.this, 222f);
            ViewGroup.LayoutParams layoutParams = mybanklist.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = mHeight;
            mybanklist.setLayoutParams(layoutParams);
        }
        madapter.setData(List);
        mybanklist.setAdapter(madapter);


        banklist_pop = new PopupWindow(WithdrawActivity.this);
        banklist_pop.setBackgroundDrawable(new ColorDrawable(0x00000000));
        banklist_pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        banklist_pop.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        banklist_pop.setTouchable(true);
        banklist_pop.setOutsideTouchable(true);
        banklist_pop.setFocusable(true);
        banklist_pop.setContentView(pop_banklist_layout);
        banklist_pop.showAsDropDown(pop_banklist_view);


        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        pop_banklist_layout.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int heightBottom = pop_banklist_layout.findViewById(
                        R.id.pop_banklist).getBottom();

                int heightTop = pop_banklist_layout.findViewById(
                        R.id.pop_order_rel).getTop();

                int heightLeft = pop_banklist_layout.findViewById(R.id.pop_banklist).getLeft();
                int heightRight = pop_banklist_layout.findViewById(R.id.pop_banklist)
                        .getRight();

                int y = (int) event.getY();
                int x = (int) event.getX();
                Log.d("777777yyy", y + "");
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > heightBottom || y > heightTop || x < heightLeft
                            || x > heightRight) {

                        banklist_pop.dismiss();
                        banklist_pop = null;
                    }
                }
                return true;
            }
        });

        iv_pop_dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                banklist_pop.dismiss();
                banklist_pop = null;
            }
        });
        add_bank_rel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WithdrawActivity.this, BankCardEditActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        mybanklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                arg0.setVisibility(View.VISIBLE);
                Map<String, String> Map = List.get(pos);
                account_id = Map.get("id");
                String url = Map.get("bankLogoImg");
                Glide.with(WithdrawActivity.this)
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.white_bg)
                        .error(R.drawable.bank_logo_moren)
                        .crossFade()
                        .into(iv_bank_logo);


                bankpos = pos;
                withdraw_bank.setText(Map.get("bankName"));

                String card_num_str = Map.get("bankCardNo");
                String right_num = card_num_str.substring(card_num_str.length() - 4, card_num_str.length());
                String left_star = "";
                for (int i = 0; card_num_str.length() - 4 - i > 0; i++) {
                    if (i % 4 == 0) {
                        left_star = " " + left_star;
                    }
                    left_star = "*" + left_star;
                }

                card_num.setText(left_star + right_num);
                banklist_pop.dismiss();
                banklist_pop = null;
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw);
        init();
        initEvent();
    }

    private void init() {
        mLoadPage = new PublicLoadPage((LinearLayout) findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(LinearLayout layout,
                                      RelativeLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {
                getApplyCash();
            }
        };

        withdraw_back = (ImageView) findViewById(R.id.iv_back);
        tv_withdraw_all_money = (TextView) findViewById(R.id.tv_withdraw_all_money);
        withdraw_edit = (ClearEditText) findViewById(R.id.withdraw_edit);
        withdraw_edit.addTextChangedListener(textWatcher);


        withdraw_bank = (TextView) findViewById(R.id.withdraw_bank);
        pop_banklist_view = findViewById(R.id.pop_banklist_view);
        withdraw_statement = (TextView) findViewById(R.id.withdraw_statement);
        EditText reg_code = (EditText) findViewById(R.id.reg_code);
        withdraw_submit = (Button) findViewById(R.id.withdraw_submit);
        card_num = (TextView) findViewById(R.id.tv_bank_num);
        tv_balance_left_value = (TextView) findViewById(R.id.tv_balance_left_value);
        iv_bank_logo = (ImageView) findViewById(R.id.iv_bank_logo);

        rl_which_bank_card = (RelativeLayout) findViewById(R.id.rl_which_bank_card);

        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");
        madapter = new PopBanklistAdapter(this);
    }

    private void initEvent() {
        bankpos = 0;
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        phonenum = sp.getString("USER_NAME", "none");
        String truename = sp.getString("truename", "none");

        key_value.put("user_id", user_id);
        key_value.put("token", token);

        //获取本用户的提现详情
        getApplyCash();
        withdraw_submit.setEnabled(false);

        if (truename.length() > 1) {
            truename = truename.substring(
                    truename.length() - 1, truename.length());

            Log.i("PHONE_NUMBER", phonenum);
            key_value.put("mobile", phonenum);
            phonenum = phonenum.substring(
                    phonenum.length() - 4, phonenum.length());

        }

        withdraw_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        withdraw_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                consume_Money = withdraw_edit.getText().toString();
                if (consume_Money.equals("") || consume_Money.equals("null")) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            WithdrawActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请输入要提现的金额");

                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {
                    //  int edit_text_Money = Integer.parseInt(consume_Money);
                    float edit_text_Money = Float.parseFloat(consume_Money);
                    if (edit_text_Money == 0) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("提现金额不能为0");
                        builder.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();

                    } else if (edit_text_Money < 50) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("提现金额必须大于或等于50");
                        builder.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    } else if (edit_text_Money > available_money) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                        builder.setTitle(R.string.activity_withdraw_dialog_title);
                        builder.setMessage("提现金额必须小于可提金额");
                        builder.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    } else if (edit_text_Money > 800) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                        builder.setTitle(getString(R.string.activity_withdraw_dialog_title));
                        builder.setMessage(getString(R.string.activity_withdraw_dialog_limit_msg));
                        builder.setNegativeButton(getString(R.string.activity_withdraw_dialog_negative),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.setPositiveButton(getString(R.string.activity_withdraw_dialog_limit_positive)
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                withdrawDeposit();
                            }
                        });
                        builder.create().show();
                    } else {
                        withdrawDeposit();
                    }
                }
            }
        });

        //		if (!user_state.equals("00")) {
        //
        //			withdraw_submit
        //					.setBackgroundResource(R.drawable.pub_grey_button_style);
        //			withdraw_submit.setClickable(false);
        //		}
        url = IpConfig.getUri("applyCashRules");
        withdraw_statement.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putExtra("url", url);
                intent.putExtra("title", "提现规则");
                // 从Activity IntentTest跳转到Activity IntentTest01
                intent.setClass(WithdrawActivity.this, JavaScriptActivity.class);
                WithdrawActivity.this.startActivity(intent);
            }
        });

    }

    private void withdrawDeposit() {
        //提现
        FetchMoney fetchMoneyRequest = new FetchMoney(WithdrawActivity.this);
        fetchMoneyRequest.execute(user_id, token, consume_Money, phonenum, account_id, errmsg, FETCH_MONEY);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("提现中...");
        dialog.show();
    }


    /**
     * 获取本用户的提现详情
     */
    private void getApplyCash() {
        mLoadPage.startLoad();
        GetApplyCash applyCashRequest = new GetApplyCash(this);
        applyCashRequest.execute(user_id, token, GET_APPLY_CASH, cardList);
    }

    private void setApplyCash(String url) {
        OkHttpUtils.post()
                .url(url)
                .params(key_value)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);

                        Message msg = Message.obtain();

                        msg.what = 1;

                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        Log.d("onSuccess", "setApplyCash " + jsonString);
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        try {

                            Log.d("9999", jsonString);
                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {

                                Message msg = Message.obtain();

                                msg.what = 1;

                                handler.sendMessage(msg);
                            } else {


                                JSONObject jsonObject = new JSONObject(jsonString);
                                String errcode = jsonObject.getString("errcode");
                                if (errcode.equals("0")) {

                                    JSONObject obj = jsonObject.getJSONObject("data");
                                    applycashMap.put("money", obj.getString("money"));
                                    applycashMap.put("ava_money", obj.getString("ava_money"));
                                    applycashMap.put("usable", obj.getBoolean("usable") + "");


                                    Message msg = new Message();
                                    msg.what = 0;
                                    handler.sendMessage(msg);


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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {

        switch (action) {
            case GET_APPLY_CASH:
                if (flag == MyApplication.DATA_OK) {

                    String ApplyCash_Url = IpConfig.getUri("applyCash");

                    setApplyCash(ApplyCash_Url);

                    mLoadPage.loadSuccess(null, null);

                    Map<String, String> dataMap = cardList.get(0);
                    account_id = dataMap.get("id");
                    String url = dataMap.get("bankLogoImg");


                    Glide.with(WithdrawActivity.this)
                            .load(url)
                            .centerCrop()
                            .placeholder(R.drawable.white_bg)
                            .error(R.drawable.bank_logo_moren)
                            .crossFade()
                            .into(iv_bank_logo);


                    withdraw_bank.setText(dataMap.get("bankName"));


                    String card_num_str = dataMap.get("bankCardNo");


                    String right_num = card_num_str.substring(card_num_str.length() - 4, card_num_str.length());

                    String left_star = "";
                    for (int i = 0; card_num_str.length() - 4 - i > 0; i++) {
                        if (i % 4 == 0) {
                            left_star = " " + left_star;
                        }
                        left_star = "*" + left_star;

                    }

                    card_num.setText(left_star + right_num);


                    rl_which_bank_card.setOnClickListener(this);


                } else if (flag == MyApplication.NET_ERROR) {
                    mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.JSON_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.DATA_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                }
                break;

            case FETCH_MONEY:
                dialog.dismiss();
                if (flag == MyApplication.DATA_OK) {
                    withdraw_submit.setEnabled(false);
                    Enabled = false;
                    errmsg = dataObj.toString();
                    CustomDialog.Builder builder = new CustomDialog.Builder(WithdrawActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage(errmsg);
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                    if (AccountBalanceActivity.AccountBalanceinstance != null) {
                                        AccountBalanceActivity.AccountBalanceinstance.finish();
                                    }

                                    MyWalletActivity.MyWalletRefresh = true;

                                    finish();
                                }
                            });
                    builder.create().show();


                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(WithdrawActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                    Toast.makeText(WithdrawActivity.this, "提现失败,请重试", Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.JSON_ERROR) {
                    Toast.makeText(WithdrawActivity.this, "提现失败,请重试", Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_ERROR) {
                    Toast.makeText(WithdrawActivity.this, "提现失败，请重试", Toast.LENGTH_SHORT).show();
                }
                break;
        }


    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.rl_which_bank_card:


                //                String[] banknameArray = new String[cardList.size()];
                //
                //                for (int i = 0; i < cardList.size(); i++) {
                //                    String bankcardno = cardList.get(i).get("bankCardNo");
                //                    banknameArray[i] = cardList.get(i).get("bankName") + "(尾号" + bankcardno.substring(
                //                            bankcardno.length() - 4, bankcardno.length()) + ")";
                //
                //
                //                }


                showCardListPopupWindow(cardList);

                break;

            case R.id.tv_withdraw_all_money:

                String money;
                if (ava_money_str.contains(",")) {
                    money = ava_money_str.replaceAll(",", "");
                    withdraw_edit.setText(money);
                } else {
                    withdraw_edit.setText(ava_money_str);
                }


                Editable editable = withdraw_edit.getText();
                int selEndIndex = editable.length();
                Selection.setSelection(editable, selEndIndex);


                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data == null)
            return;

        if (resultCode == 200) {
            bankpos = 0;
            cardList.clear();
            cardList = new ArrayList<HashMap<String, String>>();

            madapter.notifyDataSetChanged();
            if (banklist_pop != null) {
                banklist_pop.dismiss();
                banklist_pop = null;
            }
            getApplyCash();
        }
    }


    TextWatcher textWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

            if (s.toString().contains(".")) {
                if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                    s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + 3);
                    withdraw_edit.setText(s);
                    withdraw_edit.setSelection(s.length());
                }
            }
            if (s.toString().trim().substring(0).equals(".")) {
                s = "0" + s;
                withdraw_edit.setText(s);
                withdraw_edit.setSelection(2);
            }

            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(".")) {
                    withdraw_edit.setText(s.subSequence(0, 1));
                    withdraw_edit.setSelection(1);
                    return;
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();

            if (TextUtils.isEmpty(text) || text.equals("null") || !Enabled) {

                withdraw_submit.setEnabled(false);
            } else {
                withdraw_submit.setEnabled(true);

            }


        }
    };


}
