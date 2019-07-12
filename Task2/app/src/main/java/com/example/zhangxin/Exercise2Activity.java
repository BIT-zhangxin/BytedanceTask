package com.example.zhangxin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Exercise2Activity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout linearLayout;
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise2_layout);
        linearLayout=findViewById(R.id.linearLayout);
        button=findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                int num=getViewCount(linearLayout);
                Log.d("测试结果","num:"+num);
                Toast.makeText(this,"num:"+num,Toast.LENGTH_LONG).show();
                break;
        }
    }

    //计算一个view下的所有view个数
    public static int getViewCount(View view){
        int num = 1;
        if(view instanceof ViewGroup){
            num = getCountComponent((ViewGroup) view,0);
        }
        return num;
    }

    //递归函数，结算viewGroup的view个数
    private static int getCountComponent(ViewGroup v,int num){
        int t = v.getChildCount();
        for (int i = 0; i < t; i++) {
            View tmp = v.getChildAt(i);
            if (tmp instanceof ViewGroup) {
                num = getCountComponent((ViewGroup) tmp, num);
            }
        }
        return num + t;
    }
}
