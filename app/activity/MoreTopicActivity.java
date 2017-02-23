package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.MoreTopicAdapter;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.xlistview.XListView.IXListViewListener;
import com.umeng.analytics.MobclickAgent;
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
public class MoreTopicActivity extends BaseActivity implements IXListViewListener {
    public String Event_Choiceness = "Event_MoreTopicActivity_Choiceness";
    public String Event_Information = "Event_MoreTopicActivity_Information";
    public String Event_Video = "Event_MoreTopicActivity_Video";
    public String Event_Life = "Event_MoreTopicActivity_Life";
    public String Event_Train = "Event_MoreTopicActivity_Train";

    //是否点击的是视频选项，判断是否显示时长
    public boolean isVideoLengthShow = false;
    String typeCode;
    boolean hasMeasured2 = false;
    private MoreTopicAdapter adapter;
    private RadioGroup more_radio_select;
    private Map<String, String> key_value = new HashMap<String, String>();
    private List<Map<String, String>> resultdata = new ArrayList<Map<String, String>>();
    private Dialog dialog;
    private XListView mListView;
    private RelativeLayout more_topic_back;
    private ImageView top_arrow;
    private int pagenum = 1;
    private Handler mHandler;
    private List<RadioButton> RBs = new ArrayList<RadioButton>();
    private List<Map<String, String>> titlelist = new ArrayList<Map<String, String>>();
    private UnreadArticle unreadArticle;

    private Handler errcode_handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;

