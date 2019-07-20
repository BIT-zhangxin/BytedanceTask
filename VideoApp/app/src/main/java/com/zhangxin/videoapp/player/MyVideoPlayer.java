package com.zhangxin.videoapp.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.zhangxin.videoapp.R;
import java.io.IOException;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MyVideoPlayer extends RelativeLayout {

    //由ijkplayer提供，用于播放视频
    private IMediaPlayer mMediaPlayer = null;

    //视频文件地址
    private String mPath = "";
    private int resId = 0;

    private FrameLayout frameLayout_body;
    private ImageView imageView_cover;
    private SurfaceView surfaceView;
    private Button button_pause;
    private SeekBar seekBar_progress;
    private TextView textView_duration;
    private Button button_fullscreen;

    private boolean isFirstPlay=true;
    private boolean isShown;
    int videoDuration;//视频长度，单位为秒

    private VideoPlayerListener listener;
    private Context mContext;

    public static final int SECOND_CHANGE =1023;

    //计时器，间隔一定时间进行一次操作
    @SuppressLint("HandlerLeak")
    private Handler secondChangeHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SECOND_CHANGE) {
                progressChanged(true);
                secondChangeHandler.sendEmptyMessageDelayed(SECOND_CHANGE,500);
            }
        }
    };

    public MyVideoPlayer(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MyVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        secondChangeHandler.removeCallbacksAndMessages(null);
    }

    private void init(Context context){
        mContext=context;
        LayoutInflater.from(context).inflate(R.layout.my_video_player,this);
        initComponent();
        initView();
        initEvent();
    }

    private void initComponent(){
        frameLayout_body=findViewById(R.id.frameLayout_body);
        imageView_cover=findViewById(R.id.iv_cover);
        surfaceView=findViewById(R.id.surfaceView);
        button_pause=findViewById(R.id.btn_pause);
        seekBar_progress=findViewById(R.id.seekBar_progress);
        textView_duration=findViewById(R.id.tv_duration);
        button_fullscreen=findViewById(R.id.btn_fullscreen);
    }

    private void initView(){
        initSurfaceView();
        initButtonPause();
        initSeekBar();
        initTextViewDuration();
        initButtonFullscreen();
        showBar();
    }

    private void initSurfaceView(){
        surfaceView.setVisibility(INVISIBLE);
    }

    private void initButtonPause(){
        button_pause.setBackground(mContext.getDrawable(R.drawable.ic_ispausing));
    }

    private void initSeekBar(){
        seekBar_progress.setMax(100);
        seekBar_progress.setProgress(0);
        seekBar_progress.setEnabled(false);
    }

    private void initTextViewDuration(){
        setTextProgress(0,0);
    }

    private void initButtonFullscreen(){
        button_fullscreen.setBackground(mContext.getDrawable(R.drawable.ic_fullscreen));
    }

    private void initEvent(){
        frameLayout_body.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pressVideo();
            }
        });

        surfaceView.getHolder().addCallback(new PlayerSurfaceCallback());

        setListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

            }

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                button_pause.setBackground(mContext.getDrawable(R.drawable.ic_ispausing));
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                loadDuration();
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2,
                int i3) {

            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                progressChanged(false);
            }
        });

        button_pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer==null){
                    Toast.makeText(mContext,"当前没有视频！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mMediaPlayer.isPlaying()){
                    pause();
                    button_pause.setBackground(mContext.getDrawable(R.drawable.ic_ispausing));
                }
                else{
                    start();
                    button_pause.setBackground(mContext.getDrawable(R.drawable.ic_isplaying));
                }
            }
        });

        seekBar_progress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    seekTo((long)progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button_fullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity=getActivityFromView(MyVideoPlayer.this);
                if(activity.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
                    button_fullscreen.setBackground(mContext.getDrawable(R.drawable.ic_fullscreen_exit));
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    button_fullscreen.setBackground(mContext.getDrawable(R.drawable.ic_fullscreen));
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

            }
        });
    }

    public static Activity getActivityFromView(View view) {
        if (null != view) {
            Context context = view.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }

    //点击视频区域，会显示或隐藏进度栏
    protected void pressVideo(){
        if(!isShown){
            showBar();
        }
        else{
            hideBar();
        }
    }

    private void showBar(){
        button_pause.setVisibility(View.VISIBLE);
        seekBar_progress.setVisibility(View.VISIBLE);
        textView_duration.setVisibility(View.VISIBLE);
        button_fullscreen.setVisibility(View.VISIBLE);
        isShown=true;
    }

    private void hideBar(){
        button_pause.setVisibility(View.GONE);
        seekBar_progress.setVisibility(View.GONE);
        textView_duration.setVisibility(View.GONE);
        button_fullscreen.setVisibility(View.GONE);
        isShown=false;
    }

    private void setTextProgress(int progress,int duration){
        String text=getTimeString(progress)+"/"+getTimeString(duration);
        textView_duration.setText(text);
    }

    private void loadDuration(){
        videoDuration=getDurationSecond();
        seekBar_progress.setMax(videoDuration);
        setTextProgress(0,videoDuration);
    }

    private int getDurationSecond(){
        if(mMediaPlayer==null){
            return 0;
        }
        long duration=mMediaPlayer.getDuration();
        return (int)(duration/1000);
    }

    private int getProgressSecond(){
        if(mMediaPlayer==null){
            return 0;
        }
        long progress=mMediaPlayer.getCurrentPosition();
        if((progress%1000)>0){
            return (int)(progress/1000)+1;
        }
        else{
            return (int)(progress/1000);
        }
    }

    @SuppressLint("DefaultLocale")
    private String getTimeString(int second){
        int minute=second/60;
        second=second%60;
        return String.format("%02d",minute) +":"+ String.format("%02d",second);
    }

    private void progressChanged(boolean setProgress){
        int progress=getProgressSecond();
        if(setProgress){
            seekBar_progress.setProgress(progress);
        }
        setTextProgress(progress,videoDuration);
    }

    private void firstPlay(){
        imageView_cover.setVisibility(GONE);
        surfaceView.setVisibility(VISIBLE);
        seekBar_progress.setEnabled(true);
        isFirstPlay=false;
    }

    public void setImageCover(String url){
        Glide.with(imageView_cover.getContext()).load(url).into(imageView_cover);
    }

    /**
     * 设置视频地址。
     * 根据是否第一次播放视频，做不同的操作。
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        String[] strings=path.split("://");

        mPath = "http://"+strings[1];
        load();
    }

    public void setVideoResource(int resourceId) {
        resId = resourceId;
        load(resId);
    }

    //surfaceView的监听器
    private class PlayerSurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //surfaceView创建成功后，加载视频
            //给mediaPlayer设置视图
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepareAsync();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    //加载视频
    private void load() {
        //每次都要重新创建IMediaPlayer
        createPlayer();
        try {
            mMediaPlayer.setDataSource(mPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //加载视频
    private void load(int resourceId) {
        //每次都要重新创建IMediaPlayer
        createPlayer();
        AssetFileDescriptor fileDescriptor = mContext.getResources().openRawResourceFd(resourceId);
        RawDataSourceProvider provider = new RawDataSourceProvider(fileDescriptor);
        mMediaPlayer.setDataSource(provider);
    }

    //创建一个新的player
    private void createPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
//        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mMediaPlayer = ijkMediaPlayer;
        ((IjkMediaPlayer) mMediaPlayer).setSpeed(3f);
        if (listener != null) {
            mMediaPlayer.setOnBufferingUpdateListener(listener);
            mMediaPlayer.setOnCompletionListener(listener);
            mMediaPlayer.setOnPreparedListener(listener);
            mMediaPlayer.setOnInfoListener(listener);
            mMediaPlayer.setOnVideoSizeChangedListener(listener);
            mMediaPlayer.setOnErrorListener(listener);
            mMediaPlayer.setOnSeekCompleteListener(listener);
        }
    }

    public void setListener(VideoPlayerListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (mMediaPlayer != null) {
            if(isFirstPlay){
                firstPlay();
            }
            mMediaPlayer.start();
            secondChangeHandler.sendEmptyMessage(SECOND_CHANGE);
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void seekTo(long l) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(l);
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            secondChangeHandler.removeCallbacksAndMessages(null);
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            secondChangeHandler.removeCallbacksAndMessages(null);
        }
    }


    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            secondChangeHandler.removeCallbacksAndMessages(null);
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
