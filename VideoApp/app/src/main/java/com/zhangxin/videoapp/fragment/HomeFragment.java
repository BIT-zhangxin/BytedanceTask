package com.zhangxin.videoapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhangxin.videoapp.network.IMiniDouyinService;
import com.zhangxin.videoapp.R;
import com.zhangxin.videoapp.network.RetrofitManager;
import com.zhangxin.videoapp.bean.Video;
import com.zhangxin.videoapp.ui.VideoListAdapter;
import com.zhangxin.videoapp.ui.VideoListAdapter.OnItemClickListener;
import com.zhangxin.videoapp.activity.VideoPlayerActivity;
import com.zhangxin.videoapp.bean.VideoResponse;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private VideoListAdapter videoListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView=view.findViewById(R.id.recyclerView_video);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        videoListAdapter=new VideoListAdapter(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Video video=videoListAdapter.getVideo(position);
                startVideoPlayer(video);
        }
        });
        recyclerView.setAdapter(videoListAdapter);

        refreshLayout=view.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getVideoInfo();
                refreshlayout.finishRefresh(200);
            }
        });
        getVideoInfo();
        return view;
    }

    private void startVideoPlayer(Video video){
        Intent intent=new Intent(getContext(), VideoPlayerActivity.class);
        intent.putExtra("_id",video.get_id());
        intent.putExtra("student_id",video.getStudent_id());
        intent.putExtra("user_name",video.getUser_name());
        intent.putExtra("image_url",video.getImage_url());
        intent.putExtra("video_url",video.getVideo_url());
        startActivity(intent);
    }

    private void getVideoInfo() {

        Call<VideoResponse> feedResponseCall = RetrofitManager
            .get("http://test.androidcamp.bytedance.com/mini_douyin/invoke/")
            .create(IMiniDouyinService.class)
            .getFeed();
        feedResponseCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                if(response.body()==null||!response.body().isSuccess()){
                    Toast.makeText(getContext(),"获取失败",Toast.LENGTH_LONG).show();
                    return;
                }
                List<Video> videos=response.body().getFeeds();
                videoListAdapter.refresh(videos);
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(),"获取失败",Toast.LENGTH_LONG).show();
            }
        });

    }


}
