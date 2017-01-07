package com.cloudhome.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.event.ModifyUserInfoEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.AuthAvatar;
import com.cloudhome.utils.CircleImage;
import com.cloudhome.utils.Common;
import com.cloudhome.view.customview.QianDaoDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zf.iosdialog.widget.ActionSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PersonalSettingActivity extends BaseActivity implements View.OnClickListener,NetResultListener{
    private RelativeLayout iv_back;
    private TextView top_title;
    private ImageView iv_right;
    private ImageView iv_upload;
    private ClearEditText et_user_name;
    private Button btn_done;
    private Button btn_skip;
    private CircleImage iv_head_circle;
    private static final int PHOTO_REQUEST_CAMERA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private Uri uritempFile;
    private File tempFile;
    private Bitmap bitmap;
    private AuthAvatar authAvatar;
    public static final int AUTH_AVATAR=1;
    private Dialog dialog;
    private String user_id;
    private String token;
    private String userName="";
    private String fileUrl="";
    private String avatar="";
    private String user_id_encode="";
    private boolean isUseDefault=false;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int msgWhat=msg.what;
            btn_done.setEnabled(true);
            switch (msgWhat){
                case 0:
                    dialog.dismiss();
                    String avatar= (String) msg.obj;
                    SharedPreferences.Editor editor1 = sp.edit();
                    editor1.putString("avatar", avatar);
                    editor1.putString("truename", userName);
                    Log.i(TAG, "ReceiveData: "+avatar);
                    Log.i(TAG, "ReceiveData: "+userName);
                    editor1.commit();
                    //改变用户信息数据
                    EventBus.getDefault().post(new ModifyUserInfoEvent());
                    Intent intent=new Intent(PersonalSettingActivity.this,SpecialVerifyActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    dialog.dismiss();
                    Toast.makeText(PersonalSettingActivity.this, "网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
                case 3:
                    dialog.dismiss();
                    break;
                case 4:
                    dialog.dismiss();
                    String codeMsg= (String) msg.obj;
                    Toast.makeText(PersonalSettingActivity.this, codeMsg, Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        initView();
        initEvent();
    }



    private void initView() {
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        top_title.setText("个人设定");
        iv_upload= (ImageView) findViewById(R.id.iv_upload);
        iv_head_circle= (CircleImage) findViewById(R.id.iv_head_circle);
        et_user_name= (ClearEditText) findViewById(R.id.et_user_name);
        btn_done= (Button) findViewById(R.id.btn_done);
        btn_skip= (Button) findViewById(R.id.btn_skip);
        iv_right.setVisibility(View.INVISIBLE);
        iv_back.setOnClickListener(this);
        iv_upload.setOnClickListener(this);
        iv_head_circle.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        btn_skip.setOnClickListener(this);
    }

    private void initEvent() {
        user_id_encode=sp.getString("Login_UID_ENCODE", "");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_back:
                showSkipDialog();
                break;
            case R.id.iv_upload:
                showSelectPicture();
                break;
            case R.id.iv_head_circle:
                showSelectPicture();
            break;
            case R.id.btn_done:
                if(iv_head_circle.getVisibility()==View.INVISIBLE||iv_head_circle.getVisibility()==View.GONE){
                    Toast.makeText(this, "您还没有上传头像", Toast.LENGTH_SHORT).show();
                    return;
                }
                userName=et_user_name.getText().toString().trim();
                if(tempFile!=null){
                    fileUrl=tempFile.toString();
                }
                if(TextUtils.isEmpty(fileUrl)){
                    Toast.makeText(this, "您还没有上传头像", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(userName)){
                    Toast.makeText(this, "您还没有设置姓名", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.show();
                    btn_done.setEnabled(false);
                    authAvatar=new AuthAvatar(this);
                    authAvatar.execute(user_id,token,fileUrl,userName,AUTH_AVATAR);
                }
                break;
            case R.id.btn_skip:
                showSkipDialog();
                break;
        }
    }

    /**
     * 弹出选择头像的dialog
     */
    private void showSelectPicture() {
        new ActionSheetDialog(PersonalSettingActivity.this)
                .builder()
                .setTitle("设置您的头像")
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍摄新照片", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                openCamera();
                            }
                        })
                .addSheetItem("从相册中选择", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                openPhotos();
                            }
                        }).show();
    }
    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
    /**
     * 打开照相机拍照
     */
    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(getExternalFilesDir(null).getAbsolutePath(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
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
     * 是否使用默认头像
     */
    private void showSkipDialog() {
        View contentView = View.inflate(this,R.layout.dialog_skip_upload_head,null);
        TextView tv_use_default= (TextView) contentView.findViewById(R.id.tv_use_default);
        TextView tv_use_new= (TextView) contentView.findViewById(R.id.tv_use_new);

        final QianDaoDialog builder = new QianDaoDialog(this,contentView, Common.dip2px(this,320),
                Common.dip2px(this,150),R.style.qiandao_dialog);
        builder.setCanceledOnTouchOutside(false);
        builder.show();
        tv_use_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_user_name.setText("");
                iv_head_circle.setVisibility(View.INVISIBLE);
                iv_upload.setVisibility(View.VISIBLE);
                Resources res = getResources();
                Bitmap defaultBmp = BitmapFactory.decodeResource(res, R.drawable.expert_head);
                savePic(defaultBmp);
                userName="保险人";
                if(tempFile!=null){
                    fileUrl=tempFile.toString();
                }
                dialog.show();
                btn_done.setEnabled(false);
                authAvatar=new AuthAvatar(PersonalSettingActivity.this);
                authAvatar.execute(user_id,token,fileUrl,userName,AUTH_AVATAR);
                builder.dismiss();
            }
        });
        tv_use_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    private void crop(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 保存图片的宽和高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("noFaceDetection", true);
        /**
         * 此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
         * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
         */
        uritempFile = Uri.parse("file://" + "/" + getExternalFilesDir(null).getAbsolutePath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private void savePic(Bitmap b) {
        FileOutputStream fos = null;
        try {
            Log.i("PersonalSettingActivity", "start savePic");
            // String sdpath ="/storage/sdcard1/";
            // File f = new File(sdpath ,"11.bmp");
            tempFile = new File(getExternalFilesDir(null).getAbsolutePath()
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
            Log.i("PersonalSettingActivity", "strFileName 1= " + tempFile.getPath());
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.i("PersonalSettingActivity", "save pic OK!");
        } catch (FileNotFoundException e) {
            Log.i("PersonalSettingActivity", "FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("PersonalSettingActivity", "IOException");
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CAMERA) {
            if (hasSdcard()) {
                tempFile = new File(getExternalFilesDir(null).getAbsolutePath(),
                        PHOTO_FILE_NAME);
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(PersonalSettingActivity.this, "相机打开失败",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            try {
                if (data == null) {
                }
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                savePic(bitmap);
                iv_head_circle.setImageBitmap(bitmap);
                iv_head_circle.setVisibility(View.VISIBLE);
                iv_upload.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch(action){
            case AUTH_AVATAR:
                Message message = Message.obtain();
                if (flag == MyApplication.DATA_OK) {
                    String avatar=dataObj.toString();
                    message.what=0;
                    message.obj = avatar;
                    handler.sendMessage(message);
                } else if (flag == MyApplication.NET_ERROR) {
                    message.what=1;
                    handler.sendMessage(message);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    message.what=2;
                    handler.sendMessage(message);
                } else if (flag == MyApplication.JSON_ERROR) {
                    message.what=3;
                    handler.sendMessage(message);
                } else if (flag == MyApplication.DATA_ERROR) {
                    String errMsg=dataObj.toString();
                    message.what=4;
                    message.obj = errMsg;
                    handler.sendMessage(message);
                }

                break;
        }
    }
}
