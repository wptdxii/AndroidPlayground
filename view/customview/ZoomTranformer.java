package com.cloudhome.view.customview;


import android.view.View;

public class ZoomTranformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		final float scale = 1f + Math.abs(position);

		view.setAlpha(position < -1f || position > 1f ? 0f : 1f - (scale - 1f));

	}

}