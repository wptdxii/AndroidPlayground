package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.GiftUnReceiveListAdapter;
import com.cloudhome.adapter.OrderListAdapter2;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.GiftNotReceive;
import com.cloudhome.bean.OrderListBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetGiftOrderLeft;
import com.cloudhome.network.QueryMobileOrderList;
import com.cloudhome.view.customview.PublicLoadPage;
import com.cloudhome.view.xlistview.XListView;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;


public class GiftInsuranceOrderListActivity extends BaseActivity implements NetResultListener, View.OnClickListener, XListView.IXListViewListener{
    private final int GET_MyOrder_LIST = 0;
    private final int GET_FIRST_LIST = 1;

    private XListView OrderXList;
    private RelativeLayout iv_back;
    private RelativeLayout rl_right;
    private String user_id;
    private String token;
    private String user_id_encode;
    private ArrayList<OrderListBean> dataMap = new ArrayList<OrderListBean>();
    private ArrayList<GiftNotReceive> giftNotReceiveList = new ArrayList<GiftNotReceive>();

    private OrderListAdapter2 adapter;
    private GiftUnReceiveListAdapter leftAdapter;
    private ArrayList<String> codelist = new ArrayList<String>();
    private PublicLoadPage mLoadPage;
    private RadioGroup radio_select;
    private String  status = "全部";
    private int pagenum = 0;
    private Handler mHandler;
    private ArrayList<OrderListBean> dataing;
    private ArrayList<GiftNotReceive> notReveiveList;
    private RadioButton radio_one, radio_two, radio_three;
    public  static  String ordertype;
    private TextView tv_text;
    public static GiftInsuranceOrderListActivity MyNewOrderinstance=null;
    //获取未领取的赠险订单
    private GetGiftOrderLeft getGiftOrderLeft;

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
                    Toast.makeText(GiftInsuranceOrderListActivity.this, "网络连接失败，请确认网络连接后重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(GiftInsuranceOrderListActivity.this,msg.obj+"",
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

        setContentView(R.layout.activity_gift_insurance_order_list);

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");
        Intent intent = getIntent();
        ordertype = intent.getStringExtra("ordertype");

        initView();
        initEvent();
        mLoadPage.startLoad();
//        initData("0", ordertype, status);
        initLeftData("0");


        MyNewOrderinstance=this;

    }


    //获取未领取的赠险的列表
    private void initLeftData(String pagenum) {
        status="未领取";
        notReveiveList = new ArrayList<GiftNotReceive>();
        getGiftOrderLeft=new GetGiftOrderLeft(this);
        getGiftOrderLeft.execute(user_id_encode,pagenum,GET_FIRST_LIST,notReveiveList,token);
    }