            String status = data;
            // String errcode = data;
            Log.d("455454", "455445" + status);
            if (status.equals("false")) {

                Toast.makeText(MoreTopicActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {

            Map<String, Object> data = (Map<String, Object>) msg.obj;

            dialog.dismiss();
            List<Map<String, String>> topiclist = (List<Map<String, String>>) data
                    .get("topiclist");

            resultdata.addAll(topiclist);

            if (pagenum == 1) {
                adapter.setData(resultdata);
                mListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                adapter.notifyDataSetChanged();
                mListView.stopLoadMore();
            }

            if (titlelist.size() < 1) {
                titlelist = (List<Map<String, String>>) data.get("titlelist");

                // 添加单选按钮
                for (int i = 0; i < titlelist.size() && titlelist.size() < 7; i++) {
                    String title_name = titlelist.get(i)
                            .get("article_type_name");
                    RBs.get(i).setVisibility(View.VISIBLE);
                    RBs.get(i).setText(title_name);

                }

                for (int i = titlelist.size(); 0 < 6 - i
                        && titlelist.size() < 7; i++) {

                    RBs.get(i).setVisibility(View.GONE);

                }
            }

        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.more_topic);

        // sp = this.getSharedPreferences("userInfo", 0);

        init();
        initEvent();

    }

    void init() {
        more_topic_back = (RelativeLayout) findViewById(R.id.iv_back);
        TextView tv_title = (TextView) findViewById(R.id.tv_text);
        RelativeLayout rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        tv_title.setText("话题");
        rl_right.setVisibility(View.INVISIBLE);

        top_arrow = (ImageView) findViewById(R.id.top_arrow);

        mListView = (XListView) findViewById(R.id.more_topic_xlist);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(MoreTopicActivity.this);

        mHandler = new Handler();
        adapter = new MoreTopicAdapter(MoreTopicActivity.this);

        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView p_dialog = (TextView) dialog
                .findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("卖力加载中...");

        more_radio_select = (RadioGroup) findViewById(R.id.more_radio_select);
        RadioButton radio_1 = (RadioButton) findViewById(R.id.radio_1);
        RadioButton radio_2 = (RadioButton) findViewById(R.id.radio_2);
        RadioButton radio_3 = (RadioButton) findViewById(R.id.radio_3);
        RadioButton radio_4 = (RadioButton) findViewById(R.id.radio_4);
        RadioButton radio_5 = (RadioButton) findViewById(R.id.radio_5);
        RadioButton radio_6 = (RadioButton) findViewById(R.id.radio_6);

        RBs.add(radio_1);
        RBs.add(radio_2);
        RBs.add(radio_3);
        RBs.add(radio_4);
        RBs.add(radio_5);
        RBs.add(radio_6);

    }

    void initEvent() {

        key_value.put("page", "1");
        key_value.put("type", "00");

        dialog.show();
        final String MicroRead_URL = IpConfig.getUri("getMicroReadContent");

        Log.d("4444444", MicroRead_URL + "");
        setRadio(MicroRead_URL);
        more_topic_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();
            }
        });

        top_arrow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                setListViewPos(0);
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                int pos = position - 1;

                if (pos >= 0 && pos < adapter.list.size()) {

                    String content_id = adapter.list.get(pos).get("id");

//                    String url = IpConfig.getUri("getMicroReadContentDetail")
//                            + "content_id=" + content_id;
                    String url=IpConfig.getIp()+"mod=getMicroReadContentDetail&content_id=" + content_id;
                    Intent intent = new Intent();

                    intent.putExtra("title", "我的话题");

                    intent.putExtra("share_title",
                            adapter.list.get(pos).get("title"));
                    intent.putExtra("url", url);

                    intent.putExtra("img_url",
                            adapter.list.get(pos).get("small_image"));

                    intent.putExtra("brief",
                            adapter.list.get(pos).get("short_content"));

                    intent.putExtra("content_id",
                            adapter.list.get(pos).get("id"));

                    intent.setClass(MoreTopicActivity.this,
                            TopicShareWebActivity.class);

                    MoreTopicActivity.this.startActivity(intent);

                }
            }
        });

        final String URL = IpConfig.getUri("getMicroReadContent");
        more_radio_select
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @SuppressWarnings("null")
                    @SuppressLint("ShowToast")
                    @Override
                    public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                        // TODO Auto-generated method stub
                        switch (checkedId) {
                            case R.id.radio_1:
                                resultdata.clear();
                                adapter.notifyDataSetChanged();
                                pagenum = 1;
                                typeCode = titlelist.get(0).get("article_type_code");
                                //如果是视频模块
                                isVideoLengthShow = typeCode.equals("04");
                                key_value.put("page", "1");
                                key_value.put("type", typeCode);
                                setRadio(URL);
                                MobclickAgent.onEvent(MoreTopicActivity.this, Event_Choiceness);
                                break;

                            case R.id.radio_2:
                                resultdata.clear();
                                adapter.notifyDataSetChanged();
                                pagenum = 1;
                                typeCode = titlelist.get(1).get("article_type_code");
                                //如果是视频模块
                                isVideoLengthShow = typeCode.equals("04");
                                key_value.put("page", "1");
                                key_value.put("type", typeCode);

                                setRadio(URL);

                                MobclickAgent.onEvent(MoreTopicActivity.this, Event_Information);
                                break;

                            case R.id.radio_3:
                                resultdata.clear();
                                adapter.notifyDataSetChanged();
                                pagenum = 1;
                                typeCode = titlelist.get(2).get("article_type_code");
                                //如果是视频模块
                                isVideoLengthShow = typeCode.equals("04");
                                key_value.put("page", "1");
                                key_value.put("type", typeCode);

                                setRadio(URL);
                                MobclickAgent.onEvent(MoreTopicActivity.this, Event_Video);
                                break;

                            case R.id.radio_4:
                                resultdata.clear();
                                adapter.notifyDataSetChanged();
                                pagenum = 1;
                                typeCode = titlelist.get(3).get("article_type_code");
                                //如果是视频模块
                                isVideoLengthShow = typeCode.equals("04");
                                key_value.put("page", "1");
                                key_value.put("type", typeCode);

                                setRadio(URL);
                                MobclickAgent.onEvent(MoreTopicActivity.this, Event_Life);
                                break;
                            case R.id.radio_5:
                                resultdata.clear();
                                adapter.notifyDataSetChanged();
                                pagenum = 1;
                                typeCode = titlelist.get(4).get("article_type_code");
                                //如果是视频模块
                                isVideoLengthShow = typeCode.equals("04");
                                key_value.put("page", "1");
                                key_value.put("type", typeCode);

                                setRadio(URL);
                                MobclickAgent.onEvent(MoreTopicActivity.this, Event_Train);
                                break;
                            case R.id.radio_6:
                                resultdata.clear();
                                adapter.notifyDataSetChanged();
                                pagenum = 1;
                                typeCode = titlelist.get(5).get("article_type_code");
                                //如果是视频模块
                                isVideoLengthShow = typeCode.equals("04");
                                key_value.put("page", "1");
                                key_value.put("type", typeCode);

                                setRadio(URL);

                                break;
                            default:
                                break;
                        }

                    }

                    private void elseif(boolean b) {

                    }
                });

        unreadArticle=new UnreadArticle(false,this);
        unreadArticle.getUnreadArticle();

    }

    /**
     * 滚动ListView到指定位置
     *
     * @param pos
     */
    private void setListViewPos(int pos) {
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            mListView.smoothScrollToPosition(pos);
        } else {
            mListView.setSelection(pos);
        }
    }

    private void setRadio(String url) {

        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);
                        String status = "false";
                        Message message = Message.obtain();

                        message.obj = status;

                        errcode_handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        List<Map<String, String>> titlelist = new ArrayList<Map<String, String>>();
                        List<Map<String, String>> topiclist = new ArrayList<Map<String, String>>();
                        Map<String, Object> total_map = new HashMap<String, Object>();

                        try {

                            if (jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {

                                JSONObject jsonObject = new JSONObject(
                                        jsonString);
                                JSONObject dataObject = jsonObject
                                        .getJSONObject("data");

                                JSONArray title_List = dataObject
                                        .getJSONArray("title");

                                for (int i = 0; i < title_List.length(); i++) {
                                    JSONObject jsonObject2 = title_List
                                            .getJSONObject(i);
                                    Map<String, String> map = new HashMap<String, String>();
                                    // 迭代输出json的key作为map的key

                                    Iterator<String> iterator = jsonObject2
                                            .keys();
                                    while (iterator.hasNext()) {
                                        String key = iterator.next();
                                        String value = jsonObject2
                                                .getString(key);
                                        map.put(key, value);
                                    }
                                    titlelist.add(map);
                                }

                                JSONArray topic_List = dataObject
                                        .getJSONArray("list");

                                for (int i = 0; i < topic_List.length(); i++) {
                                    JSONObject jsonObject2 = topic_List
                                            .getJSONObject(i);
                                    Map<String, String> map = new HashMap<String, String>();
                                    // 迭代输出json的key作为map的key

                                    Iterator<String> iterator = jsonObject2
                                            .keys();
                                    while (iterator.hasNext()) {
                                        String key = iterator.next();
                                        String value = jsonObject2
                                                .getString(key);
                                        map.put(key, value);
                                    }
                                    topiclist.add(map);
                                }

                                total_map.put("titlelist", titlelist);

                                total_map.put("topiclist", topiclist);

                                Message message = Message.obtain();

                                message.obj = total_map;

                                handler.sendMessage(message);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    private void getRefreshItem() {
        Log.d("444444", pagenum + "");
        pagenum = 1;
        resultdata.clear();

        key_value.put("page", "1");

        final String PRODUCT_URL;

        PRODUCT_URL = IpConfig.getUri("getMicroReadContent");

        setRadio(PRODUCT_URL);

    }

    private void onLoad() {

        mListView.stopRefresh();

        mListView.stopLoadMore();

        mListView.setRefreshTime("刚刚");

    }

    private void getLoadMoreItem() {
        pagenum++;
        Log.d("555555", pagenum + "");

        key_value.put("page", pagenum + "");
        final String PRODUCT_URL;

        PRODUCT_URL = IpConfig.getUri("getMicroReadContent");

        setRadio(PRODUCT_URL);
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            public void run() {

                mListView.stopLoadMore();
                getRefreshItem();

                adapter.notifyDataSetChanged();

                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            public void run() {

                mListView.stopRefresh();
                getLoadMoreItem();

                adapter.notifyDataSetChanged();

                mListView.stopLoadMore();

            }
        }, 2000);
    }


}
