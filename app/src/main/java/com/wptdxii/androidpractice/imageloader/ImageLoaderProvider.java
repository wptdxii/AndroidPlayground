package com.wptdxii.androidpractice.imageloader;

import android.content.Context;

import com.wptdxii.androidpractice.imageloader.glide.GlideImageLoaderProvider;

/**
 * Created by wptdxii on 2016/8/20 0020.
 */
public class ImageLoaderProvider implements IImageLoaderProvider {
    private IImageLoaderProvider mProvider;

    private ImageLoaderProvider() {
        //当需要更改图片加载框架时直接在这里修改
        mProvider = new GlideImageLoaderProvider();
    }


    private static class ImageLoaderUtilHolder {
        private static ImageLoaderProvider instance = new ImageLoaderProvider();
    }

    public static ImageLoaderProvider getInstance() {
        return ImageLoaderUtilHolder.instance;
    }

    @Override
    public void loadImage(Context context, ImageLoaderConfig imageLoaderConfig) {
        mProvider.loadImage(context, imageLoaderConfig);
    }

//    @Override
//    public void loadImage(ImageLoaderConfig imageLoaderConfig) {
//        mProvider.loadImage(imageLoaderConfig);
//    }

    @Override
    public void loadCircleImage(Context context, ImageLoaderConfig imageLoaderConfig) {
        mProvider.loadCircleImage(context, imageLoaderConfig);
    }

//    @Override
//    public void loadCircleImage(ImageLoaderConfig imageLoaderConfig) {
//        mProvider.loadCircleImage(imageLoaderConfig);
//    }

    public void setLoadImageStrategy(IImageLoaderProvider strategy) {
        this.mProvider = strategy;
    }
}
