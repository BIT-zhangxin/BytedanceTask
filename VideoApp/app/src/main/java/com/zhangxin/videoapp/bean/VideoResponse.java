package com.zhangxin.videoapp.bean;

import java.util.List;

public class VideoResponse {

    private boolean success;
    private List<Video> feeds;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Video> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Video> feeds) {
        this.feeds = feeds;
    }
}
