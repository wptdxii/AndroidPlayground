package com.cloudhome.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cloudhome.BuildConfig;
import com.cloudhome.R;
import com.cloudhome.event.DisorderJumpEvent;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.utils.IpConfig;
import com.nui.multiphotopicker.adapter.ImagePublishAdapter;
import com.nui.multiphotopicker.model.ImageItem;
import com.nui.multiphotopicker.util.CustomConstants;
import com.nui.multiphotopicker.util.IntentConstants;
import com.umeng.analytics.MobclickAgent;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.zf.iosdialog.widget.ActionSheetDialog.SheetItemColor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class PolicyPictureActivity extends BaseActivity {
    public String Event_Service = "PolicyPictureActivity_Service";
    private GridView mGridView;
    private ImagePublishAdapter mAdapter;
    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
    SharedPreferences sp;
    private TextView tv_tips;
    private String user_id;
    private String token;
    private String actionUrl = "";
    SharedPreferences sp4;
    private int pagenum = 0;
    private RelativeLayout upload_rel = null;
    // 电话客服
    public static RelativeLayout ll_service_content;
    private RelativeLayout rl_left_bottom;
    public static boolean isShown;
    private ImageView iv_call_phone;
    private ImageView iv_press_to_pick_pic;
    private RelativeLayout rl_inner_rules;
    private TextView tv_upLoad_pictures;
    private Dialog dialog1;
    private AlertDialog dialog;

    private RelativeLayout iv_back;
    private TextView top_title;
    private ImageView iv_right;
    private View line;
    private String user_id_encode = "";

    final Handler timerhandler = new Handler();
    int runCount = 0;// 全局变量，用于判断是否是第一次执行
    Runnable runnable = new Runnable() {

        @Override
        public void run() {

            timerhandler.removeCallbacks(this);
            tv_tips.setVisibility(View.INVISIBLE);

        }

    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");
            Log.d("455454", "455445" + errcode);
            if (errcode.equals("0")) {
                dialog1.dismiss();
                mDataList.clear();
                mAdapter.notifyDataSetChanged();
                showSuccessDialog();

            } else {
                dialog1.dismiss();
                Toast.makeText(PolicyPictureActivity.this, "保单上传失败！",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pol_pic_main);
        sp = this.getSharedPreferences("userInfo", 0);
        sp4 = this.getSharedPreferences("ActivityInfo", 0);

        pagenum = sp4.getInt("page", 0);
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        user_id_encode = sp.getString("Login_UID_ENCODE", "");
        actionUrl = IpConfig.getUri("uploadPolicy") + "token=" + token
                + "&user_id=" + user_id_encode + "&";
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        line = findViewById(R.id.bottom_line);
        top_title.setText("保单上传");
        iv_right.setVisibility(View.INVISIBLE);
        line.setVisibility(View.GONE);

        initData();
        init();
        initEvent();

    }

    public void showSuccessDialog() {

        AlertDialog.Builder builder = new Builder(this);
        dialog = builder.create();
        View contentView = View.inflate(this, R.layout.dialog_upload_success,
                null);
        final TextView tv_content = (TextView) contentView
                .findViewById(R.id.tv_content);
        String content = "我们会尽快处理,处理完成的保单可在我的客户中查看。";
        tv_content.setText(content);
        final TextView tv_see_insurance = (TextView) contentView
                .findViewById(R.id.tv_see_insurance);
        final TextView tv_upload_again = (TextView) contentView
                .findViewById(R.id.tv_upload_again);
        tv_upload_again.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        tv_see_insurance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                EventBus.getDefault().post(new DisorderJumpEvent(1));
                Intent intent = new Intent(PolicyPictureActivity.this,
                        AllPageActivity.class);
                startActivity(intent);
            }
        });
        dialog.setView(contentView, 0, 0, 0, 0);
        dialog.show();

    }

    void init() {

        tv_tips = (TextView) findViewById(R.id.tv_tips);
        // album = (TextView)findViewById(R.id.album);
        // take_photo = (TextView)findViewById(R.id.take_photo);
        ll_service_content = (RelativeLayout) findViewById(R.id.rl_service_content);
        rl_left_bottom = (RelativeLayout) findViewById(R.id.rl_left_bottom);
        upload_rel = (RelativeLayout) super.findViewById(R.id.upload_rel);
        iv_call_phone = (ImageView) findViewById(R.id.iv_call_phone);
        iv_press_to_pick_pic = (ImageView) findViewById(R.id.iv_press_to_pick_pic);
        rl_inner_rules = (RelativeLayout) findViewById(R.id.rl_inner_rules);
        tv_upLoad_pictures = (TextView) findViewById(R.id.tv_upLoad_pictures);

        dialog1 = new Dialog(this, R.style.progress_dialog);
        dialog1.setContentView(R.layout.progress_dialog);
        dialog1.setCancelable(true);
        dialog1.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView p_dialog = (TextView) dialog1
                .findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("上传中，请稍后...");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveTempToPref();
    }

    private void saveTempToPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = JSON.toJSONString(mDataList);
        sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();

    }

    private void getTempFromPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
        if (!TextUtils.isEmpty(prefStr)) {
            List<ImageItem> tempImages = JSON.parseArray(prefStr,
                    ImageItem.class);
            mDataList = tempImages;
        }
    }

    private void removeTempFromPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
    }

    @SuppressWarnings("unchecked")
    private void initData() {
        getTempFromPref();
        List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
                .getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);


        if (incomingDataList != null) {

            mDataList.addAll(incomingDataList);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("tag", "onNewINtent执行了");
        setIntent(intent);


        initData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        Log.d("99999", "88888");

        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImagePublishAdapter(this, mDataList);
        mGridView.setAdapter(mAdapter);
        notifyDataChanged();
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        saveTempToPref();
    }

    void initEvent() {

        upload_rel.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int heightTop = upload_rel
                        .findViewById(R.id.rl_service_content).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                    if (y < heightTop) {
                        ll_service_content.setVisibility(View.INVISIBLE);
                        isShown = false;
                    }

                }

                return true;
            }
        });

        rl_left_bottom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (isShown) {
                    ll_service_content.setVisibility(View.INVISIBLE);
                    isShown = false;
                } else {
                    ll_service_content.setVisibility(View.VISIBLE);
                    isShown = true;
                }

                MobclickAgent.onEvent(PolicyPictureActivity.this, Event_Service);

            }
        });

        iv_call_phone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + "010-65886012"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        tv_upLoad_pictures.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                // uploadimage request = new uploadimage();

                final List<NameValuePair> imageNameViewPairs = new ArrayList<NameValuePair>();

                Map<String, String> files = new HashMap<String, String>();
                // String[] image =new String[9];
                final ImageItem[] itemArray = mDataList
                        .toArray(new ImageItem[mDataList.size()]);
                if (itemArray.length < 1) {
                    Toast.makeText(PolicyPictureActivity.this, "请上传保单照片1~5张",
                            Toast.LENGTH_LONG).show();
                } else {

                    dialog1.show();

                    for (int i = 1; i <= itemArray.length; i++) {// 遍历JSONArray

                        imageNameViewPairs.add(new BasicNameValuePair("policy_"
                                + i, itemArray[i - 1].sourcePath));
                        files.put("policy_" + i, itemArray[i - 1].sourcePath);
                        Log.d("4545", itemArray[i - 1].sourcePath);
                    }

                    post(actionUrl, imageNameViewPairs,
                            itemArray.length);

                }

            }
        });

        iv_press_to_pick_pic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ll_service_content.setVisibility(View.INVISIBLE);
                isShown = false;
                if (mDataList.size() >= 5) {
                    runCount = 0;
                    tv_tips.setVisibility(View.VISIBLE);

                    timerhandler.postDelayed(runnable, 2800);
                } else {
                    new ActionSheetDialog(PolicyPictureActivity.this)
                            .builder()
                            .setTitle("请选择")
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(true)
                            .addSheetItem("本地相册", SheetItemColor.Blue,
                                    new OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            String[] permissions = new String[]{
                                                    Manifest.permission.READ_EXTERNAL_STORAGE};
                                            PolicyPictureActivity.this
                                                    .requestPermissions(permissions,
                                                            new PermissionListener() {
                                                @Override
                                                public void onGranted() {
                                                    Intent intent = new Intent(
                                                            PolicyPictureActivity.this,
                                                            ImageBucketChooseActivity.class);
                                                    intent.putExtra(
                                                            IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                                                            getAvailableSize());
                                                    startActivity(intent);
                                                }

                                                @Override
                                                public void onDenied(String[] impermanentDeniedPermissions,
                                                                     String[] permanentDeniedPermissions) {
                                                    PolicyPictureActivity.this
                                                            .showRequestPermissionRationale(
                                                                    getString(R.string.msg_phontpicker_denied));
                                                }

                                                @Override
                                                public void onPermanentDenied(String[] permanentDeniedPermissions) {
                                                    PolicyPictureActivity.this
                                                            .showPermissionSettingDialog(
                                                                    getString(R.string.msg_photopicker_permanent_denied));
                                                }
                                            });


                                        }
                                    })
                            .addSheetItem("拍照", SheetItemColor.Blue,
                                    new OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {

                                            takePhoto();

                                        }
                                    }).show();
                }
            }
        });

        rl_inner_rules.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ll_service_content.setVisibility(View.INVISIBLE);
                isShown = false;
                showRules();
            }
        });

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImagePublishAdapter(this, mDataList);
        mGridView.setAdapter(mAdapter);
        // mGridView.setOnItemClickListener(new OnItemClickListener()
        // {
        // public void onItemClick(AdapterView<?> parent, View view,
        // int position, long id)
        // {
        // Log.i("gridview点击了------------------", position+"");
        // Intent intent = new Intent(PolicyPictureActivity.this,
        // ImageZoomActivity.class);
        // intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
        // (Serializable) mDataList);
        // intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION,
        // position);
        // startActivity(intent);
        //
        // }
        // }
        // );
        iv_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                mDataList.clear();


                finish();


            }
        });


    }

    private void showRules() {
        AlertDialog.Builder builder = new Builder(this);
        dialog = builder.create();
        View contentView = View.inflate(this,
                R.layout.dialog_show_insurance_rules, null);
        final TextView tv_content = (TextView) contentView
                .findViewById(R.id.tv_content);
        tv_content.setText(Html.fromHtml(getResources().getString(
                R.string.polpicstatement)));
        final TextView tv_ok = (TextView) contentView.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.setView(contentView, 0, 0, 0, 0);
        dialog.show();
    }

    private int getDataSize() {
        return mDataList == null ? 0 : mDataList.size();
    }

    private int getAvailableSize() {
        int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
        if (availSize >= 0) {
            return availSize;
        }
        return 0;
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File vFile = new File(getExternalFilesDir(null).getAbsolutePath()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        } else {
            if (vFile.exists()) {
                vFile.delete();
            }
        }

        path = vFile.getPath();
        //		Uri cameraUri = Uri.fromFile(vFile);
        Uri cameraUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1 && !TextUtils.isEmpty(path)) {
                    ImageItem item = new ImageItem();
                    item.sourcePath = path;
                    mDataList.add(item);
                }
                break;
        }
    }

    private void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public void post(String url, List<NameValuePair> nameValuePairs, int num) {

        switch (num) {
            case 1:
                File file = new File(nameValuePairs.get(0).getValue());

                OkHttpUtils.post()
                        .addFile("policy_1", file.getName(), file)

                        .url(url).addParams("file_count", num + "").build()
                        //
                        .connTimeOut(50000).readTimeOut(20000).writeTimeOut(20000)
                        .execute(new MyStringCallback());

                break;
            case 2:
                File file21 = new File(nameValuePairs.get(0).getValue());
                File file22 = new File(nameValuePairs.get(1).getValue());

                OkHttpUtils
                        .post()
                        //
                        .addFile("policy_1", file21.getName(), file21)
                        .addFile("policy_2", file22.getName(), file22)

                        .url(url).addParams("file_count", num + "").build()
                        //
                        .connTimeOut(50000).readTimeOut(20000).writeTimeOut(20000)
                        .execute(new MyStringCallback());

                break;

            case 3:

                File file31 = new File(nameValuePairs.get(0).getValue());
                File file32 = new File(nameValuePairs.get(1).getValue());
                File file33 = new File(nameValuePairs.get(2).getValue());

                OkHttpUtils
                        .post()
                        //
                        .addFile("policy_1", file31.getName(), file31)
                        .addFile("policy_2", file32.getName(), file32)
                        .addFile("policy_3", file33.getName(), file33).url(url)
                        .addParams("file_count", num + "").build()
                        //
                        .connTimeOut(50000).readTimeOut(20000).writeTimeOut(20000)
                        .execute(new MyStringCallback());

                break;

            case 4:

                File file41 = new File(nameValuePairs.get(0).getValue());
                File file42 = new File(nameValuePairs.get(1).getValue());
                File file43 = new File(nameValuePairs.get(2).getValue());
                File file44 = new File(nameValuePairs.get(3).getValue());

                OkHttpUtils
                        .post()
                        //
                        .addFile("policy_1", file41.getName(), file41)
                        .addFile("policy_2", file42.getName(), file42)
                        .addFile("policy_3", file43.getName(), file43)
                        .addFile("policy_4", file44.getName(), file44).url(url)
                        .addParams("file_count", num + "").build()
                        //
                        .connTimeOut(50000).readTimeOut(20000).writeTimeOut(20000)
                        .execute(new MyStringCallback());

                break;

            case 5:

                File file51 = new File(nameValuePairs.get(0).getValue());
                File file52 = new File(nameValuePairs.get(1).getValue());
                File file53 = new File(nameValuePairs.get(2).getValue());
                File file54 = new File(nameValuePairs.get(3).getValue());
                File file55 = new File(nameValuePairs.get(4).getValue());

                OkHttpUtils
                        .post()
                        //
                        .addFile("policy_1", file51.getName(), file51)
                        .addFile("policy_2", file52.getName(), file52)
                        .addFile("policy_3", file53.getName(), file53)
                        .addFile("policy_4", file54.getName(), file54)
                        .addFile("policy_5", file55.getName(), file55).url(url)
                        .addParams("file_count", num + "").build()
                        //
                        .connTimeOut(50000).readTimeOut(20000).writeTimeOut(20000)
                        .execute(new MyStringCallback());

                break;

        }

    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            dialog1.dismiss();
            Toast.makeText(PolicyPictureActivity.this, "保单上传失败！",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        @Override
        public void onResponse(String response, int id) {
            try {
                String jsonString = response;
                if (jsonString == null || jsonString.equals("")
                        || jsonString.equals("null")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("errcode", "1");
                    Message message = Message.obtain();
                    message.obj = map;
                    handler.sendMessage(message);
                } else {

                    Map<String, String> map = new HashMap<String, String>();

                    JSONObject jsonObject = new JSONObject(jsonString);
                    // String info =
                    // jsonObject.getString("info");
                    String data = jsonObject.getString("data");

                    Log.d("post", jsonObject + "");
                    String errcode = jsonObject.getString("errcode");

                    map.put("errcode", errcode);
                    Log.d("errcode", errcode);
                    Message message = Message.obtain();

                    message.obj = map;

                    handler.sendMessage(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            // TODO Auto-generated method stub

            mDataList.clear();
            if (pagenum == 0) {
                //				Intent intent = new Intent(PolicyPictureActivity.this,
                //						AllPageActivity.class);
                //
                //				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //				startActivity(intent);
                finish();
            } else {
                finish();
            }
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

}
