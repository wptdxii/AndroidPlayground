package com.wptdxii.playground.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.RoundImageView;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zcw.togglebutton.ToggleOrangeButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Call;

public class MyMicroActivity extends BaseActivity implements NetResultListener {


    private RoundImageView headimage;

    private ImageView e_micro_back;
    private TextView e_micro_scan;
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
    private TextView setting_show_text;
    private ImageView adiviser_phone;
    private RelativeLayout like_rel;
    private String site_url;
    private ImageView certified_img;
    private TextView expert_phonenum;
    private TextView expert_context;

    private ToggleOrangeButton show_toggle;
    private Dialog dialog;

    private ImageView certified_eye;
    private ImageView expert_phonenum_eye;
    private TextView profession_edit;
    private TextView profile_edit;
    private String num;
    private String eye_type;
    private String loginString, type;
    private Map<String, String> key_value = new HashMap<String, String>();
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
    private String isShow_in_expertlist;
    private String state;
    private String personal_specialty;
    private String personal_context;

    private String user_id;
    private String token;
    private String user_id_encode="";
    //统计接口
    private Statistics statistics = new Statistics(this);

    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            dialog.dismiss();
            String errcode = data.get("errcode");

            if (errcode.equals("0")) {

                if (eye_type.equals("1")) {
                    if (cert_num_isShowFlg.equals("0")) {
                        cert_num_isShowFlg = "1";
                        certified_eye.setImageResource(R.drawable.eye_open);

                    } else if (cert_num_isShowFlg.equals("1")) {
                        cert_num_isShowFlg = "0";
                        certified_eye.setImageResource(R.drawable.eye_close);

                    }
                    Log.d("cert_num_isShowFlg", cert_num_isShowFlg);
                    if (state.equals("01")) {
                        certified_img.setVisibility(View.GONE);
                    }

                    if ((cert_a.equals("") || cert_a.equals("null"))
                            && (cert_b.equals("") || cert_b.equals("null"))
                            && (licence.equals("") || licence.equals("null"))) {

                        cert_num.setText("暂未认证");
                    } else if (cert_num_isShowFlg.equals("0")
                            && !cert_a.equals("") && !cert_a.equals("null")) {
                        if (cert_a.length() < 4) {
                            cert_num.setText(cert_a);
                        } else {

                            String cert_numpass = cert_a.substring(0,
                                    cert_a.length() - 4)
                                    + "****";
                            cert_num.setText(cert_numpass);
                        }
                    } else if (cert_num_isShowFlg.equals("0")
                            && !cert_b.equals("") && !cert_b.equals("null")) {
                        if (cert_b.length() < 4) {
                            cert_num.setText(cert_b);
                        } else {

                            String cert_numpass = cert_b.substring(0,
                                    cert_b.length() - 4)
                                    + "****";
                            cert_num.setText(cert_numpass);
                        }
                    } else if (cert_num_isShowFlg.equals("0")
                            && !licence.equals("") && !licence.equals("null")) {
                        if (licence.length() < 4) {
                            cert_num.setText(licence);
                        } else {

                            String cert_numpass = licence.substring(0,
                                    licence.length() - 4)
                                    + "****";
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

                    Editor editor1 = sp3.edit();
                    editor1.putString("cert_num_isShowFlg", cert_num_isShowFlg);
                    editor1.commit();


                } else if (eye_type.equals("2")) {

                    if (mobile_num_short.equals("1")) {

                        mobile_num_short = "0";
                        String phone_numpass = mobile.substring(0,
                                mobile.length() - 4)
                                + "****";
                        expert_phonenum.setText(phone_numpass);

                        expert_phonenum_eye
                                .setImageResource(R.drawable.eye_close);

                    } else if (mobile_num_short.equals("0")) {

                        mobile_num_short = "1";
                        expert_phonenum.setText(mobile);
                        expert_phonenum_eye
                                .setImageResource(R.drawable.eye_open);
                    }

                    Editor editor1 = sp3.edit();
                    editor1.putString("mobile_num_short", mobile_num_short);
                    editor1.commit();

                } else if (eye_type.equals("3")) {

                    if (isShow_in_expertlist.equals("1")) {

                        isShow_in_expertlist = "0";
                        show_toggle.setToggleOff();


                        setting_show_text.setText("当前设置:首页的专家列表中不展示您的信息");
                    } else if (isShow_in_expertlist.equals("0")) {
                        isShow_in_expertlist = "1";
                        show_toggle.setToggleOn();
                        setting_show_text.setText("当前设置:首页的专家列表中展示您的信息");

                    }

                    Editor editor1 = sp3.edit();
                    editor1.putString("isShow_in_expertlist", isShow_in_expertlist);
                    editor1.commit();


                }

            } else {
                if (eye_type.equals("1")) {

                    Toast.makeText(MyMicroActivity.this, "资格证号隐藏或显示设置失败！",
                            Toast.LENGTH_LONG).show();
                } else if (eye_type.equals("2")) {
                    Toast.makeText(MyMicroActivity.this, "手机号码隐藏或显示设置失败！",
                            Toast.LENGTH_LONG).show();
                } else if (eye_type.equals("3")) {
                    Toast.makeText(MyMicroActivity.this, "专家信息首页隐藏或显示设置失败！",
                            Toast.LENGTH_LONG).show();
                }
            }

        }

    };

