package com.zhangxin.videoapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.zhangxin.videoapp.R;

public class ImageActivity extends AppCompatActivity {

    private ImageView iv_preview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        final String file_path = getIntent().getStringExtra("file_path");
        iv_preview=findViewById(R.id.iv_preview);
        Glide.with(iv_preview).load(file_path).into(iv_preview);
    }
}
