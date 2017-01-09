package com.wujay.fund.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.wujay.fund.common.AppUtil;
import com.wujay.fund.common.Constants;
import com.wujay.fund.entity.GesturePoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ��������·������
 *
 */
public class GestureDrawline extends View {
	private int mov_x;// �����������
	private int mov_y;
	private Paint paint;// ��������
	private Canvas canvas;// ����
	private Bitmap bitmap;// λͼ
	private List<GesturePoint> list;// װ�и���view����ļ���
	private List<Pair<GesturePoint, GesturePoint>> lineList;// ��¼��������
	private Map<String, GesturePoint> autoCheckPointMap;// �Զ�ѡ�е������
	private boolean isDrawEnable = true; // �Ƿ��������
	
	/**
	 * ��Ļ�Ŀ�Ⱥ͸߶�
	 */
	private int[] screenDispaly;

	/**
	 * ��ָ��ǰ���ĸ�Point��
	 */
	private GesturePoint currentPoint;
	/**
	 * �û���ͼ�Ļص�
	 */
	private GestureCallBack callBack;

	/**
	 * �û���ǰ���Ƶ�ͼ������
	 */
	private StringBuilder passWordSb;
	
	/**
	 * �Ƿ�ΪУ��
	 */
	private boolean isVerify;
	
	/**
	 * �û������passWord
	 */
	private String passWord;

	public GestureDrawline(Context context, List<GesturePoint> list, boolean isVerify,
			String passWord, GestureCallBack callBack) {
		super(context);
		screenDispaly = AppUtil.getScreenDispaly(context);
		paint = new Paint(Paint.DITHER_FLAG);// ����һ������
		bitmap = Bitmap.createBitmap(screenDispaly[0], screenDispaly[0], Bitmap.Config.ARGB_8888); // ����λͼ�Ŀ��
		canvas = new Canvas();
		canvas.setBitmap(bitmap);
		paint.setStyle(Style.STROKE);// ���÷����
		paint.setStrokeWidth(10);// �ʿ�5����
		paint.setColor(Color.rgb(245, 142, 33));// ����Ĭ��������ɫ
		paint.setAntiAlias(true);// ����ʾ���

		this.list = list;
		this.lineList = new ArrayList<Pair<GesturePoint, GesturePoint>>();
		
		initAutoCheckPointMap();
		this.callBack = callBack;

		// ��ʼ�����뻺��
		this.isVerify = isVerify;
		this.passWordSb = new StringBuilder();
		this.passWord = passWord;
	}
	
	private void initAutoCheckPointMap() {
		autoCheckPointMap = new HashMap<String,GesturePoint>();
		autoCheckPointMap.put("1,3", getGesturePointByNum(2));
		autoCheckPointMap.put("1,7", getGesturePointByNum(4));
		autoCheckPointMap.put("1,9", getGesturePointByNum(5));
		autoCheckPointMap.put("2,8", getGesturePointByNum(5));
		autoCheckPointMap.put("3,7", getGesturePointByNum(5));
		autoCheckPointMap.put("3,9", getGesturePointByNum(6));
		autoCheckPointMap.put("4,6", getGesturePointByNum(5));
		autoCheckPointMap.put("7,9", getGesturePointByNum(8));
	}
	
	private GesturePoint getGesturePointByNum(int num) {
		for (GesturePoint point : list) {
			if (point.getNum() == num) {
				return point;
			}
		}
		return null;
	}

