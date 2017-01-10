package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.adapter.C_P_I_P_Adapter;

import java.util.ArrayList;
import java.util.HashMap;
public class C_P_I_P_Activity extends BaseActivity {
    ArrayList<String> product_nameList = new ArrayList<String>();
    ArrayList<ArrayList<HashMap<String, String>>> product_priceList = new ArrayList<ArrayList<HashMap<String, String>>>();
    private ExpandableListView listView;
    private C_P_I_P_Adapter adapter;
    private RelativeLayout c_p_i_product_back;
    private RelativeLayout rl_right;
    private TextView tv_text;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.c_p_i_product);

        Intent intent = getIntent();

        product_nameList = intent.getStringArrayListExtra("product_nameList");
        product_priceList = (ArrayList<ArrayList<HashMap<String, String>>>) intent.getSerializableExtra("product_priceList");


        init();
        initEvent();


    }

    void init() {
        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        c_p_i_product_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("保险产品信息");
    }

    void initEvent() {


        adapter = new C_P_I_P_Adapter(this, product_nameList, product_priceList);

        listView.setAdapter(adapter);

        int groupCount = listView.getCount();

        for (int i = 0; i < groupCount; i++) {

            listView.expandGroup(i);

        }


        listView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView arg0, View arg1,
                                        int arg2, long arg3) {


                return true;
            }
        });

        c_p_i_product_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();

            }
        });
    }


}
