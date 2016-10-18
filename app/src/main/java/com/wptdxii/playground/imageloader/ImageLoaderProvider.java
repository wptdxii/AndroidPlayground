package com.wptdxii.playground.imageloader;

import android.content.Context;

import com.wptdxii.playground.imageloader.glide.GlideImageLoaderStrategy;

/**
 * Created by wptdxii on 2016/8/20 0020.
 */
public class ImageLoaderProvider implements IImageLoaderStrategy {
    private IImageLoaderStrategy mStrategy;

    private ImageLoaderProvider() {
        //当需要更改图片加载框架时直接在这里修改
        mStrategy = new GlideImageLoaderStrategy();
    }


    private static class ImageLoaderProviderHolder {
        private static ImageLoaderProvider instance = new ImageLoaderProvider();
    }

    public static ImageLoaderProvider getInstance() {
        return ImageLoaderProviderHolder.instance;
    }

    @Override
    public void loadImage(Context context, ImageLoaderConfig imageLoaderConfig) {
        mStrategy.loadImage(context, imageLoaderConfig);
    }

    @Override
    public void loadCircleImage(Context context, ImageLoaderConfig imageLoaderConfig) {
        mStrategy.loadCircleImage(context, imageLoaderConfig);
    }
    
    public void setLoadImageStrategy(IImageLoaderStrategy strategy) {
        this.mStrategy = strategy;
    }
}
