package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.IncomeExpendAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.IncomeExpendBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetPaymentListForWallet;
import com.cloudhome.view.customview.ListViewScrollView;
import com.cloudhome.view.customview.PublicLoadPage;

import java.util.ArrayList;
public class IncomeExpendDetailActivity extends BaseActivity implements NetResultListener, OnClickListener {

    private final int GET_INCOME_EXPEND_LIST = 0;


    private ListViewScrollView lv_income_expend;
    private Button btn_filter;
    private String user_id;
    private String token;
    private ArrayList<IncomeExpendBean> dataMap;
    private IncomeExpendAdapter adapter;

    private ArrayList<String> codelist =new ArrayList<String>();
    private PublicLoadPage mLoadPage;

    private PopupWindow popupWindow;
    private ListView filterlist;
    private View p_view;
    private RadioGroup radio_select;

    private String title="全部";
    private String type;
    private RadioButton radio_select_income;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_income_expend_detail);

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");


        Intent intent = getIntent();
        type =  intent.getStringExtra("type");
        initView();
        initEvent();

        initData(title, type);
    }


    private void initView() {


        mLoadPage = new PublicLoadPage((LinearLayout) findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(LinearLayout layout,
                                      RelativeLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {
                mLoadPage.startLoad();

                 title="全部";

                initData(title, type);
            }
        };

        lv_income_expend = (ListViewScrollView) findViewById(R.id.lv_income_expend);
        RelativeLayout iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        btn_filter = (Button) findViewById(R.id.btn_filter);

        radio_select = (RadioGroup) findViewById(R.id.radio_select);
        radio_select_income= (RadioButton) findViewById(R.id.radio_select_income);

        if(type.equals("收入"))
        {

            radio_select_income.setChecked(true);
        }
        p_view   = findViewById(R.id.p_view);


        iv_back.setOnClickListener(this);







    }
    private void initEvent() {


        lv_income_expend.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (null != adapter) {
                    IncomeExpendBean bean = (IncomeExpendBean) adapter.getItem(position);
                    Intent intent = new Intent(IncomeExpendDetailActivity.this, IncomeExpendItemDetailActivity.class);

                    intent.putExtra("payment_id", bean.getId());
                    intent.putExtra("title", bean.getTitle());
                    intent.putExtra("money", bean.getMoney());
                    intent.putExtra("category", bean.getCategory());


                    startActivity(intent);
                }

            }
        });


        radio_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @SuppressLint("ShowToast")
            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.radio_select_all:


                        type="全部";

                        initData(title, type);

                        break;
                    case R.id.radio_select_income:

                        type="收入";

                        initData(title, type);

                        break;
                    case R.id.radio_select_expend:

                        type="支出";
                        initData(title, type);

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

    private void initData(String title, String type) {

        mLoadPage.startLoad();
        dataMap = new ArrayList<IncomeExpendBean>();
        GetPaymentListForWallet getPaymentListRequest = new GetPaymentListForWallet(this);
        getPaymentListRequest.execute(user_id, token, GET_INCOME_EXPEND_LIST, dataMap, type, title, codelist);

//		getPaymentListRequest.execute("161",token,GET_INCOME_EXPEND_LIST,dataMap,type);
    }




    /**
     * 弹出筛选的弹框
     */
        private void showPopupWindow(final String[] codelistArray) {


            LinearLayout layout = (LinearLayout) LayoutInflater.from(IncomeExpendDetailActivity.this).inflate(R.layout.income_expend_detail_popwindow, null);


        filterlist = (ListView) layout.findViewById(R.id.lv_income_list);
        filterlist.setAdapter(new ArrayAdapter<String>(IncomeExpendDetailActivity.this,
                R.layout.income_expend_detail_pop_item, R.id.income_expend_txt, codelistArray));


        popupWindow = new PopupWindow(IncomeExpendDetailActivity.this);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));


        popupWindow.setWidth(getResources().getDimensionPixelSize(R.dimen.SceneProducts_p_width));
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);


        popupWindow.setTouchable(true); // 设置PopupWindow可触摸
        popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        popupWindow.setFocusable(false);


        popupWindow.setContentView(layout);
        popupWindow.showAsDropDown(p_view);
            filterlist.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                        long arg3) {


                    arg0.setVisibility(View.VISIBLE);

                    title=codelistArray[pos];
                    Log.d("888888",title);
                   if(title.equals("全部")) {
                       btn_filter.setText("筛选");
                   }else{
                       btn_filter.setText(title);
                   }
                    initData(title, type);
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            });

        }



    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_INCOME_EXPEND_LIST:
                if (flag == MyApplication.DATA_OK) {




                    btn_filter.setOnClickListener(this);
                    if (dataMap.size() <= 0) {
                        mLoadPage.loadFail(MyApplication.NO_DATA, MyApplication.BUTTON_RELOAD, 1);
                        return;
                    }

                    mLoadPage.loadSuccess(null, null);
                    adapter = new IncomeExpendAdapter(IncomeExpendDetailActivity.this, dataMap);
                    lv_income_expend.setAdapter(adapter);

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

            case R.id.btn_filter:
                String[] codelistArray = new String[codelist.size()];

                codelistArray = codelist.toArray(codelistArray);

                showPopupWindow(codelistArray);
                break;



        }

    }


}
