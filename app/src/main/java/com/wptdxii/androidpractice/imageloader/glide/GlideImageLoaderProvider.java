package com.wptdxii.androidpractice.imageloader.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.wptdxii.androidpractice.imageloader.IImageLoaderProvider;
import com.wptdxii.androidpractice.imageloader.ImageLoaderConfig;
import com.wptdxii.androidpractice.util.NetUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wptdxii on 2016/8/20 0020.
 */
public class GlideImageLoaderProvider implements IImageLoaderProvider {
//
//    @Override
//    public void loadImage(ImageLoaderConfig imageLoaderConfig) {
//        this.loadImage(App.getInstance(),imageLoaderConfig);
//    }

    @Override
    public void loadImage(Context context, ImageLoaderConfig imageLoaderConfig) {
        loadNormal(context,imageLoaderConfig);
        boolean flag = NetUtils.isWifiConnected(context);
        //如果不是在wifi下加载图片，加载缓存
        if (!flag) {
            loadCache(context, imageLoaderConfig);
            return;
        }

        int strategy = imageLoaderConfig.getWifiStrategy();
        if (strategy == ImageLoaderConfig.LOAD_STRATEGY_ONLY_WIFI) {
            int netType = NetUtils.getNetWorkType(context);
            if (netType == NetUtils.NETWORKTYPE_WIFI) {
                //如果是在wifi下才加载图片，并且当前网络是wifi,直接加载
                loadNormal(context, imageLoaderConfig);
            } else {
                //如果是在wifi下才加载图片，并且当前网络不是wifi，加载缓存
                loadCache(context, imageLoaderConfig);
            }
        } else {
            //如果不是在wifi下才加载图片
            loadNormal(context, imageLoaderConfig);
        } 
    }

   
    /**
     * load cache image with Glide
     * @param context
     * @param imageLoaderConfig
     */
    private void loadCache(Context context, ImageLoaderConfig imageLoaderConfig) {
        Glide.with(context).using(new StreamModelLoader<String>() {
            @Override
            public DataFetcher<InputStream> getResourceFetcher(final String model, int i, int i1) {
                return new DataFetcher<InputStream>() {
                    @Override
                    public InputStream loadData(Priority priority) throws Exception {
                        throw new IOException();
                    }

                    @Override
                    public void cleanup() {

                    }

                    @Override
                    public String getId() {
                        return model;
                    }

                    @Override
                    public void cancel() {

                    }
                };
            }
        })
                .load(imageLoaderConfig.getUrl())
                .placeholder(imageLoaderConfig.getPlaceHolder())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageLoaderConfig.getImgView());
    }

    /**
     * load image with Glide
     * @param context
     * @param imageLoaderConfig
     */
    private void loadNormal(Context context, ImageLoaderConfig imageLoaderConfig) {
        Glide.with(context)
                .load(imageLoaderConfig.getUrl())
                .centerCrop()
//                .placeholder(imageLoaderConfig.getPlaceHolder())
                .crossFade()
                .into(imageLoaderConfig.getImgView());
    }

    /**
     * 加载圆形图片
     * @param imageLoaderConfig
     */
//    @Override
//    public void loadCircleImage(ImageLoaderConfig imageLoaderConfig) {
//        this.loadCircleImage(App.getInstance(), imageLoaderConfig);
//    }

    /**
     * 加载圆形图片
     * @param context
     * @param imageLoaderConfig
     */
    @Override
    public void loadCircleImage(Context context, ImageLoaderConfig imageLoaderConfig) {
        Glide.with(context)
                .load(imageLoaderConfig.getUrl())
                .placeholder(imageLoaderConfig.getPlaceHolder())
                .transform(new GlideCircleTransform(context))
                .into(imageLoaderConfig.getImgView());
    }

   
}
