package com.wptdxii.androidpractice.imageloader;

import android.content.Context;

/**
 * Created by wptdxii on 2016/8/20 0020.
 */
public interface IImageLoaderProvider {
    
    void loadImage(Context context, ImageLoaderConfig imageLoaderConfig);
    
    //void loadImage(ImageLoaderConfig imageLoaderConfig);
    
    void loadCircleImage(Context context, ImageLoaderConfig imageLoaderConfig);
    
//    void loadCircleImage(ImageLoaderConfig imageLoaderConfig);
}
