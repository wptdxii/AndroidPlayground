package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UnderInsuerBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetAgents;
import com.cloudhome.utils.CircleImage;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.xlistview.XListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyUnderInsuerActivity extends BaseActivity implements View.OnClickListener,NetResultListener ,XListView.IXListViewListener{
    private TextView tv_text;
    private RelativeLayout iv_back;
    private RelativeLayout rl_right;
    private View bottom_line;

    private RelativeLayout rl_a;
    private TextView tv_people_num;//总人数
    private TextView tv_zhixia;//直辖人数
    private TextView tv_suoxia;//所辖人数

    private RelativeLayout rl_f;
    private TextView tv_people_num2;

    private XListView my_insuer_list;


    private final int GET_AGENT = 1;
    private ArrayList<UnderInsuerBean> newList;
    private ArrayList<UnderInsuerBean> oldList=new ArrayList<UnderInsuerBean>();
    private String user_id;
    private String token;
    private Map<String,Object> dataValue=new HashMap<String,Object>();
    private UnderInsuerAdapter adapter;
    private Handler mHandler;
    private int page=0;
    private RelativeLayout rl_invite;
    private RelativeLayout rl_dibu;
    private RelativeLayout rl_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_under_insuer);
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        initView();
        fetchData("0");

    }

    private void fetchData(String page) {
        newList = new ArrayList<UnderInsuerBean>();
        GetAgents getAgent = new GetAgents(this);
        getAgent.execute(user_id, GET_AGENT, page, newList, dataValue,token);
    }

    private void initView() {
        mHandler = new Handler();
        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_text.setText("我的辖下");
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        bottom_line = findViewById(R.id.bottom_line);
        bottom_line.setVisibility(View.GONE);
        rl_right.setVisibility(View.INVISIBLE);

        rl_a = (RelativeLayout) findViewById(R.id.rl_a);
        tv_people_num = (TextView) findViewById(R.id.tv_people_num);
        tv_zhixia = (TextView) findViewById(R.id.tv_zhixia);//直辖人数
        tv_suoxia = (TextView) findViewById(R.id.tv_suoxia);//所辖人数

        rl_f = (RelativeLayout) findViewById(R.id.rl_f);
        tv_people_num2 = (TextView) findViewById(R.id.tv_people_num2);
        my_insuer_list = (XListView) findViewById(R.id.my_insuer_list);

        my_insuer_list= (XListView) findViewById(R.id.my_insuer_list);
        my_insuer_list.setPullLoadEnable(true);
        my_insuer_list.setPullRefreshEnable(false);
        my_insuer_list.setXListViewListener(MyUnderInsuerActivity.this);

        rl_dibu=(RelativeLayout) findViewById(R.id.rl_dibu);
        rl_main=(RelativeLayout) findViewById(R.id.rl_main);

        rl_invite= (RelativeLayout) findViewById(R.id.rl_invite);
        rl_invite.setOnClickListener(this);

        my_insuer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UnderInsuerBean bean=oldList.get(i-1);
                String state=bean.getState();
                if("00".equals(state)){
                    String id=bean.getId();
                    Log.i("id-----",id);
                    String url = IpConfig.getIp()+ "user_id="+ id +"&token="+token+ "&mod=getHomepageForExpert";
                    Intent intent = new Intent();
                    intent.putExtra("title", "我的微站");
                    intent.putExtra("url", url);
                    intent.putExtra("needShare", false);
                    intent.setClass(MyUnderInsuerActivity.this, MicroShareWebActivity.class);
                    startActivity(intent);
                }else{
                    return;
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_invite:
                Intent intent = new Intent();
                intent.setClass(MyUnderInsuerActivity.this, InvitFriendsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                my_insuer_list.stopLoadMore();
                getRefreshItem();
                onLoad();
            }
        }, 2000);
    }

    private void getRefreshItem() {
            page = 0;
            adapter.notifyDataSetChanged();
            fetchData("0");
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                my_insuer_list.stopRefresh();
                getLoadMoreItem();
            }
        }, 2000);
    }

    private void getLoadMoreItem() {
        page++;
        fetchData(page+"");
    }

    public void onLoad(){
        my_insuer_list.stopRefresh();
        my_insuer_list.stopLoadMore();
        my_insuer_list.setRefreshTime("刚刚");
    }

    class UnderInsuerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return oldList.size();
        }

        @Override
        public Object getItem(int i) {
            return oldList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder=null;
            if(convertView==null){
                holder=new ViewHolder();
                convertView= LayoutInflater.from(MyUnderInsuerActivity.this).inflate(R.layout.item_list_my_insuer, null);
                holder.iv_left_head= (CircleImage) convertView.findViewById(R.id.iv_left_head);
                holder.iv_head= (ImageView) convertView.findViewById(R.id.iv_head);
                holder.tv_insuer_name=(TextView) convertView.findViewById(R.id.tv_insuer_name);
                holder.rl_company= (RelativeLayout) convertView.findViewById(R.id.rl_company);
                holder.tv_company=(TextView) convertView.findViewById(R.id.tv_company);
                holder.rl_address= (RelativeLayout) convertView.findViewById(R.id.rl_address);
                holder.tv_address=(TextView) convertView.findViewById(R.id.tv_address);
                holder.rl_module1= (RelativeLayout) convertView.findViewById(R.id.rl_module1);
                holder.tv1=(TextView) convertView.findViewById(R.id.tv1);
                holder.rl_module2= (RelativeLayout) convertView.findViewById(R.id.rl_module2);
                holder.tv2=(TextView) convertView.findViewById(R.id.tv2);
                holder.rl_module3= (RelativeLayout) convertView.findViewById(R.id.rl_module3);
                holder.tv3=(TextView) convertView.findViewById(R.id.tv3);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder) convertView.getTag();
            }
            UnderInsuerBean bean=oldList.get(i);

            Glide.with(MyUnderInsuerActivity.this)
                    .load(bean.getAvatar())
                    .placeholder(R.drawable.white_bg)
                    .into( holder.iv_left_head);
            holder.tv_insuer_name.setText(bean.getName());
            if("00".equals(bean.getState())){
                holder.iv_head.setBackgroundResource(R.drawable.under_already_renzheng);
            }else{
                holder.iv_head.setBackgroundResource(R.drawable.under_not_renzheng);
            }
            if(!TextUtils.isEmpty(bean.getCompany())){
                holder.rl_company.setVisibility(View.VISIBLE);
                holder.tv_company.setText(bean.getCompany());
            }else{
                holder.rl_company.setVisibility(View.GONE);
            }
            if(!TextUtils.isEmpty(bean.getCity())){
                holder.rl_address.setVisibility(View.VISIBLE);
                holder.tv_address.setText(bean.getCity());
            }else{
                holder.rl_address.setVisibility(View.GONE);
            }
            if(!TextUtils.isEmpty(bean.getRef_num())){
                holder.rl_module1.setVisibility(View.VISIBLE);
                holder.tv1.setText(bean.getRef_num()+"人");
            }else{
                holder.rl_module1.setVisibility(View.INVISIBLE);
            }
            if(!TextUtils.isEmpty(bean.getOrder_num())){
                holder.rl_module2.setVisibility(View.VISIBLE);
                holder.tv2.setText(bean.getOrder_num()+"单");
            }else{
                holder.rl_module2.setVisibility(View.INVISIBLE);
            }
            if(!TextUtils.isEmpty(bean.getPremiums())){
                holder.rl_module3.setVisibility(View.VISIBLE);
                holder.tv3.setText(bean.getPremiums()+"万");
            }else{
                holder.rl_module3.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            private CircleImage iv_left_head;
            private ImageView iv_head;
            private TextView tv_insuer_name;
            private RelativeLayout rl_company;
            private TextView tv_company;
            private RelativeLayout rl_address;
            private TextView tv_address;
            private RelativeLayout rl_module1;
            private TextView tv1;
            private RelativeLayout rl_module2;
            private TextView tv2;
            private RelativeLayout rl_module3;
            private TextView tv3;
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_AGENT:
                if (flag == MyApplication.DATA_OK) {
                    if (page == 0) {
                        if (oldList.size() < 1) {
                            if((boolean)dataValue.get("isVIP")){
                                rl_a.setVisibility(View.VISIBLE);
                                rl_f.setVisibility(View.GONE);
                                tv_people_num.setText(dataValue.get("total").toString());
                                tv_zhixia.setText(dataValue.get("direct").toString());
                                tv_suoxia.setText(dataValue.get("indirect").toString());
                            }else{
                                rl_a.setVisibility(View.GONE);
                                rl_f.setVisibility(View.VISIBLE);
                                tv_people_num2.setText(dataValue.get("total").toString());
                            }
                            oldList.addAll(newList);
                            adapter = new UnderInsuerAdapter();
                            my_insuer_list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        my_insuer_list.stopLoadMore();
                        if (newList.size() < 1) {
                            Toast.makeText(MyUnderInsuerActivity.this, "没有找到人员信息", Toast.LENGTH_SHORT).show();
                            my_insuer_list.stopLoadMore();
                            rl_main.setVisibility(View.GONE);
                            return;
                        }
                    } else if (newList.size() < 1) {
                        Toast.makeText(MyUnderInsuerActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                        my_insuer_list.stopLoadMore();
                    } else {
                        oldList.addAll(newList);
                        adapter.notifyDataSetChanged();
                        my_insuer_list.stopLoadMore();
                    }
                } else if (flag == MyApplication.NET_ERROR) {
                    rl_main.setVisibility(View.GONE);

                } else if (flag == MyApplication.DATA_EMPTY) {
                    rl_main.setVisibility(View.GONE);

                } else if (flag == MyApplication.JSON_ERROR) {
                    rl_main.setVisibility(View.GONE);

                } else if (flag == MyApplication.DATA_ERROR) {
                    rl_main.setVisibility(View.GONE);

                }
                break;
        }
    }
}
