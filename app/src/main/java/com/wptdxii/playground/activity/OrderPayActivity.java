package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.utils.GetIp;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sourceforge.simcpux.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class OrderPayActivity extends BaseActivity implements OnClickListener {


    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private String user_id;
    private String token;
    private Map<String, String> key_value = new HashMap<String, String>();
    private IWXAPI api;
    private RelativeLayout iv_back;
    private RelativeLayout rl_right;
    private TextView tv_text;

    private TextView my_order_title;
    private TextView order_price;
    private TextView order_time;

    public static OrderPayActivity OrderPayinstance=null;


    @SuppressLint("HandlerLeak")
    private Handler errcode_handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;

            String status = data;

            Log.d("455454", "455445" + status);
            if (status.equals("false")) {

                Toast.makeText(OrderPayActivity.this,
                        "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
            }
        }

    };

    private Handler null_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errmsg = data.get("errmsg");


            Toast.makeText(OrderPayActivity.this, errmsg,
                    Toast.LENGTH_SHORT).show();

        }

    };


    @SuppressLint("HandlerLeak")
    private Handler payhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            PayReq data = (PayReq) msg.obj;
            Toast.makeText(OrderPayActivity.this,
                    "正常调起支付", Toast.LENGTH_SHORT).show();
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信

            MyApplication.prepay_id = data.prepayId;

            api.sendReq(data);

        }

    };

    private String clientip,title,time,price,id,orderno;
    private RelativeLayout weixin_pay_rel   ;
    private String Event_WXPay = "OrderPayActivity_WX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_pay);


        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        Intent intent = getIntent();

        title = intent.getStringExtra("title");
        time  = intent.getStringExtra("time");
        price = intent.getStringExtra("price");
        id  = intent.getStringExtra("id");
        MyApplication.prepay_id="";
        MyApplication.java_wxpay_orderno = intent.getStringExtra("orderno");
        MyApplication.java_wxpay_entrance= intent.getStringExtra("entrance");


        api = WXAPIFactory.createWXAPI(this, getString(R.string.weixin_appid));
        api.registerApp(Constants.APP_ID);
        OrderPayinstance =this;
        init();
        initEvent();

    }

    void init() {

        my_order_title= (TextView) findViewById(R.id.my_order_title);
        order_price   = (TextView) findViewById(R.id.order_price);
        order_time    = (TextView) findViewById(R.id.order_time);
        weixin_pay_rel    = (RelativeLayout) findViewById(R.id.weixin_pay_rel);

        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right=(RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("确认支付");
        weixin_pay_rel.setOnClickListener(this);
        iv_back.setOnClickListener(this);



    }

    void initEvent() {

        my_order_title.setText(title);
        order_price.setText(price);
        order_time.setText(time);



        String nettype= GetIp.getCurrentNetType(OrderPayActivity.this);

        if(nettype.equals("wifi")){
            clientip=  GetIp.getWifiIp(OrderPayActivity.this);
        }else{
            clientip=  GetIp.getPhoneIp();
        }

        Log.d("clientip", clientip);
    }


    private void setpayData(String url) {


        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {

                        Log.e("error", "获取数据异常 ", e);
                        String status = "false";
                        Message message = Message.obtain();

                        message.obj = status;

                        errcode_handler.sendMessage(message);

                    }

                    @Override
                    public void onResponse(String response) {

                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        Map<String, String> errcode_map = new HashMap<String, String>();
                        try {

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {

                                JSONObject json = new JSONObject(jsonString);

                                String errcode = json.getString("errcode");
                                if (!errcode.equals("0")) {
                                    String errmsg = json.getString("errmsg");

                                    errcode_map.put("errcode", errcode);
                                    errcode_map.put("errmsg", errmsg);

                                    Message message2 = Message.obtain();

                                    message2.obj = errcode_map;

                                    null_handler.sendMessage(message2);

                                } else {

                                    JSONObject data = json.getJSONObject("data");

                                    PayReq req = new PayReq();

                                    req.appId = data.getString("appid");
                                    req.partnerId = data.getString("partnerid");

                                    req.packageValue = data.getString("package");


                                    req.nonceStr = data.getString("noncestr");
                                    req.prepayId = data.getString("prepayid");

                                    req.timeStamp = data.getString("timestamp");

                                    Log.d("44444", req.timeStamp);

                                    req.sign = data.getString("sign");
                                    Log.d("444445", req.sign);
                                    req.extData = "app data"; // optional


                                    Message message = Message.obtain();

                                    message.obj = req;
                                    payhandler.sendMessage(message);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }




    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {


            CustomDialog.Builder builder = new CustomDialog.Builder(OrderPayActivity.this);

            builder.setTitle("提示");
            builder.setMessage("是否放弃支付");
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //HomeCommonWeb首页产品，搜索产品，最赚钱，最热销，黄金组合都是跳的CommonWebActivity
                    //HomeWebShare是首页广告位，跳的是HomeWebShareActivity
                    //发现-展业活动-活动专区的活动，跳的是DiscoverActiveListWebActivity
//                    if (MyApplication.java_wxpay_entrance.equals("InsuranceShopWeb")) {
//                        if (InsuranceShopWebActivity.InsuranceShopWebinstance != null) {
//                            InsuranceShopWebActivity.InsuranceShopWebinstance.finish();
//                        }
//                        finish();
//                    } else if(MyApplication.java_wxpay_entrance.equals("HomeWebShare")||MyApplication.java_wxpay_entrance.equals("HomeCommonWeb")||MyApplication.java_wxpay_entrance.equals("HomeCommonWeb"))
//                    {
//                        finish();
//                    }else {
//                        finish();
//                    }
                    Intent finishIntent=new Intent();
                    setResult(200,finishIntent);
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.create().show();


        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:




                CustomDialog.Builder builder = new CustomDialog.Builder(OrderPayActivity.this);

                builder.setTitle("提示");
                builder.setMessage("是否放弃支付");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


//                        if (MyApplication.java_wxpay_entrance.equals("InsuranceShopWeb")) {
//
//
//                            if (InsuranceShopWebActivity.InsuranceShopWebinstance != null) {
//                                InsuranceShopWebActivity.InsuranceShopWebinstance.finish();
//                            }
//
//                            finish();
//
//                        }
//
//                        else if(MyApplication.java_wxpay_entrance.equals("HomeWebShare")||MyApplication.java_wxpay_entrance.equals("HomeCommonWeb")||MyApplication.java_wxpay_entrance.equals("HomeCommonWeb"))
//                        {
//                            finish();
//
//                        }else {
//
//                            finish();
//
//                        }


                        Intent finishIntent=new Intent();
                        setResult(200,finishIntent);
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.create().show();

                break;
            case R.id.weixin_pay_rel:


                boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;

                if (!isPaySupported) {
                    Toast.makeText(OrderPayActivity.this,
                            String.valueOf(isPaySupported),
                            Toast.LENGTH_SHORT).show();
                } else {
                    String url = IpConfig.getUri2("getPreparePay");
                    RelativeLayout payBtn = (RelativeLayout) findViewById(R.id.weixin_pay_rel);
                    payBtn.setEnabled(false);






                    key_value.put("userid", user_id);
                    key_value.put("token", token);
                    key_value.put("id", id);
                    key_value.put("clientip", clientip);



                    setpayData(url);

                    payBtn.setEnabled(true);

                    MobclickAgent.onEvent(OrderPayActivity.this, Event_WXPay);
                }

                break;

        }

    }
}
