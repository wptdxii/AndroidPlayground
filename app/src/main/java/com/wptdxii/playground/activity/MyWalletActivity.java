package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.MyIncomeDistributionAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.ListViewScrollView;
import com.cloudhome.view.customview.PublicLoadPage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by yangguangbaoxian on 2016/5/13.
 */
public class MyWalletActivity extends BaseActivity implements OnClickListener {


    public static Boolean MyWalletRefresh = false;

    private RelativeLayout iv_back;
    private RelativeLayout my_wallet_top_income_rel, wallet_rel;
    private MyIncomeDistributionAdapter madapter;
    private HashMap<String, String> key_value = new HashMap<String, String>();
    private List<Map<String, String>> six_month_income_list;
    private List<Map<String, String>> accoutInfo_list;

    private String balance;


    private String maxincom;
    private String url;
    private String user_id;
    private String token;
    private String isdeposit;
    private String depositmsg;
    private ListViewScrollView lvs_my_wallet;
    private boolean isCardBinded = true;

    private TextView my_wallet_top_price;
    private TextView balance_txt;

    private RelativeLayout rl_money1;
    private RelativeLayout rl_money2;
    private RelativeLayout rl_money3;

    private TextView tv_money_name1;
    private TextView tv_money_name2;
    private TextView tv_money_name3;

    private TextView tv_money_value1;
    private TextView tv_money_value2;
    private TextView tv_money_value3;


    private PublicLoadPage mLoadPage;

    private Handler handler = new Handler() {

        @SuppressWarnings("unchecked")

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    balance_txt.setText("￥" + balance);
                    int size =accoutInfo_list.size();

                 if(size==2){

                        rl_money1.setVisibility(View.VISIBLE);
                        rl_money2.setVisibility(View.GONE);
                        rl_money3.setVisibility(View.VISIBLE);
                        Map<String, String> map =accoutInfo_list.get(0);
                        Map<String, String> map2 =accoutInfo_list.get(1);

                        tv_money_name1.setText(map.get("name"));
                        tv_money_value1.setText(map.get("num"));

                        tv_money_name3.setText(map2.get("name"));
                        tv_money_value3.setText(map2.get("num"));


                    }else if(size==3){

                        rl_money1.setVisibility(View.VISIBLE);
                        rl_money2.setVisibility(View.VISIBLE);
                        rl_money3.setVisibility(View.VISIBLE);

                        Map<String, String> map =accoutInfo_list.get(0);
                        Map<String, String> map2 =accoutInfo_list.get(1);
                        Map<String, String> map3 =accoutInfo_list.get(2);

                        tv_money_name1.setText(map.get("name"));
                        tv_money_value1.setText(map.get("num"));

                        tv_money_name2.setText(map2.get("name"));
                        tv_money_value2.setText(map2.get("num"));

                        tv_money_name3.setText(map3.get("name"));
                        tv_money_value3.setText(map3.get("num"));
                    }
                    rl_money1.setOnClickListener(MyWalletActivity.this);
                    rl_money3.setOnClickListener(MyWalletActivity.this);

                  //  my_wallet_income.setText(totalincome);

                    mLoadPage.loadSuccess(null, null);
                    if ("0".equals(accoutInfo_list.get(size-1).get("num"))) {
                        isCardBinded = false;
                        tv_money_value3.setText("立即绑定");
                        tv_money_value3.setTextColor(getResources().getColor(R.color.red));
                    } else {

                        isCardBinded = true;
                     //   tv_money_value3.setText(backcardnum);
                        tv_money_value3.setTextColor(getResources().getColor(R.color.color3));
                    }
                    my_wallet_top_price.setText("上月收入最高 : ￥" + maxincom + "元  ,");
                    madapter.setData(six_month_income_list);
                    lvs_my_wallet.setAdapter(madapter);
                    madapter.notifyDataSetChanged();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        initView();
        initData();
    }


