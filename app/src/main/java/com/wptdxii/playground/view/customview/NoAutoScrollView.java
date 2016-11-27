package com.wptdxii.playground.view.customview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class NoAutoScrollView extends ScrollView {

	public NoAutoScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public NoAutoScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public NoAutoScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
		// TODO Auto-generated method stub
		return 0;
	}
}
