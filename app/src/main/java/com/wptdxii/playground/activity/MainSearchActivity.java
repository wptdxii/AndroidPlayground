package com.wptdxii.playground.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.adapter.CategorylistAdapter;
import com.cloudhome.adapter.PeopleTypeAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.ExpertBean;
import com.cloudhome.bean.MyClientBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.SearchCustomer;
import com.cloudhome.network.SearchExpert;
import com.cloudhome.network.SearchUserOfUnder;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.ListViewScrollView;
import com.cloudhome.view.customview.RoundImageView;
import com.cloudhome.view.flowlayout.FlowLayout;
import com.cloudhome.view.flowlayout.TagAdapter;
import com.cloudhome.view.flowlayout.TagFlowLayout;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by xionghu on 2016/8/26.
 * Email：965705418@qq.com
 */
public class MainSearchActivity extends BaseActivity implements View.OnClickListener,NetResultListener{

    private LinearLayout ll_lin1;
    private LinearLayout ll_lin2;
    private LinearLayout ll_lin3;
    private LinearLayout ll_lin4;

    private TextView tv_tab1;
    private TextView tv_tab2;
    private TextView tv_tab3;
    private TextView tv_tab4;

    private ImageView iv_tab1;
    private ImageView iv_tab2;
    private ImageView iv_tab4;
    private RelativeLayout iv_back;

    private ClearEditText search_edit;
    private RelativeLayout rl_tablayout;

    //公司
    private TagFlowLayout tfl_company;
    private List<String> companynamelist;
    private   int companynamepos =-1;
    private TagAdapter tagAdapter;
    private String company="";



    //品类
    private ListView lv_category_list;
    private ArrayList<HashMap<String, String>> category_list;
    private CategorylistAdapter categoryAdapter;
    public  int categorypos =-1;
    private String type="";

    //适用人群
    private ListView lv_people_list;
    private ArrayList<HashMap<String,String>> peopleTypeList;
    private PeopleTypeAdapter peopleTypeAdapter;
    public  int PeopleTypepos =-1;
    private TextView tv_search;
    private String tags="";

    private boolean searchProductFlag =true;

    private SearchCustomer searchCustomer;
    private SearchUserOfUnder searchUserOfUnder;
    private SearchExpert searchExpert;

    private static final int SEARCH_MY_CLIENT=1;
    private static final int SEARCH_MY_UNDER=2;
    private static final int SEARCH_EXPERT=3;
    private ArrayList<MyClientBean> searchClientList;
    private ArrayList<MyClientBean> searchUnderList;
    private ArrayList<ExpertBean> searchExpertList;
    private ListViewScrollView lv_client;
    private ListViewScrollView lv_under;
    private ListViewScrollView lv_expert;
    private RelativeLayout rl_search_people;
    private ScrollView scroll_search_people;
    private LinearLayout search_kinds_layout;
    SharedPreferences sp;
    private String loginString;
    private String token;
    private String user_id;
    private String user_id_encode="";
    private int fromWhere;

    private boolean isMyClientHasData=true;
    private boolean isMyUnderHasData=true;
    private boolean isExpertHasData=true;


    private String Event_Company = "MainSearchActivity_Company";
    private String Event_Category = "MainSearchActivity_Catetory";
    private String Event_Partner = "MainSearchActivity_Partner";
    private String Event_ApplicablePeople = "MainSearchActivity_ApplicablePeople";

    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    final LayoutInflater mInflater = LayoutInflater.from(MainSearchActivity.this);
                    tfl_company.setMaxSelectCount(1);
                    tagAdapter=new TagAdapter<String>(companynamelist)
                    {
                        @Override
                        public View getView(FlowLayout parent, int position, String s)
                        {
                            TextView tv = (TextView) mInflater.inflate(R.layout.main_search_company_tv,
                                    tfl_company, false);
                            tv.setText(s);
                            if(tv.isClickable())
                            {
                                companynamepos=position;
                            }

                            return tv;
                        }

                        @Override
                        public boolean setSelected(int position, String s)
                        {
                            return position==companynamepos;
                        }

                    };
                    if(companynamepos!=-1){
                        tagAdapter.setSelectedList(companynamepos);
                    }
                    tfl_company.setAdapter(tagAdapter);


