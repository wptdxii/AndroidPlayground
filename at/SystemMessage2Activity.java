package com.cloudhome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.network.retrofit.callback.BaseCallBack;
import com.cloudhome.network.retrofit.entity.SystemMessageListEntity;
import com.cloudhome.network.retrofit.entity.SystemMessageListEntity.ContentMainPopBean;
import com.cloudhome.network.retrofit.service.ApiFactory;
import com.cloudhome.view.xlistview.XListView;
import com.cloudhome.view.xlistview.XListView.IXListViewListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SystemMessage2Activity extends BaseActivity implements
        IXListViewListener {
    private MyAdapter mAdapter;
    private List<ContentMainPopBean> mResultDataList = new ArrayList<ContentMainPopBean>();
    private RelativeLayout s_m_back;
    private RelativeLayout rl_right;
    private TextView tv_text;
    private String user_id;
    private String token;
    private int mPagenum = 0;
    private Handler mHandler;
    private XListView mXlist;
    private String mSuid;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_message);
        initView();
        initData();
    }

    private void initView() {
        s_m_back = (RelativeLayout) findViewById(R.id.rl_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_share);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text = (TextView) findViewById(R.id.tv_title);
        tv_text.setText("通知");
        mXlist = (XListView) findViewById(R.id.s_m_xlist);
        mXlist.setPullLoadEnable(true);
        mXlist.setXListViewListener(SystemMessage2Activity.this);
        mAdapter = new MyAdapter(SystemMessage2Activity.this);
        mHandler = new Handler();
        mXlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                int pos = position - 1;
                if (pos >= 0 && pos < mResultDataList.size()) {
                    Intent intent = new Intent();
                    intent.putExtra("messageId", mResultDataList.get(pos).getId());
                    intent.setClass(SystemMessage2Activity.this, MessageDetailActivity.class);
                    startActivity(intent);
                }
            }
        });

        s_m_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                EventBus.getDefault().post(new DisorderJumpEvent(0));
                Intent intent1 = new Intent(SystemMessage2Activity.this, AllPageActivity.class);
                startActivity(intent1);
            }
        });
    }

    void initData() {
        mSuid = sp.getString("Encrypt_UID", "");
        getSystemMessageList();
    }

    /**
     * 获取消息列表
     */
    private void getSystemMessageList() {
        ApiFactory.getHoustonApi()
                .getSystemMessageList(mSuid, mPagenum)
                .enqueue(new BaseCallBack<SystemMessageListEntity>() {
                    @Override
                    protected void onResponse(SystemMessageListEntity body) {
                        if (body != null) {
                            handleSuccessData(body.getContent());
                        } else {
                            handleFailureData();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<SystemMessageListEntity> call, Throwable t) {
                        handleFailureData();
                    }
                });
    }


    private void handleSuccessData(List<ContentMainPopBean> list) {
        if (mPagenum == 0) {
            if (mResultDataList.size() < 1) {
                mResultDataList.addAll(list);
                mAdapter.setData(mResultDataList);
                mXlist.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mResultDataList.addAll(list);
            mAdapter.notifyDataSetChanged();
            mXlist.stopLoadMore();
        }
        mXlist.setPullLoadEnable(true);
        mXlist.setPullRefreshEnable(true);
    }

    private void handleFailureData() {
        mXlist.setPullLoadEnable(true);
        mXlist.setPullRefreshEnable(true);
    }


    public class MyAdapter extends BaseAdapter {
        Context context = null;
        private LayoutInflater layoutInflater;
        private List<ContentMainPopBean> list = null;

        public MyAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<ContentMainPopBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.system_mes_item, null);
                holder.s_sms_content = (TextView) convertView.findViewById(R.id.s_sms_content);
                holder.s_sms_time = (TextView) convertView.findViewById(R.id.s_sms_time);
                holder.s_sms_new = (TextView) convertView.findViewById(R.id.s_sms_new);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                resetViewHolder(holder);
            }
            String state = list.get(position).getState();
            if (state.equals("2")) {
                holder.s_sms_new.setVisibility(View.GONE);
            } else if (state.equals("1")) {
                holder.s_sms_new.setVisibility(View.VISIBLE);
            }
            holder.s_sms_content.setText(list.get(position).getContent());
            holder.s_sms_time.setText(list.get(position).getAddTime());
            return convertView;
        }
    }

    protected void resetViewHolder(ViewHolder p_ViewHolder) {
        p_ViewHolder.s_sms_content.setText(null);
        p_ViewHolder.s_sms_time.setText(null);
    }

    class ViewHolder {
        TextView s_sms_content;
        TextView s_sms_time;
        TextView s_sms_new;
    }


    private void getRefreshItem() {
        mPagenum = 0;
        mResultDataList.clear();
        mAdapter.notifyDataSetChanged();
        getSystemMessageList();
    }

    private void getLoadMoreItem() {
        mPagenum++;
        getSystemMessageList();
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                mXlist.stopLoadMore();
                getRefreshItem();
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                mXlist.stopRefresh();
                getLoadMoreItem();
                mXlist.setPullLoadEnable(false);
                mXlist.setPullRefreshEnable(false);
            }
        }, 2000);

    }

    private void onLoad() {
        mXlist.stopRefresh();
        mXlist.stopLoadMore();
        mXlist.setRefreshTime("刚刚");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            EventBus.getDefault().post(new DisorderJumpEvent(0));
            Intent intent1 = new Intent(SystemMessage2Activity.this, AllPageActivity.class);
            startActivity(intent1);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPagenum = 0;
        mResultDataList.clear();
        getSystemMessageList();
    }
}
