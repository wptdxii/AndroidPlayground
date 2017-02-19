package com.cloudhome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.adapter.BankCardListAdapter;
import com.cloudhome.listener.PermissionListener;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.iosalertview.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MyBankCardInfoActivity extends BaseActivity implements OnClickListener {

    private RelativeLayout iv_back;
    private TextView top_title;
    private ImageView iv_right;
    private String user_id;
    private String token;
    private Map<String, String> key_value = new HashMap<String, String>();
    private ArrayList<Map<String, String>> cardList;
    private BankCardListAdapter adapter;


    private ImageView iv_bank_logo;
    private TextView tv_bank_name;
    private TextView tv_card_type;
    private TextView tv_bank_card_num;
    private TextView  phone_num;

    private TextView unbind_banknum;
    private RelativeLayout rl_top_item, tv_bank_card_rel;
    private String banktel = "";
    private HashMap<String, String> card_info;
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:


                    Toast.makeText(MyBankCardInfoActivity.this, "解绑成功",
                            Toast.LENGTH_SHORT).show();


                    MyWalletActivity.MyWalletRefresh=true;
                    //返回上一页
                    Intent dat=new Intent();
                    setResult(200, dat);

                    finish();
                    break;
                case 1:


                    Toast.makeText(MyBankCardInfoActivity.this, msg.obj + "",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 2:


                    Toast.makeText(MyBankCardInfoActivity.this, "网络连接失败，请确认网络连接后重试",
                            Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }
        }

    };


    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_bank_card_info);

        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");

        Intent intent = getIntent();
        card_info = (HashMap<String, String>) intent.getSerializableExtra("card_info");

        initView();
        initEvent();
        initData();

    }


    private void initView() {


        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        top_title = (TextView) findViewById(R.id.tv_text);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        top_title.setText("银行卡信息");
        iv_right.setVisibility(View.INVISIBLE);
        iv_back.setOnClickListener(this);


        iv_bank_logo = (ImageView) findViewById(R.id.iv_bank_logo);
        tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
        tv_card_type = (TextView) findViewById(R.id.tv_card_type);
        tv_bank_card_num = (TextView) findViewById(R.id.tv_bank_card_num);
        phone_num = (TextView) findViewById(R.id.phone_num);


        tv_bank_card_rel = (RelativeLayout) findViewById(R.id.tv_bank_card_rel);
        unbind_banknum = (TextView) findViewById(R.id.unbind_banknum);
        tv_bank_card_rel.setOnClickListener(this);
        unbind_banknum.setOnClickListener(this);
    }

    private void initData() {

        key_value.put("userId", user_id);
        key_value.put("token", token);

        Log.d("getBankcardList", url + "userId=" + user_id + "&token=" + token);

    }

    private void initEvent() {


        banktel = card_info.get("bankTel");

        String bindedId = card_info.get("id");
        String bankCardNo = card_info.get("bankCardNo");
        key_value.put("bindedId", bindedId);
        key_value.put("cardNo", bankCardNo);
        tv_bank_name.setText(card_info.get("bankName"));
        tv_card_type.setText(card_info.get("cardsType"));

        phone_num.setText(banktel);

        Log.i("bank_logo_img---------", IpConfig.getUri2("unBindBankCard") + "&cardNo"+bankCardNo + "&bindedId" + bindedId + "&userId" +user_id);

//		String card_num=map.get("bank_account");
//		String left_star=card_num.substring(0, card_num.length()-4);
//		left_star=left_star.replaceAll("[0-9]", "*");
//
//		Log.i("left_star---------------", left_star);
//		String right_num=card_num.substring(card_num.length()-4);


        String right_num=bankCardNo.substring(bankCardNo.length() - 4, bankCardNo.length());

        String left_star ="";
        for(int i=0;bankCardNo.length() - 4-i>0;i++)
        {
            if(i%4==0){
                left_star =" "+left_star;
            }
            left_star ="*"+left_star;

        }

        tv_bank_card_num.setText(left_star+ right_num);

        String url = card_info.get("bankLogoImg");
        Glide.with(MyBankCardInfoActivity.this)
                .load(url)
                .placeholder(R.drawable.white_bg)
                .error(R.drawable.bank_logo_moren)
                .into(iv_bank_logo);

    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();


                break;

            case R.id.tv_bank_card_rel:


                requestCallPhonePermission(banktel);

                break;

            case R.id.unbind_banknum:



                CustomDialog.Builder builder = new CustomDialog.Builder(
                        MyBankCardInfoActivity.this);

                builder.setTitle("提示");
                builder.setMessage("解除绑定后，该银行卡将被删除");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String Unbind_Banknum_Url = IpConfig.getUri2("unBindBankCard");


                                setUnbind_Banknum(Unbind_Banknum_Url);

                                dialog.dismiss();

                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                            }
                        });
                builder.create().show();





                break;
        }

    }

    private void requestCallPhonePermission(final String mobile) {
        String[] permissions = new String[]{android.Manifest.permission.CALL_PHONE};
        requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + mobile));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onDenied(String[] impermanentDeniedPermissions, String[] permanentDeniedPermissions) {
                showRequestPermissionRationale(
                        getString(R.string.msg_callphone_denied));
            }

            @Override
            public void onPermanentDenied(String[] permanentDeniedPermissions) {
                showPermissionSettingDialog(
                        getString(R.string.msg_callphone_permanent_denied));
            }
        });
    }

    private void setUnbind_Banknum(String url) {


        OkHttpUtils.post()
                .url(url)
                .params(key_value)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);

                        Message msg = Message.obtain();

                        msg.what = 2;

                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        try {

                            Log.d("9999", jsonString);
                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {

                                Message msg = Message.obtain();

                                msg.what = 2;

                                handler.sendMessage(msg);
                            } else {


                                JSONObject jsonObject = new JSONObject(jsonString);


                                String errcode = jsonObject.getString("errcode");


                                if (errcode.equals("0")) {
                                    Message message = Message.obtain();


                                    message.what = 0;

                                    handler.sendMessage(message);

                                } else {
                                    String errmsg = jsonObject.getString("errmsg");
                                    Message message = Message.obtain();


                                    message.what = 1;
                                    message.obj = errmsg;
                                    handler.sendMessage(message);
                                }


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }


}
