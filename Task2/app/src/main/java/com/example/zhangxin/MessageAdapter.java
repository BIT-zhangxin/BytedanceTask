package com.example.zhangxin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private int resourceId;

    MessageAdapter(Context context, int textViewResourceId, List<Message> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Message message=getItem(position);
        @SuppressLint("ViewHolder") View view =
            LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

        TextView tv_title=view.findViewById(R.id.tv_title);
        TextView tv_description=view.findViewById(R.id.tv_description);
        TextView tv_time=view.findViewById(R.id.tv_time);

        assert message != null;
        tv_title.setText(message.getTitle());
        tv_description.setText(message.getDescription());
        tv_time.setText(getTimeString(message.getTime()));

        return view;
    }

    private String getTimeString(Timestamp time){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("HH:mm");

        String dateString = simpleDateFormat.format(time);
        return dateString;
    }
}
