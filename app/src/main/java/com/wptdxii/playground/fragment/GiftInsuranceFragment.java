package com.wptdxii.playground.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.GiftInsuranceActivity;
import com.cloudhome.activity.LoginActivity;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetGiftInsuranceList;
import com.cloudhome.network.Statistics;
import com.cloudhome.view.customview.RotateTextView;
import com.cloudhome.view.xlistview.XListView;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class GiftInsuranceFragment extends BaseFragment implements XListView.IXListViewListener,NetResultListener {
    private View view;
    private XListView gift_insurance_xlist;
//    private PublicLoadPage mLoadPage;
    private final int GET_PRODUCT_LIST=1;
    SharedPreferences sp;
    private String user_id;
    private String token;
    private String loginString;
    private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private int score;
    private GiftInsuranceAdapter adapter;
    private boolean isRefresh;
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
    private String time = "";
    private String Event_ComplimentaryIns = "GiftInsuranceFragment_ComplimentaryIns";

    public GiftInsuranceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_gift_insurance, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
//        mLoadPage = new PublicLoadPage((LinearLayout)view.findViewById(R.id.common_load)) {
//            @Override
//            public void onReLoadCLick(LinearLayout layout,RelativeLayout rl_progress, ImageView iv_loaded,
//                                      TextView tv_loaded, Button btLoad) {
//                initData();
//            }
//        };
        gift_insurance_xlist= (XListView) view.findViewById(R.id.gift_insurance_xlist);
        gift_insurance_xlist.setPullLoadEnable(false);
        gift_insurance_xlist.setPullRefreshEnable(true);
        gift_insurance_xlist.setXListViewListener(this);
        adapter=new GiftInsuranceAdapter();
        gift_insurance_xlist.setAdapter(adapter);
        gift_insurance_xlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                loginString = sp.getString("Login_STATE", "none");
                if (loginString.equals("none")) {
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent,160);
                }else{
                    int m=i-1;
                    Intent intent = new Intent(getActivity(), GiftInsuranceActivity.class);
                    String product_cover=list.get(m).get("product_cover").toString();
                    String product_id=list.get(m).get("product_id").toString();
                    String product_name=list.get(m).get("product_name").toString();
                    int val= (int) list.get(m).get("val");
                    int score= (int) list.get(m).get("score");
                    intent.putExtra("product_cover",product_cover);
                    intent.putExtra("score",score);
                    intent.putExtra("val",val);
                    intent.putExtra("product_id",product_id);
                    intent.putExtra("product_name",product_name);
                    startActivityForResult(intent,160);
                }
                Statistics statistics=new Statistics(GiftInsuranceFragment.this);
                String product_id=list.get(i-1).get("product_id").toString();
                statistics.execute("give_list_"+product_id);
                MobclickAgent.onEvent(getActivity(), Event_ComplimentaryIns);

            }
        });
        initData();
    }

    public void refreshView(){
        initData();
    }

    private void initData() {
        Log.d("GetGiftInsuranceList", "initData------");
        sp = getActivity().getSharedPreferences("userInfo", 0);
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        loginString = sp.getString("Login_STATE", "none");
        GetGiftInsuranceList request=new GetGiftInsuranceList(this);
        request.execute(user_id,GET_PRODUCT_LIST,token);
    }


    @Override
    public void onRefresh() {
        isRefresh=true;
        Log.i("刷新了","onRefresh");
        initData();
    }

    @Override
    public void onLoadMore() {

    }



    class GiftInsuranceAdapter extends BaseAdapter {

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
        public View getView(int i, View view, ViewGroup viewGroup) {
           ViewHolder holder=null;
            if(view==null){
                holder=new ViewHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.item_gift_insurance,viewGroup,false);
                holder.iv_gift_insurance= (ImageView) view.findViewById(R.id.iv_gift_insurance);
                holder.tv_gift_insurance_tag= (RotateTextView) view.findViewById(R.id.tv_gift_insurance_tag);
                holder.tv_gift_insurance_tag.setDegrees(-45);
                holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                holder.tv_age_left= (TextView) view.findViewById(R.id.tv_age_left);
                holder.tv_age= (TextView) view.findViewById(R.id.tv_age);
                holder.tv_period_left= (TextView) view.findViewById(R.id.tv_period_left);
                holder.tv_period= (TextView) view.findViewById(R.id.tv_period);
                holder.tv_score_left= (TextView) view.findViewById(R.id.tv_score_left);
                holder.tv_score= (TextView) view.findViewById(R.id.tv_score);
                holder.view_bottom=view.findViewById(R.id.view_bottom);
                view.setTag(holder);
            }else{
                holder= (ViewHolder) view.getTag();
            }
            if(i==list.size()-1){
                holder.view_bottom.setVisibility(View.VISIBLE);
            }else{
                holder.view_bottom.setVisibility(View.GONE);
            }
            Map<String,Object> map=list.get(i);

            Glide.with(GiftInsuranceFragment.this)
                    .load((map.get("product_icon").toString()))
                    .crossFade()
                    .into(holder.iv_gift_insurance);
            String label=map.get("label").toString();
            if(TextUtils.isEmpty(label)){
                holder.tv_gift_insurance_tag.setVisibility(View.GONE);
            }else{
                holder.tv_gift_insurance_tag.setVisibility(View.VISIBLE);
                holder.tv_gift_insurance_tag .setText(label);
            }
            holder.tv_name.setText(map.get("product_name").toString());
            holder.tv_age_left.setText(map.get("age_title").toString());
            holder.tv_age.setText(map.get("age_label").toString());
            holder.tv_period_left.setText(map.get("ensure_title").toString());
            holder.tv_period.setText(map.get("ensure_label").toString());
            holder.tv_score_left.setText(map.get("integral_title").toString());
            holder.tv_score.setText(map.get("integral_label").toString());
            return view;
        }
    }

    class ViewHolder{
        private ImageView iv_gift_insurance;
        private RotateTextView tv_gift_insurance_tag;
        private TextView tv_name;
        private TextView tv_age_left;
        private TextView tv_age;
        private TextView tv_period_left;
        private TextView tv_period;
        private TextView tv_score_left;
        private TextView tv_score;
        private View view_bottom;

    }


    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {

        switch (action)
        {
            case GET_PRODUCT_LIST:
                onLoad();
                if (flag == MyApplication.DATA_OK) {
                    list.clear();
                    list= (ArrayList<Map<String, Object>>) dataObj;
                    if(isRefresh){
                        adapter.notifyDataSetChanged();
                    }else{
                        gift_insurance_xlist.setAdapter(adapter);
                    }
                    setListViewHeightBasedOnChildren((ListView)gift_insurance_xlist);

//                    mLoadPage.loadSuccess(null,null);
                } else if (flag == MyApplication.NET_ERROR) {
//                    mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                }else if(flag == MyApplication.DATA_EMPTY){
//                    mLoadPage.loadFail(MyApplication.NO_DATA, MyApplication.BUTTON_RELOAD, 0);
                }else if(flag == MyApplication.JSON_ERROR){
//                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 0);
                }else if(flag == MyApplication.DATA_ERROR){
//                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 0);
                }
                break;
        }
    }

    private void onLoad() {
        gift_insurance_xlist.stopRefresh();
        if (time.equals("")) {
            gift_insurance_xlist.setRefreshTime(" 刚刚 ");
            time = dateFormat.format(new Date());
        } else {
            gift_insurance_xlist.setRefreshTime(" 今天 " + time);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==160&&resultCode==170){
            initData();
        }
    }

//    重新计算xlistview的高度
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
