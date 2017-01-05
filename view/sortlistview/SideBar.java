package com.cloudhome.view.sortlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * ��ߵ����� A-Z
 * @author ZhangZhaoCheng
 */
public class SideBar extends View {
	
	// 26����ĸ
	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	private int choose = -1;

	private TextView mTextDialog;
	private Paint paint = new Paint();
	private OnLetterChangedListener onLetterChangedListener;
	
	private float y;//����ʱ��¼Yֵ

	/**
	 * ��������м���ʾѡ����ĸ��View
	 * @param mTextDialog
	 */
	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}

	public SideBar(Context context) {
		super(context);
	}
	
	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// ��ȡ����ı䱳����ɫ.
		int height = getHeight();// ��ȡ��Ӧ�߶�
		int width = getWidth(); // ��ȡ��Ӧ���
		int singleHeight = height / b.length;// ��ȡÿһ����ĸ�ĸ߶�

		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.rgb(33, 65, 98));
			// paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			
		
			if (height>800)
			{
				paint.setTextSize(30);
			}else{
				paint.setTextSize(20);
			}
			
			// ѡ�е�״̬
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			// x��������м�-�ַ�����ȵ�һ��.
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			//y��������ַ����߶ȳ���������������߶�
			float yPos = singleHeight * i + singleHeight;
			//���A-	Z��ĸ
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();// ���û���
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		y = event.getY();// ���y����
		final int oldChoose = choose;
		// ���y������ռ�ܸ߶ȵı���*b����ĳ��Ⱦ͵��ڵ��b�еĸ���.
		final int c = (int) (y / getHeight() * b.length);

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;//
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			//���òർ���ı���ɫ
			//	setBackgroundResource(R.drawable.sidebar_background);
			if (oldChoose != c) {
				if (c >= 0 && c < b.length) {
					if (onLetterChangedListener != null) {
						onLetterChangedListener.onTouchLetterChanged(b[c]);
					}
					if (mTextDialog != null) {
						mTextDialog.setText(b[c]);
						mTextDialog.setVisibility(View.VISIBLE);
					}
					choose = c;
					invalidate();
				}
			}
			break;
		}
		return true;
	}

	/**
	 * ���û����ӿڼ���
	 */
	public void setOnLetterChangedListener(
			OnLetterChangedListener onLetterChangedListener) {
		this.onLetterChangedListener = onLetterChangedListener;
	}

	/**
	 * �����������ʾDialog�ӿ�
	 */
	public interface OnLetterChangedListener {
		void onTouchLetterChanged(String s);
	}

}