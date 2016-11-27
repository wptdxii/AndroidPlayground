package com.wptdxii.playground.view.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.cloudhome.R;

@SuppressLint("DrawAllocation") public class BrokenLineView extends View {
	
	
	   
		public String[] XLabel;    //X的刻度
	    public String[] YLabel;    //Y的刻度
	    public String[] Data;      //数据
	    public Context context;
	    float canvasWidth;
	    float canvasHeight;
	    float startXPoint;
	    float scaleX;
	    float scaleY;
	    float startYPoint; 
	    //月份的个数
	    int monthNum;
	  
        
        public BrokenLineView(Context context) {
    		super(context);
    		this.context=context;
    	}
        
        

	public BrokenLineView(Context context, AttributeSet attrs,
						  int defStyle) {
			super(context, attrs, defStyle);
			this.context=context;
		}



		public BrokenLineView(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.context=context;
		}




	public void SetInfo(String[] XLabels, String[] YLabels, String[] AllData, int monthNums)
	    {
	        XLabel=XLabels;
	        YLabel=YLabels;
	        Data=AllData;
	        monthNum=monthNums;
	        //在改变这个可能改变外观的属性后废除这个view，这样系统才知道需要重绘
	        invalidate();
	        requestLayout();
	    }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
		int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	 @SuppressLint("DrawAllocation") @Override
	    protected void onDraw(Canvas canvas){
	        
	        
	        canvas.drawColor(Color.WHITE);//设置背景颜色
	        Paint paint= new Paint();
	        paint.setStyle(Paint.Style.FILL);
	        paint.setAntiAlias(true);//去锯齿
	        paint.setColor(getResources().getColor(R.color.broken_line_gray));//颜色
	        paint.setTextSize(dip2px(context, 11));  //设置轴文字大小
	        
	        canvasWidth=canvas.getWidth();
	        canvasHeight=canvas.getHeight();
	        startXPoint=canvasWidth/(monthNum+1);
	        scaleX=canvasWidth/(monthNum+1);
	        scaleY=canvasHeight/8;
	        startYPoint=canvasHeight-scaleY;
	       
	        //设置Y轴
	        for(int i=6;i>0;i--)               
	        {
	        	paint.setStrokeWidth(dip2px(context, 1));
	        	 paint.setColor(getResources().getColor(R.color.broken_line_gray));
	            canvas.drawLine(dip2px(context, 7),i*scaleY+scaleY, canvasWidth-dip2px(context, 7), i*scaleY+scaleY, paint);
	            try
	            {
	            	paint.setColor(getResources().getColor(R.color.black_scale));
	            	int m= Math.abs(i-6);
	            	canvas.drawText(YLabel[m] , dip2px(context, 7), i*scaleY+scaleY-dip2px(context, 5), paint);  //文字
	            }
	            catch(Exception e)
	            {
	            }
	        }
	        
	        //设置X轴
	        for(int i=0;i<monthNum;i++)   
	        {
	        	paint.setColor(getResources().getColor(R.color.broken_line_gray));
	        	paint.setStrokeWidth(dip2px(context, 1));
	        	canvas.drawLine(startXPoint+i*scaleX, startYPoint, startXPoint+i*scaleX, scaleY, paint);
	            try
	            {
	            	paint.setColor(getResources().getColor(R.color.black_scale));
	            	canvas.drawText(XLabel[i] , startXPoint+i*scaleX-scaleX/3, startYPoint+scaleY-dip2px(context, 1), paint);  //文字
	            	paint.setColor(getResources().getColor(R.color.blue_line));
	            	
	                //数据值
	                    if(i>0&&YCoord(Data[i-1])!=-999&&YCoord(Data[i])!=-999)  //保证有效数据
	                    {
	                    	paint.setStrokeWidth(dip2px(context, 2));
	                    	canvas.drawLine(startXPoint+(i-1)*scaleX, YCoord(Data[i-1]), startXPoint+i*scaleX, YCoord(Data[i]), paint);
	                    }
	                   
	                    paint.setColor(getResources().getColor(R.color.blue_line));
	                    canvas.drawCircle(startXPoint+i*scaleX,YCoord(Data[i]), dip2px(context, 4), paint);
//	                    paint.setTextSize(dip2px(context, 10));
	                    if(i==monthNum-1){
	                    	 paint.setTextSize(dip2px(context, 13));
	                    	canvas.drawText(Data[i], startXPoint+i*scaleX, YCoord(Data[i])-dip2px(context, 7), paint);
	                    }
	        	        
	                   
	           }
	            catch(Exception e)
	            {
	            }
	        }
	    }
	    private float YCoord(String y0)  //计算绘制时的Y坐标，无数据时返回-999
	    {
	    	float y;
	        try
	        {
	            y= Float.parseFloat(y0);
	        }
	        catch(Exception e)
	        {
	            return -999;    //出错则返回-999
	        }
	        try
	        {
//	        	return startYPoint-y*scaleY/(Float.parseFloat(YLabel[1])-Float.parseFloat(YLabel[0]))+Float.parseFloat(YLabel[0])*scaleY/(Float.parseFloat(YLabel[1])-Float.parseFloat(YLabel[0]));
	        	return startYPoint-(y+ Float.parseFloat(YLabel[0]))*scaleY/(Float.parseFloat(YLabel[1])- Float.parseFloat(YLabel[0]));
	        }
	        catch(Exception e)
	        {
	        }
	        return y;
	    }
	    
	    
	    /**
	     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	     */
	    public static int dip2px(Context context, float dpValue) {
	        final float scale = context.getResources().getDisplayMetrics().density;
	        return (int) (dpValue * scale + 0.5f);
	    }

}
