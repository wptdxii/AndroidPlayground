package com.cloudhome.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.event.BindCardSuccessEvent;
import com.cloudhome.event.WithdrawSuccessEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangguangbaoxian on 2016/5/16.
 */
public class AccountBalanceActivity extends BaseActivity {
    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_BALANCE = "balance";
    private static final String EXTRA_DEPOSIT = "isdeposit";
    private static final String EXTRA_DEPOSITMSG = "depositmsg";
    private static final String EXTRA_BINDED = "isCardBinded";
    private static final String EXTRA_ADD = "isAdd";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.tv_balance_num)
    TextView tvBalanceNum;
    private String mBalanceNum;
    private String mIsdeposit;
    private String mDepositMsg;
    private boolean mIsCardBinded;
    private AlertDialog mAddBankCardDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_balance);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mBalanceNum = intent.getStringExtra(EXTRA_BALANCE);
        mIsdeposit = intent.getStringExtra(EXTRA_DEPOSIT);
        mDepositMsg = intent.getStringExtra(EXTRA_DEPOSITMSG);
        mIsCardBinded = intent.getBooleanExtra(EXTRA_BINDED, false);
    }

    private void initView() {
        tvTitle.setText(R.string.activity_account_balance_balance);
        btnAction.setText(R.string.activity_account_balance_detail);
        tvBalanceNum.setText(mBalanceNum);
    }


    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.btn_action)
    public void checkDetail() {
        String type = "全部";
        Intent intent = new Intent(AccountBalanceActivity.this, IncomeExpendDetailActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        startActivity(intent);
    }

    @OnClick(R.id.btn_withdraw_deposit)
    public void withDrawDeposit() {
        if (!"true".equals(mIsdeposit)) {
            if (!mIsCardBinded) {
                showAddBankDialog(mDepositMsg);
            } else {
                Toast.makeText(AccountBalanceActivity.this, mDepositMsg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent(AccountBalanceActivity.this, WithdrawActivity.class);
            startActivity(intent);
        }
    }

    private void showAddBankDialog(String note) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AccountBalanceActivity.this);
        View contentView = View.inflate(AccountBalanceActivity.this, R.layout.dialog_add_bank_insurance, null);
        TextView tvNote = (TextView) contentView.findViewById(R.id.tv_note);
        TextView tvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        TextView tvAddBank = (TextView) contentView.findViewById(R.id.tv_add_bank);
        tvNote.setText(note);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddBankCardDialog.dismiss();
            }
        });
        tvAddBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountBalanceActivity.this, BankCardEditActivity.class);
                intent.putExtra(EXTRA_ADD, true);
                startActivity(intent);
                mAddBankCardDialog.dismiss();
            }
        });
        mAddBankCardDialog = dialogBuilder.create();
        mAddBankCardDialog.setView(contentView, 0, 0, 0, 0);
        mAddBankCardDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BindCardSuccessEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WithdrawSuccessEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
