package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.OrderListAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.OrderListBean;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.QueryMobileOrderList;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.OrderLoadPage;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.xlistview.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class MyNewOrderActivity extends BaseActivity implements NetResultListener, OnClickListener, XListView.IXListViewListener {
    public static final int REQUEST_CODE_ORDER = 0;
    private final int GET_MyOrder_LIST = 0;

    private XListView OrderXList;
    private RelativeLayout iv_back;
    private RelativeLayout rl_right;
    private String user_id;
    private String token;
    private String user_id_encode;
    private ArrayList<OrderListBean> dataMap = new ArrayList<OrderListBean>();
    private OrderListAdapter adapter;
    private ArrayList<String> codelist = new ArrayList<String>();
    private OrderLoadPage mLoadPage;
    private RadioGroup radio_select;
    private String status = "全部";
    private int pagenum = 0;
    private ArrayList<OrderListBean> dataing;
    private RadioButton radio_one, radio_two, radio_three;
    public static String ordertype;
    private TextView tv_text;

    public static MyNewOrderActivity MyNewOrderinstance = null;

    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    pagenum = 0;
                    mLoadPage.startLoad();
                    dataMap.clear();
                    adapter.notifyDataSetChanged();
                    initData("0", ordertype, status);

                    break;
                case 1:


                    Toast.makeText(MyNewOrderActivity.this, "网络连接失败，请确认网络连接后重试",
                            Toast.LENGTH_SHORT).show();

                    break;
                case 2:

                    Toast.makeText(MyNewOrderActivity.this, msg.obj + "",
                            Toast.LENGTH_SHORT).show();
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

        setContentView(R.layout.my_order1);
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode = sp.getString("Login_UID_ENCODE", "");
        Intent intent = getIntent();
        ordertype = intent.getStringExtra("ordertype");

        initView();
        initEvent();
        mLoadPage.startLoad();
        initData("0", ordertype, status);

        MyNewOrderinstance = this;

    }


    private void initView() {


        mLoadPage = new OrderLoadPage((RelativeLayout) findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(RelativeLayout layout,
                                      LinearLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {
                mLoadPage.startLoad();

                pagenum = 0;
                status = "全部";

                initData("0", ordertype, status);
            }

            @Override
            public void onToShopMallCLick() {
                //发送无序跳转的广播
                //                Intent intent = new Intent("cloudhome.disorder.jump");
                //                intent.putExtra("page",0);
                //                sendBroadcast(intent);
                EventBus.getDefault().post(new DisorderJumpEvent(0));
                Intent intent1 = new Intent(MyNewOrderActivity.this, AllPageActivity.class);
                startActivity(intent1);
                finish();
            }

        };

        OrderXList = (XListView) findViewById(R.id.OrderXList);
        OrderXList.setPullLoadEnable(true);
        OrderXList.setXListViewListener(MyNewOrderActivity.this);

        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        radio_select = (RadioGroup) findViewById(R.id.radio_select);

        radio_one = (RadioButton) findViewById(R.id.radio_one);
        radio_two = (RadioButton) findViewById(R.id.radio_two);
        radio_three = (RadioButton) findViewById(R.id.radio_three);

        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_text.setText(ordertype);
        iv_back.setOnClickListener(this);
    }

    private void initEvent() {

        OrderXList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != adapter) {
                    OrderListBean bean = (OrderListBean) adapter.getItem(position - OrderXList.getHeaderViewsCount());
                    Intent intent = new Intent(MyNewOrderActivity.this, OrderDetailActivity.class);

                    intent.putExtra("orderListBean", bean);
                    startActivityForResult(intent, REQUEST_CODE_ORDER);
                }
            }
        });


        radio_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @SuppressLint("ShowToast")
            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.radio_one:

                        pagenum = 0;
                        status = codelist.get(0);
                        dataMap.clear();
                        adapter.notifyDataSetChanged();
                        initData("0", ordertype, status);

                        break;
                    case R.id.radio_two:

                        pagenum = 0;
                        status = codelist.get(1);
                        dataMap.clear();
                        adapter.notifyDataSetChanged();
                        initData("0", ordertype, status);
                        break;
                    case R.id.radio_three:

                        pagenum = 0;
                        status = codelist.get(2);
                        dataMap.clear();
                        adapter.notifyDataSetChanged();
                        initData("0", ordertype, status);

                        break;

                    default:
                        break;
                }

            }

            private void elseif(boolean b) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void initData(String page, String ordertype, String status) {


        dataing = new ArrayList<OrderListBean>();

        QueryMobileOrderList queryMobileOrderList = new QueryMobileOrderList(this);

        queryMobileOrderList.execute(user_id_encode, GET_MyOrder_LIST, dataing, page, ordertype, status, codelist, token);
    }


    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_MyOrder_LIST:
                if (flag == MyApplication.DATA_OK) {

                    radio_one.setText(codelist.get(0));
                    radio_two.setText(codelist.get(1));
                    radio_three.setText(codelist.get(2));


                    if (pagenum == 0) {


                        if (dataMap.size() < 1) {

                            dataMap.addAll(dataing);
                            adapter = new OrderListAdapter(MyNewOrderActivity.this, dataMap);
                            OrderXList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }

                        OrderXList.stopLoadMore();


                        if (dataing.size() < 1) {


                            mLoadPage.loadSuccess(null, null, 1);


                        } else {

                            mLoadPage.loadSuccess(null, null, 0);
                        }


                    } else if (dataing.size() < 1) {

                        mLoadPage.loadSuccess(null, null, 0);
                        Toast.makeText(MyNewOrderActivity.this, "没有找到订单信息",
                                Toast.LENGTH_SHORT).show();

                        OrderXList.stopLoadMore();

                    } else {

                        mLoadPage.loadSuccess(null, null, 0);
                        dataMap.addAll(dataing);
                        adapter.notifyDataSetChanged();
                        OrderXList.stopLoadMore();

                    }


                } else if (flag == MyApplication.NET_ERROR) {
                    mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    mLoadPage.loadFail(MyApplication.NO_DATA, MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.JSON_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                } else if (flag == MyApplication.DATA_ERROR) {
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 1);
                }
                break;

        }
    }


    public void setData(final String id) {


        CustomDialog.Builder builder = new CustomDialog.Builder(MyNewOrderActivity.this);

        builder.setTitle("提示");
        builder.setMessage("是否取消订单");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                String url = IpConfig.getUri2("updateMyOrderModelState");
                OkHttpUtils.post()
                        .url(url)
                        .addParams("userid", user_id)
                        .addParams("token", token)
                        .addParams("id", id)
                        .addParams("status", "cancel")
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

                                            Message msg = Message.obtain();
                                            msg.what = 0;
                                            handler.sendMessage(msg);

                                        } else {
                                            String errmsg = jsonObject.getString("errmsg");
                                            Message msg = new Message();
                                            msg.what = 2;
                                            msg.obj = errmsg;
                                            handler.sendMessage(msg);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Message msg = new Message();
                                    msg.what = 2;
                                    msg.obj = e + "";
                                    handler.sendMessage(msg);
                                }
                            }

                        });

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.create().show();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:

                finish();

                break;


        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if (!MyApplication.prepay_id.equals("")) {
            status = "全部";

            pagenum = 0;
            dataMap.clear();
            adapter.notifyDataSetChanged();

            initEvent();
            mLoadPage.startLoad();
            initData("0", ordertype, status);
        }

    }


    private void getRefreshItem() {

        pagenum = 0;
        dataMap.clear();
        adapter.notifyDataSetChanged();
        initData("0", ordertype, status);

    }

    private void getLoadMoreItem() {

        pagenum++;
        initData(pagenum + "", ordertype, status);

    }


    @Override
    public void onRefresh() {


        OrderXList.postDelayed(new Runnable() {
            public void run() {

                OrderXList.stopLoadMore();
                getRefreshItem();

                adapter.notifyDataSetChanged();

                onLoad();
            }
        }, 2000);


    }

    @Override
    public void onLoadMore() {

        OrderXList.postDelayed(new Runnable() {
            public void run() {

                OrderXList.stopRefresh();
                getLoadMoreItem();

                // adapter.notifyDataSetChanged();

                // mListView.stopLoadMore();

            }
        }, 2000);

    }

    private void onLoad() {

        OrderXList.stopRefresh();

        OrderXList.stopLoadMore();

        OrderXList.setRefreshTime("刚刚");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ORDER && resultCode == RESULT_OK) {
            boolean cancel = data.getBooleanExtra("cancel", false);

            if (cancel) {
                pagenum = 0;
                mLoadPage.startLoad();
                dataMap.clear();
                adapter.notifyDataSetChanged();
                initData("0", ordertype, status);

            }
        }
    }
}
