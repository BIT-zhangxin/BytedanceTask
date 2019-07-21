package com.zhangxin.videoapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.zhangxin.videoapp.R;
import com.zhangxin.videoapp.bean.Video;
import com.zhangxin.videoapp.player.MyVideoPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoPlayerActivity extends AppCompatActivity {

    private MyVideoPlayer myVideoPlayer;

    private String image_url;
    private String video_url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Intent intent=getIntent();

        image_url=intent.getStringExtra("image_url");
        video_url=intent.getStringExtra("video_url");

        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        myVideoPlayer=findViewById(R.id.myVideoPlayer);
        if(image_url!=null){
            myVideoPlayer.setImageCover(image_url);
        }
        myVideoPlayer.setVideoPath(video_url);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myVideoPlayer.isPlaying()) {
            myVideoPlayer.stop();
        }
        myVideoPlayer.release();
        IjkMediaPlayer.native_profileEnd();
    }
}
