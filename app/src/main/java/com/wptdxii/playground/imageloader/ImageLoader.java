package com.wptdxii.playground.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.wptdxii.playground.App;

/**
 * 加载图片的工具类
 * Created by wptdxii on 2016/8/24 0024.
 * 
 */
public class ImageLoader {
    private ImageLoader() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 使用默认配置加载图片
     * 使用全局context
     * @param imageView
     * @param url
     */
    public static void loadImage(ImageView imageView, String url) {
        ImageLoaderConfig config = getImageLoaderConfig(imageView, url);
        loadImage(config);
    }

    /**
     * 使用默认配置加载图片
     * 使用当前context
     * @param context
     * @param imageView
     * @param url
     */
    public static void loadImage(Context context, ImageView imageView, String url) {

        ImageLoaderConfig config = getImageLoaderConfig(imageView, url);
        loadImage(context,config);
    }

    /**
     * 加载圆形图片
     * 使用默认配置加载图片
     * 使用全局context
     * @param imageView
     * @param url
     */
    public static void loadCircleImage(ImageView imageView, String url) {
        ImageLoaderConfig config = getImageLoaderConfig(imageView, url);
        loadCircleImage(config);
    }

    /**
     * 使用默认配置加载圆形图片
     * 使用当前context
     * @param context
     * @param imageView
     * @param url
     */
    public static void loadCircleImage(Context context, ImageView imageView, String url) {
        ImageLoaderConfig config = getImageLoaderConfig(imageView, url);
        loadCircleImage(context,config);
    }

    /**
     * 自定义加载圆形图片配置
     * 需要指定ImageView和URL
     * 使用全局context
     * @param imageLoaderConfig
     */
    public static void loadImage(ImageLoaderConfig imageLoaderConfig) {
        ImageLoaderProvider.getInstance().loadImage(App.getInstance(),imageLoaderConfig);
    }

    /**
     * 自定义加载圆形图片配置
     * 需要指定ImageView和URL
     * 使用当前context
     * @param context
     * @param imageLoaderConfig
     */
    public static void loadImage(Context context, ImageLoaderConfig imageLoaderConfig) {
        ImageLoaderProvider.getInstance().loadImage(context,imageLoaderConfig);
    }

    /**
     * 自定义加载圆形图片配置
     * 需要指定ImageView和URL
     * 使用全局context
     * @param imageLoaderConfig
     */
    public static void loadCircleImage(ImageLoaderConfig imageLoaderConfig) {
        ImageLoaderProvider.getInstance().loadCircleImage(App.getInstance(),imageLoaderConfig);
    }

    /**
     * 自定义加载圆形图片配置
     * 需要指定ImageView和URL
     * 使用全局context
     * @param context
     * @param imageLoaderConfig
     */
    public static void loadCircleImage(Context context,ImageLoaderConfig imageLoaderConfig) {
        ImageLoaderProvider.getInstance().loadCircleImage(context,imageLoaderConfig);
    }


    /**
     * 提供默认的配置
     * @param imageView
     * @param url
     * @return
     */
    private static ImageLoaderConfig getImageLoaderConfig(ImageView imageView, String url) {
        
        return new ImageLoaderConfig.Builder()
                .imgView(imageView)
                .url(url)
                .build();
    }
}
