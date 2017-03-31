package com.cloudhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;

import static com.cloudhome.R.drawable.completed;

public class TaskInstructionActivity extends BaseActivity {
    private TextView tv_task_instruction;
    private String instructionStr="";
    private RelativeLayout iv_back;
    private TextView tv_text;
    private RelativeLayout rl_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_instruction);
        iv_back= (RelativeLayout) findViewById(R.id.rl_back);
        tv_text= (TextView) findViewById(R.id.tv_title);
        rl_right=(RelativeLayout) findViewById(R.id.rl_share);
        rl_right.setVisibility(View.INVISIBLE);
        tv_text.setText("任务说明");
        tv_task_instruction= (TextView) findViewById(R.id.tv_task_instruction);
        tv_task_instruction.setMovementMethod(ScrollingMovementMethod.getInstance());
        Intent intent=getIntent();
        instructionStr=intent.getStringExtra("instructionStr");
        instructionStr=instructionStr.replace("?", "\n").replace("###", "       ");
        tv_task_instruction.setText(instructionStr);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
