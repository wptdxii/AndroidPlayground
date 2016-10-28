package com.wptdxii.playground.activity;

import android.app.Activity;
import android.content.Context;

import com.cloudhome.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

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
    final UMSocialService mController;

    public BaseUMShare(Context context, String shareTitle, String shareDesc, String shareUrl, String shareLogo) {
        this.context = context;
        this.shareTitle = shareTitle;
        this.shareDesc = shareDesc;
        this.shareUrl = shareUrl;
        this.shareLogo = shareLogo;
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        initShare();
    }
    public BaseUMShare(Context context, String shareTitle, String shareDesc, String shareUrl, int appShareLogo) {
        this.context = context;
        this.shareTitle = shareTitle;
        this.shareDesc = shareDesc;
        this.shareUrl = shareUrl;
        this.appShareLogo = appShareLogo;
        isAppShare=true;
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    }

    public void initShare() {
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
        // 微信
        String appID = context.getString(R.string.weixin_appid);
        String appSecret = context.getString(R.string.weixin_appsecret);
        UMWXHandler wxHandler = new UMWXHandler(context,appID, appSecret);
        wxHandler.showCompressToast(false);
        wxHandler.addToSocialSDK();
        // 微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.showCompressToast(false);
        wxCircleHandler.addToSocialSDK();
        // QQ
        String QQID = context.getString(R.string.qq_appid);
        String QQSecret = context.getString(R.string.qq_appsecret);
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity) context, QQID, QQSecret);
        qqSsoHandler.addToSocialSDK();
        // QQ空间
        QQShareContent qqcontent = new QQShareContent();
        qqcontent.setShareContent(shareDesc);
        qqcontent.setTargetUrl(shareUrl);
        qqcontent.setTitle(shareTitle);
        if(isAppShare){
            qqcontent.setShareImage(new UMImage(context, appShareLogo));
        }else{
            qqcontent.setShareImage(new UMImage(context, shareLogo));
        }
        mController.setShareMedia(qqcontent);

        // 新浪
        SinaShareContent sinacontent=new SinaShareContent();
        sinacontent.setShareContent(shareDesc + shareUrl);
        if(isAppShare){
            sinacontent.setShareImage(new UMImage(context, appShareLogo));
        }else{
            sinacontent.setShareImage(new UMImage(context, shareLogo));
        }
        mController.setShareMedia(sinacontent);

        // 微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(shareDesc); // 设置分享文字
        weixinContent.setTitle(shareTitle);// 设置title
        weixinContent.setTargetUrl(shareUrl);// 设置分享内容跳转URL
        if(isAppShare){
            weixinContent.setShareImage(new UMImage(context, appShareLogo));
        }else{
            weixinContent.setShareImage(new UMImage(context, shareLogo));
        }
        mController.setShareMedia(weixinContent);

        // 微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareDesc);
        circleMedia.setTitle(shareTitle);
        circleMedia.setTargetUrl(shareUrl);
        if(isAppShare){
            circleMedia.setShareImage(new UMImage(context, appShareLogo));
        }else{
            circleMedia.setShareImage(new UMImage(context, shareLogo));
        }
        mController.setShareMedia(circleMedia);
    }

    public void InviteShare(){
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE);
        // 微信
        String appID = context.getString(R.string.weixin_appid);
        String appSecret = context.getString(R.string.weixin_appsecret);
        UMWXHandler wxHandler = new UMWXHandler(context,appID, appSecret);
        wxHandler.showCompressToast(false);
        wxHandler.addToSocialSDK();
        // 微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.showCompressToast(false);
        wxCircleHandler.addToSocialSDK();
        // 微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(shareDesc); // 设置分享文字
        weixinContent.setTitle(shareTitle);// 设置title
        weixinContent.setTargetUrl(shareUrl);// 设置分享内容跳转URL
        if(isAppShare){
            weixinContent.setShareImage(new UMImage(context, appShareLogo));
        }else{
            weixinContent.setShareImage(new UMImage(context, shareLogo));
        }
        mController.setShareMedia(weixinContent);

        // 微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareDesc);
        circleMedia.setTitle(shareTitle);
        circleMedia.setTargetUrl(shareUrl);
        if(isAppShare){
            circleMedia.setShareImage(new UMImage(context, appShareLogo));
        }else{
            circleMedia.setShareImage(new UMImage(context, shareLogo));
        }
        mController.setShareMedia(circleMedia);
    }

    public void openShare(){
        mController.openShare((Activity) context, false);
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
