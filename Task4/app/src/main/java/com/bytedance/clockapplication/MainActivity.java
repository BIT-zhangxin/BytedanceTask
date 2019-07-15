package com.bytedance.clockapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Toast;
import com.bytedance.clockapplication.widget.Clock;

public class MainActivity extends AppCompatActivity {

    public static final int TIME_CHANGE=1023;

    private View mRootView;
    private Clock mClockView;

    @SuppressLint("HandlerLeak")
    private Handler timeChangeHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TIME_CHANGE) {
                mClockView.invalidate();
                timeChangeHandler.sendEmptyMessageDelayed(TIME_CHANGE,1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootView = findViewById(R.id.root);
        mClockView = findViewById(R.id.clock);

        timeChangeHandler.sendEmptyMessage(TIME_CHANGE);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClockView.setShowAnalog(!mClockView.isShowAnalog());
            }
        });
    }

    @Override
    protected void onDestroy() {
        timeChangeHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
