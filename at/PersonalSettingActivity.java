package com.cloudhome.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.BuildConfig;
import com.cloudhome.R;
import com.cloudhome.event.ModifyUserInfoEvent;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.network.AuthAvatar;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.Constants;
import com.cloudhome.view.customview.CircleImage;
import com.cloudhome.view.customview.SkipDialog;
import com.cloudhome.view.iosalertview.ActionSheetDialog;
import com.cloudhome.view.sortlistview.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cloudhome.R.id.iv_upload;

/**
 * 个人设定的 Activity
 */
public class PersonalSettingActivity extends BaseActivity implements NetResultListener {
    private static final int PHOTO_REQUEST_CAMERA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final String PHONT_FILE_CROP = "cropPicture.jpg";
    public static final int AUTH_AVATAR = 1;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(iv_upload)
    ImageView ivUpload;
    @BindView(R.id.iv_avatar)
    CircleImage ivAvatar;
    @BindView(R.id.et_user_name)
    ClearEditText etUserName;
    @BindView(R.id.btn_done)
    Button btnDone;
    private String mUserId;
    private String mToken;
    private Dialog mLoadingDialog;
    private SkipDialog mSkipDialog;
    private String mUserName;
    private File mTempFile;
    private File mCropedFile;
    private String mFilePath;

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, PersonalSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        tvTitle.setText(R.string.activity_personal_setting_title);
        rlShare.setVisibility(View.INVISIBLE);
        mUserId = sp.getString(Constants.SP.USER_ID, "");
        mToken = sp.getString(Constants.SP.TOKEN, "");
        mLoadingDialog = new Dialog(this, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.progress_dialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvDialog = (TextView) mLoadingDialog.findViewById(R.id.id_tv_loadingmsg);
        tvDialog.setText(R.string.msg_dialog_later);
    }

    @OnClick(R.id.rl_back)
    public void back() {
        showSkipDialog();
    }

    @OnClick(R.id.iv_upload)
    public void uploadAvatar() {
        showSelectPicture();
    }

    @OnClick(R.id.iv_avatar)
    public void changeAvatar() {
        showSelectPicture();
    }

    @OnClick(R.id.btn_done)
    public void settingCompleted() {
        if (ivAvatar.getVisibility() == View.INVISIBLE || ivAvatar.getVisibility() == View.GONE) {
            Toast.makeText(this, R.string.activity_personal_setting_no_avatar, Toast.LENGTH_SHORT).show();
            return;
        }
        mUserName = etUserName.getText().toString().trim();
        if (mTempFile != null) {
            mFilePath = mTempFile.toString();
        }
        if (TextUtils.isEmpty(mFilePath)) {
            Toast.makeText(this, R.string.activity_personal_setting_no_avatar, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mUserName)) {
            Toast.makeText(this, R.string.activity_personal_setting_without_name, Toast.LENGTH_SHORT).show();
        } else {
            mLoadingDialog.show();
            btnDone.setEnabled(false);
            AuthAvatar authAvatar = new AuthAvatar(this);
            authAvatar.execute(mUserId, mToken, mFilePath, mUserName, AUTH_AVATAR);
        }
    }

    @OnClick(R.id.btn_skip)
    public void skipSetting() {
        showSkipDialog();
    }

