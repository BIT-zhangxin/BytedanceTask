package com.example.zhangxin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Exercise1Activity extends AppCompatActivity {

    /*
    * 第一题输出结果
    *
    * 开启应用
    * onCreate
    * onStart
    * onResume
    *
    * 旋转屏幕
    * onPause
    * onStop
    * onDestroy
    * onCreate
    * onStart
    * onResume
    *
    * 旋转回来
    * onPause
    * onStop
    * onDestroy
    * onCreate
    * onStart
    * onResume
    *
    * 关闭应用
    * onPause
    * onStop
    * onDestroy
    * */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise1_layout);
        Log.d("测试输出","onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("测试输出","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("测试输出","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("测试输出","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("测试输出","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("测试输出","onDestroy");
    }
}
