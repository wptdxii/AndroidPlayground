package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.ModifyUserInfoEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.AuthAvatar;
import com.cloudhome.utils.CircleImage;
import com.cloudhome.utils.IpConfig;
import com.gghl.view.wheelcity.AddressData;
import com.gghl.view.wheelcity.OnWheelChangedListener;
import com.gghl.view.wheelcity.WheelView;
import com.gghl.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.gghl.view.wheelcity.adapters.ArrayWheelAdapter;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.zf.iosdialog.widget.ActionSheetDialog.SheetItemColor;
import com.zf.iosdialog.widget.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Call;

@SuppressWarnings("RedundantIfStatement")
@SuppressLint("HandlerLeak")
public class MyInfoActivity extends BaseActivity implements NetResultListener{

    private static final int PHOTO_REQUEST_CAMERA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";

    private Dialog pdialog;
    private Bitmap bitmap;
    private File tempFile;
    private CircleImage info_userIcon;
    private RelativeLayout iv_back;
    private TextView top_title;
    private ImageView iv_right;
    private TextView my_username;
    private ImageView mytruename_in;
    private TextView my_truename, my_truename2;
    private TextView my_nickname;
    private TextView my_company;
    private TextView my_area;
    private TextView my_sex;
    private TextView verify_title;
    private RelativeLayout info_usericon_rel;
    private RelativeLayout myinfo_rel3;
    private RelativeLayout myinfo_rel4;
    private RelativeLayout myinfo_rel5;
    private RelativeLayout myinfo_rel6;
    private RelativeLayout myinfo_rel7;
    private RelativeLayout myinfo_rel8;
    private RelativeLayout myinfo_rel9;
    private RelativeLayout myinfo_rel13;
    private RelativeLayout rl_user_desc_tag;//个人简介，个人标签
    private TextView tv_desc, tv_tag;
    private RelativeLayout rl_user_desc;
    private RelativeLayout rl_user_tag;
    private View view7, view8;
    private String type = "";
    private String avatar;
    private String nickname;
    private String username;
    private String truename;
    private String mobile_area;
    private String company_name;
    private String refer_name;
    private String refer_user_id;
    private String areaing;
    private String sex_code;
    private String sex_codeing = "";
    private String TAG = "MyInfoActivity";

    private String user_id;
    private String token;
    //个人简介，个人标签
    private String user_desc, user_tag;
    private TextView verify_state;
    private String user_state;
    private Uri uritempFile;
    private Map<String, String> key_value = new HashMap<String, String>();
    public static final int AUTH_AVATAR=1;
    private AuthAvatar authAvatar;

