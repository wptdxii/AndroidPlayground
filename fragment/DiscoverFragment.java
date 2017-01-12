package com.cloudhome.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.activity.CommissionMainActivity;
import com.cloudhome.activity.InvitationActivity;
import com.cloudhome.activity.LoginActivity;
import com.cloudhome.activity.MatchmakerActivity;
import com.cloudhome.activity.MoreTopicActivity;
import com.cloudhome.activity.PolicyPictureActivity;
import com.cloudhome.activity.Proposal_SelectActivity;
import com.cloudhome.activity.UnreadArticle;
import com.cloudhome.activity.ZhanYeHuoDongActivity;
import com.cloudhome.event.LoginEvent;
import com.cloudhome.event.UnreadArticleEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.IpConfig;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class DiscoverFragment extends BaseFragment implements View.OnClickListener, NetResultListener {

    private View mView;
    private String user_type = "";
    private String user_id;
    private String token;
    private String user_id_encode;
    SharedPreferences sp;

    private String loginString = "";
    private String user_state = "";
    //统计接口
    public Statistics statistics;

    private RelativeLayout rl_hot_topic;
    private TextView tv_unread_num;
    private RelativeLayout rl_sheet_upload;
    private RelativeLayout rl_match_maker;
    private RelativeLayout rl_recommend;
    private RelativeLayout rl_check_mission;
    private RelativeLayout rl_invite_friend;
    private RelativeLayout rl_zhanye;
    Map<String, String> key_value = new HashMap<String, String>();
    private boolean canEnterChacha = false;
    private String notChachaMsg = "暂无数据";
    private boolean canEnterInvite = false;
    private String notInviteMsg = "暂无数据";
    private boolean isDataSuccess;
    private String inviteWebUrl = "";

    private String Event_HotTopic = "DiscoverFragment_HotTopic";
    private String Event_MatchMaker = "DiscoverFragment_MatchMaker";
    private String Event_CheckMission = "DiscoverFragment_CheckMission";
    private String Event_InviteFriend = "DiscoverFragment_InviteFriend";
    private String Event_GuaranteeUpload = "DiscoverFragment_GuaranteeUpload";
    private String Event_Recommend = "DiscoverFragment_Recommend";
    private String Event_Activity = "DiscoverFragment_Activity";
    private UnreadArticle unreadArticle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            sp = getActivity().getSharedPreferences("userInfo", 0);
            user_id = sp.getString("Login_UID", "");
            token = sp.getString("Login_TOKEN", "");
            user_id_encode = sp.getString("Login_UID_ENCODE", "");
            initView(inflater, container);
            initEvent();
            EventBus.getDefault().register(this);
        }
        return mView;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(LoginEvent event) {
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode = sp.getString("Login_UID_ENCODE", "");
        loginString = sp.getString("Login_STATE", "none");
        user_type = sp.getString("Login_TYPE", "");
        user_state = sp.getString("Login_CERT", "");
        initEvent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UnreadArticleEvent event) {
        int num = event.num;
        if (num == 0) {
            tv_unread_num.setVisibility(View.INVISIBLE);
            tv_unread_num.setText("");
        } else {
            tv_unread_num.setVisibility(View.VISIBLE);
            if (num > 99) {
                tv_unread_num.setText("99+");
            } else {
                tv_unread_num.setText(num + "");
            }
        }
    }


    private void initView(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.discover_activity, container, false);
        loginString = sp.getString("Login_STATE", "none");
        user_type = sp.getString("Login_TYPE", "");
        user_state = sp.getString("Login_CERT", "");

        rl_hot_topic = (RelativeLayout) mView.findViewById(R.id.rl_hot_topic);
        tv_unread_num = (TextView) mView.findViewById(R.id.tv_unread_num);
        rl_sheet_upload = (RelativeLayout) mView.findViewById(R.id.rl_sheet_upload);
        rl_match_maker = (RelativeLayout) mView.findViewById(R.id.rl_match_maker);
        rl_recommend = (RelativeLayout) mView.findViewById(R.id.rl_recommend);
        rl_check_mission = (RelativeLayout) mView.findViewById(R.id.rl_check_mission);
        rl_invite_friend = (RelativeLayout) mView.findViewById(R.id.rl_invite_friend);
        rl_zhanye = (RelativeLayout) mView.findViewById(R.id.rl_zhanye);
        rl_hot_topic.setOnClickListener(this);
        rl_sheet_upload.setOnClickListener(this);
        rl_match_maker.setOnClickListener(this);
        rl_recommend.setOnClickListener(this);
        rl_check_mission.setOnClickListener(this);
        rl_invite_friend.setOnClickListener(this);
        rl_zhanye.setOnClickListener(this);
    }

    void initEvent() {

        //获取查查佣金和邀请好友是否可以点击
        key_value.put("userId", user_id_encode);
        key_value.put("token", token);
        key_value.put("resName", "首页");
        String user_url = IpConfig.getUri2("getRoleMenus");
        setlistdata(user_url);
        unreadArticle = new UnreadArticle(true, getActivity());
        unreadArticle.getUnreadArticle();
    }


    @Override
    public void onClick(View view) {
        statistics = new Statistics(DiscoverFragment.this);
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_hot_topic:
                intent.setClass(getActivity(), MoreTopicActivity.class);
                getActivity().startActivity(intent);
                statistics.execute("index_func_hottopic");

                MobclickAgent.onEvent(getActivity(), Event_HotTopic);
                break;
            case R.id.rl_sheet_upload:
                if (loginString.equals("none")) {
                    intent.setClass(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    intent.setClass(getActivity(), PolicyPictureActivity.class);
                    getActivity().startActivity(intent);
                }
                statistics.execute("index_func_policy");
                MobclickAgent.onEvent(getActivity(), Event_GuaranteeUpload);
                break;
            case R.id.rl_match_maker:
                String url = IpConfig.getUri("personasForHtml");
                intent.putExtra("title", "红娘");
                intent.putExtra("url", url);
                intent.setClass(getActivity(), MatchmakerActivity.class);
                getActivity().startActivity(intent);
                statistics.execute("index_func_matchmaker");

                MobclickAgent.onEvent(getActivity(), Event_MatchMaker);
                break;
            case R.id.rl_recommend:
                intent.setClass(getActivity(), Proposal_SelectActivity.class);
                getActivity().startActivity(intent);
                statistics.execute("index_func_suggest");
                MobclickAgent.onEvent(getActivity(), Event_Recommend);
                break;
            case R.id.rl_check_mission:
                if (loginString.equals("none")) {
                    intent.setClass(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    if (isDataSuccess) {
                        if (canEnterChacha) {
                            intent.setClass(getActivity(), CommissionMainActivity.class);
                            getActivity().startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), notChachaMsg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        initEvent();
                        if (isDataSuccess) {
                            if (canEnterChacha) {
                                intent.setClass(getActivity(), CommissionMainActivity.class);
                                getActivity().startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), notChachaMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                statistics.execute("index_func_cc");

                MobclickAgent.onEvent(getActivity(), Event_CheckMission);
                break;
            case R.id.rl_invite_friend:
                if (loginString.equals("none")) {
                    intent.setClass(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    if (isDataSuccess) {
                        if (canEnterInvite) {
                            InvitationActivity.actionStart(getActivity(), inviteWebUrl);
                        } else {
                            Toast.makeText(getActivity(), notInviteMsg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        initEvent();
                        if (isDataSuccess) {
                            if (canEnterInvite) {
                                InvitationActivity.actionStart(getActivity(), inviteWebUrl);
                            } else {
                                Toast.makeText(getActivity(), notInviteMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                statistics.execute("mine_invite");


                MobclickAgent.onEvent(getActivity(), Event_InviteFriend);

                break;
            case R.id.rl_zhanye:
                intent.setClass(getActivity(), ZhanYeHuoDongActivity.class);
                getActivity().startActivity(intent);
                MobclickAgent.onEvent(getActivity(), Event_Activity );
                break;
        }
    }

    private static final String TAG = "DiscoverFragment";

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {

    }


    private void setlistdata(String url) {

        Log.d("7777", url);
        OkHttpUtils.get().url(url).params(key_value).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        Log.d("onSuccess98988", "onSuccess json = "
                                + jsonString);
                        Map<String, String> errcode_map = new HashMap<String, String>();
                        try {

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                String status = "false";
                            } else {

                                JSONObject jsonObject = new JSONObject(
                                        jsonString);

                                String errcode = jsonObject
                                        .getString("errcode");
                                if (!errcode.equals("0")) {
                                    String errmsg = jsonObject
                                            .getString("errmsg");
                                    errcode_map.put("errcode", errcode);
                                    errcode_map.put("errmsg", errmsg);
                                } else {
                                    inviteWebUrl = "";
                                    isDataSuccess = true;
                                    JSONObject dataObject = jsonObject
                                            .getJSONObject("data");
                                    //									JSONArray menusList = dataObject
                                    //											.getJSONArray("menus")
                                    //											.getJSONObject(0)
                                    //											.getJSONArray("menus");
                                    JSONArray menusArray = dataObject.getJSONArray("menus");
                                    for (int m = 0; m < menusArray.length(); m++) {
                                        JSONObject object = menusArray.getJSONObject(m);
                                        if (object.getString("name").equals("首页")) {
                                            JSONArray menusList = object.getJSONArray("menus");
                                            for (int i = 0; i < menusList.length(); i++) {
                                                JSONObject jsonObject2 = menusList.getJSONObject(i);
                                                if (jsonObject2.getString("name").equals("查查佣金")) {
                                                    String operate = jsonObject2.getString("operate");
                                                    if (operate.equals("1")) {
                                                        canEnterChacha = true;
                                                    } else {
                                                        canEnterChacha = false;
                                                        notChachaMsg = jsonObject2.getString("unusemsg");
                                                    }
                                                }
                                                if (jsonObject2.getString("name").equals("邀请好友")) {
                                                    String operate = jsonObject2.getString("operate");
                                                    if (operate.equals("1")) {
                                                        canEnterInvite = true;
                                                        inviteWebUrl = jsonObject2.getString("url");
                                                        inviteWebUrl = IpConfig.getIp4() + inviteWebUrl + "?user_id=" + user_id_encode + "&token=" + token;
                                                    } else {
                                                        canEnterInvite = false;
                                                        notInviteMsg = jsonObject2.getString("unusemsg");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i("hidden---", "" + hidden);
        if (!hidden) {
            if (unreadArticle == null) {
                unreadArticle = new UnreadArticle(true, getActivity());
            }
            unreadArticle.getUnreadArticle();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
