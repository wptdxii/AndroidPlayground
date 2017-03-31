package com.cloudhome.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.cloudhome.R;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.ShareBoardConfig;

/**
 * Created by bkyj-005 on 2016/9/23.
 */
public class BaseUMShare {
    private Context context;
    private String shareTitle;
    private String shareDesc;
    private String shareUrl;
    private String shareLogo;
    private int appShareLogo;
    private boolean isAppShare=false;
    private ShareAction shareAction;
    private UMShareListener umShareListener;
    private Activity mActivity;
    private ShareBoardConfig config;
    private static final int WE_CHAT=1;
    private static final int WE_CHAT_CIRCLE=2;
    private static final int QQ=3;

    public BaseUMShare(Context context, String shareTitle, String shareDesc, String shareUrl, String shareLogo) {
        this.context = context;
        this.shareTitle = shareTitle;
        this.shareDesc = shareDesc;
        this.shareUrl = shareUrl;
        this.shareLogo = shareLogo;
        mActivity= (Activity) context;
        initShare();
    }
    public BaseUMShare(Context context, String shareTitle, String shareDesc, String shareUrl, int appShareLogo) {
        this.context = context;
        this.shareTitle = shareTitle;
        this.shareDesc = shareDesc;
        this.shareUrl = shareUrl;
        this.appShareLogo = appShareLogo;
        mActivity= (Activity) context;
        isAppShare=true;
    }

    public BaseUMShare(Context context, String shareTitle, String shareDesc, String shareUrl,
                       String shareLogo, UMShareListener umShareListener) {
        this.context = context;
        this.shareTitle = shareTitle;
        this.shareDesc = shareDesc;
        this.shareUrl = shareUrl;
        this.shareLogo = shareLogo;
        this.umShareListener=umShareListener;
        mActivity= (Activity) context;
        initShare();
    }


    public void initShare() {

        shareAction=new ShareAction(mActivity);
        shareAction.setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE);
        config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        config.setCancelButtonVisibility(false);
        config.setTitleVisibility(false);
        Config.dialogSwitch = false;

        UMImage image;
        if(isAppShare){
            image = new UMImage(context, appShareLogo);//本地资源
        }else{
            image = new UMImage(context, shareLogo);//网络图片
        }
        if(umShareListener!=null){
            shareAction.setCallback(umShareListener);
        }

        shareAction.withText(shareDesc)
                .withMedia(image)
                .withTargetUrl(shareUrl)
                .withTitle(shareTitle);
    }

    public static void directShare(int platform,Context context, String shareTitle, String shareDesc, String shareUrl,
                                   String shareLogo){
        Config.dialogSwitch = false;
        UMImage  image = new UMImage(context, shareLogo);
        if(platform==WE_CHAT){
            new ShareAction((Activity) context).setPlatform(SHARE_MEDIA.WEIXIN)
                    .withText(shareDesc)
                    .withMedia(image)
                    .withTargetUrl(shareUrl)
                    .withTitle(shareTitle)
                    .share();
        }else if(platform==WE_CHAT_CIRCLE){
            new ShareAction((Activity) context).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withText(shareDesc)
                    .withMedia(image)
                    .withTargetUrl(shareUrl)
                    .withTitle(shareTitle)
                    .share();
        }else if(platform==QQ){
            new ShareAction((Activity) context).setPlatform(SHARE_MEDIA.QQ)
                    .withText(shareDesc)
                    .withMedia(image)
                    .withTargetUrl(shareUrl)
                    .withTitle(shareTitle)
                    .share();
        }

    }

    public static void sharePlatformsPicture(Context context,String imageUrl){
//        Config.dialogSwitch = false;
        ShareBoardConfig config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        config.setCancelButtonVisibility(false);
        config.setTitleVisibility(false);
        UMImage  image = new UMImage(context, imageUrl);
        new ShareAction((Activity) context).setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ)
                .withText("")
                .withMedia(image)
                .open(config);
    }

    public static void sharePicture(int platform,Context context, String picUrl){
        Dialog dialog = new Dialog(context, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");
        Config.dialogSwitch = true;
        dialog=dialog;
        UMImage  image = new UMImage(context, picUrl);
        if(platform==WE_CHAT){
            new ShareAction((Activity) context).setPlatform(SHARE_MEDIA.WEIXIN)
                    .withText("")
                    .withMedia(image)
                    .share();
        }else if(platform==WE_CHAT_CIRCLE){
            new ShareAction((Activity) context).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withText("")
                    .withMedia(image)
                    .share();
        }else if(platform==QQ){
            new ShareAction((Activity) context).setPlatform(SHARE_MEDIA.QQ)
                    .withText("")
                    .withMedia(image)
                    .share();
        }
    }

    public static void sharePictureWithListener(int platform,Context context, String picUrl,UMShareListener umShareListener){
        Config.dialogSwitch = false;
        UMImage  image = new UMImage(context, picUrl);
        if(platform==WE_CHAT){
            new ShareAction((Activity) context)
                    .setCallback(umShareListener)
                    .setPlatform(SHARE_MEDIA.WEIXIN)
                    .withText("")
                    .withMedia(image)
                    .share();
        }else if(platform==WE_CHAT_CIRCLE){
            new ShareAction((Activity) context)
                    .setCallback(umShareListener)
                    .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withText("")
                    .withMedia(image)
                    .share();
        }else if(platform==QQ){
            new ShareAction((Activity) context)
                    .setCallback(umShareListener)
                    .setPlatform(SHARE_MEDIA.QQ)
                    .withText("")
                    .withMedia(image)
                    .share();
        }
    }

    public void openShare(){
        if(shareAction!=null){
            shareAction.open(config);
        }

    }

    public static void  shareNotQQZone(Context context, String shareTitle, String shareDesc, String shareUrl,int shareLogo){
        Config.dialogSwitch = false;
        ShareBoardConfig config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        config.setCancelButtonVisibility(false);
        config.setTitleVisibility(false);
        UMImage  image = new UMImage(context, shareLogo);
        new ShareAction((Activity) context).setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ)
                .withText(shareDesc)
                .withTitle(shareTitle)
                .withTargetUrl(shareUrl)
                .withMedia(image)
                .open(config);
    }

    public void reSetShareContent(String shareTitle, String shareDesc, String shareUrl, String shareLogo){
        this.shareTitle = shareTitle;
        this.shareDesc = shareDesc;
        this.shareUrl = shareUrl;
        this.shareLogo = shareLogo;
        initShare();
    }
    public void reSetShareContent(String shareTitle, String shareDesc, String shareUrl, int appShareLogo){
        this.shareTitle = shareTitle;
        this.shareDesc = shareDesc;
        this.shareUrl = shareUrl;
        this.appShareLogo = appShareLogo;
        initShare();
    }
}