	// ��λͼ
	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}


	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isDrawEnable) {
	
			return true;
		}
		paint.setColor(Color.rgb(245, 142, 33));
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			mov_x = (int) event.getX();
			mov_y = (int) event.getY();
			currentPoint = getPointAt(mov_x, mov_y);
			if (currentPoint != null) {
				currentPoint.setPointState(Constants.POINT_STATE_SELECTED);
				passWordSb.append(currentPoint.getNum());
			}
		
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			clearScreenAndDrawList();

	
			GesturePoint pointAt = getPointAt((int) event.getX(), (int) event.getY());
		
			if (currentPoint == null && pointAt == null) {
				return true;
			} else {
				if (currentPoint == null) {
				
					currentPoint = pointAt;
				
					currentPoint.setPointState(Constants.POINT_STATE_SELECTED);
					passWordSb.append(currentPoint.getNum());
				}
			}
			if (pointAt == null || currentPoint.equals(pointAt) || Constants.POINT_STATE_SELECTED == pointAt.getPointState()) {
			
				canvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), event.getX(), event.getY(), paint);// ����
			} else {
			
				canvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), pointAt.getCenterX(), pointAt.getCenterY(), paint);// ����
				pointAt.setPointState(Constants.POINT_STATE_SELECTED);
				
				
				GesturePoint betweenPoint = getBetweenCheckPoint(currentPoint, pointAt);
				if (betweenPoint != null && Constants.POINT_STATE_SELECTED != betweenPoint.getPointState()) {
			
					Pair<GesturePoint, GesturePoint> pair1 = new Pair<GesturePoint, GesturePoint>(currentPoint, betweenPoint);
					lineList.add(pair1);
					passWordSb.append(betweenPoint.getNum());
					Pair<GesturePoint, GesturePoint> pair2 = new Pair<GesturePoint, GesturePoint>(betweenPoint, pointAt);
					lineList.add(pair2);
					passWordSb.append(pointAt.getNum());
					
					betweenPoint.setPointState(Constants.POINT_STATE_SELECTED);
				
					currentPoint = pointAt;
				} else {
					Pair<GesturePoint, GesturePoint> pair = new Pair<GesturePoint, GesturePoint>(currentPoint, pointAt);
					lineList.add(pair);
					passWordSb.append(pointAt.getNum());
					// ��ֵ��ǰ��point;
					currentPoint = pointAt;
				}
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:// ����ָ̧���ʱ��
			if (isVerify) {
				// ��������У��
				// �����Ļ�����е��ߣ�ֻ���ϼ������汣�����
				if (passWord.equals(passWordSb.toString())) {
					// �����û����Ƶ����������봫���������ͬ
					callBack.checkedSuccess();
				} else {
					// �û����Ƶ������봫������벻ͬ��
					callBack.checkedFail();
				}
			} else {
				callBack.onGestureCodeInput(passWordSb.toString());
			}
			break;
		default:
			break;
		}
		return true;
	}
	
	/**
	 * ָ��ʱ��ȥ������Ƶ�״̬
	 * @param delayTime �ӳ�ִ��ʱ��
	 */
	public void clearDrawlineState(long delayTime) {
		if (delayTime > 0) {
			// ���ƺ�ɫ��ʾ·��
			isDrawEnable = false;
			drawErrorPathTip();
		}
		new Handler().postDelayed(new clearStateRunnable(), delayTime);
	}
	
	/**
	 * �������״̬���߳�
	 */
	final class clearStateRunnable implements Runnable {
		public void run() {
			// ����passWordSb
			passWordSb = new StringBuilder();
			// ��ձ����ļ���
			lineList.clear();
			// ���»��ƽ���
			clearScreenAndDrawList();
			for (GesturePoint p : list) {
				p.setPointState(Constants.POINT_STATE_NORMAL);
			}
			invalidate();
			isDrawEnable = true;
		}
	}

	/**
	 * ͨ�����λ��ȥ�����������������ǰ������ĸ�Point�����
	 * 
	 * @param x
	 * @param y
	 * @return ���û���ҵ����򷵻�null�������û���ǰ�ƶ��ĵط����ڵ����֮��
	 */
	private GesturePoint getPointAt(int x, int y) {

		for (GesturePoint point : list) {
			// ���ж�x
			int leftX = point.getLeftX();
			int rightX = point.getRightX();
			if (!(x >= leftX && x < rightX)) {
				// ���Ϊ�٣���������һ���Ա�
				continue;
			}

			int topY = point.getTopY();
			int bottomY = point.getBottomY();
			if (!(y >= topY && y < bottomY)) {
				// ���Ϊ�٣���������һ���Ա�
				continue;
			}

			// ���ִ�е��⣬��ô˵����ǰ����ĵ��λ���ڱ��������λ������ط�
			return point;
		}

		return null;
	}
	
	private GesturePoint getBetweenCheckPoint(GesturePoint pointStart, GesturePoint pointEnd) {
		int startNum = pointStart.getNum();
		int endNum = pointEnd.getNum();
		String key = null;
		if (startNum < endNum) {
			key = startNum + "," + endNum;
		} else {
			key = endNum + "," + startNum;
		}
		return autoCheckPointMap.get(key);
	}

	/**
	 * �����Ļ�����е��ߣ�Ȼ�󻭳������������
	 */
	private void clearScreenAndDrawList() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (Pair<GesturePoint, GesturePoint> pair : lineList) {
			canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);// ����
		}
	}
	
	/**
	 * У�����/���λ��Ʋ�һ����ʾ
	 */
	private void drawErrorPathTip() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		paint.setColor(Color.rgb(154, 7, 21));// ����Ĭ����·��ɫ
		for (Pair<GesturePoint, GesturePoint> pair : lineList) {
			pair.first.setPointState(Constants.POINT_STATE_WRONG);
			pair.second.setPointState(Constants.POINT_STATE_WRONG);
			canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);// ����
		}
		invalidate();
	}


	public interface GestureCallBack {
		
		/**
		 * �û�����/��������������
		 */
		public abstract void onGestureCodeInput(String inputCode);

		/**
		 * �����û����Ƶ������봫���������ͬ
		 */
		public abstract void checkedSuccess();

		/**
		 * �����û����Ƶ������봫������벻��ͬ
		 */
		public abstract void checkedFail();
	}

}
