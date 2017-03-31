package com.cloudhome.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.MainProListAdapter;
import com.cloudhome.bean.MainProBean;
import com.cloudhome.event.CommissionRefreshEvent;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GroupProductClickStatistic;
import com.cloudhome.network.ProductClickStatistic;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.xlistview.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by xionhgu on 2016/8/29.
 * Email：965705418@qq.com
 */
public class MainProductListActivity extends BaseActivity implements View.OnClickListener,XListView.IXListViewListener,NetResultListener {

    private RelativeLayout iv_back;
    private RelativeLayout rl_right;
    private TextView tv_title;
    private Map<String, String> key_value = new HashMap<String, String>();

    private Handler mHandler;

    private MainProListAdapter madapter;
    private ArrayList<MainProBean> dataMap =new ArrayList<MainProBean>() ;

    private int pagenum = 1;
    private XListView xlv_mainlist;
    private String title;

    SharedPreferences sp;
    SharedPreferences sp5;
    private String loginString;
    private String user_id;

    //统计产品被点击的次数
    private ProductClickStatistic productClickStatistic=new ProductClickStatistic(this);
    private GroupProductClickStatistic groupProductClickStatistic=new GroupProductClickStatistic(this);
    private boolean isGroup=false;

    private boolean isCommissionShown;
    private boolean isMostProfitable=false;

    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:

                    if (pagenum == 1) {


                        if (dataMap.size() < 1) {

                            dataMap.addAll(dataing);
                            madapter.setData(dataMap);

                            madapter.isCommissionShown(isCommissionShown);
                            xlv_mainlist.setAdapter(madapter);
                            madapter.notifyDataSetChanged();

                        }

                        xlv_mainlist.stopLoadMore();


                        if (dataing.size() < 1) {
                            Toast.makeText(MainProductListActivity.this, "没有符合条件的产品或人",Toast.LENGTH_SHORT).show();
                        }else{

                            //     mLoadPage.loadSuccess(null, null,0);
                        }


                    } else if (dataing.size() < 1) {

                        //    mLoadPage.loadSuccess(null, null,0);
                        Toast.makeText(MainProductListActivity.this, "没有更多数据",
                                Toast.LENGTH_SHORT).show();

                        xlv_mainlist.stopLoadMore();

                    } else {

                        //    mLoadPage.loadSuccess(null, null,0);
                        dataMap.addAll(dataing);
                        madapter.isCommissionShown(isCommissionShown);
                        madapter.notifyDataSetChanged();
                        xlv_mainlist.stopLoadMore();

                    }


                    break;
                case 1:
                    Toast.makeText(MainProductListActivity.this, "网络连接失败，请确认网络连接后重试",
                            Toast.LENGTH_SHORT).show();

                    break;
                case 2:
                    Toast.makeText(MainProductListActivity.this, "没有符合条件的产品或人",Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(MainProductListActivity.this, "没有符合条件的产品或人",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_prolist);
        sp = getSharedPreferences("userInfo", 0);
        sp5 = this.getSharedPreferences("temp", 0);
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        isCommissionShown = sp5.getBoolean(user_id, false);
        Intent intent = getIntent();
        title=intent.getStringExtra("title");
        if("最赚钱".equals(title)){
            isMostProfitable=true;//最赚钱产品的话后面加上+10%
        }
        if(!"黄金组合".equals(title)){
            key_value.put("tags",intent.getStringExtra("tags"));
            key_value.put("search",intent.getStringExtra("search"));
            key_value.put("company",intent.getStringExtra("company"));
            key_value.put("type",intent.getStringExtra("type"));

            isGroup=false;
        }else{
            isGroup=true;
        }
        init();
        initEvent();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CommissionRefreshEvent event) {
        isCommissionShown = sp5.getBoolean(user_id, false);
        madapter.isCommissionShown(isCommissionShown);
        madapter.notifyDataSetChanged();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        isCommissionShown = sp5.getBoolean(user_id, false);
        madapter.isCommissionShown(isCommissionShown);
        madapter.notifyDataSetChanged();
    }