    private Handler errcode_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;

            dialog.dismiss();
            String status = data;

            if (status.equals("false")) {

                Toast.makeText(MyMicroActivity.this,
                        "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_micro);


        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode=sp.getString("Login_UID_ENCODE", "");
        type = sp.getString("Login_TYPE", "none");

        avatar = sp.getString("avatar", "");

        user_name = sp.getString("truename", "");

        company_name = sp2.getString("company_name", "");

        mobile_area = sp2.getString("mobile_area", "");
        good_count = sp3.getString("good_count", "0");

        cert_a = sp.getString("cert_a", "");
        cert_b = sp.getString("cert_b", "");
        licence = sp.getString("licence", "");

        mobile = sp.getString("USER_NAME", "");

        cert_num_isShowFlg = sp3.getString("cert_num_isShowFlg", "");
        mobile_num_short = sp3.getString("mobile_num_short", "");
        isShow_in_expertlist = sp3.getString("isShow_in_expertlist", "");
        state = sp.getString("Login_CERT", "");
        personal_specialty = sp3.getString("personal_specialty", "");

        personal_context = sp3.getString("personal_context", "");

        init();
        initEvent();


    }


    void init() {

        key_value.put("user_id", user_id);
        key_value.put("token", token);

        headimage = (RoundImageView) findViewById(R.id.headimage);
        e_micro_back = (ImageView) findViewById(R.id.e_micro_back);
        expertname = (TextView) findViewById(R.id.expertname);
        like_num = (TextView) findViewById(R.id.like_num);
        expert_area = (TextView) findViewById(R.id.expert_area);
        cert_num = (TextView) findViewById(R.id.cert_num);
        adiviser_phone = (ImageView) findViewById(R.id.adiviser_phone);

        expert_phonenum = (TextView) findViewById(R.id.expert_phonenum);
        expert_context = (TextView) findViewById(R.id.expert_context);
        e_micro_scan = (TextView) findViewById(R.id.e_micro_scan);
        q_grey_text1 = (TextView) findViewById(R.id.q_grey_text1);
        q_grey_text2 = (TextView) findViewById(R.id.q_grey_text2);
        q_grey_text3 = (TextView) findViewById(R.id.q_grey_text3);
        q_grey_text4 = (TextView) findViewById(R.id.q_grey_text4);
        q_grey_text5 = (TextView) findViewById(R.id.q_grey_text5);
        q_grey_text6 = (TextView) findViewById(R.id.q_grey_text6);

        certified_img = (ImageView) findViewById(R.id.certified_img);
        certified_eye = (ImageView) findViewById(R.id.certified_eye);
        expert_phonenum_eye = (ImageView) findViewById(R.id.expert_phonenum_eye);
        profession_edit = (TextView) findViewById(R.id.profession_edit);
        profile_edit = (TextView) findViewById(R.id.profile_edit);
        show_toggle = (ToggleOrangeButton) findViewById(R.id.show_toggle);

        like_rel = (RelativeLayout) findViewById(R.id.like_rel);

        setting_show_text = (TextView) findViewById(R.id.setting_show_text);


        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.fast_progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);


    }