    private Handler errcode_handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;
            pdialog.dismiss();
            String status = data;
            if (status.equals("false")) {

                Toast.makeText(MyInfoActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };

    private String avatartype ="normal";

    private Handler state_handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            if (errcode.equals("0")) {

                String cert_a = data.get("cert_a");
                String cert_b = data.get("cert_b");
                String licence = data.get("licence");
                String assessment = data.get("assessment");
                String type = data.get("type");
                user_state = data.get("state");


                Verify_State();


                Editor editor = sp.edit();
                editor.putString("cert_a", cert_a);
                editor.putString("cert_b", cert_b);
                editor.putString("licence", licence);
                editor.putString("assessment", assessment);
                editor.putString("Login_TYPE", type);
                editor.putString("Login_CERT", user_state);
                editor.commit();


            } else {

                String errmsg = data.get("errmsg");

                //Toast.makeText(getActivity(), errmsg, Toast.LENGTH_LONG).show();
            }


            // m_d_content.setText(content);

        }

    };

    private Handler area_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");
            pdialog.dismiss();
            if (errcode.equals("0")) {
                my_area.setText(mobile_area);
                Editor editor2 = sp2.edit();
                editor2.putString("mobile_area", mobile_area);
                editor2.commit();
                Toast.makeText(MyInfoActivity.this, "修改地区成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyInfoActivity.this, "修改地区失败", Toast.LENGTH_SHORT).show();
            }


        }

    };

    private Handler sex_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");
            pdialog.dismiss();
            if (errcode.equals("0")) {

                if (sex_codeing.equals("01")) {
                    my_sex.setText("男");
                } else if (sex_codeing.equals("02")) {
                    my_sex.setText("女");
                } else {
                    my_sex.setText("男");
                }

                Editor editor2 = sp2.edit();
                editor2.putString("sex", sex_codeing);
                editor2.commit();

            } else {
                Toast.makeText(MyInfoActivity.this, "性别修改失败！",
                        Toast.LENGTH_SHORT).show();
            }

        }

    };

    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            final Map<String, String> data = (Map<String, String>) msg.obj;

            pdialog.dismiss();

            String errcode = data.get("errcode");

            if (errcode.equals("0")) {

                String avataring = data.get("avatar");



                Glide.with(MyInfoActivity.this)
                        .load(avataring)
//                        .placeholder(R.drawable.white)  //占位图 图片正在加载
                        .into(info_userIcon);


                Editor editor = sp.edit();

                editor.putString("avatar", avataring);

                editor.commit();
                Toast.makeText(MyInfoActivity.this,"修改头像成功", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new ModifyUserInfoEvent());
            } else {
                Toast.makeText(MyInfoActivity.this, "设置头像失败", Toast.LENGTH_SHORT)
                        .show();
            }

        }

    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myinfo);



        nickname = sp.getString("nickname", "");
        avatar = sp.getString("avatar", "");
        username = sp.getString("USER_NAME", "none");
        truename = sp.getString("truename", "");
        company_name = sp2.getString("company_name", "");
        mobile_area = sp2.getString("mobile_area", "");
        refer_name = sp2.getString("refer_name", "");
        refer_user_id=sp2.getString("refer_user_id", "");
        user_tag = sp3.getString("personal_specialty", "");
        user_desc = sp3.getString("personal_context", "");
        Log.i("简介和tag----------", "desc--" + user_desc + "---teg" + user_tag);
        type = sp.getString("Login_TYPE", "none");

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_state = sp.getString("Login_CERT", "none");
        init();
        initEvent();

    }

    private void init() {
        info_userIcon = (CircleImage) findViewById(R.id.info_userIcon);
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        top_title.setText("个人信息");
        iv_right.setVisibility(View.INVISIBLE);
        mytruename_in = (ImageView) findViewById(R.id.mytruename_in);
        my_truename = (TextView) findViewById(R.id.my_truename);
        my_truename2 = (TextView) findViewById(R.id.my_truename2);

        my_username = (TextView) findViewById(R.id.my_username);
        my_nickname = (TextView) findViewById(R.id.my_nickname);


        my_sex = (TextView) findViewById(R.id.my_sex);
        my_company = (TextView) findViewById(R.id.my_company);
        my_area = (TextView) findViewById(R.id.my_area);
        verify_title = (TextView) findViewById(R.id.verify_title);
        verify_state = (TextView) findViewById(R.id.verify_state);


        rl_user_desc_tag = (RelativeLayout) findViewById(R.id.rl_user_desc_tag);//个人简介，个人标签
        rl_user_desc = (RelativeLayout) findViewById(R.id.rl_user_desc);
        rl_user_tag = (RelativeLayout) findViewById(R.id.rl_user_tag);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        tv_tag = (TextView) findViewById(R.id.tv_tag);

        myinfo_rel3 = (RelativeLayout) findViewById(R.id.myinfo_rel3);
        myinfo_rel4 = (RelativeLayout) findViewById(R.id.myinfo_rel4);
        myinfo_rel5 = (RelativeLayout) findViewById(R.id.myinfo_rel5);
        myinfo_rel6 = (RelativeLayout) findViewById(R.id.myinfo_rel6);
        myinfo_rel7 = (RelativeLayout) findViewById(R.id.myinfo_rel7);
        myinfo_rel8 = (RelativeLayout) findViewById(R.id.myinfo_rel8);
        myinfo_rel9 = (RelativeLayout) findViewById(R.id.myinfo_rel9);
        myinfo_rel13 = (RelativeLayout) findViewById(R.id.myinfo_rel13);

        info_usericon_rel = (RelativeLayout) findViewById(R.id.info_usericon_rel);
        view7 = findViewById(R.id.view7);
        view8 = findViewById(R.id.view8);
        pdialog = new Dialog(this, R.style.progress_dialog);
        pdialog.setContentView(R.layout.progress_dialog);
        pdialog.setCancelable(true);
        pdialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

        key_value.put("token", token);
        key_value.put("user_id", user_id);
        Verify_State();
        //获取认证状态
        String url = IpConfig.getUri("getCertificate");
        setState(url);

    }

    private void initEvent() {
        if (type.equals("02")) {
            myinfo_rel8.setVisibility(View.GONE);
            myinfo_rel9.setVisibility(View.GONE);

        }
        my_username.setText(username);

        if (nickname.equals("") || nickname.equals("null")) {
            my_nickname.setText("请输入昵称");
        } else {
            my_nickname.setText(nickname);
        }

        sex_code = sp2.getString("sex", "");

        if (sex_code.equals("01")) {
            my_sex.setText("男");
        } else if (sex_code.equals("02")) {
            my_sex.setText("女");
        } else {
            my_sex.setText("男");
        }

        if (company_name.equals("") || company_name.equals("null")) {
            my_company.setText("暂未设定");
        } else {
            my_company.setText(company_name);
        }
        my_area.setText(mobile_area);

        if (truename.equals("") || truename.equals("null")) {

            my_truename.setText("保险人");

            my_truename2.setText("保险人");
        } else {
            my_truename.setText(truename);
            my_truename2.setText(truename);

        }

        if ((avatar.length() < 6)) {

            if (type.equals("02")) {
                info_userIcon.setImageResource(R.drawable.expert_head);
            } else {
                info_userIcon.setImageResource(R.drawable.expert_head);
            }
        } else {

            if (type.equals("02")) {


//
//                //显示图片的配置
//                DisplayImageOptions options = new DisplayImageOptions.Builder()
//                        .showImageOnLoading(R.drawable.white_bg)
//                        .showImageOnFail(R.drawable.expert_head)
//                        .cacheInMemory(true)
//                        .cacheOnDisk(true)
//                        .bitmapConfig(Bitmap.Config.RGB_565)
//                        .build();
//
//
//                ImageLoader.getInstance().loadImage(avatar, options,new ImageLoadingListener() {
//
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String imageUri, View view,
//                                                FailReason failReason) {
//                        avatartype= "02";
//                        info_userIcon.setImageResource(R.drawable.expert_head);
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        info_userIcon.setImageBitmap(loadedImage);
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String imageUri, View view) {
//
//                    }
//                });
                Glide
                        .with( MyInfoActivity.this ) // could be an issue!
                        .load( avatar)
                        .asBitmap()
                        .placeholder(R.drawable.white_bg)
                        .error(R.drawable.expert_head)
                        .into( new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {

                                info_userIcon.setImageBitmap(bitmap);
                                Log.d("88888","9999991");
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);

                                avatartype= "02";
                                info_userIcon.setImageResource(R.drawable.expert_head);

                            }
                        });






            } else {



                Glide
                        .with( MyInfoActivity.this ) // could be an issue!
                        .load( avatar)
                        .asBitmap()
                        .placeholder(R.drawable.white_bg)
                        .error(R.drawable.expert_head)
                        .into( new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {

                                info_userIcon.setImageBitmap(bitmap);
                                Log.d("88888","9999991");
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);

                                avatartype= "00";
                                info_userIcon.setImageResource(R.drawable.expert_head);

                            }
                        });



