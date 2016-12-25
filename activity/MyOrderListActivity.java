package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.PublicLoadPage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;


public class MyOrderListActivity extends BaseActivity implements OnClickListener {


    private RelativeLayout iv_back;
    private RelativeLayout rl_right;
    private TextView tv_text;
    private PublicLoadPage mLoadPage;

    private ArrayList<String> ordertypelist = new ArrayList<String>();

    private ListView pop_ordertype_list;
    boolean needDisorderJump=false;
    boolean newMain=false;

    public static MyOrderListActivity MyOrderListinstance=null;

    private Handler handler = new Handler() {

        @SuppressWarnings("unchecked")

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mLoadPage.loadSuccess(null, null);


                    String[] ordertypeArray = ordertypelist.toArray(new String[ordertypelist.size()]);


                    pop_ordertype_list.setAdapter(new ArrayAdapter<String>(
                            MyOrderListActivity.this,
                            R.layout.myorder_ordertype_item, R.id.tv_text, ordertypeArray));


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
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_order_list);
        Intent intent=getIntent();
        needDisorderJump=intent.getBooleanExtra("needDisorderJump",false);
        newMain=intent.getBooleanExtra("newMain",false);

        initView();
        initEvent();

        MyOrderListinstance= this;
        String Order_Type_Url = IpConfig.getUri2("queryordercodelist");
        getOrderTypeList(Order_Type_Url);
        mLoadPage.startLoad();


    }


    private void initView() {


        mLoadPage = new PublicLoadPage((LinearLayout) findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(LinearLayout layout,
                                      RelativeLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {

                mLoadPage.startLoad();
                String Order_Type_Url = IpConfig.getUri2("queryordercodelist");
                getOrderTypeList(Order_Type_Url);

            }
        };


        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right=(RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("我的订单");


        pop_ordertype_list = (ListView) findViewById(R.id.pop_ordertype_list);

        iv_back.setOnClickListener(this);

    }

    private void initEvent() {


        pop_ordertype_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

                String ordertype = ordertypelist.get(pos);

                if (ordertype.equals("个险") || ordertype.equals("卡单")) {
                    Intent intent = new Intent(MyOrderListActivity.this, MyNewOrderActivity.class);
                    intent.putExtra("ordertype", ordertype);
                    startActivity(intent);
                } else if (ordertype.equals("赠险")) {
                    Intent intent = new Intent(MyOrderListActivity.this, GiftInsuranceOrderListActivity.class);
                    intent.putExtra("ordertype", ordertype);
                    startActivity(intent);
                } else if (ordertype.equals("平安境外险订单")) {
                    Intent intent = new Intent(MyOrderListActivity.this, MyOldOrder_AccidentActivity.class);

                    intent.putExtra("ordertype", ordertype);

                    startActivity(intent);
                } else if (ordertype.equals("车险")) {

                    Intent intent = new Intent(MyOrderListActivity.this, MyOrder_CarInsuranceActivity.class);
                    intent.putExtra("ordertype", ordertype);
                    startActivity(intent);


                } else if (ordertype.equals("询价单")) {

                    Intent intent = new Intent(MyOrderListActivity.this, MyOldOrderActivity.class);


                    intent.putExtra("ordertype", ordertype);


                    startActivity(intent);
                }

            }
        });


    }


    /**
     * 获取title ordertype列表
     *
     * @param url
     */
    private void getOrderTypeList(String url) {


        ordertypelist = new ArrayList<String>();
        OkHttpUtils.get()
                .url(url)
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
                        try {
                            if (jsonString == null || jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();
                                message.obj = status;
                            } else {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                String errcode = jsonObject.getString("errcode");

                                if (errcode.equals("0")) {

                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {

                                        ordertypelist.add(dataArray.getJSONObject(i).get("name").toString());
                                    }
                                    Message msg = new Message();
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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:

                if(needDisorderJump){

//                    Intent intent = new Intent("cloudhome.disorder.jump");
//                    intent.putExtra("page", 3);
//                    sendBroadcast(intent);
                    EventBus.getDefault().post(new DisorderJumpEvent(3));
                    Intent intent1 = new Intent(this,AllPageActivity.class);
                    startActivity(intent1);

                }
                finish();

                break;


        }

    }


}
