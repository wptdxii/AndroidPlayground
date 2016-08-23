package com.wptdxii.androidpractice.imageloader;

import android.content.Context;

import com.wptdxii.androidpractice.imageloader.glide.GlideImageLoaderStrategy;

/**
 * Created by wptdxii on 2016/8/20 0020.
 */
public class ImageLoader implements IImageLoaderStrategy{
    public static final int PIC_LARGE = 0;
    public static final int PIC_MEDIUM = 1;
    public static final int PIC_SMALL = 2;

    public static final int LOAD_STRATEGY_NORMAL = 0;
    public static final int LOAD_STRATEGY_ONLY_WIFI = 1;
    
    
    private IImageLoaderStrategy mStrategy;
    
    private ImageLoader() {
        mStrategy = new GlideImageLoaderStrategy();
    }


    private static class ImageLoaderUtilHolder {
        private static ImageLoader instance = new ImageLoader();
    }
    
    public static ImageLoader getInstance() {
        return ImageLoaderUtilHolder.instance;
    }
    @Override
    public void loadImage(Context context, ImageLoaderConfig imageLoaderConfig) {
        mStrategy.loadImage(context,imageLoaderConfig);
    }

    @Override
    public void loadCircleImage(Context context, ImageLoaderConfig imageLoaderConfig) {
        mStrategy.loadCircleImage(context,imageLoaderConfig);
    }
    
    public void setLoadImageStrategy(IImageLoaderStrategy strategy) {
        this.mStrategy = strategy;
    }
}