//
//                Glide
//                        .with(MyInfoActivity.this)
//                        .load(avatar)
//                        .centerCrop()
//                        .placeholder(R.drawable.white_bg)
//                        .error(R.drawable.expert_head)
//                        .crossFade()
//                        .into(new Target<GlideDrawable>() {
//                            @Override
//                            public void onLoadStarted(Drawable drawable) {
//
//
//                            }
//
//                            @Override
//                            public void onLoadFailed(Exception e, Drawable drawable) {
//                                avatartype= "00";
//                                info_userIcon.setImageResource(R.drawable.expert_head);
//
//                            }
//
//                            @Override
//                            public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                                info_userIcon.setImageDrawable(glideDrawable);
//
//                            }
//
//                            @Override
//                            public void onLoadCleared(Drawable drawable) {
//
//                            }
//
//                            @Override
//                            public void getSize(SizeReadyCallback sizeReadyCallback) {
//
//                            }
//
//                            @Override
//                            public void setRequest(Request request) {
//
//                            }
//
//                            @Override
//                            public Request getRequest() {
//                                return null;
//                            }
//
//                            @Override
//                            public void onStart() {
//
//                            }
//
//                            @Override
//                            public void onStop() {
//
//                            }
//
//                            @Override
//                            public void onDestroy() {
//
//                            }
//                        });
//


            }


        }

        info_userIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String[] img_url = new String[1];
                if (!(avatar.length() < 6)) {

                    //  img_url[0] = avatar;
                    if (avatartype.equals("02")) {
                        img_url[0] = "customer";

                    }else if(avatartype.equals("00")){
                        img_url[0] = "expert";
                    }else{
                        img_url[0] = avatar;
                    }

                } else {
                    if (type.equals("02")) {

                        img_url[0] = "customer";
                    } else {
                        img_url[0] = "expert";
                    }

                }

                Intent intent = new Intent();

                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, img_url);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);

                intent.setClass(MyInfoActivity.this, ImagePagerActivity.class);
                MyInfoActivity.this.startActivity(intent);

            }
        });

        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //个人简介
        rl_user_desc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MyInfoActivity.this, Profile_EditActivity.class);
                startActivity(intent);
            }
        });
        //个人标签
        rl_user_tag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MyInfoActivity.this, TagSelectActivity.class);
                startActivity(intent);
            }
        });

        info_usericon_rel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new ActionSheetDialog(MyInfoActivity.this)
                        .builder()
                        .setTitle("设置您的头像")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("拍摄新照片", SheetItemColor.Blue,
                                new OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {

                                        openCamera();

                                    }
                                })
                        .addSheetItem("从相册中选择", SheetItemColor.Blue,
                                new OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {

                                        openPhotos();

                                    }
                                }).show();
            }

        });