    private void initView() {


        mLoadPage = new PublicLoadPage((LinearLayout) findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(LinearLayout layout,
                                      RelativeLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {
                mLoadPage.startLoad();

                pagenum = 0;
                initLeftData("0");
            }
        };

        OrderXList = (XListView) findViewById(R.id.OrderXList);
        OrderXList.setPullLoadEnable(true);
        OrderXList.setXListViewListener(GiftInsuranceOrderListActivity.this);

        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right=(RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        radio_select = (RadioGroup) findViewById(R.id.radio_select);

        radio_one = (RadioButton) findViewById(R.id.radio_one);
        radio_two = (RadioButton) findViewById(R.id.radio_two);
        radio_three = (RadioButton) findViewById(R.id.radio_three);

        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText(ordertype);
        iv_back.setOnClickListener(this);

        mHandler = new Handler();
    }

    private void initEvent() {
        radio_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @SuppressLint("ShowToast")
            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.radio_one:
                        pagenum = 0;
                        giftNotReceiveList.clear();
                        leftAdapter.notifyDataSetChanged();
                        initLeftData("0");
                        break;
                    case R.id.radio_two:
                        pagenum = 0;
                        status = "处理中";
                        dataMap.clear();
                        if (null != adapter) {
                            adapter.notifyDataSetChanged();
                        }

                        initData("0", ordertype, status);
                        break;
                    case R.id.radio_three:
                        status = "已出单";
                        pagenum = 0;
                        dataMap.clear();
                        if (null != adapter) {
                            adapter.notifyDataSetChanged();
                        }
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
        queryMobileOrderList.execute(user_id_encode, GET_MyOrder_LIST, dataing, page, ordertype, status, codelist,token);
    }


    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_MyOrder_LIST:
                if (flag == MyApplication.DATA_OK) {
                    mLoadPage.loadSuccess(null, null);
                    if (pagenum == 0) {
                        if (dataMap.size() < 1) {
                            dataMap.addAll(dataing);
                            adapter = new OrderListAdapter2(GiftInsuranceOrderListActivity.this, dataMap);
                            OrderXList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        OrderXList.stopLoadMore();
                        if (dataing.size() < 1) {
                            Toast.makeText(GiftInsuranceOrderListActivity.this, "没有找到订单信息",Toast.LENGTH_SHORT).show();
                            OrderXList.stopLoadMore();
                            return;
                        }
                    } else if (dataing.size() < 1) {
                        Toast.makeText(GiftInsuranceOrderListActivity.this, "没有更多数据",Toast.LENGTH_SHORT).show();
                        OrderXList.stopLoadMore();
                    } else {
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
            case GET_FIRST_LIST:
                if (flag == MyApplication.DATA_OK) {
                    mLoadPage.loadSuccess(null, null);
                    radio_one.setText("未领取");
                    radio_two.setText("处理中");
                    radio_three.setText("已出单");
                    if (pagenum == 0) {
                        if (giftNotReceiveList.size() < 1) {

                            giftNotReceiveList.addAll(notReveiveList);
                            leftAdapter = new GiftUnReceiveListAdapter(GiftInsuranceOrderListActivity.this, giftNotReceiveList);
                            OrderXList.setAdapter(leftAdapter);
                            leftAdapter.notifyDataSetChanged();

                        }

                        OrderXList.stopLoadMore();


                        if (notReveiveList.size() < 1) {


                            Toast.makeText(GiftInsuranceOrderListActivity.this, "没有找到订单信息",
                                    Toast.LENGTH_SHORT).show();

                            OrderXList.stopLoadMore();

                            return;
                        }


                    } else if (notReveiveList.size() < 1) {


                        Toast.makeText(GiftInsuranceOrderListActivity.this, "没有更多数据",
                                Toast.LENGTH_SHORT).show();

                        OrderXList.stopLoadMore();

                    } else {


                        giftNotReceiveList.addAll(notReveiveList);
                        leftAdapter.notifyDataSetChanged();
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

        if(!MyApplication.prepay_id.equals("")) {
            status = "未领取";

            pagenum = 0;
            giftNotReceiveList.clear();
            leftAdapter.notifyDataSetChanged();
            initEvent();
            mLoadPage.startLoad();
            initLeftData("0");
        }

    }




    private void getRefreshItem() {

        if(status.equals("未领取")){
            pagenum = 0;
            giftNotReceiveList.clear();
            leftAdapter.notifyDataSetChanged();
            initLeftData("0");
        }else{
            pagenum = 0;
            dataMap.clear();
            adapter.notifyDataSetChanged();
            initData("0", ordertype, status);
        }
    }

    private void getLoadMoreItem() {
        if(status.equals("未领取")){
            pagenum++;
            initLeftData(pagenum+"");
        }else{
            pagenum++;
            initData(pagenum + "", ordertype, status);
        }
    }


    @Override
    public void onRefresh() {


        mHandler.postDelayed(new Runnable() {
            public void run() {

                OrderXList.stopLoadMore();
                getRefreshItem();


                onLoad();
            }
        }, 2000);


    }

    @Override
    public void onLoadMore() {

        mHandler.postDelayed(new Runnable() {
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
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
}
