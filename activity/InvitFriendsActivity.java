package com.cloudhome.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class InvitFriendsActivity extends BaseActivity {

    private ImageView share_img, i_f_erweima, invit_friend_back,
            erweima_bg_img;
    private String erweima_url;
    boolean start = false;

    private String user_id;
    private String token;
    private Map<String, String> key_value = new HashMap<String, String>();
    boolean hasMeasured = false;

    private int width = 0, height = 0;
    private BaseUMShare share;
    private String Event_InviteFriend = "InvitFriendsActivity_InviteFriend";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invit_friend);
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        init();
        initEvent();
    }

    private void init() {

        //http://www.baokeyun.com/api/activity/index.php
        erweima_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx64445853f60fe3e4&redirect_uri=" + IpConfig.getIp3() + "/activity/index.php&response_type=code&scope=snsapi_base&state=preRec_" + user_id + "&token=" + token + "&connect_redirect=1#wechat_redirect";
        Log.d("77777", erweima_url);
        share_img = (ImageView) findViewById(R.id.share_img);
        i_f_erweima = (ImageView) findViewById(R.id.i_f_erweima);

        erweima_bg_img = (ImageView) findViewById(R.id.erweima_bg_img);
        invit_friend_back = (ImageView) findViewById(R.id.invit_friend_back);
        // 将字体文件保存在assets/fonts/目录下，创建Typeface对象

        String shareTitle = getString(R.string.invite_share_title);
        String shareDesc = getString(R.string.invite_share_desc);
        share = new BaseUMShare(this, shareTitle, shareDesc, erweima_url, R.drawable.icon_share_logo);
        share.initShare();
    }

    private void initEvent() {

        key_value.put("user_id", user_id);
        key_value.put("token", token);
        invit_friend_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();

            }
        });
        share_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                share.openShare();
                MobclickAgent.onEvent(InvitFriendsActivity.this, Event_InviteFriend);
            }

        });

        ViewTreeObserver vto = erweima_bg_img.getViewTreeObserver();

        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (!hasMeasured) {

                    int heighting = erweima_bg_img.getMeasuredHeight();
                    int widthing = erweima_bg_img.getMeasuredWidth();
                    // 获取到宽度和高度后，可用于计算

                    Log.d("88888", heighting + "33");
                    Log.d("88888", widthing + "33");


                    int width = widthing - 50;
                    int height = heighting - 50;
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) i_f_erweima
                            .getLayoutParams();

                    layoutParams.height = heighting - 40;
                    layoutParams.width = widthing - 40;

                    layoutParams.setMargins(20, 20, 20, 20);

                    i_f_erweima.setLayoutParams(layoutParams);


                    try {

                        if (erweima_url.length() > 6) {


                            start = true;
                            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                            BitMatrix bitMatrix = new QRCodeWriter().encode(erweima_url,
                                    BarcodeFormat.QR_CODE, width, height, hints);
                            int[] pixels = new int[width * height];
                            for (int y = 0; y < height; y++) {
                                for (int x = 0; x < width; x++) {
                                    if (bitMatrix.get(x, y)) {
                                        pixels[y * width + x] = 0xff000000;
                                    } else {
                                        pixels[y * width + x] = 0xffffffff;
                                    }
                                }
                            }

                            Bitmap bitmap = Bitmap.createBitmap(width, height,
                                    Bitmap.Config.ARGB_8888);
                            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
                            i_f_erweima.setImageBitmap(bitmap);

                            File f = new File(Environment.getExternalStorageDirectory()
                                    + "/page/" + System.currentTimeMillis() + ".jpg");
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(f);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);

                                fos.flush();
                                fos.close();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    hasMeasured = true;

                }
                return true;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
}
