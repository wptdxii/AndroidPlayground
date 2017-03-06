package com.cloudhome.activity;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cloudhome.R;
public class ImagePagerActivity extends BaseActivity {
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    private static final String STATE_POSITION = "STATE_POSITION";
    private ImageView mImageView;
    private FrameLayout fl_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_detail_pager);

        String[] urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        String mImageUrl=urls[0];
        fl_back= (FrameLayout) findViewById(R.id.fl_back);
        mImageView = (ImageView)findViewById(R.id.image);
        if(mImageUrl.equals("expert")){

            mImageView.setImageResource(R.drawable.expert_head);
        }else if(mImageUrl.equals("customer")){
            mImageView.setImageResource(R.drawable.expert_head);
        }else{
            Glide.with( ImagePagerActivity.this ) // could be an issue!
                    .load( mImageUrl)
                    .asBitmap()
                    .placeholder(R.drawable.white_bg)
                    .into( new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                            mImageView.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            String message = e.toString();
                        }

                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                        }
                    });

        }

        fl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

}