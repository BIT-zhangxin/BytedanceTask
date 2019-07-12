package com.example.zhangxin.exercise4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.zhangxin.R;

public class A2 extends Activity implements View.OnClickListener {

    /*
     *
     * 练习4的答案在TaskResult文件夹中
     *
     * */

    Button button1;
    Button button2;
    Button button3;
    Button button4;

    void initComponent(){
        button1=findViewById(R.id.btn_ex_1);
        button2=findViewById(R.id.btn_ex_2);
        button3=findViewById(R.id.btn_ex_3);
        button4=findViewById(R.id.btn_ex_4);
    }

    void initEvent(){
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_ex_1:
                intent=new Intent(this,A1.class);
                startActivity(intent);
                break;
            case R.id.btn_ex_2:
                intent=new Intent(this,A2.class);
                startActivity(intent);
                break;
            case R.id.btn_ex_3:
                intent=new Intent(this,A3.class);
                startActivity(intent);
                break;
            case R.id.btn_ex_4:
                intent=new Intent(this,A4.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise4_layout);
        initComponent();
        initEvent();
        Log.d("测试输出","A2 onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("测试输出","A2 onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("测试输出","A2 onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("测试输出","A2 onDestroy");
    }
}
