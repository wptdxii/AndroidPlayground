package com.cloudhome.activity;


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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cloudhome.R;
class Select_Abb_PopupWindow extends PopupWindow  {
  
  
    private Button abbreviation_submit;  
    private View mMenuView;  
    private String strBtnSelected = "";
    
    private RadioButton c11;
    private RadioButton c12;
    private RadioButton c13;
    private RadioButton c14;
    private RadioButton c15;
    private RadioButton c16;
    private RadioButton c17;
    private RadioButton c18;
    private RadioButton c19;
    private RadioButton c21;
    private RadioButton c22;
    private RadioButton c23;
    private RadioButton c24;
    private RadioButton c25;
    private RadioButton c26;
    private RadioButton c27;
    private RadioButton c28;
    private RadioButton c29;
    private RadioButton c31;
    private RadioButton c32;
    private RadioButton c33;
    private RadioButton c34;
    private RadioButton c35;
    private RadioButton c36;
    private RadioButton c37;
    private RadioButton c38;
    private RadioButton c39;
    private RadioButton c41;
    private RadioButton c42;
    private RadioButton c43;
    private RadioButton c44;
    
    RadioButton c45;
    RadioButton c46;
    RadioButton c47;
    RadioButton c48;
    RadioButton c49;
    
    private RadioGroup grp1;
    private RadioGroup grp2;
    private RadioGroup grp3;
    private RadioGroup grp4;
  
    private BtnSelected btnListener1 = new BtnSelected("京");
    private BtnSelected btnListener2 = new BtnSelected("津");
    private BtnSelected btnListener3 = new BtnSelected("冀");
    private BtnSelected btnListener4 = new BtnSelected("沪");
    private BtnSelected btnListener5 = new BtnSelected("豫");
    private BtnSelected btnListener6 = new BtnSelected("渝");
    private BtnSelected btnListener7 = new BtnSelected("云");
    private BtnSelected btnListener8 = new BtnSelected("辽");
    private BtnSelected btnListener9 = new BtnSelected("黑");
    
    private BtnSelected btnListener21 = new BtnSelected("湘");
    private BtnSelected btnListener22 = new BtnSelected("鲁");
    private BtnSelected btnListener23 = new BtnSelected("新");
    private BtnSelected btnListener24 = new BtnSelected("苏");
    private BtnSelected btnListener25 = new BtnSelected("浙");
    private BtnSelected btnListener26 = new BtnSelected("赣");
    private BtnSelected btnListener27 = new BtnSelected("鄂");
    private BtnSelected btnListener28 = new BtnSelected("桂");
    private BtnSelected btnListener29 = new BtnSelected("甘");
    
    private BtnSelected btnListener31 = new BtnSelected("晋");
    private BtnSelected btnListener32 = new BtnSelected("蒙");
    private BtnSelected btnListener33 = new BtnSelected("陕");
    private BtnSelected btnListener34 = new BtnSelected("吉");
    private BtnSelected btnListener35 = new BtnSelected("闽");
    private BtnSelected btnListener36 = new BtnSelected("贵");
    private BtnSelected btnListener37 = new BtnSelected("粤");
    private BtnSelected btnListener38 = new BtnSelected("青");
    private BtnSelected btnListener39 = new BtnSelected("藏");
    
    private BtnSelected btnListener41 = new BtnSelected("川");
    private BtnSelected btnListener42 = new BtnSelected("宁");
    private BtnSelected btnListener43 = new BtnSelected("琼");
    private BtnSelected btnListener44 = new BtnSelected("皖");
  
    
    public class BtnSelected implements OnClickListener {
        public BtnSelected(String str) {
          bntID = str;
        }

        final public String bntID;

        public void onClick(View arg0) {
          // TODO Auto-generated method stub
          strBtnSelected = bntID;

          if (bntID.equals("京") || bntID.equals("津") || bntID.equals("冀")||bntID.equals("沪") || bntID.equals("豫")
                  || bntID.equals("渝")||bntID.equals("云") || bntID.equals("辽")
                  || bntID.equals("黑")) {
        	  
        	
        	   
        	   
            grp2.clearCheck();
            grp3.clearCheck();
            grp4.clearCheck();
          }else if(bntID.equals("湘") || bntID.equals("鲁") || bntID.equals("新")||bntID.equals("苏") || bntID.equals("浙")
                  || bntID.equals("赣")||bntID.equals("鄂") || bntID.equals("桂")
                  || bntID.equals("甘")){
        	  
        	  grp1.clearCheck();
              grp3.clearCheck();
              grp4.clearCheck();
          }else if(bntID.equals("晋") || bntID.equals("蒙") || bntID.equals("陕")||bntID.equals("吉") || bntID.equals("闽")
                  || bntID.equals("贵")||bntID.equals("粤") || bntID.equals("青")
                  || bntID.equals("藏")){
        	  
        	  grp1.clearCheck();
              grp2.clearCheck();
              grp4.clearCheck();
          }else if(bntID.equals("川") || bntID.equals("宁") || bntID.equals("琼")||bntID.equals("皖")){
        	  
        	  grp1.clearCheck();
              grp2.clearCheck();
              grp3.clearCheck();
          }
          
        }
      }

