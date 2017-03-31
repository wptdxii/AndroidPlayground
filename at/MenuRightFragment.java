package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.activity.MyInterface.OnProposal_SelectActivityChangeListener;
import com.cloudhome.adapter.Proposal_CompanyAdapter;
import com.cloudhome.adapter.Proposal_CompanyAdapter.Proposal_CompanyHolder;
import com.cloudhome.fragment.BaseFragment;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.MyGridView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
public class MenuRightFragment extends BaseFragment {

    private static OnProposal_SelectActivityChangeListener OnProposal_SelectActivityChangeListener;


    private final String mPageName = "MenuRightFragment";
    private SharedPreferences sp;
    private Proposal_SelectActivity mActivity;
    private View mView;
    private Map<String, String> key_value = new HashMap<String, String>();
    private List<Map<String, String>> companylist;
    private MyGridView company_gd;
    private String mycompany_str = "";
    private SharedPreferences sp2;
    private Proposal_CompanyAdapter p_companyadapter;
    private TextView p_s_back;
    private TextView p_s_submit;
    private int companyNum = 0;
    private String companycode = "";
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            OnMenuRightFragmentInit();


        }

    };
    private String companyname = "全部";
    private Handler errcode_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;

            String status = data;
            Log.d("455454", "455445" + status);
            if (status.equals("false")) {

                Toast.makeText(getActivity(), "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };
    private Handler null_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errmsg = data.get("errmsg");

            Toast.makeText(getActivity(), errmsg, Toast.LENGTH_SHORT).show();

        }

    };
    @SuppressLint("HandlerLeak")
    private Handler select_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {

            Map<String, List<Map<String, String>>> data = (Map<String, List<Map<String, String>>>) msg.obj;

            companylist = data.get("company_list");


            p_companyadapter = new Proposal_CompanyAdapter(companylist,
                    getActivity());
            company_gd.setAdapter(p_companyadapter);

            for (int i = 0; i < companylist.size(); i++) {

                if (mycompany_str.equals(companylist.get(i)
                        .get("company_name"))) {

                    companycode = companylist.get(i).get("company_code");
                    companyname = mycompany_str;
                    Proposal_CompanyAdapter.getIsSelected().put(i,
                            true);
                    companyNum = 1;
                }

            }


            if (companyname.equals("全部")) {


                Proposal_CompanyAdapter.getIsSelected().put(0,
                        true);
                companyNum = 1;
            }
            p_companyadapter.notifyDataSetChanged();

        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {

            sp = getActivity().getSharedPreferences("userInfo", 0);
            sp2 = getActivity().getSharedPreferences("otherinfo", 0);
            initView(inflater, container);

            initEvent();
        }
        return mView;
    }

    private void initView(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.menu_layout_right, container, false);


        company_gd = (MyGridView) mView.findViewById(R.id.company_gd);
        p_s_back = (TextView) mView.findViewById(R.id.p_s_back);
        p_s_submit = (TextView) mView.findViewById(R.id.p_s_submit);
    }

    private void initEvent() {


        key_value.put("s_version", "1");

        final String Conditions_url = IpConfig
                .getUri2("getSuggestProductQueryConditions");

        setselectdata(Conditions_url);

        mycompany_str = sp2.getString("company_name", "");

        company_gd.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

                Proposal_CompanyHolder holder = (Proposal_CompanyHolder) arg1
                        .getTag();
                if (companyNum < 1 || holder.cb.isChecked()) {
                    holder.cb.toggle();

                    Proposal_CompanyAdapter.getIsSelected().put(pos,
                            holder.cb.isChecked());

                    if (holder.cb.isChecked()) {

                        holder.name.setTextColor(getResources().getColor(
                                R.color.orange_red));

                        holder.name
                                .setBackgroundResource(R.drawable.company_checkbox_pressed);
                        companycode = companylist.get(pos)
                                .get("company_code");
                        companyname = companylist.get(pos)
                                .get("company_name");
                        companyNum++;
                    } /*else {
                        holder.name.setTextColor(getResources().getColor(
								R.color.black));
						holder.name
								.setBackgroundResource(R.drawable.company_checkbox_normal);
						companycode = "";
						companyname= "";
						companyNum--;
					}*/

                } else {

                    for (int i = 0; i < Proposal_CompanyAdapter.isSelected.size(); i++) {
                        Proposal_CompanyAdapter.getIsSelected().put(i, false);
                    }

                    holder.cb.setClickable(true);


                    Proposal_CompanyAdapter.getIsSelected().put(pos,
                            true);

                    holder.name.setTextColor(getResources().getColor(
                            R.color.orange_red));

                    holder.name.setBackgroundResource(R.drawable.company_checkbox_pressed);
                    companycode = companylist.get(pos).get("company_code");
                    companyname = companylist.get(pos)
                            .get("company_name");


                    companyNum = 1;

                    p_companyadapter.notifyDataSetChanged();

                }
                //点击之后隐藏侧滑栏
                OnProposal_SelectActivityChangeListener.onActivityChange(
                        companyname, companycode);
            }
        });
        p_s_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                OnProposal_SelectActivityChangeListener.CloseRightMenu();

            }
        });

        p_s_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                OnProposal_SelectActivityChangeListener.onActivityChange(
                        companyname, companycode);
            }
        });

    }

    private void setselectdata(String url) {


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
                        Map<String, String> errcode_map = new HashMap<String, String>();

                        Map<String, List<Map<String, String>>> map_all = new HashMap<String, List<Map<String, String>>>();

                        List<Map<String, String>> company_list = new ArrayList<Map<String, String>>();
                        List<Map<String, String>> period_list = new ArrayList<Map<String, String>>();
                        List<Map<String, String>> type_list = new ArrayList<Map<String, String>>();
                        List<Map<String, String>> feature_list = new ArrayList<Map<String, String>>();

                        try {

                            if (jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {

                                JSONObject jsonObject = new JSONObject(jsonString);
                                String errcode = jsonObject.getString("errcode");
                                if (!errcode.equals("0")) {
                                    String errmsg = jsonObject.getString("errmsg");

                                    errcode_map.put("errcode", errcode);
                                    errcode_map.put("errmsg", errmsg);

                                    Message message2 = Message.obtain();

                                    message2.obj = errcode_map;

                                    null_handler.sendMessage(message2);

                                } else {

                                    JSONObject dataObject = jsonObject
                                            .getJSONObject("data");

                                    JSONArray companyArray = dataObject
                                            .getJSONArray("company");


                                    Map<String, String> map3 = new HashMap<String, String>();

                                    map3.put("company_code", "");
                                    map3.put("company_name", "全部");

                                    company_list.add(map3);

                                    for (int i = 0; i < companyArray.length(); i++) {

                                        Map<String, String> map = new HashMap<String, String>();
                                        JSONArray jsonArray1 = companyArray
                                                .getJSONArray(i);

                                        map.put("company_code",
                                                jsonArray1.getString(0));
                                        map.put("company_name",
                                                jsonArray1.getString(1));

                                        company_list.add(map);
                                    }

                                    JSONArray periodArray = dataObject
                                            .getJSONArray("period");

                                    for (int i = 0; i < periodArray.length(); i++) {

                                        Map<String, String> map = new HashMap<String, String>();
                                        JSONArray jsonArray1 = periodArray
                                                .getJSONArray(i);

                                        map.put("period_code",
                                                jsonArray1.getString(0));
                                        map.put("period_name",
                                                jsonArray1.getString(1));

                                        period_list.add(map);
                                    }

                                    JSONArray product_typeArray = dataObject
                                            .getJSONArray("product_type");

                                    for (int i = 0; i < product_typeArray.length(); i++) {

                                        Map<String, String> map = new HashMap<String, String>();
                                        JSONArray jsonArray1 = product_typeArray
                                                .getJSONArray(i);

                                        map.put("product_type_code",
                                                jsonArray1.getString(0));
                                        map.put("product_type_name",
                                                jsonArray1.getString(1));

                                        type_list.add(map);
                                    }

                                    JSONArray product_featureArray = dataObject
                                            .getJSONArray("product_feature");

                                    for (int i = 0; i < product_featureArray
                                            .length(); i++) {

                                        Map<String, String> map = new HashMap<String, String>();
                                        JSONArray jsonArray1 = product_featureArray
                                                .getJSONArray(i);

                                        map.put("product_feature_code",
                                                jsonArray1.getString(0));
                                        map.put("product_feature_name",
                                                jsonArray1.getString(1));

                                        feature_list.add(map);
                                    }

                                    map_all.put("company_list", company_list);
                                    map_all.put("period_list", period_list);
                                    map_all.put("type_list", type_list);
                                    map_all.put("feature_list", feature_list);

                                    Message message = Message.obtain();

                                    message.obj = map_all;
                                    select_handler.sendMessage(message);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (Proposal_SelectActivity) activity;
        mActivity.setHandler(mHandler);
        OnProposal_SelectActivityChangeListener = (OnProposal_SelectActivityChangeListener) activity;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName); // 统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }


    private void OnMenuRightFragmentInit() {


        for (int i = 0; i < Proposal_CompanyAdapter.getIsSelected().size(); i++) {

            Proposal_CompanyAdapter.getIsSelected().put(i, false);

        }

        companyNum = 0;


        companycode = "";


        p_companyadapter.notifyDataSetChanged();


    }

}
