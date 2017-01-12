package com.cloudhome.view.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yangguangbaoxian on 2016/5/14.
 */
public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressBar";

    private float mMaxProgress = 100;

    private float mProgress = 0;

    private final int mCircleLineStrokeWidth = 10;

    private final int mTxtStrokeWidth = 2;

    // 画圆所在的距形区域
    private final RectF mRectF;

    private final Paint mPaint;

    private final Context mContext;


    private String mTxtHint;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;


    private int red =0;
    private int green =0 ;
    private int blue = 0;


    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(0xea, 0xea, 0xea));
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        // 位置
        mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
        mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
        mRectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
        mRectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y

        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, -90, 360, false, mPaint);

        //mPaint.setColor(Color.rgb(0xf8, 0x60, 0x30));

        mPaint.setColor(Color.rgb(red, green, blue));
       // mPaint.setColor(0xf86030);


        canvas.drawArc(mRectF, -90, (mProgress / mMaxProgress) * 360, false, mPaint);

        // 绘制进度文案显示
        mPaint.setStrokeWidth(mTxtStrokeWidth);
     //   String text = mProgress + "%";



        if (!TextUtils.isEmpty(mTxtHint)) {
            mPaint.setStrokeWidth(mTxtStrokeWidth);
            String text = mTxtHint;
            int    textHeight = height / 5;
            mPaint.setTextSize(textHeight);
            int  textWidth = (int) mPaint.measureText(text, 0, text.length());
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.rgb(0x66, 0x66, 0x66));

            canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + textHeight / 2, mPaint);

        }
    }

    public float getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        this.invalidate();
    }

    public void setProgressNotInUiThread(int progress) {
        this.mProgress = progress;
        this.postInvalidate();
    }


    public void setColor(int red,int green,int blue) {
        this.red =red ;
        this.green = green;
        this.blue =blue ;
    }

    public String getmTxtHint() {
        return mTxtHint;
    }

    public void setmTxtHint(String mTxtHint) {
        this.mTxtHint = mTxtHint;
    }
}