    public Select_Abb_PopupWindow(Activity context) {  
        super(context);  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        mMenuView = inflater.inflate(R.layout.abbreviation_dialog, null);  
      
 
       
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
        
        ColorDrawable dw = new ColorDrawable(0x40000000);  
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw);  
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框  
        mMenuView.setOnTouchListener(new OnTouchListener() {  
              
            public boolean onTouch(View v, MotionEvent event) {  
                  
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();  
              
                int y=(int) event.getY();  
                if(event.getAction()==MotionEvent.ACTION_UP){  
                	
                	 v.performClick();
                    if(y<height){  
                    //    dismiss();  
                    }  
                }                 
                return true;  
            }  
        });  
  
       
        init();
        initevent();
    }

  

	private void init(){
    	
    
    	  grp1 = (RadioGroup) mMenuView.findViewById(R.id.radio_customer);
          grp2 = (RadioGroup) mMenuView.findViewById(R.id.radio_customer2);
          grp3 = (RadioGroup) mMenuView.findViewById(R.id.radio_customer3);
          grp4 = (RadioGroup) mMenuView.findViewById(R.id.radio_customer4);
          c11 = (RadioButton) mMenuView.findViewById(R.id.c11);
          c12 = (RadioButton) mMenuView.findViewById(R.id.c12);
          c13 = (RadioButton) mMenuView.findViewById(R.id.c13);
          c14 = (RadioButton) mMenuView.findViewById(R.id.c14);
          c15 = (RadioButton) mMenuView.findViewById(R.id.c15);
          c16 = (RadioButton) mMenuView.findViewById(R.id.c16);
          c17 = (RadioButton) mMenuView.findViewById(R.id.c17);
          c18 = (RadioButton) mMenuView.findViewById(R.id.c18);
          c19 = (RadioButton) mMenuView.findViewById(R.id.c19);
          
          c21 = (RadioButton) mMenuView.findViewById(R.id.c21);
          c22 = (RadioButton) mMenuView.findViewById(R.id.c22);
          c23 = (RadioButton) mMenuView.findViewById(R.id.c23);
          c24 = (RadioButton) mMenuView.findViewById(R.id.c24);
          c25 = (RadioButton) mMenuView.findViewById(R.id.c25);
          c26 = (RadioButton) mMenuView.findViewById(R.id.c26);
          c27 = (RadioButton) mMenuView.findViewById(R.id.c27);
          c28 = (RadioButton) mMenuView.findViewById(R.id.c28);
          c29 = (RadioButton) mMenuView.findViewById(R.id.c29);
          
          c31 = (RadioButton) mMenuView.findViewById(R.id.c31);
          c32 = (RadioButton) mMenuView.findViewById(R.id.c32);
          c33 = (RadioButton) mMenuView.findViewById(R.id.c33);
          c34 = (RadioButton) mMenuView.findViewById(R.id.c34);
          c35 = (RadioButton) mMenuView.findViewById(R.id.c35);
          c36 = (RadioButton) mMenuView.findViewById(R.id.c36);
          c37 = (RadioButton) mMenuView.findViewById(R.id.c37);
          c38 = (RadioButton) mMenuView.findViewById(R.id.c38);
          c39 = (RadioButton) mMenuView.findViewById(R.id.c39);
          
          c41 = (RadioButton) mMenuView.findViewById(R.id.c41);
          c42 = (RadioButton) mMenuView.findViewById(R.id.c42);
          c43 = (RadioButton) mMenuView.findViewById(R.id.c43);
          c44 = (RadioButton) mMenuView.findViewById(R.id.c44);
       
          abbreviation_submit = (Button) mMenuView.findViewById(R.id.abbreviation_submit);
          
    }

    private void initevent() {
		
		
    	     c11.setOnClickListener(btnListener1);
             c12.setOnClickListener(btnListener2);
             c13.setOnClickListener(btnListener3);
             c14.setOnClickListener(btnListener4);
             c15.setOnClickListener(btnListener5);
             c16.setOnClickListener(btnListener6);
             c17.setOnClickListener(btnListener7);
             c18.setOnClickListener(btnListener8);
             c19.setOnClickListener(btnListener9);
    
             c21.setOnClickListener(btnListener21);
             c22.setOnClickListener(btnListener22);
             c23.setOnClickListener(btnListener23);
             c24.setOnClickListener(btnListener24);
             c25.setOnClickListener(btnListener25);
             c26.setOnClickListener(btnListener26);
             c27.setOnClickListener(btnListener27);
             c28.setOnClickListener(btnListener28);
             c29.setOnClickListener(btnListener29);
             
             c31.setOnClickListener(btnListener31);
             c32.setOnClickListener(btnListener32);
             c33.setOnClickListener(btnListener33);
             c34.setOnClickListener(btnListener34);
             c35.setOnClickListener(btnListener35);
             c36.setOnClickListener(btnListener36);
             c37.setOnClickListener(btnListener37);
             c38.setOnClickListener(btnListener38);
             c39.setOnClickListener(btnListener39);
             
             c41.setOnClickListener(btnListener41);
             c42.setOnClickListener(btnListener42);
             c43.setOnClickListener(btnListener43);
             c44.setOnClickListener(btnListener44);
           
    	    
             abbreviation_submit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(!strBtnSelected.equals(""))
					{
						
						   Message msg = new Message();
			        	   Bundle data = new Bundle();
			        	   data.putString("abbreviation", strBtnSelected);
			        	   msg.setData(data);
			        	  
			        	   CheXianBJActivity.mHandler.sendMessage(msg); 
			        	    
					}
					dismiss(); 
					
				}
			});
	}
    
  
}  