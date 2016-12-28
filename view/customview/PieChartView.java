package com.cloudhome.view.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.cloudhome.R;
import com.cloudhome.bean.PieChartBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wptdxii on 2016/11/23 0023.
 */

public class PieChartView extends View {
    private int defRadius = dip2px(55);
    private int defLetterTextSize = dip2px(12);
    private int defLetterTextColor = Color.parseColor("#999999");
    private int defDigitTextSize = dip2px(12);
    private int defDigitTextColor = Color.parseColor("#333333");
    private int defBackgroudColor = Color.parseColor("#ffffff");
    private int defLabelPaddingRight = dip2px(120);
    private int defLabelTextSeptal = dip2px(12);
    private int defLabelSize = dip2px(12);
    private int defLabelItemSeptal = dip2px(15);
    private int defEmptyColor = Color.parseColor("#028be6");


    private int mRadius;
    private int mLetterTextSize;
    private int mLetterTextColor;
    private int mDigitTextSize;
    private int mDigitTextColor;
    private int mBackgroundColor;
    private int mLabelPaddingRight;
    private int mLabelTextSeptal;
    private int mLabelSize;
    private int mLabelItemSeptal;
    private int mEmptyColor;

    private int mWidth;
    private int mHeight;

    private Paint arcPaint;
    private Paint labelTextPaint;
    private Paint labelNumPaint;
    private Paint emptyPaint;
    private Rect textBound;


    private List<PieChartBean> mData;
    private float[] scaledValues;

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.PieChartView, defStyleAttr, 0);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.PieChartView_radius, defRadius);
        mLetterTextSize = typedArray.getDimensionPixelSize
                (R.styleable.PieChartView_letterTextSize, defLetterTextSize);
        mLetterTextColor = typedArray.getColor
                (R.styleable.PieChartView_letterTextColor, defLetterTextColor);
        mDigitTextSize = typedArray.getDimensionPixelSize
                (R.styleable.PieChartView_digitTextSize, defDigitTextSize);
        mDigitTextColor = typedArray.getColor
                (R.styleable.PieChartView_digitTextColor, defDigitTextColor);
        mBackgroundColor = typedArray.getColor
                (R.styleable.PieChartView_pieChartBackgroundColor, defBackgroudColor);
        mLabelPaddingRight = typedArray.getDimensionPixelSize
                (R.styleable.PieChartView_labelPaddingRight, defLabelPaddingRight);
        mLabelTextSeptal = typedArray.getDimensionPixelSize
                (R.styleable.PieChartView_labelTextSeptal, defLabelTextSeptal);
        mLabelItemSeptal = typedArray.getDimensionPixelSize
                (R.styleable.PieChartView_labelItemSeptal, defLabelItemSeptal);
        mLabelSize = typedArray.getDimensionPixelSize
                (R.styleable.PieChartView_labelSize, defLabelSize);
        mEmptyColor = typedArray.getColor(R.styleable.PieChartView_emptyColor, defEmptyColor);

        typedArray.recycle();

        init();

    }

    private void init() {
        mData = new ArrayList<>();

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.FILL);

        labelTextPaint = new Paint();
        labelTextPaint.setAntiAlias(true);
        labelTextPaint.setStyle(Paint.Style.STROKE);
        labelTextPaint.setColor(mLetterTextColor);
        labelTextPaint.setTextSize(mLetterTextSize);

        labelNumPaint = new Paint();
        labelNumPaint.setAntiAlias(true);
        labelNumPaint.setStyle(Paint.Style.STROKE);
        labelNumPaint.setColor(mDigitTextColor);
        labelNumPaint.setTextSize(mDigitTextSize);

        emptyPaint = new Paint();
        emptyPaint.setAntiAlias(true);
        emptyPaint.setStyle(Paint.Style.FILL);
        emptyPaint.setColor(mEmptyColor);

        textBound = new Rect();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(mBackgroundColor);
        drawArc(canvas);
        drawLabel(canvas);
    }


    private void drawLabel(Canvas canvas) {
        if (mData != null && mData.size() > 0) {
            for (int i = 0; i < mData.size(); i++) {
                RectF rect = new RectF();
                rect.left = mWidth - getPaddingLeft() - mLabelPaddingRight;
                rect.top = getPaddingTop() + i * (mLabelItemSeptal + mLabelItemSeptal);
                rect.right = mWidth - getPaddingLeft() - mLabelPaddingRight + mLabelSize;
                rect.bottom = getPaddingTop() + mLabelSize + i * (mLabelItemSeptal + mLabelItemSeptal);
                arcPaint.setColor(Color.parseColor(mData.get(i).getColor()));
                canvas.drawRoundRect(rect, 4, 4, arcPaint);


                Paint.FontMetrics fontMetrics = labelTextPaint.getFontMetrics();
                float baseLine = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                canvas.drawText(mData.get(i).getCategory(), rect.right + mLabelTextSeptal,
                        baseLine, labelTextPaint);

                labelNumPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(String.valueOf(mData.get(i).getAmount()), mWidth - getPaddingRight(),
                        baseLine, labelNumPaint);

            }
        }
    }

    private void drawArc(Canvas canvas) {
        if (mData != null && mData.size() > 0) {
            scaledValues = scale(mData);

            if (getSum(mData) > 0) {

                RectF rectF = new RectF();
                rectF.left = getPaddingLeft();
                rectF.top = getPaddingTop();
                rectF.right = 2 * mRadius + getPaddingLeft();
                rectF.bottom = 2 * mRadius + getPaddingTop();
                float sliceStart = -90;
                for (int i = 0; i < mData.size(); i++) {
                    arcPaint.setColor(Color.parseColor(mData.get(i).getColor()));
                    canvas.drawArc(rectF, sliceStart, scaledValues[i], true, arcPaint);
                    sliceStart += scaledValues[i];
                }

            } else {
                float centerX = mRadius + getPaddingLeft();
                float centerY = mRadius  + getPaddingTop();
                canvas.drawCircle(centerX, centerY,mRadius, emptyPaint);
            }

        }
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

            mHeight = mRadius * 2;
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    private int dip2px(float dp) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private float[] scale(List<PieChartBean> data) {
        float[] scaledValues = new float[data.size()];
        int total = getSum(data);
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getAmount() != 0) {
                if (i != data.size() - 1) {

                    scaledValues[i] = (float) (data.get(i).getAmount() * 360 / total);
                } else {
                    scaledValues[i] = remainAngle(scaledValues);
                }
            } else {
                scaledValues[i] = 0;
            }
        }

        return scaledValues;
    }

    private float remainAngle(float[] scaledValues) {
        int sum = 0;
        for (int i = 0; i < scaledValues.length - 1; i++) {
            sum += scaledValues[i];
        }
        return 360 - sum;
    }

    private int getSum(List<PieChartBean> data) {
        int sum = 0;
        for (int i = 0; i < data.size(); i++) {
            sum += data.get(i).getAmount();
        }
        return sum;
    }

    public void setPieChartData(List<PieChartBean> data) {
        this.mData = data;
        this.invalidate();
    }
}
