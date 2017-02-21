package com.cloudhome.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.cloudhome.R;
import com.cloudhome.activity.CarInsuranceInsuredActivity;
import com.cloudhome.activity.CommissionMainActivity;
import com.cloudhome.activity.CommonWebActivity;
import com.cloudhome.activity.HomeWebShareActivity;
import com.cloudhome.activity.LoginActivity;
import com.cloudhome.activity.MainProductListActivity;
import com.cloudhome.activity.MainSearchActivity;
import com.cloudhome.activity.MyOrderListActivity;
import com.cloudhome.activity.MyWalletActivity;
import com.cloudhome.adapter.MainPopAdapter;
import com.cloudhome.adapter.MainProListAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.MainPopBean;
import com.cloudhome.bean.MainProBean;
import com.cloudhome.event.CommissionRefreshEvent;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.ActivityEntrance;
import com.cloudhome.network.GetBanner;
import com.cloudhome.network.ProductClickStatistic;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.QianDaoDialog;
import com.cloudhome.view.xlistview.XListView;
import com.umeng.analytics.MobclickAgent;
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

public class MainFragment extends BaseFragment implements NetResultListener, View.OnClickListener, XListView.IXListViewListener {

    private String EVENT_MAIN1 = "MainFragment_MakeMoney";
    private String EVENT_MAIN2 = "MainFragment_SellWell";
    private String EVENT_MAIN3 = "MainFragment_GoldenGroup";
    private View mView;
    SharedPreferences sp;
    SharedPreferences sp2;
    SharedPreferences sp5;
    public static String FRAGMENT_TAG = MainFragment.class.getSimpleName();


    private ArrayList<HashMap<String, Object>> pop_list;

    private PopupWindow pop_type;

    private ViewGroup topView;

    private RelativeLayout pop_type_layout;
    private ListView pop_type_list;

    private View p_view;
    private RelativeLayout rl_type;
    private TextView tv_type;
    private RelativeLayout rl_main1, rl_main2, rl_main3, rl_car_insurance;
    private MainPopAdapter mainPopAdapter;
    private String loginString;
    private XListView xlv_mainlist;
    private String user_id;
    private String suid;
    private String token;
    private String type;
    private String state;
    private int pagenum = 1;
    private Map<String, String> key_value = new HashMap<String, String>();

    //统计产品被点击的次数
    private ProductClickStatistic productClickStatistic = new ProductClickStatistic(this);
    private GetBanner getBanner;
    private static final int GET_BANNER = 1;
    private ArrayList<MainProBean> bannerList;
    private boolean isDataMixed = false;
    private boolean isAllProductLoaded = false;
    private boolean isAdvertiseMentLoaded = false;
    String categoryCode = "";

    // 统计接口
    private Statistics statistics;
    private Handler mHandler;

    private boolean isCommissionShown;
    private String Event_Order = "MainFragment_PopOrder";
    private String Event_Wallet = "MainFragment_PopWallet";
    private String Event_Commission = "MainFragment_PopCommission";
    private String Event_Ad = "MainFragment_Ad";
    private String Event_Product = "MainFragment_Product";
    private String Event_Search = "MainFragment_Search";
    private String Event_Popup = "MainFragment_popup";
    private HashMap<String, String> MainFragment_Ad_map = new HashMap<String, String>();
    //首页弹窗
    private ActivityEntrance activityEntrance;
    private ArrayList<MainPopBean> mainPopList = new ArrayList<MainPopBean>();
    public static final int MAIN_POP = 2;

