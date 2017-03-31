package com.cloudhome.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.view.sortlistview.ClearEditText;

import java.util.List;
class C_Name_PopupWindow extends PopupWindow {


    private View mMenuView;
    private ClearEditText sydszzrx_edit;
    private Button submit;
    private ListView listView;
    private TextView lv_title;

    public C_Name_PopupWindow(final Activity context, List<String> c_name, List<String> c_code, String c_type_name) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.c_type_window, null);


        lv_title = (TextView) mMenuView.findViewById(R.id.lv_title);

        lv_title.setText(c_type_name);
        listView = (ListView) mMenuView.findViewById(R.id.lv_dialog);

        final String[] company_name = c_name.toArray(new String[c_name.size()]);
        final String[] company_code = c_code.toArray(new String[c_code.size()]);
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(new ArrayAdapter<String>(
                context, R.layout.company_text, R.id.tv_title, company_name));


        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {


                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("c_name", company_name[pos]);
                data.putString("c_code", company_code[pos]);
                msg.setData(data);

                MyCompanyActivity.mHandler.sendMessage(msg);

                dismiss();
            }
        });

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


                int heightTop = mMenuView.findViewById(R.id.c_type_rel).getTop();
                int heightBottom = mMenuView.findViewById(R.id.c_type_rel).getBottom();
                int heightLeft = mMenuView.findViewById(R.id.c_type_rel).getLeft();
                int heightRight = mMenuView.findViewById(R.id.c_type_rel).getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                    if (y < heightTop || y > heightBottom || x < heightLeft || x > heightRight) {
                        dismiss();
                    }

                }


                return true;
            }
        });

    }

}  