package com.zhangxin.videoapp.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.zhangxin.videoapp.R;
import com.zhangxin.videoapp.bean.Video;
import java.util.Random;


class VideoViewHolder extends RecyclerView.ViewHolder {

    private ImageView iv_cover;
    private TextView tv_title;
    private TextView tv_author;
    private TextView tv_time;


    VideoViewHolder(@NonNull View view) {
        super(view);
        iv_cover=view.findViewById(R.id.iv_cover);
        tv_title=view.findViewById(R.id.tv_title);
        tv_author=view.findViewById(R.id.tv_author);
        tv_time=view.findViewById(R.id.tv_time);
    }

    void bind(Video video){
        Glide.with(iv_cover.getContext()).load(video.getImage_url()).into(iv_cover);
        //TODO：判空，加载默认图片，加载圆角
        tv_title.setText("这是学号为"+video.getStudent_id()+"的"+video.getUser_name()+"所创作的视频");
        tv_author.setText(video.getUser_name());
        tv_time.setText(getRandomTimeString());
    }

    private String getRandomTimeString(){
        String[] strings=new String[]{
            "刚刚","5分钟前",
            "10分钟前","半小时前",
            "1小时前","5小时前",
            "10小时前","1天前",
            "10天前","3个月前",
            "1年前","1世纪前"};

        Random random=new Random();
        return strings[random.nextInt(strings.length)];
    }

}
