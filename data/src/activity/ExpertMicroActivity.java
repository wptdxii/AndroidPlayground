package com.cloudhome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.event.ExpertRefreshEvent;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.RoundImageView;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Call;

public class ExpertMicroActivity extends BaseActivity {


    private RoundImageView headimage;

    private RelativeLayout e_micro_back;
    private RelativeLayout rl_right;
    private TextView tv_text;
    private TextView expertname;
    private TextView expert_area;
    private TextView like_num;
    private TextView cert_num;

    private TextView q_grey_text1;
    private TextView q_grey_text2;
    private TextView q_grey_text3;
    private TextView q_grey_text4;
    private TextView q_grey_text5;
    private TextView q_grey_text6;

    private ImageView adiviser_phone;

    private ImageView certified_img;
    private TextView expert_phonenum;
    private TextView expert_context;
    private String num;

    private RelativeLayout like_rel;

    private String loginString, type;
    private Map<String, String> key_value = new HashMap<String, String>();
    private String id;
    private String avatar;
    private String user_name;
    private String company_name;
    private String mobile_area;
    private String good_count;
    private String cert_a;
    private String cert_b;
    private String licence;
    private String mobile;
    private String cert_num_isShowFlg;
    private String mobile_num_short;
    private String state;
    private String personal_specialty;
    private String personal_context;

    private String user_id;
    private String token;
    private int count;
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            if (errcode.equals("0")) {

                String likestate = data.get("likestate");
                if (likestate.equals("2")) {
                    count = Integer.valueOf(good_count) - 1;

                    like_num.setText(count + "赞");
                    good_count = count + "";
                } else if (likestate.equals("1")) {
                    count = Integer.valueOf(good_count) + 1;
                    like_num.setText(count + "赞");
                    good_count = count + "";
                }

            } else {
                String errmsg = data.get("errmsg");
                Toast.makeText(ExpertMicroActivity.this, errmsg,
                        Toast.LENGTH_LONG).show();
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.expert_micro);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        avatar = intent.getStringExtra("avatar");
        user_name = intent.getStringExtra("user_name");
        company_name = intent.getStringExtra("company_name");
        mobile_area = intent.getStringExtra("mobile_area");
        good_count = intent.getStringExtra("good_count");
        cert_a = intent.getStringExtra("cert_a");
        cert_b = intent.getStringExtra("cert_b");
        licence = intent.getStringExtra("licence");
        mobile = intent.getStringExtra("mobile");

        cert_num_isShowFlg = intent.getStringExtra("cert_num_isShowFlg");
        mobile_num_short = intent.getStringExtra("mobile_num_short");
        state = intent.getStringExtra("state");

        personal_specialty = intent.getStringExtra("personal_specialty");
        personal_context = intent.getStringExtra("personal_context");

