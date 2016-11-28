package com.wptdxii.playground.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Call;

public class TagSelectActivity extends BaseActivity {

    public AlertDialog dialog;

    EditText et_add_tag;
    GridView mygridview;
    GridView availableGridViewTag;
    List<Integer> picturelist;
    List<String> list;
    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        private int maxLen = 6;

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int len = editable.length();
            if (len > maxLen) {
                int selEndIndex = Selection.getSelectionEnd(editable);
                String str = editable.toString();
                //截取新字符串
                String newStr = str.substring(0, maxLen);
                et_add_tag.setText(newStr);
                editable = et_add_tag.getText();
                //新字符串的长度
                int newLen = editable.length();
                //旧光标位置超过字符串长度
                if (selEndIndex > newLen) {
                    selEndIndex = editable.length();
                }
                //设置新光标所在的位置
                Selection.setSelection(editable, selEndIndex);
            }
        }
    };
    private MyAdapter adapter;
    private int listsize = 0;
    private ClearEditText et_tag;
    private TextView tag_add;
    private RelativeLayout tag_select_back;
    private TextView tv_text;
    private Button tag_select_submit;
    private String personal_specialty;
    private String setValue, user_id, token;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ArrayList<String> availableTags = new ArrayList<String>();
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            Log.d("455454", "455445" + errcode);
            if (errcode.equals("0")) {
                setValue = "";
                for (int i = 0; i < listsize; i++) {
                    setValue = setValue + list.get(i) + "|";
                }
                Editor editor3 = sp3.edit();
                editor3.putString("personal_specialty", setValue);
                editor3.commit();
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tag_select);

        personal_specialty = sp3.getString("personal_specialty", "");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        init();
        initEvent();
    }

    void init() {
        adapter = new MyAdapter(this);
        mygridview = (GridView) findViewById(R.id.mygridview);
        availableGridViewTag = (GridView) findViewById(R.id.gv_available_tag);
        tag_select_back = (RelativeLayout) findViewById(R.id.iv_back);
        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_text.setText("编辑个人标签");
        tag_select_submit = (Button) findViewById(R.id.btn_right);
        tag_select_submit.setText("保存");
        //默认选择项
        availableTags.add("车险");
        availableTags.add("重疾保障");
        availableTags.add("老年医疗");
        availableTags.add("少儿健康");
        availableTags.add("家庭保障计划");
        availableTags.add("教育储备金");
        availableTags.add("意外保障");
        availableTags.add("团意险");
        availableTags.add("+ 添加标签");
    }

    void personal_specialty() {
        int i = 0;
        String split = "|";
        StringTokenizer token = new StringTokenizer(personal_specialty, split);

        String[] Ins_Str = new String[token.countTokens()];

        while (token.hasMoreTokens()) {
            Ins_Str[i] = token.nextToken();
            i++;
        }

        Log.d("7777777777", i + "888");

        for (int n = 0; n < i && i <= 6; n++) {
            listsize = i;
            list.add(Ins_Str[n]);
        }

        adapter.setData(list);
        mygridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        MyAvailableTagAdapter tagAdapter = new MyAvailableTagAdapter(this);
        availableGridViewTag.setAdapter(tagAdapter);
    }

    void initEvent() {

        key_value.put("user_id", user_id);
        key_value.put("token", token);

        //保存
        tag_select_submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setValue = "";
                for (int i = 0; i < listsize; i++) {
                    setValue = setValue + list.get(i) + "|";
                }
                Log.d("545440", setValue);
                if (listsize == 0) {
                    setValue = "";
                }
                key_value.put("setValue", setValue);
                String PRODUCT_URL = IpConfig.getUri("setPersonalSpecialty");
                setdata(PRODUCT_URL);
            }
        });

        tag_select_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        list = new ArrayList<String>();

        personal_specialty();

        mygridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                listsize = listsize - 1;
                list.remove(position);
                adapter.setData(list);
                adapter.notifyDataSetChanged();
            }
        });

        availableGridViewTag.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                if (listsize >= 6) {
                    Toast.makeText(TagSelectActivity.this, "每人最多只能拥有六个标签", Toast.LENGTH_SHORT).show();
                } else {
                    if (position == availableTags.size() - 1) {
                        showAddTagDialog();
                    } else {
                        String currnentTag = availableTags.get(position);
                        for (int k = 0; k < list.size(); k++) {
                            String alreadyTag = list.get(k);
                            if (currnentTag.equals(alreadyTag)) {
                                Toast.makeText(TagSelectActivity.this, "不能重复添加同一标签", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        listsize = listsize + 1;
                        list.add(currnentTag);
                        adapter.setData(list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**
     * 弹出添加自定义标签对话框
     */
    private void showAddTagDialog() {
        //TODO  dialog
        AlertDialog.Builder build = new AlertDialog.Builder(TagSelectActivity.this);
        View contentView = View.inflate(TagSelectActivity.this, R.layout.dialog_add_customize_tag, null);
        Button btn_confirm = (Button) contentView.findViewById(R.id.btn_confirm);
        et_add_tag = (EditText) contentView.findViewById(R.id.et_add_customize_tag);
        et_add_tag.addTextChangedListener(mTextWatcher);
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = et_add_tag.getText().toString().trim();
                if (!TextUtils.isEmpty(tag)) {
                    listsize = listsize + 1;
                    list.add(tag);
                    adapter.setData(list);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Toast.makeText(TagSelectActivity.this, "个人标签不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog = build.create();
        dialog.setView(contentView, 0, 0, 0, 0);
        dialog.show();
    }

    private void setdata(String url) {
        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("error", "获取数据异常 ", e);
                    }

                    @Override
                    public void onResponse(String response) {


                        Map<String, String> map = new HashMap<String, String>();
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);

                        try {

                            // Log.d("44444", jsonString);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            String data = jsonObject.getString("data");

                            String errcode = jsonObject.getString("errcode");

                            map.put("errcode", errcode);
                            Log.d("44444", errcode);
                            Message message = Message.obtain();

                            message.obj = map;

                            handler.sendMessage(message);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }



    public class MyAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<String> list = null;

        public MyAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.gridview_item, null);
            } else {
                view = convertView;
            }

            TextView itemtext = (TextView) view.findViewById(R.id.itemtext);

            itemtext.setText(list.get(position));


            return view;
        }

    }

    public class MyAvailableTagAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<String> list = null;

        public MyAvailableTagAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return availableTags.size();
        }

        @Override
        public Object getItem(int position) {
            return availableTags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.available_tag_gridview_item, null);
            } else {
                view = convertView;
            }
            RelativeLayout rl_available_tag = (RelativeLayout) view.findViewById(R.id.rl_available_tag);
            TextView itemtext = (TextView) view.findViewById(R.id.itemtext);
            if (position == availableTags.size() - 1) {
                itemtext.setTextColor(getResources().getColor(R.color.blue1));
                int strokeColor = Color.parseColor("#2cbae7");//边框颜色
                GradientDrawable myGrad = (GradientDrawable) rl_available_tag.getBackground();
                myGrad.setStroke(Common.dip2px(TagSelectActivity.this, (float) 0.7), strokeColor);
            } else {
                itemtext.setTextColor(getResources().getColor(R.color.color6));
                int strokeColor = Color.parseColor("#e0e0e0");//边框颜色
                GradientDrawable myGrad = (GradientDrawable) rl_available_tag.getBackground();
                myGrad.setStroke(Common.dip2px(TagSelectActivity.this, (float) 0.7), strokeColor);
            }
            itemtext.setText(availableTags.get(position));
            return view;
        }
    }
}