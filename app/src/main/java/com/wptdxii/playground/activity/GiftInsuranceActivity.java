package com.wptdxii.playground.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.GiveGiftInsurance;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class GiftInsuranceActivity extends BaseActivity implements View.OnClickListener,NetResultListener{
    private RelativeLayout iv_back;
    private TextView tv_text;
    private Button btn_right;
    private View bottom_line;

    private String user_id;
    private String token;
    private String user_id_encode;
    Map<String, Object> map=null;
    PopupWindow popWindow;
    private Button bt_give_other;
    private Button bt_give_me;

    private ImageView iv_middle;

    private Button bt_give;
    String product_cover="";
    String product_id="";
    String product_name="";
    int score;
    int val;
    private final int GIVE_GIFT_INSURANCE=1;
    private final int GIVE_MY_SELF=2;

    Map<String, String> key_value = new HashMap<String, String>();


    private String share_url,img_url, share_title, brief;
    TextView tv_current_score;
    private BaseUMShare share;

    public static GiftInsuranceActivity GiftInsuranceinstance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gift_insurance);
        Intent intent=getIntent();
        product_cover=intent.getStringExtra("product_cover");
        product_id=intent.getStringExtra("product_id");
        product_name=intent.getStringExtra("product_name");
        score=intent.getIntExtra("score", 0);
        val=intent.getIntExtra("val", 0);

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");
        key_value.put("user_id", user_id_encode);
        key_value.put("token", token);

        Intent backIntent=new Intent();
        setResult(170, backIntent);
        initView();

    }



    private void initView() {
        iv_back= (RelativeLayout) findViewById(R.id.iv_back);
        tv_text= (TextView) findViewById(R.id.tv_text);
        btn_right= (Button) findViewById(R.id.btn_right);
        btn_right.setText("赠险说明");
        bottom_line=(View) findViewById(R.id.bottom_line);
        bottom_line.setVisibility(View.GONE);
        bt_give_other= (Button) findViewById(R.id.bt_give_other);
        bt_give_me= (Button) findViewById(R.id.bt_give_me);
        iv_middle=(ImageView) findViewById(R.id.iv_middle);


        Glide.with(GiftInsuranceActivity.this)
                .load(product_cover)
                .placeholder(R.drawable.white)  //占位图 图片正在加载
                .into(iv_middle);

        tv_text.setText(product_name);
        iv_back.setOnClickListener(this);
        bt_give_other.setOnClickListener(this);
        bt_give_me.setOnClickListener(this);
        btn_right.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){  
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_give_other:
                showPop(view);
                Statistics statistics=new Statistics(GiftInsuranceActivity.this);
                statistics.execute("give_main_give_" + product_id);
                break;
            case R.id.bt_give_me:
                if(score<val){
                    Toast.makeText(GiftInsuranceActivity.this,"积分不够哟，快去签到领积分吧！", Toast.LENGTH_SHORT).show();
                    return;
                }
                map=new HashMap<String,Object>();
                GiveGiftInsurance giveGiftInsurance=new GiveGiftInsurance(GiftInsuranceActivity.this);
                giveGiftInsurance.execute(true,user_id,product_id,1,map,GIVE_MY_SELF,token);
                Statistics statistic=new Statistics(GiftInsuranceActivity.this);
                statistic.execute("give_main_get_" + product_id);
                break;
            case R.id.btn_right:
                Intent intent=new Intent(GiftInsuranceActivity.this,GiftInsuranceInstructionActivity.class);
                intent.putExtra("title","赠险说明");
                intent.putExtra("web_address",IpConfig.getIp4()+"/active/active_gave_product_intro.html");
                startActivity(intent);
                break;
        }
    }

    private void showPop(View parent) {

        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vPopWindow=inflater.inflate(R.layout.pop_gift_insurance, null, false);
        //宽300 高300
        int height= Common.dip2px(this,290);
        popWindow = new PopupWindow(vPopWindow, RelativeLayout.LayoutParams.MATCH_PARENT,height,true);
        popWindow.setAnimationStyle(R.style.popwin_anim_style);
        bt_give = (Button) vPopWindow.findViewById(R.id.bt_give);
        //现有积分
        tv_current_score=(TextView) vPopWindow.findViewById(R.id.tv_current_score);
        tv_current_score.setText(score+"");
        //减
        RelativeLayout rl_decrease=(RelativeLayout) vPopWindow.findViewById(R.id.rl_decrease);
        final TextView tv_num=(TextView) vPopWindow.findViewById(R.id.tv_num);
        //加
        RelativeLayout rl_increase=(RelativeLayout) vPopWindow.findViewById(R.id.rl_increase);
        final TextView tv_need_score=(TextView) vPopWindow.findViewById(R.id.tv_need_score);
        tv_need_score.setText(val+"");
        //图片
        RelativeLayout rl_close=(RelativeLayout) vPopWindow.findViewById(R.id.rl_close);

        rl_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num= Integer.parseInt(tv_num.getText().toString());
                if(num==1){
                    return;
                }else{
                    int needScore=(num-1)*val;
                    tv_num.setText(num-1+"");
                    tv_need_score.setText(needScore+"");
                }
            }
        });
        rl_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num= Integer.parseInt(tv_num.getText().toString());
                int needScore=(num+1)*val;
                if(needScore>score){
                    Toast.makeText(GiftInsuranceActivity.this,"积分不够哟，快去签到领积分吧！", Toast.LENGTH_SHORT).show();
                    return;
                }
                tv_need_score.setText(needScore+"");
                tv_num.setText(num+1+"");
            }
        });
        bt_give.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num= Integer.parseInt(tv_num.getText().toString());
                int needScore=num*val;
                if(needScore>score){
                    Toast.makeText(GiftInsuranceActivity.this,"积分不够哟，快去签到领积分吧！", Toast.LENGTH_SHORT).show();
                    return;
                }
                map=new HashMap<String,Object>();
                GiveGiftInsurance giveGiftInsurance=new GiveGiftInsurance(GiftInsuranceActivity.this);
                giveGiftInsurance.execute(false,user_id,product_id,num,map,GIVE_GIFT_INSURANCE,token);
                Statistics statistics=new Statistics(GiftInsuranceActivity.this);
                statistics.execute("give_pop_give_"+product_id);
            }
        });




        rl_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popClose();
            }
        });


        popWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //设置popWindow弹出后，其他地方变暗
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=0.5f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        {
            switch (action)
            {
                case GIVE_GIFT_INSURANCE:
                    String errmsg= (String) dataObj;
                    if (flag == MyApplication.DATA_OK) {
                        popClose();
                        score= (int) map.get("score");
                        share_url=map.get("url").toString();
                         img_url=map.get("icon").toString();
                         share_title=map.get("title").toString();
                         brief=map.get("desc").toString();
                        share=new BaseUMShare(GiftInsuranceActivity.this,share_title,brief,share_url,img_url);
                        share.openShare();
                    } else if (flag == MyApplication.NET_ERROR) {
                    }else if(flag == MyApplication.DATA_EMPTY){
                    }else if(flag == MyApplication.JSON_ERROR){
                    }else if(flag == MyApplication.DATA_ERROR){
                    }
                 break;
                case GIVE_MY_SELF:
                    String msg= (String) dataObj;
                    if (flag == MyApplication.DATA_OK) {
//                        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
                        score= (int) map.get("score");
                        Intent intent=new Intent(GiftInsuranceActivity.this,GiftInsuranceWebActivity.class);
                        intent.putExtra("title",product_name);
                        intent.putExtra("web_address",map.get("url").toString());
                        startActivityForResult(intent, 200);
                    } else if (flag == MyApplication.NET_ERROR) {
                    }else if(flag == MyApplication.DATA_EMPTY){
                    }else if(flag == MyApplication.JSON_ERROR){
                    }else if(flag == MyApplication.DATA_ERROR){
                    }
                    break;
            }
        }
    }

    void popClose(){
        popWindow.dismiss();
        //设置popWindow消失后，其他地方恢复
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=1f;
        getWindow().setAttributes(lp);
    }

    //在web页返回的时候刷新积分
    @Override
    protected void onRestart() {
        super.onRestart();
        // 获取积分
        final String PRODUCT_URL = IpConfig.getUri("getScoreAndMoney");
        getScore(PRODUCT_URL);
    }

    public void getScore(String url){
        OkHttpUtils.get().url(url).params(key_value).build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                        String jsonString = response;
                        android.util.Log.d("onSuccess", "onSuccess json = " + jsonString);
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        try {
                            android.util.Log.d("5555", jsonString);
                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
//                                Toast.makeText(GiftInsuranceActivity.this, "刷新积分失败", Toast.LENGTH_SHORT).show();
                            } else {
                                Map<String, String> map = new HashMap<String, String>();
                                JSONObject jsonObject = new JSONObject(
                                        jsonString);
                                JSONObject dataObject = jsonObject
                                        .getJSONObject("data");
                                String errmsg = jsonObject.getString("errmsg");
                                String errcode = jsonObject
                                        .getString("errcode");
                                String newScore = dataObject.getString("score");
                                score= Integer.parseInt(newScore);
//                                Toast.makeText(GiftInsuranceActivity.this, "刷新积分成功"+score, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
//                            Toast.makeText(GiftInsuranceActivity.this, "刷新积分失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode==210){
            finish();
        }

    }
}
