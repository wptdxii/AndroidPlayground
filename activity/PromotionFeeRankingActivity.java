package com.cloudhome.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.PromotionFeeListAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.utils.IpConfig;
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

public class PromotionFeeRankingActivity extends BaseActivity implements View.OnClickListener {




    private String user_id;
    private String token;
    private View addfootview;
    private RelativeLayout rl_right;
    private RelativeLayout iv_back;
    private TextView tv_text;
    private TextView promotion_fee_bottom_txt;
    private TextView startdate_enddate;
    private String url;
    private HashMap<String, String> key_value = new HashMap<String, String>();
    private List<Map<String,String>> fee_rank_List;
    private PromotionFeeListAdapter adapter;
    private ListView  promotionfee_list;
    private PublicLoadPage mLoadPage;
    private String enddate;
    private String startdate;
    private String wagemon;
    private Handler handler = new Handler() {

        @SuppressWarnings("unchecked")

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                 //   mLoadPage.loadSuccess(null, null);
                    promotionfee_list.setVisibility(View.VISIBLE);


                    mLoadPage.loadSuccess(null, null);
                    adapter=new PromotionFeeListAdapter(PromotionFeeRankingActivity.this,fee_rank_List);

                    promotion_fee_bottom_txt.setText(wagemon);

                    if(promotionfee_list.getHeaderViewsCount()==0){
                        promotionfee_list.addHeaderView(addfootview);
                    }




                    startdate_enddate.setText("统计时间："+startdate+"至"+enddate);


                    promotionfee_list.setAdapter(adapter);


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

        setContentView(R.layout.promotionfeeranking);



        user_id  = sp.getString("Login_UID", "");
        token    = sp.getString("Login_TOKEN", "");

        initView();
        initData();
    }


    private void initView() {
        addfootview = LayoutInflater.from(PromotionFeeRankingActivity.this).inflate(R.layout.promotionfeeranking_headview,
                null);

        promotion_fee_bottom_txt= (TextView)addfootview.findViewById(R.id.promotion_fee_bottom_txt);
        startdate_enddate = (TextView) findViewById(R.id.startdate_enddate);

        promotionfee_list = (ListView) findViewById(R.id.promotionfee_list);

        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right= (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("推广排行榜");
        iv_back.setOnClickListener(this);



        mLoadPage = new PublicLoadPage((LinearLayout) findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(LinearLayout layout,
                                      RelativeLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {
                mLoadPage.startLoad();

                getpromotionfeeList(url);
            }
        };
    }

    private void initData() {

        mLoadPage.startLoad();
        key_value.put("userid",user_id);
        key_value.put("token",token);
        url = IpConfig.getUri2("queryUserIncomePreMonth");

        Log.d("88888",url);
        getpromotionfeeList(url);

    }



    /**获取推广费列表
     * @param url
     */
    private void getpromotionfeeList(String url) {

        fee_rank_List=new ArrayList<Map<String,String>>();
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


                                    startdate   = dataObject
                                            .getString("startdate");
                                    enddate   = dataObject
                                            .getString("enddate");
                                    wagemon   = dataObject
                                            .getString("wagemon");
                                    JSONArray listArray = dataObject
                                            .getJSONArray("list");

                                    for (int i = 0; i < listArray.length(); i++) {
                                        JSONObject jsonObject2 = listArray
                                                .getJSONObject(i);
                                        Map<String, String> map = new HashMap<String, String>();

                                        Iterator<String> iterator = jsonObject2
                                                .keys();
                                        while (iterator.hasNext()) {
                                            String key = iterator.next();
                                            String value = jsonObject2
                                                    .getString(key);
                                            map.put(key, value);
                                        }
                                        fee_rank_List.add(map);
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
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.iv_back:
                finish();
                break;


        }


    }
}
