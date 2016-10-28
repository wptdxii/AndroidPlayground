package com.wptdxii.playground.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.view.sortlistview.ClearEditText;

public class SydszzrxPopupWindow extends PopupWindow {
  
  
  
    private View mMenuView;
    private ClearEditText sydszzrx_edit;
    private Button submit;
    public SydszzrxPopupWindow(final Activity context) {
        super(context);  
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.sydszzrx_window, null);  
        
        sydszzrx_edit = (ClearEditText) mMenuView.findViewById(R.id.sydszzrx_edit);  
        submit= (Button) mMenuView.findViewById(R.id.submit);
        
        submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String sydszzrxtxt=sydszzrx_edit.getText().toString();
				int length = sydszzrxtxt.length();
				if(length<3){
					
					Toast.makeText(context, "请输入位数大于2位的数字",
							Toast.LENGTH_LONG).show();
				}else{
					
					  Message msg = new Message();
		        	   Bundle data = new Bundle();
		        	   data.putString("value", sydszzrxtxt);
		        	   msg.setData(data);
		        	  
		        	   SelectAssuranceActivity.mHandler.sendMessage(msg); 
		        	   dismiss();
		        	   
				}
				
			}
		});
        
//     
//        car_licence.setOnClickListener(new OnClickListener() {  
//  
//            public void onClick(View v) {  
//                //销毁弹出框  
//                dismiss();  
//            }  
//        });  
        
   
        //设置SelectPicPopupWindow的View  
        this.setContentView(mMenuView);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        //设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.AnimBottom);  
        //实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0x50000000);
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw);  
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框  
        mMenuView.setOnTouchListener(new OnTouchListener() {
              
            public boolean onTouch(View v, MotionEvent event) {
                  
                 
            	 v.performClick();
                             
                return true;  
            }  
        });  
  
    }  
  
}  