    void initEvent() {

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

                intent.setClass(MyMicroActivity.this, ImagePagerActivity.class);
                MyMicroActivity.this.startActivity(intent);

            }
        });

        adiviser_phone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                statistics.execute("homepage_tel");

                if (mobile_num_short.equals("0")) {

                    Toast.makeText(MyMicroActivity.this, "该专家不接受电话服务！",
                            Toast.LENGTH_LONG).show();

                } else if (mobile_num_short.equals("1")) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            MyMicroActivity.this);

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

                                    Intent intent = new Intent(
                                            Intent.ACTION_CALL);
                                    Uri data = Uri.parse("tel:" + mobile);
                                    intent.setData(data);
                                    startActivity(intent);
                                    dialog.dismiss();

                                }
                            });

                    builder.create().show();
                }

            }
        });

        like_rel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Toast.makeText(MyMicroActivity.this, "自己无法给自己点赞哦！",
                        Toast.LENGTH_SHORT).show();
            }
        });
        profession_edit.setClickable(true);
        profession_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent();
                intent.setClass(MyMicroActivity.this, TagSelectActivity.class);
                MyMicroActivity.this.startActivity(intent);
            }
        });

        if (!avatar.equals("") && !avatar.equals("null")) {

            Glide.with(MyMicroActivity.this)
                    .load(avatar)
                    .placeholder(R.drawable.white_bg)
                    .into(headimage);
        }

        if (state.equals("01") || state.equals("02")) {
            cert_num.setText("只有通过认证才能被推荐哦");
            certified_img.setVisibility(View.GONE);
            certified_eye.setVisibility(View.GONE);

        } else {

            if (cert_num_isShowFlg.equals("0")) {

                certified_eye.setImageResource(R.drawable.eye_close);

            } else if (cert_num_isShowFlg.equals("1")) {

                certified_eye.setImageResource(R.drawable.eye_open);
            }

            if ((cert_a.equals("") || cert_a.equals("null"))
                    && (cert_b.equals("") || cert_b.equals("null"))
                    && (licence.equals("") || licence.equals("null"))) {

                cert_num.setText("暂未认证");
            } else if (cert_num_isShowFlg.equals("0") && !cert_a.equals("")
                    && !cert_a.equals("null")) {
                if (cert_a.length() < 4) {
                    cert_num.setText(cert_a);
                } else {

                    String cert_numpass = cert_a.substring(0,
                            cert_a.length() - 4) + "****";
                    cert_num.setText(cert_numpass);
                }
                certified_eye.setImageResource(R.drawable.eye_close);
            } else if (cert_num_isShowFlg.equals("0") && !cert_b.equals("")
                    && !cert_b.equals("null")) {
                if (cert_b.length() < 4) {
                    cert_num.setText(cert_b);
                } else {

                    String cert_numpass = cert_b.substring(0,
                            cert_b.length() - 4) + "****";
                    cert_num.setText(cert_numpass);
                }
                certified_eye.setImageResource(R.drawable.eye_close);
            } else if (cert_num_isShowFlg.equals("0") && !licence.equals("")
                    && !licence.equals("null")) {
                if (licence.length() < 4) {
                    cert_num.setText(licence);
                } else {

                    String cert_numpass = licence.substring(0,
                            licence.length() - 4) + "****";
                    cert_num.setText(cert_numpass);
                }
                certified_eye.setImageResource(R.drawable.eye_close);
            } else if (cert_num_isShowFlg.equals("1")) {

                if (!cert_a.equals("") && !cert_a.equals("null")) {
                    cert_num.setText(cert_a);
                } else if (!cert_b.equals("") && !cert_b.equals("null")) {
                    cert_num.setText(cert_b);
                } else {
                    cert_num.setText(licence);
                }
                certified_eye.setImageResource(R.drawable.eye_open);
            }
        }

        if (mobile_num_short.equals("0")) {

            String phone_numpass = mobile.substring(0, mobile.length() - 4)
                    + "****";
            expert_phonenum.setText(phone_numpass);

            expert_phonenum_eye.setImageResource(R.drawable.eye_close);

        } else if (mobile_num_short.equals("1")) {

            expert_phonenum.setText(mobile);
            expert_phonenum_eye.setImageResource(R.drawable.eye_open);
        }

        if (isShow_in_expertlist.equals("0")) {

            setting_show_text.setText("当前设置:首页的专家列表中不展示您的信息");
            show_toggle.setToggleOff();

        } else if (isShow_in_expertlist.equals("1")) {
            setting_show_text.setText("当前设置:首页的专家列表中展示您的信息");
            show_toggle.setToggleOn();

        }

        show_toggle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                eye_type = "3";
                if (isShow_in_expertlist.equals("0")) {

                    key_value.put("setValue", "1");

                } else if (isShow_in_expertlist.equals("1")) {

                    key_value.put("setValue", "0");

                }

                String PRODUCT_URL = IpConfig.getUri("setIsShowInExpertlist");
                dialog.show();
                setdata(PRODUCT_URL);

            }
        });
        e_micro_scan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                String url = IpConfig.getIp() + "user_id=" + user_id_encode +"&token="+token+ "&mod=getHomepageForExpert";


                String shareurl = IpConfig.getIp() + "user_id=" + user_id_encode +"&token="+token+ "&mod=getHomepageForExpert";
                Intent intent = new Intent();
                String img_url;
                if (TextUtils.isEmpty(avatar)) {
                    String site_url = IpConfig.getIp3();
                    img_url = site_url + "/images/homepage_share.jpg";
                } else {
                    img_url = avatar;
                }
                if (TextUtils.isEmpty(user_name)) {
                    intent.putExtra("share_title", "资深保险销售专家——保险人");
                    intent.putExtra("brief", "您好，我是保险人。愿意为您提供所有保险相关的服务。");
                } else {
                    intent.putExtra("share_title", "资深保险销售专家——" + user_name);
                    intent.putExtra("brief", "您好，我是" + user_name + "。愿意为您提供所有保险相关的服务。");
                }
                intent.putExtra("title", "我的微站");

                intent.putExtra("url", url);
                intent.putExtra("shareurl", shareurl);
                intent.putExtra("img_url", img_url);

                intent.setClass(MyMicroActivity.this, MicroShareWebActivity.class);
                startActivity(intent);


            }
        });

        // show_toggle.setOnToggleChanged(new OnToggleChanged() {
        //
        // @Override
        // public void onToggle(boolean on) {
        // // TODO Auto-generated method stub
        // if (on == true) {
        //
        // String PRODUCT_URL = IpConfig.getUri("setIsShowInExpertlist");
        //
        // setdata(PRODUCT_URL);
        // } else if (on == false) {
        //
        // String PRODUCT_URL = IpConfig.getUri("setIsShowInExpertlist");
        //
        // setdata(PRODUCT_URL);
        // }
        // }
        // });

        certified_eye.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                eye_type = "1";
                if (cert_num_isShowFlg.equals("0")) {

                    key_value.put("setValue", "1");

                } else if (cert_num_isShowFlg.equals("1")) {

                    key_value.put("setValue", "0");

                }
                dialog.show();
                String PRODUCT_URL = IpConfig.getUri("setIsShowCertNum");

                setdata(PRODUCT_URL);
            }
        });
        expert_phonenum_eye.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                eye_type = "2";
                if (mobile_num_short.equals("0")) {

                    key_value.put("setValue", "1");

                } else if (mobile_num_short.equals("1")) {

                    key_value.put("setValue", "0");

                }
                dialog.show();
                String PRODUCT_URL = IpConfig.getUri("setShortMobileNum");

                setdata(PRODUCT_URL);
            }
        });

        profile_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent();

                intent.setClass(MyMicroActivity.this,
                        Profile_EditActivity.class);

                MyMicroActivity.this.startActivity(intent);

            }
        });
        e_micro_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        if (user_name == null || user_name.equals("")
                || user_name.equals("null")) {

            expertname.setText(mobile);
        } else {
            expertname.setText(user_name);
        }

        if (company_name == null || company_name.equals("")
                || company_name.equals("null")) {

            expert_area.setText("暂未设定" + "|" + mobile_area);
        } else {
            expert_area.setText(company_name + "|" + mobile_area);
        }

        like_num.setText(good_count + "赞");

        // key_value.put("expert_user_id", id);

        // Log.d("55555",user_id+"777"+token+"7777"+id);

        personal_specialty();

        if (!(personal_context == null) && !personal_context.equals("")
                && !personal_context.equals("null")) {
            expert_context.setText(personal_context);
        }


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

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                String data = jsonObject.getString("data");

                                String errcode = jsonObject.getString("errcode");

                                map.put("errcode", errcode);
                                Log.d("44444", errcode);
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

    void personal_specialty() {
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

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        personal_specialty = sp3.getString("personal_specialty", "");
        personal_specialty();

        personal_context = sp3.getString("personal_context", "");

        if (personal_context.equals("") || personal_context.equals("null")) {

            expert_context.setText("如果您有保险需求，可以与我联系。很乐意为您服务，成为您家庭的保险专家！");

        } else {
            expert_context.setText(personal_context);
        }
    }



    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        // TODO Auto-generated method stub

    }
}
