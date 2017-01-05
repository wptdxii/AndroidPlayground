package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ReplacementTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.gghl.view.wheelcity.AddressData;
import com.gghl.view.wheelcity.OnWheelChangedListener;
import com.gghl.view.wheelcity.WheelView;
import com.gghl.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.gghl.view.wheelcity.adapters.ArrayWheelAdapter;
import com.gghl.view.wheelview.JudgeDate;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.WheelMain;
import com.zf.iosdialog.widget.MyAlertDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
public class CheXianBJActivity extends BaseActivity {

    public static CheXianBJActivity CheXianBJ_instance = null;
    private static TextView p_abbreviation;
    static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            Bundle bd = msg.getData();

            p_abbreviation.setText(bd.getString("abbreviation"));

            // menuWindow.dismiss();

        }
    };
    private WheelMain wheelMain;
    private String txttime;
    private String c_p_date_txt;
    // 自定义的弹出框类
    private Select_Abb_PopupWindow menuWindow;
    // 自定义的弹出框类
    private LicencePopupWindow LincenceWindow;
    private ScrollView main_scroll;
    @SuppressLint("SimpleDateFormat")
    private
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private TextView c_p_city;
    private TextView c_p_date;
    private ClearEditText c_p_num;
    private ClearEditText c_p_name;
    private TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

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
            // TODO Auto-generated method stub
            String text = s.toString();


            int index = c_p_name.getSelectionStart();

            if (!isName(text)) {
                if (text.length() <= 1) {
                    s.clear();
                } else {
                    s.delete(index - 1, index);
                }
            }

        }
    };
    private ClearEditText frame_num;
    private ClearEditText engine_no;
    private ClearEditText system_type;
    private TextView carrier_company;
    private TextView policy_type;
    private TextView c_p_num_txt;
    private String type_str = "";
    private String c_p_typeing = "";
    private String company_name;
    private String[] company_id;
    private CheckBox protocol_check;
    private Button c_x_b_j_in;
    private Boolean car_check = false;
    private String areaing;
    private ImageView c_p_d_q_mark;
    private ImageView frame_num_q_mark;
    private ImageView engine_no_q_mark;
    private ImageView system_type_q_mark;
    private RelativeLayout bj_back;
    private RelativeLayout rl_right;
    private TextView tv_text;
    private String[] TypeArray = {"交强险", "商业险", "交强险+商业险"};
    private String[] TypeCodeArray = {"01", "02", "03"};

    private static boolean isCarnumberNO(String carnumber) {
        /*
         * 车牌号格式：汉字 + A-Z + 5位A-Z或0-9 （只包括了普通车牌号，教练车和部分部队车等车牌号不包括在内）
		 */
        String carnumRegex = "[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}";
        return !TextUtils.isEmpty(carnumber) && carnumber.matches(carnumRegex);
    }

    private static boolean isName(String text) {

        String text_OK = "[a-zA-Z\u4e00-\u9fa5]*";
        return !TextUtils.isEmpty(text) && text.matches(text_OK);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chexian_b_j);

        CheXianBJ_instance = this;
        Calendar calendar = Calendar.getInstance();
        txttime = calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH);


        Intent intent = getIntent();


        company_name = intent.getStringExtra("company_name");
        company_id = intent.getStringArrayExtra("company_id");


        init();
        initEvent();
    }

    private void init() {

        c_p_city = (TextView) findViewById(R.id.c_p_city);
        p_abbreviation = (TextView) findViewById(R.id.p_abbreviation);
        c_p_date = (TextView) findViewById(R.id.c_p_date);

        policy_type = (TextView) findViewById(R.id.policy_type);
        c_p_num = (ClearEditText) findViewById(R.id.c_p_num);

        c_p_name = (ClearEditText) findViewById(R.id.c_p_name);
        c_p_name.addTextChangedListener(mTextWatcher);
        frame_num = (ClearEditText) findViewById(R.id.frame_num);
        engine_no = (ClearEditText) findViewById(R.id.engine_no);
        system_type = (ClearEditText) findViewById(R.id.system_type);

        c_p_d_q_mark = (ImageView) findViewById(R.id.c_p_d_q_mark);
        frame_num_q_mark = (ImageView) findViewById(R.id.frame_num_q_mark);
        engine_no_q_mark = (ImageView) findViewById(R.id.engine_no_q_mark);
        system_type_q_mark = (ImageView) findViewById(R.id.system_type_q_mark);
        main_scroll = (ScrollView) findViewById(R.id.main_scroll);
        carrier_company = (TextView) findViewById(R.id.carrier_company);
        carrier_company.setText(company_name);
        bj_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right= (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("车险询价");
        protocol_check = (CheckBox) findViewById(R.id.protocol_check);
        c_x_b_j_in = (Button) findViewById(R.id.c_x_b_j_in);
        c_p_num_txt = (TextView) findViewById(R.id.c_p_num_txt);
    }

    private void initEvent() {

        c_p_num.setTransformationMethod(new AllCapTransformationMethod());

        frame_num.setTransformationMethod(new AllCapTransformationMethod());
        engine_no.setTransformationMethod(new AllCapTransformationMethod());

        c_p_num.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {// 修改回车键功能

                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(CheXianBJActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);


                }
                return false;

            }
        });

        c_p_name.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {// 修改回车键功能

                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(CheXianBJActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                }
                return false;

            }
        });
        frame_num.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {// 修改回车键功能

                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(CheXianBJActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                }
                return false;

            }
        });
        engine_no.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {// 修改回车键功能

                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(CheXianBJActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                }
                return false;

            }
        });

        bj_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();

            }
        });
        c_p_city.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                View view = dialogm();
                final MyAlertDialog dialog1 = new MyAlertDialog(
                        CheXianBJActivity.this).builder()
                        .setTitle("请选择地区")
                        .setView(view)
                        .setNegativeButton("取消", new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                dialog1.setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        c_p_city.setText(areaing);
                        Toast.makeText(getApplicationContext(), areaing, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                dialog1.show();

            }
        });

        p_abbreviation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                menuWindow = new Select_Abb_PopupWindow(CheXianBJActivity.this);

                menuWindow.showAtLocation(main_scroll, Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);

            }
        });

        c_p_date.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                LayoutInflater inflater1 = LayoutInflater
                        .from(CheXianBJActivity.this);
                final View timepickerview1 = inflater1.inflate(
                        R.layout.timepicker, null);
                ScreenInfo screenInfo1 = new ScreenInfo(CheXianBJActivity.this);
                wheelMain = new WheelMain(timepickerview1);
                wheelMain.screenheight = screenInfo1.getHeight();
                String time1 = txttime;
                Calendar calendar1 = Calendar.getInstance();
                if (JudgeDate.isDate(time1, "yyyy-MM-dd")) {
                    try {
                        calendar1.setTime(dateFormat.parse(time1));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                int year1 = calendar1.get(Calendar.YEAR);
                int month1 = calendar1.get(Calendar.MONTH);
                int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
                wheelMain.initDateTimePicker(year1, month1, day1);
                final MyAlertDialog dialog = new MyAlertDialog(
                        CheXianBJActivity.this).builder()
                        .setTitle("请选择")
                                // .setMsg("22")
                                // .setEditText("111")
                        .setView(timepickerview1)
                        .setNegativeButton("取消", new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                dialog.setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        c_p_date_txt = wheelMain.getTime();

                        c_p_date.setText(c_p_date_txt);

                        Toast.makeText(getApplicationContext(),
                                wheelMain.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });

        c_p_d_q_mark.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                LincenceWindow = new LicencePopupWindow(CheXianBJActivity.this);

                // 显示窗口
                LincenceWindow.showAtLocation(main_scroll, Gravity.TOP, 0, 0);
            }
        });
        frame_num_q_mark.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                LincenceWindow = new LicencePopupWindow(CheXianBJActivity.this);
                LincenceWindow.showAtLocation(main_scroll, Gravity.TOP, 0, 0);
            }
        });

        engine_no_q_mark.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                LincenceWindow = new LicencePopupWindow(CheXianBJActivity.this);
                LincenceWindow.showAtLocation(main_scroll, Gravity.TOP, 0, 0);
            }
        });
        system_type_q_mark.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                LincenceWindow = new LicencePopupWindow(CheXianBJActivity.this);
                LincenceWindow.showAtLocation(main_scroll, Gravity.TOP, 0, 0);
            }
        });

        policy_type.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                View view = p_type_dialogm();
                final MyAlertDialog dialog = new MyAlertDialog(
                        CheXianBJActivity.this).builder().setTitle("请选择")
                        .setCancelable(false)
                                // .setMsg("")
                                // .setEditText("111")
                        .setView(view)

                        .setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // user_type=user_typeing;
                                policy_type.setText(type_str);

                            }
                        });
                dialog.show();
            }
        });


        protocol_check
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub

                        car_check = isChecked;
                        if (isChecked) {

                            c_p_num.setVisibility(View.GONE);
                            c_p_num.setText("");
                            c_p_num_txt.setVisibility(View.VISIBLE);
                        } else {
                            c_p_num.setVisibility(View.VISIBLE);
                            c_p_num_txt.setVisibility(View.GONE);
                        }
                    }
                });

        c_x_b_j_in.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String c_p_citystr = c_p_city.getText().toString();
                String carnostr = p_abbreviation.getText().toString()
                        + c_p_num.getText().toString().toUpperCase(Locale.getDefault());


                String namestr = c_p_name.getText().toString();

                String frame_numstr = frame_num.getText().toString().toUpperCase(Locale.getDefault());
                String engine_nostr = engine_no.getText().toString().toUpperCase(Locale.getDefault());


                String policy_typestr = policy_type.getText().toString();

                String company_namestr = carrier_company.getText().toString();
                if (!isCarnumberNO(carnostr) && !car_check) {
                    Toast.makeText(CheXianBJActivity.this, "请检查车牌号",
                            Toast.LENGTH_SHORT).show();
                } else if (namestr.equals("null") || namestr.equals("")) {
                    Toast.makeText(CheXianBJActivity.this, "请检查车主姓名",
                            Toast.LENGTH_SHORT).show();
                } else if (frame_numstr.equals("null")
                        || frame_numstr.equals("")) {
                    Toast.makeText(CheXianBJActivity.this, "请检查车辆识别代号",
                            Toast.LENGTH_SHORT).show();
                } else if (engine_nostr.equals("null")
                        || engine_nostr.equals("")) {
                    Toast.makeText(CheXianBJActivity.this, "请检查发动机号码",
                            Toast.LENGTH_SHORT).show();
                } else if (policy_typestr.equals("请选择保险类型")) {
                    Toast.makeText(CheXianBJActivity.this, "请选择保险类型",
                            Toast.LENGTH_SHORT).show();
                } else if (company_namestr.equals("请选择承保公司")) {
                    Toast.makeText(CheXianBJActivity.this, "请选择承保公司",
                            Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent();

                    if (!car_check) {
                        intent.putExtra("car_no", carnostr);
                    } else {
                        intent.putExtra("car_no", "新车未上牌");
                    }

                    intent.putExtra("frame_num", frame_numstr);
                    intent.putExtra("engine_no", engine_nostr);
                    intent.putExtra("owner_name", namestr);
                    intent.putExtra("system_type", "");
                    intent.putExtra("c_p_type", c_p_typeing);
                    intent.putExtra("insurance_company", company_id);
                    intent.putExtra("regist_date", "");
                    intent.putExtra("running_area", c_p_citystr);
                    intent.setClass(CheXianBJActivity.this,
                            SelectAssuranceActivity.class);

                    CheXianBJActivity.this.startActivity(intent);

                }

            }
        });

    }

    @SuppressLint("InflateParams")
    private View dialogm() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_country);
        country.setVisibleItems(1);
        country.setViewAdapter(new CountryAdapter(this));

        final String cities[][] = AddressData.CITIES;
        final WheelView city = (WheelView) contentView
                .findViewById(R.id.wheelcity_city);
        city.setVisibleItems(1);

        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateCities(city, cities, newValue);

                String province = AddressData.PROVINCES[country
                        .getCurrentItem()];
                if (province.equals("北京") || province.equals("上海")
                        || province.equals("天津") || province.equals("重庆")) {
                    areaing = province;
                } else {
                    areaing = AddressData.CITIES[country.getCurrentItem()][city
                            .getCurrentItem()];
                }

            }
        });

        city.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String province = AddressData.PROVINCES[country
                        .getCurrentItem()];
                if (province.equals("北京") || province.equals("上海")
                        || province.equals("天津") || province.equals("重庆")) {
                    areaing = province;
                } else {
                    areaing = AddressData.CITIES[country.getCurrentItem()][city
                            .getCurrentItem()];
                }

            }
        });

        country.setCurrentItem(0);

        city.setCurrentItem(0);
        updateCities(city, cities, 0);

        String province = AddressData.PROVINCES[country.getCurrentItem()];
        if (province.equals("北京") || province.equals("上海")
                || province.equals("天津") || province.equals("重庆")) {
            areaing = province;
        } else {
            areaing = AddressData.CITIES[country.getCurrentItem()][city
                    .getCurrentItem()];
        }

        return contentView;
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
                cities[index]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    @SuppressLint("InflateParams")
    private View p_type_dialogm() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_single_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_single);
        country.setVisibleItems(1);
        country.setViewAdapter(new typeAdapter(this));
        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                type_str = TypeArray[country.getCurrentItem()];
                c_p_typeing = TypeCodeArray[country.getCurrentItem()];

            }
        });

        country.setCurrentItem(0);

        type_str = TypeArray[country.getCurrentItem()];
        c_p_typeing = TypeCodeArray[country.getCurrentItem()];
        return contentView;
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

    private boolean isShouldHideInput(View v, MotionEvent event) {
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

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] = AddressData.PROVINCES;

        /**
         * Constructor
         */
        CountryAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            return super.getItem(index, cachedView, parent);
        }

        @Override
        public int getItemsCount() {
            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries[index];
        }
    }

    /**
     * Adapter for countries
     */
    private class typeAdapter extends AbstractWheelTextAdapter {

        // Countries names
        private String countries[] = TypeArray;

        /**
         * Constructor
         */
        typeAdapter(Context context) {
            super(context, R.layout.w_single_item_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            return super.getItem(index, cachedView, parent);
        }

        @Override
        public int getItemsCount() {
            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries[index];
        }
    }

    /**
     * @author bruce.z
     */
    private class AllCapTransformationMethod extends
            ReplacementTransformationMethod {

        @Override
        protected char[] getOriginal() {
            return new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                    'w', 'x', 'y', 'z'};
        }

        @Override
        protected char[] getReplacement() {
            return new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                    'W', 'X', 'Y', 'Z'};
        }

    }
}
