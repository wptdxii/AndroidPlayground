package com.cloudhome.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;
public class OtherInfoActicity extends BaseActivity {


    private ClearEditText city;
    TextWatcher city_Watcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = s.toString();
            int index = city.getSelectionStart();

            if (!isName(text)) {
                if (text.length() <= 1) {
                    s.clear();
                } else if ((index - 1) < 0) {
                    s.clear();
                } else {
                    s.delete(index - 1, index);
                }
            }

        }
    };
    private ClearEditText emergency_contact_p, emergency_contact_num;
    TextWatcher emergency_Watcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable s) {


//			int selEndIndex = Selection.getSelectionEnd(s);
//			selEndIndex = s.length();
//			Selection.setSelection(s, selEndIndex);
//
            String text = s.toString();
            int index = emergency_contact_p.getSelectionStart();

            if (!isName(text)) {
                if (text.length() <= 1) {
                    s.clear();
                } else if ((index - 1) < 0) {
                    s.clear();
                } else {
                    s.delete(index - 1, index);
                }
            }

        }
    };
    private RelativeLayout layout;
    private PopupWindow popupWindow;
    private View popview;
    private RelativeLayout purpose_rel;
    private ClearEditText travel_destination;
    TextWatcher travel_Watcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = s.toString();
            int index = travel_destination.getSelectionStart();

            if (!isName(text)) {
                if (text.length() <= 1) {
                    s.clear();
                } else if ((index - 1) < 0) {
                    s.clear();
                } else {
                    s.delete(index - 1, index);
                }
            }

        }
    };
    private TextView travel_purpose;
    private String[] trip_purpose = {"旅游", "探亲", "留学"};
    private String token;
    private String user_id;
    private ImageView otherinfo_back;
    private TextView save;

    public static boolean isName(String text) {

        String text_OK = "[a-zA-Z\u4e00-\u9fa5]*";
        return !TextUtils.isEmpty(text) && text.matches(text_OK);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.otherinfo);


        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");

        init();
        initEvent();

    }

    void init() {

        otherinfo_back = (ImageView) findViewById(R.id.otherinfo_back);
        popview = findViewById(R.id.popview);
        travel_destination = (ClearEditText) findViewById(R.id.travel_destination);
        travel_destination.addTextChangedListener(travel_Watcher);
        city = (ClearEditText) findViewById(R.id.city);
        city.addTextChangedListener(city_Watcher);
        emergency_contact_p = (ClearEditText) findViewById(R.id.emergency_contact_p);


        emergency_contact_p.addTextChangedListener(emergency_Watcher);

        emergency_contact_num = (ClearEditText) findViewById(R.id.emergency_contact_num);

        purpose_rel = (RelativeLayout) findViewById(R.id.purpose_rel);
        travel_purpose = (TextView) findViewById(R.id.travel_purpose);
        save = (TextView) findViewById(R.id.save);

        Intent intent = getIntent();
        String otherInfo = intent.getStringExtra("otherInfo");
        try {
            JSONObject obj = new JSONObject(otherInfo);
            travel_destination.setText(obj.getString("to"));
            travel_purpose.setText(obj.getString("for"));
            city.setText(obj.getString("visa_city"));
            emergency_contact_p.setText(obj.getString("emergency_name"));
            emergency_contact_num.setText(obj.getString("emergency_tel"));


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    void initEvent() {


        otherinfo_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = getIntent();

                setResult(0, intent);
                finish();

            }
        });
        purpose_rel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showPopupWindow();
            }
        });

        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String t_d_str = travel_destination.getText().toString();
                String t_p_str = travel_purpose.getText().toString();
                String city_str = city.getText().toString();
                String c_name = emergency_contact_p.getText().toString();

                String c_phone = emergency_contact_num.getText().toString();

                if (t_d_str.equals("null") || t_d_str.equals("")) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            OtherInfoActicity.this);

                    builder.setTitle("提示");
                    builder.setMessage("请输入出行目的地");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.create().show();

                } else if (t_p_str.equals("请选择")) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            OtherInfoActicity.this);

                    builder.setTitle("提示");
                    builder.setMessage("请选择出行目的");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.create().show();

                } else if (city_str.equals("null") || city_str.equals("")) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            OtherInfoActicity.this);

                    builder.setTitle("提示");
                    builder.setMessage("请输入签证办理城市");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();

                                }
                            });
                    builder.create().show();

                } /*else if (c_name.equals("null") || c_name.equals("")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							OtherInfoActicity.this);

					builder.setTitle("提示");
					builder.setMessage("请输入紧急联系人姓名");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();

				} else if (c_phone.equals("null") || c_phone.equals("")) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							OtherInfoActicity.this);

					builder.setTitle("提示");
					builder.setMessage("请输入紧急联系人电话");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();
				} */ else {

                    if (c_name.equals("null") || c_name.equals("")) {
                        c_name = "";
                    }
                    if (c_phone.equals("null") || c_phone.equals("")) {
                        c_phone = "";
                    }
                    String otherstr = Json_Value(t_d_str, t_p_str, city_str,
                            c_name, c_phone);

                    Intent intent = getIntent();

                    intent.putExtra("other", otherstr);
                    intent.putExtra("to", t_d_str);

                    setResult(3, intent);

                    OtherInfoActicity.this.finish();

                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = getIntent();

            setResult(0, intent);

        }
        return super.onKeyDown(keyCode, event);
    }

    public String Json_Value(String to, String forstr, String visa_city,
                             String emergency_name, String emergency_tel) {
        String jsonresult = "";// 定义返回字符串

        try {

            JSONObject jsonObj = new JSONObject();// pet对象，json形式
            jsonObj.put("to", to);// 向pet对象里面添加值
            jsonObj.put("for", forstr);
            jsonObj.put("visa_city", visa_city);
            jsonObj.put("emergency_name", emergency_name);
            jsonObj.put("emergency_tel", emergency_tel);

            jsonresult = jsonObj.toString();

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return jsonresult;
    }

    private void showPopupWindow() {
        layout = ((RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.type_pop, null));
        ListView listViewSpinner = (ListView) this.layout.findViewById(R.id.lv_dialog);

        listViewSpinner.setAdapter(new ArrayAdapter<String>(
                OtherInfoActicity.this, R.layout.text, R.id.tv_text,
                trip_purpose));

        popupWindow = new PopupWindow(this);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth(LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setContentView(this.layout);
        popupWindow.showAsDropDown(this.popview);

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        layout.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int heightTop = layout.findViewById(R.id.c_type_rel).getTop();
                int heightBottom = layout.findViewById(R.id.c_type_rel)
                        .getBottom();
                int heightLeft = layout.findViewById(R.id.c_type_rel).getLeft();
                int heightRight = layout.findViewById(R.id.c_type_rel)
                        .getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                    if (y < heightTop || y > heightBottom || x < heightLeft
                            || x > heightRight) {
                        popupWindow.dismiss();
                    }

                }

                return true;
            }
        });

        listViewSpinner.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                arg0.setVisibility(View.VISIBLE);
                travel_purpose.setText(trip_purpose[pos]);
                popupWindow.dismiss();
                popupWindow = null;

            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }
}
