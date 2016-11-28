package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AddChartActivity extends BaseActivity {



    private ImageView addchart_back;
    private TextView addchart_title;
    private Button addchart_submit;
    private EditText addchart_edit;


    private String user_id;
    private String token;
    private String id;
    private String user_name;
    private Map<String, String> key_value = new HashMap<String, String>();
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            if (errcode.equals("0")) {

                finish();
                Toast.makeText(AddChartActivity.this, "您的留言成功！",
                        Toast.LENGTH_LONG).show();
            } else {
                finish();
                Toast.makeText(AddChartActivity.this, "您的留言失败！",
                        Toast.LENGTH_LONG).show();
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.addchart);


        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");


        Intent intent = getIntent();

        id = intent.getStringExtra("id");

        user_name = intent.getStringExtra("user_name");


        init();
        initEvent();

    }

    private void init() {


        addchart_back = (ImageView) findViewById(R.id.addchart_back);
        addchart_title = (TextView) findViewById(R.id.addchart_title);

        addchart_edit = (EditText) findViewById(R.id.addchart_edit);

        addchart_submit = (Button) findViewById(R.id.addchart_submit);

    }

    private void initEvent() {

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        key_value.put("user_id", user_id);
        key_value.put("token", token);
        key_value.put("expert_user_id", id);
        addchart_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();

            }
        });

        addchart_title.setText("与" + user_name + "对话");
        addchart_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String context = addchart_edit.getText().toString();

                if (context.equals("") || context.equals("null")) {
                    Toast.makeText(AddChartActivity.this, "留言不能为空！",
                            Toast.LENGTH_SHORT).show();
                } else {

                    key_value.put("question_context", context);
                    final String url = IpConfig.getUri("addChartQuestion");
                    setdata(url);
                }
            }
        });
    }



    private void setdata(String url) {


        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {

                        Log.e("error", "获取数据异常 ", e);

                    }

                    @Override
                    public void onResponse(String response) {

                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);

                        try {

                            // Log.d("44444", jsonString);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            String data = jsonObject.getString("data");

                            String errcode = jsonObject.getString("errcode");


                            map.put("errcode", errcode);
                            Log.d("44444", errcode);
                            Message message = Message.obtain();

                            message.obj = map;

                            handler.sendMessage(message);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }
}
