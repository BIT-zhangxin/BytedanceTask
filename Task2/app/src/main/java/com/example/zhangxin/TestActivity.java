package com.example.zhangxin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
