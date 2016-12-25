package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
@SuppressLint("HandlerLeak")
public class CreditsActivity extends BaseActivity {

    String url;
    private String user_id;
    private String token;
    private String type;
    private TextView mycredits;
    private RelativeLayout credits_rel2;
    private RelativeLayout credits_rel3;
    private Map<String, String> key_value = new HashMap<String, String>();
    private String score = "0";
    private RelativeLayout credits_back;
    private RelativeLayout rl_right;
    private TextView tv_text;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            if (errcode.equals("0")) {
                score = data.get("score");
                mycredits.setText(score + "分");
            } else {
                String errmsg = data.get("errmsg");

                Toast.makeText(CreditsActivity.this, errmsg, Toast.LENGTH_SHORT)
                        .show();
            }
        }

    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.credits);


        init();
        initEvent();

    }



    void init() {
        mycredits = (TextView) findViewById(R.id.mycredits);
        credits_rel2 = (RelativeLayout) findViewById(R.id.credits_rel2);
        credits_rel3 = (RelativeLayout) findViewById(R.id.credits_rel3);
        credits_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text=(TextView) findViewById(R.id.tv_text);
        tv_text.setText("积分兑换");
    }

    void initEvent() {
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        type = sp.getString("Login_TYPE", "none");
        key_value.put("token", token);
        key_value.put("user_id", user_id);

        final String Score_URL = IpConfig.getUri("getScoreAndMoney");

        setdata(Score_URL);

        if (type.equals("02")) {
//			credits_rel2.setVisibility(View.GONE);
            url = IpConfig.getUri("webview_score") + "&userType=2";
        } else {
            url = IpConfig.getUri("webview_score") + "&userType=1";
        }
        credits_rel2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putExtra("score", score);
                intent.setClass(CreditsActivity.this, CreToBlanceActivity.class);
                CreditsActivity.this.startActivity(intent);
            }
        });
        credits_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        credits_rel3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent();
                intent.putExtra("url", url);
                intent.putExtra("title", "积分规则");
                Log.i("积分规则url",url);
                intent.setClass(CreditsActivity.this, JavaScriptActivity.class);
                CreditsActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        final String Score_URL = IpConfig.getUri("getScoreAndMoney");

        setdata(Score_URL);

    }

    private void setdata(String url) {

        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        Log.e("error", "获取数据异常 ", e);
                        Toast.makeText(CreditsActivity.this, "网络连接失败，请确认网络连接后重试",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);

                        try {

                            if (jsonString.equals("") || jsonString.equals("null")) {

                                Toast.makeText(CreditsActivity.this, "网络连接失败，请确认网络连接后重试",
                                        Toast.LENGTH_SHORT).show();
                            } else {


                                JSONObject jsonObject = new JSONObject(
                                        jsonString);
                                String data = jsonObject.getString("data");

                                String errcode = jsonObject
                                        .getString("errcode");

                                JSONObject dataObject = new JSONObject(data);

                                if (errcode.equals("0")) {
                                    String score = dataObject
                                            .getString("score");
                                    map.put("score", score);
                                } else {

                                    map.put("errcode", errcode);
                                }


                                map.put("errcode", errcode);

                                Message message = Message.obtain();

                                message.obj = map;

                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

    }
}