        init();
        initEvent();

    }


    void init() {

        like_rel = (RelativeLayout) findViewById(R.id.like_rel);
        headimage = (RoundImageView) findViewById(R.id.headimage);
        e_micro_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_text.setText("保险专家微站");
        expertname = (TextView) findViewById(R.id.expertname);
        like_num = (TextView) findViewById(R.id.like_num);
        expert_area = (TextView) findViewById(R.id.expert_area);
        cert_num = (TextView) findViewById(R.id.cert_num);
        adiviser_phone = (ImageView) findViewById(R.id.adiviser_phone);

        certified_img = (ImageView) findViewById(R.id.certified_img);
        expert_phonenum = (TextView) findViewById(R.id.expert_phonenum);
        expert_context = (TextView) findViewById(R.id.expert_context);
        q_grey_text1 = (TextView) findViewById(R.id.q_grey_text1);
        q_grey_text2 = (TextView) findViewById(R.id.q_grey_text2);
        q_grey_text3 = (TextView) findViewById(R.id.q_grey_text3);
        q_grey_text4 = (TextView) findViewById(R.id.q_grey_text4);
        q_grey_text5 = (TextView) findViewById(R.id.q_grey_text5);
        q_grey_text6 = (TextView) findViewById(R.id.q_grey_text6);

    }

    void initEvent() {

        if (!(avatar.length() < 6)) {


            Glide.with(ExpertMicroActivity.this)
                    .load(avatar)
                    .placeholder(R.drawable.white)  //占位图 图片正在加载
                    .into(headimage);


        }

        e_micro_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
                EventBus.getDefault().post(new ExpertRefreshEvent(good_count));

            }
        });

        expertname.setText(user_name);

        // expert_area.setText(company_name + "|" + mobile_area);

        if (company_name == null || company_name.equals("")
                || company_name.equals("null")) {

            expert_area.setText("暂未设定" + "|" + mobile_area);

        } else {

            expert_area.setText(company_name + "|" + mobile_area);

        }
        like_num.setText(good_count + "赞");

        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        type = sp.getString("Login_TYPE", "none");
        key_value.put("user_id", user_id);
        key_value.put("token", token);
        key_value.put("expert_user_id", id);

        like_rel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (loginString.equals("none")) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            ExpertMicroActivity.this);

                    builder.setTitle("提示");
                    builder.setMessage("您还未登录");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.create().show();

                } else {

                    final String PRODUCT_URL = IpConfig.getUri("setGoodExpert");
                    setdata(PRODUCT_URL);
                }

            }
        });
        adiviser_phone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (mobile_num_short.equals("0")) {

                    Toast.makeText(ExpertMicroActivity.this, "该专家不接受电话服务！",
                            Toast.LENGTH_LONG).show();

                } else if (mobile_num_short.equals("1")) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            ExpertMicroActivity.this);

                    builder.setMessage(mobile);

                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    dialog.dismiss();
                                    requestCallPhonePermission();
                                }
                            });

                    builder.create().show();
                }
            }
        });

        if (state.equals("01")) {
            certified_img.setVisibility(View.GONE);
        }
        if ((cert_a.equals("") || cert_a.equals("null"))
                && (cert_b.equals("") || cert_b.equals("null"))
                && (licence.equals("") || licence.equals("null"))) {

            cert_num.setText("未知");
        } else if (cert_num_isShowFlg.equals("0") && !cert_a.equals("")
                && !cert_a.equals("null")) {
            if (cert_a.length() < 4) {
                cert_num.setText(cert_a);
            } else {

                String cert_numpass = cert_a.substring(0, cert_a.length() - 4)
                        + "****";
                cert_num.setText(cert_numpass);
            }
        } else if (cert_num_isShowFlg.equals("0") && !cert_b.equals("")
                && !cert_b.equals("null")) {
            if (cert_b.length() < 4) {
                cert_num.setText(cert_b);
            } else {

                String cert_numpass = cert_b.substring(0, cert_b.length() - 4)
                        + "****";
                cert_num.setText(cert_numpass);
            }
        } else if (cert_num_isShowFlg.equals("0") && !licence.equals("")
                && !licence.equals("null")) {
            if (licence.length() < 4) {
                cert_num.setText(licence);
            } else {

                String cert_numpass = licence
                        .substring(0, licence.length() - 4) + "****";
                cert_num.setText(cert_numpass);
            }
        } else if (cert_num_isShowFlg.equals("1")) {
            if (!cert_a.equals("") && !cert_a.equals("null")) {
                cert_num.setText(cert_a);
            } else if (!cert_b.equals("") && !cert_b.equals("null")) {
                cert_num.setText(cert_b);
            } else {
                cert_num.setText(licence);
            }
        }

        if (mobile_num_short.equals("0")) {

            String phone_numpass = mobile.substring(0, mobile.length() - 4)
                    + "****";
            expert_phonenum.setText(phone_numpass);

        } else if (mobile_num_short.equals("1")) {

            expert_phonenum.setText(mobile);

        }

        int i = 0;
        String split = "|";
        StringTokenizer token = new StringTokenizer(personal_specialty, split);

        String[] Ins_Str = new String[token.countTokens()];

        while (token.hasMoreTokens()) {

            Ins_Str[i] = token.nextToken();
            i++;
        }
        if (i == 6) {
            q_grey_text1.setVisibility(View.VISIBLE);
            q_grey_text2.setVisibility(View.VISIBLE);
            q_grey_text3.setVisibility(View.VISIBLE);
            q_grey_text4.setVisibility(View.VISIBLE);
            q_grey_text5.setVisibility(View.VISIBLE);
            q_grey_text6.setVisibility(View.VISIBLE);

            q_grey_text1.setText(Ins_Str[0]);
            q_grey_text2.setText(Ins_Str[1]);
            q_grey_text3.setText(Ins_Str[2]);
            q_grey_text4.setText(Ins_Str[3]);
            q_grey_text5.setText(Ins_Str[4]);
            q_grey_text6.setText(Ins_Str[5]);
        } else if (i == 5) {
            q_grey_text1.setVisibility(View.VISIBLE);
            q_grey_text2.setVisibility(View.VISIBLE);
            q_grey_text3.setVisibility(View.VISIBLE);
            q_grey_text4.setVisibility(View.VISIBLE);
            q_grey_text5.setVisibility(View.VISIBLE);
            q_grey_text6.setVisibility(View.GONE);

            q_grey_text1.setText(Ins_Str[0]);
            q_grey_text2.setText(Ins_Str[1]);
            q_grey_text3.setText(Ins_Str[2]);
            q_grey_text4.setText(Ins_Str[3]);
            q_grey_text5.setText(Ins_Str[4]);

        } else if (i == 4) {
            q_grey_text1.setVisibility(View.VISIBLE);
            q_grey_text2.setVisibility(View.VISIBLE);
            q_grey_text3.setVisibility(View.VISIBLE);
            q_grey_text4.setVisibility(View.VISIBLE);
            q_grey_text5.setVisibility(View.GONE);
            q_grey_text6.setVisibility(View.GONE);

            q_grey_text1.setText(Ins_Str[0]);
            q_grey_text2.setText(Ins_Str[1]);
            q_grey_text3.setText(Ins_Str[2]);
            q_grey_text4.setText(Ins_Str[3]);

        } else if (i == 3) {

            q_grey_text1.setVisibility(View.VISIBLE);
            q_grey_text2.setVisibility(View.VISIBLE);
            q_grey_text3.setVisibility(View.VISIBLE);
            q_grey_text4.setVisibility(View.GONE);
            q_grey_text5.setVisibility(View.GONE);
            q_grey_text6.setVisibility(View.GONE);

            q_grey_text1.setText(Ins_Str[0]);
            q_grey_text2.setText(Ins_Str[1]);
            q_grey_text3.setText(Ins_Str[2]);

        } else if (i == 2) {

            q_grey_text1.setVisibility(View.VISIBLE);
            q_grey_text2.setVisibility(View.VISIBLE);
            q_grey_text3.setVisibility(View.GONE);
            q_grey_text4.setVisibility(View.GONE);
            q_grey_text5.setVisibility(View.GONE);
            q_grey_text6.setVisibility(View.GONE);

            q_grey_text1.setText(Ins_Str[0]);
            q_grey_text2.setText(Ins_Str[1]);

        } else if (i == 1) {
            if (Ins_Str[0] == null || Ins_Str[0].equals("")) {
                q_grey_text1.setVisibility(View.GONE);
                q_grey_text2.setVisibility(View.GONE);
                q_grey_text3.setVisibility(View.GONE);
                q_grey_text4.setVisibility(View.GONE);
                q_grey_text5.setVisibility(View.GONE);
                q_grey_text6.setVisibility(View.GONE);
            }
            q_grey_text1.setVisibility(View.VISIBLE);
            q_grey_text2.setVisibility(View.GONE);
            q_grey_text3.setVisibility(View.GONE);
            q_grey_text4.setVisibility(View.GONE);
            q_grey_text5.setVisibility(View.GONE);
            q_grey_text6.setVisibility(View.GONE);
            q_grey_text1.setText(Ins_Str[0]);
        } else {
            q_grey_text1.setVisibility(View.GONE);
            q_grey_text2.setVisibility(View.GONE);
            q_grey_text3.setVisibility(View.GONE);
            q_grey_text4.setVisibility(View.GONE);
            q_grey_text5.setVisibility(View.GONE);
            q_grey_text6.setVisibility(View.GONE);
        }

        if (!(personal_context == null) && !personal_context.equals("")
                && !personal_context.equals("null")) {
            expert_context.setText(personal_context);
        }

        headimage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String[] img_url = new String[1];
                if (!(avatar.length() < 6)) {

                    img_url[0] = avatar;

                } else {
                    img_url[0] = "expert";

                }

                Intent intent = new Intent();

                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, img_url);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);

                intent.setClass(ExpertMicroActivity.this,
                        ImagePagerActivity.class);
                ExpertMicroActivity.this.startActivity(intent);

            }
        });
    }

    private void requestCallPhonePermission() {
        String[] permissions = new String[]{android.Manifest.permission.CALL_PHONE};
        requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mobile));
                startActivity(intent);
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
                ExpertMicroActivity.this
                        .showRequestPermissionRationale(
                                getString(R.string.msg_callphone_denied));
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                ExpertMicroActivity.this
                        .showPermissionSettingDialog(
                                getString(R.string.msg_callphone_permanent_denied));
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
                    public void onError(Call call, Exception e, int id) {

                        Log.e("error", "获取数据异常 ", e);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);

                        try {

                            // Log.d("44444", jsonString);
                            JSONObject jsonObject = new JSONObject(jsonString);

                            String errcode = jsonObject.getString("errcode");
                            if (errcode.equals("0")) {

                                String data = jsonObject.getString("data");
                                map.put("likestate", data);
                            }

                            String errmsg = jsonObject.getString("errmsg");
                            map.put("errcode", errcode);
                            map.put("errmsg", errmsg);

                            Message message = Message.obtain();

                            message.obj = map;

                            handler.sendMessage(message);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                EventBus.getDefault().post(new ExpertRefreshEvent(good_count));
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
