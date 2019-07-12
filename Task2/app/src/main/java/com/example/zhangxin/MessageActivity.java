package com.example.zhangxin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity {

    private TextView tv_title_message;
    private TextView tv_description_message;
    private TextView tv_time_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
        initComponent();
        initView();
    }

    void initComponent(){
        tv_title_message=findViewById(R.id.tv_title_message);
        tv_description_message=findViewById(R.id.tv_description_message);
        tv_time_message=findViewById(R.id.tv_time_message);
    }

    void initView(){
        Bundle bundle=getIntent().getExtras();
        assert bundle != null;
        tv_title_message.setText(bundle.getString("title","这是标题"));
        tv_description_message.setText(bundle.getString("description","这是描述"));
        tv_time_message.setText(bundle.getString("time","这是时间"));
    }

}
