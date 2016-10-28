package com.wptdxii.playground.activity;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cloudhome.R;
import com.cloudhome.fragment.BaseFragment;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;


public class ImageDetailFragment extends BaseFragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);
		
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}
		});
		
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		if(mImageUrl.equals("expert")){
			
			mImageView.setImageResource(R.drawable.expert_head);
		}else if(mImageUrl.equals("customer")){
			mImageView.setImageResource(R.drawable.customer_head);
		}else{


			Glide
					.with( ImageDetailFragment.this ) // could be an issue!
					.load( mImageUrl)
					.asBitmap()
					.placeholder(R.drawable.white_bg)
					.into( new SimpleTarget<Bitmap>() {
						@Override
						public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {

							mImageView.setImageBitmap(bitmap);
							progressBar.setVisibility(View.GONE);
							mAttacher.update();
						}

						@Override
						public void onLoadFailed(Exception e, Drawable errorDrawable) {
							super.onLoadFailed(e, errorDrawable);

							String message = e.toString();


							Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
							progressBar.setVisibility(View.GONE);

						}

						@Override
						public void onLoadStarted(Drawable placeholder) {
							super.onLoadStarted(placeholder);

							progressBar.setVisibility(View.VISIBLE);


						}
					});




		}
		
		
	}

}