    /**
     * 弹出选择头像的dialog
     */
    private void showSelectPicture() {
        new ActionSheetDialog(PersonalSettingActivity.this)
                .builder()
                .setTitle(getString(R.string.activity_personal_setting_action_sheet_title))
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(getString(R.string.activity_personal_setting_take_photo),
                        ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                openCamera();
                            }
                        })
                .addSheetItem(getString(R.string.activity_personal_setting_select_photo),
                        ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                pickPhoto();
                            }
                        }).show();
    }

    /**
     * 选择照片
     */
    private void pickPhoto() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            PersonalSettingActivity.this
                    .requestPermissions(permissions,
                            new PermissionListener() {
                                @Override
                                public void onGranted() {
                                    openPhotos();
                                }

                                @Override
                                public void onDenied(String[] impermanentDenied,
                                                     String[] permanentDenied) {
                                    PersonalSettingActivity.this
                                            .showRequestPermissionRationale(
                                                    getString(R.string.msg_photopicker_denied));
                                }

                                @Override
                                public void onPermanentDenied(String[] permanentDeniedPermissions) {
                                    PersonalSettingActivity.this
                                            .showPermissionSettingDialog(
                                                    getString(R.string.msg_photopicker_permanent_denied));
                                }
                            });
        } else {
            openPhotos();
        }
    }

    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 打开照相机拍照
     */
    private void openCamera() {
        if (hasSdcard()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File imagePath = new File(getExternalFilesDir(null).getAbsolutePath(), PHOTO_FILE_NAME);
            if (imagePath.exists() && imagePath.isFile()) {
                imagePath.delete();
            }
            Uri cameraUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cameraUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID.concat(".provider"), imagePath);
            } else {
                cameraUri = Uri.fromFile(imagePath);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
        } else {
            Toast.makeText(this, R.string.activity_personal_setting_check_sd, Toast.LENGTH_SHORT).show();
        }
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
        if (mSkipDialog == null) {
            View contentView = View.inflate(this, R.layout.dialog_skip_upload_avatar, null);
            mSkipDialog = new SkipDialog(this, contentView, Common.dip2px(this, 320),
                    Common.dip2px(this, 150), R.style.qiandao_dialog);
            mSkipDialog.setCanceledOnTouchOutside(false);
            ButterKnife.bind(new SkipDialogViewHolder(), contentView);
        }
        mSkipDialog.show();
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void cropPicture(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        grantUriPermission("com.android.camera", uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("cropPicture", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 保存图片的宽和高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        //intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        mCropedFile = new File(getExternalFilesDir(null).getAbsolutePath(), PHONT_FILE_CROP);
        if (!mCropedFile.getParentFile().exists()) {
            mCropedFile.getParentFile().mkdir();
        }
        if (mCropedFile.exists() && mCropedFile.isFile()) {
            mCropedFile.delete();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCropedFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 保存头像
     * @param bitmap
     */
    private void saveAvatar(Bitmap bitmap) {
        FileOutputStream fos;
        try {
            mTempFile = new File(getExternalFilesDir(null).getAbsolutePath().concat("/myimage/"),
                    String.valueOf(System.currentTimeMillis()).concat(".jpg"));
            if (!mTempFile.exists()) {
                File vDirPath = mTempFile.getParentFile();
                vDirPath.mkdirs();
            } else {
                if (mTempFile.exists()) {
                    mTempFile.delete();
                }
            }
            if (mTempFile.exists()) {
                mTempFile.delete();
            }
            fos = new FileOutputStream(mTempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                cropPicture(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CAMERA) {
            if (hasSdcard()) {
                mTempFile = new File(getExternalFilesDir(null).getAbsolutePath(), PHOTO_FILE_NAME);
                if (mTempFile.exists() && mTempFile.isFile()) {
                    Uri uri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID.concat(".provider"), mTempFile);
                    cropPicture(uri);
                }
            } else {
                Toast.makeText(this, R.string.activity_personal_setting_camera_failure, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            try {
                if (mCropedFile.exists() && mCropedFile.isFile()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(Uri.fromFile(mCropedFile)));
                    saveAvatar(bitmap);
                    ivAvatar.setImageBitmap(bitmap);
                    ivAvatar.setVisibility(View.VISIBLE);
                    ivUpload.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case AUTH_AVATAR:
                mLoadingDialog.dismiss();
                Log.e(TAG, "ReceiveData: flag" + flag );
                if (flag == Constants.REQUEST.DATA_OK) {
                    String avatar = dataObj.toString();
                    SharedPreferences.Editor editor1 = sp.edit();
                    editor1.putString(Constants.SP.AVATAR, avatar);
                    editor1.putString(Constants.SP.TRUE_NAME, mUserName);
                    editor1.commit();
                    //改变用户信息数据
                    EventBus.getDefault().post(new ModifyUserInfoEvent());
                    Intent intent = new Intent(PersonalSettingActivity.this, ExclusiveCertActivity.class);
                    startActivity(intent);
                } else if (flag == Constants.REQUEST.NET_ERROR) {
                    Toast.makeText(PersonalSettingActivity.this, R.string.msg_net_retry, Toast.LENGTH_SHORT).show();
                } else if (flag == Constants.REQUEST.DATA_ERROR) {
                    String errMsg = dataObj.toString();
                    Toast.makeText(PersonalSettingActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    class SkipDialogViewHolder {
        @OnClick(R.id.tv_use_default)
        public void skip() {
            mSkipDialog.dismiss();
            etUserName.setText("");
            ivAvatar.setVisibility(View.INVISIBLE);
            ivUpload.setVisibility(View.VISIBLE);
            Resources res = getResources();
            Bitmap defaultBitmap = BitmapFactory.decodeResource(res, R.drawable.expert_head);
            saveAvatar(defaultBitmap);
            mUserName = "保险人";
            if (mTempFile != null) {
                mFilePath = mTempFile.toString();
            }
            mLoadingDialog.show();
            btnDone.setEnabled(false);
            AuthAvatar authAvatar = new AuthAvatar(PersonalSettingActivity.this);
            authAvatar.execute(mUserId, mToken, mFilePath, mUserName, AUTH_AVATAR);
        }

        @OnClick(R.id.tv_modify)
        public void modify() {
            mSkipDialog.dismiss();
        }
    }
}
