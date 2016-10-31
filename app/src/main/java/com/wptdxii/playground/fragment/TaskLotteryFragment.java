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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.BigLotteryActivity;
import com.cloudhome.activity.LoginActivity;
import com.cloudhome.activity.MatchmakerActivity;
import com.cloudhome.activity.MoreTopicActivity;
import com.cloudhome.activity.Proposal_SelectActivity;
import com.cloudhome.activity.TaskInstructionActivity;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GetActiveInfo;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.CircleImage;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.PublicLoadPage;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskLotteryFragment extends BaseFragment implements NetResultListener,View.OnClickListener {
    private View view;

    private ImageView iv_float_left;
    private ImageView iv_float_right_top;
    private ImageView iv_float_right;
    private CircleImage iv_head_circle;
    private TextView tv_name;
    private TextView tv_lottery_num;
    private Button ib_new_customer;
    private ImageView iv_new_customer_complete;
    private Button ib_share_topic;
    private ImageView iv_share_topic_complete;
    private Button ib_make_template;
    private ImageView iv_make_insurance_complete;
    private ImageView iv_goto_lottery;
    private PublicLoadPage mLoadPage;
    private ImageView iv_task_detail;
    String activeDescription="";

    SharedPreferences sp;
    private String user_id;
    private String token;
    private String loginString;
    private Map<String, String> dataValue = null;
    private final int GET_ACTIVE_INFO=1;
    //统计
    private Statistics statistics=new Statistics(this);
    private String Event_GetLottery = "TaskLotteryFragment_Lottery";
    private String Event_MatchMaker = "TaskLotteryFragment_MatchMaker";
    private String Event_ShareTopic = "TaskLotteryFragment_ShareTopic";
    private String Event_MakeTemplate = "TaskLotteryFragment_MakeTemplate";


    public TaskLotteryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_left_task_lottery, container, false);
        initView(view);

        initData();
        return view;
    }

    public void refreshView(){
        initData();
    }



    private void initView(View view) {
        mLoadPage = new PublicLoadPage((LinearLayout)view.findViewById(R.id.common_load)) {
            @Override
            public void onReLoadCLick(LinearLayout layout,
                                      RelativeLayout rl_progress, ImageView iv_loaded,
                                      TextView tv_loaded, Button btLoad) {
                initData();
            }
        };

        iv_task_detail=(ImageView) view.findViewById(R.id.iv_task_detail);
        iv_float_left= (ImageView) view.findViewById(R.id.iv_float_left);
        iv_float_right_top= (ImageView) view.findViewById(R.id.iv_float_right_top);
        iv_float_right= (ImageView) view.findViewById(R.id.iv_float_right);
        iv_head_circle=(CircleImage)view.findViewById(R.id.iv_head_circle);
        tv_name= (TextView) view.findViewById(R.id.tv_name);
        tv_lottery_num= (TextView) view.findViewById(R.id.tv_lottery_num);
        ib_new_customer= (Button) view.findViewById(R.id.ib_new_customer);
        iv_new_customer_complete= (ImageView) view.findViewById(R.id.iv_new_customer_complete);
        ib_share_topic= (Button) view.findViewById(R.id.ib_share_topic);
        iv_share_topic_complete= (ImageView) view.findViewById(R.id.iv_share_topic_complete);
        ib_make_template= (Button) view.findViewById(R.id.ib_make_template);
        iv_make_insurance_complete= (ImageView) view.findViewById(R.id.iv_make_insurance_complete);
        iv_goto_lottery= (ImageView) view.findViewById(R.id.iv_goto_lottery);

        ib_new_customer.setOnClickListener(this);
        ib_share_topic.setOnClickListener(this);
        ib_make_template.setOnClickListener(this);
        iv_goto_lottery.setOnClickListener(this);
        iv_task_detail.setOnClickListener(this);

        Animation anim1= AnimationUtils.loadAnimation(getActivity(), R.anim.float_image_task_lottery);
        Animation anim2= AnimationUtils.loadAnimation(getActivity(), R.anim.float_top_right_image_task_lottery);
        Animation anim3= AnimationUtils.loadAnimation(getActivity(), R.anim.float_right_image_task_lottery);
        iv_float_left.startAnimation(anim1);
        iv_float_right_top.startAnimation(anim2);
        iv_float_right.startAnimation(anim3);
    }
    private void initData() {

        sp = getActivity().getSharedPreferences("userInfo", 0);
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        loginString = sp.getString("Login_STATE", "none");
        dataValue = new HashMap<String, String>();
        Log.i("请求数据","getActiveInfo");
        mLoadPage.startLoad();
        GetActiveInfo getActiveInfo=new GetActiveInfo(this);
        getActiveInfo.execute(user_id,GET_ACTIVE_INFO,dataValue,token);
    }


    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action)
        {
            case GET_ACTIVE_INFO:
                if (flag == MyApplication.DATA_OK) {
                    mLoadPage.loadSuccess(null, null);
                    if(loginString.equals("none")){
                        iv_head_circle.setImageResource(R.drawable.main_default);
                    }else{


                        Glide.with(TaskLotteryFragment.this)
                                .load(dataValue.get("imgUrl"))
                                .placeholder(R.drawable.head_fail)
                                .error(R.drawable.head_fail)
                                .crossFade()
                                .into(iv_head_circle);
                    }

                    if(TextUtils.isEmpty(dataValue.get("userName"))){
                        tv_name.setText("保险人");
                    }else{
                        tv_name.setText(dataValue.get("userName"));
                    }

                    int times= Integer.parseInt(dataValue.get("times"));
                    if(times==0){
                        tv_lottery_num.setText("您还没有抽奖机会，先去做任务吧");
                    }else{
                        tv_lottery_num.setText("你有"+dataValue.get("times")+"次抽奖机会");
                    }

                    if(dataValue.get("matchmaker").equals("0")){
                        ib_new_customer.setEnabled(true);
                        iv_new_customer_complete.setVisibility(View.GONE);
                    }else{
                        ib_new_customer.setEnabled(false);
                        iv_new_customer_complete.setVisibility(View.VISIBLE);
                    }
                    if(dataValue.get("microtopic").equals("0")){
                        ib_share_topic.setEnabled(true);
                        iv_share_topic_complete.setVisibility(View.GONE);
                    }else{
                        ib_share_topic.setEnabled(false);
                        iv_share_topic_complete.setVisibility(View.VISIBLE);
                    }
                    if(dataValue.get("proposal").equals("0")){
                        ib_make_template.setEnabled(true);
                        iv_make_insurance_complete.setVisibility(View.GONE);
                    }else{
                        ib_make_template.setEnabled(false);
                        iv_make_insurance_complete.setVisibility(View.VISIBLE);
                    }
                    activeDescription=dataValue.get("activeDescription");

                } else if (flag == MyApplication.NET_ERROR) {
                    mLoadPage.loadFail(MyApplication.NO_NET, MyApplication.BUTTON_RELOAD, 0);
                }else if(flag == MyApplication.DATA_EMPTY){
                    mLoadPage.loadFail(MyApplication.NO_DATA, MyApplication.BUTTON_RELOAD, 0);
                }else if(flag == MyApplication.JSON_ERROR){
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 0);
                }else if(flag == MyApplication.DATA_ERROR){
                    mLoadPage.loadFail(MyApplication.FETCH_DATA_FAILED, MyApplication.BUTTON_RELOAD, 0);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent=null;
        switch (view.getId()){
            case R.id.iv_goto_lottery:
                if(isLogin()){
                    intent=new Intent(getActivity(),BigLotteryActivity.class);
                    intent.putExtra("title","抽大奖");
                    String web_address=dataValue.get("lotteryInterface").toString();
                    web_address=web_address+"&client_type=android";
                    intent.putExtra("web_address",web_address);
                    startActivityForResult(intent,150);
                }else{
                    intent=new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 150);
                }
                statistics.execute("active_lottery");
                MobclickAgent.onEvent(getActivity(), Event_GetLottery);
                break;
            case R.id.ib_new_customer:
                if(isLogin()){
                    String url = IpConfig.getUri("personasForHtml");
                    intent = new Intent();
                    intent.putExtra("title", "红娘");
                    intent.putExtra("url", url);
                    intent.setClass(getActivity(), MatchmakerActivity.class);
                    startActivityForResult(intent, 150);
                }else{
                    intent=new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent,150);
                }
                MobclickAgent.onEvent(getActivity(), Event_MatchMaker);

                break;
            case R.id.ib_share_topic:
                if(isLogin()){
                   intent=new Intent(getActivity(), MoreTopicActivity.class);
                    startActivityForResult(intent, 150);
                }else{
                    intent=new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent,150);
                }
                MobclickAgent.onEvent(getActivity(), Event_ShareTopic);

                break;
            case R.id.ib_make_template:
                if(isLogin()){
                    intent=new Intent(getActivity(), Proposal_SelectActivity.class);
                    startActivityForResult(intent,150);
                }else{
                    intent=new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent,150);
                }
                MobclickAgent.onEvent(getActivity(), Event_MakeTemplate);
                break;
            case R.id.iv_task_detail:
                intent=new Intent(getActivity(), TaskInstructionActivity.class);
                intent.putExtra("instructionStr",activeDescription);
                startActivity(intent);
                break;
        }
    }

    public boolean isLogin(){
        loginString = sp.getString("Login_STATE", "none");
        if (loginString.equals("none")) {
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==150&&resultCode==170){
//            refreshView();
//        }
//        if(requestCode==150){
            refreshView();
//        }
    }
}