    private void initView() {


        mLoadPage = new PublicLoadPage((LinearLayout) findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(LinearLayout layout,
                                      RelativeLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {
                mLoadPage.startLoad();

                setData(url);
            }
        };
        madapter = new MyIncomeDistributionAdapter(MyWalletActivity.this);
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        wallet_rel = (RelativeLayout) findViewById(R.id.wallet_rel);
        my_wallet_top_income_rel = (RelativeLayout) findViewById(R.id.my_wallet_top_income_rel);

//        rel_bank_info         = (RelativeLayout) findViewById(R.id.rel_bank_info);
//        accumulated_income_rel= (RelativeLayout) findViewById(R.id.accumulated_income_rel);
//
        rl_money1= (RelativeLayout) findViewById(R.id.rl_money1);
        rl_money2= (RelativeLayout) findViewById(R.id.rl_money2);
        rl_money3= (RelativeLayout) findViewById(R.id.rl_money3);

        tv_money_name1= (TextView) findViewById(R.id.tv_money_name1);
        tv_money_name2= (TextView) findViewById(R.id.tv_money_name2);
        tv_money_name3= (TextView) findViewById(R.id.tv_money_name3);

        tv_money_value1= (TextView) findViewById(R.id.tv_money_value1);
        tv_money_value2= (TextView) findViewById(R.id.tv_money_value2);
        tv_money_value3= (TextView) findViewById(R.id.tv_money_value3);
        wallet_rel.setOnClickListener(this);
        my_wallet_top_income_rel.setOnClickListener(this);

        lvs_my_wallet = (ListViewScrollView) findViewById(R.id.lvs_my_wallet);
        lvs_my_wallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyWalletActivity.this, IncomeExpendDetailActivity2.class);
                intent.putExtra("title", six_month_income_list.get(position).get("title"));
                startActivity(intent);
            }
        });
        balance_txt = (TextView) findViewById(R.id.balance);
        my_wallet_top_price = (TextView) findViewById(R.id.my_wallet_top_price);
    }

    private void initData() {
        mLoadPage.startLoad();
        url = IpConfig.getUri2("queryUserIncomeLastSixMonth");
        Log.d("888888", url);
        key_value.put("userid", user_id);
        key_value.put("token", token);
        Log.d("888888", user_id + "----" + token);
        setData(url);
    }


    /**
     * 获取推广费列表
     *
     * @param url
     */
    private void setData(String url) {

        six_month_income_list = new ArrayList<Map<String, String>>();
        accoutInfo_list       = new ArrayList<Map<String, String>>();

        OkHttpUtils.post()
                .url(url)
                .params(key_value)//不带参数
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        try {
                            if (jsonString == null || jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();
                                message.obj = status;


                            } else {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                String errcode = jsonObject.getString("errcode");

                                if (errcode.equals("0")) {

                                    JSONObject dataObject = jsonObject
                                            .getJSONObject("data");

                                    balance = dataObject.getString("balance");


                                    maxincom = dataObject.getString("maxincom");
                                    isdeposit = dataObject.getString("isdeposit");
                                    depositmsg = dataObject.getString("depositmsg");

                                    JSONArray accoutInfolist = dataObject.getJSONArray("accoutInfo");
                                    for (int i = 0; i < accoutInfolist.length(); i++) {
                                        JSONObject jsonObject2 = accoutInfolist.getJSONObject(i);

                                        Map<String, String> map = new HashMap<String, String>();

                                        Iterator<String> iterator = jsonObject2
                                                .keys();
                                        while (iterator.hasNext()) {
                                            String key = iterator.next();
                                            String value = jsonObject2
                                                    .getString(key);
                                            map.put(key, value);
                                        }
                                        accoutInfo_list.add(map);
                                    }




                                    JSONArray sixmonlist = dataObject.getJSONArray("sixmonlist");


                                    for (int i = 0; i < sixmonlist.length(); i++) {
                                        JSONObject jsonObject2 = sixmonlist.getJSONObject(i);

                                        Map<String, String> map = new HashMap<String, String>();

                                        Iterator<String> iterator = jsonObject2
                                                .keys();
                                        while (iterator.hasNext()) {
                                            String key = iterator.next();
                                            String value = jsonObject2
                                                    .getString(key);
                                            map.put(key, value);
                                        }
                                        six_month_income_list.add(map);
                                    }


                                    Message msg = Message.obtain();


                                    msg.what = 0;

                                    handler.sendMessage(msg);

                                } else {
                                    Message msg = new Message();
                                    msg.what = 2;
                                    handler.sendMessage(msg);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Message msg = new Message();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        }
                    }
                });

    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if (MyWalletRefresh) {
            initData();
        }
        MyWalletRefresh = false;
    }


    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.my_wallet_top_income_rel:

                intent = new Intent(MyWalletActivity.this, PromotionFeeRankingActivity.class);
                MyWalletActivity.this.startActivity(intent);

                break;

            case R.id.rl_money1:

                intent = new Intent(MyWalletActivity.this, IncomeExpendDetailActivity.class);
                intent.putExtra("type", "收入");
                MyWalletActivity.this.startActivity(intent);

                break;

            case R.id.rl_money3:
                if (isCardBinded) {


                    intent = new Intent(MyWalletActivity.this, MyBankCardActivity.class);
                    startActivity(intent);

                } else {

                    intent = new Intent(MyWalletActivity.this, BankCardEditActivity.class);
                    startActivity(intent);

                }

                break;
            case R.id.wallet_rel:


                intent = new Intent(MyWalletActivity.this, AccountBalanceActivity.class);
                intent.putExtra("balance", balance);
                intent.putExtra("isdeposit", isdeposit);
                intent.putExtra("depositmsg", depositmsg);
                intent.putExtra("isCardBinded", isCardBinded);
                MyWalletActivity.this.startActivity(intent);

                break;


        }
    }


}
