package com.wptdxii.playground.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScollview extends ScrollView {

	private float xDistance, yDistance, xLast, yLast;

	private ScrollBottomListener scrollBottomListener; 
	private ScrollUPListener scrollUPListener; 
	
	public MyScollview(Context context) {
		super(context);
	}

	public MyScollview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyScollview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (t + getHeight() >= computeVerticalScrollRange()-20) {
			// ScrollView滑动到底部了
			scrollBottomListener.scrollBottom();
		}else{
			scrollUPListener.scrollUP();
		}
	}

	public void setScrollBottomListener(
			ScrollBottomListener scrollBottomListener) {
		this.scrollBottomListener = scrollBottomListener;
	}

	public interface ScrollBottomListener {
		public void scrollBottom();
	}
	
	public void setScrollUPListener(
			ScrollUPListener scrollUPListener) {
		this.scrollUPListener = scrollUPListener;
	}

	public interface ScrollUPListener {
		public void scrollUP();
	}
}