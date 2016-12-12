package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.cloudhome.view.sortlistview.ClearEditText;
import com.gghl.view.wheelcity.OnWheelChangedListener;
import com.gghl.view.wheelcity.WheelView;
import com.gghl.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.zf.iosdialog.widget.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class C_InfoEditActivity extends BaseActivity {


    private RelativeLayout customer_info_back;
    private TextView tv_text;
    private Map<String, String> key_value = new HashMap<String, String>();
    private TextView c_i_name;
    private TextView c_i_sex;
    private TextView c_i_date;
    private ClearEditText c_i_income;
    private TextWatcher incomeWatcher = new TextWatcher() {


        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub


            if (s.toString().contains(".")) {
                if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                    s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + 3);
                    c_i_income.setText(s);
                    c_i_income.setSelection(s.length());
                }
            }


//        	  if (s.toString().contains(".")) {
//
//
//                  if (s.length() - 1 - s.toString().indexOf(".") >2) {
//
//
//                      s = s.toString().subSequence(0,s.toString().indexOf(".") + 3);
//
//                     // s.indexOf("key")
//
//
//                    c_i_income.setText(s);
//                    c_i_income.setSelection(s.length());
//                      }
//                  else if(0<=s.length() - 1 - s.toString().indexOf(".")&&s.length() - 1 - s.toString().indexOf(".") <=2)
//                    	{
//                        	String incomeedit = s.toString();
//
//
//                        	String i_last= incomeedit.substring(incomeedit.length()-1,incomeedit.length());
//                        	//String i_last =incomeedit.last
//                	  if(i_last.equals("."))
//                			  {
//                		  c_i_income.setText(incomeedit.substring(0,incomeedit.length()-1));
//
//                		  Editable editable = c_i_income.getText();
//           			 int selEndIndex = Selection.getSelectionEnd(editable);
//           			 selEndIndex = editable.length();
//         			 Selection.setSelection(editable, selEndIndex);
//                      //    c_i_income.setSelection(s.length()-1);
//                			  }
//
//                          }
//
//
//                  }
//


            if (s.toString().trim().equals(".")) {
                s = "0" + s;
                c_i_income.setText(s);
                c_i_income.setSelection(2);
            }

            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(".")) {
                    c_i_income.setText(s.subSequence(0, 1));
                    c_i_income.setSelection(1);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

    };
    private TextView c_i_social_security;
    private TextView c_i_health;
    private TextView c_i_type;
    private TextView c_i_num;
    private TextView c_i_career;
    private ClearEditText c_i_phonenum;
    private ClearEditText c_i_email;
    private ClearEditText c_i_remarks;
    private TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        private int maxLen = 300;

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
            temp = s;


        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2,
                                  int arg3) {


        }


        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub


            // Editable editable = profile_edit.getText();
            int len = editable.length();

            if (len > maxLen) {
                int selEndIndex = Selection.getSelectionEnd(editable);
                String str = editable.toString();
                //截取新字符串
                String newStr = str.substring(0, maxLen);
                c_i_remarks.setText(newStr);
                editable = c_i_remarks.getText();

                //新字符串的长度
                int newLen = editable.length();
                //旧光标位置超过字符串长度
                if (selEndIndex > newLen) {
                    selEndIndex = editable.length();
                }
                //设置新光标所在的位置
                Selection.setSelection(editable, selEndIndex);


                Toast.makeText(C_InfoEditActivity.this, "输入的字数不能超过300字",
                        Toast.LENGTH_SHORT).show();
//			        	CustomDialog.Builder builder = new CustomDialog.Builder(
//			        			Profile_EditActivity.this);
//
//						builder.setTitle("提示");
//						builder.setMessage("输入的字数不能超过200字");
//
//						builder.setPositiveButton("确定",
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int which) {
//										dialog.dismiss();
//
//									}
//								});
//						builder.create().show();


            }
        }
    };
    private Button customer_info_submit;
    private Handler mHandler;
    private Boolean Dothas = false;
    private String user_id;
    private String token;
    private String customer_id;
    private String[] JobArray;
    private String[] JobCodeArray;
    private String[] SocialArray = {"未知", "有", "无"};
    private String[] SocialCodeArray = {"00", "01", "02"};
    private String[] HealthArray = {"未知", "超优", "优选", "优标", "标准", "次优", "次标"};
    private String[] HealthCodeArray = {"00", "01", "02", "03", "04", "11", "12"};
    private String name, sex, birthday, income, medicare, medicare_code,
            health, health_code, id_type, idno, job, job_code, mobile, email,
            remark;
    private Dialog dialog;
    private List<Map<String, Object>> resultdata = new ArrayList<Map<String, Object>>();
    private Handler errcode_handler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;

            dialog.dismiss();
            // String errcode = data;
            Log.d("455454", "455445" + (String) msg.obj);
            if (((String) msg.obj).equals("false")) {

                Toast.makeText(C_InfoEditActivity.this, "网络连接失败，请确认网络连接后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errcode = data.get("errcode");

            if (errcode.equals("0")) {


                finish();
            } else {
                String errmsg = data.get("errmsg");


                CustomDialog.Builder builder = new CustomDialog.Builder(
                        C_InfoEditActivity.this);

                builder.setTitle("提示");
                builder.setMessage(errmsg);
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                            }
                        });
                builder.create().show();

            }
            // m_d_content.setText(content);

        }

    };
    private Handler job_handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            Map<String, String> data = (Map<String, String>) msg.obj;

            dialog.dismiss();
            String code = data.get("code");
            String jobname = data.get("jobname");
            int i = 0, j = 0;
            String split = "|";
            StringTokenizer token = new StringTokenizer(jobname, split);
            StringTokenizer token2 = new StringTokenizer(code, split);
            JobArray = new String[token.countTokens()];
            JobCodeArray = new String[token2.countTokens()];

            while (token.hasMoreTokens()) {

                JobArray[i] = token.nextToken();
                System.out.println(JobArray[i]);
                i++;
            }

            while (token2.hasMoreTokens()) {

                JobCodeArray[j] = token2.nextToken();
                System.out.println(JobCodeArray[j]);
                j++;
            }

            c_i_career.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    if (JobArray.length > 0) {
                        View view = dialogm();
                        final MyAlertDialog dialog = new MyAlertDialog(
                                C_InfoEditActivity.this)
                                .builder()
                                .setTitle("请选择")
                                        // .setMsg("")
                                        // .setEditText("111")
                                .setView(view)
                                .setPositiveButton("确定", new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        c_i_career.setText(job);

                                        key_value.put("job", job_code);

                                    }
                                });
                        dialog.show();
                    }
                }
            });

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.c_info_edit);
        Intent intent = getIntent();

        name = intent.getStringExtra("name");

        sex = intent.getStringExtra("sex");
        birthday = intent.getStringExtra("birthday");
        income = intent.getStringExtra("income");
        medicare = intent.getStringExtra("medicare");
        medicare_code = intent.getStringExtra("medicare_code");
        health = intent.getStringExtra("health");
        health_code = intent.getStringExtra("health_code");
        id_type = intent.getStringExtra("id_type");
        idno = intent.getStringExtra("idno");

        job = intent.getStringExtra("job");
        job_code = intent.getStringExtra("job_code");
        if(TextUtils.isEmpty(job_code)){
            job_code="";
        }
        mobile = intent.getStringExtra("mobile");
        email = intent.getStringExtra("email");
        remark = intent.getStringExtra("remark");

        customer_id = intent.getStringExtra("customer_id");

        Log.d("4545", customer_id);
        init();
        Infoinit();

        initEvent();

    }



    private void init() {

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        customer_info_back = (RelativeLayout) findViewById(R.id.iv_back);
        tv_text= (TextView) findViewById(R.id.tv_text);
        tv_text.setText("编辑客户信息");


        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");

        mHandler = new Handler();
    }

    private void Infoinit() {

        customer_info_submit = (Button) findViewById(R.id.btn_right);
        customer_info_submit.setText("保存");
        c_i_name = (TextView) findViewById(R.id.c_i_name);
        c_i_sex = (TextView) findViewById(R.id.c_i_sex);
        c_i_date = (TextView) findViewById(R.id.c_i_date);
        c_i_income = (ClearEditText) findViewById(R.id.c_i_income);
        c_i_income.addTextChangedListener(incomeWatcher);
        c_i_social_security = (TextView) findViewById(R.id.c_i_social_security);
        c_i_health = (TextView) findViewById(R.id.c_i_health);
        c_i_type = (TextView) findViewById(R.id.c_i_type);
        c_i_num = (TextView) findViewById(R.id.c_i_num);
        c_i_career = (TextView) findViewById(R.id.c_i_career);
        c_i_phonenum = (ClearEditText) findViewById(R.id.c_i_phonenum);
        c_i_email = (ClearEditText) findViewById(R.id.c_i_email);
        c_i_remarks = (ClearEditText) findViewById(R.id.c_i_remarks);
        c_i_remarks.addTextChangedListener(mTextWatcher);

        if (sex == null || sex.equals("null") || sex.equals("")) {
            c_i_sex.setText("");
        } else {
            c_i_sex.setText(sex);
        }
        c_i_date.setText(birthday);

        c_i_income.setText(income);

        Editable editable2 = c_i_income.getText();
        int selEndIndex2 = Selection.getSelectionEnd(editable2);
        selEndIndex2 = editable2.length();
        Selection.setSelection(editable2, selEndIndex2);

        c_i_social_security.setText(medicare);

        c_i_health.setText(health);
        c_i_type.setText(id_type);

        c_i_num.setText(idno);
        c_i_career.setText(job);
        c_i_phonenum.setText(mobile);
        c_i_email.setText(email);


        c_i_remarks.setText(remark);
        Editable editable = c_i_remarks.getText();
        int selEndIndex = Selection.getSelectionEnd(editable);
        selEndIndex = editable.length();
        Selection.setSelection(editable, selEndIndex);


    }

    private void initEvent() {

        Log.d("user_id", user_id);
        Log.d("token", token);
        key_value.put("customer_id", customer_id);
        key_value.put("user_id", user_id);
        key_value.put("token", token);

        key_value.put("health", health_code);
        key_value.put("medicare", medicare_code);
        key_value.put("job", job_code);
        Log.i("bug--customer_id", customer_id);
        Log.i("bug--user_id", user_id);
        Log.i("bug--token", token);

        dialog.show();
        final String joburl = IpConfig.getUri("getJob");

        setjobcode(joburl);

        customer_info_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        customer_info_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String phonenum = c_i_phonenum.getText().toString();
                String emailstr = c_i_email.getText().toString();
                String incomestr = c_i_income.getText().toString();
                String remarkstr = c_i_remarks.getText().toString();
                int p_Length = phonenum.length();

                int e_Length = emailstr.length();
                if (p_Length > 0 && p_Length != 11) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            C_InfoEditActivity.this);

                    builder.setTitle("提示");
                    builder.setMessage("请输入正确的电话号码");


                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else if (e_Length > 0 && !isEmail(emailstr)) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            C_InfoEditActivity.this);

                    builder.setTitle("提示");
                    builder.setMessage("请输入正确的邮箱地址");


                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {

                    key_value.put("income", incomestr);
                    key_value.put("email", emailstr);
                    key_value.put("tel", phonenum);
                    key_value.put("remark", remarkstr);

                    final String url = IpConfig.getUri("updateCustomerInfo");

                    setinfo(url);

                }

            }
        });

        c_i_health.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                View view = health_dialogm();
                final MyAlertDialog dialog = new MyAlertDialog(
                        C_InfoEditActivity.this)
                        .builder()
                        .setTitle("请选择")
                                // .setMsg("")
                                // .setEditText("111")
                        .setView(view)
                        .setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                c_i_health.setText(health);

                                key_value.put("health", health_code);

                            }
                        });
                dialog.show();
            }
        });

        c_i_social_security.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                View view = social_dialogm();
                final MyAlertDialog dialog = new MyAlertDialog(
                        C_InfoEditActivity.this)
                        .builder()
                        .setTitle("请选择")
                                // .setMsg("")
                                // .setEditText("111")
                        .setView(view)
                        .setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                c_i_social_security.setText(medicare);

                                key_value.put("medicare", medicare_code);

                            }
                        });
                dialog.show();
            }
        });
    }

    @SuppressLint("InflateParams")
    private View dialogm() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_single_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_single);
        country.setVisibleItems(1);
        country.setViewAdapter(new JobAdapter(this));
        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                job = JobArray[country.getCurrentItem()];
                job_code = JobCodeArray[country.getCurrentItem()];

            }
        });

        country.setCurrentItem(0);

        job = JobArray[country.getCurrentItem()];
        job_code = JobCodeArray[country.getCurrentItem()];
        return contentView;
    }

    @SuppressLint("InflateParams")
    private View social_dialogm() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_single_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_single);
        country.setVisibleItems(1);
        country.setViewAdapter(new SocialAdapter(this));
        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                medicare = SocialArray[country.getCurrentItem()];
                medicare_code = SocialCodeArray[country.getCurrentItem()];

            }
        });

        country.setCurrentItem(0);

        medicare = SocialArray[country.getCurrentItem()];
        medicare_code = SocialCodeArray[country.getCurrentItem()];
        return contentView;
    }

    private void setinfo(String url) {

        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);
                        String status = "false";
                        Message message = Message.obtain();

                        message.obj = status;

                        errcode_handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        Map<String, String> map = new HashMap<String, String>();
                        try {

                            Log.d("44444", jsonString);

                            if (jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {

                                JSONObject jsonObject = new JSONObject(jsonString);

                                String errcode = jsonObject.getString("errcode");
                                String errmsg = jsonObject.getString("errmsg");
                                map.put("errcode", errcode);
                                map.put("errmsg", errmsg);

                                Message message = Message.obtain();

                                message.obj = map;

                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    @SuppressLint("InflateParams")
    private View health_dialogm() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.wheelcity_single_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_single);
        country.setVisibleItems(1);
        country.setViewAdapter(new HealthAdapter(this));
        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                health = HealthArray[country.getCurrentItem()];
                health_code = HealthCodeArray[country.getCurrentItem()];

            }
        });

        country.setCurrentItem(0);

        health = HealthArray[country.getCurrentItem()];
        health_code = HealthCodeArray[country.getCurrentItem()];
        return contentView;
    }

    private void setjobcode(String url) {


        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);
                        String status = "false";
                        Message message = Message.obtain();
                        message.obj = status;
                        errcode_handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        try {

                            Log.d("44444", jsonString);
                            if (jsonString.equals("") || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray dataList = jsonObject
                                        .getJSONArray("data");
                                String code = "";
                                String jobname = "";
                                for (int i = 0; i < dataList.length(); i++) {
                                    JSONObject jsonObject2 = dataList
                                            .getJSONObject(i);

                                    code = code + "|"
                                            + jsonObject2.getString("code");
                                    jobname = jobname + "|"
                                            + jsonObject2.getString("name");
                                }
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("code", code);
                                map.put("jobname", jobname);
                                Message message = Message.obtain();

                                message.obj = map;

                                job_handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    private boolean isEmail(String email) {

        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

        Pattern p = Pattern.compile(str);

        Matcher m = p.matcher(email);

        return m.matches();

    }

    /**
     * Adapter for countries
     */
    private class JobAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] = JobArray;

        /**
         * Constructor
         */
        JobAdapter(Context context) {
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
     * Adapter for countries
     */
    private class SocialAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] = SocialArray;

        /**
         * Constructor
         */
        SocialAdapter(Context context) {
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
     * Adapter for countries
     */
    private class HealthAdapter extends AbstractWheelTextAdapter {

        // Countries names
        private String countries[] = HealthArray;

        /**
         * Constructor
         */
        HealthAdapter(Context context) {
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
}
