package com.zhangxin.videoapp.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.zhangxin.videoapp.R;
import com.zhangxin.videoapp.bean.Video;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VideoListAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private OnItemClickListener listener;
    private List<Video> videos=new ArrayList<>();

    public VideoListAdapter(OnItemClickListener listener) {
        this.listener=listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoViewHolder videoViewHolder, int pos) {
        videoViewHolder.bind(videos.get(pos));

        videoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(listener != null) {
                    int pos = videoViewHolder.getLayoutPosition();
                    listener.onItemClick(videoViewHolder.itemView, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public Video getVideo(int position){
        return videos.get(position);
    }

    public void refresh(List<Video> videoList){
        videos.clear();
        if(videoList!=null){
            Random r=new Random();
            int showSize=Math.min(50,videoList.size());
            for(int i=0;i<showSize;i++){
                videos.add(videoList.get(r.nextInt(videoList.size())));
            }
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
}
