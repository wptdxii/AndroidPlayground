package com.wptdxii.androidpractice.imageloader;

import android.widget.ImageView;

import com.wptdxii.androidpractice.R;

/**
 * 配置加载图片时的属性
 * Created by wptdxii on 2016/8/19 0019.
 *
 */
public class ImageLoaderConfig {
    private int type;  //类型 (大图，中图，小图)
    private String url; //需要解析的url
    private int placeHolder; //当没有成功加载的时候显示的图片
    private ImageView imgView; //ImageView的实例
    private int wifiStrategy;//加载策略，是否在wifi下才加载
    
    private ImageLoaderConfig(Builder builder) {
        this.type = builder.type;
        this.url = builder.url;
        this.placeHolder = builder.placeHolder;
        this.imgView = builder.imgView;
        this.wifiStrategy = builder.wifiStrategy;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public int getPlaceHolder() {
        return placeHolder;
    }

    public ImageView getImgView() {
        return imgView;
    }

    public int getWifiStrategy() {
        return wifiStrategy;
    }

    public static class Builder {
        private int type;
        private String url;
        private int placeHolder;
        private ImageView imgView;
        private int wifiStrategy;
        
        public Builder() {
            this.type = ImageLoader.PIC_SMALL;
            this.url = "";
            this.placeHolder = R.drawable.icon_load_default_img;
            this.imgView = null;
            this.wifiStrategy = ImageLoader.LOAD_STRATEGY_NORMAL;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder palceHolder(int palceHolder) {
            this.placeHolder = placeHolder;
            return this;
        }

        public Builder imgView(ImageView imgView) {
            this.imgView = imgView;
            return this;
        }

        public Builder wifiStrategy(int wifiStrategy) {
            this.wifiStrategy = wifiStrategy;
            return this;
        }
        
        public ImageLoaderConfig build() {
            return new ImageLoaderConfig(this);
        }
    }
}
