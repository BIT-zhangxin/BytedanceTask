package com.example.chapter3.homework;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class FriendAdapter extends ArrayAdapter<Friend> {

    private int resourceId;

    FriendAdapter(Context context, int textViewResourceId, List<Friend> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Friend friend =getItem(position);
        View view;
        if(convertView!=null){
            view=convertView;
        }
        else{
            view=LayoutInflater.from(
                getContext()).inflate(resourceId, parent,false);
        }

        TextView tv_title=view.findViewById(R.id.tv_title);
        TextView tv_description=view.findViewById(R.id.tv_description);

        assert friend != null;
        tv_title.setText(friend.getTitle());
        tv_description.setText(friend.getDescription());

        return view;
    }
}