                    break;

                case 0:
                    categoryAdapter = new CategorylistAdapter(MainSearchActivity.this);
                    categoryAdapter.setData(category_list);
                    lv_category_list.setAdapter(categoryAdapter);
                    break;
                case 3:
                    peopleTypeAdapter = new PeopleTypeAdapter(MainSearchActivity.this);
                    peopleTypeAdapter.setData(peopleTypeList);
                    lv_people_list.setAdapter(peopleTypeAdapter);
                    break;
                case 1:


                    Toast.makeText(MainSearchActivity.this, "网络连接失败，请确认网络连接后重试",
                            Toast.LENGTH_SHORT).show();

                    break;
                case 2:

                    Toast.makeText(MainSearchActivity.this, msg.obj + "",
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
        sp = getSharedPreferences("userInfo", 0);
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");
        Intent intent=getIntent();
        fromWhere=intent.getIntExtra("fromWhere",0);
        //初始化控件
        initView();
        initEvent();
        if(fromWhere==1){
            ll_lin1.performClick();
        }else if(fromWhere==2){
            ll_lin3.performClick();
        }

    }

    private void initView() {
        search_edit = (ClearEditText)findViewById(R.id.search_edit);
        tv_search = (TextView)findViewById(R.id.tv_search);
        tv_search.setOnClickListener(this);
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        ll_lin1 = (LinearLayout) findViewById(R.id.ll_lin1);
        ll_lin2 = (LinearLayout) findViewById(R.id.ll_lin2);
        ll_lin3 = (LinearLayout) findViewById(R.id.ll_lin3);
        ll_lin4 = (LinearLayout) findViewById(R.id.ll_lin4);

        tv_tab1 = (TextView) findViewById(R.id.tv_tab1);
        tv_tab2 = (TextView) findViewById(R.id.tv_tab2);
        tv_tab3 = (TextView) findViewById(R.id.tv_tab3);
        tv_tab4 = (TextView) findViewById(R.id.tv_tab4);


        iv_tab1 = (ImageView) findViewById(R.id.iv_tab1);
        iv_tab2 = (ImageView) findViewById(R.id.iv_tab2);
        iv_tab4 = (ImageView) findViewById(R.id.iv_tab4);

        rl_tablayout= (RelativeLayout) findViewById(R.id.rl_tablayout);
        tfl_company = (TagFlowLayout) findViewById(R.id.tfl_company);
        lv_category_list= (ListView) findViewById(R.id.lv_category_list);
        lv_people_list= (ListView) findViewById(R.id.lv_people_list);

        lv_client= (ListViewScrollView) findViewById(R.id.lv_1);
        lv_under= (ListViewScrollView) findViewById(R.id.lv_2);
        lv_expert= (ListViewScrollView) findViewById(R.id.lv_3);
        rl_search_people= (RelativeLayout) findViewById(R.id.rl_search_people);
        scroll_search_people= (ScrollView) findViewById(R.id.scroll_search_people);
        search_kinds_layout= (LinearLayout) findViewById(R.id.search_kinds_layout);

        ll_lin1.setOnClickListener(this);
        ll_lin2.setOnClickListener(this);
        ll_lin3.setOnClickListener(this);
        ll_lin4.setOnClickListener(this);

        lv_client.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyClientBean bean = searchClientList.get(i);
                Intent intent = new Intent();
                intent.putExtra("customer_id", bean.getClientId());
                intent.setClass(MainSearchActivity.this, CustomerInfoActivity.class);
                startActivity(intent);
            }
        });
        lv_under.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyClientBean bean=searchUnderList.get(i);
                String state = bean.getUnderState();
                if ("00".equals(state)) {
                    String id = bean.getUnderId();
                    Log.i("id-----", id);
                    String url = IpConfig.getIp() + "user_id=" + id+"&token="+token + "&mod=getHomepageForExpert";
                    Intent intent = new Intent();
                    intent.putExtra("title", "我的微站");
                    intent.putExtra("url", url);
                    intent.putExtra("needShare", false);
                    intent.setClass(MainSearchActivity.this, MicroShareWebActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainSearchActivity.this,"该用户还未认证", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lv_expert.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ExpertBean bean = searchExpertList.get(i);
                Intent intent = new Intent();
                intent.putExtra("id", bean.getId());
                intent.putExtra("avatar", bean.getAvatar());
                intent.putExtra("user_name", bean.getUser_name());
                intent.putExtra("company_name", bean.getCompany_name());
                intent.putExtra("mobile_area", bean.getMobile_area());
                intent.putExtra("good_count", bean.getGood_count());
                intent.putExtra("cert_a", bean.getCert_a());
                intent.putExtra("cert_b", bean.getCert_b());
                intent.putExtra("licence", bean.getLicence());
                intent.putExtra("mobile", bean.getMobile());
                intent.putExtra("cert_num_isShowFlg", bean.getCert_num_isShowFlg());
                intent.putExtra("mobile_num_short", bean.getMobile_num_short());
                intent.putExtra("state", bean.getState());
                intent.putExtra("personal_specialty", bean.getPersonal_specialty());
                intent.putExtra("personal_context", bean.getPersonal_context());
                intent.setClass(MainSearchActivity.this, ExpertMicroActivity.class);
                startActivity(intent);
            }
        });

        tfl_company.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if(company.equals(companynamelist.get(position))){
                    company="";
                    companynamepos=-1;
                }else{
                    company=companynamelist.get(position);
                    companynamepos=position;
                }

                return false;
            }
        });

    }


    private void initEvent() {


        watchSearch();
        getProComapnyData(IpConfig.getUri2("listProCompany"));
        getCategoryData(IpConfig.getUri2("listProCategory"));
        getSuitablePeople(IpConfig.getUri2("forPeople"));

        lv_category_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                categorypos = position;
                categoryAdapter.notifyDataSetChanged();
                type = category_list.get(position).get("code");
            }
        });








        lv_people_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PeopleTypepos = position;
                peopleTypeAdapter.notifyDataSetChanged();
                tags = peopleTypeList.get(position).get("code");
            }
        });

    }




    /**
     * @方法说明:监控软键盘的的搜索按钮
     * @方法名称:watchSearch
     * @返回值:void
     */
    public void watchSearch() {
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) search_edit.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(MainSearchActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);


                    // 搜索，进行自己要的操作...
                    String search = search_edit.getText().toString();
                    if (search.equals("") && company.equals("") && type.equals("") && tags.equals("")) {
                        Toast.makeText(MainSearchActivity.this, "没有符合条件的产品或人", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (searchProductFlag) {
                        //搜索产品
                        Intent intent = new Intent();
                        intent.setClass(MainSearchActivity.this, MainProductListActivity.class);
                        intent.putExtra("tags", tags);
                        intent.putExtra("title", "搜索结果");
                        intent.putExtra("search", search);
                        intent.putExtra("company", company);
                        intent.putExtra("type", type);
                        MainSearchActivity.this.startActivity(intent);
                        Log.i("searchcompanytypetags", search + "---" + company + "---" + type + "---" + tags);
                    } else {
                        //搜索人
                        isMyClientHasData=true;
                        isMyUnderHasData=true;
                        isExpertHasData=true;
                        searchClientList = new ArrayList<MyClientBean>();
                        searchUnderList = new ArrayList<MyClientBean>();
                        searchExpertList = new ArrayList<ExpertBean>();
                        searchCustomer = new SearchCustomer(MainSearchActivity.this);
                        searchUserOfUnder = new SearchUserOfUnder(MainSearchActivity.this);
                        searchExpert = new SearchExpert(MainSearchActivity.this);
                        searchCustomer.execute(user_id, search, SEARCH_MY_CLIENT, searchClientList,token);
                        searchUserOfUnder.execute(user_id, search, SEARCH_MY_UNDER, searchUnderList,token);
                        searchExpert.execute(search, "01", "0", SEARCH_EXPERT, searchExpertList);
                    }


                    return true;
                }
                return false;
            }
        });
    }

    private void getProComapnyData(String url) {
        companynamelist = new ArrayList<String>();
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


                                        companynamelist.add(dataArray.getString(i));


                                    }
                                    Message msg = new Message();
                                    msg.what = -1;
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

    /**
     * 获取Category列表
     *
     * @param url
     */
    private void getCategoryData(String url) {


        category_list = new ArrayList<HashMap<String, String>>();

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
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("name", dataArray.getJSONObject(i).getString("category"));
                                        map.put("code", dataArray.getJSONObject(i).getString("code"));

                                        category_list.add(map);

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

    private void getSuitablePeople(String forPeople) {
        peopleTypeList=new ArrayList<HashMap<String,String>>();
        OkHttpUtils.get()
                .url(forPeople)
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
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("name", dataArray.getJSONObject(i).getString("category"));
                                        map.put("code", dataArray.getJSONObject(i).getString("code"));
                                        map.put("extra", dataArray.getJSONObject(i).getString("extra"));
                                        peopleTypeList.add(map);
                                    }
                                    Message msg = new Message();
                                    msg.what = 3;
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

    private void setTabSelection(int index) {

        switch (index) {
            case -1:

                ll_lin1.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin2.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin3.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin4.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));


                tv_tab1.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab2.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab3.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab4.setTextColor(this.getResources().getColor(R.color.color3));

                iv_tab1.setImageResource(R.drawable.icon_down);
                iv_tab2.setImageResource(R.drawable.icon_down);
                iv_tab4.setImageResource(R.drawable.icon_down);



                break;

            case 0:

           //     ll_lin1.setBackgroundResource(R.drawable.main_search_button_check);
                ll_lin2.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin3.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin4.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));


           //     tv_tab1.setTextColor(this.getResources().getColor(R.color.title_blue));
                tv_tab2.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab3.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab4.setTextColor(this.getResources().getColor(R.color.color3));


            //    iv_tab1.setImageResource(R.drawable.icon_up);
                iv_tab2.setImageResource(R.drawable.icon_down);
                iv_tab4.setImageResource(R.drawable.icon_down);

               if(null!=tagAdapter){
                   tagAdapter.notifyDataChanged();
               }
                break;
            case 1:
                ll_lin1.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
          //      ll_lin2.setBackgroundResource(R.drawable.main_search_button_check);
                ll_lin3.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin4.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));


                tv_tab1.setTextColor(this.getResources().getColor(R.color.color3));
            //    tv_tab2.setTextColor(this.getResources().getColor(R.color.title_blue));
                tv_tab3.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab4.setTextColor(this.getResources().getColor(R.color.color3));


                iv_tab1.setImageResource(R.drawable.icon_down);
            //    iv_tab2.setImageResource(R.drawable.icon_up);
                iv_tab4.setImageResource(R.drawable.icon_down);
                if(null!=categoryAdapter){
                    categoryAdapter.notifyDataSetChanged();
                }
                break;

            case 2:

                ll_lin1.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin2.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin3.setBackgroundResource(R.drawable.main_search_button_check);
                ll_lin4.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));


                tv_tab1.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab2.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab3.setTextColor(this.getResources().getColor(R.color.title_blue));
                tv_tab4.setTextColor(this.getResources().getColor(R.color.color3));


                iv_tab1.setImageResource(R.drawable.icon_down);
                iv_tab2.setImageResource(R.drawable.icon_down);
                iv_tab4.setImageResource(R.drawable.icon_down);
                break;
            case 3:
                if(null!=peopleTypeAdapter){
                    peopleTypeAdapter.notifyDataSetChanged();
                }
                ll_lin1.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin2.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                ll_lin3.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
           //     ll_lin4.setBackgroundResource(R.drawable.main_search_button_check);

                tv_tab1.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab2.setTextColor(this.getResources().getColor(R.color.color3));
                tv_tab3.setTextColor(this.getResources().getColor(R.color.color3));
          //      tv_tab4.setTextColor(this.getResources().getColor(R.color.title_blue));


                iv_tab1.setImageResource(R.drawable.icon_down);
                iv_tab2.setImageResource(R.drawable.icon_down);
         ///       iv_tab4.setImageResource(R.drawable.icon_up);

                break;


        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_lin1:

                setTabSelection(0);
                if(tfl_company.isShown()) {

                    tfl_company.setVisibility(View.GONE);

                    tv_tab1.setTextColor(this.getResources().getColor(R.color.color3));
                    ll_lin1.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                    iv_tab1.setImageResource(R.drawable.icon_down);

                    rl_tablayout.setVisibility(View.GONE);
                }else{
                    tfl_company.setVisibility(View.VISIBLE);
                    tv_tab1.setTextColor(this.getResources().getColor(R.color.title_blue));
                    ll_lin1.setBackgroundResource(R.drawable.main_search_button_check);
                    iv_tab1.setImageResource(R.drawable.icon_up);
                    rl_tablayout.setVisibility(View.VISIBLE);
                }
                lv_category_list.setVisibility(View.GONE);
                lv_people_list.setVisibility(View.GONE);

                type="";
                tags="";
                 categorypos =-1;
                 PeopleTypepos =-1;
                searchProductFlag =true;

                MobclickAgent.onEvent(this, Event_Company);
                break;


            case R.id.ll_lin2:

                setTabSelection(1);

                tfl_company.setVisibility(View.GONE);

                if(lv_category_list.isShown()) {

                    lv_category_list.setVisibility(View.GONE);
                    tv_tab2.setTextColor(this.getResources().getColor(R.color.color3));
                    ll_lin2.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                    iv_tab2.setImageResource(R.drawable.icon_down);

                    rl_tablayout.setVisibility(View.GONE);
                }else{
                    lv_category_list.setVisibility(View.VISIBLE);
                    tv_tab2.setTextColor(this.getResources().getColor(R.color.title_blue));
                    ll_lin2.setBackgroundResource(R.drawable.main_search_button_check);
                    iv_tab2.setImageResource(R.drawable.icon_up);

                    rl_tablayout.setVisibility(View.VISIBLE);
                }


                lv_people_list.setVisibility(View.GONE);

                company="";
                tags="";
                companynamepos =-1;
                //categorypos =-1;
                 PeopleTypepos =-1;
                searchProductFlag =true;

                MobclickAgent.onEvent(this, Event_Category);
                break;

            case R.id.ll_lin3:
                setTabSelection(2);

                tfl_company.setVisibility(View.GONE);
                lv_category_list.setVisibility(View.GONE);
                lv_people_list.setVisibility(View.GONE);

                company="";
                type="";
                tags="";
                companynamepos =-1;
                categorypos =-1;
                PeopleTypepos =-1;
                searchProductFlag =false;

                MobclickAgent.onEvent(this, Event_Partner);
                break;

            case R.id.ll_lin4:

                tfl_company.setVisibility(View.GONE);
                lv_category_list.setVisibility(View.GONE);

                if(lv_people_list.isShown()) {

                    lv_people_list.setVisibility(View.GONE);
                    tv_tab4.setTextColor(this.getResources().getColor(R.color.color3));
                    ll_lin4.setBackgroundColor(getResources().getColor(R.color.mian_search_tab_bg));
                    iv_tab4.setImageResource(R.drawable.icon_down);

                    rl_tablayout.setVisibility(View.GONE);
                }else{
                    lv_people_list.setVisibility(View.VISIBLE);
                    tv_tab4.setTextColor(this.getResources().getColor(R.color.title_blue));
                    ll_lin4.setBackgroundResource(R.drawable.main_search_button_check);
                    iv_tab4.setImageResource(R.drawable.icon_up);

                    rl_tablayout.setVisibility(View.VISIBLE);
                }

                setTabSelection(3);

                company="";
                type="";
                companynamepos =-1;
                categorypos =-1;
                searchProductFlag =true;

                MobclickAgent.onEvent(this, Event_ApplicablePeople);
                break;


            case R.id.iv_back:
                if(!searchProductFlag&&rl_search_people.isShown()){
                    rl_search_people.setVisibility(View.GONE);
                    search_kinds_layout.setVisibility(View.VISIBLE);
                }else{
                    finish();
                }

                break;
            case R.id.tv_search:
                String search =search_edit.getText().toString();
                if(search.equals("")&&company.equals("")&&type.equals("")&&tags.equals("")){
                    Toast.makeText(this,"没有符合条件的产品或人", Toast.LENGTH_SHORT).show();
                    return;
                }
               if(searchProductFlag) {
                   Intent intent = new Intent();
                   intent.setClass(MainSearchActivity.this, MainProductListActivity.class);
                   intent.putExtra("tags", tags);
                   intent.putExtra("title", "搜索结果");
                   intent.putExtra("search",search);
                   intent.putExtra("company", company);
                   intent.putExtra("type", type);
                   MainSearchActivity.this.startActivity(intent);
                   Log.i("searchcompanytypetags", search + "---" + company + "---" + type + "---"+tags);
               }else{
                   //搜索人
                   Log.i("llala","---------------");
                   isMyClientHasData=true;
                   isMyUnderHasData=true;
                   isExpertHasData=true;
                   searchClientList=new ArrayList<MyClientBean>();
                   searchUnderList=new ArrayList<MyClientBean>();
                   searchExpertList=new ArrayList<ExpertBean>();
                   searchCustomer=new SearchCustomer(this);
                   searchUserOfUnder=new SearchUserOfUnder(this);
                   searchExpert=new SearchExpert(this);
                   searchCustomer.execute(user_id,search, SEARCH_MY_CLIENT, searchClientList,token);
                   searchUserOfUnder.execute(user_id, search,SEARCH_MY_UNDER, searchUnderList,token);
                   searchExpert.execute(search,"01","0",SEARCH_EXPERT,searchExpertList);
               }

                break;

        }

    }



    class MyAdapter extends BaseAdapter {
        private ArrayList list;
        private int index;

        public MyAdapter(ArrayList list, int index) {
            this.list = list;
            this.index = index;
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder=null;
            if(view==null){
                holder=new ViewHolder();
                view= LayoutInflater.from(MainSearchActivity.this).inflate(R.layout.item_zhanye_client_under_list,null);
                holder.iv_round= (RoundImageView) view.findViewById(R.id.iv_round);
                holder.iv_round.setRectAdius(Common.dip2px(MainSearchActivity.this, 10));
                holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                holder.bottom_line=view.findViewById(R.id.bottom_line);
                view.setTag(holder);
            }else{
                holder= (ViewHolder) view.getTag();
            }


            if(i==list.size()-1){
                holder.bottom_line.setVisibility(View.GONE);
            }else{
                holder.bottom_line.setVisibility(View.VISIBLE);
            }
            if(index==1){
                MyClientBean bean= (MyClientBean) list.get(i);
                holder.tv_name.setText(bean.getName());
                holder.iv_round.setBackgroundResource(R.drawable.people_default_icon);
            }else if(index==2){
                MyClientBean bean= (MyClientBean) list.get(i);
                holder.tv_name.setText(bean.getName());
                Glide.with(MainSearchActivity.this)
                        .load(bean.getUnderAvatar())
                        .placeholder(R.drawable.people_default_icon)
                        .error(R.drawable.people_default_icon)//占位图 图片正在加载
                        .into(holder.iv_round);
            }else if(index==3){
                ExpertBean bean= (ExpertBean) list.get(i);
                holder.tv_name.setText(bean.getUser_name());
                Glide.with(MainSearchActivity.this)
                        .load(bean.getAvatar())
                        .placeholder(R.drawable.people_default_icon)
                        .error(R.drawable.people_default_icon)//占位图 图片正在加载
                        .into(holder.iv_round);
            }
            return view;
        }

        class ViewHolder{
            private RoundImageView iv_round;
            private TextView tv_name;
            private View bottom_line;
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case SEARCH_MY_CLIENT:
                if (flag == MyApplication.DATA_OK) {
                    MyAdapter adapter1=new MyAdapter(searchClientList,1);
                    lv_client.setAdapter(adapter1);
                    rl_tablayout.setVisibility(View.VISIBLE);
                    rl_search_people.setVisibility(View.VISIBLE);
                    search_kinds_layout.setVisibility(View.GONE);
                    if(searchClientList.size()>0){
                        isMyClientHasData=true;
                    }else {
                        isMyClientHasData=false;
                    }


                } else if (flag == MyApplication.NET_ERROR) {
                    isMyClientHasData=false;
                } else if (flag == MyApplication.DATA_EMPTY) {
                    isMyClientHasData=false;
                } else if (flag == MyApplication.JSON_ERROR) {
                    isMyClientHasData=false;
                } else if (flag == MyApplication.DATA_ERROR) {
                    isMyClientHasData=false;
                }
                if(isMyClientHasData==false&&isMyUnderHasData==false&&isExpertHasData==false){
                    Toast.makeText(MainSearchActivity.this, "没有符合条件的产品或人", Toast.LENGTH_SHORT).show();
                }
                break;
            case SEARCH_MY_UNDER:
                if (flag == MyApplication.DATA_OK) {
                    MyAdapter adapter2=new MyAdapter(searchUnderList,2);
                    lv_under.setAdapter(adapter2);
                    rl_tablayout.setVisibility(View.VISIBLE);
                    rl_search_people.setVisibility(View.VISIBLE);
                    search_kinds_layout.setVisibility(View.GONE);
                    if(searchUnderList.size()>0){
                        isMyUnderHasData=true;
                    }else{
                        isMyUnderHasData=false;
                    }
                } else if (flag == MyApplication.NET_ERROR) {
                    isMyUnderHasData=false;
                } else if (flag == MyApplication.DATA_EMPTY) {
                    isMyUnderHasData=false;
                } else if (flag == MyApplication.JSON_ERROR) {
                    isMyUnderHasData=false;
                } else if (flag == MyApplication.DATA_ERROR) {
                    isMyUnderHasData=false;
                }
                if(isMyClientHasData==false&&isMyUnderHasData==false&&isExpertHasData==false){
                    Toast.makeText(MainSearchActivity.this, "没有符合条件的产品或人", Toast.LENGTH_SHORT).show();
                }
                break;
            case SEARCH_EXPERT:
                if (flag == MyApplication.DATA_OK) {
                    MyAdapter adapter3=new MyAdapter(searchExpertList,3);
                    lv_expert.setAdapter(adapter3);
                    rl_tablayout.setVisibility(View.VISIBLE);
                    rl_search_people.setVisibility(View.VISIBLE);
                    search_kinds_layout.setVisibility(View.GONE);
                    if(searchExpertList.size()>0){
                        isExpertHasData=true;
                    }else{
                        isExpertHasData=false;
                    }
                } else if (flag == MyApplication.NET_ERROR) {
                    isExpertHasData=false;
                } else if (flag == MyApplication.DATA_EMPTY) {
                    isExpertHasData=false;
                } else if (flag == MyApplication.JSON_ERROR) {
                    isExpertHasData=false;
                } else if (flag == MyApplication.DATA_ERROR) {
                    isExpertHasData=false;
                }
                if(isMyClientHasData==false&&isMyUnderHasData==false&&isExpertHasData==false){
                    Toast.makeText(MainSearchActivity.this, "没有符合条件的产品或人", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