//        if ((!refer_name.equals("") && !refer_name.equals("null"))
//                || type.equals("00") || type.equals("03") || type.equals("04") || type.equals("05")) {

        if(!TextUtils.isEmpty(refer_user_id)){
            view7.setVisibility(View.GONE);
            myinfo_rel5.setVisibility(View.GONE);
        }




        myinfo_rel3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (user_state.equals("00") || user_state.equals("02")) {


                } else if (user_state.equals("01") || user_state.equals("03")) {


                    Intent intent = new Intent();
                    intent.setClass(MyInfoActivity.this, MyTruenameActivity.class);
                    MyInfoActivity.this.startActivity(intent);

                }
            }
        });


        myinfo_rel4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                if (user_state.equals("01")) {

                    Intent intent = new Intent();
                    intent.setClass(MyInfoActivity.this, VerifyMemberActivity.class);
                    MyInfoActivity.this.startActivity(intent);

                } else if (user_state.equals("02") || user_state.equals("00")) {


                    Intent intent = new Intent();
                    intent.setClass(MyInfoActivity.this, VerifiedInfoActivity.class);
                    MyInfoActivity.this.startActivity(intent);
                } else if (user_state.equals("03")) {


                    Intent intent = new Intent();
                    intent.setClass(MyInfoActivity.this, Verified_failure_InfoActivity.class);
                    MyInfoActivity.this.startActivity(intent);

                }

            }
        });

        myinfo_rel5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (refer_name.equals("") || refer_name.equals("null")) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(MyInfoActivity.this,
                            MyReferralActivity.class);
                    MyInfoActivity.this.startActivity(intent);
                }

            }
        });

        myinfo_rel6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();

                intent.setClass(MyInfoActivity.this,
                        MyInvitationCodeActivity.class);
                MyInfoActivity.this.startActivity(intent);
            }
        });
        myinfo_rel7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();

                intent.setClass(MyInfoActivity.this, MyNicknameActivity.class);
                MyInfoActivity.this.startActivity(intent);
            }
        });
        myinfo_rel8.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();

                intent.setClass(MyInfoActivity.this, MyCompanyActivity.class);
                MyInfoActivity.this.startActivity(intent);
            }
        });

        myinfo_rel9.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                View view = area_dialogm();
                final MyAlertDialog dialog1 = new MyAlertDialog(MyInfoActivity.this)
                        .builder()
                        .setTitle("请选择地区")
                        .setView(view)
                        .setNegativeButton("取消", new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                dialog1.setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mobile_area = areaing;


                        pdialog.show();
                        final String area_url = IpConfig.getUri("modifyMobileArea");

                        setarea_data(area_url);

                    }
                });
                dialog1.show();


            }
        });

        myinfo_rel13.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView p_dialog = (TextView) pdialog
                        .findViewById(R.id.id_tv_loadingmsg);
                p_dialog.setText("请稍后...");
                final String PRODUCT_URL = IpConfig.getUri("change_sex");
                new ActionSheetDialog(MyInfoActivity.this)
                        .builder()
                        .setTitle("性别")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("男", SheetItemColor.Blue,
                                new OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {

                                        pdialog.show();
                                        sex_codeing = "01";
                                        key_value.put("sex", "01");
                                        setsexdata(PRODUCT_URL);
                                    }
                                })
                        .addSheetItem("女", SheetItemColor.Blue,
                                new OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {

                                        pdialog.show();
                                        sex_codeing = "02";
                                        key_value.put("sex", "02");
                                        setsexdata(PRODUCT_URL);

                                    }
                                }).show();
            }

        });

    }






    private void setsexdata(String url) {

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

                                JSONObject jsonObject = new JSONObject(
                                        jsonString);
                                String data = jsonObject.getString("data");

                                String errcode = jsonObject
                                        .getString("errcode");

                                Log.d("44444", data);

                                map.put("errcode", errcode);

                                Message message = Message.obtain();

                                message.obj = map;

                                sex_handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void Verify_State() {

        // 已认证
        if (user_state.equals("00")) {

            verify_title.setText("展业资质");
            verify_state.setVisibility(View.VISIBLE);
            verify_state.setText("已认证");
            view8.setVisibility(View.VISIBLE);
            myinfo_rel6.setVisibility(View.VISIBLE);

            mytruename_in.setVisibility(View.GONE);
            my_truename.setVisibility(View.GONE);
            my_truename2.setVisibility(View.VISIBLE);
            //显示个人简介和个人标签
            rl_user_desc_tag.setVisibility(View.VISIBLE);
            //设置个人简介和标签的值
            setDescTag();

        }// 未认证
        else if (user_state.equals("01")) {

            verify_title.setText("资质认证");
            verify_state.setVisibility(View.INVISIBLE);
            myinfo_rel6.setVisibility(View.GONE);
            view8.setVisibility(View.GONE);
            mytruename_in.setVisibility(View.VISIBLE);
            my_truename.setVisibility(View.VISIBLE);
            my_truename2.setVisibility(View.GONE);
            //隐藏个人简介和个人标签
            rl_user_desc_tag.setVisibility(View.GONE);

        }// 认证中
        else if (user_state.equals("02")) {

            verify_title.setText("展业资质");
            verify_state.setVisibility(View.VISIBLE);
            verify_state.setText("认证中");
            myinfo_rel6.setVisibility(View.GONE);
            view8.setVisibility(View.GONE);
            mytruename_in.setVisibility(View.GONE);
            my_truename.setVisibility(View.GONE);
            my_truename2.setVisibility(View.VISIBLE);
            //隐藏个人简介和个人标签
            rl_user_desc_tag.setVisibility(View.GONE);
        }// 认证失败
        else if (user_state.equals("03")) {

            verify_title.setText("展业资质");
            verify_state.setVisibility(View.VISIBLE);
            verify_state.setText("认证失败");
            myinfo_rel6.setVisibility(View.GONE);
            view8.setVisibility(View.GONE);

            mytruename_in.setVisibility(View.VISIBLE);
            my_truename.setVisibility(View.VISIBLE);
            my_truename2.setVisibility(View.GONE);
            //隐藏个人简介和个人标签
            rl_user_desc_tag.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        nickname = sp.getString("nickname", "");
        type = sp.getString("Login_TYPE", "none");
        user_state = sp.getString("Login_CERT", "none");
        user_tag = sp3.getString("personal_specialty", "");
        user_desc = sp3.getString("personal_context", "");

        Verify_State();
        String url = IpConfig.getUri("getCertificate");

        setState(url);

        if (nickname.equals("") || nickname.equals("null")) {
            my_nickname.setText("请输入昵称");
        } else {
            my_nickname.setText(nickname);
        }
        truename = sp.getString("truename", "");

        if (truename.equals("") || truename.equals("null")) {

            my_truename.setText("保险人");
        } else {
            my_truename.setText(truename);
        }


        company_name = sp2.getString("company_name", "none");

        if (company_name.equals("") || company_name.equals("null")) {
            my_company.setText("暂未设定");
        } else {
            my_company.setText(company_name);
        }
        mobile_area = sp2.getString("mobile_area", "none");
        my_area.setText(mobile_area);

        refer_name = sp2.getString("refer_name", "none");
        refer_user_id=sp2.getString("refer_user_id", "");
        if (!TextUtils.isEmpty(refer_user_id)) {
            // my_referrer.setVisibility(View.VISIBLE);
            // myreferrer_in.setVisibility(View.GONE);
            // my_referrer.setText(refer_name);
            myinfo_rel5.setVisibility(View.GONE);

        }


    }


    private void setState(String url) {


        OkHttpUtils.post()
                .url(url)
                .params(key_value)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {

                        Log.e("error", "获取数据异常 ", e);

                        String status = "false";
                        Message message = Message.obtain();

                        message.obj = status;

                        errcode_handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(String response) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        try {

                            Log.d("9999", jsonString);
                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {

                                Map<String, String> map = new HashMap<String, String>();
                                JSONObject jsonObject = new JSONObject(jsonString);

                                JSONObject dataObject = jsonObject
                                        .getJSONObject("data");

                                String licence = dataObject.getString("licence");
                                String cert_b = dataObject.getString("cert_b");
                                String cert_a = dataObject.getString("cert_a");
                                String assessment = dataObject.getString("assessment");
                                String type = dataObject.getString("type");
                                String state = dataObject.getString("state");


                                String errmsg = jsonObject.getString("errmsg");
                                String errcode = jsonObject.getString("errcode");
                                Message message = Message.obtain();

                                map.put("errcode", errcode);
                                map.put("errmsg", errmsg);
                                map.put("licence", licence);
                                map.put("cert_b", cert_b);
                                map.put("cert_a", cert_a);
                                map.put("assessment", assessment);

                                map.put("type", type);
                                map.put("state", state);

                                message.obj = map;

                                state_handler.sendMessage(message);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    /**
     * 打开相册选择照片
     */
    private void openPhotos() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        String IMAGE_UNSPECIFIED = "image/jpg;image/png";
        intent.setType(IMAGE_UNSPECIFIED);
        // intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

    }

    /**
     * 打开照相机拍照
     */
    private void openCamera() {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    private void crop(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1); // 剪切的宽高比为1：2
//        intent.putExtra("aspectY", 2);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
       // 保存图片的宽和高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        //intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        //intent.putExtra("return-data", true);

        /**
         * 此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
         * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
         */
        //intent.putExtra("return-data", true);

        //uritempFile为Uri类变量，实例化uritempFile
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {


                Log.d("88888", "7777777");
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAMERA) {
            if (hasSdcard()) {
                Log.d("88888", "88888888");
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        PHOTO_FILE_NAME);
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(MyInfoActivity.this, "相机打开失败",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            try {
                if (data == null) {
                    Log.d("88888", "99999");
                }
                //bitmap = data.getParcelableExtra("data");


                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                // setCamImage(bitmap);
                // Bitmap SmallBitmap=getSmallBitmap(tempFile.toString());
                // setCamImage(SmallBitmap);

                savePic(bitmap);

                //旧php更换头像
//                String requestURL = IpConfig.getUri("setAvatar");
//                UploadImage(requestURL, tempFile.toString());
                //新java更换头像
                TextView p_dialog = (TextView) pdialog
                        .findViewById(R.id.id_tv_loadingmsg);
                p_dialog.setText("系统正在处理您的请求");
                pdialog.show();
                authAvatar=new AuthAvatar(this);
                authAvatar.execute(user_id,token,tempFile.toString(),"",AUTH_AVATAR);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void savePic(Bitmap b) {

        FileOutputStream fos = null;
        try {
            Log.i(TAG, "start savePic");

            // String sdpath ="/storage/sdcard1/";
            // File f = new File(sdpath ,"11.bmp");

            tempFile = new File(Environment.getExternalStorageDirectory()
                    + "/myimage/", String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            if (!tempFile.exists()) {
                File vDirPath = tempFile.getParentFile();
                vDirPath.mkdirs();
            } else {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }
            if (tempFile.exists()) {
                tempFile.delete();
            }

            fos = new FileOutputStream(tempFile);
            Log.i(TAG, "strFileName 1= " + tempFile.getPath());
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.i(TAG, "save pic OK!");
        } catch (FileNotFoundException e) {
            Log.i(TAG, "FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "IOException");
            e.printStackTrace();
        }
    }

    private void UploadImage(String url, String imagefile_str) {

        File file = new File(imagefile_str);

        Map<String, String> map = new HashMap<String, String>();
        map.put("token", token);
        map.put("user_id", user_id);

        TextView p_dialog = (TextView) pdialog
                .findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("系统正在处理您的请求");

        pdialog.show();
        OkHttpUtils.post().addFile("avatar", file.getName(), file)//
                .url(url).params(map).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {

                pdialog.dismiss();
                Toast.makeText(MyInfoActivity.this, "设置头像失败",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                String jsonString = response;
                Map<String, String> codemap = new HashMap<String, String>();
                try {

                    if (jsonString == null || jsonString.equals("")
                            || jsonString.equals("null")) {

                        codemap.put("errcode", "1020");
                    } else {
                        // Log.d("44444", jsonString);
                        JSONObject jsonObject = new JSONObject(
                                jsonString);

                        String errcode = jsonObject
                                .getString("errcode");
                        if (!errcode.equals("0")) {

                            codemap.put("errcode", errcode);

                        } else {

                            String data = jsonObject.getString("data");

                            JSONObject dataObject = new JSONObject(data);
                            String avataring = dataObject
                                    .getString("avatar");

                            codemap.put("errcode", errcode);
                            codemap.put("avatar", avataring);

                        }

                        Message message = Message.obtain();

                        message.obj = codemap;

                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    @SuppressLint("InflateParams")
    private View area_dialogm() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_country);
        country.setVisibleItems(1);
        country.setViewAdapter(new CountryAdapter(this));

        final String cities[][] = AddressData.CITIES;
        final WheelView city = (WheelView) contentView
                .findViewById(R.id.wheelcity_city);
        city.setVisibleItems(1);


        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateCities(city, cities, newValue);
                areaing = AddressData.PROVINCES[country.getCurrentItem()]
                        + AddressData.CITIES[country.getCurrentItem()][city
                        .getCurrentItem()];


            }
        });

        city.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                areaing = AddressData.PROVINCES[country.getCurrentItem()]
                        + AddressData.CITIES[country.getCurrentItem()][city
                        .getCurrentItem()];

            }
        });


        country.setCurrentItem(0);
        city.setCurrentItem(0);
        updateCities(city, cities, 0);
        areaing = AddressData.PROVINCES[country.getCurrentItem()]
                + AddressData.CITIES[country.getCurrentItem()][city
                .getCurrentItem()];
        return contentView;
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, cities[index]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    private void setarea_data(String url) {


        OkHttpUtils.post()//
                .url(url)//
                .addParams("mobile_area", mobile_area)
                .addParams("token", token)
                .addParams("user_id", user_id)
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

                            Log.d("44444", data);

                            map.put("errcode", errcode);

                            Message message = Message.obtain();

                            message.obj = map;

                            area_handler.sendMessage(message);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });


    }



    private void setDescTag() {
        if (TextUtils.isEmpty(user_desc)) {
            tv_desc.setText("未填写");
        } else {
            tv_desc.setText(user_desc);
        }

        if (TextUtils.isEmpty(user_tag)) {
            tv_tag.setText("未设置");
        } else {
            int i = 0;
            String split = "|";
            StringTokenizer token = new StringTokenizer(user_tag, split);
            String[] Ins_Str = new String[token.countTokens()];
            while (token.hasMoreTokens()) {
                Ins_Str[i] = token.nextToken();
                i++;
            }
            if (i > 0) {
                tv_tag.setText(Ins_Str[0]);
            }
        }

    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch(action){
            case AUTH_AVATAR:
                Message message = Message.obtain();
                Map<String, String> codemap = new HashMap<String, String>();
                if (flag == MyApplication.DATA_OK) {
                    String avatar=dataObj.toString();
                    codemap.put("errcode", "0");
                    codemap.put("avatar", avatar);
                    message.obj = codemap;
                    handler.sendMessage(message);
                } else if (flag == MyApplication.NET_ERROR) {
                    codemap.put("errcode", "1");
                    handler.sendMessage(message);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    codemap.put("errcode", "2");
                    handler.sendMessage(message);
                } else if (flag == MyApplication.JSON_ERROR) {
                    codemap.put("errcode", "3");
                    handler.sendMessage(message);
                } else if (flag == MyApplication.DATA_ERROR) {
                    codemap.put("errcode", "4");
                    handler.sendMessage(message);
                }

                break;
        }
    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] = AddressData.PROVINCES;

        /**
         * Constructor
         */
        CountryAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries[index];
        }
    }
}
