package com.zhangxin.videoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.zhangxin.videoapp.R;
import com.zhangxin.videoapp.bean.Video;
import com.zhangxin.videoapp.player.MyVideoPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoPlayerActivity extends AppCompatActivity {

    private MyVideoPlayer myVideoPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Intent intent=getIntent();

        Video video=new Video();
        video.setImage_url(intent.getStringExtra("image_url"));
        video.setVideo_url(intent.getStringExtra("video_url"));

        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        myVideoPlayer=findViewById(R.id.myVideoPlayer);
        myVideoPlayer.setImageCover(video.getImage_url());
        myVideoPlayer.setVideoPath(video.getVideo_url());
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