    private void init() {
        iv_back  = (RelativeLayout) findViewById(R.id.rl_back);
        iv_back.setOnClickListener(this);
        rl_right=(RelativeLayout) findViewById(R.id.rl_share);
        rl_right.setVisibility(View.INVISIBLE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        xlv_mainlist = (XListView) findViewById(R.id.xlv_mainlist);
        xlv_mainlist.setPullLoadEnable(true);
        xlv_mainlist.setXListViewListener(this);
        xlv_mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = i - 1;
                Log.d("88888", pos + "");
                if (pos >= 0 && pos < dataMap.size()) {
                    if (loginString.equals("none")) {
                        Intent intent = new Intent();
                        intent.setClass(MainProductListActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        MainProBean proBean = dataMap.get(pos);
                        String url = proBean.getUrl();
                        Log.i("打开的url", url);
                        //本地浏览量加1
                        if (isGroup) {
                            int clickNum = Integer.parseInt(dataMap.get(pos).getHits()) + 1;
                            dataMap.get(pos).setHits(clickNum + "");
                        } else {
                            int clickNum = Integer.parseInt(dataMap.get(pos).getPopularityreal()) + 1;
                            dataMap.get(pos).setPopularityreal(clickNum + "");
                        }

                        //境外旅行
                        if ("".equals(url)) {
                            //不用原生
//                            String product_id = proBean.getCode();
//                            Intent intent = new Intent(MainProductListActivity.this, RecProductDetailActivity.class);
//                            intent.putExtra("product_id", product_id);
//                            startActivity(intent);
                            url=IpConfig.getIp2()+"/product/toProductInfoPage";
                        }
                            //组合险后面拼的id
                            if (isGroup) {
                                if (url.contains("?")) {
                                    url = url + "&id=" + proBean.getId();
                                } else {
                                    url = url + "?id=" + proBean.getId();
                                }
                            } else {
                                //热销赚钱后面拼的code
                                if (url.contains("?")) {
                                    url = url + "&code=" + proBean.getCode();
                                } else {
                                    url = url + "?code=" + proBean.getCode();
                                }
                            }

                            Intent intent = new Intent();
                            intent.putExtra("title", proBean.getName());
                            intent.putExtra("web_address", url);
                            if (isGroup) {
                                intent.putExtra("shareDesc", proBean.getFeature());
                                intent.putExtra("isGroup", true);
                            } else {
                                intent.putExtra("shareDesc", proBean.getFeaturedesc());
                            }
                            intent.putExtra("shareTitle", proBean.getName());
                            intent.putExtra("shareLogo", IpConfig.getIp3() + "/" + proBean.getImgurl());
                            intent.putExtra("shareUrl", url);
                            if(isGroup){
                                intent.putExtra("needShare", true);
                            }else{
                                intent.putExtra("needShare", proBean.isShare());
                            }
                            intent.setClass(MainProductListActivity.this, CommonWebActivity.class);
                            startActivity(intent);

                        //统计产品被点击
                        if (isGroup) {
                            groupProductClickStatistic.execute(proBean.getId());
                        } else {
                            productClickStatistic.execute(proBean.getId());
                        }
                    }

                }
            }
        });
    }


    private void initEvent() {
        tv_title.setText(title);
        mHandler = new Handler();
        if(isGroup){
            getGroupListData(IpConfig.getUri2("groupList"));
            madapter = new MainProListAdapter(MainProductListActivity.this,true);
        }else{
            getproListData(IpConfig.getUri2("productlist"));
            madapter = new MainProListAdapter(MainProductListActivity.this);
        }


    }

    private void getRefreshItem() {
        pagenum = 1;
        dataMap.clear();

        madapter.isCommissionShown(isCommissionShown);
        madapter.notifyDataSetChanged();
        key_value.put("page", pagenum + "");
        if(isGroup){
            getGroupListData(IpConfig.getUri2("groupList"));
        }else{
            getproListData(IpConfig.getUri2("productlist"));
        }
    }

    private void getLoadMoreItem() {
        pagenum++;
        key_value.put("page", pagenum + "");
        if(isGroup){
            getGroupListData(IpConfig.getUri2("groupList"));
        }else{
            getproListData(IpConfig.getUri2("productlist"));
        }
    }


    @Override
    public void onRefresh() {

        mHandler.postDelayed(new Runnable() {
            public void run() {

                xlv_mainlist.stopLoadMore();
                getRefreshItem();

                madapter.isCommissionShown(isCommissionShown);
                madapter.notifyDataSetChanged();

                onLoad();
            }
        }, 2000);

    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            public void run() {

                xlv_mainlist.stopRefresh();
                getLoadMoreItem();

                // adapter.notifyDataSetChanged();

                // mListView.stopLoadMore();

            }
        }, 2000);

    }
    private void onLoad() {

        xlv_mainlist.stopRefresh();
        xlv_mainlist.stopLoadMore();
        xlv_mainlist.setRefreshTime("刚刚");


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;

        }
    }






    private ArrayList<MainProBean> dataing;

    private void getproListData(String url) {
        dataing = new ArrayList<MainProBean>();
        OkHttpUtils.get()
                .url(url)
                .params(key_value)
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
                        Log.d("onSucces_search_result", "onSuccess json = " + jsonString);
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

                                        JSONObject obj1 = dataArray.getJSONObject(i);

                                        MainProBean bean = new MainProBean();


                                        bean.setId(obj1.getString("id"));
                                        bean.setName(obj1.getString("name"));
                                        bean.setCode(obj1.getString("code"));
                                        bean.setCompany(obj1.getString("company"));
                                        bean.setFeaturedesc(obj1.getString("featureDesc"));
                                        if(isMostProfitable){
                                            bean.setRate(obj1.getString("rate")+"%+10");
                                        }else{
                                            bean.setRate(obj1.getString("rate"));
                                        }

                                        bean.setRatepostfix(obj1.getString("ratePostfix"));
                                        bean.setPrice(obj1.getString("price"));
                                        bean.setPricepostfix(obj1.getString("pricePostfix"));
                                        bean.setPopularityreal(obj1.getString("popularityReal"));
                                        if(obj1.getString("isShare").equals("1")){
                                            bean.setShare(true);
                                        }else{
                                            bean.setShare(false);
                                        }
                                        //境外旅行险传的是null
                                        if(TextUtils.isEmpty(obj1.getString("url"))||"null".equals(obj1.getString("url"))){
                                            bean.setUrl("");
                                        }else{
                                            bean.setUrl(obj1.getString("url"));
                                        }
                                        bean.setImgurl(obj1.getString("imgUrl"));
                                        bean.setTags(obj1.getString("tags"));
                                        bean.setTrain(obj1.getBoolean("train"));
                                        bean.setIsAdvertisement(false);
                                        dataing.add(bean);
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

    private void getGroupListData(String url) {
        dataing = new ArrayList<MainProBean>();
        OkHttpUtils.get()
                .url(url)
                .params(key_value)
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
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject obj1 = dataArray.getJSONObject(i);
                                        MainProBean bean = new MainProBean();
                                        bean.setApply(obj1.getString("apply"));
                                        bean.setFeature(obj1.getString("feature"));
                                        bean.setHits(obj1.getString("hits"));
                                        bean.setId(obj1.getString("id"));
                                        bean.setName(obj1.getString("name"));
                                        bean.setPrices(obj1.getString("prices"));
                                        bean.setProductName(obj1.getString("productName"));
                                        bean.setUrl(obj1.getString("url"));
                                        bean.setImgurl(obj1.getString("imgUrl"));
                                        dataing.add(bean);
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
    protected void onRestart() {
        super.onRestart();
        if(madapter!=null){
            madapter.isCommissionShown(isCommissionShown);
            madapter.notifyDataSetChanged();
        }

    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
