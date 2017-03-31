package com.wptdxii.playground.imageloader;

import android.content.Context;

/**
 * 定义加载图片策略的接口
 * 使用不同的图片库，需要实现该接口
 * Created by wptdxii on 2016/8/20 0020.
 *
 */
public interface IImageLoaderStrategy {
    
    void loadImage(Context context, ImageLoaderConfig imageLoaderConfig);
    
    void loadCircleImage(Context context, ImageLoaderConfig imageLoaderConfig);
}
