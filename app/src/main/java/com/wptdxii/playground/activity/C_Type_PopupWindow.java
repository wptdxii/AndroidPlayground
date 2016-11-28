package com.wptdxii.playground.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
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
import android.widget.RelativeLayout;

import com.cloudhome.R;
import com.cloudhome.view.sortlistview.ClearEditText;

import java.util.List;

public class C_Type_PopupWindow extends PopupWindow {


    private View mMenuView;
    private ClearEditText sydszzrx_edit;
    private Button submit;
    private ListView listView;
    private List<String> c_name;
    private List<String> c_code;
    private C_Name_PopupWindow C_Name_PopupWindow;

    public C_Type_PopupWindow(final Activity context, final String[] c_type, final List<List<String>> name, final List<List<String>> code, final RelativeLayout m_c_main) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.c_type_window, null);


        listView = (ListView) mMenuView.findViewById(R.id.lv_dialog);
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(new ArrayAdapter<String>(
                context, R.layout.company_text, R.id.tv_text, c_type));


        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

                c_name = name.get(pos);
                c_code = code.get(pos);
                String c_type_name = c_type[pos];
                C_Name_PopupWindow = new C_Name_PopupWindow(context, c_name, c_code, c_type_name);
                C_Name_PopupWindow.showAtLocation(m_c_main, Gravity.TOP, 0, 0);

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