    private void showtypePopupWindow() {


        pop_type_layout = ((RelativeLayout) LayoutInflater.from(getActivity()).inflate(
                R.layout.main_insurance_type_pop, null));
        pop_type_list = (ListView) pop_type_layout
                .findViewById(R.id.lv_pop_list);
        mainPopAdapter.setData(pop_list);
        pop_type_list.setAdapter(mainPopAdapter);
        pop_type = new PopupWindow(pop_type_layout, Common.dip2px(getActivity(), 200), Common.dip2px(getActivity(), 393), true);
        pop_type.showAsDropDown(tv_type);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        pop_type_layout.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                closePopupWindow();
                return true;
            }
        });

        pop_type_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

                arg0.setVisibility(View.VISIBLE);

                closePopupWindow();


                pagenum = 1;
                dataMap.clear();
                madapter.notifyDataSetChanged();
                key_value.put("page", pagenum + "");
                categoryCode = pop_list.get(pos).get("code").toString();
                key_value.put("type", categoryCode);
                getproListData(IpConfig.getUri2("productlist"));
                if ("".equals(categoryCode)) {
                    getAdvertisementData();
                    isAllProductLoaded = false;
                    isDataMixed = false;
                    isAdvertiseMentLoaded = false;
                    rl_car_insurance.setVisibility(View.VISIBLE);
                } else {
                    rl_car_insurance.setVisibility(View.GONE);
                }
            }
        });
    }

    private void closePopupWindow() {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 1.0f;
        getActivity().getWindow().setAttributes(lp);
        if (pop_type != null && pop_type.isShowing()) {
            pop_type.dismiss();
            pop_type = null;

        }
    }


    private RelativeLayout pop_add_rel;

    private PopupWindow pop_add;

    private void showaddPopWindow() {

        statistics = new Statistics(MainFragment.this);
        pop_add_rel = ((RelativeLayout) LayoutInflater.from(getActivity()).inflate(
                R.layout.main_add_pop, null));

        RelativeLayout rl_add1 = (RelativeLayout) pop_add_rel.findViewById(R.id.rl_add1);
        RelativeLayout rl_add2 = (RelativeLayout) pop_add_rel.findViewById(R.id.rl_add2);
        RelativeLayout rl_add3 = (RelativeLayout) pop_add_rel.findViewById(R.id.rl_add3);

        final ImageView iv_cancel = (ImageView) pop_add_rel.findViewById(R.id.iv_cancel);
        ImageView iv_pop_search = (ImageView) pop_add_rel.findViewById(R.id.iv_pop_search);


        pop_add = new PopupWindow(getActivity());
        pop_add.setBackgroundDrawable(new ColorDrawable(0x00000000));
        pop_add.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop_add.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        pop_add.setTouchable(true);
        pop_add.setOutsideTouchable(false);
        pop_add.setContentView(pop_add_rel);
        pop_add.showAsDropDown(p_view);

        rl_add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statistics.execute("mine_order");

                if (loginString.equals("none")) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MyOrderListActivity.class);
                    getActivity().startActivity(intent);

                }

                MobclickAgent.onEvent(getActivity(), Event_Order);

            }
        });
        rl_add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statistics.execute("mine_package");

                if (loginString.equals("none")) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);

                } else {
                    // 我的钱包
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MyWalletActivity.class);
                    getActivity().startActivity(intent);

                }

                MobclickAgent.onEvent(getActivity(), Event_Wallet);

            }
        });
        rl_add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statistics.execute("index_func_cc");
                if (loginString.equals("none")) {

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);

                } else {

                    String ccyj_reference_flag = sp2.getString("ccyj_reference_flag", "");
                    if ("0".equals(ccyj_reference_flag)) {
                        Toast.makeText(getActivity(), "暂无数据，请稍后访问",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), CommissionMainActivity.class);
                    getActivity().startActivity(intent);
                }

                MobclickAgent.onEvent(getActivity(), Event_Commission);

            }
        });

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iv_add.setVisibility(View.VISIBLE);

                pop_add.dismiss();
                pop_add = null;
            }
        });

        iv_pop_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(getActivity(), MainSearchActivity.class);
                getActivity().startActivity(intent);
                MobclickAgent.onEvent(getActivity(), Event_Search);
            }
        });

    }

    private RelativeLayout iv_add;
    private RelativeLayout iv_search;
    private MainProListAdapter madapter;
    private ArrayList<MainProBean> dataMap = new ArrayList<MainProBean>();

    private String key_commission;
    private boolean isCategory = true;//产品分类获取是否成功


    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    if (pagenum == 1) {
                        if (dataMap.size() < 1) {
                            dataMap.addAll(dataing);
                            //                            madapter.setData(dataMap);
                            //                            xlv_mainlist.setAdapter(madapter);
                            //                            madapter.notifyDataSetChanged();
                        }
                        //与广告数据进行混合
                        if ("".equals(categoryCode)) {
                            isAllProductLoaded = true;
                            MixData();
                        } else {
                            madapter.setData(dataMap);
                            madapter.isCommissionShown(isCommissionShown);
                            xlv_mainlist.setAdapter(madapter);
                            madapter.notifyDataSetChanged();
                        }

                        xlv_mainlist.stopLoadMore();

                    } else if (dataing.size() < 1) {
                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
                        xlv_mainlist.stopLoadMore();
                    } else {
                        dataMap.addAll(dataing);
                        madapter.notifyDataSetChanged();
                        xlv_mainlist.stopLoadMore();
                    }


                    break;

                case 0:
                    rl_type.setOnClickListener(MainFragment.this);


                    break;
                case 1:
                    //获取产品分类失败
                    isCategory = false;
                    Toast.makeText(getActivity(), "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
                    break;
                case 2:

                    Toast.makeText(getActivity(), msg.obj + "",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    //获取产品列表失败
                    Toast.makeText(getActivity(), "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
                    xlv_mainlist.stopLoadMore();
                    madapter.setData(dataMap);
                    xlv_mainlist.setAdapter(madapter);
                    break;
                case 4:
                    //获取产品分类成功

                    if (!isCategory) {
                        showtypePopupWindow();
                    }
                    isCategory = true;
                    break;

                default:
                    break;
            }
        }

    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            sp = getActivity().getSharedPreferences("userInfo", 0);
            sp2 = getActivity().getSharedPreferences("otherinfo", 0);
            sp5 = getActivity().getSharedPreferences("temp", 0);
            initView(inflater, container);
            initEvent();
            EventBus.getDefault().register(this);
        }
        return mView;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CommissionRefreshEvent event) {
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        suid = sp.getString("Encrypt_UID", "");
        token = sp.getString("Login_TOKEN", "");
        type = sp.getString("Login_TYPE", "");
        state = sp.getString("Login_CERT", "");
        isCommissionShown = sp5.getBoolean(user_id, false);
        madapter.isCommissionShown(isCommissionShown);
        madapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        suid = sp.getString("Encrypt_UID", "");
        token = sp.getString("Login_TOKEN", "");
        type = sp.getString("Login_TYPE", "");
        state = sp.getString("Login_CERT", "");
        isCommissionShown = sp5.getBoolean(user_id, false);
        madapter.isCommissionShown(isCommissionShown);
        madapter.notifyDataSetChanged();
    }

    private void initEvent() {
        mHandler = new Handler();
        getOrderTypeData(IpConfig.getUri2("listProCategory"));
        getproListData(IpConfig.getUri2("productlist"));
        getAdvertisementData();
        activityEntrance = new ActivityEntrance(this, getActivity());
        activityEntrance.execute(user_id, "popup", token, suid, MAIN_POP, mainPopList);

        xlv_mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int pos = position - 2;

                if (pos >= 0 && pos < dataMap.size()) {


                    MainProBean proBean = dataMap.get(pos);
                    //是广告还是产品
                    boolean isAdvertise = proBean.isAdvertisement();
                    if (isAdvertise) {
                        String title = proBean.getTitle();
                        String logo = proBean.getLogo();
                        String brief = proBean.getBrief();
                        String is_share = proBean.getIs_share();
                        String loginFlag = proBean.getLoginFlag();
                        String abc = proBean.getPage();

                        bannerStastics(proBean.getRecordId());

                        if (loginFlag.equals("1") && loginString.equals("none")) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("title", title);
                            intent.putExtra("img", IpConfig.getIp3() + logo);
                            intent.putExtra("brief", brief);
                            intent.putExtra("is_share", is_share);
                            intent.putExtra("url", abc);
                            intent.putExtra("share_title", title);
                            // 从Activity IntentTest跳转到Activity IntentTest01
                            intent.setClass(getActivity(),
                                    HomeWebShareActivity.class);
                            getActivity().startActivity(intent);
                        }
                        MainFragment_Ad_map.put("title", title);
                        MobclickAgent.onEvent(getActivity(), Event_Ad, MainFragment_Ad_map);
                    } else {
                        if (loginString.equals("none")) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                        } else {
                            //本地浏览量加1
                            int clickNum = Integer.parseInt(dataMap.get(pos).getPopularityreal()) + 1;
                            dataMap.get(pos).setPopularityreal(clickNum + "");

                            String url = proBean.getUrl();
                            //境外旅行
                            if ("".equals(url)) {
                                //                                String product_id = proBean.getCode();
                                //                                Intent intent = new Intent(getActivity(), RecProductDetailActivity.class);
                                //                                intent.putExtra("product_id", product_id);
                                //                                startActivity(intent);
                                url = IpConfig.getIp2() + "/product/toProductInfoPage";
                            }
                            if (url.contains("?")) {
                                url = url + "&code=" + proBean.getCode();
                            } else {
                                url = url + "?code=" + proBean.getCode();
                            }
                            //                            url = "http://10.10.10.155:8080/web/html/product/templates/template.html#/product?code=9210101003&user_id=10";
                            Intent intent = new Intent();
                            intent.putExtra("title", proBean.getName());
                            intent.putExtra("web_address", url);
                            intent.putExtra("shareDesc", proBean.getFeaturedesc());
                            intent.putExtra("shareTitle", proBean.getName());
                            intent.putExtra("shareLogo", IpConfig.getIp3() + "/" + proBean.getImgurl());
                            intent.putExtra("shareUrl", url);
                            intent.putExtra("needShare", proBean.isShare());
                            intent.setClass(getActivity(), CommonWebActivity.class);
                            getActivity().startActivity(intent);
                            //统计产品被点击
                            productClickStatistic.execute(proBean.getId());
                            MobclickAgent.onEvent(getActivity(), Event_Product);
                        }
                    }


                }
            }
        });


    }

    private void showAdPop() {
        View contentView = View.inflate(getActivity(), R.layout.dialog_pop_main, null);
        ImageView iv_pop = (ImageView) contentView.findViewById(R.id.iv_pop);
        final RelativeLayout rl_close = (RelativeLayout) contentView.findViewById(R.id.rl_close);
        //        String im="http://img.hb.aicdn.com/167e84a5aab000ea4cdd7181f3dd6ca1d3d6ad163248e-jxKRAm_fw658";
        //        String im="http://h.hiphotos.baidu.com/zhidao/pic/item/ac6eddc451da81cb0e2614565266d0160824316c.jpg";
        final MainPopBean bean = mainPopList.get(0);
        String im = bean.getImgUrl();

        Glide.with(getActivity())
                .load(im)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .crossFade()
                .into(new GlideDrawableImageViewTarget(iv_pop) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        rl_close.setVisibility(View.VISIBLE);
                    }
                });

        final QianDaoDialog builder = new QianDaoDialog(getActivity(), contentView, Common.dip2px(getActivity(), 250),
                Common.dip2px(getActivity(), 320), R.style.qiandao_dialog);
        builder.setCanceledOnTouchOutside(false);
        builder.show();
        rl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        iv_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("title", bean.getTitle());
                intent.putExtra("img", bean.getShareImgUrl());
                intent.putExtra("brief", bean.getShareDesc());
                intent.putExtra("is_share", bean.getNeedShare());
                intent.putExtra("url", bean.getUrl());
                intent.putExtra("share_title", bean.getShareTitle());
                intent.putExtra("isHasShareUrl", true);
                intent.putExtra("shareUrl", bean.getShareUrl());
                // 从Activity IntentTest跳转到Activity IntentTest01
                intent.setClass(getActivity(), HomeWebShareActivity.class);
                getActivity().startActivity(intent);
                builder.dismiss();
                MobclickAgent.onEvent(getActivity(), Event_Popup);
            }
        });
    }

    private void bannerStastics(String recordId) {
        String bannerStasticsUrl = IpConfig.getUri("bannerStastics");
        HashMap<String, String> parames = new HashMap<>();
        parames.put("record_id", recordId);
        parames.put("user_id", user_id);

        OkHttpUtils.get()
                .url(bannerStasticsUrl)
                .params(parames)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                    }
                });

    }


    private void getAdvertisementData() {
        bannerList = new ArrayList<MainProBean>();
        getBanner = new GetBanner(this);
        getBanner.execute(GET_BANNER, bannerList);
    }


    private void initView(LayoutInflater inflater, ViewGroup container) {
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        suid = sp.getString("Encrypt_UID", "");
        token = sp.getString("Login_TOKEN", "");
        type = sp.getString("Login_TYPE", "");
        state = sp.getString("Login_CERT", "");
        isCommissionShown = sp5.getBoolean(user_id, false);
        topView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.mian_top_layout, null);
        rl_type = (RelativeLayout) topView.findViewById(R.id.rl_type);
        tv_type = (TextView) topView.findViewById(R.id.tv_type);
        rl_main1 = (RelativeLayout) topView.findViewById(R.id.rl_main1);
        rl_main2 = (RelativeLayout) topView.findViewById(R.id.rl_main2);
        rl_main3 = (RelativeLayout) topView.findViewById(R.id.rl_main3);
        rl_car_insurance = (RelativeLayout) topView.findViewById(R.id.rl_car_insurance);
        rl_main1.setOnClickListener(this);
        rl_main2.setOnClickListener(this);
        rl_main3.setOnClickListener(this);
        rl_car_insurance.setOnClickListener(this);
        rl_type.setOnClickListener(MainFragment.this);

        mView = inflater.inflate(R.layout.activity_main, container, false);
        p_view = mView.findViewById(R.id.p_view);
        iv_add = (RelativeLayout) mView.findViewById(R.id.iv_add);
        iv_search = (RelativeLayout) mView.findViewById(R.id.iv_search);
        iv_add.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        xlv_mainlist = (XListView) mView.findViewById(R.id.xlv_mainlist);
        xlv_mainlist.addHeaderView(topView, null, false);
        xlv_mainlist.setPullLoadEnable(true);
        xlv_mainlist.setXListViewListener(this);

        mainPopAdapter = new MainPopAdapter(getActivity());
        madapter = new MainProListAdapter(getActivity());
    }


    @Override
    public void onStart() {
        super.onStart();

        if (madapter != null) {
            madapter.notifyDataSetChanged();
        }
    }


    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_BANNER:
                isAdvertiseMentLoaded = true;
                MixData();
                if (flag == MyApplication.DATA_OK) {
                    Log.i("guanggao", "DATA_OK");
                } else if (flag == MyApplication.NET_ERROR) {
                    Log.i("guanggao", "NET_ERROR");
                } else if (flag == MyApplication.DATA_EMPTY) {
                    Log.i("guanggao", "DATA_EMPTY");
                } else if (flag == MyApplication.JSON_ERROR) {
                    Log.i("guanggao", "JSON_ERROR");
                } else if (flag == MyApplication.DATA_ERROR) {
                    Log.i("guanggao", "DATA_ERROR");
                }
                break;
            case MAIN_POP:
                if (flag == MyApplication.DATA_OK) {
                    mainPopList.clear();
                    mainPopList = (ArrayList<MainPopBean>) dataObj;
                    Log.i("mainPopList", mainPopList.size() + "");
                    if (mainPopList.size() > 0) {
                        showAdPop();
                    }
                } else if (flag == MyApplication.NET_ERROR) {
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                }
                break;
        }

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_type:
                Log.i("isCategory", isCategory + "");
                if (isCategory) {
                    showtypePopupWindow();
                } else {
                    getOrderTypeData(IpConfig.getUri2("listProCategory"));
                }

                break;


            case R.id.iv_add:
                iv_add.setVisibility(View.INVISIBLE);
                showaddPopWindow();
                break;

            case R.id.iv_search:
                if (loginString.equals("none")) {
                    intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    intent = new Intent();
                    intent.setClass(getActivity(), MainSearchActivity.class);
                    intent.putExtra("fromWhere", 1);
                    getActivity().startActivity(intent);
                }
                break;
            case R.id.rl_main1:
                intent = new Intent();
                intent.setClass(getActivity(), MainProductListActivity.class);
                intent.putExtra("tags", "最赚钱");
                intent.putExtra("title", "最赚钱");
                intent.putExtra("search", "");
                intent.putExtra("company", "");
                intent.putExtra("type", "");
                getActivity().startActivity(intent);

                MobclickAgent.onEvent(getActivity(), EVENT_MAIN1);
                break;
            case R.id.rl_main2:
                intent = new Intent();
                intent.setClass(getActivity(), MainProductListActivity.class);
                intent.putExtra("tags", "最热销");
                intent.putExtra("title", "最热销");
                intent.putExtra("search", "");
                intent.putExtra("company", "");
                intent.putExtra("type", "");
                getActivity().startActivity(intent);

                MobclickAgent.onEvent(getActivity(), EVENT_MAIN2);
                break;
            case R.id.rl_main3:
                intent = new Intent();
                intent.setClass(getActivity(), MainProductListActivity.class);
                intent.putExtra("tags", "黄金组合");
                intent.putExtra("title", "黄金组合");
                intent.putExtra("search", "");
                intent.putExtra("company", "");
                intent.putExtra("type", "");
                startActivity(intent);

                MobclickAgent.onEvent(getActivity(), EVENT_MAIN3);
                break;
            case R.id.rl_car_insurance:
                if (loginString.equals("none")) {
                    intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent();
                    intent.setClass(getActivity(), CarInsuranceInsuredActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private void getRefreshItem() {

        pagenum = 1;
        dataMap.clear();
        madapter.notifyDataSetChanged();
        key_value.put("page", pagenum + "");

        getproListData(IpConfig.getUri2("productlist"));
        getAdvertisementData();
        isAdvertiseMentLoaded = false;
        isDataMixed = false;
        isAllProductLoaded = false;


    }

    private void getLoadMoreItem() {
        pagenum++;

        key_value.put("page", pagenum + "");
        getproListData(IpConfig.getUri2("productlist"));
    }

    @Override
    public void onRefresh() {

        mHandler.postDelayed(new Runnable() {
            public void run() {

                xlv_mainlist.stopLoadMore();
                getRefreshItem();

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


    /**
     * 获取title ordertype列表
     *
     * @param url
     */
    private void getOrderTypeData(String url) {
        pop_list = new ArrayList<HashMap<String, Object>>();
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
                                    HashMap<String, Object> map1 = new HashMap<String, Object>();
                                    map1.put("img", "");
                                    map1.put("name", "全部");
                                    map1.put("code", "");
                                    pop_list.add(map1);
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        HashMap<String, Object> map = new HashMap<String, Object>();
                                        String code = dataArray.getJSONObject(i).getString("code");
                                        map.put("img", dataArray.getJSONObject(i).getString("extra"));
                                        map.put("name", dataArray.getJSONObject(i).getString("category"));
                                        map.put("code", code);

                                        pop_list.add(map);

                                    }
                                    Message msg = new Message();
                                    msg.what = 4;
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

    private ArrayList<MainProBean> dataing;
    private ArrayList<MainProBean> temporaryList;

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
                        msg.what = 3;
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
                                        bean.setIsAdvertisement(false);

                                        bean.setId(obj1.getString("id"));
                                        bean.setName(obj1.getString("name"));
                                        bean.setCode(obj1.getString("code"));
                                        bean.setCompany(obj1.getString("company"));
                                        bean.setFeaturedesc(obj1.getString("featureDesc"));
                                        bean.setRate(obj1.getString("rate"));
                                        bean.setRatepostfix(obj1.getString("ratePostfix"));
                                        bean.setPrice(obj1.getString("price"));
                                        bean.setPricepostfix(obj1.getString("pricePostfix"));
                                        bean.setPopularityreal(obj1.getString("popularityReal"));
                                        if (obj1.getString("isShare").equals("1")) {
                                            bean.setShare(true);
                                        } else {
                                            bean.setShare(false);
                                        }
                                        //境外旅行险传的是null
                                        if (TextUtils.isEmpty(obj1.getString("url")) || "null".equals(obj1.getString("url"))) {
                                            bean.setUrl("");
                                        } else {
                                            bean.setUrl(obj1.getString("url"));
                                        }

                                        bean.setImgurl(obj1.getString("imgUrl"));
                                        bean.setTags(obj1.getString("tags"));
                                        bean.setTrain(obj1.getBoolean("train"));


                                        dataing.add(bean);


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

    private void MixData() {
        Log.i("guanggao", "mix1");
        if (!isDataMixed) {
            Log.i("guanggao", "mix2");
            temporaryList = dataMap;
            if (isAllProductLoaded && isAdvertiseMentLoaded) {
                Log.i("guanggao", "mix3");
                Log.i("guanggao", dataMap.size() + "---" + bannerList.size());
                int k = 0;
                for (int i = 0; i < temporaryList.size(); i++) {
                    if (i % 4 == 3) {
                        if (k < bannerList.size()) {
                            dataMap.add(i, bannerList.get(k));
                            k++;
                        }
                    }
                }
                Log.i("guanggao", dataMap.size() + "---");
                isDataMixed = true;
                madapter.setData(dataMap);
                madapter.isCommissionShown(isCommissionShown);
                xlv_mainlist.setAdapter(madapter);
                madapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
