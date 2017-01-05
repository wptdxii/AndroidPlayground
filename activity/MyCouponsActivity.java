package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.adapter.MyCouponAdapter;
import com.cloudhome.application.MyApplication;
import com.cloudhome.bean.UserPrizeBean;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.AddContactMsg;
import com.cloudhome.network.GetUserPrize;
import com.cloudhome.utils.Common;
import com.cloudhome.utils.RegexUtils;
import com.cloudhome.view.customview.QianDaoDialog;

import java.util.ArrayList;

public class MyCouponsActivity extends BaseActivity implements View.OnClickListener,NetResultListener {

    public static final int GET_USER_PRIZE = 1;
    public static final int ADD_CONTACT_MSG = 2;
    private RelativeLayout rl_back;
    private TextView tv_text;
    private RelativeLayout rl_right;
    private ListView lv_my_coupon;
    private GetUserPrize getUserPrize;
    private AddContactMsg addContactMsg;
    private ArrayList<UserPrizeBean> list;
    private String loginString;
    private String user_id;
    private String token;
    private TextView tv_earn_coupon;
    private TextView look_old_coupon;
    private QianDaoDialog builder;
    private MyCouponAdapter myCouponAdapter;
    private RelativeLayout no_ticket;
    private ImageView iv_get_ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupons);
        loginString = sp.getString("Login_STATE", "none");
        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        initView();
        initData();
    }

    private void initView() {
        rl_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        tv_text = (TextView) findViewById(R.id.tv_text);
        rl_back.setOnClickListener(this);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text.setText("我的礼券");
        no_ticket= (RelativeLayout) findViewById(R.id.no_ticket);
        iv_get_ticket= (ImageView) findViewById(R.id.iv_get_ticket);
        lv_my_coupon = (ListView) findViewById(R.id.lv_my_coupon);
        tv_earn_coupon=(TextView) findViewById(R.id.tv_earn_coupon);
        look_old_coupon=(TextView) findViewById(R.id.look_old_coupon);
        tv_earn_coupon.setOnClickListener(this);
        look_old_coupon.setOnClickListener(this);
        iv_get_ticket.setOnClickListener(this);

    }

    private void initData() {
        list = new ArrayList<UserPrizeBean>();
        getUserPrize = new GetUserPrize(this);
        getUserPrize.execute(user_id, list, GET_USER_PRIZE,token,"no");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_earn_coupon:
                finish();
                break;
            case R.id.look_old_coupon:
                Intent intent =new Intent (MyCouponsActivity.this,OldCouponActivity.class);
                startActivityForResult(intent,100);
                break;
            case R.id.iv_get_ticket:
                finish();
                break;
        }
    }

//    弹出领取礼券
    public void showGetCouponPop(final UserPrizeBean bean){
        View contentView = View.inflate(this,R.layout.dialog_get_coupon,null);
        TextView tv_title= (TextView) contentView.findViewById(R.id.tv_title);
        tv_title.setText("恭喜您，获得"+bean.getMouldName()+"!");
        final EditText et_name=(EditText) contentView.findViewById(R.id.et_name);
        final EditText et_phonenum=(EditText) contentView.findViewById(R.id.et_phonenum);
        final EditText et_addresss=(EditText) contentView.findViewById(R.id.et_addresss);
        ImageView iv_get_coupon= (ImageView) contentView.findViewById(R.id.iv_get_coupon);
        RelativeLayout rl_close= (RelativeLayout) contentView.findViewById(R.id.rl_close);
        iv_get_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=et_name.getText().toString().trim();
                String phone=et_phonenum.getText().toString().trim();
                String address=et_addresss.getText().toString().trim();
                if(name.contains("&")){
                    Toast.makeText(MyCouponsActivity.this,"请填写正确的姓名",Toast.LENGTH_SHORT).show();
                }else if("".equals(name)){
                    Toast.makeText(MyCouponsActivity.this,"请填写收件人的姓名",Toast.LENGTH_SHORT).show();
                }else if("".equals(phone)){
                    Toast.makeText(MyCouponsActivity.this,"请填写收件人的联系方式",Toast.LENGTH_SHORT).show();
                }else if(!RegexUtils.isMobileNO(phone)){
                    Toast.makeText(MyCouponsActivity.this,"请正确填写联系方式",Toast.LENGTH_SHORT).show();
                }else if("".equals(address)){
                    Toast.makeText(MyCouponsActivity.this,"请填写收件人的联系地址",Toast.LENGTH_SHORT).show();
                }else{
                    addContactMsg=new AddContactMsg(MyCouponsActivity.this);
                    addContactMsg.execute(name,phone,address,bean.getId(),ADD_CONTACT_MSG);
                }
            }
        });
        rl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        builder = new QianDaoDialog(this,contentView, Common.dip2px(this,310),
                Common.dip2px(this,375),R.style.qiandao_dialog);
        builder.setCanceledOnTouchOutside(false);
        builder.setCancelable(false);
        builder.show();

    }

    @Override
    public void ReceiveData(int action, int flag, Object dataObj) {
        switch (action) {
            case GET_USER_PRIZE:
                if (flag == MyApplication.DATA_OK) {
                    String errmsg=dataObj.toString();
                    if(list.size()>0){
                        myCouponAdapter=new MyCouponAdapter(list, MyCouponsActivity.this,1);
                        lv_my_coupon.setAdapter(myCouponAdapter);
                    }else{
                        lv_my_coupon.setVisibility(View.GONE);
                        no_ticket.setVisibility(View.VISIBLE);
                    }

                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(MyCouponsActivity.this,"网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                    lv_my_coupon.setVisibility(View.GONE);
                    no_ticket.setVisibility(View.VISIBLE);
                } else if (flag == MyApplication.DATA_EMPTY) {
                    lv_my_coupon.setVisibility(View.GONE);
                    no_ticket.setVisibility(View.VISIBLE);
                } else if (flag == MyApplication.JSON_ERROR) {
                    lv_my_coupon.setVisibility(View.GONE);
                    no_ticket.setVisibility(View.VISIBLE);
                } else if (flag == MyApplication.DATA_ERROR) {
                    String errmsg=dataObj.toString();
                    Toast.makeText(MyCouponsActivity.this,errmsg,Toast.LENGTH_SHORT).show();
                    lv_my_coupon.setVisibility(View.GONE);
                    no_ticket.setVisibility(View.VISIBLE);
                }
                break;
            case ADD_CONTACT_MSG:
                if (flag == MyApplication.DATA_OK) {
                    Toast.makeText(MyCouponsActivity.this,"领取成功",Toast.LENGTH_SHORT).show();
                    if(builder.isShowing()){
                        builder.dismiss();
                    }
                    //重新刷新列表
                    initData();
                } else if (flag == MyApplication.NET_ERROR) {
                    Toast.makeText(MyCouponsActivity.this,"网络连接失败，请确认网络连接后重试",Toast.LENGTH_SHORT).show();
                } else if (flag == MyApplication.DATA_EMPTY) {
                } else if (flag == MyApplication.JSON_ERROR) {
                } else if (flag == MyApplication.DATA_ERROR) {
                    String errmsg=dataObj.toString();
                    Toast.makeText(MyCouponsActivity.this,errmsg,Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100&&resultCode==200){
            finish();
        }
    }
}
