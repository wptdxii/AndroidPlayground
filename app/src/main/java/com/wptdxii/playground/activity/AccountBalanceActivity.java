package com.cloudhome.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;

/**
 * Created by yangguangbaoxian on 2016/5/16.
 */
public class AccountBalanceActivity extends BaseActivity implements View.OnClickListener {

    private AlertDialog dialog;
    private String balance;
    private String isdeposit;
    private String depositmsg;
    private TextView account_balance;
    private boolean isCardBinded;

    public static AccountBalanceActivity AccountBalanceinstance=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_balance);


        String user_id = sp.getString("Login_UID", "");
        String token = sp.getString("Login_TOKEN", "");

        Intent intent = getIntent();
        balance = intent.getStringExtra("balance");
        isdeposit = intent.getStringExtra("isdeposit");
        depositmsg = intent.getStringExtra("depositmsg");
        isCardBinded = intent.getBooleanExtra("isCardBinded", false);

        AccountBalanceinstance=this;

        initView();
        initData();
    }

    private void initView() {


        RelativeLayout iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        Button iv_right = (Button) findViewById(R.id.btn_right);
        TextView tv_text = (TextView) findViewById(R.id.tv_text);
        tv_text.setText("账户余额");
        iv_right.setText("收支明细");
        account_balance = (TextView) findViewById(R.id.account_balance);
        Button withdraw_cash = (Button) findViewById(R.id.withdraw_cash);

        iv_back.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        withdraw_cash.setOnClickListener(this);

    }

    private void initData() {
        account_balance.setText(balance);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_back:

                finish();

                break;

            case R.id.btn_right:

                intent = new Intent(AccountBalanceActivity.this, IncomeExpendDetailActivity.class);
                intent.putExtra("type", "全部");
                startActivity(intent);

                break;
            case R.id.withdraw_cash:

                if (!isdeposit.equals("true")) {
                    if (!isCardBinded) {
                        showAddBankDialog(depositmsg);
                    } else {
                        Toast.makeText(AccountBalanceActivity.this, depositmsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                intent = new Intent(AccountBalanceActivity.this, WithdrawActivity.class);
                startActivity(intent);
                break;
        }

    }


    private void showAddBankDialog(String note) {
        AlertDialog.Builder build = new AlertDialog.Builder(AccountBalanceActivity.this);
        View contentView = View.inflate(AccountBalanceActivity.this, R.layout.dialog_add_bank_insurance, null);
        TextView tv_note = (TextView) contentView.findViewById(R.id.tv_note);
        TextView tv_cancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        TextView tv_add_bank = (TextView) contentView.findViewById(R.id.tv_add_bank);
        tv_note.setText(note);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_add_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountBalanceActivity.this, BankCardEditActivity.class);
                intent.putExtra("isAdd", true);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog = build.create();
        dialog.setView(contentView, 0, 0, 0, 0);
        dialog.show();

    }

}
