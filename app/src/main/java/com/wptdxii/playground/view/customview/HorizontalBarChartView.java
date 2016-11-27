package com.wptdxii.playground.view.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.cloudhome.R;
import com.cloudhome.bean.BarChartBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wptdxii on 2016/11/21 0021.
 */

public class HorizontalBarChartView extends View {
    private int defCoordinatesLineWidth = dip2px(1);
    private int defCoordinatesTextSize = dip2px(12);
    private int defCoordinatesTextColor = Color.parseColor("#999999");
    private int defBackgroudColor = Color.parseColor("#ffffff");
    private int defXSeptalSize = dip2px(45);
    private int defYSeptalSize = dip2px(20);
    private int defXAxisTextSize = dip2px(12);
    private int defYAxisTextSize = dip2px(12);
    private int defXAxisTextColor = Color.parseColor("#666666");
    private int defYAxisTextColor = Color.parseColor("#666666");
    private int defBarHeight = dip2px(12);
    private int defChartTextColor = Color.parseColor("#333333");
    private int defChartTextSize = dip2px(12);
    private int defChartTextSeptalSize = dip2px(5);
    private int defSeptalCount = 6;
    private int defPaddingLeft = dip2px(30);
    private int defPaddingBottom = dip2px(15);
    private int defSeptalValue = 20;

    //TODO dip to px
    private int mCoordinatesLineWidth;
    private int mCoordinatesTextSize;
    private int mCoordinatesTextColor;
    private int mBackgroundColor;
    private int mXSeptalSize;
    private int mYSeptialSize;
    private int mXAxisTextSize;
    private int mYAxisTextSize;
    private int mXAxisTextColor;
    private int mYAxisTextColor;
    private int mBarHeight;
    private int mChartTextColor;
    private int mChartTextSize;
    private int mChartTextSeptalSize;
    private int mSeptalCount;

    private int mWidth;
    private int mHeight;
    private int mPaddingBottom;
    private int mPaddingLeft;

    private Paint coordinatesPaint;
    private Paint mXTextPaint;
    private Paint mYTextPaint;
    private Paint mSeptalPaint;
    private Paint mRectPaint;
    private Paint mChartValuePaint;
    private Rect textBound;

    private List<BarChartBean> mData;
    private int mSeptalValue;


    public HorizontalBarChartView(Context context) {
        this(context, null);

    }

