package com.cloudhome.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.AdministerActivity;
import com.cloudhome.activity.CustomerInfoActivity;
import com.cloudhome.activity.LoginActivity;
import com.cloudhome.activity.MainSearchActivity;
import com.cloudhome.activity.MoreExpertActivity;
import com.cloudhome.activity.RegisterActivity;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.MyClientBean;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetCustomer;
import com.cloudhome.network.Statistics;
import com.cloudhome.view.customview.SearchListView;
import com.cloudhome.view.xlistview.XListView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;


public class Micro_CommunityFragment extends BaseFragment implements NetResultListener,
        View.OnClickListener, XListView.IXListViewListener {
    SharedPreferences sp;
    private String loginString;
    private String token;
    private String user_id;
    private View mView;
    private View mHeader;
    private View zhan_ye_list, my_login_include;
    private TextView tab_login_but;
    private TextView tab_reg_but;
    //右侧搜索字母
    private SearchListView letterListView;
    private HashMap<String, Integer> alphaIndexerClient;
    private WindowManager windowManager;
    private TextView tvOverlay;
    private Handler handler = new Handler();
    private OverlayThread overlayThread;
    private ArrayList<MyClientBean> clientData;
    private ArrayList<MyClientBean> adapterData;
    private static final int GET_CUSTOMER = 1;
    //记录我的客户显示还是隐藏
    private boolean isClientShow = true;
    private RelativeLayout rl_expert;
    private RelativeLayout rl_administer;
    private RelativeLayout rl_my_client;
    private XListView lv_my_client;
    //统计接口
    public Statistics statistics;
    private String RBstr = "0";
    public static String expert_jump = "false";

    private RelativeLayout rl_search;
    //arrow
    private ImageView expert_arrow;
    private String Event_MyClient = "Micro_CommunityFragment_MyClient";
    private String Event_Expert = "Micro_CommunityyFragment_Expert";

    private MyAdapter adapter;
    private boolean refresh = false;

    private Dialog dialog;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sp = getActivity().getSharedPreferences("userInfo", 0);
        loginString = sp.getString("Login_STATE", "none");
        //        user_id = sp.getString("Login_UID", "");
        user_id = sp.getString("Login_UID_ENCODE", "");
        token = sp.getString("Login_TOKEN", "");
        adapterData = new ArrayList<>();
        clientData = new ArrayList<>();
        initView(inflater, container);
        EventBus.getDefault().register(this);
        return mView;
    }

    private void initDialog() {
        dialog = new Dialog(getActivity(), R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView dialogContent = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        dialogContent.setText("加载中...");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID_ENCODE", "");
        if (loginString.equals("none")) {
            my_login_include.setVisibility(View.VISIBLE);
            zhan_ye_list.setVisibility(View.GONE);
            initLoginEvent();
        } else {
            Log.i("接收到receiver", "000");
            zhan_ye_list.setVisibility(View.VISIBLE);
            my_login_include.setVisibility(View.GONE);
            loginString = sp.getString("Login_STATE", "none");
            user_id = sp.getString("Login_UID", "");
            token = sp.getString("Login_TOKEN", "");
            dialog.show();
            initEvent();
        }
    }

    private void initView(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.fragment_task_lottery, container, false);
        mHeader = inflater.inflate(R.layout.layout_header_exhibition, null);
        zhan_ye_list = mView.findViewById(R.id.zhan_ye_list);
        my_login_include = mView.findViewById(R.id.my_login_include);
        tab_login_but = (TextView) mView.findViewById(R.id.tab_login_but);
        tab_reg_but = (TextView) mView.findViewById(R.id.tab_reg_but);

        rl_search = (RelativeLayout) mHeader.findViewById(R.id.rl_search);
        rl_expert = (RelativeLayout) mHeader.findViewById(R.id.rl_expert);
        rl_administer = (RelativeLayout) mHeader.findViewById(R.id.rl_administer);
        rl_my_client = (RelativeLayout) mHeader.findViewById(R.id.rl_my_client);
        lv_my_client = (XListView) mView.findViewById(R.id.lv_my_client);
        expert_arrow = (ImageView) mHeader.findViewById(R.id.expert_arrow);

        rl_search.setOnClickListener(this);
        rl_expert.setOnClickListener(this);
        rl_my_client.setOnClickListener(this);
        rl_administer.setOnClickListener(this);
        lv_my_client.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                i = i - lv_my_client.getHeaderViewsCount();
                if (adapterData != null && adapterData.size() > 0) {

                    Intent intent = new Intent();
                    // 设置传递的参数
                    intent.putExtra("customer_id", adapterData.get(i).getClientId());
                    intent.setClass(getActivity(), CustomerInfoActivity.class);
                    startActivity(intent);
                    MobclickAgent.onEvent(getActivity(), Event_MyClient);
                }

            }
        });
        lv_my_client.addHeaderView(mHeader);
        lv_my_client.setPullRefreshEnable(true);
        lv_my_client.setXListViewListener(this);
        lv_my_client.setPullLoadEnable(false);
        letterListView = (SearchListView) mView.findViewById(R.id.search_lv);
        adapter = new MyAdapter(adapterData);
        lv_my_client.setAdapter(adapter);
        initDialog();
        if (loginString.equals("none")) {
            my_login_include.setVisibility(View.VISIBLE);
            zhan_ye_list.setVisibility(View.GONE);
            initLoginEvent();
        } else {
            zhan_ye_list.setVisibility(View.VISIBLE);
            my_login_include.setVisibility(View.GONE);
            dialog.show();
            initEvent();
        }

    }

    private void initEvent() {
        GetCustomer getCustomer = new GetCustomer(this);
        getCustomer.execute(user_id, GET_CUSTOMER, clientData, token);
    }


    private void initRightSearch() {
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        overlayThread = new OverlayThread();
        initOverlay();
    }


    void initLoginEvent() {
        tab_login_but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        });
        tab_reg_but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), RegisterActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }


    private final String mPageName = "Micro_CommunityFragment";

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName); // 统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_CUSTOMER:
                if (flag == MyApplication.DATA_OK) {
                    initAlphaIndexer(clientData);
                    initRightSearch();

                    adapterData.clear();
                    if (isClientShow) {
                        adapterData.addAll(clientData);
                    }

                    adapter.notifyDataSetChanged();


                } else if (flag == MyApplication.DATA_EMPTY) {
                    if (refresh) {
                        adapterData.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (refresh) {
                        Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
                    } else {
                        adapterData.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    }

                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (refresh) {
                    lv_my_client.setRefreshTime("刚刚");
                }
                refresh = false;
                lv_my_client.stopRefresh();

                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), MainSearchActivity.class);
                intent1.putExtra("fromWhere", 2);
                getActivity().startActivity(intent1);
                break;
            case R.id.rl_my_client:
                adapterData.clear();
                if (!isClientShow) {
                    adapterData.addAll(clientData);
                    expert_arrow.setBackgroundResource(R.drawable.icon_down);
                } else {
                    expert_arrow.setBackgroundResource(R.drawable.icon_right);
                }
                adapter.notifyDataSetChanged();
                isClientShow = !isClientShow;

                break;
            case R.id.rl_expert:
                statistics = new Statistics(Micro_CommunityFragment.this);
                statistics.execute("found_expert");
                RBstr = "-1";
                expert_jump = "ture";
                Intent intent = new Intent();
                intent.setClass(getActivity(), MoreExpertActivity.class);
                getActivity().startActivity(intent);

                MobclickAgent.onEvent(getActivity(), Event_Expert);
                break;
            case R.id.rl_administer:
                Intent administerIntent = new Intent(getActivity(), AdministerActivity.class);
                startActivity(administerIntent);
                break;

        }
    }

    @Override
    public void onRefresh() {
        refresh = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initEvent();

            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {

    }

    private static final String TAG = "Micro_CommunityFragment";

    private class LetterListViewListener implements SearchListView.OnTouchingLetterChangedListener {
        @SuppressLint("NewApi")
        @Override
        public void onTouchingLetterChanged(final String s) {
            Log.e(TAG, "onTouchingLetterChanged: " + s);
            if (alphaIndexerClient.get(s) != null) {
                int position = alphaIndexerClient.get(s);
                if (isClientShow) {
                    lv_my_client.setSelection(position + lv_my_client.getHeaderViewsCount());
                }
            }
            tvOverlay.setText(s);
            tvOverlay.setVisibility(View.VISIBLE);
            handler.removeCallbacks(overlayThread);
            // 延迟一秒后执行，让overlay为不可见
            handler.postDelayed(overlayThread, 1000);
        }
    }

    private void initAlphaIndexer(ArrayList<MyClientBean> list) {
        alphaIndexerClient = new HashMap<String, Integer>();

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {

                String firstLetter = list.get(i).getNameFirstWord();
                if (i >= 1 && firstLetter.equals(list.get(i - 1).getNameFirstWord())) {
                    continue;
                }
                alphaIndexerClient.put(firstLetter, i);
            }
        }


    }

    // 初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        tvOverlay = (TextView) inflater.inflate(R.layout.serach_overlay, null);
        tvOverlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(tvOverlay, layoutParams);
    }

    // 设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            tvOverlay.setVisibility(View.GONE);
        }

    }

    class MyAdapter extends BaseAdapter {
        private ArrayList<MyClientBean> list;

        public MyAdapter(ArrayList<MyClientBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_client_exhibition, null);
                holder.imgClientAvatar = (ImageView) convertView.findViewById(R.id.img_client_avatar);
                holder.tvClientName = (TextView) convertView.findViewById(R.id.tv_client_name);
                holder.tvPolicyNum = (TextView) convertView.findViewById(R.id.tv_client_policyscale_num);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            MyClientBean bean = list.get(i);
            holder.tvClientName.setText(bean.getName());
            holder.tvPolicyNum.setText(bean.getClientPolicyNum() + "");
            String sex = bean.getClientSex();
            int avatarResId = -1;
            if ("01".equals(sex)) {
                avatarResId = R.drawable.icon_client_male;
            } else if ("02".equals(sex)) {
                avatarResId = R.drawable.icon_client_female;
            }

            Glide.with(Micro_CommunityFragment.this)
                    .load(avatarResId)
                    .placeholder(R.drawable.icon_avatar)
                    .error(R.drawable.icon_avatar)//占位图 图片正在加载
                    .into(holder.imgClientAvatar);
            return convertView;
        }

        class ViewHolder {
            ImageView imgClientAvatar;
            TextView tvClientName;
            TextView tvPolicyNum;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }
}
