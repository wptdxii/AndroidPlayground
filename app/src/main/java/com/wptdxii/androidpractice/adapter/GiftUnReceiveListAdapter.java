package com.wptdxii.androidpractice.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.GiftInsuranceOrderListActivity;
import com.cloudhome.bean.GiftNotReceive;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.ArrayList;

public class GiftUnReceiveListAdapter extends BaseAdapter {
    private ArrayList<GiftNotReceive> dataMap;
    private LayoutInflater mInflater;
    private Context context;
    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");


    public GiftUnReceiveListAdapter(Context context, ArrayList<GiftNotReceive> dataMap) {
        super();
        this.dataMap = dataMap;
        mInflater = LayoutInflater.from(context);
        this.context = context;

    }

    @Override
    public int getCount() {
        return dataMap.size();
    }

    @Override
    public Object getItem(int position) {
        return dataMap.get(position);
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_gift_insurance_order_layout, null);
            holder.productimageurl = (ImageView) convertView.findViewById(R.id.productimageurl);
            holder.productname = (TextView) convertView.findViewById(R.id.productname);
            holder.share_tiem = (TextView) convertView.findViewById(R.id.share_tiem);
            holder.end_time = (TextView) convertView.findViewById(R.id.end_time);
            holder.share_again = (TextView) convertView.findViewById(R.id.share_again);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GiftNotReceive bean = dataMap.get(position);


        Glide.with(context)
                .load(bean.getProduct_icon())
                .centerCrop()
                .placeholder(R.drawable.white)  //占位图 图片正在加载
                .crossFade()
                .into( holder.productimageurl);




        holder.productname.setText(bean.getProduct_name());
        holder.share_tiem.setText(bean.getValid_time_begin());
        holder.end_time.setText(bean.getValid_time_end());
        holder.share_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("share", "dianjile");
                String share_title = bean.getShare_title();
                String share_url = bean.getShare_url();
                String brief = bean.getShare_desc();
                String img_url = bean.getShare_icon();
                initShare(share_title, share_url, brief, img_url);
                mController.openShare((GiftInsuranceOrderListActivity) context, false);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView productimageurl;
        TextView productname;
        TextView share_tiem;
        TextView end_time;
        TextView share_again;
    }

    private void initShare(String share_title, String share_url, String brief, String img_url) {


        String appID = context.getString(R.string.weixin_appid);
        String appSecret = context.getString(R.string.weixin_appsecret);
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context,
                appID, appSecret);
        wxHandler.addToSocialSDK();

        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(
                context, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((GiftInsuranceOrderListActivity) context, "1104898238", "2DdbISbzGWLBISzz");
        qqSsoHandler.addToSocialSDK();

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);

        // 设置QQ空间分享内容
        QQShareContent qqcontent = new QQShareContent();
        qqcontent.setShareContent(brief);
        qqcontent.setTargetUrl(share_url);
        qqcontent.setTitle(share_title);
        qqcontent.setShareImage(new UMImage(context, img_url));
        mController.setShareMedia(qqcontent);

        // 设置QQ空间分享内容
        SinaShareContent sinacontent = new SinaShareContent();
        // +"专业的保险导购平台、海量保险客户等你来！");
        sinacontent.setShareContent(share_title + share_url);
        sinacontent.setShareImage(new UMImage(context,
                img_url));
        mController.setShareMedia(sinacontent);

        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置分享文字
        weixinContent.setShareContent(brief);
        // 设置title
        weixinContent.setTitle(share_title);
        // 设置分享内容跳转URL
        Log.d("999999", share_url + "777");
        weixinContent.setTargetUrl(share_url);
        // 设置分享图片
        weixinContent.setShareImage(new UMImage(context,
                img_url));
        mController.setShareMedia(weixinContent);

        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(brief);

        // 设置朋友圈title
        circleMedia.setTitle(share_title);
        circleMedia.setTargetUrl(share_url);
        circleMedia.setShareImage(new UMImage(context,
                img_url));
        mController.setShareMedia(circleMedia);
    }

}