    public HorizontalBarChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public HorizontalBarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HorizontalBarChartView, defStyleAttr, 0);
        mCoordinatesLineWidth = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_coordinatesLineWidth, defCoordinatesLineWidth);
        mCoordinatesTextSize = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_coordinatesTextSize, defCoordinatesTextSize);
        mCoordinatesTextColor = typedArray.getColor
                (R.styleable.HorizontalBarChartView_coordinatesTextColor, defCoordinatesTextColor);
        mBackgroundColor = typedArray.getColor
                (R.styleable.HorizontalBarChartView_barChartBackgroundColor, defBackgroudColor);
        mXSeptalSize = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_xSeptalSize, defXSeptalSize);
        mYSeptialSize = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_ySeptalSize, defYSeptalSize);
        mXAxisTextSize = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_xAxisTextSize, defXAxisTextSize);
        mYAxisTextSize = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_yAxisTextSize, defYAxisTextSize);
        mXAxisTextColor = typedArray.getColor
                (R.styleable.HorizontalBarChartView_xAxisTextColor, defXAxisTextColor);
        mYAxisTextColor = typedArray.getColor
                (R.styleable.HorizontalBarChartView_yAxisTextColor, defYAxisTextColor);
        mBarHeight = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_barChartHeight, defBarHeight);
        mChartTextColor = typedArray.getColor
                (R.styleable.HorizontalBarChartView_barChartTextColor, defChartTextColor);
        mChartTextSize = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_barChartTextSize, defChartTextSize);
        mChartTextSeptalSize = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_barChartTextSeptalSize, defChartTextSeptalSize);
        mSeptalCount = typedArray.getInteger
                (R.styleable.HorizontalBarChartView_septalCount, defSeptalCount);
        mPaddingLeft = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_paddingLeft, defPaddingLeft);
        mPaddingBottom = typedArray.getDimensionPixelSize
                (R.styleable.HorizontalBarChartView_paddingBottom, defPaddingBottom);
        mSeptalValue = typedArray.getInteger
                (R.styleable.HorizontalBarChartView_septalValue, defSeptalValue);

        typedArray.recycle();

        init();
    }

    private void init() {
        mData = new ArrayList<>();
        coordinatesPaint = new Paint();
        coordinatesPaint.setAntiAlias(true);
        coordinatesPaint.setColor(mCoordinatesTextColor);
        coordinatesPaint.setStrokeWidth(mCoordinatesLineWidth);
        coordinatesPaint.setStyle(Paint.Style.FILL);

        mSeptalPaint = new Paint();
        mSeptalPaint.setAntiAlias(true);
        mSeptalPaint.setColor(mCoordinatesTextColor);
        mSeptalPaint.setStrokeWidth(mCoordinatesLineWidth);
        mSeptalPaint.setStyle(Paint.Style.FILL);

        mXTextPaint = new Paint();
        mXTextPaint.setAntiAlias(true);
        mXTextPaint.setColor(mXAxisTextColor);
        mXTextPaint.setTextSize(mXAxisTextSize);
        mXTextPaint.setStyle(Paint.Style.STROKE);

        mYTextPaint = new Paint();
        mYTextPaint.setAntiAlias(true);
        mYTextPaint.setColor(mYAxisTextColor);
        mYTextPaint.setTextSize(mYAxisTextSize);
        mYTextPaint.setStyle(Paint.Style.STROKE);

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setTextSize(mChartTextSize);
        mRectPaint.setColor(Color.BLACK);
        mRectPaint.setStyle(Paint.Style.FILL);

        mChartValuePaint = new Paint();
        mChartValuePaint.setAntiAlias(true);
        mChartValuePaint.setTextSize(mChartTextSize);
        mChartValuePaint.setColor(mChartTextColor);
        mChartValuePaint.setStyle(Paint.Style.STROKE);

        textBound = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(mBackgroundColor);
        drawCoordinates(canvas);
        drawCoordinatesXvalues(canvas);
        drawCoordinatesYvalues(canvas);

    }


    private void drawCoordinatesYvalues(Canvas canvas) {
        if (mData != null && mData.size() > 0) {
            for (int i = 0; i < mData.size(); i++) {

                Rect rect = new Rect();
                rect.left = getPaddingLeft() + mPaddingLeft;
                rect.top = (int) (mHeight - getPaddingBottom() - mPaddingBottom
                        - (float) (i + 1) * (mYSeptialSize + mBarHeight));
                rect.right = (int) (getPaddingLeft() + mPaddingLeft +
                        (float) mData.get(i).getAmount() / mSeptalValue * mXSeptalSize);
                rect.bottom = (int) (mHeight - getPaddingBottom() - mPaddingBottom
                        - (float) (i + 1) * mYSeptialSize - (float) i * mBarHeight);
                mRectPaint.setColor(Color.parseColor(mData.get(i).getColor()));
                canvas.drawRect(rect, mRectPaint);


                Paint.FontMetrics textfontMetrics = mYTextPaint.getFontMetrics();
                float textBaseLine = (rect.bottom + rect.top - textfontMetrics.bottom - textfontMetrics.top) / 2;
                canvas.drawText(mData.get(i).getCategory(), getPaddingLeft(),
                        textBaseLine, mYTextPaint);


                Paint.FontMetrics chartValuefontMetrics = mChartValuePaint.getFontMetrics();
                float chartValueBaseLine = (rect.bottom + rect.top - chartValuefontMetrics.bottom - chartValuefontMetrics.top) / 2;
                canvas.drawText(String.valueOf(mData.get(i).getAmount()), rect.right + mChartTextSeptalSize,
                        chartValueBaseLine, mChartValuePaint);
            }
        }

    }

    private void drawCoordinatesXvalues(Canvas canvas) {
        for (int i = 0; i < mSeptalCount + 1; i++) {

            if (i != 0) {
                // x septal line
                canvas.drawLine(getPaddingLeft() + mPaddingLeft + (float) i * mXSeptalSize, getPaddingBottom(),
                        getPaddingLeft() + mPaddingLeft + (float) i * mXSeptalSize,
                        mHeight - getPaddingBottom() - getPaddingTop() - mPaddingBottom, mSeptalPaint);

            }

            String xValue = String.valueOf(i * mSeptalValue);
            mXTextPaint.getTextBounds(xValue, 0, xValue.length(), textBound);
            // -textBound.width()/2 是为了让字体居中与间断线
            canvas.drawText(xValue,
                    getPaddingLeft() + (float) (i * mXSeptalSize) + mPaddingLeft - (float) textBound.width() / 2,
                    mHeight - getPaddingBottom(), mXTextPaint);
        }
    }

    private void drawCoordinates(Canvas canvas) {

        // x coordinate
        canvas.drawLine(getPaddingLeft() + mPaddingLeft, mHeight - getPaddingBottom() - mPaddingBottom,
                mWidth - getPaddingRight() - getPaddingRight(), mHeight - getPaddingBottom() - mPaddingBottom,
                coordinatesPaint);

        // y coordinate
        canvas.drawLine(getPaddingLeft() + mPaddingLeft, getPaddingTop(), getPaddingLeft() + mPaddingLeft,
                mHeight - getPaddingBottom() - getPaddingTop() - mPaddingBottom, coordinatesPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 300;

        }

        if (heightSpecMode == MeasureSpec.EXACTLY) {
            mHeight = (mWidth / 5) * 4;
        } else {
            int defBarChartCount;
            if (mData.size() <= 3) {
                defBarChartCount = 3;
            } else {
                defBarChartCount = mData.size();
            }
            mHeight = (mBarHeight + mYSeptialSize) * defBarChartCount + mPaddingBottom + mYSeptialSize;
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * set x septal value  default is 20
     *
     * @param septalValue
     */
    public void setSeptalValue(int septalValue) {
        this.mSeptalValue = septalValue;
        this.invalidate();
    }

    /**
     * should set data first
     *
     * @param data
     */
    public void setBarChartData(List<BarChartBean> data) {
        this.mData = data;
        int max = getMaxValue(data);
        if (max > mSeptalValue * mSeptalCount) {
            expansionSeptalValue(max);
        }
        this.invalidate();
    }

    private void expansionSeptalValue(int value) {
        mSeptalValue += 5;
        int maxValue = mSeptalValue * mSeptalCount;
        if (maxValue < value) {
            expansionSeptalValue(value);
        }

    }

    private int getMaxValue(List<BarChartBean> data) {
        int temp = data.get(0).getAmount();
        for (int i = 0; i < data.size(); i++) {
            int value = data.get(i).getAmount();
            if (value > temp) {
                temp = value;
            }
        }
        return temp;
    }

    private int dip2px(float dp) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
