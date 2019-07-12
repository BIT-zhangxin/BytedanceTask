package com.example.zhangxin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Exercise3Activity extends AppCompatActivity {

    ListView listView;
    List<Message> messageList =new ArrayList<>();

    public static final int MESSAGE_REQUEST_CODE=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise3_layout);
        initComponent();
        iniData();
        initView();
        initEvent();
    }

    void initComponent(){
        listView=findViewById(R.id.list_view_message);
    }

    void iniData(){

        for(int i=0;i<30;i++){
            Message message=new Message();
            message.setId(i);
            message.setTitle("标题"+i);
            message.setDescription("描述"+i);
            message.setTime(new Timestamp(System.currentTimeMillis()));
            messageList.add(message);
        }
    }

    void initView(){
        MessageAdapter messageAdapter=new MessageAdapter(this,R.layout.message_item,messageList);
        listView.setAdapter(messageAdapter);
    }

    void initEvent(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(Exercise3Activity.this,MessageActivity.class);
                intent.putExtras(messageBundle(messageList.get(position)));
                startActivityForResult(intent,MESSAGE_REQUEST_CODE);
            }
        });
    }

    Bundle messageBundle(Message message){
        Bundle bundle=new Bundle();
        bundle.putInt("id",message.getId());
        bundle.putString("title",message.getTitle());
        bundle.putString("description",message.getDescription());
        bundle.putString("time",message.getTime().toString());
        return bundle;
    }
}
