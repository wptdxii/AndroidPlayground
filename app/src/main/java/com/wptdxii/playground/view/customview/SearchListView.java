package com.wptdxii.playground.view.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cloudhome.utils.Common;

/**
 * Created by bkyj-005 on 2016/8/26.
 */
public class SearchListView extends View {
    OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    int choose = -1;
    Paint paint = new Paint();
    boolean showBkg = false;
    private Context context;

    public SearchListView(Context context) {
        super(context);
        this.context = context;
    }

    public SearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SearchListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBkg) {
            //背景颜色
            canvas.drawColor(Color.parseColor("#1E000000"));
        }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / b.length;
        for (int i = 0; i < b.length; i++) {
            //            paint.setColor(Color.parseColor("#FF4987"));
            //字体颜色
            paint.setColor(Color.parseColor("#4C4C4C"));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextSize(Common.dip2px(context, 12));
            // /paint.set
            paint.setAntiAlias(true);
            if (i == choose) {
                //选中的字体颜色
                //                paint.setColor(Color.parseColor("#DA5749"));
                paint.setColor(Color.parseColor("#0091FC"));
                paint.setFakeBoldText(true);
            }
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < b.length) {
                        listener.onTouchingLetterChanged(b[c]);
                        choose = c;
                        invalidate();
                    }

                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < b.length) {
                        listener.onTouchingLetterChanged(b[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}
